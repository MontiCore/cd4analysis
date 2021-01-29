package de.monticore.cdassociation._symboltable;

import de.monticore.cd._symboltable.CDSymbolTableHelper;
import de.monticore.cdassociation._ast.ASTCDAssocLeftSide;
import de.monticore.cdassociation._ast.ASTCDAssocRightSide;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._ast.ASTCDRole;
import de.monticore.cdassociation._visitor.CDAssociationVisitor2;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import net.sourceforge.plantuml.Log;

import java.util.List;
import java.util.Optional;

import static de.monticore.cd._symboltable.CDSymbolTableHelper.*;

public class CDAssociationSymbolTableCompleter
    implements CDAssociationVisitor2 {
  protected final List<ASTMCImportStatement> imports;
  protected final ASTMCQualifiedName packageDeclaration;
  protected final CDSymbolTableHelper symbolTableHelper;

  public CDAssociationSymbolTableCompleter(List<ASTMCImportStatement> imports, ASTMCQualifiedName packageDeclaration) {
    this.imports = imports;
    this.packageDeclaration = packageDeclaration;
    this.symbolTableHelper = new CDSymbolTableHelper();
  }

  public CDSymbolTableHelper getSymbolTableHelper() {
    return symbolTableHelper;
  }

  @Override
  public void visit(CDRoleSymbol symbol) {
    // Compute the !final! SymTypeExpression for the type of the symbol
    {
      // Fetch the preliminary type
      String typeName = symbol.getType().getTypeInfo().getName();

      // store all found type symbols here
      Optional<TypeSymbol> typeSymbol = resolveUniqueTypeSymbol(imports, packageDeclaration, typeName, symbol.getEnclosingScope());

      // replace the !preliminary! SymTypeExpression stored in the field with the !final! one
      typeSymbol.map(SymTypeExpressionFactory::createTypeExpression).ifPresent(symbol::setType);
    }

    if (symbol.isPresentTypeQualifier()) {
      resolveSymTypeExpression(imports, packageDeclaration, symbol.getTypeQualifier(), symbol.getEnclosingScope()).ifPresent(symbol::setTypeQualifier);
    }
    else if (symbol.isPresentAttributeQualifier()) {
      final VariableSymbol variableSymbol = symbol.getAttributeQualifier();
      resolveUniqueVariableSymbol(imports, packageDeclaration, symbol.getType().getTypeInfo().getName(), variableSymbol.getName(), symbol.getEnclosingScope())
          .ifPresent(symbol::setAttributeQualifier);
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
      leftType = resolveUniqueTypeSymbol(imports, packageDeclaration, leftSide.getMCQualifiedType().getMCQualifiedName().getQName(), node.getEnclosingScope()).get();
    }

    final TypeSymbol rightType;
    if (rightSide.isPresentSymbol()) {
      rightType = rightSide.getSymbol().getType().getTypeInfo();
    }
    else {
      rightType = resolveUniqueTypeSymbol(imports, packageDeclaration, rightSide.getMCQualifiedType().getMCQualifiedName().getQName(), node.getEnclosingScope()).get();
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
    symbol.getEnclosingScope().remove((FieldSymbol) symbol);
    symbol.getEnclosingScope().remove((VariableSymbol) symbol);

    if (!spannedScope.getCDRoleSymbols().containsKey(symbol.getName())) {
      // add the symbol to the type; add to all relevant lists
      spannedScope.add(symbol);
      spannedScope.add((FieldSymbol) symbol);
      spannedScope.add((VariableSymbol) symbol);
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
