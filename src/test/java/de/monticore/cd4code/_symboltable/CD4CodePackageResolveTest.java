/*
 * (c) https://github.com/MontiCore/monticore
 */

/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4code._symboltable;

import de.monticore.cd.TestBasis;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._parser.CD4CodeParser;
import de.monticore.cd4code.cocos.CD4CodeCoCosDelegator;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.io.paths.ModelPath;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

public class CD4CodePackageResolveTest extends TestBasis {
  CD4CodeParser p = new CD4CodeParser();

  @Test
  public void completeModel() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parse(getFilePath("cd4code/parser/Packages.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);
    final ASTCDCompilationUnit node = astcdCompilationUnit.get();

    CD4CodeGlobalScope globalScope = CD4CodeMill
        .cD4CodeGlobalScopeBuilder()
        .setModelPath(new ModelPath(Paths.get(PATH)))
        .setModelFileExtension(CD4CodeGlobalScope.EXTENSION)
        .build();
    final CD4CodeSymbolTableCreatorDelegator symbolTableCreator = CD4CodeMill
        .cD4CodeSymbolTableCreatorDelegatorBuilder()
        .setGlobalScope(globalScope)
        .build();

    globalScope.addBuiltInTypes();

    symbolTableCreator.createFromAST(node);
    checkLogError();

    new CD4CodeCoCosDelegator().getCheckerForAllCoCos().checkAll(node);
  }
}
