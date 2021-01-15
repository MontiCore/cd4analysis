/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4code.trafo;

import de.monticore.cd._parser.CDAfterParseHelper;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._visitor.CD4CodeHandler;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cd4code._visitor.CD4CodeVisitor;
import de.monticore.cd4code._visitor.CD4CodeVisitor2;

public class CD4CodeTrafo4Defaults extends CDAfterParseHelper
    implements CD4CodeVisitor2, CD4CodeHandler {
  protected CD4CodeTraverser traverser;
  protected CD4CodeVisitor symbolTableCreator;

  public CD4CodeTrafo4Defaults() {
    this(new CDAfterParseHelper(),
        CD4CodeMill.cD4CodeSymbolTableCreator());
  }

  public CD4CodeTrafo4Defaults(CDAfterParseHelper cdAfterParseHelper, CD4CodeVisitor symbolTableCreator) {
    super(cdAfterParseHelper);
    this.symbolTableCreator = symbolTableCreator;
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
