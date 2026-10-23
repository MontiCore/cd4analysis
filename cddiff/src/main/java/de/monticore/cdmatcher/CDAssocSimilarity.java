/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmatcher;

import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import java.util.*;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.TypeCheck3;
import org.antlr.v4.runtime.misc.Triple;

public class CDAssocSimilarity implements CDSimilarity<ASTCDAssociation> {
  
  protected Set<Triple<ASTCDType, ASTCDType, Double>> typeSimilaritySet;
  
  public CDAssocSimilarity(Set<Triple<ASTCDType, ASTCDType, Double>> typeSimilaritySet) {
    this.typeSimilaritySet = typeSimilaritySet;
  }
  
  @Override
  public Double computeWeight(ASTCDAssociation srcElem, ASTCDAssociation tgtElem) {
    double score = 0.0;
    
    /*
     * We determine the maximum score for a straight and a reverse match.
     * The straight match is scored slightly more.
     */
    score += Double.max(computeSideScore(srcElem.getLeft(), tgtElem.getLeft()) + computeSideScore(
        srcElem.getRight(), tgtElem.getRight()) + 0.01, computeSideScore(srcElem.getLeft(), tgtElem
            .getRight()) + computeSideScore(srcElem.getRight(), tgtElem.getLeft()));
    
    /*
     * A match of the association name is weighted more than a role name,
     * but less than two role-names.
     */
    if (srcElem.isPresentName() && tgtElem.isPresentName() && srcElem.getName().equals(tgtElem
        .getName())) {
      score += 1.5;
    }
    
    return score;
  }
  
  /**
   * Determines the similarity score of srcSide to tgtSide
   *
   * @param srcSide AssocSide of the src-association
   * @param tgtSide AssocSide of the tgt-association
   * @return similarity-score as a double
   */
  protected double computeSideScore(ASTCDAssocSide srcSide, ASTCDAssocSide tgtSide) {
    double score = 0.0;
    
    /*
     * First we need to determine the score of the type-match.
     * A type-match is weighted less than a role-name match as we assume that
     * the associations are already filtered according to a best-match of types
     * as well as their sub- and supertypes.
     */
    
    SymTypeExpression symTypesrcTSymbol = TypeCheck3.symTypeFromAST(srcSide.getMCQualifiedType());
    SymTypeExpression symTypetgtTSymbol = TypeCheck3.symTypeFromAST(tgtSide.getMCQualifiedType());
    
    if (!symTypesrcTSymbol.isObscureType() && !symTypetgtTSymbol.isObscureType()
        && symTypesrcTSymbol.getSourceInfo().getSourceSymbol().isPresent() && symTypetgtTSymbol
            .getSourceInfo().getSourceSymbol().isPresent()) {
      if (symTypesrcTSymbol.getSourceInfo().getSourceSymbol().get() instanceof CDTypeSymbol
          && symTypetgtTSymbol.getSourceInfo().getSourceSymbol().get() instanceof CDTypeSymbol) {
        ASTCDType srcType = ((CDTypeSymbol) symTypesrcTSymbol.getSourceInfo().getSourceSymbol()
            .get()).getAstNode();
        ASTCDType tgtType = ((CDTypeSymbol) symTypetgtTSymbol.getSourceInfo().getSourceSymbol()
            .get()).getAstNode();
        
        // Is there a better way to do this?
        Optional<Triple<ASTCDType, ASTCDType, Double>> entry = typeSimilaritySet.stream().filter(
            t -> t.a.equals(srcType) && t.b.equals(tgtType)).findFirst();
        
        if (entry.isPresent()) {
          // We scale the score down to max 1.02.
          score += Double.min(entry.get().c, 1.02);
        }
      }
      
    }
    else {
      // If we cannot use getDefiningSymbol(), we instead match via q-name
      Optional<Triple<ASTCDType, ASTCDType, Double>> entry = typeSimilaritySet.stream().filter(
          t -> t.a.getSymbol().getInternalQualifiedName().contains(srcSide.getMCQualifiedType()
              .getMCQualifiedName().getQName()) && t.b.getSymbol().getInternalQualifiedName()
                  .contains(tgtSide.getMCQualifiedType().getMCQualifiedName().getQName())).max(
                      Comparator.comparingDouble(e -> e.c));
      
      // If the type still cannot be resolved, we check if the q-names match
      if (entry.isPresent()) {
        // We scale the score down to max 1.0.
        score += Double.min(entry.get().c, 1.0);
      }
      else if (srcSide.getMCQualifiedType().getMCQualifiedName().getQName().equals(tgtSide
          .getMCQualifiedType().getMCQualifiedName().getQName())) {
        // We scale the score down to max 0.98.
        score += 0.98;
      }
    }
    
    if (srcSide.isPresentCDRole() && tgtSide.isPresentCDRole() && srcSide.getName().equals(tgtSide
        .getName())) {
      score += 1.1;
    }
    
    return score;
  }
  
}
