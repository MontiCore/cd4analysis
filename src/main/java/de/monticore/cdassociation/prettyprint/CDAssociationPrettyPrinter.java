/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdassociation.prettyprint;

import de.monticore.cd.prettyprint.PrettyPrintUtil;
import de.monticore.cdassociation._ast.*;
import de.monticore.cdassociation._visitor.CDAssociationHandler;
import de.monticore.cdassociation._visitor.CDAssociationTraverser;
import de.monticore.cdassociation._visitor.CDAssociationVisitor2;
import de.monticore.prettyprint.IndentPrinter;

public class CDAssociationPrettyPrinter extends PrettyPrintUtil
    implements CDAssociationVisitor2, CDAssociationHandler {
  protected CDAssociationTraverser traverser;

  public CDAssociationPrettyPrinter() {
    this(new IndentPrinter());
  }

  public CDAssociationPrettyPrinter(IndentPrinter printer) {
    super(printer);
  }

  @Override
  public CDAssociationTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(CDAssociationTraverser traverser) {
    this.traverser = traverser;
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

    node.getModifier().accept(getTraverser());

    node.getCDAssocType().accept(getTraverser());
    print(" ");

    if (node.isPresentName()) {
      print(node.getName());
    }
    print(" ");
  }

  @Override
  public void traverse(ASTCDAssociation node) {
    node.getLeft().accept(getTraverser());
    print(" ");
    node.getCDAssocDir().accept(getTraverser());
    print(" ");
    node.getRight().accept(getTraverser());
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
      node.getCDOrdered().accept(getTraverser());
      print(" ");
    }

    node.getModifier().accept(getTraverser());

    if (node.isPresentCDCardinality()) {
      node.getCDCardinality().accept(getTraverser());
      print(" ");
    }

    node.getMCQualifiedType().accept(getTraverser());
    print(" ");

    if (node.isPresentCDQualifier()) {
      node.getCDQualifier().accept(getTraverser());
    }

    if (node.isPresentCDRole()) {
      print(" ");
      node.getCDRole().accept(getTraverser());
    }
  }

  @Override
  public void handle(ASTCDAssocRightSide node) {
    if (node.isPresentCDRole()) {
      node.getCDRole().accept(getTraverser());
      print(" ");
    }

    if (node.isPresentCDQualifier()) {
      node.getCDQualifier().accept(getTraverser());
      print(" ");
    }

    node.getMCQualifiedType().accept(getTraverser());
    print(" ");

    if (node.isPresentCDCardinality()) {
      node.getCDCardinality().accept(getTraverser());
      print(" ");
    }

    node.getModifier().accept(getTraverser());

    if (node.isPresentCDOrdered()) {
      node.getCDOrdered().accept(getTraverser());
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
      node.getByType().accept(getTraverser());
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
    node.getCDAssocRightSide().accept(getTraverser());
    print(";");
    printPostComments(node);
    println();
  }

  public String prettyprint(ASTCDAssociation node) {
    getPrinter().clearBuffer();
    node.accept(getTraverser());
    return getPrinter().getContent();
  }
}
