/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdgen.gradleplugin;

import de.monticore.gradle.common.GradleLog;
import de.se_rwth.commons.logging.Log;

import java.util.Arrays;

public class CDGenToolInvoker {
  
  protected static final String CDGEN_TOOL_CLASS = "de.monticore.cdgen.CDGenTool";
  
  public static void run(String[] args) {
    GradleLog.init();
    Log.info("Starting CDGenTool: \n" + "\t  java -jar CDGenTool.jar " + Arrays.toString(args),
        CDGenToolInvoker.class.getName());
    invokeGradleMain(args);
  }
  
  public static void invokeGradleMain(String[] args) {
    try {
      Class.forName(CDGEN_TOOL_CLASS).getMethod("gradleMain", String[].class).invoke(null,
          (Object) args);
    }
    catch (ReflectiveOperationException e) {
      throw new IllegalStateException("Could not invoke " + CDGEN_TOOL_CLASS
          + ".gradleMain(String[]); is the cd4analysis generator on the cdTool configuration?", e);
    }
  }
  
}
