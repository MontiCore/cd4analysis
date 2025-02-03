/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdgen.gradleplugin;

import de.monticore.gradle.common.GradleLog;
import de.se_rwth.commons.logging.Log;

import java.util.Arrays;

public class CDGenToolInvoker {
  public static void run(String[] args) {
    GradleLog.init();
    Log.info("Starting CDGenTool: \n" +
      "\t  java -jar CDGenTool.jar " + Arrays.toString(args), CDGenToolInvoker.class.getName());
    de.monticore.cdgen.CDGenTool.gradleMain(args);
  }

}
