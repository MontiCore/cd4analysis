package de.monticore.cdconcretization.type;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconcretization.cd.CDCompletionContext;
import de.monticore.cdmatcher.MatchingStrategy;

public interface TypeCompletionContext extends CDCompletionContext {

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
