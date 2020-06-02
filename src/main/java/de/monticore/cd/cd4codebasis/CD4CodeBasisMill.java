/*
 * (c) https://github.com/MontiCore/monticore
 */

/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd.cd4codebasis;

import de.monticore.cd.cd4codebasis._symboltable.CD4CodeSymModifierBuilder;

public class CD4CodeBasisMill extends CD4CodeBasisMillTOP {
  protected static CD4CodeBasisMill millCD4CodeSymModifier;

  public CD4CodeBasisMill() {
    millSymModifier = getMill();
  }

  public static CD4CodeSymModifierBuilder symModifierBuilder() {
    if (millCD4CodeSymModifier == null) {
      millCD4CodeSymModifier = getMill();
    }
    return millCD4CodeSymModifier._cd4CodeSymModifierBuilder();
  }

  protected CD4CodeSymModifierBuilder _cd4CodeSymModifierBuilder() {
    return new CD4CodeSymModifierBuilder();
  }

  protected CD4CodeSymModifierBuilder _symModifierBuilder() {
    return _cd4CodeSymModifierBuilder();
  }
}
