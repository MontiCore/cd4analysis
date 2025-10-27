/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmatcher.iterative.matching.attribute;

import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cddiff.ow2cw.CDAttributeHelper;
import de.monticore.cdmatcher.MatchingStrategy;
import de.monticore.cdmatcher.caching.CachedMatches;
import de.monticore.cdmatcher.iterative.matching.MatchMCType;

import static com.google.common.math.DoubleMath.mean;

public class MatchCDAttributeByNameAndType implements MatchingStrategy<ASTCDAttribute> {

  private final CachedMatches cachedMatches;
  private final MatchingStrategy<ASTCDAttribute> nameMatcher;


  public MatchCDAttributeByNameAndType(CachedMatches cachedMatches, MatchingStrategy<ASTCDAttribute> nameMatcher) {
    this.cachedMatches = cachedMatches;
    this.nameMatcher = nameMatcher;
  }

  @Override
  public double getScore(ASTCDAttribute srcElem, ASTCDAttribute tgtElem) {
    ICD4CodeArtifactScope srcScope = CDAttributeHelper.getCD4CodeArtifactScope(srcElem.getEnclosingScope());
    ICD4CodeArtifactScope tgtScope = CDAttributeHelper.getCD4CodeArtifactScope(tgtElem.getEnclosingScope());

    double typeMatching = new MatchMCType(cachedMatches, srcScope, tgtScope)
      .getScore(srcElem.getMCType(), tgtElem.getMCType());

    double score = nameMatcher.getScore(srcElem, tgtElem);

    score = mean(score, typeMatching);

    cachedMatches.putMatch(srcElem, tgtElem, score);
    return score;
  }

}
