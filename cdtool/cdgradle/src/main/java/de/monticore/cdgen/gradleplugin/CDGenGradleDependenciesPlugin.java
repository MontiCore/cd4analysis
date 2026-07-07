/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdgen.gradleplugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.plugins.JavaLibraryPlugin;

import java.io.IOException;
import java.util.Properties;

@SuppressWarnings("unused")
public class CDGenGradleDependenciesPlugin implements Plugin<Project> {
  
  /**
   * Configuration containing the classpath of the generator tool.
   */
  public static final String CONFIG_TOOL = "cdTool";
  
  /**
   * Configuration containing the classpath of the target.
   * it will be added to the api configuration & passed as a symbol path for class2mc
   */
  public static final String CONFIG_TARGET_RUNTIME = "cdToolTargetRuntime";
  
  @Override
  public void apply(Project project) {
    project.getPluginManager().apply(JavaLibraryPlugin.class);
    
    // Setup cdTool dependency
    var properties = loadProperties();
    String version = properties.getProperty("version");
    
    Configuration toolConfig = project.getConfigurations().maybeCreate(CONFIG_TOOL);
    toolConfig.setCanBeResolved(true);
    Configuration toolRuntimeConfig = project.getConfigurations().maybeCreate(
        CONFIG_TARGET_RUNTIME);
    toolRuntimeConfig.setCanBeResolved(true);
    
    toolConfig.defaultDependencies(dependencies -> {
      dependencies.add(project.getDependencies().create("de.monticore.lang:cd4analysis:"
          + version));
    });
    
    toolRuntimeConfig.defaultDependencies(dependencies -> {
      dependencies.add(project.getDependencies().create("de.monticore.lang:cd-runtime:" + version
          + ":cd-runtime"));
    });
    
    project.getTasks().withType(CDGenTask.class).configureEach(t -> t.getExtraClasspathElements()
        .from(toolConfig));
    
    project.getTasks().withType(CDGenTask.class).configureEach(t -> t.getTargetSymbolPath().from(
        toolRuntimeConfig));
    
    project.getConfigurations().named("api").configure(api -> api.extendsFrom(toolRuntimeConfig));
    
  }
  
  public Properties loadProperties() {
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
