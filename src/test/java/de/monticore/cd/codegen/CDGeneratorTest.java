/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen;

import com.google.common.collect.Lists;
import de.monticore.cd.codegen.methods.MethodDecorator;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4code.CD4CodeTestBasis;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;

public class CDGeneratorTest extends CD4CodeTestBasis {

  private static final String MODEL_PATH = "src/test/resources/";

  private GlobalExtensionManagement glex;

  private ASTCDCompilationUnit compUnit;

  @Before
  @Override
  public void initObjects() {
    super.initObjects();
    Log.init();
    glex = new GlobalExtensionManagement();
    this.glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());
    CD4C.reset();
  }

  @Test
  public void testOutput() {
    compUnit = parse("cd/codegen/GenAuction.cd");
    GeneratorSetup generatorSetup = new GeneratorSetup();
    CD4C.init(generatorSetup);
    this.glex.bindHookPoint("ClassContent:Elements", new TemplateHookPoint("de.monticore.cd.codegen.AuctionElements"));

    generatorSetup.setGlex(glex);
    generatorSetup.setOutputDirectory(new File("target/generated"));
    CDGenerator generator = new CDGenerator(generatorSetup);
    generator.generate(compUnit);
  }

  @Test
  public void testOutput_WithMethods() {
    compUnit = parse("cd/codegen/GenAuction.cd");
    GeneratorSetup generatorSetup = new GeneratorSetup();
    CD4C.init(generatorSetup);
    this.glex.bindHookPoint("ClassContent:Elements", new TemplateHookPoint("de.monticore.cd.codegen.AuctionElements"));

    generatorSetup.setGlex(glex);
    generatorSetup.setOutputDirectory(new File("target/generated/methods"));
    MethodDecorator decorator = new MethodDecorator(glex);
    for (ASTCDClass clazz: compUnit.getCDDefinition().getCDClassesList()) {
      List<ASTCDMethod> methods = Lists.newArrayList();
      clazz.getCDAttributeList().forEach(a -> methods.addAll(decorator.decorate(a)));
      clazz.addAllCDMembers(methods);
    }
    CDGenerator generator = new CDGenerator(generatorSetup);
    generator.generate(compUnit);
  }

  @Test
  public void testOutput_WithAssocsAndMethods() {
    compUnit = parse("cd/codegen/GenAuction.cd");
    prepareST(compUnit);

    GeneratorSetup generatorSetup = new GeneratorSetup();
    CD4C.init(generatorSetup);
    this.glex.bindHookPoint("ClassContent:Elements", new TemplateHookPoint("de.monticore.cd.codegen.AuctionElements"));

    generatorSetup.setGlex(glex);
    generatorSetup.setOutputDirectory(new File("target/generated/assocsandmethods"));
    MethodDecorator decorator = new MethodDecorator(glex);
    for (ASTCDClass clazz: compUnit.getCDDefinition().getCDClassesList()) {
      List<ASTCDMethod> methods = Lists.newArrayList();
      clazz.getCDAttributeList().forEach(a -> methods.addAll(decorator.decorate(a)));
      clazz.addAllCDMembers(methods);
    }
    CDGenerator generator = new CDGenerator(generatorSetup);
    generator.generate(compUnit);
  }

  @Test
  public void testEnum() {
    compUnit = parse("cd/codegen/GenAuction_WithEnum.cd");
    GeneratorSetup generatorSetup = new GeneratorSetup();
    CD4C.init(generatorSetup);

    generatorSetup.setGlex(glex);
    generatorSetup.setOutputDirectory(new File("target/generated/auction_enum"));
    CDGenerator generator = new CDGenerator(generatorSetup);
    generator.generate(compUnit);
  }

}
