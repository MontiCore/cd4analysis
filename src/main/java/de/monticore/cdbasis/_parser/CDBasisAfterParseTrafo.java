/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdbasis._parser;

import de.monticore.cd._parser.CDAfterParseHelper;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._visitor.CDBasisVisitor;

public class CDBasisAfterParseTrafo extends CDAfterParseHelper
    implements CDBasisVisitor {
  protected CDBasisVisitor realThis;

  public CDBasisAfterParseTrafo() {
    this(new CDAfterParseHelper());
  }

  public CDBasisAfterParseTrafo(CDAfterParseHelper cdAfterParseHelper) {
    super(cdAfterParseHelper);
    setRealThis(this);
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
  public void visit(ASTCDClass node) {
    typeStack.push(node);
    removedDirectCompositions.clear();
  }

  @Override
  public void endVisit(ASTCDClass node) {
    node.removeAllCDMembers(removedDirectCompositions);
    typeStack.pop();
  }

  @Override
  public void visit(ASTCDDefinition node) {
    createdAssociations.clear();
  }

  @Override
  public void endVisit(ASTCDDefinition node) { // TODO SVa: change to CDPackage
    node.addAllCDElements(createdAssociations);
  }
}
