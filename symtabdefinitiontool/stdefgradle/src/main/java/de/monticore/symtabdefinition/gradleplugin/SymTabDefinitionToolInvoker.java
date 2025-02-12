// (c) https://github.com/MontiCore/monticore
package de.monticore.symtabdefinition.gradleplugin;

import de.monticore.gradle.common.GradleLog;
import de.monticore.symtabdefinition.SymTabDefinitionTool;
import de.se_rwth.commons.logging.Log;

import java.util.Arrays;

public class SymTabDefinitionToolInvoker {

  protected static final String LOG_Name =
    SymTabDefinitionToolInvoker.class.getName();

  public static void run(String[] args) {
    GradleLog.init();
    Log.info("Starting SymTabDefinitionTool:" + System.lineSeparator()
        + "\targs: " + Arrays.toString(args),
      LOG_Name
    );
    SymTabDefinitionTool.main(args);
  }
}
