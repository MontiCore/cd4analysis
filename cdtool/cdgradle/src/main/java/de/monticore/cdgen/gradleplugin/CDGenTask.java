/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdgen.gradleplugin;

import de.monticore.gradle.common.MCAllFilesTask;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import de.monticore.gradle.queue.ICachedQueueTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.*;

/**
 * Gradle Task of the {@link de.monticore.cdgen.CDGenTool} It is an all-files task, as -i A.cd -i
 * B.cd is allowed
 */
@CacheableTask
public abstract class CDGenTask extends MCAllFilesTask implements ICachedQueueTask {
  
  public CDGenTask() {
    super("CDGenTask", null);
    getMainClass().convention("de.monticore.cdgen.CDGenTool");
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
  
  /**
   * the symbol path of the target artifact (e.g., containing the cd-runtime)
   *
   * @return -path values
   */
  @Optional
  @InputFiles
  @PathSensitive(PathSensitivity.RELATIVE)
  abstract ConfigurableFileCollection getTargetSymbolPath();
  
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
    if (!getTargetSymbolPath().isEmpty()) { // model paths
      List<Path> modelPath = new ArrayList<>();
      getTargetSymbolPath().forEach(it -> modelPath.add(it.toPath()));
      list.add("-path");
      modelPath.forEach(p -> list.add(handlePath.apply(p)));
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
  protected void prepareWorkQueue() {
    // Use the improved shared-isolated-work-queue of se-commons
    this.workQueue = doGetSharedQueueService().newWorkQueue(getWorkerExecutor(),
        getExtraClasspathElements());
  }
  
}
