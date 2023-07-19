/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2smt.cd2smtGenerator;

import com.microsoft.z3.*;
import de.monticore.cd2smt.Helper.CDHelper;
import de.monticore.cd2smt.Helper.IdentifiableBoolExpr;
import de.monticore.cd2smt.ODArtifacts.MinObject;
import de.monticore.cd2smt.ODArtifacts.SMTObject;
import de.monticore.cd2smt.cd2smtGenerator.assocStrategies.AssociationStrategy;
import de.monticore.cd2smt.cd2smtGenerator.assocStrategies.defaultAssocStrategy.DefaultAssocStrategy;
import de.monticore.cd2smt.cd2smtGenerator.assocStrategies.one2one.One2OneAssocStrategy;
import de.monticore.cd2smt.cd2smtGenerator.classStrategies.ClassStrategy;
import de.monticore.cd2smt.cd2smtGenerator.classStrategies.distinctSort.DSClassStrategy;
import de.monticore.cd2smt.cd2smtGenerator.classStrategies.finiteDs.FiniteDSClassStrategy;
import de.monticore.cd2smt.cd2smtGenerator.classStrategies.finiteSs.FiniteSSClassStrategy;
import de.monticore.cd2smt.cd2smtGenerator.classStrategies.singleSort.SSClassStrategy;
import de.monticore.cd2smt.cd2smtGenerator.inhrStrategies.InheritanceData;
import de.monticore.cd2smt.cd2smtGenerator.inhrStrategies.InheritanceStrategy;
import de.monticore.cd2smt.cd2smtGenerator.inhrStrategies.SExpression.SEInheritanceStrategy;
import de.monticore.cd2smt.cd2smtGenerator.inhrStrategies.multExpression.MEInheritanceStrategy;
import de.monticore.cd2smt.smt2odgenerator.SMT2ODGenerator;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.odbasis._ast.ASTODArtifact;
import de.se_rwth.commons.logging.Log;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * this class Convert a class diagram into SMT using three different Strategies a class-strategy to
 * convert classes and interface an association-strategy to Convert Association an
 * inheritance-strategy to convert inheritance
 */
public class CD2SMTGenerator {

  private ClassStrategy classStrategy;
  private InheritanceStrategy inheritanceStrategy;
  private AssociationStrategy associationStrategy;
  private DataWrapper dataWrapper;
  private final SMT2ODGenerator smt2ODGenerator = new SMT2ODGenerator();
  private Context ctx;

  public CD2SMTGenerator(
      ClassStrategy.Strategy cs, InheritanceData.Strategy is, AssociationStrategy.Strategy as) {
    if (is == InheritanceData.Strategy.SE && cs != ClassStrategy.Strategy.SSCOMB) {

      Log.error(
          "The Class Strategy Single Sort Combined (SSCOMB) can only be combine with The inheritance Strategy"
              + " Single Sort Composed (SECOMB) ");
    }

    // set classtrategy
    switch (cs) {
      case DS:
        this.classStrategy = new DSClassStrategy();
        break;
      case SS:
        this.classStrategy = new SSClassStrategy();
        break;
      case SSCOMB:
        this.classStrategy = new SEInheritanceStrategy();
        break;
      case FINITEDS:
        this.classStrategy = new FiniteDSClassStrategy();
        break;
      case FINITESS:
        this.classStrategy = new FiniteSSClassStrategy();
    }

    // set association Strategy
    switch (as) {
      case ONE2ONE:
        this.associationStrategy = new One2OneAssocStrategy();
        break;
      case DEFAULT:
        this.associationStrategy = new DefaultAssocStrategy();
        break;
    }

    // set Inheritance Strategy
    switch (is) {
      case ME:
        this.inheritanceStrategy = new MEInheritanceStrategy();
        break;
      case SE:
        this.inheritanceStrategy = (SEInheritanceStrategy) (this.classStrategy);
    }
  }

  public Solver makeSolver(List<IdentifiableBoolExpr> constraints) {
    Solver solver = ctx.mkSolver();
    inheritanceStrategy
        .getInheritanceConstraints()
        .forEach(
            c -> solver.assertAndTrack(c.getValue(), ctx.mkBoolConst(String.valueOf(c.getId()))));

    associationStrategy
        .getAssociationsConstraints()
        .forEach(
            c -> solver.assertAndTrack(c.getValue(), ctx.mkBoolConst(String.valueOf(c.getId()))));

    classStrategy
        .getClassConstraints()
        .forEach(
            c -> solver.assertAndTrack(c.getValue(), ctx.mkBoolConst(String.valueOf(c.getId()))));

    constraints.forEach(
        c -> solver.assertAndTrack(c.getValue(), ctx.mkBoolConst(String.valueOf(c.getId()))));
    return solver;
  }

  /**
   * this function convert class diagram into SMT using the three different Strategies
   *
   * @param ctx the SMT Context
   * @param astCd the class diagram to translate
   */
  public void cd2smt(ASTCDCompilationUnit astCd, Context ctx) {
    this.ctx = ctx;
    // set All Associations Role
    dataWrapper = new DataWrapper(classStrategy, associationStrategy, inheritanceStrategy, astCd);
    classStrategy.cd2smt(astCd, ctx);

    inheritanceStrategy.cd2smt(astCd, ctx, classStrategy);

    associationStrategy.cd2smt(astCd, ctx, classStrategy, inheritanceStrategy);
  }

  public Sort getSort(ASTCDType astcdType) {
    return dataWrapper.getSort(astcdType);
  }

  public BoolExpr hasType(Expr<? extends Sort> expr, ASTCDType astcdType) {
    return dataWrapper.hasType(expr, astcdType);
  }

  public Expr<? extends Sort> getAttribute(
      ASTCDType astCdType, String attributeName, Expr<? extends Sort> cDTypeExpr) {
    return dataWrapper.getAttribute(astCdType, attributeName, cDTypeExpr);
  }

  public Set<IdentifiableBoolExpr> getClassConstraints() {
    return dataWrapper.getClassConstraints();
  }

  public Expr<? extends Sort> getEnumConstant(
      ASTCDEnum enumeration, ASTCDEnumConstant enumConstant) {
    return dataWrapper.getEnumConstant(enumeration, enumConstant);
  }

  public BoolExpr mkForall(ASTCDType type, Expr<?> var, BoolExpr body) {
    return dataWrapper.mkForall(type, var, body);
  }

  public BoolExpr mkExists(ASTCDType type, Expr<?> var, BoolExpr body) {
    return dataWrapper.mkExists(type, var, body);
  }

  public BoolExpr mkForall(List<ASTCDType> types, List<Expr<?>> vars, BoolExpr body) {
    return dataWrapper.mkForall(types, vars, body);
  }

  public BoolExpr mkExists(List<ASTCDType> types, List<Expr<?>> vars, BoolExpr body) {
    return dataWrapper.mkExists(types, vars, body);
  }

  public BoolExpr evaluateLink(
      ASTCDAssociation association,
      ASTCDType type1,
      ASTCDType type2,
      Expr<? extends Sort> expr1,
      Expr<? extends Sort> expr2) {
    return dataWrapper.evaluateLink(association, type1, type2, expr1, expr2);
  }

  public Set<IdentifiableBoolExpr> getAssociationsConstraints() {
    return dataWrapper.getAssociationsConstraints();
  }

  public Expr<? extends Sort> getSuperInstance(
      ASTCDType objType, ASTCDType superType, Expr<? extends Sort> objExpr) {
    return dataWrapper.getSuperInstance(objType, superType, objExpr);
  }

  public BoolExpr instanceOf(Expr<? extends Sort> obj, ASTCDType objType) {
    return dataWrapper.instanceOf(obj, objType);
  }

  public BoolExpr filterObject(Expr<? extends Sort> obj, ASTCDType type) {
    return dataWrapper.filterObject(obj, type);
  }

  public Set<IdentifiableBoolExpr> getInheritanceConstraints() {
    return dataWrapper.getInheritanceConstraints();
  }

  public Context getContext() {
    return ctx;
  }

  public ASTCDCompilationUnit getClassDiagram() {
    return dataWrapper.getClassDiagram();
  }

  public Optional<ASTODArtifact> smt2od(Model model, Boolean partial, String odName) {
    // get all objects
    Set<MinObject> minObjects = classStrategy.smt2od(model, partial);
    // transform MinObject into SMTObjects
    Set<SMTObject> objectSet2 = new HashSet<>();
    for (MinObject entry : minObjects) {
      objectSet2.add(new SMTObject(entry));
    }

    // get the superclass instances
    objectSet2 = inheritanceStrategy.smt2od(model, objectSet2);

    // get link between Objects
    objectSet2 = associationStrategy.smt2od(model, objectSet2);

    // remove the subclass instances and their links and Interface  objects
    Set<SMTObject> objectSet = new HashSet<>();
    for (SMTObject entry : objectSet2) {
      if (!(entry.getType() == CDHelper.ObjType.ABSTRACT_OBJ)) {
        objectSet.add(entry);
      }
    }
    return smt2ODGenerator.buildOd(objectSet, odName, model, dataWrapper);
  }
}
