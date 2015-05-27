package de.monticore.umlcd4a.cocos.ebnf;

import de.monticore.cocos.CoCoLog;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDDefinition;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisASTCDDefinitionCoCo;

public class DiagramNameUpperCase implements CD4AnalysisASTCDDefinitionCoCo {
  
  public static final String ERROR_CODE = "0xC4A01";
  
  public static final String ERROR_MSG_FORMAT = "First character of the diagram name %s must be upper-case.";
  
  @Override
  public void check(ASTCDDefinition cdDefinition) {
    if (!Character.isUpperCase(cdDefinition.getName().charAt(0))) {
      CoCoLog.error(ERROR_CODE,
          String.format(ERROR_MSG_FORMAT, cdDefinition.getName()),
          cdDefinition.get_SourcePositionStart());
    }
  }
}
