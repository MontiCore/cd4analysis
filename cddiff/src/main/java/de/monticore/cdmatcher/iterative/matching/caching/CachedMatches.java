/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmatcher.iterative.matching.caching;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;

public class CachedMatches {
  
  private static final CachedMatch<ASTCDType> typeMatches = new CachedMatch<>();
  private static final CachedMatch<ASTCDAttribute> attributeMatches = new CachedMatch<>();
  private static final CachedMatch<ASTCDAssociation> assocMatches = new CachedMatch<>();
  
  public void putMatch(ASTCDType srcElem, ASTCDType tgtElem, Double value) {
    typeMatches.putMatch(srcElem, tgtElem, value);
  }
  
  public void putMatch(ASTCDAttribute srcElem, ASTCDAttribute tgtElem, Double value) {
    attributeMatches.putMatch(srcElem, tgtElem, value);
  }
  
  public void putMatch(ASTCDAssociation srcElem, ASTCDAssociation tgtElem, Double value) {
    assocMatches.putMatch(srcElem, tgtElem, value);
  }
  
  public Double getMatch(ASTCDType srcElem, ASTCDType tgtElem) {
    return typeMatches.getMatch(srcElem, tgtElem);
  }
  
  public Double getMatch(ASTCDAttribute srcElem, ASTCDAttribute tgtElem) {
    return attributeMatches.getMatch(srcElem, tgtElem);
  }
  
  public Double getMatch(ASTCDAssociation srcElem, ASTCDAssociation tgtElem) {
    return assocMatches.getMatch(srcElem, tgtElem);
  }
  
  public CachedMatch<ASTCDType> getTypeMatches() { return typeMatches; }
  
  public CachedMatch<ASTCDAttribute> getAttributeMatches() { return attributeMatches; }
  
  public CachedMatch<ASTCDAssociation> getAssocMatches() { return assocMatches; }
  
}
