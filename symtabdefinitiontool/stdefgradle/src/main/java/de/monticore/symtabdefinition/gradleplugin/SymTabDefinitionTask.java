// (c) https://github.com/MontiCore/monticore
package de.monticore.symtabdefinition.gradleplugin;

import de.monticore.gradle.common.AToolAction;
import de.monticore.gradle.common.MCAllFilesTask;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class SymTabDefinitionTask extends MCAllFilesTask {

  public SymTabDefinitionTask() {
    super(SymTabDefinitionTask.class.getName(), null);
  }

  @Optional
  @Input
  abstract ListProperty<String> getOptions();

  @Optional
  @Input
  abstract Property<Boolean> getClass2MC();

  @Override
  protected List<String> createArgList(Function<Path, String> handlePath) {
    // not using super.createArgList() here,
    // as that adds several options unknown to the SymTabDefinitionTool
    List<String> result = new ArrayList<>();
    if (getInput().isEmpty()) {
      result.add("-" + getInputOptionString());
      result.add(
        handlePath.apply(getProject().getProjectDir().toPath())
          + "/src/main/symtabdefinition"
      );
    }
    else {
      getInputFilesAsStream()
        .forEach(f -> {
          result.add("-" + getInputOptionString());
          result.add(handlePath.apply(f.toPath()));
        });
    }
    // import symbols
    if (getSymbolPath().getElements().isPresent() &&
      !getSymbolPath().getElements().get().isEmpty()) {
      result.add("-path");
      result.add(
        getSymbolPath().getFiles().stream()
          .map(f -> " " + handlePath.apply(f.toPath()))
          .collect(Collectors.joining())
      );
    }
    if (getClass2MC().isPresent() && getClass2MC().get()) {
      result.add("--class2mc");
    }
    // export symbols
    result.add("-s");
    result.add(handlePath.apply(getOutputDir().get().getAsFile().toPath()));

    return result;
  }

  @Override
  protected Class<? extends AToolAction> getToolAction() {
    return SymTabDefinitionAction.class;
  }

  @Override
  protected Consumer<String[]> getRunMethod() {
    return SymTabDefinitionToolInvoker::run;
  }
}
