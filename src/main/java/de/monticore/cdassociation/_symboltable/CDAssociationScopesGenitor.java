package de.monticore.cdassociation._symboltable;

import de.monticore.cd._symboltable.CDSymbolTableHelper;
import de.monticore.cdassociation.CDAssociationMill;
import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._ast.ASTCDDirectComposition;
import de.monticore.cdassociation._ast.ASTCDRole;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbolSurrogate;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.logging.Log;

import java.util.Deque;
import java.util.Optional;

public class CDAssociationScopesGenitor extends CDAssociationScopesGenitorTOP {
  protected CDSymbolTableHelper symbolTableHelper;

  public CDAssociationScopesGenitor(ICDAssociationScope enclosingScope) {
    super(enclosingScope);
    init();
  }

  public CDAssociationScopesGenitor(Deque<? extends ICDAssociationScope> scopeStack) {
    super(scopeStack);
    init();
  }

  public CDAssociationScopesGenitor() {
    super();
    init();
  }

  protected void init() {
    symbolTableHelper = new CDSymbolTableHelper();
  }

  public void setSymbolTableHelper(CDSymbolTableHelper cdSymbolTableHelper) {
    this.symbolTableHelper = cdSymbolTableHelper;
  }

  public CDSymbolTableHelper getSymbolTableHelper() {
    return symbolTableHelper;
  }

  @Override
  public void handle(ASTCDAssociation node) {
    node.setEnclosingScope(getCurrentScope().get());

    CDAssociationSymbolBuilder symbol = create_CDAssociation(node);
    if (symbol != null) {
      addToScopeAndLinkWithNode(symbol.build(), node);
    }

    if (node.getLeft().isPresentCDRole()) {
      if (buildCDRole(node, true)) {
        return;
      }
    }
    if (node.getRight().isPresentCDRole()) {
      if (buildCDRole(node, false)) {
        return;
      }
    }

    createAndInit_SymAssociation(node);

    if (symbol != null) {
      removeCurrentScope();
    }
  }

  public boolean buildCDRole(ASTCDAssociation node, boolean isLeft) {
    final ASTCDAssocSide side = isLeft ? node.getLeft() : node.getRight();
    final ASTCDRole cdRole = side.getCDRole();

    final CDRoleSymbol cdRoleSymbol = create_CDRole(cdRole).build();
    addToScopeAndLinkWithNode(cdRoleSymbol, cdRole);
    initialize_CDRole(cdRoleSymbol, node, isLeft);

    return false;
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
    return Optional.of(CDAssociationMill
        .symAssocationBuilder()
    );
  }

  /**
   * this method can only be used, when both sides have a CDRole
   *
   * @param symAssociation
   * @param node
   * @return
   */
  protected boolean initialize_SymAssociation(SymAssociationBuilder symAssociation, ASTCDAssociation node) {
    node.getCDAssocType().accept(symbolTableHelper.getAssocTypeVisitor(symAssociation));

    return true;
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

  @Override
  public void handle(ASTCDRole node) {
    // do nothing
  }

  protected void initialize_CDRole(CDRoleSymbol symbol, ASTCDAssociation ast, boolean isLeft) {
    final ASTCDAssocSide side = isLeft ? ast.getLeft() : ast.getRight();

    final Optional<SymTypeExpression> typeResult = getSymTypeExpression(ast, side);
    if (!typeResult.isPresent()) {
      return;
    }
    symbol.setType(typeResult.get());

    symbolTableHelper.getModifierHandler().handle(side.getModifier(), symbol);

    ast.getCDAssocDir().accept(symbolTableHelper.getNavigableVisitor());
    symbol.setIsDefinitiveNavigable(isLeft ? symbolTableHelper.getNavigableVisitor().isDefinitiveNavigableLeft() : symbolTableHelper.getNavigableVisitor().isDefinitiveNavigableRight());

    if (side.isPresentCDCardinality()) {
      symbol.setCardinality(side.getCDCardinality());
    }

    handleQualifier(symbol, side, typeResult.get());
    symbol.setIsOrdered(side.isPresentCDOrdered());
    symbol.setIsLeft(isLeft);
  }

  protected Optional<SymTypeExpression> getSymTypeExpression(ASTCDAssociation ast, ASTCDAssocSide side) {
    side.getMCQualifiedType().setEnclosingScope(scopeStack.peekLast()); // TODO SVa: remove when #2549 is fixed
    final Optional<SymTypeExpression> typeResult = symbolTableHelper.getTypeChecker().calculateType(side.getMCQualifiedType());
    if (!typeResult.isPresent()) {
      Log.error(String.format(
          "0xCDA62: The type %s of the role (%s) could not be calculated",
          symbolTableHelper.getPrettyPrinter().prettyprint(side.getMCQualifiedType()),
          side.getName(ast)),
          side.getMCQualifiedType().get_SourcePositionStart());
    }
    return typeResult;
  }

  protected void handleQualifier(CDRoleSymbol symbol, ASTCDAssocSide side, SymTypeExpression type) {
    if (side.isPresentCDQualifier()) {
      if (side.getCDQualifier().isPresentByType()) {
        side.getCDQualifier().getByType().setEnclosingScope(scopeStack.peekLast()); // TODO SVa: remove when #2549 is fixed
        final Optional<SymTypeExpression> result = symbolTableHelper.getTypeChecker().calculateType(side.getCDQualifier().getByType());
        if (!result.isPresent()) {
          Log.error(String.format(
              "0xCDA63: The type of the interface (%s) could not be calculated",
              side.getCDQualifier().getByType().getClass().getSimpleName()),
              side.getCDQualifier().get_SourcePositionStart());
        }
        else {
          symbol.setTypeQualifier(result.get());
        }
      }
      else if (side.getCDQualifier().isPresentByAttributeName()) {
        // create a surrogate, which is resolved later in the SymbolTableCompleter
        final FieldSymbolSurrogate attributeQualifier = new FieldSymbolSurrogate(
            side.getCDQualifier().getByAttributeName()
        );
        attributeQualifier.setEnclosingScope(side.getEnclosingScope());
        symbol.setAttributeQualifier(attributeQualifier);
      }
    }
  }

  @Override
  public void visit(ASTCDDirectComposition node) {
    throw new IllegalStateException("0xCDA65: Cannot create a symbol for CDDirectComposition, please transform to a CDAssociation.");
  }
}
