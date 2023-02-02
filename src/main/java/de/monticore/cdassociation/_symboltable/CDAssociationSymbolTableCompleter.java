/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdassociation._symboltable;

import de.monticore.cdassociation._ast.ASTCDAssocLeftSide;
import de.monticore.cdassociation._ast.ASTCDAssocRightSide;
import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._visitor.CDAssociationHandler;
import de.monticore.cdassociation._visitor.CDAssociationTraverser;
import de.monticore.cdassociation._visitor.CDAssociationVisitor2;
import de.monticore.cdassociation.prettyprint.CDAssociationFullPrettyPrinter;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.FullSynthesizeFromMCBasicTypes;
import de.monticore.types.check.ISynthesize;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCheckResult;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

public class CDAssociationSymbolTableCompleter
    implements CDAssociationVisitor2, CDAssociationHandler {
  protected CDAssociationTraverser traverser;

  protected ISynthesize typeSynthesizer;
  protected CDAssociationFullPrettyPrinter prettyPrinter;

  public CDAssociationSymbolTableCompleter(ISynthesize typeSynthesizer) {
    this.typeSynthesizer = typeSynthesizer;
    prettyPrinter = new CDAssociationFullPrettyPrinter();
  }

  public CDAssociationSymbolTableCompleter() {
    this(new FullSynthesizeFromMCBasicTypes());
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

    symbol.setAssocSide(side);
    final Optional<SymTypeExpression> typeResult = getSymTypeExpression(ast, side);
    if (!typeResult.isPresent()) {
      return;
    }
    symbol.setType(typeResult.get());

    setupModifiers(side.getModifier(), symbol);

    symbol.setIsDefinitiveNavigable(
      isLeft ? ast.getCDAssocDir().isDefinitiveNavigableLeft()
        : ast.getCDAssocDir().isDefinitiveNavigableRight());

    if (side.isPresentCDCardinality()) {
      symbol.setCardinality(side.getCDCardinality());
    }

    handleQualifier(symbol, side);
    symbol.setIsOrdered(side.isPresentCDOrdered());
    symbol.setIsLeft(isLeft);
  }

  protected Optional<SymTypeExpression> getSymTypeExpression(
      ASTCDAssociation ast, ASTCDAssocSide side) {
    final TypeCheckResult typeResult =
        getTypeSynthesizer().synthesizeType(side.getMCQualifiedType());
    if (!typeResult.isPresentResult()) {
      Log.error(
          String.format(
              "0xCDA62: The type %s of the role (%s) could not be calculated",
              getPrettyPrinter().prettyprint(side.getMCQualifiedType()),
              side.getName(ast)),
          side.getMCQualifiedType().get_SourcePositionStart());
      return Optional.empty();
    }

    // check if the type can be resolved

    return Optional.of(typeResult.getResult());
  }

  protected void handleQualifier(CDRoleSymbol symbol, ASTCDAssocSide side) {
    if (side.isPresentCDQualifier()) {
      if (side.getCDQualifier().isPresentByType()) {
        final TypeCheckResult result =
            getTypeSynthesizer().synthesizeType(side.getCDQualifier().getByType());
        if (!result.isPresentResult()) {
          Log.error(
              String.format(
                  "0xCDA63: The type of the interface (%s) could not be calculated",
                  side.getCDQualifier().getByType().getClass().getSimpleName()),
              side.getCDQualifier().get_SourcePositionStart());
        } else {
          symbol.setTypeQualifier(result.getResult());
        }
      } else if (side.getCDQualifier().isPresentByAttributeName()) {
        final Optional<VariableSymbol> variableSymbol =
            symbol.getEnclosingScope().resolveVariable(side.getCDQualifier().getByAttributeName());
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
    } else {
      final TypeCheckResult result =
          getTypeSynthesizer().synthesizeType(leftSide.getMCQualifiedType().getMCQualifiedName());
      leftType = result.getResult().getTypeInfo();
    }

    final TypeSymbol rightType;
    if (rightSide.isPresentSymbol()) {
      rightType = rightSide.getSymbol().getType().getTypeInfo();
    } else {
      final TypeCheckResult result =
          getTypeSynthesizer().synthesizeType(rightSide.getMCQualifiedType().getMCQualifiedName());
      rightType = result.getResult().getTypeInfo();
    }

    if (leftSide.isPresentSymbol()) {
      CDAssociationSymbolTableCompleter.addRoleToTheirType(leftSide.getSymbol(), rightType);
    }
    if (rightSide.isPresentSymbol()) {
      CDAssociationSymbolTableCompleter.addRoleToTheirType(rightSide.getSymbol(), leftType);
    }
  }

  public void setupModifiers(ASTModifier modifier, CDRoleSymbol roleSymbol) {
    roleSymbol.setIsPublic(modifier.isPublic());
    roleSymbol.setIsPrivate(modifier.isPrivate());
    roleSymbol.setIsProtected(modifier.isProtected());
    roleSymbol.setIsStatic(modifier.isStatic());
    roleSymbol.setIsFinal(modifier.isFinal());
    roleSymbol.setIsDerived(modifier.isDerived());
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

  public ISynthesize getTypeSynthesizer() {
    return typeSynthesizer;
  }

  public void setTypeSynthesizer(ISynthesize typeSynthesizer) {
    this.typeSynthesizer = typeSynthesizer;
  }

  public CDAssociationFullPrettyPrinter getPrettyPrinter() {
    return prettyPrinter;
  }

  public void setPrettyPrinter(CDAssociationFullPrettyPrinter prettyPrinter) {
    this.prettyPrinter = prettyPrinter;
  }

  @Override
  public CDAssociationTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(CDAssociationTraverser traverser) {
    this.traverser = traverser;
  }

}
