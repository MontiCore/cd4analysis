package de.monticore.cdmatcher.iterative.matching.method;

import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cddiff.ow2cw.CDAttributeHelper;
import de.monticore.cdmatcher.MatchingStrategy;
import de.monticore.cdmatcher.MultipleMatchingStrategy;
import de.monticore.cdmatcher.caching.CachedMatches;
import de.monticore.cdmatcher.iterative.matching.MatchMCType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.HashSet;

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
    ICD4CodeArtifactScope srcScope = CDAttributeHelper.getCD4CodeArtifactScope(srcElem.getEnclosingScope());
    ICD4CodeArtifactScope tgtScope = CDAttributeHelper.getCD4CodeArtifactScope(tgtElem.getEnclosingScope());
    MatchMCType mcTypeMatcher = new MatchMCType(cachedMatches, srcScope, tgtScope);
    MatchingStrategy<ASTCDParameter> parameterMatcher = new MatchCDParameter(cachedMatches, parameterNameMatcher);

    double nameScore = nameMatcher.getScore(srcElem, tgtElem);
    double returnScore = 0.0;

    if(!srcElem.getMCReturnType().isPresentMCType() && !tgtElem.getMCReturnType().isPresentMCType()) {
      // both void
      returnScore = 1.0;
    } else if (srcElem.getMCReturnType().isPresentMCType() && tgtElem.getMCReturnType().isPresentMCType()) {
      ASTMCType srcReturnType = srcElem.getMCReturnType().getMCType();
      ASTMCType tgtReturnType = tgtElem.getMCReturnType().getMCType();
      returnScore = mcTypeMatcher.getScore(srcReturnType, tgtReturnType);
    }

    double paramScore = getBestMatchingScore(srcElem, tgtElem,
      method -> new HashSet<>(method.getCDParameterList()),
      parameterMatcher);

    double score = mean(nameScore, returnScore, paramScore);


    cachedMatches.putMatch(srcElem, tgtElem, score);
    return score;
  }
}
