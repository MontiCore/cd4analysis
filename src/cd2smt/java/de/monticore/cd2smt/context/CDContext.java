package de.monticore.cd2smt.context;

import com.microsoft.z3.*;
import de.monticore.cd2smt.Helper.IdentifiableBoolExpr;
import de.monticore.cd2smt.Helper.SMTNameHelper;
import de.monticore.cd2smt.context.CDArtifacts.SMTAssociation;
import de.monticore.cd2smt.context.CDArtifacts.SMTCDType;
import de.monticore.cd2smt.context.CDArtifacts.SMTClass;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.se_rwth.commons.logging.Log;

import java.util.*;


public class CDContext {

  private final Map<ASTCDAssociation, SMTAssociation> smtAssociations;
  private final Map<ASTCDType, SMTCDType> smtCDTypes;
  private final Context context;
  private final List<IdentifiableBoolExpr> associationConstraints;
  private final List<IdentifiableBoolExpr> inheritanceConstraints;


  public CDContext(Context context) {
    this.context = context;
    smtCDTypes = new HashMap<>();
    smtAssociations = new HashMap<>();
    associationConstraints = new ArrayList<>();
    inheritanceConstraints = new ArrayList<>();
  }

  public Context getContext() {
    return context;
  }

  public List<IdentifiableBoolExpr> getAssociationConstraints() {
    return associationConstraints;
  }

  public List<IdentifiableBoolExpr> getInheritanceConstraints() {
    return inheritanceConstraints;
  }

  public Map<ASTCDAssociation, SMTAssociation> getSMTAssociations() {
    return smtAssociations;
  }

  public Map<ASTCDType, SMTCDType> getSmtCDTypes() {
    return smtCDTypes;
  }

  public void addInheritanceConstr(IdentifiableBoolExpr constraint) {
    this.inheritanceConstraints.add(constraint);
  }

  public void addAssociationConstraints(IdentifiableBoolExpr constraint) {
    this.associationConstraints.add(constraint);
  }

  public void addCDTYpe(ASTCDType astcdType, SMTCDType smtcdType) {
    smtCDTypes.put(astcdType, smtcdType);
  }

  public Optional<SMTCDType> getSMTCDType(String className) {
    for (Map.Entry<ASTCDType, SMTCDType> entry : smtCDTypes.entrySet()) {
      if (entry.getKey().getName().equals(className)) {
        return Optional.of(entry.getValue());
      }
    }
    return Optional.empty();
  }

  public Optional<SMTCDType> getSMTCDType(Expr<? extends Sort> obj) {
    String className = obj.getSort().toString().split("_")[0];
    return getSMTCDType(className);
  }

  public FuncDecl<? extends Sort> getAttributeFunc(SMTCDType smtcdType, String attr) {
    assert smtcdType != null;
    for (FuncDecl<? extends Sort> entry : smtcdType.getAttributes()) {
      if (entry.getName().toString().equals(SMTNameHelper.printAttributeNameSMT(smtcdType.getASTCDType(), attr))) {
        return entry;
      }
    }
    Log.error("attribute " + attr + "not found in the smtclass " + smtcdType.getASTCDType().getName());
    return null;
  }

  public SMTAssociation getAssocFunc(SMTCDType smtcdType, String otherRole) {
    assert smtcdType != null;
    for (Map.Entry<ASTCDAssociation, SMTAssociation> entry : smtcdType.getSMTAssociations().entrySet()) {
      if (entry.getKey().getRight().getCDRole().getName().equals(otherRole) &&
        !entry.getKey().getRight().getName().equals(smtcdType.getASTCDType().getName())) {
        return entry.getValue();
      }
      if (entry.getKey().getLeft().getCDRole().getName().equals(otherRole) &&
        !entry.getKey().getLeft().getName().equals(smtcdType.getASTCDType().getName())) {
        return entry.getValue();
      }
    }
    Log.error("No Associations  Founds for the role  " + otherRole + " in the smt class " + smtcdType.getASTCDType().getName());
    return null;
  }

  public boolean containsAttribute(SMTClass smtClass, String AttrName) {
    for (ASTCDAttribute attribute : smtClass.getASTCDType().getCDAttributeList()) {
      if (attribute.getName().equals(AttrName)) {
        return true;
      }
    }
    return false;
  }

  public static Solver makeSolver(Context ctx, List<IdentifiableBoolExpr> constraints) {
    Solver solver = ctx.mkSolver();
    constraints.forEach(c -> solver.assertAndTrack(c.getValue(), ctx.mkBoolConst(String.valueOf(c.getId()))));
    return solver;
  }


}





