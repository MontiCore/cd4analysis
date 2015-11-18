/*
 * Copyright (c) 2014 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import de.monticore.CommonModelNameCalculator;
import de.monticore.symboltable.SymbolKind;
import de.monticore.umlcd4a.symboltable.CDFieldSymbol;
import de.monticore.umlcd4a.symboltable.CDSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.Splitters;

public class CD4AnalysisModelNamerCalculator extends CommonModelNameCalculator {

  @Override
  public Set<String> calculateModelNames(final String name, final SymbolKind kind) {
    String modelName = null;

    if (CDSymbol.KIND.isKindOf(kind)) {
      // e.g., if p.CD, return unchanged
      modelName = name;
    }
    else if (CDTypeSymbol.KIND.isKindOf(kind)) {
      // e.g., if p.CD.Clazz, return p.CD
      if (!Names.getQualifier(name).isEmpty()) {
        modelName = Names.getQualifier(name);
      }
    }
    else if (CDFieldSymbol.KIND.isKindOf(kind)) {
      // e.g., if p.CD.Clazz.Field return p.CD
      List<String> nameParts = Splitters.DOT.splitToList(name);

      // at least 3, because of CD.Clazz.field
      if (nameParts.size() >= 3) {
        // cut the last two name parts (e.g., Clazz.field)
        modelName = Joiners.DOT.join(nameParts.subList(0, nameParts.size()-2));
      }

    }

    if (modelName != null) {
      return ImmutableSet.of(modelName);
    }

    return Collections.emptySet();
  }
}
