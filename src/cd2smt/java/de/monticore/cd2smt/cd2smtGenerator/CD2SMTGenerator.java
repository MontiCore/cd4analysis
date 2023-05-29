/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2smt.cd2smtGenerator;

import com.microsoft.z3.*;
import de.monticore.cd2smt.Helper.CDHelper;
import de.monticore.cd2smt.Helper.IdentifiableBoolExpr;
import de.monticore.cd2smt.ODArtifacts.MinObject;
import de.monticore.cd2smt.ODArtifacts.SMTObject;
import de.monticore.cd2smt.cd2smtGenerator.assocStrategies.AssociationStrategy;
import de.monticore.cd2smt.cd2smtGenerator.assocStrategies.AssociationsData;
import de.monticore.cd2smt.cd2smtGenerator.assocStrategies.defaultAssocStrategy.DefaultAssocStrategy;
import de.monticore.cd2smt.cd2smtGenerator.assocStrategies.one2one.One2OneAssocStrategy;
import de.monticore.cd2smt.cd2smtGenerator.classStrategies.ClassData;
import de.monticore.cd2smt.cd2smtGenerator.classStrategies.ClassStrategy;
import de.monticore.cd2smt.cd2smtGenerator.classStrategies.distinctSort.DSClassStrategy;
import de.monticore.cd2smt.cd2smtGenerator.classStrategies.singleSort.SSClassStrategy;
import de.monticore.cd2smt.cd2smtGenerator.inhrStrategies.InheritanceData;
import de.monticore.cd2smt.cd2smtGenerator.inhrStrategies.InheritanceStrategy;
import de.monticore.cd2smt.cd2smtGenerator.inhrStrategies.SExpression.SEInheritanceStrategy;
import de.monticore.cd2smt.cd2smtGenerator.inhrStrategies.multExpression.MEInheritanceStrategy;
import de.monticore.cd2smt.smt2odgenerator.SMT2ODGenerator;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
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
public class CD2SMTGenerator implements ClassData, AssociationsData, InheritanceData {

  private ClassStrategy classStrategy = null;
  private InheritanceStrategy inheritanceStrategy = null;
  private AssociationStrategy associationStrategy = null;
  private DataWrapper dataWrapper;
  private final SMT2ODGenerator smt2ODGenerator = new SMT2ODGenerator();
  private Context ctx;

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
    CheckStrategies();
    this.ctx = ctx;
    // set All Associations Role
    dataWrapper = new DataWrapper(classStrategy, associationStrategy, inheritanceStrategy, astCd);
    classStrategy.cd2smt(astCd, ctx);

    inheritanceStrategy.cd2smt(astCd, ctx, classStrategy);

    associationStrategy.cd2smt(astCd, ctx, classStrategy, inheritanceStrategy);
  }

  public void setClassStrategy(ClassStrategy.Strategy strategy) {
    switch (strategy) {
      case DS:
        this.classStrategy = new DSClassStrategy();
        break;
      case SS:
        this.classStrategy = new SSClassStrategy();
        break;
      case SSCOMB:
        if (this.inheritanceStrategy == null) {
          this.classStrategy = new SEInheritanceStrategy();
        } else if (this.inheritanceStrategy instanceof SEInheritanceStrategy) {
          this.classStrategy = (SEInheritanceStrategy) inheritanceStrategy;
        } else {
          Log.error(
              "The Class Strategy Single Sort Composed (SSComp) can only be combine with The inheritance Strategy"
                  + " Single Sort Composed (SSComp) ");
        }
    }
  }

  public void setInheritanceStrategy(InheritanceStrategy.Strategy strategy) {
    switch (strategy) {
      case ME:
        this.inheritanceStrategy = new MEInheritanceStrategy();
        break;

      case SE:
        if (this.classStrategy == null) {
          this.classStrategy = new SEInheritanceStrategy();
        } else if (this.classStrategy instanceof SEInheritanceStrategy) {
          this.inheritanceStrategy = (SEInheritanceStrategy) classStrategy;
        } else {
          Log.error(
              "The Inheritance Strategy Single Sort Composed (SSComp) can only be combine with The Class  Strategy"
                  + " Single Sort Composed (SSComp) ");
        }
    }
  }

  public void setAssociationStrategy(AssociationStrategy.Strategy strategy) {
    switch (strategy) {
      case ONE2ONE:
        this.associationStrategy = new One2OneAssocStrategy();
        break;
      case DEFAULT:
        this.associationStrategy = new DefaultAssocStrategy();
        break;
    }
  }

  @Override
  public Sort getSort(ASTCDType astcdType) {
    return dataWrapper.getSort(astcdType);
  }

  @Override
  public BoolExpr hasType(Expr<? extends Sort> expr, ASTCDType astcdType) {
    return dataWrapper.hasType(expr, astcdType);
  }

  @Override
  public Expr<? extends Sort> getAttribute(
      ASTCDType astCdType, String attributeName, Expr<? extends Sort> cDTypeExpr) {
    return dataWrapper.getAttribute(astCdType, attributeName, cDTypeExpr);
  }

  @Override
  public Set<IdentifiableBoolExpr> getClassConstraints() {
    return dataWrapper.getClassConstraints();
  }

  @Override
  public BoolExpr evaluateLink(
      ASTCDAssociation association,
      ASTCDType type1,
      ASTCDType type2,
      Expr<? extends Sort> expr1,
      Expr<? extends Sort> expr2) {
    return dataWrapper.evaluateLink(association, type1, type2, expr1, expr2);
  }

  @Override
  public Set<IdentifiableBoolExpr> getAssociationsConstraints() {
    return dataWrapper.getAssociationsConstraints();
  }

  @Override
  public Expr<? extends Sort> getSuperInstance(
      ASTCDType objType, ASTCDType superType, Expr<? extends Sort> objExpr) {
    return dataWrapper.getSuperInstance(objType, superType, objExpr);
  }

  @Override
  public BoolExpr instanceOf(Expr<? extends Sort> obj, ASTCDType objType) {
    return dataWrapper.instanceOf(obj, objType);
  }

  @Override
  public BoolExpr filterObject(Expr<? extends Sort> obj, ASTCDType type) {
    return dataWrapper.filterObject(obj, type);
  }

  @Override
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

  private void CheckStrategies() {
    if (classStrategy == null) {
      Log.error("Class Strategy muss be set to perform the CD2SMT-Operation");
    }

    if (associationStrategy == null) {
      Log.error("Association Strategy muss be set to perform the CD2SMT-Operation");
    }

    if (inheritanceStrategy == null) {
      Log.error("Inheritance Strategy muss be set to perform the CD2SMT-Operation");
    }
  }

  public void initDefaultStrategies() {
    classStrategy = new DSClassStrategy();
    inheritanceStrategy = new MEInheritanceStrategy();
    associationStrategy = new DefaultAssocStrategy();
  }
}
