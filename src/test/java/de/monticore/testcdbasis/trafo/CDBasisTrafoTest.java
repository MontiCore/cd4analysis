/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcdbasis.trafo;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

import de.monticore.cd.TestBasis;
import de.monticore.cd.facade.CDModifier;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdbasis.trafo.CDBasisCombinePackagesTrafo;
import de.monticore.cdbasis.trafo.CDBasisDefaultPackageTrafo;
import de.monticore.testcdbasis.TestCDBasisMill;
import de.monticore.testcdbasis._parser.TestCDBasisParser;
import de.monticore.testcdbasis._visitor.TestCDBasisTraverser;
import de.se_rwth.commons.logging.Log;
import java.io.IOException;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;

public class CDBasisTrafoTest extends TestBasis {

  @Before
  public void setupAll() {
    // reset the GlobalScope
    TestCDBasisMill.reset();
    TestCDBasisMill.init();
    TestCDBasisMill.globalScope().clear();

    // reset the logger
    Log.init();
    Log.enableFailQuick(false);
  }

  @Test
  public void trafoTest() throws IOException {
    TestCDBasisParser parser = TestCDBasisMill.parser();
    Optional<ASTCDCompilationUnit> astOpt = parser.parse(getFilePath("cdbasis/trafo/PkgMerge.cd"));

    assertFalse(parser.hasErrors());
    assertTrue(astOpt.isPresent());

    ASTCDDefinition cd = astOpt.get().getCDDefinition();

    // before trafo
    assertEquals(3, cd.getCDElementList().size());

    // after parse trafo
    TestCDBasisTraverser t = TestCDBasisMill.traverser();
    CDBasisCombinePackagesTrafo trafo = new CDBasisCombinePackagesTrafo();
    t.add4CDBasis(trafo);
    astOpt.get().accept(t);

    // after trafo
    assertEquals(2, cd.getCDElementList().size());
  }

  @Test
  public void trafoTestWithDefaultPkg() throws IOException {
    TestCDBasisParser parser = TestCDBasisMill.parser();
    Optional<ASTCDCompilationUnit> astOpt =
        parser.parse(getFilePath("cdbasis/trafo/DefaultPkg.cd"));

    assertFalse(parser.hasErrors());
    assertTrue(astOpt.isPresent());

    ASTCDDefinition cd = astOpt.get().getCDDefinition();

    // before trafo
    assertFalse(cd.isPresentDefaultPackage());
    assertEquals(4, cd.getCDElementList().size());

    // after parse trafo
    TestCDBasisTraverser t = TestCDBasisMill.traverser();
    CDBasisCombinePackagesTrafo trafo1 = new CDBasisCombinePackagesTrafo();
    CDBasisDefaultPackageTrafo trafo2 = new CDBasisDefaultPackageTrafo();
    t.add4CDBasis(trafo1);
    t.add4CDBasis(trafo2);
    astOpt.get().accept(t);

    // after trafo
    assertTrue(cd.isPresentDefaultPackage());
    assertEquals(3, cd.getCDElementList().size());

    assertEquals(cd.getCDElement(0), cd.getDefaultPackage());
    String expectedDefPkgName =
        astOpt.get().getMCPackageDeclaration().getMCQualifiedName().getQName()
            + "."
            + cd.getName().toLowerCase();
    assertEquals(expectedDefPkgName, cd.getDefaultPackage().getMCQualifiedName().getQName());

    for (ASTCDElement e : cd.getCDElementList()) {
      assertTrue(e instanceof ASTCDPackage);
      ASTCDPackage pkg = (ASTCDPackage) e;
      assertTrue(pkg.getMCQualifiedName().getQName().startsWith(expectedDefPkgName));
    }
  }

  @Test
  public void testAddingBahavior() throws IOException {
    TestCDBasisParser parser = TestCDBasisMill.parser();
    Optional<ASTCDCompilationUnit> astOpt =
        parser.parse(getFilePath("cdbasis/trafo/Empty4DefaultPkg.cd"));

    assertFalse(parser.hasErrors());
    assertTrue(astOpt.isPresent());

    ASTCDDefinition cd = astOpt.get().getCDDefinition();

    // before trafo
    assertFalse(cd.isPresentDefaultPackage());
    assertEquals(0, cd.getCDElementList().size());

    // before trafo, everything is added the the cd directly
    ASTCDClass classA =
        TestCDBasisMill.cDClassBuilder()
            .setName("A")
            .setModifier(CDModifier.PUBLIC.build())
            .build();
    cd.addCDElement(classA);
    assertEquals(1, cd.getCDElementList().size());
    assertEquals(classA, cd.getCDElement(0));

    // after parse trafo
    TestCDBasisTraverser t = TestCDBasisMill.traverser();
    CDBasisCombinePackagesTrafo trafo1 = new CDBasisCombinePackagesTrafo();
    CDBasisDefaultPackageTrafo trafo2 = new CDBasisDefaultPackageTrafo();
    t.add4CDBasis(trafo1);
    t.add4CDBasis(trafo2);
    astOpt.get().accept(t);

    // after trafo, only default pkg is directly in cd
    assertTrue(cd.isPresentDefaultPackage());
    assertEquals(1, cd.getCDElementList().size());
    assertEquals(cd.getCDElement(0), cd.getDefaultPackage());

    // default trafo contains class A
    assertEquals(1, cd.getDefaultPackage().getCDElementList().size());
    assertEquals(classA, cd.getDefaultPackage().getCDElement(0));

    // every element is now directly added to default pkg
    ASTCDClass classB =
        TestCDBasisMill.cDClassBuilder()
            .setName("B")
            .setModifier(CDModifier.PUBLIC.build())
            .build();
    cd.addCDElement(classB);
    assertEquals(1, cd.getCDElementList().size());
    assertEquals(2, cd.getDefaultPackage().getCDElementList().size());
    assertEquals(classA, cd.getDefaultPackage().getCDElement(0));
    assertEquals(classB, cd.getDefaultPackage().getCDElement(1));
  }
}
