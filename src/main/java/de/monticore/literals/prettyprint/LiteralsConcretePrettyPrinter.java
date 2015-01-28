package de.monticore.literals.prettyprint;

import mc.ast.ASTNode;
import mc.ast.AbstractVisitor;
import mc.ast.ConcretePrettyPrinter;
import mc.helper.IndentPrinter;
import de.monticore.literals._ast.ASTBooleanLiteral;
import de.monticore.literals._ast.ASTBooleanLiteralList;
import de.monticore.literals._ast.ASTCharLiteral;
import de.monticore.literals._ast.ASTCharLiteralList;
import de.monticore.literals._ast.ASTDoubleLiteral;
import de.monticore.literals._ast.ASTDoubleLiteralList;
import de.monticore.literals._ast.ASTFloatLiteral;
import de.monticore.literals._ast.ASTFloatLiteralList;
import de.monticore.literals._ast.ASTIntLiteral;
import de.monticore.literals._ast.ASTIntLiteralList;
import de.monticore.literals._ast.ASTLiteral;
import de.monticore.literals._ast.ASTLiteralList;
import de.monticore.literals._ast.ASTLongLiteral;
import de.monticore.literals._ast.ASTLongLiteralList;
import de.monticore.literals._ast.ASTNullLiteral;
import de.monticore.literals._ast.ASTNullLiteralList;
import de.monticore.literals._ast.ASTNumericLiteral;
import de.monticore.literals._ast.ASTNumericLiteralList;
import de.monticore.literals._ast.ASTSignedDoubleLiteral;
import de.monticore.literals._ast.ASTSignedDoubleLiteralList;
import de.monticore.literals._ast.ASTSignedFloatLiteral;
import de.monticore.literals._ast.ASTSignedFloatLiteralList;
import de.monticore.literals._ast.ASTSignedIntLiteral;
import de.monticore.literals._ast.ASTSignedIntLiteralList;
import de.monticore.literals._ast.ASTSignedLongLiteral;
import de.monticore.literals._ast.ASTSignedLongLiteralList;
import de.monticore.literals._ast.ASTStringLiteral;
import de.monticore.literals._ast.ASTStringLiteralList;

/**
 * Concrete pretty-printer for printing literals.
 * 
 * @author Martin Schindler
 */
//STATE ? * maybe own project usable as lib
public class LiteralsConcretePrettyPrinter extends ConcretePrettyPrinter {
  
  /**
   * The following classes can be pretty-printed
   * 
   * @return classes can be pretty-printed by this class
   */
  @Override
  public Class<?>[] getResponsibleClasses() {
    Class<?>[] classes = new Class[] {
        ASTBooleanLiteral.class, ASTBooleanLiteralList.class, 
        ASTCharLiteral.class, ASTCharLiteralList.class, 
        ASTDoubleLiteral.class, ASTDoubleLiteralList.class, 
        ASTFloatLiteral.class, ASTFloatLiteralList.class, 
        ASTIntLiteral.class, ASTIntLiteralList.class, 
        ASTLiteral.class, ASTLiteralList.class,
        ASTLongLiteral.class, ASTLongLiteralList.class, 
        ASTNullLiteral.class, ASTNullLiteralList.class, 
        ASTNumericLiteral.class, ASTNumericLiteralList.class,
        ASTStringLiteral.class, ASTStringLiteralList.class, 
        ASTSignedIntLiteral.class, ASTSignedIntLiteralList.class, 
        ASTSignedDoubleLiteral.class, ASTSignedDoubleLiteralList.class, 
        ASTSignedFloatLiteral.class, ASTSignedFloatLiteralList.class, 
        ASTSignedLongLiteral.class, ASTSignedLongLiteralList.class};
    return classes;
  }
  
  /**
   * Pretty-print literals
   * 
   * @param a ASTNode to pretty print
   * @param printer Printer to use
   */
  @Override
  public void prettyPrint(ASTNode a, IndentPrinter printer) {
    // Run visitor
    AbstractVisitor.run(new LiteralsPrettyPrinterConcreteVisitor(printer), a);
  }
  
}
