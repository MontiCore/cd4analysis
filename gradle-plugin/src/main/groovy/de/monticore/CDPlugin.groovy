/* (c) https://github.com/MontiCore/monticore */
package de.monticore

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * This class realizes the plugin itself.
 * The plugin is only used to provide task types
 * MCTask and GroovyTask but no predefined task instances
 */
public class CDPlugin implements Plugin<Project> {

  public void apply(Project project) {
    project.ext.CDTask = de.monticore.CDTask
    project.configurations.create("model")
  }
}
