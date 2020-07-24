/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdassociation.prettyprint;

import de.monticore.cd.plantuml.PlantUMLPrettyPrintUtil;
import de.monticore.cdassociation._ast.*;
import de.monticore.cdassociation._visitor.CDAssociationVisitor;

import java.util.Stack;

public class CDAssociationPlantUMLPrettyPrinter extends PlantUMLPrettyPrintUtil
    implements CDAssociationVisitor {
  protected CDAssociationVisitor realThis;
  protected Stack<Boolean> stackIsAssociation;

  public CDAssociationPlantUMLPrettyPrinter() {
    this(new PlantUMLPrettyPrintUtil());
  }

  public CDAssociationPlantUMLPrettyPrinter(PlantUMLPrettyPrintUtil util) {
    super(util);
    setRealThis(this);
    this.stackIsAssociation = new Stack<>();
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
    if (immediatelyPrintAssociations.get()) {
      printComment(node);
      node.getCDAssocType().accept(getRealThis());
    }
  }

  @Override
  public void traverse(ASTCDAssociation node) {
    if (plantUMLConfig.getShowAssoc()) {
      if (immediatelyPrintAssociations.get()) {
        node.getLeft().accept(getRealThis());
        print(" ");
        node.getCDAssocDir().accept(getRealThis());
        print(" ");
        node.getRight().accept(getRealThis());
      }
      else {
        associations.add(node);
      }
    }
  }

  @Override
  public void endVisit(ASTCDAssociation node) {
    if (immediatelyPrintAssociations.get()) {
      if (plantUMLConfig.getShowAssoc() && node.isPresentName()) {
        print(" : " + shorten(node.getName()));
      }
      println();
    }
  }

  @Override
  public void visit(ASTCDLeftToRightDir node) {
    print("--");
    if (!stackIsAssociation.isEmpty() && stackIsAssociation.peek()) {
      print("o");
    }
    else {
      print("*");
    }
  }

  @Override
  public void visit(ASTCDRightToLeftDir node) {
    if (!stackIsAssociation.isEmpty() && stackIsAssociation.peek()) {
      print("o");
    }
    else {
      print("*");
    }
    print("--");
  }

  @Override
  public void visit(ASTCDBiDir node) {
    if (!stackIsAssociation.isEmpty() && stackIsAssociation.peek()) {
      print("o");
    }
    else {
      print("*");
    }
    print("--");
    if (!stackIsAssociation.isEmpty() && stackIsAssociation.peek()) {
      print("o");
    }
    else {
      print("*");
    }
  }

  @Override
  public void visit(ASTCDUnspecifiedDir node) {
    print("--");
  }

  @Override
  public void handle(ASTCDAssocLeftSide node) {
    print(node.getCDRole().getSymbol().getType().getTypeInfo().getFullName());

    if ((plantUMLConfig.getShowCard() && node.isPresentCDCardinality()) || plantUMLConfig.getShowRoles() && (node.isPresentCDRole() || node.isPresentCDOrdered())) {
      print(" \"");
    }

    if (plantUMLConfig.getShowCard() && node.isPresentCDCardinality()) {
      node.getCDCardinality().accept(getRealThis());
      print(" ");
    }

    if (plantUMLConfig.getShowRoles() && node.isPresentCDRole()) {
      node.getCDRole().accept(getRealThis());
    }
    if (node.isPresentCDOrdered()) {
      print(" <<");
      node.getCDOrdered().accept(getRealThis());
      print(">>");
    }

    if ((plantUMLConfig.getShowCard() && node.isPresentCDCardinality()) || plantUMLConfig.getShowRoles() && (node.isPresentCDRole() || node.isPresentCDOrdered())) {
      print("\"");
    }
  }

  @Override
  public void handle(ASTCDAssocRightSide node) {
    if ((plantUMLConfig.getShowCard() && node.isPresentCDCardinality()) || plantUMLConfig.getShowRoles() && (node.isPresentCDRole() || node.isPresentCDOrdered())) {
      print("\"");
    }

    if (node.isPresentCDOrdered()) {
      print(" <<");
      node.getCDOrdered().accept(getRealThis());
      print(">>");
    }
    if (plantUMLConfig.getShowRoles() && node.isPresentCDRole()) {
      node.getCDRole().accept(getRealThis());
    }

    if (plantUMLConfig.getShowCard() && node.isPresentCDCardinality()) {
      print(" ");
      node.getCDCardinality().accept(getRealThis());
    }

    if ((plantUMLConfig.getShowCard() && node.isPresentCDCardinality()) || plantUMLConfig.getShowRoles() && (node.isPresentCDRole() || node.isPresentCDOrdered())) {
      print("\" ");
    }

    print(node.getCDRole().getSymbol().getType().getTypeInfo().getFullName());
  }

  @Override
  public void visit(ASTCDRole node) {
    print(shorten(node.getName()));
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

  @Override
  public void visit(ASTCDOrdered node) {
    print("ordered");
  }
}
