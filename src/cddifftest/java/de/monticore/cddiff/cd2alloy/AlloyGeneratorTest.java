/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.cd2alloy;

import de.monticore.cd2alloy.generator.CD2AlloyGenerator;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffTestBasis;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertNotNull;

/**
 * This is an integration test that e.g. checks that generated files exist as expected.
 */
public class AlloyGeneratorTest extends CDDiffTestBasis {

  @Test
  public void testVehicleManagement() {
    // Parse Test Module
    final ASTCDCompilationUnit ast = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/VehicleManagement/cd1.cd");
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
    final ASTCDCompilationUnit astV1 = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/Employees/Employees1.cd");
    assertNotNull(astV1);
    final ASTCDCompilationUnit astV2 = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/Employees/Employees2.cd");
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
    final ASTCDCompilationUnit astV1 = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/Employees/Employees3.cd");
    assertNotNull(astV1);
    final ASTCDCompilationUnit astV2 = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/Employees/Employees4.cd");
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
    final ASTCDCompilationUnit astV1 = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/Employees/Employees4.cd");
    assertNotNull(astV1);
    final ASTCDCompilationUnit astV2 = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/Employees/Employees3.cd");
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

}
