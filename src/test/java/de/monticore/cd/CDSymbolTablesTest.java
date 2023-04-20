/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code.CD4CodeTestBasis;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cd4code.trafo.CD4CodeAfterParseTrafo;
import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.junit.Test;

public class CDSymbolTablesTest extends CD4CodeTestBasis {

  @Test
  public void getAttributesInHierarchyTest() throws IOException {
    final ICD4CodeArtifactScope scope = getICD4CodeArtifactScope("cd/SymbolTableTest.cd");
    final Optional<CDTypeSymbol> type = scope.resolveCDType("A");
    assertTrue(type.isPresent());
    List<ASTCDAttribute> attributes =
        CDSymbolTables.getAttributesInHierarchy(type.get().getAstNode());
    assertEquals(8, attributes.size());
  }

  @Test
  public void getInheritedAttributesInHierarchyTest() throws IOException {
    final ICD4CodeArtifactScope scope = getICD4CodeArtifactScope("cd/SymbolTableTest.cd");
    final Optional<CDTypeSymbol> type = scope.resolveCDType("A");
    assertTrue(type.isPresent());
    List<ASTCDAttribute> attributes =
        CDSymbolTables.getInheritedAttributesInHierarchy(type.get().getAstNode());
    assertEquals(5, attributes.size());
  }

  @Test
  public void getTransitiveSuperClassesTest() throws IOException {
    final ICD4CodeArtifactScope scope = getICD4CodeArtifactScope("cd/SymbolTableTest.cd");
    final Optional<CDTypeSymbol> type = scope.resolveCDType("A");
    assertTrue(type.isPresent());
    List<ASTCDClass> classes =
        CDSymbolTables.getTransitiveSuperClasses((ASTCDClass) type.get().getAstNode());
    assertEquals(2, classes.size());
  }

  @Test
  public void getTransitiveSuperInterfacesTest() throws IOException {
    final ICD4CodeArtifactScope scope = getICD4CodeArtifactScope("cd/SymbolTableTest.cd");
    final Optional<CDTypeSymbol> type = scope.resolveCDType("A");
    assertTrue(type.isPresent());
    List<ASTCDInterface> interfaces =
        CDSymbolTables.getTransitiveSuperInterfaces(type.get().getAstNode());
    assertEquals(2, interfaces.size());
  }

  @Test
  public void getTransitiveSuperTypesTest() throws IOException {
    final ICD4CodeArtifactScope scope = getICD4CodeArtifactScope("cd/SymbolTableTest.cd");
    final Optional<CDTypeSymbol> type = scope.resolveCDType("A");
    assertTrue(type.isPresent());
    List<ASTCDType> types = CDSymbolTables.getTransitiveSuperTypes(type.get().getAstNode());
    assertEquals(5, types.size());

    final Optional<CDTypeSymbol> gType = scope.resolveCDType("G");
    assertTrue(gType.isPresent());
    List<ASTCDType> gTypes = CDSymbolTables.getTransitiveSuperTypes(gType.get().getAstNode());
    assertEquals(2, gTypes.size());

  }

  @Test
  public void getAssociationsTest() throws IOException {
    final ICD4CodeArtifactScope scope = getICD4CodeArtifactScope("cd/SymbolTableTest.cd");
    final Optional<CDTypeSymbol> type = scope.resolveCDType("A");
    assertTrue(type.isPresent());
    List<ASTCDAssocSide> assocs = CDSymbolTables.getAssociations(type.get().getAstNode());
    assertEquals(1, assocs.size());
  }

  @Test
  public void getAssociationsInHierarchyTest() throws IOException {
    final ICD4CodeArtifactScope scope = getICD4CodeArtifactScope("cd/SymbolTableTest.cd");
    final Optional<CDTypeSymbol> type = scope.resolveCDType("A");
    assertTrue(type.isPresent());
    List<ASTCDAssocSide> assocs =
        CDSymbolTables.getAssociationsInHierarchy(type.get().getAstNode());
    assertEquals(3, assocs.size());
  }

  private ICD4CodeArtifactScope getICD4CodeArtifactScope(String filePath) throws IOException {
    final Optional<ASTCDCompilationUnit> astcdcompilationunit = p.parse(getFilePath(filePath));
    checkNullAndPresence(p, astcdcompilationunit);
    final ASTCDCompilationUnit node = astcdcompilationunit.get();
    new CD4CodeAfterParseTrafo().transform(node);

    final ICD4CodeArtifactScope scope = CD4CodeMill.scopesGenitorDelegator().createFromAST(node);
    node.accept(new CD4CodeSymbolTableCompleter(node).getTraverser());
    return scope;
  }
}
