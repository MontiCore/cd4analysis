/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen.method.mutator;

import de.monticore.cd.codegen.methods.mutator.ListMutatorDecorator;
import de.monticore.cd.facade.CDAttributeFacade;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.cd.codegen.DecoratorAssert.*;
import static de.monticore.cd.codegen.DecoratorTestUtil.getMethodBy;
import static de.monticore.cd.codegen.DecoratorTestUtil.getMethodsBy;
import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class ListMutatorDecoratorTest {

  private final GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private List<ASTCDMethod> methods;

  @Before
  public void setup() {
    LogStub.init();
    LogStub.enableFailQuick(false);
    ASTMCType listType = MCTypeFacade.getInstance().createListTypeOf(String.class);
    ASTCDAttribute attribute = CDAttributeFacade.getInstance().createAttribute(PROTECTED.build(), listType, "a");
    ListMutatorDecorator listMutatorDecorator = new ListMutatorDecorator(glex);
    this.methods = listMutatorDecorator.decorate(attribute);
  }

  @Test
  public void testMethods() {
    assertEquals(15, methods.size());
  }

  @Test
  public void testSetListMethod() {
    ASTCDMethod method = getMethodBy("setAList", 1, this.methods);
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertDeepEquals(PUBLIC, method.getModifier());
    assertEquals(1,method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameterList().get(0);
    assertListOf(String.class, parameter.getMCType());
    assertEquals("a", parameter.getName());
  }

  @Test
  public void testClearMethod() {
    ASTCDMethod method = getMethodBy("clearA", this.methods);
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getCDParameterList().isEmpty());
  }

  @Test
  public void testAddMethod() {
    ASTCDMethod method = getMethodBy("addA", 1, this.methods);
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertBoolean(method.getMCReturnType().getMCType());
    assertDeepEquals(PUBLIC, method.getModifier());
    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertDeepEquals(String.class, parameter.getMCType());
    assertEquals("element", parameter.getName());
  }

  @Test
  public void testAddAllMethod() {
    ASTCDMethod method = getMethodBy("addAllA", 1, this.methods);
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertBoolean(method.getMCReturnType().getMCType());
    assertDeepEquals(PUBLIC, method.getModifier());
    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    ASTMCType expectedParameterType = MCTypeFacade.getInstance().createCollectionTypeOf("? extends String");
    assertDeepEquals(expectedParameterType, parameter.getMCType());
    assertEquals("collection", parameter.getName());
  }

  @Test
  public void testRemoveMethod() {
    List<ASTCDMethod> methods = getMethodsBy("removeA", 1, this.methods);
    assertEquals(2, methods.size());
    ASTMCType expectedReturnType = MCTypeFacade.getInstance().createBooleanType();
    methods = methods.stream().filter(m -> m.getMCReturnType().getMCType().deepEquals(expectedReturnType)).collect(Collectors.toList());
    assertEquals(1, methods.size());
    ASTCDMethod method = methods.get(0);
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertBoolean(method.getMCReturnType().getMCType());
    assertDeepEquals(PUBLIC, method.getModifier());
    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertDeepEquals(Object.class, parameter.getMCType());
    assertEquals("element", parameter.getName());
  }

  @Test
  public void testRemoveAllMethod() {
    ASTCDMethod method = getMethodBy("removeAllA", this.methods);
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
  public void testRetainAllMethod() {
    ASTCDMethod method = getMethodBy("retainAllA", this.methods);
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
  public void testRemoveIfMethod() {
    ASTCDMethod method = getMethodBy("removeIfA", this.methods);
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertBoolean(method.getMCReturnType().getMCType());
    assertDeepEquals(PUBLIC, method.getModifier());
    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertDeepEquals("Predicate<? super String>", parameter.getMCType());
    assertEquals("filter", parameter.getName());
  }

  @Test
  public void testForEachMethod() {
    ASTCDMethod method = getMethodBy("forEachA", this.methods);
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertDeepEquals(PUBLIC, method.getModifier());
    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertDeepEquals("Consumer<? super String>", parameter.getMCType());
    assertEquals("action", parameter.getName());
  }

  @Test
  public void testAddWithIndexMethod() {
    ASTCDMethod method = getMethodBy("addA", 2, this.methods);
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertDeepEquals(PUBLIC, method.getModifier());
    assertEquals(2, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertInt(parameter.getMCType());
    assertEquals("index", parameter.getName());
    parameter = method.getCDParameter(1);
    assertDeepEquals(String.class, parameter.getMCType());
    assertEquals("element", parameter.getName());
  }

  @Test
  public void testAddAllWithIndexMethod() {
    ASTCDMethod method = getMethodBy("addAllA", 2, this.methods);
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertBoolean(method.getMCReturnType().getMCType());
    assertDeepEquals(PUBLIC, method.getModifier());
    assertEquals(2, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertInt(parameter.getMCType());
    assertEquals("index", parameter.getName());
    parameter = method.getCDParameter(1);
    ASTMCType expectedParameterType = MCTypeFacade.getInstance().createCollectionTypeOf("? extends String");

    assertDeepEquals(expectedParameterType, parameter.getMCType());
    assertEquals("collection", parameter.getName());
  }

  @Test
  public void testRemoveWithIndexMethod() {
    List<ASTCDMethod> methods = getMethodsBy("removeA", 1, this.methods);
    assertEquals(2, methods.size());
    ASTMCType exptectedReturnType = MCTypeFacade.getInstance().createQualifiedType(String.class);
    methods = methods.stream().filter(m -> m.getMCReturnType().getMCType().deepEquals(exptectedReturnType)).collect(Collectors.toList());
    assertEquals(1, methods.size());
    ASTCDMethod method = methods.get(0);
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(exptectedReturnType, method.getMCReturnType().getMCType());
    assertDeepEquals(PUBLIC, method.getModifier());
    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertInt(parameter.getMCType());
    assertEquals("index", parameter.getName());
  }

  @Test
  public void testSetWithIndexMethod() {
    ASTCDMethod method = getMethodBy("setA", 2, this.methods);
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());
    assertDeepEquals(PUBLIC, method.getModifier());
    assertEquals(2, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertInt(parameter.getMCType());
    assertEquals("index", parameter.getName());
    parameter = method.getCDParameter(1);
    assertDeepEquals(String.class, parameter.getMCType());
    assertEquals("element", parameter.getName());
  }

  @Test
  public void testReplaceAllMethod() {
    ASTCDMethod method = getMethodBy("replaceAllA", this.methods);
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertDeepEquals(PUBLIC, method.getModifier());
    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertDeepEquals("UnaryOperator<String>", parameter.getMCType());
    assertEquals("operator", parameter.getName());
  }

  @Test
  public void testSortMethod() {
    ASTCDMethod method = getMethodBy("sortA", this.methods);
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertDeepEquals(PUBLIC, method.getModifier());
    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertDeepEquals("Comparator<? super String>", parameter.getMCType());
    assertEquals("comparator", parameter.getName());
  }

  @Test
  public void testDerivedAttr() {
    ASTMCType listType = MCTypeFacade.getInstance().createListTypeOf(String.class);
    ASTCDAttribute attribute = CDAttributeFacade.getInstance().createAttribute(PROTECTED.build(), listType, "a");
    attribute.getModifier().setDerived(true);
    ListMutatorDecorator listMutatorDecorator = new ListMutatorDecorator(glex);
    List<ASTCDMethod> methList = listMutatorDecorator.decorate(attribute);
    assertEquals(0, methList.size());
  }
}
