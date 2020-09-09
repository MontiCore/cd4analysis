/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdbasis._symboltable;

import de.monticore.cd._symboltable.CDSymbolTableHelper;
import de.monticore.cdassociation._symboltable.ICDAssociationScope;
import de.monticore.cdbasis.CDBasisMill;
import de.monticore.cdbasis._ast.*;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symboltable.ImportStatement;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

import java.util.Collections;
import java.util.Deque;
import java.util.Optional;
import java.util.stream.Collectors;

public class CDBasisSymbolTableCreator extends CDBasisSymbolTableCreatorTOP {
  protected CDSymbolTableHelper symbolTableHelper;

  public CDBasisSymbolTableCreator(ICDBasisScope enclosingScope) {
    super(enclosingScope);
    init();
  }

  public CDBasisSymbolTableCreator(Deque<? extends ICDBasisScope> scopeStack) {
    super(scopeStack);
    init();
  }

  protected void init() {
    setRealThis(this);
    symbolTableHelper = new CDSymbolTableHelper();
  }

  public void setSymbolTableHelper(CDSymbolTableHelper cdSymbolTableHelper) {
    this.symbolTableHelper = cdSymbolTableHelper;
  }

  @Override
  public ICDBasisArtifactScope createFromAST(ASTCDCompilationUnit rootNode) {
    ICDBasisArtifactScope artifactScope = CDBasisMill
        .cDBasisArtifactScopeBuilder()
        .setPackageName(
            Names.getQualifiedName(rootNode.isPresentCDPackageStatement() ? rootNode.getCDPackageStatement().getPackageList() : Collections.emptyList()))
        .setImportsList(rootNode.getMCImportStatementList().stream().map(i -> new ImportStatement(i.getQName(), i.isStar())).collect(Collectors.toList()))
        .build();
    putOnStack(artifactScope);
    rootNode.accept(getRealThis());
    return artifactScope;
  }

  @Override
  public void visit(ASTCDCompilationUnit node) {
    Log.debug("Building Symboltable for CD: " + node.getCDDefinition().getName(),
        getClass().getSimpleName());

    symbolTableHelper.setImports(node.getMCImportStatementList());

    super.visit(node);
  }

  @Override
  public void visit(ASTCDDefinition node) {
    final ICDBasisScope artifactScope = scopeStack.peekLast();
    assert artifactScope != null;
    artifactScope.setName(node.getName());
    super.visit(node);
  }

  @Override
  public void visit(ASTCDPackage node) {
    super.visit(node);
    assert scopeStack.peekLast() != null;
    scopeStack.peekLast().setName(node.getMCQualifiedName().getQName());
  }

  @Override
  public void visit(ASTCDClass node) {
    symbolTableHelper.addToCDTypeStack(node.getName());
    super.visit(node);
  }

  @Override
  public void endVisit(ASTCDClass node) {
    super.endVisit(node);
    symbolTableHelper.removeFromCDTypeStack();
  }

  @Override
  protected void initialize_CDClass(CDTypeSymbol symbol, ASTCDClass ast) {
    super.initialize_CDClass(symbol, ast);
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
  protected void initialize_CDAttribute(FieldSymbol symbol, ASTCDAttribute ast) {
    super.initialize_CDAttribute(symbol, ast);

    symbolTableHelper.getModifierHandler().handle(ast.getModifier(), symbol);

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
      symbol.setType(typeResult.get());
    }

    // don't store the initial value in the ST
  }

  @Override
  public void endVisit(ASTCDCompilationUnit node) {
    symbolTableHelper.getHandledAssociations().forEach(a -> {
      // the symbol is a field of the type of the other side
      // as there are handled associations, we at least have a CDAssociationScope
      ((ICDAssociationScope) a.getLeft().getType().getTypeInfo().getSpannedScope()).add(a.getRight());
      ((ICDAssociationScope) a.getRight().getType().getTypeInfo().getSpannedScope()).add(a.getLeft());
    });
  }
}
