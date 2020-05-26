/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.cocos.mcg2ebnf;

import de.monticore.cd.cd4analysis._ast.ASTCDAssociation;
import de.monticore.cd.cocos.CD4ACoCoHelper;
import de.monticore.cd.cd4analysis._ast.ASTCDStereotype;
import de.monticore.cd.cd4analysis._cocos.CD4AnalysisASTCDAssociationCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Stereotypes are forbidding for Associations in the ebnf.
 *
 */
public class AssociationNoStereotypesCoCo implements CD4AnalysisASTCDAssociationCoCo {
  
  /**
   * @see de.monticore.cd._cocos.CD4AnalysisASTCDAssociationCoCo#check(de.monticore.cd._ast.ASTCDAssociation)
   */
  @Override
  public void check(ASTCDAssociation node) {
    if (node.isPresentStereotype()) {
      ASTCDStereotype stereoType = node.getStereotype();
      if (!stereoType.getValueList().isEmpty()) {
        Log.error(
            String.format("0xC4A71 Association %s may not have stereotypes.",
                CD4ACoCoHelper.printAssociation(node)),
            stereoType.get_SourcePositionStart());
      }
    }
  }
}
