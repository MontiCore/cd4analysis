/*
 * Copyright (c) 2014 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos.ebnf;

import com.google.common.base.Optional;

import de.cd4analysis._ast.ASTCDAssociation;
import de.cd4analysis._cocos.CD4AnalysisASTCDAssociationCoCo;
import de.monticore.cocos.CoCoHelper;
import de.monticore.umlcd4a.cocos.CD4ACoCoHelper;
import de.se_rwth.commons.logging.Log;

/**
 * Ensures that role names are lower-case.
 *
 * @author Robert Heim
 */
public class RoleNamesLowerCaseCoco implements CD4AnalysisASTCDAssociationCoCo {
  
  public static final String ERROR_CODE = "0xC4A17";
  
  public static final String ERROR_MSG_FORMAT = "Role %s of association %s must start in lower-case.";
  
  @Override
  public void check(ASTCDAssociation node) {
    if (!checkRole(node.getLeftRole())) {
      Log.error(CoCoHelper.buildErrorMsg(
          ERROR_CODE,
          String.format(ERROR_MSG_FORMAT, node.getLeftRole().get(),
              CD4ACoCoHelper.printAssociation(node)),
          node.get_SourcePositionStart()));
    }
    else if (!checkRole(node.getRightRole())) {
      Log.error(CoCoHelper.buildErrorMsg(
          ERROR_CODE,
          String.format(ERROR_MSG_FORMAT, node.getRightRole().get(),
              CD4ACoCoHelper.printAssociation(node)),
          node.get_SourcePositionStart()));
    }
  }
  
  private boolean checkRole(Optional<String> optional) {
    if (!optional.isPresent()) {
      return true;
    }
    String r = optional.get();
    if (r.length() == 0) {
      return true;
    }
    return !Character.isUpperCase(r.charAt(0));
  }
}
