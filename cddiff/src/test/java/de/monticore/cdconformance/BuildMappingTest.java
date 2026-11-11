/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconformance;

import static de.monticore.cdconformance.CDConfParameter.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDMethodSignature;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BuildMappingTest extends ConfAbstractTest {
  
  @BeforeEach
  public void init() {
    parseModels("Concrete.cd", "Reference.cd");
    checker = new CDConformanceChecker(Set.of(STEREOTYPE_MAPPING, NAME_MAPPING,
        ALLOW_CARD_RESTRICTION, SRC_TARGET_ASSOC_MAPPING));
    assertTrue(checker.checkConformance(conCD, refCD, Set.of("ref")));
  }
  
  @Test
  public void TestTypeMap() {
    
    ASTCDType account = getType("Account", conCD);
    Set<ASTCDType> refTypes = checker.getIncarnationMapping().getReferenceElements(account);
    Assertions.assertEquals(1, refTypes.size());
    Assertions.assertEquals("Account", refTypes.stream().findFirst().get().getName());
    
    ASTCDType bAccount = getType("BankAccount", conCD);
    refTypes = checker.getIncarnationMapping().getReferenceElements(bAccount);
    Assertions.assertEquals(1, refTypes.size());
    Assertions.assertEquals("Account", refTypes.stream().findFirst().get().getName());
    
    ASTCDType deposit = getType("Deposit", conCD);
    refTypes = checker.getIncarnationMapping().getReferenceElements(deposit);
    Assertions.assertEquals(0, refTypes.size());
  }
  
  @Test
  public void TestAssociationMap() {
    ASTCDAssociation hasItems = getAssociation("hasItems", conCD);
    Set<ASTCDAssociation> refAssoc = checker.getIncarnationMapping().getReferenceElements(hasItems);
    Assertions.assertEquals(1, refAssoc.size());
    Assertions.assertEquals("hasItems", refAssoc.stream().findFirst().get().getName());
  }
  
  @Test
  public void testAttributeMap() {
    ASTCDAttribute name = getAttribute("BankAccount.name", conCD);
    Set<ASTCDAttribute> refAttributes = checker.getIncarnationMapping().getReferenceElements(name);
    Assertions.assertEquals(1, refAttributes.size());
    Assertions.assertEquals("username", refAttributes.stream().findFirst().get().getName());
    
    ASTCDAttribute itemId = getAttribute("Item.itemId", conCD);
    refAttributes = checker.getIncarnationMapping().getReferenceElements(itemId);
    Assertions.assertEquals(1, refAttributes.size());
    Assertions.assertEquals("id", refAttributes.stream().findFirst().get().getName());
  }
  
  @Test
  public void testMethodMap() {
    ASTCDMethod method = getMethod("BankAccount", "execute", conCD);
    Set<ASTCDMethod> refMethod = checker.getIncarnationMapping().getReferenceElements(method);
    Assertions.assertEquals(1, refMethod.size());
    Assertions.assertEquals("operation", refMethod.stream().findFirst().get().getName());
    
    Set<ASTCDMethod> conElements = checker.getIncarnationMapping().getIncarnations(refMethod
        .stream().findFirst().get());
    Assertions.assertEquals(2, conElements.size());
    Assertions.assertEquals(Set.of("execute", "operation"), conElements.stream().map(
        ASTCDMethod::getName).collect(Collectors.toSet()));
  }
  
  private ASTCDType getType(String name, ASTCDCompilationUnit cd) {
    Optional<CDTypeSymbol> symbol = cd.getEnclosingScope().resolveCDType(name);
    Assertions.assertTrue(symbol.isPresent());
    return symbol.get().getAstNode();
  }
  
  private ASTCDAssociation getAssociation(String name, ASTCDCompilationUnit cd) {
    Optional<ASTCDAssociation> association = cd.getCDDefinition().getCDAssociationsList().stream()
        .filter(assoc -> assoc.isPresentName() && assoc.getName().equals(name)).findFirst();
    Assertions.assertTrue(association.isPresent());
    return association.get();
  }
  
  private ASTCDAttribute getAttribute(String name, ASTCDCompilationUnit cd) {
    Optional<FieldSymbol> symbol = cd.getEnclosingScope().resolveField(name);
    Assertions.assertTrue(symbol.isPresent());
    return (ASTCDAttribute) symbol.get().getAstNode();
  }
  
  private ASTCDMethod getMethod(String typeName, String methodName, ASTCDCompilationUnit cd) {
    Optional<CDTypeSymbol> type = cd.getEnclosingScope().resolveCDType(typeName);
    Assertions.assertTrue(type.isPresent());
    ASTCDMethodSignature method = type.get().getMethodSignatureList(methodName).get(0).getAstNode();
    
    return (ASTCDMethod) method;
  }
  
}
