package de.monticore.cdconcretization.cd;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconcretization.ScopedIncarnationBindings;
import de.monticore.cdconformance.CDConfParameter;
import de.monticore.cdmatcher.MatchingStrategy;
import de.monticore.symboltable.IScope;
import java.util.Set;

/**
 * Provides basic information for the completion process. This includes access to the top level
 * concrete and reference CD, the mapping name and the matching strategies for the incarnation
 * mapping.
 */
public interface CDCompletionContext {

  ASTCDCompilationUnit getConcreteCD();

  ASTCDCompilationUnit getReferenceCD();

  String getMappingName(); // TODO maybe even have additional config object for this

  String getUnderspecifiedPlaceholderTypeName();

  /** Parameters for the conformance checker, that also influence the completion behavior. */
  Set<CDConfParameter> getConformanceParams();

  MatchingStrategy<ASTCDType> getTypeIncStrategy();

  MatchingStrategy<ASTCDType> getTypeIncStrategyMatchingSubTypes();

  MatchingStrategy<ASTCDAssociation> getAssociationIncStrategy();

  MatchingStrategy<ASTCDAttribute> getAttributeIncStrategy(
      ASTCDType concreteType, ASTCDType referenceType);

  ScopedIncarnationBindings getScopedIncarnationBindings();

  Set<ASTCDType> getTypeIncarnations(ASTCDType referenceType);

  Set<ASTCDType> getTypeIncarnations(IScope scope, ASTCDType referenceType);

  Set<ASTCDAttribute> getAttributeIncarnations(ASTCDAttribute referenceAttribute);

  Set<ASTCDAttribute> getAttributeIncarnations(IScope scope, ASTCDAttribute referenceAttribute);
}
