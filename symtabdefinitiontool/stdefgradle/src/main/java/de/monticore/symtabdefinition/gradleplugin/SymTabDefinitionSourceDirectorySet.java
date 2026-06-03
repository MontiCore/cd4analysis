/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symtabdefinition.gradleplugin;

import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.internal.file.DefaultSourceDirectorySet;
import org.gradle.api.internal.tasks.TaskDependencyFactory;
import org.gradle.api.tasks.SourceSet;

import javax.inject.Inject;

public interface SymTabDefinitionSourceDirectorySet extends SourceDirectorySet {
  
  /** Where to find the models (similar to java, resources, etc.) */
  String SOURCEDIRSET_NAME = "symtabdefinition";
  
  static SymTabDefinitionSourceDirectorySet getSTDefSet(SourceSet sourceSet) {
    return sourceSet.getExtensions().getByType(SymTabDefinitionSourceDirectorySet.class);
  }
  
  // default implementation
  abstract class DefaultSymTabDefinitionSourceDirectorySet extends DefaultSourceDirectorySet
      implements SymTabDefinitionSourceDirectorySet {
    
    @Inject
    public DefaultSymTabDefinitionSourceDirectorySet(SourceDirectorySet sourceSet,
        TaskDependencyFactory taskDependencyFactory) {
      super(sourceSet, taskDependencyFactory);
    }
    
  }
  
}
