/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.ow2cw;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffTestBasis;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cddiff.ow2cw.expander.VariableExpander;
import java.util.HashSet;
import org.junit.Assert;
import org.junit.Test;

public class VariableDiffTest extends CDDiffTestBasis {

  @Test
  public void testVariableEmployees() {

    ASTCDCompilationUnit cd1 =
        parseModel("src/test/resources/de/monticore/cddiff/variablediff/VariableEmployees1.cd");
    ASTCDCompilationUnit cd2 =
        parseModel("src/test/resources/de/monticore/cddiff/variablediff/VariableEmployees2.cd");

    new ReductionTrafo().transform(cd2, cd1);

    CDDiffUtil.refreshSymbolTable(cd1);
    CDDiffUtil.refreshSymbolTable(cd2);

    ICD4CodeArtifactScope scope1 = (ICD4CodeArtifactScope) cd1.getEnclosingScope();

    System.out.println(CD4CodeMill.prettyPrint(cd1, true));

    Assert.assertTrue(
        cd2.getCDDefinition().getModifier().isPresentStereotype()
            && cd2.getCDDefinition()
                .getModifier()
                .getStereotype()
                .contains(VariableExpander.VAR_TAG));

    Assert.assertTrue(
        cd2.getCDDefinition().getCDClassesList().stream()
            .noneMatch(
                subClass ->
                    subClass.getName().contains("Sub4Diff")
                        || subClass.getName().contains("ManagerTask")));

    Assert.assertTrue(
        cd2.getCDDefinition().getCDInterfacesList().stream()
            .noneMatch(subClass -> subClass.getName().contains("Doable")));

    Assert.assertEquals(3, cd2.getCDDefinition().getCDAssociationsList().size());

    int found = 0;
    for (ASTCDClass current : new HashSet<>(cd1.getCDDefinition().getCDClassesList())) {
      if (current.getName().equals("Employee")) {
        Assert.assertFalse(CDInheritanceHelper.isSuperOf("Insurable", "Employee", scope1));
        found++;
      }
      if (current.getName().equals("Person")) {
        Assert.assertTrue(current.getCDAttributeList().isEmpty());
        found++;
      }
    }
    Assert.assertEquals(2, found);

    Assert.assertEquals(3, cd1.getCDDefinition().getCDAssociationsList().size());
    Assert.assertEquals(
        2, cd1.getCDDefinition().getCDEnumsList().get(0).getCDEnumConstantList().size());
  }
}
