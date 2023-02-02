package de.monticore;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._parser.CD4CodeParser;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiff;
import de.monticore.cddiff.alloycddiff.CDSemantics;
import de.monticore.cdmerge.CDMerge;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class CombinedFunctionalityTest {

  @Before
  public void init() {
    CD4CodeMill.reset();
    CD4CodeMill.init();
    CD4CodeMill.globalScope().init();
    BuiltInTypes.addBuiltInTypes(CD4CodeMill.globalScope());
  }

  @Test
  public void testMaCoCo() {
    String base_path = "src/test/resources/de/monticore/cd4analysis/examples/industrial_strength_models/";
    try {
      CD4CodeParser parser = CD4CodeMill.parser();

      Set<ASTCDCompilationUnit> mergeSet =
        Arrays.stream(new File(base_path + "MaCoCoMerge/").listFiles())
          .map(file -> {
            try {
              return parser.parse(file.getAbsolutePath());
            } catch (IOException e) {
              fail();
              return null;
            }
          })
          .peek(optional -> assertTrue(optional.isPresent()))
          .map(Optional::get)
          .collect(Collectors.toUnmodifiableSet());

      ASTCDCompilationUnit merged = CDMerge.merge(mergeSet, "MergedDomain", new HashSet<>());


      Optional<ASTCDCompilationUnit> expected =
        parser.parse(base_path + "MaCoCo.cd");
      Assert.assertTrue(expected.isPresent());


      Assert.assertTrue(
          CDDiff.computeSyntax2SemDiff(
                  merged, expected.get(), CDSemantics.MULTI_INSTANCE_CLOSED_WORLD)
              .isEmpty());

    } catch (IOException e) {
      Assert.fail(e.getMessage());
    }
  }
}
