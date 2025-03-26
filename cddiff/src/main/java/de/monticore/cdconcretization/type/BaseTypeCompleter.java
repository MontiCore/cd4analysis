package de.monticore.cdconcretization.type;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cdmatcher.MatchingStrategy;

/**
 * Completes the types of a CD by adding missing types from the reference CD.
 */
public class BaseTypeCompleter extends AbstractTypeCompleter {

  private final MatchingStrategy<ASTCDType> typeIncStrategy;

  public BaseTypeCompleter(MatchingStrategy<ASTCDType> typeIncStrategy) {
    this.typeIncStrategy = typeIncStrategy;
  }

  @Override
  protected void completeType(ASTCDDefinition concreteCD, ASTCDClass referenceType) {
    if (concreteCD.getCDClassesList().stream()
        .noneMatch(cClass -> typeIncStrategy.isMatched(cClass, referenceType))) {
      addTypeIncarnation(concreteCD, referenceType);
    }
    next(concreteCD, referenceType);
  }

  @Override
  protected void completeType(ASTCDDefinition concreteCD, ASTCDInterface referenceType) {
    if (concreteCD.getCDInterfacesList().stream()
        .noneMatch(cInterface -> typeIncStrategy.isMatched(cInterface, referenceType))) {
      addTypeIncarnation(concreteCD, referenceType);
    }
    next(concreteCD, referenceType);
  }

  @Override
  protected void completeType(ASTCDDefinition concreteCD, ASTCDEnum referenceType) {
    if (concreteCD.getCDEnumsList().stream()
        .noneMatch(cEnum -> typeIncStrategy.isMatched(cEnum, referenceType))) {
      addTypeIncarnation(concreteCD, referenceType);
    }
    next(concreteCD, referenceType);
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
