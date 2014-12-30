package cd4analysis.symboltable;

import cd4analysis.CD4AnalysisLanguage;
import de.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.symboltable.CompilationUnitScope;
import de.monticore.symboltable.ResolverConfiguration;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.resolving.DefaultResolver;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class CD4AnalysisSymbolTableCreatorTest {

  @Test
  public void testSymbolTableCreation() {
    ResolverConfiguration resolverConfiguration = new ResolverConfiguration();
    resolverConfiguration.addTopScopeResolvers(DefaultResolver.newResolver(CDTypeSymbol.class,
        CDTypeSymbol.KIND));
    resolverConfiguration.addTopScopeResolvers(DefaultResolver.newResolver(CDFieldSymbol.class,
        CDFieldSymbol.KIND));
    resolverConfiguration.addTopScopeResolvers(DefaultResolver.newResolver(CDAssociationSymbol.class,
        CDAssociationSymbol.KIND));

    CD4AnalysisLanguage cdLanguage = new CD4AnalysisLanguage();

    ASTCDCompilationUnit compilationUnit;
    try {
      compilationUnit = cdLanguage.getParser().parse
          ("src/test/resources/cd4analysis/symboltable/CD1.cd").get();
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }

    Scope topScope = cdLanguage.getSymbolTableCreator(resolverConfiguration, null).get()
        .createFromAST(compilationUnit);

    assertTrue(topScope instanceof CompilationUnitScope);

    CDTypeSymbol personType = topScope.<CDTypeSymbol>resolve("Person", CDTypeSymbol.KIND).orNull();
    assertNotNull(personType);

    assertNotNull(personType.getSpannedScope());
    assertSame(personType, personType.getSpannedScope().getSpanningSymbol().get());
    assertEquals("cd4analysis.symboltable.CD1.Person", personType.getName());
    assertEquals(1, personType.getFields().size());
    assertEquals("name", personType.getField("name").get().getName());
    // Stereotypes
    assertEquals(2, personType.getStereotypes().size());
    assertEquals("S1", personType.getStereotype("S1").get().getName());
    assertEquals("S2", personType.getStereotype("S2").get().getName());
    // TODO PN name and value are not distinguished. Is this ok?
    assertEquals("S1", personType.getStereotype("S1").get().getValue());
    assertEquals("S2", personType.getStereotype("S2").get().getValue());
    // Field Stereotypes
    assertEquals(1, personType.getField("name").get().getStereotypes().size());
    assertEquals("SF", personType.getField("name").get().getStereotype("SF").get().getName());
    assertEquals("SF", personType.getField("name").get().getStereotype("SF").get().getValue());

    CDTypeSymbol profType = topScope.<CDTypeSymbol>resolve("Prof", CDTypeSymbol.KIND).orNull();
    assertNotNull(profType);
    assertEquals("cd4analysis.symboltable.CD1.Prof", profType.getName());
    assertEquals(1, profType.getFields().size());
    assertEquals("uni", profType.getField("uni").get().getName());
    assertTrue(profType.getField("uni").get().isDerived());
    // Super class
    assertTrue(profType.getSuperClass().isPresent());
    assertEquals(personType.getName(), profType.getSuperClass().get().getName());
    // Interfaces
    assertEquals(2, profType.getInterfaces().size());
    assertEquals("cd4analysis.symboltable.CD1.Printable", profType.getInterfaces().get(0).getName());
    assertEquals("cd4analysis.symboltable.CD1.Callable", profType.getInterfaces().get(1).getName());


    CDTypeSymbol printableType = topScope.<CDTypeSymbol>resolve("Printable", CDTypeSymbol.KIND)
        .orNull();
    assertNotNull(printableType);
    assertEquals("cd4analysis.symboltable.CD1.Printable", printableType.getName());
    assertTrue(printableType.isInterface());

    CDTypeSymbol callableType = topScope.<CDTypeSymbol>resolve("Callable", CDTypeSymbol.KIND)
        .orNull();
    assertNotNull(callableType);
    assertEquals("cd4analysis.symboltable.CD1.Callable", callableType.getName());
    assertTrue(callableType.isInterface());
    assertEquals(1, callableType.getInterfaces().size());
    assertEquals("cd4analysis.symboltable.CD1.Printable", callableType.getInterfaces().get(0).getName());

    CDTypeSymbol enumType = topScope.<CDTypeSymbol>resolve("E", CDTypeSymbol.KIND).orNull();
    assertNotNull(enumType);
    assertEquals("cd4analysis.symboltable.CD1.E", enumType.getName());
    assertTrue(enumType.isEnum());
    // Enum Constants
    assertEquals(2, enumType.getEnumConstants().size());
    assertEquals("A", enumType.getEnumConstants().get(0).getName());
    assertEquals(enumType.getName(), enumType.getEnumConstants().get(0).getType().getName());
    assertEquals("B", enumType.getEnumConstants().get(1).getName());
    assertEquals(enumType.getName(), enumType.getEnumConstants().get(1).getType().getName());
    assertEquals(enumType.getEnumConstants(), enumType.getFields());
    // Interfaces
    assertEquals(1, enumType.getInterfaces().size());
    assertEquals("cd4analysis.symboltable.CD1.Printable", enumType.getInterfaces().get(0).getName());

    CDAssociationSymbol associationSymbol = topScope.<CDAssociationSymbol>resolve("member",
        CDAssociationSymbol.KIND).orNull();
    assertNotNull(associationSymbol);
    assertEquals("member", associationSymbol.getName());
    assertTrue(associationSymbol.isBidirectional());
    assertEquals(personType.getName(), associationSymbol.getSourceType().getName());
    assertEquals(profType.getName(), associationSymbol.getTargetType().getName());
    assertEquals(Cardinality.STAR, associationSymbol.getSourceCardinality().getMax());
    assertTrue(associationSymbol.getSourceCardinality().isMultiple());
    assertEquals(1, associationSymbol.getTargetCardinality().getMax());
    assertFalse(associationSymbol.getTargetCardinality().isMultiple());

  }
  
}