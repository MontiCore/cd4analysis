/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconcretization.stereotype;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.monticore.umlstereotype._ast.ASTStereotype;
import de.se_rwth.commons.logging.Log;
import java.util.Optional;

public class StereotypeUtil {
  
  public static final String FOR_EACH_STEREOTYPE = "forEach";
  
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
