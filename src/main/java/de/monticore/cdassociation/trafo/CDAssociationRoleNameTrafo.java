/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdassociation.trafo;

import de.monticore.cd._parser.CDAfterParseHelper;
import de.monticore.cd.facade.MCQualifiedNameFacade;
import de.monticore.cdassociation.CDAssociationMill;
import de.monticore.cdassociation._ast.ASTCDAssocLeftSide;
import de.monticore.cdassociation._ast.ASTCDAssocRightSide;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._symboltable.CDAssociationScopesGenitor;
import de.monticore.cdassociation._symboltable.CDAssociationSymbolTableCompleter;
import de.monticore.cdassociation._visitor.CDAssociationHandler;
import de.monticore.cdassociation._visitor.CDAssociationTraverser;
import de.monticore.cdassociation._visitor.CDAssociationVisitor2;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.se_rwth.commons.logging.Log;

import java.util.List;

import static de.monticore.cdassociation.trafo.CDAssociationDirectCompositionTrafo.createASTCDRoleIfAbsent;

public class CDAssociationRoleNameTrafo extends CDAfterParseHelper
    implements CDAssociationVisitor2, CDAssociationHandler {
  protected CDAssociationTraverser traverser;
  protected CDAssociationScopesGenitor symbolTableCreator;
  protected CDAssociationSymbolTableCompleter symbolTableCompleter;

  public CDAssociationRoleNameTrafo() {
    this(new CDAfterParseHelper(),
        CDAssociationMill.scopesGenitor());
  }

  public CDAssociationRoleNameTrafo(CDAfterParseHelper cdAfterParseHelper, CDAssociationScopesGenitor symbolTableCreator) {
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
    symbolTableCreator.addToScopeStack(node.getEnclosingScope());
    createASTCDRoleIfAbsent(node);
  }

  @Override
  public void endVisit(ASTCDAssociation node) {
    // if there is a SymAssociation already, the don't create a new one
    if (node.getLeft().isPresentSymbol() && !node.getLeft().getSymbol().isPresentAssoc()) {
      symbolTableCreator.createAndInit_SymAssociation(node);

      final ASTCDAssocLeftSide leftSide = node.getLeft();
      final ASTCDAssocRightSide rightSide = node.getRight();
      CDAssociationSymbolTableCompleter.addRoleToTheirType(leftSide.getSymbol(), rightSide.getSymbol().getType().getTypeInfo());
      CDAssociationSymbolTableCompleter.addRoleToTheirType(rightSide.getSymbol(), leftSide.getSymbol().getType().getTypeInfo());
    }

    assocStack.pop();
    symbolTableCreator.removeCurrentScope();
  }

  @Override
  public void visit(ASTCDAssocLeftSide node) {
    if (node.isPresentCDRole() && !node.isPresentSymbol()) {
      symbolTableCreator.buildCDRole(assocStack.peek(), true);

      // complete the types for the newly created CDRoleSymbols
      symbolTableCompleter.visit(node.getSymbol());
    }
  }

  @Override
  public void visit(ASTCDAssocRightSide node) {
    if (node.isPresentCDRole() && !node.isPresentSymbol()) {
      symbolTableCreator.buildCDRole(assocStack.peek(), false);

      // complete the types for the newly created CDRoleSymbols
      symbolTableCompleter.visit(node.getSymbol());
    }
  }

  public void transform(ASTCDCompilationUnit compilationUnit)
      throws RuntimeException {
    if (!compilationUnit.getCDDefinition().isPresentSymbol()) {
      final String msg = "0xCD0B1: can't start the transformation, the symbol table is missing";
      Log.error(msg);
      throw new RuntimeException(msg);
    }

    init(compilationUnit);

    compilationUnit.accept(getTraverser());
  }

  public void init(ASTCDCompilationUnit compilationUnit) {
    final List<ASTMCImportStatement> imports = compilationUnit.getMCImportStatementList();
    final ASTMCQualifiedName packageDeclaration = MCQualifiedNameFacade.createQualifiedName("");
    symbolTableCompleter =
        new CDAssociationSymbolTableCompleter(
            imports,
            packageDeclaration
        );
  }
}
