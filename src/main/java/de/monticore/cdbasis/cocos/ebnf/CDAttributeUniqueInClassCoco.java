/*
 * (c) https://github.com/MontiCore/monticore
 */

/*
 * (c) https://github.com/MontiCore/monticore
 */

/*
 * (c) https://github.com/MontiCore/monticore
 */

/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdbasis.cocos.ebnf;

import de.monticore.cd.cocos.CoCoHelper;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._cocos.CDBasisASTCDClassCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Ensures that an attribute name does not occur twice in a class.
 */
public class CDAttributeUniqueInClassCoco implements CDBasisASTCDClassCoCo {
  @Override
  public void check(ASTCDClass node) {
    CoCoHelper.findDuplicates(node.getSymbol().getFieldList()).forEach(e ->
        Log.error(
            String.format("0xCDC06: Attribute %s is defined multiple times in class %s.",
                e.getName(), node.getName()),
            node.get_SourcePositionStart())
    );
  }

}
