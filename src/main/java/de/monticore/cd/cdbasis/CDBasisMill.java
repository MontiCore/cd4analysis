/*
 * (c) https://github.com/MontiCore/monticore
 */

/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd.cdbasis;

import de.monticore.cd.cdbasis._symboltable.SymModifier;

public class CDBasisMill extends CDBasisMillTOP {
  protected static CDBasisMill millSymModifier;

  public static SymModifier SymModifierBuilder() {
    if (millSymModifier == null) {
      millSymModifier = getMill();
    }
    return millSymModifier._SymModifierBuilder();

  }

  protected SymModifier _SymModifierBuilder() {
    return new SymModifier();
  }
}
