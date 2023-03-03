package de.monticore.conformance;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import java.util.Set;
import java.util.stream.Collectors;

@Deprecated
public class ConformanceChecker {

  // todo: (1) Strategy Pattern, (2) Mapping concrete -> reference, (3) Resolve?
  public static boolean check(
      ASTCDCompilationUnit concreteCD, ASTCDCompilationUnit referenceCD, String mappingID) {
    return new ConformanceChecker(concreteCD, referenceCD, mappingID).checkConformance();
  }

  protected ASTCDCompilationUnit conCD;
  protected ASTCDCompilationUnit refCD;
  protected String mappingID;

  protected ConformanceChecker(
      ASTCDCompilationUnit concreteCD, ASTCDCompilationUnit referenceCD, String mappingID) {
    conCD = concreteCD;
    refCD = referenceCD;
    this.mappingID = mappingID;
  }

  protected boolean checkConformance() {
    return (refCD.getCDDefinition().getCDClassesList().stream().allMatch(this::checkIncarnations)
        && refCD.getCDDefinition().getCDInterfacesList().stream().allMatch(this::checkIncarnations)
        && refCD.getCDDefinition().getCDEnumsList().stream().allMatch(this::checkIncarnations)
        && refCD.getCDDefinition().getCDAssociationsList().stream()
            .allMatch(this::checkIncarnations));
  }

  protected boolean checkIncarnations(ASTCDClass refClass) {
    Set<ASTCDClass> incarnations =
        conCD.getCDDefinition().getCDClassesList().stream()
            .filter(conClass -> isIncarnation(conClass, refClass))
            .collect(Collectors.toSet());
    if (incarnations.isEmpty()) {
      return false;
    }
    return incarnations.stream().allMatch(incarnation -> conforms(incarnation, refClass));
  }

  protected boolean checkIncarnations(ASTCDAssociation refAssoc) {
    Set<ASTCDAssociation> incarnations =
        conCD.getCDDefinition().getCDAssociationsList().stream()
            .filter(conAssoc -> isIncarnation(conAssoc, refAssoc))
            .collect(Collectors.toSet());
    if (incarnations.isEmpty()) {
      return false;
    }
    return incarnations.stream().allMatch(incarnation -> conforms(incarnation, refAssoc));
  }

  protected boolean isIncarnation(ASTCDType conType, ASTCDType refType) {
    return conType.getModifier().isPresentStereotype()
        && conType.getModifier().getStereotype().contains(mappingID, refType.getName());
  }

  protected boolean isIncarnation(ASTCDAssociation cAssoc, ASTCDAssociation rAssoc) {
    if (cAssoc.isPresentName()) {
      return cAssoc.getModifier().isPresentStereotype()
          && rAssoc.getModifier().getStereotype().contains(mappingID, cAssoc.getName());
    }
    // todo: resolve types and check for role-names in navigable direction
    return false;
  }

  protected boolean conforms(ASTCDClass incarnation, ASTCDClass refClass) {
    // todo: check modifier, attributes, associations, interfaces, etc...
    boolean attributes =
        refClass.getCDAttributeList().stream()
            .allMatch(
                rAttr ->
                    incarnation.getCDAttributeList().stream()
                        .anyMatch(cAttr -> cAttr.deepEquals(rAttr)));
    boolean associations =
        refCD.getCDDefinition().getCDAssociationsList().stream()
            .filter(
                rAssoc ->
                    rAssoc.getLeftQualifiedName().getQName().contains(refClass.getName())
                        || rAssoc.getRightQualifiedName().getQName().contains(refClass.getName()))
            .allMatch(
                rAssoc ->
                    conCD.getCDDefinition().getCDAssociationsList().stream()
                        .filter(
                            cAssoc ->
                                cAssoc
                                        .getLeftQualifiedName()
                                        .getQName()
                                        .contains(incarnation.getName())
                                    || cAssoc
                                        .getRightQualifiedName()
                                        .getQName()
                                        .contains(incarnation.getName()))
                        .anyMatch(cAssoc -> isIncarnation(cAssoc, rAssoc)));
    boolean superclasses =
        CDDiffUtil.getAllSuperclasses(refClass, refCD.getCDDefinition().getCDClassesList()).stream()
            .allMatch(
                refSuper ->
                    CDDiffUtil.getAllSuperclasses(
                            incarnation, conCD.getCDDefinition().getCDClassesList())
                        .stream()
                        .anyMatch(conSuper -> isIncarnation(conSuper, refSuper)));
    return attributes && associations && superclasses;
  }

  protected boolean conforms(ASTCDAssociation incarnation, ASTCDAssociation refAssoc) {
    // todo: resolve types and check if incarnation
    // todo: check modifier, role-names, navigation, cardinalities, etc...
    return true;
  }

  // todo: implement
  protected boolean checkIncarnations(ASTCDInterface refInterface) {
    return false;
  }

  protected boolean checkIncarnations(ASTCDEnum refEnum) {
    return false;
  }
}
