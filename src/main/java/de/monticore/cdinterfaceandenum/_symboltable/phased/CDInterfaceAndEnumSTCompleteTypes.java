package de.monticore.cdinterfaceandenum._symboltable.phased;

import de.monticore.cd._symboltable.CDSymbolTableHelper;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdbasis._symboltable.ICDBasisScope;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cdinterfaceandenum._visitor.CDInterfaceAndEnumVisitor;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbolBuilder;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfObject;
import de.se_rwth.commons.logging.Log;

import java.util.Deque;
import java.util.Optional;
import java.util.stream.Collectors;

public class CDInterfaceAndEnumSTCompleteTypes
    implements CDInterfaceAndEnumVisitor {
  protected Deque<ICDBasisScope> scopeStack;
  protected CDSymbolTableHelper symbolTableHelper;
  protected CDInterfaceAndEnumVisitor realThis = this;

  public CDInterfaceAndEnumSTCompleteTypes(Deque<ICDBasisScope> scopeStack, CDSymbolTableHelper symbolTableHelper) {
    this.scopeStack = scopeStack;
    this.symbolTableHelper = symbolTableHelper;
  }

  @Override
  public CDInterfaceAndEnumVisitor getRealThis() {
    return realThis;
  }

  @Override
  public void setRealThis(CDInterfaceAndEnumVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public void visit(CDTypeSymbol node) {
    node.getAstNode().accept(getRealThis());
  }

  @Override
  public void visit(FieldSymbol node) {
    node.getAstNode().accept(getRealThis());
  }

  @Override
  public void visit(ASTCDInterface ast) {
    CDInterfaceAndEnumVisitor.super.visit(ast);
    final CDTypeSymbol symbol = ast.getSymbol();
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

        assert(result.get().getTypeInfo() != null);
        return result;
      }).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList()));
    }
  }

  @Override
  public void traverse(ASTCDInterface node) {
    symbolTableHelper.addToCDTypeStack(node.getName());
    CDInterfaceAndEnumVisitor.super.traverse(node);
    symbolTableHelper.removeFromCDTypeStack();
  }

  @Override
  public void visit(ASTCDEnum ast) {
    CDInterfaceAndEnumVisitor.super.visit(ast);
    final CDTypeSymbol symbol = ast.getSymbol();
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

        assert(result.get().getTypeInfo() != null);
        return result;
      }).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList()));
    }

    // add enum to stack, to later set it as the type of the EnumConstants
    symbolTableHelper.addToCDTypeStack(ast.getName());
  }

  @Override
  public void endVisit(ASTCDEnum node) {
    CDInterfaceAndEnumVisitor.super.endVisit(node);
    symbolTableHelper.removeFromCDTypeStack();
  }

  @Override
  public void traverse(ASTCDEnum node) {
    // add enum to stack, to later set it as the type of the EnumConstants
    symbolTableHelper.addToCDTypeStack(node.getName());
    CDInterfaceAndEnumVisitor.super.traverse(node);
    symbolTableHelper.removeFromCDTypeStack();
  }

  @Override
  public void visit(ASTCDEnumConstant node) {
    CDInterfaceAndEnumVisitor.super.visit(node);
    final FieldSymbol symbol = node.getSymbol();
    symbol.setIsStatic(true);
    symbol.setIsReadOnly(true);
    symbol.setIsFinal(true);
    symbol.setIsPublic(true);

    // create a SymType for the enum, because the type of the enum constant is the enum itself
    final String enumName = symbolTableHelper.getCurrentCDTypeOnStack();
    final SymTypeOfObject typeObject = SymTypeExpressionFactory.createTypeObject(enumName, scopeStack.peekLast());
    symbol.setType(typeObject);

    // Don't store the arguments in the ST
  }
}
