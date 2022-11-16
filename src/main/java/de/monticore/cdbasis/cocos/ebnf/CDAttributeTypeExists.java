/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdbasis.cocos.ebnf;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._cocos.CDBasisASTCDAttributeCoCo;

/** Checks that types of attributes are resolvable. */
public class CDAttributeTypeExists implements CDBasisASTCDAttributeCoCo {

  @Override
  public void check(ASTCDAttribute node) {
    if (node.isPresentSymbol() && node.getSymbol().getType() != null) {
      //noinspection ResultOfMethodCallIgnored
      node.getSymbol().getType().getTypeInfo();
    }
  }
}
