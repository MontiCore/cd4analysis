// (c) https://github.com/MontiCore/monticore
package de.monticore.symtabdefinition.gradleplugin;

import de.monticore.gradle.common.AToolAction;
import de.monticore.gradle.internal.isolation.CachedIsolation;

public abstract class SymTabDefinitionAction extends AToolAction {

  protected static final CachedIsolation.WithClassPath isolator =
    new CachedIsolation.WithClassPath();

  @Override
  protected void doRun(String[] args) {
    final String prefix = "[" + getParameters().getProgressName().get() + "] ";
    isolator.executeInClassloader(SymTabDefinitionToolInvoker.class.getName(), "run",
      args, prefix, getParameters().getExtraClasspathElements()
    );
  }

}
