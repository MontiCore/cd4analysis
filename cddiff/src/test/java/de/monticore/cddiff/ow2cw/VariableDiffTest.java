/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.ow2cw;

import static org.junit.jupiter.api.Assertions.*;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffTestBasis;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cddiff.ow2cw.expander.VariableExpander;
import java.util.HashSet;
import org.junit.jupiter.api.Test;

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

    assertTrue(
        cd2.getCDDefinition().getModifier().isPresentStereotype()
            && cd2.getCDDefinition()
                .getModifier()
                .getStereotype()
                .contains(VariableExpander.VAR_TAG));

    assertTrue(
        cd2.getCDDefinition().getCDClassesList().stream()
            .noneMatch(
                subClass ->
                    subClass.getName().contains("Sub4Diff")
                        || subClass.getName().contains("ManagerTask")));

    assertTrue(
        cd2.getCDDefinition().getCDInterfacesList().stream()
            .noneMatch(subClass -> subClass.getName().contains("Doable")));

    assertEquals(3, cd2.getCDDefinition().getCDAssociationsList().size());

    int found = 0;
    for (ASTCDClass current : new HashSet<>(cd1.getCDDefinition().getCDClassesList())) {
      if (current.getName().equals("Employee")) {
        assertFalse(CDInheritanceHelper.isSuperOf("Insurable", "Employee", scope1));
        found++;
      }
      if (current.getName().equals("Person")) {
        assertTrue(current.getCDAttributeList().isEmpty());
        found++;
      }
    }
    assertEquals(2, found);

    assertEquals(3, cd1.getCDDefinition().getCDAssociationsList().size());
    assertEquals(2, cd1.getCDDefinition().getCDEnumsList().get(0).getCDEnumConstantList().size());
  }
}
