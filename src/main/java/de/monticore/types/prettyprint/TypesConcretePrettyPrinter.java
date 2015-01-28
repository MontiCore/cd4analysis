package de.monticore.types.prettyprint;

import mc.ast.ASTNode;
import mc.ast.AbstractVisitor;
import mc.ast.ConcretePrettyPrinter;
import mc.helper.IndentPrinter;
import de.monticore.types._ast.ASTArrayType;
import de.monticore.types._ast.ASTArrayTypeList;
import de.monticore.types._ast.ASTComplexReferenceType;
import de.monticore.types._ast.ASTPrimitiveType;
import de.monticore.types._ast.ASTPrimitiveTypeList;
import de.monticore.types._ast.ASTQualifiedName;
import de.monticore.types._ast.ASTQualifiedNameList;
import de.monticore.types._ast.ASTReferenceType;
import de.monticore.types._ast.ASTReferenceTypeList;
import de.monticore.types._ast.ASTReturnType;
import de.monticore.types._ast.ASTReturnTypeList;
import de.monticore.types._ast.ASTSimpleReferenceType;
import de.monticore.types._ast.ASTSimpleReferenceTypeList;
import de.monticore.types._ast.ASTType;
import de.monticore.types._ast.ASTTypeArgument;
import de.monticore.types._ast.ASTTypeArgumentList;
import de.monticore.types._ast.ASTTypeArguments;
import de.monticore.types._ast.ASTTypeArgumentsList;
import de.monticore.types._ast.ASTTypeList;
import de.monticore.types._ast.ASTTypeParameters;
import de.monticore.types._ast.ASTTypeParametersList;
import de.monticore.types._ast.ASTTypeVariableDeclaration;
import de.monticore.types._ast.ASTTypeVariableDeclarationList;
import de.monticore.types._ast.ASTVoidType;
import de.monticore.types._ast.ASTVoidTypeList;
import de.monticore.types._ast.ASTWildcardType;
import de.monticore.types._ast.ASTWildcardTypeList;

/**
 * Concrete pretty-printer for printing types.
 * 
 * @author Martin Schindler
 */
public class TypesConcretePrettyPrinter extends ConcretePrettyPrinter {
  
  /**
   * The following classes can be pretty-printed
   * 
   * @return classes can be pretty-printed by this class
   */
  @Override
  public Class<?>[] getResponsibleClasses() {
    return new Class[] { ASTArrayType.class, ASTArrayTypeList.class, ASTComplexReferenceType.class, ASTQualifiedName.class, ASTQualifiedNameList.class, ASTPrimitiveType.class, ASTPrimitiveTypeList.class, ASTReferenceType.class, ASTReferenceTypeList.class, ASTReturnType.class, ASTReturnTypeList.class, ASTSimpleReferenceType.class, ASTSimpleReferenceTypeList.class, ASTType.class, ASTTypeArgument.class, ASTTypeArgumentList.class, ASTTypeArguments.class, ASTTypeArgumentsList.class, ASTTypeList.class, ASTTypeParameters.class, ASTTypeParametersList.class, ASTTypeVariableDeclaration.class, ASTTypeVariableDeclarationList.class, ASTVoidType.class, ASTVoidTypeList.class, ASTWildcardType.class, ASTWildcardTypeList.class };
  }
  
  /**
   * Pretty-print class diagrams
   * 
   * @param a ASTNode to pretty print
   * @param printer Printer to use
   */
  @Override
  public void prettyPrint(ASTNode a, IndentPrinter printer) {
    // Run visitor
    AbstractVisitor.run(new TypesPrettyPrinterConcreteVisitor(printer), a);
  }
  
}
