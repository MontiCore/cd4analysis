package de.monticore.cddiff.syntax2semdiff.SemDiff.CDInterfaces;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cddiff.syntax2semdiff.SemDiff.CDInterfacesNew.ACDMemberDiff;
import de.monticore.cddiff.syntax2semdiff.SemDiff.DataStructure;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.umlmodifier._ast.ASTModifier;
import edu.mit.csail.sdg.alloy4.Pair;

import java.util.List;

public interface ICDTypeDiff {
  List<ACDMemberDiff> getChangedMembers();
  List<ACDMemberDiff> getChangedModifier();
  List<ASTCDAttribute> getAddedAttributes();
  List<ASTCDAttribute> getDeletedAttribute();
  List<ASTCDEnumConstant> getAddedConstants();
  List<ASTCDEnumConstant> getDeletedConstants();


  /**
   * Check if attributes (added or deleted) change the semantic of a class.
   * @return true if attribute can't be instantiated.
   * This function is similar to the one for added classes.
   */
  void changedAttribute();

  /**
   * Find if a change of a modifier has a meaning for a diagram.
   * From abstract to non-abstract: semantic difference - class can be instantiated.
   * From non-abstract to abstract: possible semantic difference - another class uses this abstract class.
   * @return true if we have a semantic difference.
   * This function kind of uses multiple others: inheritance hierarchy, comparison of associations.
   */
  void isClassNeeded();

  /**
   * Get all classes with an enum that containt the new constant.
   * @param astcdEnum
   * @return list of those classes.
   * This function is similar to the one for added/deleted Enum-classes.
   */
  List<ASTCDClass> getClassesForEnum(ASTCDEnum astcdEnum);
}
