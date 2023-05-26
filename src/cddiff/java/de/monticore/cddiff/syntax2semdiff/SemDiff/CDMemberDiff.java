package de.monticore.cddiff.syntax2semdiff.SemDiff;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.umlmodifier._ast.ASTModifier;

import java.util.List;

public interface CDMemberDiff {
  List<DataStructure.DiffPair<ASTModifier>> getChangedModifier();
  //changed type
  List<DataStructure.DiffPair<ASTCDType>> getChangedTypes();

  /**
   * Check if an attribute with a changed modifier is in some inheritance structures
   * @param attribute
   * @return true if it is found in the other class diagram
   * Subfunctions: comparing attributes, computing inheritance hierarchy
   */
  boolean isContained(ASTCDAttribute attribute);
}
