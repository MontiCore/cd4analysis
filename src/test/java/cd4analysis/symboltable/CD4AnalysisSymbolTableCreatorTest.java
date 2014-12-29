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
  public void test() {
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

    CDTypeSymbol personType = (CDTypeSymbol) topScope.resolve("Person", CDTypeSymbol.KIND).orNull();
    assertNotNull(personType);

    assertNotNull(personType.getSpannedScope());
    assertSame(personType, personType.getSpannedScope().getSpanningSymbol().get());
    assertEquals("cd4analysis.symboltable.CD1.Person", personType.getName());
    assertEquals(1, personType.getFields().size());
    assertEquals("name", personType.getField("name").get().getName());

    CDTypeSymbol profType = (CDTypeSymbol) topScope.resolve("Prof", CDTypeSymbol.KIND).orNull();
    assertNotNull(profType);
    assertEquals("cd4analysis.symboltable.CD1.Prof", profType.getName());

    assertEquals(1, profType.getSuperClasses().size());

  }
  
}