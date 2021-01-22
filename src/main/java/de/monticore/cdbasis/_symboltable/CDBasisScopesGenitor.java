/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdbasis._symboltable;

import de.monticore.cd._symboltable.CDSymbolTableHelper;
import de.monticore.cdassociation._symboltable.ICDAssociationScope;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.logging.Log;

import java.util.Deque;
import java.util.Optional;
import java.util.stream.Collectors;

public class CDBasisScopesGenitor extends CDBasisScopesGenitorTOP {
  protected CDSymbolTableHelper symbolTableHelper;

  public CDBasisScopesGenitor(ICDBasisScope enclosingScope) {
    super(enclosingScope);
    init();
  }

  public CDBasisScopesGenitor(Deque<? extends ICDBasisScope> scopeStack) {
    super(scopeStack);
    init();
  }

  public CDBasisScopesGenitor() {
    super();
    init();
  }

  protected void init() {
    symbolTableHelper = new CDSymbolTableHelper();
  }

  public void setSymbolTableHelper(CDSymbolTableHelper cdSymbolTableHelper) {
    this.symbolTableHelper = cdSymbolTableHelper;
  }

  @Override
  public void visit(ASTCDDefinition node) {
    final ICDBasisScope artifactScope = scopeStack.peekLast();
    assert artifactScope != null;
    artifactScope.setName(node.getName());
    super.visit(node);
  }

  @Override
  public void visit(ASTCDClass node) {
    symbolTableHelper.addToCDTypeStack(node.getName());
    super.visit(node);
  }

  @Override
  public void endVisit(ASTCDClass node) {
    super.endVisit(node);
    assert node.getSymbol() != null;
    initialize_CDClass(node.getSymbol(), node);
    symbolTableHelper.removeFromCDTypeStack();
  }

  protected void initialize_CDClass(CDTypeSymbol symbol, ASTCDClass ast) {
    symbol.setIsClass(true);

    symbolTableHelper.getModifierHandler().handle(ast.getModifier(), symbol);

    if (ast.isPresentCDExtendUsage()) {

      // add the !preliminary! SymTypeExpressions of the extended classes to the SuperTypesList of symbol
      symbol.addAllSuperTypes(ast.getCDExtendUsage().streamSuperclass().map(s -> {
        final Optional<SymTypeExpression> result = symbolTableHelper.getTypeChecker().calculateType(s);
        if (!result.isPresent()) {
          Log.error(String.format("0xCDA00: The type of the extended classes (%s) could not be calculated", symbolTableHelper.getPrettyPrinter().prettyprint(s)), s.get_SourcePositionStart());
        }
        return result;
      }).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList()));
    }

    // add the !preliminary! SymTypeExpressions of the implemented interfaces to the SuperTypesList of symbol
    if (ast.isPresentCDInterfaceUsage()) {
      symbol.addAllSuperTypes(ast.getCDInterfaceUsage().streamInterface().map(s -> {
        final Optional<SymTypeExpression> result = symbolTableHelper.getTypeChecker().calculateType(s);
        if (!result.isPresent()) {
          Log.error(String.format("0xCDA01: The type of the interface (%s) could not be calculated", s.getClass().getSimpleName()), s.get_SourcePositionStart());
        }
        return result;
      }).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList()));
    }
  }

  @Override
  public void endVisit(ASTCDAttribute node) {
    super.endVisit(node);
    assert node.getSymbol() != null;
    initialize_CDAttribute(node.getSymbol(), node);
  }

  protected void initialize_CDAttribute(FieldSymbol symbol, ASTCDAttribute ast) {
    symbolTableHelper.getModifierHandler().handle(ast.getModifier(), symbol);

    // calculate !preliminary! type of the field and set it accordingly
    final Optional<SymTypeExpression> typeResult = symbolTableHelper.getTypeChecker().calculateType(ast.getMCType());
    if (!typeResult.isPresent()) {
      Log.error(String.format("0xCDA02: The type (%s) of the attribute (%s) could not be calculated", symbolTableHelper.getPrettyPrinter().prettyprint(ast.getMCType()), ast.getName()), ast.getMCType().get_SourcePositionStart());
    }
    else {
      symbol.setType(typeResult.get());
    }
  }

  @Override
  public void endVisit(ASTCDCompilationUnit node) {
    // the symbol is a field of the type of the other side
    // as there are handled associations, we at least have a CDAssociationScope
    symbolTableHelper.getHandledRoles().forEach((r, t) -> {
      final ICDAssociationScope spannedScope = (ICDAssociationScope) t.getTypeInfo().getSpannedScope();
      if (!spannedScope.getCDRoleSymbols().containsKey(r.getName())) {
        spannedScope.add(r);
      }
    });
  }
}
