package de.monticore.cdconcretization.attribute;

import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdmatcher.MatchingStrategy;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Completes a concrete type by adding the given reference attribute if it is not
 * already present.
 */
public class BaseAttributeCompleter extends AbstractAttributeCompleter {

  private final MatchingStrategy<ASTCDAttribute> attributeIncStrategy;

  public BaseAttributeCompleter(MatchingStrategy<ASTCDAttribute> attributeIncStrategy) {
    this.attributeIncStrategy = attributeIncStrategy;
  }

  @Override
  public void completeAttribute(ASTCDType concreteType, ASTCDAttribute referenceAttribute) {
    // 1. check if the concrete type already has a matching attribute (also in superclasses)
    List<ASTCDAttribute> allConcreteAttributesInHierarchy =
        CDSymbolTables.getAttributesInHierarchy(concreteType);
    List<ASTCDAttribute> incarnations =
        allConcreteAttributesInHierarchy.stream()
            .filter(cAttribute -> attributeIncStrategy.isMatched(cAttribute, referenceAttribute))
            .collect(Collectors.toList());
    if (incarnations.isEmpty()) {
      // 2. add the attribute
      ASTCDAttribute clone = referenceAttribute.deepClone();
      concreteType.addCDMember(clone);
    } else {
      // TODO should we check if the type is correct? Or should we let the final conformance check
      // fail?
      // TODO if we check the type: we mus consider incarnations of the reference type!
    }
  }
}
