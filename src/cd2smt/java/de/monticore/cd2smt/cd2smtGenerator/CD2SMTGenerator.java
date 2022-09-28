package de.monticore.cd2smt.cd2smtGenerator;

import com.microsoft.z3.*;
import de.monticore.cd2smt.Helper.CDHelper;
import de.monticore.cd2smt.Helper.SMTNameHelper;
import de.monticore.cd2smt.context.CDContext;
import de.monticore.cd2smt.context.CDArtifacts.SMTAssociation;
import de.monticore.cd2smt.context.CDArtifacts.SMTClass;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class CD2SMTGenerator {

  public CD2SMTGenerator() {
  }


  /**
   * declared a Class diagram in SMT Context
   * @param astCd the class diagram to declared
   * @param  ctx  the context
   * @return the  CDContext that contains all sorts and Function Declaration
   */
  public CDContext cd2smt(ASTCDCompilationUnit astCd, Context ctx) {

  final CDContext cdContext = new CDContext(ctx);

    //set All Associations Role
    CDHelper.setAssociationsRoles(astCd);

    //declare all classes
    astCd.getCDDefinition().getCDClassesList().forEach(Class-> declareClass(cdContext, astCd.getCDDefinition(), Class));

    //declare  all associations
    astCd.getCDDefinition().getCDAssociationsList().forEach(assoc -> declareAssociation(cdContext, assoc, astCd.getCDDefinition()));

    //add all constraints to the context
    cdContext.setAssociationConstraints(buildAssocConstraints(cdContext, astCd.getCDDefinition()));
    cdContext.setInheritanceConstraints(buildInheritanceConstraints(cdContext, astCd.getCDDefinition()));

    return cdContext;
  }


  //-----------------------------------Class--declaration---------------------------------------------------------------
  protected void declareClass(CDContext cdContext, ASTCDDefinition cd, ASTCDClass myClass) {
    //create SMTClass object to save Class information
    SMTClass smtClass = new SMTClass();
    smtClass.setClass(myClass);

    //(declare-sort A_obj 0)
    UninterpretedSort classSort = cdContext.getContext().mkUninterpretedSort(cdContext.getContext().
      mkSymbol(SMTNameHelper.printSMTClassName(myClass)));
    smtClass.setSort(classSort);

    //(declare-datatype B_subclasses ((TT_NO_SUBTYPE) (TT_B)))
    List<ASTCDClass> subclassList = cdContext.getSubclassList(cd, myClass);
    Constructor<Sort>[] constructors = new Constructor[subclassList.size() + 1];
    constructors[0] = cdContext.getContext().mkConstructor("TT_NO_SUBTYPE", "IS_TT_NO_SUBTYPE",
      null, null, null);
    smtClass.getSubClassConstrList().put(myClass, constructors[0]);

    if (subclassList.size() > 0)
      for (int i = 0; i < subclassList.size(); i++) {
        constructors[i + 1] = cdContext.getContext().mkConstructor("TT_" + subclassList.get(i).getName(), "IS_TT" +
          subclassList.get(i).getName(), null, null, null);
        smtClass.getSubClassConstrList().put(subclassList.get(i), constructors[i + 1]);
      }
    smtClass.setSubclassDatatype( cdContext.getContext().mkDatatypeSort(myClass.getName() + "_subclasses", constructors));

    //(declare-fun b_get_subclass (B_obj) B_subclasses)
    smtClass.setSubClass( cdContext.getContext().mkFuncDecl(SMTNameHelper.printSubclassFuncName(myClass), classSort, smtClass.getSubclassDatatype()));

    //(declare-fun a_attrib_something (A_obj) String) declare all attributes
    for (ASTCDAttribute myAttribute : myClass.getCDAttributeList()) {
      String attribName = SMTNameHelper.printAttributeNameSMT(myClass, myAttribute);
      Sort sort = CDHelper.parseAttribType2SMT(cdContext.getContext(), myAttribute);
      FuncDecl<Sort> attributeFunc = cdContext.getContext().mkFuncDecl(attribName, classSort,
        sort);
      smtClass.getAttributes().add(attributeFunc);
    }
    cdContext.getSmtClasses().put(myClass, smtClass);

  }




  //-----------------------------------Association------------------------------------------------------------------------
  protected void declareAssociation(CDContext cdContext, ASTCDAssociation myAssociation, ASTCDDefinition cd) {
    SMTAssociation smtAssociation = new SMTAssociation();
    String assocName = SMTNameHelper.printSMTAssociationName(myAssociation);
    //get the link and the right class of the Association
    ASTCDClass leftClass = CDHelper.getClass(myAssociation.getRightQualifiedName().getQName(), cd);
    ASTCDClass rightClass = CDHelper.getClass(myAssociation.getLeftQualifiedName().getQName(), cd);

    //set the name and the role
    smtAssociation.setLeftRole( myAssociation.getLeft().getCDRole().getName());
    smtAssociation.setRightRole( myAssociation.getRight().getCDRole().getName());

    assert cdContext.getSmtClasses().containsKey(rightClass);
    smtAssociation.setLeft( cdContext.getSmtClasses().get(leftClass));
    assert cdContext.getSmtClasses().containsKey(leftClass);
    smtAssociation.setRight( cdContext.getSmtClasses().get(rightClass));

    //set the name of the Association
    if (myAssociation.isPresentName()) {
      smtAssociation.setName(myAssociation.getName());
    }

    smtAssociation.getLeft().getSMTAssociations().put(myAssociation,smtAssociation);
    smtAssociation.getRight().getSMTAssociations().put(myAssociation,smtAssociation);

    //set the Association function
    Sort rightSortSMT = cdContext.getSmtClasses().get(leftClass).getSort();
    Sort leftSortSMT = cdContext.getSmtClasses().get(rightClass).getSort();
    smtAssociation.setAssocFunc( cdContext.getContext().mkFuncDecl(assocName,
      new Sort[]{leftSortSMT, rightSortSMT}, cdContext.getContext().getBoolSort()));

    cdContext.getSMTAssociations().put(myAssociation, smtAssociation);
  }


  List<Pair<String,BoolExpr>> buildAssocConstraints(CDContext cdContext, ASTCDDefinition cd) {
    List<Pair<String,BoolExpr>> constraints = new LinkedList<>();

    for (ASTCDAssociation myAssoc : cd.getCDAssociationsList()) {
      //get the sort for the left and right objects
      Sort rightSortSMT = cdContext.getSmtClasses().get(CDHelper.getClass(myAssoc.getRightQualifiedName().getQName(), cd)).getSort();
      Sort leftSortSMT = cdContext.getSmtClasses().get(CDHelper.getClass(myAssoc.getLeftQualifiedName().getQName(), cd)).getSort();
      String assocName = SMTNameHelper.printSMTAssociationName(myAssoc);
      SMTAssociation smtAssociation = cdContext.getSMTAssociations().get(myAssoc);
      //build constants for quantifiers scope
      Expr<Sort> r1 = cdContext.getContext().mkConst(assocName + "r1", rightSortSMT);
      Expr<Sort> l1 = cdContext.getContext().mkConst(assocName + "l1", leftSortSMT);
      Expr<Sort> l2 = cdContext.getContext().mkConst(assocName + "l2", leftSortSMT);
      Expr<Sort> r2 = cdContext.getContext().mkConst(assocName + "r2", rightSortSMT);
      //Cardinality on the right side
      if (myAssoc.getRight().isPresentCDCardinality()) {
        //[1..*]
        BoolExpr atLeastOne = cdContext.getContext().mkForall(new Expr[]{l1},
          cdContext.getContext().mkExists(new Expr[]{r1},
            cdContext.getContext().mkApp(smtAssociation.getAssocFunc(), l1, r1),
            0, null, null, null, null), 0,
          null, null, null, null);

        //[1..0]
        BoolExpr optional = cdContext.getContext().mkForall(new Expr[]{l1, r1, r2},
          cdContext.getContext().mkImplies(
            cdContext.getContext().mkAnd(
              cdContext.getContext().mkApp(smtAssociation.getAssocFunc(), l1, r1),
              cdContext.getContext().mkApp(smtAssociation.getAssocFunc(), l1, r2)),
            cdContext.getContext().mkEq(r1, r2)),
          0, null, null, null, null);

        if (myAssoc.getRight().getCDCardinality().isAtLeastOne()) {
          constraints.add(new ImmutablePair<>(assocName+"_right",atLeastOne));
        } else if (myAssoc.getRight().getCDCardinality().isOpt()) {
          constraints.add(new ImmutablePair<>(assocName+"_right",optional));
        } else if (myAssoc.getRight().getCDCardinality().isOne()) {
          constraints.add(new ImmutablePair<>(assocName+ "_right" ,cdContext.getContext().mkAnd(atLeastOne, optional)));
        }
      }

      //Cardinality on the left side
      if (myAssoc.getLeft().isPresentCDCardinality()) {

        //[1..*]
        BoolExpr atLeastOne = cdContext.getContext().mkForall(new Expr[]{r1},
          cdContext.getContext().mkExists(new Expr[]{l1},
            cdContext.getContext().mkApp(smtAssociation.getAssocFunc(), l1, r1),
            0, null, null, null, null), 0,
          null, null, null, null);

        //[1..0]
        BoolExpr optional = cdContext.getContext().mkForall(new Expr[]{l1, r1, l2},
          cdContext.getContext().mkImplies(
            cdContext.getContext().mkAnd(
              cdContext.getContext().mkApp(smtAssociation.getAssocFunc(), l1, r1),
              cdContext.getContext().mkApp(smtAssociation.getAssocFunc(), l2, r1)),
            cdContext.getContext().mkEq(l2, l1)),
          0, null, null, null, null);

        if (myAssoc.getLeft().getCDCardinality().isAtLeastOne()) {
          constraints.add(new ImmutablePair<>(assocName + "_left",atLeastOne));
        } else if (myAssoc.getLeft().getCDCardinality().isOpt()) {
          constraints.add(new ImmutablePair<>(assocName+ "_left",optional));
        } else if (myAssoc.getLeft().getCDCardinality().isOne()) {
          constraints.add(new ImmutablePair<>(assocName + "_left", cdContext.getContext().mkAnd(atLeastOne, optional)));
        }
      }
    }
    return constraints;
  }
  //-----------------------------------inheritance----------------------------------------------------------------------
  List<Pair<String,BoolExpr>> buildInheritanceConstraints(CDContext cdContext, ASTCDDefinition cd) {
    List<Pair<String,BoolExpr>> constraints = new LinkedList<>();
    for (ASTCDClass myClass : cd.getCDClassesList()) {
      if (!myClass.getSuperclassList().isEmpty()) {
        //(declare-fun convert_to_A (B_obj) A_obj)
        Optional<ASTCDClass> superClass = Optional.of(CDHelper.getClass(myClass.getSuperclassList().get(0).
          printType(new MCBasicTypesFullPrettyPrinter(new IndentPrinter())), cd));

        //add declare  the function to the smt representation of the subclass
        cdContext.getSmtClasses().get(myClass).setConvert2Superclass(cdContext.getContext().mkFuncDecl(
          "Convert_" + myClass.getName() + "_to_" + superClass.get().getName(),
          cdContext.getSmtClasses().get(myClass).getSort(), cdContext.getSmtClasses().get(superClass.get()).getSort()));

        //constraints to make sure that the function convert_to will be defined
        Expr<Sort> subclassObj = cdContext.getContext().mkConst(myClass.getName() + "scc", cdContext.getSmtClasses().
          get(myClass).getSort());
        Expr<Sort> superClassObj = cdContext.getContext().mkConst(superClass.get().
          getName() + "scc", cdContext.getSmtClasses().get(superClass.get()).getSort());

        BoolExpr constrConvert = cdContext.getContext().mkForall(new Expr[]{subclassObj}, cdContext
            .getContext().mkExists(new Expr[]{superClassObj},
              cdContext.getContext().mkEq(cdContext.getContext().mkApp(cdContext.getSmtClasses()
                .get(myClass).getConvert2Superclass(), subclassObj), superClassObj), 0, null,
              null, null, null), 0,
          null, null, null, null);
        constraints.add(new ImmutablePair<>(myClass.getName()+"inher",constrConvert));


        //the conversion must be a bijection
        Expr<Sort> b1 = cdContext.getContext().mkConst("b1", cdContext.getSmtClasses().get(myClass).getSort());
        Expr<Sort> b2 = cdContext.getContext().mkConst("b2", cdContext.getSmtClasses().get(myClass).getSort());
        BoolExpr bijection = cdContext.getContext().mkForall(new Expr[]{b1, b2}, cdContext.getContext().mkEq(
            cdContext.getContext().mkEq(cdContext.getContext().mkApp(cdContext.getSmtClasses().get(myClass).
                getConvert2Superclass(), b1),
              cdContext.getContext().mkApp(cdContext.getSmtClasses().get(myClass).getConvert2Superclass(), b2)), cdContext
              .getContext().mkEq(b1, b2)),
          0, null, null, null, null);
        constraints.add(new ImmutablePair<>(myClass.getName()+"inhrbijection",bijection));

        Expr<Sort> b3 = cdContext.getContext().mkConst("b3", cdContext.getSmtClasses().get(myClass).getSort());

        Expr<? extends Sort> sort = cdContext.getContext().mkConst(cdContext.getSmtClasses().get(superClass.get())
          .getSubClassConstrList().get(superClass.get()).ConstructorDecl());

        BoolExpr typeConstr = cdContext.getContext().mkForall(new Expr[]{b3}, cdContext.getContext().mkEq(
            cdContext.getContext().mkApp(cdContext.getSmtClasses().get(superClass.get()).getSubClass(),
              cdContext.getContext().mkApp(cdContext.getSmtClasses().get(myClass).getConvert2Superclass(), b1)), sort),
          0, null, null, null, null);
        constraints.add(new ImmutablePair<>(myClass.getName()+"inhrTypeCheck",typeConstr));
      }
    }
    return constraints;
  }
}









