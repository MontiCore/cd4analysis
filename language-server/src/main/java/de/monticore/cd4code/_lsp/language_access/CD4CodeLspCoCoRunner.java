/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4code._lsp.language_access;

import de.mclsg.lsp.document_management.DocumentManager;
import de.monticore.cd4code._cocos.CD4CodeCoCoChecker;
import de.monticore.cd4code.cocos.CD4CodeCoCosDelegator;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;

public class CD4CodeLspCoCoRunner extends CD4CodeLspCoCoRunnerTOP {
  
  private CD4CodeCoCoChecker checker;
  
  public CD4CodeLspCoCoRunner(DocumentManager documentManager) {
    super(documentManager);
    CD4CodeCoCosDelegator cd4CodeCoCos = new CD4CodeCoCosDelegator();
    checker = cd4CodeCoCos.getCheckerForAllCoCos();
  }
  
  @Override
  public boolean needsSymbols() {
    return true;
  }
  
  @Override
  public void runAllCoCos(ASTCDCompilationUnit ast) {
    checker.checkAll(ast);
  }
  
}
