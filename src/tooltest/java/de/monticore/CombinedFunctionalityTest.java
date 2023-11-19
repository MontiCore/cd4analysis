package de.monticore;

import static org.junit.jupiter.api.Assertions.*;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4analysis._prettyprint.CD4AnalysisPrettyPrinter;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._parser.CD4CodeParser;
import de.monticore.cd4code.trafo.CD4CodeDirectCompositionTrafo;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cddiff.syn2semdiff.Syn2SemDiff;
import de.monticore.cddiff.syn2semdiff.datastructures.AssocStruct;
import de.monticore.cddiff.syndiff.CDSyntaxDiff;
import de.monticore.cdmerge.CDMerge;
import de.monticore.cdmerge.config.MergeParameter;
import de.monticore.od4report.OD4ReportMill;
import de.monticore.odbasis._ast.ASTODArtifact;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CombinedFunctionalityTest {

  @BeforeEach
  public void init() {
    Log.init();
    CD4CodeMill.reset();
    CD4CodeMill.init();
    CD4CodeMill.globalScope().init();
    BuiltInTypes.addBuiltInTypes(CD4CodeMill.globalScope());
  }

  protected static ASTCDCompilationUnit parseCDModel(String cdFilePath) {
    CD4CodeParser cdParser = CD4CodeMill.parser();
    final Optional<ASTCDCompilationUnit> optCdAST;
    try {
      optCdAST = cdParser.parse(cdFilePath);
    } catch (IOException e) {
      fail();
      throw new RuntimeException(e);
    }
    assert (optCdAST.isPresent());
    return optCdAST.get();
  }

  /** Fails in GitLab pipeline for unknown reason; could not reproduce failure locally. */
  @Test//Fixed test
  public void testMaCoCo() {
    String base_path = "src/tooltest/resources/de/monticore/macoco/";

    Set<ASTCDCompilationUnit> mergeSet =
        Arrays.stream(new File(base_path + "parts/").listFiles())
            .map(f -> parseCDModel(f.getAbsolutePath()))
            .collect(Collectors.toCollection(LinkedHashSet::new));

    Set<MergeParameter> paramSet = new HashSet<>();
    paramSet.add(MergeParameter.LOG_TO_CONSOLE);

    ASTCDCompilationUnit merged = CDMerge.merge(mergeSet, "MergedDomain", paramSet);
    assertNotNull(merged);

    CDDiffUtil.refreshSymbolTable(merged);

    ASTCDCompilationUnit expected =
        parseCDModel(Path.of(base_path, "MaCoCo.cd").toAbsolutePath().toString());
    new CD4CodeDirectCompositionTrafo().transform(expected);
    CDDiffUtil.refreshSymbolTable(expected);

    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(merged, expected);
    Assertions.assertEquals(new ArrayList<>(), syntaxDiff.getBaseDiff());

    // witnesses should be empty
    Syn2SemDiff syn2semdiff = new Syn2SemDiff(merged, expected);
    List<ASTODArtifact> witnesses = syn2semdiff.generateODs(true);
    OD4ReportMill.init();

    if (!witnesses.isEmpty()) {
      for (ASTODArtifact witness : witnesses) {
        System.out.println(OD4ReportMill.prettyPrint(witness, true));
      }
      // fail if witnesses is not empty
      fail();
    }
  }
}
