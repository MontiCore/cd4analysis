package de.monticore.types.prettyprint;

import java.util.Iterator;

import mc.helper.IndentPrinter;
import mc.helper.NameHelper;
import de.monticore.literals.prettyprint.LiteralsPrettyPrinterConcreteVisitor;
import de.monticore.types._ast.ASTArrayType;
import de.monticore.types._ast.ASTComplexReferenceType;
import de.monticore.types._ast.ASTConstantsTypes;
import de.monticore.types._ast.ASTPrimitiveType;
import de.monticore.types._ast.ASTQualifiedName;
import de.monticore.types._ast.ASTQualifiedNameList;
import de.monticore.types._ast.ASTReferenceTypeList;
import de.monticore.types._ast.ASTSimpleReferenceType;
import de.monticore.types._ast.ASTTypeArguments;
import de.monticore.types._ast.ASTTypeList;
import de.monticore.types._ast.ASTTypeParameters;
import de.monticore.types._ast.ASTTypeVariableDeclaration;
import de.monticore.types._ast.ASTVoidType;
import de.monticore.types._ast.ASTWildcardType;

/**
 * This class is responsible for pretty-printing types of the common type system. It is implemented
 * using the Visitor pattern. The Visitor pattern traverses a tree in depth first, the visit and
 * ownVisit-methods are called when a node is traversed, the endVisit methods are called when the
 * whole subtree of a node has been traversed. The ownVisit-Methods stop the automatic traversal
 * order and allow to explictly visit subtrees by calling getVisitor().startVisit(ASTNode)
 * 
 * @author Martin Schindler
 */
public class TypesPrettyPrinterConcreteVisitor extends LiteralsPrettyPrinterConcreteVisitor {
  
  // printer to use
  private IndentPrinter printer;
  
  /**
   * Constructor.
   * 
   * @param parent the parent pretty printer, needed to give control to the embedded pretty printer
   * when embedding is detected.
   * @param printer the printer to write to.
   */
  public TypesPrettyPrinterConcreteVisitor(IndentPrinter printer) {
    super(printer);
    this.printer = printer;
  }
  
  /**
   * Prints qualified names
   * 
   * @param a qualified name
   */
  public void ownVisit(ASTQualifiedName a) {
    printer.print(mc.helper.NameHelper.dotSeparatedStringFromList(a.getParts()));
  }
  
  /**
   * Prints a list of qualified names
   * 
   * @param a list of qualified names
   */
  public void ownVisit(ASTQualifiedNameList a) {
    printList(a.iterator(), ", ");
  }
  
  /**
   * Prints an array of a primitive or complex array type
   * 
   * @param a array type
   */
  public void ownVisit(ASTArrayType a) {
    // print primitive type
    visitor.startVisit(a.getComponentType());
    // print dimension
    for (int i = 0; i < a.getDimensions(); i++) {
      printer.print("[]");
    }
  }
  
  /**
   * Prints a void type.
   * 
   * @param a void type
   */
  public void visit(ASTVoidType a) {
    printer.print("void");
  }
  
  /**
   * Prints a primitive type.
   * 
   * @param a primitive type
   */
  public void visit(ASTPrimitiveType a) {
    switch (a.getPrimitive()) {
      case ASTConstantsTypes.BOOLEAN: 
        printer.print("boolean");
        break;
      case ASTConstantsTypes.BYTE:
        printer.print("byte");
        break;
      case ASTConstantsTypes.CHAR:
        printer.print("char");
        break;
      case ASTConstantsTypes.SHORT:
        printer.print("short");
        break;
      case ASTConstantsTypes.INT:
        printer.print("int");
        break;
      case ASTConstantsTypes.FLOAT:
        printer.print("float");
        break;
      case ASTConstantsTypes.LONG: 
        printer.print("long");
        break;
      case ASTConstantsTypes.DOUBLE: 
        printer.print("double");
        break;
      default: printer.print("");
    }
  }
  
  /**
   * Prints a simple reference type
   * 
   * @param a simple reference type
   */
  public void visit(ASTSimpleReferenceType a) {
    // print qualified name
    printer.print(NameHelper.dotSeparatedStringFromList(a.getName()));
    // optional type arguments are printed automatically by visitor concept
  }
  
  /**
   * Prints a complex reference type
   * 
   * @param a complex reference type
   */
  public void ownVisit(ASTComplexReferenceType a) {
    printList(a.getSimpleReferenceType().iterator(), ".");
  }
  
  /**
   * Prints type arguments (Generics)
   * 
   * @param a type arguments
   */
  public void ownVisit(ASTTypeArguments a) {
    printer.print("<");
    printList(a.getTypeArguments().iterator(), ", ");
    printer.print(">");
  }
  
  /**
   * Prints a wildcard type of a type argument (Generics)
   * 
   * @param a wildcard type
   */
  public void ownVisit(ASTWildcardType a) {
    printer.print("?");
    if (a.getUpperBound().isPresent()) {
      printer.print(" extends ");
      visitor.startVisit(a.getUpperBound().get());
    }
    else if (a.getLowerBound().isPresent()) {
      printer.print(" super ");
      visitor.startVisit(a.getLowerBound().get());
    }
  }
  
  /**
   * Prints type parameters (Generics)
   * 
   * @param a type parameters
   */
  public void ownVisit(ASTTypeParameters a) {
    if (a.getTypeVariableDeclarations().size() > 0) {
      printer.print("<");
      printList(a.getTypeVariableDeclarations().iterator(), ", ");
      printer.print(">");
    }
  }
  
  /**
   * Prints a type variable declaration (Generics)
   * 
   * @param a type variable declaration
   */
  public void ownVisit(ASTTypeVariableDeclaration a) {
    printer.print(a.getName());
    if (a.getUpperBounds() != null && !a.getUpperBounds().isEmpty()) {
      printer.print(" extends ");
      printList(a.getUpperBounds().iterator(), " & ");
    }
  }
  
  /**
   * Prints List of Types
   * 
   * @param a list of types
   */
  public void ownVisit(ASTTypeList a) {
    printList(a.iterator(), ", ");
  }
  
  /**
   * Prints List of Types
   * 
   * @param a list of types
   */
  public void ownVisit(ASTReferenceTypeList a) {
    printList(a.iterator(), ", ");
  }
  
  /**
   * Prints a list of ASTQualifiedNames in an ownVisit method
   * 
   * @param iter iterator for the list of ASTQualifiedNames
   * @param seperator string for seperating the ASTQualifiedNames
   */
  protected void printQualifiedNameList(Iterator<ASTQualifiedName> iter, String seperator) {
    // print by iterate through all items
    String sep = "";
    while (iter.hasNext()) {
      printer.print(sep);
      printer.print(NameHelper.dotSeparatedStringFromList(iter.next().getParts())); // visit
                                                                                    // item
      sep = seperator;
    }
  }
  
}
