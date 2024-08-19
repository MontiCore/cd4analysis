/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.merging;

import de.monticore.cd4code._ast.ASTCD4CodeNode;
import de.monticore.cdbasis._ast.ASTCDBasisNode;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdmerge.log.ErrorLevel;
import de.monticore.cdmerge.log.MergePhase;
import de.monticore.cdmerge.matching.matchresult.CDMatch;
import de.monticore.cdmerge.merging.mergeresult.MergeBlackBoard;
import de.monticore.cdmerge.merging.strategies.AssociationMerger;
import de.monticore.cdmerge.merging.strategies.TypeMerger;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.monticore.umlstereotype._ast.ASTStereotype;
import java.util.Optional;

/** Abstract base class for concrete CD merging strategies */
public abstract class CDMerger {

  private TypeMerger typeMerger;

  private AssociationMerger associationMerger;

  private MergeBlackBoard mergeBlackBoard;

  public CDMerger(
      MergeBlackBoard blackBoard, TypeMerger typeMerger, AssociationMerger associationMerger) {
    this.typeMerger = typeMerger;
    this.associationMerger = associationMerger;
    this.mergeBlackBoard = blackBoard;
  }

  protected AssociationMerger getAssociationMerger() {
    return associationMerger;
  }

  protected TypeMerger getTypeMerger() {
    return typeMerger;
  }

  public void mergeCDs(ASTCDDefinition cd1, ASTCDDefinition cd2, CDMatch matchResult) {

    mergeStereotypes(cd1.getModifier(), cd2.getModifier())
        .ifPresent(
            st ->
                mergeBlackBoard
                    .getIntermediateMergedCD()
                    .getCDDefinition()
                    .getModifier()
                    .setStereotype(st));

    if (mergeBlackBoard.getConfig().mergeComments()) {
      mergeBlackBoard.addLog(
          ErrorLevel.FINE, "Merging CD Comments", MergePhase.CD_MERGING, cd1, cd2);
      mergeComments(cd1, cd2, mergeBlackBoard.getIntermediateMergedCD());
    }

    mergeBlackBoard.addLog(
        ErrorLevel.FINE,
        "Merging type declarations and attributes",
        MergePhase.TYPE_MERGING,
        cd1,
        cd2);

    mergeTypes(cd1, cd2, matchResult);

    mergeBlackBoard.addLog(
        ErrorLevel.FINE, "Merging associations", MergePhase.ASSOCIATION_MERGING, cd1, cd2);
    mergeAssociations(cd1, cd2, matchResult);
  }

  private Optional<ASTStereotype> mergeStereotypes(ASTModifier modifier1, ASTModifier modifier2) {

    Optional<ASTStereotype> stereotype = Optional.empty();

    // STEREOTYPES
    if (modifier1.isPresentStereotype()) {
      stereotype = Optional.of(modifier1.getStereotype().deepClone());
      if (modifier2.isPresentStereotype()) {
        if (stereotype.get().getValuesList().stream()
            .anyMatch(
                sv1 ->
                    modifier2.getStereotype().getValuesList().stream()
                        .anyMatch(
                            sv2 ->
                                sv1.getName().equals(sv2.getName())
                                    && !sv1.getValue().equals(sv2.getValue())))) {
          return Optional.empty();
        }
        stereotype.get().addAllValues(modifier2.getStereotype().getValuesList());
      }
    } else if (modifier2.isPresentStereotype()) {
      stereotype = Optional.of(modifier2.getStereotype().deepClone());
    }
    return stereotype;
  }

  protected void mergeTypes(ASTCDDefinition cd1, ASTCDDefinition cd2, CDMatch matchResult) {
    getTypeMerger().mergeTypes(cd1, cd2, matchResult);
  }

  protected void mergeAssociations(ASTCDDefinition cd1, ASTCDDefinition cd2, CDMatch matchResult) {
    getAssociationMerger().mergeAssociations(cd1, cd2, matchResult);
  }

  protected abstract void mergeComments(
      ASTCD4CodeNode left, ASTCD4CodeNode right, ASTCD4CodeNode merged);

  protected abstract void mergeComments(
      ASTCDBasisNode left, ASTCDBasisNode right, ASTCDBasisNode merged);
}
