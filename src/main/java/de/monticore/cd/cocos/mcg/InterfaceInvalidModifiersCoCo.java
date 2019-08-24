/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.cocos.mcg;

import de.monticore.cd.cd4analysis._ast.ASTCDInterface;
import de.monticore.cd.cd4analysis._ast.ASTModifier;
import de.monticore.cd.cd4analysis._cocos.CD4AnalysisASTCDInterfaceCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Interfaces may only be public (or have no modifier).
 *
 * @author Robert Heim
 */
public class InterfaceInvalidModifiersCoCo implements CD4AnalysisASTCDInterfaceCoCo {
  
  /**
   * @see de.monticore.cd._cocos.CD4AnalysisASTCDInterfaceCoCo#check(ASTCDInterface)
   */
  @Override
  public void check(ASTCDInterface node) {
    if (node.isPresentModifier()) {
      ASTModifier mod = node.getModifier();
      check(mod.isAbstract(), "abstract", mod, node);
      check(mod.isDerived(), "derived", mod, node);
      check(mod.isFinal(), "final", mod, node);
      check(mod.isPrivate(), "private", mod, node);
      check(mod.isProtected(), "protected", mod, node);
      check(mod.isStatic(), "static", mod, node);
    }
  }
  
  private void check(boolean invalid, String modifier, ASTModifier mod, ASTCDInterface node) {
    if (invalid) {
      Log.error(
          String.format("0xC4A56 Interface %s has invalid modifier %s.", node.getName(), "\""
              + modifier + "\""),
          mod.get_SourcePositionStart());
    }
  }
}
