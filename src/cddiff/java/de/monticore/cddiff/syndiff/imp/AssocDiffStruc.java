package de.monticore.cddiff.syndiff.imp;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._ast.ASTCDRole;
import de.monticore.cdbasis._ast.ASTCDClass;
import edu.mit.csail.sdg.alloy4.Pair;

import java.util.List;
import java.util.Optional;

public class AssocDiffStruc {
  private ASTCDAssociation association;
  private List<Pair<ClassSide, Integer>> changedCard = null;
  private List<Pair<ClassSide, ASTCDRole>> changedRoleNames = null;
  private ASTCDClass changedTgt = null;
  private boolean changedDir = false;

  public AssocDiffStruc() {
  }

  public AssocDiffStruc(ASTCDAssociation association, List<Pair<ClassSide, Integer>> changedCard, List<Pair<ClassSide, ASTCDRole>> changedRoleNames, ASTCDClass changedTgt, boolean changedDir) {
    this.association = association;
    this.changedCard = changedCard;
    this.changedRoleNames = changedRoleNames;
    this.changedTgt = changedTgt;
    this.changedDir = changedDir;
  }

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

  public ASTCDClass getChangedTgt() {
    return changedTgt;
  }

  public void setChangedTgt(ASTCDClass changedTgt) {
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
}
