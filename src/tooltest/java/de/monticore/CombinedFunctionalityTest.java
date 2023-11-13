package de.monticore;

import static org.junit.jupiter.api.Assertions.*;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._parser.CD4CodeParser;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;

import de.monticore.cddiff.syndiff.CDSyntaxDiff;
import de.monticore.cdmerge.CDMerge;
import de.monticore.cdmerge.config.MergeParameter;
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
  @Test
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

    ASTCDCompilationUnit expected =
        parseCDModel(Path.of(base_path, "MaCoCo.cd").toAbsolutePath().toString());


    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(merged,expected);
    Assertions.assertEquals(new ArrayList<>(), syntaxDiff.getBaseDiff());
  }

}
