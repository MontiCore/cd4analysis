/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdassociation._symboltable;

import de.monticore.cdassociation.CDAssociationMill;
import de.monticore.cdassociation._ast.*;
import de.monticore.cdassociation._visitor.CDAssociationNavigableVisitor;
import de.monticore.cdbasis.modifier.ModifierHandler;
import de.monticore.cdbasis.typescalculator.DeriveSymTypeOfCDBasis;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.typesymbols._symboltable.FieldSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.Deque;
import java.util.List;
import java.util.Optional;

public class CDAssociationSymbolTableCreator
    extends CDAssociationSymbolTableCreatorTOP {
  protected DeriveSymTypeOfCDBasis typeCheck;
  protected ModifierHandler modifierHandler;
  protected CDAssociationNavigableVisitor navigableVisitor;

  public CDAssociationSymbolTableCreator(ICDAssociationScope enclosingScope) {
    super(enclosingScope);
    init();
  }

  public CDAssociationSymbolTableCreator(Deque<? extends ICDAssociationScope> scopeStack) {
    super(scopeStack);
    init();
  }

  protected void init() {
    typeCheck = new DeriveSymTypeOfCDBasis();
    modifierHandler = new ModifierHandler();
    navigableVisitor = CDAssociationMill.associationNavigableVisitor();
  }

  public DeriveSymTypeOfCDBasis getTypeCheck() {
    return typeCheck;
  }

  public ModifierHandler getModifierHandler() {
    return this.modifierHandler;
  }

  public CDAssociationNavigableVisitor getNavigableVisitor() {
    return navigableVisitor;
  }

  public void setTypeCheck(DeriveSymTypeOfCDBasis typeCheck) {
    this.typeCheck = typeCheck;
  }

  public void setModifierHandler(ModifierHandler modifierHandler) {
    this.modifierHandler = modifierHandler;
  }

  public void setNavigableVisitor(CDAssociationNavigableVisitor navigableVisitor) {
    this.navigableVisitor = navigableVisitor;
  }

  @Override
  public void visit(ASTCDAssociation node) {
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
    { // left
      final ASTCDRole leftRole = node.getLeft().getCDRole();

      CDRoleSymbol symbol = create_CDRole(leftRole);
      initialize_CDRole(symbol, node, true);
      symAssociation.setLeftRole(symbol);
      addToScopeAndLinkWithNode(symbol, leftRole);
    }
    { // left
      final ASTCDRole leftRole = node.getLeft().getCDRole();

      CDRoleSymbol symbol = create_CDRole(leftRole);
      initialize_CDRole(symbol, node, false);
      symAssociation.setRightRole(symbol);
      addToScopeAndLinkWithNode(symbol, leftRole);
    }
    symAssociation.setIsAssociation(node.isAssociation());
    symAssociation.setIsAssociation(node.isComposition());
    symAssociation.setIsDerived(node.isDerived());
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
    final ASTCDAssociationSide side = isLeft ? ast.getLeft() : ast.getRight();
    ASTCDRole role = side.getCDRole();

    super.initialize_CDRole(symbol, role);

    getModifierHandler().handle(ast.getModifier(), symbol);
    getNavigableVisitor().visit(ast.getCDAssociationDirection());

    symbol.setIsDefinitiveNavigable(isLeft ? getNavigableVisitor().isDefinitiveNavigableLeft() : getNavigableVisitor().isDefinitiveNavigableRight());
    symbol.setCardinality(side.getCDCardinality());

    handleQualifier(symbol, side);
    symbol.setIsOrdered(side.isPresentCDOrdered());
  }

  protected void handleQualifier(CDRoleSymbol symbol, ASTCDAssociationSide side) {
    if (side.isPresentCDQualifier()) {
      if (side.getCDQualifier().isPresentByType()) {
        final Optional<SymTypeExpression> result = getTypeCheck().calculateType(side.getCDQualifier().getByType());
        if (!result.isPresent()) {
          Log.error(String.format("0xA0000: The type of the interface (%s) could not be calculated", side.getCDQualifier().getByType().getClass().getSimpleName()));
        }
        else {
          symbol.setTypeQualifier(result.get());
        }
      }
      else if (side.getCDQualifier().isPresentByAttributeName()) {
        final SymTypeExpression type = symbol.getAssociation().getOtherRole(symbol).getType();
        final List<FieldSymbol> fieldList = type.getFieldList(side.getCDQualifier().getByAttributeName());
        if (fieldList.size() != 1) {
          Log.error(String.format("0xA0000: The attribute (%s) of the class (%s) could not be found, but is needed by the qualifier", side.getCDQualifier().getByAttributeName(), type.print()));
        }

        symbol.setAttributeQualifier(fieldList.get(0));
      }
    }
  }

  @Override
  public void visit(ASTCDDirectComposition node) {
    super.visit(node);

    // TODO SVa: should this create a SymAssociation? yes - transform to association, then this is ignorable

    final ASTCDAssociationRightSide side = node.getCDAssociationRightSide();

    CDRoleSymbol symbol = create_CDRole(side.getCDRole());
    symbol.setIsVariable(true);
    getModifierHandler().handle(side.getModifier(), symbol);
    symbol.setIsDefinitiveNavigable(true);
    symbol.setCardinality(side.getCDCardinality());
    handleQualifier(symbol, side);
    symbol.setIsOrdered(side.isPresentCDOrdered());
    addToScope(symbol);
  }
}
