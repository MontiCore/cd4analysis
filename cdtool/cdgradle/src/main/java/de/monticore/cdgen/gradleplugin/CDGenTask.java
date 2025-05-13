/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdgen.gradleplugin;

import de.monticore.gradle.common.AToolAction;
import de.monticore.gradle.common.MCAllFilesTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.FileCollection;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.OutputFiles;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Gradle Task of the {@link de.monticore.cdgen.CDGenTool} It is an all-files task, as -i A.cd -i
 * B.cd is allowed
 */
public abstract class CDGenTask extends MCAllFilesTask {
  public CDGenTask() {
    super("CDGenTask", null);
  }

  @Optional
  @Input
  abstract ListProperty<String> getOptions();

  @Optional
  @Input
  abstract Property<Boolean> getClass2MC();

  /**
   * Whether CoCos should be checked, default is true
   *
   * @return property
   */
  @Optional
  @Input
  abstract Property<Boolean> getCoCos();

  /**
   * If present, the symbol tables will be exported into this directory
   *
   * @return property
   */
  @Optional
  @OutputDirectory
  abstract DirectoryProperty getSymbolOutput();

  @Override
  protected List<String> createArgList(Function<Path, String> handlePath) {
    var list = super.createArgList(handlePath);
    if (getOptions().isPresent() && !getOptions().get().isEmpty()) {
      list.add("-cliconfig");
      list.addAll(getOptions().get());
    }
    if (getClass2MC().isPresent() && getClass2MC().get()) {
      list.add("--class2mc");
    }
    if (getCoCos().getOrElse(true)) {
      list.add("--checkcococs");
    }
    if (getSymbolOutput().isPresent()) {
      list.add("-s");
      list.add(getSymbolOutput().get().getAsFile().getAbsolutePath());
    }
    return list;
  }

  @Override
  public void startGeneration(List<String> args, String progressName) {
    // Do not run for no inputs
    if (this.getInput().getAsFileTree().getFiles().isEmpty()) return;
    super.startGeneration(args, progressName);
  }

  @Override
  protected Class<? extends AToolAction> getToolAction() {
    return CDGenAction.class;
  }

  @Override
  protected Consumer<String[]> getRunMethod() {
    return CDGenToolInvoker::run;
  }

  /**
   * @return a lazy, but live {@link FileCollection} of the exported decorated symbol tables
   */
  @OutputFiles
  public FileCollection getDecoratedSymbolFiles() {
    return this.getSymbolOutput().getAsFileTree().filter(f -> f.getName().endsWith(".deccdsym"));
  }

  /**
   * @return a lazy, but live {@link FileCollection} of the exported original symbol tables
   */
  @OutputFiles
  public FileCollection getOriginalSymbolFiles() {
    return this.getSymbolOutput().getAsFileTree().filter(f -> f.getName().endsWith(".cdsym"));
  }
}
