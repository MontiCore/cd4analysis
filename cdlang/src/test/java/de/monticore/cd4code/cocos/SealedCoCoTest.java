/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4code.cocos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code.CD4CodeTestBasis;
import de.monticore.cd4code._prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cd4codebasis._ast.ASTCDInterface;
import de.monticore.cd4codebasis.cocos.ebnf.CDPermittedTypesAreSubtypes;
import de.monticore.cd4codebasis.cocos.ebnf.CDSealedModifierCombinations;
import de.monticore.cd4codebasis.cocos.ebnf.CDSealedOnlyOnClassesAndInterfaces;
import de.monticore.cd4codebasis.cocos.ebnf.CDSubtypesRespectRestriction;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.symboltable.ImportStatement;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.umlmodifier._prettyprint.UMLModifierPrettyPrinter;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the CoCos for sealed and non-sealed types and permits clauses.
 */
public class SealedCoCoTest extends CD4CodeTestBasis {
  
  protected static final String MODELS = "cd4code/cocos/sealed/";
  
  protected List<String> checkAndGetErrorCodes(String model) {
    ASTCDCompilationUnit ast = parse(MODELS + model);
    prepareST(ast);
    cd4CodeCoCos.getCheckerForAllCoCos().checkAll(ast);
    return errorCodes();
  }
  
  /**
   * Like {@link #checkAndGetErrorCodes}, but errors of the symbol table creation are part of the
   * result.
   */
  protected List<String> buildAndCheckAndGetErrorCodes(String model) {
    ASTCDCompilationUnit ast = parse(MODELS + model);
    CD4CodeMill.scopesGenitorDelegator().createFromAST(ast).addImports(new ImportStatement(
        "java.lang", true));
    ast.accept(new CD4CodeSymbolTableCompleter(ast).getTraverser());
    cd4CodeCoCos.getCheckerForAllCoCos().checkAll(ast);
    return errorCodes();
  }
  
  protected List<String> errorCodes() {
    return Log.getFindings().stream().filter(Finding::isError).map(f -> f.getMsg().substring(0, 7))
        .sorted().collect(Collectors.toList());
  }
  
  /**
   * The expected errors are checked by the tests, runs before the check of the test basis.
   */
  @AfterEach
  public void clearExpectedErrors() {
    Log.getFindings().clear();
  }
  
  @Test
  public void testValid() {
    assertEquals(List.of(), checkAndGetErrorCodes("SealedValid.cd"), getJoinedErrors());
  }
  
  @Test
  public void testUnresolvablePermittedTypes() {
    // reported by the completer and again by the type check, like for unresolvable supertypes;
    // CDPermittedTypesAreSubtypes skips them
    assertEquals(List.of("0xA0324", "0xA0324", "0xA0324", "0xA0324"), buildAndCheckAndGetErrorCodes(
        "SealedUnresolvablePermittedTypes.cd"), getJoinedErrors());
  }
  
  @Test
  public void testPermittedTypesAreSubtypes() {
    String code = CDPermittedTypesAreSubtypes.ERROR_CODE;
    assertEquals(List.of(code, code, code, CDPermittedTypesAreSubtypes.ERROR_CODE_DUPLICATE),
        checkAndGetErrorCodes("SealedPermittedTypesAreSubtypesInvalid.cd"), getJoinedErrors());
  }
  
  @Test
  public void testSubtypesRespectRestriction() {
    assertEquals(List.of(CDSubtypesRespectRestriction.ERROR_CODE_NOT_PERMITTED,
        CDSubtypesRespectRestriction.ERROR_CODE_NOT_PERMITTED,
        CDSubtypesRespectRestriction.ERROR_CODE_PERMITTED_MODIFIER,
        CDSubtypesRespectRestriction.ERROR_CODE_NON_SEALED_WITHOUT_RESTRICTION,
        CDSealedModifierCombinations.ERROR_CODE_SEALED_WITHOUT_PERMITS), checkAndGetErrorCodes(
            "SealedHierarchyInvalid.cd"), getJoinedErrors());
  }
  
  /**
   * The completer stores the permitted types as the subtype restriction of the symbol.
   */
  @Test
  public void testSubTypeRestrictionInSymbol() {
    ASTCDCompilationUnit ast = parse(MODELS + "SealedValid.cd");
    prepareST(ast);
    OOTypeSymbol vehicle = ast.getCDDefinition().getCDClassesList().get(0).getSymbol();
    assertTrue(vehicle.hasSubTypeRestriction());
    assertEquals(List.of("Car", "Truck", "Bike"), vehicle.getSubTypeRestrictionList().stream().map(
        t -> t.getTypeInfo().getName()).collect(Collectors.toList()));
    OOTypeSymbol car = ast.getCDDefinition().getCDClassesList().get(1).getSymbol();
    assertTrue(!car.hasSubTypeRestriction());
  }
  
  @Test
  public void testModifierCombinations() {
    assertEquals(List.of(CDSealedModifierCombinations.ERROR_CODE_EXCLUSIVE,
        CDSealedModifierCombinations.ERROR_CODE_EXCLUSIVE,
        CDSealedModifierCombinations.ERROR_CODE_ABSTRACT_FINAL,
        CDSealedModifierCombinations.ERROR_CODE_FINAL_INTERFACE,
        CDSealedModifierCombinations.ERROR_CODE_PERMITS_WITHOUT_SEALED,
        CDSealedModifierCombinations.ERROR_CODE_PERMITS_WITHOUT_SEALED), checkAndGetErrorCodes(
            "SealedModifierCombinationsInvalid.cd"), getJoinedErrors());
  }
  
  @Test
  public void testSealedOnlyOnClassesAndInterfaces() {
    String code = CDSealedOnlyOnClassesAndInterfaces.ERROR_CODE;
    // attribute, method, constructor, enum, class diagram, association, and both ends
    assertEquals(List.of(code, code, code, code, code, code, code, code), checkAndGetErrorCodes(
        "SealedOnlyOnClassesAndInterfacesInvalid.cd"), getJoinedErrors());
  }
  
  /**
   * The check works on symbols, so a permitted type without AST (e.g., loaded from a symbol file)
   * is checked against the supertypes stored in its symbol.
   */
  @Test
  public void testPermittedTypeWithoutAST() {
    ASTCDCompilationUnit ast = parse(MODELS + "SealedImportedSubtype.cd");
    ICD4CodeArtifactScope scope = CD4CodeMill.scopesGenitorDelegator().createFromAST(ast);
    scope.addImports(new ImportStatement("java.lang", true));
    
    ICD4CodeArtifactScope library = CD4CodeMill.artifactScope();
    library.setName("Library");
    library.setPackageName("");
    CD4CodeMill.globalScope().addSubScope(library);
    OOTypeSymbol circle = CD4CodeMill.oOTypeSymbolBuilder().setName("Circle").setIsClass(true)
        .setSpannedScope(CD4CodeMill.scope()).build();
    library.add(circle);
    
    // the permitted type is resolved while the symbol table is completed
    ast.accept(new CD4CodeSymbolTableCompleter(ast).getTraverser());
    checkLogError();
    ASTCDInterface shape = (ASTCDInterface) ast.getCDDefinition().getCDInterfacesList().get(0);
    
    // Circle does not implement Shape yet
    new CDPermittedTypesAreSubtypes().check(shape);
    assertEquals(List.of(CDPermittedTypesAreSubtypes.ERROR_CODE), errorCodes());
    Log.getFindings().clear();
    
    circle.addSuperTypes(SymTypeExpressionFactory.createTypeObject(shape.getSymbol()));
    cd4CodeCoCos.getCheckerForAllCoCos().checkAll(ast);
    assertEquals(List.of(), errorCodes(), getJoinedErrors());
  }
  
  @Test
  public void testSealedAndPermitsAsNames() {
    parse(MODELS + "SealedAsName.cd");
    assertTrue(Log.getFindings().isEmpty(), getJoinedErrors());
  }
  
  /**
   * non-sealed consists of three tokens, which must not be separated by whitespace.
   */
  @Test
  public void testNonSealedWithWhitespace() throws IOException {
    assertTrue(p.parse_String("classdiagram C { non-sealed class A; }").isPresent(),
        getJoinedErrors());
    assertTrue(p.parse_String("classdiagram C { non - sealed class A; }").isEmpty());
    assertTrue(p.parse_String("classdiagram C { non -sealed class A; }").isEmpty());
    assertTrue(p.parse_String("classdiagram C { non- sealed class A; }").isEmpty());
  }
  
  @Test
  public void testPrettyPrint() throws IOException {
    Optional<ASTCDCompilationUnit> parsed = p.parse(getFilePath(MODELS + "SealedValid.cd"));
    assertTrue(parsed.isPresent(), getJoinedErrors());
    String printed = CD4CodeMill.prettyPrint(parsed.get(), false);
    assertTrue(printed.contains("non-sealed class Truck extends Vehicle"), printed);
    assertTrue(printed.contains("sealed interface Colored permits"), printed);
    
    Optional<ASTCDCompilationUnit> reparsed = p.parse_String(printed);
    assertTrue(reparsed.isPresent(), printed);
    assertTrue(parsed.get().deepEquals(reparsed.get()), printed);
  }
  
  /**
   * The UML modifiers of a sealed modifier follow the settings of the registered UMLModifier
   * printer.
   */
  @Test
  public void testPrettyPrintShortForms() throws IOException {
    Optional<ASTCDCompilationUnit> parsed = p.parse_String(
        "classdiagram C { public sealed class A permits B; public final class B extends A; }");
    assertTrue(parsed.isPresent(), getJoinedErrors());
    CD4CodeFullPrettyPrinter shortPrinter = new CD4CodeFullPrettyPrinter(new IndentPrinter(),
        false);
    ((UMLModifierPrettyPrinter) shortPrinter.getTraverser().getUMLModifierHandler().get())
        .setUseShortForms(true);
    String printed = shortPrinter.prettyprint(parsed.get());
    assertTrue(printed.contains("+ sealed class A"), printed);
    assertTrue(printed.contains("+ final class B"), printed);
  }
  
}
