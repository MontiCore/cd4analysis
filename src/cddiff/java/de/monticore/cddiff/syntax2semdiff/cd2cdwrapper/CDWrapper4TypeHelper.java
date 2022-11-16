package de.monticore.cddiff.syntax2semdiff.cd2cdwrapper;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.metamodel.CDAssociationWrapperPack;
import de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.metamodel.CDStatus;
import de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.metamodel.CDTypeWrapper;
import de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.metamodel.CDTypeWrapperKind;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CDWrapper4TypeHelper {

  /** get the corresponding CDTypeWrapper kind by ASTCDType */
  public static CDTypeWrapperKind distinguishASTCDTypeHelper(ASTCDType astcdType) {
    if (astcdType instanceof ASTCDClass) {
      if (astcdType.getModifier().isAbstract()) {
        return CDTypeWrapperKind.CDWRAPPER_ABSTRACT_CLASS;
      } else {
        return CDTypeWrapperKind.CDWRAPPER_CLASS;
      }
    } else if (astcdType instanceof ASTCDEnum) {
      return CDTypeWrapperKind.CDWRAPPER_ENUM;
    } else {
      return CDTypeWrapperKind.CDWRAPPER_INTERFACE;
    }
  }

  /** get the corresponding prefix of CDTypeWrapper name by cDTypeWrapperKind */
  public static String getCDTypeWrapperKindStrHelper(CDTypeWrapperKind cDTypeWrapperKind) {
    switch (cDTypeWrapperKind) {
      case CDWRAPPER_CLASS:
        return "CDWrapperClass";
      case CDWRAPPER_ENUM:
        return "CDWrapperEnum";
      case CDWRAPPER_ABSTRACT_CLASS:
        return "CDWrapperAbstractClass";
      case CDWRAPPER_INTERFACE:
        return "CDWrapperInterface";
      default:
        return null;
    }
  }

  /** using the original class name to find corresponding CDTypeWrapper in CDTypeWrapperGroup */
  public static CDTypeWrapper getCDTypeWrapper4OriginalClassName(
      Map<String, CDTypeWrapper> cDTypeWrapperGroup, String originalClassName) {
    if (cDTypeWrapperGroup.containsKey("CDWrapperClass_" + originalClassName)) {
      return cDTypeWrapperGroup.get("CDWrapperClass_" + originalClassName);
    } else if (cDTypeWrapperGroup.containsKey("CDWrapperAbstractClass_" + originalClassName)) {
      return cDTypeWrapperGroup.get("CDWrapperAbstractClass_" + originalClassName);
    } else if (cDTypeWrapperGroup.containsKey("CDWrapperInterface_" + originalClassName)) {
      return cDTypeWrapperGroup.get("CDWrapperInterface_" + originalClassName);
    } else {
      return cDTypeWrapperGroup.get("CDWrapperEnum_" + originalClassName);
    }
  }

  /** check superclasses set and subclasses set for multi-instance */
  public static boolean checkClassSet4MultiInstance(
      Set<String> baseClassSet, Set<String> compareClassSet) {

    Set<String> modifiedBaseClassSet = new HashSet<>();
    Set<String> modifiedCompareClassSet = new HashSet<>();

    baseClassSet.forEach(e -> modifiedBaseClassSet.add(e.split("_")[1]));

    compareClassSet.forEach(e -> modifiedCompareClassSet.add(e.split("_")[1]));

    return modifiedBaseClassSet.equals(modifiedCompareClassSet);
  }

  /** update CD status for CDTypeWrapper if its corresponding assoc has conflict */
  public static void updateCDStatus4CDTypeWrapper(CDTypeWrapper cdTypeWrapper) {
    if (cdTypeWrapper.getCDWrapperKind() != CDTypeWrapperKind.CDWRAPPER_ENUM) {
      cdTypeWrapper.setStatus(CDStatus.LOCKED);
    }
  }

  /**
   * update CD status for two CDTypeWrappers in CDAssociationWrapper if these CDAssociationWrapper
   * has conflict
   */
  public static void updateCDStatus4CDTypeWrapper(
      List<CDAssociationWrapperPack> cdAssociationWrapperPacks) {
    cdAssociationWrapperPacks.forEach(
        e -> {
          updateCDStatus4CDTypeWrapper(e.getCDAssociationWrapper().getCDWrapperLeftClass());
          updateCDStatus4CDTypeWrapper(e.getCDAssociationWrapper().getCDWrapperRightClass());
        });
  }
}
