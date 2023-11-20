/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcd4codebasis._symboltable;

import static org.junit.Assert.*;

import com.google.common.collect.Lists;
import de.monticore.cd4codebasis._symboltable.CDMethodSignatureSymbol;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.io.paths.MCPath;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symboltable.ImportStatement;
import de.monticore.testcd4codebasis.CD4CodeBasisTestBasis;
import de.monticore.testcd4codebasis.TestCD4CodeBasisMill;
import de.se_rwth.commons.logging.Log;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.Test;

public class CD4CodeBasisDeSerTest extends CD4CodeBasisTestBasis {

  private static final String SYMBOL_PATH = "src/test/resources/";

  @Test
  public void serializationTest() {
    TestCD4CodeBasisMill.globalScope().setSymbolPath(new MCPath(Paths.get(SYMBOL_PATH)));
    String artifact = SYMBOL_PATH + "de/monticore/cd4codebasis/symboltable/SerializationCD.cd";
    ASTCDCompilationUnit ast = loadModel(artifact);
    ITestCD4CodeBasisArtifactScope artifactScope = createSymbolTableFromAST(ast);
    ast.accept(new TestCD4CodeBasisSymbolTableCompleter(ast).getTraverser());

    // store symtab
    TestCD4CodeBasisSymbols2Json symbols2Json = new TestCD4CodeBasisSymbols2Json();
    String serialized = symbols2Json.serialize(artifactScope);
    assertNotNull(serialized);
    assertNotEquals("", serialized);

    // check for contents
    assertTrue(serialized.contains("\"name\":\"A\""));
    assertTrue(serialized.contains("\"name\":\"C\""));
    assertTrue(serialized.contains("\"name\":\"SomeType\""));
    assertTrue(serialized.contains("\"name\":\"foo\""));
    assertTrue(serialized.contains("\"name\":\"bar\""));

    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void deserializationTest() {
    ITestCD4CodeBasisGlobalScope gs = TestCD4CodeBasisMill.globalScope();
    gs.clear();
    gs.setSymbolPath(new MCPath(Paths.get(SYMBOL_PATH)));
    assertTrue(gs.getSubScopes().isEmpty());
    gs.loadFileForModelName("de.monticore.cd4codebasis.symboltable.SerializationCD");
    assertEquals(1, gs.getSubScopes().size());

    // resolve for class A
    Optional<CDTypeSymbol> a =
        gs.resolveCDType("de.monticore.cd4codebasis.symboltable.SerializationCD.A");
    assertTrue(a.isPresent());
    List<CDMethodSignatureSymbol> a_constr =
        gs.resolveCDMethodSignatureMany(
            "de.monticore.cd4codebasis.symboltable.SerializationCD.A.A");
    assertEquals(2, a_constr.size());
    Optional<FieldSymbol> a_constr_param =
        gs.resolveField("de.monticore.cd4codebasis.symboltable.SerializationCD.A.A.st");
    assertTrue(a_constr_param.isPresent());
    Optional<CDMethodSignatureSymbol> a_meth =
        gs.resolveCDMethodSignature("de.monticore.cd4codebasis.symboltable.SerializationCD.A.foo");
    assertTrue(a_meth.isPresent());
    Optional<FieldSymbol> a_meth_param =
        gs.resolveField("de.monticore.cd4codebasis.symboltable.SerializationCD.A.foo.st");
    assertTrue(a_meth_param.isPresent());

    // resolve for class C
    Optional<CDTypeSymbol> c =
        gs.resolveCDType("de.monticore.cd4codebasis.symboltable.SerializationCD.b.C");
    assertTrue(c.isPresent());
    List<CDMethodSignatureSymbol> c_constr =
        gs.resolveCDMethodSignatureMany(
            "de.monticore.cd4codebasis.symboltable.SerializationCD.b.C.C");
    assertEquals(2, c_constr.size());
    Optional<FieldSymbol> c_constr_param =
        gs.resolveField("de.monticore.cd4codebasis.symboltable.SerializationCD.b.C.C.st");
    assertTrue(c_constr_param.isPresent());
    Optional<CDMethodSignatureSymbol> c_meth =
        gs.resolveCDMethodSignature(
            "de.monticore.cd4codebasis.symboltable.SerializationCD.b.C.bar");
    assertTrue(c_meth.isPresent());
    Optional<FieldSymbol> c_meth_param =
        gs.resolveField("de.monticore.cd4codebasis.symboltable.SerializationCD.b.C.bar.st");
    assertTrue(c_meth_param.isPresent());

    // resolve for class SomeType
    Optional<CDTypeSymbol> someType =
        gs.resolveCDType("de.monticore.cd4codebasis.symboltable.SerializationCD.SomeType");
    assertTrue(someType.isPresent());
  }

  private ASTCDCompilationUnit loadModel(String pathToArtifact) {
    try {
      return p.parse(Paths.get(pathToArtifact).toString()).orElseThrow(NoSuchElementException::new);
    } catch (IOException | NoSuchElementException e) {
      System.err.println("Loading artifact: " + pathToArtifact + " failed: " + e.getMessage());
      fail();
    }
    throw new IllegalStateException("Something went wrong..");
  }

  private ITestCD4CodeBasisArtifactScope createSymbolTableFromAST(ASTCDCompilationUnit ast) {
    ITestCD4CodeBasisArtifactScope as =
        TestCD4CodeBasisMill.scopesGenitorDelegator().createFromAST(ast);

    // set package
    as.setPackageName(ast.getMCPackageDeclaration().getMCQualifiedName().getQName());

    // add imports
    List<ImportStatement> imports = Lists.newArrayList();
    ast.getMCImportStatementList()
        .forEach(i -> imports.add(new ImportStatement(i.getQName(), i.isStar())));
    as.setImportsList(imports);

    return as;
  }
}
