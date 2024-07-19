/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._parser.CD4CodeParser;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.se_rwth.commons.logging.LogStub;
import java.io.IOException;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;

public class ConstructorDecoratorTest {

  @Before
  public void before() {
    CD4CodeMill.globalScope().clear();
    CD4CodeMill.reset();
    CD4CodeMill.init();
    LogStub.init();
  }

  @Test
  public void testDecorator() throws IOException {
    CD4CodeParser parser = new CD4CodeParser();
    final Optional<ASTCDCompilationUnit> optAST =
        parser.parse("src/test/resources/de/monticore/cd/codegen/constructor/SimpleCD.cd");
    assertTrue(optAST.isPresent());
    final ASTCDCompilationUnit ast = optAST.get();

    ConstructorDecorator decorator = new ConstructorDecorator();

    assertEquals(2, ast.getCDDefinition().getCDClassesList().size());
    assertEquals(ast.getCDDefinition().getCDClassesList().get(0).getCDMemberList().size(), 4);
    assertTrue(ast.getCDDefinition().getCDClassesList().get(1).getCDConstructorList().isEmpty());
    decorator.decorate(ast);
    assertEquals(ast.getCDDefinition().getCDClassesList().get(0).getCDMemberList().size(), 6);
    assertEquals(ast.getCDDefinition().getCDClassesList().get(1).getCDMemberList().size(), 1);
    assertEquals(
        ((ASTCDConstructor)
                (ast.getCDDefinition().getCDClassesList().get(0).getCDMemberList().get(5)))
            .getCDParameterList()
            .get(0)
            .getName(),
        "x");
    assertEquals(
        ((ASTCDConstructor)
                (ast.getCDDefinition().getCDClassesList().get(0).getCDMemberList().get(5)))
            .getCDParameterList()
            .get(1)
            .getName(),
        "isTrue");
  }
}
