package de.monticore.cdmatcher;

import de.monticore.cd4code.CD4CodeMill;
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

    //fixme: assoc matches are bad

    score += Double.max(
      computeSideScore(srcElem.getLeft(),tgtElem.getLeft())
        + computeSideScore(srcElem.getRight(),tgtElem.getRight())+0.1,
      computeSideScore(srcElem.getLeft(),tgtElem.getRight())
        + computeSideScore(srcElem.getRight(),tgtElem.getLeft()));

    if (srcElem.isPresentName() && tgtElem.isPresentName() && srcElem.getName().equals(tgtElem.getName())){
      score++;
    }

    //System.out.println("[ASSOC MATCH]: " + CD4CodeMill.prettyPrint(srcElem,false) + " [WITH] " +  CD4CodeMill.prettyPrint(srcElem,true) + " : " +score);


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
        /*
        System.out.println("[HERE1]: " + entry.get().a.getName()
          + " ==> " + entry.get().b.getName()
          + " : " + entry.get().c
        );
         */
      }

    } else {
      Optional<Triple<ASTCDType,ASTCDType,Double>> entry = typeSimilaritySet.stream()
        .filter(t ->
          t.a.getSymbol().getInternalQualifiedName().contains(
            srcSide.getMCQualifiedType().getMCQualifiedName().getQName()
          )
            && t.b.getSymbol().getInternalQualifiedName().contains(
              tgtSide.getMCQualifiedType().getMCQualifiedName().getQName()
          ))
        .findFirst();

      if (entry.isPresent()) {
        score += entry.get().c;
        /*
        System.out.println("[HERE2]: " + entry.get().a.getName()
        + " ==> " + entry.get().b.getName()
          + " : " + entry.get().c
        );

         */
      } else if (srcSide.getMCQualifiedType().getMCQualifiedName().getQName().equals(
        tgtSide.getMCQualifiedType().getMCQualifiedName().getQName())) {
        assert false;
        score++;
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
