/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symtabdefinition.symboltable;

import static org.junit.Assert.assertEquals;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.symtabdefinition.SymTabDefinitionTestBasis;
import de.monticore.symtabdefinition._symboltable.ISymTabDefinitionArtifactScope;
import java.io.IOException;
import org.junit.jupiter.api.Test;

public class SymTabDefinitionSymbolTableCompleterTest extends SymTabDefinitionTestBasis {

  // TypeVariables are moved into their respective scopes
  @Test
  public void testMovedTypeVariablesIntoCorrectScopes() throws IOException {
    final ASTCDCompilationUnit ast = parse("stdefinition/symboltable/Complete.cd");
    prepareST(ast);
    ISymTabDefinitionArtifactScope as = (ISymTabDefinitionArtifactScope) ast.getEnclosingScope();
    assertEquals(0, as.getTypeVarSymbols().size());
    assertEquals(0, as.resolveFunction("f").get().getSpannedScope().getTypeVarSymbols().size());
    assertEquals(
        1, as.resolveFunction("getTarget").get().getSpannedScope().getTypeVarSymbols().size());
    assertEquals(
        2, as.resolveFunction("p.getMapString").get().getSpannedScope().getTypeVarSymbols().size());

    checkLogError();
  }
}
