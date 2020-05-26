/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cd.cocos.ebnf;

import de.monticore.cd.cd4analysis.CD4AnalysisMill;
import de.monticore.cd.cd4analysis._ast.ASTCDAssociation;
import de.monticore.cd.cocos.CD4ACoCoHelper;
import de.monticore.cd.cd4analysis._ast.ASTCardinality;
import de.monticore.cd.cd4analysis._cocos.CD4AnalysisASTCDAssociationCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that the cardinality of compositions is not larger than one. Note that
 * we expect all compositions to have navigation direction "->", "--" or "<->"
 * where the composit is always on the left side. So this CoCo only checks the
 * cardinality of the left side.
 *
 */
public class CompositionCardinalityValid implements
    CD4AnalysisASTCDAssociationCoCo {
  
  @Override
  public void check(ASTCDAssociation assoc) {
    if (assoc.isComposition()) {
      ASTCardinality cardinality = null;
      if (assoc.isPresentLeftCardinality()) {
        cardinality = assoc.getLeftCardinality();
      }
      else {
        // default cardinality is 1 for compositions.
        cardinality = CD4AnalysisMill.cardinalityBuilder().setOne(true).build();
      }
      
      boolean isCardinalityValid = cardinality.isOne() | cardinality.isOptional();
      
      if (!isCardinalityValid) {
        Log.error(String.format(
            "0xC4A18 The composition %s has an invalid cardinality %s larger than one.",
            CD4ACoCoHelper.printAssociation(assoc),
            CD4ACoCoHelper.printCardinality(cardinality)),
            assoc.get_SourcePositionStart());
      }
    }
  }
}
