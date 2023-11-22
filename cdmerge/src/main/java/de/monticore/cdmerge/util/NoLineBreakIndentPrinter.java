/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.util;

import de.monticore.prettyprint.IndentPrinter;

/**
 * Simple subclass which replaces linebreaks by blanks for inline logging- Don't use for whole CDs
 * just for simple model elements
 */
public class NoLineBreakIndentPrinter extends IndentPrinter {

  /** @see {@link IndentPrinter} */
  public NoLineBreakIndentPrinter(StringBuilder sb) {
    super(sb);
  }

  @Override
  public void println() {
    doPrint(" ");
  }

  @Override
  public String getContent() {
    String printResult = super.getContent();
    return printResult.replaceAll("\n", " ");
  }
}
