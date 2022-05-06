package de.monticore.cddiff.ow2cw;

import de.monticore.cd.facade.CDAttributeFacade;
import de.monticore.cd.facade.MCQualifiedNameFacade;
import de.monticore.cd4analysis._auxiliary.MCBasicTypesMillForCD4Analysis;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cddiff.CDDiffTestBasis;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.ow2cw.CDInheritanceHelper;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CDInheritanceHelperTest extends CDDiffTestBasis {
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

    CDInheritanceHelper.copyInheritance(lecture1, lecture2);

    CD4CodeFullPrettyPrinter pprinter = new CD4CodeFullPrettyPrinter();
    lecture1.accept(pprinter.getTraverser());
    String cd1 = pprinter.getPrinter().getContent();

    pprinter = new CD4CodeFullPrettyPrinter();
    lecture2.accept(pprinter.getTraverser());
    String cd2 = pprinter.getPrinter().getContent();

    Assert.assertEquals(cd1, cd2);

  }

  @Test
  public void testIsNewSuper() {
    ASTCDCompilationUnit lecture1 = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/Lecture/Lecture1.cd");

    ICD4CodeArtifactScope scope1 = CD4CodeMill.scopesGenitorDelegator().createFromAST(lecture1);

    List<String> subList = new ArrayList<>();
    subList.add("Presenting");
    subList.add("Lecturer");
    subList.add("PostDoc");
    subList.add("Professor");

    ASTMCObjectType newSuper = MCBasicTypesMillForCD4Analysis.mCQualifiedTypeBuilder()
        .setMCQualifiedName(MCQualifiedNameFacade.createQualifiedName("Presenting"))
        .build();

    for (ASTCDClass targetClass : lecture1.getCDDefinition().getCDClassesList()) {
      if (subList.stream().anyMatch(superType -> targetClass.getName().equals(superType))) {
        Assert.assertFalse(CDInheritanceHelper.isNewSuper(newSuper, targetClass, scope1));
      }
      else {
        Assert.assertTrue(CDInheritanceHelper.isNewSuper(newSuper, targetClass, scope1));
      }
    }

    for (ASTCDInterface targetInterface : lecture1.getCDDefinition().getCDInterfacesList()) {
      if (subList.stream().anyMatch(superType -> targetInterface.getName().equals(superType))) {
        Assert.assertFalse(CDInheritanceHelper.isNewSuper(newSuper, targetInterface, scope1));
      }
      else {
        Assert.assertTrue(CDInheritanceHelper.isNewSuper(newSuper, targetInterface, scope1));
      }
    }
  }

  @Test
  public void testInducesNoInheritanceCycle() {
    ASTCDCompilationUnit lecture1 = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/Lecture/Lecture1.cd");

    ICD4CodeArtifactScope scope1 = CD4CodeMill.scopesGenitorDelegator().createFromAST(lecture1);

    List<String> superList = new ArrayList<>();
    superList.add("Being");
    superList.add("Person");
    superList.add("Professor");

    ASTMCObjectType newSuper = MCBasicTypesMillForCD4Analysis.mCQualifiedTypeBuilder()
        .setMCQualifiedName(MCQualifiedNameFacade.createQualifiedName("Professor"))
        .build();

    for (ASTCDClass targetClass : lecture1.getCDDefinition().getCDClassesList()) {
      if (superList.stream().anyMatch(superType -> targetClass.getName().equals(superType))) {
        Assert.assertFalse(
            CDInheritanceHelper.inducesNoInheritanceCycle(newSuper, targetClass, scope1));
      }
      else {
        Assert.assertTrue(
            CDInheritanceHelper.inducesNoInheritanceCycle(newSuper, targetClass, scope1));
      }
    }
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
    CDInheritanceHelper.removeRedundantAttributes(employees8);

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
  public void testFindInSuper() {
    ASTCDCompilationUnit cd1 = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/VehicleManagement/cd1.cd");
    ICD4CodeArtifactScope scope1 = CD4CodeMill.scopesGenitorDelegator().createFromAST(cd1);

    Optional<CDTypeSymbol> opt = scope1.resolveCDType("Truck");

    opt.ifPresent(cdTypeSymbol -> Assert.assertTrue(CDInheritanceHelper.findInSuper(
        CDAttributeFacade.getInstance()
            .createAttribute(CD4CodeMill.modifierBuilder().build(), "String", "licensePlate"),
        cdTypeSymbol.getAstNode(), scope1)));
  }

  @Test
  public void testGetAllSuper() {
    ASTCDCompilationUnit lecture1 = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/Lecture/Lecture1.cd");

    ICD4CodeArtifactScope scope1 = CD4CodeMill.scopesGenitorDelegator().createFromAST(lecture1);

    List<String> superList = new ArrayList<>();
    superList.add("Being");
    superList.add("Person");
    superList.add("Presenting");
    superList.add("Lecturer");
    superList.add("Professor");

    Optional<CDTypeSymbol> opt = scope1.resolveCDType("Professor");

    if (opt.isPresent()) {
      for (ASTCDType superType : CDInheritanceHelper.getAllSuper(opt.get().getAstNode(), scope1)) {
        Assert.assertTrue(superList.stream().anyMatch(name -> name.equals(superType.getName())));
      }
    }
  }

  @Test
  public void testResolveClosestType() {
    ASTCDCompilationUnit employees8 = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/Employees/Employees8.cd");

    ICD4CodeArtifactScope scope8 = CD4CodeMill.scopesGenitorDelegator().createFromAST(employees8);
    scope8.resolveCDType("Manager")
        .ifPresent(src -> Assert.assertEquals("ins.Employee",
            CDInheritanceHelper.resolveClosestType(src.getAstNode(), "Employee", scope8)
                .getSymbol()
                .getFullName()));
  }

}
