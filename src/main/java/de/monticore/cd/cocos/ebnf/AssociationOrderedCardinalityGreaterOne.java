/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cd.cocos.ebnf;

import de.monticore.cd.cd4analysis._ast.ASTCDAssociation;
import de.monticore.cd.cocos.CD4ACoCoHelper;
import de.monticore.cd.cd4analysis._ast.ASTCDStereoValue;
import de.monticore.cd.cd4analysis._ast.ASTCardinality;
import de.monticore.cd.cd4analysis._ast.ASTModifier;
import de.monticore.cd.cd4analysis._cocos.CD4AnalysisASTCDAssociationCoCo;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.Optional;

/**
 * Checks that the cardinality of an ordered association is greater than 1.
 *
 * @author Robert Heim
 */
public class AssociationOrderedCardinalityGreaterOne implements
    CD4AnalysisASTCDAssociationCoCo {

  /**
   * @see de.monticore.cd.cd4analysis._cocos.CD4AnalysisASTCDAssociationCoCo#check(de.monticore.cd._ast.ASTCDAssociation)
   */
  @Override
  public void check(ASTCDAssociation assoc) {
    boolean err = false;
    if (assoc.isPresentLeftModifier()
        && isOrdered(assoc.getLeftModifier())
        && assoc.isPresentLeftCardinality()) {
      err = check(assoc.getLeftCardinality(), assoc);
    }
    if (!err && assoc.isPresentRightModifier()
        && isOrdered(assoc.getRightModifier())
        && assoc.isPresentRightCardinality()) {
      check(assoc.getRightCardinality(), assoc);
    }
  }

  /**
   * Does the check on the given cardinality.
   *
   * @param card  the cardinality under test
   * @param assoc the association under test
   * @return whether ther was a coco error or not
   */
  private boolean check(ASTCardinality card, ASTCDAssociation assoc) {
    if (card.isOne() || card.isOptional()) {
      Log.error(
          String
              .format(
                  "0xC4A24 Association %s is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1.",
                  CD4ACoCoHelper.printAssociation(assoc)),
          assoc.get_SourcePositionStart());
      return true;
    }
    return false;
  }

  private boolean isOrdered(ASTModifier mod) {
    if (mod.isPresentStereotype()) {
      List<ASTCDStereoValue> list = mod.getStereotype().getValueList();
      for (ASTCDStereoValue l : list) {
        if ("ordered".equals(l.getName())) {
          return true;
        }
      }
    }

    return false;
  }

}
