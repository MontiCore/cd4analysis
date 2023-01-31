package de.monticore;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._parser.CD4CodeParser;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiff;
import de.monticore.cddiff.alloycddiff.CDSemantics;
import de.monticore.cdmerge.CDMerge;
import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
    try {
      CD4CodeParser parser = CD4CodeMill.parser();
      Optional<ASTCDCompilationUnit> expected =
          parser.parse(
              "src/test/resources/de"
                  + "/monticore"
                  + "/cd4analysis"
                  + "/examples"
                  + "/industrial_strength_models/MaCoCo.cd");
      Assert.assertTrue(expected.isPresent());

      // todo: merge parts of MaCoCo
      Set<ASTCDCompilationUnit> mergeSet = new HashSet<>();
      mergeSet.add(expected.get().deepClone());
      mergeSet.add(expected.get().deepClone());

      ASTCDCompilationUnit result = CDMerge.merge(mergeSet, "MergedDomain", new HashSet<>());

      Assert.assertTrue(
          CDDiff.computeSyntax2SemDiff(
                  result, expected.get(), CDSemantics.MULTI_INSTANCE_CLOSED_WORLD)
              .isEmpty());

    } catch (IOException e) {
      Assert.fail(e.getMessage());
    }
  }
}
