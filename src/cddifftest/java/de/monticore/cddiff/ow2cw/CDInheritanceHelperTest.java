/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.ow2cw;

import de.monticore.cd.facade.CDAttributeFacade;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cddiff.CDDiffTestBasis;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;

public class CDInheritanceHelperTest extends CDDiffTestBasis {

  @Test
  public void testFindInSuper() {
    ASTCDCompilationUnit cd1 =
        parseModel("src/cddifftest/resources/de/monticore/cddiff/VehicleManagement/cd1.cd");
    ICD4CodeArtifactScope scope1 = CD4CodeMill.scopesGenitorDelegator().createFromAST(cd1);

    Optional<CDTypeSymbol> opt = scope1.resolveCDType("Truck");

    opt.ifPresent(
        cdTypeSymbol ->
            Assert.assertTrue(
                CDInheritanceHelper.isAttributInSuper(
                    CDAttributeFacade.getInstance()
                        .createAttribute(
                            CD4CodeMill.modifierBuilder().build(), "String", "licensePlate"),
                    cdTypeSymbol.getAstNode(),
                    scope1)));
  }

  @Test
  public void testGetAllSuper() {
    ASTCDCompilationUnit lecture1 =
        parseModel("src/cddifftest/resources/de/monticore/cddiff/Lecture/Lecture1.cd");

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
    ASTCDCompilationUnit employees8 =
        parseModel("src/cddifftest/resources/de/monticore/cddiff/Employees/Employees8.cd");

    ICD4CodeArtifactScope scope8 = CD4CodeMill.scopesGenitorDelegator().createFromAST(employees8);
    scope8
        .resolveCDType("Manager")
        .ifPresent(
            src ->
                Assert.assertEquals(
                    "ins.Employee",
                    CDInheritanceHelper.resolveClosestType(src.getAstNode(), "Employee", scope8)
                        .getSymbol()
                        .getFullName()));
  }
}
