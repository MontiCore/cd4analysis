package de.monticore.cddiff.ow2cw;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffTestBasis;
import de.monticore.ow2cw.CDInheritanceHelper;
import org.junit.Assert;
import org.junit.Test;

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
    //todo: implement
  }

  @Test
  public void testInducesNoInheritanceCycle() {
    //todo: implement
  }

  @Test
  public void testRemoveRedundantAttributes() {
    //todo: implement
  }

  @Test
  public void testFindInSuper() {
    //todo: implement
  }

  @Test
  public void testGetAllSuper() {
    //todo: implement
  }

  @Test
  public void testResolveClosestType() {
    //todo: implement
  }

}
