/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2smt.cd2smtGenerator.assocStrategies;

import com.microsoft.z3.Context;
import com.microsoft.z3.Model;
import de.monticore.cd2smt.ODArtifacts.SMTObject;
import de.monticore.cd2smt.cd2smtGenerator.classStrategies.ClassData;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import java.util.Set;

public interface AssociationStrategy extends AssociationsData {
  /**
   * @param ast the Model as class diagram
   * @param ctx the SMT context
   * @param classData the data form the declaration Classes and Interfaces
   */
  void cd2smt(ASTCDCompilationUnit ast, Context ctx, ClassData classData);

  /**
   * @param model the Model produced by the SMT Solver
   * @param objectSet set with SMTObjects without links
   * @return set of linked SMTObjects
   */
  Set<SMTObject> smt2od(Model model, Set<SMTObject> objectSet);

  public enum Strategy {
    DEFAULT,
    ONE2ONE
  }
}
