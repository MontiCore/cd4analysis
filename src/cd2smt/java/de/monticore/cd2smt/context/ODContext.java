package de.monticore.cd2smt.context;

import com.microsoft.z3.*;
import com.microsoft.z3.enumerations.Z3_lbool;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDDefinition;

import java.util.*;

public class ODContext {
  private Map<Expr<Sort>, SMTObject> objectMap;



  protected void addObject(Expr<Sort> expr, SMTObject obj) {
    objectMap.put(expr, obj);
  }

  public Map<Expr<Sort>, SMTObject> getObjectMap() {
    return objectMap;
  }

  public void setObjectMap(Map<Expr<Sort>, SMTObject> objectMap) {
    this.objectMap = objectMap;
  }

  public ODContext (CDContext cdContext, ASTCDDefinition cd) {
    objectMap = new HashMap<>();
    List<Expr> objToDelete = new ArrayList<>();


    //add constraints and get the Model
    List<BoolExpr> constraints = cdContext.getClassConstrs();
    constraints.addAll(cdContext.getInherConstr());
    constraints.addAll(cdContext.getAssocConstr());

    Optional<Model> modelopt = getModel(cdContext.getContext(), constraints);
    if (modelopt.isPresent()) {
     Model model = modelopt.get() ;
      //get all objects
      for (Sort mySort : model.getSorts()) {
        for (Expr<Sort> element : model.getSortUniverse(mySort)) {
          SMTObject obj = new SMTObject();
          for (FuncDecl func : cdContext.getSmtClasses().get(cdContext.getClass(mySort.toString().split("_")[0], cd).
            get()).getAttributes()) {
            obj.addAttribute(func, model.eval(func.apply(element), true));
            obj.setSmtExpr( element);
          }
          addObject(element, obj);
        }
      }


      //get link between Objects
      for (Map.Entry<ASTCDAssociation, FuncDecl<BoolSort>> assoc : cdContext.getAssocFunctions().entrySet()) {
        Sort leftSort = assoc.getValue().getDomain()[0];
        Sort rightSort = assoc.getValue().getDomain()[1];

        for (Map.Entry<Expr<Sort>, SMTObject> leftObj : objectMap.entrySet())
          for (Map.Entry<Expr<Sort>, SMTObject> rightObj : objectMap.entrySet())
            if ((leftSort.equals(leftObj.getValue().getSmtExpr().getSort())) &&
              (rightObj.getValue().getSmtExpr().getSort().equals(rightSort)) &&
              (model.eval(assoc.getValue().apply(leftObj.getValue().getSmtExpr(), rightObj.getValue().getSmtExpr()),
                true).getBoolValue() == Z3_lbool.Z3_L_TRUE)) {
              objectMap.get(leftObj.getKey()).getLinkedObjects().add(rightObj.getValue());

            }
      }
      //get the subclass instances
      for (Map.Entry<Expr<Sort>, SMTObject> obj : objectMap.entrySet()) {
        Optional<FuncDecl<UninterpretedSort>> converTo = cdContext.getSmtClasses().
          get(cdContext.getClass(obj.getValue().getSmtExpr().getSort().toString().
            split("_")[0], cd).get()).getConvert2Superclass();
        if (converTo.isPresent()) {
          Expr subObj = model.eval(converTo.get().apply(obj.getValue().getSmtExpr()), true);
          //add it to the subclass
          objectMap.get(obj.getKey()).setSuperClass( Optional.of(objectMap.get(subObj)));
          objToDelete.add(subObj);


        }
      }
      ////remove the subclass instances and their links
      for (Expr expr : objToDelete) {
        SMTObject obj = objectMap.get(expr);
        objectMap.remove(expr);
      }
    }
  }

    Optional<Model> getModel (Context ctx, List < BoolExpr > constraints){
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

}
