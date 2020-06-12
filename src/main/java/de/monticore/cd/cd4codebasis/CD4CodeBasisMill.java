/*
 * (c) https://github.com/MontiCore/monticore
 */

/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd.cd4codebasis;

import de.monticore.cd.cd4codebasis._symboltable.CD4CodeSymModifierBuilder;
import de.monticore.cd.cd4codebasis._visitor.CD4CodeSymModifierVisitor;

public class CD4CodeBasisMill extends CD4CodeBasisMillTOP {
  protected static CD4CodeBasisMill millCD4CodeSymModifier;
  protected static CD4CodeBasisMill millCD4CodeSymModifierVisitor;

  public CD4CodeBasisMill() {
    millCD4CodeSymModifier = getMill();
  }

  public static CD4CodeSymModifierBuilder symModifierBuilder() {
    if (millCD4CodeSymModifier == null) {
      millCD4CodeSymModifier = getMill();
    }
    return millCD4CodeSymModifier._symModifierBuilder();
  }

  protected CD4CodeSymModifierBuilder _symModifierBuilder() {
    return new CD4CodeSymModifierBuilder();
  }

  public static CD4CodeSymModifierVisitor symModifierVisitor() {
    if (millCD4CodeSymModifierVisitor == null) {
      millCD4CodeSymModifierVisitor = getMill();
    }
    return millCD4CodeSymModifierVisitor._symModifierVisitor();
  }

  protected CD4CodeSymModifierVisitor _symModifierVisitor() {
    return new CD4CodeSymModifierVisitor(symModifierBuilder());
  }

}
