/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4codebasis.trafo;

import de.monticore.cd._parser.CDAfterParseHelper;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._visitor.CD4CodeBasisVisitor;

public class CD4CodeBasisTrafo4Defaults extends CDAfterParseHelper
    implements CD4CodeBasisVisitor {
  protected CD4CodeBasisVisitor realThis;
  protected CD4CodeBasisVisitor symbolTableCreator;

  public CD4CodeBasisTrafo4Defaults() {
    this(new CDAfterParseHelper(),
        CD4CodeMill.cD4CodeSymbolTableCreator());
  }

  public CD4CodeBasisTrafo4Defaults(CDAfterParseHelper cdAfterParseHelper, CD4CodeBasisVisitor symbolTableCreator) {
    super(cdAfterParseHelper);
    setRealThis(this);
    this.symbolTableCreator = symbolTableCreator;
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
