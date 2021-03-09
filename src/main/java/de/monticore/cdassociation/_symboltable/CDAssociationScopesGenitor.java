package de.monticore.cdassociation._symboltable;

import de.monticore.cdassociation.CDAssociationMill;
import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._ast.ASTCDDirectComposition;
import de.monticore.cdassociation._ast.ASTCDRole;
import de.monticore.cdassociation._visitor.CDAssocTypeForSymAssociationVisitor;
import de.monticore.cdassociation._visitor.CDAssociationTraverser;

import java.util.Deque;
import java.util.Optional;

public class CDAssociationScopesGenitor extends CDAssociationScopesGenitorTOP {
  public CDAssociationScopesGenitor(ICDAssociationScope enclosingScope) {
    super(enclosingScope);
  }

  public CDAssociationScopesGenitor(Deque<? extends ICDAssociationScope> scopeStack) {
    super(scopeStack);
  }

  public CDAssociationScopesGenitor() {
    super();
  }

  @Override
  public void handle(ASTCDAssociation node) {
    node.setEnclosingScope(getCurrentScope().get());

    CDAssociationSymbolBuilder symbol = create_CDAssociation(node);
    if (symbol != null) {
      addToScopeAndLinkWithNode(symbol.build(), node);
    }

    if (node.getLeft().isPresentCDRole()) {
      buildCDRole(node, true);
    }
    if (node.getRight().isPresentCDRole()) {
      buildCDRole(node, false);
    }

    createAndInit_SymAssociation(node);

    node.getLeft().setEnclosingScope(node.getEnclosingScope());
    initSide(node.getLeft());
    node.getRight().setEnclosingScope(node.getEnclosingScope());
    initSide(node.getRight());

    if (symbol != null) {
      removeCurrentScope();
    }
  }

  protected void initSide(ASTCDAssocSide side) {
    if (side.isPresentCDQualifier()) {
      if (side.getCDQualifier().isPresentByType()) {
        side.getCDQualifier().getByType().setEnclosingScope(side.getEnclosingScope());
      }
    }
    side.getMCQualifiedType().setEnclosingScope(side.getEnclosingScope());
    side.getMCQualifiedType().getMCQualifiedName().setEnclosingScope(side.getEnclosingScope());
  }

  @Override
  public void handle(ASTCDRole node) {
    // do nothing, everything is handled by the association
  }

  @Override
  public void visit(ASTCDDirectComposition node) {
    throw new IllegalStateException("0xCDA65: Cannot create a symbol for CDDirectComposition, please transform to a CDAssociation using CD4AnalysisDirectCompositionTrafo or CD4CodeDirectCompositionTrafo.");
  }

  @Override
  protected CDAssociationSymbolBuilder create_CDAssociation(ASTCDAssociation ast) {
    if (ast.isPresentName()) {
      return super.create_CDAssociation(ast);
    }
    else {
      return null;
    }
  }

  public void buildCDRole(ASTCDAssociation node, boolean isLeft) {
    final ASTCDAssocSide side = isLeft ? node.getLeft() : node.getRight();
    final ASTCDRole cdRole = side.getCDRole();

    final CDRoleSymbol cdRoleSymbol = create_CDRole(cdRole).build();
    addToScopeAndLinkWithNode(cdRoleSymbol, cdRole);
  }

  public void createAndInit_SymAssociation(ASTCDAssociation node) {
    // create the SymAssociation connected to the CDAssociationSymbol
    // only if both role names are set
    Optional<SymAssociationBuilder> symAssociation = create_SymAssociation(node);
    if (symAssociation.isPresent()) {
      initialize_SymAssociation(symAssociation.get(), node);
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
    return Optional.of(new SymAssociationBuilder());
  }

  /**
   * this method can only be used, when both sides have a CDRole
   *
   * @param symAssociation
   * @param node
   * @return
   */
  protected boolean initialize_SymAssociation(SymAssociationBuilder symAssociation, ASTCDAssociation node) {
    CDAssociationTraverser t = CDAssociationMill.traverser();
    t.add4CDAssociation(new CDAssocTypeForSymAssociationVisitor(symAssociation));
    node.getCDAssocType().accept(t);

    return true;
  }
}
