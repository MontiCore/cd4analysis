/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.cd2alloy;

import static org.junit.Assert.assertNotNull;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffTestBasis;
import de.monticore.cddiff.cd2alloy.generator.OpenWorldGenerator;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;

public class OpenWorldGeneratorTest extends CDDiffTestBasis {

  @Test
  public void testGenerator() {
    final ASTCDCompilationUnit ast1 =
        parseModel(
            "src/cddifftest/resources/de/monticore/cddiff/Abstract2Interface/AbstractPerson.cd");
    assertNotNull(ast1);

    final ASTCDCompilationUnit ast2 =
        parseModel(
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

  @Test
  public void testDigitalTwin() {
    final ASTCDCompilationUnit ast1 =
        parseModel("src/cddifftest/resources/de/monticore/cddiff/DigitalTwins/DigitalTwin3.cd");
    assertNotNull(ast1);

    final ASTCDCompilationUnit ast2 =
        parseModel("src/cddifftest/resources/de/monticore/cddiff/DigitalTwins/DigitalTwin2.cd");
    assertNotNull(ast2);

    // Create Output Path
    final Path outputDirectory = Paths.get("target/generated/cddiff-test/ow-alloy2");

    // Initialize set of asts
    final Set<ASTCDCompilationUnit> asts = new HashSet<>();
    asts.add(ast1);
    asts.add(ast2);

    // Call generator
    OpenWorldGenerator.getInstance().generateModuleToFile(asts, outputDirectory.toFile(), true);
  }

  @Test
  public void testEmployees() {
    final ASTCDCompilationUnit ast1 =
        parseModel("src/cddifftest/resources/de/monticore/cddiff/Employees/Employees0.cd");
    assertNotNull(ast1);

    final ASTCDCompilationUnit ast2 =
        parseModel("src/cddifftest/resources/de/monticore/cddiff/Employees/Employees1.cd");
    assertNotNull(ast2);

    // Create Output Path
    final Path outputDirectory = Paths.get("target/generated/cddiff-test/ow-alloy3");

    // Initialize set of asts
    final Set<ASTCDCompilationUnit> asts = new HashSet<>();
    asts.add(ast1);
    asts.add(ast2);

    // Call generator
    OpenWorldGenerator.getInstance().generateModuleToFile(asts, outputDirectory.toFile(), true);
  }

  @Test
  public void testLibraryV4V3() {
    final ASTCDCompilationUnit ast1 = parseModel("src/tooltest/resources/cddiff/LibraryV4.cd");
    assertNotNull(ast1);

    final ASTCDCompilationUnit ast2 = parseModel("src/tooltest/resources/cddiff/LibraryV3.cd");
    assertNotNull(ast2);

    // Create Output Path
    final Path outputDirectory = Paths.get("target/generated/cddiff-test/ow-alloy4");

    // Initialize set of asts
    final Set<ASTCDCompilationUnit> asts = new HashSet<>();
    asts.add(ast1);
    asts.add(ast2);

    // Call generator
    OpenWorldGenerator.getInstance().generateModuleToFile(asts, outputDirectory.toFile(), true);
  }

  @Test
  public void testMyCompanyV2V1() {
    final ASTCDCompilationUnit ast1 = parseModel("src/tooltest/resources/cd4analysis/MyCompanyV2.cd");
    assertNotNull(ast1);

    final ASTCDCompilationUnit ast2 = parseModel("src/tooltest/resources/cd4analysis/MyCompanyV1.cd");
    assertNotNull(ast2);

    // Create Output Path
    final Path outputDirectory = Paths.get("target/generated/cddiff-test/ow-alloy4");

    // Initialize set of asts
    final Set<ASTCDCompilationUnit> asts = new HashSet<>();
    asts.add(ast1);
    asts.add(ast2);

    // Call generator
    OpenWorldGenerator.getInstance().generateModuleToFile(asts, outputDirectory.toFile(), true);
  }
}
