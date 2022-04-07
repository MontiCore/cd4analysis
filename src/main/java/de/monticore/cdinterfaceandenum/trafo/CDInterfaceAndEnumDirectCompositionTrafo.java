/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdinterfaceandenum.trafo;

import de.monticore.cd._parser.CDAfterParseHelper;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cdinterfaceandenum._visitor.CDInterfaceAndEnumHandler;
import de.monticore.cdinterfaceandenum._visitor.CDInterfaceAndEnumTraverser;
import de.monticore.cdinterfaceandenum._visitor.CDInterfaceAndEnumVisitor2;

/**
 * this class should only be used with {@link de.monticore.cdassociation.trafo.CDAssociationDirectCompositionTrafo}
 */
public class CDInterfaceAndEnumDirectCompositionTrafo extends CDAfterParseHelper
    implements CDInterfaceAndEnumVisitor2, CDInterfaceAndEnumHandler {
  protected CDInterfaceAndEnumTraverser traverser;

  public CDInterfaceAndEnumDirectCompositionTrafo() {
    this(new CDAfterParseHelper());
  }

  public CDInterfaceAndEnumDirectCompositionTrafo(CDAfterParseHelper cdAfterParseHelper) {
    super(cdAfterParseHelper);
  }

  @Override
  public CDInterfaceAndEnumTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(CDInterfaceAndEnumTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public void visit(ASTCDInterface node) {
    typeStack.push(node);
    removedDirectCompositions.clear();
  }

  @Override
  public void endVisit(ASTCDInterface node) {
    node.removeAllCDMembers(removedDirectCompositions);
    typeStack.pop();
  }

  @Override
  public void visit(ASTCDEnum node) {
    typeStack.push(node);
    removedDirectCompositions.clear();
  }

  @Override
  public void endVisit(ASTCDEnum node) {
    node.removeAllCDMembers(removedDirectCompositions);
    typeStack.pop();
  }
}
