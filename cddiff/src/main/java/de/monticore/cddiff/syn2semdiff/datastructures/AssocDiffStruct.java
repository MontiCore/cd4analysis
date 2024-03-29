package de.monticore.cddiff.syn2semdiff.datastructures;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._ast.ASTCDRole;
import de.monticore.cdbasis._ast.ASTCDType;
import edu.mit.csail.sdg.alloy4.Pair;
import java.util.List;

/**
 * Data structure for preparing the differences related to changed associations. The corresponding
 * attributes are set if they lead to a semantic difference. Otherwise, they are null or false.
 */
public class AssocDiffStruct {
  private ASTCDAssociation association;
  private List<Pair<ClassSide, Integer>> changedCard = null;
  private List<Pair<ClassSide, ASTCDRole>> changedRoleNames = null;
  private ASTCDType changedTgt = null;
  private ASTCDType changedSrc = null;
  private boolean changedDir = false;

  public AssocDiffStruct() {}

  public List<Pair<ClassSide, Integer>> getChangedCard() {
    return changedCard;
  }

  public void setChangedCard(List<Pair<ClassSide, Integer>> changedCard) {
    this.changedCard = changedCard;
  }

  public List<Pair<ClassSide, ASTCDRole>> getChangedRoleNames() {
    return changedRoleNames;
  }

  public void setChangedRoleNames(List<Pair<ClassSide, ASTCDRole>> changedRoleNames) {
    this.changedRoleNames = changedRoleNames;
  }

  public ASTCDType getChangedTgt() {
    return changedTgt;
  }

  public void setChangedTgt(ASTCDType changedTgt) {
    this.changedTgt = changedTgt;
  }

  public boolean isChangedDir() {
    return changedDir;
  }

  public void setChangedDir(boolean changedDir) {
    this.changedDir = changedDir;
  }

  public ASTCDAssociation getAssociation() {
    return association;
  }

  public void setAssociation(ASTCDAssociation association) {
    this.association = association;
  }

  public ASTCDType getChangedSrc() {
    return changedSrc;
  }

  public void setChangedSrc(ASTCDType changedSrc) {
    this.changedSrc = changedSrc;
  }
}
