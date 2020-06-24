/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4codebasis.prettyprint;

import de.monticore.cd.prettyprint.PrettyPrintUtil;
import de.monticore.cd4codebasis._ast.*;
import de.monticore.cd4codebasis._visitor.CD4CodeBasisVisitor;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

public class CD4CodeBasisPrettyPrinter extends PrettyPrintUtil
    implements CD4CodeBasisVisitor {
  protected CD4CodeBasisVisitor realThis;

  public CD4CodeBasisPrettyPrinter() {
    this(new IndentPrinter());
    setRealThis(this);
  }

  public CD4CodeBasisPrettyPrinter(IndentPrinter printer) {
    super(printer);
  }

  @Override
  public CD4CodeBasisVisitor getRealThis() {
    return realThis;
  }

  @Override
  public void setRealThis(CD4CodeBasisVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public void traverse(ASTCDThrowsDeclaration node) {
    print("throws ");
    printList(getRealThis(), node.getExceptionList().iterator(), ", ");
  }

  @Override
  public void traverse(ASTCDMethod node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getModifier().accept(getRealThis());
    node.getMCReturnType().accept(getRealThis());
    print(" " + node.getName() + "(");
    printSeparatorCD4CodeBasis(getRealThis(), node.getCDParameterList().iterator(), ", ");
    print(")");
    if (node.isPresentCDThrowsDeclaration()) {
      print(" ");
      node.getCDThrowsDeclaration().accept(getRealThis());
    }
    println(";");
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void traverse(ASTCDConstructor node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getModifier().accept(getRealThis());
    print(node.getName() + "(");
    printSeparatorCD4CodeBasis(getRealThis(), node.getCDParameterList().iterator(), ", ");
    print(")");
    if (node.isPresentCDThrowsDeclaration()) {
      print(" ");
      node.getCDThrowsDeclaration().accept(getRealThis());
    }
    println(";");
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void traverse(ASTCDParameter node) {
    node.getMCType().accept(getRealThis());
    if (node.isEllipsis()) {
      print("...");
    }
    print(" " + node.getName());
    if (node.isPresentDefaultValue()) {
      print(" = ");
      node.getDefaultValue().accept(getRealThis());
    }
  }

  @Override
  public void traverse(ASTCD4CodeEnumConstant node) {
    print(node.getName());
    if (node.isPresentArguments()) {
      node.getArguments().accept(getRealThis());
    }
  }
}
