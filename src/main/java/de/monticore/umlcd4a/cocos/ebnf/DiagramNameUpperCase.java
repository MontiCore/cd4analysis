package de.monticore.umlcd4a.cocos.ebnf;

import de.monticore.umlcd4a.cd4analysis._ast.ASTCDDefinition;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisASTCDDefinitionCoCo;
import de.se_rwth.commons.logging.Log;

public class DiagramNameUpperCase implements CD4AnalysisASTCDDefinitionCoCo {
  
  @Override
  public void check(ASTCDDefinition cdDefinition) {
    if (!Character.isUpperCase(cdDefinition.getName().charAt(0))) {
      Log.error(String.format("0xC4A01 First character of the diagram name %s must be upper-case.",
          cdDefinition.getName()),
          cdDefinition.get_SourcePositionStart());
    }
  }
}
