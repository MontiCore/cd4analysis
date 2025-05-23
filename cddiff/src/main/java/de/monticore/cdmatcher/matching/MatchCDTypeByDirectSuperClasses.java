package de.monticore.cdmatcher.matching;

import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cdmatcher.similarity.CDTypeSimilarity;
import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.runtime.misc.Triple;

import java.util.Set;
import java.util.stream.Collectors;

public class MatchCDTypeByDirectSuperClasses implements MatchingStrategy<ASTCDType> {


  @Override
  public double getScore(ASTCDType srcElem, ASTCDType tgtElem) {
    Set<ASTCDType> srcStrictSubTypes =
      CDDiffUtil.getAllStrictSubTypes(srcElem, srcElem.getCDDefinition());
    Set<ASTCDType> tgtStrictSubTypes =
      CDDiffUtil.getAllStrictSubTypes(tgtElem, tgtElem.getCDDefinition());
    CDTypeSimilarity similarity = new CDTypeSimilarity();

    return srcStrictSubTypes.stream()
      .flatMap(srcSuper -> tgtStrictSubTypes.stream().map(tgtSuper -> new Pair<>(srcSuper, tgtSuper)))
      .map(entry -> new Triple<>(entry.a, entry.b, similarity.computeWeight(entry.a, entry.b)))
      .collect(Collectors.toMap(
        t -> t.a,
        t -> t.c,
        Double::max
      )).values().stream().collect(Collectors.averagingDouble(Double::doubleValue));
  }

}
