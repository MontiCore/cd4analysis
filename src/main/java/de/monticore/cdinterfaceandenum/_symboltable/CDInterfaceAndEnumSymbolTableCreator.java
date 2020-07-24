/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdinterfaceandenum._symboltable;

import de.monticore.cd._symboltable.CDSymbolTableHelper;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.Deque;
import java.util.Optional;
import java.util.stream.Collectors;

public class CDInterfaceAndEnumSymbolTableCreator
    extends CDInterfaceAndEnumSymbolTableCreatorTOP {
  protected CDSymbolTableHelper symbolTableHelper;

  public CDInterfaceAndEnumSymbolTableCreator(ICDInterfaceAndEnumScope enclosingScope) {
    super(enclosingScope);
    setRealThis(this);
    init();
  }

  public CDInterfaceAndEnumSymbolTableCreator(Deque<? extends ICDInterfaceAndEnumScope> scopeStack) {
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
  public void visit(ASTCDInterface node) {
    super.visit(node);
    symbolTableHelper.addToCDTypeStack(node.getName());
  }

  @Override
  public void endVisit(ASTCDInterface node) {
    super.endVisit(node);
    symbolTableHelper.removeFromCDTypeStack();
  }

  @Override
  public void visit(ASTCDEnum node) {
    super.visit(node);
    symbolTableHelper.addToCDTypeStack(node.getName());
  }

  @Override
  public void endVisit(ASTCDEnum node) {
    super.endVisit(node);
    symbolTableHelper.removeFromCDTypeStack();
  }

  @Override
  protected void initialize_CDInterface(CDTypeSymbol symbol, ASTCDInterface ast) {
    super.initialize_CDInterface(symbol, ast);
    symbol.setIsInterface(true);

    symbolTableHelper.getModifierHandler().handle(ast.getModifier(), symbol);

    if (ast.isPresentCDExtendUsage()) {
      symbol.addAllSuperTypes(ast.getCDExtendUsage().streamSuperclass().map(s -> {
        s.setEnclosingScope(scopeStack.peekLast()); // TODO SVa: remove when #2549 is fixed
        final Optional<SymTypeExpression> result = symbolTableHelper.getTypeChecker().calculateType(s);
        if (!result.isPresent()) {
          Log.error(String.format(
              "0xCDA30: The type of the extended interfaces (%s) could not be calculated",
              symbolTableHelper.getPrettyPrinter().prettyprint(s)),
              s.get_SourcePositionStart());
        }
        return result;
      }).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList()));
    }
  }

  @Override
  protected void initialize_CDEnum(CDTypeSymbol symbol, ASTCDEnum ast) {
    super.initialize_CDEnum(symbol, ast);
    symbol.setIsEnum(true);

    symbolTableHelper.getModifierHandler().handle(ast.getModifier(), symbol);

    if (ast.isPresentCDInterfaceUsage()) {
      symbol.addAllSuperTypes(ast.getCDInterfaceUsage().streamInterface().map(s -> {
        s.setEnclosingScope(scopeStack.peekLast()); // TODO SVa: remove when #2549 is fixed
        final Optional<SymTypeExpression> result = symbolTableHelper.getTypeChecker().calculateType(s);
        if (!result.isPresent()) {
          Log.error(String.format(
              "0xCDA31: The type of the interface (%s) could not be calculated",
              s.getClass().getSimpleName()),
              s.get_SourcePositionStart());
        }
        return result;
      }).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList()));
    }
  }

  @Override
  protected void initialize_CDEnumConstant(FieldSymbol symbol, ASTCDEnumConstant ast) {
    super.initialize_CDEnumConstant(symbol, ast);
    symbol.setIsStatic(true);
    symbol.setIsReadOnly(true);
    symbol.setIsStatic(true);
    symbol.setIsPublic(true);

    symbol.setType(SymTypeExpressionFactory.createTypeObject(
        symbolTableHelper.getCurrentCDTypeOnStack(),
        scopeStack.peekLast()
    ));
  }
}
