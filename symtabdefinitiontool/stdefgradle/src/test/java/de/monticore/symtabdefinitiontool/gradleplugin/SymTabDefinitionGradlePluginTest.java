// (c) https://github.com/MontiCore/monticore
package de.monticore.symtabdefinitiontool.gradleplugin;

import org.apache.commons.io.FileUtils;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Properties;

public class SymTabDefinitionGradlePluginTest {

  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();
  File testProjectDir;
  File settingsFile;
  File propertiesFile;
  File buildFile;
  File modelDir;

  @Before
  public void setup() throws IOException {
    testProjectDir = temporaryFolder.newFolder();
    settingsFile = new File(testProjectDir, "settings.gradle");
    buildFile = new File(testProjectDir, "build.gradle");
    propertiesFile = new File(testProjectDir, "gradle.properties");
    modelDir = new File(testProjectDir, "src/main/symtabdefinition");
    modelDir.mkdirs();
  }

  @Test
  public void testSTDef_v7_4_2() throws IOException {
    testSTDef("7.4.2");
  }

  @Test
  public void testSTDef_v8_0_1() throws IOException {
    this.testSTDef("8.0.1");
  }

  @Test
  public void testSTDef_v8_7() throws IOException {
    this.testSTDef("8.7");
  }

  void testSTDef(String version) throws IOException {
    writeFile(settingsFile, "rootProject.name = 'hello-world'");
    String projVersion = loadProperties().getProperty("version");

    File cdlangLibs = new File("../../cdlang/target/libs");
    File cd4aJarFile = new File(cdlangLibs, "cd4analysis-" + projVersion + ".jar");
    Assert.assertTrue(cdlangLibs.exists());

    File stdeftoolLibs = new File("../target/libs");
    File stdeftoolJarFile = new File(stdeftoolLibs, "cd4analysis-" + projVersion + "-symtabdefinitiontool.jar");
    Assert.assertTrue(stdeftoolLibs.exists());

    String buildFileContent = "plugins {\n" +
      "  id 'de.rwth.se.symtabdefinition'\n" +
      "}\n " +
      "repositories {\n" +
      " maven{ url 'https://nexus.se.rwth-aachen.de/content/groups/public' }\n" +
      " mavenCentral()\n" +
      "}\n" +
      // We have to inject the cdlang jar for this project (as it is not yet published)
      "dependencies {\n" +
      "  stdefTool files('" + cd4aJarFile.getAbsolutePath().replace("\\", "\\\\") + "')\n" +
      // Along with the transitive dependencies
      " stdefTool \"de.monticore:monticore-grammar:" + projVersion + "\"\n" +
      "}";
    writeFile(buildFile, buildFileContent);
    FileUtils.copyDirectory(
      new File("src/test/resources/symtabdefinition"),
      modelDir
    );

    BuildResult result = GradleRunner.create()
      //.withDebug(true) // add to debug
      .withPluginClasspath()
      .withGradleVersion(version)
      .withProjectDir(testProjectDir)
      .withArguments("build", "--info", "--stacktrace")
      .build();
    Assert.assertEquals(TaskOutcome.SUCCESS, result.task(":generateSymbolTables").getOutcome());
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

}
