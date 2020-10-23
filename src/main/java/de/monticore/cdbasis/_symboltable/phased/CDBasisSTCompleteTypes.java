package de.monticore.cdbasis._symboltable.phased;

import de.monticore.cd._symboltable.CDSymbolTableHelper;
import de.monticore.cdassociation._symboltable.ICDAssociationScope;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdbasis._symboltable.ICDBasisScope;
import de.monticore.cdbasis._visitor.CDBasisVisitor;
import de.monticore.cdinterfaceandenum._visitor.CDInterfaceAndEnumVisitor;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.logging.Log;

import java.util.Deque;
import java.util.Optional;
import java.util.stream.Collectors;

public class CDBasisSTCompleteTypes
    implements CDBasisVisitor {
  protected Deque<ICDBasisScope> scopeStack; // TODO SVa: can be removed?
  protected CDSymbolTableHelper symbolTableHelper;
  protected CDBasisVisitor realThis = this;

  public CDBasisSTCompleteTypes(Deque<ICDBasisScope> scopeStack, CDSymbolTableHelper symbolTableHelper) {
    this.scopeStack = scopeStack;
    this.symbolTableHelper = symbolTableHelper;
  }

  @Override
  public CDBasisVisitor getRealThis() {
    return realThis;
  }

  @Override
  public void setRealThis(CDBasisVisitor realThis) {
    this.realThis = realThis;
  }

  public void setSymbolTableHelper(CDSymbolTableHelper cdSymbolTableHelper) {
    this.symbolTableHelper = cdSymbolTableHelper;
  }

  @Override
  public void visit(ASTCDCompilationUnit node) {
    Log.debug("Building Symboltable for CD: " + node.getCDDefinition().getName(),
        getClass().getSimpleName());

    symbolTableHelper.setImports(node.getMCImportStatementList());
    CDBasisVisitor.super.visit(node);
  }

  @Override
  public void endVisit(ASTCDCompilationUnit node) {
    // the symbol is a field of the type of the other side
    // as there are handled associations, we at least have a CDAssociationScope
    symbolTableHelper.getHandledRoles().forEach((r, t) ->
        ((ICDAssociationScope) t.getTypeInfo().getSpannedScope()).add(r)
    );
  }

  @Override
  public void visit(ASTCDDefinition node) {
    final ICDBasisScope artifactScope = scopeStack.peekLast();
    assert artifactScope != null;
    artifactScope.setName(node.getName());
    CDBasisVisitor.super.visit(node);
  }

  @Override
  public void visit(ASTCDClass ast) {
    CDBasisVisitor.super.visit(ast);
    final CDTypeSymbol symbol = ast.getSymbol();

    symbol.setIsClass(true);
    symbolTableHelper.getModifierHandler().handle(ast.getModifier(), symbol);

    if (ast.isPresentCDExtendUsage()) {
      symbol.addAllSuperTypes(ast.getCDExtendUsage().streamSuperclass().map(s -> {
        s.setEnclosingScope(scopeStack.peekLast()); // TODO SVa: remove when #2549 is fixed

        /*
        Set<String> qualifiedNames = symbolTableHelper.calculateQualifiedNames(s.getName()); // move to symbolTableHelper.resolveCDType
        // "A" -> ["A", "de.monticore.cdbasis.parser.Simple.A", ...] // qualified name muss die info von allen imports haben

        final Optional<SymTypeExpression> result = symbolTableHelper.resolveCDType(qualifiedNames); // resolve nach allen potentiellen namen
        */

        final Optional<SymTypeExpression> result = symbolTableHelper.getTypeChecker().calculateType(s);
        if (!result.isPresent()) {
          Log.error(String.format(
              "0xCDA00: The type of the extended classes (%s) could not be calculated",
              symbolTableHelper.getPrettyPrinter().prettyprint(s)),
              s.get_SourcePositionStart());
        }

        assert(result.get().getTypeInfo() != null);
        return result;
      }).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList()));
    }

    if (ast.isPresentCDInterfaceUsage()) {
      symbol.addAllSuperTypes(ast.getCDInterfaceUsage().streamInterface().map(s -> {
        s.setEnclosingScope(scopeStack.peekLast()); // TODO SVa: remove when #2549 is fixed
        final Optional<SymTypeExpression> result = symbolTableHelper.getTypeChecker().calculateType(s);
        if (!result.isPresent()) {
          Log.error(String.format(
              "0xCDA01: The type of the interface (%s) could not be calculated",
              s.getClass().getSimpleName()),
              s.get_SourcePositionStart());
        }
        return result;
      }).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList()));
    }
  }

  @Override
  public void traverse(ASTCDClass node) {
    symbolTableHelper.addToCDTypeStack(node.getName());
    CDBasisVisitor.super.traverse(node);
    symbolTableHelper.removeFromCDTypeStack();
  }

  @Override
  public void visit(ASTCDAttribute ast) {
    CDBasisVisitor.super.visit(ast);
    final FieldSymbol symbol = ast.getSymbol();

    symbolTableHelper.getModifierHandler().handle(ast.getModifier(), symbol);

    // don't store the initial value in the ST

    ast.getMCType().setEnclosingScope(scopeStack.peekLast()); // TODO SVa: remove when #2549 is fixed
    final Optional<SymTypeExpression> typeResult = symbolTableHelper.getTypeChecker().calculateType(ast.getMCType());
    if (!typeResult.isPresent()) {
      Log.error(String.format(
          "0xCDA02: The type (%s) of the attribute (%s) could not be calculated",
          symbolTableHelper.getPrettyPrinter().prettyprint(ast.getMCType()),
          ast.getName()),
          ast.getMCType().get_SourcePositionStart());
    }
    else {
      assert(typeResult.get().getTypeInfo() != null);
      symbol.setType(typeResult.get());
    }
  }
}
