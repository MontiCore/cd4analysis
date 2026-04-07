/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmatcher.caching;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import org.antlr.v4.runtime.misc.Pair;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class CachedMatches {

  /** ASTCDAttribute does not override equals and hashCode
   * For caching purposes it is inefficient to use the default implementations, as two attributes that have the same name and type can never have different scores.
   * The wrapper class overrides equals and hashCode so the HashMap in CachedMatch can work more efficiently.
   * The usage of the wrapper class is internal to CachedMatches only.
   */
  private static class CDAttributeWrapper {
    private final ASTCDAttribute attribute;

    public CDAttributeWrapper(ASTCDAttribute attribute) {
      this.attribute = attribute;
    }

    public ASTCDAttribute getAttribute() {
      return attribute;
    }

    @Override
    public int hashCode() {
      return Objects.hash(attribute.getName(), attribute.getMCType().printType());
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      CDAttributeWrapper other = (CDAttributeWrapper) obj;
      return attribute.getName().equals(other.attribute.getName()) &&
             attribute.getMCType().printType().equals(other.attribute.getMCType().printType());
    }
  }

  private final CachedMatch<ASTCDType> typeMatches = new CachedMatch<>();
  private final CachedMatch<CDAttributeWrapper> attributeMatches = new CachedMatch<>();
  private final CachedMatch<ASTCDAssociation> assocMatches = new CachedMatch<>();
  private final CachedMatch<ASTCDMethod> methodMatches = new CachedMatch<>();
  private double biggestChange = 0.0;

  public void putMatch(ASTCDType srcElem, ASTCDType tgtElem, Double value) {
    Double oldValue = typeMatches.putMatch(srcElem, tgtElem, value);
    updateBiggestChange(oldValue, value);
  }

  public void putMatch(ASTCDAttribute srcElem, ASTCDAttribute tgtElem, Double value) {
    attributeMatches.putMatch(new CDAttributeWrapper(srcElem), new CDAttributeWrapper(tgtElem), value);
  }

  public void putMatch(ASTCDAssociation srcElem, ASTCDAssociation tgtElem, Double value) {
    assocMatches.putMatch(srcElem, tgtElem, value);
  }

  public void  putMatch(ASTCDMethod srcElem, ASTCDMethod tgtElem, Double value) {
    methodMatches.putMatch(srcElem, tgtElem, value);
  }

  public Double getMatch(ASTCDType srcElem, ASTCDType tgtElem) {
    return typeMatches.getMatch(srcElem, tgtElem);
  }

  public Double getMatch(ASTCDAttribute srcElem, ASTCDAttribute tgtElem) {
    return attributeMatches.getMatch(new CDAttributeWrapper(srcElem), new CDAttributeWrapper(tgtElem));
  }

  public Double getMatch(ASTCDAssociation srcElem, ASTCDAssociation tgtElem) {
    return assocMatches.getMatch(srcElem, tgtElem);
  }

  public Double getMatch(ASTCDMethod srcElem, ASTCDMethod tgtElem) {
    return methodMatches.getMatch(srcElem, tgtElem);
  }

  public CachedMatch<ASTCDType> getTypeMatches() { return typeMatches; }


  /**
   * Returns the attribute matches stored in the cache.
   * The lists will be used to add equivalent attributes from the lists to the result.
   * The resulting CachedMatch will contain a separate entry for each pair of attributes even if they have the same name and type (as opposed to the method without parameters).
   * @param srcAttributes List of attributes in the source CD
   * @param tgtAttributes List of attributes in the target CD
   * @return CachedMatch containing the attribute matches
   */
  public CachedMatch<ASTCDAttribute> getAttributeMatches(Set<ASTCDAttribute> srcAttributes, Set<ASTCDAttribute> tgtAttributes) {
    Map<CDAttributeWrapper, List<CDAttributeWrapper>> equalSrcAttributes = new HashMap<>();
    Map<CDAttributeWrapper, List<CDAttributeWrapper>> equalTgtAttributes = new HashMap<>();
    srcAttributes.stream().map(CDAttributeWrapper::new).forEach(attr -> {
      equalSrcAttributes.putIfAbsent(attr, new java.util.LinkedList<>());
      equalSrcAttributes.get(attr).add(attr);
    });
    tgtAttributes.stream().map(CDAttributeWrapper::new).forEach(attr -> {
      equalTgtAttributes.putIfAbsent(attr, new java.util.LinkedList<>());
      equalTgtAttributes.get(attr).add(attr);
    });

    CachedMatch<ASTCDAttribute> unwrappedMatches = new CachedMatch<>();

    attributeMatches.getMatches().entrySet().stream()
      // add all equivalent source attributes
      .flatMap(entry -> equalSrcAttributes.getOrDefault(entry.getKey().a, new java.util.LinkedList<>()).stream()
        .map(srcAttribute -> new AbstractMap.SimpleEntry<>(new Pair<>(srcAttribute, entry.getKey().b), entry.getValue())))
      // add all equivalent target attributes
      .flatMap(entry -> equalTgtAttributes.getOrDefault(entry.getKey().b, new java.util.LinkedList<>()).stream()
        .map(tgtAttribute -> new AbstractMap.SimpleEntry<>(new Pair<>(entry.getKey().a, tgtAttribute), entry.getValue())))

      .forEach(entry -> unwrappedMatches.putMatch(entry.getKey().a.getAttribute(), entry.getKey().b.getAttribute(), entry.getValue()));

    return unwrappedMatches;
  }

  /**
   * Returns the attribute matches stored in the cache.
   * For efficiency reasons, only one instance of each attribute with the same name and type is stored in the cache.
   * Any pair of attributes with the same name and type will map to the same score.
   * For a separate entry for each pair of attributes, use the method with parameters.
   * @return CachedMatch containing the attribute matches
   */
  public CachedMatch<ASTCDAttribute> getAttributeMatches() {
    CachedMatch<ASTCDAttribute> unwrappedMatches = new CachedMatch<>();
    attributeMatches.getMatches().forEach(
      (keyPair, value) -> unwrappedMatches.putMatch(keyPair.a.getAttribute(), keyPair.b.getAttribute(), value)
    );
    return unwrappedMatches;
  }

  public CachedMatch<ASTCDAssociation> getAssocMatches() { return assocMatches; }

  public CachedMatch<ASTCDMethod> getMethodMatches() { return methodMatches; }

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

  public void clearIteration() {
    attributeMatches.clear();
    assocMatches.clear();
    methodMatches.clear();
    resetBiggestChange();
  }

}
