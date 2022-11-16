/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen.method.accessor;

import static de.monticore.cd.codegen.DecoratorAssert.*;
import static de.monticore.cd.codegen.DecoratorTestUtil.getMethodBy;
import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

import de.monticore.cd.codegen.methods.accessor.ListAccessorDecorator;
import de.monticore.cd.facade.CDAttributeFacade;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;
import de.se_rwth.commons.logging.LogStub;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class ListAccessorDecoratorTest {

  private final GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private List<ASTCDMethod> methods;

  @Before
  public void setup() {
    LogStub.init();
    ASTMCType listType = MCTypeFacade.getInstance().createListTypeOf(String.class);
    ASTCDAttribute attribute =
        CDAttributeFacade.getInstance().createAttribute(PROTECTED.build(), listType, "a");
    ListAccessorDecorator listAccessorDecorator = new ListAccessorDecorator(glex);
    this.methods = listAccessorDecorator.decorate(attribute);
  }

  @Test
  public void testMethods() {
    assertEquals(19, methods.size());
  }

  @Test
  public void testGetListMethod() {
    ASTCDMethod method = getMethodBy("getAList", 0, this.methods);
    ASTMCType expectedReturnType = MCTypeFacade.getInstance().createListTypeOf("String");
    assertDeepEquals(expectedReturnType, method.getMCReturnType().getMCType());
    assertDeepEquals(PUBLIC, method.getModifier());
  }

  @Test
  public void testContainsMethod() {
    ASTCDMethod method = getMethodBy("containsA", this.methods);
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertBoolean(method.getMCReturnType().getMCType());
    assertDeepEquals(PUBLIC, method.getModifier());

    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertDeepEquals(Object.class, parameter.getMCType());
    assertEquals("element", parameter.getName());
  }

  @Test
  public void testContainsAllMethod() {
    ASTCDMethod method = getMethodBy("containsAllA", this.methods);
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertBoolean(method.getMCReturnType().getMCType());
    assertDeepEquals(PUBLIC, method.getModifier());

    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    ASTMCType expectedParameterType = MCTypeFacade.getInstance().createCollectionTypeOf("?");
    assertDeepEquals(expectedParameterType, parameter.getMCType());
    assertEquals("collection", parameter.getName());
  }

  @Test
  public void testIsEmptyMethod() {
    ASTCDMethod method = getMethodBy("isEmptyA", this.methods);
    assertTrue(method.getCDParameterList().isEmpty());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertBoolean(method.getMCReturnType().getMCType());
    assertDeepEquals(PUBLIC, method.getModifier());
  }

  @Test
  public void testSizeMethod() {
    ASTCDMethod method = getMethodBy("sizeA", this.methods);
    assertTrue(method.getCDParameterList().isEmpty());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertInt(method.getMCReturnType().getMCType());
    assertDeepEquals(PUBLIC, method.getModifier());
  }

  @Test
  public void testToArrayWithParamMethod() {
    ASTCDMethod method = getMethodBy("toArrayA", 1, this.methods);
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertArrayOf(String.class, method.getMCReturnType().getMCType());
    assertDeepEquals(PUBLIC, method.getModifier());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertArrayOf(String.class, parameter.getMCType());
    assertEquals("array", parameter.getName());
  }

  @Test
  public void testToArrayMethod() {
    ASTCDMethod method = getMethodBy("toArrayA", 0, this.methods);
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertArrayOf(Object.class, method.getMCReturnType().getMCType());
    assertDeepEquals(PUBLIC, method.getModifier());
  }

  @Test
  public void testSpliteratorMethod() {
    ASTCDMethod method = getMethodBy("spliteratorA", this.methods);
    assertTrue(method.getCDParameterList().isEmpty());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals("Spliterator<String>", method.getMCReturnType().getMCType());
    assertDeepEquals(PUBLIC, method.getModifier());
  }

  @Test
  public void testStreamMethod() {
    ASTCDMethod method = getMethodBy("streamA", this.methods);
    assertTrue(method.getCDParameterList().isEmpty());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals("Stream<String>", method.getMCReturnType().getMCType());
    assertDeepEquals(PUBLIC, method.getModifier());
  }

  @Test
  public void testParallelStreamMethod() {
    ASTCDMethod method = getMethodBy("parallelStreamA", this.methods);
    assertTrue(method.getCDParameterList().isEmpty());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals("Stream<String>", method.getMCReturnType().getMCType());
    assertDeepEquals(PUBLIC, method.getModifier());
  }

  @Test
  public void testGetWithIndexMethod() {
    ASTCDMethod method = getMethodBy("getA", 1, this.methods);
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());
    assertDeepEquals(PUBLIC, method.getModifier());

    ASTCDParameter parameter = method.getCDParameter(0);
    assertInt(parameter.getMCType());
    assertEquals("index", parameter.getName());
  }

  @Test
  public void testIndexOfMethod() {
    ASTCDMethod method = getMethodBy("indexOfA", this.methods);
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertInt(method.getMCReturnType().getMCType());
    assertDeepEquals(PUBLIC, method.getModifier());

    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertDeepEquals(Object.class, parameter.getMCType());
    assertEquals("element", parameter.getName());
  }

  @Test
  public void testLastIndexOfMethod() {
    ASTCDMethod method = getMethodBy("lastIndexOfA", this.methods);
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertInt(method.getMCReturnType().getMCType());
    assertDeepEquals(PUBLIC, method.getModifier());

    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertDeepEquals(Object.class, parameter.getMCType());
    assertEquals("element", parameter.getName());
  }

  @Test
  public void testEqualsMethod() {
    ASTCDMethod method = getMethodBy("equalsA", this.methods);
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertBoolean(method.getMCReturnType().getMCType());
    assertDeepEquals(PUBLIC, method.getModifier());

    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertDeepEquals(Object.class, parameter.getMCType());
    assertEquals("o", parameter.getName());
  }

  @Test
  public void testHashCodeMethod() {
    ASTCDMethod method = getMethodBy("hashCodeA", this.methods);
    assertTrue(method.getCDParameterList().isEmpty());
    assertEquals(
        "int",
        method.getMCReturnType().printType(new MCBasicTypesFullPrettyPrinter(new IndentPrinter())));
    assertDeepEquals(PUBLIC, method.getModifier());
  }

  @Test
  public void testListIteratorMethod() {
    ASTCDMethod method = getMethodBy("listIteratorA", 0, this.methods);
    assertTrue(method.getCDParameterList().isEmpty());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals("ListIterator<String>", method.getMCReturnType().getMCType());
    assertDeepEquals(PUBLIC, method.getModifier());
  }

  @Test
  public void testListIteratorWithIndexMethod() {
    ASTCDMethod method = getMethodBy("listIteratorA", 1, this.methods);
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals("ListIterator<String>", method.getMCReturnType().getMCType());
    assertDeepEquals(PUBLIC, method.getModifier());

    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertInt(parameter.getMCType());
    assertEquals("index", parameter.getName());
  }

  @Test
  public void testSubListMethod() {
    ASTCDMethod method = getMethodBy("subListA", this.methods);
    ASTMCType expectedReturnType = MCTypeFacade.getInstance().createListTypeOf("String");
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(expectedReturnType, method.getMCReturnType().getMCType());
    assertDeepEquals(PUBLIC, method.getModifier());

    assertEquals(2, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertInt(parameter.getMCType());
    assertEquals("start", parameter.getName());

    parameter = method.getCDParameter(1);
    assertInt(parameter.getMCType());
    assertEquals("end", parameter.getName());
  }

  @Test
  public void testDerivedAttr() {
    ASTMCType listType = MCTypeFacade.getInstance().createListTypeOf(String.class);
    ASTCDAttribute attribute =
        CDAttributeFacade.getInstance().createAttribute(PROTECTED.build(), listType, "a");
    attribute.getModifier().setDerived(true);
    ListAccessorDecorator listAccessorDecorator = new ListAccessorDecorator(glex);
    List<ASTCDMethod> methList = listAccessorDecorator.decorate(attribute);
    assertEquals(1, methList.size());
    assertTrue(methList.get(0).getModifier().isAbstract());
  }
}
