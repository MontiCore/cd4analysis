/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen;

import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code.CD4CodeTestBasis;
import de.monticore.cd4code._parser.CD4CodeParser;
import de.monticore.cd4code.trafo.CD4CodeAfterParseTrafo;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.fail;

public class CDGeneratorTest extends CD4CodeTestBasis {

  private static final String MODEL_PATH = "src/test/resources/";

  private GlobalExtensionManagement glex;

  private ASTCDCompilationUnit compUnit;

  @Before
  @Override
  public void initObjects() {
    super.initObjects();
    compUnit = parse("de.monticore.cd.codegen.GenAuction");
    glex = new GlobalExtensionManagement();
    this.glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());
    CD4C.reset();
  }

  @Test
  public void testOutput() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    CD4C.init(generatorSetup);
    this.glex.bindHookPoint("ClassContent:Elements", new TemplateHookPoint("de.monticore.cd.codegen.AuctionElements"));

    generatorSetup.setGlex(glex);
    generatorSetup.setOutputDirectory(new File("target/generated"));
    CDGenerator generator = new CDGenerator(generatorSetup);
    generator.generate(compUnit);
  }

  public ASTCDCompilationUnit parse(String name) {
    String qualifiedName = name.replace(".", "/");

    CD4CodeParser parser = CD4CodeMill.parser();
    Optional<ASTCDCompilationUnit> ast = Optional.empty();
    try {
      ast = parser.parse(MODEL_PATH + qualifiedName + ".cd");
    } catch (IOException e) {
      fail(String.format("Failed to load model '%s'", qualifiedName));
    }
    if (!ast.isPresent()) {
      fail(String.format("Failed to load model '%s'", qualifiedName));
    }

    new CD4CodeAfterParseTrafo().transform(ast.get());
    return ast.get();
  }

}
