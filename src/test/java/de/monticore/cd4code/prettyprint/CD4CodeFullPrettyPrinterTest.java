/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4code.prettyprint;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code.CD4CodeTestBasis;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cd4code.cocos.CD4CodeCoCosDelegator;
import de.monticore.cd4code.trafo.CD4CodeAfterParseTrafo;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import de.monticore.symboltable.ImportStatement;
import org.junit.Test;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class CD4CodeFullPrettyPrinterTest extends CD4CodeTestBasis {

  @Test
  public void completeModel() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit =
        p.parse(getFilePath("cd4code/parser/MyLife2.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);
    final ASTCDCompilationUnit node = astcdCompilationUnit.get();
    new CD4CodeAfterParseTrafo().transform(node);

    String output = printer.prettyprint(astcdCompilationUnit.get());

    final Optional<ASTCDCompilationUnit> astcdCompilationUnitReParsed = p.parse_String(output);
    checkNullAndPresence(p, astcdCompilationUnitReParsed);

    final ASTCDCompilationUnit nodeReparsed = astcdCompilationUnitReParsed.get();

    new CD4CodeAfterParseTrafo().transform(nodeReparsed);
    createSymTabWithImports(nodeReparsed);
    nodeReparsed.accept(new CD4CodeSymbolTableCompleter(nodeReparsed).getTraverser());
    checkLogError();

    new CD4CodeCoCosDelegator().getCheckerForAllCoCos().checkAll(nodeReparsed);
  }

  protected void createSymTabWithImports(ASTCDCompilationUnit ast) {
    ICD4CodeArtifactScope as = CD4CodeMill.scopesGenitorDelegator().createFromAST(ast);
    List<ImportStatement> imports = ast.getMCImportStatementList().stream()
      .map(i -> new ImportStatement(i.getMCQualifiedName().getQName(), i.isStar()))
      .collect(Collectors.toList());
    as.setImportsList(imports);
  }
}
