/*
 * Copyright (c) 2014 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package cd4analysis;

import de.monticore.modelloader.ModelNameCalculator;
import de.se_rwth.commons.Names;
import mc.helper.NameHelper;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;

public class CD4AnalysisModelNameCalculator implements ModelNameCalculator {
  @Override
  public String calculateModelName(String name) {
    checkArgument(!isNullOrEmpty(name));

    // a.b.CD.MyClass => a.b.CD is model name
    if (NameHelper.isQualifiedName(name)) {
      return Names.getQualifier(name);
    }

    return name;
  }
}
