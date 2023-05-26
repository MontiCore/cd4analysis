package de.monticore.cddiff.syntax2semdiff.SemDiff;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.umlmodifier._ast.ASTModifier;

import java.util.List;

public interface CDTypeDiff {
  List<DataStructure.DiffPair<ASTModifier>> getChangedModifier();
  //Changed name
  List<DataStructure.DiffPair<ASTCDAttribute>> getAddedAttributes();
  //Changed name
  List<DataStructure.DiffPair<ASTCDAttribute>> getDeletedAttributes();
  List<DataStructure.DiffPair<ASTCDEnum>> getAddedEnums();
  List<DataStructure.DiffPair<ASTCDEnum>> getDeletedEnums();


  /**
   * Check if an attribute (added or deleted) changes the semantic of a class.
   * @param attribute
   * @return true if attribute can't be instantiated.
   * This function is similar to the one for added classes.
   */
  boolean changedAttribute(ASTCDAttribute attribute);

  /**
   * Find if a change of a modifier has a meaning for a diagram.
   * From abstract to non-abstract: semantic difference - class can be instantiated.
   * From non-abstract to abstract: possible semantic difference - another class uses this abstract class.
   * @param astcdClass
   * @return true if we have a semantic difference.
   * This function kind of uses multiple others: inheritance hierarchy, comparison of associations.
   */
  boolean isClassNeeded(ASTCDClass astcdClass);

  /**
   * Get all classes with an enum that containt the new constant.
   * @param astcdEnum
   * @return list of those classes.
   * This function is similar to the one for added/deleted Enum-classes.
   */
  List<ASTCDClass> getClassesForEnum(ASTCDEnum astcdEnum);
}
