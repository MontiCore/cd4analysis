/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdbasis.cocos.ebnf;

import de.monticore.cd.cocos.ImplementOnlyInterfaces;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._cocos.CDBasisASTCDClassCoCo;

/**
 * See {@link ImplementOnlyInterfaces}.
 */
public class CDClassImplementsOnlyInterfaces extends ImplementOnlyInterfaces
    implements CDBasisASTCDClassCoCo {

  /**
   * @see CDBasisASTCDClassCoCo#check(ASTCDClass)
   */
  @Override
  public void check(ASTCDClass node) {
    super.check(node);
  }
}
