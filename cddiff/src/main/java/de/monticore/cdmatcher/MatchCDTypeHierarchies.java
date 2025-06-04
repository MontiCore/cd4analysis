/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmatcher;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cddiff.CDDiffUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Matches a type to all sub- and supertypes of any other type that is matched via an existing type
 * matching.
 */
public class MatchCDTypeHierarchies implements MatchingStrategy<ASTCDType> {
  
  protected MatchingStrategy<ASTCDType> typeMatcher;
  protected Set<ASTCDType> srcTypes;
  protected Set<ASTCDType> tgtTypes;
  protected Map<ASTCDType, Set<ASTCDType>> srcCDType2Hierarchy = new HashMap<>();
  protected Map<ASTCDType, Set<ASTCDType>> tgtCDType2Hierarchy = new HashMap<>();
  
  public MatchCDTypeHierarchies(MatchingStrategy<ASTCDType> typeMatcher, ASTCDCompilationUnit srcCD,
      ASTCDCompilationUnit tgtCD) {
    this.typeMatcher = typeMatcher;
    
    srcTypes = CDDiffUtil.getAllTypesFromCD(srcCD);
    tgtTypes = CDDiffUtil.getAllTypesFromCD(tgtCD);
    
    srcTypes.forEach(srcType -> srcCDType2Hierarchy.put(srcType, CDDiffUtil.getAllSuperTypes(
        srcType)));
    
    for (ASTCDType srcType : srcTypes) {
      for (ASTCDType superType : srcCDType2Hierarchy.get(srcType)) {
        if (!srcTypes.contains(superType)) {
          System.out.println(superType.getSymbol().getFullName());
        }
        srcCDType2Hierarchy.get(superType).add(srcType);
      }
    }
    
    tgtTypes.forEach(tgtType -> tgtCDType2Hierarchy.put(tgtType, CDDiffUtil.getAllSuperTypes(
        tgtType)));
    
    for (ASTCDType tgtType : tgtTypes) {
      for (ASTCDType superType : tgtCDType2Hierarchy.get(tgtType)) {
        tgtCDType2Hierarchy.get(superType).add(tgtType);
      }
    }
  }
  
  @Override
  public List<ASTCDType> getMatchedElements(ASTCDType srcElem) {
    return tgtTypes.stream().filter(tgtElem -> isMatched(srcElem, tgtElem)).collect(Collectors
        .toList());
  }
  
  @Override
  public boolean isMatched(ASTCDType srcElem, ASTCDType tgtElem) {
    if (srcCDType2Hierarchy.containsKey(srcElem) && tgtCDType2Hierarchy.containsKey(tgtElem)) {
      return srcCDType2Hierarchy.get(srcElem).stream().anyMatch(srcType -> tgtCDType2Hierarchy.get(
          tgtElem).stream().anyMatch(tgtType -> typeMatcher.isMatched(srcType, tgtType)));
    }
    return false;
  }
  
}
