/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd.plantuml;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.monticore.umlmodifier._visitor.UMLModifierVisitor;

public class UMLModiferPlantUMLPrettyPrinter extends PlantUMLPrettyPrintUtil
    implements UMLModifierVisitor {

  public UMLModiferPlantUMLPrettyPrinter() {
  }

  public UMLModiferPlantUMLPrettyPrinter(IndentPrinter printer, PlantUMLConfig config) {
    super(printer, config);
  }

  @Override
  public void setRealThis(UMLModifierVisitor realThis) {
    // do nothing
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
