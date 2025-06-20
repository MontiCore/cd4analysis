/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconformance;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconformance.inc.CDIncarnationMapping;
import de.monticore.cdconformance.inc.attribute.CDAttributeMatchingStrategy;
import de.monticore.cdconformance.inc.method.CDMethodMatchingStrategy;
import de.monticore.cdconformance.inc.type.MCTypeMatcher;
import de.monticore.cdmatcher.ExternalCandidatesMatchingStrategy;

import java.util.Set;

public interface CDConformanceContext {
  
  /** @return the concrete class diagram that is currently being completed. */
  ASTCDCompilationUnit getConcreteCD();
  
  /** @return the reference class diagram that is used to complete the concrete class diagram. */
  ASTCDCompilationUnit getReferenceCD();
  
  /**
   * @return the name of the stereotype that is used to annotate the concrete class diagram with the
   * name of the reference model element that they incarnate.
   */
  String getMappingName(); // TODO maybe even have additional config object for this
  
  /**
   * @return the name of the type that is used to mark attribute types, method return types or
   * parameter types as unspecified.
   */
  String getUnderspecifiedPlaceholderTypeName();
  
  /** Parameters for the conformance checker, that also influence the completion behavior. */
  Set<CDConfParameter> getConformanceParams();
  
  /**
   * @return the incarnation strategy that can be used to find incarnations of types in the current
   * context.
   */
  ExternalCandidatesMatchingStrategy<ASTCDType> getTypeIncStrategy();
  
  /**
   * The same type incarnation strategy as {@link #getTypeIncStrategy()} but if the {@link
   * CDConfParameter#INHERITANCE} parameter is present, this strategy will also match concrete types
   * if one of their subtypes is an incarnation of the reference type.
   */
  ExternalCandidatesMatchingStrategy<ASTCDType> getTypeIncStrategyMatchingSubTypes();
  
  /**
   * @return the incarnation strategy that can be used to find incarnations of associations in the
   * current context.
   */
  ExternalCandidatesMatchingStrategy<ASTCDAssociation> getAssociationIncStrategy();
  
  CDAttributeMatchingStrategy getAttributeIncStrategy();
  
  CDMethodMatchingStrategy getMethodIncStrategy();
  
  MCTypeMatcher getMCTypeMatcher();
  
  CDIncarnationMapping getIncarnationMapping();
  
}
