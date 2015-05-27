package de.monticore.umlcd4a.cocos.ebnf;

import java.util.ArrayList;
import java.util.Collection;

import de.monticore.cocos.CoCoLog;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDDefinition;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDType;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisASTCDDefinitionCoCo;

/**
 * Checks that type names start upper-case.
 *
 * @author Robert Heim
 */
public class TypeNameUpperCase implements CD4AnalysisASTCDDefinitionCoCo {
  
  public static final String ERROR_CODE = "0xC4A05";
  
  public static final String ERROR_MSG_FORMAT = "The first character of the %s %s must be upper-case.";
  
  @Override
  public void check(ASTCDDefinition cdDefinition) {
    Collection<ASTCDType> types = new ArrayList<>();
    types.addAll(cdDefinition.getCDClasses());
    check(types, "class");
    types.clear();
    types.addAll(cdDefinition.getCDInterfaces());
    check(types, "interface");
    types.clear();
    types.addAll(cdDefinition.getCDEnums());
    check(types, "enum");
  }
  
  /**
   * Does the actual check.
   * 
   * @param types
   * @param kind kind of the types (class, interface, or enum)
   */
  private void check(Collection<ASTCDType> types, String kind) {
    for (ASTCDType cdType : types) {
      if (!Character.isUpperCase(cdType.getName().charAt(0))) {
        CoCoLog.error(ERROR_CODE,
            String.format(ERROR_MSG_FORMAT, kind, cdType.getName()),
            cdType.get_SourcePositionStart());
      }
    }
  }
}
