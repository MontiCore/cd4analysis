/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconcretization.type.method;

import de.monticore.cd.facade.MCQualifiedNameFacade;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdconcretization.CDRefSymbolHandlerDelegator;
import de.monticore.cdconcretization.CompletionException;
import de.monticore.cdconcretization.stereotype.StereotypeUtil;
import de.monticore.cdconcretization.type.TypeCompletionContext;
import de.monticore.cdconcretization.util.MethodSignatureString;
import de.monticore.cdconcretization.util.NameUtil;
import de.monticore.cdconcretization.util.SymbolUtil;
import de.monticore.cdconformance.CDConfParameter;
import de.monticore.cdconformance.inc.MethodOverloadingCDIncBindings;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.se_rwth.commons.Names;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ForEachMethodCompleter extends AbstractMethodInTypeCompleter {
  
  @Override
  public void completeMethodInType(ASTCDType concreteType, ASTCDMethod referenceMethod,
      TypeCompletionContext context) throws CompletionException {
    Optional<String> stereotypeValue = StereotypeUtil.getForEachStereotypeValue(referenceMethod
        .getModifier(), "Stereotype value must not be empty for stereotype 'forEach'");
    if (stereotypeValue.isPresent()) {
      CDRefSymbolHandlerDelegator symbolHandler = new CDRefSymbolHandlerDelegator();
      symbolHandler.setAttributeHandler(paramAttr -> completeMethodUsingAttribute(referenceMethod,
          paramAttr, context));
      symbolHandler.setTypeHandler(paramType -> completeMethodUsingType(referenceMethod, paramType,
          context));
      symbolHandler.resolveSymbol(context.getReferenceType().getSpannedScope(), stereotypeValue
          .get(), referenceMethod.get_SourcePositionStart());
      // each handler will call super.completeTypeForAttribute() if necessary
    }
    else {
      super.completeMethodInType(concreteType, referenceMethod, context);
    }
  }
  
  private void completeMethodUsingAttribute(ASTCDMethod referenceMethod,
      ASTCDAttribute paramAttribute, TypeCompletionContext context) throws CompletionException {
    // group attribute incarnations by their declaring type
    Map<CDTypeSymbol, List<ASTCDAttribute>> attributesByDeclaringType = context
        .getAttributeIncarnations(paramAttribute).stream().collect(Collectors.groupingBy(
            attr -> (CDTypeSymbol) attr.getEnclosingScope().getSpanningSymbol()));
    
    for (Map.Entry<CDTypeSymbol, List<ASTCDAttribute>> entry : attributesByDeclaringType
        .entrySet()) {
      CDTypeSymbol paramIncarnationDeclaringType = entry.getKey();
      List<ASTCDAttribute> paramAttributeIncarnations = entry.getValue();
      
      // if we have more than one declaring type incarnation, we need to add a suffix to the new
      // method name
      String declaringTypeNameWithoutCDQualifier = SymbolUtil.getFullNameWithoutCD(
          paramIncarnationDeclaringType);
      String declaringTypeSuffix = attributesByDeclaringType.size() > 1 ? "_" + NameUtil
          .escapeQualifiedNameAsIdentifier(declaringTypeNameWithoutCDQualifier) : "";
      
      for (ASTCDAttribute paramAttributeInc : paramAttributeIncarnations) {
        // now we have a specific incarnation of the parameter attribute in the concrete CD.
        // we can now construct a new method based on this incarnation for the concrete type
        ASTCDMethod newMethod = cloneWithOriginalTypes(referenceMethod);
        
        // 1. decide name of the new method
        Optional<String> adaptedName = NameUtil.adaptTemplatedName(referenceMethod.getName(),
            paramAttribute.getName(), paramAttributeInc.getName());
        if (context.isForEachNameAdaptationEnabled() && adaptedName.isPresent()) {
          newMethod.setName(adaptedName.get() + declaringTypeSuffix);
        }
        else {
          // Default: add the param incarnation name as suffix
          // if we have more than one declaring type incarnation, we need to add a suffix to the
          // new method
          String attributeSuffix = paramAttributeIncarnations.size() > 1 ? "_" + paramAttributeInc
              .getName() : "";
          newMethod.setName(referenceMethod.getName() + declaringTypeSuffix + attributeSuffix);
        }
        
        // 2. decide return type of the new method
        if (referenceMethod.getMCReturnType().isPresentMCType() && referenceMethod.getMCReturnType()
            .getMCType().deepEquals(paramAttribute.getMCType())) {
          // Convention: If the param attribute type matches the reference method return type
          // -> Use the attribute incarnation type as return type
          newMethod.setMCReturnType(CD4CodeMill.mCReturnTypeBuilder().setMCType(paramAttributeInc
              .getMCType()).build());
        }
        // ELSE: Default: keep the return type of the reference method resp.
        
        // 3. adapt parameters
        for (int i = 0; i < newMethod.getCDParameterList().size(); i++) {
          ASTCDParameter newParameter = newMethod.getCDParameter(i);
          // 3.1 parameter name
          Optional<String> adaptedParameterName = NameUtil.adaptTemplatedName(newParameter
              .getName(), paramAttribute.getName(), paramAttributeInc.getName());
          if (context.isForEachNameAdaptationEnabled() && adaptedParameterName.isPresent()) {
            newParameter.setName(adaptedParameterName.get());
          }
          // ELSE: parameter name stays as is! only needs to be unique in scope of the method
          
          // 3.2 parameter type
          if (newParameter.getMCType().deepEquals(paramAttribute.getMCType())) {
            // Convention: If the param attribute type matches the reference method parameter type
            // -> Use the attribute incarnation type as parameter type
            newParameter.setMCType(paramAttributeInc.getMCType());
          }
          // ELSE: Default: keep the parameter type of the reference attribute resp.
        }
        
        // 4. remove forEach stereotype from concrete method but add a reference to the
        // original method
        StereotypeUtil.removeForEachStereotype(newMethod.getModifier());
        StereotypeUtil.addStereotype(newMethod.getModifier(), context.getMappingName(),
            MethodSignatureString.printSignatureIfOverloaded(referenceMethod.getSymbol()));
        
        String newMethodQualifier = paramIncarnationDeclaringType.getFullName();
        String bindingContextKey;
        if (context.getConformanceParams().contains(CDConfParameter.METHOD_OVERLOADING)) {
          bindingContextKey = MethodOverloadingCDIncBindings.computeMethodSymbolKey(
              newMethodQualifier, newMethod);
        }
        else {
          bindingContextKey = Names.getQualifiedName(newMethodQualifier, newMethod.getName());
        }
        context.getIncarnationMapping().addBinding(bindingContextKey, paramAttribute.getSymbol(),
            paramAttributeInc.getSymbol());
        // TODO Should we also add a TYPE binding for the type of the parameter attribute?
        // alternative: is the ScopedIncarnationBindings class so "intelligent" that it looks up
        // type bindings by checking field symbol types, method return & parameter types? -> sounds
        // messy!
        // --> first think about how we would use this information -> only to build an advanced
        // incarnation mapping data structure, e.g., as input to the OCL adaptation tool
        
        // 5. pass the new method to the next completer
        super.completeMethodInType(context.getConcreteType(), newMethod, context);
      }
    }
  }
  
  private void completeMethodUsingType(ASTCDMethod referenceMethod, ASTCDType paramType,
      TypeCompletionContext context) throws CompletionException {
    
    Set<ASTCDType> paramTypeIncarnations = context.getTypeIncarnations(paramType);
    for (ASTCDType paramTypeInc : paramTypeIncarnations) {
      // now we have a specific incarnation of the parameter type in the concrete CD.
      // we can now construct a new method based on this incarnation for the concrete type
      ASTCDMethod newMethod = cloneWithOriginalTypes(referenceMethod);
      
      // 1. decide return type of the new method
      if (referenceMethod.getMCReturnType().isPresentMCType() && referenceMethod.getMCReturnType()
          .getMCType().getDefiningSymbol().get().getFullName().equals(paramType.getSymbol()
              .getFullName())) {
        // Convention: If the param attribute type matches the reference method return type
        // -> Use the attribute incarnation type as return type
        newMethod.setMCReturnType(CD4CodeMill.mCReturnTypeBuilder().setMCType(
            createQualifiedTypeInScope(context.getConcreteType().getSpannedScope(), paramTypeInc
                .getSymbol().getInternalQualifiedName())).build());
      }
      // ELSE: Default: keep the return type of the reference method resp.
      
      boolean parameterSignatureAdapted = false;
      // 2. adapt parameters
      for (int i = 0; i < referenceMethod.getCDParameterList().size(); i++) {
        ASTCDParameter referenceParameter = referenceMethod.getCDParameter(i);
        ASTCDParameter newParameter = newMethod.getCDParameter(i);
        // 2.1 parameter name
        Optional<String> adaptedParameterName = NameUtil.adaptTemplatedName(referenceParameter
            .getName(), paramType.getName(), paramTypeInc.getName());
        if (context.isForEachNameAdaptationEnabled() && adaptedParameterName.isPresent()) {
          newParameter.setName(adaptedParameterName.get());
        }
        // ELSE: parameter name stays as is! only needs to be unique in scope of the method
        
        // 2.2 parameter type
        if (referenceParameter.getMCType().getDefiningSymbol().get().getFullName().equals(paramType
            .getSymbol().getFullName())) {
          // TODO naming is weird when we talk about parameters and a parameter element!
          // Convention: If the param type matches the reference method parameter type
          // -> Use the type incarnation as parameter type
          newParameter.setMCType(createQualifiedTypeInScope(context.getConcreteType()
              .getEnclosingScope(), paramTypeInc.getSymbol().getInternalQualifiedName()));
          parameterSignatureAdapted = true;
        }
        // ELSE: Default: keep the parameter type of the reference method resp.
      }
      
      // 3. decide name of the new method
      Optional<String> adaptedName = NameUtil.adaptTemplatedName(referenceMethod.getName(),
          paramType.getName(), paramTypeInc.getName());
      if (context.isForEachNameAdaptationEnabled() && adaptedName.isPresent()) {
        newMethod.setName(adaptedName.get());
      }
      else if (!parameterSignatureAdapted || !context.getConformanceParams().contains(
          CDConfParameter.METHOD_OVERLOADING)) {
        // Default: add the param incarnation name as suffix
        // If method overloading is enabled, we only have to add a suffix if we have not changed
        // the parameter signature. If method overloading is not allowed, we always add a suffix
        String typeSuffix = paramTypeIncarnations.size() > 1 ? "_" + paramTypeInc.getName() : "";
        newMethod.setName(referenceMethod.getName() + typeSuffix);
      }
      
      // 4. remove forEach stereotype from concrete method but add a reference to the
      // original method
      StereotypeUtil.removeForEachStereotype(newMethod.getModifier());
      StereotypeUtil.addStereotype(newMethod.getModifier(), context.getMappingName(),
          MethodSignatureString.printSignatureIfOverloaded(referenceMethod.getSymbol()));
      
      // 5. pass the new method to the next completer
      super.completeMethodInType(context.getConcreteType(), newMethod, context);
    }
  }
  
  /**
   * Clones the given method and sets the original {@link
   * de.monticore.types.mcbasictypes._ast.ASTMCType} instances of the parameters and return type.
   *
   * @param original the original method
   * @return a clone of the original method with the original type objects
   */
  private static ASTCDMethod cloneWithOriginalTypes(ASTCDMethod original) {
    // NOTE: We have to reuse the MCType instances form the original method.
    // The 'deepClone()' method does not copy symbol table information and therefore the type
    // check will not work on the cloned MCType instances.
    ASTCDMethod clone = original.deepClone();
    // a type is only available if the method has not the return type 'void'
    if (clone.getMCReturnType().isPresentMCType()) {
      clone.getMCReturnType().setMCType(original.getMCReturnType().getMCType());
    }
    for (int i = 0; i < clone.getCDParameterList().size(); i++) {
      ASTCDParameter cloneParameter = clone.getCDParameter(i);
      ASTCDParameter originalParameter = original.getCDParameter(i);
      cloneParameter.setMCType(originalParameter.getMCType());
    }
    return clone;
  }
  
  private static ASTMCQualifiedType createQualifiedTypeInScope(IBasicSymbolsScope scope,
      String name) {
    ASTMCQualifiedName mcQualifiedName = MCQualifiedNameFacade.createQualifiedName(name);
    /*
     * We have to set the enclosing scope so the type can be resolved if the type check is
     * used on the cloned method.
     */
    mcQualifiedName.setEnclosingScope(scope);
    ASTMCQualifiedType qualifiedType = CD4CodeMill.mCQualifiedTypeBuilder().setMCQualifiedName(
        mcQualifiedName).build();
    qualifiedType.setEnclosingScope(scope);
    return qualifiedType;
  }
  
}
