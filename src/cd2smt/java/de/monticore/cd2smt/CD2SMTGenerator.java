package de.monticore.cd2smt;


import com.microsoft.z3.*;
import com.microsoft.z3.enumerations.Z3_lbool;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.odbasis._ast.ASTODArtifact;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;

import java.util.*;

public class CD2SMTGenerator {

  protected Map<ASTCDClass, UninterpretedSort> sorts;
  protected Map<ASTCDAttribute, FuncDecl<Sort>> attribFunctions;
  protected Map<ASTCDAssociation, FuncDecl<BoolSort>> assocFunctions;


  public CD2SMTGenerator() {
    sorts = new HashMap<>();
    attribFunctions = new HashMap<>();
    assocFunctions = new HashMap<>();
  }


  Optional<Model> getModel(Context ctx, List<BoolExpr> constraints) {
    Solver s = ctx.mkSolver();
    for (BoolExpr expr : constraints)
      s.add(expr);
    if (s.check() == Status.SATISFIABLE)
      return Optional.of(s.getModel());
    else {
      System.out.println("UNSAT--UNSAT--UNSAT--UNSAT--UNSAT--");
      return Optional.empty();
    }
  }

  protected Optional<ASTODArtifact> cd2od(ASTCDDefinition cd) {
    Context ctx = cd2smt(new Context(), cd);
    SMT2ODGenerator smtOdGenerator = new SMT2ODGenerator();

    //add constraints and get the Model
    List<BoolExpr> constraints = makeSimpleConstraint(ctx, cd);
    constraints.addAll(makeAssocConstraints(ctx, cd));
    Model model = getModel(ctx, constraints).get();

    //get all objects
    for (Sort mySort : model.getSorts()) {
      Expr<Sort> universes[] = model.getSortUniverse(mySort);
      for (Expr<Sort> element : universes) {//for All defined sorts
        SMT2ODGenerator.SMTObject obj = new SMT2ODGenerator.SMTObject();
        for (FuncDecl func : model.getFuncDecls()) {  //for all funcs
          if (func.getArity() == 1 && func.getDomain()[0].equals(mySort)) { //if the func pass
            obj.addAttribute(func, model.eval(func.apply(element), true));
          }
          obj.name = element.toString();
          obj.type = mySort;
          obj.smtExpr = element;
        }
        smtOdGenerator.addObject(obj);
      }
    }

    //get link between Objects
    for (Map.Entry<ASTCDAssociation, FuncDecl<BoolSort>> assoc : assocFunctions.entrySet()) {
      Sort leftSort = assoc.getValue().getDomain()[0];
      Sort rightSort = assoc.getValue().getDomain()[1];

      for (SMT2ODGenerator.SMTObject leftObj : smtOdGenerator.objectSet)
        for (SMT2ODGenerator.SMTObject rightObj : smtOdGenerator.objectSet)
          if ((leftSort.equals(leftObj.smtExpr.getSort()) )&&
              (rightObj.smtExpr.getSort().equals(rightSort)) &&
            (model.eval(assoc.getValue().apply(leftObj.smtExpr, rightObj.smtExpr),
            true).getBoolValue() == Z3_lbool.Z3_L_TRUE) ) {

            smtOdGenerator.addLink(leftObj, rightObj);
          }
    }
  //  System.out.println(model);
    return Optional.of(smtOdGenerator.buildOd());
  }

  protected Context cd2smt(Context ctx, ASTCDDefinition cd) {
    //setup
    Map<String, String> cfg = new HashMap<>();
    cfg.put("model", "true");
    cfg.put("proof", "true");
    ctx = new Context(cfg);

    //declare all classes
    for (ASTCDClass myclass : cd.getCDClassesList())
      ctx = declareClassSMT(ctx, cd, myclass);

    //declare  all associations
    for (ASTCDAssociation myAssociation : cd.getCDAssociationsList())
      ctx = declareAssociationSMT(ctx, myAssociation, cd);

    return ctx;
  }

  //temporary to produce Models
  List<BoolExpr> makeSimpleConstraint(Context ctx, ASTCDDefinition cd) {
    List<BoolExpr> res = new LinkedList<>();
    for (ASTCDClass myClass : cd.getCDClassesList()) {

      Sort classSort = sorts.get(myClass);
      Expr<Sort> object1 = ctx.mkConst(myClass.getName() + "1", classSort);
      Expr<Sort> object2 = ctx.mkConst(myClass.getName() + "2", classSort);

      for (ASTCDAttribute myAttribute : myClass.getCDAttributeList()) {
        Expr<BoolSort> body = (ctx.mkEq(ctx.mkApp(attribFunctions.get(myAttribute), object1),
          ctx.mkApp(attribFunctions.get(myAttribute), object2)));
        BoolExpr constr = ctx.mkForall(new Expr[]{object1, object2}, body, 0,
          null, null, null, null);
        res.add(constr);
      }
    }

    return res;
  }

  protected Context declareClassSMT(Context ctx, ASTCDDefinition cd, ASTCDClass myClass) {
    //(declare-sort A_obj 0)
    String className = printClassNameSMT(myClass);
    UninterpretedSort classSort = ctx.mkUninterpretedSort(ctx.mkSymbol(className));
    sorts.put(myClass, classSort);
    ctx.mkConst(myClass.getName() + "obj", classSort);


    //(declare-fun a_attrib_something (A_obj) String)
    for (ASTCDAttribute myAttribute : myClass.getCDAttributeList()) {
      String attribName = printAttributeNameSMT(myClass, myAttribute);
      FuncDecl<Sort> attributeFunc = ctx.mkFuncDecl(attribName, classSort, parseAttribType2SMT(ctx, myAttribute));
      attribFunctions.put(myAttribute, attributeFunc);
    }
    return ctx;
  }

  protected Context declareAssociationSMT(Context ctx, ASTCDAssociation myAssociation, ASTCDDefinition cd) {
    //(declare-fun bc_assoc (B_obj C_obj) Bool)
    Sort rightSortSMT = sorts.get(getClass(myAssociation.getRightQualifiedName().getQName(), cd).get());
    Sort leftSortSMT = sorts.get(getClass(myAssociation.getLeftQualifiedName().getQName(), cd).get());
    String assocName = printAssociationNameSMT(myAssociation);
    FuncDecl<BoolSort> assocFunc = ctx.mkFuncDecl(assocName, new Sort[]{leftSortSMT, rightSortSMT}, ctx.getBoolSort());
    assocFunctions.put(myAssociation, assocFunc);

    return ctx;
  }

  List<BoolExpr> makeAssocConstraints(Context ctx, ASTCDDefinition cd) {
    List<BoolExpr> constraints = new LinkedList<>();

      for (ASTCDAssociation myAssoc : cd.getCDAssociationsList()) {
        Sort rightSortSMT = sorts.get(getClass(myAssoc.getRightQualifiedName().getQName(), cd).get());
        Sort leftSortSMT = sorts.get(getClass(myAssoc.getLeftQualifiedName().getQName(), cd).get());
        String assocName = printAssociationNameSMT(myAssoc);
        FuncDecl assocFunc = assocFunctions.get(myAssoc);

        //Cardinality on the right side
        if (myAssoc.getRight().isPresentCDCardinality()) {
          Expr<Sort> r1 = ctx.mkConst(assocName + "r1", rightSortSMT);
          Expr<Sort> l1 = ctx.mkConst(assocName + "l1", leftSortSMT);

          //[1..*]
          BoolExpr atLeastOne = ctx.mkForall(new Expr[]{l1},
            ctx.mkExists(new Expr[]{r1},
              ctx.mkApp(assocFunc, l1, r1),
              0, null, null, null, null), 0,
            null, null, null, null);
          //[1..0]
          Expr<Sort> r2 = ctx.mkConst(assocName + "r2", rightSortSMT);
          BoolExpr optional = ctx.mkForall(new Expr[]{l1, r1, r2},
            ctx.mkImplies(
              ctx.mkAnd(
                ctx.mkApp(assocFunc, l1, r1),
                ctx.mkApp(assocFunc, l1, r2)),
              ctx.mkEq(r1, r2)),
            0, null, null, null, null);

          if (myAssoc.getRight().getCDCardinality().isAtLeastOne())
            constraints.add(atLeastOne);
          else if (myAssoc.getRight().getCDCardinality().isOpt())
            constraints.add(optional);
          else if (myAssoc.getRight().getCDCardinality().isOne())
            constraints.add(ctx.mkAnd(atLeastOne, optional));
        }


        //Cardinality on the right side
        if (myAssoc.getLeft().isPresentCDCardinality()) {
          Expr<Sort> r1 = ctx.mkConst(assocName + "r11", rightSortSMT);
          Expr<Sort> l1 = ctx.mkConst(assocName + "l11", leftSortSMT);

          //[1..*]
          BoolExpr atLeastOne = ctx.mkForall(new Expr[]{r1},
            ctx.mkExists(new Expr[]{l1},
              ctx.mkApp(assocFunc, l1, r1),
              0, null, null, null, null), 0,
            null, null, null, null);
          //[1..0]
          Expr<Sort> l2 = ctx.mkConst(assocName + "r2", leftSortSMT);
          BoolExpr optional = ctx.mkForall(new Expr[]{l1, r1, l2},
            ctx.mkImplies(
              ctx.mkAnd(
                ctx.mkApp(assocFunc, l1, r1),
                ctx.mkApp(assocFunc, l2, r1)),
              ctx.mkEq(l2, l1)),
            0, null, null, null, null);

          if (myAssoc.getLeft().getCDCardinality().isAtLeastOne())
            constraints.add(atLeastOne);
          else if (myAssoc.getLeft().getCDCardinality().isOpt())
            constraints.add(optional);
          else if (myAssoc.getLeft().getCDCardinality().isOne())
            constraints.add(ctx.mkAnd(atLeastOne, optional));
        }

      }



    return constraints;
  }

  protected List<ASTCDClass> getSubclassList(ASTCDDefinition cd, ASTCDClass myClass) {
    List<ASTCDClass> subclasses = new LinkedList<>();
    for (ASTCDClass entry : cd.getCDClassesList()) {
      for (ASTMCObjectType entry2 : entry.getSuperclassList()) {
        if (entry2.printType(new MCBasicTypesFullPrettyPrinter(new IndentPrinter())).equals(myClass.getName()))
          subclasses.add(entry);
      }
    }
    return subclasses;
  }

  protected String printSubclassDatatypeName(ASTCDClass myClass) {
    return myClass.getName() + "_subclasses";
  }

  protected String printAttributeNameSMT(ASTCDClass myClass, ASTCDAttribute myAttribute) {
    return fCharToLowerCase(myClass.getName()) + "_attrib_" + myAttribute.getName();
  }

  protected String printSubclassFuncName(ASTCDClass myClass) {
    return fCharToLowerCase(myClass.getName()) + "_get_subclass";
  }

  protected String printClassNameSMT(ASTCDClass myClass) {
    return myClass.getName() + "_obj";
  }

  protected String printAssociationNameSMT(ASTCDAssociation myAssociation) {
    String right = myAssociation.getRight().getName();
    String left = myAssociation.getLeft().getName();
    return fCharToLowerCase(left) + "_" + fCharToLowerCase(right) + "_assoc";
  }

  protected String fCharToLowerCase(String str) {
    return Character.toLowerCase(str.charAt(0)) + str.substring(1);
  }

  protected Sort parseAttribType2SMT(Context ctx, ASTCDAttribute myAtribute) {
    String att = myAtribute.printType();
    switch (att) {
      case "boolean":
        return ctx.mkBoolSort();
      case "int":
        return ctx.mkIntSort();
      case "double":
        return ctx.mkRealSort();
      case "java.lang.String":
        return ctx.mkStringSort();
      default:
        System.out.println("type not support \n interpret like a String");
        return ctx.mkStringSort();
    }
  }
  protected Optional<List<ASTCDAttribute>> getAttributeList(String className, ASTCDDefinition cd) {
    List<ASTCDAttribute> attributes = new LinkedList<>();
    for (ASTCDClass myClass : cd.getCDClassesList()) {
      if (myClass.getName().equals(className))
        attributes = myClass.getCDAttributeList();
    }
    return Optional.of(attributes);
  }

  protected Optional<ASTCDClass> getClass(String className, ASTCDDefinition cd) {
    ASTCDClass res = new ASTCDClass();
    for (ASTCDClass myClass : cd.getCDClassesList()) {
      if (myClass.getName().equals(className))
        res = myClass;
    }
    return Optional.of(res);
  }
}

// Superklasse von einer Klasse
// entry.getSuperclassList().get(0).printType(new MCBasicTypesFullPrettyPrinter(new IndentPrinter()));







