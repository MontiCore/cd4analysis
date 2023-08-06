package de.monticore.cddiff.syndiff.datastructures;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import edu.mit.csail.sdg.alloy4.Pair;

import java.util.ArrayList;
import java.util.List;

public class InheritanceDiff {
  private Pair<ASTCDClass, ASTCDClass> astcdClasses;
  private List<ASTCDClass> oldDirectSuper;
  private List<ASTCDClass> newDirectSuper;

  public InheritanceDiff(Pair<ASTCDClass, ASTCDClass> astcdClasses) {
    this.astcdClasses = astcdClasses;
  }
  public Pair<ASTCDClass, ASTCDClass> getAstcdClasses() {
    return astcdClasses;
  }
  public void setAstcdClasses(Pair<ASTCDClass, ASTCDClass> astcdClasses) {
    this.astcdClasses = astcdClasses;
  }
  public List<ASTCDClass> getOldDirectSuper() {
    return oldDirectSuper;
  }
  public void setOldDirectSuper(List<ASTCDClass> oldDirectSuper) {
    this.oldDirectSuper = oldDirectSuper;
  }
  public List<ASTCDClass> getNewDirectSuper() {
    return newDirectSuper;
  }
  public void setNewDirectSuper(List<ASTCDClass> newDirectSuper) {
    this.newDirectSuper = newDirectSuper;
  }
}
