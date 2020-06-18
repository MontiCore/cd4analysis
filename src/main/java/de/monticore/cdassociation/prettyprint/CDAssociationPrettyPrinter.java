/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdassociation.prettyprint;

import de.monticore.cd.prettyprint.PrettyPrintUtil;
import de.monticore.cdassociation._ast.*;
import de.monticore.cdassociation._visitor.CDAssociationVisitor;
import de.monticore.prettyprint.IndentPrinter;

public class CDAssociationPrettyPrinter extends PrettyPrintUtil
    implements CDAssociationVisitor {
  protected IndentPrinter printer;
  protected CDAssociationVisitor realThis;

  public CDAssociationPrettyPrinter() {
    this(new IndentPrinter());
  }

  public CDAssociationPrettyPrinter(IndentPrinter printer) {
    this.printer = printer;
  }

  @Override
  public IndentPrinter getPrinter() {
    return printer;
  }

  public void setPrinter(IndentPrinter printer) {
    this.printer = printer;
  }

  @Override
  public CDAssociationVisitor getRealThis() {
    return realThis;
  }

  public void setRealThis(CDAssociationVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public void visit(ASTCDAssociation node) {
    printPreComments(node.iterator_PreComments());

    node.getModifier().accept(getRealThis());

    if (node.isAssociation()) {
      getPrinter().print("association");
    }
    else if (node.isComposition()) {
      getPrinter().print("composition");
    }
    getPrinter().print(" ");

    if (node.isDerived()) {
      getPrinter().print("/");
    }
    if (node.isPresentName()) {
      getPrinter().print(node.getName());
    }
    getPrinter().print(" ");
  }

  @Override
  public void traverse(ASTCDAssociation node) {
    node.getLeft().accept(getRealThis());
    getPrinter().print(" ");
    node.getCDAssociationDirection().accept(getRealThis());
    getPrinter().print(" ");
    node.getRight().accept(getRealThis());
  }

  @Override
  public void endVisit(ASTCDAssociation node) {
    getPrinter().print(";");
    printPostComments(node.get_PostCommentList().iterator());
  }

  @Override
  public void visit(ASTCDLeftToRightDir node) {
    getPrinter().print("->");
  }

  @Override
  public void visit(ASTCDRightToLeftDir node) {
    getPrinter().print("<-");
  }

  @Override
  public void visit(ASTCDBiDir node) {
    getPrinter().print("<->");
  }

  @Override
  public void visit(ASTCDUnspecifiedDir node) {
    getPrinter().print("--");
  }

  @Override
  public void handle(ASTCDAssociationLeftSide node) {
    if (node.isPresentCDOrdered()) {
      node.getCDOrdered().accept(getRealThis());
      getPrinter().print(" ");
    }

    node.getModifier().accept(getRealThis());

    if (node.isPresentCDCardinality()) {
      node.getCDCardinality().accept(getRealThis());
      getPrinter().print(" ");
    }

    node.getMCQualifiedName().accept(getRealThis());
    getPrinter().print(" ");

    if (node.isPresentCDQualifier()) {

    }
  }
}
