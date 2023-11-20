/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.plantuml;

import de.monticore.umlmodifier._ast.ASTModifier;
import de.monticore.umlmodifier._visitor.UMLModifierVisitor2;

public class UMLModiferPlantUMLPrettyPrinter extends PlantUMLPrettyPrintUtil
    implements UMLModifierVisitor2 {

  public UMLModiferPlantUMLPrettyPrinter() {}

  public UMLModiferPlantUMLPrettyPrinter(PlantUMLPrettyPrintUtil util) {
    super(util);
  }

  @Override
  public void visit(ASTModifier node) {
    if (node.isPrivate()) {
      print("-");
    }
    if (node.isProtected()) {
      print("#");
    }
    if (node.isPublic()) {
      print("+");
    }

    if (node.isStatic()) {
      print("{static} ");
    }
    if (node.isAbstract()) {
      print("{abstract} ");
    }
  }
}
