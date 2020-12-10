/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdassociation.trafo;

import de.monticore.cd._parser.CDAfterParseHelper;
import de.monticore.cdassociation.CDAssociationMill;
import de.monticore.cdassociation._ast.ASTCDAssocLeftSide;
import de.monticore.cdassociation._ast.ASTCDAssocRightSide;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._symboltable.CDAssociationSymbolTableCreator;
import de.monticore.cdassociation._visitor.CDAssociationVisitor;

import static de.monticore.cdassociation._parser.CDAssociationAfterParseTrafo.createASTCDRoleIfAbsent;

public class CDAssociationTrafo4Defaults extends CDAfterParseHelper
    implements CDAssociationVisitor {
  protected CDAssociationVisitor realThis;
  protected CDAssociationVisitor symbolTableCreator;

  public CDAssociationTrafo4Defaults() {
    this(new CDAfterParseHelper(),
        CDAssociationMill.cDAssociationSymbolTableCreator());
  }

  public CDAssociationTrafo4Defaults(CDAfterParseHelper cdAfterParseHelper, CDAssociationVisitor symbolTableCreator) {
    super(cdAfterParseHelper);
    setRealThis(this);
    this.symbolTableCreator = symbolTableCreator;
  }

  @Override
  public CDAssociationVisitor getRealThis() {
    return realThis;
  }

  @Override
  public void setRealThis(CDAssociationVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public void visit(ASTCDAssociation node) {
    assocStack.push(node);
    createASTCDRoleIfAbsent(node);
  }

  @Override
  public void endVisit(ASTCDAssociation node) {
    assocStack.pop();
    if (symbolTableCreator instanceof CDAssociationSymbolTableCreator) {
      ((CDAssociationSymbolTableCreator) symbolTableCreator).createAndInit_SymAssociation(node);
    }
  }

  @Override
  public void visit(ASTCDAssocLeftSide node) {
    if (node.isPresentCDRole() && !node.isPresentSymbol()) {
      if (symbolTableCreator instanceof CDAssociationSymbolTableCreator) {
        ((CDAssociationSymbolTableCreator) symbolTableCreator).buildCDRole(assocStack.peek(), true);
      }
    }
  }

  @Override
  public void visit(ASTCDAssocRightSide node) {
    if (node.isPresentCDRole() && !node.isPresentSymbol()) {
      if (symbolTableCreator instanceof CDAssociationSymbolTableCreator) {
        ((CDAssociationSymbolTableCreator) symbolTableCreator).buildCDRole(assocStack.peek(), false);
      }
    }
  }
}
