package de.monticore.cd2smt.context;

import de.monticore.cd2smt.Helper.CDHelper;
import com.microsoft.z3.*;
import com.microsoft.z3.enumerations.Z3_lbool;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.se_rwth.commons.logging.Log;

import java.util.*;

public class ODContext {
  private final Map<Expr<Sort>, SMTObject> objectMap;

  protected void addObject(Expr<Sort> expr, SMTObject obj) {
    objectMap.put(expr, obj);
  }

  public Map<Expr<Sort>, SMTObject> getObjectMap() {
    return objectMap;
  }


  public ODContext (CDContext cdContext, ASTCDDefinition cd) {
    objectMap = new HashMap<>();
    List<Expr<? extends Sort>> objToDelete = new ArrayList<>();


    //add constraints and get the Model
    List<BoolExpr> constraints = cdContext.getClassConstrs();
    constraints.addAll(cdContext.getInherConstr());
    constraints.addAll(cdContext.getAssocConstr());
    Optional<Model> modelOpt = getModel(cdContext.getContext(), constraints);
    if (modelOpt.isEmpty()) {
      Log.error("************Model not found************Model not found************Model not found************Model not found");
    }
    assert modelOpt.isPresent();
    Model model = modelOpt.get();

    //get all objects
    for (Sort mySort : model.getSorts()) {
      for (Expr<Sort> smtExpr : model.getSortUniverse(mySort)) {
        SMTObject obj = new SMTObject(smtExpr);
        for (FuncDecl<Sort> func : cdContext.getSmtClasses().get(CDHelper.getClass(mySort.toString().split("_")[0], cd)).getAttributes()) {
          obj.addAttribute(func, model.eval(func.apply(smtExpr), true));
        }
        addObject(smtExpr, obj);
      }
    }


    //get link between Objects
    for (SMTAssociation assoc : cdContext.getSMTAssociations().values()) {
      Sort leftSort = assoc.getAssocFunc().getDomain()[0];
      Sort rightSort = assoc.getAssocFunc().getDomain()[1];

      for (SMTObject leftObj : objectMap.values()) {
        for (SMTObject rightObj : objectMap.values()) {
          if ((leftObj.hasSort(leftSort)) && (rightObj.hasSort(rightSort))) {
            if ((model.eval(assoc.getAssocFunc().apply(leftObj.getSmtExpr(), rightObj.getSmtExpr()), true).getBoolValue() == Z3_lbool.Z3_L_TRUE)) {
              leftObj.getLinkedObjects().add(new LinkedSMTObject(rightObj, assoc, false));
            }
          }
        }
      }
    }

    //get the superclass instances
    for (SMTObject obj : objectMap.values()) {
      FuncDecl<UninterpretedSort> converTo = cdContext.getSmtClasses().
        get(CDHelper.getClass(obj.getSmtExpr().getSort().toString().
          split("_")[0], cd)).getConvert2Superclass();
      if (converTo != null) {
        Expr<? extends Sort> subObj = model.eval(converTo.apply(obj.getSmtExpr()), true);
        obj.setSuperClass(objectMap.get(subObj));
        objToDelete.add(subObj);
      }
    }
    ////remove the subclass instances and their links
    for (Expr<? extends Sort> expr : objToDelete) {
      objectMap.remove(expr);
    }


  }

 public  static   Optional<Model> getModel (Context ctx, List < BoolExpr > constraints){
      Solver s = ctx.mkSolver();
      for (BoolExpr expr : constraints)
        s.add(expr);
      if (s.check() == Status.SATISFIABLE)
        return Optional.of(s.getModel());
      else {
        return Optional.empty();
      }

  }


}
