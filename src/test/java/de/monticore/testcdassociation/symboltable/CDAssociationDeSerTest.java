/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcdassociation.symboltable;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotEquals;

import de.monticore.cdassociation._symboltable.CDAssociationSymbol;
import de.monticore.cdassociation._symboltable.CDRoleSymbol;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.io.paths.MCPath;
import de.monticore.testcdassociation.CDAssociationTestBasis;
import de.monticore.testcdassociation.TestCDAssociationMill;
import de.monticore.testcdassociation._symboltable.ITestCDAssociationArtifactScope;
import de.monticore.testcdassociation._symboltable.ITestCDAssociationGlobalScope;
import de.monticore.testcdassociation._symboltable.TestCDAssociationSymbols2Json;
import de.se_rwth.commons.logging.Log;
import java.nio.file.Paths;
import java.util.Optional;
import org.junit.Test;

public class CDAssociationDeSerTest extends CDAssociationTestBasis {

  private static final String SYMBOL_PATH = "src/test/resources/";

  @Test
  public void serializationTest() {
    String artifact = "cdassociation/symboltable/SerializationCD.cd";
    ASTCDCompilationUnit ast = parseModel(artifact);
    afterParseTrafo(ast);

    ITestCDAssociationArtifactScope artifactScope = createSymTab(ast);
    completeSymTab(ast);

    // store symtab
    TestCDAssociationSymbols2Json symbols2Json = new TestCDAssociationSymbols2Json();
    String serialized = symbols2Json.serialize(artifactScope);
    assertNotNull(serialized);
    assertNotEquals("", serialized);

    // check for contents
    assertTrue(serialized.contains("\"name\":\"A\""));
    assertTrue(serialized.contains("\"name\":\"B\""));
    assertTrue(serialized.contains("\"name\":\"namedAssoc\""));
    assertTrue(serialized.contains("\"name\":\"namedAssocWithRoles\""));
    assertTrue(serialized.contains("\"name\":\"myA\""));
    assertTrue(serialized.contains("\"name\":\"myB\""));
    assertTrue(serialized.contains("\"name\":\"D\""));
    assertTrue(serialized.contains("\"name\":\"directWithRole\""));

    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void deserializationTest() {
    ITestCDAssociationGlobalScope gs = TestCDAssociationMill.globalScope();
    gs.clear();
    gs.setSymbolPath(new MCPath(Paths.get(SYMBOL_PATH)));
    assertTrue(gs.getSubScopes().isEmpty());
    gs.loadFileForModelName("de.monticore.cdassociation.symboltable.SerializationCD");
    assertEquals(1, gs.getSubScopes().size());

    // resolve for class A
    Optional<CDTypeSymbol> a =
        gs.resolveCDType("de.monticore.cdassociation.symboltable.SerializationCD.A");
    assertTrue(a.isPresent());
    Optional<CDRoleSymbol> a_role1 =
        gs.resolveCDRole("de.monticore.cdassociation.symboltable.SerializationCD.A.b");
    assertTrue(a_role1.isPresent());
    Optional<CDRoleSymbol> a_role2 =
        gs.resolveCDRole("de.monticore.cdassociation.symboltable.SerializationCD.A.myB");
    assertTrue(a_role2.isPresent());
    Optional<CDRoleSymbol> a_role3 =
        gs.resolveCDRole("de.monticore.cdassociation.symboltable.SerializationCD.A.d");
    assertTrue(a_role3.isPresent());

    // resolve for class B
    Optional<CDTypeSymbol> b =
        gs.resolveCDType("de.monticore.cdassociation.symboltable.SerializationCD.B");
    assertTrue(b.isPresent());
    Optional<CDRoleSymbol> b_role1 =
        gs.resolveCDRole("de.monticore.cdassociation.symboltable.SerializationCD.B.a");
    assertTrue(b_role1.isPresent());
    Optional<CDRoleSymbol> b_role2 =
        gs.resolveCDRole("de.monticore.cdassociation.symboltable.SerializationCD.B.myA");
    assertTrue(b_role2.isPresent());
    Optional<CDRoleSymbol> b_role3 =
        gs.resolveCDRole("de.monticore.cdassociation.symboltable.SerializationCD.B.d");
    assertTrue(b_role3.isPresent());

    // resolve for class D
    Optional<CDTypeSymbol> d =
        gs.resolveCDType("de.monticore.cdassociation.symboltable.SerializationCD.D");
    assertTrue(d.isPresent());
    Optional<CDRoleSymbol> d_role1 =
        gs.resolveCDRole("de.monticore.cdassociation.symboltable.SerializationCD.D.a");
    assertTrue(d_role1.isPresent());
    Optional<CDRoleSymbol> d_role2 =
        gs.resolveCDRole("de.monticore.cdassociation.symboltable.SerializationCD.D.directWithRole");
    assertTrue(d_role2.isPresent());

    // resolve for assoc namedAssoc
    Optional<CDAssociationSymbol> assoc1 =
        gs.resolveCDAssociation(
            "de.monticore.cdassociation.symboltable.SerializationCD.namedAssoc");
    assertTrue(assoc1.isPresent());

    // resolve for assoc namedAssocWithRoles
    Optional<CDAssociationSymbol> assoc2 =
        gs.resolveCDAssociation(
            "de.monticore.cdassociation.symboltable.SerializationCD.namedAssocWithRoles");
    assertTrue(assoc2.isPresent());
  }
}
