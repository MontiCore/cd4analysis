/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.alloycddiff.alloyRunner;

import edu.mit.csail.sdg.alloy4.A4Reporter;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4.ErrorWarning;
import edu.mit.csail.sdg.alloy4compiler.ast.Command;
import edu.mit.csail.sdg.alloy4compiler.parser.CompModule;
import edu.mit.csail.sdg.alloy4compiler.parser.CompUtil;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Options;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;
import edu.mit.csail.sdg.alloy4compiler.translator.TranslateAlloyToKodkod;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Abstract class for the computation of alloy solutions for alloy modules
 * containing predicates
 *
 */
public abstract class AlloyRunner {
  
  public List<AlloySolutionHandler> runAlloy(Path moduleFile) {
    // Initialize Result
    List<AlloySolutionHandler> result;
    
    // Chooses the Alloy4 options
    A4Options opt = new A4Options();
    opt.solver = A4Options.SatSolver.SAT4J;
    
    // Alloy4 sends diagnostic messages and progress reports to the A4Reporter.
    // By default, the A4Reporter ignores all these events (but you can extend
    // the A4Reporter to display the event for the user)
    A4Reporter rep = new A4Reporter() {
      // For example, here we choose to display each "warning" by printing it to
      // System.out
      @Override
      public void warning(ErrorWarning msg) {
        System.out.print("Relevance Warning:" + System.lineSeparator() + (msg.toString().trim()) + System.lineSeparator() + System.lineSeparator());
        System.out.flush();
      }
    };
    
    // Compute result
    result = runAlloy(moduleFile, rep, opt);
    
    // Return result
    return result;
  }
  
  public List<AlloySolutionHandler> runAlloy(Path moduleFile, A4Reporter rep, A4Options opt) {
    // Initialize Result
    List<AlloySolutionHandler> result = new ArrayList<>();
    
    // Parse the Module
    System.out.println("=========== Parsing+Typechecking =============");
    
    // Try to parse the model
    Optional<CompModule> optModule = Optional.empty();
    try {
      optModule = Optional.ofNullable(
          CompUtil.parseEverything_fromFile(rep, null, moduleFile.toAbsolutePath().toString()));
    }
    catch (Err e) {
      e.printStackTrace();
    }
    
    // If present proceed with executing the model
    if (optModule.isPresent()) {
      // Get module from optional
      CompModule module = optModule.get();
      
      // Initialize runner with default options
      A4Options options = new A4Options();
      options.solver = A4Options.SatSolver.SAT4J;
      
      // Execute all commands from the module
      for (Command command : module.getAllCommands()) {
        // // Execute the command
        System.out.println("============ Command " + command + ": ============");
        
        A4Solution sol;
        try {
          sol = TranslateAlloyToKodkod.execute_command(rep,
              module.getAllReachableSigs(), command, options);
          
          // Generate solution handler and add it to result
          result.add(solutionHandlerGenerator(module, command, sol));
        }
        catch (Err e) {
          e.printStackTrace();
        }
      }
    }
    
    // Return the result
    return result;
  }
  
  /**
   * Returns a concrete solution handler based on module, command and solution
   */
  public abstract AlloySolutionHandler solutionHandlerGenerator(CompModule module, Command command,
      A4Solution solution);
  
}
