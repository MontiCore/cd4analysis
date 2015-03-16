/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos.mcg2ebnf;

import de.cd4analysis._ast.ASTCDAssociation;
import de.cd4analysis._ast.ASTStereotype;
import de.cd4analysis._cocos.CD4AnalysisASTCDAssociationCoCo;
import de.monticore.cocos.CoCoHelper;
import de.monticore.umlcd4a.cocos.CD4ACoCoHelper;
import de.se_rwth.commons.logging.Log;

/**
 * Stereotypes are forbidding for Associations in the ebnf.
 *
 * @author Robert Heim
 */
public class AssociationNoStereotypesCoCo implements CD4AnalysisASTCDAssociationCoCo {
  public static final String ERROR_CODE = "0xCD4AC3011";
  
  public static final String ERROR_MSG_FORMAT = "Association %s may not have stereotypes.";
  
  /**
   * @see de.monticore.umlcd4a._cocos.CD4AnalysisASTCDAssociationCoCo#check(de.monticore.umlcd4a._ast.ASTCDAssociation)
   */
  @Override
  public void check(ASTCDAssociation node) {
    if (node.getStereotype().isPresent()) {
      ASTStereotype stereoType = node.getStereotype().get();
      if (stereoType.getValues().size() > 0) {
        Log.error(CoCoHelper.buildErrorMsg(
            ERROR_CODE,
            String.format(ERROR_MSG_FORMAT, CD4ACoCoHelper.printAssociation(node)),
            stereoType.get_SourcePositionStart()));
      }
    }
  }
}