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
import de.monticore.refmodel.Binding;
import de.monticore.refmodel.BindingConflictException;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symbols.oosymbols.refmodel.IOOSymbolsIncMapping;
import de.monticore.symbols.oosymbols.refmodel.OOSymbolsIncMapping;
import de.monticore.symboltable.IScope;
import de.monticore.symboltable.ISymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import org.apache.commons.lang3.NotImplementedException;

import java.util.Collections;
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
  
  private final OOSymbolsIncMapping ooSymbolsIncMapping;
  
  public DefaultCDIncarnationMapping(ASTCDCompilationUnit concreteCD,
      ExternalCandidatesMatchingStrategy<ASTCDType> typeIncStrategy,
      MCTypeMatchingStrategy mcTypeIncStrategy, CDAttributeMatchingStrategy attributeIncStrategy,
      CDMethodMatchingStrategy methodIncStrategy,
      ExternalCandidatesMatchingStrategy<ASTCDAssociation> associationIncStrategy,
      OOSymbolsIncMapping ooSymbolsIncMapping) {
    this.concreteCD = concreteCD;
    this.typeIncStrategy = typeIncStrategy;
    this.mcTypeIncStrategy = mcTypeIncStrategy;
    this.attributeIncStrategy = attributeIncStrategy;
    this.methodIncStrategy = methodIncStrategy;
    this.associationIncStrategy = associationIncStrategy;
    this.ooSymbolsIncMapping = ooSymbolsIncMapping;
  }
  
  @Override
  public IOOSymbolsIncMapping asOOSymbolsIncMapping() {
    return ooSymbolsIncMapping;
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
    return ooSymbolsIncMapping.getScopedMapping(scope).getIncarnations(referenceType.getSymbol())
        .stream().map(SymbolUtil::cdTypeFromTypeSymbol).collect(Collectors.toSet());
  }
  
  @Override
  public Set<ASTCDType> getIncarnations(ISymbol contextSymbol, ASTCDType referenceType) {
    return ooSymbolsIncMapping.getScopedMapping(contextSymbol).getIncarnations(referenceType
        .getSymbol()).stream().map(SymbolUtil::cdTypeFromTypeSymbol).collect(Collectors.toSet());
  }
  
  @Override
  public boolean isIncarnation(ISymbol contextSymbol, ASTCDType conType, ASTCDType refType) {
    // TODO can be optimized once LocalIncMapping supports isIncarnation checks directly
    return getIncarnations(contextSymbol, refType).stream().anyMatch(type -> type == conType);
  }
  
  @Override
  public boolean isIncarnation(ASTCDType conType, ASTCDType refType) {
    return typeIncStrategy.isMatched(conType, refType);
  }
  
  @Override
  public Set<TypeSymbol> getBindings(ISymbol contextSymbol, TypeSymbol referenceType) {
    return ooSymbolsIncMapping.getScopedBindings(contextSymbol).getBinding(referenceType).map(
        Binding::getConcreteElements).orElse(Collections.emptySet());
  }
  
  @Override
  public Set<TypeSymbol> getBindings(IScope concreteScope, TypeSymbol referenceType) {
    return ooSymbolsIncMapping.getScopedBindings(concreteScope).getBinding(referenceType).map(
        Binding::getConcreteElements).orElse(Collections.emptySet());
  }
  
  @Override
  public void addBinding(String contextSymbolName, FieldSymbol referenceField,
      FieldSymbol concreteField) {
    try {
      ooSymbolsIncMapping.getLocalOnlyBindings(contextSymbolName).addFieldBinding(Binding
          .createStrict(referenceField, concreteField));
    }
    catch (BindingConflictException e) {
      throw new RuntimeException("Binding exception", e);
    }
  }
  
  @Override
  public void addBinding(ISymbol contextSymbol, FieldSymbol referenceField,
      FieldSymbol concreteField) {
    try {
      ooSymbolsIncMapping.getScopedBindings(contextSymbol).addFieldBinding(Binding.createStrict(
          referenceField, concreteField));
    }
    catch (BindingConflictException e) {
      throw new RuntimeException("Binding exception", e);
    }
  }
  
  @Override
  public Set<ASTCDAttribute> getReferenceElements(ASTCDAttribute concreteAttribute) {
    ASTCDType concreteType = (ASTCDType) concreteAttribute.getSymbol().getEnclosingScope()
        .getAstNode();
    Set<ASTCDAttribute> refElements = new HashSet<>();
    for (ASTCDType declaringRefType : getReferenceElements(concreteType)) {
      refElements.addAll(getReferenceElements(concreteAttribute, declaringRefType));
    }
    return refElements;
  }
  
  @Override
  public Set<ASTCDAttribute> getReferenceElements(ASTCDAttribute concreteAttribute,
      ASTCDType declaringRefType) {
    attributeIncStrategy.setReferenceType(declaringRefType);
    return new HashSet<>(attributeIncStrategy.getMatchedElements(concreteAttribute));
  }
  
  @Override
  public Set<ASTCDAttribute> getIncarnations(ASTCDAttribute referenceAttribute) {
    ASTCDType declaringType = (ASTCDType) referenceAttribute.getSymbol().getEnclosingScope()
        .getAstNode();
    return getIncarnations(declaringType).stream().flatMap((
        cAttributeDeclaringType) -> cAttributeDeclaringType.getCDAttributeList().stream().filter(
            attributeIncarnation -> isIncarnation(attributeIncarnation, referenceAttribute)))
        .collect(Collectors.toSet());
  }
  
  @Override
  public Set<ASTCDAttribute> getIncarnations(IScope scope, ASTCDAttribute referenceAttribute) {
    return ooSymbolsIncMapping.getScopedMapping(scope).getIncarnations(referenceAttribute
        .getSymbol()).stream().map(SymbolUtil::cdAttributeFromFieldSymbol).collect(Collectors
            .toSet());
  }
  
  public Set<ASTCDAttribute> getIncarnations(ISymbol contextSymbol,
      ASTCDAttribute referenceAttribute) {
    return ooSymbolsIncMapping.getScopedMapping(contextSymbol).getIncarnations(referenceAttribute
        .getSymbol()).stream().map(SymbolUtil::cdAttributeFromFieldSymbol).collect(Collectors
            .toSet());
  }
  
  @Override
  public boolean isIncarnation(ISymbol contextSymbol, ASTCDAttribute conAttribute,
      ASTCDAttribute refAttribute) {
    // TODO can be optimized once LocalIncMapping supports isIncarnation checks directly
    return getIncarnations(contextSymbol, refAttribute).contains(conAttribute);
  }
  
  @Override
  public boolean isIncarnation(ASTCDAttribute conAttribute, ASTCDAttribute refAttribute) {
    return attributeIncStrategy.isMatched(conAttribute, refAttribute);
  }
  
  @Override
  public Set<FieldSymbol> getBindings(ISymbol contextSymbol, FieldSymbol referenceField) {
    return ooSymbolsIncMapping.getScopedBindings(contextSymbol).getBinding(referenceField).map(
        Binding::getConcreteElements).orElse(Collections.emptySet());
  }
  
  @Override
  public Set<FieldSymbol> getBindings(IScope concreteScope, FieldSymbol referenceField) {
    return ooSymbolsIncMapping.getScopedBindings(concreteScope).getBinding(referenceField).map(
        Binding::getConcreteElements).orElse(Collections.emptySet());
  }
  
  @Override
  public void addBinding(ISymbol contextSymbol, MethodSymbol referenceMethod,
      MethodSymbol concreteMethod) {
    try {
      ooSymbolsIncMapping.getScopedBindings(contextSymbol).addMethodBinding(Binding.createStrict(
          referenceMethod, concreteMethod));
    }
    catch (BindingConflictException e) {
      throw new RuntimeException("Binding exception", e);
    }
  }
  
  @Override
  public Set<ASTCDMethod> getReferenceElements(ASTCDMethod concreteMethod) {
    ASTCDType concreteType = (ASTCDType) concreteMethod.getSymbol().getEnclosingScope()
        .getAstNode();
    Set<ASTCDMethod> refElements = new HashSet<>();
    for (ASTCDType declaringRefType : getReferenceElements(concreteType)) {
      refElements.addAll(getReferenceElements(concreteMethod, declaringRefType));
    }
    return refElements;
  }
  
  @Override
  public Set<ASTCDMethod> getReferenceElements(ASTCDMethod concreteMethod,
      ASTCDType declaringRefType) {
    methodIncStrategy.setReferenceType(declaringRefType);
    return new HashSet<>(methodIncStrategy.getMatchedElements(concreteMethod));
  }
  
  @Override
  public Set<ASTCDMethod> getIncarnations(ASTCDMethod referenceMethod) {
    ASTCDType declaringType = (ASTCDType) referenceMethod.getSymbol().getEnclosingScope()
        .getAstNode();
    return getIncarnations(declaringType).stream().flatMap(cMethodDeclaringType -> {
      methodIncStrategy.setReferenceType(declaringType);
      return cMethodDeclaringType.getCDMethodList().stream().filter(
          methodIncarnation -> methodIncStrategy.isMatched(methodIncarnation, referenceMethod));
    }).collect(Collectors.toSet());
  }
  
  @Override
  public Set<ASTCDMethod> getIncarnations(IScope scope, ASTCDMethod referenceMethod) {
    return ooSymbolsIncMapping.getScopedMapping(scope).getIncarnations(referenceMethod.getSymbol())
        .stream().map(SymbolUtil::cdMethodFromMethodSymbol).collect(Collectors.toSet());
  }
  
  public Set<ASTCDMethod> getIncarnations(ISymbol contextSymbol, ASTCDMethod referenceMethod) {
    return ooSymbolsIncMapping.getScopedMapping(contextSymbol).getIncarnations(referenceMethod
        .getSymbol()).stream().map(SymbolUtil::cdMethodFromMethodSymbol).collect(Collectors
            .toSet());
  }
  
  @Override
  public boolean isIncarnation(ISymbol contextSymbol, ASTCDMethod conMethod,
      ASTCDMethod refMethod) {
    // TODO can be optimized once LocalIncMapping supports isIncarnation checks directly
    return getIncarnations(contextSymbol, refMethod).contains(conMethod);
  }
  
  @Override
  public boolean isIncarnation(ASTCDMethod conMethod, ASTCDMethod refMethod) {
    return methodIncStrategy.isMatched(conMethod, refMethod);
  }
  
  @Override
  public Set<MethodSymbol> getBindings(ISymbol contextSymbol, MethodSymbol referenceMethod) {
    return ooSymbolsIncMapping.getScopedBindings(contextSymbol).getBinding(referenceMethod).map(
        Binding::getConcreteElements).orElse(Collections.emptySet());
  }
  
  @Override
  public Set<MethodSymbol> getBindings(IScope scope, MethodSymbol referenceMethod) {
    return ooSymbolsIncMapping.getScopedBindings(scope).getBinding(referenceMethod).map(
        Binding::getConcreteElements).orElse(Collections.emptySet());
  }
  
  @Override
  public void addBinding(ISymbol contextSymbol, CDAssociationSymbol refAssociation,
      Set<CDAssociationSymbol> conAssociations) {
    // TODO implement association support
    throw new NotImplementedException();
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
  public Set<ASTCDAssociation> getIncarnations(IScope scope, ASTCDAssociation refAssociation) {
    // TODO implement association support
    throw new NotImplementedException();
  }
  
  @Override
  public boolean isIncarnation(ISymbol contextSymbol, ASTCDAssociation conAssociation,
      ASTCDAssociation refAssociation) {
    // TODO implement association support
    throw new NotImplementedException();
  }
  
  @Override
  public boolean isIncarnation(ASTCDAssociation conAssociation, ASTCDAssociation refAssociation) {
    return associationIncStrategy.isMatched(conAssociation, refAssociation);
  }
  
  @Override
  public String computeSymbolKey(ISymbol symbol) {
    return ooSymbolsIncMapping.computeSymbolKey(symbol);
  }
  
  @Override
  public void addBinding(String contextSymbolName, TypeSymbol referenceType,
      TypeSymbol concreteType) {
    try {
      ooSymbolsIncMapping.getLocalOnlyBindings(contextSymbolName).addTypeBinding(Binding
          .createStrict(referenceType, concreteType));
    }
    catch (BindingConflictException e) {
      throw new RuntimeException("Binding exception", e);
    }
  }
  
  @Override
  public void addBinding(ISymbol contextSymbol, TypeSymbol referenceType, TypeSymbol concreteType) {
    try {
      ooSymbolsIncMapping.getScopedBindings(contextSymbol).addTypeBinding(Binding.createStrict(
          referenceType, concreteType));
    }
    catch (BindingConflictException e) {
      throw new RuntimeException("Binding exception", e);
    }
  }
  
  @Override
  public boolean isIncarnation(ASTMCType conType, ASTMCType refType) {
    return mcTypeIncStrategy.isMatched(conType, refType, this::isIncarnation);
  }
  
  @Override
  public boolean isIncarnation(ISymbol contextSymbol, ASTMCType conType, ASTMCType refType) {
    return mcTypeIncStrategy.isMatched(conType, refType, (conCDType, refCDType) -> isIncarnation(
        contextSymbol, conCDType, refCDType));
  }
  
}
