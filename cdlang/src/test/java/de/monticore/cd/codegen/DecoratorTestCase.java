/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen;

import static org.junit.Assert.fail;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._parser.CD4CodeParser;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cd4code._symboltable.ICD4CodeGlobalScope;
import de.monticore.cd4code.trafo.CD4CodeAfterParseTrafo;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.se_rwth.commons.logging.Log;
import java.io.IOException;
import java.util.Optional;
import org.junit.Before;

public abstract class DecoratorTestCase {

  private static final String MODEL_PATH = "src/test/resources/";

  @Before
  public void setUpDecoratorTestCase() {
    Log.init();
    Log.enableFailQuick(false);
    CD4CodeMill.reset();
    CD4CodeMill.init();
    ICD4CodeGlobalScope globalScope = CD4CodeMill.globalScope();
    BuiltInTypes.addBuiltInTypes(globalScope);
    // globalScope.setModelPath(new ModelPath(Paths.get(MODEL_PATH)));
  }

  public ASTCDCompilationUnit parse(String... names) {
    String qualifiedName = String.join("/", names);

    CD4CodeParser parser = CD4CodeMill.parser();
    Optional<ASTCDCompilationUnit> ast = null;
    try {
      ast = parser.parse(MODEL_PATH + qualifiedName + ".cd");
    } catch (IOException e) {
      fail(String.format("Failed to load model '%s'", qualifiedName));
    }
    if (!ast.isPresent()) {
      fail(String.format("Failed to load model '%s'", qualifiedName));
    }

    ASTCDCompilationUnit comp = ast.get();
    new CD4CodeAfterParseTrafo().transform(ast.get());

    ICD4CodeArtifactScope scope = CD4CodeMill.scopesGenitorDelegator().createFromAST(comp);
    comp.getEnclosingScope().setAstNode(comp);
    for (ASTMCImportStatement imp : comp.getMCImportStatementList()) {
      if (!CD4CodeMill.globalScope().resolveDiagram(imp.getQName()).isPresent()) {
        parse(
            imp.getMCQualifiedName()
                .getPartsList()
                .toArray(new String[imp.getMCQualifiedName().sizeParts()]));
      }
    }
    return comp;
  }
}
