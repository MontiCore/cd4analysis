/* (c) https://github.com/MontiCore/monticore */
package de.monticore.trafo.constraintModularisationTests;

import static org.junit.Assert.assertTrue;

import de.monticore.cd._visitor.CDMemberVisitor;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.tf.*;
import java.io.IOException;
import java.util.Optional;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/** Created by Alexander Wilts on 31.10.2016. */
public class ConstraintTest {

  private final String DexInfrastructureCD =
      "src/test/resources/de/monticore/trafo/GroupDexInfrastructure.cd";

  @BeforeClass
  public static void init() {
    CD4CodeMill.init();
  }

  @Test
  public void testTrafoWithSchemaVariable() throws IOException {

    Optional<ASTCDCompilationUnit> astOpt = CD4CodeMill.parser().parse(DexInfrastructureCD);
    assertTrue(astOpt.isPresent());
    ASTCDCompilationUnit ast = astOpt.get();
    ASTCDCompilationUnit astClone = ast.deepClone();

    TrafoWithSchemaVariable trafo = new TrafoWithSchemaVariable(ast);
    while (trafo.doPatternMatching()) {
      trafo.doReplacement();
      trafo = new TrafoWithSchemaVariable(ast);
    }

    TrafoResultComparator comparator = new TrafoResultComparator(astClone, ast);
    assertTrue(comparator.checkAddedMethods("GroupImpl", "someMethod"));
    assertTrue(comparator.checkChangedMethodCount("GroupImpl", 1));
    assertTrue(comparator.checkAddedMethods("PersonImpl", "someMethod"));
    assertTrue(comparator.checkAddedMethods("GroupProxy", "someMethod"));
    assertTrue(comparator.checkAddedMethods("PersonProxy", "someMethod"));
    assertTrue(comparator.checkAddedMethods("PersonBuilder"));
    assertTrue(comparator.checkChangedMethodCount("PersonBuilder", 0));
    assertTrue(comparator.checkAddedMethods("GroupBuilder"));
  }

  @Test
  public void testTrafoWithIdentifierVariable() throws IOException {

    Optional<ASTCDCompilationUnit> astOpt = CD4CodeMill.parser().parse(DexInfrastructureCD);
    assertTrue(astOpt.isPresent());
    ASTCDCompilationUnit ast = astOpt.get();
    ASTCDCompilationUnit astClone = ast.deepClone();

    TrafoWithIdentifierVariable trafo = new TrafoWithIdentifierVariable(ast);
    while (trafo.doPatternMatching()) {
      trafo.doReplacement();
      trafo = new TrafoWithIdentifierVariable(ast);
    }

    TrafoResultComparator comparator = new TrafoResultComparator(astClone, ast);
    assertTrue(comparator.checkAddedMethods("GroupImpl", "someMethod"));
    assertTrue(comparator.checkChangedMethodCount("GroupImpl", 1));
    assertTrue(comparator.checkAddedMethods("PersonImpl", "someMethod"));
    assertTrue(comparator.checkAddedMethods("GroupProxy", "someMethod"));
    assertTrue(comparator.checkAddedMethods("PersonProxy", "someMethod"));
    assertTrue(comparator.checkAddedMethods("PersonBuilder"));
    assertTrue(comparator.checkChangedMethodCount("PersonBuilder", 0));
    assertTrue(comparator.checkAddedMethods("GroupBuilder"));
  }

  @Test
  public void testTrafoWithList() throws IOException {

    Optional<ASTCDCompilationUnit> astOpt = CD4CodeMill.parser().parse(DexInfrastructureCD);
    assertTrue(astOpt.isPresent());
    ASTCDCompilationUnit ast = astOpt.get();
    ASTCDCompilationUnit astClone = ast.deepClone();

    TrafoWithList trafo = new TrafoWithList(ast);
    while (trafo.doPatternMatching()) {
      System.out.println("Matched");
      trafo.doReplacement();
      trafo = new TrafoWithList(ast);
    }
    //    System.out.println(new CDPrettyPrinterConcreteVisitor(new
    // IndentPrinter()).prettyprint(ast));

    TrafoResultComparator comparator = new TrafoResultComparator(astClone, ast);
    assertTrue(comparator.checkAddedMethods("GroupImpl", "someMethod"));
    assertTrue(comparator.checkChangedMethodCount("GroupImpl", 1));
    assertTrue(comparator.checkAddedMethods("PersonImpl", "someMethod"));
    assertTrue(comparator.checkChangedMethodCount("PersonImpl", 1));
    assertTrue(comparator.checkAddedMethods("GroupProxy", "someMethod"));
    assertTrue(comparator.checkAddedMethods("PersonProxy", "someMethod"));
    assertTrue(comparator.checkAddedMethods("PersonBuilder", "someMethod"));
    assertTrue(comparator.checkAddedMethods("GroupBuilder", "someMethod"));
  }

  @Test
  public void testTrafoOptional() throws IOException {

    Optional<ASTCDCompilationUnit> astOpt = CD4CodeMill.parser().parse(DexInfrastructureCD);
    assertTrue(astOpt.isPresent());
    ASTCDCompilationUnit ast = astOpt.get();
    ASTCDCompilationUnit astClone = ast.deepClone();

    TrafoOptional trafo = new TrafoOptional(ast);

    while (trafo.doPatternMatching()) {
      trafo.doReplacement();
      trafo = new TrafoOptional(ast);
    }

    TrafoResultComparator comparator = new TrafoResultComparator(astClone, ast);
    assertTrue(comparator.checkRemovedMethods("PersonManager"));
    assertTrue(comparator.checkRemovedMethods("PersonImpl", "somePredefinedMethod"));
  }

  @Test
  public void testTrafoListWithOptional() throws IOException {

    Optional<ASTCDCompilationUnit> astOpt = CD4CodeMill.parser().parse(DexInfrastructureCD);
    assertTrue(astOpt.isPresent());
    ASTCDCompilationUnit ast = astOpt.get();
    ASTCDCompilationUnit astClone = ast.deepClone();

    TrafoListWithOptional trafo = new TrafoListWithOptional(ast);
    while (trafo.doPatternMatching()) {
      trafo.doReplacement();
      trafo = new TrafoListWithOptional(ast);
    }

    TrafoResultComparator comparator = new TrafoResultComparator(astClone, ast);
    assertTrue(comparator.checkAddedMethods("GroupManager", "someMethod"));
    assertTrue(comparator.checkAddedMethods("GroupImpl", "someMethod"));
    assertTrue(comparator.checkAddedMethods("PersonImpl"));
  }

  @Test
  public void testTrafoOptionalWithNot() throws IOException {

    Optional<ASTCDCompilationUnit> astOpt = CD4CodeMill.parser().parse(DexInfrastructureCD);
    assertTrue(astOpt.isPresent());
    ASTCDCompilationUnit ast = astOpt.get();
    ASTCDCompilationUnit astClone = ast.deepClone();

    TrafoOptionalWithNot trafo = new TrafoOptionalWithNot(ast);
    while (trafo.doPatternMatching()) {
      trafo.doReplacement();
      trafo = new TrafoOptionalWithNot(ast);
    }

    TrafoResultComparator comparator = new TrafoResultComparator(astClone, ast);
    assertTrue(comparator.checkChangedMethodCount("PersonImpl", 1));
    assertTrue(comparator.checkChangedMethodCount("PersonProxy", 0));
    assertTrue(comparator.checkChangedMethodCount("GroupImpl", 1));
    assertTrue(comparator.checkAddedMethods("GroupProxy", "someMethod"));
    assertTrue(comparator.checkAddedMethods("PersonImpl", "someMethod"));
    assertTrue(comparator.checkAddedMethods("GroupImpl", "someMethod"));
    assertTrue(comparator.checkChangedMethodCount("GroupProxy", 1));
  }

  @Test
  public void testTrafoNotInConstraint() throws IOException {

    Optional<ASTCDCompilationUnit> astOpt = CD4CodeMill.parser().parse(DexInfrastructureCD);
    assertTrue(astOpt.isPresent());
    ASTCDCompilationUnit ast = astOpt.get();
    ASTCDCompilationUnit astClone = ast.deepClone();

    TrafoNotInConstraint trafo = new TrafoNotInConstraint(ast);
    while (trafo.doPatternMatching()) {
      trafo.doReplacement();
      trafo = new TrafoNotInConstraint(ast);
    }

    TrafoResultComparator comparator = new TrafoResultComparator(astClone, ast);
    assertTrue(comparator.checkAddedMethods("GroupImpl", "someMethod"));
    assertTrue(comparator.checkAddedMethods("PersonImpl", "someMethod"));
    assertTrue(comparator.checkChangedMethodCount("GroupImpl", 1));
    assertTrue(comparator.checkChangedMethodCount("PersonImpl", 1));
  }

  @Test
  public void testTrafoNotInConstraint2() throws IOException {

    Optional<ASTCDCompilationUnit> astOpt = CD4CodeMill.parser().parse(DexInfrastructureCD);
    assertTrue(astOpt.isPresent());
    ASTCDCompilationUnit ast = astOpt.get();
    ASTCDCompilationUnit astClone = ast.deepClone();

    TrafoNotInConstraint2 trafo = new TrafoNotInConstraint2(ast);
    while (trafo.doPatternMatching()) {
      trafo.doReplacement();
      trafo = new TrafoNotInConstraint2(ast);
    }

    TrafoResultComparator comparator = new TrafoResultComparator(astClone, ast);
    assertTrue(comparator.checkAddedClass("GroupBase"));
    assertTrue(comparator.checkAddedClass("PersonBase"));
    assertTrue(comparator.checkAddedMethods("GroupProxy", "someMethod"));
    assertTrue(comparator.checkAddedMethods("PersonProxy", "someMethod"));
    assertTrue(comparator.checkChangedClassCount(2));
  }

  @Test
  public void testTrafoNotInOrConstraint() throws IOException {

    Optional<ASTCDCompilationUnit> astOpt = CD4CodeMill.parser().parse(DexInfrastructureCD);
    assertTrue(astOpt.isPresent());
    ASTCDCompilationUnit ast = astOpt.get();
    ASTCDCompilationUnit astClone = ast.deepClone();

    TrafoNotInOrConstraint trafo = new TrafoNotInOrConstraint(ast);
    while (trafo.doPatternMatching()) {
      trafo.doReplacement();
      trafo = new TrafoNotInOrConstraint(ast);
    }

    TrafoResultComparator comparator = new TrafoResultComparator(astClone, ast);
    assertTrue(comparator.checkAddedMethods("GroupProxy", "someMethod"));
    assertTrue(comparator.checkAddedMethods("PersonProxy", "someMethod"));
    assertTrue(comparator.checkChangedMethodCount("GroupImpl", 0));
    assertTrue(comparator.checkChangedMethodCount("PersonImpl", 0));
    assertTrue(comparator.checkChangedMethodCount("GroupProxy", 1));
    assertTrue(comparator.checkChangedMethodCount("PersonProxy", 1));
  }

  @Test
  public void testTrafoNotInOrConstraint2() throws IOException {

    Optional<ASTCDCompilationUnit> astOpt = CD4CodeMill.parser().parse(DexInfrastructureCD);
    assertTrue(astOpt.isPresent());
    ASTCDCompilationUnit ast = astOpt.get();
    ASTCDCompilationUnit astClone = ast.deepClone();

    TrafoNotInOrConstraint2 trafo = new TrafoNotInOrConstraint2(ast);
    while (trafo.doPatternMatching()) {
      trafo.doReplacement();
      trafo = new TrafoNotInOrConstraint2(ast);
    }

    TrafoResultComparator comparator = new TrafoResultComparator(astClone, ast);
    assertTrue(comparator.checkAddedMethods("GroupImpl", "someMethod"));
    assertTrue(comparator.checkAddedMethods("PersonImpl", "someMethod"));
    assertTrue(comparator.checkAddedMethods("GroupProxy", "someMethod"));
    assertTrue(comparator.checkAddedMethods("PersonProxy", "someMethod"));
    assertTrue(comparator.checkChangedMethodCount("GroupImpl", 1));
    assertTrue(comparator.checkChangedMethodCount("PersonImpl", 1));
    assertTrue(comparator.checkChangedMethodCount("GroupProxy", 1));
    assertTrue(comparator.checkChangedMethodCount("PersonProxy", 1));
  }

  @Test
  public void testTrafoNotInOrConstraint3() throws IOException {

    Optional<ASTCDCompilationUnit> astOpt = CD4CodeMill.parser().parse(DexInfrastructureCD);
    assertTrue(astOpt.isPresent());
    ASTCDCompilationUnit ast = astOpt.get();
    ASTCDCompilationUnit astClone = ast.deepClone();

    TrafoNotInOrConstraint3 trafo = new TrafoNotInOrConstraint3(ast);
    while (trafo.doPatternMatching()) {
      trafo.doReplacement();
      trafo = new TrafoNotInOrConstraint3(ast);
    }

    TrafoResultComparator comparator = new TrafoResultComparator(astClone, ast);
    assertTrue(comparator.checkAddedClass("GroupFactory"));
    assertTrue(comparator.checkAddedClass("PersonFactory"));
    assertTrue(comparator.checkChangedClassCount(2));
  }

  @Test
  public void testTrafoOptionalAndNot() throws IOException {

    Optional<ASTCDCompilationUnit> astOpt = CD4CodeMill.parser().parse(DexInfrastructureCD);
    assertTrue(astOpt.isPresent());
    ASTCDCompilationUnit ast = astOpt.get();
    ASTCDCompilationUnit astClone = ast.deepClone();

    TrafoOptionalAndNot trafo = new TrafoOptionalAndNot(ast);
    while (trafo.doPatternMatching()) {
      trafo.doReplacement();
      trafo = new TrafoOptionalAndNot(ast);
    }

    TrafoResultComparator comparator = new TrafoResultComparator(astClone, ast);
    assertTrue(comparator.checkAddedMethods("GroupImpl", "someMethod"));
    assertTrue(comparator.checkAddedMethods("PersonImpl", "someMethod"));
    assertTrue(comparator.checkAddedMethods("GroupProxy", "someMethod"));
    assertTrue(comparator.checkAddedMethods("PersonProxy", "someMethod"));
    assertTrue(comparator.checkChangedMethodCount("GroupImpl", 1));
    assertTrue(comparator.checkChangedMethodCount("PersonImpl", 1));
    assertTrue(comparator.checkChangedMethodCount("GroupProxy", 1));
    assertTrue(comparator.checkChangedMethodCount("PersonProxy", 1));
    assertTrue(comparator.checkAddedClass("GroupBase"));
    assertTrue(comparator.checkAddedClass("PersonBase"));
    assertTrue(comparator.checkChangedClassCount(3));
  }

  @Test
  public void testTrafoOrConstraint() throws IOException {

    Optional<ASTCDCompilationUnit> astOpt = CD4CodeMill.parser().parse(DexInfrastructureCD);
    assertTrue(astOpt.isPresent());
    ASTCDCompilationUnit ast = astOpt.get();
    ASTCDCompilationUnit astClone = ast.deepClone();

    TrafoOrConstraint trafo = new TrafoOrConstraint(ast);
    while (trafo.doPatternMatching()) {
      trafo.doReplacement();
      trafo = new TrafoOrConstraint(ast);
    }

    TrafoResultComparator comparator = new TrafoResultComparator(astClone, ast);
    assertTrue(comparator.checkAddedMethods("GroupImpl", "someMethod"));
    assertTrue(comparator.checkAddedMethods("PersonImpl", "someMethod"));
    assertTrue(comparator.checkAddedMethods("GroupProxy", "someMethod"));
    assertTrue(comparator.checkAddedMethods("PersonProxy", "someMethod"));
    assertTrue(comparator.checkChangedMethodCount("GroupImpl", 1));
    assertTrue(comparator.checkChangedMethodCount("PersonImpl", 1));
    assertTrue(comparator.checkChangedMethodCount("GroupProxy", 1));
    assertTrue(comparator.checkChangedMethodCount("PersonProxy", 1));
  }

  @Test
  public void testTrafoOptionalInAndConstraint() throws IOException {

    Optional<ASTCDCompilationUnit> astOpt = CD4CodeMill.parser().parse(DexInfrastructureCD);
    assertTrue(astOpt.isPresent());
    ASTCDCompilationUnit ast = astOpt.get();
    ASTCDCompilationUnit astClone = ast.deepClone();

    TrafoOptionalInAndConstraint trafo = new TrafoOptionalInAndConstraint(ast);
    while (trafo.doPatternMatching()) {
      trafo.doReplacement();
      trafo = new TrafoOptionalInAndConstraint(ast);
    }

    TrafoResultComparator comparator = new TrafoResultComparator(astClone, ast);
    assertTrue(comparator.checkAddedMethods("GroupImpl", "someMethod"));
    assertTrue(comparator.checkAddedMethods("PersonImpl", "someMethod"));
    assertTrue(comparator.checkAddedMethods("GroupProxy", "someMethod"));
    assertTrue(comparator.checkAddedMethods("PersonProxy", "someMethod"));
    assertTrue(comparator.checkChangedMethodCount("GroupImpl", 1));
    assertTrue(comparator.checkChangedMethodCount("PersonImpl", 1));
    assertTrue(comparator.checkChangedMethodCount("GroupProxy", 1));
    assertTrue(comparator.checkChangedMethodCount("PersonProxy", 1));
  }

  @Test
  public void testTrafoOptionalInOrConstraint() throws IOException {

    Optional<ASTCDCompilationUnit> astOpt = CD4CodeMill.parser().parse(DexInfrastructureCD);
    assertTrue(astOpt.isPresent());
    ASTCDCompilationUnit ast = astOpt.get();
    ASTCDCompilationUnit astClone = ast.deepClone();

    TrafoOptionalInOrConstraint trafo = new TrafoOptionalInOrConstraint(ast);
    while (trafo.doPatternMatching()) {
      trafo.doReplacement();
      trafo = new TrafoOptionalInOrConstraint(ast);
    }

    TrafoResultComparator comparator = new TrafoResultComparator(astClone, ast);
    assertTrue(comparator.checkAddedMethods("GroupImpl", "someMethod"));
    assertTrue(comparator.checkAddedMethods("PersonImpl", "someMethod"));
    assertTrue(comparator.checkAddedMethods("PersonProxy", "someMethod"));
    assertTrue(comparator.checkChangedMethodCount("GroupImpl", 1));
    assertTrue(comparator.checkChangedMethodCount("PersonImpl", 1));
    assertTrue(comparator.checkChangedMethodCount("PersonBuilder", 0));
    assertTrue(comparator.checkChangedMethodCount("GroupProxy", 0));
    assertTrue(comparator.checkChangedMethodCount("PersonProxy", 1));
  }

  @Test
  @Ignore // TODO: This test fails due to the order of interfaces and classes
  public void testTrafoOptionalInOrInList() throws IOException {
    Optional<ASTCDCompilationUnit> astOpt = CD4CodeMill.parser().parse(DexInfrastructureCD);

    CD4CodeMill.scopesGenitorDelegator().createFromAST(astOpt.get());

    assertTrue(astOpt.isPresent());
    ASTCDCompilationUnit ast = astOpt.get();
    ASTCDCompilationUnit astClone = ast.deepClone();

    TrafoOptionalInOrInList trafo = new TrafoOptionalInOrInList(ast);
    while (trafo.doPatternMatching()) {
      trafo.doReplacement();
      trafo = new TrafoOptionalInOrInList(ast);
    }

    TrafoResultComparator comparator = new TrafoResultComparator(astClone, ast);
    assertTrue(comparator.checkAddedMethods("GroupImpl", "someMethod"));
    assertTrue(comparator.checkAddedMethods("PersonImpl", "someMethod"));
    assertTrue(comparator.checkAddedMethods("PersonProxy", "someMethod"));
    assertTrue(comparator.checkChangedMethodCount("GroupImpl", 1));
    assertTrue(comparator.checkChangedMethodCount("PersonImpl", 1));
    assertTrue(comparator.checkChangedMethodCount("PersonBuilder", 0));
    assertTrue(comparator.checkChangedMethodCount("GroupProxy", 0));
    assertTrue(comparator.checkChangedMethodCount("PersonProxy", 1));
  }

  @Test
  public void testTrafoNotInOptional() throws IOException {

    Optional<ASTCDCompilationUnit> astOpt = CD4CodeMill.parser().parse(DexInfrastructureCD);
    assertTrue(astOpt.isPresent());
    ASTCDCompilationUnit ast = astOpt.get();
    ASTCDCompilationUnit astClone = ast.deepClone();

    TrafoNotInOptional trafo = new TrafoNotInOptional(ast);
    while (trafo.doPatternMatching()) {
      trafo.doReplacement();
      trafo = new TrafoNotInOptional(ast);
    }

    TrafoResultComparator comparator = new TrafoResultComparator(astClone, ast);
    assertTrue(comparator.checkAddedMethods("GroupImpl", "someMethod"));
    assertTrue(comparator.checkAddedMethods("PersonImpl", "someMethod"));
    assertTrue(comparator.checkAddedMethods("GroupProxy", "someMethod"));
    assertTrue(comparator.checkChangedMethodCount("GroupImpl", 1));
    assertTrue(comparator.checkChangedMethodCount("PersonImpl", 1));
    assertTrue(comparator.checkChangedMethodCount("GroupProxy", 1));
    assertTrue(comparator.checkChangedMethodCount("PersonProxy", 0));
  }

  @Test
  public void testTrafoMultipleLists() throws IOException {

    Optional<ASTCDCompilationUnit> astOpt = CD4CodeMill.parser().parse(DexInfrastructureCD);
    assertTrue(astOpt.isPresent());
    ASTCDCompilationUnit ast = astOpt.get();
    ASTCDCompilationUnit astClone = ast.deepClone();

    TrafoMultipleLists trafo = new TrafoMultipleLists(ast);
    while (trafo.doPatternMatching()) {
      trafo.doReplacement();
      trafo = new TrafoMultipleLists(ast);
    }

    TrafoResultComparator comparator = new TrafoResultComparator(astClone, ast);
    assertTrue(comparator.checkAddedMethods("GroupImpl", "someMethod"));
    assertTrue(comparator.checkAddedMethods("PersonImpl", "someMethod"));
    assertTrue(comparator.checkAddedMethods("GroupProxy", "someMethod"));
    assertTrue(comparator.checkAddedMethods("PersonProxy", "someMethod"));
    assertTrue(comparator.checkAddedMethods("Group", "someMethod"));
    assertTrue(comparator.checkAddedMethods("Person", "someMethod"));
    assertTrue(comparator.checkChangedMethodCount("GroupImpl", 1));
    assertTrue(comparator.checkChangedMethodCount("PersonImpl", 1));
    assertTrue(comparator.checkChangedMethodCount("GroupProxy", 1));
    assertTrue(comparator.checkChangedMethodCount("PersonProxy", 1));
    assertTrue(comparator.checkChangedMethodCount("Group", 1));
    assertTrue(comparator.checkChangedMethodCount("Person", 1));
  }

//  @Test
//  public void testTrafoTwoOptionalsInAssignment() throws IOException {
//
//    Optional<ASTCDCompilationUnit> astOpt = CD4CodeMill.parser().parse(DexInfrastructureCD);
//    assertTrue(astOpt.isPresent());
//    ASTCDCompilationUnit ast = astOpt.get();
//    ASTCDCompilationUnit astClone = ast.deepClone();
//
//    TrafoTwoOptionalsInAssignment trafo = new TrafoTwoOptionalsInAssignment(ast);
//    while (trafo.doPatternMatching()) {
//      trafo.doReplacement();
//      trafo = new TrafoTwoOptionalsInAssignment(ast);
//    }
//
//    TrafoResultComparator comparator = new TrafoResultComparator(astClone, ast);
//    assertTrue(comparator.checkAddedAttribute("Group", "firstNamelastName"));
//  }

//  @Test
//  public void testTrafoTwoOptionalsInAssignment2() throws IOException {
//
//    Optional<ASTCDCompilationUnit> astOpt = CD4CodeMill.parser().parse(DexInfrastructureCD);
//    assertTrue(astOpt.isPresent());
//    ASTCDCompilationUnit ast = astOpt.get();
//    ASTCDCompilationUnit astClone = ast.deepClone();
//
//    TrafoTwoOptionalsInAssignment2 trafo = new TrafoTwoOptionalsInAssignment2(ast);
//    while (trafo.doPatternMatching()) {
//      trafo.doReplacement();
//      trafo = new TrafoTwoOptionalsInAssignment2(ast);
//    }
//
//    TrafoResultComparator comparator = new TrafoResultComparator(astClone, ast);
//    assertTrue(comparator.checkAddedAttribute("Group", "firstNamelastName"));
//  }

  /**
   * @param ast contains some classes that should be verified
   * @param className name of a specific class to be verified
   * @param methodNames names of the methods that should be contained in the class
   * @return true, if the class with the given names contains the methods with given methodNames.
   *     returns false otherwise.
   *     <p>Checks if a class with the given name in the given ast contains exactly(!) the methods
   *     with the given methodNames
   */
  // TODO: Can be removed after performance tests
  private boolean containsMethods(
      ASTCDCompilationUnit ast, String className, String... methodNames) {

    Optional<ASTCDClass> cDClass =
        ast.getCDDefinition().getCDClassesList().stream()
            .filter(x -> x.getName().equals(className))
            .findAny();
    if (!cDClass.isPresent()) {
      return false;
    } else {
      // Check if class contains the correct number of methods
      if (!(cDClass.get().getCDMethodList().size() == methodNames.length)) {
        return false;
      }

      // Check for each methodName if the method is present in the given class
      for (String methodName : methodNames) {
        Optional<ASTCDMethod> cDMethod =
            cDClass.get().getCDMemberList(CDMemberVisitor.Options.METHODS).stream()
                .map(x -> (ASTCDMethod) x)
                .filter(x -> x.getName().equals(methodName))
                .findAny();
        if (!cDMethod.isPresent()) {
          return false;
        }
      }
    }
    return true;
  }
}
