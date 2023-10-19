package de.monticore.cddiff.syndiff.datastructures;

import de.monticore.cdbasis._ast.ASTCDClass;
import edu.mit.csail.sdg.alloy4.Pair;
import java.util.List;

/**
 * Data structure for inheritance. This class is used to check what comparisons must be done based on the changes in the inheritance.
 * For example, if a relation to a superclass is deleted, the attributes and associations (ingoing and outgoing) from this superClass are still contained in the astcdClass in the srcCD (astcdClass.a).
 */
public class InheritanceDiff {
  private Pair<ASTCDClass, ASTCDClass> astcdClasses;
  private List<ASTCDClass> deletedSuperClasses;
  private List<ASTCDClass> newSuperClasses;

  public InheritanceDiff(Pair<ASTCDClass, ASTCDClass> astcdClasses) {
    this.astcdClasses = astcdClasses;
  }

  public Pair<ASTCDClass, ASTCDClass> getAstcdClasses() {
    return astcdClasses;
  }

  public void setAstcdClasses(Pair<ASTCDClass, ASTCDClass> astcdClasses) {
    this.astcdClasses = astcdClasses;
  }

  public List<ASTCDClass> getDeletedSuperClasses() {
    return deletedSuperClasses;
  }

  public void setDeletedSuperClasses(List<ASTCDClass> deletedSuperClasses) {
    this.deletedSuperClasses = deletedSuperClasses;
  }

  public List<ASTCDClass> getNewSuperClasses() {
    return newSuperClasses;
  }

  public void setNewSuperClasses(List<ASTCDClass> newSuperClasses) {
    this.newSuperClasses = newSuperClasses;
  }
}
