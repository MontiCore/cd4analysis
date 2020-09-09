/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4code._symboltable;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code.CD4CodeTestBasis;
import de.monticore.cd4codebasis._symboltable.CDMethodSignatureSymbol;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.Names;
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

    final ICD4CodeArtifactScope scope = symbolTableCreator.createFromAST(node);

    final String serializedST = deSer.serialize(scope);
    final ICD4CodeArtifactScope deserialize = getGlobalScopeForDeserialization(serializedST);

    final Optional<CDMethodSignatureSymbol> cdMethodSignatureSymbol = deserialize.resolveCDMethodSignature("B.getX");
    assertTrue(cdMethodSignatureSymbol.isPresent());
    assertFalse(cdMethodSignatureSymbol.get().isIsConstructor());
    assertEquals("bs", cdMethodSignatureSymbol.get().getParameterList().get(1).getName());
  }

  @Test
  public void storeModel() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parse(getFilePath("cd4code/parser/Complete.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);

    final ASTCDCompilationUnit node = astcdCompilationUnit.get();
    final ICD4CodeArtifactScope scope = symbolTableCreator.createFromAST(node);
    final Optional<CDMethodSignatureSymbol> getXMethodSymbol = scope.resolveCDMethodSignature("B.getX");
    assertTrue(getXMethodSymbol.isPresent());
    assertEquals("de.monticore.cd4code.parser.B.getX", getXMethodSymbol.get().getFullName());

    final String path = getTmpAbsolutePath();
    deSer.store(scope, Paths.get(path));
    final ICD4CodeArtifactScope deserialize = deSer.load(Paths.get(path, Names.getPathFromPackage(scope.getRealPackageName()), scope.getName() + ".cdsym").toUri().toURL());
    addGlobalScopeForDeserialization(deserialize);

    final Optional<CDMethodSignatureSymbol> cdMethodSignatureSymbol = deserialize.resolveCDMethodSignature("B.getX");
    assertTrue(cdMethodSignatureSymbol.isPresent());
    assertFalse(cdMethodSignatureSymbol.get().isIsConstructor());
    assertEquals("bs", cdMethodSignatureSymbol.get().getParameterList().get(1).getName());

    assertEquals(scope.getRealPackageName(), deserialize.getRealPackageName());
    assertEquals(getXMethodSymbol.get().getFullName(), cdMethodSignatureSymbol.get().getFullName());
  }

  public ICD4CodeArtifactScope getGlobalScopeForDeserialization(String serializedST) {
    final ICD4CodeArtifactScope deserialize = deSer.deserialize(serializedST);
    addGlobalScopeForDeserialization(deserialize);
    return deserialize;
  }

  public ICD4CodeArtifactScope addGlobalScopeForDeserialization(ICD4CodeArtifactScope deserialize) {
    final ICD4CodeGlobalScope globalScopeForDeserialization = CD4CodeMill
        .cD4CodeGlobalScopeBuilder()
        .setModelPath(new ModelPath(Paths.get(PATH)))
        .setModelFileExtension(CD4CodeGlobalScope.EXTENSION)
        .addBuiltInTypes()
        .build();
    globalScopeForDeserialization.addSubScope(deserialize);
    return deserialize;
  }
}
