package de.monticore.cd2smt.cd2smtGenerator;

import com.microsoft.z3.*;
import de.monticore.cd2smt.context.CDContext;
import de.monticore.cd2smt.context.SMTClass;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;

import java.util.*;

public class CD2SMTGenerator {

  public CD2SMTGenerator() {
  }


  /**
   * declared an object diagram in a SMT Context
   *
   * @param cd        the class diagram to declared
   * @return the context
   */
  public CDContext cd2smt(ASTCDDefinition cd) {
    //setup
    CDContext cdContext = new CDContext() ;
    Map<String, String> cfg = new HashMap<>();
    cfg.put("model", "true");
    cfg.put("proof", "true");
    cdContext.setContext( new Context(cfg));

    //declare all classes
    for (ASTCDClass myclass : cd.getCDClassesList())
      cdContext = declareClass(cdContext, cd, myclass);

    //declare  all associations
    for (ASTCDAssociation myAssociation : cd.getCDAssociationsList())
      cdContext = declareAssociation(cdContext, myAssociation, cd);

    cdContext.setClassConstrs(buildClassConstraint(cdContext, cd));
    cdContext.setAssocConstr(buildAssocConstraints(cdContext, cd));
    cdContext.setInherConstr( buildInheritanceConstraints(cdContext, cd));
    return cdContext;
  }


  //-----------------------------------Class--declaration---------------------------------------------------------------
  protected CDContext declareClass(CDContext cdContext, ASTCDDefinition cd, ASTCDClass myClass) {
    SMTClass smtClass = new SMTClass();

    //(declare-sort A_obj 0)
    String className = cdContext.printSMTClassName(myClass);
    UninterpretedSort classSort = cdContext.getContext().mkUninterpretedSort(cdContext.getContext().mkSymbol(className));
    smtClass.setSort( classSort);

    //(declare-datatype B_subclasses ((TT_NO_SUBTYPE) (TT_B)))
    List<ASTCDClass> subclassList = cdContext.getSubclassList(cd, myClass);
    Constructor[] constructors = new Constructor[subclassList.size() + 1];
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
    smtClass.setSubClass( cdContext.getContext().mkFuncDecl(cdContext.
      printSubclassFuncName(myClass), classSort, smtClass.getSubclassDatatype()));

    //(declare-fun a_attrib_something (A_obj) String)
    for (ASTCDAttribute myAttribute : myClass.getCDAttributeList()) {
      String attribName = cdContext.printAttributeNameSMT(myClass, myAttribute);
      FuncDecl<Sort> attributeFunc = cdContext.getContext().mkFuncDecl(attribName, classSort, cdContext.
        parseAttribType2SMT(cdContext.getContext(), myAttribute));
      smtClass.getAttributes().add(attributeFunc);
    }
    cdContext.getSmtClasses().put(myClass, smtClass);
    return cdContext;
  }

  /**
   * add constraints to the solver to make sure that
   * all attribute of each function will be defined
   *
   * @param cdContext the context where the class diagram are already declared
   * @param cd        the class diagram
   * @return the list of constraint
   */
  List<BoolExpr> buildClassConstraint(CDContext cdContext, ASTCDDefinition cd) {
    List<BoolExpr> res = new LinkedList<>();
    for (ASTCDClass myClass : cd.getCDClassesList()) {

      Sort classSort = cdContext.getSmtClasses().get(myClass).getSort();
      Expr<Sort> classObj = cdContext.getContext().mkConst(myClass.getName() + "1", classSort);

      for (FuncDecl<Sort> attrFunc : cdContext.getSmtClasses().get(myClass).getAttributes()) {
        Expr<Sort> attrObj = cdContext.getContext().mkConst(myClass.getName() + "2", attrFunc.getRange());

        BoolExpr constr = cdContext.getContext().mkForall(new Expr[]{classObj}, cdContext.getContext().mkExists(new Expr[]{attrObj},
            cdContext.getContext().mkEq(cdContext.getContext().mkApp(attrFunc, classObj), attrObj), 0, null,
            null, null, null), 0,
          null, null, null, null);
        res.add(constr);
      }
    }
    return res;
  }

  //-----------------------------------Association------------------------------------------------------------------------
  protected CDContext declareAssociation(CDContext cdContext, ASTCDAssociation myAssociation, ASTCDDefinition cd) {
    //(declare-fun bc_assoc (B_obj C_obj) Bool)
    Sort rightSortSMT = cdContext.getSmtClasses().get(cdContext.getClass(myAssociation.getRightQualifiedName().getQName(), cd).get()).getSort();
    Sort leftSortSMT = cdContext.getSmtClasses().get(cdContext.getClass(myAssociation.getLeftQualifiedName().getQName(), cd).get()).getSort();
    String assocName = cdContext.printSMTAssociationName(myAssociation);
    FuncDecl<BoolSort> assocFunc = cdContext.getContext().mkFuncDecl(assocName, new Sort[]{leftSortSMT, rightSortSMT}, cdContext.getContext().getBoolSort());
    cdContext.getAssocFunctions().put(myAssociation, assocFunc);

    return cdContext;
  }

  List<BoolExpr> buildAssocConstraints(CDContext cdContext, ASTCDDefinition cd) {
    List<BoolExpr> constraints = new LinkedList<>();

    for (ASTCDAssociation myAssoc : cd.getCDAssociationsList()) {
      Sort rightSortSMT = cdContext.getSmtClasses().get(cdContext.getClass(myAssoc.getRightQualifiedName().getQName(), cd).get()).getSort();
      Sort leftSortSMT = cdContext.getSmtClasses().get(cdContext.getClass(myAssoc.getLeftQualifiedName().getQName(), cd).get()).getSort();
      String assocName = cdContext.printSMTAssociationName(myAssoc);
      FuncDecl assocFunc = cdContext.getAssocFunctions().get(myAssoc);

      //Cardinality on the right side
      if (myAssoc.getRight().isPresentCDCardinality()) {
        Expr<Sort> r1 = cdContext.getContext().mkConst(assocName + "r1", rightSortSMT);
        Expr<Sort> l1 = cdContext.getContext().mkConst(assocName + "l1", leftSortSMT);

        //[1..*]
        BoolExpr atLeastOne = cdContext.getContext().mkForall(new Expr[]{l1},
          cdContext.getContext().mkExists(new Expr[]{r1},
            cdContext.getContext().mkApp(assocFunc, l1, r1),
            0, null, null, null, null), 0,
          null, null, null, null);
        //[1..0]
        Expr<Sort> r2 = cdContext.getContext().mkConst(assocName + "r2", rightSortSMT);
        BoolExpr optional = cdContext.getContext().mkForall(new Expr[]{l1, r1, r2},
          cdContext.getContext().mkImplies(
            cdContext.getContext().mkAnd(
              cdContext.getContext().mkApp(assocFunc, l1, r1),
              cdContext.getContext().mkApp(assocFunc, l1, r2)),
            cdContext.getContext().mkEq(r1, r2)),
          0, null, null, null, null);

        if (myAssoc.getRight().getCDCardinality().isAtLeastOne())
          constraints.add(atLeastOne);
        else if (myAssoc.getRight().getCDCardinality().isOpt())
          constraints.add(optional);
        else if (myAssoc.getRight().getCDCardinality().isOne())
          constraints.add(cdContext.getContext().mkAnd(atLeastOne, optional));
      }


      //Cardinality on the left side
      if (myAssoc.getLeft().isPresentCDCardinality()) {
        Expr<Sort> rr1 = cdContext.getContext().mkConst(assocName + "r11", rightSortSMT);
        Expr<Sort> ll1 = cdContext.getContext().mkConst(assocName + "l11", leftSortSMT);

        //[1..*]
        BoolExpr atLeastOne = cdContext.getContext().mkForall(new Expr[]{rr1},
          cdContext.getContext().mkExists(new Expr[]{ll1},
            cdContext.getContext().mkApp(assocFunc, ll1, rr1),
            0, null, null, null, null), 0,
          null, null, null, null);
        //[1..0]
        Expr<Sort> l2 = cdContext.getContext().mkConst(assocName + "l2", leftSortSMT);
        BoolExpr optional = cdContext.getContext().mkForall(new Expr[]{ll1, rr1, l2},
          cdContext.getContext().mkImplies(
            cdContext.getContext().mkAnd(
              cdContext.getContext().mkApp(assocFunc, ll1, rr1),
              cdContext.getContext().mkApp(assocFunc, l2, rr1)),
            cdContext.getContext().mkEq(l2, ll1)),
          0, null, null, null, null);

        if (myAssoc.getLeft().getCDCardinality().isAtLeastOne())
          constraints.add(atLeastOne);
        else if (myAssoc.getLeft().getCDCardinality().isOpt())
          constraints.add(optional);
        else if (myAssoc.getLeft().getCDCardinality().isOne())
          constraints.add(cdContext.getContext().mkAnd(atLeastOne, optional));
      }

    }


    return constraints;
  }

  //-----------------------------------inheritance----------------------------------------------------------------------
  List<BoolExpr> buildInheritanceConstraints(CDContext cdContext, ASTCDDefinition cd) {
    List<BoolExpr> constraints = new LinkedList<>();
    for (ASTCDClass myClass : cd.getCDClassesList()) {
      if (!myClass.getSuperclassList().isEmpty()) {
        //convert an object to an object of the superclass
        //(declare-fun convert_to_A (B_obj) A_obj)
        Optional<ASTCDClass> superClass = cdContext.getClass(myClass.getSuperclassList().get(0).
          printType(new MCBasicTypesFullPrettyPrinter(new IndentPrinter())), cd);

        //add declare  the function to the smt representation of the subclass
        cdContext.getSmtClasses().get(myClass).setConvert2Superclass( Optional.of(cdContext.getContext().mkFuncDecl(
          "Convert_" + myClass.getName() + "_to_" + superClass.get().getName(),
          cdContext.getSmtClasses().get(myClass).getSort(), cdContext.getSmtClasses().get(superClass.get()).getSort())));

        //constraints to make sure that the function convert_to will be defined
        Expr<Sort> subclassObj = cdContext.getContext().mkConst(myClass.getName() + "scc", cdContext.getSmtClasses().
          get(myClass).getSort());
        Expr<Sort> superClassObj = cdContext.getContext().mkConst(superClass.get().
          getName() + "scc", cdContext.getSmtClasses().get(superClass.get()).getSort());

        BoolExpr constr = cdContext.getContext().mkForall(new Expr[]{subclassObj}, cdContext
            .getContext().mkExists(new Expr[]{superClassObj},
              cdContext.getContext().mkEq(cdContext.getContext().mkApp(cdContext.getSmtClasses()
                .get(myClass).getConvert2Superclass().get(), subclassObj), superClassObj), 0, null,
              null, null, null), 0,
          null, null, null, null);
        constraints.add(constr);


        //the conversion must be bijectif
        Expr<Sort> b1 = cdContext.getContext().mkConst("b1", cdContext.getSmtClasses().get(myClass).getSort());
        Expr<Sort> b2 = cdContext.getContext().mkConst("b2", cdContext.getSmtClasses().get(myClass).getSort());
        BoolExpr bijektiv = cdContext.getContext().mkForall(new Expr[]{b1, b2}, cdContext.getContext().mkEq(
            cdContext.getContext().mkEq(cdContext.getContext().mkApp(cdContext.getSmtClasses().get(myClass).
                getConvert2Superclass().get(), b1),
              cdContext.getContext().mkApp(cdContext.getSmtClasses().get(myClass).getConvert2Superclass().get(), b2)), cdContext
              .getContext().mkEq(b1, b2)),
          0, null, null, null, null);
        constraints.add(bijektiv);

        Expr<Sort> b3 = cdContext.getContext().mkConst("b3", cdContext.getSmtClasses().get(myClass).getSort());

        Expr x = cdContext.getContext().mkConst(cdContext.getSmtClasses().get(superClass.get())
          .getSubClassConstrList().get(superClass.get()).ConstructorDecl());

        BoolExpr typeConstr = cdContext.getContext().mkForall(new Expr[]{b3}, cdContext.getContext().mkEq(
            cdContext.getContext().mkApp(cdContext.getSmtClasses().get(superClass.get()).getSubClass(),
              cdContext.getContext().mkApp(cdContext.getSmtClasses().get(myClass).getConvert2Superclass().get(), b1)), x),
          0, null, null, null, null);
        constraints.add(bijektiv);
        constraints.add(typeConstr);
      }

    }
    return constraints;
  }

}









