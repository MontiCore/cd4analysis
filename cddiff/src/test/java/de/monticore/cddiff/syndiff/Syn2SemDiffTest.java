/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.syndiff;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.alloycddiff.CDSemantics;
import de.monticore.cddiff.ow2cw.ReductionTrafo;
import de.monticore.cddiff.syn2semdiff.Syn2SemDiff;
import de.monticore.od4report._prettyprint.OD4ReportFullPrettyPrinter;
import de.monticore.odbasis._ast.ASTODArtifact;
import de.monticore.odvalidity.OD2CDMatcher;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static de.monticore.cddiff.alloycddiff.CDSemantics.SIMPLE_CLOSED_WORLD;
import static de.monticore.cddiff.alloycddiff.CDSemantics.STA_CLOSED_WORLD;
import static de.monticore.cddiff.alloycddiff.CDSemantics.STA_OPEN_WORLD;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Syn2SemDiffTest extends SynDiffTestBasis {

  private static final String cddiffDir = "src/test/resources/de/monticore/cddiff/";
  private static final String validationDir = "src/test/resources/validation/";

  public static Stream<Arguments> emptyWitnesses() {
    return Stream.of(
        Arguments.of(cddiffDir, "syndiff/AssocDiff/AssocDeletedMerging/CD51.cd", "syndiff/AssocDiff/AssocDeletedMerging/CD52.cd", false, false),
        Arguments.of(cddiffDir, "syndiff/AssocDiff/AssocDeletedMerging/CD52.cd", "syndiff/AssocDiff/AssocDeletedMerging/CD51.cd", false, false),
        Arguments.of(cddiffDir, "syndiff/SyntaxDiff/MoveAttributes/CD11.cd", "syndiff/SyntaxDiff/MoveAttributes/CD12.cd", false, false),
        Arguments.of(cddiffDir, "syndiff/SyntaxDiff/MoveAttributes/CD12.cd", "syndiff/SyntaxDiff/MoveAttributes/CD11.cd", false, false),
        Arguments.of(cddiffDir, "syndiff/AssocDiff/AssocDeletedMerging/CD52.cd", "syndiff/AssocDiff/AssocDeletedMerging/CD51.cd", false, true),
        Arguments.of(cddiffDir, "syndiff/SyntaxDiff/MoveAttributes/CD11.cd", "syndiff/SyntaxDiff/MoveAttributes/CD12.cd", false, true),
        Arguments.of(cddiffDir, "DigitalTwins/DigitalTwin2.cd", "DigitalTwins/DigitalTwin3.cd", false, false),
        Arguments.of(cddiffDir, "DigitalTwins/DigitalTwin3.cd", "DigitalTwins/DigitalTwin2.cd", false, false),
        Arguments.of(cddiffDir, "DigitalTwins/DigitalTwin2.cd", "DigitalTwins/DigitalTwin1.cd", true, true),
        Arguments.of(cddiffDir, "Employees/Employees2.cd", "Employees/Employees1.cd", true, true),
        Arguments.of(cddiffDir, "Employees/Employees8.cd", "Employees/Employees7.cd", true, true),
        Arguments.of(validationDir, "cddiff/LibraryV3.cd", "cddiff/LibraryV2.cd", true, true),
        Arguments.of(validationDir, "cddiff/LibraryV5.cd", "cddiff/LibraryV4.cd", true, true),
        Arguments.of(validationDir, "cd4analysis/ManagementV2.cd", "cd4analysis/ManagementV1.cd", true, true),
        Arguments.of(validationDir, "cd4analysis/MyCompanyV2.cd", "cd4analysis/MyCompanyV1.cd", true, true),
        Arguments.of(validationDir, "cd4analysis/MyExampleV2.cd", "cd4analysis/MyExampleV1.cd", true, true)
    );
  }

  @ParameterizedTest
  @MethodSource("emptyWitnesses")
  public void testEmptyWitnesses(String baseDir, String srcPath, String tgtPath, boolean staDiff, boolean reduction ) {
    dir = baseDir;
    parseModels(srcPath, tgtPath);

    if(reduction){
      ReductionTrafo trafo = new ReductionTrafo();
      trafo.transform(src, tgt);
    }

    Syn2SemDiff syn2semdiff = new Syn2SemDiff(src, tgt);
    List<ASTODArtifact> witnesses = syn2semdiff.generateODs(staDiff);

    assertTrue(witnesses.isEmpty());
  }

  public static Stream<Arguments> witnesses() {
    return Stream.of(
      //Arguments.of("syndiff/AssocDiff/AssocDeletedMerging/CD51.cd", "syndiff/AssocDiff/AssocDeletedMerging/CD52.cd", SIMPLE_CLOSED_WORLD, true),
      Arguments.of(cddiffDir, "syndiff/SyntaxDiff/AddedDeletedAssocs/CD31.cd", "syndiff/SyntaxDiff/AddedDeletedAssocs/CD32.cd", SIMPLE_CLOSED_WORLD, false),
      Arguments.of(cddiffDir, "syndiff/SyntaxDiff/MoveAttributes/CD12.cd", "syndiff/SyntaxDiff/MoveAttributes/CD11.cd", SIMPLE_CLOSED_WORLD, true),
      Arguments.of(cddiffDir, "syndiff/SyntaxDiff/SS1.cd", "syndiff/SyntaxDiff/SS2.cd", SIMPLE_CLOSED_WORLD, false),
      Arguments.of(cddiffDir, "DigitalTwins/DigitalTwin2.cd", "DigitalTwins/DigitalTwin1.cd", SIMPLE_CLOSED_WORLD, false),
      Arguments.of(cddiffDir, "DigitalTwins/DigitalTwin1.cd", "DigitalTwins/DigitalTwin2.cd", SIMPLE_CLOSED_WORLD, true),
      Arguments.of(cddiffDir, "DigitalTwins/DigitalTwin2.cd", "DigitalTwins/DigitalTwin3.cd", SIMPLE_CLOSED_WORLD, true),
      Arguments.of(cddiffDir, "DigitalTwins/DigitalTwin3.cd", "DigitalTwins/DigitalTwin2.cd", SIMPLE_CLOSED_WORLD, true),
      Arguments.of(cddiffDir, "Employees/Employees1.cd", "Employees/Employees2.cd", SIMPLE_CLOSED_WORLD, false),
      Arguments.of(cddiffDir, "Employees/Employees2.cd", "Employees/Employees1.cd", SIMPLE_CLOSED_WORLD, false),
      Arguments.of(cddiffDir, "Employees/Employees7.cd", "Employees/Employees8.cd", STA_CLOSED_WORLD, true)
    );
  }

  @ParameterizedTest
  @MethodSource("witnesses")
  public void testWitnesses(String baseDir, String srcPath, String tgtPath, CDSemantics semantics, boolean reduction) {
    dir = baseDir;
    parseModels(srcPath, tgtPath);

    if(reduction){
      ReductionTrafo trafo = new ReductionTrafo();
      trafo.transform(src, tgt);
    }

    Syn2SemDiff syn2semdiff = new Syn2SemDiff(src, tgt);
    List<ASTODArtifact> witnesses = syn2semdiff.generateODs(semantics.equals(STA_CLOSED_WORLD) || semantics.equals(STA_OPEN_WORLD));

    assertFalse(witnesses.isEmpty());

    checkDiffWitnesses(semantics, src, tgt, witnesses);
  }

  public static Stream<Arguments> witnessesReduction() {
    return Stream.of(
        Arguments.of(cddiffDir, "DigitalTwins/DigitalTwin3.cd", "DigitalTwins/DigitalTwin2.cd"),
        Arguments.of(validationDir, "Performance/5A.cd", "Performance/5B.cd"),
        Arguments.of(validationDir, "Performance/10A.cd", "Performance/10B.cd"),
        Arguments.of(validationDir, "Performance/15A.cd", "Performance/15B.cd"),
        Arguments.of(validationDir, "Performance/20A.cd", "Performance/20B.cd"),
        Arguments.of(validationDir, "Performance/25A.cd", "Performance/25B.cd"),
        Arguments.of(validationDir, "cddiff/DEv2.cd", "cddiff/DEv1.cd"),
        Arguments.of(validationDir, "cddiff/EAv2.cd", "cddiff/EAv1.cd"),
        Arguments.of(validationDir, "cddiff/EMTv1.cd", "cddiff/EMTv2.cd"),
        Arguments.of(validationDir, "cddiff/LibraryV2.cd", "cddiff/LibraryV1.cd"),
        Arguments.of(validationDir, "cddiff/LibraryV4.cd", "cddiff/LibraryV3.cd"),
        Arguments.of(validationDir, "cd4analysis/MyLifeV2.cd", "cd4analysis/MyLifeV1.cd"),
        Arguments.of(validationDir, "cd4analysis/TeachingV2.cd", "cd4analysis/TeachingV1.cd")
    );
  }

  @ParameterizedTest
  @MethodSource("witnessesReduction")
  public void testWitnessesReduction(String baseDir, String srcPath, String tgtPath) {
    dir = baseDir;
    parseModels(srcPath, tgtPath);

    ASTCDCompilationUnit srcOriginal = src.deepClone();
    ASTCDCompilationUnit tgtOriginal = tgt.deepClone();

    // reduction-based
    ReductionTrafo trafo = new ReductionTrafo();
    trafo.transform(src, tgt);

    Syn2SemDiff syn2semdiff = new Syn2SemDiff(src, tgt);
    List<ASTODArtifact> witnesses = syn2semdiff.generateODs(true);

    Assertions.assertFalse(witnesses.isEmpty());

    checkDiffWitnesses(CDSemantics.STA_CLOSED_WORLD, src, tgt, witnesses);

    CD4CodeMill.scopesGenitorDelegator().createFromAST(srcOriginal);
    CD4CodeMill.scopesGenitorDelegator().createFromAST(tgtOriginal);
    checkDiffWitnesses(CDSemantics.STA_OPEN_WORLD, srcOriginal, tgtOriginal, witnesses);
  }

  protected void checkDiffWitnesses(CDSemantics semantics, ASTCDCompilationUnit cd1,
                                    ASTCDCompilationUnit cd2, Collection<ASTODArtifact> witnesses) {
    for (ASTODArtifact od : witnesses) {
      if (!new OD2CDMatcher().checkIfDiffWitness(semantics, cd1, cd2, od)) {
        Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
        Assertions.fail();
      }
    }
  }
}
