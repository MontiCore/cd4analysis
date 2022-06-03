package de.monticore.cddiff.ow2cw;

import de.monticore.cd.facade.CDExtendUsageFacade;
import de.monticore.cd.facade.CDInterfaceUsageFacade;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffTestBasis;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.ow2cw.expander.FullExpander;
import de.monticore.ow2cw.expander.OpenWorldExpander;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class FullExpanderTest extends CDDiffTestBasis {

  @Test
  public void testAddNewSubClass() {
    ASTCDCompilationUnit machines = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/Machines/Machines2.cd");
    CD4CodeMill.scopesGenitorDelegator().createFromAST(machines);

    FullExpander fullExpander = new FullExpander(new OpenWorldExpander(machines));

    for (ASTCDClass superClass : machines.getCDDefinition().getCDClassesList()) {
      if (superClass.getSymbol().getFullName().equals("future.FlyingCar")) {
        fullExpander.addNewSubClass("SpaceCar", superClass);
      }
    }
    for (ASTCDInterface astcdInterface : machines.getCDDefinition().getCDInterfacesList()) {
      if (astcdInterface.getSymbol().getFullName().equals("UI")) {
        fullExpander.addNewSubClass("WeirdUI", astcdInterface);
      }
    }

    CD4CodeFullPrettyPrinter pprinter = new CD4CodeFullPrettyPrinter();
    machines.accept(pprinter.getTraverser());
    String result = pprinter.getPrinter().getContent();

    pprinter = new CD4CodeFullPrettyPrinter();
    machines = parseModel("src/cddifftest/resources/de/monticore/cddiff/Machines/Machines3.cd");
    machines.getCDDefinition().setName("Machines2");
    CD4CodeMill.scopesGenitorDelegator().createFromAST(machines);
    machines.accept(pprinter.getTraverser());

    Assert.assertEquals(result, pprinter.getPrinter().getContent());

  }

  @Test
  public void testAddClass2Package() {
    ASTCDCompilationUnit machines = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/Machines/Machines1.cd");
    CD4CodeMill.scopesGenitorDelegator().createFromAST(machines);

    FullExpander fullExpander = new FullExpander(new OpenWorldExpander(machines));

    ASTCDClass car = CD4CodeMill.cDClassBuilder()
        .setModifier(CD4CodeMill.modifierBuilder().build())
        .setName("Car")
        .setCDExtendUsage(CDExtendUsageFacade.getInstance().createCDExtendUsage("Machine"))
        .setCDInterfaceUsage(CDInterfaceUsageFacade.getInstance().createCDInterfaceUsage("UI"))
        .build();

    fullExpander.addType2Package(car.deepClone(), "old");
    fullExpander.addType2Package(car.deepClone(), "new");

    car = CD4CodeMill.cDClassBuilder()
        .setModifier(CD4CodeMill.modifierBuilder().build())
        .setName("FlyingCar")
        .setCDExtendUsage(CDExtendUsageFacade.getInstance().createCDExtendUsage("new.Car"))
        .setCDInterfaceUsageAbsent()
        .build();

    fullExpander.addType2Package(car.deepClone(), "future");

    CD4CodeFullPrettyPrinter pprinter = new CD4CodeFullPrettyPrinter();
    machines.accept(pprinter.getTraverser());
    String result = pprinter.getPrinter().getContent();

    pprinter = new CD4CodeFullPrettyPrinter();
    machines = parseModel("src/cddifftest/resources/de/monticore/cddiff/Machines/Machines2.cd");
    machines.getCDDefinition().setName("Machines1");
    CD4CodeMill.scopesGenitorDelegator().createFromAST(machines);
    machines.accept(pprinter.getTraverser());

    Assert.assertEquals(result, pprinter.getPrinter().getContent());

  }

  @Test
  public void testAddClone() {
    ASTCDCompilationUnit machines4 = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/Machines/Machines4.cd");

    ASTCDCompilationUnit machines5 = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/Machines/Machines5.cd");

    CD4CodeMill.scopesGenitorDelegator().createFromAST(machines4);
    ICD4CodeArtifactScope scope5 = CD4CodeMill.scopesGenitorDelegator().createFromAST(machines5);

    FullExpander fullExpander = new FullExpander(new OpenWorldExpander(machines4));

    scope5.resolveCDTypeDown("new.Truck").ifPresent(type -> fullExpander.addClone(type.getAstNode()));
    scope5.resolveCDTypeDown("ancient.Wheel")
        .ifPresent(type -> fullExpander.addClone(type.getAstNode()));

    CD4CodeFullPrettyPrinter pprinter = new CD4CodeFullPrettyPrinter();
    machines4.accept(pprinter.getTraverser());
    String result = pprinter.getPrinter().getContent();

    pprinter = new CD4CodeFullPrettyPrinter();
    machines5.getCDDefinition().setName("Machines4");
    machines5.accept(pprinter.getTraverser());

    Assert.assertEquals(result, pprinter.getPrinter().getContent());
  }

  @Test
  public void testAddDummyClass() {
    ASTCDCompilationUnit machines3 = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/Machines/Machines3.cd");

    ASTCDCompilationUnit machines4 = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/Machines/Machines4.cd");

    CD4CodeMill.scopesGenitorDelegator().createFromAST(machines3);
    CD4CodeMill.scopesGenitorDelegator().createFromAST(machines4);

    FullExpander fullExpander = new FullExpander(new OpenWorldExpander(machines3));

    for (ASTCDClass astcdClass : machines4.getCDDefinition().getCDClassesList()) {
      if (astcdClass.getSymbol().getFullName().equals("future.FutureThing")) {
        fullExpander.addDummyClass(astcdClass);
      }
    }

    CD4CodeFullPrettyPrinter pprinter = new CD4CodeFullPrettyPrinter();
    machines3.accept(pprinter.getTraverser());
    String result = pprinter.getPrinter().getContent();

    pprinter = new CD4CodeFullPrettyPrinter();
    machines4.getCDDefinition().setName("Machines3");
    machines4.accept(pprinter.getTraverser());

    Assert.assertEquals(result, pprinter.getPrinter().getContent());
  }

  @Test
  public void testAddMissingTypesAndAttributes() {
    ASTCDCompilationUnit lecture4 = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/Lecture/Lecture4.cd");
    ASTCDCompilationUnit lecture5 = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/Lecture/Lecture5.cd");

    CD4CodeMill.scopesGenitorDelegator().createFromAST(lecture4);
    CD4CodeMill.scopesGenitorDelegator().createFromAST(lecture5);

    FullExpander fullExpander = new FullExpander(new OpenWorldExpander(lecture5));

    fullExpander.addMissingTypesAndAttributes(lecture4.getCDDefinition().getCDInterfacesList());
    fullExpander.addMissingTypesAndAttributes(lecture4.getCDDefinition().getCDClassesList());

    CD4CodeMill.scopesGenitorDelegator().createFromAST(lecture5);

    CD4CodeFullPrettyPrinter pprinter = new CD4CodeFullPrettyPrinter();
    lecture5.accept(pprinter.getTraverser());
    String result = pprinter.getPrinter().getContent();

    pprinter = new CD4CodeFullPrettyPrinter();
    lecture4.getCDDefinition().setName("Lecture5");
    CD4CodeMill.scopesGenitorDelegator().createFromAST(lecture4);
    lecture4.accept(pprinter.getTraverser());

    Assert.assertEquals(result, pprinter.getPrinter().getContent());
  }

  @Test
  public void testAddMissingEnumsAndConstants() {
    ASTCDCompilationUnit enumComp1 = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/EnumComposition/enumCompV1.cd");
    ASTCDCompilationUnit enumComp2 = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/EnumComposition/enumCompV3.cd");

    CD4CodeMill.scopesGenitorDelegator().createFromAST(enumComp1);
    CD4CodeMill.scopesGenitorDelegator().createFromAST(enumComp2);

    FullExpander fullExpander = new FullExpander(new OpenWorldExpander(enumComp1));

    fullExpander.addMissingEnumsAndConstants(enumComp2.getCDDefinition().getCDEnumsList());

    CD4CodeMill.scopesGenitorDelegator().createFromAST(enumComp1);

    CD4CodeFullPrettyPrinter pprinter = new CD4CodeFullPrettyPrinter();
    enumComp1.accept(pprinter.getTraverser());
    String result = pprinter.getPrinter().getContent();

    pprinter = new CD4CodeFullPrettyPrinter();
    enumComp2.getCDDefinition().setName("enumCompV1");
    CD4CodeMill.scopesGenitorDelegator().createFromAST(enumComp2);
    enumComp2.accept(pprinter.getTraverser());

    Assert.assertEquals(result, pprinter.getPrinter().getContent());

  }

  @Test
  public void testAddMissingAssociations() {
    ASTCDCompilationUnit lecture = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/Lecture/Lecture2.cd");

    List<ASTCDAssociation> originals = lecture.getCDDefinition().getCDAssociationsList();
    List<ASTCDAssociation> copies = new ArrayList<>();

    for (ASTCDAssociation assoc : originals) {
      copies.add(assoc.deepClone());
    }

    lecture.getCDDefinition().removeAllCDElements(originals);

    CD4CodeMill.scopesGenitorDelegator().createFromAST(lecture);
    FullExpander fullExpander = new FullExpander( new OpenWorldExpander(lecture));

    fullExpander.addMissingAssociations(copies, false);
    CD4CodeMill.scopesGenitorDelegator().createFromAST(lecture);

    CD4CodeFullPrettyPrinter pprinter = new CD4CodeFullPrettyPrinter();
    lecture.accept(pprinter.getTraverser());
    String result = pprinter.getPrinter().getContent();

    pprinter = new CD4CodeFullPrettyPrinter();
    lecture = parseModel("src/cddifftest/resources/de/monticore/cddiff/Lecture/Lecture3.cd");
    lecture.getCDDefinition().setName("Lecture2");
    CD4CodeMill.scopesGenitorDelegator().createFromAST(lecture);
    lecture.accept(pprinter.getTraverser());

    Assert.assertEquals(result, pprinter.getPrinter().getContent());

  }

  @Test
  public void testDeterminePackageName() {
    ASTCDCompilationUnit employees8 = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/Employees/Employees8.cd");
    ICD4CodeArtifactScope scope = CD4CodeMill.scopesGenitorDelegator().createFromAST(employees8);
    OpenWorldExpander fullExpander = new OpenWorldExpander(employees8);

    scope.resolveCDTypeDown("ins.Employee")
        .ifPresent(
            type -> Assert.assertEquals("ins", fullExpander.determinePackageName(type.getAstNode())));
  }

  @Test
  public void testUpdateDir2Match() {

    final ASTCDCompilationUnit lecture1 = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/Lecture/Lecture1.cd");

    final ASTCDCompilationUnit lecture2 = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/Lecture/Lecture2.cd");

    FullExpander fullExpander = new FullExpander(new OpenWorldExpander(lecture2));

    fullExpander.updateDir2Match(lecture1.getCDDefinition().getCDAssociationsList());

    Assert.assertTrue(lecture2.getCDDefinition()
        .getCDAssociationsList()
        .stream()
        .anyMatch(assoc2 -> assoc2.getCDAssocDir().isBidirectional()));

    Assert.assertTrue(lecture2.getCDDefinition()
        .getCDAssociationsList()
        .stream()
        .allMatch(assoc2 -> assoc2.getCDAssocDir().isDefinitiveNavigableRight()));

    Assert.assertFalse(lecture2.getCDDefinition()
        .getCDAssociationsList()
        .stream()
        .allMatch(assoc2 -> assoc2.getCDAssocDir().isBidirectional()));
  }

}
