/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdassociation.prettyprint;

import de.monticore.cd.prettyprint.PrettyPrintUtil;
import de.monticore.cdassociation._ast.*;
import de.monticore.cdassociation._visitor.CDAssociationVisitor;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

public class CDAssociationPrettyPrinter extends PrettyPrintUtil
    implements CDAssociationVisitor {
  protected CDAssociationVisitor realThis;

  public CDAssociationPrettyPrinter() {
    this(new IndentPrinter());
  }

  public CDAssociationPrettyPrinter(IndentPrinter printer) {
    super(printer);
  }

  @Override
  public CDAssociationVisitor getRealThis() {
    return realThis;
  }

  public void setRealThis(CDAssociationVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public void visit(ASTCDAssocTypeAssoc node) {
    print("association");
  }

  @Override
  public void visit(ASTCDAssocTypeComp node) {
    print("composition");
  }

  @Override
  public void visit(ASTCDAssociation node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());

    node.getModifier().accept(getRealThis());

    node.getCDAssocType().accept(getRealThis());
    print(" ");

    if (node.isDerived()) {
      print("/");
    }
    if (node.isPresentName()) {
      print(node.getName());
    }
    print(" ");
  }

  @Override
  public void traverse(ASTCDAssociation node) {
    node.getLeft().accept(getRealThis());
    print(" ");
    node.getCDAssociationDirection().accept(getRealThis());
    print(" ");
    node.getRight().accept(getRealThis());
  }

  @Override
  public void endVisit(ASTCDAssociation node) {
    print(";");
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void visit(ASTCDLeftToRightDir node) {
    print("->");
  }

  @Override
  public void visit(ASTCDRightToLeftDir node) {
    print("<-");
  }

  @Override
  public void visit(ASTCDBiDir node) {
    print("<->");
  }

  @Override
  public void visit(ASTCDUnspecifiedDir node) {
    print("--");
  }

  @Override
  public void handle(ASTCDAssociationLeftSide node) {
    if (node.isPresentCDOrdered()) {
      node.getCDOrdered().accept(getRealThis());
      print(" ");
    }

    node.getModifier().accept(getRealThis());

    if (node.isPresentCDCardinality()) {
      node.getCDCardinality().accept(getRealThis());
      print(" ");
    }

    node.getMCQualifiedName().accept(getRealThis());
    print(" ");

    if (node.isPresentCDQualifier()) {

    }
  }
}
