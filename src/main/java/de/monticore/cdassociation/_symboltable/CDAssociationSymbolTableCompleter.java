/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdassociation._symboltable;

import de.monticore.cd._symboltable.CDSymbolTableHelper;
import de.monticore.cdassociation.CDAssociationMill;
import de.monticore.cdassociation._ast.*;
import de.monticore.cdassociation._visitor.CDAssociationHandler;
import de.monticore.cdassociation._visitor.CDAssociationTraverser;
import de.monticore.cdassociation._visitor.CDAssociationVisitor2;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbolSurrogate;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.Optional;


public class CDAssociationSymbolTableCompleter
    implements CDAssociationVisitor2, CDAssociationHandler {
  protected CDSymbolTableHelper symbolTableHelper;
  protected CDAssociationTraverser traverser;

  public CDAssociationSymbolTableCompleter(CDSymbolTableHelper symbolTableHelper) {
    this.symbolTableHelper = symbolTableHelper;
  }

  public CDAssociationSymbolTableCompleter(List<ASTMCImportStatement> imports, ASTMCQualifiedName packageDeclaration) {
    this.symbolTableHelper = new CDSymbolTableHelper()
        .setPackageDeclaration(packageDeclaration);
  }

  @Override
  public CDAssociationTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(CDAssociationTraverser traverser) {
    this.traverser = traverser;
  }

  public CDSymbolTableHelper getSymbolTableHelper() {
    return symbolTableHelper;
  }

  public void setSymbolTableHelper(CDSymbolTableHelper cdSymbolTableHelper) {
    this.symbolTableHelper = cdSymbolTableHelper;
  }

  @Override
  public void handle(ASTCDAssociation node) {
    if (node.getLeft().isPresentSymbol()) {
      initialize_CDRole(node.getLeft().getSymbol(), node, true);
    }
    if (node.getRight().isPresentSymbol()) {
      initialize_CDRole(node.getRight().getSymbol(), node, false);
    }
    endVisit(node);
  }

  public void initialize_CDRole(CDRoleSymbol symbol, ASTCDAssociation ast, boolean isLeft) {
    final ASTCDAssocSide side = isLeft ? ast.getLeft() : ast.getRight();

    final Optional<SymTypeExpression> typeResult = getSymTypeExpression(ast, side);
    if (!typeResult.isPresent()) {
      return;
    }
    symbol.setType(typeResult.get());

    symbolTableHelper.getModifierHandler().handle(side.getModifier(), symbol);

    CDAssociationTraverser t = CDAssociationMill.traverser();
    t.add4CDAssociation(symbolTableHelper.getNavigableVisitor());
    ast.getCDAssocDir().accept(t);
    symbol.setIsDefinitiveNavigable(isLeft ? symbolTableHelper.getNavigableVisitor().isDefinitiveNavigableLeft() : symbolTableHelper.getNavigableVisitor().isDefinitiveNavigableRight());

    if (side.isPresentCDCardinality()) {
      symbol.setCardinality(side.getCDCardinality());
    }

    handleQualifier(symbol, side);
    symbol.setIsOrdered(side.isPresentCDOrdered());
    symbol.setIsLeft(isLeft);
  }

  protected Optional<SymTypeExpression> getSymTypeExpression(ASTCDAssociation ast, ASTCDAssocSide side) {
    final Optional<SymTypeExpression> typeResult = symbolTableHelper.getTypeChecker().calculateType(side.getMCQualifiedType());
    if (!typeResult.isPresent()) {
      Log.error(String.format(
          "0xCDA62: The type %s of the role (%s) could not be calculated",
          symbolTableHelper.getPrettyPrinter().prettyprint(side.getMCQualifiedType()),
          side.getName(ast)),
          side.getMCQualifiedType().get_SourcePositionStart());
    }

    // check if the type can be resolved

    return typeResult;
  }

  protected void handleQualifier(CDRoleSymbol symbol, ASTCDAssocSide side) {
    if (side.isPresentCDQualifier()) {
      if (side.getCDQualifier().isPresentByType()) {
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
        final Optional<VariableSymbol> variableSymbol = symbol.getEnclosingScope().resolveVariable(side.getCDQualifier().getByAttributeName());
        if (variableSymbol.isPresent()) {
          variableSymbol.get().setEnclosingScope(side.getEnclosingScope());
          symbol.setAttributeQualifier(variableSymbol.get());
        }
      }
    }
  }

  @Override
  public void endVisit(ASTCDAssociation node) {
    final ASTCDAssocLeftSide leftSide = node.getLeft();
    final ASTCDAssocRightSide rightSide = node.getRight();

    final TypeSymbol leftType;
    if (leftSide.isPresentSymbol()) {
      leftType = leftSide.getSymbol().getType().getTypeInfo();
    }
    else {
      final Optional<SymTypeExpression> result = symbolTableHelper.getTypeChecker().calculateType(leftSide.getMCQualifiedType().getMCQualifiedName());
      // Result exists (or an error has already been called)
      leftType = result.get().getTypeInfo();
    }

    final TypeSymbol rightType;
    if (rightSide.isPresentSymbol()) {
      rightType = rightSide.getSymbol().getType().getTypeInfo();
    }
    else {
      final Optional<SymTypeExpression> result = symbolTableHelper.getTypeChecker().calculateType(rightSide.getMCQualifiedType().getMCQualifiedName());
      // Result exists (or an error has already been called)
      rightType = result.get().getTypeInfo();
    }

    if (leftSide.isPresentSymbol()) {
      CDAssociationSymbolTableCompleter.addRoleToTheirType(leftSide.getSymbol(), rightType);
    }
    if (rightSide.isPresentSymbol()) {
      CDAssociationSymbolTableCompleter.addRoleToTheirType(rightSide.getSymbol(), leftType);
    }
  }

  public static void addRoleToTheirType(CDRoleSymbol symbol, TypeSymbol otherType) {
    // move the RoleSymbol to their Type
    final ICDAssociationScope spannedScope = (ICDAssociationScope) otherType.getSpannedScope();

    // remove the role from its current scope(s)
    symbol.getEnclosingScope().remove(symbol);

    if (!spannedScope.getCDRoleSymbols().containsKey(symbol.getName())) {
      // add the symbol to the type; add to all relevant lists
      spannedScope.add(symbol);
    }
  }

  /*
  The following visit methods must be overriden because both implemented interface
  provide default methods for the visit methods.
   */
  public void visit(de.monticore.symboltable.ISymbol node) {
  }

  public void endVisit(de.monticore.symboltable.ISymbol node) {
  }

  public void endVisit(de.monticore.ast.ASTNode node) {
  }

  public void visit(de.monticore.ast.ASTNode node) {
  }

  public void visit(de.monticore.symboltable.IScope node) {
  }

  public void endVisit(de.monticore.symboltable.IScope node) {
  }
}
