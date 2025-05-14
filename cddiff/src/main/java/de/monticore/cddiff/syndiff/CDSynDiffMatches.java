package de.monticore.cddiff.syndiff;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdmatcher.*;
import org.antlr.v4.runtime.misc.MultiMap;
import org.antlr.v4.runtime.misc.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class CDSynDiffMatches {
  protected Map<ASTCDType,ASTCDType> typeMatches;
  protected Map<ASTCDAssociation,ASTCDAssociation> assocMatches;

  public CDSynDiffMatches(ASTCDCompilationUnit cd1, ASTCDCompilationUnit cd2, boolean matchStructure) {

    Set<ASTCDType> srcTypes = getTypesFromCD(cd1);
    Set<ASTCDType> tgtTypes = getTypesFromCD(cd2);

    srcTypes.forEach(type -> type.getSymbol().getCDRoleList().forEach(role -> role.getAssocSide().getEnclosingScope().getAstNode()));

    MatchingStrategy<ASTCDType> typeMatcher = new MatchCDTypesByName2Set(tgtTypes);
    Map<ASTCDType,ASTCDType> typeMatchesByName = computeMatching(srcTypes, typeMatcher);

    typeMatcher = new MatchSuperTypes2Set(new CachedMatches<>(typeMatchesByName), tgtTypes);
    MultiMap<ASTCDType,ASTCDType> typeMatches4Assocs= computeMultiMatching(srcTypes, typeMatcher);

    Set<ASTCDAssociation> srcAssocs = getAssocsFromCD(cd1);
    Set<ASTCDAssociation> tgtAssocs = getAssocsFromCD(cd2);

    if (matchStructure){

      typeMatcher = new MatchCDTypeByStructure2Set(tgtTypes);
      MultiMap<ASTCDType,ASTCDType> typeMatches = computeMultiMatching(srcTypes, typeMatcher);

      for (ASTCDType srcType : srcTypes){
        if (typeMatchesByName.containsKey(srcType)){
          typeMatches.get(srcType).add(typeMatchesByName.get(srcType));
        }
      }


      Map<Pair<ASTCDType,ASTCDType>,Double> typeSimilarityMap = computeValueMapping(typeMatches,new CDTypeSimilarity());
      this.typeMatches = computeBestMatching(typeMatches,typeSimilarityMap);

      typeMatcher = new MatchSuperTypes2Set(new CachedMultiMatches<>(typeMatches),tgtTypes);
      typeMatches4Assocs = computeMultiMatching(srcTypes, typeMatcher);

    } else {
      typeMatches = typeMatchesByName;
    }

    MatchingStrategy<ASTCDAssociation> assocMatcher = new MatchAssocsByRole2Set(new CachedMultiMatches<>(typeMatches4Assocs),cd1,cd2,tgtAssocs);
    MultiMap<ASTCDAssociation,ASTCDAssociation> assocMatches = computeMultiMatching(srcAssocs, assocMatcher);

    Map<Pair<ASTCDType,ASTCDType>,Double> typeSimilarityMap = computeValueMapping(typeMatches4Assocs,new CDTypeSimilarity());
    Map<Pair<ASTCDAssociation,ASTCDAssociation>,Double> assocSimilarityMap = computeValueMapping(assocMatches, new CDAssocSimilarity(typeSimilarityMap));
    this.assocMatches = computeBestMatching(assocMatches, assocSimilarityMap);
  }

  protected <T>Map<T, T> computeBestMatching(MultiMap<T, T> matches, Map<Pair<T,T>,Double> valueMap) {
    Map<T,T> bestMatches = new LinkedHashMap<>();
    Set<T> remainingSrcTypes = matches.keySet();
    Set<T> remainingTgtTypes = matches.values().stream().flatMap(List::stream).collect(Collectors.toSet());

    /*
     * modified selection sort always puts the highest value matches in the map
     * with the lowest amount of matches for the srcType
     */
    for (Pair<T,T> match : valueMap.keySet()){
      if (remainingSrcTypes.contains(match.a) && remainingTgtTypes.contains(match.b)){
        Pair<T,T> bestMatch = match;
        double bestScore = valueMap.get(bestMatch);
        for (Pair<T,T> altMatch : valueMap.keySet()){
          if (remainingSrcTypes.contains(altMatch.a) && remainingTgtTypes.contains(altMatch.b)){
            double score = valueMap.get(altMatch);
            if (score > bestScore || (score == bestScore && matches.get(altMatch.a).size() < matches.get(bestMatch.a).size())){
              bestMatch = altMatch;
            }
          }
        }
        bestMatches.put(bestMatch.a, bestMatch.b);
        remainingSrcTypes.remove(match.a);
        remainingTgtTypes.remove(match.b);
      }

    }

    return bestMatches;
  }

  protected <T> Map<Pair<T, T>, Double> computeValueMapping(MultiMap<T, T> matches, CDSimilarity<T> similarity) {
    Map<Pair<T,T>,Double> valueMap = new LinkedHashMap<>();

    for (T srcElem : matches.keySet()){
      for (T tgtElem : matches.get(srcElem)){
        valueMap.put(new Pair<>(srcElem,tgtElem),similarity.computeWeight(srcElem,tgtElem));
      }
    }
    return valueMap;
  }

  protected <T> Map<T,T> computeMatching(Set<T> srcSet, MatchingStrategy<T> matcher){
    Map<T,T> matching = new LinkedHashMap<>();
    for (T srcType : srcSet){
      List<T> matches = matcher.getMatchedElements(srcType);
      if (matches.size() == 1){
        matching.put(srcType, matches.get(0));
      }
    }
    return matching;
  }

  protected <T> MultiMap<T,T> computeMultiMatching(Set<T> srcSet, MatchingStrategy<T> matcher){
    MultiMap<T,T> matching = new MultiMap<>();
    for (T srcType : srcSet){
      matching.put(srcType,matcher.getMatchedElements(srcType));
    }
    return matching;
  }

  protected Set<ASTCDType> getTypesFromCD(ASTCDCompilationUnit cd){
    return cd.getCDDefinition().getCDElementList().stream().filter(e -> e instanceof ASTCDType)
      .map(e -> (ASTCDType) e).collect(Collectors.toSet());
  }

  protected Set<ASTCDAssociation> getAssocsFromCD(ASTCDCompilationUnit cd){
    return cd.getCDDefinition().getCDElementList().stream().filter(e -> e instanceof ASTCDAssociation)
      .map(e -> (ASTCDAssociation) e).collect(Collectors.toSet());
  }

  public Map<ASTCDType, ASTCDType> getTypeMatches() {
    return typeMatches;
  }

  public Map<ASTCDAssociation, ASTCDAssociation> getAssocMatches() {
    return assocMatches;
  }

}
