/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdbasis.cocos;

import de.monticore.cdbasis.CDBasisMill;
import de.monticore.cdbasis._ast.ASTCDTargetImportStatement;
import de.monticore.cdbasis._cocos.CDBasisASTCDTargetImportStatementCoCo;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.types.mcbasictypes._cocos.MCBasicTypesASTMCImportStatementCoCo;
import de.se_rwth.commons.logging.Log;

public class CDOnlyResolvableImportStatements implements CDBasisASTCDTargetImportStatementCoCo,
    MCBasicTypesASTMCImportStatementCoCo {
  
  @Override
  public void check(ASTCDTargetImportStatement node) {
    if (node.isStar())
      return; // TODO: Star imports can not be checked at the moment
    String qName = node.getMCQualifiedName().getQName();
    if (CDBasisMill.globalScope().resolveTypeMany(qName).isEmpty()) {
      Log.warn("0xCDC40: Could not resolve target import ` " + qName + "` within the symbol path.",
          node.get_SourcePositionStart(), node.get_SourcePositionEnd());
    }
  }
  
  @Override
  public void check(ASTMCImportStatement node) {
    if (node.isStar())
      return; // TODO: Star imports can not be checked at the moment
    String qName = node.getMCQualifiedName().getQName();
    if (CDBasisMill.globalScope().resolveTypeMany(qName).isEmpty()) {
      Log.error("0xCDC41: Could not resolve import `" + qName + "` within the symbol path.", node
          .get_SourcePositionStart(), node.get_SourcePositionEnd());
    }
  }
  
}
