/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdgen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.se_rwth.commons.logging.LogStub;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class CDGenGradlePluginTest {
  
  private static final List<File> PLUGIN_CLASSPATH = parseClasspath("cdgen.pluginClasspath");
  
  private static final String CD4A_JAR = requiredProperty("cdgen.cd4aJar");
  
  private static final String RUNTIME_JAR = requiredProperty("cdgen.runtimeJar");
  
  private static final String VERSION = requiredProperty("cdgen.version");
  
  @TempDir
  File testProjectDir;
  
  @ParameterizedTest
  @ValueSource(strings = { "8.5", "8.7", "8.14" })
  public void testCDGen(String gradleVersion) throws IOException {
    FileUtils.copyDirectory(new File("src/test/resources/testProject"), testProjectDir);
    
    BuildResult result = runner(gradleVersion).withArguments(withProperties("build", "--info",
        "--stacktrace")).build();
    assertEquals(TaskOutcome.SUCCESS, result.task(":generateClassDiagrams").getOutcome());
    assertEquals(TaskOutcome.SUCCESS, result.task(":compileJava").getOutcome());
    
    String log = result.getOutput().replace('\\', '/');
    assertTrue(log.contains("/cd-runtime/target/libs/") && log.contains("-cd-runtime.jar"),
        "cdToolTargetRuntime was not the local :cd-runtime build");
    assertFalse(log.matches("(?s).*modules-2/files-[^\\s\"]*/de\\.monticore\\.lang/cd4analysis/.*"),
        "a Nexus de.monticore.lang:cd4analysis leaked onto the generator classpath");
    
    File symbolsOut = new File(testProjectDir, "build/cdgensymbols/main/original/MyCD.cdsym");
    assertTrue(symbolsOut.exists(), "Exported original symbols missing");
    
    // The generated symbols must be resolvable
    LogStub.initPlusLog();
    CD4CodeMill.init();
    BasicSymbolsMill.initializePrimitives();
    BasicSymbolsMill.initializeString();
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
  
  /** The plugin with a decorator in a custom source set and a custom config template. */
  @ParameterizedTest
  @ValueSource(strings = { "8.5", "8.7", "8.14" })
  public void testCDGenOwnDecorator(String gradleVersion) throws IOException {
    FileUtils.copyDirectory(new File("src/test/resources/testProject"), testProjectDir);
    
    BuildResult result = runner(gradleVersion).withArguments(withProperties("build", "--info",
        "--stacktrace", "-PwithCustomDec=true")).build();
    assertEquals(TaskOutcome.SUCCESS, result.task(":generateClassDiagrams").getOutcome());
    assertEquals(TaskOutcome.SUCCESS, result.task(":compileJava").getOutcome());
    
    if (!result.getOutput().contains("I am decorating")) {
      System.err.println(result.getOutput());
      fail("Failed to find \"I am decorating\" in output");
    }
  }
  
  private GradleRunner runner(String gradleVersion) {
    return GradleRunner.create().withPluginClasspath(PLUGIN_CLASSPATH).withGradleVersion(
        gradleVersion).withProjectDir(testProjectDir);
  }
  
  private List<String> withProperties(String... args) {
    List<String> ret = new ArrayList<>(Arrays.asList(args));
    
    String mavenRepo = System.getProperty("maven.repo.local");
    if (mavenRepo != null && !mavenRepo.isEmpty()) {
      ret.add("-Dmaven.repo.local=" + mavenRepo);
    }
    String useLocalRepo = System.getProperty("useLocalRepo");
    if (useLocalRepo != null && !useLocalRepo.isEmpty()) {
      ret.add("-PuseLocalRepo=" + useLocalRepo);
    }
    
    ret.add("-Pversion=" + VERSION);
    ret.add("-Pcdgen_cd4aJarFile=" + CD4A_JAR);
    ret.add("-Pcdgen_runtimeJarFile=" + RUNTIME_JAR);
    return ret;
  }
  
  private static List<File> parseClasspath(String propertyKey) {
    return Arrays.stream(requiredProperty(propertyKey).split(File.pathSeparator)).map(File::new)
        .collect(Collectors.toList());
  }
  
  private static String requiredProperty(String key) {
    String value = System.getProperty(key);
    if (value == null || value.isEmpty()) {
      throw new IllegalStateException("system property '" + key
          + "' is not set -- run this via the :cdgradle-it:test task");
    }
    return value;
  }
  
}
