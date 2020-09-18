/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4code.trafo;

import de.monticore.cd._parser.CDAfterParseHelper;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeScope;
import de.monticore.cd4code._visitor.CD4CodeVisitor;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;

import java.util.Deque;

public class CD4CodeTrafo4Defaults extends CDAfterParseHelper
    implements CD4CodeVisitor {
  protected CD4CodeVisitor realThis;
  protected CD4CodeVisitor symbolTableCreator;

  public CD4CodeTrafo4Defaults(Deque<ICD4CodeScope> scopeStack) {
    this(new CDAfterParseHelper(),
        CD4CodeMill.cD4CodeSymbolTableCreatorBuilder().setScopeStack(scopeStack).build());
  }

  public CD4CodeTrafo4Defaults(CDAfterParseHelper cdAfterParseHelper, CD4CodeVisitor symbolTableCreator) {
    super(cdAfterParseHelper);
    setRealThis(this);
    this.symbolTableCreator = symbolTableCreator;
  }

  @Override
  public CD4CodeVisitor getRealThis() {
    return realThis;
  }

  @Override
  public void setRealThis(CD4CodeVisitor realThis) {
    this.realThis = realThis;
  }
}
