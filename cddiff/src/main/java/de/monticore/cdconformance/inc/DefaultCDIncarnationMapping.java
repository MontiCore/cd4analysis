/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconformance.inc;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._symboltable.CDAssociationSymbol;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconcretization.ConcretizationHelper;
import de.monticore.cdconcretization.util.SymbolUtil;
import de.monticore.cdconformance.inc.attribute.CDAttributeMatchingStrategy;
import de.monticore.cdconformance.inc.mctype.MCTypeMatchingStrategy;
import de.monticore.cdconformance.inc.method.CDMethodMatchingStrategy;
import de.monticore.cdmatcher.ExternalCandidatesMatchingStrategy;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symboltable.IScope;
import de.monticore.symboltable.ISymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class DefaultCDIncarnationMapping implements CDIncarnationMapping {
  
  private final ASTCDCompilationUnit concreteCD;
  private final ExternalCandidatesMatchingStrategy<ASTCDType> typeIncStrategy;
  private final MCTypeMatchingStrategy mcTypeIncStrategy;
  private final CDAttributeMatchingStrategy attributeIncStrategy;
  private final CDMethodMatchingStrategy methodIncStrategy;
  private final ExternalCandidatesMatchingStrategy<ASTCDAssociation> associationIncStrategy;
  
  private final CDIncarnationBindings bindings;
  
  public DefaultCDIncarnationMapping(ASTCDCompilationUnit concreteCD,
      ExternalCandidatesMatchingStrategy<ASTCDType> typeIncStrategy,
      MCTypeMatchingStrategy mcTypeIncStrategy, CDAttributeMatchingStrategy attributeIncStrategy,
      CDMethodMatchingStrategy methodIncStrategy,
      ExternalCandidatesMatchingStrategy<ASTCDAssociation> associationIncStrategy,
      CDIncarnationBindings bindings) {
    this.concreteCD = concreteCD;
    this.typeIncStrategy = typeIncStrategy;
    this.mcTypeIncStrategy = mcTypeIncStrategy;
    this.attributeIncStrategy = attributeIncStrategy;
    this.methodIncStrategy = methodIncStrategy;
    this.associationIncStrategy = associationIncStrategy;
    this.bindings = bindings;
  }
  
  @Override
  public ExternalCandidatesMatchingStrategy<ASTCDType> getTypeIncStrategy() {
    return typeIncStrategy;
  }
  
  @Override
  public MCTypeMatchingStrategy getMCTypeIncStrategy() { return mcTypeIncStrategy; }
  
  @Override
  public CDAttributeMatchingStrategy getAttributeIncStrategy() { return attributeIncStrategy; }
  
  @Override
  public CDMethodMatchingStrategy getMethodIncStrategy() { return methodIncStrategy; }
  
  @Override
  public ExternalCandidatesMatchingStrategy<ASTCDAssociation> getAssociationIncStrategy() {
    return associationIncStrategy;
  }
  
  @Override
  public Set<ASTCDType> getReferenceElements(ASTCDType concreteType) {
    return new HashSet<>(typeIncStrategy.getMatchedElements(concreteType));
  }
  
  @Override
  public Set<ASTCDType> getIncarnations(ASTCDType referenceType) {
    return ConcretizationHelper.getCDTypes(concreteCD).stream().filter(type -> typeIncStrategy
        .isMatched(type, referenceType)).collect(Collectors.toSet());
  }
  
  @Override
  public Set<ASTCDType> getIncarnations(IScope scope, ASTCDType referenceType) {
    return getIncarnations(bindings.getBindings(scope, referenceType.getSymbol()), referenceType);
  }
  
  @Override
  public Set<ASTCDType> getIncarnations(ISymbol contextSymbol, ASTCDType referenceType) {
    return getIncarnations(bindings.getBindings(contextSymbol, referenceType.getSymbol()),
        referenceType);
  }
  
  protected boolean isIncarnation(ISymbol contextSymbol, ASTCDType conType, ASTCDType refType) {
    Set<TypeSymbol> typeBindings = bindings.getBindings(contextSymbol, refType.getSymbol());
    // 1. check for scoped incarnation bindings
    if (!typeBindings.isEmpty()) {
      // map symbols back to AST nodes
      return typeBindings.stream().map(SymbolUtil::cdTypeFromTypeSymbol).anyMatch(c -> c.equals(
          conType));
    }
    else {
      // 2. use usual incarnation strategies
      return getTypeIncStrategy().isMatched(conType, refType);
    }
  }
  
  protected Set<ASTCDType> getIncarnations(Set<TypeSymbol> typeBindings, ASTCDType referenceType) {
    // 1. check for scoped incarnation bindings
    if (!typeBindings.isEmpty()) {
      // map symbols back to AST nodes
      return typeBindings.stream().map(SymbolUtil::cdTypeFromTypeSymbol).collect(Collectors
          .toSet());
    }
    else {
      // 2. Find all incarnations using the usual incarnation strategies
      return getIncarnations(referenceType);
    }
  }
  
  @Override
  public void addBinding(String contextSymbolName, TypeSymbol referenceElement,
      Set<TypeSymbol> concreteElements) {
    bindings.addBinding(contextSymbolName, referenceElement, concreteElements);
  }
  
  @Override
  public Set<TypeSymbol> getBindings(ISymbol contextSymbol, TypeSymbol referenceElement) {
    return bindings.getBindings(contextSymbol, referenceElement);
  }
  
  @Override
  public Set<TypeSymbol> getBindings(IScope concreteScope, TypeSymbol referenceType) {
    return bindings.getBindings(concreteScope, referenceType);
  }
  
  @Override
  public Set<ASTCDAttribute> getReferenceElements(ASTCDAttribute conAttribute) {
    ASTCDType concreteType = (ASTCDType) conAttribute.getSymbol().getEnclosingScope().getAstNode();
    Set<ASTCDAttribute> refElements = new HashSet<>();
    getReferenceElements(concreteType).forEach(refType -> {
      attributeIncStrategy.setReferenceType(refType);
      refElements.addAll(attributeIncStrategy.getMatchedElements(conAttribute));
    });
    return refElements;
  }
  
  @Override
  public Set<ASTCDAttribute> getIncarnations(ASTCDAttribute referenceAttribute) {
    ASTCDType declaringType = (ASTCDType) referenceAttribute.getSymbol().getEnclosingScope()
        .getAstNode();
    return getIncarnations(declaringType).stream().flatMap((cAttributeDeclaringType) -> {
      CDAttributeMatchingStrategy attributeIncStrategy = getAttributeIncStrategy();
      attributeIncStrategy.setReferenceType(declaringType);
      return cAttributeDeclaringType.getCDAttributeList().stream().filter(
          attributeIncarnation -> attributeIncStrategy.isMatched(attributeIncarnation,
              referenceAttribute));
    }).collect(Collectors.toSet());
  }
  
  @Override
  public Set<ASTCDAttribute> getIncarnations(IScope scope, ASTCDAttribute referenceAttribute) {
    Set<FieldSymbol> fieldIncarnationsOpt = bindings.getBindings(scope, referenceAttribute
        .getSymbol());
    if (!fieldIncarnationsOpt.isEmpty()) {
      // map symbols back to AST nodes
      return fieldIncarnationsOpt.stream().map(SymbolUtil::cdAttributeFromFieldSymbol).collect(
          Collectors.toSet());
    }
    else {
      // 2. Find all incarnations using the usual incarnation strategies
      ASTCDType attributeDeclaringType = (ASTCDType) referenceAttribute.getSymbol()
          .getEnclosingScope().getAstNode();
      
      // TODO What about the "deep" case where attributes are matched in supertypes?
      
      return getIncarnations(scope, attributeDeclaringType).stream().flatMap((
          cAttributeDeclaringType) -> {
        attributeIncStrategy.setReferenceType(attributeDeclaringType);
        return cAttributeDeclaringType.getCDAttributeList().stream().filter(
            attributeIncarnation -> attributeIncStrategy.isMatched(attributeIncarnation,
                referenceAttribute));
      }).collect(Collectors.toSet());
    }
  }
  
  @Override
  public void addBinding(String contextSymbolName, FieldSymbol referenceElement,
      Set<FieldSymbol> concreteElements) {
    bindings.addBinding(contextSymbolName, referenceElement, concreteElements);
  }
  
  @Override
  public Set<FieldSymbol> getBindings(ISymbol contextSymbol, FieldSymbol referenceElement) {
    return bindings.getBindings(contextSymbol, referenceElement);
  }
  
  @Override
  public Set<FieldSymbol> getBindings(IScope concreteScope, FieldSymbol referenceElement) {
    return bindings.getBindings(concreteScope, referenceElement);
  }
  
  @Override
  public Set<ASTCDMethod> getReferenceElements(ASTCDMethod concreteElement) {
    ASTCDType concreteType = (ASTCDType) concreteElement.getSymbol().getEnclosingScope()
        .getAstNode();
    Set<ASTCDMethod> refElements = new HashSet<>();
    getReferenceElements(concreteType).forEach(refType -> {
      methodIncStrategy.setReferenceType(refType);
      refElements.addAll(methodIncStrategy.getMatchedElements(concreteElement));
    });
    return refElements;
  }
  
  @Override
  public Set<ASTCDMethod> getIncarnations(ASTCDMethod referenceElement) {
    ASTCDType declaringType = (ASTCDType) referenceElement.getSymbol().getEnclosingScope()
        .getAstNode();
    return getIncarnations(declaringType).stream().flatMap((cMethodDeclaringType) -> {
      methodIncStrategy.setReferenceType(declaringType);
      return cMethodDeclaringType.getCDMethodList().stream().filter(
          methodIncarnation -> methodIncStrategy.isMatched(methodIncarnation, referenceElement));
    }).collect(Collectors.toSet());
  }
  
  @Override
  public Set<ASTCDMethod> getIncarnations(IScope scope, ASTCDMethod referenceElement) {
    Set<MethodSymbol> methodIncarnationsOpt = bindings.getBindings(scope, referenceElement
        .getSymbol());
    if (!methodIncarnationsOpt.isEmpty()) {
      // map symbols back to AST nodes
      return methodIncarnationsOpt.stream().map(SymbolUtil::cdMethodFromMethodSymbol).collect(
          Collectors.toSet());
    }
    else {
      // 2. Find all incarnations using the usual incarnation strategies
      ASTCDType methodDeclaringType = (ASTCDType) referenceElement.getSymbol().getEnclosingScope()
          .getAstNode();
      
      // TODO What about the "deep" case where methods are matched in supertypes?
      
      return getIncarnations(scope, methodDeclaringType).stream().flatMap((
          cMethodDeclaringType) -> {
        methodIncStrategy.setReferenceType(methodDeclaringType);
        return cMethodDeclaringType.getCDMethodList().stream().filter(
            methodIncarnation -> methodIncStrategy.isMatched(methodIncarnation, referenceElement));
      }).collect(Collectors.toSet());
    }
  }
  
  @Override
  public void addBinding(String contextSymbolName, MethodSymbol referenceElement,
      Set<MethodSymbol> concreteElements) {
    bindings.addBinding(contextSymbolName, referenceElement, concreteElements);
  }
  
  @Override
  public Set<MethodSymbol> getBindings(ISymbol contextSymbol, MethodSymbol referenceElement) {
    return bindings.getBindings(contextSymbol, referenceElement);
  }
  
  @Override
  public Set<MethodSymbol> getBindings(IScope scope, MethodSymbol referenceElement) {
    return bindings.getBindings(scope, referenceElement);
  }
  
  @Override
  public Set<ASTCDAssociation> getReferenceElements(ASTCDAssociation concreteAssoc) {
    return new HashSet<>(associationIncStrategy.getMatchedElements(concreteAssoc));
  }
  
  @Override
  public Set<ASTCDAssociation> getIncarnations(ASTCDAssociation referenceAssoc) {
    return concreteCD.getCDDefinition().getCDAssociationsList().stream().filter(
        concAssoc -> associationIncStrategy.isMatched(concAssoc, referenceAssoc)).collect(Collectors
            .toSet());
  }
  
  @Override
  public Set<ASTCDAssociation> getIncarnations(IScope scope, ASTCDAssociation referenceElement) {
    // TODO implement association support
    return null;
  }
  
  @Override
  public void addBinding(String contextSymbolName, CDAssociationSymbol referenceElement,
      Set<CDAssociationSymbol> concreteElements) {
    bindings.addBinding(contextSymbolName, referenceElement, concreteElements);
  }
  
  @Override
  public String computeSymbolKey(ISymbol symbol) {
    return bindings.computeSymbolKey(symbol);
  }
  
  @Override
  public boolean isIncarnation(ASTMCType conType, ASTMCType refType) {
    MCTypeMatchingStrategy mcTypeIncStrategy = getMCTypeIncStrategy();
    mcTypeIncStrategy.setTypeMatcher(getTypeIncStrategy()::isMatched);
    return mcTypeIncStrategy.isMatched(conType, refType);
  }
  
  @Override
  public boolean isIncarnation(ISymbol contextSymbol, ASTMCType conType, ASTMCType refType) {
    MCTypeMatchingStrategy mcTypeIncStrategy = getMCTypeIncStrategy();
    mcTypeIncStrategy.setTypeMatcher((conCDType, refCDType) -> isIncarnation(contextSymbol,
        conCDType, refCDType));
    return mcTypeIncStrategy.isMatched(conType, refType);
  }
  
}
