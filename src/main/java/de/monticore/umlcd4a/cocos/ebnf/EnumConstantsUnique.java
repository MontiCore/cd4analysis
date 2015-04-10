package de.monticore.umlcd4a.cocos.ebnf;

import java.util.Collection;
import java.util.HashSet;

import de.monticore.cocos.CoCoLog;
import de.monticore.umlcd4a._ast.ASTCDEnum;
import de.monticore.umlcd4a._ast.ASTCDEnumConstant;
import de.monticore.umlcd4a._cocos.CD4AnalysisASTCDEnumCoCo;

/**
 * Checks uniqueness among the enum constants.
 * 
 * @author Robert Heim
 */
public class EnumConstantsUnique implements CD4AnalysisASTCDEnumCoCo {
  public static final String ERROR_CODE = "0xC4A06";
  
  public static final String ERROR_MSG_FORMAT = "Duplicate enum constant: %s.";
  
  @Override
  public void check(ASTCDEnum node) {
    
    Collection<String> usedNames = new HashSet<String>();
    for (ASTCDEnumConstant constant : node.getCDEnumConstants()) {
      String name = constant.getName();
      if (usedNames.contains(name)) {
        CoCoLog.error(ERROR_CODE,
            String.format(ERROR_MSG_FORMAT, name),
            constant.get_SourcePositionStart());
      }
      usedNames.add(name);
    }
  }
}
