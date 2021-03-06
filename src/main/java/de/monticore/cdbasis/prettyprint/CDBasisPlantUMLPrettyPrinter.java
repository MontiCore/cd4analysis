/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdbasis.prettyprint;

import de.monticore.cd.plantuml.PlantUMLPrettyPrintUtil;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdbasis._visitor.CDBasisHandler;
import de.monticore.cdbasis._visitor.CDBasisTraverser;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;

import java.util.stream.Collectors;

public class CDBasisPlantUMLPrettyPrinter extends PlantUMLPrettyPrintUtil
    implements CDBasisVisitor2, CDBasisHandler {

  protected CDBasisTraverser traverser;

  public CDBasisPlantUMLPrettyPrinter() {
    this(new PlantUMLPrettyPrintUtil());
  }

  public CDBasisPlantUMLPrettyPrinter(PlantUMLPrettyPrintUtil util) {
    super(util);
  }

  @Override
  public CDBasisTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(CDBasisTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public void traverse(ASTCDCompilationUnit node) {
    printComment(node);

    if (null != node.getCDDefinition()) {
      node.getCDDefinition().accept(getTraverser());
    }
  }

  @Override
  public void visit(ASTCDDefinition node) {
    printComment(node, node.getName());
    println("@startuml");
    indent();
    if (getPlantUMLConfig().getOrtho()) {
      println("skinparam linetype ortho");
    }
    if (getPlantUMLConfig().getNodesep() != -1) {
      println("skinparam nodesep " + getPlantUMLConfig().getNodesep());
    }
    if (getPlantUMLConfig().getRanksep() != -1) {
      println("skinparam ranksep " + getPlantUMLConfig().getRanksep());
    }

    println("skinparam classAttributeIconSize 0");

    println("skinparam legend {");
    indent();
    println("BorderColor black");
    println("BackGroundColor white");
    unindent();
    println("}");
  }

  @Override
  public void traverse(ASTCDDefinition node) {
    println("legend top right");
    indent();
    println(node.getDefaultPackageName() + "." + node.getName() + " CD");
    unindent();
    println("end legend");

    for (ASTCDElement element : node.getCDElementList()) {
      printComment(node);
      element.accept(getTraverser());
      println();
    }
  }

  @Override
  public void endVisit(ASTCDDefinition node) {
    // associations can not be printed in a package
    immediatelyPrintAssociations.set(true);
    associations.forEach(a -> a.accept(getTraverser()));

    println("center footer generated with MontiCore using PlantUML");

    unindent();
    println("@enduml");
  }

  @Override
  public void visit(ASTCDPackage node) {
    print("namespace ");
    print(node.getMCQualifiedName().getQName());
    println(" {");
    indent();
  }

  @Override
  public void traverse(ASTCDPackage node) {
    node.streamCDElements().forEach(e -> e.accept(getTraverser()));
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
    printList(getTraverser(), node.getInterfaceList().iterator(), ", ");
  }

  @Override
  public void visit(ASTCDExtendUsage node) {
    print(" extends ");
  }

  @Override
  public void traverse(ASTCDExtendUsage node) {
    printList(getTraverser(), node.getSuperclassList().iterator(), ", ");
  }

  @Override
  public void visit(ASTCDClass node) {
    nameStack.push(node.getName());

    printComment(node);

    print("class " + node.getName());
    if (node.isPresentCDExtendUsage()) {
      print(" extends ");
      print(node.getSymbol().getSuperClassesOnly().stream().map(s -> s.getTypeInfo().getFullName()).collect(Collectors.joining(", ")));
    }
    if (node.isPresentCDInterfaceUsage()) {
      print(" implements ");
      print(node.getSymbol().getInterfaceList().stream().map(s -> s.getTypeInfo().getFullName()).collect(Collectors.joining(", ")));
    }

    if (plantUMLConfig.getShowAtt() && !node.isEmptyCDMembers()) {
      println(" {");
      indent();
    }
  }

  @Override
  public void traverse(ASTCDClass node) {
    node.getCDMemberList().forEach(m -> m.accept(getTraverser()));
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
        node.getModifier().accept(getTraverser());
      }
      node.getMCType().accept(getTraverser());
      println(" " + node.getName());
    }
  }

}
