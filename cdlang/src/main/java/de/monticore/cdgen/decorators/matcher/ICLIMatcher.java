/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdgen.decorators.matcher;

import de.monticore.cdgen.MatchResult;

import javax.annotation.Nullable;

@FunctionalInterface
public interface ICLIMatcher {
  MatchResult match(String name, @Nullable String value);

  static ICLIMatcher applyName(String name) {
    return (n,v) -> name.equals(n) ? MatchResult.APPLY : MatchResult.DEFAULT;
  }

  static ICLIMatcher ignoreName(String name) {
    return (n,v) -> name.equals(n) ? MatchResult.IGNORE : MatchResult.DEFAULT;
  }
}
