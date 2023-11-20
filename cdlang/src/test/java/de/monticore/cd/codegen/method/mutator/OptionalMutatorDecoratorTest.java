/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen.method.mutator;

import static de.monticore.cd.codegen.DecoratorAssert.assertDeepEquals;
import static de.monticore.cd.codegen.DecoratorTestUtil.getMethodBy;
import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

import de.monticore.cd.codegen.methods.mutator.OptionalMutatorDecorator;
import de.monticore.cd.facade.CDAttributeFacade;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.LogStub;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class OptionalMutatorDecoratorTest {

  private final GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private List<ASTCDMethod> methods;

  @Before
  public void setup() {
    LogStub.init();
    ASTMCType optionalType = MCTypeFacade.getInstance().createOptionalTypeOf(String.class);
    ASTCDAttribute attribute =
        CDAttributeFacade.getInstance().createAttribute(PROTECTED.build(), optionalType, "a");
    OptionalMutatorDecorator optionalMutatorDecorator = new OptionalMutatorDecorator(glex);
    this.methods = optionalMutatorDecorator.decorate(attribute);
  }

  @Test
  public void testMethods() {
    assertEquals(2, methods.size());
  }

  @Test
  public void testGetMethod() {
    ASTCDMethod method = getMethodBy("setA", this.methods);
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertDeepEquals(PUBLIC, method.getModifier());
    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertDeepEquals(String.class, parameter.getMCType());
    assertEquals("a", parameter.getName());
  }

  @Test
  public void testIsPresentMethod() {
    ASTCDMethod method = getMethodBy("setAAbsent", this.methods);
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getCDParameterList().isEmpty());
  }
}
