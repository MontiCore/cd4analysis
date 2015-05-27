package de.monticore.umlcd4a.cocos.ebnf;

import java.util.Optional;

import de.monticore.cocos.CoCoLog;
import de.monticore.umlcd4a._ast.ASTCDAssociation;
import de.monticore.umlcd4a._ast.ASTCardinality;
import de.monticore.umlcd4a._ast.ASTModifier;
import de.monticore.umlcd4a._ast.ASTStereoValue;
import de.monticore.umlcd4a._ast.ASTStereoValueList;
import de.monticore.umlcd4a._cocos.CD4AnalysisASTCDAssociationCoCo;
import de.monticore.umlcd4a.cocos.CD4ACoCoHelper;

/**
 * Checks that the cardinality of an ordered association is greater than 1.
 *
 * @author Robert Heim
 */
public class AssociationOrderedCardinalityGreaterOne implements
    CD4AnalysisASTCDAssociationCoCo {
  
  public static final String ERROR_CODE = "0xC4A24";
  
  public static final String ERROR_MSG_FORMAT = "Association %s is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1.";
  
  /**
   * @see de.monticore.umlcd4a._cocos.CD4AnalysisASTCDAssociationCoCo#check(de.monticore.umlcd4a._ast.ASTCDAssociation)
   */
  @Override
  public void check(ASTCDAssociation assoc) {
    boolean err = false;
    if (assoc.getLeftModifier().isPresent()
        && isOrdered(assoc.getLeftModifier().get())) {
      err = check(assoc.getLeftCardinality(), assoc);
    }
    
    if (!err && assoc.getRightModifier().isPresent()
        && isOrdered(assoc.getRightModifier().get())) {
      check(assoc.getRightCardinality(), assoc);
    }
    
  }
  
  /**
   * Does the check on the given cardinality.
   * 
   * @param card the cardinality under test
   * @param assoc the association under test
   * @return whether ther was a coco error or not
   */
  private boolean check(Optional<ASTCardinality> card, ASTCDAssociation assoc) {
    if (card.isPresent()) {
      if (card.get().isOne() || card.get().isOptional()) {
        CoCoLog.error(ERROR_CODE,
            String.format(ERROR_MSG_FORMAT, CD4ACoCoHelper.printAssociation(assoc)),
            assoc.get_SourcePositionStart());
        return true;
      }
    }
    return false;
    
  }
  
  private boolean isOrdered(ASTModifier mod) {
    if (mod.getStereotype().isPresent()) {
      ASTStereoValueList list = mod.getStereotype().get().getValues();
      for (ASTStereoValue l : list) {
        if (l.getName().equals("ordered")) {
          return true;
        }
      }
    }
    
    return false;
  }
  
}