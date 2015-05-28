/*
 * Copyright (c) 2014 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a;

import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import de.monticore.CommonModelNameCalculator;
import de.monticore.symboltable.SymbolKind;
import de.monticore.umlcd4a.symboltable.CDFieldSymbol;
import de.monticore.umlcd4a.symboltable.CDSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

public class CD4AnalysisModelNamerCalculator extends CommonModelNameCalculator {

  @Override
  public String calculateModelName(String name, SymbolKind kind) {
    String modelName = name;

    if (CDSymbol.KIND.isSame(kind)) {
      // e.g., if p.CD, return unchanged
      modelName = name;
    }
    else if (CDTypeSymbol.KIND.isSame(kind)) {
      // e.g., if p.CD.Clazz, return p.CD
      if (!Names.getQualifier(name).isEmpty()) {
        modelName = Names.getQualifier(name);
      }
    }
    else if (CDFieldSymbol.KIND.isSame(kind)) {
      // e.g., if p.CD.Clazz.Field return p.CD
      List<String> nameParts = Splitter.on(".").splitToList(name);

      // at least 3, because of CD.Clazz.field
      if (nameParts.size() >= 3) {
        // cut the last two name parts (e.g., Clazz.field)
        modelName = Joiner.on(".").join(nameParts.subList(0, nameParts.size()-2));
      }

    }
    else {
      Log.warn("0xC4B70 Symbol kind '" + kind.getName() + "' is unknown.");
    }

    return modelName;
  }
}
