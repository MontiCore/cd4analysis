/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdbasis.trafo;

import de.monticore.cd._parser.CDAfterParseHelper;
import de.monticore.cdbasis.CDBasisMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.ICDBasisScope;
import de.monticore.cdbasis._visitor.CDBasisVisitor;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class CDBasisTrafo4Defaults extends CDAfterParseHelper
    implements CDBasisVisitor {
  protected CDBasisVisitor realThis;
  protected CDBasisVisitor symbolTableCreator;
  protected List<String> packageNameList = new ArrayList<>(); //default, if the model has no package

  public CDBasisTrafo4Defaults(Deque<ICDBasisScope> scopeStack) {
    this(new CDAfterParseHelper(),
        CDBasisMill.cDBasisSymbolTableCreatorBuilder().setScopeStack(scopeStack).build());
  }

  public CDBasisTrafo4Defaults(CDAfterParseHelper cdAfterParseHelper, CDBasisVisitor symbolTableCreator) {
    super(cdAfterParseHelper);
    setRealThis(this);
    this.symbolTableCreator = symbolTableCreator;
  }

  @Override
  public CDBasisVisitor getRealThis() {
    return realThis;
  }

  @Override
  public void setRealThis(CDBasisVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public void endVisit(ASTCDCompilationUnit node) {
    symbolTableCreator.endVisit(node);
  }
}
