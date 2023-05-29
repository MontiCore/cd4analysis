package de.monticore.cddiff.syntax2semdiff.SemDiff;

import com.google.common.collect.ArrayListMultimap;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static de.monticore.cddiff.ow2cw.CDAssociationHelper.matchRoleNames;

/*
 * A function to transform association into matrix must be implemented.
 * A function with a suitable datastructure for getting the values from the matrix needs to be implemented.
 * */
public class ConstraintSolver {
/*
  private ASTCDAssociation mergeAssociations(List<ASTCDAssociation> astcdAssociationList, ASTCDCompilationUnit astcdCompilationUnit) {
    List<ASTCDAssociation> assocsToMerge = new ArrayList<>();
    for (ASTCDAssociation astcdAssociation : astcdAssociationList) {
      for (ASTCDAssociation astcdAssociation1 : astcdAssociationList) {
        if (!astcdAssociation.equals(astcdAssociation1) & matchRoleNames(astcdAssociation.getLeft(), astcdAssociation1.getLeft()) & matchRoleNames(astcdAssociation
          .getRight(), astcdAssociation1.getRight()) & astcdCompilationUnit.getEnclosingScope().resolveDiagramDown(astcdAssociation.getLeftQualifiedName().getQName()).equals(astcdCompilationUnit.getEnclosingScope().resolveDiagramDown(astcdAssociation1.getLeftQualifiedName().getQName()))) {

        }
      }
    }
  }
*/
private List<List<Integer>> mergeAssociations(ASTCDCompilationUnit astcdCompilationUnit) {
  ArrayListMultimap<ASTCDAssociation, ASTCDAssociation> map = ArrayListMultimap.create();
  List<List<Integer>> forMatrix = new ArrayList<>();
  for (ASTCDAssociation astcdAssociation : astcdCompilationUnit.getCDDefinition().getCDAssociationsList()) {
    map.put(astcdAssociation, null);
    for (ASTCDAssociation astcdAssociation1 : astcdCompilationUnit.getCDDefinition().getCDAssociationsList()) {
      if (!astcdAssociation.equals(astcdAssociation1) && matchRoleNames(astcdAssociation.getLeft(), astcdAssociation1.getLeft()) && matchRoleNames(astcdAssociation
        .getRight(), astcdAssociation1.getRight()) &&
        astcdCompilationUnit.getEnclosingScope().resolveDiagramDown(astcdAssociation.getLeftQualifiedName().getQName()).equals(astcdCompilationUnit.getEnclosingScope().resolveDiagramDown(astcdAssociation1.getLeftQualifiedName().getQName()))
        && astcdCompilationUnit.getEnclosingScope().resolveDiagramDown(astcdAssociation.getLeftQualifiedName().getQName()).equals(astcdCompilationUnit.getEnclosingScope().resolveDiagramDown(astcdAssociation1.getLeftQualifiedName().getQName()))) {
        map.put(astcdAssociation, astcdAssociation1);

      }
    }
  }
  for (ASTCDAssociation astcdAssociation : map.keySet()) {
    List<Integer> innerListLeft = new ArrayList<>();
    innerListLeft.add(astcdAssociation.getLeft().getCDCardinality().getLowerBound());
    innerListLeft.add(astcdAssociation.getLeft().getCDCardinality().getUpperBound());
    List<Integer> innerListRight = new ArrayList<>();
    innerListRight.add(astcdAssociation.getRight().getCDCardinality().getLowerBound());
    innerListRight.add(astcdAssociation.getRight().getCDCardinality().getUpperBound());
    forMatrix.add(innerListLeft);
    forMatrix.add(innerListRight);
    for (ASTCDAssociation astcdAssociation1 : map.get(astcdAssociation)) {
      innerListLeft.add(astcdAssociation1.getLeft().getCDCardinality().getLowerBound());
      innerListLeft.add(astcdAssociation1.getLeft().getCDCardinality().getUpperBound());
      innerListRight.add(astcdAssociation1.getRight().getCDCardinality().getLowerBound());
      innerListRight.add(astcdAssociation1.getRight().getCDCardinality().getUpperBound());
    }
  }
  return forMatrix;
}
/*
  private ArrayListMultimap computeRanges(ASTCDCompilationUnit astcdCompilationUnit) {
    ArrayListMultimap arrayListMultimap = ArrayListMultimap.create();
    for (ASTCDClass astcdClass : astcdCompilationUnit.getCDDefinition().getCDClassesList()) {
      for (ASTCDAssociation astcdAssociation : astcdCompilationUnit.getCDDefinition().getCDAssociationsListForType(astcdClass)) {

      }
    }
    return arrayListMultimap;
  }

*/
private static void toMatrix(List<List<Integer>> ranges) {

  int evenCount = (int) ranges.stream()
    .flatMapToInt(innerList -> IntStream.range(0, innerList.size()))
    .filter(i -> i % 2 == 0)
    .count();
  int numCols = ranges.size();

  int[][] A = new int[evenCount][numCols];
  int[][] B = new int[evenCount][numCols];
  int[] a = new int[numCols];
  int[] b = new int[numCols];

  int index = 0;
  for (int i = 0; i < numCols; i++) {
    List<Integer> range = ranges.get(i);
    int k = 0;
    for (int j = 0; i < range.size(); i += 2) {
      Optional<Integer> optionalValue = Optional.ofNullable(range.get(i));
      if (optionalValue.isPresent()) {
        Integer value = optionalValue.get();
        a[index+k] = value;
        A[index+k][i] = 1;
        k++;
      }
    }
    k = 0;
    for (int j = 1; i < range.size(); i += 2) {
      Optional<Integer> optionalValue = Optional.ofNullable(range.get(i));
      if (optionalValue.isPresent()) {
        Integer value = optionalValue.get();
        b[index+k] = value;
        B[index+k][i] = 1;
        k++;
      }
    }
    k = 0;

    index += range.size();
  }
}

  /*
   * Transform a system of inequalities Ax<=b
   * to a system of equalities Ax=b.
   * We use slack variables that represent the difference between both types.
   * The function returns a matrix with its vector [B|c]
   * */
  private static int[][] transformInequalitiesToEqualities(int[][] A, int[][] B, int[] a, int[] b) {
    int numRowsA = A.length;
    int numColsA = A[0].length;
    int numRowsB = B.length;
    int numColsB = B[0].length;

    int numSlackVariables = numRowsA + numRowsB;
    int numVariables = numColsA + numColsB + numSlackVariables;

    int[][] equalities = new int[numRowsA + numRowsB][numVariables + 1];

    for (int i = 0; i < numRowsA; i++) {
      System.arraycopy(A[i], 0, equalities[i], 0, numColsA);
      equalities[i][numColsA + i] = 1;  // Slack variable
      equalities[i][numVariables] = a[i];
    }

    for (int i = 0; i < numRowsB; i++) {
      System.arraycopy(B[i], 0, equalities[numRowsA + i], numColsA, numColsB);
      equalities[numRowsA + i][numColsA + numSlackVariables + i] = 1;  // Slack variable
      equalities[numRowsA + i][numVariables] = b[i];
    }

    return equalities;
  }
  /*
  private static int[][] toEqualityM(int[][] A, int[] b){
    int m = A.length;
    int n = A[0].length;

    // create a new matrix with slack variables
    int[][] B = new int[m][n + m];
    for (int i = 0; i < m; i++) {
      for (int j = 0; j < n; j++) {
        B[i][j] = A[i][j];
      }
      B[i][n + i] = 1;
    }

    // create a new right-hand side vector with zeros and slack variables
    int[] c = new int[n + m];
    for (int i = 0; i < n; i++) {
      c[i] = 0;
    }
    for (int i = 0; i < m; i++) {
      c[n + i] = b[i];
    }

    int[][] solution = new int[B.length][B[0].length+1];
    for (int i = 0; i < B.length; i++) {
      for (int j = 0; j < B[0].length; j++) {
        solution[i][j] = B[i][j];
      }
      solution[i][B[0].length] = b[i];
    }

    return solution;
  }
    private static int[][] toEqualityM(int[][] A, int[] b, int[][] B, int[] c) {
    int numRowsA = A.length;
    int numColsA = A[0].length;
    int numRowsB = B.length;
    int numColsB = B[0].length;

    // create a new matrix with slack and surplus variables
    int[][] C = new int[numRowsA + numRowsB][numColsA + numColsB];
    for (int i = 0; i < numRowsA; i++) {
        for (int j = 0; j < numColsA; j++) {
            C[i][j] = A[i][j];
        }
        for (int j = 0; j < numColsB; j++) {
            C[i][numColsA + j] = 0; // Slack variables
        }
    }
    for (int i = 0; i < numRowsB; i++) {
        for (int j = 0; j < numColsA; j++) {
            C[numRowsA + i][j] = 0; // Surplus variables
        }
        for (int j = 0; j < numColsB; j++) {
            C[numRowsA + i][numColsA + j] = -B[i][j]; // Surplus variables with negation
        }
    }

    // create a new right-hand side vector with slack and surplus variables
    int[] v = new int[numRowsA + numRowsB];
    for (int i = 0; i < numRowsA; i++) {
        v[i] = b[i];
    }
    for (int i = 0; i < numRowsB; i++) {
        v[numRowsA + i] = -c[i]; // Surplus variables with negation
    }

    int[][] solution = new int[C.length][C[0].length + 1];
    for (int i = 0; i < C.length; i++) {
        for (int j = 0; j < C[0].length; j++) {
            solution[i][j] = C[i][j];
        }
        solution[i][C[0].length] = v[i];
    }

    return solution;
}
    */
  private void defineRanges(int[][] coefficients, int[] constants){
    int[] solution = solve(coefficients, constants);
    int[][] inverted = inverse(coefficients);
    for (int i = 0; i < solution.length; i++) {
      int min = (int)Math.ceil(solution[i]);
      int max = Integer.MAX_VALUE;
      for (int j = 0; j < inverted[i].length; j++) {
        if (inverted[i][j] > 0) {
          max = Math.min(max, (int)Math.floor((double)constants[j] / inverted[i][j]));
        }
      }
      System.out.println("Variable " + (i+1) + " has a range of positive integer values from " +
        min + " to " + max);
    }
  }

  /*
   * Gaussian elimination for Ax=b with partial pivoting
   * where A is a matrix and b is a vector.
   * http://www.math.sjsu.edu/~foster/m143m/gaussian_elimination_algorithms_4.pdf
   */
  private static int[] solve(int[][] A, int[] b) {
    int pivotRow = -1;
    int pivotCol = -1;
    int[] solution = new int[A[0].length];
    boolean[] used = new boolean[A[0].length];//a pivot element can be used only once

    for (int i = 0; i < A.length; i++) {
      for (int j = 0; j < A[0].length; j++) {
        //find the biggest element in the row
        if (!used[j] && A[i][j] > 0 && (pivotCol == -1 || A[i][j] * A[pivotRow][pivotCol] > A[pivotRow][j] * A[i][pivotCol])) {
          pivotRow = i;
          pivotCol = j;
        }
      }

      //No pivot element found - no solution
      if (pivotCol == -1) {
        throw new RuntimeException();
      }

      used[pivotCol] = true;

      int pivotValue = A[pivotRow][pivotCol];
      for (int j = 0; j < A[0].length; j++) {
        if (!used[j]) {
          int factor = A[j][pivotCol] / pivotValue;//number for each underlying element in the pivot row
          for (int k = 0; k < A.length; k++) {
            A[j][k] = A[j][k] - factor * A[pivotRow][k];
          }
          b[j] = b[j] - factor * b[pivotRow];
        }
      }

      solution[pivotCol] = b[pivotRow] / pivotValue;
    }

    return solution;
  }

  /* Computes the inverse of a square matrix with non-negative integers using Gaussian elimination
   * https://code.bcanotesnepal.com/2021/04/28/matrix-inverse-using-gauss-jordan-method-c-program/
   * */
  private static int[][] inverse(int[][] matrix) {
    int n = matrix.length;
    int[][] inverse = new int[n][n];
    int[][] augmented = new int[n][2 * n];

    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        augmented[i][j] = matrix[i][j];
        if (i == j) {
          augmented[i][j + n] = 1;//The right side is an identity matrix with the same size of A
        }
      }
    }

    // Gaussian algorithm
    for (int i = 0; i < n; i++) {

      int maxRow = i;
      //Find the biggest element in the column
      for (int j = i + 1; j < n; j++) {
        if (augmented[j][i] > augmented[maxRow][i]) {
          maxRow = j;
        }
      }

      //Can't inverse matrix
      if (augmented[maxRow][i] == 0) {
        throw new RuntimeException();
      }

      // Swap pivot row with current row
      if (maxRow != i) {
        int[] temp = augmented[i];
        augmented[i] = augmented[maxRow];
        augmented[maxRow] = temp;
      }

      // Scale current row to have 1 in pivot position
      int pivot = augmented[i][i];
      for (int j = i; j < 2 * n; j++) {
        augmented[i][j] = augmented[i][j] / pivot;
      }

      // Subtract current row from all other rows
      for (int j = 0; j < n; j++) {
        if (j != i) {
          int factor = augmented[j][i];
          for (int k = i; k < 2 * n; k++) {
            augmented[j][k] = augmented[j][k] - factor * augmented[i][k];
          }
        }
      }
    }

    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        inverse[i][j] = augmented[i][j + n];//Inverse matrix on the right side
      }
    }

    return inverse;
  }

}
