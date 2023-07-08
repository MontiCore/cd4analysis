package de.monticore.conformance;

import static de.monticore.conformance.ConfParameter.NAME_MAPPING;
import static de.monticore.conformance.ConfParameter.STEREOTYPE_MAPPING;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BuildMappingTest extends ConfAbstractTest {
  @BeforeEach
  public void init() {
    parseModels("Concrete.cd", "Reference.cd");
    checker = new ConformanceChecker(Set.of(STEREOTYPE_MAPPING, NAME_MAPPING));
    assertTrue(checker.checkConformance(conCD, refCD, Set.of("ref")));
  }

  @Test
  public void TestTypeMap() {

    ASTCDType account = getType("Account", conCD);
    List<ASTCDType> refTypes = checker.getRefElements(account);
    Assertions.assertEquals(1, refTypes.size());
    Assertions.assertEquals("Account", refTypes.get(0).getName());

    ASTCDType bAccount = getType("BankAccount", conCD);
    refTypes = checker.getRefElements(bAccount);
    Assertions.assertEquals(1, refTypes.size());
    Assertions.assertEquals("Account", refTypes.get(0).getName());

    ASTCDType deposit = getType("Deposit", conCD);
    refTypes = checker.getRefElements(deposit);
    Assertions.assertEquals(0, refTypes.size());
  }

  @Test
  public void TestAssociationMap() {
    ASTCDAssociation hasItems = getAssociation("hasItems", conCD);
    List<ASTCDAssociation> refAssoc = checker.getRefElements(hasItems);
    Assertions.assertEquals(1, refAssoc.size());
    Assertions.assertEquals("hasItems", refAssoc.get(0).getName());
  }

  @Test
  public void testAttributeMap() {
    ASTCDType bAccount = getType("BankAccount", conCD);
    ASTCDAttribute name = getAttribute("BankAccount.name", conCD);
    List<ASTCDAttribute> refAttributes = checker.getRefElements(bAccount, name);
    Assertions.assertEquals(1, refAttributes.size());
    Assertions.assertEquals("username", refAttributes.get(0).getName());

    ASTCDType item = getType("Item", conCD);
    ASTCDAttribute itemId = getAttribute("Item.itemId", conCD);
    refAttributes = checker.getRefElements(item, itemId);
    Assertions.assertEquals(1, refAttributes.size());
    Assertions.assertEquals("id", refAttributes.get(0).getName());
  }

  private ASTCDType getType(String name, ASTCDCompilationUnit cd) {
    Optional<CDTypeSymbol> symbol = cd.getEnclosingScope().resolveCDType(name);
    Assertions.assertTrue(symbol.isPresent());
    return symbol.get().getAstNode();
  }

  private ASTCDAssociation getAssociation(String name, ASTCDCompilationUnit cd) {
    Optional<ASTCDAssociation> association =
        cd.getCDDefinition().getCDAssociationsList().stream()
            .filter(assoc -> assoc.isPresentName() && assoc.getName().equals(name))
            .findFirst();
    Assertions.assertTrue(association.isPresent());
    return association.get();
  }

  private ASTCDAttribute getAttribute(String name, ASTCDCompilationUnit cd) {
    Optional<FieldSymbol> symbol = cd.getEnclosingScope().resolveField(name);
    Assertions.assertTrue(symbol.isPresent());
    return (ASTCDAttribute) symbol.get().getAstNode();
  }
}
