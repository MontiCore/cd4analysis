/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdbasis._prettyprint;

import de.monticore.prettyprint.IndentPrinter;

/**
 * Preliminary class that allows the pretty printing of
 * 'sealed' and 'non-sealed'. Should be removed when 'sealed'
 * and 'non-sealed' are integrated in UMLModifier.mc4.
 */
public class CDBasisPrettyPrinter extends CDBasisPrettyPrinterTOP {
  
  protected boolean useShortForms = false;
  
  public CDBasisPrettyPrinter(IndentPrinter printer, boolean printComments) {
    super(printer, printComments);
  }
  
  public boolean isUseShortForms() { return useShortForms; }
  
  public void setUseShortForms(boolean useShortForms) { this.useShortForms = useShortForms; }
  
  @Override
  public void handle(de.monticore.cdbasis._ast.ASTCDSealedModifier node) {
    // hand-written due to the possible short forms
    
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    
    if (node.isPresentStereotype()) {
      node.getStereotype().accept(getTraverser());
    }
    
    if (node.isPublic()) {
      getPrinter().print(this.isUseShortForms() ? "+ " : "public ");
    }
    
    if (node.isPrivate()) {
      getPrinter().print(this.isUseShortForms() ? "- " : "private ");
    }
    
    if (node.isProtected()) {
      getPrinter().print(this.isUseShortForms() ? "# " : "protected ");
    }
    
    if (node.isFinal()) {
      getPrinter().print("final ");
    }
    
    if (node.isAbstract()) {
      getPrinter().print("abstract ");
    }
    
    if (node.isLocal()) {
      getPrinter().print("local ");
    }
    
    if (node.isDerived()) {
      getPrinter().print(this.isUseShortForms() ? "/ " : "derived ");
    }
    
    if (node.isReadonly()) {
      getPrinter().print(this.isUseShortForms() ? "? " : "readonly ");
    }
    
    if (node.isStatic()) {
      getPrinter().print("static ");
    }
    
    if (node.isSealed()) {
      getPrinter().print("sealed ");
    }
    
    if (node.isNonSealed()) {
      getPrinter().print("non-sealed ");
    }
    
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
    
  }
  
}
