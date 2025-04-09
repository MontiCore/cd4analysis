package de.monticore.cdconcretization;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.ICDBasisScope;
import de.monticore.symbols.basicsymbols._ast.ASTType;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.oosymbols._ast.ASTField;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.lang3.function.FailableConsumer;

import java.util.Optional;

/**
 * Resolves a symbol name to an AST node from a CD. When a matching AST node is found,
 * the appropriate handler for the different kinds of model elements is called (CDType, CDAttribute,
 * CDMethod).
 * This class is used to process references used e.g., in the 'forEach' stereotype which can be
 * of different kinds.
 */
public class CDRefSymbolHandlerDelegator {

  private static final String LOG_NAME = CDRefSymbolHandlerDelegator.class.getName();

  private FailableConsumer<ASTCDType, CompletionException> typeHandler;
  private FailableConsumer<ASTCDAttribute, CompletionException> attributeHandler;

  /**
   * Resolves a symbol and calls the appropriate handler depending on the kind of model element.
   *
   * @param scope the scope to resolve the symbol in
   * @param referenceSymbol the symbol to resolve
   * @param sourcePosition the source position of the reference symbol. For logging purposes
   * @throws CompletionException
   */
  public void resolveSymbol(
          ICDBasisScope scope, String referenceSymbol, SourcePosition sourcePosition)
          throws CompletionException {
    boolean processed = resolveAsTypeSymbol(scope, referenceSymbol, sourcePosition);
    if (!processed) {
      processed = resolveAsAttributeSymbol(scope, referenceSymbol, sourcePosition);
    }
    // TODO Support method, and association references
    if (!processed) {
      // TODO Rework error logging/reporting & exception usage
      throw new CompletionException(
              "Unsupported forEach reference referenceSymbol" + referenceSymbol + " at " + sourcePosition);
    }
  }

  /**
   * Tries to resolve the given reference as a type symbol, e.g. 'Foo'.
   *
   * @return true if the reference was processed, false otherwise
   * @throws CompletionException
   */
  protected boolean resolveAsTypeSymbol(
          ICDBasisScope scope, String referenceSymbol, SourcePosition sourcePosition)
          throws CompletionException {
    Optional<TypeSymbol> typeSymbol = scope.resolveType(referenceSymbol);
    if (typeSymbol.isPresent()) {
      ASTType type = typeSymbol.get().getAstNode();
      if (type instanceof ASTCDType) {
        ASTCDType rTargetType = (ASTCDType) type;
        Log.debug("Resolved CDType reference: " + rTargetType, LOG_NAME);
        if (typeHandler == null) {
          throw new CompletionException(
                  "A reference to a CDType is not supported @ " + sourcePosition);
        }
        typeHandler.accept(rTargetType);
        return true;
      } else {
        throw new CompletionException(
                "Referenced type symbol "
                        + referenceSymbol
                        + " is not a CDType! (type: "
                        + type.getClass().getName()
                        + ") @ " + sourcePosition);
      }
    }
    return false;
  }

  /**
   * Tries to resolve the given reference as an attribute symbol, e.g. 'Foo.attr'.
   *
   * @return true if the reference was processed, false otherwise
   * @throws CompletionException
   */
  private boolean resolveAsAttributeSymbol(
          ICDBasisScope scope, String referenceExpr, SourcePosition sourcePosition)
          throws CompletionException {
    Optional<FieldSymbol> fieldSymbol = scope.resolveField(referenceExpr);
    if (fieldSymbol.isPresent()) {
      ASTField field = fieldSymbol.get().getAstNode();
      if (field instanceof ASTCDAttribute) {
        ASTCDAttribute rTargetAttribute = (ASTCDAttribute) field;
        Log.debug("Resolved CDAttribute reference: " + rTargetAttribute, LOG_NAME);
        if (attributeHandler == null) {
          throw new CompletionException(
                  "A reference to a CDAttribute is not supported @ " + sourcePosition);
        }
        attributeHandler.accept(rTargetAttribute);
        return true;
      } else {
        throw new CompletionException(
                "Referenced field symbol "
                        + referenceExpr
                        + " is not a CDAttribute! (type: "
                        + field.getClass().getName()
                        + ") @" + sourcePosition);
      }
    }
    return false;
  }

  /**
   * Sets the handler to be called when a CDType reference is resolved.
   *
   * @param typeHandler the handler to be called
   */
  public void setTypeHandler(FailableConsumer<ASTCDType, CompletionException> typeHandler) {
    this.typeHandler = typeHandler;
  }

  /**
   * Sets the handler to be called when a CDAttribute reference is resolved.
   *
   * @param attributeHandler the handler to be called
   */
  public void setAttributeHandler(
          FailableConsumer<ASTCDAttribute, CompletionException> attributeHandler) {
    this.attributeHandler = attributeHandler;
  }
}
