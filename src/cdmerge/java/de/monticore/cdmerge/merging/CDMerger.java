package de.monticore.cdmerge.merging;

import de.monticore.cd4analysis._ast.ASTCD4AnalysisNode;
import de.monticore.cdbasis._ast.ASTCDBasisNode;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdmerge.log.ErrorLevel;
import de.monticore.cdmerge.log.MergePhase;
import de.monticore.cdmerge.matching.matchresult.CDMatch;
import de.monticore.cdmerge.merging.mergeresult.MergeBlackBoard;
import de.monticore.cdmerge.merging.strategies.AssociationMerger;
import de.monticore.cdmerge.merging.strategies.TypeMerger;

/**
 * Abstract base class for concrete CD merging strategies
 */
public abstract class CDMerger {

  private TypeMerger typeMerger;

  private AssociationMerger associationMerger;

  private MergeBlackBoard mergeBlackBoard;

  public CDMerger(MergeBlackBoard blackBoard, TypeMerger typeMerger,
      AssociationMerger associationMerger) {
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

    if (mergeBlackBoard.getConfig().mergeComments()) {
      mergeBlackBoard.addLog(ErrorLevel.FINE, "Merging CD Comments", MergePhase.CD_MERGING, cd1,
          cd2);
      mergeComments(cd1, cd2, mergeBlackBoard.getIntermediateMergedCD());
    }

    mergeBlackBoard.addLog(ErrorLevel.FINE, "Merging type declarations and attributes",
        MergePhase.TYPE_MERGING, cd1, cd2);

    mergeTypes(cd1, cd2, matchResult);

    mergeBlackBoard.addLog(ErrorLevel.FINE, "Merging associations", MergePhase.ASSOCIATION_MERGING,
        cd1, cd2);
    mergeAssociations(cd1, cd2, matchResult);
  }

  protected void mergeTypes(ASTCDDefinition cd1, ASTCDDefinition cd2, CDMatch matchResult) {
    getTypeMerger().mergeTypes(cd1, cd2, matchResult);
  }

  protected void mergeAssociations(ASTCDDefinition cd1, ASTCDDefinition cd2, CDMatch matchResult) {
    getAssociationMerger().mergeAssociations(cd1, cd2, matchResult);
  }

  protected abstract void mergeComments(ASTCD4AnalysisNode left, ASTCD4AnalysisNode right,
      ASTCD4AnalysisNode merged);

  protected abstract void mergeComments(ASTCDBasisNode left, ASTCDBasisNode right,
      ASTCDBasisNode merged);

}
