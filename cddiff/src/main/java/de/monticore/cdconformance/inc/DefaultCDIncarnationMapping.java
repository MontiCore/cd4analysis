/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconformance.inc;

import com.google.common.collect.SetMultimap;
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
import org.apache.commons.lang3.NotImplementedException;

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
  
  @Override
  public boolean isIncarnation(ISymbol contextSymbol, ASTCDType conType, ASTCDType refType) {
    Set<TypeSymbol> typeBindings = bindings.getBindings(contextSymbol, refType.getSymbol());
    // 1. check for scoped incarnation bindings
    if (!typeBindings.isEmpty()) {
      // map symbols back to AST nodes
      return typeBindings.stream().map(SymbolUtil::cdTypeFromTypeSymbol).anyMatch(c -> c.equals(
          conType));
    }
    else {
      // 2. use usual incarnation strategies
      return isIncarnation(conType, refType);
    }
  }
  
  @Override
  public boolean isIncarnation(ASTCDType conType, ASTCDType refType) {
    return typeIncStrategy.isMatched(conType, refType);
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
  public void addBinding(String contextSymbolName, TypeSymbol referenceType,
      Set<TypeSymbol> concreteTypes) {
    bindings.addBinding(contextSymbolName, referenceType, concreteTypes);
  }
  
  @Override
  public Set<TypeSymbol> getBindings(ISymbol contextSymbol, TypeSymbol referenceType) {
    return bindings.getBindings(contextSymbol, referenceType);
  }
  
  @Override
  public Set<TypeSymbol> getBindings(IScope concreteScope, TypeSymbol referenceType) {
    return bindings.getBindings(concreteScope, referenceType);
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
      
      return getIncarnations(scope, attributeDeclaringType).stream().flatMap(
          cAttributeDeclaringType -> {
            attributeIncStrategy.setReferenceType(attributeDeclaringType);
            return cAttributeDeclaringType.getCDAttributeList().stream().filter(
                attributeIncarnation -> attributeIncStrategy.isMatched(attributeIncarnation,
                    referenceAttribute));
          }).collect(Collectors.toSet());
    }
  }
  
  @Override
  public boolean isIncarnation(ISymbol contextSymbol, ASTCDAttribute conAttribute,
      ASTCDAttribute refAttribute) {
    Set<FieldSymbol> fieldBindings = bindings.getBindings(contextSymbol, refAttribute.getSymbol());
    // 1. check for scoped incarnation bindings
    if (!fieldBindings.isEmpty()) {
      // map symbols back to AST nodes
      return fieldBindings.stream().map(SymbolUtil::cdAttributeFromFieldSymbol).anyMatch(c -> c
          .equals(conAttribute));
    }
    else {
      // 2. use usual incarnation strategies
      return isIncarnation(conAttribute, refAttribute);
    }
  }
  
  @Override
  public boolean isIncarnation(ASTCDAttribute conAttribute, ASTCDAttribute refAttribute) {
    return attributeIncStrategy.isMatched(conAttribute, refAttribute);
  }
  
  @Override
  public void addBinding(String contextSymbolName, FieldSymbol referenceField,
      Set<FieldSymbol> concreteFields) {
    bindings.addBinding(contextSymbolName, referenceField, concreteFields);
  }
  
  @Override
  public Set<FieldSymbol> getBindings(ISymbol contextSymbol, FieldSymbol referenceField) {
    return bindings.getBindings(contextSymbol, referenceField);
  }
  
  @Override
  public Set<FieldSymbol> getBindings(IScope concreteScope, FieldSymbol referenceField) {
    return bindings.getBindings(concreteScope, referenceField);
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
    Set<MethodSymbol> methodIncarnationsOpt = bindings.getBindings(scope, referenceMethod
        .getSymbol());
    if (!methodIncarnationsOpt.isEmpty()) {
      // map symbols back to AST nodes
      return methodIncarnationsOpt.stream().map(SymbolUtil::cdMethodFromMethodSymbol).collect(
          Collectors.toSet());
    }
    else {
      // 2. Find all incarnations using the usual incarnation strategies
      ASTCDType methodDeclaringType = (ASTCDType) referenceMethod.getSymbol().getEnclosingScope()
          .getAstNode();
      
      // TODO What about the "deep" case where methods are matched in supertypes?
      
      return getIncarnations(scope, methodDeclaringType).stream().flatMap(cMethodDeclaringType -> {
        methodIncStrategy.setReferenceType(methodDeclaringType);
        return cMethodDeclaringType.getCDMethodList().stream().filter(
            methodIncarnation -> methodIncStrategy.isMatched(methodIncarnation, referenceMethod));
      }).collect(Collectors.toSet());
    }
  }
  
  @Override
  public boolean isIncarnation(ISymbol contextSymbol, ASTCDMethod conMethod,
      ASTCDMethod refMethod) {
    Set<MethodSymbol> methodBindings = bindings.getBindings(contextSymbol, refMethod.getSymbol());
    // 1. check for scoped incarnation bindings
    if (!methodBindings.isEmpty()) {
      // map symbols back to AST nodes
      return methodBindings.stream().map(SymbolUtil::cdMethodFromMethodSymbol).anyMatch(c -> c
          .equals(conMethod));
    }
    else {
      // 2. use usual incarnation strategies
      return isIncarnation(conMethod, refMethod);
    }
  }
  
  @Override
  public boolean isIncarnation(ASTCDMethod conMethod, ASTCDMethod refMethod) {
    return methodIncStrategy.isMatched(conMethod, refMethod);
  }
  
  @Override
  public void addBinding(String contextSymbolName, MethodSymbol referenceMethod,
      Set<MethodSymbol> concreteMethods) {
    bindings.addBinding(contextSymbolName, referenceMethod, concreteMethods);
  }
  
  @Override
  public Set<MethodSymbol> getBindings(ISymbol contextSymbol, MethodSymbol referenceMethod) {
    return bindings.getBindings(contextSymbol, referenceMethod);
  }
  
  @Override
  public Set<MethodSymbol> getBindings(IScope scope, MethodSymbol referenceMethod) {
    return bindings.getBindings(scope, referenceMethod);
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
  public void addBinding(String contextSymbolName, CDAssociationSymbol refAssociation,
      Set<CDAssociationSymbol> conAssociations) {
    bindings.addBinding(contextSymbolName, refAssociation, conAssociations);
  }
  
  @Override
  public String computeSymbolKey(ISymbol symbol) {
    return bindings.computeSymbolKey(symbol);
  }
  
  @Override
  public boolean isIncarnation(ASTMCType conType, ASTMCType refType) {
    mcTypeIncStrategy.setCDTypeMatcher(this::isIncarnation);
    return mcTypeIncStrategy.isMatched(conType, refType);
  }
  
  @Override
  public boolean isIncarnation(ISymbol contextSymbol, ASTMCType conType, ASTMCType refType) {
    mcTypeIncStrategy.setCDTypeMatcher((conCDType, refCDType) -> isIncarnation(contextSymbol,
        conCDType, refCDType));
    return mcTypeIncStrategy.isMatched(conType, refType);
  }
  
  @Override
  public SetMultimap<String, TypeSymbol> getTypeBindings(IScope concreteScope) {
    return bindings.getTypeBindings(concreteScope);
  }
  
  @Override
  public SetMultimap<String, TypeSymbol> getTypeBindings(ISymbol contextSymbol) {
    return bindings.getTypeBindings(contextSymbol);
  }
  
  @Override
  public SetMultimap<String, FieldSymbol> getFieldBindings(IScope concreteScope) {
    return bindings.getFieldBindings(concreteScope);
  }
  
  @Override
  public SetMultimap<String, FieldSymbol> getFieldBindings(ISymbol contextSymbol) {
    return bindings.getFieldBindings(contextSymbol);
  }
  
  @Override
  public SetMultimap<String, MethodSymbol> getMethodBindings(IScope concreteScope) {
    return bindings.getMethodBindings(concreteScope);
  }
  
  @Override
  public SetMultimap<String, MethodSymbol> getMethodBindings(ISymbol contextSymbol) {
    return bindings.getMethodBindings(contextSymbol);
  }
  
}
