package de.monticore.cddiff.syndiff;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdmatcher.*;
import java.util.*;
import java.util.stream.Collectors;
import org.antlr.v4.runtime.misc.MultiMap;
import org.antlr.v4.runtime.misc.Triple;

/**
 * This class should be used to construct a matching of respectively types and
 * associations between the srcCD and the tgtCD for the SynDiff and Syn2SemDiff.
 * Recomputing of matches should be avoided.
 */
public class CDSynDiffMatches {
  protected Map<ASTCDType, ASTCDType> typeMatches;
  protected MultiMap<ASTCDType, ASTCDType> typeMatches4Assocs;
  protected Map<ASTCDAssociation, ASTCDAssociation> assocMatches;

  public CDSynDiffMatches(
    ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD, boolean matchStructure) {

    // Compute types of srcCD and tgtCD without using the traverser
    Set<ASTCDType> srcTypes = getAllTypesFromCD(srcCD);
    Set<ASTCDType> tgtTypes = getAllTypesFromCD(tgtCD);

    // compute a matching of types by name
    MatchingStrategy<ASTCDType> typeMatcher = new MatchCDTypesByQName2Set(tgtTypes);
    Map<ASTCDType, ASTCDType> typeMatchesByName = computeMatching(srcTypes, typeMatcher);

    /*
     * Compute a matching of types of the srcCD if their super-types match according
     * to the previous matching.
     * This is used for the association matching, when moving an association to a
     * subtype should be detected.
     * todo: Rethink this approach!
     */
    typeMatcher = new MatchSuperTypes2Set(new CachedMatches<>(typeMatchesByName), tgtTypes);
    MultiMap<ASTCDType, ASTCDType> typeMatches4Assocs = computeMultiMatching(srcTypes, typeMatcher);

    // Compute associations of srcCD and tgtCD without using the traverser
    Set<ASTCDAssociation> srcAssocs = getAllAssocsFromCD(srcCD);
    Set<ASTCDAssociation> tgtAssocs = getAllAssocsFromCD(tgtCD);

    /*
     * Types are matched according to structural similarities.
     * The previous matching is added to the new multi-matching.
     */
    if (matchStructure) {

      typeMatcher = new MatchCDTypeByStructure2Set(tgtTypes);
      MultiMap<ASTCDType, ASTCDType> typeMatches = computeMultiMatching(srcTypes, typeMatcher);

      for (ASTCDType srcType : srcTypes) {
        if (typeMatchesByName.containsKey(srcType)) {
          typeMatches.get(srcType).add(typeMatchesByName.get(srcType));
        }
      }

      // We compute a best-match to reduce the multimap to a map.
      Set<Triple<ASTCDType, ASTCDType, Double>> typeSimilaritySet =
        computeValueSet(typeMatches, new CDTypeSimilarity());
      this.typeMatches = computeBestMatching(typeMatches, typeSimilaritySet);

      this.typeMatches.forEach((src,tgt) ->  System.out.println("[BEST MATCH] "+ src.getName() + " ==> " + tgt.getName()));

      // We add the structural matching to the type-matching for associations
      typeMatcher = new MatchSuperTypes2Set(new CachedMatches<>(this.typeMatches), tgtTypes);
      typeMatches4Assocs = computeMultiMatching(srcTypes, typeMatcher);

    } else {
      typeMatches = typeMatchesByName;
    }

    //fixme: assoc matching has to be reworked entirely

    this.typeMatches4Assocs = typeMatches4Assocs;

    MatchingStrategy<ASTCDAssociation> assocMatcher =
      new MatchAssocsByRole2Set(
        new CachedMultiMatches<>(typeMatches4Assocs), srcCD, tgtCD, tgtAssocs);
    MultiMap<ASTCDAssociation, ASTCDAssociation> assocMatches =
      computeMultiMatching(srcAssocs, assocMatcher);

    // add greedy assoc matches if structure matching is active
    if (matchStructure) {
      assocMatcher = new MatchCDAssocsGreedy2Set(
        new CachedMultiMatches<>(typeMatches4Assocs), srcCD, tgtCD, tgtAssocs);
      MultiMap<ASTCDAssociation, ASTCDAssociation> greedyAssocMatches =
        computeMultiMatching(srcAssocs, assocMatcher);

      for (ASTCDAssociation srcAssoc : srcAssocs) {
        assocMatches.get(srcAssoc).addAll(greedyAssocMatches.get(srcAssoc));
      }

    }

    // Compute best-match for associations using the similarity metric for types.
    Set<Triple<ASTCDType, ASTCDType, Double>> typeSimilaritySet =
      computeValueSet(typeMatches4Assocs, new CDTypeSimilarity());
    Set<Triple<ASTCDAssociation, ASTCDAssociation, Double>> assocSimilaritySet =
      computeValueSet(assocMatches, new CDAssocSimilarity(typeSimilaritySet));
    this.assocMatches = computeBestMatching(assocMatches, assocSimilaritySet);

    this.assocMatches.forEach((src,tgt) ->  System.out.println("[BEST MATCH] "+ CD4CodeMill.prettyPrint(src,false) + " ==> " + CD4CodeMill.prettyPrint(tgt,false)));

  }

  /**
   * Helper-function that computes a best-matching given a multi-matching
   * and a set of element-element-score triples.
   */
  protected <T> Map<T, T> computeBestMatching(
    MultiMap<T, T> matches, Set<Triple<T,T,Double>> valueSet) {
    List<Triple<T,T,Double>> remainingMatches = new ArrayList<>(valueSet);
    Map<T, T> bestMatches = new LinkedHashMap<>();

    /*
     * modified selection sort always puts the highest value matches in the map
     * with the lowest amount of matches for the srcType
     */
    while (!remainingMatches.isEmpty()) {
      Triple<T, T, Double> bestMatch = remainingMatches.get(0);
      double bestScore = bestMatch.c;
      for (Triple<T, T, Double> match : valueSet) {
        if (remainingMatches.contains(match)) {
          double score = match.c;
          if (score > bestScore
            || (score == bestScore
            && matches.get(match.a).size() < matches.get(bestMatch.a).size())) {
            bestMatch = match;
            bestScore = score;
          }
        }
      }
      bestMatches.put(bestMatch.a, bestMatch.b);
      remainingMatches.remove(bestMatch);
      for (Triple<T, T, Double> match : valueSet){
        if (remainingMatches.contains(match) && (match.a.equals(bestMatch.a) || match.b.equals(bestMatch.b))) {
          remainingMatches.remove(match);
        }
      }
    }

    return bestMatches;
  }

  /**
   * Helper-function that computes the score for each matching pair of elements
   * in a multi-matching and outputs the set of element-element-score triples.
   */
  protected <T> Set<Triple<T,T,Double>> computeValueSet(
    MultiMap<T, T> matches, CDSimilarity<T> similarity) {
    Set<Triple<T,T,Double>> valueSet = new LinkedHashSet<>();

    for (T srcElem : matches.keySet()) {
      for (T tgtElem : matches.get(srcElem)) {
        valueSet.add(new Triple<>(srcElem,tgtElem,similarity.computeWeight(srcElem,tgtElem)));
      }
    }
    return valueSet;
  }

  protected <T> Map<T, T> computeMatching(Set<T> srcSet, MatchingStrategy<T> matcher) {
    Map<T, T> matching = new LinkedHashMap<>();
    for (T srcType : srcSet) {
      List<T> matches = matcher.getMatchedElements(srcType);
      if (matches.size() == 1) {
        matching.put(srcType, matches.get(0));
      }
    }
    return matching;
  }

  protected <T> MultiMap<T, T> computeMultiMatching(Set<T> srcSet, MatchingStrategy<T> matcher) {
    MultiMap<T, T> matching = new MultiMap<>();
    for (T srcType : srcSet) {
      matching.put(srcType, matcher.getMatchedElements(srcType));
    }
    return matching;
  }

  protected Set<ASTCDType> getAllTypesFromCD(ASTCDCompilationUnit cd) {
    Set<ASTCDType> types =
      cd.getCDDefinition().getCDElementList().stream()
        .filter(e -> e instanceof ASTCDType)
        .map(e -> (ASTCDType) e)
        .collect(Collectors.toSet());
    types.addAll(
      cd.getCDDefinition().getCDElementList().stream()
        .filter(e -> e instanceof ASTCDPackage)
        .flatMap(p -> getAllTypesFromPackage((ASTCDPackage) p).stream())
        .collect(Collectors.toSet()));
    return types;
  }

  protected Set<ASTCDType> getAllTypesFromPackage(ASTCDPackage astcdPackage) {
    Set<ASTCDType> types =
      astcdPackage.getCDElementList().stream()
        .filter(e -> e instanceof ASTCDType)
        .map(e -> (ASTCDType) e)
        .collect(Collectors.toSet());
    types.addAll(
      astcdPackage.getCDElementList().stream()
        .filter(e -> e instanceof ASTCDPackage)
        .flatMap(p -> getAllTypesFromPackage((ASTCDPackage) p).stream())
        .collect(Collectors.toSet()));
    return types;
  }

  protected Set<ASTCDAssociation> getAllAssocsFromCD(ASTCDCompilationUnit cd) {
    Set<ASTCDAssociation> assocs =
      cd.getCDDefinition().getCDElementList().stream()
        .filter(e -> e instanceof ASTCDAssociation)
        .map(e -> (ASTCDAssociation) e)
        .collect(Collectors.toSet());
    assocs.addAll(
      cd.getCDDefinition().getCDElementList().stream()
        .filter(e -> e instanceof ASTCDPackage)
        .flatMap(p -> getAllAssocsFromPackages((ASTCDPackage) p).stream())
        .collect(Collectors.toSet()));
    return assocs;
  }

  protected Set<ASTCDAssociation> getAllAssocsFromPackages(ASTCDPackage astcdPackage) {
    Set<ASTCDAssociation> assocs =
      astcdPackage.getCDElementList().stream()
        .filter(e -> e instanceof ASTCDAssociation)
        .map(e -> (ASTCDAssociation) e)
        .collect(Collectors.toSet());
    assocs.addAll(
      astcdPackage.getCDElementList().stream()
        .filter(e -> e instanceof ASTCDPackage)
        .flatMap(p -> getAllAssocsFromPackages((ASTCDPackage) p).stream())
        .collect(Collectors.toSet()));
    return assocs;
  }

  public Map<ASTCDType, ASTCDType> getTypeMatches() {
    return typeMatches;
  }

  public Map<ASTCDAssociation, ASTCDAssociation> getAssocMatches() {
    return assocMatches;
  }

  @Deprecated
  public MultiMap<ASTCDType, ASTCDType> getTypeMatches4Assocs() {
    return typeMatches4Assocs;
  }
}
