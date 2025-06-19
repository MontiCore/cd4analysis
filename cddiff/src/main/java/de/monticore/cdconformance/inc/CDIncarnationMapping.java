/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconformance.inc;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconcretization.util.SymbolUtil;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
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
  
  boolean isIncarnation(ASTCDType conType, ASTCDType refType);
  
  boolean isIncarnation(ISymbol contextSymbol, ASTCDType conType, ASTCDType refType);
  
  Set<ASTCDType> getReferenceElements(ASTCDType concreteElement);
  
  Set<ASTCDType> getIncarnations(ASTCDType referenceElement);
  
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
  
  Set<ASTCDType> getIncarnations(ISymbol contextSymbol, ASTCDType referenceType);
  
  default Set<TypeSymbol> getIncarnations(IScope scope, TypeSymbol referenceElement) {
    return getIncarnations(scope, SymbolUtil.cdTypeFromTypeSymbol(referenceElement)).stream().map(
        ASTCDType::getSymbol).collect(Collectors.toSet());
  }
  
  // -----------------------------
  // ----- Attribute Mapping -----
  // -----------------------------
  
  boolean isIncarnation(ASTCDAttribute conAttribute, ASTCDAttribute refAttribute);
  
  boolean isIncarnation(ISymbol contextSymbol, ASTCDAttribute conAttribute,
      ASTCDAttribute refAttribute);
  
  Set<ASTCDAttribute> getReferenceElements(ASTCDAttribute concreteElement);
  
  /**
   * Returns all reference attributes incarnated by the given concrete element that are declared
   * in the given reference type.<br>
   *
   * @param concreteElement
   * @param declaringRefType
   * @return
   */
  Set<ASTCDAttribute> getReferenceElements(ASTCDAttribute concreteElement,
      ASTCDType declaringRefType);
  
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
  
  // --------------------------
  // ----- Method Mapping -----
  // --------------------------
  
  Set<ASTCDMethod> getReferenceElements(ASTCDMethod concreteElement);
  
  /**
   * Returns all reference methods incarnated by the given concrete element that are declared
   * in the given reference type.<br>
   *
   * @param concreteElement
   * @param declaringRefType
   * @return
   */
  Set<ASTCDMethod> getReferenceElements(ASTCDMethod concreteElement, ASTCDType declaringRefType);
  
  Set<ASTCDMethod> getIncarnations(ASTCDMethod referenceElement);
  
  Set<ASTCDMethod> getIncarnations(IScope scope, ASTCDMethod referenceElement);
  
  boolean isIncarnation(ASTCDMethod conMethod, ASTCDMethod refMethod);
  
  boolean isIncarnation(ISymbol contextSymbol, ASTCDMethod conMethod, ASTCDMethod refMethod);
  
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
