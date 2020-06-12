/*
 * (c) https://github.com/MontiCore/monticore
 */

/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd.cdbasis;

import de.monticore.cd.cdbasis._symboltable.SymModifierBuilder;
import de.monticore.cd.cdbasis._visitor.SymModifierVisitor;

public class CDBasisMill extends CDBasisMillTOP {
  protected static CDBasisMill millSymModifier;
  protected static CDBasisMill millSymModifierVisitor;

  public static SymModifierBuilder symModifierBuilder() {
    if (millSymModifier == null) {
      millSymModifier = getMill();
    }
    return millSymModifier._symModifierBuilder();
  }

  protected SymModifierBuilder _symModifierBuilder() {
    return new SymModifierBuilder();
  }

  public static SymModifierVisitor symModifierVisitor() {
    if (millSymModifier == null) {
      millSymModifier = getMill();
    }
    return millSymModifier._symModifierVisitor();
  }

  protected SymModifierVisitor _symModifierVisitor() {
    return new SymModifierVisitor();
  }
}
