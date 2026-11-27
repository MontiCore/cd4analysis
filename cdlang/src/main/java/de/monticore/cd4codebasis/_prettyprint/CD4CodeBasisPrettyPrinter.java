/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4codebasis._prettyprint;

import de.monticore.cd4codebasis._ast.ASTCDSealedModifier;
import de.monticore.prettyprint.IndentPrinter;

public class CD4CodeBasisPrettyPrinter extends CD4CodeBasisPrettyPrinterTOP {
  
  public CD4CodeBasisPrettyPrinter(IndentPrinter printer, boolean printComments) {
    super(printer, printComments);
  }
  
  @Override
  public void handle(ASTCDSealedModifier node) {
    // hand-written: the registered UMLModifier handler prints the UML modifiers (incl. short
    // forms) and the comments, so post-comments precede sealed and non-sealed
    getTraverser().getUMLModifierHandler().ifPresent(h -> h.handle(node));
    
    if (node.isSealed()) {
      getPrinter().print("sealed ");
    }
    
    if (node.isNonSealed()) {
      getPrinter().print("non-sealed ");
    }
  }
  
}
