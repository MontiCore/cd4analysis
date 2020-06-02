/*
 * (c) https://github.com/MontiCore/monticore
 */

/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd.cdbasis;

import de.monticore.cd.cdbasis._symboltable.SymModifierBuilder;

public class CDBasisMill extends CDBasisMillTOP {
  protected static CDBasisMill millSymModifier;

  public static SymModifierBuilder symModifierBuilder() {
    if (millSymModifier == null) {
      millSymModifier = getMill();
    }
    return millSymModifier._symModifierBuilder();
  }

  protected SymModifierBuilder _symModifierBuilder() {
    return new SymModifierBuilder();
  }
}
