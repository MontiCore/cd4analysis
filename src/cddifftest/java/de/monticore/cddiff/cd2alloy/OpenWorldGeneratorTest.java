package de.monticore.cddiff.cd2alloy;

import de.monticore.cd2alloy.generator.CD2AlloyGenerator;
import de.monticore.cd2alloy.generator.OpenWorldGenerator;
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
  public void testEmployee(){
    final ASTCDCompilationUnit ast = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/VehicleManagement/cd1.cd");
    assertNotNull(ast);

    // Create Output Path
    final Path outputDirectory = Paths.get("target/generated/cddiff-test/ow-alloy");

    // Initialize set of asts
    final Set<ASTCDCompilationUnit> asts = new HashSet<>();
    asts.add(ast);

    // Call generator
    OpenWorldGenerator.getInstance().generateModuleToFile(asts, outputDirectory.toFile(), true);
  }
}
