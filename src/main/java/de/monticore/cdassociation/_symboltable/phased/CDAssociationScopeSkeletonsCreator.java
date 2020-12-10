/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdassociation._symboltable.phased;

import de.monticore.cd._symboltable.CDSymbolTableHelper;
import de.monticore.cdassociation.CDAssociationMill;
import de.monticore.cdassociation._ast.*;
import de.monticore.cdassociation._symboltable.CDAssociationSymbol;
import de.monticore.cdassociation._symboltable.CDRoleSymbolBuilder;
import de.monticore.cdassociation._symboltable.ICDAssociationScope;
import de.monticore.cdassociation._symboltable.SymAssociationBuilder;

import java.util.Deque;
import java.util.Optional;
import java.util.Stack;

public class CDAssociationScopeSkeletonsCreator
    extends CDAssociationScopeSkeletonsCreatorTOP {
  protected CDSymbolTableHelper symbolTableHelper;
  protected Stack<ASTCDAssociation> cdAssociationStack = new Stack<>();
  protected Stack<ASTCDAssocSide> cdAssocSideStack = new Stack<>();

  public CDAssociationScopeSkeletonsCreator(ICDAssociationScope enclosingScope) {
    super(enclosingScope);
    setRealThis(this);
    init();
  }

  public CDAssociationScopeSkeletonsCreator(Deque<? extends ICDAssociationScope> scopeStack) {
    super(scopeStack);
    setRealThis(this);
    init();
  }

  protected void init() {
    symbolTableHelper = new CDSymbolTableHelper();
  }

  public void setSymbolTableHelper(CDSymbolTableHelper cdSymbolTableHelper) {
    this.symbolTableHelper = cdSymbolTableHelper;
  }

  @Override
  public void visit(ASTCDAssociation node) {
    // the association symbol is only created when it has a name
    if (node.isPresentName()) {
      CDAssociationSymbol symbol = create_CDAssociation(node).build();
      addToScopeAndLinkWithNode(symbol, node);
    }
    this.cdAssociationStack.push(node);
  }

  @Override
  public void endVisit(ASTCDAssociation node) {
    if (node.getLeft().isPresentSymbol()) {
      node.getLeft().getSymbol().setIsLeft(true);
    }
    if (node.getRight().isPresentSymbol()) {
      node.getRight().getSymbol().setIsLeft(false);
    }

    handle_SymAssociation(node);
    this.cdAssociationStack.pop();
    super.endVisit(node);
  }

  @Override
  public void traverse(ASTCDAssocLeftSide node) {
    this.cdAssocSideStack.push(node);
    super.traverse(node);
    this.cdAssocSideStack.pop();
  }

  @Override
  public void traverse(ASTCDAssocRightSide node) {
    this.cdAssocSideStack.push(node);
    super.traverse(node);
    this.cdAssocSideStack.pop();
  }

  @Override
  protected void initialize_CDRole(CDRoleSymbolBuilder symbol, ASTCDRole ast) {
    super.initialize_CDRole(symbol, ast);

    final ASTCDAssocSide side = this.cdAssocSideStack.peek();
    symbolTableHelper.getModifierHandler().handle(side.getModifier(), symbol);

    final ASTCDAssociation assoc = this.cdAssociationStack.peek();
    assoc.getCDAssocDir().accept(symbolTableHelper.getNavigableVisitor());
    symbol.setIsDefinitiveNavigable(side.isLeft() ? symbolTableHelper.getNavigableVisitor().isDefinitiveNavigableLeft() : symbolTableHelper.getNavigableVisitor().isDefinitiveNavigableRight());

    if (side.isPresentCDCardinality()) {
      symbol.setCardinality(side.getCDCardinality());
    }

    symbol.setIsOrdered(side.isPresentCDOrdered());
    symbol.setIsLeft(side.isLeft());
  }

  @Override
  public void visit(ASTCDDirectComposition node) {
    throw new IllegalStateException("0xCDA66: Cannot create a symbol for CDDirectComposition, please transform to a CDAssociation.");
  }

  public void handle_SymAssociation(ASTCDAssociation node) {
    // create the SymAssociation connected to the CDAssociationSymbol
    // only if both role names are set
    Optional<SymAssociationBuilder> symAssociation = create_SymAssociation(node);
    if (symAssociation.isPresent()) {
      if (node.isPresentSymbol()) {
        // only link the association symbol to the symAssociation when
        // the symAssociation is created
        symAssociation.get().setAssociationSymbol(node.getSymbol());
      }
      symAssociation.get()
          .setLeftRole(node.getLeft().getSymbol())
          .setRightRole(node.getRight().getSymbol())
          .build();
    }
  }

  protected Optional<SymAssociationBuilder> create_SymAssociation(ASTCDAssociation node) {
    if (!node.getLeft().isPresentCDRole() || !node.getRight().isPresentCDRole()) {
      // cant create the symbol structure when the role has no name
      // and therefore the symbol
      return Optional.empty();
    }
    final SymAssociationBuilder symAssociationBuilder = CDAssociationMill
        .symAssocationBuilder();
    initialize_SymAssociation(symAssociationBuilder, node);
    return Optional.of(symAssociationBuilder);
  }

  /**
   * this method can only be used, when both sides have a CDRole
   *
   * @param symAssociation
   * @param node
   * @return
   */
  protected void initialize_SymAssociation(SymAssociationBuilder symAssociation, ASTCDAssociation node) {
    node.getCDAssocType().accept(symbolTableHelper.getAssocTypeVisitor(symAssociation));
  }

}
