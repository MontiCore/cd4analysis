/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdgen.gradleplugin;

import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.internal.file.DefaultSourceDirectorySet;
import org.gradle.api.tasks.SourceSet;

import javax.annotation.Nonnull;

/**
 * A set of source files
 */
public interface CDSourceDirectorySet extends SourceDirectorySet {

  /**
   * Constant of where this SourceDirectorySet can be found (similar to java or resources)
   */
  final String SOURCEDIRSET_NAME = "cds";

  static CDSourceDirectorySet getCDs(@Nonnull SourceSet sourceSet) {
    return sourceSet.getExtensions().getByType(CDSourceDirectorySet.class);
  }

  // Default implementation class
  class DefaultCDSourceDirectorySet extends DefaultSourceDirectorySet implements CDSourceDirectorySet {
    public DefaultCDSourceDirectorySet(SourceDirectorySet sourceSet) {
      super(sourceSet);
    }
  }
}
