/* (c) https://github.com/MontiCore/monticore */
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
  private String visualization;

  public CDBasisPlantUMLPrettyPrinter() {
    this(new PlantUMLPrettyPrintUtil());
  }

  public CDBasisPlantUMLPrettyPrinter(PlantUMLPrettyPrintUtil util) {
    super(util);
    visualization =
        "<style>\n"
            + "\tclassDiagram {\n"
            + "\t\tclass {\n"
            + "\t\t\tBackgroundColor White\n"
            + "\t\t\tRoundCorner 0\n"
            + "\t  }\n"
            + "\t  legend {\n"
            + "      BackgroundColor White\n"
            + "      RoundCorner 0\n"
            + "    }\n"
            + "</style>\n"
            + "hide circle\n"
            + "hide empty members\n";
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
    println(visualization);
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
    unindent();
  }

  @Override
  public void traverse(ASTCDDefinition node) {
    println("legend top right");
    indent();
    println("CD");
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

    if (plantUMLConfig.getShowModifier() && hasModifier(node.getModifier())) {
      print(" << ");
      node.getModifier().accept(getTraverser());
      print(">>");
    }

    if (node.isPresentCDExtendUsage()) {
      print(" extends ");
      print(
          node.getSuperclassList().stream()
              .map(s -> s.printType())
              .collect(Collectors.joining(", ")));
    }
    if (node.isPresentCDInterfaceUsage()) {
      print(" implements ");
      print(
          node.getInterfaceList().stream()
              .map(s -> s.printType())
              .collect(Collectors.joining(", ")));
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
    } else {
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
