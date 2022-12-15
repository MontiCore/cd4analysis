package de.monticore.cd2smt.cd2smtGenerator.inhrStrategies;

import com.microsoft.z3.Context;
import com.microsoft.z3.Model;
import de.monticore.cd2smt.ODArtifacts.SMTObject;
import de.monticore.cd2smt.cd2smtGenerator.classStrategies.ClassData;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;

import java.util.Set;

public interface InheritanceStrategy extends InheritanceData {
  /**
   * @param ast Model as class diagram
   * @param ctx the SMT context
   * @param classData the data form the declaration Classes and Interfaces
   */
  void cd2smt(ASTCDCompilationUnit ast, Context ctx, ClassData classData);

  /**
   * @param model the Model produced by the SMT Solver
   * @param objectSet set with SMTObjects without super-instance
   * @return set of SMTObjects with their super-instances
   */
  Set<SMTObject> smt2od(Model model, Set<SMTObject> objectSet);
}
