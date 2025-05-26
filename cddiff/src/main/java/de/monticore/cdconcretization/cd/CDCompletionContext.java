/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconcretization.cd;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconcretization.ScopedIncarnationBindings;
import de.monticore.cdconcretization.type.TypeCompletionContext;
import de.monticore.cdconformance.CDConfParameter;
import de.monticore.cdmatcher.MatchingStrategy;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symboltable.IScope;
import java.util.Set;

/**
 * Provides useful information for the completion process. This includes access to the top level
 * concrete and reference CD, the mapping name and the matching strategies for the incarnation
 * mapping.
 */
public interface CDCompletionContext {
  
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
  
  boolean isForEachNameAdaptationEnabled();
  
  /**
   * @return the incarnation strategy that can be used to find incarnations of types in the current
   * context.
   */
  MatchingStrategy<ASTCDType> getTypeIncStrategy();
  
  /**
   * The same type incarnation strategy as {@link #getTypeIncStrategy()} but if the {@link
   * CDConfParameter#INHERITANCE} parameter is present, this strategy will also match concrete types
   * if one of their subtypes is an incarnation of the reference type.
   */
  MatchingStrategy<ASTCDType> getTypeIncStrategyMatchingSubTypes();
  
  /**
   * @return the incarnation strategy that can be used to find incarnations of associations in the
   * current context.
   */
  MatchingStrategy<ASTCDAssociation> getAssociationIncStrategy();
  
  /**
   * Creates an attribute matching strategy that matches against the attributes of a specific
   * reference type.<br>
   * <br>
   * <b>Note:</b> Most likely you should use {@link TypeCompletionContext#getAttributeIncStrategy()}
   * instead, which gives you the correct strategy for the current type context.
   *
   * @param referenceType the reference type to which the attributes belong
   * @return the matching strategy that can be used to find incarnations in context of the given
   * type.
   */
  MatchingStrategy<ASTCDAttribute> createAttributeIncStrategy(ASTCDType referenceType);
  
  /**
   * Creates a method matching strategy that matches against the methods of a specific reference
   * type.<br>
   * <br>
   * <b>Note:</b> Most likely you should use {@link TypeCompletionContext#getMethodIncStrategy()}
   * instead, which gives you the correct strategy for the current type context.
   *
   * @param referenceType the reference type to which the methods belong
   * @return the matching strategy that can be used to find incarnations in context of the given
   *     type.
   */
  MatchingStrategy<ASTCDMethod> createMethodIncStrategy(ASTCDType referenceType);

  /**
   * The scoped incarnation binding stored restrictions of the incarnation binding in certain
   * scopes.
   *
   * @return the scoped incarnation binding in this context.
   */
  ScopedIncarnationBindings getScopedIncarnationBindings();
  
  /**
   * Returns all incarnations of the given reference type in the scope of <b>this context</b>.
   *
   * @param referenceType the reference type for which incarnations are searched
   * @return all incarnations of the given reference type in this context
   * @see #getTypeIncarnations(IScope, ASTCDType)
   */
  Set<ASTCDType> getTypeIncarnations(ASTCDType referenceType);
  
  /**
   * Returns all incarnations of the given reference type in a certain scope.<br>
   * <br>
   * The incarnation mapping in a certain scope can be limited to a subset of the incarnations using
   * {@link ScopedIncarnationBindings#addTypeBinding(String, TypeSymbol, Set)}
   *
   * @param scope the scope in which incarnations are searched
   * @param referenceType the reference type for which incarnations are searched
   * @return all incarnations of the given reference type in the given scope
   */
  Set<ASTCDType> getTypeIncarnations(IScope scope, ASTCDType referenceType);
  
  /**
   * Returns all incarnations of the given reference type in the scope of <b>this context</b>.
   *
   * @param referenceAttribute the reference attribute for which incarnations are searched
   * @return all incarnations of the given reference type in this context
   * @see #getAttributeIncarnations(IScope, ASTCDAttribute)
   */
  Set<ASTCDAttribute> getAttributeIncarnations(ASTCDAttribute referenceAttribute);
  
  /**
   * Returns all incarnations of the given reference type in a certain scope. <br>
   * The incarnation mapping in a certain scope can be limited to a subset of the incarnations using
   * {@link ScopedIncarnationBindings#addFieldBinding(String, FieldSymbol, Set)}
   *
   * @param scope the scope in which incarnations are searched
   * @param referenceAttribute the reference attribute for which incarnations are searched
   * @return all incarnations of the given reference type in the given scope
   */
  Set<ASTCDAttribute> getAttributeIncarnations(IScope scope, ASTCDAttribute referenceAttribute);
  
}
