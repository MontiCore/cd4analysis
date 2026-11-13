/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4codebasis.cocos.ebnf;

import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._cocos.CD4CodeBasisASTCDConstructorCoCo;
import de.monticore.cd4codebasis._cocos.CD4CodeBasisASTCDMethodCoCo;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._cocos.CDBasisASTCDAttributeCoCo;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._cocos.CDInterfaceAndEnumASTCDEnumCoCo;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that 'sealed' and 'non-sealed' are only used for classes and interfaces.
 */
public class CDSealedOnlyOnClassesAndInterfaces implements CDBasisASTCDAttributeCoCo,
    CD4CodeBasisASTCDMethodCoCo, CD4CodeBasisASTCDConstructorCoCo, CDInterfaceAndEnumASTCDEnumCoCo {
  
  public static final String ERROR_CODE = "0xCDCFF";
  
  @Override
  public void check(ASTCDAttribute node) {
    check(node.getModifier(), "attribute", node.getName());
  }
  
  @Override
  public void check(ASTCDMethod node) {
    check(node.getModifier(), "method", node.getName());
  }
  
  @Override
  public void check(ASTCDConstructor node) {
    check(node.getModifier(), "constructor", node.getName());
  }
  
  @Override
  public void check(ASTCDEnum node) {
    check(node.getModifier(), "enum", node.getName());
  }
  
  protected void check(ASTModifier mod, String kind, String name) {
    if (CDSealedModifierCombinations.isSealed(mod) || CDSealedModifierCombinations.isNonSealed(
        mod)) {
      Log.error(String.format("%s: The %s '%s' must not be 'sealed' or 'non-sealed'.", ERROR_CODE,
          kind, name), mod.get_SourcePositionStart(), mod.get_SourcePositionEnd());
    }
  }
  
}
