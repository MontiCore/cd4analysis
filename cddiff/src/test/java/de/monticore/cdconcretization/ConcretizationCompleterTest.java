package de.monticore.cdconcretization;

import static de.monticore.cdconformance.CDConfParameter.*;
import static org.junit.jupiter.api.Assertions.*;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cdassociation._symboltable.CDRoleSymbol;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdconformance.CDConformanceChecker;
import de.monticore.cdconformance.inc.association.*;
import de.monticore.cdconformance.inc.type.CompTypeIncStrategy;
import de.monticore.cdconformance.inc.type.EqTypeIncStrategy;
import de.monticore.cdconformance.inc.type.STTypeIncStrategy;
import de.se_rwth.commons.logging.Log;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class ConcretizationCompleterTest {
  public static final String dir = "src/test/resources/de/monticore/cdconcretization/";

  protected ASTCDCompilationUnit refCD;

  protected ASTCDCompilationUnit conCD;

  @BeforeEach
  public void setup() {
    Log.init();
    CD4CodeMill.reset();
    CD4CodeMill.init();
    CD4CodeMill.globalScope().clear();
    BuiltInTypes.addBuiltInTypes(CD4CodeMill.globalScope());
  }

  @Test
  void testEvaluation() {
    testConcretizedConformsToRefAndExpectedOut(
        "EvaluationConc.cd", "EvaluationRef.cd", "EvaluationOut.cd");
  }

  /**
   * Test that checks if all the types in the reference CD that are missing in the concrete CD are
   * added based on predefined CDs.
   */
  @Test
  void testTypeMissing() {
    testConcretizedConformsToRefAndExpectedOut(
        "types/valid/TypeMissingConc.cd",
        "types/valid/TypeMissingRef.cd",
        "types/valid/TypeMissingOut.cd");
  }

  /** Test that checks if completeInheritance works correctly (after adding the types) */
  @Test
  void testTypeMissingInheritance() {
    testConcretizedEqualsRef(
        "inheritance/MissingInheritanceConc.cd", "inheritance/MissingInheritanceRef.cd");
  }

  /**
   * Test that checks if all the attributes in the reference CD that are missing in the concrete CD
   * are added based on predefined CDs.
   */
  @Test
  void testMissingAttributes() {
    testConcretizedEqualsRef(
        "attributes/valid/AttributesMissingConc.cd", "attributes/valid/AttributesMissingRef.cd");
  }

  @Test
  void testTwoMissingAttributes() {
    testConcretizedEqualsRef(
        "attributes/valid/TwoAttributesMissingConc.cd",
        "attributes/valid/TwoAttributesMissingRef.cd");
  }

  @Test
  void testTwoMissingAttributesOneMatch() {
    testConcretizedEqualsRef(
        "attributes/valid/TwoAttributesMissingOneMatchConc.cd",
        "attributes/valid/TwoAttributesMissingOneMatchRef.cd");
  }

  /**
   * The attributes expected by the reference class are already present in the direct superclass of
   * 'Employee'. The concretization should not add them again.
   */
  @Test
  void testAttributeExistsInConcreteSuperclass() {
    testConcretizedConformsToRefAndExpectedOut(
        "attributes/valid/AttributeInSuperClassConc.cd",
        "attributes/valid/AttributeInSuperClassRef.cd",
        "attributes/valid/AttributeInSuperClassOut.cd");

    // The conformance check is not enough here. The tool must not add attributes to the concrete
    // class if they
    // are already inherited from a superclass.
    List<String> attributesInSuperclass = Arrays.asList("firstName", "lastName");

    ASTCDClass employeeClass =
        conCD.getCDDefinition().getCDClassesList().stream()
            .filter(cdClass -> cdClass.getName().equals("Employee"))
            .findFirst()
            .orElseThrow();
    employeeClass.getCDAttributeList().stream()
        .filter(cdAttribute -> attributesInSuperclass.contains(cdAttribute.getName()))
        .findAny()
        .ifPresent(
            cdAttribute -> {
              fail(
                  "Attribute "
                      + cdAttribute
                      + " should not be added to the concrete class 'Employee' as it is already "
                      + "inherited from the superclass 'Person'");
            });
  }

  /**
   * The attributes expected by the reference class are already present "deep" in the class
   * hierarchy of 'Employee'. The concretization should not add them again.
   */
  @Test
  void testAttributeExistsInConcreteDeepSuperclass() {
    testConcretizedConformsToRefAndExpectedOut(
        "attributes/valid/AttributeInDeepSuperClassConc.cd",
        "attributes/valid/AttributeInDeepSuperClassRef.cd",
        "attributes/valid/AttributeInDeepSuperClassOut.cd");

    // The conformance check is not enough here. The tool must not add attributes to the concrete
    // class if they
    // are already inherited from a superclass.
    List<String> attributesInSuperclass = Arrays.asList("firstName", "lastName");

    ASTCDClass employeeClass =
        conCD.getCDDefinition().getCDClassesList().stream()
            .filter(cdClass -> cdClass.getName().equals("Employee"))
            .findFirst()
            .orElseThrow();
    employeeClass.getCDAttributeList().stream()
        .filter(cdAttribute -> attributesInSuperclass.contains(cdAttribute.getName()))
        .findAny()
        .ifPresent(
            cdAttribute -> {
              fail(
                  "Attribute "
                      + cdAttribute
                      + " should not be added to the concrete class 'Employee' as it is already "
                      + "inherited from the superclass 'Person'");
            });
  }

  @Test
  void testMissingEnumMember() {
    testConcretizedConformsToRefAndExpectedOut(
        "types/enums/EnumMemberMissingConc.cd",
        "types/enums/EnumMemberMissingRef.cd",
        "types/enums/EnumMemberMissingOut.cd");
  }

  @Test
  void testMultipleIncarnation() {
    testConcretizedConformsToRefAndExpectedOut(
        "multipleIncarnation/ClassMIConc.cd",
        "multipleIncarnation/ClassMIRef.cd",
        "multipleIncarnation/ClassMIOut.cd");
  }

  @Test
  void testAssocBothSidesMI() {
    testConcretizedConformsToRefAndExpectedOut(
        "multipleIncarnation/BothAssocSidesMIConc.cd",
        "multipleIncarnation/BothAssocSidesMIRef.cd",
        "multipleIncarnation/BothAssocSidesMIOut.cd");
  }

  @Test
  void testAssocBothSidesMIOneAssocExistsPerTypeIncarnation() {
    /*
     * The tool only adds associations between each pair of type incarnations if the concrete CD
     * does not already contain an association for each SINGLE type incarnation!
     */
    testConcretizedConformsToRefAndExpectedOut(
        "multipleIncarnation/BothAssocSidesMIOneAssocExistsConc.cd",
        "multipleIncarnation/BothAssocSidesMIRef.cd",
            "multipleIncarnation/BothAssocSidesMIOneAssocExistsOut.cd");
  }

  @Test
  void testMIUnequalCardinalities() {
    try {
      parseAndConcretize(
          "multipleIncarnation/UnequalCardCon.cd", "multipleIncarnation/UnequalCardRef.cd");
      fail("Expected CompletionException");
    } catch (CompletionException e) {
      System.out.println("Completion failed as expected: " + e.getMessage());
    }
  }

  @Test
  void testInterfaceMI() {
    testConcretizedConformsToRefAndExpectedOut(
        "multipleIncarnation/InterfaceMIConc.cd",
        "multipleIncarnation/InterfaceMIRef.cd",
        "multipleIncarnation/InterfaceMIOut.cd");
  }

  @Test
  @Disabled("disabled until issue 9 is clarified")
  public void testAttributeTypeMI() {
    testConcretizedConformsToRefAndExpectedOut(
        "multipleIncarnation/AttributeTypeMIConc.cd",
        "multipleIncarnation/AttributeTypeMIRef.cd",
        "multipleIncarnation/AttributeTypeMIOut.cd");
  }

  @Test
  @Disabled
  // todo: this test but later
  void testMultipleMappingIncarnation() {}

  /** Test that checks if attributes are inherited in the correct way with a valid example. */
  @Test
  void testInheritanceValid() {
    testConcretizedConformsToRefAndExpectedOut(
        "inheritance/AttributeInheritanceConc.cd",
        "inheritance/AttributeInheritanceRef.cd",
        "inheritance/AttributeInheritanceOut.cd");
  }

  @Test
  @Disabled
  void testAttributeTypeMismatchWithSuperclass() {
    try {
      parseAndConcretize(
          "inheritance/AttributeTypeMismatchConc.cd", "inheritance/AttributeTypeMismatchRef.cd");
      fail("Expected CompletionException");
    } catch (CompletionException e) {
      System.out.println("Completion failed as expected: " + e.getMessage());
    }
    // todo: look at cds -> teacher inherites int number and also has attribute double number
  }

  // AssociationTests

  @Test
  @Disabled("disabled until issue 13 is clarified")
  void testAssocMissingSimple() {
    testConcretizedEqualsRef(
        "associations/AssociationMissingSimpleConc.cd",
        "associations/AssociationMissingSimpleRef.cd");
  }

  @Test
  void testAssocMissingCardinality() {
    testConcretizedEqualsRef(
        "associations/AssociationMissingCardinalityConc.cd",
        "associations/AssociationMissingCardinalityRef.cd");
  }

  @Test
  void testAssocMissingRolename() {
    testConcretizedEqualsRef(
        "associations/AssociationMissingRolenameConc.cd",
        "associations/AssociationMissingRolenameRef.cd");
  }

  @Test
  void testAssocMissingFinal() {
    testConcretizedConformsToRefAndExpectedOut(
        "associations/AssociationMissingFinalConc.cd",
        "associations/AssociationMissingFinalRef.cd",
        "associations/AssociationMissingFinalOut.cd");
  }

  @Test
  void testAssocMultipleTypeIncarnation() {
    testConcretizedConformsToRefAndExpectedOut(
        "associations/AssociationMultipleTypeIncarnationConc.cd",
        "associations/AssociationMultipleTypeIncarnationRef.cd",
        "associations/AssociationMultipleTypeIncarnationOut.cd");
  }

  @Test
  void testAssocInSuperType() {
    testConcretizedConformsToRefAndExpectedOut(
        "associations/AssociationInSuperTypeConc.cd",
        "associations/AssociationInSuperTypeRef.cd",
        "associations/AssociationInSuperTypeOut.cd");
  }

  @Test
  void testAssocSuperMatchingTest() {
    testConcretizedConformsToRefAndExpectedOut(
        "associations/AssociationSuperMatchingConc.cd",
        "associations/AssociationSuperMatchingRef.cd",
        "associations/AssociationSuperMatchingOut.cd");
  }

  @Test
  void testAssocSuperMatchingConformanceTest() {
    parseModels(
        "associations/AssociationSuperMatchingOut.cd",
        "associations/AssociationSuperMatchingRef.cd");
    assertTrue(
            new CDConformanceChecker(
                    Set.of(
                            STEREOTYPE_MAPPING,
                            NAME_MAPPING,
                            SRC_TARGET_ASSOC_MAPPING,
                            INHERITANCE,
                            ALLOW_CARD_RESTRICTION))
                    .checkConformance(conCD, refCD, Set.of("ref")));
  }

  @Test
  void testAssociationReverseMatch() {
    testConcretizedConformsToRefAndExpectedOut(
        "associations/AssociationReverseMatchConc.cd",
        "associations/AssociationReverseMatchRef.cd",
        "associations/AssociationReverseMatchOut.cd");
  }

  @Test
  void testTypeMIOneAssocExists() {
    testConcretizedConformsToRefAndExpectedOut(
            "associations/TypeMIOneAssocExistsConc.cd",
            "associations/TypeMIOneAssocExistsRef.cd",
            "associations/TypeMIOneAssocExistsOut.cd");
  }

  @Test
  void testCDHelperExample() {
    testConcretizedConformsToRefAndExpectedOut(
        "helper/HelperConc.cd", "helper/HelperRef.cd", "helper/HelperOut.cd");
  }

  // ConcretizationHelper tests
  @Test
  void testCDHelperMappings() throws CompletionException {
    String mapping = "ref";
    parseAndConcretize("helper/HelperConc.cd", "helper/HelperRef.cd");

    System.out.println(CD4CodeMill.prettyPrint(conCD, false));

    CompTypeIncStrategy typeIncStrategy = new CompTypeIncStrategy(refCD, mapping);
    typeIncStrategy.addIncStrategy(new STTypeIncStrategy(refCD, mapping));
    typeIncStrategy.addIncStrategy(new EqTypeIncStrategy(refCD, mapping));

    CompAssocIncStrategy assocIncStrategy = new CompAssocIncStrategy(refCD, mapping);
    assocIncStrategy.addIncStrategy(new STNamedAssocIncStrategy(refCD, mapping));
    assocIncStrategy.addIncStrategy(new EqNameAssocIncStrategy(refCD, mapping));
    assocIncStrategy.addIncStrategy(
            new RolePrefixInNavDirIncStrategy(typeIncStrategy, conCD, refCD));
    assocIncStrategy.addIncStrategy(
            new RolePrefixIfPresentIncStrategy(typeIncStrategy, conCD, refCD));

    ConcretizationHelper helper =
        new ConcretizationHelper(
            conCD,
            refCD,
            typeIncStrategy,
            assocIncStrategy);

    helper.mapReferenceToConcreteRoles();

    Map<CDTypeSymbol, Set<CDTypeSymbol>> actualMap = helper.typeMapping;

    Map<String, Set<String>> expectedMap = new HashMap<>();
    expectedMap.put("B", new HashSet<>(Arrays.asList("B", "C", "D")));
    expectedMap.put("A", new HashSet<>(Collections.singleton("A")));

    Map<String, Set<String>> actualMapTemp = new HashMap<>();
    for (Map.Entry<CDTypeSymbol, Set<CDTypeSymbol>> entry : actualMap.entrySet()) {
      String keyName = entry.getKey().getName();
      Set<String> valueNames =
          entry.getValue().stream().map(CDTypeSymbol::getName).collect(Collectors.toSet());
      actualMapTemp.put(keyName, valueNames);
    }
    assertEquals(actualMapTemp, expectedMap);

    Map<CDRoleSymbol, Set<CDRoleSymbol>> actualMap2 = helper.roleMapping;

    Map<String, Set<String>> expectedMap2 = new HashMap<>();
    expectedMap2.put(
        "roleNameRight",
        new HashSet<>(Arrays.asList("roleNameRight_C", "roleNameRight_D", "roleNameRight_B")));
    expectedMap2.put(
        "roleNameLeft",
        new HashSet<>(Arrays.asList("roleNameLeft_A", "roleNameLeft_A", "roleNameLeft_A")));

    Map<String, Set<String>> actualMapTemp2 = new HashMap<>();
    for (Map.Entry<CDRoleSymbol, Set<CDRoleSymbol>> entry : actualMap2.entrySet()) {
      String keyName = entry.getKey().getName();
      Set<String> valueNames =
          entry.getValue().stream().map(CDRoleSymbol::getName).collect(Collectors.toSet());
      actualMapTemp2.put(keyName, valueNames);
    }
    assertEquals(actualMapTemp2, expectedMap2);
    // todo: there is a bug somewhere in the mapping of roles to their respective other type
    /*

    Map<CDRoleSymbol, Set<CDTypeSymbol>> actualMap3 = helper.roleToTypeMapping;

    Map<String, Set<String>> expectedMap3 = new HashMap<>();
    expectedMap3.put("roleNameRight_D", new HashSet<>(Collections.singleton("A")));
    expectedMap3.put("roleNameRight_B", new HashSet<>(Collections.singleton("A")));
    expectedMap3.put("roleNameRight_C", new HashSet<>(Collections.singleton("A")));
    expectedMap3.put("roleNameLeft_A", new HashSet<>(Arrays.asList("C", "B", "D")));


    Map<String, Set<String>> actualMapTemp3 = new HashMap<>();
    for (Map.Entry<CDRoleSymbol, Set<CDTypeSymbol>> entry : actualMap3.entrySet()) {
      String keyName = entry.getKey().getName();
      Set<String> valueNames = entry.getValue().stream()
        .map(CDTypeSymbol::getName)
        .collect(Collectors.toSet());
      actualMapTemp3.put(keyName, valueNames);
    }
    assertEquals(actualMapTemp3, expectedMap3);

     */
  }

  /***
   * Parses the two models and checks if the concretized CD equals the reference CD.
   * <br>
   * Use this to test if basic completion of model elements works, without any application of
   * explicit incarnation mappings.
   *
   * @param conc the path to the concrete CD
   * @param ref the path to the reference CD
   */
  private void testConcretizedEqualsRef(String conc, String ref) {
    try {
      parseAndConcretize(conc, ref);
    } catch (CompletionException e) {
      fail("CompletionException", e);
    }

    // to use deep equals, both CDs need to have the same name
    conCD.getCDDefinition().setName(refCD.getCDDefinition().getName());
    assertTrue(conCD.deepEquals(refCD, false));
  }

  /**
   * Parses the two models and checks if the concretized CD conforms to the reference CD. <br>
   * Use this for all non-trivial test cases where the concretization is no longer expected to equal
   * to the reference CD.
   *
   * @param conc the path to the concrete CC
   * @param ref the path to the reference CD
   */
  private void testConcretizedConformsToRef(String conc, String ref) {
    try {
      parseAndConcretize(conc, ref);
    } catch (CompletionException e) {
      fail("CompletionException", e);
    }
    assertTrue(
        new CDConformanceChecker(
                Set.of(
                    STEREOTYPE_MAPPING,
                    NAME_MAPPING,
                    SRC_TARGET_ASSOC_MAPPING,
                    INHERITANCE,
                    ALLOW_CARD_RESTRICTION))
            .checkConformance(conCD, refCD, Set.of("ref")));
  }

  private void testConcretizedConformsToRefAndExpectedOut(String conc, String ref, String out) {
    ASTCDCompilationUnit expectedCD = parseCD(out);
    // 1. concretize and check conformance
    testConcretizedConformsToRef(conc, ref);
    // 2. check if concretized CD equals expected output
    assertTrue(conCD.deepEquals(expectedCD, false), "Concretized output does not match expected");
  }

  private void parseAndConcretize(String conc, String ref) throws CompletionException {
    parseModels(conc, ref);
    // ConcretizationCompleter completer = new ConcretizationCompleter("ref");
    ConcretizationCompleter completer = new ConcretizationCompleter("ref");
    completer.complete(conCD, refCD);
    System.out.println("Concretized CD:");
    System.out.println(CD4CodeMill.prettyPrint(conCD, false));
  }

  private void parseModels(String concrete, String ref) {
    this.refCD = parseCD(ref);
    this.conCD = parseCD(concrete);
  }

  private ASTCDCompilationUnit parseCD(String filePath) {
    ASTCDCompilationUnit cd;
    try {
      cd =
          CD4CodeMill.parser()
              .parseCDCompilationUnit(dir + filePath)
              .orElseThrow(() -> new RuntimeException("Could not parse CD: " + filePath));
    } catch (IOException e) {
      throw new RuntimeException("Failed to load CD: " + filePath, e);
    }
    CD4CodeMill.scopesGenitorDelegator().createFromAST(cd);
    cd.accept(new CD4CodeSymbolTableCompleter(cd).getTraverser());
    return cd;
  }
}
