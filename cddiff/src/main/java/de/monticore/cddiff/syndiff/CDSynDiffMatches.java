package de.monticore.cddiff.syndiff;

import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cddiff.ow2cw.CDAssociationHelper;
import de.monticore.cddiff.ow2cw.CDAttributeHelper;
import de.monticore.cddiff.ow2cw.CDInheritanceHelper;
import de.monticore.cdmatcher.BooleanMatchingStrategy;
import de.monticore.cdmatcher.MatchBySimilarity;
import de.monticore.cdmatcher.MatchingStrategy;
import de.monticore.cdmatcher.booleanMatching.BooleanMatchFromCache;
import de.monticore.cdmatcher.booleanMatching.MatchCDAssocsBySrcTypeAndTgtRole;
import de.monticore.cdmatcher.booleanMatching.MatchCDAssocsGreedy;
import de.monticore.cdmatcher.booleanMatching.MatchCDTypesByQName;
import de.monticore.cdmatcher.caching.CachedMatch;
import de.monticore.cdmatcher.caching.CachedMatches;
import de.monticore.cdmatcher.caching.StructureCache;
import de.monticore.cdmatcher.iterative.matching.association.MatchCDAssocByBestSuperType;
import de.monticore.cdmatcher.iterative.matching.attribute.MatchCDAttributeByNameAndType;
import de.monticore.cdmatcher.iterative.matching.cdtype.MatchCDTypeByDirectAssocs;
import de.monticore.cdmatcher.iterative.matching.cdtype.MatchCDTypeByDirectAttributes;
import de.monticore.cdmatcher.iterative.matching.cdtype.MatchCDTypeByDirectSubClasses;
import de.monticore.cdmatcher.iterative.matching.cdtype.MatchCDTypeByDirectSuperClasses;
import de.monticore.cdmatcher.iterative.matching.cdtype.MatchCDTypeComposite;
import de.monticore.cdmatcher.iterative.matching.cdtype.MatchCDTypeFromCache;
import de.monticore.cdmatcher.similarity.CDAssocEmbeddingSimilarity;
import de.monticore.cdmatcher.similarity.CDAssocSimilarity4Iterative;
import de.monticore.cdmatcher.similarity.CDAttributeEmbeddingSimilarity;
import de.monticore.cdmatcher.similarity.CDAttributeSimilarity;
import de.monticore.cdmatcher.similarity.CDTypeEmbeddingSimilarity;
import de.monticore.cdmatcher.similarity.CDTypeEmbeddingWithAttributesSimilarity;
import de.monticore.cdmatcher.similarity.CDTypeSimilarity;
import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.misc.Pair;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static de.monticore.cddiff.CDDiffUtil.getAllCDTypes;

/**
 * This class should be used to construct a matching of respectively types and associations between
 * the srcCD and the tgtCD for the SynDiff and Syn2SemDiff. Recomputing of matches should be
 * avoided.
 */
public class CDSynDiffMatches {
  private final Map<ASTCDType, ASTCDType> typeMatches;
  private CachedMatches scoredMatches = null;
  private final Map<ASTCDAssociation, ASTCDAssociation> assocMatches;
  private final Map<ASTCDAttribute, ASTCDAttribute> attributeMatches;
  private final StructureCache structureCache;

  private static final double MINIMUM_CHANGE_THRESHOLD = 0.001;

  /**
   * Uses iterative matching to compute matches of types, associations and attributes between src and tgt CD.
   *
   * @param srcCD the source class diagram
   * @param tgtCD the target class diagram
   * @param matchingIterations the maximum number of iterations to perform for matching
   * @param threshold the minimum similarity between two elements to be considered a match
   * @param useEmbedding whether to use embeddings for matching, make sure to call CDEmbeddingSimilarity.initialize(String) before using this
   */
  public CDSynDiffMatches(
      ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD, int matchingIterations, double threshold, boolean useEmbedding) {

    CachedMatches cachedMatches = new CachedMatches();
    structureCache = new StructureCache();

    setupStructureCache(srcCD, structureCache);
    setupStructureCache(tgtCD, structureCache);

    preCalculateExactMatches(srcCD, tgtCD, cachedMatches, structureCache);

    // Compute types of srcCD and tgtCD without using the traverser
    Set<ASTCDType> srcTypes = CDDiffUtil.getAllTypesFromCD(srcCD);
    Set<ASTCDType> tgtTypes = CDDiffUtil.getAllTypesFromCD(tgtCD);

    Set<MatchingStrategy<ASTCDType>> matchingStrategies;

    if(useEmbedding) {
      MatchCDTypeFromCache.setDefaultFallbackStrategy(new MatchBySimilarity<>(new CDTypeEmbeddingSimilarity()));
      matchingStrategies = new HashSet<>((Set.of(
        new MatchBySimilarity<>(new CDTypeEmbeddingWithAttributesSimilarity(structureCache)),
        new MatchCDTypeByDirectAssocs(new MatchCDAssocByBestSuperType(cachedMatches, structureCache, new MatchBySimilarity<>(new CDAssocEmbeddingSimilarity())), structureCache),
        new MatchCDTypeByDirectAttributes(structureCache, new MatchBySimilarity<>(new CDAttributeEmbeddingSimilarity())),
        new MatchCDTypeByDirectSubClasses(cachedMatches, structureCache),
        new MatchCDTypeByDirectSuperClasses(cachedMatches, structureCache)
      )));
    } else {
      MatchCDTypeFromCache.setDefaultFallbackStrategy(new MatchBySimilarity<>(new CDTypeSimilarity()));
      matchingStrategies = new HashSet<>((Set.of(
        new MatchBySimilarity<>(new CDTypeSimilarity()),
        new MatchCDTypeByDirectAssocs(new MatchCDAssocByBestSuperType(cachedMatches, structureCache, new MatchBySimilarity<>(new CDAssocSimilarity4Iterative())), structureCache),
        new MatchCDTypeByDirectAttributes(structureCache, new MatchCDAttributeByNameAndType(cachedMatches, new MatchBySimilarity<>(new CDAttributeSimilarity()))),
        new MatchCDTypeByDirectSubClasses(cachedMatches, structureCache),
        new MatchCDTypeByDirectSuperClasses(cachedMatches, structureCache)
      )));
    }

    MatchCDTypeComposite matcher = new MatchCDTypeComposite(matchingStrategies, cachedMatches);

    for(int i = 0; i < matchingIterations; i++) {
      cachedMatches.resetBiggestChange();
      for (ASTCDType srcType : srcTypes) {
        for (ASTCDType tgtType : tgtTypes) {
          matcher.getScore(srcType, tgtType);
        }
      }
      if(cachedMatches.getBiggestChange() < MINIMUM_CHANGE_THRESHOLD) break;
    }

    scoredMatches = cachedMatches;
    // compute a matching of types by name
    typeMatches = computeMatching(cachedMatches.getTypeMatches(), threshold);
    assocMatches = computeMatching(cachedMatches.getAssocMatches(), threshold);
    attributeMatches = computeMatching(cachedMatches.getAttributeMatches(), threshold);
  }

  public CDSynDiffMatches(ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD,
                          boolean matchStructure) {

    structureCache = new StructureCache();

    setupStructureCache(srcCD, structureCache);
    setupStructureCache(tgtCD, structureCache);

    // Compute types of srcCD and tgtCD without using the traverser
    Set<ASTCDType> srcTypes = CDDiffUtil.getAllTypesFromCD(srcCD);
    Set<ASTCDType> tgtTypes = CDDiffUtil.getAllTypesFromCD(tgtCD);

    // compute a matching of types by name
    MatchingStrategy<ASTCDType> typeMatcher = new MatchCDTypesByQName();
    CachedMatch<ASTCDType> nameMatches = new CachedMatch<>();
    applyMatchingStrategy(srcTypes, tgtTypes, typeMatcher, nameMatches);

    // Compute associations of srcCD and tgtCD without using the traverser
    Set<ASTCDAssociation> srcAssocs = CDDiffUtil.getAllAssocsFromCD(srcCD);
    Set<ASTCDAssociation> tgtAssocs = CDDiffUtil.getAllAssocsFromCD(tgtCD);

    CachedMatch<ASTCDType> typeAssocMatches = nameMatches;

    /*
     * Types are matched according to structural similarities.
     * The previous matching is added to the new multi-matching.
     */
    if (matchStructure) {

      CachedMatch<ASTCDType> structureMatch = new CachedMatch<>();
      typeMatcher = new MatchBySimilarity<>(new CDTypeSimilarity());
      applyMatchingStrategy(srcTypes, tgtTypes, typeMatcher, structureMatch);

      typeAssocMatches = CachedMatch.merge(List.of(nameMatches, structureMatch), Double::max);

      typeMatches = computeMatching(typeAssocMatches, 0.5);

    }
    else {
      typeMatches = computeMatching(nameMatches, 1.0);
    }

    BooleanMatchingStrategy<ASTCDType> assocTypeMatcher = new BooleanMatchFromCache<>(typeAssocMatches, 0.5);
    BooleanMatchingStrategy<ASTCDAssociation> assocMatcher = new MatchCDAssocsBySrcTypeAndTgtRole(assocTypeMatcher, structureCache);
    CachedMatch<ASTCDAssociation> assocNonStructureMatch = new CachedMatch<>();
    applyMatchingStrategy(srcAssocs, tgtAssocs, assocMatcher, assocNonStructureMatch);

    // add greedy assoc matches if structure matching is active
    if (matchStructure) {
      assocMatcher = new MatchCDAssocsGreedy(assocTypeMatcher, structureCache);
      CachedMatch<ASTCDAssociation> assocStructureMatch = new CachedMatch<>();
      applyMatchingStrategy(srcAssocs, tgtAssocs, assocMatcher, assocStructureMatch);

      assocMatches = computeMatching(CachedMatch.merge(List.of(assocNonStructureMatch, assocStructureMatch), Double::max), 1.0);
    } else {
      assocMatches = computeMatching(assocNonStructureMatch, 1.0);
    }

    attributeMatches = new LinkedHashMap<>();
  }

  public static void setupStructureCache(ASTCDCompilationUnit cD, StructureCache structureCache) {

    boolean success = getAllCDTypes(cD).stream().map(structureCache::addType).reduce(Boolean::logicalAnd).orElse(true);
    if (!success) {
      Log.warn("StructureCache already contains types from CD: " + cD.getCDDefinition().getName());
    }

    // Add direct members
    for (ASTCDType type : getAllCDTypes(cD)) {
      success =
        structureCache.addAllDirectSuperTypes(type, CDInheritanceHelper.getDirectSuperClasses(type, (ICD4CodeArtifactScope) cD.getEnclosingScope())) &&
          structureCache.addAllDirectAssociations(type, CDAssociationHelper.getDirectAssociations(type, cD)) &&
          structureCache.addAllDirectAttributes(type, CDAttributeHelper.getAttributes(type));
      if (!success) {
        Log.warn("StructureCache already contains members from  type: " + type.getName());
      }
    }

    // complete Transitive closure
    for (ASTCDType type : getAllCDTypes(cD)) {
      Set<ASTCDType> superTypes = getAllSuperSuperTypesFromCache(type, structureCache);
      success = structureCache.addAllSuperTypes(type, superTypes);
      if (!success) {
        Log.warn("StructureCache already contains super types from type: " + type.getName());
      }

      Set<ASTCDAssociation> associations = superTypes.stream().map(structureCache::getDirectAssociations).collect(HashSet::new, Set::addAll, Set::addAll);
      associations.addAll(structureCache.getDirectAssociations(type));
      success = structureCache.addAllAssociations(type, associations);
      if (!success) {
        Log.warn("StructureCache already contains associations from type: " + type.getName());
      }

      Set<ASTCDAttribute> attributes = superTypes.stream().map(structureCache::getDirectAttributes).collect(HashSet::new, Set::addAll, Set::addAll);
      attributes.addAll(structureCache.getDirectAttributes(type));
      success = structureCache.addAllAttributes(type, attributes);
      if (!success) {
        Log.warn("StructureCache already contains attributes from type: " + type.getName());
      }
    }

    // Add subtypes
    for(ASTCDType type : getAllCDTypes(cD)) {
      Set<ASTCDType> directSubTypes = getAllCDTypes(cD).stream()
        .filter(t -> structureCache.getDirectSuperTypes(t).contains(type))
        .collect(Collectors.toSet());
      success = structureCache.addAllDirectSubTypes(type, directSubTypes);
      if (!success) {
        Log.warn("StructureCache already contains direct subtypes from type: " + type.getName());
      }
      Set<ASTCDType> subTypes = getAllCDTypes(cD).stream()
        .filter(t -> structureCache.getSuperTypes(t).contains(type))
        .collect(Collectors.toSet());
      success = structureCache.addAllSubTypes(type, subTypes);
      if (!success) {
        Log.warn("StructureCache already contains subtypes from type: " + type.getName());
      }
    }

    // Setup Assoc Cache
    for (ASTCDAssociation assoc : cD.getCDDefinition().getCDAssociationsList()) {
      ASTCDType leftType = CDAssociationHelper.getCDTypeSymbol(assoc.getLeft());
      ASTCDType rightType = CDAssociationHelper.getCDTypeSymbol(assoc.getRight());

      boolean added = structureCache.addAssociation(assoc, leftType, rightType);
      if (!added) {
        Log.warn("StructureCache already contains association: " + assoc.getName());
      }
    }
  }

  private static void preCalculateExactMatches(ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD, CachedMatches cachedMatches, StructureCache structureCache) {
    List<ASTCDType> srcTypes = getAllCDTypes(srcCD);
    List<ASTCDType> tgtTypes = getAllCDTypes(tgtCD);

    // Consider Types to be the same if Names are identical and Attribute Types and Names are the same
    srcTypes.stream()
      .flatMap(src -> tgtTypes.stream().map(tgt -> new Pair<>(src, tgt)))
      .filter(match -> match.a.isPresentSymbol() && match.b.isPresentSymbol())
      .filter(match -> match.a.getSymbol().getInternalQualifiedName().equals(match.b.getSymbol().getInternalQualifiedName()))
      .filter(match -> hasSameAttributes(match.a, match.b, structureCache))
      .forEach(
        match -> cachedMatches.putMatch(match.a, match.b, 1.0)
      );

    List<ASTCDAssociation> srcAssocs = srcCD.getCDDefinition().getCDAssociationsList();
    List<ASTCDAssociation> tgtAssocs = tgtCD.getCDDefinition().getCDAssociationsList();

    // Consider Assocs to be the same if direction, type, left name, right name and name (if present) are the identical
    srcAssocs.stream()
      .flatMap(src -> tgtAssocs.stream().map(tgt -> new Pair<>(src, tgt)))
      .filter(match -> match.a.isPresentName() == match.b.isPresentName())
      .filter(match -> !match.a.isPresentName() || match.a.getName().equals(match.b.getName()))
      .filter(match -> match.a.getLeft().getName().equals(match.b.getLeft().getName()))
      .filter(match -> match.a.getRight().getName().equals(match.b.getRight().getName()))
      .filter(match -> match.a.getCDAssocDir().getClass().equals(match.b.getCDAssocDir().getClass()))
      .filter(match -> match.a.getCDAssocType().getClass().equals(match.b.getCDAssocType().getClass()))
      .forEach(match -> cachedMatches.putMatch(match.a, match.b, 1.0));
  }

  private static boolean hasSameAttributes(ASTCDType src, ASTCDType tgt, StructureCache structureCache) {
    Set<ASTCDAttribute> srcAttributes = structureCache.getDirectAttributes(src);
    Set<ASTCDAttribute> tgtAttributes = structureCache.getDirectAttributes(tgt);

    if(srcAttributes.size() != tgtAttributes.size()) { return false; }

    Set<Pair<String, String>> srcAttrNameAndType = srcAttributes.stream()
      .map(
        attr -> new Pair<>(attr.getName(), CDAttributeHelper.resolveClass(attr))
      )
      .filter(pair -> pair.b == null || pair.b.isPresentSymbol())
      .map(pair -> new Pair<>(pair.a, pair.b == null ? null : pair.b.getSymbol().getInternalQualifiedName()))
      .collect(Collectors.toSet());

    if(srcAttributes.size() != srcAttrNameAndType.size()) { return false; }

    Set<Pair<String, String>> tgtAttrNameAndType = tgtAttributes.stream()
      .map(
        attr -> new Pair<>(attr.getName(), CDAttributeHelper.resolveClass(attr))
      ).filter(pair -> pair.b == null || pair.b.isPresentSymbol())
      .map(pair -> new Pair<>(pair.a, pair.b == null ? null : pair.b.getSymbol().getInternalQualifiedName()))
      .collect(Collectors.toSet());

    if(tgtAttributes.size() != tgtAttrNameAndType.size()) { return false; }

    return srcAttrNameAndType.equals(tgtAttrNameAndType);
  }

  public static Set<ASTCDType> getAllSuperSuperTypesFromCache(ASTCDType type, StructureCache structureCache) {
    Set<ASTCDType> superTypes = new HashSet<>();
    for (ASTCDType superType : structureCache.getDirectSuperTypes(type)) {
      superTypes.addAll(getAllSuperSuperTypesFromCache(superType, structureCache));
    }
    superTypes.addAll(structureCache.getDirectSuperTypes(type));
    return superTypes;
  }


  public static <T> Map<T, T> computeMatching(CachedMatch<T> matches, double threshold) {
    Map<T, T> matching = new LinkedHashMap<>();
    List<Map.Entry<Pair<T, T>, Double>> matchScores = matches.getMatches().entrySet().stream().sorted(
      Map.Entry.comparingByValue(Comparator.reverseOrder())
    ).collect(Collectors.toList());
    Iterator<Map.Entry<Pair<T, T>, Double>> iterator = matchScores.iterator();

    Set<T> matchedMapValues = new HashSet<>();

    while (iterator.hasNext()) {
      Map.Entry<Pair<T, T>, Double> entry = iterator.next();
      if(entry.getValue() < threshold) {
        break;
      }
      Pair<T, T> pair = entry.getKey();
      if (!matching.containsKey(pair.a) && !matchedMapValues.contains(pair.b)) {
        matching.put(pair.a, pair.b);
        matchedMapValues.add(pair.b);
      }
    }
    return matching;
  }

  public static <T> void applyMatchingStrategy(Set<T> srcElements, Set<T> tgtElements, MatchingStrategy<T> matcher, CachedMatch<T> cache) {
    srcElements.stream().flatMap(src -> tgtElements.stream().map(tgt -> new Pair<>(src, tgt)))
      .forEach(pair -> cache.putMatch(pair.a, pair.b, matcher.getScore(pair.a, pair.b)));
  }

  public Map<ASTCDType, ASTCDType> getTypeMatches() {
    return typeMatches;
  }

  public Map<ASTCDAssociation, ASTCDAssociation> getAssocMatches() {
    return assocMatches;
  }

  public Map<ASTCDAttribute, ASTCDAttribute> getAttributeMatches() {
    return attributeMatches;
  }

  public StructureCache getStructureCache() {
    return structureCache;
  }

  public CachedMatches getScoredMatches() {
    return scoredMatches;
  }
}
