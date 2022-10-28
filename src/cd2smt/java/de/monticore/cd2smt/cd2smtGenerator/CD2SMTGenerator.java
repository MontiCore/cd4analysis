package de.monticore.cd2smt.cd2smtGenerator;

import com.microsoft.z3.*;
import de.monticore.cd2smt.Helper.CDHelper;
import de.monticore.cd2smt.Helper.IdentifiableBoolExpr;
import de.monticore.cd2smt.ODArtifacts.MinObject;
import de.monticore.cd2smt.ODArtifacts.SMTObject;
import de.monticore.cd2smt.cd2smtGenerator.assocStrategies.AssociationStrategy;
import de.monticore.cd2smt.cd2smtGenerator.assocStrategies.AssociationsData;
import de.monticore.cd2smt.cd2smtGenerator.assocStrategies.defaultAssocStrategy.DefaultAssocStrategy;
import de.monticore.cd2smt.cd2smtGenerator.classStrategies.ClassData;
import de.monticore.cd2smt.cd2smtGenerator.classStrategies.ClassStrategy;
import de.monticore.cd2smt.cd2smtGenerator.classStrategies.distinctSort.DistinctSort;
import de.monticore.cd2smt.cd2smtGenerator.inhrStrategies.InheritanceData;
import de.monticore.cd2smt.cd2smtGenerator.inhrStrategies.InheritanceStrategy;
import de.monticore.cd2smt.cd2smtGenerator.inhrStrategies.defaultInhrStratregy.DefaultInhrStrategy;
import de.monticore.cd2smt.smt2odgenerator.SMT2ODGenerator;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.odbasis._ast.ASTODArtifact;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * this class Convert a class diagram into SMT using three different Strategies
 * a class-strategy to convert classes and interface
 * an association-strategy to Convert Association
 * an inheritance-strategy to convert inheritance
 */
public class CD2SMTGenerator implements ClassData, AssociationsData, InheritanceData {

  private final ClassStrategy classStrategy = new DistinctSort();
  private final InheritanceStrategy inheritanceStrategy = new DefaultInhrStrategy();
  private final AssociationStrategy associationStrategy = new DefaultAssocStrategy();
  private final SMT2ODGenerator smt2ODGenerator = new SMT2ODGenerator();
  private Context ctx;
  private ASTCDCompilationUnit astCd;

  public Solver makeSolver(List<IdentifiableBoolExpr> constraints) {
    Solver solver = ctx.mkSolver();
    inheritanceStrategy.getInheritanceConstraints().forEach(c -> solver.assertAndTrack(c.getValue(), ctx.mkBoolConst(String.valueOf(c.getId()))));
    associationStrategy.getAssociationsConstraints().forEach(c -> solver.assertAndTrack(c.getValue(), ctx.mkBoolConst(String.valueOf(c.getId()))));
    constraints.forEach(c -> solver.assertAndTrack(c.getValue(), ctx.mkBoolConst(String.valueOf(c.getId()))));
    return solver;
  }

  /**
   * this function convert class diagram into SMT using the three different Strategies
   *
   * @param ctx   the SMT Context
   * @param astCd the class diagram to translate
   */
  public void cd2smt(ASTCDCompilationUnit astCd, Context ctx) {
    this.ctx = ctx;
    this.astCd = astCd;
    //set All Associations Role
    CDHelper.setAssociationsRoles(astCd);

    classStrategy.cd2smt(astCd, ctx);

    inheritanceStrategy.cd2smt(astCd, ctx, classStrategy);

    associationStrategy.cd2smt(astCd, ctx, classStrategy);

  }

  @Override
  public Sort getSort(ASTCDType astcdType) {
    return classStrategy.getSort(astcdType);
  }

  @Override
  public Expr<? extends Sort> getSortFilter(ASTCDType astcdType) {
    return classStrategy.getSortFilter(astcdType);
  }

  @Override
  public Expr<? extends Sort> getAttribute(ASTCDType astCdType, ASTCDAttribute astCdAttribute, Expr<? extends Sort> cDTypeExpr) {
    return classStrategy.getAttribute(astCdType, astCdAttribute, cDTypeExpr);
  }

  @Override
  public BoolExpr evaluateLink(ASTCDAssociation association, Expr<? extends Sort> left, Expr<? extends Sort> right) {
    return associationStrategy.evaluateLink(association, left, right);
  }

  @Override
  public Set<IdentifiableBoolExpr> getAssociationsConstraints() {
    return associationStrategy.getAssociationsConstraints();
  }

  @Override
  public Expr<? extends Sort> getSuperInstance(ASTCDType objType, ASTCDType superType, Expr<? extends Sort> objExpr) {
    return inheritanceStrategy.getSuperInstance(objType, superType, objExpr);
  }

  @Override
  public Set<IdentifiableBoolExpr> getInheritanceConstraints() {
    return inheritanceStrategy.getInheritanceConstraints();
  }

  public Context getContext() {
    return ctx;
  }

  public ASTCDCompilationUnit getClassDiagram() {
    return astCd;
  }

  public Optional<ASTODArtifact> smt2od(Model model, Boolean partial, String odName) {
    //get all objects
    Set<MinObject> minObjects = classStrategy.smt2od(model, partial);
    //transform MinObject into SMTObjects
    Set<SMTObject> objectSet2 = new HashSet<>();
    for (MinObject entry : minObjects) {
      objectSet2.add(new SMTObject(entry));
    }

    //get the superclass instances
    objectSet2 = inheritanceStrategy.smt2od(model, objectSet2);

    //get link between Objects
    objectSet2 = associationStrategy.smt2od(model, objectSet2);

    ////remove the subclass instances and their links and Interface  objects
    Set<SMTObject> objectSet = new HashSet<>();
    for (SMTObject entry : objectSet2) {
      if (!entry.isAbstract()) {
        objectSet.add(entry);
      }
    }
    return smt2ODGenerator.buildOd(objectSet, odName, model);
  }

}








