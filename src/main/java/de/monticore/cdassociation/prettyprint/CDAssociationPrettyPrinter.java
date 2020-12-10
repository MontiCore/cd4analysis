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
  protected CDAssociationVisitor realThis;

  public CDAssociationPrettyPrinter() {
    this(new IndentPrinter());
  }

  public CDAssociationPrettyPrinter(IndentPrinter printer) {
    super(printer);
    setRealThis(this);
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
    printPreComments(node);

    node.getModifier().accept(getRealThis());

    node.getCDAssocType().accept(getRealThis());
    print(" ");

    if (node.isPresentName()) {
      print(node.getName());
    }
    print(" ");
  }

  @Override
  public void traverse(ASTCDAssociation node) {
    node.getLeft().accept(getRealThis());
    print(" ");
    node.getCDAssocDir().accept(getRealThis());
    print(" ");
    node.getRight().accept(getRealThis());
  }

  @Override
  public void endVisit(ASTCDAssociation node) {
    print(";");
    printPostComments(node);
    println();
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
  public void handle(ASTCDAssocLeftSide node) {
    if (node.isPresentCDOrdered()) {
      node.getCDOrdered().accept(getRealThis());
      print(" ");
    }

    node.getModifier().accept(getRealThis());

    if (node.isPresentCDCardinality()) {
      node.getCDCardinality().accept(getRealThis());
      print(" ");
    }

    node.getMCQualifiedType().accept(getRealThis());
    print(" ");

    if (node.isPresentCDQualifier()) {
      node.getCDQualifier().accept(getRealThis());
    }

    if (node.isPresentCDRole()) {
      print(" ");
      node.getCDRole().accept(getRealThis());
    }
  }

  @Override
  public void handle(ASTCDAssocRightSide node) {
    if (node.isPresentCDRole()) {
      node.getCDRole().accept(getRealThis());
      print(" ");
    }

    if (node.isPresentCDQualifier()) {
      node.getCDQualifier().accept(getRealThis());
      print(" ");
    }

    node.getMCQualifiedType().accept(getRealThis());
    print(" ");

    if (node.isPresentCDCardinality()) {
      node.getCDCardinality().accept(getRealThis());
      print(" ");
    }

    node.getModifier().accept(getRealThis());

    if (node.isPresentCDOrdered()) {
      node.getCDOrdered().accept(getRealThis());
    }
  }

  @Override
  public void visit(ASTCDRole node) {
    print("(" + node.getName() + ")");
  }

  @Override
  public void visit(ASTCDCardMult node) {
    print("[*]");
  }

  @Override
  public void visit(ASTCDCardOne node) {
    print("[1]");
  }

  @Override
  public void visit(ASTCDCardAtLeastOne node) {
    print("[1..*]");
  }

  @Override
  public void visit(ASTCDCardOpt node) {
    print("[0..1]");
  }

  @Override
  public void handle(ASTCDQualifier node) {
    if (node.isPresentByAttributeName()) {
      print("[[" + node.getByAttributeName() + "]]");
    }

    if (node.isPresentByType()) {
      print("[");
      node.getByType().accept(getRealThis());
      print("]");
    }
  }

  @Override
  public void visit(ASTCDOrdered node) {
    print("{ordered}");
  }

  @Override
  public void handle(ASTCDDirectComposition node) {
    printPreComments(node);
    print("-> ");
    node.getCDAssocRightSide().accept(getRealThis());
    print(";");
    printPostComments(node);
    println();
  }

  public String prettyprint(ASTCDAssociation node) {
    getPrinter().clearBuffer();
    node.accept(getRealThis());
    return getPrinter().getContent();
  }
}
