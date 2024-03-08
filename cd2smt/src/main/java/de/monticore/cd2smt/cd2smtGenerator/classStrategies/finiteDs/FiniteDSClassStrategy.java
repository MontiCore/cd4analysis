package de.monticore.cd2smt.cd2smtGenerator.classStrategies.finiteDs;

import com.microsoft.z3.*;
import de.monticore.cd2smt.ODArtifacts.MinObject;
import de.monticore.cd2smt.cd2smtGenerator.CD2SMTMill;
import de.monticore.cd2smt.cd2smtGenerator.classStrategies.distinctSort.DSClassStrategy;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import java.util.*;

public class FiniteDSClassStrategy extends DSClassStrategy {

  private final Map<ASTCDType, Integer> cardMap;

  @Override
  public void cd2smt(ASTCDCompilationUnit ast, Context context) {
    this.ast = ast;
    this.ctx = context;
    super.cd2smt(ast, context);
  }

  public FiniteDSClassStrategy() {
    typeMap = new HashMap<>();
    this.cardMap = CD2SMTMill.getCardinalities();
  }

  @Override
  protected Sort declareSort(ASTCDType astcdType) {

    int size = cardMap.get(astcdType);
    if (size > 0) {
      Constructor[] constructor = new Constructor[size];
      for (int i = 0; i < cardMap.get(astcdType); i++) {
        constructor[i] = mkconstructor(astcdType.getName() + "_" + i);
      }
      return ctx.mkDatatypeSort(printSMTCDTypeName(astcdType), constructor);

    } else {
      return super.declareSort(astcdType);
      // TODO: 19.06.2023 fixme this is a temporary solution
    }
  }

  protected Constructor<Sort> mkconstructor(String name) {
    return ctx.mkConstructor(name, name, null, null, null);
  }

  protected Expr<?> mkConst(int index, ASTCDType type) {
    return ctx.mkConst(((DatatypeSort<?>) getSort(type)).getConstructors()[index]);
  }

  protected List<Expr<?>> generateTypeUniverse(ASTCDType type) {
    List<Expr<?>> universe = new ArrayList<>();
    for (int i = 0; i < cardMap.get(type); i++) {
      universe.add(mkConst(i, type));
    }
    return universe;
  }

  @Override
  public BoolExpr mkForall(ASTCDType type, Expr<?> var, BoolExpr body) {
    List<Expr<?>> typeUniverses = generateTypeUniverse(type);
    BoolExpr res = ctx.mkTrue();

    for (Expr<?> param : typeUniverses) {
      res = ctx.mkAnd(res, body.substitute(var, param));
    }

    return res;
  }

  @Override
  public Set<MinObject> smt2od(Model model, Boolean partial) {
    Set<MinObject> objectSet = new HashSet<>();

    for (Map.Entry<ASTCDType, Sort> entry : typeMap.entrySet()) {
      if (!(entry.getKey() instanceof ASTCDEnum)) {
        for (int i = 0; i < cardMap.get(entry.getKey()); i++) {
          Expr<?> smtExpr = mkConst(i, entry.getKey());
          objectSet.add(buildObject(entry.getValue(), smtExpr, model, partial));
        }
      }
    }
    return objectSet;
  }
}
