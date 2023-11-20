/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen.method.accessor;

import static de.monticore.cd.codegen.DecoratorAssert.assertDeepEquals;
import static de.monticore.cd.codegen.DecoratorTestUtil.getMethodBy;
import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.monticore.cd.codegen.methods.accessor.MandatoryAccessorDecorator;
import de.monticore.cd.facade.CDAttributeFacade;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.logging.LogStub;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class MandatoryAccessorDecoratorTest {

  private final GlobalExtensionManagement glex = new GlobalExtensionManagement();

  @Before
  public void setup() {
    LogStub.init();
  }

  @Test
  public void testGetMethodString() {
    ASTCDAttribute attribute =
        CDAttributeFacade.getInstance().createAttribute(PROTECTED.build(), String.class, "a");

    MandatoryAccessorDecorator mandatoryAccessorDecorator = new MandatoryAccessorDecorator(glex);
    List<ASTCDMethod> methods = mandatoryAccessorDecorator.decorate(attribute);

    assertEquals(1, methods.size());
    ASTCDMethod method = getMethodBy("getA", methods);
    assertTrue(method.getCDParameterList().isEmpty());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());
    assertDeepEquals(PUBLIC, method.getModifier());
  }

  @Test
  public void testGetMethodBoolean() {
    ASTCDAttribute attribute =
        CDAttributeFacade.getInstance().createAttribute(PROTECTED.build(), String.class, "a");
    MandatoryAccessorDecorator mandatoryAccessorDecorator = new MandatoryAccessorDecorator(glex);
    List<ASTCDMethod> methods = mandatoryAccessorDecorator.decorate(attribute);

    assertEquals(1, methods.size());
    ASTCDMethod method = getMethodBy("getA", methods);
    assertTrue(method.getCDParameterList().isEmpty());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());
    assertDeepEquals(PUBLIC, method.getModifier());
  }

  @Test
  public void testDerivedAttr() {
    ASTCDAttribute attribute =
        CDAttributeFacade.getInstance().createAttribute(PROTECTED.build(), String.class, "a");
    attribute.getModifier().setDerived(true);
    MandatoryAccessorDecorator mandatoryAccessorDecorator = new MandatoryAccessorDecorator(glex);
    List<ASTCDMethod> methList = mandatoryAccessorDecorator.decorate(attribute);
    assertEquals(1, methList.size());
    assertTrue(methList.get(0).getModifier().isAbstract());
  }
}
