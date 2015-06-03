/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos.ebnf;

import de.monticore.cocos.CoCoLog;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAssociation;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDQualifier;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisASTCDAssociationCoCo;
import de.monticore.umlcd4a.cocos.CD4ACoCoHelper;
import de.monticore.umlcd4a.symboltable.CDQualifierSymbol;

/**
 * Checks that qualifier is at the correct side w.r.t. navigation direction.
 *
 * @author Robert Heim
 */
public class AssociationQualifierOnCorrectSide
    implements CD4AnalysisASTCDAssociationCoCo {
  
  public static final String ERROR_CODE = "0xC4A35";
  
  public static final String ERROR_MSG_FORMAT = "The qualifier %s of the qualified association %s is at an invalid position regarding the association's direction.";
  
  /**
   * @see de.monticore.umlcd4a._cocos.CD4AnalysisASTCDAssociationCoCo#check(de.monticore.umlcd4a._ast.ASTCDAssociation)
   */
  @Override
  public void check(ASTCDAssociation node) {
    // only check other side when first side generated no error.
    boolean valid = true;
    if (node.getLeftQualifier().isPresent()) {
      valid = node.isLeftToRight() | node.isBidirectional() | node.isUnspecified();
      if (!valid) {
        error(node.getLeftQualifier().get(), node);
      }
    }
    if (valid && node.getRightQualifier().isPresent()) {
      valid = node.isRightToLeft() | node.isBidirectional() | node.isUnspecified();
      if (!valid) {
        error(node.getRightQualifier().get(), node);
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
    CDQualifierSymbol sym = (CDQualifierSymbol) qualifier.getSymbol().get();
    String qualifierName = sym.getName();
    CoCoLog.error(ERROR_CODE,
        String.format(ERROR_MSG_FORMAT,
            qualifierName,
            CD4ACoCoHelper.printAssociation(node)),
        qualifier.get_SourcePositionStart());
    
  }
  
}
