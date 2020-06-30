/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4code.prettyprint;

import de.monticore.cd.TestBasis;
import de.monticore.cd4analysis._symboltable.CD4AnalysisGlobalScope;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._parser.CD4CodeParser;
import de.monticore.cd4code._symboltable.CD4CodeGlobalScope;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCreatorDelegator;
import de.monticore.cd4code.cocos.CD4CodeCoCosDelegator;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.io.paths.ModelPath;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

public class CD4CodePrettyPrinterTest extends TestBasis {
  CD4CodeParser p = new CD4CodeParser();
  CD4CodePrettyPrinter printer = new CD4CodePrettyPrinter();

  @Ignore
  @Test
  public void completeModel() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parse(getFilePath("cd4code/parser/MyLife2.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);

    String output = printer.prettyprint(astcdCompilationUnit.get());

    final Optional<ASTCDCompilationUnit> astcdCompilationUnitReParsed = p.parse_StringCDCompilationUnit(output);
    checkNullAndPresence(p, astcdCompilationUnitReParsed);

    final ASTCDCompilationUnit node = astcdCompilationUnitReParsed.get();

    CD4CodeGlobalScope globalScope = CD4CodeMill
        .cD4CodeGlobalScopeBuilder()
        .setModelPath(new ModelPath(Paths.get(PATH)))
        .setModelFileExtension(CD4AnalysisGlobalScope.EXTENSION)
        .build();
    final CD4CodeSymbolTableCreatorDelegator symbolTableCreator = CD4CodeMill
        .cD4CodeSymbolTableCreatorDelegatorBuilder()
        .setGlobalScope(globalScope)
        .build();

    symbolTableCreator.createFromAST(node);
    checkLogError();

    new CD4CodeCoCosDelegator().getCheckerForAllCoCos().checkAll(node);
  }
}
