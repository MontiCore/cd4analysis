/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4codebasis.trafo;

import de.monticore.cd._parser.CDAfterParseHelper;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._visitor.CD4CodeBasisHandler;
import de.monticore.cd4codebasis._visitor.CD4CodeBasisTraverser;
import de.monticore.cd4codebasis._visitor.CD4CodeBasisVisitor;
import de.monticore.cd4codebasis._visitor.CD4CodeBasisVisitor2;

public class CD4CodeBasisTrafo4Defaults extends CDAfterParseHelper
    implements CD4CodeBasisVisitor2, CD4CodeBasisHandler {
  protected CD4CodeBasisTraverser traverser;
  protected CD4CodeBasisVisitor symbolTableCreator;

  public CD4CodeBasisTrafo4Defaults() {
    this(new CDAfterParseHelper(),
        CD4CodeMill.cD4CodeSymbolTableCreator());
  }

  public CD4CodeBasisTrafo4Defaults(CDAfterParseHelper cdAfterParseHelper, CD4CodeBasisVisitor symbolTableCreator) {
    super(cdAfterParseHelper);
    this.symbolTableCreator = symbolTableCreator;
  }

  @Override
  public CD4CodeBasisTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(CD4CodeBasisTraverser traverser) {
    this.traverser = traverser;
  }
}
