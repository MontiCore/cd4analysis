/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdassociation._symboltable;

import de.monticore.cd._symboltable.CDSymbolTableHelper;
import de.monticore.cdassociation.CDAssociationMill;
import de.monticore.cdassociation._ast.*;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.typesymbols._symboltable.FieldSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.Deque;
import java.util.List;
import java.util.Optional;

public class CDAssociationSymbolTableCreator
    extends CDAssociationSymbolTableCreatorTOP {
  protected CDSymbolTableHelper symbolTableHelper;

  public CDAssociationSymbolTableCreator(ICDAssociationScope enclosingScope) {
    super(enclosingScope);
    setRealThis(this);
    init();
  }

  public CDAssociationSymbolTableCreator(Deque<? extends ICDAssociationScope> scopeStack) {
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
  public void handle(ASTCDAssociation node) {
    CDAssociationSymbol symbol = create_CDAssociation(node);
    if (symbol != null) {
      initialize_CDAssociation(symbol, node);
      addToScopeAndLinkWithNode(symbol, node);
    }

    SymAssociationBuilder symAssociation = create_SymAssociation(symbol);
    initialize_SymAssociation(symAssociation, node);
  }

  protected SymAssociationBuilder create_SymAssociation(CDAssociationSymbol symbol) {
    final SymAssociationBuilder symAssociationBuilder = CDAssociationMill.symAssocationBuilder();
    if (symbol != null) {
      symAssociationBuilder.setAssociationSymbol(symbol);
    }

    return symAssociationBuilder;
  }

  private void initialize_SymAssociation(SymAssociationBuilder symAssociation, ASTCDAssociation node) {
    if (!node.getLeft().isPresentCDRole()) {
      Log.error(String.format(
          "0xCDA60: The left role of the association (%s) is not set",
          node.getPrintableName()),
          node.get_SourcePositionStart());
      return;
    }
    if (!node.getRight().isPresentCDRole()) {
      Log.error(String.format(
          "0xCDA61: The right role of the association (%s) is not set",
          node.getPrintableName()),
          node.get_SourcePositionStart());
      return;
    }

    // left
    final ASTCDRole leftRole = node.getLeft().getCDRole();
    CDRoleSymbol leftRoleSymbol = create_CDRole(leftRole);
    // the enclosing scope for the type has to be set manually,
    // because we need to resolve the type, even when we don't
    // have a CDAssociationSymbol and therefore no current scope
    node.getLeft().getMCQualifiedType().setEnclosingScope(scopeStack.peekLast()); // TODO SVa: remove when #2549 is fixed
    initialize_CDRole(leftRoleSymbol, node, true);
    symAssociation.setLeftRole(leftRoleSymbol);

    // right
    final ASTCDRole rightRole = node.getLeft().getCDRole();
    CDRoleSymbol rightRoleSymbol = create_CDRole(rightRole);
    node.getRight().getMCQualifiedType().setEnclosingScope(scopeStack.peekLast()); // TODO SVa: remove when #2549 is fixed
    initialize_CDRole(rightRoleSymbol, node, false);
    symAssociation.setRightRole(rightRoleSymbol);

    // the symbol is a field of the type of the other side
    rightRoleSymbol.getType().getTypeInfo().addFieldSymbol(leftRoleSymbol);
    leftRoleSymbol.getType().getTypeInfo().addFieldSymbol(rightRoleSymbol);

    node.getCDAssocType().accept(symbolTableHelper.getAssocTypeVisitor(symAssociation));
  }

  @Override
  protected CDAssociationSymbol create_CDAssociation(ASTCDAssociation ast) {
    if (ast.isPresentName()) {
      return super.create_CDAssociation(ast);
    }
    else {
      return null;
    }
  }

  @Override
  public void visit(ASTCDRole node) {
    // do nothing
  }

  protected void initialize_CDRole(CDRoleSymbol symbol, ASTCDAssociation ast, boolean isLeft) {
    final ASTCDAssocSide side = isLeft ? ast.getLeft() : ast.getRight();
    ASTCDRole role = side.getCDRole();

    initialize_CDRole(symbol, role);

    side.getMCQualifiedType().setEnclosingScope(scopeStack.peekLast()); // TODO SVa: remove when #2549 is fixed
    final Optional<SymTypeExpression> typeResult = symbolTableHelper.getTypeChecker().calculateType(side.getMCQualifiedType());
    if (!typeResult.isPresent()) {
      Log.error(String.format(
          "0xCDA62: The type %s of the role (%s) could not be calculated",
          symbolTableHelper.getPrettyPrinter().prettyprint(side.getMCQualifiedType()),
          side.getName()),
          side.getMCQualifiedType().get_SourcePositionStart());
      return;
    }
    symbol.setType(typeResult.get());

    symbolTableHelper.getModifierHandler().handle(ast.getModifier(), symbol);

    symbolTableHelper.getNavigableVisitor().visit(ast.getCDAssocDir());
    symbol.setIsDefinitiveNavigable(isLeft ? symbolTableHelper.getNavigableVisitor().isDefinitiveNavigableLeft() : symbolTableHelper.getNavigableVisitor().isDefinitiveNavigableRight());

    if (side.isPresentCDCardinality()) {
      symbol.setCardinality(side.getCDCardinality());
    }

    handleQualifier(symbol, side);
    symbol.setIsOrdered(side.isPresentCDOrdered());
  }

  protected void handleQualifier(CDRoleSymbol symbol, ASTCDAssocSide side) {
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
        final SymTypeExpression type = symbol.getAssociation().getOtherRole(symbol).getType();
        final List<FieldSymbol> fieldList = type.getFieldList(side.getCDQualifier().getByAttributeName());
        if (fieldList.size() != 1) {
          Log.error(String.format(
              "0xCDA64: The attribute (%s) of the class (%s) could not be found, but is needed by the qualifier",
              side.getCDQualifier().getByAttributeName(),
              type.print()),
              side.getCDQualifier().get_SourcePositionStart());
        }

        symbol.setAttributeQualifier(fieldList.get(0));
      }
    }
  }

  @Override
  public void visit(ASTCDDirectComposition node) {
    super.visit(node);

    // TODO SVa: transform to association, then this is ignorable

    final ASTCDAssocRightSide side = node.getCDAssocRightSide();

    CDRoleSymbol symbol = create_CDRole(side.getCDRole());
    symbol.setIsVariable(true);
    symbolTableHelper.getModifierHandler().handle(side.getModifier(), symbol);
    symbol.setIsDefinitiveNavigable(true);
    symbol.setCardinality(side.getCDCardinality());
    handleQualifier(symbol, side);
    symbol.setIsOrdered(side.isPresentCDOrdered());
    addToScope(symbol);
  }
}
