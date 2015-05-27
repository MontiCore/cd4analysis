package de.monticore.umlcd4a.cocos.ebnf;

import de.monticore.cocos.CoCoLog;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAssociation;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisASTCDAssociationCoCo;
import de.monticore.umlcd4a.cocos.CD4ACoCoHelper;

/**
 * Checks that role names start lower-case.
 *
 * @author Robert Heim
 */
public class AssociationRoleNameLowerCase implements CD4AnalysisASTCDAssociationCoCo {
  
  public static final String ERROR_CODE = "0xC4A17";
  
  public static final String ERROR_MSG_FORMAT = "Role %s of association %s must start in lower-case.";
  
  @Override
  public void check(ASTCDAssociation assoc) {
    boolean err = false;
    if (assoc.getLeftRole().isPresent()) {
      err = check(assoc.getLeftRole().get(), assoc);
    }
    if (!err && assoc.getRightRole().isPresent()) {
      check(assoc.getRightRole().get(), assoc);
    }
  }
  
  /**
   * Does the actual check.
   * 
   * @param roleName name under test
   * @param assoc association under test
   * @return whether there was an error or not
   */
  private boolean check(String roleName, ASTCDAssociation assoc) {
    if (roleName.isEmpty()) {
      return false;
    }
    if (!Character.isLowerCase(roleName.charAt(0))) {
      CoCoLog.error(ERROR_CODE,
          String.format(ERROR_MSG_FORMAT, roleName, CD4ACoCoHelper.printAssociation(assoc)),
          assoc.get_SourcePositionStart());
      return true;
    }
    return false;
  }
  
}
