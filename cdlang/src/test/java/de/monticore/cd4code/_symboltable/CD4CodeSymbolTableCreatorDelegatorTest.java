/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4code._symboltable;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code.CD4CodeTestBasis;
import de.monticore.cd4code.trafo.CD4CodeAfterParseTrafo;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import java.io.IOException;
import java.util.Optional;
import org.junit.Test;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class CD4CodeSymbolTableCreatorDelegatorTest extends CD4CodeTestBasis {

  @Test
  public void completeModel() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit =
        p.parse(getFilePath("cd4code/parser/Complete.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);
    final ASTCDCompilationUnit node = astcdCompilationUnit.get();
    new CD4CodeAfterParseTrafo().transform(node);

    CD4CodeMill.scopesGenitorDelegator().createFromAST(node);
    checkLogError();

    cd4CodeCoCos.getCheckerForAllCoCos().checkAll(node);
  }
}
