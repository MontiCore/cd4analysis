package de.monticore.cdmatcher.matching.caching;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;

public class CachedMatches {
  private static final CachedMatch<ASTCDType> typeMatches = new CachedMatch<>();
  private static final CachedMatch<ASTCDAttribute> attributeMatches = new CachedMatch<>();
  private static final CachedMatch<ASTCDAssociation> assocMatches = new CachedMatch<>();
  private static Double biggestChange = 0.0;

  public static void putMatch(ASTCDType srcElem, ASTCDType tgtElem, Double value) {
    Double old = typeMatches.putMatch(srcElem, tgtElem, value);
    if(old == null){
      biggestChange = value;
    } else if (Math.abs(old - value) > biggestChange) {
      biggestChange = Math.abs(old - value);
    }
  }

  public static void putMatch(ASTCDAttribute srcElem, ASTCDAttribute tgtElem, Double value) {
    Double old = attributeMatches.putMatch(srcElem, tgtElem, value);
    if(old == null){
      biggestChange = value;
    } else if (Math.abs(old - value) > biggestChange) {
      biggestChange = Math.abs(old - value);
    }
  }

  public static void putMatch(ASTCDAssociation srcElem, ASTCDAssociation tgtElem, Double value) {
    Double old = assocMatches.putMatch(srcElem, tgtElem, value);
    if(old == null){
      biggestChange = value;
    } else if (Math.abs(old - value) > biggestChange) {
      biggestChange = Math.abs(old - value);
    }
  }

  public static Double getMatch(ASTCDType srcElem, ASTCDType tgtElem) {
    return typeMatches.getMatch(srcElem, tgtElem);
  }

  public static Double getMatch(ASTCDAttribute srcElem, ASTCDAttribute tgtElem) {
    return attributeMatches.getMatch(srcElem, tgtElem);
  }

  public static Double getMatch(ASTCDAssociation srcElem, ASTCDAssociation tgtElem) {
    return assocMatches.getMatch(srcElem, tgtElem);
  }

  public static CachedMatch<ASTCDType> getTypeMatches() {
    return typeMatches;
  }

  public static CachedMatch<ASTCDAttribute> getAttributeMatches() {
    return attributeMatches;
  }

  public static CachedMatch<ASTCDAssociation> getAssocMatches() {
    return assocMatches;
  }

  public static Double getBiggestChange() {
    return biggestChange;
  }

  public static void resetBiggestChange(){
    biggestChange = 0.0;
  }

  public static void clear() {
    typeMatches.clear();
    attributeMatches.clear();
    assocMatches.clear();
    biggestChange = 0.0;
  }
}
