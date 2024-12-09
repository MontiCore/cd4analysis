package de.monticore.cdconcretization;

import static de.monticore.cdconformance.CDConfParameter.*;
import static org.junit.jupiter.api.Assertions.*;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cdassociation._symboltable.CDRoleSymbol;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdconformance.CDConformanceChecker;
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

  public void parseModels(String concrete, String ref) {
    try {
      Optional<ASTCDCompilationUnit> conCD =
          CD4CodeMill.parser().parseCDCompilationUnit(dir + concrete);
      Optional<ASTCDCompilationUnit> refCD = CD4CodeMill.parser().parseCDCompilationUnit(dir + ref);
      if (conCD.isPresent() && refCD.isPresent()) {
        CD4CodeMill.scopesGenitorDelegator().createFromAST(conCD.get());
        CD4CodeMill.scopesGenitorDelegator().createFromAST(refCD.get());
        conCD.get().accept(new CD4CodeSymbolTableCompleter(conCD.get()).getTraverser());
        refCD.get().accept(new CD4CodeSymbolTableCompleter(refCD.get()).getTraverser());
        this.refCD = refCD.get();
        this.conCD = conCD.get();
      } else {
        fail("Could not parse CDs.");
      }

    } catch (IOException e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testEvaluation() throws CompletionException {
    parseModels("ConcEvaluation.cd", "RefEvaluation.cd");

    ConcretizationCompleter completer = new ConcretizationCompleter();
    completer.complete(refCD, conCD);

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

  /**
   * Test that checks if all the types in the reference CD that are missing in the concrete CD are
   * added based on predefined CDs.
   */
  @Test
  @Disabled
  public void testTypeMissing() throws CompletionException {
    parseModels("types/valid/ConcTypeMissing.cd", "types/valid/RefTypeMissing.cd");
    DefaultTypeIncCompleter incarnationCompleter = new DefaultTypeIncCompleter(conCD, refCD, "ref");
    incarnationCompleter.completeIncarnations();
    // conCD.getCDDefinition().setName("RefTypeMissing");
    // System.out.println(CD4CodeMill.prettyPrint(conCD, false));
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

  /** Test that checks if completeInheritance works correctly (after adding the types) */
  @Test
  public void testTypeMissingInheritance() throws CompletionException {
    parseModels("inheritance/ConcMissingInheritance.cd", "inheritance/RefMissingInheritance.cd");
    DefaultTypeIncCompleter incarnationCompleter =
        new DefaultTypeIncCompleter(conCD, refCD, "incarnates");
    incarnationCompleter.completeIncarnations();
    conCD.getCDDefinition().setName("RefMissingInheritance");
    assertTrue(conCD.deepEquals(refCD, false));
  }

  /**
   * Test that checks if all the attributes in the reference CD that are missing in the concrete CD
   * are added based on predefined CDs.
   */
  @Test
  public void testMissingAttributes() throws CompletionException {
    parseModels(
        "attributes/valid/ConcAttributesMissing.cd", "attributes/valid/RefAttributesMissing.cd");
    DefaultTypeIncCompleter incarnationCompleter =
        new DefaultTypeIncCompleter(conCD, refCD, "incarnates");

    conCD.getCDDefinition().setName("RefAttributesMissing");
    incarnationCompleter.completeIncarnations();
    assertTrue(conCD.deepEquals(refCD));
  }

  @Test
  public void testMissingEnumMember() throws CompletionException {
    parseModels("types/enums/ConcEnumMemberMissing.cd", "types/enums/RefEnumMemberMissing.cd");
    DefaultTypeIncCompleter incarnationCompleter =
        new DefaultTypeIncCompleter(conCD, refCD, "incarnates");
    incarnationCompleter.completeIncarnations();
    conCD.getCDDefinition().setName("RefEnumMemberMissing");
    // System.out.println(CD4CodeMill.prettyPrint(conCD, false));
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
  public void testMultipleIncarnation() throws CompletionException {
    parseModels(
        "multipleIncarnation/ConcMultipleIncarnation.cd",
        "multipleIncarnation/RefMultipleIncarnation.cd");
    DefaultTypeIncCompleter incarnationCompleter = new DefaultTypeIncCompleter(conCD, refCD, "ref");
    incarnationCompleter.completeIncarnations();
    // System.out.println(CD4CodeMill.prettyPrint(conCD, false));

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
  public void testMIBothSides() throws CompletionException {
    parseModels("multipleIncarnation/ConcMIBothSides.cd", "multipleIncarnation/RefMIBothSides.cd");

    ConcretizationCompleter completer = new ConcretizationCompleter();
    completer.complete(refCD, conCD);

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
  @Disabled
  // todo: this test but later
  public void testMultipleMappingIncarnation() {}

  /** Test that checks if attributes are inherited in the correct way with a valid example. */
  @Test
  public void testInheritanceValid() throws CompletionException {
    parseModels(
        "inheritance/ConcAttributeInheritance.cd", "inheritance/RefAttributeInheritance.cd");
    DefaultTypeIncCompleter incarnationCompleter =
        new DefaultTypeIncCompleter(conCD, refCD, "incarnates");
    incarnationCompleter.completeIncarnations();

    // System.out.println(CD4CodeMill.prettyPrint(conCD, false));
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
  @Disabled
  public void testAttributetypeIncarnation() {
    parseModels(
        "inheritance/ConcAttributetypeIncarnation.cd",
        "inheritance/RefAttributetypeIncarnation.cd");
    // todo: look at cds -> teacher inherites int number and also has attribute double number
  }

  // AssociationTests

  @Test
  public void testAssocMissingSimple() {
    parseModels(
        "associations/ConcAssociationMissingSimple.cd",
        "associations/RefAssociationMissingSimple.cd");

    DefaultAssocIncCompleter incarnationCompleter =
        new DefaultAssocIncCompleter(conCD, refCD, "incarnates");
    try {
      incarnationCompleter.completeIncarnations();
      conCD.getCDDefinition().setName("RefAssociationMissingSimple");

      assertTrue(
          new CDConformanceChecker(
                  Set.of(
                      STEREOTYPE_MAPPING,
                      NAME_MAPPING,
                      SRC_TARGET_ASSOC_MAPPING,
                      INHERITANCE,
                      ALLOW_CARD_RESTRICTION))
              .checkConformance(conCD, refCD, Set.of("ref")));

    } catch (CompletionException e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testAssocMissingCardinality() {
    parseModels(
        "associations/ConcAssociationMissingCardinality.cd",
        "associations/RefAssociationMissingCardinality.cd");

    DefaultAssocIncCompleter incarnationCompleter =
        new DefaultAssocIncCompleter(conCD, refCD, "incarnates");
    try {
      incarnationCompleter.completeIncarnations();
      conCD.getCDDefinition().setName("RefAssociationMissingCardinality");
      assertTrue(conCD.deepEquals(refCD, false));
    } catch (CompletionException e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testAssocMissingRolename() {
    parseModels(
        "associations/ConcAssociationMissingRolename.cd",
        "associations/RefAssociationMissingRolename.cd");

    DefaultAssocIncCompleter incarnationCompleter =
        new DefaultAssocIncCompleter(conCD, refCD, "incarnates");
    try {
      incarnationCompleter.completeIncarnations();
      conCD.getCDDefinition().setName("RefAssociationMissingRolename");
      assertTrue(conCD.deepEquals(refCD));
    } catch (CompletionException e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testAssocMissingFinal() {
    parseModels(
        "associations/ConcAssociationMissingFinal.cd",
        "associations/RefAssociationMissingFinal.cd");

    DefaultAssocIncCompleter incarnationCompleter =
        new DefaultAssocIncCompleter(conCD, refCD, "incarnates");
    try {
      incarnationCompleter.completeIncarnations();
      // System.out.println(CD4CodeMill.prettyPrint(conCD, false));

      assertTrue(
          new CDConformanceChecker(
                  Set.of(
                      STEREOTYPE_MAPPING,
                      NAME_MAPPING,
                      SRC_TARGET_ASSOC_MAPPING,
                      INHERITANCE,
                      ALLOW_CARD_RESTRICTION))
              .checkConformance(conCD, refCD, Set.of("ref")));

    } catch (CompletionException e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testAssocMultipleTypeIncarnation() {
    parseModels(
        "associations/ConcAssocMultipleTypeIncarnation.cd",
        "associations/RefAssocMultipleTypeIncarnation.cd");

    DefaultAssocIncCompleter incarnationCompleter =
        new DefaultAssocIncCompleter(conCD, refCD, "ref");
    try {
      incarnationCompleter.completeIncarnations();
      // System.out.println(CD4CodeMill.prettyPrint(conCD, false));

      assertTrue(
          new CDConformanceChecker(
                  Set.of(
                      STEREOTYPE_MAPPING,
                      NAME_MAPPING,
                      SRC_TARGET_ASSOC_MAPPING,
                      INHERITANCE,
                      ALLOW_CARD_RESTRICTION))
              .checkConformance(conCD, refCD, Set.of("ref")));

    } catch (CompletionException e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testAssociationReverseMatch() {
    parseModels(
        "associations/ConcAssociationReverseMatch.cd",
        "associations/RefAssociationReverseMatch.cd");

    DefaultAssocIncCompleter incarnationCompleter =
        new DefaultAssocIncCompleter(conCD, refCD, "ref");
    try {
      incarnationCompleter.completeIncarnations();

      assertTrue(
          new CDConformanceChecker(
                  Set.of(
                      STEREOTYPE_MAPPING,
                      NAME_MAPPING,
                      SRC_TARGET_ASSOC_MAPPING,
                      INHERITANCE,
                      ALLOW_CARD_RESTRICTION))
              .checkConformance(conCD, refCD, Set.of("ref")));

    } catch (CompletionException e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testAssocRename() {
    parseModels("associations/ConcAssocRename.cd", "associations/RefAssocRename.cd");

    DefaultAssocIncCompleter incarnationCompleter =
        new DefaultAssocIncCompleter(conCD, refCD, "ref");
    try {
      incarnationCompleter.completeIncarnations();

      assertTrue(
          new CDConformanceChecker(
                  Set.of(
                      STEREOTYPE_MAPPING,
                      NAME_MAPPING,
                      SRC_TARGET_ASSOC_MAPPING,
                      INHERITANCE,
                      ALLOW_CARD_RESTRICTION))
              .checkConformance(conCD, refCD, Set.of("ref")));

    } catch (CompletionException e) {
      fail(e.getMessage());
    }
  }

  // ConcretizationHelper tests
  @Test
  public void testCDHelperMappings() throws CompletionException {
    parseModels("helper/ConcHelper.cd", "helper/RefHelper.cd");

    DefaultTypeIncCompleter defaultTypeIncCompleter =
        new DefaultTypeIncCompleter(conCD, refCD, "ref");
    DefaultAssocIncCompleter defaultAssocIncCompleter =
        new DefaultAssocIncCompleter(conCD, refCD, "ref");

    defaultTypeIncCompleter.completeIncarnations();
    defaultAssocIncCompleter.completeIncarnations();

    System.out.println(CD4CodeMill.prettyPrint(conCD, false));

    ConcretizationHelper helper =
        new ConcretizationHelper(
            conCD,
            refCD,
            defaultTypeIncCompleter.getTypeStrategy(),
            defaultAssocIncCompleter.getCompAssocIncStrategy());

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
}
