/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4code._symboltable;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code.CD4CodeTestBasis;
import de.monticore.cd4code.trafo.CD4CodeAfterParseTrafo;
import de.monticore.cd4codebasis._symboltable.CDMethodSignatureSymbol;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.io.paths.MCPath;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.*;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class CD4CodeDeSerTest extends CD4CodeTestBasis {
  @Test
  public void completeModel() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parse(getFilePath("cd4code/parser/Complete.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);
    final ASTCDCompilationUnit node = astcdCompilationUnit.get();
    new CD4CodeAfterParseTrafo().transform(node);

    final ICD4CodeArtifactScope scope = CD4CodeMill.scopesGenitorDelegator().createFromAST(node);
    node.accept(new CD4CodeSymbolTableCompleter(node).getTraverser());

    final String serializedST = symbols2Json.serialize(scope);
    final ICD4CodeArtifactScope deserialize = getGlobalScopeForDeserialization(serializedST);

    final Optional<CDTypeSymbol> b = deserialize.resolveCDType("B");
    final Optional<CDMethodSignatureSymbol> cdMethodSignatureSymbol = b.get()
        .getMethodSignatureList("getX").stream().distinct().findAny();

    assertTrue(cdMethodSignatureSymbol.isPresent());
    assertFalse(cdMethodSignatureSymbol.get().isIsConstructor());
    assertEquals("bs", cdMethodSignatureSymbol.get().getParameterList().get(1).getName());
  }

  @Test
  public void storeModel() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parse(getFilePath("cd4code/parser/Complete.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);

    final ASTCDCompilationUnit node = astcdCompilationUnit.get();
    new CD4CodeAfterParseTrafo().transform(node);
    final ICD4CodeArtifactScope scope = CD4CodeMill.scopesGenitorDelegator().createFromAST(node);
    node.accept(new CD4CodeSymbolTableCompleter(node).getTraverser());

    final Optional<CDTypeSymbol> b = scope.resolveCDType("B");
    final Optional<CDMethodSignatureSymbol> getXMethodSymbol = b.get()
        .getMethodSignatureList("getX").stream().distinct().findAny();

    assertTrue(getXMethodSymbol.isPresent());
    assertEquals("de.monticore.cd4code.parser.B.getX", getXMethodSymbol.get().getFullName());

    final String path = getTmpFilePath(scope.getName() + ".cdsym");
    symbols2Json.store(scope, path);
    final ICD4CodeArtifactScope deserialize = symbols2Json.load(Paths.get(path).toUri().toURL());
    addGlobalScopeForDeserialization(deserialize);

    final Optional<CDTypeSymbol> deserializedB = deserialize.resolveCDType("B");
    final Optional<CDMethodSignatureSymbol> cdMethodSignatureSymbol = deserializedB.get()
        .getMethodSignatureList("getX").stream().distinct().findAny();

    assertTrue(cdMethodSignatureSymbol.isPresent());
    assertFalse(cdMethodSignatureSymbol.get().isIsConstructor());
    assertEquals("bs", cdMethodSignatureSymbol.get().getParameterList().get(1).getName());

    assertEquals(scope.getRealPackageName(), deserialize.getRealPackageName());
    assertEquals(getXMethodSymbol.get().getFullName(), cdMethodSignatureSymbol.get().getFullName());
  }

  public ICD4CodeArtifactScope getGlobalScopeForDeserialization(String serializedST) {
    final ICD4CodeArtifactScope deserialize = symbols2Json.deserialize(serializedST);
    addGlobalScopeForDeserialization(deserialize);
    return deserialize;
  }

  public ICD4CodeArtifactScope addGlobalScopeForDeserialization(ICD4CodeArtifactScope deserialize) {
    // explicitly not using the mill for initializing a global scope
    final CD4CodeGlobalScope globalScopeForDeserialization = new CD4CodeGlobalScope();
    globalScopeForDeserialization.setSymbolPath(new MCPath(Paths.get(PATH)));
    BuiltInTypes.addBuiltInTypes(globalScopeForDeserialization);
    globalScopeForDeserialization.addSubScope(deserialize);
    return deserialize;
  }
}
