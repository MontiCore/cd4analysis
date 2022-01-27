/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcdassociation.symboltable;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis._symboltable.CD4AnalysisSymbolTableCompleter;
import de.monticore.cd4analysis._visitor.CD4AnalysisTraverser;
import de.monticore.cd4analysis.trafo.CD4AnalysisAfterParseTrafo;
import de.monticore.cdassociation._symboltable.CDRoleSymbol;
import de.monticore.cdassociation._visitor.CDAssociationTraverser;
import de.monticore.cdassociation.trafo.CDAssociationCreateFieldsFromAllRoles;
import de.monticore.cdassociation.trafo.CDAssociationCreateFieldsFromNavigableRoles;
import de.monticore.cdassociation.trafo.CDAssociationRoleNameTrafo;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdbasis._symboltable.ICDBasisArtifactScope;
import de.monticore.testcdassociation.CDAssociationTestBasis;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.logging.Log;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class CDAssociationTrafoTest extends CDAssociationTestBasis {
  @Test
  public void genitorTest() {
    final ASTCDCompilationUnit astcdCompilationUnit = parseModel("cdassociation/parser/Simple.cd");
    new CD4AnalysisAfterParseTrafo().transform(astcdCompilationUnit);
    final ICDBasisArtifactScope artifactScope = createST(astcdCompilationUnit);

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
    assertEquals(0, b.get().getAstNode().getCDAttributeList().size());

    { // add FieldSymbols for the CDRoleSymbols
      final CDAssociationCreateFieldsFromAllRoles cdAssociationCreateFieldsFromNavigableRoles = new CDAssociationCreateFieldsFromNavigableRoles();
      final CD4AnalysisTraverser traverser = CD4AnalysisMill.traverser();
      traverser.add4CDAssociation(cdAssociationCreateFieldsFromNavigableRoles);
      traverser.setCDAssociationHandler(cdAssociationCreateFieldsFromNavigableRoles);
      cdAssociationCreateFieldsFromNavigableRoles.transform(astcdCompilationUnit);
    }

    checkLogError();

    assertEquals(2, b.get().getFieldList().size());
    assertEquals(2, b.get().getAstNode().getCDAttributeList().size());

    final SymTypeExpression type = b.get().getFieldList().get(1).getType();
    assertTrue(type.isGenericType());
    assertEquals("java.util.List", type.getTypeInfo().getFullName());
  }

  @Test
  public void roleWithSameNameAsAttribute() {
    final ASTCDCompilationUnit astcdCompilationUnit = parseModel("cdassociation/parser/RoleNameExistsAsAttributeName.cd");
    new CD4AnalysisAfterParseTrafo().transform(astcdCompilationUnit);
    final ICDBasisArtifactScope artifactScope = createST(astcdCompilationUnit);

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

    { // add FieldSymbols for the CDRoleSymbols
      final CDAssociationCreateFieldsFromAllRoles cdAssociationCreateFieldsFromAllRoles = new CDAssociationCreateFieldsFromAllRoles();
      final CD4AnalysisTraverser traverser = CD4AnalysisMill.traverser();
      traverser.add4CDAssociation(cdAssociationCreateFieldsFromAllRoles);
      traverser.setCDAssociationHandler(cdAssociationCreateFieldsFromAllRoles);

      cdAssociationCreateFieldsFromAllRoles.transform(astcdCompilationUnit);
      assertEquals(2, Log.getErrorCount());
      assertEquals("0xCD0B7: a FieldSymbol with the name 'b' already exists in 'de.monticore.A' (defined in RoleNameExistsAsAttributeName.cd:<6,4>)", Log.getFindings().get(0).getMsg());
      assertEquals("0xCD0B7: a FieldSymbol with the name 'value' already exists in 'de.monticore.B' (defined in RoleNameExistsAsAttributeName.cd:<10,4>)", Log.getFindings().get(1).getMsg());

      Log.getFindings().clear();
    }
  }
}
