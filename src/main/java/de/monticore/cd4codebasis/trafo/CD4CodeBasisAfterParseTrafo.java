/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4codebasis.trafo;

import de.monticore.cd._parser.CDAfterParseHelper;
import de.monticore.cd4codebasis._visitor.CD4CodeBasisHandler;
import de.monticore.cd4codebasis._visitor.CD4CodeBasisTraverser;
import de.monticore.cd4codebasis._visitor.CD4CodeBasisVisitor2;

public class CD4CodeBasisAfterParseTrafo extends CDAfterParseHelper
    implements CD4CodeBasisVisitor2, CD4CodeBasisHandler {
  protected CD4CodeBasisTraverser traverser;

  public CD4CodeBasisAfterParseTrafo() {
    this(new CDAfterParseHelper());
  }

  public CD4CodeBasisAfterParseTrafo(CDAfterParseHelper cdAfterParseHelper) {
    super(cdAfterParseHelper);
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
