/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2smt.cd2smtGenerator;

import com.microsoft.z3.*;
import de.monticore.cd2smt.Helper.CDHelper;
import de.monticore.cd2smt.Helper.IdentifiableBoolExpr;
import de.monticore.cd2smt.cd2smtGenerator.assocStrategies.AssociationsData;
import de.monticore.cd2smt.cd2smtGenerator.classStrategies.ClassData;
import de.monticore.cd2smt.cd2smtGenerator.inhrStrategies.InheritanceData;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.se_rwth.commons.logging.Log;
import java.util.*;
import java.util.function.Function;

public class DataWrapper implements ClassData, AssociationsData, InheritanceData {
  protected final ASTCDCompilationUnit ast;
  private final ClassData classData;
  private final AssociationsData associationsData;
  private final InheritanceData inheritanceData;

  public DataWrapper(
      ClassData classData,
      AssociationsData associationsData,
      InheritanceData inheritanceData,
      ASTCDCompilationUnit ast) {
    this.classData = classData;
    this.associationsData = associationsData;
    this.inheritanceData = inheritanceData;
    this.ast = ast;
  }

  public Optional<BoolExpr> evaluateLinkHelper(
      ASTCDAssociation association,
      ASTCDType subType1,
      ASTCDType subType2,
      Expr<? extends Sort> expr1,
      Expr<? extends Sort> expr2) {

    ASTCDType type1 =
        CDHelper.getASTCDType(association.getLeftQualifiedName().getQName(), ast.getCDDefinition());
    ASTCDType type2 =
        CDHelper.getASTCDType(
            association.getRightQualifiedName().getQName(), ast.getCDDefinition());

    Set<ASTCDType> supertypeList1 = new HashSet<>();
    supertypeList1.add(subType1);
    Set<ASTCDType> supertypeList2 = new HashSet<>();
    supertypeList2.add(subType2);

    supertypeList1.addAll(CDHelper.getSuperTypeAllDeep(subType1, ast.getCDDefinition()));
    supertypeList2.addAll(CDHelper.getSuperTypeAllDeep(subType2, ast.getCDDefinition()));
    BoolExpr res;
    if (supertypeList1.contains(type1) && supertypeList2.contains(type2)) {
      res =
          associationsData.evaluateLink(
              association,
              type1,
              type2,
              getSuperInstance(subType1, type1, expr1),
              getSuperInstance(subType2, type2, expr2));
    } else if (supertypeList1.contains(type2) && supertypeList2.contains(type1)) {
      res =
          associationsData.evaluateLink(
              association,
              type1,
              type2,
              getSuperInstance(subType2, type1, expr2),
              getSuperInstance(subType1, type2, expr1));
    } else {
      res = null;
    }

    return Optional.ofNullable(res);
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
  public BoolExpr hasType(Expr<? extends Sort> expr, ASTCDType astcdType) {
    return classData.hasType(expr, astcdType);
  }

  @Override
  public Expr<? extends Sort> getAttribute(
      ASTCDType astCdType, String attributeName, Expr<? extends Sort> cDTypeExpr) {
    Optional<Expr<? extends Sort>> res = getAttributeHelper(astCdType, attributeName, cDTypeExpr);
    return res.orElse(null);
  }

  @Override
  public ASTCDCompilationUnit getClassDiagram() {
    return classData.getClassDiagram();
  }

  @Override
  public Set<IdentifiableBoolExpr> getClassConstraints() {
    return classData.getClassConstraints();
  }

  @Override
  public Context getContext() {
    return classData.getContext();
  }

  @Override
  public Expr<? extends Sort> getEnumConstant(
      ASTCDEnum enumeration, ASTCDEnumConstant enumConstant) {
    return classData.getEnumConstant(enumeration, enumConstant);
  }

  @Override
  public BoolExpr mkForall(ASTCDType type, Expr<?> var, Function<Expr<?>, BoolExpr> body) {
    return inheritanceData.mkForall(type, var, body);
  }

  @Override
  public BoolExpr mkExists(ASTCDType type, Expr<?> var, Function<Expr<?>, BoolExpr> body) {
    return inheritanceData.mkExists(type, var, body);
  }

  protected Optional<Expr<? extends Sort>> getAttributeHelper(
      ASTCDType astCdType, String attributeName, Expr<? extends Sort> cDTypeExpr) {
    if (CDHelper.containsProperAttribute(astCdType, attributeName)) {
      return Optional.of(classData.getAttribute(astCdType, attributeName, cDTypeExpr));
    }
    List<ASTCDType> superclassList = CDHelper.getSuperTypeList(astCdType, ast.getCDDefinition());
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

  @Override
  public BoolExpr instanceOf(Expr<? extends Sort> obj, ASTCDType objType) {
    return inheritanceData.instanceOf(obj, objType);
  }

  @Override
  public BoolExpr filterObject(Expr<? extends Sort> obj, ASTCDType type) {
    return inheritanceData.filterObject(obj, type);
  }

  protected Optional<Expr<? extends Sort>> getSuperInstanceHelper(
      ASTCDType objType, ASTCDType superType, Expr<? extends Sort> objExpr) {
    List<ASTCDType> superClassList = CDHelper.getSuperTypeList(objType, ast.getCDDefinition());
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
