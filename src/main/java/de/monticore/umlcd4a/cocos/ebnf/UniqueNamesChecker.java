package de.monticore.umlcd4a.cocos.ebnf;

import java.util.HashSet;
import java.util.Set;

import de.monticore.cocos.CoCoLog;
import de.monticore.umlcd4a._ast.ASTCDClass;
import de.monticore.umlcd4a._ast.ASTCDClassList;
import de.monticore.umlcd4a._ast.ASTCDDefinition;
import de.monticore.umlcd4a._ast.ASTCDEnum;
import de.monticore.umlcd4a._ast.ASTCDEnumList;
import de.monticore.umlcd4a._ast.ASTCDInterface;
import de.monticore.umlcd4a._ast.ASTCDInterfaceList;
import de.monticore.umlcd4a._cocos.CD4AnalysisASTCDDefinitionCoCo;

/**
 * Checks uniqueness of class,interface and enum names
 * 
 * @author eikermann
 *
 */
public class UniqueNamesChecker implements CD4AnalysisASTCDDefinitionCoCo {
  public static final String ERROR_CODE = "0xD???";

  public static final String ERROR_MSG_FORMAT = "The name %s is used several times. Classes, interfaces and enumerations may not use the same names.";

  private Set<String> usedNames = new HashSet<String>();

  @Override
  public void check(ASTCDDefinition cdDefinition) {

    ASTCDClassList classList = cdDefinition.getCDClasses();
    ASTCDEnumList enumList = cdDefinition.getCDEnums();
    ASTCDInterfaceList interfaceList = cdDefinition.getCDInterfaces();

    for (ASTCDClass cdType : classList) {

      String className = cdType.getName();

      if (usedNames.contains(className)) {

        CoCoLog.error(ERROR_CODE,
            String.format(ERROR_MSG_FORMAT, className),
            cdType.get_SourcePositionStart());
      }
      usedNames.add(className);
    }

    for (ASTCDInterface cdType : interfaceList) {

      String className = cdType.getName();

      if (usedNames.contains(className)) {

        CoCoLog.error(ERROR_CODE,
            String.format(ERROR_MSG_FORMAT, className),
            cdType.get_SourcePositionStart());
      }
      usedNames.add(className);
    }

    for (ASTCDEnum cdType : enumList) {

      String className = cdType.getName();

      if (usedNames.contains(className)) {

        CoCoLog.error(ERROR_CODE,
            String.format(ERROR_MSG_FORMAT, className),
            cdType.get_SourcePositionStart());
      }
      usedNames.add(className);
    }
  }
}
