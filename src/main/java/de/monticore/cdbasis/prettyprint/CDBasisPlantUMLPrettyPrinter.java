/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdbasis.prettyprint;

import de.monticore.cd.plantuml.PlantUMLConfig;
import de.monticore.cd.plantuml.PlantUMLPrettyPrintUtil;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdbasis._visitor.CDBasisVisitor;
import de.monticore.prettyprint.IndentPrinter;

public class CDBasisPlantUMLPrettyPrinter extends PlantUMLPrettyPrintUtil
    implements CDBasisVisitor {
  protected CDBasisVisitor realThis;

  public CDBasisPlantUMLPrettyPrinter() {
    this(new IndentPrinter(), new PlantUMLConfig());
  }

  public CDBasisPlantUMLPrettyPrinter(IndentPrinter printer, PlantUMLConfig config) {
    super(printer, config);
    setRealThis(this);
  }

  @Override
  public CDBasisVisitor getRealThis() {
    return realThis;
  }

  @Override
  public void setRealThis(CDBasisVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public void traverse(ASTCDCompilationUnit node) {
    printComment(node);

    if (null != node.getCDDefinition()) {
      node.getCDDefinition().accept(getRealThis());
    }
  }

  @Override
  public void visit(ASTCDDefinition node) {
    printComment(node, node.getName());
    print("@startuml");
    if (getPlantUMLConfig().getOrtho()) {
      getPrinter().print("\nskinparam linetype ortho");
    }
    if (getPlantUMLConfig().getNodesep() != -1) {
      getPrinter().print("\nskinparam nodesep " + getPlantUMLConfig().getNodesep());
    }
    if (getPlantUMLConfig().getRanksep() != -1) {
      getPrinter().print("\nskinparam ranksep " + getPlantUMLConfig().getRanksep());
    }
    println();
    indent();
  }

  @Override
  public void traverse(ASTCDDefinition node) {
    for (ASTCDElement element : node.getCDElementList()) {
      printComment(node);
      element.accept(getRealThis());
      println();
    }
  }

  @Override
  public void endVisit(ASTCDDefinition node) {
    // associations can not be printed in a package
    immediatelyPrintAssociations = true;
    associations.forEach(a -> a.accept(getRealThis()));

    unindent();
    println("@enduml");
  }

  @Override
  public void visit(ASTCDPackage node) {
    // TODO SVa: maybe that should be namespaces, so that we can have same class names
    print("package \"");
    print(node.getMCQualifiedName().getQName());
    println(" <<Frame>> \" {");
    indent();
  }

  @Override
  public void traverse(ASTCDPackage node) {
    node.streamCDElements().forEach(e -> e.accept(getRealThis()));
  }

  @Override
  public void endVisit(ASTCDPackage node) {
    unindent();
    println("}");
  }

  @Override
  public void visit(ASTCDInterfaceUsage node) {
    print(" implements ");
  }

  @Override
  public void traverse(ASTCDInterfaceUsage node) {
    // TODO SVa: the name should be the model-relative-name,
    //  so that the packages work correctly in any order
    printList(getRealThis(), node.getInterfaceList().iterator(), ", ");
  }

  @Override
  public void visit(ASTCDExtendUsage node) {
    print(" extends ");
  }

  @Override
  public void traverse(ASTCDExtendUsage node) {
    // TODO SVa: the name should be the model-relative-name,
    //  so that the packages work correctly in any order
    printList(getRealThis(), node.getSuperclasList().iterator(), ", ");
  }

  @Override
  public void visit(ASTCDClass node) {
    nameStack.push(node.getName());

    printComment(node);

    if (plantUMLConfig.getShowModifier()) {
      node.getModifier().accept(getRealThis());
    }

    print("class " + node.getName());
    if (node.isPresentCDExtendUsage()) {
      node.getCDExtendUsage().accept(getRealThis());
    }
    if (node.isPresentCDInterfaceUsage()) {
      node.getCDInterfaceUsage().accept(getRealThis());
    }

    if (plantUMLConfig.getShowAtt() && !node.isEmptyCDMembers()) {
      println(" {");
      indent();
    }
  }

  @Override
  public void traverse(ASTCDClass node) {
    node.getCDMemberList().forEach(m -> m.accept(getRealThis()));
  }

  @Override
  public void endVisit(ASTCDClass node) {
    if (plantUMLConfig.getShowAtt() && !node.isEmptyCDMembers()) {
      unindent();
      println("}");
    }
    else {
      println();
    }

    nameStack.pop();
  }

  @Override
  public void handle(ASTCDAttribute node) {
    if (plantUMLConfig.getShowAtt()) {
      print("{field} "); // be sure that this is handled as a field
      if (plantUMLConfig.getShowModifier()) {
        node.getModifier().accept(getRealThis());
      }
      node.getMCType().accept(getRealThis());
      println(" " + node.getName());
    }
  }

}
