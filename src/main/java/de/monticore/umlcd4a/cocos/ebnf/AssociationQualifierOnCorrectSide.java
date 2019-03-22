/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos.ebnf;

import de.monticore.types.TypesPrinter;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAssociation;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDQualifier;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisASTCDAssociationCoCo;
import de.monticore.umlcd4a.cocos.CD4ACoCoHelper;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that qualifier is at the correct side w.r.t. navigation direction.
 *
 * @author Robert Heim
 */
public class AssociationQualifierOnCorrectSide
    implements CD4AnalysisASTCDAssociationCoCo {
  
  /**
   * @see de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisASTCDAssociationCoCo#check(de.monticore.umlcd4a.cd4analysis._ast.ASTCDAssociation)
   */
  @Override
  public void check(ASTCDAssociation node) {
    // only check other side when first side generated no error.
    boolean valid = true;
    if (node.isPresentLeftQualifier()) {
      valid = node.isLeftToRight() | node.isBidirectional() | node.isUnspecified();
      if (!valid) {
        error(node.getLeftQualifier(), node);
      }
    }
    if (valid && node.isPresentRightQualifier()) {
      valid = node.isRightToLeft() | node.isBidirectional() | node.isUnspecified();
      if (!valid) {
        error(node.getRightQualifier(), node);
      }
      
    }
  }
  
  /**
   * Issues the CoCo error.
   * 
   * @param qualifier qualifier under test
   * @param node the association under test
   * @return whether there was a coco error or not
   */
  private void error(ASTCDQualifier qualifier, ASTCDAssociation node) {
    String qualifierName = "";
    if (qualifier.isPresentType()) {
      qualifierName = TypesPrinter.printType(qualifier.getType());
    } else if (qualifier.isPresentName()) {
      qualifierName = qualifier.getName();
    }
    Log.error(
        String
            .format(
                "0xC4A35 The qualifier %s of the qualified association %s is at an invalid position regarding the association's direction.",
                qualifierName,
                CD4ACoCoHelper.printAssociation(node)),
        qualifier.get_SourcePositionStart());
  }
  
}
