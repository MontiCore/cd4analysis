/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcdassociation.symboltable;

import static org.junit.jupiter.api.Assertions.*;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cdassociation.CDAssociationMill;
import de.monticore.cdassociation._symboltable.CDAssociationSymbol;
import de.monticore.cdassociation._symboltable.CDRoleSymbol;
import de.monticore.cdassociation._symboltable.deser.CDCardinalityDeSer;
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
import org.junit.jupiter.api.Test;

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
  
  /**
   * Serializing the same model twice (run parse + symbol table creation again) should
   * yield the same output.
   */
  @Test
  public void serializationIsDeterministicTest() {
    // every association of this model is explicitly named
    String artifact = "cdassociation/symboltable/SerializationNamedCD.cd";

    String first = parseAndSerialize(artifact);

    // clear the symbol table in between both runs
    ITestCDAssociationGlobalScope gs = TestCDAssociationMill.globalScope();
    gs.clear();
    gs.setSymbolPath(new MCPath(Paths.get(PATH)));
    BuiltInTypes.addBuiltInTypes(gs);
    assertTrue(gs.getSubScopes().isEmpty());

    String second = parseAndSerialize(artifact);

    assertEquals(first, second);
    assertTrue(first.contains("\"association\":\"de.monticore.cdassociation.symboltable."
        + "SerializationNamedCD.namedAssoc\""), first);
    // no warning about unnamed associations
    assertTrue(Log.getFindings().isEmpty(), getJoinedErrors());
  }

  protected String parseAndSerialize(String artifact) {
    ASTCDCompilationUnit ast = parseModel(artifact);
    afterParseTrafo(ast);

    ITestCDAssociationArtifactScope artifactScope = createSymTab(ast);
    completeSymTab(ast);

    String serialized = new TestCDAssociationSymbols2Json().serialize(artifactScope);
    assertNotNull(serialized);
    assertNotEquals("", serialized);
    return serialized;
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
    Optional<CDTypeSymbol> a = gs.resolveCDType(
        "de.monticore.cdassociation.symboltable.SerializationCD.A");
    assertTrue(a.isPresent());
    Optional<CDRoleSymbol> a_role1 = gs.resolveCDRole(
        "de.monticore.cdassociation.symboltable.SerializationCD.A.b");
    assertTrue(a_role1.isPresent());
    Optional<CDRoleSymbol> a_role2 = gs.resolveCDRole(
        "de.monticore.cdassociation.symboltable.SerializationCD.A.myB");
    assertTrue(a_role2.isPresent());
    Optional<CDRoleSymbol> a_role3 = gs.resolveCDRole(
        "de.monticore.cdassociation.symboltable.SerializationCD.A.d");
    assertTrue(a_role3.isPresent());
    
    // resolve for class B
    Optional<CDTypeSymbol> b = gs.resolveCDType(
        "de.monticore.cdassociation.symboltable.SerializationCD.B");
    assertTrue(b.isPresent());
    Optional<CDRoleSymbol> b_role1 = gs.resolveCDRole(
        "de.monticore.cdassociation.symboltable.SerializationCD.B.a");
    assertTrue(b_role1.isPresent());
    Optional<CDRoleSymbol> b_role2 = gs.resolveCDRole(
        "de.monticore.cdassociation.symboltable.SerializationCD.B.myA");
    assertTrue(b_role2.isPresent());
    Optional<CDRoleSymbol> b_role3 = gs.resolveCDRole(
        "de.monticore.cdassociation.symboltable.SerializationCD.B.d");
    assertTrue(b_role3.isPresent());
    
    // resolve for class D
    Optional<CDTypeSymbol> d = gs.resolveCDType(
        "de.monticore.cdassociation.symboltable.SerializationCD.D");
    assertTrue(d.isPresent());
    Optional<CDRoleSymbol> d_role1 = gs.resolveCDRole(
        "de.monticore.cdassociation.symboltable.SerializationCD.D.a");
    assertTrue(d_role1.isPresent());
    Optional<CDRoleSymbol> d_role2 = gs.resolveCDRole(
        "de.monticore.cdassociation.symboltable.SerializationCD.D.directWithRole");
    assertTrue(d_role2.isPresent());
    
    // resolve for assoc namedAssoc
    Optional<CDAssociationSymbol> assoc1 = gs.resolveCDAssociation(
        "de.monticore.cdassociation.symboltable.SerializationCD.namedAssoc");
    assertTrue(assoc1.isPresent());
    
    // resolve for assoc namedAssocWithRoles
    Optional<CDAssociationSymbol> assoc2 = gs.resolveCDAssociation(
        "de.monticore.cdassociation.symboltable.SerializationCD.namedAssocWithRoles");
    assertTrue(assoc2.isPresent());
  }
  
  /** Checks new cardinalities that are not [*], [1], [0..1], [1..*] */
  @Test
  public void testCreateCardinalityFromString() {
    // Single literals other than "*" or "1"
    assertEquals("[2]", CDAssociationMill.prettyPrint(new CDCardinalityDeSer().createFromString(
        "[2]"), false));
    
    // Finite ranges besides "[0..1]"
    assertEquals("[13..20]", CDAssociationMill.prettyPrint(new CDCardinalityDeSer()
        .createFromString("[13..20]"), false));
    
    // Infinite ranges not starting from "1"
    assertEquals("[9..*]", CDAssociationMill.prettyPrint(new CDCardinalityDeSer().createFromString(
        "[9..*]"), false));
  }
  
}
