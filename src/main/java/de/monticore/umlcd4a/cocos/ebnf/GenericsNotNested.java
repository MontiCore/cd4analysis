package de.monticore.umlcd4a.cocos.ebnf;

import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisASTCDAttributeCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that generics are not nested.
 *
 * @author Robert Heim
 */
public class GenericsNotNested implements CD4AnalysisASTCDAttributeCoCo {
  
  @Override
  public void check(ASTCDAttribute attr) {
    ASTType type = attr.getType();
    String typeStr = TypesPrinter.printType(type);
    if (typeStr.matches("(.*)<(.*)<(.*)")) {
      Log.error(String.format(
          "0xC4A29 Invalid type parameter %s. Generic types may not be nested.", typeStr),
          type.get_SourcePositionStart());
    }
  }
}
