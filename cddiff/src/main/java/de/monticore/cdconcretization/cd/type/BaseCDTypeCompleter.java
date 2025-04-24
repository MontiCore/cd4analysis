package de.monticore.cdconcretization.cd.type;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdconcretization.cd.CDCompletionContext;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;

/** Completes the types of a CD by adding missing types from the reference CD. */
public class BaseCDTypeCompleter extends AbstractCDTypeCompleter {

  @Override
  protected void completeType(
      ASTCDDefinition concreteCD, ASTCDClass referenceType, CDCompletionContext context) {
    if (concreteCD.getCDClassesList().stream()
        .noneMatch(cClass -> context.getTypeIncStrategy().isMatched(cClass, referenceType))) {
      addTypeIncarnation(concreteCD, referenceType);
    }
    next(concreteCD, referenceType, context);
  }

  @Override
  protected void completeType(
      ASTCDDefinition concreteCD, ASTCDInterface referenceType, CDCompletionContext context) {
    if (concreteCD.getCDInterfacesList().stream()
        .noneMatch(
            cInterface -> context.getTypeIncStrategy().isMatched(cInterface, referenceType))) {
      addTypeIncarnation(concreteCD, referenceType);
    }
    next(concreteCD, referenceType, context);
  }

  @Override
  protected void completeType(
      ASTCDDefinition concreteCD, ASTCDEnum referenceType, CDCompletionContext context) {
    if (concreteCD.getCDEnumsList().stream()
        .noneMatch(cEnum -> context.getTypeIncStrategy().isMatched(cEnum, referenceType))) {
      addTypeIncarnation(concreteCD, referenceType);
    }
    next(concreteCD, referenceType, context);
  }

  private void addTypeIncarnation(ASTCDDefinition concreteCD, ASTCDClass referenceClass) {
    ASTCDClass clone = referenceClass.deepClone();
    clone.setCDExtendUsageAbsent();
    clone.setCDInterfaceUsageAbsent();
    // DIFFERENCE to previous implementation: we clear all members. We need to process the
    // members individually later because of the advanced stereotypes (e.g. forEach)
    // TODO Maybe instead of clearing everything, construct a new class object and just set the name
    clone.clearCDMembers();
    concreteCD.getCDElementList().add(clone);
  }

  private void addTypeIncarnation(ASTCDDefinition concreteCD, ASTCDEnum referenceEnum) {
    ASTCDEnum clone = referenceEnum.deepClone();
    clone.setCDInterfaceUsageAbsent();
    clone.clearCDMembers();
    concreteCD.getCDElementList().add(clone);
  }

  private void addTypeIncarnation(ASTCDDefinition concreteCD, ASTCDInterface referenceInterface) {
    ASTCDInterface clone = referenceInterface.deepClone();
    clone.setCDExtendUsageAbsent();
    clone.clearCDMembers();
    concreteCD.getCDElementList().add(clone);
  }
}
