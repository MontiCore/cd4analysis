/* (c) https://github.com/MontiCore/monticore */
package de.monticore.similaritymeasure.measures;

import de.monticore.alloycddiff.alloyRunner.AlloyDiffSolution;
import de.monticore.alloycddiff.classDifference.ClassDifference;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;

import java.util.Optional;

/**
 * A similarity measure for class diagrams in cd4analysis based on the size of their semantic
 * difference solution size
 */
public class SemanticObjectSizeMeasure {
  // Parameter to specify the size of the search space
  int k;

  /**
   * Constructor for de.cddiff.similaritymeasure.measures.SemanticObjectSizeMeasure
   */
  public SemanticObjectSizeMeasure() {
    // TODO Set to better size or estimate it.
    this.k = 2;
  }

  /**
   * Constructor for de.cddiff.similaritymeasure.measures.SemanticObjectSizeMeasure
   *
   * @param k Parameter defining the maximal solution size of the class difference regarded.
   */
  public SemanticObjectSizeMeasure(int k) {
    this.k = k;
  }

  /**
   * Computes the size based difference measure between two ASTs, without regarding their order.
   *
   * @param x AST representing the left operand of cddiff_k(x,y)
   * @param y AST representing the right operand of cddiff_k(x,y)
   * @param k Solution size limit in which differences should be searched
   * @return Size based difference measurement of x and y
   */
  public double sizeDifference(ASTCDCompilationUnit x, ASTCDCompilationUnit y, int k) {
    double result = 0;

    Optional<AlloyDiffSolution> optS = ClassDifference.cddiff(x, y, k);

    if (optS.isPresent()) {
      AlloyDiffSolution S = optS.get();
      result = S.generateUniqueODs().size();
    }

    return result;
  }

  /**
   * Computes the size based difference measure between two ASTs, regarding their order.
   *
   * @param x AST which should be compared with y
   * @param y AST which should be compared with x
   * @return Size based difference measurement of x and y
   */
  public double difference(ASTCDCompilationUnit x, ASTCDCompilationUnit y) {

    double xy = sizeDifference(x, y, k);
    double yx = sizeDifference(y, x, k);
    return Math.max(xy, yx);
  }

}
