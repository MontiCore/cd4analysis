/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconcretization.type.method;

import com.google.common.collect.Lists;
import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._symboltable.ICDBasisScope;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbolTOP;
import de.monticore.cdconcretization.ConcretizationHelper;
import de.monticore.cdconcretization.stereotype.StereotypeUtil;
import de.monticore.cdconformance.CDConfParameter;
import de.monticore.cdconcretization.type.TypeCompletionContext;
import de.monticore.cdconcretization.util.MethodSignatureString;
import de.monticore.cdconcretization.util.NameUtil;
import de.monticore.symbols.basicsymbols._ast.ASTType;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbolTOP;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.*;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;

import java.util.*;
import java.util.stream.Collectors;

/** Completes a concrete type by adding the given reference method if it is not already present. */
public class BaseMethodInTypeCompleter extends AbstractMethodInTypeCompleter {
  
  @Override
  public void completeMethodInType(ASTCDType concreteType, ASTCDMethod referenceMethod,
      TypeCompletionContext context) {
    List<ASTCDMethod> allConcreteAttributesInHierarchy = CDSymbolTables.getMethodsInHierarchy(
        concreteType);
    List<ASTCDMethod> incarnations = allConcreteAttributesInHierarchy.stream().filter(
        cMethod -> context.getIncarnationMapping().isIncarnation(cMethod, referenceMethod))
        .toList();
    if (incarnations.isEmpty()) {
      createMethodIncarnations(concreteType, referenceMethod, context);
    }
  }
  
  /**
   * Creates incarnations of the given reference method in the given concrete type. If the method
   * return type or any parameter type is a CD type with multiple incarnations, a new method is
   * created for each combination of incarnations.
   *
   * @param concreteType the concrete type to add the method to
   * @param referenceMethod the reference method to clone
   * @param context the completion context
   */
  private void createMethodIncarnations(ASTCDType concreteType, ASTCDMethod referenceMethod,
      TypeCompletionContext context) {
    List<ASTMCReturnType> returnTypeIncarnations;
    if (referenceMethod.getMCReturnType().isPresentMCType()) {
      returnTypeIncarnations = findTypeIncarnations(referenceMethod.getMCReturnType().getMCType(),
          context).stream().map(type -> CD4CodeMill.mCReturnTypeBuilder().setMCType(type).build())
          .collect(Collectors.toList());
    }
    else {
      // otherwise it is a void type, so we just copy the void type
      returnTypeIncarnations = Collections.singletonList(referenceMethod.getMCReturnType()
          .deepClone());
    }
    
    List<List<ASTMCType>> parameterTypeIncarnations = referenceMethod.getCDParameterList().stream()
        .map(parameter -> findTypeIncarnations(parameter.getMCType(), context)).collect(Collectors
            .toList());
    
    addMethodIncarnations(concreteType, referenceMethod, context, returnTypeIncarnations,
        parameterTypeIncarnations, context);
  }
  
  // TODO This is generic functionality not limited to methods. It should be extracted and reused
  // for attributes and associations as well.
  /**
   * Finds all incarnations of the given reference method in the given concrete type. If the type is
   * not a CD type, or there is no incarnation, the type is returned as is.
   *
   * @param refMCType the reference type to find incarnations for
   * @param context the completion context
   * @return a list of all incarnations of the given reference type
   */
  private List<ASTMCType> findTypeIncarnations(ASTMCType refMCType, TypeCompletionContext context) {
    SymTypeExpression symTypeExpr = TypeCheck3.symTypeFromAST(refMCType);
    
    // make sure we do not add the 'any' type to the concrete CD
    if (symTypeExpr.getTypeInfo().getFullName().equals(context
        .getUnderspecifiedPlaceholderTypeName())) {
      Log.warn("Underspecified placeholder type not allowed in method without incarnations",
          refMCType.get_SourcePositionStart());
      return Collections.singletonList(refMCType);
    }
    
    Optional<ASTType> rAttributeTypeOpt = symTypeExpr.hasTypeInfo() && symTypeExpr.getTypeInfo()
        .isPresentAstNode() ? Optional.ofNullable(symTypeExpr.getTypeInfo().getAstNode()) : Optional
            .empty();
    if (rAttributeTypeOpt.isEmpty() || !(rAttributeTypeOpt.get() instanceof ASTCDType)) {
      if (refMCType instanceof ASTMCCollectionTypesNode) {
        // if it is a collection type, we can find the incarnations of the item type
        return findMCCollectionTypeIncarnations((ASTMCCollectionTypesNode) refMCType, context);
      }
      /*
       * If it is neither a CD type nor an MCCollection type, we cannot have incarnations and just
       * use the type as is!
       */
      return Collections.singletonList(refMCType);
    }
    
    ASTCDType rAttributeType = (ASTCDType) rAttributeTypeOpt.get();
    Set<ASTCDType> typeIncarnations = context.getTypeIncarnations(rAttributeType);
    if (typeIncarnations.isEmpty()) {
      // if we do not have any incarnations, we can just use the type as is
      return Collections.singletonList(refMCType);
    }
    else {
      List<ASTMCType> typeIncarnationsList = new ArrayList<>();
      for (ASTCDType typeIncarnation : typeIncarnations) {
        typeIncarnationsList.add(ConcretizationHelper.createQualifiedTypeInScope(context
            .getConcreteType().getSpannedScope(), typeIncarnation.getSymbol()
                .getInternalQualifiedName()));
      }
      return typeIncarnationsList;
    }
  }
  
  /**
   * Finds all incarnations of the given reference MCCollection type in the given context.
   * This checks for incarnations of the item/key/value type argument of the different collection
   * types and creates multiple instances if the item/key/value type has multiple incarnations.
   *
   * @param refMCType the reference MCCollection type to find incarnations for
   * @param context the completion context to use for finding the incarnations
   * @return
   */
  protected List<ASTMCType> findMCCollectionTypeIncarnations(ASTMCCollectionTypesNode refMCType,
      TypeCompletionContext context) {
    /*
     * TODO What we should actually do here is reuse the reference adaptation framework for
     *  MCTypes so we can adapt every nested type etc.
     *  -> in general we could reuse all the adaptation logic to realize the for each and multi
     *  incarnation handling! -> think about it: if a type has multiple incarnations and we
     *  therefore create multiple incarnations of a method this is in fact reference model adaptation!
     *  -> only that we not apply it to the whole artifact but a single method.
     *  workaround for now: hardcoded behavior for MCCollection types
     */
    if (refMCType instanceof ASTMCListType) {
      ASTMCType refItemType = ((ASTMCListType) refMCType).getMCTypeArgument().getMCTypeOpt().get();
      List<ASTMCType> itemTypeIncs = findTypeIncarnations(refItemType, context);
      return itemTypeIncs.stream().map(typeIncarnation -> CD4CodeMill.mCListTypeBuilder()
          .setMCTypeArgument(createTypeArgument(typeIncarnation)).build()).collect(Collectors
              .toList());
    }
    else if (refMCType instanceof ASTMCSetType) {
      ASTMCType refItemType = ((ASTMCSetType) refMCType).getMCTypeArgument().getMCTypeOpt().get();
      List<ASTMCType> itemTypeIncs = findTypeIncarnations(refItemType, context);
      return itemTypeIncs.stream().map(typeIncarnation -> CD4CodeMill.mCSetTypeBuilder()
          .setMCTypeArgument(createTypeArgument(typeIncarnation)).build()).collect(Collectors
              .toList());
    }
    else if (refMCType instanceof ASTMCOptionalType) {
      ASTMCType refItemType = ((ASTMCOptionalType) refMCType).getMCTypeArgument().getMCTypeOpt()
          .get();
      List<ASTMCType> itemTypeIncs = findTypeIncarnations(refItemType, context);
      return itemTypeIncs.stream().map(typeIncarnation -> CD4CodeMill.mCOptionalTypeBuilder()
          .setMCTypeArgument(createTypeArgument(typeIncarnation)).build()).collect(Collectors
              .toList());
    }
    else if (refMCType instanceof ASTMCMapType) {
      ASTMCType keyType = ((ASTMCMapType) refMCType).getKey().getMCTypeOpt().get();
      ASTMCType valueType = ((ASTMCMapType) refMCType).getValue().getMCTypeOpt().get();
      List<ASTMCTypeArgument> keyTypeArgIncs = findTypeIncarnations(keyType, context).stream().map(
          this::createTypeArgument).collect(Collectors.toList());
      List<ASTMCTypeArgument> valueTypeArgIncs = findTypeIncarnations(valueType, context).stream()
          .map(this::createTypeArgument).collect(Collectors.toList());
      /*
       * TODO We must only combine the incarnations of the key and value type if they are not
       *  conflicting or imply any conflicting bindings!
       *  -> we would have the same issues, e.g. when choosing method parameter incarnations
       *  -> again, this is something we already solved in the reference adaptation framework
       *  -> so we should reuse it here as well!
       */
      return Lists.cartesianProduct(keyTypeArgIncs, valueTypeArgIncs).stream().map(
          argPair -> CD4CodeMill.mCMapTypeBuilder().setKey(argPair.get(0)).setValue(argPair.get(1))
              .build()).collect(Collectors.toList());
    }
    throw new UnsupportedOperationException("Unsupported MCCollectionTypes type: " + refMCType
        .getClass().getName());
  }
  
  private String adaptMethodNameFromTypePairs(String name, ASTCDMethod referenceMethod,
      ASTMCReturnType returnTypeIncarnation, List<ASTMCType> parameterCombination,
      TypeCompletionContext context) {
    String result = name;
    if (referenceMethod.getMCReturnType().isPresentMCType() && returnTypeIncarnation
        .isPresentMCType()) {
      Optional<ASTCDType> refRetType = resolveReferenceCDType(referenceMethod.getMCReturnType()
          .getMCType(), context);
      Optional<ASTCDType> conRetType = resolveConcreteCDType(returnTypeIncarnation.getMCType(),
          context);
      if (refRetType.isPresent() && conRetType.isPresent()) {
        result = NameUtil.adaptTemplatedName(result, refRetType.get().getName(), conRetType.get()
            .getName()).orElse(result);
      }
    }
    for (int i = 0; i < referenceMethod.getCDParameterList().size(); i++) {
      Optional<ASTCDType> refParamType = resolveReferenceCDType(referenceMethod.getCDParameterList()
          .get(i).getMCType(), context);
      Optional<ASTCDType> conParamType = resolveConcreteCDType(parameterCombination.get(i),
          context);
      if (refParamType.isPresent() && conParamType.isPresent()) {
        result = NameUtil.adaptTemplatedName(result, refParamType.get().getName(), conParamType
            .get().getName()).orElse(result);
      }
    }
    return result;
  }
  
  private Optional<ASTCDType> resolveReferenceCDType(ASTMCType mcType,
      TypeCompletionContext context) {
    return resolveCDType(mcType, context.getReferenceCD().getEnclosingScope());
  }
  
  private Optional<ASTCDType> resolveConcreteCDType(ASTMCType mcType,
      TypeCompletionContext context) {
    return resolveCDType(mcType, context.getConcreteCD().getEnclosingScope());
  }
  
  private Optional<ASTCDType> resolveCDType(ASTMCType mcType, ICDBasisScope scope) {
    if (!(mcType instanceof ASTMCQualifiedType)) {
      return Optional.empty();
    }
    String typeName = ((ASTMCQualifiedType) mcType).getMCQualifiedName().getQName();
    return scope.resolveCDTypeDown(typeName).filter(TypeSymbolTOP::isPresentAstNode).map(
        CDTypeSymbolTOP::getAstNode);
  }
  
  protected ASTMCTypeArgument createTypeArgument(ASTMCType mcType) {
    if (mcType instanceof ASTMCQualifiedType) {
      return CD4CodeMill.mCBasicTypeArgumentBuilder().setMCQualifiedType(
          (ASTMCQualifiedType) mcType).build();
    }
    else if ((mcType instanceof ASTMCPrimitiveType)) {
      return CD4CodeMill.mCPrimitiveTypeArgumentBuilder().setMCPrimitiveType(
          (ASTMCPrimitiveType) mcType).build();
    }
    else {
      throw new UnsupportedOperationException("Unsupported type argument type: " + mcType.getClass()
          .getName());
    }
  }
  
  /**
   * Adds one method for each return type and combination of the cartesian product of parameter
   * types.
   *
   * @param concreteType the concrete type to add the method to
   * @param referenceMethod the reference method to clone
   * @param context the completion context
   * @param returnTypeIncarnations the incarnations of the return type
   * @param parameterTypeIncarnations the incarnations of each parameter type
   * @param typeCompletionContext
   */
  private void addMethodIncarnations(ASTCDType concreteType, ASTCDMethod referenceMethod,
      TypeCompletionContext context, List<ASTMCReturnType> returnTypeIncarnations,
      List<List<ASTMCType>> parameterTypeIncarnations,
      TypeCompletionContext typeCompletionContext) {
    
    List<List<ASTMCType>> parameterCombinations = Lists.cartesianProduct(parameterTypeIncarnations);
    
    for (ASTMCReturnType returnTypeIncarnation : returnTypeIncarnations) {
      for (List<ASTMCType> parameterCombination : parameterCombinations) {
        ASTCDMethod methodClone = referenceMethod.deepClone();
        
        // 1. decide name of method
        // Apply implicit name adaptation first (before suffix), using the specific type pairs
        // of the return type and parameters for this incarnation combination.
        String methodName = referenceMethod.getName();
        if (context.isImplicitNameAdaptationEnabled()) {
          methodName = adaptMethodNameFromTypePairs(methodName, referenceMethod,
              returnTypeIncarnation, parameterCombination, context);
          if (!methodName.equals(referenceMethod.getName()) && !context.getConformanceParams()
              .contains(CDConfParameter.ADAPTED_NAME_MAPPING)) {
            StereotypeUtil.addStereotype(methodClone.getModifier(), context.getMappingName(),
                MethodSignatureString.printSignatureIfOverloaded(referenceMethod.getSymbol()));
          }
        }
        if (returnTypeIncarnations.size() > 1) {
          // if we have more than one return type incarnation, we need to add a suffix to the new
          // methods name
          // TODO not necessarily! If we change the parameter signature at the same time, we can
          // keep the original method name!
          //  -> see how we did it in ForEachMethodCompleter
          methodName = methodName + "_" + NameUtil.escapeQualifiedNameAsIdentifier(
              returnTypeIncarnation.printType());
        }
        methodClone.setName(methodName);
        
        // 2. set return type of the incarnation
        // use FQ name to avoid messing with imports / name conflicts
        methodClone.setMCReturnType(returnTypeIncarnation);
        
        // 3. set parameter types of the incarnation
        for (int i = 0; i < methodClone.getCDParameterList().size(); i++) {
          ASTCDParameter parameterClone = methodClone.getCDParameterList().get(i);
          parameterClone.setMCType(parameterCombination.get(i));
          if (context.isImplicitNameAdaptationEnabled()) {
            Optional<ASTCDType> refParamType = resolveReferenceCDType(referenceMethod
                .getCDParameterList().get(i).getMCType(), context);
            Optional<ASTCDType> conParamType = resolveConcreteCDType(parameterCombination.get(i),
                context);
            if (refParamType.isPresent() && conParamType.isPresent()) {
              NameUtil.adaptTemplatedName(parameterClone.getName(), refParamType.get().getName(),
                  conParamType.get().getName()).ifPresent(parameterClone::setName);
            }
          }
          methodClone.setCDParameter(i, parameterClone);
        }
        
        if (returnTypeIncarnations.size() > 1 || parameterCombinations.size() > 1) {
          // only add the stereotype if we have multiple incarnations
          StereotypeUtil.addStereotype(methodClone.getModifier(), context.getMappingName(),
              MethodSignatureString.printSignatureIfOverloaded(referenceMethod.getSymbol()));
        }
        
        concreteType.addCDMember(methodClone);
      }
    }
  }
  
}
