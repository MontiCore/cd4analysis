package de.monticore.umlcd4a.cocos.ebnf;

import de.monticore.cocos.CoCoLog;
import de.monticore.umlcd4a._ast.ASTCDAssociation;
import de.monticore.umlcd4a._ast.ASTCardinality;
import de.monticore.umlcd4a._cocos.CD4AnalysisASTCDAssociationCoCo;
import de.monticore.umlcd4a.cocos.CD4ACoCoHelper;

/**
 * Checks that the cardinality of compositions is not larger than one. Note that
 * we expect all compositions to have navigation direction "->", "--" or "<->"
 * where the composit is always on the left side. So this CoCo only checks the
 * cardinality of the left side.
 *
 * @author Robert Heim
 */
public class CompositionCardinalityValid implements
    CD4AnalysisASTCDAssociationCoCo {
  
  public static final String ERROR_CODE = "0xC4A18";
  
  public static final String ERROR_MSG_FORMAT = "The composition %s has an invalid cardinality %s larger than one.";
  
  @Override
  public void check(ASTCDAssociation assoc) {
    if (assoc.isComposition()) {
      ASTCardinality cardinality = null;
      if (assoc.getLeftCardinality().isPresent()) {
        cardinality = assoc.getLeftCardinality().get();
      }
      else {
        // default cardinality is 1 for compositions.
        cardinality = ASTCardinality.getBuilder().one(true).build();
      }
      
      boolean isCardinalityValid = cardinality.isOne() | cardinality.isOptional();
      
      if (!isCardinalityValid) {
        CoCoLog.error(ERROR_CODE,
            String.format(ERROR_MSG_FORMAT,
                CD4ACoCoHelper.printAssociation(assoc),
                CD4ACoCoHelper.printCardinality(cardinality)),
            assoc.get_SourcePositionStart());
      }
    }
  }
}
