package de.monticore.umlcd4a._ast;

import static de.monticore.umlcd4a.prettyprint.AstPrinter.EMPTY_STRING;
import mc.helper.IndentPrinter;
import de.monticore.literals.prettyprint.LiteralsConcretePrettyPrinter;
import de.monticore.types.TypesPrinter;
import de.monticore.types._ast.ASTType;

public class ASTCDAttribute extends ASTCDAttributeTOP
    implements ASTCD4AnalysisBase {
  
  protected ASTCDAttribute() {
  }
  
  protected ASTCDAttribute(
      ASTModifier modifier,
      ASTType type,
      String name,
      ASTValue value) {
    super(modifier, type, name, value);
  }
  
  /**
   * Print the string of a ASTModifier type, e.g. abstract private final
   * 
   * @return a string, e.g. abstract private final 
   */
  public String printModifier() {
    if (!modifierIsPresent()) {
      return EMPTY_STRING;
    }
    
    StringBuilder modifierStr = new StringBuilder();
    if (modifier.get().isAbstract()) {
      modifierStr.append(" abstract ");
    }
    if (modifier.get().isPublic()) {
      modifierStr.append(" public ");
    }
    else if (modifier.get().isPrivate()) {
      modifierStr.append(" private ");
    }
    else if (modifier.get().isProtected()) {
      modifierStr.append(" protected ");
    }
    if (modifier.get().isFinal()) {
      modifierStr.append(" final ");
    }
    if (modifier.get().isStatic()) {
      modifierStr.append(" static ");
    }
    
    return modifierStr.toString();
  }
  
  /**
   * Prints a value of an attribute
   * 
   * @return a string representing the ASTValue
   */
  public String printValue() {
    if (!valueIsPresent()) {
      return EMPTY_STRING;
    }
    
    LiteralsConcretePrettyPrinter p = new LiteralsConcretePrettyPrinter();
    IndentPrinter iPrinter = new IndentPrinter();
    p.prettyPrint(value.get(), iPrinter);
    return iPrinter.getContent().trim().intern();
  }
  
  /**
   * Prints an attribute type
   * 
   * @return String representation of the ASTType
   */
  public String printType() {
    return TypesPrinter.printType(type);
  }
}
