/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconformance.inc;

import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._visitor.CD4CodeBasisVisitor2;
import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._visitor.CDAssociationVisitor2;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdbasis._symboltable.ICDBasisScope;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.cdconcretization.CDRefSymbolHandlerDelegator;
import de.monticore.cdconcretization.CompletionException;
import de.monticore.cdconcretization.stereotype.BindingValue;
import de.monticore.cdconcretization.stereotype.StereotypeUtil;
import de.monticore.cdconcretization.util.SymbolUtil;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symboltable.ISymbol;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

/**
 * Visitor that derives incarnation bindings for CD elements from "bind" stereotypes.
 */
public class STBindingDerivingVisitor implements CDBasisVisitor2, CD4CodeBasisVisitor2,
    CDAssociationVisitor2 {
  
  private static final String LOG_NAME = STBindingDerivingVisitor.class.getName();
  
  private final CDIncarnationMapping incMapping;
  
  private boolean foundInvalidBinding = false;
  
  public STBindingDerivingVisitor(CDIncarnationMapping incMapping) {
    this.incMapping = incMapping;
  }
  
  public void addToTraverser(CD4CodeTraverser traverser) {
    traverser.add4CDBasis(this);
    traverser.add4CD4CodeBasis(this);
    traverser.add4CDAssociation(this);
  }
  
  public boolean hasFoundInvalidBinding() {
    return foundInvalidBinding;
  }
  
  @Override
  public void visit(ASTCDType node) {
    checkForBindingStereotype(node.getEnclosingScope(), node.getSymbol(), node.getModifier());
  }
  
  @Override
  public void visit(ASTCDAttribute node) {
    checkForBindingStereotype(node.getEnclosingScope(), node.getSymbol(), node.getModifier());
  }
  
  @Override
  public void visit(ASTCDMethod node) {
    checkForBindingStereotype(node.getEnclosingScope(), node.getSymbol(), node.getModifier());
  }
  
  @Override
  public void visit(ASTCDAssociation node) {
    // TODO check this when working on associations
    // an association side has only a symbol if it has a name
    // Either, we not allow binding stereotypes on unnamed associations or we add the bindings
    // from the association level to each association side (always having an implicit name from the
    // source/target type).
    //checkForBindingStereotype(node.getEnclosingScope(), node.getSymbol(), node.getModifier());
  }
  
  @Override
  public void visit(ASTCDAssocSide node) {
    // TODO check this when working on associations
    // an association side has only a symbol if it has a role name
    //  However, we can always have an implicit name from the source/target type!
    //checkForBindingStereotype(node.getEnclosingScope(), node.getSymbol(), node.getModifier());
  }
  
  @Override
  public void visit(ASTCDDefinition node) {
    // we have no use case for this yet, but why not allow it?
    checkForBindingStereotype(node.getEnclosingScope(), node.getSymbol(), node.getModifier());
  }
  
  /**
   * Checks if the given modifier contains a binding stereotype and resolves it in the given scope.
   * If the binding can be resolved, it will be added to the incarnation mapping.
   *
   * @param concreteScope the scope in which the binding is resolved
   * @param cdSymbol the CD symbol to which the binding applies
   * @param modifier the modifier that may contain a binding stereotype
   */
  private void checkForBindingStereotype(ICDBasisScope concreteScope, ISymbol cdSymbol,
      ASTModifier modifier) {
    Optional<BindingValue> binding = StereotypeUtil.getIncarnationBindingStereotypeValue(modifier,
        "Bind stereotype without value");
    if (binding.isPresent()) {
      CDRefSymbolHandlerDelegator handler = new CDRefSymbolHandlerDelegator();
      handler.setTypeHandler((refType) -> handleTypeBinding(concreteScope, cdSymbol, refType,
          binding.get()));
      handler.setAttributeHandler((refAttribute) -> handleAttributeBinding(concreteScope, cdSymbol,
          refAttribute, binding.get()));
      // TODO support methods & associations
      try {
        handler.resolveSymbol(concreteScope, binding.get().getReferenceName(), modifier
            .get_SourcePositionStart());
      }
      catch (CompletionException e) {
        // TODO refactor to not throw completion exception for "user errors", e.g. invalid model -> Log.error instead
        throw new RuntimeException(e);
      }
    }
  }
  
  /**
   * Handles the binding for a reference type by resolving each incarnation as type symbol in the
   * given scope and adding the bindings to the incarnation mapping.
   *
   * @param concreteScope the scope in which symbols are resolved
   * @param annotatedSymbol the symbol that is annotated with the binding stereotype
   * @param referenceType the reference type that is bound with this binding
   * @param binding the binding value that contains the incarnation names
   */
  private void handleTypeBinding(ICDBasisScope concreteScope, ISymbol annotatedSymbol,
      ASTCDType referenceType, BindingValue binding) {
    for (String incarnationName : binding.getIncarnationNames()) {
      Optional<CDTypeSymbol> incarnationType = concreteScope.resolveCDType(incarnationName);
      if (incarnationType.isPresent()) {
        // TODO refactor by introducing a separate method for checking if the field is an
        //  incarnation of the reference attribute -> maybe in CDIncarnationMapping?
        if (incMapping.getReferenceElements(incarnationType.get().getAstNode()).contains(
            referenceType)) {
          incMapping.addBinding(annotatedSymbol.getFullName(), referenceType.getSymbol(),
              incarnationType.get());
          Log.info("Added binding from stereotype @ '" + annotatedSymbol.getFullName() + "':  "
              + referenceType.getSymbol().getFullName() + "=" + incarnationType.get().getFullName(),
              LOG_NAME);
        }
        else {
          Log.warn("The incarnation binding '" + incarnationType.get().getFullName()
              + "' is not an incarnation of reference type '" + referenceType.getSymbol()
                  .getFullName() + "'.", annotatedSymbol.getSourcePosition());
          foundInvalidBinding = true;
        }
      }
      else {
        Log.warn("The incarnation binding '" + incarnationName + "' cannot be resolved as a "
            + "CDType. Reference type: '" + referenceType.getName() + "'", annotatedSymbol
                .getSourcePosition());
        foundInvalidBinding = true;
      }
    }
  }
  
  /**
   * Handles the binding for a reference attribute by resolving each incarnation as field symbol
   * in the given scope and adding the bindings to the incarnation mapping.
   *
   * @param scope the scope in which symbols are resolved
   * @param annotatedSymbol the symbol that is annotated with the binding stereotype
   * @param referenceAttribute the reference attribute that is bound with this binding
   * @param binding the binding value that contains the incarnation names
   */
  private void handleAttributeBinding(ICDBasisScope scope, ISymbol annotatedSymbol,
      ASTCDAttribute referenceAttribute, BindingValue binding) {
    for (String incarnationName : binding.getIncarnationNames()) {
      Optional<FieldSymbol> incarnationField = scope.resolveField(incarnationName);
      if (incarnationField.isPresent()) {
        // TODO refactor by introducing a separate method for checking if the field is an
        //  incarnation of the reference attribute -> maybe in CDIncarnationMapping?
        if (incMapping.getReferenceElements(SymbolUtil.cdAttributeFromFieldSymbol(incarnationField
            .get())).contains(referenceAttribute)) {
          incMapping.addBinding(annotatedSymbol.getFullName(), referenceAttribute.getSymbol(),
              incarnationField.get());
          Log.info("Added binding from stereotype @ '" + annotatedSymbol.getFullName() + "':  "
              + referenceAttribute.getSymbol().getFullName() + "=" + incarnationField.get()
                  .getFullName(), LOG_NAME);
        }
        else {
          Log.warn("The incarnation binding '" + incarnationField.get().getFullName()
              + "' is not an incarnation of reference attribute '" + referenceAttribute.getSymbol()
                  .getFullName() + "'.", annotatedSymbol.getSourcePosition());
          foundInvalidBinding = true;
        }
      }
      else {
        Log.warn("The incarnation binding '" + incarnationName + "' cannot be resolved as a "
            + "CDAttribute. Reference attribute: '" + referenceAttribute.getName() + "'",
            annotatedSymbol.getSourcePosition());
        foundInvalidBinding = true;
      }
    }
  }
  
}
