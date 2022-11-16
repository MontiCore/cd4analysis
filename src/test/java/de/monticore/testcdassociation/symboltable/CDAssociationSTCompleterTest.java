/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcdassociation.symboltable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis._symboltable.CD4AnalysisSymbolTableCompleter;
import de.monticore.cd4analysis._visitor.CD4AnalysisTraverser;
import de.monticore.cd4analysis.trafo.CD4AnalysisAfterParseTrafo;
import de.monticore.cdassociation._symboltable.CDRoleSymbol;
import de.monticore.cdassociation._visitor.CDAssociationTraverser;
import de.monticore.cdassociation.trafo.CDAssociationRoleNameTrafo;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdbasis._symboltable.ICDBasisArtifactScope;
import de.monticore.testcdassociation.CDAssociationTestBasis;
import java.util.List;
import java.util.Optional;
import org.junit.Test;

public class CDAssociationSTCompleterTest extends CDAssociationTestBasis {

  @Test
  public void genitorTest() {
    final ASTCDCompilationUnit astcdCompilationUnit = parseModel("cdassociation/parser/Simple.cd");
    new CD4AnalysisAfterParseTrafo().transform(astcdCompilationUnit);
    ICDBasisArtifactScope artifactScope = createST(astcdCompilationUnit);

    {
      final CD4AnalysisTraverser traverser =
          new CD4AnalysisSymbolTableCompleter(astcdCompilationUnit).getTraverser();
      astcdCompilationUnit.accept(traverser);

      checkLogError();
    }

    { // add role names
      final CDAssociationRoleNameTrafo cdAssociationRoleNameTrafo =
          new CDAssociationRoleNameTrafo();
      final CDAssociationTraverser traverser = CD4AnalysisMill.traverser();
      traverser.add4CDAssociation(cdAssociationRoleNameTrafo);
      traverser.setCDAssociationHandler(cdAssociationRoleNameTrafo);
      cdAssociationRoleNameTrafo.transform(astcdCompilationUnit);

      checkLogError();
    }

    // should only be the default package
    assertEquals(1, artifactScope.getSubScopes().size());

    final Optional<CDTypeSymbol> b = artifactScope.resolveCDType("B");
    assertTrue(b.isPresent());

    final List<CDRoleSymbol> cdRoleList = b.get().getCDRoleList();
    assertEquals(2, cdRoleList.size());
    assertTrue(cdRoleList.stream().anyMatch(r -> r.getName().equals("s")));
    assertTrue(cdRoleList.stream().anyMatch(r -> r.getName().equals("s2")));

    final Optional<CDRoleSymbol> s =
        cdRoleList.stream().filter(r -> r.getName().equals("s")).findFirst();
    assertTrue(s.isPresent());
    assertEquals("de.monticore.B.s", s.get().getFullName());

    checkLogError();
  }
}
