/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4codebasis.prettyprint;

import de.monticore.cd.prettyprint.PrettyPrintUtil;
import de.monticore.cd4codebasis._ast.*;
import de.monticore.cd4codebasis._visitor.CD4CodeBasisHandler;
import de.monticore.cd4codebasis._visitor.CD4CodeBasisTraverser;
import de.monticore.cd4codebasis._visitor.CD4CodeBasisVisitor2;
import de.monticore.prettyprint.IndentPrinter;

public class CD4CodeBasisPrettyPrinter extends PrettyPrintUtil
    implements CD4CodeBasisVisitor2, CD4CodeBasisHandler {
  protected CD4CodeBasisTraverser traverser;

  public CD4CodeBasisPrettyPrinter() {
    this(new IndentPrinter());
  }

  public CD4CodeBasisPrettyPrinter(IndentPrinter printer) {
    super(printer);
  }

  @Override
  public CD4CodeBasisTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(CD4CodeBasisTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public void traverse(ASTCDThrowsDeclaration node) {
    print("throws ");
    printList(getTraverser(), node.getExceptionList().iterator(), ", ");
  }

  @Override
  public void traverse(ASTCDMethod node) {
    printPreComments(node);
    node.getModifier().accept(getTraverser());
    node.getMCReturnType().accept(getTraverser());
    print(" " + node.getName() + "(");
    printSeparatorCD4CodeBasis(getTraverser(), node.getCDParameterList().iterator(), ", ");
    print(")");
    if (node.isPresentCDThrowsDeclaration()) {
      print(" ");
      node.getCDThrowsDeclaration().accept(getTraverser());
    }
    println(";");
    printPostComments(node);
  }

  @Override
  public void traverse(ASTCDConstructor node) {
    printPreComments(node);
    node.getModifier().accept(getTraverser());
    print(node.getName() + "(");
    printSeparatorCD4CodeBasis(getTraverser(), node.getCDParameterList().iterator(), ", ");
    print(")");
    if (node.isPresentCDThrowsDeclaration()) {
      print(" ");
      node.getCDThrowsDeclaration().accept(getTraverser());
    }
    println(";");
    printPostComments(node);
  }

  @Override
  public void traverse(ASTCDParameter node) {
    node.getMCType().accept(getTraverser());
    if (node.isEllipsis()) {
      print("...");
    }
    print(" " + node.getName());
    if (node.isPresentDefaultValue()) {
      print(" = ");
      node.getDefaultValue().accept(getTraverser());
    }
  }

  @Override
  public void traverse(ASTCD4CodeEnumConstant node) {
    print(node.getName());
    if (node.isPresentArguments()) {
      node.getArguments().accept(getTraverser());
    }
  }
}
