/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcdinterfaceandenum._symboltable;

import static org.junit.Assert.*;

import com.google.common.collect.LinkedListMultimap;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.io.paths.MCPath;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symboltable.ImportStatement;
import de.monticore.testcdinterfaceandenum.CDInterfaceAndEnumTestBasis;
import de.monticore.testcdinterfaceandenum.TestCDInterfaceAndEnumMill;
import de.se_rwth.commons.logging.Log;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.Test;

public class CDInterfaceAndEnumSTCompleterTest extends CDInterfaceAndEnumTestBasis {

  private static final String SYMBOL_PATH = "src/test/resources/";
  TestCDInterfaceAndEnumSymbols2Json symbols2Json;

  @Test
  public void genitorTest() {
    TestCDInterfaceAndEnumMill.globalScope().setSymbolPath(new MCPath(Paths.get(SYMBOL_PATH)));
    String artifact =
        SYMBOL_PATH + "de/monticore/cdinterfaceenum/symboltable/CorrectTypeUsagesEnumInterface.cd";
    ASTCDCompilationUnit ast = parseModel(artifact);
    ITestCDInterfaceAndEnumArtifactScope artifactScope = createSymTab(ast);
    addPkgAndImports(artifactScope, ast);
    completeSymTab(ast);

    assertEquals(3, artifactScope.getSubScopes().size());

    LinkedListMultimap<String, CDTypeSymbol> cdTypeSymbols = artifactScope.getCDTypeSymbols();
    assertEquals(3, cdTypeSymbols.size());
    assertTrue(cdTypeSymbols.containsKey("C"));
    assertTrue(cdTypeSymbols.containsKey("D"));
    assertTrue(cdTypeSymbols.containsKey("MyInterface"));

    assertEquals(1, cdTypeSymbols.get("D").size());
    CDTypeSymbol classD = cdTypeSymbols.get("D").get(0);

    List<FieldSymbol> dFields = classD.getFieldList();
    assertEquals(4, dFields.size());

    FieldSymbol cField = dFields.get(0);
    assertEquals("de.monticore.cdinterfaceandenum.symboltable.CorrectTypeUsagesEnumInterface.D.c", cField.getFullName());
    assertEquals("C", cField.getType().getTypeInfo().getName());

    FieldSymbol someImportedTypeField = dFields.get(1);
    assertEquals(
        "de.monticore.cdinterfaceandenum.symboltable.CorrectTypeUsagesEnumInterface.D.x", someImportedTypeField.getFullName());
    assertEquals("SomeImportedType", someImportedTypeField.getType().getTypeInfo().getName());

    FieldSymbol iField = dFields.get(2);
    assertEquals("de.monticore.cdinterfaceandenum.symboltable.CorrectTypeUsagesEnumInterface.D.i", iField.getFullName());
    assertEquals("MyOtherInterface", iField.getType().getTypeInfo().getName());

    FieldSymbol eField = dFields.get(3);
    assertEquals("de.monticore.cdinterfaceandenum.symboltable.CorrectTypeUsagesEnumInterface.D.e", eField.getFullName());
    assertEquals("MyEnum", eField.getType().getTypeInfo().getName());

    assertEquals(1, cdTypeSymbols.get("MyInterface").size());
    CDTypeSymbol myInterfaceSymbol = cdTypeSymbols.get("MyInterface").get(0);
    assertTrue(myInterfaceSymbol.isIsInterface());
    assertEquals(1, myInterfaceSymbol.getSuperTypesList().size());

    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void resolvingTest() {
    TestCDInterfaceAndEnumMill.globalScope().setSymbolPath(new MCPath(Paths.get(SYMBOL_PATH)));
    String artifact =
        SYMBOL_PATH + "de/monticore/cdinterfaceenum/symboltable/CorrectTypeUsagesEnumInterface.cd";
    ASTCDCompilationUnit ast = parseModel(artifact);
    ITestCDInterfaceAndEnumArtifactScope artifactScope = createSymTab(ast);
    addPkgAndImports(artifactScope, ast);
    completeSymTab(ast);

    List<TypeSymbol> resolvedTypes1 = artifactScope.resolveTypeMany("C");
    assertEquals(1, resolvedTypes1.size());

    List<TypeSymbol> resolvedTypes2 = artifactScope.resolveTypeMany("D");
    assertEquals(1, resolvedTypes2.size());

    List<TypeSymbol> resolvedTypes3 = artifactScope.resolveTypeMany("E");
    assertEquals(0, resolvedTypes3.size());

    List<TypeSymbol> resolvedTypes4 = artifactScope.resolveTypeMany("MyInterface");
    assertEquals(1, resolvedTypes4.size());

    assertEquals(1, resolvedTypes4.get(0).getSuperTypesList().size());
    assertEquals(
        "MyOtherInterface",
        resolvedTypes4.get(0).getSuperTypesList().get(0).getTypeInfo().getName());

    List<DiagramSymbol> resolvedDiagram =
        artifactScope.resolveDiagramMany("CorrectTypeUsagesEnumInterface");
    assertEquals(1, resolvedDiagram.size());

    TestCDInterfaceAndEnumMill.globalScope().addSubScope(artifactScope);
    assertSame(artifactScope, TestCDInterfaceAndEnumMill.globalScope().getSubScopes().get(0));

    String asPkg = "de.monticore.cdinterfaceandenum.symboltable.";
    List<TypeSymbol> resolvedTypesGS1 =
        TestCDInterfaceAndEnumMill.globalScope().resolveTypeMany(asPkg + "C");
    assertEquals(1, resolvedTypesGS1.size());

    List<TypeSymbol> resolvedTypesGS2 =
        TestCDInterfaceAndEnumMill.globalScope().resolveTypeMany(asPkg + "D");
    assertEquals(1, resolvedTypesGS2.size());

    List<TypeSymbol> resolvedTypesGS3 =
        TestCDInterfaceAndEnumMill.globalScope().resolveTypeMany(asPkg + "E");
    assertEquals(0, resolvedTypesGS3.size());

    List<TypeSymbol> resolvedTypesGS4 =
        TestCDInterfaceAndEnumMill.globalScope().resolveTypeMany(asPkg + "MyInterface");
    assertEquals(1, resolvedTypesGS4.size());

    List<DiagramSymbol> resolvedDiagramGS =
        TestCDInterfaceAndEnumMill.globalScope()
            .resolveDiagramMany(asPkg + "CorrectTypeUsagesEnumInterface");
    assertEquals(1, resolvedDiagramGS.size());

    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void serializationTest() {
    symbols2Json = new TestCDInterfaceAndEnumSymbols2Json();
    String artifact = SYMBOL_PATH + "de/monticore/cdinterfaceenum/symboltable/EnumAndInterface.cd";
    ASTCDCompilationUnit ast = parseModel(artifact);
    ITestCDInterfaceAndEnumArtifactScope artifactScope = createSymTab(ast);
    completeSymTab(ast);
    String serialized = symbols2Json.serialize(artifactScope);
    assertNotNull(serialized);
    assertNotEquals("", serialized);
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void symbolTableCompleterNoErrorTest() {
    TestCDInterfaceAndEnumMill.globalScope().setSymbolPath(new MCPath(Paths.get(SYMBOL_PATH)));
    String artifact =
        SYMBOL_PATH + "de/monticore/cdinterfaceenum/symboltable/CorrectTypeUsagesEnumInterface.cd";
    ASTCDCompilationUnit ast = parseModel(artifact);
    ITestCDInterfaceAndEnumArtifactScope artifactScope = createSymTab(ast);

    addPkgAndImports(artifactScope, ast);
    completeSymTab(ast);

    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void constantsTest() {
    String artifact = SYMBOL_PATH + "de/monticore/cdinterfaceenum/symboltable/EnumConstants.cd";
    ASTCDCompilationUnit ast = parseModel(artifact);

    ITestCDInterfaceAndEnumArtifactScope artifactScope = createSymTab(ast);
    completeSymTab(ast);

    Optional<FieldSymbol> idleSym = artifactScope.resolveField("DrivingState.IDLE");
    Optional<FieldSymbol> drivingSym = artifactScope.resolveField("DrivingState.DRIVING");

    assertTrue(idleSym.isPresent());
    assertTrue(drivingSym.isPresent());

    assertEquals("DrivingState", idleSym.get().getType().getTypeInfo().getName());
    assertEquals("DrivingState", drivingSym.get().getType().getTypeInfo().getName());
  }

  protected void addPkgAndImports(
      ITestCDInterfaceAndEnumArtifactScope artifactScope, ASTCDCompilationUnit ast) {
    artifactScope.setPackageName(ast.getMCPackageDeclaration().getMCQualifiedName().getQName());
    artifactScope.addAllImports(
        ast.getMCImportStatementList().stream()
            .map(i -> new ImportStatement(i.getQName(), i.isStar()))
            .collect(Collectors.toList()));
  }
}
