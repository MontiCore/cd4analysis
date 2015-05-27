package de.monticore.umlcd4a.cocos.ebnf;

import de.monticore.cocos.CoCoLog;
import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisASTCDAttributeCoCo;

/**
 * Checks that generics are not nested.
 *
 * @author Robert Heim
 */
public class GenericsNotNested implements CD4AnalysisASTCDAttributeCoCo {
  
  public static final String ERROR_CODE = "0xC4A29";
  
  public static final String ERROR_MSG_FORMAT = "Invalid type parameter %s. Generic types may not be nested.";
  
  @Override
  public void check(ASTCDAttribute attr) {
    ASTType type = attr.getType();
    String typeStr = TypesPrinter.printType(type);
    if (typeStr.matches("(.*)<(.*)<(.*)")) {
      CoCoLog.error(ERROR_CODE,
          String.format(ERROR_MSG_FORMAT, typeStr),
          type.get_SourcePositionStart());
    }
  }
}
