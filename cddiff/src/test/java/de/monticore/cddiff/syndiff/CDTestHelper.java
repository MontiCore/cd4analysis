package de.monticore.cddiff.syndiff;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class CDTestHelper {

  public static ASTCDType getASTCDType(String className, ASTCDDefinition cd) {
    for (ASTCDClass myClass : cd.getCDClassesList()) {
      if (myClass.getName().equals(className)) {
        return myClass;
      }
    }
    for (ASTCDInterface astcdInterface : cd.getCDInterfacesList()) {
      if (astcdInterface.getName().equals(className)) {
        return astcdInterface;
      }
    }

    for (ASTCDEnum astcdEnum : cd.getCDEnumsList()) {
      if (astcdEnum.getName().equals(className)) {
        return astcdEnum;
      }
    }
    Log.error(" class " + className + " not found in classdiagram " + cd.getName());
    return null;
  }

  public static ASTCDClass getClass(String className, ASTCDDefinition cd) {
    for (ASTCDClass myClass : cd.getCDClassesList()) {
      if (myClass.getName().equals(className)) {
        return myClass;
      }
    }
    Log.error(" class " + className + " not found in classdiagram " + cd.getName());
    return null;
  }

  public static ASTCDEnum getEnum(String enumName, ASTCDDefinition cd) {
    for (ASTCDEnum astcdEnum : cd.getCDEnumsList()) {
      if (astcdEnum.getName().equals(enumName)) {
        return astcdEnum;
      }
    }
    Log.error(" Enumeration " + enumName + " not found in classdiagram " + cd.getName());
    return null;
  }

  public static boolean isPrimitiveType(ASTMCType type) {
    return Set.of("int", "double", "Double", "Integer", "boolean", "Boolean", "String")
        .contains(type.printType());
  }

  public static ASTCDAssociation getAssociation(
      ASTCDType objType, String otherRole, ASTCDDefinition cd) {
    Set<ASTCDType> objTypes = getSuperTypeAllDeep(objType, cd);
    objTypes.add(objType);
    ASTCDType leftType;
    ASTCDType rightType;
    String leftRole;
    String rightRole;

    for (ASTCDAssociation association : cd.getCDAssociationsList()) {
      leftType = getASTCDType(association.getLeftQualifiedName().getQName(), cd);
      rightType = getASTCDType(association.getRightQualifiedName().getQName(), cd);
      leftRole = association.getLeft().getCDRole().getName();
      rightRole = association.getRight().getCDRole().getName();

      if (objTypes.contains(leftType) && otherRole.equals(rightRole)
          || objTypes.contains(rightType) && otherRole.equals(leftRole)) {
        return association;
      }
    }
    return null;
  }

  private static void getSuperTypeAllDeepHelper(
      ASTCDType astcdType, ASTCDDefinition cd, Set<ASTCDType> res) {
    if (astcdType.getInterfaceList().isEmpty() && astcdType.getSuperclassList().isEmpty()) {
      return;
    }
    List<ASTCDType> superClassList = getSuperTypeList(astcdType, cd);
    res.add(astcdType);
    for (ASTCDType astcType1 : superClassList) {
      res.add(astcType1);
      getSuperTypeAllDeepHelper(astcType1, cd, res);
    }
  }

  public static List<ASTCDType> getSuperTypeList(ASTCDType astcdType, ASTCDDefinition cd) {
    List<ASTCDType> res =
        astcdType.getSuperclassList().stream()
            .map(mcType -> getASTCDType(mcType.printType(), cd))
            .collect(Collectors.toList());

    res.addAll(
        astcdType.getInterfaceList().stream()
            .map(mcType -> getASTCDType(mcType.printType(), cd))
            .collect(Collectors.toList()));

    return res;
  }

  public static Set<ASTCDType> getSuperTypeAllDeep(ASTCDType astcdType, ASTCDDefinition cd) {
    Set<ASTCDType> res = new HashSet<>();
    getSuperTypeAllDeepHelper(astcdType, cd, res);
    res.remove(astcdType);
    return res;
  }

  public static ASTCDAttribute getAttribute(ASTCDType astcdType, String attrName) {
    Optional<ASTCDAttribute> attr =
        astcdType.getCDAttributeList().stream().filter(a -> a.getName().equals(attrName)).findAny();
    if (attr.isEmpty()) {
      Log.error("attribute " + attrName + " not found in class " + astcdType.getName());
    }
    assert attr.isPresent();
    return attr.get();
  }
}
