/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdinterfaceandenum.cocos.ebnf;

import de.monticore.cd.cocos.ImplementOnlyInterfaces;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cdinterfaceandenum._cocos.CDInterfaceAndEnumASTCDInterfaceCoCo;

/**
 * Checks that interfaces do only extend other interfaces.
 */
public class CDInterfaceExtendsOnlyInterfaces extends ImplementOnlyInterfaces
    implements CDInterfaceAndEnumASTCDInterfaceCoCo {

  /**
   * @see de.monticore.cdinterfaceandenum._cocos.CDInterfaceAndEnumASTCDEnumCoCo#check(ASTCDEnum)
   */
  @Override
  public void check(ASTCDInterface node) {
    super.check(node);
  }
}
