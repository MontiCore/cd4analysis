/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen.method.mutator;

import de.monticore.cd.codegen.methods.mutator.MandatoryMutatorDecorator;
import de.monticore.cd.facade.CDAttributeFacade;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static de.monticore.cd.codegen.DecoratorAssert.assertDeepEquals;
import static de.monticore.cd.codegen.DecoratorTestUtil.getMethodBy;
import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MandatoryMutatorDecoratorTest {

  private final GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private List<ASTCDMethod> methods;

  @Before
  public void setup() {
    LogStub.init();
    ASTCDAttribute attribute = CDAttributeFacade.getInstance().createAttribute(PROTECTED.build(), String.class, "a");
    MandatoryMutatorDecorator mandatoryMutatorDecorator = new MandatoryMutatorDecorator(glex);
    this.methods = mandatoryMutatorDecorator.decorate(attribute);
  }

  @Test
  public void testMethods() {
    assertEquals(1, methods.size());
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

}
