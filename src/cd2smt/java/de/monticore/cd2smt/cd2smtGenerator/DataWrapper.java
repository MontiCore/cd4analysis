package de.monticore.cd2smt.cd2smtGenerator;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Sort;
import de.monticore.cd2smt.Helper.CDHelper;
import de.monticore.cd2smt.Helper.IdentifiableBoolExpr;
import de.monticore.cd2smt.cd2smtGenerator.assocStrategies.AssociationsData;
import de.monticore.cd2smt.cd2smtGenerator.classStrategies.ClassData;
import de.monticore.cd2smt.cd2smtGenerator.inhrStrategies.InheritanceData;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.se_rwth.commons.logging.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class DataWrapper implements ClassData, AssociationsData, InheritanceData {
  protected final ASTCDCompilationUnit astcdCompilationUnit;
  private final ClassData classData;
  private final AssociationsData associationsData;
  private final InheritanceData inheritanceData;

  public DataWrapper(
      ClassData classData,
      AssociationsData associationsData,
      InheritanceData inheritanceData,
      ASTCDCompilationUnit astcdCompilationUnit) {
    this.classData = classData;
    this.associationsData = associationsData;
    this.inheritanceData = inheritanceData;
    this.astcdCompilationUnit = astcdCompilationUnit;
  }

  public Optional<BoolExpr> evaluateLinkHelper(
      ASTCDAssociation association,
      ASTCDType subType1,
      ASTCDType subType2,
      Expr<? extends Sort> expr1,
      Expr<? extends Sort> expr2) {

    ASTCDType type1 =
        CDHelper.getASTCDType(
            association.getRightQualifiedName().getQName(), astcdCompilationUnit.getCDDefinition());
    ASTCDType type2 =
        CDHelper.getASTCDType(
            association.getLeftQualifiedName().getQName(), astcdCompilationUnit.getCDDefinition());

    List<ASTCDType> supertypeList1 = new ArrayList<>();
    supertypeList1.add(subType1);
    List<ASTCDType> supertypeList2 = new ArrayList<>();
    supertypeList2.add(subType2);

    CDHelper.getAllSuperType(subType1, astcdCompilationUnit.getCDDefinition(), supertypeList1);
    CDHelper.getAllSuperType(subType2, astcdCompilationUnit.getCDDefinition(), supertypeList2);

    ASTCDType superType1 =
        supertypeList1.contains(type1) || supertypeList2.contains(type2) ? type1 : type2;
    assert superType1 != null;
    ASTCDType superType2 = superType1.equals(type1) ? type2 : type1;

    return Optional.of(
        associationsData.evaluateLink(
            association,
            superType1,
            superType2,
            getSuperInstance(subType1, superType1, expr1),
            getSuperInstance(subType2, superType2, expr2)));
  }

  @Override
  public BoolExpr evaluateLink(
      ASTCDAssociation association,
      ASTCDType type1,
      ASTCDType type2,
      Expr<? extends Sort> expr1,
      Expr<? extends Sort> expr2) {

    Optional<BoolExpr> res = evaluateLinkHelper(association, type1, type2, expr1, expr2);
    if (res.isEmpty()) {
      Log.error(
          "No Link found between the Object of Sort "
              + expr1.getSort()
              + " and "
              + expr2.getSort());
    }
    assert res.isPresent();
    return res.get();
  }

  @Override
  public Set<IdentifiableBoolExpr> getAssociationsConstraints() {
    return associationsData.getAssociationsConstraints();
  }

  @Override
  public Sort getSort(ASTCDType astCdType) {
    return classData.getSort(astCdType);
  }

  @Override
  public BoolExpr isInstanceOf(Expr<? extends Sort> expr, ASTCDType astCdType) {
    return classData.isInstanceOf(expr, astCdType);
  }

  @Override
  public Expr<? extends Sort> getAttribute(
      ASTCDType astCdType, String attributeName, Expr<? extends Sort> cDTypeExpr) {
    Optional<Expr<? extends Sort>> res = getAttributeHelper(astCdType, attributeName, cDTypeExpr);
    if (res.isEmpty()) {
      Log.error(
          "the attribute " + attributeName + " not found for the ASTCDType " + astCdType.getName());
    }
    assert res.isPresent();
    return res.get();
  }

  protected Optional<Expr<? extends Sort>> getAttributeHelper(
      ASTCDType astCdType, String attributeName, Expr<? extends Sort> cDTypeExpr) {
    if (CDHelper.containsProperAttribute(astCdType, attributeName)) {
      return Optional.of(classData.getAttribute(astCdType, attributeName, cDTypeExpr));
    }
    List<ASTCDType> superclassList =
        CDHelper.getSuperTypeList(astCdType, astcdCompilationUnit.getCDDefinition());
    for (ASTCDType superType : superclassList) {
      Optional<Expr<? extends Sort>> res =
          getAttributeHelper(
              superType, attributeName, getSuperInstance(astCdType, superType, cDTypeExpr));
      if (res.isPresent()) {
        return res;
      }
    }

    return Optional.empty();
  }

  @Override
  public Expr<? extends Sort> getSuperInstance(
      ASTCDType objType, ASTCDType superType, Expr<? extends Sort> objExpr) {
    if (objType.equals(superType)) {
      return objExpr;
    }

    Optional<Expr<? extends Sort>> res = getSuperInstanceHelper(objType, superType, objExpr);
    if (res.isEmpty()) {
      Log.error(
          "the super Type "
              + superType.getName()
              + " not found for the class "
              + objType.getName());
    }
    assert res.isPresent();
    return res.get();
  }

  protected Optional<Expr<? extends Sort>> getSuperInstanceHelper(
      ASTCDType objType, ASTCDType superType, Expr<? extends Sort> objExpr) {
    List<ASTCDType> superClassList =
        CDHelper.getSuperTypeList(objType, astcdCompilationUnit.getCDDefinition());
    for (ASTCDType newSuperType : superClassList) {
      if (newSuperType.equals(superType)) {
        return Optional.of(inheritanceData.getSuperInstance(objType, superType, objExpr));
      } else {
        for (ASTCDType newObjType : superClassList) {
          Optional<Expr<? extends Sort>> res =
              getSuperInstanceHelper(
                  newObjType,
                  superType,
                  inheritanceData.getSuperInstance(objType, newObjType, objExpr));
          if (res.isPresent()) {
            return res;
          }
        }
      }
    }
    return Optional.empty();
  }

  @Override
  public Set<IdentifiableBoolExpr> getInheritanceConstraints() {
    return inheritanceData.getInheritanceConstraints();
  }
}
