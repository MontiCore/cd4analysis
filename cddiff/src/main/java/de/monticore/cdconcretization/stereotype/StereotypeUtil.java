/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconcretization.stereotype;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.monticore.umlstereotype._ast.ASTStereotype;
import de.se_rwth.commons.logging.Log;
import java.util.Optional;
import java.util.Set;

public class StereotypeUtil {
  
  public static final String FOR_EACH_STEREOTYPE = "forEach";
  
  /**
   * Stereotype used to mark elements that are optional in the reference model.
   */
  public static final String OPTIONAL_STEREOTYPE = "optional";
  
  /**
   * Stereotype for binding a reference to a concrete type. This can be used by modelers to
   * manually inform other tools about the specific type incarnation to use in a certain scope of
   * the model.<br>
   * <b>NOTE:</b> By default this is not necessary if "forEach" is used in the reference model.
   * Tools try to derive the parameter element incarnation from the elements name, e.g. if we have
   * a reference type <code>DataClassBuilder</code> and concrete type <code>EmployeeBuilder</code>,
   * we know the incarnation of <code>DataClass</code> in this context is <code>Employee</code>.
   * <br>
   * // TODO update example to something where deriving by name does not work
   * <b>Example:</b> Assuming we have a reference model defining a 'DataClass' and a 'Builder' class
   * where the 'Builder' class is annotated with <code>forEach="DataClass"</code>. This means that
   * within the scope of a specific 'Builder' class incarnation, the reference to 'DataClass' is
   * bound to a specific incarnation as well. This is useful for tools that need to know the
   * specific type to use in a certain context, e.g., OCL or reference code adaptation.
   */
  public static final String BIND_STEREOTYPE = "bind";
  
  private StereotypeUtil() {}
  
  public static void addForEachStereotype(ASTModifier modifier, String content) {
    addStereotype(modifier, FOR_EACH_STEREOTYPE, content);
  }
  
  public static void removeForEachStereotype(ASTModifier modifier) {
    removeStereotype(modifier, FOR_EACH_STEREOTYPE);
  }
  
  public static Optional<String> getForEachStereotypeValue(ASTModifier modifier,
      String valueEmptyWarning) {
    return getStereotypeValue(modifier, FOR_EACH_STEREOTYPE, valueEmptyWarning);
  }
  
  public static void addIncarnationBindingStereotype(ASTModifier modifier, String referenceElement,
      String concreteElement) {
    addStereotype(modifier, BIND_STEREOTYPE, new BindingValue(referenceElement, Set.of(
        concreteElement)).print());
  }
  
  public static Optional<BindingValue> getIncarnationBindingStereotypeValue(ASTModifier modifier,
      String valueEmptyWarning) {
    return getStereotypeValue(modifier, BIND_STEREOTYPE, valueEmptyWarning).map(
        BindingValue::parseFromString);
  }
  
  /**
   * Checks if the given modifier contains the "optional" stereotype.
   *
   * @param modifier the ASTModifier to check
   * @return true if the "optional" stereotype is present, false otherwise
   */
  public static boolean isOptional(ASTModifier modifier) {
    if (modifier.isPresentStereotype()) {
      ASTStereotype stereotype = modifier.getStereotype();
      return stereotype.contains(OPTIONAL_STEREOTYPE);
    }
    return false;
  }
  
  /**
   * Adds the stereotype with the given name and content to the modifier.
   *
   * @param modifier
   * @param name
   * @param content
   */
  public static void addStereotype(ASTModifier modifier, String name, String content) {
    ASTStereotype stereotype;
    if (modifier.isPresentStereotype()) {
      stereotype = modifier.getStereotype();
    }
    else {
      stereotype = CD4CodeMill.stereotypeBuilder().build();
      modifier.setStereotype(stereotype);
    }
    stereotype.addValues(CD4CodeMill.stereoValueBuilder().setName(name).setContent(content)
        .build());
  }
  
  /**
   * Removes the stereotype with the given name from the modifier.
   *
   * @param modifier
   * @param name
   */
  public static void removeStereotype(ASTModifier modifier, String name) {
    if (modifier.isPresentStereotype()) {
      ASTStereotype stereotype = modifier.getStereotype();
      stereotype.removeIfValues(value -> value.getName().equals(name));
    }
  }
  
  /***
   * Returns the value of the stereotype with the given name.
   * If the stereotype does not exist or the value is empty, an empty optional is returned.
   * Additionally, the given warning is logged if the stereotype exists but the value is empty.
   *
   * @param modifier
   * @param name
   * @param noValueWarning
   * @return
   */
  public static Optional<String> getStereotypeValue(ASTModifier modifier, String name,
      String noValueWarning) {
    if (modifier.isPresentStereotype()) {
      ASTStereotype stereotype = modifier.getStereotype();
      if (stereotype.contains(name)) {
        String value = stereotype.getValue(name);
        if (value == null || value.isEmpty()) {
          Log.warn(noValueWarning, modifier.get_SourcePositionStart());
          return Optional.empty();
        }
        return Optional.of(value);
      }
    }
    return Optional.empty();
  }
  
}
