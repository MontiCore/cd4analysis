package de.monticore.cdconcretization.type.attribute;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdconcretization.CompletionException;
import de.monticore.cdconcretization.stereotype.StereotypeUtil;
import de.monticore.cdconcretization.type.TypeCompletionContext;
import de.monticore.cdconcretization.util.NameUtil;
import de.monticore.cdconcretization.util.SymbolUtil;
import de.monticore.symbols.oosymbols._ast.ASTField;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.se_rwth.commons.Names;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Processes reference attributes that are annotated with the stereotype 'forEach'. The value of the
 * stereotype is expected to be a reference to another model element. The reference element (target)
 * is resolved and for each incarnation of the target element one attribute incarnation is created
 * parameterized by the target incarnation.<br>
 * Currently supported target references are:
 *
 * <ul>
 *   <li>attributes (e.g., 'Foo.attr')
 * </ul>
 */
public class ForEachTypeAttributeCompleter extends AbstractTypeAttributeCompleter {

  // TODO for testing only. Move to global ConcretizationContext of it stays
  private static final boolean ADD_BINDING_STEREOTYPE_TO_ATTRIBUTES = false;

  @Override
  public void completeTypeForAttribute(
      ASTCDType concreteType, ASTCDAttribute referenceAttribute, TypeCompletionContext context)
      throws CompletionException {
    Optional<String> stereotypeValue =
        StereotypeUtil.getForEachStereotypeValue(
            referenceAttribute.getModifier(),
            "Stereotype value must not be empty for stereotype 'forEach. '"
                + referenceAttribute.get_SourcePositionStart());
    if (stereotypeValue.isPresent()) {
      boolean processed =
          processAsAttributeReference(referenceAttribute, context, stereotypeValue.get());
      // TODO Support other references than attributes (e.g., types, methods)
      if (!processed) {
        throw new CompletionException(
            "Unsupported forEach reference expression" + stereotypeValue.get());
      }
    } else {
      super.completeTypeForAttribute(concreteType, referenceAttribute, context);
    }
  }

  /**
   * Tries to process the given reference as an attribute reference, e.g. 'Foo.attr'.
   *
   * @param context
   * @param referenceExpr
   * @return true if the reference was processed, false otherwise
   * @throws CompletionException
   */
  private boolean processAsAttributeReference(
      ASTCDAttribute referenceAttribute, TypeCompletionContext context, String referenceExpr)
      throws CompletionException {
    Optional<FieldSymbol> fieldSymbol =
        context.getReferenceCD().getEnclosingScope().resolveField(referenceExpr);
    if (fieldSymbol.isPresent()) {
      ASTField field = fieldSymbol.get().getAstNode();
      // is field an attribute?
      if (field instanceof ASTCDAttribute) {
        ASTCDAttribute rTargetAttribute = (ASTCDAttribute) field;
        completeAttributeUsingAttribute(referenceAttribute, rTargetAttribute, context);
        return true;
      } else {
        throw new CompletionException(
            "Referenced field symbol "
                + referenceExpr
                + " is not a CDAttribute! (type: "
                + field.getClass().getName()
                + ")");
      }
    }
    return false;
  }

  /**
   * Creates a new attribute for each incarnation of the target attribute. If the declaring type of
   * the target attribute has multiple incarnations, the new attributes will additionally be
   * parameterized by the declaring type incarnation.<br>
   * <br>
   * Rules for the new attribute incarnations:
   *
   * <ul>
   *   <li>If the reference and target attribute names match, the attribute incarnation has the name
   *       of the target incarnation
   *   <li>Otherwise, each attribute incarnation has the name of the reference attribute with a
   *       suffix of the target attribute name
   *   <li>If the reference and target attribute types match, the attribute incarnation has the type
   *       of the target attribute
   *   <li>Otherwise, each attribute incarnation has the type of the reference attribute
   * </ul>
   *
   * @param referenceAttribute the original reference attribute
   * @param rTargetAttribute the target of the expression used in the forEach stereotype. The
   *     attribute by which this construction is parameterized.
   * @param context the completion context
   */
  private void completeAttributeUsingAttribute(
      ASTCDAttribute referenceAttribute,
      ASTCDAttribute rTargetAttribute, // TODO maybe name paramAttribute instead of 'target' ?
      TypeCompletionContext context)
      throws CompletionException {

    // group attribute incarnations by their declaring type
    Map<CDTypeSymbol, List<ASTCDAttribute>> attributesByDeclaringType =
            context.getAttributeIncarnations(rTargetAttribute).stream()
            .collect(
                Collectors.groupingBy(
                    attr -> (CDTypeSymbol) attr.getEnclosingScope().getSpanningSymbol()));

    // now, we can get all the attribute incarnations for each declaring type incarnation
    // if there is no incarnation of the declaring type, we do not need to create any new attributes
    for (Map.Entry<CDTypeSymbol, List<ASTCDAttribute>> entry :
        attributesByDeclaringType.entrySet()) {
      CDTypeSymbol cAttributeDeclaringType = entry.getKey();
      List<ASTCDAttribute> attributeIncarnations = entry.getValue();

      // if we have more than one declaring type incarnation, we need to add a suffix to the new
      // attributes
      String declaringTypeNameWithoutCDQualifier = SymbolUtil.getFullNameWithoutCD(cAttributeDeclaringType);
      String declaringTypeSuffix =
              attributesByDeclaringType.size() > 1
              ? "_" + NameUtil.escapeQualifiedNameAsIdentifier(declaringTypeNameWithoutCDQualifier)
              : "";

      for (ASTCDAttribute cAttribute : attributeIncarnations) {
        // now we have a specific incarnation of the reference attribute in the concrete CD.
        // we can now construct a new attribute based on this incarnation for the concrete type

        // if we have more than one attribute type incarnation, we need to add a suffix to the
        // new attributes
        String attributeSuffix = attributeIncarnations.size() > 1 ? "_" + cAttribute.getName() : "";

        ASTCDAttribute newAttribute = referenceAttribute.deepClone();

        // 1. decide name of the new attribute
        if (referenceAttribute.getName().equals(rTargetAttribute.getName())) {
          // Convention: If the REFERENCED attribute name matches the reference attribute name
          // -> Use the REFERENCED name without a suffix (but still a type suffix)
          newAttribute.setName(cAttribute.getName() + declaringTypeSuffix);
        } else {
          // Default: add the REFERENCED attribute incarnation name as suffix
          newAttribute.setName(
                  referenceAttribute.getName() + declaringTypeSuffix + attributeSuffix);
        }

        // 2. decide type of the new attribute
        if (referenceAttribute.getMCType().deepEquals(rTargetAttribute.getMCType())) {
          // Convention: If the REFERENCED attribute type matches the reference attribute type
          // -> Use the REFERENCED type for every incarnation
          newAttribute.setMCType(cAttribute.getMCType());
        } else {
          // Default: keep the type of the reference attribute resp.
          /*
           * NOTE: The base completer down the line might resolve the type of the reference
           * attribute to multiple incarnations and therefore add even more incarnations of the
           * attribute.
           */
          newAttribute.setMCType(referenceAttribute.getMCType());
        }

        // 3. remove forEach stereotype from concrete attribute but add a reference to the
        // original attribute
        StereotypeUtil.removeForEachStereotype(newAttribute.getModifier());
        // TODO Once we allow a single concrete element to be an incarnation of multiple
        // reference elements, we need to merge them here
        StereotypeUtil.addStereotype(
            newAttribute.getModifier(),
            context.getMappingName(),
            referenceAttribute.getSymbol().getFullName());

        if (ADD_BINDING_STEREOTYPE_TO_ATTRIBUTES) {
          StereotypeUtil.addIncarnationBindingStereotype(
                  newAttribute.getModifier(),
                  rTargetAttribute.getSymbol().getFullName(),
                  SymbolUtil.getFullNameWithoutCD(cAttribute.getSymbol()));
        }

        String newAttrQualifier = cAttributeDeclaringType.getFullName();
        String newAttrFullName = Names.getQualifiedName(newAttrQualifier, newAttribute.getName());
        context.getScopedIncarnationBindings().addFieldBinding(
            newAttrFullName,
            rTargetAttribute.getSymbol(),
            Set.of(cAttribute.getSymbol()));

        // 4. pass the new attribute to the next completer
        super.completeTypeForAttribute(context.getConcreteType(), newAttribute, context);
      }
    }
  }
}
