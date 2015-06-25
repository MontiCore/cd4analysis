package de.monticore.umlcd4a.prettyprint;

import java.util.Iterator;

import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.prettyprint.TypesPrettyPrinterConcreteVisitor;
import de.monticore.types.types._ast.ASTImportStatement;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCD4AnalysisBase;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAssociation;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDConstructor;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDDefinition;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDEnum;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDEnumConstant;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDEnumConstantList;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDInterface;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDParameter;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDParameterList;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDQualifier;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCardinality;
import de.monticore.umlcd4a.cd4analysis._ast.ASTModifier;
import de.monticore.umlcd4a.cd4analysis._ast.ASTStereoValue;
import de.monticore.umlcd4a.cd4analysis._ast.ASTStereoValueList;
import de.monticore.umlcd4a.cd4analysis._ast.ASTStereotype;
import de.monticore.umlcd4a.cd4analysis._ast.ASTStereotypeList;
import de.monticore.umlcd4a.cd4analysis._visitor.CD4AnalysisVisitor;
import de.se_rwth.commons.Names;

/**
 * This class is responsible for pretty-printing class diagrams. It is implemented using the Visitor
 * pattern. The Visitor pattern traverses a tree in depth first, the visit and ownVisit-methods are
 * called when a node is traversed, the endVisit methods are called when the whole subtree of a node
 * has been traversed. The ownVisit-Methods stop the automatic traversal order and allow to
 * explictly visit subtrees by calling getVisitor().startVisit(ASTNode)
 * 
 * @author Martin Schindler
 */
public class CDPrettyPrinterConcreteVisitor extends TypesPrettyPrinterConcreteVisitor implements CD4AnalysisVisitor {
  
  /**
   * Constructor.
   * 
   * @param parent the parent pretty printer, needed to give control to the embedded pretty printer
   * when embedding is detected.
   * @param printer the printer to write to.
   */
  public CDPrettyPrinterConcreteVisitor(IndentPrinter printer) {
    super(printer);
  }
  
  /**
   * Prints the compilation unit of a class diagram (start of the pretty print)
   * 
   * @param a CD compilation unit
   */
  @Override
  public void handle(ASTCDCompilationUnit unit) {
    if (unit.getPackage() != null && !unit.getPackage().isEmpty()) {
      printer
          .println("package " + Names.getQualifiedName(unit.getPackage()) + ";\n");
    }
    if (unit.getImportStatements() != null && !unit.getImportStatements().isEmpty()) {
      for (ASTImportStatement s : unit.getImportStatements()) {
        getPrinter().print("import " + Names.getQualifiedName(s.getImportList()));
        if (s.isStar()) {
          getPrinter().println(".*;");
        }
        else {
          getPrinter().println(";");
        }
      }
      getPrinter().println();
    }
    unit.getCDDefinition().accept(getRealThis());
  }
  
  /**
   * Prints the class diagram definition
   * 
   * @param a class diagram definition
   */
  @Override
  public void handle(ASTCDDefinition a) {
    
    // print classdiagram name and parameters
    getPrinter().print("classdiagram " + a.getName());
    // print body
    getPrinter().println("{");
    getPrinter().indent();
    a.getCDInterfaces().accept(getRealThis());
    a.getCDClasses().accept(getRealThis());
    a.getCDEnums().accept(getRealThis());
    a.getCDAssociations().accept(getRealThis());
    getPrinter().unindent();
    getPrinter().print("\n}\n");
  }
  
  /**
   * Prints a class in a class diagram
   * 
   * @param a class
   */
  @Override
  public void handle(ASTCDClass a) {
    getPrinter().println();
    // print completeness
    // print class modifier
    if (a.getModifier().isPresent()) {
      a.getModifier().get().accept(getRealThis());
    }
    // print class name
    getPrinter().print("class " + a.getName());
    // print generic type parameters
    // print superclasses
    if (a.getSuperclass().isPresent()) {
      getPrinter().print(" extends ");
      a.getSuperclass().get().accept(getRealThis());
    }
    // print interfaces
    if (a.getInterfaces().size() != 0) {
      getPrinter().print(" implements ");
      a.getInterfaces().accept(getRealThis());
    }
    // print class body
    if (a.getCDConstructors().size() != 0 || a.getCDMethods().size() != 0
        || a.getCDAttributes().size() != 0) {
      getPrinter().println("{");
      getPrinter().indent();
      a.getCDAttributes().accept(getRealThis());
      a.getCDConstructors().accept(getRealThis());
      a.getCDMethods().accept(getRealThis());
      getPrinter().unindent();
      getPrinter().println("}");
    }
    else {
      getPrinter().println(";");
    }
  }
  
  /**
   * Prints an interface in a class diagram
   * 
   * @param a interface
   */
  @Override
  public void handle(ASTCDInterface a) {
    getPrinter().println();
    // print modifier
    if (a.getModifier().isPresent()) {
      a.getModifier().get().accept(getRealThis());
    }
    // print interface name
    getPrinter().print("interface " + a.getName());
    // print implemented interfaces
    if (a.getInterfaces().size() != 0) {
      getPrinter().print(" extends ");
      a.getInterfaces().accept(getRealThis());
    }
    // print interface body
    if (a.getCDMethods().size() != 0 || a.getCDAttributes().size() != 0) {
      getPrinter().println("{");
      getPrinter().indent();
      a.getCDAttributes().accept(getRealThis());
      a.getCDMethods().accept(getRealThis());
      getPrinter().unindent();
      getPrinter().println("}");
    }
    else {
      getPrinter().println(";");
    }
  }
  
  /**
   * Prints an enum in a class diagram
   * 
   * @param a enum
   */
  @Override
  public void handle(ASTCDEnum a) {
    getPrinter().println();
    // print enum modifier
    if (a.getModifier().isPresent()) {
      a.getModifier().get().accept(getRealThis());
    }
    // print enum name
    getPrinter().print("enum " + a.getName());
    // print interfaces
    if (a.getInterfaces().size() != 0) {
      getPrinter().print(" implements ");
      a.getInterfaces().accept(getRealThis());
    }
    // print enum body
    if (a.getCDEnumConstants().size() != 0 || a.getCDConstructors().size() != 0
        || a.getCDMethods().size() != 0) {
      getPrinter().println("{");
      getPrinter().indent();
      if (a.getCDEnumConstants().size() != 0) {
        a.getCDEnumConstants().accept(getRealThis());
        getPrinter().println(";");
      }
      if (a.getCDConstructors().size() != 0 || a.getCDMethods().size() != 0) {
        getPrinter().println();
        a.getCDConstructors().accept(getRealThis());
        a.getCDMethods().accept(getRealThis());
      }
      getPrinter().unindent();
      getPrinter().println("}");
    }
    else {
      getPrinter().println(";");
    }
  }
  
  /**
   * Prints an enum constant in a class diagram
   * 
   * @param a enum constant
   */
  @Override
  public void handle(ASTCDEnumConstant a) {
    // print enum name
    getPrinter().print(a.getName());
    // print parameters
    if (!a.getCDEnumParameters().isEmpty()) {
      getPrinter().print("(");
      printSeparator(a.getCDEnumParameters().iterator(), ", ");
      getPrinter().print(")");
    }
  }
  
  /**
   * Prints a list of enum constants in a class diagram
   * 
   * @param a list of enum constants
   */
  @Override
  public void handle(ASTCDEnumConstantList a) {
    printSeparator(a.iterator(), ",\n");
  }
  
  /**
   * Prints a method of a class in a class diagram
   * 
   * @param a method
   */
  @Override
  public void handle(ASTCDMethod a) {
    CommentPrettyPrinter.printPreComments(a, printer);
    
    // print modifier
    a.getModifier().accept(getRealThis());
    // print generics
    // print return type
    a.getReturnType().accept(getRealThis());
    // print name
    getPrinter().print(" " + a.getName());
    // print parameters
    getPrinter().print("(");
    a.getCDParameters().accept(getRealThis());
    getPrinter().print(")");
    // print exception
    if (a.getExceptions().size() != 0) {
      getPrinter().print(" throws ");
      a.getExceptions().accept(getRealThis());
    }
    getPrinter().println(";");
    CommentPrettyPrinter.printPostComments(a, printer);
  }
  
  /**
   * Prints a constructor of a class in a class diagram
   * 
   * @param a constructor
   */
  @Override
  public void handle(ASTCDConstructor a) {
    // print modifier
    a.getModifier().accept(getRealThis());
    // print name
    getPrinter().print(a.getName());
    // print parameters
    getPrinter().print("(");
    a.getCDParameters().accept(getRealThis());
    getPrinter().print(")");
    // print exception
    if (a.getExceptions().size() != 0) {
      getPrinter().print(" throws ");
      a.getExceptions().accept(getRealThis());
    }
    getPrinter().println(";");
  }
  
  /**
   * Prints a parameter
   * 
   * @param a parameter
   */
  @Override
  public void handle(ASTCDParameter a) {
    a.getType().accept(getRealThis());
    if (a.isEllipsis()) {
      getPrinter().print("...");
    }
    getPrinter().print(" ");
    getPrinter().print(a.getName());
  }
  
  /**
   * Prints a list of parameters
   * 
   * @param a list of parameters
   */
  @Override
  public void handle(ASTCDParameterList a) {
    printSeparator(a.iterator(), ", ");
  }
  
  /**
   * Prints an attribute of class or interface in a class diagram
   * 
   * @param a attribute
   */
  @Override
  public void handle(ASTCDAttribute a) {
    // print modifier
    if (a.getModifier().isPresent()) {
      a.getModifier().get().accept(getRealThis());
    }
    // print type
    a.getType().accept(getRealThis());
    // print name
    getPrinter().print(" " + a.getName());
    // print attribute value
    if (a.getValue().isPresent()) {
      getPrinter().print(" = ");
      a.getValue().get().getSignedLiteral().accept(getRealThis());
    }
    getPrinter().println(";");
  }
  
  /**
   * Prints a qualifier of an association in a class diagram
   * 
   * @param a qualifier
   */
  @Override
  public void handle(ASTCDQualifier a) {
    if (a.getName().isPresent()) {
      getPrinter().print("[[");
      getPrinter().print(a.getName().get());
      getPrinter().print("]]");
    }
    else {
      if (a.getType().isPresent()) {
        getPrinter().print("[");
        a.getType().get().accept(getRealThis());
        getPrinter().print("]");
      }
    }
  }
  
  /**
   * Prints an association, aggregation or composition in a class diagram
   * 
   * @param a association, aggregation or composition
   */
  @Override
  public void handle(ASTCDAssociation a) {
    getPrinter().println();
    // print stereotype
    if (a.getStereotype().isPresent()) {
      a.getStereotype().get().accept(getRealThis());
      getPrinter().print(" ");
    }
    // print type of the link
    if (a.isAssociation()) {
      getPrinter().print("association ");
    }
    else if (a.isComposition()) {
      getPrinter().print("composition ");
    }
    // print name
    if (a.isDerived()) {
      getPrinter().print("/");
    }
    if (a.getName().isPresent()) {
      getPrinter().print(a.getName().get() + " ");
    }
    // print left modifier
    if (a.getLeftModifier().isPresent())
      a.getLeftModifier().get().accept(getRealThis());
    // print left cardinality
    if (a.getLeftCardinality().isPresent()) {
      a.getLeftCardinality().get().accept(getRealThis());
      getPrinter().print(" ");
    }
    // print left link class
    a.getLeftReferenceName().accept(getRealThis());
    getPrinter().print(" ");
    // print left qualifier
    if (a.getLeftQualifier().isPresent()) {
      a.getLeftQualifier().get().accept(getRealThis());
    }
    // print left role
    if (a.getLeftRole().isPresent()) {
      getPrinter().print("(");
      getPrinter().print(a.getLeftRole().get());
      getPrinter().print(") ");
    }
    // print arrow
    if (a.isLeftToRight()) {
      getPrinter().print("->");
    }
    if (a.isRightToLeft()) {
      getPrinter().print("<-");
    }
    if (a.isBidirectional()) {
      getPrinter().print("<->");
    }
    if (a.isUnspecified()) {
      getPrinter().print("--");
    }
    // print right role
    if (a.getRightRole().isPresent()) {
      getPrinter().print(" (");
      getPrinter().print(a.getRightRole().get());
      getPrinter().print(")");
    }
    // print right qualifier
    if (a.getRightQualifier().isPresent()) {
      a.getRightQualifier().get().accept(getRealThis());
    }
    // print right link class
    getPrinter().print(" ");
    a.getRightReferenceName().accept(getRealThis());
    // print right cardinality
    if (a.getRightCardinality().isPresent()) {
      getPrinter().print(" ");
      a.getRightCardinality().get().accept(getRealThis());
    }
    getPrinter().print(" ");
    // print right modifier
    if (a.getRightModifier().isPresent()) {
      a.getRightModifier().get().accept(getRealThis());
    }
    getPrinter().println(";");
  }
  
  @Override
  public void handle(ASTModifier a) {
    if (a.getStereotype().isPresent()) {
      a.getStereotype().get().accept(getRealThis());
    }
    if (a.isAbstract()) {
      getPrinter().print("abstract ");
    }
    if (a.isFinal()) {
      getPrinter().print("final ");
    }
    if (a.isStatic()) {
      getPrinter().print("static ");
    }
    if (a.isPrivate()) {
      getPrinter().print("private ");
    }
    if (a.isProtected()) {
      getPrinter().print("protected ");
    }
    if (a.isPublic()) {
      getPrinter().print("public ");
    }
    if (a.isDerived()) {
      getPrinter().print("derived ");
    }
  }
  
  /**
   * Prints a list of stereotype values
   * 
   * @param a list of stereotype values
   */
  @Override
  public void handle(ASTStereotypeList a) {
    printSeparator(a.iterator(), " ");
  }

  /**
   * Prints the start of stereotypes
   * 
   * @param a stereotype
   */
  @Override
  public void visit(ASTStereotype a) {
    getPrinter().print("<<");
  }
  
  /**
   * Prints the end of stereotypes
   * 
   * @param a stereotype
   */
  @Override
  public void endVisit(ASTStereotype a) {
    getPrinter().print(">>");
  }

  /**
   * Prints stereovaluelist and adds a delimiter between the items
   * 
   * @param a stereotype value
   */
  @Override
  public void handle(ASTStereoValueList a) {
    printSeparator(a.iterator(), ",");
  }

  /**
   * Prints stereotype values
   * 
   * @param a stereotype value
   */
  @Override
  public void visit(ASTStereoValue a) {
    getPrinter().print(a.getName());
    if (a.getValue().isPresent()) {
      printer.print("=\"" + a.getValue().get() + "\"");
    }
  }
  
  /**
   * Prints cardinalities
   * 
   * @param a cardinality
   */
  @Override
  public void handle(ASTCardinality a) {
    if (a.isMany()) {
      getPrinter().print(" [*] ");
    }
    if (a.isOne()) {
      getPrinter().print(" [1] ");
    }
    if (a.isOneToMany()) {
      getPrinter().print(" [1..*] ");
    }
    if (a.isOptional()) {
      getPrinter().print(" [0..1] ");
    }
 
  }

  /**
   * Prints a list of ASTQualifiedNames in an ownVisit method
   * 
   * @param iter iterator for the list of ASTQualifiedNames
   * @param seperator string for seperating the ASTQualifiedNames
   */
  private void printSeparator(Iterator<? extends ASTCD4AnalysisBase> iter, String seperator) {
    // print by iterate through all items
    String sep = "";
    while (iter.hasNext()) {
      getPrinter().print(sep);
      iter.next().accept(getRealThis());
      sep = seperator;
    }
  }
  
  /**
   * This method prettyprints a given node from class diagram.
   * 
   * @param a A node from class diagram.
   * @return String representation.
   */
  public String prettyprint(ASTCD4AnalysisBase a) {
    getPrinter().clearBuffer();
    a.accept(getRealThis());
    return getPrinter().getContent();
  }

}
