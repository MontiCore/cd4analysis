package de.monticore.cddiff.syntax2semdiff.SemDiff.CDInterfaces;

import com.google.common.collect.ArrayListMultimap;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.syntax2semdiff.SemDiff.DataStructure;
import edu.mit.csail.sdg.alloy4.Pair;

import java.util.List;

public interface ICDAssocDiff {



//  /**
//   *
//   * Based on the overlappingAssociations update the multymap with subclasses for each association.
//   * Search for a full matching between the subclasses in cd1 and cd2.
//   * If none is found, a semantic difference exists.
//   * This function must be used before handling association
//   * difference - possible inconsistent output.
//   */
//  void createSubClass();

//  /**
//   * Get the difference of two classes from cd1 and cd2.
//   * This function will be used for all differences - multiplicity, direction..
//   * @param target
//   * @param source
//   * @return list of pairs - association and its range.
//   * Subfunctions: search for association(s) with a specific role name,
//   * matching of ranges and role names, comparison of directions and more
//   */
//  List<Pair> findDiff(ASTCDClass target, ASTCDClass source);

  /**
   * Compare ranges and attributes between classes that are using the
   * @param astcdAssociation as an inheritance association(deleted or added).
   * @return list of pairs of found differences.
   */
  List<DataStructure.DiffPair> compareAttAndRelations(ASTCDAssociation astcdAssociation);

//  /**
//   * Compute all ranges for all classes.
//   * @param astcdCompilationUnit
//   * @return multymap with list of pairs of associations and ranges for each class.
//   * Subfunctions: ConstraintSolver, add inheritance relations to subclasses, duplicated associations
//   */
//  ArrayListMultimap computeAllRanges(ASTCDCompilationUnit astcdCompilationUnit);

  /**
   * Get the two classes that are connected via an association.
   * @param astcdCompilationUnit
   * @param astcdAssociation
   * @return pair of two classes.
   */
  Pair<ASTCDClass, ASTCDClass> getConnectedClasses(ASTCDCompilationUnit astcdCompilationUnit, ASTCDAssociation astcdAssociation);


}
