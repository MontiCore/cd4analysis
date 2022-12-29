/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.monticore.cd4analysis._symboltable.ICD4AnalysisGlobalScope;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._symboltable.CDRoleSymbol;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfGenerics;
import de.monticore.types.check.SymTypeOfObject;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.monticore.umlstereotype._ast.ASTStereotype;
import java.util.*;

/** This class transforms the cd4a-classdiagram into an JSON Schema. */
public class CD2JsonTransform {

  /**
   * Stores the resulting JSON-Schema. Each call of visit(ASTCDClass astcdClass) is updating the
   * result.
   */
  private final ObjectNode classSchemata = JsonNodeFactory.instance.objectNode();

  /** JSON value that gets assigned to CD StereoValues without a value property */
  private static final boolean DEFAULT_ANNOTATION_VALUE = true;

  private final ICD4AnalysisGlobalScope globalScope;

  public CD2JsonTransform(ICD4AnalysisGlobalScope globalScope) {
    this.globalScope = globalScope;
  }

  /** Create JSON-Schema for the given class and stores it in variable "classSchemata" */
  public void visit(ASTCDClass astcdClass) {
    ObjectNode node = new ObjectMapper().createObjectNode();
    ObjectNode properties = new ObjectMapper().createObjectNode();

    // Iterate over all superclasses and add the attributes to the properties
    for (ASTCDClass superClass : CD2JsonTransform.getAllSuperClasses(astcdClass)) {
      superClass
          .getSymbol()
          .getFieldList()
          .forEach(attr -> properties.set(attr.getName(), getField(attr)));
    }

    // Add the attributes of the child-class to the properties
    astcdClass
        .getSymbol()
        .getFieldList()
        .forEach(attr -> properties.set(attr.getName(), getField(attr)));

    node.put("title", astcdClass.getName());
    node.put("type", "object");
    // node.put("$schema", "https://json-schema.org/draft/2019-09/schema");
    node.set("properties", properties);

    ObjectNode result = new ObjectMapper().createObjectNode();
    result.set(astcdClass.getName(), node);
    classSchemata.setAll(result);
  }

  /**
   * Returns JSON-Schema with information of the field (including annotations)
   *
   * @param f Information of the fied
   */
  protected ObjectNode getField(FieldSymbol f) {
    SymTypeExpression attributeType = f.getType();

    ObjectNode result = getAttribute(attributeType);

    // Add additional information to attribute (e.g. derived or annotations)
    if (f.isPresentAstNode() && f.getAstNode() instanceof ASTCDAttribute) {
      ASTCDAttribute attribute = ((ASTCDAttribute) f.getAstNode());
      result.put("derived", ((ASTCDAttribute) f.getAstNode()).getModifier().isDerived());
      if (hasAttributeAnnotations(attribute)) {
        ObjectNode annotations = new ObjectMapper().createObjectNode();
        attribute
            .getModifier()
            .getStereotype()
            .forEachValues(
                annotation -> {
                  if (annotation.getValue().isEmpty()) {
                    annotations.put(annotation.getName(), DEFAULT_ANNOTATION_VALUE);
                  } else {
                    annotations.put(annotation.getName(), annotation.getValue());
                  }
                });
        result.set("annotations", annotations);
      }

    } else { // association assumed
      Optional<CDRoleSymbol> associationRole = getRoleFromField(f);
      Optional<ASTCDAssociation> association = Optional.empty();
      if (associationRole.isPresent()) {
        association = getAssociationForRole(associationRole.get());
      }
      if (association.isPresent()) {
        ObjectNode annotations = new ObjectMapper().createObjectNode();
        getAnnotationFromAssociation(association.get())
            .ifPresent(
                annotation ->
                    annotation.forEachValues(
                        annotationValue -> {
                          if (annotationValue.getValue().isEmpty()) {
                            annotations.put(annotationValue.getName(), DEFAULT_ANNOTATION_VALUE);
                          } else {
                            annotations.put(annotationValue.getName(), annotationValue.getValue());
                          }
                        }));
        result.set("annotations", annotations);
      }
    }
    return result;
  }

  /**
   * Returns JSON-Schema with information of the type
   *
   * @param attr Type Information
   */
  protected static ObjectNode getAttribute(SymTypeExpression attr) {

    // Mapping from Java to TypeScript types
    Map<String, String> m = new HashMap<>();
    m.put("float", "number");
    m.put("Float", "number");
    m.put("int", "number");
    m.put("Integer", "number");
    m.put("long", "number");
    m.put("Long", "number");
    m.put("double", "number");
    m.put("Double", "number");

    m.put("String", "string");

    m.put("boolean", "boolean");
    m.put("Boolean", "boolean");

    ObjectNode result = new ObjectMapper().createObjectNode();

    if (m.containsKey(attr.getTypeInfo().getName())) { // Primitive Types
      result.put("type", m.get(attr.getTypeInfo().getName()));
    } else if (attr instanceof SymTypeOfGenerics) { // List<T> OR Optional<T>
      SymTypeOfGenerics gAttr = (SymTypeOfGenerics) attr;
      if (gAttr.getFullName().equals("java.util.Optional")) {
        result.setAll(getAttribute(gAttr.getArgument(0)));
      } else {
        result.put("type", "array");
        result.set("items", getAttribute(gAttr.getArgument(0)));
      }
      result.put("required", false);

    } else if (attr instanceof SymTypeOfObject) { // Associations to other Classes
      if (attr.getTypeInfo().isPresentAstNode()
          && attr.getTypeInfo().getAstNode() instanceof ASTCDEnum) {
        ASTCDEnum attrEnum = (ASTCDEnum) attr.getTypeInfo().getAstNode();
        result.put("type", "string");

        ArrayNode arrayEnumValues = new ObjectMapper().createArrayNode();
        attrEnum.getCDEnumConstantList().forEach(x -> arrayEnumValues.add(x.getName()));
        result.set("enum", arrayEnumValues);
      } else {
        result.put("$ref", "#/" + attr.getTypeInfo().getName());
      }

    } else {
      assert (false);
    }
    return result;
  }

  /**
   * @return List with all superclasses of given class. The given class (parameter astcdClass) is
   *     not in the result
   */
  public static List<ASTCDClass> getAllSuperClasses(ASTCDClass astcdClass) {
    List<ASTCDClass> allSuperClasses = new ArrayList<>();
    for (ASTMCObjectType superClass : astcdClass.getSuperclassList()) {
      Optional<CDTypeSymbol> superTypeSymbol =
          astcdClass
              .getEnclosingScope()
              .resolveCDType(
                  new MCBasicTypesFullPrettyPrinter(new IndentPrinter()).prettyprint(superClass));

      if (superTypeSymbol.isPresent() && superTypeSymbol.get().getAstNode() instanceof ASTCDClass) {
        allSuperClasses.add((ASTCDClass) superTypeSymbol.get().getAstNode());
        allSuperClasses.addAll(
            CD2JsonTransform.getAllSuperClasses((ASTCDClass) superTypeSymbol.get().getAstNode()));
      }
    }

    return allSuperClasses;
  }

  /***
   * @param attribute  class attribute
   * @return true if attribute has a non-empty stereotype
   */
  private static boolean hasAttributeAnnotations(ASTCDAttribute attribute) {
    if (attribute.getModifier().isPresentStereotype())
      return !attribute.getModifier().getStereotype().isEmptyValues();
    return false;
  }

  /***
   * extracts annotations out off an association
   * assumption: the annotation is provided before the assumption
   */
  private static Optional<ASTStereotype> getAnnotationFromAssociation(
      ASTCDAssociation association) {
    ASTModifier modifier = association.getModifier();
    if (modifier.isPresentStereotype() && !modifier.getStereotype().isEmptyValues())
      return Optional.of(association.getModifier().getStereotype());
    return Optional.empty();
  }

  /***
   * looks up the field symbol in the symbol table
   * @return the role symbol if found or an empty result
   */
  private Optional<CDRoleSymbol> getRoleFromField(FieldSymbol fieldSymbol) {
    return globalScope.resolveCDRole(fieldSymbol.getFullName());
  }

  /***
   * extracts the association out off a role
   * @return the corresponding association AST node if found, otherwise empty
   */
  private static Optional<ASTCDAssociation> getAssociationForRole(CDRoleSymbol roleSymbol) {
    if (roleSymbol.isPresentAssoc() && roleSymbol.getAssoc().isPresentAssociation()) {
      return Optional.of(roleSymbol.getAssoc().getAssociation().getAstNode());
    } else {
      return Optional.empty();
    }
  }

  public JsonNode getScheme() {
    return classSchemata;
  }
}
