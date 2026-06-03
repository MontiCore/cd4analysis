/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdgen.gradleplugin;

import de.monticore.gradle.common.AToolAction;
import de.monticore.gradle.common.MCAllFilesTask;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;

/**
 * Gradle Task of the {@link de.monticore.cdgen.CDGenTool} It is an all-files task, as -i A.cd -i
 * B.cd is allowed
 */
@CacheableTask
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
   * If present, the original symbol tables will be exported into this directory
   *
   * @return property
   */
  @Optional
  @OutputDirectory
  abstract DirectoryProperty getOriginalSymbolOutput();
  
  /**
   * If present, the decorated symbol tables will be exported into this directory
   *
   * @return property
   */
  @Optional
  @OutputDirectory
  abstract DirectoryProperty getDecoratedSymbolOutput();
  
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
    // We have to use two separate directories for the symbol outputs
    // to avoid: 0xA1294 The following entries for the file `MyCD\..*sym` are ambiguous
    if (getOriginalSymbolOutput().isPresent()) {
      list.add("-s");
      list.add(getOriginalSymbolOutput().get().getAsFile().getAbsolutePath());
    }
    if (getDecoratedSymbolOutput().isPresent()) {
      list.add("-sd");
      list.add(getDecoratedSymbolOutput().get().getAsFile().getAbsolutePath());
    }
    return list;
  }
  
  @Override
  public void startGeneration(List<String> args, String progressName) {
    // Do not run for no inputs
    if (this.getInput().getAsFileTree().getFiles().isEmpty())
      return;
    super.startGeneration(args, progressName);
  }
  
  @Override
  protected Class<? extends AToolAction> getToolAction() { return CDGenAction.class; }
  
  @Override
  protected Consumer<String[]> getRunMethod() { return CDGenToolInvoker::run; }
  
}
