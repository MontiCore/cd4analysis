/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.cd2alloy;

import static org.junit.Assert.assertNotNull;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffTestBasis;
import de.monticore.cddiff.alloycddiff.CDSemantics;
import de.monticore.cddiff.alloycddiff.DiffModuleGenerator;
import de.monticore.cddiff.cd2alloy.generator.CD2AlloyGenerator;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;

/** This is an integration test that e.g. checks that generated files exist as expected. */
public class AlloyGeneratorTest extends CDDiffTestBasis {

  @Test
  public void testVehicleManagement() {
    // Parse Test Module
    final ASTCDCompilationUnit ast =
        parseModel("src/cddifftest/resources/de/monticore/cddiff/VehicleManagement/cd1.cd");
    assertNotNull(ast);

    // Create Output Path
    final Path outputDirectory = Paths.get("target/generated/cddiff-test/");

    // Initialize set of asts
    final Set<ASTCDCompilationUnit> asts = new HashSet<>();
    asts.add(ast);

    // Call generator
    CD2AlloyGenerator.getInstance().generateModuleToFile(asts, outputDirectory.toFile());
  }

  @Test
  public void testEmployees() {
    // Parse Test Modules
    final ASTCDCompilationUnit astV1 =
        parseModel("src/cddifftest/resources/de/monticore/cddiff/Employees/Employees1.cd");
    assertNotNull(astV1);
    final ASTCDCompilationUnit astV2 =
        parseModel("src/cddifftest/resources/de/monticore/cddiff/Employees/Employees2.cd");
    assertNotNull(astV2);

    // Create Output Path
    final Path outputDirectory = Paths.get("target/generated/cddiff-test/");

    // Initialize set of asts
    final Set<ASTCDCompilationUnit> asts = new HashSet<>();
    asts.add(astV1);
    asts.add(astV2);

    // Call generator
    CD2AlloyGenerator.getInstance().generateModuleToFile(asts, outputDirectory.toFile());
  }

  @Test
  public void testEmployeesWithPackages() {
    // Parse Test Modules
    final ASTCDCompilationUnit astV1 =
        parseModel("src/cddifftest/resources/de/monticore/cddiff/Employees/Employees3.cd");
    assertNotNull(astV1);
    final ASTCDCompilationUnit astV2 =
        parseModel("src/cddifftest/resources/de/monticore/cddiff/Employees/Employees4.cd");
    assertNotNull(astV2);

    // Create Output Path
    final Path outputDirectory = Paths.get("target/generated/cddiff-test/");

    // Initialize set of asts
    final Set<ASTCDCompilationUnit> asts = new HashSet<>();
    asts.add(astV1);
    asts.add(astV2);

    // Call generator
    CD2AlloyGenerator.getInstance().generateModuleToFile(asts, outputDirectory.toFile());
  }

  @Test
  public void testEmployeesWithNewSemantics() {
    // Parse Test Modules
    final ASTCDCompilationUnit astV1 =
        parseModel("src/cddifftest/resources/de/monticore/cddiff/Employees/Employees4.cd");
    assertNotNull(astV1);
    final ASTCDCompilationUnit astV2 =
        parseModel("src/cddifftest/resources/de/monticore/cddiff/Employees/Employees3.cd");
    assertNotNull(astV2);

    // Create Output Path
    final Path outputDirectory = Paths.get("target/generated/cddiff-test/new-semantics");

    // Initialize set of asts
    final Set<ASTCDCompilationUnit> asts = new HashSet<>();
    asts.add(astV1);
    asts.add(astV2);

    // Call generator
    CD2AlloyGenerator.getInstance().generateModuleToFile(asts, outputDirectory.toFile(), true);
  }

  @Test
  public void testEmployeesWithFullNames() {
    // Parse Test Modules
    final ASTCDCompilationUnit astV1 =
        parseModel(
            "src/cddifftest/resources/de/monticore/cddiff/FullNameEmployees/FNEmployees1" + ".cd");
    assertNotNull(astV1);
    final ASTCDCompilationUnit astV2 =
        parseModel(
            "src/cddifftest/resources/de/monticore/cddiff/FullNameEmployees/FNEmployees2" + ".cd");
    assertNotNull(astV2);

    // Create Output Path
    final Path outputDirectory = Paths.get("target/generated/cddiff-test/full-name");

    // Initialize set of asts
    final Set<ASTCDCompilationUnit> asts = new HashSet<>();
    asts.add(astV1);
    asts.add(astV2);

    // Call generator
    DiffModuleGenerator.generateDiffPredicateToFile(
        astV1, astV2, 20, CDSemantics.MULTI_INSTANCE_CLOSED_WORLD, outputDirectory.toFile());
  }

  @Test
  public void testDigitalTwins() {
    // Parse Test Modules
    final ASTCDCompilationUnit astV1 = parseModel("doc/DigitalTwin3.cd");
    assertNotNull(astV1);
    final ASTCDCompilationUnit astV2 = parseModel("doc/DigitalTwin2.cd");
    assertNotNull(astV2);

    // Initialize set of asts
    final Set<ASTCDCompilationUnit> asts = new HashSet<>();
    asts.add(astV1);
    asts.add(astV2);

    // Create Output Path

    Path outputDirectory = Paths.get("target/generated/cddiff-example/closed_world");

    // Call generator
    DiffModuleGenerator.generateDiffPredicateToFile(
        astV1, astV2, 7, CDSemantics.MULTI_INSTANCE_CLOSED_WORLD, outputDirectory.toFile());

    outputDirectory = Paths.get("target/generated/cddiff-example/open_world");

    // Call generator
    DiffModuleGenerator.generateDiffPredicateToFile(
        astV1, astV2, 7, CDSemantics.MULTI_INSTANCE_OPEN_WORLD, outputDirectory.toFile());
  }
}
