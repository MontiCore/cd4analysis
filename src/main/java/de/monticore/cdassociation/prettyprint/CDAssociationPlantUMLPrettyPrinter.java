/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdassociation.prettyprint;

import de.monticore.cd.plantuml.PlantUMLPrettyPrintUtil;
import de.monticore.cdassociation._ast.*;
import de.monticore.cdassociation._visitor.CDAssociationVisitor;
import de.monticore.prettyprint.IndentPrinter;

import java.util.Stack;

public class CDAssociationPlantUMLPrettyPrinter extends PlantUMLPrettyPrintUtil
    implements CDAssociationVisitor {
  protected CDAssociationVisitor realThis;
  protected Stack<Boolean> stackIsAssociation;
  protected Stack<Boolean> stackIsDerived;

  public CDAssociationPlantUMLPrettyPrinter() {
    this(new IndentPrinter());
  }

  public CDAssociationPlantUMLPrettyPrinter(IndentPrinter printer) {
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
    stackIsAssociation.push(true);
  }

  @Override
  public void endVisit(ASTCDAssocTypeAssoc node) {
    stackIsAssociation.pop();
  }

  @Override
  public void visit(ASTCDAssocTypeComp node) {
    stackIsAssociation.push(false);
  }

  @Override
  public void endVisit(ASTCDAssocTypeComp node) {
    stackIsAssociation.pop();
  }

  @Override
  public void visit(ASTCDAssociation node) {
    printComment(node);
    node.getCDAssocType().accept(getRealThis());
    stackIsDerived.push(node.isDerived());
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
    if (plantUMLConfig.getShowAssoc() && node.isPresentName()) {
      print(" : " + shorten(node.getName()));
    }
    println();
    stackIsDerived.pop();
  }

  @Override
  public void visit(ASTCDLeftToRightDir node) {
    if (!stackIsDerived.isEmpty() && stackIsDerived.peek()) {
      print("..");
    }
    else {
      print("--");
    }
    if (!stackIsAssociation.isEmpty() && stackIsAssociation.peek()) {
      print("o");
    }
    else {
      print("*");
    }

    stackIsDerived.pop();
  }

  @Override
  public void visit(ASTCDRightToLeftDir node) {
    if (!stackIsAssociation.isEmpty() && stackIsAssociation.peek()) {
      print("o");
    }
    else {
      print("*");
    }
    if (!stackIsDerived.isEmpty() && stackIsDerived.peek()) {
      print("..");
    }
    else {
      print("--");
    }
  }

  @Override
  public void visit(ASTCDBiDir node) {
    if (!stackIsAssociation.isEmpty() && stackIsAssociation.peek()) {
      print("o");
    }
    else {
      print("*");
    }
    if (!stackIsDerived.isEmpty() && stackIsDerived.peek()) {
      print("..");
    }
    else {
      print("--");
    }
    if (!stackIsAssociation.isEmpty() && stackIsAssociation.peek()) {
      print("o");
    }
    else {
      print("*");
    }
  }

  @Override
  public void visit(ASTCDUnspecifiedDir node) {
    if (!stackIsDerived.isEmpty() && stackIsDerived.peek()) {
      print("..");
    }
    else {
      print("--");
    }
  }

  @Override
  public void handle(ASTCDAssocLeftSide node) {
    if (plantUMLConfig.getShowCard() && node.isPresentCDCardinality()) {
      node.getCDCardinality().accept(getRealThis());
      print(" ");
    }

    node.getMCQualifiedName().accept(getRealThis());
    print(" \"");

    if (plantUMLConfig.getShowRoles() && node.isPresentCDRole()) {
      node.getCDRole().accept(getRealThis());
    }
    if (node.isPresentCDOrdered()) {
      print(" <<");
      node.getCDOrdered().accept(getRealThis());
      print(">>");
    }
    print("\"");
  }

  @Override
  public void handle(ASTCDAssocRightSide node) {
    print("\"");
    if (node.isPresentCDOrdered()) {
      print(" <<");
      node.getCDOrdered().accept(getRealThis());
      print(">>");
    }
    if (plantUMLConfig.getShowRoles() && node.isPresentCDRole()) {
      node.getCDRole().accept(getRealThis());
    }
    print("\" ");
    node.getMCQualifiedName().accept(getRealThis());

    if (plantUMLConfig.getShowCard() && node.isPresentCDCardinality()) {
      print(" ");
      node.getCDCardinality().accept(getRealThis());
    }
  }

  @Override
  public void visit(ASTCDRole node) {
    print("(" + shorten(node.getName()) + ")");
  }

  @Override
  public void visit(ASTCDCardMult node) {
    print("*");
  }

  @Override
  public void visit(ASTCDCardOne node) {
    print("1");
  }

  @Override
  public void visit(ASTCDCardAtLeastOne node) {
    print("1..*");
  }

  @Override
  public void visit(ASTCDCardOpt node) {
    print("0..1");
  }
}
