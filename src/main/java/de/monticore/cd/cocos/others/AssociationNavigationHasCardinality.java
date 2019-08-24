/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cd.cocos.others;

import de.monticore.cd.cd4analysis._ast.ASTCDAssociation;
import de.monticore.cd.cd4analysis._cocos.CD4AnalysisASTCDAssociationCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that association names start lower-case.
 *
 * @author Robert Heim
 */
public class AssociationNavigationHasCardinality implements CD4AnalysisASTCDAssociationCoCo {
  
  @Override
  public void check(ASTCDAssociation a) {
    if ((a.isRightToLeft() || a.isBidirectional()) && !a.isPresentLeftCardinality()) {
      Log.error(
              String.format("0xC4A38 Association `%s` has left navigation arrow (<-), but no left cardinality.", a),
              a.get_SourcePositionStart());
    }
    if ((a.isLeftToRight() || a.isBidirectional()) && !a.isPresentRightCardinality()) {
      Log.error(
              String.format("0xC4A38 Association `%s` has right navigation arrow (->), but no right cardinality.", a),
              a.get_SourcePositionStart());
    }
  }
}
