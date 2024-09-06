package de.monticore.cdconcretization;

import static de.monticore.cdconformance.CDConfParameter.*;
import static org.junit.jupiter.api.Assertions.*;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdconformance.CDConformanceChecker;
import de.se_rwth.commons.logging.Log;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
// todo: tests aufteilen

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
  @Disabled
  public void testCoconcretizationMerge() throws CompletionException {
    parseModels("ICCD.cd", "RCD.cd");
    ConcretizationCompleter merger = new ConcretizationCompleter();
    merger.merge(refCD, conCD);
    conCD.getCDDefinition().setName("RCD");
    System.out.println(CD4CodeMill.prettyPrint(conCD, false));
    assertTrue(conCD.deepEquals(refCD));
  }

  /**
   * Test that checks if all the types in the reference CD that are missing in the concrete CD are
   * added based on predefined CDs.
   */
  @Test
  public void testTypeMissing() {
    parseModels("types/valid/ConcTypeMissing.cd", "types/valid/RefTypeMissing.cd");
    DefaultTypeIncCompleter incarnationCompleter =
        new DefaultTypeIncCompleter(conCD, refCD, "incarnates");
    incarnationCompleter.identifyAndAddMissingTypeIncarnations();
    conCD.getCDDefinition().setName("RefTypeMissing");
    // System.out.println(CD4CodeMill.prettyPrint(conCD, false));
    assertTrue(conCD.deepEquals(refCD));
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
    // System.out.println(CD4CodeMill.prettyPrint(conCD, false));
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
    assertTrue(conCD.deepEquals(refCD));
  }

  @Test
  public void testMultipleIncarnation() throws CompletionException {
    parseModels(
        "multipleIncarnation/ConcMultipleIncarnation.cd",
        "multipleIncarnation/RefMultipleIncarnation.cd");
    DefaultTypeIncCompleter incarnationCompleter = new DefaultTypeIncCompleter(conCD, refCD, "ref");

    conCD.getCDDefinition().setName("ConcMultipleIncarnation");
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
      //System.out.println(CD4CodeMill.prettyPrint(conCD, false));

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
      //System.out.println(CD4CodeMill.prettyPrint(conCD, false));
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
      // System.out.println(CD4CodeMill.prettyPrint(conCD, false));
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
      conCD.getCDDefinition().setName("RefAssociationMissingFinal");
      //System.out.println(CD4CodeMill.prettyPrint(conCD, false));

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
      //System.out.println(CD4CodeMill.prettyPrint(conCD, false));

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
      //System.out.println(CD4CodeMill.prettyPrint(conCD, false));

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
    parseModels(
      "associations/ConcAssocRename.cd",
      "associations/RefAssocRename.cd");

    DefaultAssocIncCompleter incarnationCompleter =
      new DefaultAssocIncCompleter(conCD, refCD, "ref");
    try {
      incarnationCompleter.completeIncarnations();
      //System.out.println(CD4CodeMill.prettyPrint(conCD, false));

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


  // todo: more complex tests with edge cases




}
