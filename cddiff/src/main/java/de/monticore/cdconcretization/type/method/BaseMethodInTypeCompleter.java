/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconcretization.type.method;

import com.google.common.collect.Lists;
import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cd.facade.MCQualifiedNameFacade;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code.typescalculator.FullSynthesizeFromCD4Code;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconcretization.ConcretizationHelper;
import de.monticore.cdconcretization.stereotype.StereotypeUtil;
import de.monticore.cdconcretization.type.TypeCompletionContext;
import de.monticore.cdconcretization.util.MethodSignatureString;
import de.monticore.cdconcretization.util.NameUtil;
import de.monticore.symbols.basicsymbols._ast.ASTType;
import de.monticore.types.check.ISynthesize;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import java.util.*;
import java.util.stream.Collectors;

/** Completes a concrete type by adding the given reference method if it is not already present. */
public class BaseMethodInTypeCompleter extends AbstractMethodInTypeCompleter {
  
  private final ISynthesize typeCalculator = new FullSynthesizeFromCD4Code();
  
  @Override
  public void completeMethodInType(ASTCDType concreteType, ASTCDMethod referenceMethod,
      TypeCompletionContext context) {
    List<ASTCDMethod> allConcreteAttributesInHierarchy = CDSymbolTables.getMethodsInHierarchy(
        concreteType);
    List<ASTCDMethod> incarnations = allConcreteAttributesInHierarchy.stream().filter(
        cMethod -> context.getIncarnationMapping().isIncarnation(cMethod, referenceMethod)).collect(
            Collectors.toList());
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
        parameterTypeIncarnations);
  }
  
  /**
   * Finds all incarnations of the given reference method in the given concrete type. If the type is
   * not a CD type, or there is no incarnation, the type is returned as is.
   *
   * @param refMCType the reference type to find incarnations for
   * @param context the completion context
   * @return a list of all incarnations of the given reference type
   */
  private List<ASTMCType> findTypeIncarnations(ASTMCType refMCType, TypeCompletionContext context) {
    SymTypeExpression symTypeExpr = typeCalculator.synthesizeType(refMCType).getResult();
    
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
      // if it is not a CD type, we cannot have incarnations and just use the type as is!
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
        typeIncarnationsList.add(ConcretizationHelper.createQualifiedTypeInScope(
                context.getConcreteType().getSpannedScope(), typeIncarnation.getSymbol()
                .getInternalQualifiedName()));
      }
      return typeIncarnationsList;
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
   */
  private void addMethodIncarnations(ASTCDType concreteType, ASTCDMethod referenceMethod,
      TypeCompletionContext context, List<ASTMCReturnType> returnTypeIncarnations,
      List<List<ASTMCType>> parameterTypeIncarnations) {
    
    List<List<ASTMCType>> parameterCombinations = Lists.cartesianProduct(parameterTypeIncarnations);
    
    for (ASTMCReturnType returnTypeIncarnation : returnTypeIncarnations) {
      for (List<ASTMCType> parameterCombination : parameterCombinations) {
        ASTCDMethod methodClone = referenceMethod.deepClone();
        
        // 1. decide name of method
        if (returnTypeIncarnations.size() > 1) {
          // if we have more than one return type incarnation, we need to add a suffix to the new
          // methods name
          // TODO not necessarily! If we change the parameter signature at the same time, we can
          // keep the original method name!
          //  -> see how we did it in ForEachMethodCompleter
          methodClone.setName(referenceMethod.getName() + "_" + NameUtil
              .escapeQualifiedNameAsIdentifier(returnTypeIncarnation.printType()));
        }
        
        // 2. set return type of the incarnation
        // use FQ name to avoid messing with imports / name conflicts
        methodClone.setMCReturnType(returnTypeIncarnation);
        
        // 3. set parameter types of the incarnation
        for (int i = 0; i < methodClone.getCDParameterList().size(); i++) {
          ASTCDParameter parameterClone = methodClone.getCDParameterList().get(i);
          parameterClone.setMCType(parameterCombination.get(i));
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
