/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmatcher;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cddiff.syndiff.CDSynDiffMatches;
import de.monticore.cddiff.syndiff.SynDiffTestBasis;
import de.monticore.cdmatcher.booleanMatching.MatchCDAssocsByName;
import de.monticore.cdmatcher.booleanMatching.MatchCDAssocsBySrcTypeAndTgtRole;
import de.monticore.cdmatcher.booleanMatching.MatchCDTypesByName;
import de.monticore.cdmatcher.booleanMatching.MatchCDTypesToSuperType;
import de.monticore.cdmatcher.caching.CachedMatch;
import de.monticore.cdmatcher.caching.StructureCache;
import de.monticore.cdmatcher.similarity.CDTypeSimilarity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CD2CDMatcherTest extends SynDiffTestBasis {

  @BeforeAll
  public static void init() {
    dir = "src/test/resources/de/monticore/cdmatcher/";
  }

  @Test
  public void testMatchAssocName() {
    parseModels("Source.cd", "Refinement.cd");
    CachedMatch<ASTCDAssociation> cachedMatch = new CachedMatch<>();
    CDSynDiffMatches.applyMatchingStrategy(
      new HashSet<>(src.getCDDefinition().getCDAssociationsList()),
      new HashSet<>(tgt.getCDDefinition().getCDAssociationsList()),
      new MatchCDAssocsByName(),
      cachedMatch);
    assertEquals(1, cachedMatch.getMatches().size());
    for(var match : cachedMatch.getMatches().entrySet()) {
      if(match.getValue() > 0.0) {
        assertEquals(match.getKey().a.getName(), match.getKey().b.getName());
      }
    }
  }

  @Test
  public void testMatchTypeName() {
    parseModels("Source.cd", "Refinement.cd");
    CachedMatch<ASTCDType> cachedMatch = new CachedMatch<>();
    CDSynDiffMatches.applyMatchingStrategy(
      new HashSet<>(CDDiffUtil.getAllCDTypes(src)),
      new HashSet<>(CDDiffUtil.getAllCDTypes(tgt)),
      new MatchCDTypesByName(),
      cachedMatch);
    assertEquals(2, cachedMatch.getMatches().values().stream().filter(d -> d >= 1.0).count());
    for(var match : cachedMatch.getMatches().entrySet()) {
      if(match.getValue() > 0.0) {
        assertEquals(match.getKey().a.getName(), match.getKey().b.getName());
      }
    }
  }

  @Test
  public void testMatchStructureType() {
    parseModels("Source6.cd", "Refinement6.cd");
    CachedMatch<ASTCDType> cachedMatch = new CachedMatch<>();
    CDSimilarity<ASTCDType> similarity = new CDTypeSimilarity();
    CDSynDiffMatches.applyMatchingStrategy(
      new HashSet<>(CDDiffUtil.getAllCDTypes(src)),
      new HashSet<>(CDDiffUtil.getAllCDTypes(tgt)),
      new MatchBySimilarity<>(similarity),
      cachedMatch);
    assertEquals(2, cachedMatch.getMatches().values().stream().filter(d -> d >= 0.5).count());
    for(var match : cachedMatch.getMatches().entrySet()) {
      if(match.getValue() >= 0.5) {
        assertEquals(similarity.computeWeight(match.getKey().a, match.getKey().b), match.getValue());
      }
    }
  }

  @Test
  public void testMatchSubToSuperClass() {
    parseModels("Source2.cd", "Refinement2.cd");
    CachedMatch<ASTCDType> cachedMatch = new CachedMatch<>();
    StructureCache structureCache = new StructureCache();
    CDSynDiffMatches.setupStructureCache(src, structureCache);
    CDSynDiffMatches.setupStructureCache(tgt, structureCache);

    CDSynDiffMatches.applyMatchingStrategy(
      new HashSet<>(CDDiffUtil.getAllCDTypes(src)),
      new HashSet<>(CDDiffUtil.getAllCDTypes(tgt)),
      new MatchCDTypesToSuperType(new MatchCDTypesByName(), structureCache),
      cachedMatch);
    assertEquals(1, cachedMatch.getMatches().values().stream().filter(d -> d >= 1.0).count());
    for(var match : cachedMatch.getMatches().entrySet()) {
      if(match.getValue() > 0.0) {
        assertTrue(structureCache.getSuperTypes(match.getKey().a).stream().anyMatch(superType -> superType.getName().equals(match.getKey().b.getName())));
      }
    }
  }

  @Test
  public void testMatchSrcClassTgtRoleName() {
    parseModels("Source3.cd", "Refinement3.cd");
    CachedMatch<ASTCDAssociation> cachedMatch = new CachedMatch<>();
    StructureCache structureCache = new StructureCache();
    CDSynDiffMatches.setupStructureCache(src, structureCache);
    CDSynDiffMatches.setupStructureCache(tgt, structureCache);

    CDSynDiffMatches.applyMatchingStrategy(
      new HashSet<>(src.getCDDefinition().getCDAssociationsList()),
      new HashSet<>(tgt.getCDDefinition().getCDAssociationsList()),
      new MatchCDAssocsBySrcTypeAndTgtRole(new MatchCDTypesByName(), structureCache),
      cachedMatch);
    assertEquals(1, cachedMatch.getMatches().values().stream().filter(d -> d >= 1.0).count());
  }

}
