package de.monticore.cddiff.ow2cw;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffTestBasis;
import org.junit.Assert;
import org.junit.Test;

public class VariableDiffTest extends CDDiffTestBasis {

  @Test
  public void testStereotype() {

    CD4CodeMill.globalScope().clear();

    ASTCDCompilationUnit cd = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/variablediff/VariableEmployees.cd");

    CD4CodeMill.scopesGenitorDelegator().createFromAST(cd);

    boolean found = false;
    for (ASTCDClass current : cd.getCDDefinition().getCDClassesList()){
      if (current.getName().equals("Employee")){
        Assert.assertTrue(current.getModifier().getStereotype().contains("complete"));
        found = true;
      }
    }
    Assert.assertTrue(found);

  }
}
