/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdgen.gradleplugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.Directory;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.internal.lambdas.SerializableLambdas;
import org.gradle.api.plugins.JavaLibraryPlugin;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.SourceSet;

import java.io.IOException;
import java.util.Properties;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class CDGenGradlePlugin implements Plugin<Project> {
  public static final String CONFIG_TOOL = "cdTool";

  @Override
  public void apply(Project project) {
    project.getPluginManager().apply(JavaLibraryPlugin.class);

    // Setup cdTool dependency
    var properties = loadProperties();
    String version = properties.getProperty("version");
    var toolConfig = project.getConfigurations().maybeCreate(CONFIG_TOOL);
    toolConfig.setCanBeResolved(true);

    toolConfig.defaultDependencies(dependencies -> {
      dependencies.add(project.getDependencies().create("de.monticore.lang:cd4analysis:" + version));
    });

    project.getTasks().withType(CDGenTask.class).configureEach(t -> t.getExtraClasspathElements().from(toolConfig));

    // Set up source-Sets
    project.getExtensions().getByType(JavaPluginExtension.class).getSourceSets().all(sourceSet -> {
      var cdSrcDirSet = addSourceSetExtension(sourceSet, project);

      var task = project.getTasks().register(sourceSet.getTaskName("generate", "ClassDiagrams"), CDGenTask.class, genTask -> {
        genTask.setDescription( "Generates java code from the class diagram models in source set ${sourceSet.name}.");

        genTask.getInput().from(cdSrcDirSet.getSourceDirectories());
        genTask.getOutputDir().set(cdSrcDirSet.getDestinationDirectory());

        sourceSet.getJava().srcDir(genTask.getOutputDir());
        genTask.getHandWrittenCodeDir().setFrom(project.provider(() ->
          sourceSet.getJava().getSourceDirectories().getFiles().stream().filter(
            it -> !it.toString().startsWith(project.getLayout().getBuildDirectory().get().toString())
          ).collect(Collectors.toList())));
      });
        CDSourceDirectorySet.getCDs(sourceSet).compiledBy(task, CDGenTask::getOutputDir);
        project.getTasks().named(sourceSet.getCompileJavaTaskName()).configure(t -> t.dependsOn(task));
    });
  }

  public Properties loadProperties() {
    Properties properties = new Properties();
    try {
      properties.load(this.getClass().getClassLoader().getResourceAsStream("buildInfo.properties"));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return properties;
  }

  /**
   * Adds the "cd" extension to every source set
   */
  protected CDSourceDirectorySet addSourceSetExtension(SourceSet sourceSet, Project project) {
    SourceDirectorySet vanillaSrcDirSet = project.getObjects().sourceDirectorySet(CDSourceDirectorySet.SOURCEDIRSET_NAME, sourceSet.getName() + " class diagram source");

    CDSourceDirectorySet cdSrcDirSet = sourceSet.getExtensions().create(
      CDSourceDirectorySet.class,
      CDSourceDirectorySet.SOURCEDIRSET_NAME,
      CDSourceDirectorySet.DefaultCDSourceDirectorySet.class,
      vanillaSrcDirSet);


    // By default, output into a generated/test-${NonMainName}sources/cdgen/sourcecode directory
    String buildDir = "generated-" + (SourceSet.isMain(sourceSet) ? "" : sourceSet.getName()) + "sources/cdgen/sourcecode";

    Provider<Directory> destinationDir = project.getLayout().getBuildDirectory().dir(buildDir);
    cdSrcDirSet.getDestinationDirectory().convention(destinationDir);

    // Use the src/${sourcesetname}/${name} as an input by default
    cdSrcDirSet.srcDir(project.file("src/" + sourceSet.getName() + "/" + CDSourceDirectorySet.SOURCEDIRSET_NAME));
    // and only work on mc4 and mlc files
    cdSrcDirSet.getFilter().include("**/*.cd");


    // Casting the SrcDirSet to a FileCollection seems to be necessary due to compatibility reasons with the
    // configuration cache.
    // See https://github.com/gradle/gradle/blob/d36380f26658d5cf0bf1bfb3180b9eee6d1b65a5/subprojects/scala/src/main/java/org/gradle/api/plugins/scala/ScalaBasePlugin.java#L194
    FileCollection mcSrcSetCast = cdSrcDirSet;
    sourceSet.getResources().exclude(SerializableLambdas.spec(el -> mcSrcSetCast.contains(el.getFile())));
    sourceSet.getAllSource().source(cdSrcDirSet);

    return cdSrcDirSet;
  }

}
