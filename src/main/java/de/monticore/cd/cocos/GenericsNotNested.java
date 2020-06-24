/*
 * (c) https://github.com/MontiCore/monticore
 */

/*
 * (c) https://github.com/MontiCore/monticore
 */

/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cd.cocos;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._cocos.CDBasisASTCDAttributeCoCo;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.prettyprint.MCCollectionTypesPrettyPrinter;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that generics are not nested.
 */
public class GenericsNotNested implements CDBasisASTCDAttributeCoCo {

  @Override
  public void check(ASTCDAttribute attr) {
    ASTMCType type = attr.getMCType();
    String typeStr = type.printType(new MCCollectionTypesPrettyPrinter(new IndentPrinter()));
    if (typeStr.matches("(.*)<(.*)<(.*)")) {
      Log.error(String.format(
          "0xCDCF2: Invalid type parameter %s. Generic types may not be nested.", typeStr),
          type.get_SourcePositionStart());
    }
  }
}
