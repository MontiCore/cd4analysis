package de.monticore.cdmatcher;

import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.symboltable.ISymbol;
import org.antlr.v4.runtime.misc.Triple;

import java.util.*;

public class CDAssocSimilarity implements CDSimilarity<ASTCDAssociation>{

  protected Set<Triple<ASTCDType, ASTCDType, Double>> typeSimilaritySet;

  public CDAssocSimilarity(Set<Triple<ASTCDType, ASTCDType, Double>> typeSimilaritySet){
    this.typeSimilaritySet = typeSimilaritySet;
  }

  @Override
  public Double computeWeight(ASTCDAssociation srcElem, ASTCDAssociation tgtElem) {
    double score = 0.0;

    score += Double.max(
      computeSideScore(srcElem.getLeft(),tgtElem.getLeft())
        + computeSideScore(srcElem.getRight(),tgtElem.getRight()),
      computeSideScore(srcElem.getLeft(),tgtElem.getRight())
        + computeSideScore(srcElem.getRight(),tgtElem.getLeft()));

    if (srcElem.isPresentName() && tgtElem.isPresentName() && srcElem.getName().equals(tgtElem.getName())){
      score++;
    }

    return score;
  }

  protected double computeSideScore(ASTCDAssocSide srcSide, ASTCDAssocSide tgtSide) {
    double score = 0.0;

    Optional<ISymbol> srcTSymbol = srcSide.getMCQualifiedType().getDefiningSymbol();
    Optional<ISymbol> tgtTSymbol = tgtSide.getMCQualifiedType().getDefiningSymbol();

    if (srcTSymbol.isPresent() && srcTSymbol.get() instanceof CDTypeSymbol
      && tgtTSymbol.isPresent() && tgtTSymbol.get() instanceof CDTypeSymbol) {
      ASTCDType srcType = ((CDTypeSymbol) srcTSymbol.get()).getAstNode();
      ASTCDType tgtType = ((CDTypeSymbol) tgtTSymbol.get()).getAstNode();

      // Is there a better way to do this?
      Optional<Triple<ASTCDType,ASTCDType,Double>> entry = typeSimilaritySet.stream()
        .filter(t -> t.a.equals(srcType) && t.b.equals(tgtType))
        .findFirst();

      if (entry.isPresent()) {
        score += entry.get().c;
      }

    }

    if (srcSide.isPresentCDRole()
      && tgtSide.isPresentCDRole()
      && srcSide.getName().equals(tgtSide.getName())) {
      score += 0.5;
    }

    return score;
  }

}
