/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdgen.gradleplugin;

import java.util.stream.Collectors;

import de.monticore.gradle.queue.CachedQueueServicePlugin;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.Directory;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.internal.lambdas.SerializableLambdas;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.SourceSet;

import javax.inject.Inject;

@SuppressWarnings("unused")
public class CDGenGradlePlugin implements Plugin<Project> {
  
  private final ObjectFactory objectFactory;
  
  @Inject
  public CDGenGradlePlugin(ObjectFactory objectFactory) {
    this.objectFactory = objectFactory;
  }
  
  @Override
  public void apply(Project project) {
    // Set up the improved work-queue
    project.getPluginManager().apply(CachedQueueServicePlugin.class);
    
    // Populate the "cdTool" configuration with the generator itself and
    // Populate the "cdToolTargetRuntime" configuration with the runtime
    project.getPluginManager().apply(CDGenGradleDependenciesPlugin.class);
    
    // Set up source-(directory)-Sets
    project.getExtensions().getByType(JavaPluginExtension.class).getSourceSets().all(sourceSet -> {
      var cdSrcDirSet = addSourceSetExtension(sourceSet, project);
      
      var task = project.getTasks().register(sourceSet.getTaskName("generate", "ClassDiagrams"),
          CDGenTask.class, genTask -> {
            genTask.setDescription(
                "Generates java code from the class diagram models in source set ${sourceSet.name}.");
            
            genTask.getInput().from(cdSrcDirSet.getSourceDirectories());
            genTask.getOutputDir().set(cdSrcDirSet.getDestinationDirectory());
            genTask.getOriginalSymbolOutput().set(project.getLayout().getBuildDirectory().dir(
                "cdgensymbols/" + sourceSet.getName() + "/original"));
            genTask.getDecoratedSymbolOutput().set(project.getLayout().getBuildDirectory().dir(
                "cdgensymbols/" + sourceSet.getName() + "/decorated"));
            
            sourceSet.getJava().srcDir(genTask.getOutputDir());
            genTask.getHandWrittenCodeDir().setFrom(project.provider(() -> sourceSet.getJava()
                .getSourceDirectories().getFiles().stream().filter(it -> !it.toString().startsWith(
                    project.getLayout().getBuildDirectory().get().toString())).collect(Collectors
                        .toList())));
          });
      CDSourceDirectorySet.getCDs(sourceSet).compiledBy(task, CDGenTask::getOutputDir);
      project.getTasks().named(sourceSet.getCompileJavaTaskName()).configure(t -> t.dependsOn(
          task));
    });
  }
  
  /** Adds the "cd" extension to every source set */
  protected CDSourceDirectorySet addSourceSetExtension(SourceSet sourceSet, Project project) {
    SourceDirectorySet vanillaSrcDirSet = project.getObjects().sourceDirectorySet(
        CDSourceDirectorySet.SOURCEDIRSET_NAME, sourceSet.getName() + " class diagram source");
    
    CDSourceDirectorySet cdSrcDirSet = objectFactory.newInstance(
        CDSourceDirectorySet.DefaultCDSourceDirectorySet.class, vanillaSrcDirSet);
    
    sourceSet.getExtensions().add(CDSourceDirectorySet.class,
        CDSourceDirectorySet.SOURCEDIRSET_NAME, cdSrcDirSet);
    
    // By default, output into a generated/test-${NonMainName}sources/cdgen/sourcecode directory
    String buildDir = "generated-" + (SourceSet.isMain(sourceSet) ? "" : sourceSet.getName())
        + "sources/cdgen/sourcecode";
    
    Provider<Directory> destinationDir = project.getLayout().getBuildDirectory().dir(buildDir);
    cdSrcDirSet.getDestinationDirectory().convention(destinationDir);
    
    // Use the src/${sourcesetname}/${name} as an input by default
    cdSrcDirSet.srcDir(project.file("src/" + sourceSet.getName() + "/"
        + CDSourceDirectorySet.SOURCEDIRSET_NAME));
    // and only work on mc4 and mlc files
    cdSrcDirSet.getFilter().include("**/*.cd");
    
    // Casting the SrcDirSet to a FileCollection seems to be necessary due to compatibility reasons
    // with the configuration cache.
    // See
    // https://github.com/gradle/gradle/blob/d36380f26658d5cf0bf1bfb3180b9eee6d1b65a5/subprojects/scala/src/main/java/org/gradle/api/plugins/scala/ScalaBasePlugin.java#L194
    FileCollection mcSrcSetCast = cdSrcDirSet;
    sourceSet.getResources().exclude(SerializableLambdas.spec(el -> mcSrcSetCast.contains(el
        .getFile())));
    sourceSet.getAllSource().source(cdSrcDirSet);
    
    return cdSrcDirSet;
  }
  
}
