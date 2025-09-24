/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconformance.inc;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdconcretization.ConcretizationHelper;
import de.monticore.cdconformance.inc.attribute.CDAttributeMatchingStrategy;
import de.monticore.cdconformance.inc.method.CDMethodMatchingStrategy;
import de.monticore.cdmatcher.ExternalCandidatesMatchingStrategy;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.symbols.oosymbols.refmodel.IOOSymbolsLocalIncMapping;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents the full incarnation mapping between a reference and a concrete CD model
 * <i>without</i> any restrictions by bindings.<br>
 * <br>
 * This is basically, the "link" between the AST-based, handwritten matching strategies and the
 * systematic, symbol-based incarnation mapping & binding infrastructure which could be generated
 * automatically in the future.
 */
public class CDFullIncMapping implements IOOSymbolsLocalIncMapping {
  
  private final ASTCDCompilationUnit concreteCD;
  private final ExternalCandidatesMatchingStrategy<ASTCDType> typeIncStrategy;
  private final CDAttributeMatchingStrategy attributeIncStrategy;
  private final CDMethodMatchingStrategy methodIncStrategy;
  
  public CDFullIncMapping(ASTCDCompilationUnit concreteCD,
      ExternalCandidatesMatchingStrategy<ASTCDType> typeIncStrategy,
      CDAttributeMatchingStrategy attributeIncStrategy,
      CDMethodMatchingStrategy methodIncStrategy) {
    this.concreteCD = concreteCD;
    this.typeIncStrategy = typeIncStrategy;
    this.attributeIncStrategy = attributeIncStrategy;
    this.methodIncStrategy = methodIncStrategy;
  }
  
  @Override
  public Set<OOTypeSymbol> getIncarnations(OOTypeSymbol typeSymbol) {
    return getIncarnations((TypeSymbol) typeSymbol).stream().filter(m -> m instanceof OOTypeSymbol)
        .map(m -> (OOTypeSymbol) m).collect(Collectors.toSet());
  }
  
  @Override
  public Set<FieldSymbol> getIncarnations(FieldSymbol fieldSymbol) {
    return getIncarnations((VariableSymbol) fieldSymbol).stream().filter(
        m -> m instanceof FieldSymbol).map(m -> (FieldSymbol) m).collect(Collectors.toSet());
  }
  
  @Override
  public Set<MethodSymbol> getIncarnations(MethodSymbol methodSymbol) {
    return getIncarnations((FunctionSymbol) methodSymbol).stream().filter(
        m -> m instanceof MethodSymbol).map(m -> (MethodSymbol) m).collect(Collectors.toSet());
  }
  
  @Override
  public Set<TypeSymbol> getIncarnations(TypeSymbol typeSymbol) {
    if (typeSymbol.isPresentAstNode() && typeSymbol.getAstNode() instanceof ASTCDType) {
      return getASTCDTypeIncarnations((ASTCDType) typeSymbol.getAstNode()).stream().map(
          ASTCDType::getSymbol).collect(Collectors.toSet());
    }
    else {
      return Collections.emptySet();
    }
  }
  
  @Override
  public Set<VariableSymbol> getIncarnations(VariableSymbol variableSymbol) {
    if (variableSymbol.isPresentAstNode() && variableSymbol
        .getAstNode() instanceof ASTCDAttribute) {
      ASTCDAttribute referenceAttribute = (ASTCDAttribute) variableSymbol.getAstNode();
      ASTCDType attributeDeclaringType = (ASTCDType) variableSymbol.getEnclosingScope()
          .getAstNode();
      
      // TODO What about the "deep" case where attributes are matched in supertypes?
      return getASTCDTypeIncarnations(attributeDeclaringType).stream().flatMap(
          cAttributeDeclaringType -> {
            attributeIncStrategy.setReferenceType(attributeDeclaringType);
            return cAttributeDeclaringType.getCDAttributeList().stream().filter(
                attributeIncarnation -> attributeIncStrategy.isMatched(attributeIncarnation,
                    referenceAttribute));
          }).map(ASTCDAttribute::getSymbol).collect(Collectors.toSet());
    }
    else {
      return Collections.emptySet();
    }
  }
  
  @Override
  public Set<FunctionSymbol> getIncarnations(FunctionSymbol functionSymbol) {
    if (functionSymbol.isPresentAstNode() && functionSymbol.getAstNode() instanceof ASTCDMethod) {
      ASTCDMethod referenceMethod = (ASTCDMethod) functionSymbol.getAstNode();
      ASTCDType declaringType = (ASTCDType) functionSymbol.getEnclosingScope().getAstNode();
      
      // TODO What about the "deep" case where attributes are matched in supertypes?
      return getASTCDTypeIncarnations(declaringType).stream().flatMap(cAttributeDeclaringType -> {
        methodIncStrategy.setReferenceType(declaringType);
        return cAttributeDeclaringType.getCDMethodList().stream().filter(
            methodInc -> methodIncStrategy.isMatched(methodInc, referenceMethod));
      }).map(ASTCDMethod::getSymbol).collect(Collectors.toSet());
    }
    else {
      return Collections.emptySet();
    }
  }
  
  protected Set<ASTCDType> getASTCDTypeIncarnations(ASTCDType cdType) {
    return ConcretizationHelper.getCDTypes(concreteCD).stream().filter(type -> typeIncStrategy
        .isMatched(type, cdType)).collect(Collectors.toSet());
  }
  
}
