package de.monticore.cdconcretization;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdmatcher.MatchingStrategy;

/**
 * Provides basic information for the completion process. This includes access to the top level
 * concrete and reference CD, the mapping name and the matching strategies for the incarnation
 * mapping.
 */
public interface CompletionContext {

  ASTCDCompilationUnit getConcreteCD();

  ASTCDCompilationUnit getReferenceCD();

  String getMappingName(); // TODO maybe even have additional config object for this

  MatchingStrategy<ASTCDType> getTypeIncStrategy();

  MatchingStrategy<ASTCDType> getTypeIncStrategyMatchingSubTypes();

  MatchingStrategy<ASTCDAssociation> getAssociationIncStrategy();
}
