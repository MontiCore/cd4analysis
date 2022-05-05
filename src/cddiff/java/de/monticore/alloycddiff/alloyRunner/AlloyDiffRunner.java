/* (c) https://github.com/MontiCore/monticore */
package de.monticore.alloycddiff.alloyRunner;

import edu.mit.csail.sdg.ast.Command;
import edu.mit.csail.sdg.parser.CompModule;
import edu.mit.csail.sdg.translator.A4Solution;

/**
 * A concrete runner for alloy modules computing diff solutions for class diagrams
 */
public class AlloyDiffRunner extends AlloyRunner {
  /**
   * @see de.monticore.alloycddiff.alloyRunner.AlloyRunner#solutionHandlerGenerator(edu.mit.csail.sdg.parser.CompModule,
   * edu.mit.csail.sdg.ast.Command, edu.mit.csail.sdg.translator.A4Solution)
   */
  @Override
  public AlloySolutionHandler solutionHandlerGenerator(CompModule module, Command command,
      A4Solution solution) {

    // Generate AlloyDiffSolution

    return new AlloyDiffSolution(module, command, solution);
  }

}
