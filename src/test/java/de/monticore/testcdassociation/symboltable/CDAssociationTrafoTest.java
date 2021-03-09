package de.monticore.testcdassociation.symboltable;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis._symboltable.CD4AnalysisSymbolTableCompleter;
import de.monticore.cd4analysis._symboltable.ICD4AnalysisArtifactScope;
import de.monticore.cd4analysis._visitor.CD4AnalysisTraverser;
import de.monticore.cd4analysis.trafo.CD4AnalysisAfterParseTrafo;
import de.monticore.cdassociation._symboltable.CDRoleSymbol;
import de.monticore.cdassociation._visitor.CDAssociationTraverser;
import de.monticore.cdassociation.trafo.CDAssociationCreateFieldsFromAllRoles;
import de.monticore.cdassociation.trafo.CDAssociationRoleNameTrafo;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.testcdassociation.CDAssociationTestBasis;
import de.monticore.types.check.SymTypeExpression;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class CDAssociationTrafoTest extends CDAssociationTestBasis {

  @Test
  public void genitorTest() {
    final ASTCDCompilationUnit astcdCompilationUnit = parseModel("cdassociation/parser/Simple.cd");
    new CD4AnalysisAfterParseTrafo().transform(astcdCompilationUnit);
    final ICD4AnalysisArtifactScope artifactScope = createST(astcdCompilationUnit);

    {
      final CD4AnalysisTraverser traverser = new CD4AnalysisSymbolTableCompleter(astcdCompilationUnit).getTraverser();
      astcdCompilationUnit.accept(traverser);

      checkLogError();
    }

    { // add role names
      final CDAssociationRoleNameTrafo cdAssociationRoleNameTrafo = new CDAssociationRoleNameTrafo();
      final CDAssociationTraverser traverser = CD4AnalysisMill.traverser();
      traverser.add4CDAssociation(cdAssociationRoleNameTrafo);
      traverser.setCDAssociationHandler(cdAssociationRoleNameTrafo);
      cdAssociationRoleNameTrafo.transform(astcdCompilationUnit);

      checkLogError();
    }

    final Optional<CDTypeSymbol> b = artifactScope.resolveCDType("B");
    assertTrue(b.isPresent());

    final List<CDRoleSymbol> cdRoleList = b.get().getCDRoleList();
    assertEquals(2, cdRoleList.size());
    assertTrue(cdRoleList.stream().anyMatch(r -> r.getName().equals("s")));
    assertTrue(cdRoleList.stream().anyMatch(r -> r.getName().equals("s2")));

    assertTrue(b.get().getFieldList().isEmpty());

    { // add FieldSymbols for the CDRoleSymbols
      final CDAssociationCreateFieldsFromAllRoles cdAssociationCreateFieldsFromAllRoles = new CDAssociationCreateFieldsFromAllRoles();
      final CD4AnalysisTraverser traverser = CD4AnalysisMill.traverser();
      traverser.add4CDAssociation(cdAssociationCreateFieldsFromAllRoles);
      traverser.setCDAssociationHandler(cdAssociationCreateFieldsFromAllRoles);
      cdAssociationCreateFieldsFromAllRoles.transform(astcdCompilationUnit);
    }

    checkLogError();

    assertFalse(b.get().getFieldList().isEmpty());
    assertEquals(2, b.get().getFieldList().size());

    final SymTypeExpression type = b.get().getFieldList().get(1).getType();
    assertTrue(type.isGenericType());
    assertEquals("java.util.List", type.getTypeInfo().getFullName());
  }

}
