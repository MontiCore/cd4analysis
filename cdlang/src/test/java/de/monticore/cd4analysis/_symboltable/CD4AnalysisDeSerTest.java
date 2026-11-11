/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4analysis._symboltable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis.CD4AnalysisTestBasis;
import de.monticore.cd4analysis._visitor.CD4AnalysisTraverser;
import de.monticore.cd4analysis.trafo.CD4AnalysisAfterParseTrafo;
import de.monticore.cdassociation._symboltable.CDAssociationSymbol;
import de.monticore.cdassociation._symboltable.CDRoleSymbol;
import de.monticore.cdbasis.CDBasisMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.io.paths.MCPath;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.types.check.SymTypeExpression;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import de.monticore.types3.util.WithinScopeBasicSymbolsResolver;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class CD4AnalysisDeSerTest extends CD4AnalysisTestBasis {
  
  @Test
  public void completeModel() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parse(getFilePath(
        "cd4analysis/parser/STTest.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);
    final ASTCDCompilationUnit node = astcdCompilationUnit.get();
    new CD4AnalysisAfterParseTrafo().transform(node);
    
    final ICD4AnalysisArtifactScope scope = CD4AnalysisMill.scopesGenitorDelegator().createFromAST(
        node);
    
    {
      final CD4AnalysisTraverser traverser = new CD4AnalysisSymbolTableCompleter(node)
          .getTraverser();
      node.accept(traverser);
      
      checkLogError();
    }
    
    final String serializedST = symbols2Json.serialize(scope);
    final ICD4AnalysisArtifactScope deserialize = getGlobalScopeForDeserialization(serializedST);
    
    final Optional<CDTypeSymbol> d = deserialize.resolveCDType("D");
    assertTrue(d.isPresent());
    assertEquals(1, d.get().getFieldList("DEE").size());
  }
  
  @Test
  public void simple() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parse(getFilePath(
        "cd4analysis/parser/SimpleSTTest.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);
    final ASTCDCompilationUnit node = astcdCompilationUnit.get();
    new CD4AnalysisAfterParseTrafo().transform(node);
    
    final ICD4AnalysisArtifactScope scope = CD4AnalysisMill.scopesGenitorDelegator().createFromAST(
        node);
    node.accept(new CD4AnalysisSymbolTableCompleter(node).getTraverser());
    
    final String serializedST = symbols2Json.serialize(scope);
    final ICD4AnalysisArtifactScope deserialize = getGlobalScopeForDeserialization(serializedST);
    
    final Optional<CDTypeSymbol> b = deserialize.resolveCDType("B");
    assertTrue(b.isPresent());
    assertEquals(1, b.get().getCDRoleList("item").size());
  }
  
  @Test
  public void minimal() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parse(getFilePath(
        "cd4analysis/parser/MinimalSTTest.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);
    final ASTCDCompilationUnit node = astcdCompilationUnit.get();
    new CD4AnalysisAfterParseTrafo().transform(node);
    
    final ICD4AnalysisArtifactScope scope = CD4AnalysisMill.scopesGenitorDelegator().createFromAST(
        node);
    node.accept(new CD4AnalysisSymbolTableCompleter(node).getTraverser());
    
    final String serializedST = symbols2Json.serialize(scope);
    final ICD4AnalysisArtifactScope deserialize = getGlobalScopeForDeserialization(serializedST);
    
    final Optional<CDTypeSymbol> a = deserialize.resolveCDType("A");
    assertTrue(a.isPresent());
    final List<CDRoleSymbol> roles = a.get().getCDRoleList();
    assertEquals(2, roles.size());
    
    final Optional<CDAssociationSymbol> s = deserialize.resolveCDAssociation("atoa");
    assertTrue(s.isPresent());
    final CDRoleSymbol left = s.get().getLeft();
    assertEquals("lower", left.getName());
    assertFalse(left.isIsDefinitiveNavigable());
    final CDRoleSymbol right = s.get().getRight();
    assertEquals("upper", right.getName());
    assertTrue(right.isIsDefinitiveNavigable());
  }
  
  /**
   * Serializes a model in which every association is explicitly named twice (parsed twice, with a
   * cleared symbol table in between). Both outputs must be the same and the SymAssociations must
   * be restored when deserializing.
   */
  @Test
  public void namedAssociationRoundTrip() throws IOException {
    final String model = "cdassociation/symboltable/SerializationNamedCD.cd";
    final String cdName = "de.monticore.cdassociation.symboltable.SerializationNamedCD.";
    
    final String first = parseAndSerialize(model);
    
    // clear the symbol table in between both runs
    final ICD4AnalysisGlobalScope globalScope = CD4AnalysisMill.globalScope();
    globalScope.clear();
    globalScope.setSymbolPath(new MCPath(Paths.get(PATH)));
    BuiltInTypes.addBuiltInTypes(globalScope);
    assertTrue(globalScope.getSubScopes().isEmpty());
    
    final String second = parseAndSerialize(model);
    assertEquals(first, second);
    // no warning about unnamed associations
    assertTrue(Log.getFindings().isEmpty(), getJoinedErrors());
    
    // the SymAssociations are identified via the name of the association
    assertTrue(first.contains("\"association\":\"" + cdName + "namedAssoc\""), first);
    assertTrue(first.contains("\"" + cdName + "namedAssoc\":{"), first);
    
    final ICD4AnalysisArtifactScope deserialize = getGlobalScopeForDeserialization(second);
    final CDTypeSymbol a = deserialize.resolveCDType("A").get();
    final CDTypeSymbol b = deserialize.resolveCDType("B").get();
    final CDTypeSymbol d = deserialize.resolveCDType("D").get();
    
    // association symbol and both roles share one SymAssociation
    final CDAssociationSymbol withRoles = deserialize.resolveCDAssociation("namedAssocWithRoles")
        .get();
    assertTrue(withRoles.isPresentAssoc());
    assertTrue(withRoles.getAssoc().isAssociation());
    assertFalse(withRoles.getAssoc().isComposition());
    assertEquals("myA", withRoles.getLeft().getName());
    assertEquals("myB", withRoles.getRight().getName());
    assertSame(a.getCDRoleList("myB").get(0), withRoles.getRight());
    assertSame(b.getCDRoleList("myA").get(0), withRoles.getLeft());
    assertSame(withRoles.getAssoc(), withRoles.getLeft().getAssoc());
    assertSame(withRoles.getAssoc(), withRoles.getRight().getAssoc());
    assertSame(withRoles.getLeft(), withRoles.getRight().getOtherSide());
    
    final CDAssociationSymbol namedAssoc = deserialize.resolveCDAssociation("namedAssoc").get();
    assertTrue(namedAssoc.isPresentAssoc());
    assertNotSame(withRoles.getAssoc(), namedAssoc.getAssoc());
    assertSame(a.getCDRoleList("b").get(0), namedAssoc.getRight());
    assertSame(b.getCDRoleList("a").get(0), namedAssoc.getLeft());
    
    final CDAssociationSymbol composition = deserialize.resolveCDAssociation("namedComposition")
        .get();
    assertTrue(composition.isPresentAssoc());
    assertTrue(composition.getAssoc().isComposition());
    assertSame(d.getCDRoleList("part").get(0), composition.getRight());
    assertSame(composition.getAssoc(), composition.getRight().getAssoc());
  }
  
  /** An association without a name is serialized using a non-deterministic identifier */
  @Test
  public void unnamedAssociationWarns() throws IOException {
    parseAndSerialize("cdassociation/symboltable/SerializationCD.cd");
    assertEquals(0, Log.getErrorCount());
    // A -> B and both direct compositions of D are not named
    assertEquals(3, Log.getFindings().stream().filter(f -> f.isWarning() && f.getMsg().startsWith(
        "0xCD002")).count(), getJoinedErrors());
    Log.getFindings().clear();
  }
  
  protected String parseAndSerialize(String model) throws IOException {
    final ASTCDCompilationUnit node = parse(model);
    final ICD4AnalysisArtifactScope scope = CD4AnalysisMill.scopesGenitorDelegator().createFromAST(
        node);
    node.accept(new CD4AnalysisSymbolTableCompleter(node).getTraverser());
    checkLogError();
    return new CD4AnalysisSymbols2Json().serialize(scope);
  }
  
  @Disabled("no inner classes are allowed")
  @Test
  public void innerClass() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parse(getFilePath(
        "cd4analysis/parser/MinimalSTTest.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);
    final ASTCDCompilationUnit node = astcdCompilationUnit.get();
    new CD4AnalysisAfterParseTrafo().transform(node);
    
    final ICD4AnalysisArtifactScope scope = CD4AnalysisMill.scopesGenitorDelegator().createFromAST(
        node);
    final Optional<CDTypeSymbol> c = scope.resolveCDType("A");
    final ICD4AnalysisScope innerSpanningScope = CD4AnalysisMill.scope();
    innerSpanningScope.setEnclosingScope((ICD4AnalysisScope) c.get().getSpannedScope());
    Optional<SymTypeExpression> aType = WithinScopeBasicSymbolsResolver.resolveType(
        innerSpanningScope, "A");
    assertTrue(aType.isPresent());
    innerSpanningScope.add(OOSymbolsMill.fieldSymbolBuilder().setName("field").setType(aType.get())
        .build());
    final CDTypeSymbol inner = CDBasisMill.cDTypeSymbolBuilder().setName("Inner").setIsClass(true)
        .setIsStatic(true).setSpannedScope(innerSpanningScope).build();
    c.get().getSpannedScope().add(inner);
    innerSpanningScope.setSpanningSymbol(inner);
    
    final String serializedST = symbols2Json.serialize(scope);
    final ICD4AnalysisArtifactScope deserialize = getGlobalScopeForDeserialization(serializedST);
    
    final Optional<CDTypeSymbol> innerType = deserialize.resolveCDTypeMany("Inner").stream()
        .distinct().findAny();
    assertTrue(innerType.isPresent());
    
    final Optional<CDTypeSymbol> a = deserialize.resolveCDType("A");
    assertTrue(a.isPresent());
    
    final Optional<CDTypeSymbol> aInnerType = a.get().getSpannedScope().resolveCDTypeLocally(
        "Inner");
    assertTrue(aInnerType.isPresent());
    assertEquals(innerType, aInnerType);
    
    final Optional<FieldSymbol> fieldSymbol = aInnerType.get().getSpannedScope()
        .resolveFieldLocally("field");
    assertTrue(fieldSymbol.isPresent());
  }
  
  private List<CDTypeSymbol> uniqueList(List<CDTypeSymbol> inner) {
    return inner.stream().distinct().collect(Collectors.toList());
  }
  
  public ICD4AnalysisArtifactScope getGlobalScopeForDeserialization(String serializedST) {
    final ICD4AnalysisArtifactScope deserialize = symbols2Json.deserialize(serializedST);
    
    // explicitly not using the mill for initializing a global scope
    final CD4AnalysisGlobalScope globalScopeForDeserialization = new CD4AnalysisGlobalScope();
    globalScopeForDeserialization.setSymbolPath(new MCPath(Paths.get(PATH)));
    BuiltInTypes.addBuiltInTypes(globalScopeForDeserialization);
    globalScopeForDeserialization.addSubScope(deserialize);
    return deserialize;
  }
  
}
