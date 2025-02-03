/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdgen.decorators.matcher;

import de.monticore.cdgen.MatchResult;
import de.monticore.umlstereotype._ast.ASTStereoValue;

@FunctionalInterface
public interface IStereoMatcher {
  MatchResult match(ASTStereoValue value);

  static IStereoMatcher applyName(String name) {
    return value -> name.equals(value.getName()) ? MatchResult.APPLY : MatchResult.DEFAULT;
  }

  static IStereoMatcher ignoreName(String name) {
    return value -> name.equals(value.getName()) ? MatchResult.IGNORE : MatchResult.DEFAULT;
  }
}
