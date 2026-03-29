/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconcretization.type.attribute;

import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cd.facade.MCQualifiedNameFacade;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code.typescalculator.FullSynthesizeFromCD4Code;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconcretization.CompletionException;
import de.monticore.cdconcretization.stereotype.StereotypeUtil;
import de.monticore.cdconformance.CDConfParameter;
import de.monticore.cdconcretization.type.TypeCompletionContext;
import de.monticore.cdconcretization.util.NameUtil;
import de.monticore.symbols.basicsymbols._ast.ASTType;
import de.monticore.types.check.SymTypeExpression;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Completes a concrete type by adding the given reference attribute if it is not already present.
 */
public class BaseAttributeInTypeCompleter extends AbstractAttributeInTypeCompleter {
  
  @Override
  public void completeAttributeInType(ASTCDType concreteType, ASTCDAttribute referenceAttribute,
      TypeCompletionContext context) throws CompletionException {
    // 1. check if the concrete type already has a matching attribute (also in superclasses)
    List<ASTCDAttribute> allConcreteAttributesInHierarchy = CDSymbolTables.getAttributesInHierarchy(
        concreteType);
    List<ASTCDAttribute> incarnations = allConcreteAttributesInHierarchy.stream().filter(
        cAttribute -> context.getIncarnationMapping().isIncarnation(cAttribute, referenceAttribute))
        .collect(Collectors.toList());
    if (incarnations.isEmpty()) {
      createAttributeIncarnations(concreteType, referenceAttribute, context);
    }
    else {
      // TODO should we check if the type is correct? Or should we let the final conformance check
      // fail?
      // TODO if we check the type: we mus consider incarnations of the reference type!
    }
  }
  
  /**
   * Creates incarnations of the given reference attribute in the given concrete type. If the
   * attribute type is a CD type with multiple incarnations, a new attribute is created for each
   * incarnation.
   *
   * @param concreteType
   * @param referenceAttribute
   * @param context
   * @throws CompletionException
   */
  private void createAttributeIncarnations(ASTCDType concreteType,
      ASTCDAttribute referenceAttribute, TypeCompletionContext context) throws CompletionException {
    SymTypeExpression attributeSymType = new FullSynthesizeFromCD4Code().synthesizeType(
        referenceAttribute.getMCType()).getResult();
    
    // make sure we do not add the 'any' type to the concrete CD
    if (attributeSymType.getTypeInfo().getFullName().equals(context
        .getUnderspecifiedPlaceholderTypeName())) {
      throw new CompletionException(
          "Underspecified placeholder type without incarnations found in attribute '"
              + referenceAttribute.getName() + "' in reference type '" + context.getReferenceType()
                  .getName() + "'");
    }
    
    Optional<ASTType> rAttributeTypeOpt = attributeSymType.hasTypeInfo() && attributeSymType
        .getTypeInfo().isPresentAstNode() ? Optional.ofNullable(attributeSymType.getTypeInfo()
            .getAstNode()) : Optional.empty();
    if (rAttributeTypeOpt.isEmpty() || !(rAttributeTypeOpt.get() instanceof ASTCDType)) {
      // if it is not a CD type, we cannot have incarnations and just use the type as is!
      ASTCDAttribute clone = referenceAttribute.deepClone();
      concreteType.addCDMember(clone);
      return;
    }
    
    ASTCDType rAttributeType = (ASTCDType) rAttributeTypeOpt.get();
    Set<ASTCDType> typeIncarnations = context.getTypeIncarnations(rAttributeType);
    
    for (ASTCDType cAttributeType : typeIncarnations) {
      ASTCDAttribute attributeIncarnation = referenceAttribute.deepClone();
      
      // 1. decide name of incarnation
      if (typeIncarnations.size() > 1) {
        // if we have more than one type incarnation, we need to add a suffix to the new attributes
        attributeIncarnation.setName(referenceAttribute.getName() + "_" + cAttributeType.getName());
        // since name changed we also need to add a stereotype mapping
        StereotypeUtil.addStereotype(attributeIncarnation.getModifier(), context.getMappingName(),
            referenceAttribute.getSymbol().getFullName());
      }
      if (context.isImplicitNameAdaptationEnabled()) {
        NameUtil.adaptTemplatedName(attributeIncarnation.getName(),
            rAttributeType.getName(), cAttributeType.getName()).ifPresent(adapted -> {
              attributeIncarnation.setName(adapted);
              if (!context.getConformanceParams().contains(CDConfParameter.ADAPTED_NAME_MAPPING)) {
                StereotypeUtil.addStereotype(attributeIncarnation.getModifier(),
                    context.getMappingName(), referenceAttribute.getSymbol().getFullName());
              }
            });
      }

      // 2. set type of incarnation
      // use FQ name to avoid messing with imports / name conflicts
      attributeIncarnation.setMCType(CD4CodeMill.mCQualifiedTypeBuilder().setMCQualifiedName(
          MCQualifiedNameFacade.createQualifiedName(cAttributeType.getSymbol()
              .getInternalQualifiedName())).build());
      
      concreteType.addCDMember(attributeIncarnation);
    }
  }
  
}
