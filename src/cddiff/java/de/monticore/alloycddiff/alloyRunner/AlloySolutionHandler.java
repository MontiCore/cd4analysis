/* (c) https://github.com/MontiCore/monticore */
package de.monticore.alloycddiff.alloyRunner;

import edu.mit.csail.sdg.alloy4.A4Reporter;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.ast.Command;
import edu.mit.csail.sdg.alloy4compiler.parser.CompModule;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Options;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;
import edu.mit.csail.sdg.alloy4compiler.translator.TranslateAlloyToKodkod;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * This abstract class serves as abstraction layer between the alloy module and
 * the solution generation.
 *
 */
public abstract class AlloySolutionHandler {
  // The alloy module the solution refers to
  protected CompModule alloyModule;

  // The command of the alloy module corresponding to the module
  protected Command command;

  // The initial solution object created by alloy
  protected A4Solution initialSolution;

  // Solution limit
  protected int solutionLimit = 1000;

  // Flag indicating if the solution limiter should be activated
  protected boolean limited = false;

  /**
   * Constructor for de.cddiff.alloycddiff.alloyrunner.AlloySolutionHandler
   */
  public AlloySolutionHandler(CompModule alloyModule, Command command, A4Solution initialSolution) {
    // Note that the alloy command must be part of the alloy module
    assert (alloyModule.getAllCommands().contains(command));

    // Set all parameters
    this.alloyModule = alloyModule;
    this.command = command;
    this.initialSolution = initialSolution;
  }

  /**
   * Constructor for de.cddiff.alloycddiff.alloyrunner.AlloySolutionHandler
   * generating the solution using alloys standard solver and reporter. The
   * alloy error exception is forwarded and has to be handled by the user.
   */
  public AlloySolutionHandler(CompModule alloyModule, Command command) throws Err {
    // Note that the alloy command must be part of the alloy module
    assert (alloyModule.getAllCommands().contains(command));

    // Set all parameters
    this.alloyModule = alloyModule;
    this.command = command;

    // Generate initial solution from module using the standard options
    // Get standard solver
    A4Options opt = new A4Options();
    opt.solver = A4Options.SatSolver.SAT4J;

    // Get standard reporter (which ignores all events by default)
    A4Reporter rep = new A4Reporter();

    this.initialSolution = TranslateAlloyToKodkod.execute_command(rep,
        alloyModule.getAllReachableSigs(), command, opt);
  }

  /**
   * A function which should save all available solution to outputDirectory
   */
  abstract void generateSolutionsToPath(Path outputDirectory);

  /**
   * Get all satisfiable alloy solutions
   *
   * @return List of all satisfiable alloy solutions
   */
  public List<A4Solution> getAllSatSolutions() {
    List<A4Solution> result = new ArrayList<>();

    // Set solution as initial value
    A4Solution currentSolution = initialSolution;

    // Do this for all solutions
    while (currentSolution.satisfiable()) {
      // Add current solution
      result.add(currentSolution);

      // Increase loop variable
      try {
        currentSolution = currentSolution.next();
      }
      catch (Err e) {
        e.printStackTrace();
        return result;
      }
    }

    return result;
  }

  /**
   * Computes the number of satisfiable solutions. WARNING: Do not use this
   * function if you compute all solutions separately
   *
   * @return Number of satisfiable solutions.
   */
  public int getNumberOfSatSolutions() {
    // Initialize result
    int result = 0;

    // Set solution as initial value
    A4Solution currentSolution = initialSolution;

    // Do this for all solutions
    while (currentSolution.satisfiable()) {
      // Add current solution
      result++;

      // Increase loop variable
      try {
        currentSolution = currentSolution.next();
      }
      catch (Err e) {
        e.printStackTrace();
        return result;
      }
    }

    return result;
  }

  /**
   * @return alloyModule
   */
  public CompModule getAlloyModule() {
    return this.alloyModule;
  }

  /**
   * @return command
   */
  public Command getCommand() {
    return this.command;
  }

  /**
   * @return initialSolution
   */
  public A4Solution getInitialSolution() {
    return this.initialSolution;
  }

  /**
   * @return solutionLimit
   */
  public int getSolutionLimit() {
    return this.solutionLimit;
  }

  /**
   * @param solutionLimit the solutionLimit to set
   */
  public void setSolutionLimit(int solutionLimit) {
    this.solutionLimit = solutionLimit;
  }

  /**
   * @return limited
   */
  public boolean isLimited() {
    return this.limited;
  }

  /**
   * @param limited the limited to set
   */
  public void setLimited(boolean limited) {
    this.limited = limited;
  }
}
