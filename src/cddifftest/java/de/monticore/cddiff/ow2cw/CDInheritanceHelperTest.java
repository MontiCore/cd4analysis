package de.monticore.cddiff.ow2cw;

import de.monticore.cd.facade.CDAttributeFacade;
import de.monticore.cd.facade.MCQualifiedNameFacade;
import de.monticore.cd4analysis._auxiliary.MCBasicTypesMillForCD4Analysis;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
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
