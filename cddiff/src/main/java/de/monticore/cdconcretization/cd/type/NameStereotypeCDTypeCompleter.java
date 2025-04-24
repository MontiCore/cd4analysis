package de.monticore.cdconcretization.cd.type;

import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconcretization.cd.CDCompletionContext;
import de.monticore.umlstereotype._ast.ASTStereotype;
import java.util.Optional;

// TODO WIP: Just for demonstration of new architecture
public class NameStereotypeCDTypeCompleter extends AbstractCDTypeCompleter {

  public static final String NAME_STEREOTYPE = "name";

  @Override
  public void completeCDForType(
      ASTCDDefinition concreteCD, ASTCDType referenceType, CDCompletionContext context) {
    // only process type if the stereotype is present
    Optional<String> stereotypeValue = getNameStereotypeValue(referenceType);
    if (stereotypeValue.isPresent()) {
      String name = computeNameFromTemplate(stereotypeValue.get());
      ASTCDType renamedType = renameType(referenceType, name);
      next(concreteCD, renamedType, context);
    } else {
      next(concreteCD, referenceType, context);
    }
  }

  Optional<String> getNameStereotypeValue(ASTCDType type) {
    if (type.getModifier().isPresentStereotype()) {
      ASTStereotype stereotype = type.getModifier().getStereotype();
      if (stereotype.contains(NAME_STEREOTYPE)) {
        String value = stereotype.getValue(NAME_STEREOTYPE);
        if (value == null || !value.isEmpty()) {
          // TODO Log warning: stereotype value must not be empty for stereotype "name"
          return Optional.empty();
        }
        return Optional.of(value);
      }
    }
    return Optional.empty();
  }

  private ASTCDType renameType(ASTCDType type, String name) {
    ASTCDType renamedType = type.deepClone();
    renamedType.setName(name);
    return renamedType;
  }

  protected String computeNameFromTemplate(String template) {
    // TODO implement, define what variables exists etc.
    return template;
  }
}
