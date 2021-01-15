/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4code.trafo;

import de.monticore.cd._parser.CDAfterParseHelper;
import de.monticore.cd4code._visitor.CD4CodeHandler;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cd4code._visitor.CD4CodeVisitor2;

public class CD4CodeAfterParseTrafo extends CDAfterParseHelper
    implements CD4CodeVisitor2, CD4CodeHandler {
  protected CD4CodeTraverser traverser;

  public CD4CodeAfterParseTrafo() {
    this(new CDAfterParseHelper());
  }

  public CD4CodeAfterParseTrafo(CDAfterParseHelper cdAfterParseHelper) {
    super(cdAfterParseHelper);
  }

  @Override
  public CD4CodeTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(CD4CodeTraverser traverser) {
    this.traverser = traverser;
  }
}
