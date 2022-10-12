package de.monticore.cd2smt.cd2smtGenerator;

import com.microsoft.z3.*;
import de.monticore.cd2smt.Helper.CDHelper;
import de.monticore.cd2smt.Helper.Identifiable;
import de.monticore.cd2smt.Helper.SMTNameHelper;
import de.monticore.cd2smt.context.CDArtifacts.SMTAssociation;
import de.monticore.cd2smt.context.CDArtifacts.SMTCDType;
import de.monticore.cd2smt.context.CDArtifacts.SMTClass;
import de.monticore.cd2smt.context.CDArtifacts.SMTInterface;
import de.monticore.cd2smt.context.CDContext;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;

import java.util.*;

public class CD2SMTGenerator {

  public CD2SMTGenerator() {
  }


  /**
   * declared a Class diagram in SMT Context
   *
   * @param astCd the class diagram to declared
   * @param ctx   the context
   * @return the  CDContext that contains all sorts and Function Declaration
   */
  public CDContext cd2smt(ASTCDCompilationUnit astCd, Context ctx) {

    final CDContext cdContext = new CDContext(ctx);

    //set All Associations Role
    CDHelper.setAssociationsRoles(astCd);

    //declare all classes
    astCd.getCDDefinition().getCDClassesList().forEach(Class -> declareClass(cdContext, astCd.getCDDefinition(), Class));

    //declare all interfaces
    astCd.getCDDefinition().getCDInterfacesList().forEach(Interface -> declareInterface(cdContext, astCd.getCDDefinition(), Interface));

    //declare  all associations
    astCd.getCDDefinition().getCDAssociationsList().forEach(assoc -> declareAssociation(cdContext, assoc, astCd.getCDDefinition()));


    //add all constraints to the context
    buildAssocConstraints(cdContext, astCd.getCDDefinition()).forEach(cdContext::addAssociationConstraints);
    buildInheritanceConstraints(cdContext, astCd.getCDDefinition()).forEach(cdContext::addInheritanceConstr);

    return cdContext;
  }


  //-----------------------------------Class-And-Interface-declaration---------------------------------------------------------------
  protected void declareASTCDType(CDContext cdContext, ASTCDDefinition cd, ASTCDType astcdType, SMTCDType smtcdType) {
    //create SMTClass object to save Class information
    if (astcdType instanceof ASTCDClass) {
      smtcdType.setCDType((ASTCDClass) astcdType);
    }
    if (astcdType instanceof ASTCDInterface) {
      smtcdType.setCDType((ASTCDInterface) astcdType);
    }

    //(declare-sort A_obj 0)
    UninterpretedSort interfaceSort = cdContext.getContext().mkUninterpretedSort(cdContext.getContext().
      mkSymbol(SMTNameHelper.printSMTCDTypeName(astcdType)));
    smtcdType.setSort(interfaceSort);

    //(declare-datatype B_subclasses ((TT_NO_SUBTYPE) (TT_B)))
    buildSubclassConstructorList(astcdType, cdContext, cd).forEach(smtcdType::addSubclassConstructor);

    //declare datatype und function to match each object to the subclass instance
    Constructor<Sort>[] constructors = smtcdType.getSubClassConstructorList().values().toArray(new Constructor[0]);
    if (constructors.length > 0) {
      smtcdType.setSubclassDatatype(cdContext.getContext().mkDatatypeSort(astcdType.getName() + "_subclasses", constructors));
      smtcdType.setGetSubClass(cdContext.getContext().mkFuncDecl(SMTNameHelper.printSubclassFuncName(astcdType), interfaceSort, smtcdType.getSubclassDatatype()));
    }
    //(declare-fun a_attrib_something (A_obj) String) declare all attributes
    for (ASTCDAttribute myAttribute : astcdType.getCDAttributeList()) {
      String attribName = SMTNameHelper.printAttributeNameSMT(astcdType, myAttribute);
      Sort sort = CDHelper.parseAttribType2SMT(cdContext.getContext(), myAttribute);
      FuncDecl<Sort> attributeFunc = cdContext.getContext().mkFuncDecl(attribName, interfaceSort,
        sort);
      smtcdType.getAttributes().add(attributeFunc);
    }
  }


  protected Map<ASTCDType, Constructor<Sort>> buildSubclassConstructorList(ASTCDType astcdType, CDContext cdContext, ASTCDDefinition cd) {
    List<ASTCDClass> subClassList = CDHelper.getSubclassList(cd, astcdType);
    Map<ASTCDType, Constructor<Sort>> res = new HashMap<>();

    //add Constructor for subclasses
    for (ASTCDClass entry : subClassList) {
      Constructor<Sort> constructor = cdContext.getContext().mkConstructor("TT_" + entry.getName(), "IS_TT" +
        entry.getName(), null, null, null);
      res.put(entry, constructor);
    }

    //add constructor for subInterfaces
    if (astcdType instanceof ASTCDInterface) {
      for (ASTCDInterface entry : CDHelper.getSubInterfaceList(cd, (ASTCDInterface) astcdType)) {
        Constructor<Sort> constructor = cdContext.getContext().mkConstructor("TT_" + entry.getName(), "IS_TT" +
          entry.getName(), null, null, null);
        res.put(entry, constructor);
      }
      return res;

      //add constructor for No subclass
    } else if (astcdType instanceof ASTCDClass) {
      res.put(astcdType, cdContext.getContext().mkConstructor("TT_NO_SUBTYPE", "IS_TT_NO_SUBTYPE",
        null, null, null));
      return res;
    } else {
      Log.error("build of subclass Datatype ist not define for the class" + astcdType.getClass().getName());
      return res;
    }
  }

  protected void declareClass(CDContext cdContext, ASTCDDefinition cd, ASTCDClass myClass) {
    SMTClass smtClass = new SMTClass();

    declareASTCDType(cdContext, cd, myClass, smtClass);

    cdContext.addCDTYpe(myClass, smtClass);
  }

  protected void declareInterface(CDContext cdContext, ASTCDDefinition cd, ASTCDInterface astcdInterface) {
    SMTInterface smtInterface = new SMTInterface();

    declareASTCDType(cdContext, cd, astcdInterface, smtInterface);

    cdContext.addCDTYpe(astcdInterface, smtInterface);
  }


  //-----------------------------------Association------------------------------------------------------------------------
  protected void declareAssociation(CDContext cdContext, ASTCDAssociation myAssociation, ASTCDDefinition cd) {
    SMTAssociation smtAssociation = new SMTAssociation();
    String assocName = SMTNameHelper.printSMTAssociationName(myAssociation);
    //get the link and the right class of the Association
    ASTCDType leftClass = CDHelper.getASTCDType(myAssociation.getRightQualifiedName().getQName(), cd);
    ASTCDType rightClass = CDHelper.getASTCDType(myAssociation.getLeftQualifiedName().getQName(), cd);

    //set the name and the role
    smtAssociation.setLeftRole(myAssociation.getLeft().getCDRole().getName());
    smtAssociation.setRightRole(myAssociation.getRight().getCDRole().getName());

    assert cdContext.getSmtCDTypes().containsKey(rightClass);
    smtAssociation.setLeft(cdContext.getSmtCDTypes().get(leftClass));
    assert cdContext.getSmtCDTypes().containsKey(leftClass);
    smtAssociation.setRight(cdContext.getSmtCDTypes().get(rightClass));

    //set the name of the Association
    if (myAssociation.isPresentName()) {
      smtAssociation.setName(myAssociation.getName());
    }

    smtAssociation.getLeft().getSMTAssociations().put(myAssociation, smtAssociation);
    smtAssociation.getRight().getSMTAssociations().put(myAssociation, smtAssociation);

    //set the Association function
    Sort rightSortSMT = cdContext.getSmtCDTypes().get(leftClass).getSort();
    Sort leftSortSMT = cdContext.getSmtCDTypes().get(rightClass).getSort();
    smtAssociation.setAssocFunc(cdContext.getContext().mkFuncDecl(assocName,
      new Sort[]{leftSortSMT, rightSortSMT}, cdContext.getContext().getBoolSort()));

    cdContext.getSMTAssociations().put(myAssociation, smtAssociation);
  }

  protected BoolExpr buildAtLeastOneConstraint(CDContext cdContext, FuncDecl<BoolSort> assocFunc, Expr<? extends Sort> obj, Expr<? extends Sort> otherObj, boolean isLeft) {
    Expr<? extends Sort> left = !isLeft ? obj : otherObj;
    Expr<? extends Sort> right = isLeft ? obj : otherObj;
    return cdContext.getContext().mkForall(new Expr[]{obj},
      cdContext.getContext().mkExists(new Expr[]{otherObj},
        cdContext.getContext().mkApp(assocFunc, left, right),
        0, null, null, null, null), 0,
      null, null, null, null);
  }

  protected BoolExpr buildOptionalConstraint(CDContext cdContext, FuncDecl<BoolSort> assocFunc, Expr<? extends Sort> obj1, Expr<? extends Sort> otherObj1, Expr<? extends Sort> otherObj2, boolean isLeft) {
    Expr<? extends Sort> left1 = !isLeft ? obj1 : otherObj1;
    Expr<? extends Sort> left2 = !isLeft ? obj1 : otherObj2;
    Expr<? extends Sort> right1 = isLeft ? obj1 : otherObj1;
    Expr<? extends Sort> right2 = isLeft ? obj1 : otherObj1;
    return cdContext.getContext().mkForall(new Expr[]{obj1, otherObj1, otherObj2},
      cdContext.getContext().mkImplies(
        cdContext.getContext().mkAnd(
          cdContext.getContext().mkApp(assocFunc, left1, right1),
          cdContext.getContext().mkApp(assocFunc, left2, right2)),
        cdContext.getContext().mkEq(otherObj1, otherObj2)),
      0, null, null, null, null);
  }

  List<Identifiable<BoolExpr>> buildAssocConstraints(CDContext cdContext, ASTCDDefinition cd) {
    List<Identifiable<BoolExpr>> constraints = new LinkedList<>();

    for (ASTCDAssociation myAssoc : cd.getCDAssociationsList()) {
      //get the sort for the left and right objects
      Sort rightSortSMT = cdContext.getSmtCDTypes().get(CDHelper.getASTCDType(myAssoc.getRightQualifiedName().getQName(), cd)).getSort();
      Sort leftSortSMT = cdContext.getSmtCDTypes().get(CDHelper.getASTCDType(myAssoc.getLeftQualifiedName().getQName(), cd)).getSort();
      String assocName = SMTNameHelper.printSMTAssociationName(myAssoc);
      SMTAssociation smtAssociation = cdContext.getSMTAssociations().get(myAssoc);
      //build constants for quantifiers scope
      Expr<Sort> r1 = cdContext.getContext().mkConst(assocName + "r1", rightSortSMT);
      Expr<Sort> l1 = cdContext.getContext().mkConst(assocName + "l1", leftSortSMT);
      Expr<Sort> l2 = cdContext.getContext().mkConst(assocName + "l2", leftSortSMT);
      Expr<Sort> r2 = cdContext.getContext().mkConst(assocName + "r2", rightSortSMT);

      //position
      SourcePosition srcPos = myAssoc.get_SourcePositionStart();
      assert srcPos.getFileName().isPresent();

      //Cardinality on the right side
      if (myAssoc.getRight().isPresentCDCardinality()) {
        BoolExpr atLeastOne = buildAtLeastOneConstraint(cdContext, smtAssociation.getAssocFunc(), l1, r1, false);
        BoolExpr optional = buildOptionalConstraint(cdContext, smtAssociation.getAssocFunc(), l1, r1, r2, false);

        //get the source Position fo the cardinality
        SourcePosition cardSrcPos = myAssoc.getRight().getCDCardinality().get_SourcePositionStart();

        if (myAssoc.getRight().getCDCardinality().isAtLeastOne()) {
          constraints.add(Identifiable.buildBoolExprIdentifiable(atLeastOne, cardSrcPos, Optional.of("Cardinality_right")));
        } else if (myAssoc.getRight().getCDCardinality().isOpt()) {
          constraints.add(Identifiable.buildBoolExprIdentifiable(optional, cardSrcPos, Optional.of("Cardinality_right")));
        } else if (myAssoc.getRight().getCDCardinality().isOne()) {
          constraints.add(Identifiable.buildBoolExprIdentifiable(cdContext.getContext().mkAnd(atLeastOne, optional), cardSrcPos, Optional.of("Cardinality_right")));
        }
      }

      //Cardinality on the left side
      if (myAssoc.getLeft().isPresentCDCardinality()) {
        BoolExpr atLeastOne = buildAtLeastOneConstraint(cdContext, smtAssociation.getAssocFunc(), r1, l1, true);
        BoolExpr optional = buildOptionalConstraint(cdContext, smtAssociation.getAssocFunc(), r1, l1, l2, true);

        //get the source Position fo the cardinality
        SourcePosition cardSrcPos = myAssoc.getLeft().getCDCardinality().get_SourcePositionStart();

        if (myAssoc.getLeft().getCDCardinality().isAtLeastOne()) {
          constraints.add(Identifiable.buildBoolExprIdentifiable(atLeastOne, cardSrcPos, Optional.of("Cardinality_left")));
        } else if (myAssoc.getLeft().getCDCardinality().isOpt()) {
          constraints.add(Identifiable.buildBoolExprIdentifiable(optional, cardSrcPos, Optional.of("Cardinality_left")));
        } else if (myAssoc.getLeft().getCDCardinality().isOne()) {
          constraints.add(Identifiable.buildBoolExprIdentifiable(cdContext.getContext().mkAnd(atLeastOne, optional), cardSrcPos, Optional.of("Cardinality_left")));
        }

      }
    }
    return constraints;
  }

  //-----------------------------------inheritance----------------------------------------------------------------------
  List<Identifiable<BoolExpr>> buildInheritanceConstraints(CDContext cdContext, ASTCDDefinition cd) {
    List<Identifiable<BoolExpr>> constraints = new LinkedList<>();

    for (ASTCDClass myClass : cd.getCDClassesList()) {

      //add constraint for classes that inherit from another classes
      if (!myClass.getSuperclassList().isEmpty()) {
        Optional<ASTCDClass> superClass = Optional.ofNullable(CDHelper.getClass(myClass.getSuperclassList().get(0).
          printType(new MCBasicTypesFullPrettyPrinter(new IndentPrinter())), cd));
        assert superClass.isPresent();
        constraints.addAll(buildCDTypeInheritanceConstraint(cdContext, myClass, cdContext.getSmtCDTypes().get(myClass),
          superClass.get(), cdContext.getSmtCDTypes().get(superClass.get())));
      }

      //add constraint for  classes that  inherit  from interfacees
      for (ASTMCObjectType superC : myClass.getInterfaceList()) {
        Optional<ASTCDInterface> superInterface = Optional.ofNullable(CDHelper.getInterface(superC.
          printType(new MCBasicTypesFullPrettyPrinter(new IndentPrinter())), cd));
        assert superInterface.isPresent();
        constraints.addAll(buildCDTypeInheritanceConstraint(cdContext, myClass, cdContext.getSmtCDTypes().get(myClass),
          superInterface.get(), cdContext.getSmtCDTypes().get(superInterface.get())));
      }
    }
    //add constraint for  interfaces that  inherit from  interfaces
    for (ASTCDInterface myInterface : cd.getCDInterfacesList()) {
      for (ASTMCObjectType superInterf : myInterface.getInterfaceList()) {
        Optional<ASTCDInterface> superInterface = Optional.ofNullable(CDHelper.getInterface(superInterf.
          printType(new MCBasicTypesFullPrettyPrinter(new IndentPrinter())), cd));
        assert superInterface.isPresent();

        constraints.addAll(buildCDTypeInheritanceConstraint(cdContext, myInterface, cdContext.getSmtCDTypes().get(myInterface),
          superInterface.get(), cdContext.getSmtCDTypes().get(superInterface.get())));

      }
    }
    //asset each Interface Obj muss have a super instance Object
    constraints.addAll(mkConstrExistSubclass4Interf(cd, cdContext));
    return constraints;
  }

  protected List<Identifiable<BoolExpr>> mkConstrExistSubclass4Interf(ASTCDDefinition cd, CDContext cdContext) {
    List<Identifiable<BoolExpr>> constraints = new ArrayList<>();
    for (ASTCDInterface myInterface : cd.getCDInterfacesList()) {
      //make constraint for existence of super Instance of each Interface instance
      Expr<? extends Sort> interfObj = cdContext.getContext().mkConst(myInterface.getName(), cdContext.getSmtCDTypes().get(myInterface).getSort());
      BoolExpr helpConstr = cdContext.getContext().mkFalse();
      for (ASTCDType subInstance : cdContext.getSmtCDTypes().get(myInterface).getSubClassConstructorList().keySet()) {

        SMTCDType subSMTInstance = cdContext.getSmtCDTypes().get(subInstance);
        Expr<Sort> subInstanceObj = cdContext.getContext().mkConst(subInstance.getName(), subSMTInstance.getSort());

        helpConstr = cdContext.getContext().mkOr(helpConstr, cdContext.getContext().mkExists(new Expr[]{subInstanceObj},
          cdContext.getContext().mkEq(cdContext.getContext().mkApp(subSMTInstance.getConvert2SuperInterface().get(myInterface),
            subInstanceObj), interfObj), 0, null, null, null, null));
      }

      BoolExpr superInstanceConstraint = cdContext.getContext().mkForall(new Expr[]{interfObj}, helpConstr,
        0, null, null, null, null);
      constraints.add(Identifiable.buildBoolExprIdentifiable(superInstanceConstraint, myInterface
        .get_SourcePositionStart(), Optional.of(myInterface.getName() + "Exist_super")));
    }
    return constraints;
  }

  List<Identifiable<BoolExpr>> buildCDTypeInheritanceConstraint(CDContext cdContext, ASTCDType subType, SMTCDType smtSubType, ASTCDType SuperType, SMTCDType smtSuperType) {
    List<Identifiable<BoolExpr>> constraints = new LinkedList<>();

    SourcePosition srcPos = subType.get_SourcePositionStart();
    assert srcPos.getFileName().isPresent();

    //add declare  the function to the smt representation of the subclass
    FuncDecl<UninterpretedSort> convert2Super = cdContext.getContext().mkFuncDecl("Convert_" + subType.getName() + "_to_" + SuperType.getName(),
      smtSubType.getSort(), smtSuperType.getSort());
    if (SuperType instanceof ASTCDInterface) {
      smtSubType.addConvert2SuperInterfFunc((ASTCDInterface) SuperType, convert2Super);
    } else {
      smtSubType.setConvert2Superclass(convert2Super);
    }

    //constraints to make sure that the function convert_to will be defined
    Expr<Sort> subclassObj = cdContext.getContext().mkConst(subType.getName() + "scc", smtSubType.getSort());
    Expr<Sort> superClassObj = cdContext.getContext().mkConst(SuperType.getName() + "scc", smtSuperType.getSort());

    BoolExpr constrConvert = cdContext.getContext().mkForall(new Expr[]{subclassObj}, cdContext
        .getContext().mkExists(new Expr[]{superClassObj},
          cdContext.getContext().mkEq(cdContext.getContext().mkApp(convert2Super, subclassObj), superClassObj), 0, null,
          null, null, null), 0,
      null, null, null, null);
    constraints.add(Identifiable.buildBoolExprIdentifiable(constrConvert, srcPos, Optional.of(subType.getName() + "_inheritance_convert")));

    //the conversion must be a bijection
    Expr<Sort> b1 = cdContext.getContext().mkConst("b1", smtSubType.getSort());
    Expr<Sort> b2 = cdContext.getContext().mkConst("b2", smtSubType.getSort());
    BoolExpr bijection = cdContext.getContext().mkForall(new Expr[]{b1, b2}, cdContext.getContext().mkEq(
        cdContext.getContext().mkEq(cdContext.getContext().mkApp(convert2Super, b1),
          cdContext.getContext().mkApp(convert2Super, b2)), cdContext
          .getContext().mkEq(b1, b2)),
      0, null, null, null, null);
    constraints.add(Identifiable.buildBoolExprIdentifiable(bijection, srcPos, Optional.of(subType.getName() + "_inheritance_bijection")));

    if (smtSuperType.getSubClassConstructorList().size() > 0) {
      //type Constraint
      Expr<Sort> b3 = cdContext.getContext().mkConst("b3", smtSubType.getSort());
      Expr<? extends Sort> sort = cdContext.getContext().mkConst(smtSuperType.getSubClassConstructorList().get(subType).ConstructorDecl());
      BoolExpr typeConstr = cdContext.getContext().mkForall(new Expr[]{b3}, cdContext.getContext().mkEq(
          cdContext.getContext().mkApp(smtSuperType.getSubClass(),
            cdContext.getContext().mkApp(convert2Super, b1)), sort),
        0, null, null, null, null);
      //add the constraints to the list
      constraints.add(Identifiable.buildBoolExprIdentifiable(typeConstr, srcPos, Optional.of(subType.getName() + "_inheritance_type_constr")));
    }
    return constraints;
  }
}









