/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdgen;

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

public class CDGenGradlePluginTest {
  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();
  File testProjectDir;
  File settingsFile;
  File propertiesFile;
  File buildFile;
  File cdsDir;

  File resourceMainDir;

  @Before
  public void setup() throws IOException {
    testProjectDir = temporaryFolder.newFolder();
    settingsFile = new File(testProjectDir, "settings.gradle");
    buildFile = new File(testProjectDir, "build.gradle");
    propertiesFile = new File(testProjectDir, "gradle.properties");
    cdsDir = new File(testProjectDir, "src/main/cds");
    cdsDir.mkdirs();
    resourceMainDir = new File(testProjectDir, "src/main/resources");
    resourceMainDir.mkdirs();
  }

  @Test
  public void testCDGen_v7_4_2() throws IOException {
    testCDGen("7.4.2");
  }

  @Test
  public void testCDGen_v8_0_1() throws IOException {
    this.testCDGen("8.0.1");
  }

  @Test
  public void testCDGen_v8_7() throws IOException {
    this.testCDGen("8.7");
  }

  void testCDGen(String version) throws IOException {
    writeFile(settingsFile, "rootProject.name = 'hello-world'");
    File libs = new File("../../cdlang/target/libs");

    String projVersion = loadProperties().getProperty("version");
    File cd4aJarFile = new File(libs, "cd4analysis-" + projVersion + ".jar");

    Assert.assertTrue(libs.exists());
    String buildFileContent = "plugins {" +
      "    id 'de.rwth.se.cdgen' " +
      "}\n " +
      "repositories {\n" +
      " maven{ url  'https://nexus.se.rwth-aachen.de/content/groups/public' }\n" +
      " mavenCentral()\n" +
      "}\n" +
      // We have to inject the cdlang jar for this project (as it is not yet published)
      "dependencies {\n" +
      " cdTool files('" + cd4aJarFile.getAbsolutePath().replace("\\", "\\\\") + "')\n" +
      // Along with the transitive dependencies
      " cdTool \"de.monticore:monticore-grammar:" + projVersion + "\" \n " +
      "}";
    writeFile(buildFile, buildFileContent);
    Files.copy(new File("src/test/resources/MyCD.cd").toPath(), new File(cdsDir, "MyCD.cd").toPath());

    BuildResult result = GradleRunner.create()
      .withPluginClasspath()
      .withGradleVersion(version)
      .withProjectDir(testProjectDir)
      .withArguments("build", "--info", "--stacktrace")
      .build();
    Assert.assertEquals(TaskOutcome.SUCCESS, result.task(":generateClassDiagrams").getOutcome());
    Assert.assertEquals(TaskOutcome.SUCCESS, result.task(":compileJava").getOutcome());
  }

  @Test
  public void testCDGenOwnDecorator_v7_4_2() throws IOException {
    this.testCDGenOwnDecorator("7.4.2");
  }

  @Test
  public void testCDGenOwnDecorator_v8_0_1() throws IOException {
    this.testCDGenOwnDecorator("8.0.1");
  }

  @Test
  public void testCDGenOwnDecorator_v8_7() throws IOException {
    this.testCDGenOwnDecorator("8.7");
  }

  /**
   *
   * @param version
   * @throws IOException
   */
  void testCDGenOwnDecorator(String version) throws IOException {
    writeFile(settingsFile, "rootProject.name = 'hello-world'");
    File libs = new File("../../cdlang/target/libs");

    String projVersion = loadProperties().getProperty("version");
    File cd4aJarFile = new File(libs, "cd4analysis-" + projVersion + ".jar");

    Assert.assertTrue(libs.exists());
    String buildFileContent = "plugins {" +
      "    id 'de.rwth.se.cdgen' " +
      "}\n " +
      "repositories {\n" +
      " maven{ url  'https://nexus.se.rwth-aachen.de/content/groups/public' }\n" +
      " mavenCentral()\n" +
      "}\n" +
      // Define a sourceset in which we write our own decorator
      "sourceSets{\n" +
      "  decorators {\n" +
      "   java.srcDir('src/dec/java') \n" +
      " }" +
      "}\n" +
      // We have to inject the cdlang jar for this project (as it is not yet published)
      "dependencies {\n" +
      " cdTool files('" + cd4aJarFile.getAbsolutePath().replace("\\", "\\\\") + "')\n" +
      // Along with the transitive dependencies
      " cdTool \"de.monticore:monticore-grammar:" + projVersion + "\" \n " +
      "}\n" +
      // the decorator sourceset requires the same dependencies as cdTool
      "configurations.decoratorsImplementation.extendsFrom(configurations.cdTool)\n" +
      "generateClassDiagrams {\n" +
      "  configTemplate='CD2OwnDecorator' \n " +
      "  tmplDir=file('src/main/resources') \n " +
      "  getExtraClasspathElements().from(sourceSets.decorators.output) \n " +
      "}\n" +
      "\n";
    writeFile(buildFile, buildFileContent);
    Files.copy(new File("src/test/resources/MyCD.cd").toPath(), new File(cdsDir, "MyCD.cd").toPath());
    File srcSet = new File(testProjectDir, "src/dec/java/mc");
    srcSet.mkdirs();
    Files.copy(new File("src/test/resources/MyOwnDecorator.java").toPath(), new File(srcSet, "MyOwnDecorator.java").toPath());
    Files.copy(new File("src/test/resources/CD2OwnDecorator.ftl").toPath(), new File(resourceMainDir, "CD2OwnDecorator.ftl").toPath());

    BuildResult result = GradleRunner.create()
      .withPluginClasspath()
      .withGradleVersion(version)
      .withProjectDir(testProjectDir)
      .withArguments("build", "--info", "--stacktrace")
      .build();
    Assert.assertEquals(TaskOutcome.SUCCESS, result.task(":generateClassDiagrams").getOutcome());
    Assert.assertEquals(TaskOutcome.SUCCESS, result.task(":compileJava").getOutcome());

    if (!result.getOutput().contains("I am decorating")) {
      System.err.println(result.getOutput());
      Assert.fail("Failed to find \"I am decorating\" in output");
    }
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
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return properties;
  }

}
