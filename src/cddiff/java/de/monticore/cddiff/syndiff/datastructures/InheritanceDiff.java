package de.monticore.cddiff.syndiff.datastructures;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDType;
import edu.mit.csail.sdg.alloy4.Pair;
import java.util.List;

/**
 * Data structure for inheritance. This class is used to check what comparisons must be done based on the changes in the inheritance.
 * For example, if a relation to a superclass is deleted, the attributes and associations (ingoing and outgoing) from this superClass are still contained in the astcdClass in the srcCD (astcdClass.a).
 */
public class InheritanceDiff {
  private Pair<ASTCDType, ASTCDType> astcdClasses;
  private List<ASTCDType> deletedSuperClasses;
  private List<ASTCDType> newSuperClasses;

  public InheritanceDiff(Pair<ASTCDType, ASTCDType> astcdClasses) {
    this.astcdClasses = astcdClasses;
  }

  public Pair<ASTCDType, ASTCDType> getAstcdClasses() {
    return astcdClasses;
  }

  public void setAstcdClasses(Pair<ASTCDType, ASTCDType> astcdClasses) {
    this.astcdClasses = astcdClasses;
  }

  public List<ASTCDType> getDeletedSuperClasses() {
    return deletedSuperClasses;
  }

  public void setDeletedSuperClasses(List<ASTCDType> deletedSuperClasses) {
    this.deletedSuperClasses = deletedSuperClasses;
  }

  public List<ASTCDType> getNewSuperClasses() {
    return newSuperClasses;
  }

  public void setNewSuperClasses(List<ASTCDType> newSuperClasses) {
    this.newSuperClasses = newSuperClasses;
  }
}
