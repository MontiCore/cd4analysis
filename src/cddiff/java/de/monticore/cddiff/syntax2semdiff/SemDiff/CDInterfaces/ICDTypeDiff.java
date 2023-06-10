package de.monticore.cddiff.syntax2semdiff.SemDiff.CDInterfaces;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cddiff.syntax2semdiff.SemDiff.CDInterfacesNew.ACDMemberDiff;
import de.monticore.cddiff.syntax2semdiff.SemDiff.CDInterfacesNew.ACDTypeDiff;
import de.monticore.cddiff.syntax2semdiff.SemDiff.DataStructure;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.umlmodifier._ast.ASTModifier;
import edu.mit.csail.sdg.alloy4.Pair;

import java.util.List;

public interface ICDTypeDiff {
  List<ACDMemberDiff> getChangedMembers();
  List<ACDMemberDiff> getChangedModifier();
  //changed type
  List<ACDMemberDiff> getChangedTypes();
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
   * Get all classes with an enum that containt the new constant.
   * @param astcdEnum
   * @return list of those classes.
   * This function is similar to the one for added/deleted Enum-classes.
   */
  List<ASTCDClass> getClassesForEnum(ASTCDEnum astcdEnum);

  /**
   * Check if an attribute with a changed modifier is in some inheritance structures
   * @param attribute
   * @return true if it is found in the other class diagram
   * Subfunctions: comparing attributes, computing inheritance hierarchy
   */
  boolean isContained(ASTCDAttribute attribute);
}
