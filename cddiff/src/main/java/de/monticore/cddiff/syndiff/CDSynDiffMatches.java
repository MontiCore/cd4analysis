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
import de.monticore.cdmatcher.matching.association.MatchCDAssocByBestSuperType;
import de.monticore.cdmatcher.matching.caching.CachedMatch;
import de.monticore.cdmatcher.matching.MatchingStrategy;
import de.monticore.cdmatcher.matching.caching.CachedMatches;
import de.monticore.cdmatcher.matching.caching.StructureCache;
import de.monticore.cdmatcher.matching.cdtype.*;
import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.misc.Pair;

import java.util.*;
import java.util.stream.Collectors;

import static de.monticore.cddiff.CDDiffUtil.getAllCDTypes;

/**
 * This class should be used to construct a matching of respectively types and associations between
 * the srcCD and the tgtCD for the SynDiff and Syn2SemDiff. Recomputing of matches should be
 * avoided.
 */
public class CDSynDiffMatches {
  protected Map<ASTCDType, ASTCDType> typeMatches;
  protected Map<ASTCDAssociation, ASTCDAssociation> assocMatches;
  protected Map<ASTCDAttribute, ASTCDAttribute> attributeMatches;
  private static final double MINIMUM_CHANGE_THRESHOLD = 0.001;

  /**
   * The constructor call computes all matches of types and associations between srcCD and tgtCD.
   *
   * @param matchStructure determines whether structural similarities are used to determine type
   *     matches
   */
  public CDSynDiffMatches(
      ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD, boolean matchStructure, int matchingIterations, double threshold) {

    CachedMatches.clear();

    // Compute types of srcCD and tgtCD without using the traverser
    Set<ASTCDType> srcTypes = CDDiffUtil.getAllTypesFromCD(srcCD);
    Set<ASTCDType> tgtTypes = CDDiffUtil.getAllTypesFromCD(tgtCD);

    Set<MatchingStrategy<ASTCDType>> matchingStrategies = new HashSet<>((Set.of(
      new MatchCDTypeByName(),
      new MatchCDTypeByDirectAssocs(new MatchCDAssocByBestSuperType()),
      new MatchCDTypeByDirectAttributes()
    )));
    if(matchStructure) {
      matchingStrategies.addAll(
        Set.of(
          new MatchCDTypeByDirectSubClasses(),
          new MatchCDTypeByDirectSuperClasses()
        )
      );
    }

    StructureCache.clear();
    setupStructureCache(srcCD);
    setupStructureCache(tgtCD);

    preCalculateExactMatches(srcCD, tgtCD);

    MatchCDType matcher = new MatchCDType(matchingStrategies);

    for(int i = 0; i < matchingIterations; i++) {
      CachedMatches.resetBiggestChange();
      for (ASTCDType srcType : srcTypes) {
        for (ASTCDType tgtType : tgtTypes) {
          matcher.getScore(srcType, tgtType);
        }
      }
      if(CachedMatches.getBiggestChange() < MINIMUM_CHANGE_THRESHOLD) break;
    }

    // compute a matching of types by name
    typeMatches = computeMatching(CachedMatches.getTypeMatches(), threshold);
    assocMatches = computeMatching(CachedMatches.getAssocMatches(), threshold);
    attributeMatches = computeMatching(CachedMatches.getAttributeMatches(), threshold);
  }

  private static void setupStructureCache(ASTCDCompilationUnit cD) {

    boolean success = getAllCDTypes(cD).stream().map(StructureCache::addType).reduce(Boolean::logicalAnd).orElse(true);
    if (!success) {
      Log.warn("StructureCache already contains types from CD: " + cD.getCDDefinition().getName());
    }

    // Add direct members
    for (ASTCDType type : getAllCDTypes(cD)) {
      success =
        StructureCache.addAllDirectSuperTypes(type, CDInheritanceHelper.getDirectSuperClasses(type, (ICD4CodeArtifactScope) cD.getEnclosingScope())) &&
        StructureCache.addAllDirectAssociations(type, CDAssociationHelper.getDirectAssociations(type, cD)) &&
        StructureCache.addAllDirectAttributes(type, CDAttributeHelper.getAttributes(type));
      if (!success) {
        Log.warn("StructureCache already contains members from  type: " + type.getName());
      }
    }

    // complete Transitive closure
    for (ASTCDType type : getAllCDTypes(cD)) {
      Set<ASTCDType> superTypes = getAllSuperSuperTypesFromCache(type);
      success = StructureCache.addAllSuperTypes(type, superTypes);
      if (!success) {
        Log.warn("StructureCache already contains super types from type: " + type.getName());
      }

      Set<ASTCDAssociation> associations = superTypes.stream().map(StructureCache::getDirectAssociations).collect(HashSet::new, Set::addAll, Set::addAll);
      associations.addAll(StructureCache.getDirectAssociations(type));
      success = StructureCache.addAllAssociations(type, associations);
      if (!success) {
        Log.warn("StructureCache already contains associations from type: " + type.getName());
      }

      Set<ASTCDAttribute> attributes = superTypes.stream().map(StructureCache::getDirectAttributes).collect(HashSet::new, Set::addAll, Set::addAll);
      attributes.addAll(StructureCache.getDirectAttributes(type));
      success = StructureCache.addAllAttributes(type, attributes);
      if (!success) {
        Log.warn("StructureCache already contains attributes from type: " + type.getName());
      }
    }

    // Add direct subtypes
    for(ASTCDType type : getAllCDTypes(cD)) {
      Set<ASTCDType> subTypes = getAllCDTypes(cD).stream()
        .filter(t -> StructureCache.getDirectSuperTypes(t).contains(type))
        .collect(Collectors.toSet());
      success = StructureCache.addAllDirectSubTypes(type, subTypes);
      if (!success) {
        Log.warn("StructureCache already contains direct subtypes from type: " + type.getName());
      }
    }

    // Setup Assoc Cache
    for (ASTCDAssociation assoc : cD.getCDDefinition().getCDAssociationsList()) {
      ASTCDType leftType = CDAssociationHelper.getCDTypeSymbol(assoc.getLeft());
      ASTCDType rightType = CDAssociationHelper.getCDTypeSymbol(assoc.getRight());

      boolean added = StructureCache.addAssociation(assoc, leftType, rightType);
      if (!added) {
        Log.warn("StructureCache already contains association: " + assoc.getName());
      }
    }
  }

  private static void preCalculateExactMatches(ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD) {
    List<ASTCDType> srcTypes = getAllCDTypes(srcCD);
    List<ASTCDType> tgtTypes = getAllCDTypes(tgtCD);

    // Consider Types to be the same if Names are identical and Attribute Types and Names are the same
    srcTypes.stream()
      .flatMap(src -> tgtTypes.stream().map(tgt -> new Pair<>(src, tgt)))
      .filter(match -> match.a.isPresentSymbol() && match.b.isPresentSymbol())
      .filter(match -> match.a.getSymbol().getInternalQualifiedName().equals(match.b.getSymbol().getInternalQualifiedName()))
      .filter(match -> hasSameAttributes(match.a, match.b))
      .forEach(
        match -> CachedMatches.putMatch(match.a, match.b, 1.0)
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
      .forEach(match -> CachedMatches.putMatch(match.a, match.b, 1.0));
  }

  private static boolean hasSameAttributes(ASTCDType src, ASTCDType tgt) {
    Set<ASTCDAttribute> srcAttributes = StructureCache.getDirectAttributes(src);
    Set<ASTCDAttribute> tgtAttributes = StructureCache.getDirectAttributes(tgt);

    if(srcAttributes.size() != tgtAttributes.size()) { return false; }

    Set<Pair<String, String>> srcAttrNameAndType = srcAttributes.stream()
      .map(
        attr -> new Pair<>(attr.getName(), CDAttributeHelper.resolveClass(attr))
      )
      .filter(pair -> pair.b != null && pair.b.isPresentSymbol())
      .map(pair -> new Pair<>(pair.a, pair.b.getSymbol().getInternalQualifiedName()))
      .collect(Collectors.toSet());

    if(srcAttributes.size() != srcAttrNameAndType.size()) { return false; }

    Set<Pair<String, String>> tgtAttrNameAndType = tgtAttributes.stream()
      .map(
        attr -> new Pair<>(attr.getName(), CDAttributeHelper.resolveClass(attr))
      ).filter(pair -> pair.b != null && pair.b.isPresentSymbol())
      .map(pair -> new Pair<>(pair.a, pair.b.getSymbol().getInternalQualifiedName()))
      .collect(Collectors.toSet());

    if(tgtAttributes.size() != tgtAttrNameAndType.size()) { return false; }

    return srcAttrNameAndType.equals(tgtAttrNameAndType);
  }

  public static Set<ASTCDType> getAllSuperSuperTypesFromCache(ASTCDType type) {
    Set<ASTCDType> superTypes = new HashSet<>();
    for (ASTCDType superType : StructureCache.getDirectSuperTypes(type)) {
      superTypes.addAll(getAllSuperSuperTypesFromCache(superType));
    }
    superTypes.addAll(StructureCache.getDirectSuperTypes(type));
    return superTypes;
  }


  /** computes a matching based on a MatchingStrategy */
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

  public Map<ASTCDType, ASTCDType> getTypeMatches() {
    return typeMatches;
  }

  public Map<ASTCDAssociation, ASTCDAssociation> getAssocMatches() {
    return assocMatches;
  }

  public Map<ASTCDAttribute, ASTCDAttribute> getAttributeMatches() {
    return attributeMatches;
  }
}
