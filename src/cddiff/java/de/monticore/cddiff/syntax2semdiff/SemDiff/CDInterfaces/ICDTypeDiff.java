package de.monticore.cddiff.syntax2semdiff.SemDiff.CDInterfaces;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cddiff.syntax2semdiff.SemDiff.CDImplementations.CDMemberDiff;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import edu.mit.csail.sdg.alloy4.Pair;

import java.util.List;

public interface ICDTypeDiff {
  List<CDMemberDiff> getChangedMembers();
  List<ASTCDAttribute> getAddedAttributes();
  List<ASTCDAttribute> getDeletedAttribute();
  List<ASTCDEnumConstant> getAddedConstants();
  List<ASTCDEnumConstant> getDeletedConstants();

  List<Pair<ASTCDAttribute, ASTCDAttribute>> getMatchedAttributes();
  List<Pair<ASTCDEnumConstant, ASTCDEnumConstant>> getMatchedConstants();

  /**
   * Check if attributes (added or deleted) change the semantic of a class.
   * @return true if attribute can't be instantiated.
   * This function is similar to the one for added classes.
   */
  void changedAttribute();

  /**
   * Get all classes that use the ASTCDEnum as an attribute.
   * We use this function when the typeDiff is between enums.
   * @return list of those classes.
   */
  List<ASTCDClass> getClassesForEnum();

  /**
   * Check the difference in the modifier of the classes.
   * It must also save the information for building the object diagrams.
   * @return A String with the old and new roles.
   */
  String sterDiff();

  /**
   * Check the difference in the attribute types of the classes.
   * It must also save the information for building the object diagrams.
   * @return A String with the old and new types.
   */
  String attDiff();
}
