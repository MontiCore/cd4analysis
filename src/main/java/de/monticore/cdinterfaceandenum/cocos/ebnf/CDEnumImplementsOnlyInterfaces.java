/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdinterfaceandenum.cocos.ebnf;

import de.monticore.cd.cocos.ImplementOnlyInterfaces;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._cocos.CDInterfaceAndEnumASTCDEnumCoCo;

/**
 * See {@link ImplementOnlyInterfaces}.
 */
public class CDEnumImplementsOnlyInterfaces extends ImplementOnlyInterfaces
    implements CDInterfaceAndEnumASTCDEnumCoCo {

  /**
   * @see de.monticore.cdinterfaceandenum._cocos.CDInterfaceAndEnumASTCDEnumCoCo#check(ASTCDEnum)
   */
  @Override
  public void check(ASTCDEnum node) {
    super.check(node);
  }
}
