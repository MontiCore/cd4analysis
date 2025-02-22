/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4analysis.trafo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis.CD4AnalysisTestBasis;
import de.monticore.cd4analysis._symboltable.CD4AnalysisSymbolTableCompleter;
import de.monticore.cd4analysis._visitor.CD4AnalysisTraverser;
import de.monticore.cdassociation._symboltable.CDRoleSymbol;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdbasis._symboltable.ICDBasisArtifactScope;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.logging.Log;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.junit.Ignore;
import org.junit.Test;

public class CDAssociationTrafoTest extends CD4AnalysisTestBasis {
  @Test
  public void genitorTest() throws IOException {
    final ASTCDCompilationUnit astcdCompilationUnit = parse("cdassociation/parser/Simple.cd");
    new CD4AnalysisAfterParseTrafo().transform(astcdCompilationUnit);
    final ICDBasisArtifactScope artifactScope = createSymTab(astcdCompilationUnit);

    {
      final CD4AnalysisTraverser traverser =
          new CD4AnalysisSymbolTableCompleter(astcdCompilationUnit).getTraverser();
      astcdCompilationUnit.accept(traverser);

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
      final CDAssociationCreateFieldsFromAllRoles cdAssociationCreateFieldsFromNavigableRoles =
          new CDAssociationCreateFieldsFromNavigableRoles();
      final CD4AnalysisTraverser traverser = CD4AnalysisMill.inheritanceTraverser();
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
  public void roleWithSameNameAsAttribute() throws IOException {
    final ASTCDCompilationUnit astcdCompilationUnit =
        parse("cdassociation/parser/RoleNameExistsAsAttributeName.cd");
    new CD4AnalysisAfterParseTrafo().transform(astcdCompilationUnit);
    final ICDBasisArtifactScope artifactScope = createSymTab(astcdCompilationUnit);

    {
      final CD4AnalysisTraverser traverser =
          new CD4AnalysisSymbolTableCompleter(astcdCompilationUnit).getTraverser();
      astcdCompilationUnit.accept(traverser);

      checkLogError();
    }

    { // add FieldSymbols for the CDRoleSymbols
      final CDAssociationCreateFieldsFromAllRoles cdAssociationCreateFieldsFromAllRoles =
          new CDAssociationCreateFieldsFromAllRoles();
      final CD4AnalysisTraverser traverser = CD4AnalysisMill.inheritanceTraverser();
      traverser.add4CDAssociation(cdAssociationCreateFieldsFromAllRoles);
      traverser.setCDAssociationHandler(cdAssociationCreateFieldsFromAllRoles);

      cdAssociationCreateFieldsFromAllRoles.transform(astcdCompilationUnit);
      assertEquals(2, Log.getErrorCount());
      assertEquals(
          "0xCD0B7: a FieldSymbol with the name 'b' already exists in 'RoleNameExistsAsAttributeName.A' (defined in RoleNameExistsAsAttributeName.cd:<6,4>)",
          Log.getFindings().get(0).getMsg());
      assertEquals(
          "0xCD0B7: a FieldSymbol with the name 'value' already exists in 'RoleNameExistsAsAttributeName.B' (defined in RoleNameExistsAsAttributeName.cd:<10,4>)",
          Log.getFindings().get(1).getMsg());

      Log.getFindings().clear();
    }
  }

  @Test
  public void testWithCircularAssocsAll() throws IOException {
    this.testWithCircularAssocs(new CDAssociationCreateFieldsFromAllRoles());
  }

  @Test
  @Ignore // TODO: See #3940
  public void testWithCircularAssocsNavigable() throws IOException {
    this.testWithCircularAssocs(new CDAssociationCreateFieldsFromNavigableRoles());
  }

  protected void testWithCircularAssocs(
      CDAssociationCreateFieldsFromAllRoles cdAssociationCreateFieldsTrafo) throws IOException {
    ASTCDCompilationUnit compUnit = parse("cd/codegen/CyclicAssocs.cd");
    prepareST(compUnit);

    final CD4AnalysisTraverser traverser = CD4AnalysisMill.inheritanceTraverser();
    traverser.add4CDAssociation(cdAssociationCreateFieldsTrafo);
    traverser.setCDAssociationHandler(cdAssociationCreateFieldsTrafo);
    cdAssociationCreateFieldsTrafo.transform(compUnit);

    for (ASTCDClass clazz : compUnit.getCDDefinition().getCDClassesList()) {
      assertNotEquals(
          clazz.getName() + " did not generate with its attribute",
          0,
          clazz.getCDAttributeList().size());
    }
  }
}
