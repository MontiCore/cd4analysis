package de.monticore.cddiff.ow2cw;

import de.monticore.cd.facade.CDAttributeFacade;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cddiff.CDDiffTestBasis;
import de.monticore.ow2cw.CDInheritanceHelper;
import de.monticore.ow2cw.ReductionTrafo;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ReductionTrafoTest extends CDDiffTestBasis {

  @Test
  public void testTrafo() {

    CD4CodeMill.globalScope().clear();

    ASTCDCompilationUnit m1Ast = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/Employees" + "/Employees5.cd");
    ASTCDCompilationUnit m2Ast = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/Employees" + "/Employees6.cd");

    ReductionTrafo trafo = new ReductionTrafo();
    trafo.transform(m1Ast, m2Ast);

    CD4CodeFullPrettyPrinter pprinter = new CD4CodeFullPrettyPrinter();
    m1Ast.accept(pprinter.getTraverser());
    String cd1 = pprinter.getPrinter().getContent();

    pprinter = new CD4CodeFullPrettyPrinter();
    m2Ast.accept(pprinter.getTraverser());
    String cd2 = pprinter.getPrinter().getContent();

    // Set Output Path
    String outputPath = "target/generated/cddiff-test/trafo/";
    Path outputFile1 = Paths.get(outputPath, m1Ast.getCDDefinition().getName() + ".cd");
    Path outputFile2 = Paths.get(outputPath, m2Ast.getCDDefinition().getName() + ".cd");

    // Write results into a file
    try {
      FileUtils.writeStringToFile(outputFile1.toFile(), cd1, Charset.defaultCharset());
      FileUtils.writeStringToFile(outputFile2.toFile(), cd2, Charset.defaultCharset());
    }
    catch (IOException e) {
      e.printStackTrace();
    }

  }

  @Test
  public void testTrafoWithPackages() {
    CD4CodeMill.globalScope().clear();

    String outputPath = "target/generated/cddiff-test/trafo-with-packages/";

    ASTCDCompilationUnit m1Ast = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/Employees/Employees8.cd");
    ASTCDCompilationUnit m2Ast = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/Employees/Employees7.cd");

    ReductionTrafo trafo = new ReductionTrafo();
    trafo.transform(m1Ast, m2Ast);

    CD4CodeFullPrettyPrinter pprinter = new CD4CodeFullPrettyPrinter();
    m1Ast.accept(pprinter.getTraverser());
    String cd1 = pprinter.getPrinter().getContent();

    pprinter = new CD4CodeFullPrettyPrinter();
    m2Ast.accept(pprinter.getTraverser());
    String cd2 = pprinter.getPrinter().getContent();

    Path outputFile1 = Paths.get(outputPath, m1Ast.getCDDefinition().getName() + ".cd");
    Path outputFile2 = Paths.get(outputPath, m2Ast.getCDDefinition().getName() + ".cd");

    // Write results into a file
    try {
      FileUtils.writeStringToFile(outputFile1.toFile(), cd1, Charset.defaultCharset());
      FileUtils.writeStringToFile(outputFile2.toFile(), cd2, Charset.defaultCharset());
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testCopyInheritance() {
    ASTCDCompilationUnit lecture1 = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/Lecture/Lecture1.cd");

    ASTCDCompilationUnit lecture2 = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/Lecture/Lecture2.cd");

    lecture1.getCDDefinition()
        .removeAllCDElements(lecture1.getCDDefinition().getCDAssociationsList());
    lecture2.getCDDefinition()
        .removeAllCDElements(lecture2.getCDDefinition().getCDAssociationsList());

    lecture2.getCDDefinition().setName(lecture1.getCDDefinition().getName());

    CD4CodeMill.scopesGenitorDelegator().createFromAST(lecture1);
    CD4CodeMill.scopesGenitorDelegator().createFromAST(lecture2);

    ReductionTrafo trafo = new ReductionTrafo();
    trafo.copyInheritance(lecture1, lecture2);

    CD4CodeFullPrettyPrinter pprinter = new CD4CodeFullPrettyPrinter();
    lecture1.accept(pprinter.getTraverser());
    String cd1 = pprinter.getPrinter().getContent();

    pprinter = new CD4CodeFullPrettyPrinter();
    lecture2.accept(pprinter.getTraverser());
    String cd2 = pprinter.getPrinter().getContent();

    Assert.assertEquals(cd1, cd2);

  }

  @Test
  public void testRemoveRedundantAttributes() {
    ASTCDCompilationUnit employees8 = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/Employees/Employees8.cd");

    List<String> subList = new ArrayList<>();
    subList.add("ins.Manager");
    subList.add("emp.Employee");
    subList.add("Person");

    List<ASTCDType> typeList = new ArrayList<>();
    typeList.addAll(employees8.getCDDefinition().getCDClassesList());
    typeList.addAll(employees8.getCDDefinition().getCDInterfacesList());

    for (ASTCDType type : typeList) {
      type.addCDMember(CDAttributeFacade.getInstance()
          .createAttribute(CD4CodeMill.modifierBuilder().build(), "Date", "test"));
    }
    ReductionTrafo trafo = new ReductionTrafo();
    trafo.removeRedundantAttributes(employees8);

    for (ASTCDType type : typeList) {
      if (subList.stream().anyMatch(sub -> sub.equals(type.getSymbol().getFullName()))) {
        Assert.assertFalse(type.getCDAttributeList()
            .stream()
            .anyMatch(attribute -> attribute.getName().equals("test")));
      }
      else {
        Assert.assertTrue(type.getCDAttributeList()
            .stream()
            .anyMatch(attribute -> attribute.getName().equals("test")));
      }
    }
  }

  @Test
  public void testCreateCommonInterface() {
    ASTCDCompilationUnit employees8 = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/Employees/Employees8.cd");
    new ReductionTrafo().createCommonInterface(employees8, "Object");
    ICD4CodeArtifactScope scope = CD4CodeMill.scopesGenitorDelegator().createFromAST(employees8);

    Assert.assertTrue(employees8.getCDDefinition()
        .getCDClassesList()
        .stream()
        .allMatch(
            cdClass -> CDInheritanceHelper.isSuperOf("Object", cdClass.getSymbol().getFullName(),
                scope)));

    Assert.assertTrue(employees8.getCDDefinition()
        .getCDInterfacesList()
        .stream()
        .allMatch(cdInterface -> CDInheritanceHelper.isSuperOf("Object",
            cdInterface.getSymbol().getFullName(), scope)));
  }


}
