/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconcretization.util;

import static org.junit.jupiter.api.Assertions.*;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdconcretization.AbstractCDConcretizationTest;
import de.monticore.cdconcretization.UnderspecifiedPlaceholderType;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.se_rwth.commons.logging.Log;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class MethodSignatureStringTest {
  
  private static IOOSymbolsScope scope;
  
  @BeforeAll
  public static void setup() {
    Log.init();
    Log.enableFailQuick(false);
    Log.clearFindings();
    CD4CodeMill.reset();
    CD4CodeMill.init();
    CD4CodeMill.globalScope().clear();
    BuiltInTypes.addBuiltInTypes(CD4CodeMill.globalScope());
    UnderspecifiedPlaceholderType.addPlaceholderType(CD4CodeMill.globalScope());
    
    ASTCDCompilationUnit cd = AbstractCDConcretizationTest.parseCD(
        "util/MethodSignatureStrings.cd");
    scope = cd.getEnclosingScope();
  }
  
  @ParameterizedTest
  @MethodSource
  void testResolveValidExistingSignatures(String signatureString, String symbolName,
      List<String> parameterTypes) {
    Optional<MethodSymbol> resolvedSymbol = MethodSignatureString.resolveMethodSignature(scope,
        signatureString);
    assertTrue(Log.getFindings().isEmpty(), "There should not be any errors!");
    assertTrue(resolvedSymbol.isPresent(), "Signature string should resolve to a symbol");
    assertEquals("MethodSignatureStrings." + symbolName, resolvedSymbol.get().getFullName(),
        "Resolved symbol name should match");
    assertEquals(parameterTypes.size(), resolvedSymbol.get().getParameterList().size(),
        "Number of parameters should match");
    for (int i = 0; i < parameterTypes.size(); i++) {
      String expectedType = parameterTypes.get(i);
      String actualType = resolvedSymbol.get().getParameterList().get(i).getType().print();
      assertEquals(expectedType, actualType, "Parameter type at index " + i + " should match");
    }
  }
  
  private static Stream<Arguments> testResolveValidExistingSignatures() {
    return Stream.of(Arguments.of("A.m1()", "A.m1", List.of()), Arguments.of("A.m1(int)", "A.m1",
        List.of("int")), Arguments.of("A.m1(String)", "A.m1", List.of("String")), Arguments.of(
            "A.m1(int, String)", "A.m1", List.of("int", "String")), Arguments.of(
                "A.m1(int, String, double)", "A.m1", List.of("int", "String", "double")), Arguments
                    .of("A.m1(int, String, double, boolean)", "A.m1", List.of("int", "String",
                        "double", "boolean")),
        // ensure whitespace is ignored
        Arguments.of("A.m1(       )", "A.m1", List.of()), Arguments.of(
            "A.m1(int,      String,   double,  boolean)", "A.m1", List.of("int", "String", "double",
                "boolean")),
        // ensure whitespace is mot required
        Arguments.of("A.m1(int,String,double,boolean)", "A.m1", List.of("int", "String", "double",
            "boolean")), Arguments.of("A.m2", "A.m2", List.of()), Arguments.of("A.m2()", "A.m2",
                List.of()), Arguments.of("A.m3", "A.m3", List.of("int", "String")), Arguments.of(
                    "A.m3(int, String)", "A.m3", List.of("int", "String")));
  }
  
  @ParameterizedTest
  @MethodSource
  void testResolveValidUnknownSignatures(String signatureString) {
    Optional<MethodSymbol> resolvedSymbol = MethodSignatureString.resolveMethodSignature(scope,
        signatureString);
    assertTrue(Log.getFindings().isEmpty(), "There should not be any errors!");
    assertTrue(resolvedSymbol.isEmpty(), "Signature string should not resolve to a symbol");
  }
  
  private static Stream<String> testResolveValidUnknownSignatures() {
    return Stream.of("B.foo", "B.foo()", "A.unknown", "A.unknown()", "A.m2(int)" // m2 exists but not with parameters
    );
  }
  
  @ParameterizedTest
  @MethodSource
  void testResolveInvalidSignatures(String signatureString) {
    Optional<MethodSymbol> resolvedSymbol = MethodSignatureString.resolveMethodSignature(scope,
        signatureString);
    assertFalse(Log.getFindings().isEmpty(), "There should be an error!");
    assertTrue(resolvedSymbol.isEmpty(), "Invalid signature string should not resolve to a symbol");
  }
  
  private static Stream<String> testResolveInvalidSignatures() {
    return Stream.of("B.foo(", "B.foo)", "A.m1(UnknownType)", "A.m1" // not unique
    );
  }
  
}
