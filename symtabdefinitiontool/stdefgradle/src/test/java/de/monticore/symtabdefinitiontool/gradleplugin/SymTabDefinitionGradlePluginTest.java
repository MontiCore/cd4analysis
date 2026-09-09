/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symtabdefinitiontool.gradleplugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

import org.apache.commons.io.FileUtils;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.annotation.Nullable;

import static org.junit.jupiter.api.Assertions.*;

public class SymTabDefinitionGradlePluginTest {
  
  @TempDir
  File testProjectDir;
  File settingsFile;
  File propertiesFile;
  File buildFile;
  File modelDir;
  
  @BeforeEach
  public void setup() throws IOException {
    settingsFile = new File(testProjectDir, "settings.gradle");
    buildFile = new File(testProjectDir, "build.gradle");
    propertiesFile = new File(testProjectDir, "gradle.properties");
    modelDir = new File(testProjectDir, "src/main/symtabdefinition");
    modelDir.mkdirs();
  }
  
  @ParameterizedTest
  @ValueSource(strings = { "8.5", "8.7", "8.14.4", "9.5.1" })
  void testSTDef(String version) throws IOException {
    writeFile(settingsFile, "rootProject.name = 'hello-world'");
    String projVersion = loadProperties().getProperty("version");
    
    File cdlangLibs = new File("../../cdlang/target/libs");
    File cd4aJarFile = new File(cdlangLibs, "cd4analysis-" + projVersion + ".jar");
    assertTrue(cd4aJarFile.exists());
    
    File stdeftoolLibs = new File("../target/libs");
    File stdeftoolJarFile = new File(stdeftoolLibs, "cd4analysis-" + projVersion
        + "-symtabdefinitiontool.jar");
    assertTrue(stdeftoolJarFile.exists());
    
    // We have to inject the cdlang jar for this project (as it is not yet published)
    // Along with the transitive dependencies
    String buildFileContent = """
        plugins {
          id 'de.rwth.se.symtabdefinition'
        }
        repositories {
         if ("true".equals(getProperty('useLocalRepo'))) {
            mavenLocal()
         }
         maven{
          url = 'https://nexus.se.rwth-aachen.de/content/groups/public'
         }
         mavenCentral()
        }
        dependencies {
          stdefTool files('%s')
          stdefTool "de.monticore:monticore-grammar:%s"
        }""".formatted(cd4aJarFile.getAbsolutePath().replace("\\", "\\\\"), projVersion);
    writeFile(buildFile, buildFileContent);
    FileUtils.copyDirectory(new File("src/test/resources/symtabdefinition"), modelDir);
    
    BuildResult result = GradleRunner.create()
        // .withDebug(true) // add to debug
        .withPluginClasspath().withGradleVersion(version).withProjectDir(testProjectDir)
        .withArguments(withProperties("generateSymbolTables", "--info", "--stacktrace")).build();
    assertNotNull(result.task(":generateSymbolTables"),
        "'generateSymbolTables' task not found! The gradle plugin has most likely not been applied.");
    assertEquals(TaskOutcome.SUCCESS, result.task(":generateSymbolTables").getOutcome());
  }
  
  void writeFile(File destination, String content) throws IOException {
    destination.getParentFile().mkdirs();
    destination.createNewFile();
    Files.write(destination.toPath(), Collections.singleton(content));
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
      if (mavenRepo == null || mavenRepo.isEmpty()) {
        // Fallback for executing tests locally
        ret.add("-Dmaven.repo.local=" + new File(System.getProperty("user.home"),
            ".m2/repository"));
      }
    }
    return ret;
  }
  
}
