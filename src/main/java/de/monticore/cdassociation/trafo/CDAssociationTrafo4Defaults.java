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
import de.monticore.cdassociation._visitor.CDAssociationHandler;
import de.monticore.cdassociation._visitor.CDAssociationTraverser;
import de.monticore.cdassociation._visitor.CDAssociationVisitor;
import de.monticore.cdassociation._visitor.CDAssociationVisitor2;

import static de.monticore.cdassociation.trafo.CDAssociationAfterParseTrafo.createASTCDRoleIfAbsent;

public class CDAssociationTrafo4Defaults extends CDAfterParseHelper
    implements CDAssociationVisitor2, CDAssociationHandler {
  protected CDAssociationTraverser traverser;
  protected CDAssociationVisitor symbolTableCreator;

  public CDAssociationTrafo4Defaults() {
    this(new CDAfterParseHelper(),
        CDAssociationMill.cDAssociationSymbolTableCreator());
  }

  public CDAssociationTrafo4Defaults(CDAfterParseHelper cdAfterParseHelper, CDAssociationVisitor symbolTableCreator) {
    super(cdAfterParseHelper);
    this.symbolTableCreator = symbolTableCreator;
  }

  @Override
  public CDAssociationTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(CDAssociationTraverser traverser) {
    this.traverser = traverser;
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
