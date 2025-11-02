package de.monticore.cdmatcher.iterative.matching.method;

import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cddiff.ow2cw.CDAttributeHelper;
import de.monticore.cdmatcher.MatchingStrategy;
import de.monticore.cdmatcher.caching.CachedMatches;
import de.monticore.cdmatcher.iterative.matching.MatchMCType;

import static com.google.common.math.DoubleMath.mean;

public class MatchCDParameter implements MatchingStrategy<ASTCDParameter> {

  private final CachedMatches cachedMatches;
  private final MatchingStrategy<ASTCDParameter> nameMatcher;

  public MatchCDParameter(CachedMatches cachedMatches, MatchingStrategy<ASTCDParameter> nameMatcher) {
    this.cachedMatches = cachedMatches;
    this.nameMatcher = nameMatcher;
  }


  @Override
  public double getScore(ASTCDParameter srcElem, ASTCDParameter tgtElem) {
    ICD4CodeArtifactScope srcScope = CDAttributeHelper.getCD4CodeArtifactScope(srcElem.getEnclosingScope());
    ICD4CodeArtifactScope tgtScope = CDAttributeHelper.getCD4CodeArtifactScope(tgtElem.getEnclosingScope());
    MatchMCType mcTypeMatcher = new MatchMCType(cachedMatches, srcScope, tgtScope);

    double nameScore = nameMatcher.getScore(srcElem, tgtElem);
    double typeScore = mcTypeMatcher.getScore(srcElem.getMCType(), tgtElem.getMCType());

    return mean(nameScore, typeScore);
  }
}
