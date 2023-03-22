/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcdassociation.symboltable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.monticore.cdassociation._symboltable.CDRoleSymbol;
import de.monticore.cdassociation.trafo.CDAssociationDirectCompositionTrafo;
import de.monticore.cdassociation.trafo.CDAssociationRoleNameTrafo;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdbasis._symboltable.ICDBasisArtifactScope;
import de.monticore.testcdassociation.CDAssociationTestBasis;
import de.monticore.testcdassociation.TestCDAssociationMill;
import de.monticore.testcdassociation._symboltable.ITestCDAssociationArtifactScope;
import de.monticore.testcdassociation._visitor.TestCDAssociationTraverser;
import java.util.List;
import java.util.Optional;

import org.junit.Ignore;
import org.junit.Test;

public class CDAssociationSTCompleterTest extends CDAssociationTestBasis {

  @Test
  public void genitorTest() {
    ICDBasisArtifactScope artifactScope = processModel("cdassociation/parser/Simple.cd");

    // should be no package but scopes of sub-elements
    assertEquals(10, artifactScope.getSubScopes().size());

    final Optional<CDTypeSymbol> b = artifactScope.resolveCDType("B");
    assertTrue(b.isPresent());

    final List<CDRoleSymbol> cdRoleList = b.get().getCDRoleList();
    assertEquals(2, cdRoleList.size());
    assertTrue(cdRoleList.stream().anyMatch(r -> r.getName().equals("s")));
    assertTrue(cdRoleList.stream().anyMatch(r -> r.getName().equals("s2")));

    final Optional<CDRoleSymbol> s =
        cdRoleList.stream().filter(r -> r.getName().equals("s")).findFirst();
    assertTrue(s.isPresent());
    assertEquals("Simple.B.s", s.get().getFullName());

    checkLogError();
  }

  @Ignore // TODO: resolven mit Paketen checken
  @Test
  public void genitorTestWithPkg() {
    ICDBasisArtifactScope artifactScope = processModel("cdassociation/parser/SimpleWithPkg.cd");

    // should only be the default package
    assertEquals(1, artifactScope.getSubScopes().size());

    final Optional<CDTypeSymbol> b = artifactScope.resolveCDType("de.monticore.B");
    assertTrue(b.isPresent());

    final List<CDRoleSymbol> cdRoleList = b.get().getCDRoleList();
    assertEquals(2, cdRoleList.size());
    assertTrue(cdRoleList.stream().anyMatch(r -> r.getName().equals("s")));
    assertTrue(cdRoleList.stream().anyMatch(r -> r.getName().equals("s2")));

    final Optional<CDRoleSymbol> s =
        cdRoleList.stream().filter(r -> r.getName().equals("s")).findFirst();
    assertTrue(s.isPresent());
    assertEquals("Simple.de.monticore.B.s", s.get().getFullName());

    checkLogError();
  }

  public ICDBasisArtifactScope processModel(String model) {
    final ASTCDCompilationUnit astcdCompilationUnit = parseModel(model);

    // after parse trafo
    TestCDAssociationTraverser t = TestCDAssociationMill.traverser();
    CDAssociationDirectCompositionTrafo trafo = new CDAssociationDirectCompositionTrafo();
    t.add4CDBasis(trafo);
    t.add4CDAssociation(trafo);
    astcdCompilationUnit.accept(t);

    ITestCDAssociationArtifactScope artifactScope = createSymTab(astcdCompilationUnit);
    completeSymTab(astcdCompilationUnit);

    checkLogError();
    return artifactScope;
  }
}
