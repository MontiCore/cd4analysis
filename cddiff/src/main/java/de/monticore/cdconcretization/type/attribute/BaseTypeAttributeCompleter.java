package de.monticore.cdconcretization.type.attribute;

import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cd.facade.MCQualifiedNameFacade;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code.typescalculator.FullSynthesizeFromCD4Code;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconcretization.CompletionException;
import de.monticore.cdconcretization.ConcretizationHelper;
import de.monticore.cdconcretization.type.TypeCompletionContext;
import de.monticore.symbols.basicsymbols._ast.ASTType;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.umlstereotype._ast.ASTStereotype;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Completes a concrete type by adding the given reference attribute if it is not already present.
 */
public class BaseTypeAttributeCompleter extends AbstractTypeAttributeCompleter {

  @Override
  public void completeTypeForAttribute(
      ASTCDType concreteType, ASTCDAttribute referenceAttribute, TypeCompletionContext context)
      throws CompletionException {
    // 1. check if the concrete type already has a matching attribute (also in superclasses)
    List<ASTCDAttribute> allConcreteAttributesInHierarchy =
        CDSymbolTables.getAttributesInHierarchy(concreteType);
    List<ASTCDAttribute> incarnations =
        allConcreteAttributesInHierarchy.stream()
            .filter(
                cAttribute ->
                    context.getAttributeIncStrategy().isMatched(cAttribute, referenceAttribute))
            .collect(Collectors.toList());
    if (incarnations.isEmpty()) {
      createAttributeIncarnations(concreteType, referenceAttribute, context);
    } else {
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
  private void createAttributeIncarnations(
      ASTCDType concreteType, ASTCDAttribute referenceAttribute, TypeCompletionContext context)
      throws CompletionException {
    // find incarnations of attribute type and create one attribute incarnation for each type

    // TODO TypeCheck3 not initialized error -> Can we even use TypeCheck3 in CD4A?
    // SymTypeExpression attributeSymType =
    // TypeCheck3.symTypeFromAST(referenceAttribute.getMCType());

    // TODO if we cannot use TypeCheck3 not, how do we correctly use the old type check?
    SymTypeExpression attributeSymType =
        new FullSynthesizeFromCD4Code().synthesizeType(referenceAttribute.getMCType()).getResult();

    Optional<ASTType> rAttributeTypeOpt =
        attributeSymType.hasTypeInfo() && attributeSymType.getTypeInfo().isPresentAstNode()
            ? Optional.ofNullable(attributeSymType.getTypeInfo().getAstNode())
            : Optional.empty();
    if (rAttributeTypeOpt.isEmpty() || !(rAttributeTypeOpt.get() instanceof ASTCDType)) {
      // if it is not a CD type, we cannot have incarnations and just use the type as is!
      ASTCDAttribute clone = referenceAttribute.deepClone();
      concreteType.addCDMember(clone);
      return;
    }

    ASTCDType rAttributeType = (ASTCDType) rAttributeTypeOpt.get();
    Set<ASTCDType> typeIncarnations =
        ConcretizationHelper.getCDTypes(context.getConcreteCD()).stream()
            .filter(type -> context.getTypeIncStrategy().isMatched(type, rAttributeType))
            .collect(Collectors.toSet());

    for (ASTCDType cAttributeType : typeIncarnations) {
      ASTCDAttribute attributeIncarnation = referenceAttribute.deepClone();

      // 1. decide name of incarnation
      if (typeIncarnations.size() > 1) {
        // if we have more than one type incarnation, we need to add a suffix to the new attributes
        attributeIncarnation.setName(referenceAttribute.getName() + "_" + cAttributeType.getName());
      }

      // 2. set type of incarnation
      // use FQ name to avoid messing with imports / name conflicts
      attributeIncarnation.setMCType(
          CD4CodeMill.mCQualifiedTypeBuilder()
              .setMCQualifiedName(
                  MCQualifiedNameFacade.createQualifiedName(
                      cAttributeType.getSymbol().getFullName()))
              .build());

      ASTStereotype stereotype;
      if (attributeIncarnation.getModifier().isPresentStereotype()) {
        stereotype = attributeIncarnation.getModifier().getStereotype();
      } else {
        stereotype = CD4CodeMill.stereotypeBuilder().build();
        attributeIncarnation.getModifier().setStereotype(stereotype);
      }
      stereotype.addValues(
          CD4CodeMill.stereoValueBuilder()
              .setName(context.getMappingName())
              .setContent(
                  referenceAttribute
                      .getSymbol()
                      .getFullName()) // TODO maybe cut off the CD name from FQName?
              .build());

      concreteType.addCDMember(attributeIncarnation);
    }
  }
}
