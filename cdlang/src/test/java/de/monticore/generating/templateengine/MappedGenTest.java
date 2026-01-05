/* (c) https://github.com/MontiCore/monticore */
package de.monticore.generating.templateengine;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.hookpoints.TemplateHookPointWithInfo;
import de.se_rwth.commons.SourcePositionBuilder;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

public class MappedGenTest {
  
  @BeforeEach
  public void before() {
    LogStub.initPlusLog();
    CD4CodeMill.init();
  }
  
  @Test
  public void doTest() {
    SourceMapData data = new SourceMapData();
    GeneratorSetup generatorSetup = new GeneratorSetup() {
      
      @Override
      public TemplateController getNewTemplateController(String templateName) {
        return new SourceMapAwareTemplateController(this, templateName, data);
      }
      
    };
    GlobalExtensionManagement glex = new SourceMapAwareGlobalExtensionManagement();
    generatorSetup.setGlex(glex);
    File output = new File("target/mappgedgentest/1");
    output.mkdirs();
    generatorSetup.setOutputDirectory(output);
    
    GeneratorEngine engine = new GeneratorEngine(generatorSetup);
    
    ASTCDClass astcdClass = CD4CodeMill.cDClassBuilder().uncheckedBuild();
    astcdClass.set_SourcePositionStart(new SourcePositionBuilder().setFileName("myFile").setColumn(
        42).build());
    
    glex.addAfterTemplate("Included:SomeHP", new TemplateHookPointWithInfo("sourcemap.HookPoint"));
    
    File outpuFile = new File(output, "out.txt");
    engine.generate("sourcemap.Main", outpuFile.toPath(), astcdClass);
    System.err.println(output.getAbsoluteFile());
  }
  
}
