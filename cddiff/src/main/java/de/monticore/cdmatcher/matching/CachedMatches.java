package de.monticore.cdmatcher.matching;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import org.antlr.v4.runtime.misc.Pair;

import java.util.HashMap;
import java.util.Map;

public class CachedMatches {
  private static final Map<Pair<ASTCDType, ASTCDType>, Double> classMatches = new HashMap<>();
  private static final Map<Pair<ASTCDAttribute, ASTCDAttribute>, Double> attributeMatches = new HashMap<>();
  private static final Map<Pair<ASTCDAssociation, ASTCDAssociation>, Double> assocMatches = new HashMap<>();

  public static void putMatch(ASTCDType srcElem, ASTCDType tgtElem, Double value) {
    classMatches.put(new Pair<>(srcElem, tgtElem), value);
  }

  public static void putMatch(ASTCDAttribute srcElem, ASTCDAttribute tgtElem, Double value) {
    attributeMatches.put(new Pair<>(srcElem, tgtElem), value);
  }

  public static void putMatch(ASTCDAssociation srcElem, ASTCDAssociation tgtElem, Double value) {
    assocMatches.put(new Pair<>(srcElem, tgtElem), value);
  }

  public static Double getMatch(ASTCDType srcElem, ASTCDType tgtElem) {
    return classMatches.get(new Pair<>(srcElem, tgtElem));
  }

  public static Double getMatch(ASTCDAttribute srcElem, ASTCDAttribute tgtElem) {
    return attributeMatches.get(new Pair<>(srcElem, tgtElem));
  }

  public static Double getMatch(ASTCDAssociation srcElem, ASTCDAssociation tgtElem) {
    return assocMatches.get(new Pair<>(srcElem, tgtElem));
  }

}
