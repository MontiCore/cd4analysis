/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdgen;

import static org.junit.jupiter.api.Assertions.*;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.se_rwth.commons.logging.LogStub;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import javax.annotation.Nullable;

import org.apache.commons.io.FileUtils;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class CDGenGradlePluginTest {
  
  @TempDir
  File testProjectDir;
  
  @Test
  public void testCDGen_v8_4() throws IOException {
    this.testCDGen("8.4");
  }
  
  @Test
  public void testCDGen_v8_7() throws IOException {
    this.testCDGen("8.7");
  }
  
  @Test
  public void testCDGen_v8_14() throws IOException {
    this.testCDGen("8.14");
  }
  
  void testCDGen(String version) throws IOException {
    FileUtils.copyDirectory(new File("src/test/resources/cdgradle-it"), testProjectDir);
    
    BuildResult result = GradleRunner.create().withPluginClasspath().withGradleVersion(version)
        .withProjectDir(testProjectDir).withArguments(withProperties("build", "--info",
            "--stacktrace")).build();
    assertEquals(TaskOutcome.SUCCESS, result.task(":generateClassDiagrams").getOutcome());
    assertEquals(TaskOutcome.SUCCESS, result.task(":compileJava").getOutcome());
    
    File origSymbolsOut = new File(testProjectDir, "build/cdgensymbols/main/original/MyCD.cdsym");
    assertTrue(origSymbolsOut.exists(), "Exported original symbols missing");
    
    File symbolsOut = new File(testProjectDir, "build/cdgensymbols/main/original/MyCD.cdsym");
    assertTrue(symbolsOut.exists(), "Exported decorated symbols missing");
    
    // Test if we can successfully resolve a decorated function
    LogStub.initPlusLog();
    CD4CodeMill.init();
    BasicSymbolsMill.initializePrimitives();
    BasicSymbolsMill.initializeString();
    // Load the (freshly generated) CD-sym
    CD4CodeMill.globalScope().getSymbolPath().addEntry(symbolsOut.getParentFile().toPath());
    CD4CodeMill.globalScope().loadDiagram("MyCD");
    
    CD4CodeMill.globalScope().resolveMethod("MyCD.MyCD.CanBeObserved.getName");
    // Check for a method within a class, which is TOPed
    // We explicitly expect the method to be resolvable via IncompleteA
    CD4CodeMill.globalScope().resolveMethod("MyCD.MyCD.IncompleteA.getName");
    CD4CodeMill.globalScope().resolveType("MyCD.MyCD.BBuilder");
    
    Assertions.assertEquals(0, LogStub.getFindingsCount());
    
    CD4CodeMill.reset();
  }
  
  @Test
  public void testCDGenOwnDecorator_v8_4() throws IOException {
    this.testCDGenOwnDecorator("8.4");
  }
  
  @Test
  public void testCDGenOwnDecorator_v8_7() throws IOException {
    this.testCDGenOwnDecorator("8.7");
  }
  
  @Test
  public void testCDGenOwnDecorator_v8_14() throws IOException {
    this.testCDGenOwnDecorator("8.14");
  }
  
  /**
   * Test the CDGenPlugin with a decorator written in a custom sourceSet and a custom config
   * template
   *
   * @param version gradle version
   * @throws IOException in case of errors
   */
  void testCDGenOwnDecorator(String version) throws IOException {
    FileUtils.copyDirectory(new File("src/test/resources/cdgradle-it"), testProjectDir);
    
    BuildResult result = GradleRunner.create().withPluginClasspath().withGradleVersion(version)
        .withProjectDir(testProjectDir).withArguments(withProperties("build", "--info",
            "--stacktrace", "-PwithCustomDec=true" // with custom decorator
        )).build();
    assertEquals(TaskOutcome.SUCCESS, result.task(":generateClassDiagrams").getOutcome());
    assertEquals(TaskOutcome.SUCCESS, result.task(":compileJava").getOutcome());
    
    if (!result.getOutput().contains("I am decorating")) {
      System.err.println(result.getOutput());
      fail("Failed to find \"I am decorating\" in output");
    }
  }
  
  Properties loadProperties() {
    Properties properties = new Properties();
    try {
      properties.load(this.getClass().getClassLoader().getResourceAsStream("buildInfo.properties"));
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
    return properties;
  }
  
  List<String> withProperties(String... args) {
    return withProperties(Arrays.asList(args));
  }
  
  List<String> withProperties(List<String> runnerArgs) {
    List<String> ret = new ArrayList<>(runnerArgs);
    @Nullable
    String mavenRepo = System.getProperty("maven.repo.local");
    if (mavenRepo != null && !mavenRepo.isEmpty()) {
      ret.add("-Dmaven.repo.local=" + mavenRepo);
    }
    @Nullable
    String useLocalRepo = System.getProperty("useLocalRepo");
    if (useLocalRepo != null && !useLocalRepo.isEmpty()) {
      ret.add("-PuseLocalRepo=" + useLocalRepo);
    }
    
    File langLibs = new File("../../cdlang/target/libs");
    File runtimeLibs = new File("../../cd-runtime/target/libs");
    
    String projVersion = loadProperties().getProperty("version");
    File cd4aJarFile = new File(langLibs, "cd4analysis-" + projVersion + ".jar").getAbsoluteFile();
    File runtimeJarFile = new File(runtimeLibs, "cd4analysis-" + projVersion + "-cd-runtime.jar")
        .getAbsoluteFile();
    assertTrue(cd4aJarFile.exists());
    assertTrue(runtimeJarFile.exists());
    
    ret.add("-Pversion=" + projVersion);
    ret.add("-Pcdgen_cd4aJarFile=" + cd4aJarFile);
    ret.add("-Pcdgen_runtimeJarFile=" + runtimeJarFile);
    
    return ret;
  }
  
}
