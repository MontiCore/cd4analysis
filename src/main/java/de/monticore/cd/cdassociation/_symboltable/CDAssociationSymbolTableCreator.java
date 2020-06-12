/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd.cdassociation._symboltable;

import de.monticore.cd.cdassociation.CDAssociationMill;
import de.monticore.cd.cdassociation._ast.*;
import de.monticore.cd.cdassociation._visitor.CDAssociationNavigableVisitor;
import de.monticore.cd.cdbasis.CDBasisMill;
import de.monticore.cd.cdbasis._visitor.SymModifierVisitor;
import de.monticore.cd.cdbasis.typescalculator.DeriveSymTypeOfCDBasis;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.logging.Log;

import java.util.Deque;
import java.util.Optional;

public class CDAssociationSymbolTableCreator
    extends CDAssociationSymbolTableCreatorTOP {
  protected DeriveSymTypeOfCDBasis typeCheck;
  protected SymModifierVisitor symModifierVisitor;
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
    symModifierVisitor = CDBasisMill.symModifierVisitor();
    navigableVisitor = CDAssociationMill.associationNavigableVisitor();
  }

  public DeriveSymTypeOfCDBasis getTypeCheck() {
    return typeCheck;
  }

  public SymModifierVisitor getSymModifierVisitor() {
    return symModifierVisitor;
  }

  public CDAssociationNavigableVisitor getNavigableVisitor() {
    return navigableVisitor;
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

    symbol.setModifier(getSymModifierVisitor().visitAll(side.getCDModifierList()).build());
    getNavigableVisitor().visit(ast.getCDAssociationDirection());

    symbol.setIsNavigable(isLeft ? getNavigableVisitor().isNavigableLeft() : getNavigableVisitor().isNavigableRight());
    symbol.setCardinality(side.getCDCardinality());

    handleQualifier(symbol, side);
    symbol.setIsOrdered(side.isPresentOrdered());
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
        /*final String byAttributeName = side.getCDQualifier().getByAttributeName();
        symbol.setAttributeQualifier(byAttributeName);*/
        // TODO SVa: where to store the field symbol?
      }
    }
  }

  @Override
  public void visit(ASTCDDirectComposition node) {
    super.visit(node);

    // TODO SVa: should this create a SymAssociation?

    final ASTCDAssociationRightSide side = node.getCDAssociationRightSide();

    CDRoleSymbol symbol = create_CDRole(side.getCDRole());
    symbol.setIsVariable(true);
    symbol.setModifier(getSymModifierVisitor().visitAll(side.getCDModifierList()).build());
    symbol.setIsNavigable(true);
    symbol.setCardinality(side.getCDCardinality());
    handleQualifier(symbol, side);
    symbol.setIsOrdered(side.isPresentOrdered());
    addToScope(symbol);
  }
}
