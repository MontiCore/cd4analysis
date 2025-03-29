package de.monticore.cdconcretization.attribute;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconcretization.CompletionContext;
import de.monticore.cdmatcher.MatchingStrategy;

public interface TypeCompletionContext extends CompletionContext {

  ASTCDType getConcreteType();

  ASTCDType getReferenceType();

  /**
   * Returns the matching strategy for the attribute incarnations. The strategy returned here is
   * only valid in the current type context!
   *
   * @return
   */
  MatchingStrategy<ASTCDAttribute> getAttributeIncStrategy();
}
