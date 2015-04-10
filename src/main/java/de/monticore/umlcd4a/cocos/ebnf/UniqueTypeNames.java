package de.monticore.umlcd4a.cocos.ebnf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import de.monticore.cocos.CoCoLog;
import de.monticore.umlcd4a._ast.ASTCDDefinition;
import de.monticore.umlcd4a._ast.ASTCDType;
import de.monticore.umlcd4a._cocos.CD4AnalysisASTCDDefinitionCoCo;

/**
 * Checks uniqueness among the names of classes, interfaces, and enums.
 * 
 * @author Robert Heim
 */
public class UniqueTypeNames implements CD4AnalysisASTCDDefinitionCoCo {
  public static final String ERROR_CODE = "0xC4A04";
  
  public static final String ERROR_MSG_FORMAT = "The name %s is used several times. Classes, interfaces and enumerations may not use the same names.";
  
  @Override
  public void check(ASTCDDefinition cdDefinition) {
    
    Collection<ASTCDType> types = new ArrayList<>();
    types.addAll(cdDefinition.getCDClasses());
    types.addAll(cdDefinition.getCDEnums());
    types.addAll(cdDefinition.getCDInterfaces());
    
    Collection<String> usedNames = new HashSet<String>();
    for (ASTCDType type : types) {
      String name = type.getName();
      if (usedNames.contains(name)) {
        CoCoLog.error(ERROR_CODE,
            String.format(ERROR_MSG_FORMAT, name),
            type.get_SourcePositionStart());
      }
      usedNames.add(name);
    }
  }
}
