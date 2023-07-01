package de.monticore.cd2smt.cd2smtGenerator.classStrategies.finiteDs;

import com.microsoft.z3.*;
import de.monticore.cd2smt.Helper.CDHelper;
import de.monticore.cd2smt.ODArtifacts.MinObject;
import de.monticore.cd2smt.cd2smtGenerator.classStrategies.distinctSort.DSClassStrategy;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import java.util.*;
import java.util.function.Function;

public class FiniteDSClassStrategy extends DSClassStrategy {
  protected final int MAX = 5; // FIXME: 13.06.2023 delete
  Map<ASTCDType, Integer> cardMap = new HashMap<>();

  @Override
  public void cd2smt(ASTCDCompilationUnit ast, Context context) {
    this.ast = ast;
    this.ctx = context;
    initCardinalities();
    super.cd2smt(ast, context);
  }

  public FiniteDSClassStrategy() {
    typeMap = new HashMap<>();
  }

  @Override
  protected Sort declareSort(ASTCDType astcdType) {
    Constructor[] constructor = new Constructor[cardMap.get(astcdType)];
    for (int i = 0; i < cardMap.get(astcdType); i++) {
      constructor[i] = mkconstructor(astcdType.getName() + "_" + i);
    }
    return ctx.mkDatatypeSort(printSMTCDTypeName(astcdType), constructor);
  }

  protected Constructor<Sort> mkconstructor(String name) {
    return ctx.mkConstructor(name, name, null, null, null);
  }

  protected Expr<?> mkConst(int index, ASTCDType astcdType) {
    return ctx.mkConst(((DatatypeSort<?>) getSort(astcdType)).getConstructors()[index]);
  }

  @Override
  public BoolExpr mkForall(ASTCDType type, Expr<?> var, Function<Expr<?>, BoolExpr> body) {
    BoolExpr res = ctx.mkTrue();
    for (int i = 0; i < cardMap.get(type); i++) {
      Expr<?> var2 = mkConst(i, type);
      res = ctx.mkAnd(res, body.apply(var2));
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

  public void initCardinalities() {

    for (ASTCDType astcdType : CDHelper.getASTCDTypes(ast.getCDDefinition())) {
      cardMap.put(astcdType, 5);
    }
  }
}
