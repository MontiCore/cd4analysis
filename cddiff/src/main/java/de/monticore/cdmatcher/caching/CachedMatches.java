/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmatcher.caching;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;

public class CachedMatches {

  private final CachedMatch<ASTCDType> typeMatches = new CachedMatch<>();
  private final CachedMatch<ASTCDAttribute> attributeMatches = new CachedMatch<>();
  private final CachedMatch<ASTCDAssociation> assocMatches = new CachedMatch<>();
  private double biggestChange = 0.0;

  public void putMatch(ASTCDType srcElem, ASTCDType tgtElem, Double value) {
    Double oldValue = typeMatches.putMatch(srcElem, tgtElem, value);
    updateBiggestChange(oldValue, value);
  }

  public void putMatch(ASTCDAttribute srcElem, ASTCDAttribute tgtElem, Double value) {
    Double oldValue = attributeMatches.putMatch(srcElem, tgtElem, value);
    updateBiggestChange(oldValue, value);
  }

  public void putMatch(ASTCDAssociation srcElem, ASTCDAssociation tgtElem, Double value) {
    Double oldValue = assocMatches.putMatch(srcElem, tgtElem, value);
    updateBiggestChange(oldValue, value);
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

  public double getBiggestChange() {
    return biggestChange;
  }

  public void resetBiggestChange() {
    biggestChange = 0.0;
  }

  private void updateBiggestChange(Double oldValue, Double newValue) {
    if(oldValue == null){
      biggestChange = newValue;
    } else {
      biggestChange = Math.max(biggestChange, Math.abs(oldValue - newValue));
    }
  }

}
