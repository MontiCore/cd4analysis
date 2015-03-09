package de.monticore.umlcd4a.prettyprint;

import mc.ast.ConcretePrettyPrinter;
import mc.ast.PrettyPrinter;
import mc.helper.IndentPrinter;
import mc.helper.NameHelper;
import de.cd4analysis._ast.ASTCDAssociation;
import de.cd4analysis._ast.ASTCDAttribute;
import de.cd4analysis._ast.ASTCDClass;
import de.cd4analysis._ast.ASTCDCompilationUnit;
import de.cd4analysis._ast.ASTCDConstructor;
import de.cd4analysis._ast.ASTCDDefinition;
import de.cd4analysis._ast.ASTCDEnum;
import de.cd4analysis._ast.ASTCDEnumConstant;
import de.cd4analysis._ast.ASTCDEnumConstantList;
import de.cd4analysis._ast.ASTCDEnumParameter;
import de.cd4analysis._ast.ASTCDInterface;
import de.cd4analysis._ast.ASTCDMethod;
import de.cd4analysis._ast.ASTCDParameter;
import de.cd4analysis._ast.ASTCDParameterList;
import de.cd4analysis._ast.ASTCDQualifier;
import de.cd4analysis._ast.ASTCardinality;
import de.cd4analysis._ast.ASTModifier;
import de.cd4analysis._ast.ASTStereoValue;
import de.cd4analysis._ast.ASTStereotype;
import de.cd4analysis._ast.ASTStereotypeList;
import de.monticore.types._ast.ASTImportStatement;
import de.monticore.types.prettyprint.TypesPrettyPrinterConcreteVisitor;

/**
 * This class is responsible for pretty-printing class diagrams. It is implemented using the Visitor
 * pattern. The Visitor pattern traverses a tree in depth first, the visit and ownVisit-methods are
 * called when a node is traversed, the endVisit methods are called when the whole subtree of a node
 * has been traversed. The ownVisit-Methods stop the automatic traversal order and allow to
 * explictly visit subtrees by calling getVisitor().startVisit(ASTNode)
 * 
 * @author Martin Schindler
 */
public class CDPrettyPrinterConcreteVisitor extends TypesPrettyPrinterConcreteVisitor {
  
  // printer to use
  private IndentPrinter printer;
  
  private PrettyPrinter prettyPrinter;
  
  /**
   * Constructor.
   * 
   * @param parent the parent pretty printer, needed to give control to the embedded pretty printer
   * when embedding is detected.
   * @param printer the printer to write to.
   */
  public CDPrettyPrinterConcreteVisitor(PrettyPrinter parent, IndentPrinter printer) {
    super(printer);
    this.prettyPrinter = parent;
    this.printer = printer;
  }
  
  /**
   * Prints the compilation unit of a class diagram (start of the pretty print)
   * 
   * @param a CD compilation unit
   */
  public void ownVisit(ASTCDCompilationUnit unit) {
    if (unit.getPackage() != null && !unit.getPackage().isEmpty()) {
      printer
          .println("package " + NameHelper.dotSeparatedStringFromList(unit.getPackage()) + ";\n");
    }
    if (unit.getImportStatements() != null && !unit.getImportStatements().isEmpty()) {
      for (ASTImportStatement s : unit.getImportStatements()) {
        printer.print("import " + NameHelper.dotSeparatedStringFromList(s.getImportList()));
        if (s.isStar()) {
          printer.println(".*;");
        }
        else {
          printer.println(";");
        }
      }
      printer.println();
    }
    visitor.startVisit(unit.getCDDefinition());
  }
  
  /**
   * Prints the class diagram definition
   * 
   * @param a class diagram definition
   */
  public void ownVisit(ASTCDDefinition a) {
    
    // print classdiagram name and parameters
    printer.print("classdiagram " + a.getName());
    // print body
    printer.println("{");
    printer.indent();
    visitor.startVisit(a.getCDInterfaces());
    visitor.startVisit(a.getCDClasses());
    visitor.startVisit(a.getCDEnums());
    visitor.startVisit(a.getCDAssociations());
    printer.unindent();
    printer.print("\n}\n");
  }
  
  /**
   * Prints a class in a class diagram
   * 
   * @param a class
   */
  public void ownVisit(ASTCDClass a) {
    printer.println();
    // print completeness
    // print class modifier
    if (a.getModifier().isPresent()) {
      visitor.startVisit(a.getModifier().get());
    }
    // print class name
    printer.print("class " + a.getName());
    // print generic type parameters
    // print superclasses
    if (a.getSuperclass().isPresent()) {
      printer.print(" extends ");
      visitor.startVisit(a.getSuperclass().get());
    }
    // print interfaces
    if (a.getInterfaces().size() != 0) {
      printer.print(" implements ");
      visitor.startVisit(a.getInterfaces());
    }
    // print class body
    if (a.getCDConstructors().size() != 0 || a.getCDMethods().size() != 0
        || a.getCDAttributes().size() != 0) {
      printer.println("{");
      printer.indent();
      visitor.startVisit(a.getCDAttributes());
      visitor.startVisit(a.getCDConstructors());
      visitor.startVisit(a.getCDMethods());
      printer.unindent();
      printer.println("}");
    }
    else {
      printer.println(";");
    }
  }
  
  /**
   * Prints an interface in a class diagram
   * 
   * @param a interface
   */
  public void ownVisit(ASTCDInterface a) {
    printer.println();
    // print modifier
    if (a.getModifier().isPresent()) {
      visitor.startVisit(a.getModifier().get());
    }
    // print interface name
    printer.print("interface " + a.getName());
    // print implemented interfaces
    if (a.getInterfaces().size() != 0) {
      printer.print(" extends ");
      visitor.startVisit(a.getInterfaces());
    }
    // print interface body
    if (a.getCDMethods().size() != 0 || a.getCDAttributes().size() != 0) {
      printer.println("{");
      printer.indent();
      visitor.startVisit(a.getCDAttributes());
      visitor.startVisit(a.getCDMethods());
      printer.unindent();
      printer.println("}");
    }
    else {
      printer.println(";");
    }
  }
  
  /**
   * Prints an enum in a class diagram
   * 
   * @param a enum
   */
  public void ownVisit(ASTCDEnum a) {
    printer.println();
    // print enum modifier
    if (a.getModifier().isPresent()) {
      visitor.startVisit(a.getModifier().get());
    }
    // print enum name
    printer.print("enum " + a.getName());
    // print interfaces
    if (a.getInterfaces().size() != 0) {
      printer.print(" implements ");
      visitor.startVisit(a.getInterfaces());
    }
    // print enum body
    if (a.getCDEnumConstants().size() != 0 || a.getCDConstructors().size() != 0
        || a.getCDMethods().size() != 0) {
      printer.println("{");
      printer.indent();
      if (a.getCDEnumConstants().size() != 0) {
        visitor.startVisit(a.getCDEnumConstants());
        printer.println(";");
      }
      if (a.getCDConstructors().size() != 0 || a.getCDMethods().size() != 0) {
        printer.println();
        visitor.startVisit(a.getCDConstructors());
        visitor.startVisit(a.getCDMethods());
      }
      printer.unindent();
      printer.println("}");
    }
    else {
      printer.println(";");
    }
  }
  
  /**
   * Prints an enum constant in a class diagram
   * 
   * @param a enum constant
   */
  public void ownVisit(ASTCDEnumConstant a) {
    // print enum name
    printer.print(a.getName());
    // print parameters
    if (!a.getCDEnumParameters().isEmpty()) {
      printer.print("(");
      printList(a.getCDEnumParameters().iterator(), ", ");
      printer.print(")");
    }
  }
  
  public void ownVisit(ASTCDEnumParameter a) {
    prettyPrinter.prettyPrint(a.getValue(), printer);
  }
  
  /**
   * Prints a list of enum constants in a class diagram
   * 
   * @param a list of enum constants
   */
  public void ownVisit(ASTCDEnumConstantList a) {
    printList(a.iterator(), ",\n");
  }
  
  /**
   * Prints a method of a class in a class diagram
   * 
   * @param a method
   */
  public void ownVisit(ASTCDMethod a) {
    ConcretePrettyPrinter.printPreComments(a, printer);
    
    // print modifier
    visitor.startVisit(a.getModifier());
    // print generics
    // print return type
    visitor.startVisit(a.getReturnType());
    // print name
    printer.print(" " + a.getName());
    // print parameters
    printer.print("(");
    visitor.startVisit(a.getCDParameters());
    printer.print(")");
    // print exception
    if (a.getExceptions().size() != 0) {
      printer.print(" throws ");
      visitor.startVisit(a.getExceptions());
    }
    printer.println(";");
    ConcretePrettyPrinter.printPostComments(a, printer);
  }
  
  /**
   * Prints a constructor of a class in a class diagram
   * 
   * @param a constructor
   */
  public void ownVisit(ASTCDConstructor a) {
    // print modifier
    visitor.startVisit(a.getModifier());
    // print name
    printer.print(a.getName());
    // print parameters
    printer.print("(");
    visitor.startVisit(a.getCDParameters());
    printer.print(")");
    // print exception
    if (a.getExceptions().size() != 0) {
      printer.print(" throws ");
      visitor.startVisit(a.getExceptions());
    }
    printer.println(";");
  }
  
  /**
   * Prints a parameter
   * 
   * @param a parameter
   */
  public void ownVisit(ASTCDParameter a) {
    visitor.startVisit(a.getType());
    if (a.isEllipsis()) {
      printer.print("...");
    }
    printer.print(" ");
    printer.print(a.getName());
  }
  
  /**
   * Prints a list of parameters
   * 
   * @param a list of parameters
   */
  public void ownVisit(ASTCDParameterList a) {
    printList(a.iterator(), ", ");
  }
  
  /**
   * Prints an attribute of class or interface in a class diagram
   * 
   * @param a attribute
   */
  public void ownVisit(ASTCDAttribute a) {
    // print modifier
    if (a.getModifier().isPresent()) {
      visitor.startVisit(a.getModifier().get());
    }
    // print type
    visitor.startVisit(a.getType());
    // print name
    printer.print(" " + a.getName());
    // print attribute value
    if (a.getValue().isPresent()) {
      printer.print(" = ");
      visitor.startVisit(a.getValue().get().getLiteral());
    }
    printer.println(";");
  }
  
  /**
   * Prints a qualifier of an association in a class diagram
   * 
   * @param a qualifier
   */
  public void ownVisit(ASTCDQualifier a) {
    if (a.getName().isPresent()) {
      printer.print(a.getName().get());
    }
    else {
      if (a.getType().isPresent()) {
        visitor.startVisit(a.getType().get());
      }
    }
  }
  
  /**
   * Prints an association, aggregation or composition in a class diagram
   * 
   * @param a association, aggregation or composition
   */
  public void ownVisit(ASTCDAssociation a) {
    printer.println();
    // print stereotype
    if (a.getStereotype().isPresent()) {
      visitor.startVisit(a.getStereotype().get());
      printer.print(" ");
    }
    // print type of the link
    if (a.isAssociation()) {
      printer.print("association ");
    }
    else if (a.isComposition()) {
      printer.print("composition ");
    }
    // print name
    if (a.isDerived()) {
      printer.print("/");
    }
    if (a.getName().isPresent()) {
      printer.print(a.getName().get() + " ");
    }
    // print left modifier
    if (a.getLeftModifier().isPresent())
      visitor.startVisit(a.getLeftModifier().get());
    // print left cardinality
    if (a.getLeftCardinality().isPresent()) {
      visitor.startVisit(a.getLeftCardinality().get());
      printer.print(" ");
    }
    // print left link class
    visitor.startVisit(a.getLeftReferenceName());
    printer.print(" ");
    // print left qualifier
    if (a.getLeftQualifier().isPresent()) {
      printer.print("[");
      visitor.startVisit(a.getLeftQualifier().get());
      printer.print("] ");
    }
    // print left role
    if (a.getLeftRole().isPresent()) {
      printer.print("(");
      printer.print(a.getLeftRole().get());
      printer.print(") ");
    }
    // print arrow
    if (a.isLeftToRight()) {
      printer.print("->");
    }
    if (a.isRightToLeft()) {
      printer.print("<-");
    }
    if (a.isBidirectional()) {
      printer.print("<->");
    }
    if (a.isSimple()) {
      printer.print("--");
    }
    // print right role
    if (a.getRightRole().isPresent()) {
      printer.print(" (");
      printer.print(a.getRightRole().get());
      printer.print(")");
    }
    // print right qualifier
    if (a.getRightQualifier().isPresent()) {
      printer.print(" [");
      visitor.startVisit(a.getRightQualifier().get());
      printer.print("]");
    }
    // print right link class
    printer.print(" ");
    visitor.startVisit(a.getRightReferenceName());
    // print right cardinality
    if (a.getRightCardinality().isPresent()) {
      printer.print(" ");
      visitor.startVisit(a.getRightCardinality().get());
    }
    printer.print(" ");
    // print right modifier
    if (a.getRightModifier().isPresent()) {
      visitor.startVisit(a.getRightModifier().get());
    }
    printer.println(";");
  }
  
  public void ownVisit(ASTModifier a) {
    if (a.getStereotype().isPresent()) {
      visitor.startVisit(a.getStereotype().get());
    }
    if (a.isAbstract()) {
      printer.print("abstract ");
    }
    if (a.isFinal()) {
      printer.print("final ");
    }
    if (a.isStatic()) {
      printer.print("static ");
    }
    if (a.isPrivate()) {
      printer.print("private ");
    }
    if (a.isProtected()) {
      printer.print("protected ");
    }
    if (a.isPublic()) {
      printer.print("public ");
    }
    if (a.isDerived()) {
      printer.print("derived ");
    }
  }
  
  /**
   * Prints a list of stereotype values
   * 
   * @param a list of stereotype values
   */
  public void ownVisit(ASTStereotypeList a) {
    printList(a.iterator(), " ");
  }

  /**
   * Prints the start of stereotypes
   * 
   * @param a stereotype
   */
  public void visit(ASTStereotype a) {
    printer.print("<<");
  }
  
  /**
   * Prints the end of stereotypes
   * 
   * @param a stereotype
   */
  public void endVisit(ASTStereotype a) {
    printer.print(">>");
  }

  /**
   * Prints stereotype values
   * 
   * @param a stereotype value
   */
  public void visit(ASTStereoValue a) {
    printer.print(a.getName());
  }
  
  /**
   * Prints cardinalities
   * 
   * @param a cardinality
   */
  public void ownVisit(ASTCardinality a) {
    if (a.isMany()) {
      printer.print(" [*] ");
    }
    if (a.isOne()) {
      printer.print(" [1] ");
    }
    if (a.isOneToMany()) {
      printer.print(" [1..*] ");
    }
    if (a.isOptional()) {
      printer.print(" [0..1] ");
    }
 
  }


}
