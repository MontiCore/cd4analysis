package de.monticore.umlcd4a.cocos.permutations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAssociation;
import de.monticore.umlcd4a.cd4analysis._ast.ASTModifier;
import de.monticore.umlcd4a.cd4analysis._ast.CD4AnalysisNodeFactory;

public class SetModifier implements
    Function<Permutation<ASTCDAssociation>, Set<Permutation<ASTCDAssociation>>> {
  
  @Override
  public Set<Permutation<ASTCDAssociation>> apply(Permutation<ASTCDAssociation> assocPermutation) {
    
    List<ASTModifier> cardinalities = createModifiers();
    
    Set<Permutation<ASTCDAssociation>> successorPermutations = Permuter.permute(
        this::setLeftModifier,
        Collections.singleton(assocPermutation), cardinalities);
    
    successorPermutations = Permuter.permute(this::setRightModifier, successorPermutations,
        cardinalities);
    
    return successorPermutations;
  }
  
  private List<ASTModifier> createModifiers() {
    List<ASTModifier> modifiers = new ArrayList<>();
    
    ASTModifier abstractMod = CD4AnalysisNodeFactory.createASTModifier();
    abstractMod.setAbstract(true);
    modifiers.add(abstractMod);
    
    ASTModifier finalMod = CD4AnalysisNodeFactory.createASTModifier();
    finalMod.setFinal(true);
    modifiers.add(finalMod);
    
    ASTModifier staticMod = CD4AnalysisNodeFactory.createASTModifier();
    staticMod.setStatic(true);
    modifiers.add(staticMod);
    
    ASTModifier derivedMod = CD4AnalysisNodeFactory.createASTModifier();
    derivedMod.setDerived(true);
    modifiers.add(derivedMod);
    
    ASTModifier privateMod = CD4AnalysisNodeFactory.createASTModifier();
    privateMod.setPrivate(true);
    modifiers.add(privateMod);
    
    ASTModifier protectedMod = CD4AnalysisNodeFactory.createASTModifier();
    protectedMod.setProtected(true);
    modifiers.add(protectedMod);
    
    ASTModifier publicMod = CD4AnalysisNodeFactory.createASTModifier();
    publicMod.setPublic(true);
    modifiers.add(publicMod);
    
    return modifiers;
  }
  
  private Permutation<ASTCDAssociation> setLeftModifier(
      Permutation<ASTCDAssociation> assocPermutation,
      ASTModifier modifier) {
    Permutation<ASTCDAssociation> copy = assocPermutation.copy();
    copy.delegate().setLeftModifier(modifier);
    return copy;
  }
  
  private Permutation<ASTCDAssociation> setRightModifier(
      Permutation<ASTCDAssociation> assocPermutation,
      ASTModifier modifier) {
    Permutation<ASTCDAssociation> copy = assocPermutation.copy();
    return copy;
  }
  
}
