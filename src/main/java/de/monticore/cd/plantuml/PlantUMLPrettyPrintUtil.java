/*
 * (c) https://github.com/MontiCore/monticore
 */

/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd.plantuml;

import de.monticore.ast.ASTNode;
import de.monticore.cd.prettyprint.PrettyPrintUtil;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.prettyprint.IndentPrinter;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;

public abstract class PlantUMLPrettyPrintUtil extends PrettyPrintUtil {
  protected PlantUMLConfig plantUMLConfig;
  protected Stack<String> nameStack;
  protected boolean immediatelyPrintAssociations = false;
  protected Set<ASTCDAssociation> associations;

  public PlantUMLPrettyPrintUtil() {
    this(new IndentPrinter(), new PlantUMLConfig());
  }

  public PlantUMLPrettyPrintUtil(IndentPrinter printer, PlantUMLConfig config) {
    super(printer);
    this.nameStack = new Stack<>();
    this.plantUMLConfig = config;
    this.associations = new HashSet<>();
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
    if (plantUMLConfig.getShowComments()) {
      PlantUMLCommentPrinter.printCommentToNote(node, getPlantUMLConfig(), getPrinter());
    }
  }

  public void printComment(ASTNode node, String connectTo) {
    if (plantUMLConfig.getShowComments()) {
      // TODO SVa: check if namestack contains something -> use it for connectsTo

      PlantUMLCommentPrinter.printCommentToNote(node, Optional.of(connectTo), getPlantUMLConfig(), getPrinter());
    }
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
