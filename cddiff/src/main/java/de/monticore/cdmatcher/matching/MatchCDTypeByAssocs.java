package de.monticore.cdmatcher.matching;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cdmatcher.similarity.CDTypeSimilarity;
import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.runtime.misc.Triple;

import java.util.Set;
import java.util.stream.Collectors;

public class MatchCDTypeByAssocs implements MatchingStrategy<ASTCDType> {

  @Override
  public double getScore(ASTCDType srcElem, ASTCDType tgtElem) {
    Set<ASTCDAssociation> srcAssocs = srcElem.getCDRoleList().stream().map(r -> r.getSymbol().getAssoc().getAssociation().getAstNode()).collect(Collectors.toSet());
    Set<ASTCDAssociation> tgtAssocs = tgtElem.getCDRoleList().stream().map(r -> r.getSymbol().getAssoc().getAssociation().getAstNode()).collect(Collectors.toSet());

    MatchingStrategy<ASTCDAssociation> matcher = new MatchCDAssoc();

    return srcAssocs.stream()
      .flatMap(srcSuper -> tgtAssocs.stream().map(tgtSuper -> new Pair<>(srcSuper, tgtSuper)))
      .map(entry -> new Triple<>(entry.a, entry.b, matcher.getScore(entry.a, entry.b)))
      .collect(Collectors.toMap(
        t -> t.a,
        t -> t.c,
        Double::max
      )).values().stream().collect(Collectors.averagingDouble(Double::doubleValue));

  }
}
