/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmatcher.iterative.matching.attribute;

import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cddiff.ow2cw.CDAttributeHelper;
import de.monticore.cdmatcher.MatchingStrategy;
import de.monticore.cdmatcher.caching.CachedMatches;
import de.monticore.cdmatcher.iterative.matching.MatchMCType;
import de.monticore.cdmatcher.iterative.matching.MatchModifier;

import java.util.LinkedList;
import java.util.List;

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
    Double cachedScore = cachedMatches.getMatch(srcElem, tgtElem);
    if (cachedScore != null) {
      return cachedScore;
    }

    ICD4CodeArtifactScope srcScope = CDAttributeHelper.getCD4CodeArtifactScope(srcElem.getEnclosingScope());
    ICD4CodeArtifactScope tgtScope = CDAttributeHelper.getCD4CodeArtifactScope(tgtElem.getEnclosingScope());

    List<Double> scores = new LinkedList<>();

    scores.add(new MatchMCType(cachedMatches, srcScope, tgtScope)
      .getScore(srcElem.getMCType(), tgtElem.getMCType()));

    scores.add(nameMatcher.getScore(srcElem, tgtElem));

    if(MatchModifier.hasModifier(srcElem.getModifier()) || MatchModifier.hasModifier(tgtElem.getModifier())) {
      scores.add(new MatchModifier()
        .getScore(srcElem.getModifier(), tgtElem.getModifier()));
    }

    double score = mean(scores);

    cachedMatches.putMatch(srcElem, tgtElem, score);
    return score;
  }

}
