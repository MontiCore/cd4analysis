/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen.method.accessor;

import static de.monticore.cd.codegen.DecoratorAssert.assertBoolean;
import static de.monticore.cd.codegen.DecoratorAssert.assertDeepEquals;
import static de.monticore.cd.codegen.DecoratorTestUtil.getMethodBy;
import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

import de.monticore.cd.codegen.DecoratorTestCase;
import de.monticore.cd.codegen.methods.accessor.OptionalAccessorDecorator;
import de.monticore.cd.facade.CDAttributeFacade;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.LogStub;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class OptionalAccessorDecoratorTest extends DecoratorTestCase {

  private final GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private List<ASTCDMethod> methods;

  @Before
  public void setup() {
    LogStub.init();

    // dummy cd needed for a good generated error Code
    ASTCDCompilationUnit cd = this.parse("de", "monticore", "cd", "codegen", "Automaton");

    ASTMCType optType = MCTypeFacade.getInstance().createOptionalTypeOf(String.class);
    ASTCDAttribute attribute =
        CDAttributeFacade.getInstance().createAttribute(PROTECTED.build(), optType, "a");
    OptionalAccessorDecorator optionalAccessorDecorator = new OptionalAccessorDecorator(glex);
    this.methods = optionalAccessorDecorator.decorate(attribute);
  }

  @Test
  public void testMethods() {
    assertEquals(2, methods.size());
  }

  @Test
  public void testGetMethod() {
    ASTCDMethod method = getMethodBy("getA", this.methods);
    assertTrue(method.getCDParameterList().isEmpty());
    Assert.assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());
    assertDeepEquals(PUBLIC, method.getModifier());
  }

  @Test
  public void testIsPresentMethod() {
    ASTCDMethod method = getMethodBy("isPresentA", this.methods);
    Assert.assertTrue(method.getMCReturnType().isPresentMCType());
    assertBoolean(method.getMCReturnType().getMCType());
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getCDParameterList().isEmpty());
  }

  @Test
  public void testDerivedAttr() {
    ASTMCType optType = MCTypeFacade.getInstance().createOptionalTypeOf(String.class);
    ASTCDAttribute attribute =
        CDAttributeFacade.getInstance().createAttribute(PROTECTED.build(), optType, "a");
    attribute.getModifier().setDerived(true);
    OptionalAccessorDecorator optionalAccessorDecorator = new OptionalAccessorDecorator(glex);
    List<ASTCDMethod> methList = optionalAccessorDecorator.decorate(attribute);
    assertEquals(1, methList.size());
    assertTrue(methList.get(0).getModifier().isAbstract());
  }
}
