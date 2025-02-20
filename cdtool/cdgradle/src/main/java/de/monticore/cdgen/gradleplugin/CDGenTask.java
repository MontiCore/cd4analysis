/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdgen.gradleplugin;

import de.monticore.gradle.common.AToolAction;
import de.monticore.gradle.common.MCSingleFileTask;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class CDGenTask extends MCSingleFileTask {
  public CDGenTask() {
    super("CDGenTask", null);
  }

  @Optional
  @Input
  abstract ListProperty<String> getOptions();

  @Optional
  @Input
  abstract Property<Boolean> getClass2MC();

  @Override
  protected List<String> createArgList(Path filePath, Function<Path, String> handlePath) {
    var list = super.createArgList(filePath, handlePath);
    if (getOptions().isPresent() && !getOptions().get().isEmpty()) {
      list.add("-cliconfig");
      list.addAll(getOptions().get());
    }
    if (getClass2MC().isPresent() && getClass2MC().get()) {
      list.add("--class2mc");
    }
    return list;
  }

  @Override
  protected Class<? extends AToolAction> getToolAction() {
    return CDGenAction.class;
  }

  @Override
  protected Consumer<String[]> getRunMethod() {
    return CDGenToolInvoker::run;
  }
}
