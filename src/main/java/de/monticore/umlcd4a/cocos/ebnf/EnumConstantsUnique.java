package de.monticore.umlcd4a.cocos.ebnf;

import java.util.Collection;
import java.util.HashSet;

import de.monticore.umlcd4a.cd4analysis._ast.ASTCDEnum;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDEnumConstant;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisASTCDEnumCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Checks uniqueness among the enum constants.
 * 
 * @author Robert Heim
 */
public class EnumConstantsUnique implements CD4AnalysisASTCDEnumCoCo {
  
  @Override
  public void check(ASTCDEnum node) {
    Collection<String> usedNames = new HashSet<String>();
    for (ASTCDEnumConstant constant : node.getCDEnumConstants()) {
      String name = constant.getName();
      if (usedNames.contains(name)) {
        Log.error(String.format("0xC4A06 Duplicate enum constant: %s.", name),
            constant.get_SourcePositionStart());
      }
      usedNames.add(name);
    }
  }
}
