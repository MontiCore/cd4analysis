/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2smt.cd2smtGenerator.assocStrategies;

import com.microsoft.z3.Context;
import com.microsoft.z3.Model;
import de.monticore.cd2smt.ODArtifacts.SMTObject;
import de.monticore.cd2smt.cd2smtGenerator.classStrategies.ClassData;
import de.monticore.cd2smt.cd2smtGenerator.inhrStrategies.InheritanceData;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import java.util.Set;

public interface AssociationStrategy extends AssociationsData {
  /**
   * This function converts the association relations of a class diagram in smt.
   *
   * @param ast the class diagram.
   * @param ctx the SMT context.
   * @param classData the data form the declaration Classes and Interfaces.
   * @param inheritanceData the data from the declaration of inheritance relations.
   */
  void cd2smt(
      ASTCDCompilationUnit ast, Context ctx, ClassData classData, InheritanceData inheritanceData);

  /**
   * @param model the Model produced by the SMT Solver.
   * @param objectSet set with SMTObjects without links.
   * @return set of linked SMTObjects.
   */
  Set<SMTObject> smt2od(Model model, Set<SMTObject> objectSet);

  /**
   * different Strategies to convert association relations in smt.
   * <li>DEFAULT: each association relation * is converted as a function (Type1 ==> Type2 ==> Bool).
   * <li/>
   * <li>ONE2ONE: special case of the DEFAULT the * association with cardinality [1]--[1] are
   *     converted as function (Type1 ==> Type2).
   * <li/>
   */
  enum Strategy {
    DEFAULT,
    ONE2ONE
  }
}
