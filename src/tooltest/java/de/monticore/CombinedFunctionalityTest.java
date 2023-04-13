package de.monticore;

import static org.junit.jupiter.api.Assertions.*;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._parser.CD4CodeParser;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiff;
import de.monticore.cddiff.alloycddiff.CDSemantics;
import de.monticore.cdmerge.CDMerge;
import de.monticore.cdmerge.config.MergeParameter;
import de.se_rwth.commons.logging.Log;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
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
  @Disabled
  @Test
  public void testMaCoCo() {
    String base_path =
        "src/test/resources/de/monticore/cd4analysis/examples/industrial_strength_models/";

    Set<ASTCDCompilationUnit> mergeSet =
        Arrays.stream(new File(base_path + "MaCoCoMerge/").listFiles())
            .map(f -> parseCDModel(f.getAbsolutePath()))
            .collect(Collectors.toCollection(LinkedHashSet::new));

    Set<MergeParameter> paramSet = new HashSet<>();
    paramSet.add(MergeParameter.LOG_TO_CONSOLE);

    ASTCDCompilationUnit merged = CDMerge.merge(mergeSet, "MergedDomain", paramSet);
    assertNotNull(merged);

    ASTCDCompilationUnit expected =
        parseCDModel(Path.of(base_path, "MaCoCo.cd").toAbsolutePath().toString());

    assertEquals(
        new ArrayList<>(),
        CDDiff.computeSyntax2SemDiff(merged, expected, CDSemantics.MULTI_INSTANCE_CLOSED_WORLD));
  }
}
