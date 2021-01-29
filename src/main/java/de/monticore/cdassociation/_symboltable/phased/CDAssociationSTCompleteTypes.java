package de.monticore.cdassociation._symboltable.phased;

import de.monticore.cd._symboltable.CDSymbolTableHelper;
import de.monticore.cdassociation.CDAssociationMill;
import de.monticore.cdassociation._ast.*;
import de.monticore.cdassociation._symboltable.CDAssociationSymbol;
import de.monticore.cdassociation._symboltable.CDRoleSymbol;
import de.monticore.cdassociation._symboltable.SymAssociationBuilder;
import de.monticore.cdassociation._visitor.CDAssociationVisitor;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.Optional;
import java.util.Stack;

public class CDAssociationSTCompleteTypes implements CDAssociationVisitor {
  protected CDAssociationVisitor realThis = this;
  protected CDSymbolTableHelper symbolTableHelper;
  protected Stack<ASTCDAssociation> cdAssociationStack = new Stack<>();
  protected Stack<ASTCDAssocSide> cdAssocSideStack = new Stack<>();

  public CDAssociationSTCompleteTypes(CDSymbolTableHelper symbolTableHelper) {
    this.symbolTableHelper = symbolTableHelper;
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
  public void endVisit(ASTCDAssociation node) {
    if (node.getLeft().isPresentSymbol()) {
      node.getLeft().getSymbol().setIsLeft(true);
    }
    if (node.getRight().isPresentSymbol()) {
      node.getRight().getSymbol().setIsLeft(false);
    }

    handle_SymAssociation(node);
    CDAssociationVisitor.super.endVisit(node);
  }

  @Override
  public void traverse(ASTCDAssociation node) {
    this.cdAssociationStack.push(node);
    CDAssociationVisitor.super.traverse(node);
    this.cdAssociationStack.pop();
  }

  @Override
  public void visit(ASTCDRole node) {
    final CDRoleSymbol symbol = node.getSymbol();
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

    final Optional<SymTypeExpression> typeResult = getSymTypeExpression(side, assoc);
    if (!typeResult.isPresent()) {
      return;
    }
    symbol.setType(typeResult.get());
    handleQualifier(symbol, side);

    final Optional<SymTypeExpression> oppositeTypeResult = getSymTypeExpression(side.isLeft() ? assoc.getRight() : assoc.getLeft(), assoc);
    if (!oppositeTypeResult.isPresent()) {
      return;
    }
    symbolTableHelper.addToHandledRoles(symbol, oppositeTypeResult.get().getTypeInfo());
  }

  @Override
  public void traverse(ASTCDAssocLeftSide node) {
    this.cdAssocSideStack.push(node);
    CDAssociationVisitor.super.traverse(node);
    this.cdAssocSideStack.pop();
  }

  @Override
  public void traverse(ASTCDAssocRightSide node) {
    this.cdAssocSideStack.push(node);
    CDAssociationVisitor.super.traverse(node);
    this.cdAssocSideStack.pop();
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

  protected Optional<SymTypeExpression> getSymTypeExpression(ASTCDAssocSide side, ASTCDAssociation assoc) {
    final Optional<SymTypeExpression> typeResult = symbolTableHelper.getTypeChecker().calculateType(side.getMCQualifiedType());
    if (!typeResult.isPresent()) {
      Log.error(String.format(
          "0xCDA62: The type %s of the role (%s) could not be calculated",
          symbolTableHelper.getPrettyPrinter().prettyprint(side.getMCQualifiedType()),
          side.getName(assoc)),
          side.getMCQualifiedType().get_SourcePositionStart());
    }

    assert(typeResult.get().getTypeInfo() != null);
    return typeResult;
  }

  protected void handleQualifier(CDRoleSymbol symbol, ASTCDAssocSide side) {
    if (side.isPresentCDQualifier()) {
      if (side.getCDQualifier().isPresentByType()) {
        final Optional<SymTypeExpression> result = symbolTableHelper.getTypeChecker().calculateType(side.getCDQualifier().getByType());
        if (!result.isPresent()) {
          Log.error(String.format(
              "0xCDA63: The type of the class/interface (%s) could not be calculated",
              side.getCDQualifier().getByType().getClass().getSimpleName()),
              side.getCDQualifier().get_SourcePositionStart());
        }
        else {

          assert(result.get().getTypeInfo() != null);
          symbol.setTypeQualifier(result.get());
        }
      }
      else if (side.getCDQualifier().isPresentByAttributeName()) {
        final SymTypeExpression type = symbol.getAssoc().getOtherRole(symbol).getType();
        final List<VariableSymbol> variableList = type.getFieldList(side.getCDQualifier().getByAttributeName());
        if (variableList.size() == 0) {
          Log.error(String.format(
              "0xCDA64: The attribute (%s) of the class (%s) could not be found, but is needed by the qualifier",
              side.getCDQualifier().getByAttributeName(),
              type.print()),
              side.getCDQualifier().get_SourcePositionStart());
          return;
        }
        else if (variableList.size() > 1) {
          Log.error(String.format(
              "0xCDA65: The attribute (%s) of the class (%s) is not ambiguous, but is needed by the qualifier",
              side.getCDQualifier().getByAttributeName(),
              type.print()),
              side.getCDQualifier().get_SourcePositionStart());
          return;
        }

        symbol.setAttributeQualifier(variableList.get(0));
      }
    }
  }
}
