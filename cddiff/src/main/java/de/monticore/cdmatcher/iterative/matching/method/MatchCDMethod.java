package de.monticore.cdmatcher.iterative.matching.method;

import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cddiff.ow2cw.CDAttributeHelper;
import de.monticore.cdmatcher.MatchingStrategy;
import de.monticore.cdmatcher.MultipleMatchingStrategy;
import de.monticore.cdmatcher.caching.CachedMatches;
import de.monticore.cdmatcher.iterative.matching.MatchMCType;
import de.monticore.cdmatcher.iterative.matching.MatchModifier;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import static com.google.common.math.DoubleMath.mean;

public class MatchCDMethod extends MultipleMatchingStrategy<ASTCDMethod, ASTCDParameter> {

  private final CachedMatches cachedMatches;
  private final MatchingStrategy<ASTCDMethod> nameMatcher;
  private final MatchingStrategy<ASTCDParameter> parameterNameMatcher;

  public MatchCDMethod(CachedMatches cachedMatches, MatchingStrategy<ASTCDMethod> nameMatcher, MatchingStrategy<ASTCDParameter> parameterNameMatcher) {
    this.cachedMatches = cachedMatches;
    this.nameMatcher = nameMatcher;
    this.parameterNameMatcher = parameterNameMatcher;
  }


  @Override
  public double getScore(ASTCDMethod srcElem, ASTCDMethod tgtElem) {
    Double cachedScore = cachedMatches.getMatch(srcElem, tgtElem);
    if (cachedScore != null) {
      return cachedScore;
    }

    ICD4CodeArtifactScope srcScope = CDAttributeHelper.getCD4CodeArtifactScope(srcElem.getEnclosingScope());
    ICD4CodeArtifactScope tgtScope = CDAttributeHelper.getCD4CodeArtifactScope(tgtElem.getEnclosingScope());
    MatchMCType mcTypeMatcher = new MatchMCType(cachedMatches, srcScope, tgtScope);
    MatchingStrategy<ASTCDParameter> parameterMatcher = new MatchCDParameter(cachedMatches, parameterNameMatcher);

    List<Double> scores = new LinkedList<>();

    scores.add(nameMatcher.getScore(srcElem, tgtElem));
    double returnScore = 0.0;

    if(!srcElem.getMCReturnType().isPresentMCType() && !tgtElem.getMCReturnType().isPresentMCType()) {
      // both void
      returnScore = 1.0;
    } else if (srcElem.getMCReturnType().isPresentMCType() && tgtElem.getMCReturnType().isPresentMCType()) {
      ASTMCType srcReturnType = srcElem.getMCReturnType().getMCType();
      ASTMCType tgtReturnType = tgtElem.getMCReturnType().getMCType();
      returnScore = mcTypeMatcher.getScore(srcReturnType, tgtReturnType);
    }

    scores.add(returnScore);

    scores.add(getBestMatchingScore(srcElem, tgtElem,
      method -> new HashSet<>(method.getCDParameterList()),
      parameterMatcher));

    if(MatchModifier.hasModifier(srcElem.getModifier()) || MatchModifier.hasModifier(tgtElem.getModifier())) {
      scores.add(new MatchModifier()
        .getScore(srcElem.getModifier(), tgtElem.getModifier()));
    }

    double score = mean(scores);


    cachedMatches.putMatch(srcElem, tgtElem, score);
    return score;
  }
}
