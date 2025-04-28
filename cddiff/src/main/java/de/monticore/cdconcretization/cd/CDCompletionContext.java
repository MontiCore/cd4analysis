package de.monticore.cdconcretization.cd;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconcretization.ScopedIncarnationBindings;
import de.monticore.cdconcretization.type.TypeCompletionContext;
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

  /**
   * Creates an attribute matching strategy that matches the attributes of a specific concrete type
   * with attributes of a specific reference type. The strategy is only valid when comparing
   * attributes of these two given types!<br>
   * <br>
   * <b>Note:</b> Most likely you should use {@link TypeCompletionContext#getAttributeIncStrategy()}
   * instead, which gives you the correct strategy for the current type context.
   *
   * @param concreteType the concrete type to which the attributes belong
   * @param referenceType the reference type to which the attributes belong
   * @return the matching strategy that can be used to find incarnations in context of the given
   * type.
   */
  MatchingStrategy<ASTCDAttribute> createAttributeIncStrategy(
      ASTCDType concreteType, ASTCDType referenceType);

  ScopedIncarnationBindings getScopedIncarnationBindings();

  Set<ASTCDType> getTypeIncarnations(ASTCDType referenceType);

  Set<ASTCDType> getTypeIncarnations(IScope scope, ASTCDType referenceType);

  Set<ASTCDAttribute> getAttributeIncarnations(ASTCDAttribute referenceAttribute);

  Set<ASTCDAttribute> getAttributeIncarnations(IScope scope, ASTCDAttribute referenceAttribute);
}
