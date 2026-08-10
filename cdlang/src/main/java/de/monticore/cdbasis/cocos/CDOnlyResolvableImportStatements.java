package de.monticore.cdbasis.cocos;

import de.monticore.cdbasis.CDBasisMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDTargetImportStatement;
import de.monticore.cdbasis._cocos.CDBasisASTCDTargetImportStatementCoCo;
import de.monticore.cdbasis._symboltable.ICDBasisScope;
import de.monticore.grammar.MCGrammarSymbolTableHelper;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.types.mcbasictypes._cocos.MCBasicTypesASTMCImportStatementCoCo;
import de.se_rwth.commons.logging.Log;
import java.util.Optional;

public class CDOnlyResolvableImportStatements
    implements CDBasisASTCDTargetImportStatementCoCo, MCBasicTypesASTMCImportStatementCoCo {

  private Optional<TypeSymbol> resolve(String qName) {
    return CDBasisMill.globalScope().resolveType(qName);
  }

  @Override
  public void check(ASTCDTargetImportStatement node) {
    String qName = node.getMCQualifiedName().getQName();
    if (resolve(qName).isEmpty()) {
      Log.error("0xCDC40: Unresolved target import statement: " + qName);
    }
  }

  @Override
  public void check(ASTMCImportStatement node) {
    String qName = node.getMCQualifiedName().getQName();
    if (resolve(qName).isEmpty()) {
      Log.error("0xCDC41: Unresolved mc import statement: " + qName);
    }
  }
}