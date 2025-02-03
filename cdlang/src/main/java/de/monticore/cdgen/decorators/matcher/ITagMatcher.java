/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdgen.decorators.matcher;

import de.monticore.cdgen.MatchResult;
import de.monticore.tagging.tags._ast.ASTSimpleTag;
import de.monticore.tagging.tags._ast.ASTTag;

@FunctionalInterface
public interface ITagMatcher {
  MatchResult match(ASTTag tag);

  static ITagMatcher applyName(String name) {
    return tag -> tag instanceof ASTSimpleTag && name.equals(((ASTSimpleTag) tag).getName()) ? MatchResult.APPLY : MatchResult.DEFAULT;
  }

  static ITagMatcher ignoreName(String name) {
    return tag -> tag instanceof ASTSimpleTag && name.equals(((ASTSimpleTag) tag).getName()) ? MatchResult.IGNORE : MatchResult.DEFAULT;
  }
}
