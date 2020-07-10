/*
 * (c) https://github.com/MontiCore/monticore
 */

/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd.plantuml;

import de.monticore.ast.ASTNode;
import de.monticore.cd.prettyprint.PrettyPrintUtil;
import de.monticore.prettyprint.IndentPrinter;

import java.util.Optional;
import java.util.Stack;

public abstract class PlantUMLPrettyPrintUtil extends PrettyPrintUtil {
  protected PlantUMLConfig plantUMLConfig;
  protected Stack<String> nameStack;

  public PlantUMLPrettyPrintUtil() {
    this(new IndentPrinter());
  }

  public PlantUMLPrettyPrintUtil(IndentPrinter printer) {
    super(printer);
    nameStack = new Stack<>();
  }

  public PlantUMLConfig getPlantUMLConfig() {
    return plantUMLConfig;
  }

  public void setPlantUMLConfig(PlantUMLConfig plantUMLConfig) {
    this.plantUMLConfig = plantUMLConfig;
  }

  public Stack<String> getNameStack() {
    return nameStack;
  }

  public void setNameStack(Stack<String> nameStack) {
    this.nameStack = nameStack;
  }

  public void printComment(ASTNode node) {
    PlantUMLCommentPrinter.printCommentToNote(node, getPlantUMLConfig(), getPrinter());
  }

  public void printComment(ASTNode node, String connectTo) {
    // TODO SVa: check if namestack contains something -> use it for connectsTo

    PlantUMLCommentPrinter.printCommentToNote(node, Optional.of(connectTo), getPlantUMLConfig(), getPrinter());
  }

  public String shorten(String text) {
    if (!plantUMLConfig.getShortenWords()) {
      return text;
    }

    StringBuilder uc = new StringBuilder();
    for (int i = 1; i < text.length(); i++) {
      char c = text.charAt(i);
      uc.append(Character.isUpperCase(c) ? c : "");
    }
    if (uc.length() > 0) {
      return text.charAt(0) + uc.toString();
    }

    if (text.length() < 7) {
      return text;
    }

    return text.substring(0, 5) + "~";
  }
}
