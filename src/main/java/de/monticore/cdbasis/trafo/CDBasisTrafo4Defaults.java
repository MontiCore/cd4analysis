/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdbasis.trafo;

import de.monticore.cd._parser.CDAfterParseHelper;
import de.monticore.cdbasis.CDBasisMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._visitor.CDBasisHandler;
import de.monticore.cdbasis._visitor.CDBasisTraverser;
import de.monticore.cdbasis._visitor.CDBasisVisitor;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;

import java.util.ArrayList;
import java.util.List;

public class CDBasisTrafo4Defaults extends CDAfterParseHelper
    implements CDBasisVisitor2, CDBasisHandler {
  protected CDBasisTraverser traverser;
  protected CDBasisVisitor symbolTableCreator;
  protected List<String> packageNameList = new ArrayList<>(); //default, if the model has no package

  public CDBasisTrafo4Defaults() {
    this(new CDAfterParseHelper(),
        CDBasisMill.cDBasisSymbolTableCreator());
  }

  public CDBasisTrafo4Defaults(CDAfterParseHelper cdAfterParseHelper, CDBasisVisitor symbolTableCreator) {
    super(cdAfterParseHelper);
    this.symbolTableCreator = symbolTableCreator;
  }

  @Override
  public CDBasisTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(CDBasisTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public void endVisit(ASTCDCompilationUnit node) {
    symbolTableCreator.endVisit(node);
  }
}
