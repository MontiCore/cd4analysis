/* (c) https://github.com/MontiCore/monticore */
package de.monticore.stdefinition.symboltable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.stdefinition.STDefinitionTestBasis;
import de.monticore.stdefinition._symboltable.ISTDefinitionArtifactScope;
import java.io.IOException;
import java.util.Optional;
import org.junit.jupiter.api.Test;

public class STDefinitionSymbolTableCompleterTest extends STDefinitionTestBasis {

  // TypeVariables are moved into their respective scopes
  @Test
  public void testMovedTypeVariablesIntoCorrectScopes() throws IOException {
    final Optional<ASTCDCompilationUnit> optAST =
        parser.parse(getFilePath("stdefinition/symboltable/Complete.cd"));
    assertTrue(optAST.isPresent());
    final ASTCDCompilationUnit ast = optAST.get();
    prepareST(ast);
    checkLogError();
    ISTDefinitionArtifactScope as = (ISTDefinitionArtifactScope) ast.getEnclosingScope();
    assertEquals(0, as.getTypeVarSymbols().size());
    assertEquals(0, as.resolveFunction("f").get().getSpannedScope().getTypeVarSymbols().size());
    assertEquals(
        1, as.resolveFunction("getTarget").get().getSpannedScope().getTypeVarSymbols().size());
    assertEquals(
        2, as.resolveFunction("p.getMapString").get().getSpannedScope().getTypeVarSymbols().size());

    checkLogError();
  }
}
