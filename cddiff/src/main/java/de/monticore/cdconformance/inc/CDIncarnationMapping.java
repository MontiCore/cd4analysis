/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconformance.inc;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconcretization.util.SymbolUtil;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symboltable.IScope;
import de.monticore.symboltable.ISymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Central interface representing the incarnation mapping between a concrete and reference class
 * diagram.
 */
public interface CDIncarnationMapping extends CDIncarnationBindings {
  
  // TODO Consider renaming the "global" getIncarnations/isIncarnation methods to avoid confusion with the scope-based methods
  
  // ------------------------
  // ----- Type Mapping -----
  // ------------------------
  
  /**
   * Checks whether the given concrete type is an incarnation of the given reference type without
   * considering any binding restrictions.
   *
   * @param conType the concrete type to check
   * @param refType the reference type to check against
   * @return true if the concrete type is an incarnation of the reference type, false otherwise
   */
  boolean isIncarnation(ASTCDType conType, ASTCDType refType);
  
  /**
   * Checks whether the given concrete type is an incarnation of the given reference type in the
   * context of the given symbol.<br>
   * <br>
   * This method considers all incarnation bindings that are attached to the context symbol or
   * its enclosing scope.<br>
   *
   * @param contextSymbol the context symbol in which the incarnation mapping is checked
   * @param conType the concrete type to check
   * @param refType the reference type to check against
   * @return
   */
  boolean isIncarnation(ISymbol contextSymbol, ASTCDType conType, ASTCDType refType);
  
  /**
   * Returns all reference types incarnated by the given concrete element.
   *
   * @param concreteElement the concrete element for which reference types are searched
   * @return all reference types incarnated by the given concrete element
   */
  Set<ASTCDType> getReferenceElements(ASTCDType concreteElement);
  
  /**
   * Returns all incarnations of the given reference type in the concrete model without considering
   * any binding restrictions.
   *
   * @param referenceElement the reference type for which incarnations are searched
   * @return all type incarnations of the given reference type in the concrete model
   */
  Set<ASTCDType> getIncarnations(ASTCDType referenceElement);
  
  /**
   * Returns all incarnations of the given reference type in the concrete model.
   *
   * @param referenceElement the reference type for which incarnations are searched
   * @return all type incarnations of the given reference type in the concrete model
   */
  default Set<TypeSymbol> getIncarnations(TypeSymbol referenceElement) {
    return getIncarnations(SymbolUtil.cdTypeFromTypeSymbol(referenceElement)).stream().map(
        ASTCDType::getSymbol).collect(Collectors.toSet());
  }
  
  /**
   * Returns all incarnations of the given reference type in a certain scope.<br>
   * <br>
   * The incarnation mapping in a certain scope can be limited to a subset of the incarnations using
   * {@link #addBinding(String, TypeSymbol, Set)}
   *
   * @param scope the scope in which incarnations are searched
   * @param referenceType the reference type for which incarnations are searched
   * @return all incarnations of the given reference type in the given scope
   */
  Set<ASTCDType> getIncarnations(IScope scope, ASTCDType referenceType);
  
  /**
   * Returns all incarnations of the given reference type in the context of the given symbol.<br>
   * <br>
   * This method considers all incarnation bindings that are attached to the context symbol or
   * its enclosing scope.<br>
   *
   * @param contextSymbol the context symbol in which the incarnations are searched
   * @param referenceType the reference type for which incarnations are searched
   * @return all incarnations of the given reference type in the context of the given symbol
   */
  Set<ASTCDType> getIncarnations(ISymbol contextSymbol, ASTCDType referenceType);
  
  /**
   * Returns all incarnations of the given reference type in a certain scope.<br>
   *
   * @param scope the scope in which incarnations are searched
   * @param referenceElement the reference type for which incarnations are searched
   * @return all incarnations of the given reference type in the given scope
   */
  default Set<TypeSymbol> getIncarnations(IScope scope, TypeSymbol referenceElement) {
    return getIncarnations(scope, SymbolUtil.cdTypeFromTypeSymbol(referenceElement)).stream().map(
        ASTCDType::getSymbol).collect(Collectors.toSet());
  }
  
  // -----------------------------
  // ----- Attribute Mapping -----
  // -----------------------------
  
  /**
   * Checks whether the given concrete attribute is an incarnation of the given reference attribute
   * without considering any binding restrictions.
   *
   * @param conAttribute the concrete attribute to check
   * @param refAttribute the reference attribute to check against
   * @return true if the concrete attribute is an incarnation of the reference attribute, false
   * otherwise
   */
  boolean isIncarnation(ASTCDAttribute conAttribute, ASTCDAttribute refAttribute);
  
  /**
   * Checks whether the given concrete attribute is an incarnation of the given reference attribute
   * in the context of the given symbol.<br>
   * <br>
   * This method considers all incarnation bindings that are attached to the context symbol or
   * its enclosing scope.<br>
   *
   * @param contextSymbol the context symbol in which the incarnation mapping is checked
   * @param conAttribute the concrete attribute to check
   * @param refAttribute the reference attribute to check against
   * @return true if the concrete attribute is an incarnation of the reference attribute, false
   * otherwise
   */
  boolean isIncarnation(ISymbol contextSymbol, ASTCDAttribute conAttribute,
      ASTCDAttribute refAttribute);
  
  /**
   * Returns all reference attributes incarnated by the given concrete attribute.
   *
   * @param concreteAttribute the concrete element for which reference attributes are searched
   * @return all reference attributes incarnated by the given concrete element
   */
  Set<ASTCDAttribute> getReferenceElements(ASTCDAttribute concreteAttribute);
  
  /**
   * Returns all reference attributes incarnated by the given concrete attribute that are declared
   * in the given reference type.
   *
   * @param concreteAttribute the concrete element for which reference attributes are searched
   * @param declaringRefType the reference type in which the attributes are declared
   * @return all reference attribute in the declaring type incarnated by the given concrete element
   */
  Set<ASTCDAttribute> getReferenceElements(ASTCDAttribute concreteAttribute,
      ASTCDType declaringRefType);
  
  /**
   * Returns all incarnations of the given reference attribute in the concrete model without
   * considering any binding restrictions.
   *
   * @param referenceElement the reference attribute for which incarnations are searched
   * @return all attribute incarnations of the given reference attribute in the concrete model
   */
  Set<ASTCDAttribute> getIncarnations(ASTCDAttribute referenceElement);
  
  /**
   * Returns all incarnations of the given reference type in a certain scope. <br>
   * The incarnation mapping in a certain scope can be limited to a subset of the incarnations using
   * {@link DefaultCDIncarnationBindings#addBinding(String, FieldSymbol, Set)}
   *
   * @param scope the scope in which incarnations are searched
   * @param referenceAttribute the reference attribute for which incarnations are searched
   * @return all incarnations of the given reference type in the given scope
   */
  Set<ASTCDAttribute> getIncarnations(IScope scope, ASTCDAttribute referenceAttribute);
  
  default Set<FieldSymbol> getIncarnations(FieldSymbol referenceElement) {
    return getIncarnations(SymbolUtil.cdAttributeFromFieldSymbol(referenceElement)).stream().map(
        ASTCDAttribute::getSymbol).collect(Collectors.toSet());
  }
  
  // --------------------------
  // ----- Method Mapping -----
  // --------------------------
  
  /**
   * Checks whether the given concrete method is an incarnation of the given reference method
   * without considering any binding restrictions.
   *
   * @param conMethod the concrete method to check
   * @param refMethod the reference method to check against
   * @return true if the concrete method is an incarnation of the reference method, false otherwise
   */
  boolean isIncarnation(ASTCDMethod conMethod, ASTCDMethod refMethod);
  
  /**
   * Checks whether the given concrete method is an incarnation of the given reference method
   * in the context of the given symbol.<br>
   * <br>
   * This method considers all incarnation bindings that are attached to the context symbol or
   * its enclosing scope.<br>
   *
   * @param contextSymbol the context symbol in which the incarnation mapping is checked
   * @param conMethod the concrete method to check
   * @param refMethod the reference method to check against
   * @return true if the concrete method is an incarnation of the reference method, false otherwise
   */
  boolean isIncarnation(ISymbol contextSymbol, ASTCDMethod conMethod, ASTCDMethod refMethod);
  
  /**
   * Returns all reference methods incarnated by the given concrete method.
   *
   * @param concreteMethod the concrete element for which reference methods are searched
   * @return all reference methods incarnated by the given concrete element
   */
  Set<ASTCDMethod> getReferenceElements(ASTCDMethod concreteMethod);
  
  /**
   * Returns all reference methods incarnated by the given concrete element that are declared
   * in the given reference type.
   *
   * @param concreteElement the concrete element for which reference methods are searched
   * @param declaringRefType the reference type in which the methods are declared
   * @return all reference methods in the declaring type incarnated by the given concrete element
   */
  Set<ASTCDMethod> getReferenceElements(ASTCDMethod concreteElement, ASTCDType declaringRefType);
  
  /**
   * Returns all incarnations of the given reference method in the concrete model without
   * considering any binding restrictions.
   *
   * @param referenceElement the reference method for which incarnations are searched
   * @return all method incarnations of the given reference method in the concrete model
   */
  Set<ASTCDMethod> getIncarnations(ASTCDMethod referenceElement);
  
  /**
   * Returns all incarnations of the given reference method in a certain scope.<br>
   * <br>
   * The incarnation mapping in a certain scope can be limited to a subset of the incarnations using
   * {@link CDIncarnationBindings#addBinding(ISymbol, MethodSymbol, Set)}
   *
   * @param scope the scope in which incarnations are searched
   * @param referenceElement the reference method for which incarnations are searched
   * @return all incarnations of the given reference method in the given scope
   */
  Set<ASTCDMethod> getIncarnations(IScope scope, ASTCDMethod referenceElement);
  
  // -------------------------------
  // ----- Association Mapping -----
  // -------------------------------
  
  Set<ASTCDAssociation> getReferenceElements(ASTCDAssociation concreteElement);
  
  Set<ASTCDAssociation> getIncarnations(ASTCDAssociation referenceElement);
  
  Set<ASTCDAssociation> getIncarnations(IScope scope, ASTCDAssociation referenceElement);
  
  boolean isIncarnation(ASTCDAssociation conAssociation, ASTCDAssociation refAssociation);
  
  boolean isIncarnation(ISymbol contextSymbol, ASTCDAssociation conAssociation,
      ASTCDAssociation refAssociation);
  
  // -------------------------------
  // ----- MCType Mapping -----
  // -------------------------------
  
  boolean isIncarnation(ASTMCType conType, ASTMCType refType);
  
  boolean isIncarnation(ISymbol contextSymbol, ASTMCType conType, ASTMCType refType);
  
}
