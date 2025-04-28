package de.monticore.cdconcretization.type.attribute;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdconcretization.CDRefSymbolHandlerDelegator;
import de.monticore.cdconcretization.CompletionException;
import de.monticore.cdconcretization.stereotype.StereotypeUtil;
import de.monticore.cdconcretization.type.TypeCompletionContext;
import de.monticore.cdconcretization.util.NameUtil;
import de.monticore.cdconcretization.util.SymbolUtil;
import de.se_rwth.commons.Names;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Processes reference attributes that are annotated with the stereotype 'forEach'. The value of the
 * stereotype is expected to be a reference to another model element (hook element). For each
 * incarnation of this hook element, one attribute incarnation is created parameterized by the hook
 * element incarnation.<br>
 * <br>
 * Currently supported hook elements are:
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
      CDRefSymbolHandlerDelegator symbolHandler = new CDRefSymbolHandlerDelegator();
      symbolHandler.setAttributeHandler(
          hookAttribute -> completeAttributeUsingAttribute(referenceAttribute, hookAttribute, context));
      // TODO Add support for other hook elements
      symbolHandler.resolveSymbol(
          context.getReferenceCD().getEnclosingScope(),
          stereotypeValue.get(),
          referenceAttribute.get_SourcePositionStart());
      // each handler will call super.completeTypeForAttribute() if necessary
    } else {
      super.completeTypeForAttribute(concreteType, referenceAttribute, context);
    }
  }

  /**
   * Creates a new attribute for each incarnation of the hook attribute. If the declaring type of
   * the hook attribute has multiple incarnations, the new attributes will additionally be
   * parameterized by the declaring type incarnation.<br>
   * <br>
   * Rules for the new attribute incarnations:
   *
   * <ul>
   *   <li>If the reference and hook attribute names match, the attribute incarnation has the name
   *       of the hook incarnation
   *   <li>Otherwise, each attribute incarnation has the name of the reference attribute with a
   *       suffix of the hook attribute name
   *   <li>If the reference and hook attribute types match, the attribute incarnation has the type
   *       of the hook attribute
   *   <li>Otherwise, each attribute incarnation has the type of the reference attribute
   * </ul>
   *
   * @param referenceAttribute the original reference attribute
   * @param hookAttribute the attribute referenced in the forEach stereotype. The
   *     attribute by which this construction is parameterized.
   * @param context the completion context
   */
  private void completeAttributeUsingAttribute(
      ASTCDAttribute referenceAttribute,
      ASTCDAttribute hookAttribute,
      TypeCompletionContext context)
      throws CompletionException {

    // group attribute incarnations by their declaring type
    Map<CDTypeSymbol, List<ASTCDAttribute>> attributesByDeclaringType =
            context.getAttributeIncarnations(hookAttribute).stream()
            .collect(
                Collectors.groupingBy(
                    attr -> (CDTypeSymbol) attr.getEnclosingScope().getSpanningSymbol()));

    // now, we can get all the attribute incarnations for each declaring type incarnation
    // if there is no incarnation of the declaring type, we do not need to create any new attributes
    for (Map.Entry<CDTypeSymbol, List<ASTCDAttribute>> entry :
        attributesByDeclaringType.entrySet()) {
      CDTypeSymbol hookIncarnationDeclaringType = entry.getKey();
      List<ASTCDAttribute> hookIncarnations = entry.getValue();

      // if we have more than one declaring type incarnation, we need to add a suffix to the new
      // attributes
      String declaringTypeNameWithoutCDQualifier = SymbolUtil.getFullNameWithoutCD(hookIncarnationDeclaringType);
      String declaringTypeSuffix =
              attributesByDeclaringType.size() > 1
              ? "_" + NameUtil.escapeQualifiedNameAsIdentifier(declaringTypeNameWithoutCDQualifier)
              : "";

      for (ASTCDAttribute hookIncarnation : hookIncarnations) {
        // now we have a specific incarnation of the hook attribute in the concrete CD.
        // we can now construct a new attribute based on this incarnation for the concrete type

        // if we have more than one declaring type incarnation, we need to add a suffix to the
        // new attributes
        String attributeSuffix = hookIncarnations.size() > 1 ? "_" + hookIncarnation.getName() : "";

        ASTCDAttribute newAttribute = referenceAttribute.deepClone();

        // 1. decide name of the new attribute
        if (referenceAttribute.getName().equals(hookAttribute.getName())) {
          // Convention: If the hook attribute name matches the reference attribute name
          // -> Use the hook incarnation name without a suffix (but still a type suffix)
          newAttribute.setName(hookIncarnation.getName() + declaringTypeSuffix);
        } else {
          // Default: add the hook incarnation name as suffix
          newAttribute.setName(
                  referenceAttribute.getName() + declaringTypeSuffix + attributeSuffix);
        }

        // 2. decide type of the new attribute
        if (referenceAttribute.getMCType().deepEquals(hookAttribute.getMCType())) {
          // Convention: If the hook attribute type matches the reference attribute type
          // -> Use the hook incarnation type for every incarnation
          newAttribute.setMCType(hookIncarnation.getMCType());
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
                  hookAttribute.getSymbol().getFullName(),
                  SymbolUtil.getFullNameWithoutCD(hookIncarnation.getSymbol()));
        }

        String newAttrQualifier = hookIncarnationDeclaringType.getFullName();
        String newAttrFullName = Names.getQualifiedName(newAttrQualifier, newAttribute.getName());
        context.getScopedIncarnationBindings().addFieldBinding(
            newAttrFullName,
            hookAttribute.getSymbol(),
            Set.of(hookIncarnation.getSymbol()));

        // 4. pass the new attribute to the next completer
        super.completeTypeForAttribute(context.getConcreteType(), newAttribute, context);
      }
    }
  }
}
