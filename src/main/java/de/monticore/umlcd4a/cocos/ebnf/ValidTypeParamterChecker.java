package de.monticore.umlcd4a.cocos.ebnf;

import de.monticore.cocos.CoCoLog;
import de.monticore.types.TypesPrinter;
import de.monticore.types._ast.ASTType;
import de.monticore.umlcd4a._ast.ASTCDAttribute;
import de.monticore.umlcd4a._ast.ASTCDClass;
import de.monticore.umlcd4a._ast.ASTCDDefinition;
import de.monticore.umlcd4a._cocos.CD4AnalysisASTCDDefinitionCoCo;

public class ValidTypeParamterChecker implements CD4AnalysisASTCDDefinitionCoCo {

  public static final String ERROR_CODE = "0xD???";

  public static final String ERROR_MSG_FORMAT = "Invalid type parameter [%s]. Generic types may not be nested";

  public void check(ASTCDDefinition cdDefinition) {
    for (ASTCDClass clazz : cdDefinition.getCDClasses()) {
      for (ASTCDAttribute attr : clazz.getCDAttributes()) {
        ASTType type = attr.getType();
        if (TypesPrinter.printType(type).matches("*<*<")) {

          CoCoLog.error(ERROR_CODE,
              String.format(ERROR_MSG_FORMAT, TypesPrinter.printType(type)),
              type.get_SourcePositionStart());

        }
      }
    }
  }
}
