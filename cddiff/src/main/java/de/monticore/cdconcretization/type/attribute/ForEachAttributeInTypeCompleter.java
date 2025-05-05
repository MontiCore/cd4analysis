package de.monticore.cdconcretization.type.attribute;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconcretization.CompletionException;
import de.monticore.cdconcretization.ConcretizationHelper;
import de.monticore.cdconcretization.type.TypeCompletionContext;
import de.monticore.symbols.oosymbols._ast.ASTField;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.umlstereotype._ast.ASTStereotype;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Processes reference attributes that are annotated with the stereotype 'forEach'. The value of the
 * stereotype is expected to be a reference to another model element. The reference element (target)
 * is resolved and for each incarnation of the target element one attribute incarnation is created
 * parameterized by the target incarnation.<br>
 * Currently supported target references are:
 * <ul>
 *   <li>attributes (e.g., 'Foo.attr')
 * </ul>
 */
public class ForEachAttributeInTypeCompleter extends AbstractAttributeInTypeCompleter {

  private static final String FOR_EACH_STEREOTYPE = "forEach";

  @Override
  public void completeAttributeInType(
      ASTCDType concreteType, ASTCDAttribute referenceAttribute, TypeCompletionContext context)
      throws CompletionException {
    Optional<String> stereotypeValue = getStereotypeValue(referenceAttribute);
    if (stereotypeValue.isPresent()) {
      boolean processed =
          processAsAttributeReference(referenceAttribute, context, stereotypeValue.get());
      // TODO Support other references than attributes (e.g., types, methods)
      if (!processed) {
        throw new CompletionException(
            "Unsupported forEach reference expression" + stereotypeValue.get());
      }
    } else {
      super.completeAttributeInType(concreteType, referenceAttribute, context);
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
        ASTCDType rTargetAttributeDeclaringType =
                (ASTCDType) fieldSymbol.get().getEnclosingScope().getAstNode();
        completeAttributeUsingAttribute(referenceAttribute, rTargetAttribute, rTargetAttributeDeclaringType, context);
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
   * Creates a new attribute for each incarnation of the target attribute. If the declaring type
   * of the target attribute has multiple incarnations, the new attributes will additionally be
   * parameterized by the declaring type incarnation.<br>
   * <br>
   * Rules for the new attribute incarnations:
   * <ul>
   *   <li>If the reference and target attribute names match, the attribute incarnation has the name of the target incarnation</li>
   *   <li>Otherwise, each attribute incarnation has the name of the reference attribute with a suffix of the target attribute name</li>
   *   <li>If the reference and target attribute types match, the attribute incarnation has the type of the target attribute</li>
   *   <li>Otherwise, each attribute incarnation has the type of the reference attribute</li>
   * </ul>
   *
   * @param referenceAttribute the original reference attribute
   * @param rTargetAttribute the target of the expression used in the forEach stereotype. The attribute by which this construction is parameterized.
   * @param rTargetAttributeDeclaringType the type in which the target attribute is declared
   * @param context the completion context
   */
  private void completeAttributeUsingAttribute(
      ASTCDAttribute referenceAttribute,
      ASTCDAttribute rTargetAttribute, // TODO maybe name paramAttribute instead of 'target' ?
      ASTCDType rTargetAttributeDeclaringType,
      TypeCompletionContext context)
      throws CompletionException {

    Set<ASTCDType> declaringTypeIncarnations =
            ConcretizationHelper.getCDTypes(context.getConcreteCD()).stream()
                    .filter(type -> context.getTypeIncStrategy().isMatched(type, rTargetAttributeDeclaringType))
                    .collect(Collectors.toSet());
    System.out.println(
            "Found type icnarnations for "
                    + rTargetAttributeDeclaringType.getName()
                    + ": "
                    + declaringTypeIncarnations);
    // TODO we likely need another abstraction for this. The type itself could also be annotated
    // with a forEach which already produced mutliple incarnations?
    // do we really want to execute the matching strategies again and again?
    if (declaringTypeIncarnations.isEmpty()) {
      // TODO then we should use the reference type - it should match here anyway if we use the
      // match by name strategy!?
      throw new CompletionException("Not (yet) supported scenario");
    }
    // now, we can get all the attribute incarnations for each declaring type incarnation
    for (ASTCDType cAttributeDeclaringType : declaringTypeIncarnations) {
      // if we have more than one declaring type incarnation, we need to add a suffix to the new
      // attributes
      // TODO Actually, we should use the type FULL NAME here, not just the name (and replace .
      // with _)
      // TODO even then we can have name conflicts if we really want to provoke them
      String declaringTypeSuffix =
              declaringTypeIncarnations.size() > 1 ? "_" + cAttributeDeclaringType.getName() : "";

      Set<ASTCDAttribute> attributeIncarnations =
              cAttributeDeclaringType.getCDAttributeList().stream()
                      .filter(
                              attributeIncarnation ->
                                      context
                                              .getAttributeIncStrategy(cAttributeDeclaringType, rTargetAttributeDeclaringType)
                                              .isMatched(attributeIncarnation, rTargetAttribute))
                      .collect(Collectors.toSet());
      System.out.println(
              "Found attribute incarnations for "
                      + rTargetAttribute.getName()
                      + ": "
                      + attributeIncarnations.stream()
                      .map(a -> CD4CodeMill.prettyPrint(a, false))
                      .collect(Collectors.toList()));

      for (ASTCDAttribute cAttribute : attributeIncarnations) {
        // now we have a specific incarnation of the reference attribute in the concrete CD.
        // we can now construct a new attribute based on this incarnation for the concrete type

        // if we have more than one attribute type incarnation, we need to add a suffix to the
        // new attributes
        String attributeSuffix =
                attributeIncarnations.size() > 1 ? "_" + cAttribute.getName() : "";

        // TODO: we never defined how this should work: but this seems like a reasonable
        // approach
        ASTCDAttribute attributeIncarnation = referenceAttribute.deepClone();

        // 1. decide name of the new attribute
        if (referenceAttribute.getName().equals(rTargetAttribute.getName())) {
          // Convention: If the REFERENCED attribute name matches the reference attribute name
          // -> Use the REFERENCED name without a suffix (but still a type suffix)
          attributeIncarnation.setName(cAttribute.getName() + declaringTypeSuffix);
        } else {
          // Default: add the REFERENCED attribute incarnation name as suffix
          attributeIncarnation.setName(
                  referenceAttribute.getName() + declaringTypeSuffix + attributeSuffix);
        }

        // 2. decide type of the new attribute
        if (referenceAttribute.getMCType().deepEquals(rTargetAttribute.getMCType())) {
          // Convention: If the REFERENCED attribute type matches the reference attribute type
          // -> Use the REFERENCED type for every incarnation
          attributeIncarnation.setMCType(cAttribute.getMCType());
        } else {
          // Default: keep the type of the reference attribute resp.
          /*
           * NOTE: The base completer down the line might resolve the type of the reference
           * attribute to multiple incarnations and therefore add even more incarnations of the
           * attribute.
           */
          attributeIncarnation.setMCType(referenceAttribute.getMCType());
        }

        // 3. remove forEach stereotype from concrete attribute but add a reference to the
        // original attribute
        // TODO Once we allow a single concrete element to be an incarnation of multiple
        // reference elements, we need to merge them here
        ASTStereotype stereotype = attributeIncarnation.getModifier().getStereotype();
        stereotype.removeIfValues(value -> value.getName().equals(FOR_EACH_STEREOTYPE));
        stereotype.addValues(
                CD4CodeMill.stereoValueBuilder()
                        .setName(context.getMappingName())
                        .setContent(
                                referenceAttribute
                                        .getSymbol()
                                        .getFullName()) // TODO maybe cut off the CD name from FQName?
                        .build());

        // 4. pass the new attribute to the next completer
        super.completeAttributeInType(context.getConcreteType(), attributeIncarnation, context);
      }
    }
  }

  private Optional<String> getStereotypeValue(ASTCDAttribute attribute) {
    if (attribute.getModifier().isPresentStereotype()) {
      ASTStereotype stereotype = attribute.getModifier().getStereotype();
      if (stereotype.contains(FOR_EACH_STEREOTYPE)) {
        String value = stereotype.getValue(FOR_EACH_STEREOTYPE);
        if (value == null || value.isEmpty()) {
          // TODO Log warning: stereotype value must not be empty for stereotype "forEach"
          return Optional.empty();
        }
        return Optional.of(value);
      }
    }
    return Optional.empty();
  }
}
