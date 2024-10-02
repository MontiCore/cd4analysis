/* (c) https://github.com/MontiCore/monticore */
package de.monticore.stdefinition;

import static org.junit.Assert.fail;

import de.monticore.cd.TestBasis;
import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4code.trafo.CD4CodeAfterParseTrafo;
import de.monticore.cd4code.typescalculator.FullSynthesizeFromCD4Code;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.io.paths.MCPath;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.stdefinition._cocos.STDefinitionCoCoChecker;
import de.monticore.stdefinition._parser.STDefinitionParser;
import de.monticore.stdefinition._prettyprint.STDefinitionFullPrettyPrinter;
import de.monticore.stdefinition._symboltable.ISTDefinitionGlobalScope;
import de.monticore.stdefinition._symboltable.STDefinitionSymbolTableCompleter;
import de.monticore.stdefinition._symboltable.STDefinitionSymbols2Json;
import de.monticore.stdefinition._visitor.STDefinitionTraverser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;

public class STDefinitionTestBasis extends TestBasis {
  protected STDefinitionParser parser;
  protected STDefinitionFullPrettyPrinter printer;
  protected STDefinitionSymbols2Json symbols2Json;
  protected STDefinitionCoCoChecker coCoChecker;

  @BeforeEach
  public void initObjects() {
    LogStub.init();
    Log.enableFailQuick(false);
    STDefinitionMill.reset();
    STDefinitionMill.init();
    parser = STDefinitionMill.parser();

    ISTDefinitionGlobalScope globalScope = STDefinitionMill.globalScope();
    globalScope.clear();
    globalScope.setSymbolPath(new MCPath(Paths.get(PATH)));
    BuiltInTypes.addBuiltInTypes(globalScope);

    printer = new STDefinitionFullPrettyPrinter(new IndentPrinter(), true);
    symbols2Json = new STDefinitionSymbols2Json();
    coCoChecker = new STDefinitionCoCoChecker();
  }

  protected ASTCDCompilationUnit parse(String filePath) {
    Optional<ASTCDCompilationUnit> astcdCompilationUnit = Optional.empty();
    try {
      astcdCompilationUnit = parser.parse(getFilePath(filePath));
    } catch (IOException e) {
      fail("Exception during parsing");
    }
    checkNullAndPresence(parser, astcdCompilationUnit);
    final ASTCDCompilationUnit node = astcdCompilationUnit.get();

    // Trafos after parsing
    new CD4CodeAfterParseTrafo().transform(node);
    return node;
  }

  protected void prepareST(ASTCDCompilationUnit node) {
    // First pass for symbol table
    STDefinitionMill.scopesGenitorDelegator().createFromAST(node);
    checkLogError();

    // Second pass for symbol table
    STDefinitionTraverser traverser = STDefinitionMill.traverser();
    traverser.add4STDefinition(
        new STDefinitionSymbolTableCompleter(new FullSynthesizeFromCD4Code()));
    node.accept(traverser);
    checkLogError();
  }
}
