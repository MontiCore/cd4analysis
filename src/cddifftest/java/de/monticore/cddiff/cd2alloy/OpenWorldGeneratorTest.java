package de.monticore.cddiff.cd2alloy;

import de.monticore.cddiff.cd2alloy.generator.OpenWorldGenerator;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffTestBasis;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertNotNull;

public class OpenWorldGeneratorTest extends CDDiffTestBasis {

  @Test
  public void testGenerator(){
    final ASTCDCompilationUnit ast1 = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/Abstract2Interface/AbstractPerson.cd");
    assertNotNull(ast1);

    final ASTCDCompilationUnit ast2 = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/Abstract2Interface/InterfacePerson.cd");
    assertNotNull(ast2);

    // Create Output Path
    final Path outputDirectory = Paths.get("target/generated/cddiff-test/ow-alloy");

    // Initialize set of asts
    final Set<ASTCDCompilationUnit> asts = new HashSet<>();
    asts.add(ast1);
    asts.add(ast2);

    // Call generator
    OpenWorldGenerator.getInstance().generateModuleToFile(asts, outputDirectory.toFile(), true);
  }
}
