/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4codebasis._parser;

import de.monticore.cd._parser.CDAfterParseHelper;
import de.monticore.cd4codebasis._visitor.CD4CodeBasisVisitor;

public class CD4CodeBasisAfterParseTrafo extends CDAfterParseHelper
    implements CD4CodeBasisVisitor {
  protected CD4CodeBasisVisitor realThis;

  public CD4CodeBasisAfterParseTrafo() {
    this(new CDAfterParseHelper());
  }

  public CD4CodeBasisAfterParseTrafo(CDAfterParseHelper cdAfterParseHelper) {
    super(cdAfterParseHelper);
    setRealThis(this);
  }

  @Override
  public CD4CodeBasisVisitor getRealThis() {
    return realThis;
  }

  @Override
  public void setRealThis(CD4CodeBasisVisitor realThis) {
    this.realThis = realThis;
  }
}
