/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconcretization;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.ICDBasisScope;
import de.monticore.cdconcretization.util.MethodSignatureString;
import de.monticore.symbols.basicsymbols._ast.ASTType;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.oosymbols._ast.ASTField;
import de.monticore.symbols.oosymbols._ast.ASTMethod;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;
import java.util.Optional;

import org.apache.commons.lang3.function.FailableConsumer;
import org.apache.commons.lang3.function.FailableRunnable;

/**
 * Resolves a symbol name to an AST node from a CD. When a matching AST node is found, the
 * appropriate handler for the different kinds of model elements is called (CDType, CDAttribute,
 * CDMethod). This class is used to process references used e.g., in the 'forEach' stereotype which
 * can be of different kinds.
 */
public class CDRefSymbolHandlerDelegator<E extends Throwable> {
  
  private static final String LOG_NAME = CDRefSymbolHandlerDelegator.class.getName();
  
  private FailableConsumer<ASTCDType, E> typeHandler;
  private FailableConsumer<ASTCDAttribute, E> attributeHandler;
  private FailableConsumer<ASTCDMethod, E> methodHandler;
  
  private FailableRunnable<E> defaultHandler;
  
  /**
   * Resolves a symbol and calls the appropriate handler depending on the kind of model element.
   *
   * @param scope the scope to resolve the symbol in
   * @param referenceSymbol the symbol to resolve
   * @param sourcePosition the source position of the reference symbol. For logging purposes
   * @throws E exception that might be thrown from the handlers
   */
  public void resolveSymbol(ICDBasisScope scope, String referenceSymbol,
      SourcePosition sourcePosition) throws E {
    boolean processed = resolveAsTypeSymbol(scope, referenceSymbol, sourcePosition);
    if (!processed) {
      processed = resolveAsAttributeSymbol(scope, referenceSymbol, sourcePosition);
    }
    if (!processed) {
      // IMPORTANT: Method resolution must be the last, as it can have the same name as attributes
      // However, if the name is not unique, we can always add () to indicate a method, but
      // attributes always use the plain name.
      processed = resolveAsMethodSymbol(scope, referenceSymbol, sourcePosition);
    }
    // TODO Support association references
    if (!processed && defaultHandler != null) {
      defaultHandler.run();
    }
  }
  
  /**
   * Tries to resolve the given reference as a type symbol, e.g. 'Foo'.
   *
   * @return true if the reference was processed, false otherwise
   * @throws E exception that might be thrown from the handlers
   */
  protected boolean resolveAsTypeSymbol(ICDBasisScope scope, String referenceSymbol,
      SourcePosition sourcePosition) throws E {
    Optional<TypeSymbol> typeSymbol = scope.resolveType(referenceSymbol);
    if (typeSymbol.isPresent()) {
      ASTType type = typeSymbol.get().getAstNode();
      if (type instanceof ASTCDType) {
        ASTCDType referencedType = (ASTCDType) type;
        Log.debug("Resolved CDType reference: " + referencedType, LOG_NAME);
        if (typeHandler == null) {
          Log.error("A reference to a CDType is not supported @ " + sourcePosition);
          return false;
        }
        typeHandler.accept(referencedType);
        return true;
      }
      else {
        Log.error("Referenced type symbol " + referenceSymbol + " is not a CDType! (type: " + type
            .getClass().getName() + ") @ " + sourcePosition);
      }
    }
    return false;
  }
  
  /**
   * Tries to resolve the given reference as an attribute symbol, e.g. 'Foo.attr'.
   *
   * @return true if the reference was processed, false otherwise
   * @throws E exception that might be thrown from the handlers
   */
  private boolean resolveAsAttributeSymbol(ICDBasisScope scope, String referenceExpr,
      SourcePosition sourcePosition) throws E {
    Optional<FieldSymbol> fieldSymbol = scope.resolveField(referenceExpr);
    if (fieldSymbol.isPresent()) {
      ASTField field = fieldSymbol.get().getAstNode();
      if (field instanceof ASTCDAttribute) {
        ASTCDAttribute referencedAttribute = (ASTCDAttribute) field;
        Log.debug("Resolved CDAttribute reference: " + referencedAttribute, LOG_NAME);
        if (attributeHandler == null) {
          Log.error("A reference to a CDAttribute is not supported @ " + sourcePosition);
          return false;
        }
        attributeHandler.accept(referencedAttribute);
        return true;
      }
      else {
        Log.error("Referenced field symbol " + referenceExpr + " is not a CDAttribute! (type: "
            + field.getClass().getName() + ") @" + sourcePosition);
      }
    }
    return false;
  }
  
  /**
   * Tries to resolve the given reference as a method symbol, e.g. 'Foo.method', or 'Foo.method()'
   * as encoded by {@link MethodSignatureString}.
   *
   * @return true if the reference was processed, false otherwise
   * @throws E exception that might be thrown from the handlers
   */
  private boolean resolveAsMethodSymbol(ICDBasisScope scope, String referenceExpr,
      SourcePosition sourcePosition) throws E {
    Optional<MethodSymbol> methodSymbol = MethodSignatureString.resolveMethodSignature(scope,
        referenceExpr);
    if (methodSymbol.isPresent()) {
      ASTMethod method = methodSymbol.get().getAstNode();
      if (method instanceof ASTCDMethod) {
        ASTCDMethod referencedMethod = (ASTCDMethod) method;
        Log.debug("Resolved CDMethod reference: " + referencedMethod, LOG_NAME);
        if (methodHandler == null) {
          Log.error("A reference to a CDMethod is not supported @ " + sourcePosition);
          return false;
        }
        methodHandler.accept(referencedMethod);
        return true;
      }
      else {
        Log.error("Referenced method symbol " + referenceExpr + " is not a CDMethod! (type: "
            + method.getClass().getName() + ") @" + sourcePosition);
      }
    }
    return false;
  }
  
  /**
   * Sets the handler to be called when a CDType reference is resolved.
   *
   * @param typeHandler the handler to be called
   */
  public CDRefSymbolHandlerDelegator<E> onType(FailableConsumer<ASTCDType, E> typeHandler) {
    this.typeHandler = typeHandler;
    return this;
  }
  
  /**
   * Sets the handler to be called when a CDAttribute reference is resolved.
   *
   * @param attributeHandler the handler to be called
   */
  public CDRefSymbolHandlerDelegator<E> onAttribute(
      FailableConsumer<ASTCDAttribute, E> attributeHandler) {
    this.attributeHandler = attributeHandler;
    return this;
  }
  
  /**
   * Sets the handler to be called when a CDMethod reference is resolved.
   *
   * @param methodHandler the handler to be called
   */
  public CDRefSymbolHandlerDelegator<E> onMethod(FailableConsumer<ASTCDMethod, E> methodHandler) {
    this.methodHandler = methodHandler;
    return this;
  }
  
  /**
   * Sets the default handler to be called when no specific handler is set for a reference.
   *
   * @param defaultHandler the default handler to be called
   */
  public CDRefSymbolHandlerDelegator<E> onDefault(FailableRunnable<E> defaultHandler) {
    this.defaultHandler = defaultHandler;
    return this;
  }
  
}
