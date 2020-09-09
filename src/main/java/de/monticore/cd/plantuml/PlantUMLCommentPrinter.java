/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd.plantuml;

import de.monticore.ast.ASTNode;
import de.monticore.prettyprint.IndentPrinter;

import java.util.Optional;

public class PlantUMLCommentPrinter {
  public static void printCommentToNote(ASTNode a, PlantUMLConfig config, IndentPrinter printer) {
    printCommentToNote(a, Optional.empty(), config, printer);
  }

  public static void printCommentToNote(ASTNode a, @SuppressWarnings("OptionalUsedAsFieldOrParameterType") Optional<String> connectTo, PlantUMLConfig config, IndentPrinter printer) {
    if (!config.getShowComments()) {
      return;
    }

    // TODO SVa: write comment (pre and post)
    /*
    note as N1
      This note is <u>also</u>
      <b><color:royalBlue>on several</color>
      <s>words</s> lines
      And this is hosted by <img:sourceforge.jpg>
    end note
     */
  }
}
