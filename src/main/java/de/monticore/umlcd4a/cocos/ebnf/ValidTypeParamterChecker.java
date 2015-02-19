package de.monticore.umlcd4a.cocos.ebnf;

import de.cd4analysis._ast.ASTCDAttribute;
import de.cd4analysis._ast.ASTCDClass;
import de.cd4analysis._ast.ASTCDDefinition;
import de.cd4analysis._cocos.CD4AnalysisASTCDDefinitionCoCo;
import de.monticore.cocos.CoCoHelper;
import de.monticore.types.TypesPrinter;
import de.monticore.types._ast.ASTType;
import de.monticore.types._ast.ASTTypeArgument;
import de.monticore.types._ast.ASTTypeArguments;

public class ValidTypeParamterChecker implements CD4AnalysisASTCDDefinitionCoCo {

  public static final String ERROR_CODE = "0xD???";

  public static final String ERROR_MSG_FORMAT = "Invalid type parameter [%s]. Generic types may not be nested";

  public void check(ASTCDDefinition cdDefinition) {
    for (ASTCDClass clazz : cdDefinition.getCDClasses()) {
      for (ASTCDAttribute attr : clazz.getCDAttributes()) {
        ASTType type = attr.getType();
        if (TypesPrinter.printType(type).matches("*<*<")) {

          CoCoHelper.buildErrorMsg(ERROR_CODE,
              String.format(ERROR_MSG_FORMAT, TypesPrinter.printType(type)),
              type.get_SourcePositionStart());

        }
      }
    }
  }
}
