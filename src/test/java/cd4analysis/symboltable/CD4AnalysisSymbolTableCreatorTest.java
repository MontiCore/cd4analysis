package cd4analysis.symboltable;

import cd4analysis.CD4AnalysisLanguage;
import de.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.symboltable.CompilationUnitScope;
import de.monticore.symboltable.ResolverConfiguration;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.resolving.DefaultResolver;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class CD4AnalysisSymbolTableCreatorTest {

  @Test
  public void testSymbolTableCreation() {
    ResolverConfiguration resolverConfiguration = new ResolverConfiguration();
    resolverConfiguration.addTopScopeResolvers(DefaultResolver.newResolver(CDTypeSymbol.class,
        CDTypeSymbol.KIND));
    resolverConfiguration.addTopScopeResolvers(DefaultResolver.newResolver(CDFieldSymbol.class,
        CDFieldSymbol.KIND));

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

    CDTypeSymbol profType = topScope.<CDTypeSymbol>resolve("Prof", CDTypeSymbol.KIND).orNull();
    assertNotNull(profType);
    assertEquals("cd4analysis.symboltable.CD1.Prof", profType.getName());
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

  }
  
}