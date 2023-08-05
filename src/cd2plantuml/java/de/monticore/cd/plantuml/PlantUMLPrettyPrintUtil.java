/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.plantuml;

import de.monticore.ast.ASTNode;
import de.monticore.cd.prettyprint.PrettyPrintUtil;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.umlmodifier._ast.ASTModifier;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;

public class PlantUMLPrettyPrintUtil extends PrettyPrintUtil {
  protected PlantUMLConfig plantUMLConfig;
  protected Stack<String> nameStack;
  protected AtomicBoolean immediatelyPrintAssociations = new AtomicBoolean(true);
  protected final Set<ASTCDAssociation> associations;

  public PlantUMLPrettyPrintUtil() {
    this(new IndentPrinter(), new PlantUMLConfig());
  }

  public PlantUMLPrettyPrintUtil(IndentPrinter printer, PlantUMLConfig config) {
    super(printer);
    this.nameStack = new Stack<>();
    this.plantUMLConfig = config;
    this.associations = new HashSet<>();
  }

  public PlantUMLPrettyPrintUtil(PlantUMLPrettyPrintUtil other) {
    super(other.printer);
    this.plantUMLConfig = other.getPlantUMLConfig();
    this.nameStack = other.nameStack;
    this.immediatelyPrintAssociations = other.immediatelyPrintAssociations;
    this.associations = other.associations;
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

      PlantUMLCommentPrinter.printCommentToNote(
          node, Optional.of(connectTo), getPlantUMLConfig(), getPrinter());
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

  public boolean hasModifier(ASTModifier modifier){
    return modifier.isAbstract() ||
           modifier.isStatic() ||
           modifier.isLocal() ||
           modifier.isFinal() ||
           modifier.isDerived() ||
           modifier.isProtected() ||
           modifier.isPrivate() ||
           modifier.isReadonly();
  }
}
