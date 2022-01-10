/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.alloycddiff.alloyRunner;

import de.monticore.cddiff.alloy2od.generator.Alloy2ODGenerator;
import de.monticore.odbasis._ast.ASTODArtifact;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.ast.Command;
import edu.mit.csail.sdg.parser.CompModule;
import edu.mit.csail.sdg.translator.A4Solution;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * A concrete alloy solution handler to process alloy based diff solutions for
 * class diagrams
 *
 * 
 */
public class AlloyDiffSolution extends AlloySolutionHandler {
  
  /**
   * Constructor for de.cddiff.alloycddiff.alloyrunner.AlloyDiffSolution
   */
  public AlloyDiffSolution(CompModule alloyModule, Command command, A4Solution initialSolution) {
    super(alloyModule, command, initialSolution);
  }
  
  /**
   * Constructor for de.cddiff.alloycddiff.alloyrunner.AlloyDiffSolution
   */
  public AlloyDiffSolution(CompModule alloyModule, Command command) throws Err {
    super(alloyModule, command);
  }
  
  /**
   * @see de.monticore.cddiff.alloycddiff.alloyRunner.AlloySolutionHandler#generateSolutionsToPath(Path)
   */
  @Override
  public void generateSolutionsToPath(Path outputDirectory) {
    if(limited) {
       Alloy2ODGenerator.generateLimited(alloyModule, initialSolution, solutionLimit, outputDirectory.toFile());
    } else {
      Alloy2ODGenerator.generateAll(alloyModule, initialSolution, outputDirectory.toFile());
    }
  }

  /**
   *
   * Generates parsed Object diagrams
   *
   * @return A list of corresponding ASTs of parsed Object diagrams
   */
  public List<ASTODArtifact> generateODs() {
    return Alloy2ODGenerator.generateLimitODs(alloyModule, initialSolution, solutionLimit);
  }

  /**
   * @see de.monticore.cddiff.alloycddiff.alloyRunner.AlloySolutionHandler#generateSolutionsToPath(Path)
   * TODO: This is incorrectly implemented.
   */
  public void generateUniqueSolutionsToPath(Path outputDirectory) {
 // Variable for possibly multiple solutions
    int number = 0;
    
    // Set solution as initial value
    A4Solution currentSolution = initialSolution;
    
    // Already computed solutions 
    List<String> alreadyComputed = new ArrayList<>();
    
    // Do this for all solutions
    while (currentSolution.satisfiable() && (!limited || number < solutionLimit )) {
      // Derive module name
      String name = initialSolution.getOriginalFilename() + number;
      
      // Generate module
      String currentOD = Alloy2ODGenerator.generateString(alloyModule, currentSolution, number);
      
      if(!alreadyComputed.contains(currentOD.replaceFirst("objectdiagram od[0-9]+ \\{", ""))) {
        // Save module
        Alloy2ODGenerator.saveOD(currentOD, name, outputDirectory.toFile());
        
        // Add to computed models
        alreadyComputed.add(currentOD.replaceFirst("objectdiagram od[0-9]+ \\{", "")); 
        }
      // Increase loop variables
      try {
        currentSolution = currentSolution.next();
      }
      catch (Err e) {
        e.printStackTrace();
        return;
      }
      number++;
    }
  }
  
  /**
   * 
   * Generates parsed Object diagrams 
   * 
   * @return A list of corresponding ASTs of parsed Object diagrams
   */
  public List<ASTODArtifact> generateUniqueODs() {
    return Alloy2ODGenerator.generateUniqueODs(alloyModule, initialSolution);
  }
}
