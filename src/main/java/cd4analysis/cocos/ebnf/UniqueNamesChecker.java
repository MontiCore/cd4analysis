package cd4analysis.cocos.ebnf;

import java.util.HashSet;
import java.util.Set;

import de.cd4analysis._ast.ASTCDClass;
import de.cd4analysis._ast.ASTCDClassList;
import de.cd4analysis._ast.ASTCDDefinition;
import de.cd4analysis._ast.ASTCDEnum;
import de.cd4analysis._ast.ASTCDEnumList;
import de.cd4analysis._ast.ASTCDInterface;
import de.cd4analysis._ast.ASTCDInterfaceList;
import de.cd4analysis._cocos.CD4AnalysisASTCDDefinitionCoCo;
import de.monticore.cocos.CoCoHelper;

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

        CoCoHelper.buildErrorMsg(ERROR_CODE,
            String.format(ERROR_MSG_FORMAT, className),
            cdType.get_SourcePositionStart());
      }
      usedNames.add(className);
    }

    for (ASTCDInterface cdType : interfaceList) {

      String className = cdType.getName();

      if (usedNames.contains(className)) {

        CoCoHelper.buildErrorMsg(ERROR_CODE,
            String.format(ERROR_MSG_FORMAT, className),
            cdType.get_SourcePositionStart());
      }
      usedNames.add(className);
    }

    for (ASTCDEnum cdType : enumList) {

      String className = cdType.getName();

      if (usedNames.contains(className)) {

        CoCoHelper.buildErrorMsg(ERROR_CODE,
            String.format(ERROR_MSG_FORMAT, className),
            cdType.get_SourcePositionStart());
      }
      usedNames.add(className);
    }
  }
}
