package de.monticore.umlcd4a.cocos.ebnf;

import java.util.List;
import java.util.stream.Collectors;

import de.monticore.cocos.CoCoLog;
import de.monticore.umlcd4a._ast.ASTCDDefinition;
import de.monticore.umlcd4a._ast.ASTCDType;
import de.monticore.umlcd4a._cocos.CD4AnalysisASTCDDefinitionCoCo;

public class NoKeywordsUsed implements CD4AnalysisASTCDDefinitionCoCo {
  
  public static final String ERROR_CODE = "0xC4A03";
  
  public static final String ERROR_MSG_FORMAT = "Name %s is reserved for internal use.";
  
  // TODO AR <- RH Liste vervollständigen
  private String[] keywords = { "DAO", "Factory" };
  
  @Override
  public void check(ASTCDDefinition cdDefinition) {
    check(cdDefinition.getCDClasses().stream().map(t -> (ASTCDType) t).collect(Collectors.toList()));
    check(cdDefinition.getCDInterfaces().stream().map(t -> (ASTCDType) t)
        .collect(Collectors.toList()));
    check(cdDefinition.getCDEnums().stream().map(t -> (ASTCDType) t).collect(Collectors.toList()));
  }
  
  private void check(List<ASTCDType> types) {
    for (ASTCDType t : types) {
      for (String keyword : keywords) {
        if (t.getName().toLowerCase().equals(keyword.toLowerCase())) {
          CoCoLog.error(ERROR_CODE,
              String.format(ERROR_MSG_FORMAT, t.getName()),
              t.get_SourcePositionStart());
        }
      }
    }
  }
}
