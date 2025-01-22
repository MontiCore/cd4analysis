/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symtabdefinition;

import static org.junit.Assert.fail;

import de.monticore.cd.TestBasis;
import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4code.trafo.CD4CodeAfterParseTrafo;
import de.monticore.cd4code.typescalculator.FullSynthesizeFromCD4Code;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.io.paths.MCPath;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symtabdefinition._cocos.SymTabDefinitionCoCoChecker;
import de.monticore.symtabdefinition._parser.SymTabDefinitionParser;
import de.monticore.symtabdefinition._prettyprint.SymTabDefinitionFullPrettyPrinter;
import de.monticore.symtabdefinition._symboltable.ISymTabDefinitionGlobalScope;
import de.monticore.symtabdefinition._symboltable.SymTabDefinitionSymbolTableCompleter;
import de.monticore.symtabdefinition._symboltable.SymTabDefinitionSymbols2Json;
import de.monticore.symtabdefinition._visitor.SymTabDefinitionTraverser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;

public class SymTabDefinitionTestBasis extends TestBasis {
  protected SymTabDefinitionParser parser;
  protected SymTabDefinitionFullPrettyPrinter printer;
  protected SymTabDefinitionSymbols2Json symbols2Json;
  protected SymTabDefinitionCoCoChecker coCoChecker;

  @BeforeEach
  public void initObjects() {
    LogStub.init();
    Log.enableFailQuick(false);
    SymTabDefinitionMill.reset();
    SymTabDefinitionMill.init();
    parser = SymTabDefinitionMill.parser();

    ISymTabDefinitionGlobalScope globalScope = SymTabDefinitionMill.globalScope();
    globalScope.clear();
    globalScope.setSymbolPath(new MCPath(Paths.get(PATH)));
    BuiltInTypes.addBuiltInTypes(globalScope);

    printer = new SymTabDefinitionFullPrettyPrinter(new IndentPrinter(), true);
    symbols2Json = new SymTabDefinitionSymbols2Json();
    coCoChecker = new SymTabDefinitionCoCoChecker();
  }

  protected ASTCDCompilationUnit parse(String filePath) {
    Optional<ASTCDCompilationUnit> astcdCompilationUnit = Optional.empty();
    try {
      astcdCompilationUnit = parser.parse(getFilePath(filePath));
    } catch (IOException e) {
      fail("Exception during parsing: " + e);
    }
    checkNullAndPresence(parser, astcdCompilationUnit);
    final ASTCDCompilationUnit node = astcdCompilationUnit.get();

    // Trafos after parsing
    new CD4CodeAfterParseTrafo().transform(node);
    return node;
  }

  protected void prepareST(ASTCDCompilationUnit node) {
    // First pass for symbol table
    SymTabDefinitionMill.scopesGenitorDelegator().createFromAST(node);
    checkLogError();

    // Second pass for symbol table
    SymTabDefinitionTraverser traverser = SymTabDefinitionMill.traverser();
    traverser.add4SymTabDefinition(
        new SymTabDefinitionSymbolTableCompleter(new FullSynthesizeFromCD4Code()));
    node.accept(traverser);
    checkLogError();
  }
}
