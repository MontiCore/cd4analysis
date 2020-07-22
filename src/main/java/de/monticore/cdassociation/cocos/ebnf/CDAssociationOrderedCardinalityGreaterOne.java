/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdassociation.cocos.ebnf;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._ast.ASTCDCardinality;
import de.monticore.cdassociation._cocos.CDAssociationASTCDAssociationCoCo;
import de.monticore.cdassociation.prettyprint.CDAssociationPrettyPrinter;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that the cardinality of an ordered association is greater than 1.
 */
public class CDAssociationOrderedCardinalityGreaterOne
    implements CDAssociationASTCDAssociationCoCo {

  CDAssociationPrettyPrinter prettyPrinter = new CDAssociationPrettyPrinter();

  /**
   * @see de.monticore.cdassociation._cocos.CDAssociationASTCDAssociationCoCo#check(de.monticore.cdassociation._ast.ASTCDAssociation)
   */
  @Override
  public void check(ASTCDAssociation assoc) {
    if (assoc.getLeft().isPresentCDOrdered() && assoc.getLeft().isPresentCDCardinality()) {
      check(assoc.getLeft().getCDCardinality(), assoc);
    }
    if (assoc.getRight().isPresentCDOrdered() && assoc.getRight().isPresentCDCardinality()) {
      check(assoc.getRight().getCDCardinality(), assoc);
    }
  }

  /**
   * Does the check on the given cardinality.
   *
   * @param card  the cardinality under test
   * @param assoc the association under test
   * @return whether ther was a coco error or not
   */
  private boolean check(ASTCDCardinality card, ASTCDAssociation assoc) {
    if (card.getUpperBound() <= 1 && !card.toCardinality().isNoUpperLimit()) {
      Log.error(
          String
              .format(
                  "0xCDC65 Association %s is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1.",
                  prettyPrinter.prettyprint(assoc)),
          assoc.get_SourcePositionStart());
      return true;
    }
    return false;
  }

}
