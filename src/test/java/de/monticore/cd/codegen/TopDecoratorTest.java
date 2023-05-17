/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen;

import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.generating.GeneratorEngine;
import de.monticore.io.paths.MCPath;
import de.monticore.umlmodifier.UMLModifierMill;
import de.monticore.umlstereotype.UMLStereotypeMill;
import de.monticore.umlstereotype._ast.ASTStereoValue;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import de.se_rwth.commons.logging.MCFatalError;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.List;

import static de.monticore.cd.codegen.DecoratorAssert.assertDeepEquals;
import static de.monticore.cd.facade.CDModifier.*;
import static org.junit.Assert.*;

public class TopDecoratorTest extends DecoratorTestCase {

  @Mock
  private MCPath targetPath;

  private TopDecorator topDecorator;

  private ASTCDCompilationUnit topCD;

  @Before
  public void setup() {
    LogStub.init();
    this.targetPath = Mockito.mock(MCPath.class);
    this.topDecorator = new TopDecorator(this.targetPath);
    this.topCD = this.parse("de", "monticore", "cd", "codegen", "Top");
  }

  @Test
  public void testTopNeededHwNotFound() {
    //Replaces the log stub  with normal log to check for properly configured fail quick
    Log.init();

    //Setup
    String reasonString = "needStereoReason";
    ASTStereoValue stereoValue = TopDecorator.NEEDS_TOP_STEREO_BUILDER.apply(reasonString);
    ASTCDClass clazz = buildClassWithStereotype(stereoValue);

    //Unit
    try {
      this.topDecorator.checkNeedsHandwrittenClass(false, clazz);
      fail("No Exception thrown (should fail quick)");
    } catch (MCFatalError ignored) {
    }

    //Check Logs
    List<Finding> findings = Log.getFindings();
    assertTrue("Log messages empty", findings.size() > 0);
    Finding lastMsg = findings.get(findings.size() - 1);
    assertTrue("Last Message was not an error", lastMsg.isError());
    assertTrue("Reason not found in last message", lastMsg.getMsg().contains(reasonString));
  }

  @Test
  public void testTopNeededHwFound() {
    //Replaces the log stub  with normal log to check for properly configured fail quick
    Log.init();

    //Setup
    String reasonString = "needStereoReason";
    ASTStereoValue stereoValue = TopDecorator.NEEDS_TOP_STEREO_BUILDER.apply(reasonString);
    ASTCDClass clazz = buildClassWithStereotype(stereoValue);

    //Unit
    this.topDecorator.checkNeedsHandwrittenClass(true, clazz);

    //CheckLogs
    List<Finding> findings = Log.getFindings();
    assertFalse("Log messages not empty", findings.size() > 0);
  }


  @Test
  public void testHandWrittenClassFound() {
    MockedStatic<GeneratorEngine> engineMock = Mockito.mockStatic(GeneratorEngine.class);
    engineMock
      .when(
        () ->
          GeneratorEngine.existsHandwrittenClass(
            Mockito.any(MCPath.class), Mockito.any(String.class)))
      .thenReturn(true);
    ASTCDDefinition ast = this.topDecorator.decorate(this.topCD).getCDDefinition();

    assertEquals(1, ast.getCDClassesList().size());
    ASTCDClass cdClass = ast.getCDClassesList().get(0);
    assertEquals("CTOP", cdClass.getName());
    assertDeepEquals(PUBLIC_ABSTRACT, cdClass.getModifier());

    assertEquals(1, cdClass.getCDConstructorList().size());
    ASTCDConstructor constructor = cdClass.getCDConstructorList().get(0);
    assertEquals("CTOP", constructor.getName());
    assertDeepEquals(PROTECTED, constructor.getModifier());

    assertEquals(1, ast.getCDInterfacesList().size());
    ASTCDInterface cdInterface = ast.getCDInterfacesList().get(0);
    assertEquals("ITOP", cdInterface.getName());
    assertDeepEquals(PUBLIC, cdInterface.getModifier());

    assertEquals(1, ast.getCDEnumsList().size());
    ASTCDEnum cdEnum = ast.getCDEnumsList().get(0);
    assertEquals("ETOP", cdEnum.getName());
    assertDeepEquals(PUBLIC, cdEnum.getModifier());
    engineMock.close();
  }

  @Test
  public void testHandWrittenClassInLocalPackageFound() {
    MockedStatic<GeneratorEngine> engineMock = Mockito.mockStatic(GeneratorEngine.class);
    engineMock
      .when(
        () ->
          GeneratorEngine.existsHandwrittenClass(
            Mockito.any(MCPath.class), Mockito.any(String.class)))
      .thenReturn(true);
    this.topDecorator.decorate(this.topCD);
    ASTCDDefinition ast = topCD.getCDDefinition();

    assertEquals(1, ast.getCDClassesList().size());
    ASTCDClass cdClass = ast.getCDClassesList().get(0);
    assertEquals("CTOP", cdClass.getName());
    assertDeepEquals(PUBLIC_ABSTRACT, cdClass.getModifier());

    assertEquals(1, cdClass.getCDConstructorList().size());
    ASTCDConstructor constructor = cdClass.getCDConstructorList().get(0);
    assertEquals("CTOP", constructor.getName());
    assertDeepEquals(PROTECTED, constructor.getModifier());

    assertEquals(1, ast.getCDInterfacesList().size());
    ASTCDInterface cdInterface = ast.getCDInterfacesList().get(0);
    assertEquals("ITOP", cdInterface.getName());
    assertDeepEquals(PUBLIC, cdInterface.getModifier());

    assertEquals(1, ast.getCDEnumsList().size());
    ASTCDEnum cdEnum = ast.getCDEnumsList().get(0);
    assertEquals("ETOP", cdEnum.getName());
    assertDeepEquals(PUBLIC, cdEnum.getModifier());
    engineMock.close();
  }

  @Test
  public void testHandWrittenClassNotFound() {
    MockedStatic<GeneratorEngine> engineMock = Mockito.mockStatic(GeneratorEngine.class);
    engineMock
      .when(
        () ->
          GeneratorEngine.existsHandwrittenClass(
            Mockito.any(MCPath.class), Mockito.any(String.class)))
      .thenReturn(false);
    ASTCDDefinition ast = this.topDecorator.decorate(this.topCD).getCDDefinition();

    assertEquals(1, ast.getCDClassesList().size());
    ASTCDClass cdClass = ast.getCDClassesList().get(0);
    assertEquals("C", cdClass.getName());
    assertDeepEquals(PUBLIC, cdClass.getModifier());

    assertEquals(1, cdClass.getCDConstructorList().size());
    ASTCDConstructor constructor = cdClass.getCDConstructorList().get(0);
    assertEquals("C", constructor.getName());
    assertDeepEquals(PROTECTED, constructor.getModifier());

    assertEquals(1, ast.getCDInterfacesList().size());
    ASTCDInterface cdInterface = ast.getCDInterfacesList().get(0);
    assertEquals("I", cdInterface.getName());
    assertDeepEquals(PUBLIC, cdInterface.getModifier());

    assertEquals(1, ast.getCDEnumsList().size());
    ASTCDEnum cdEnum = ast.getCDEnumsList().get(0);
    assertEquals("E", cdEnum.getName());
    assertDeepEquals(PUBLIC, cdEnum.getModifier());
    engineMock.close();
  }

  private ASTCDClass buildClassWithStereotype(ASTStereoValue stereoValue) {
    ASTCDClass clazz = new ASTCDClass();
    clazz.setModifier(
      UMLModifierMill.modifierBuilder()
        .setStereotype(
          UMLStereotypeMill.stereotypeBuilder()
            .addValues(stereoValue)
            .build()
        ).build()
    );
    return clazz;
  }
}
