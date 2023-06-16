/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2smt.cd2smtGenerator.classStrategies;

import com.microsoft.z3.*;
import de.monticore.cd2smt.ODArtifacts.MinObject;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import java.util.Set;

public interface ClassStrategy extends ClassData {
  /***
   * Transform classes and interfaces in a class diagram in smt.
   *
   * @param ast the Model as Class Diagram.
   * @param context the SMT context.
   */
  void cd2smt(ASTCDCompilationUnit ast, Context context);

  /***
   * @param model the model produced by the SMT-Solver.
   * @param partial to produce a partial Model, some irrelevant attributes won't be produce.
   * @return set of MinObject. (object self and attributes as SMT-expressions)
   */
  Set<MinObject> smt2od(Model model, Boolean partial);
  /***
   * different Strategy to convert classes and interfaces.
   * DS: DistinctSort: each Type is converted as a new Sort.
   * SS: there is a Unique Common Sort representing all types.
   * SSCOMB: SS-Strategy Combined With an Inheritance Strategy.
   */
  enum Strategy {
    SS,
    DS,
    SSCOMB
  }
}
