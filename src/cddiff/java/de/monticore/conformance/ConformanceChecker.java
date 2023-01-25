package de.monticore.conformance;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import java.util.Set;
import java.util.stream.Collectors;

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

  protected boolean conforms(ASTCDClass incarnation, ASTCDClass refClass) {
    // todo: check modifier, attributes, associations, interfaces, etc...
    return CDDiffUtil.getAllSuperclasses(refClass, refCD.getCDDefinition().getCDClassesList())
        .stream()
        .allMatch(
            refSuper ->
                CDDiffUtil.getAllSuperclasses(
                        incarnation, conCD.getCDDefinition().getCDClassesList())
                    .stream()
                    .anyMatch(conSuper -> isIncarnation(conSuper, refSuper)));
  }

  protected boolean isIncarnation(ASTCDClass conClass, ASTCDClass refClass) {
    return conClass.getName().contains(refClass.getName())
        || (conClass.getModifier().isPresentStereotype()
            && conClass.getModifier().getStereotype().contains(mappingID, refClass.getName()));
  }

  // todo: implement
  protected boolean checkIncarnations(ASTCDInterface refInterface) {
    return false;
  }

  protected boolean checkIncarnations(ASTCDEnum refEnum) {
    return false;
  }

  protected boolean checkIncarnations(ASTCDAssociation refAssoc) {
    return false;
  }
}
