/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdbasis._symboltable.phased;

import de.monticore.cd._symboltable.CDSymbolTableHelper;
import de.monticore.cdassociation._symboltable.ICDAssociationScope;
import de.monticore.cdbasis.CDBasisMill;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._symboltable.CDTypeSymbolBuilder;
import de.monticore.cdbasis._symboltable.ICDBasisArtifactScope;
import de.monticore.cdbasis._symboltable.ICDBasisScope;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbolBuilder;
import de.monticore.symboltable.ImportStatement;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

import java.util.Collections;
import java.util.Deque;
import java.util.stream.Collectors;

public class CDBasisScopeSkeletonsCreator
    extends CDBasisScopeSkeletonsCreatorTOP {
  protected CDSymbolTableHelper symbolTableHelper;

  public CDBasisScopeSkeletonsCreator(ICDBasisScope enclosingScope) {
    super(enclosingScope);
    init();
  }

  public CDBasisScopeSkeletonsCreator(Deque<? extends ICDBasisScope> scopeStack) {
    super(scopeStack);
    init();
  }

  protected void init() {
    //setRealThis(this);
    symbolTableHelper = new CDSymbolTableHelper();
  }

  public void setSymbolTableHelper(CDSymbolTableHelper cdSymbolTableHelper) {
    this.symbolTableHelper = cdSymbolTableHelper;
  }

  // TODO SVa: remove?
  @Override
  public ICDBasisArtifactScope createFromAST(ASTCDCompilationUnit rootNode) {
    ICDBasisArtifactScope artifactScope = CDBasisMill
        .artifactScope();
    artifactScope.setPackageName(
            rootNode.isPresentMCPackageDeclaration() ? rootNode.getMCPackageDeclaration().getMCQualifiedName().getQName() : "");
    artifactScope.setImportsList(rootNode.getMCImportStatementList().stream().map(i -> new ImportStatement(i.getQName(), i.isStar())).collect(Collectors.toList()));
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
  protected void initialize_CDClass(CDTypeSymbolBuilder symbol, ASTCDClass ast) {
    super.initialize_CDClass(symbol, ast);
    symbol.setIsClass(true);

    symbolTableHelper.getModifierHandler().handle(ast.getModifier(), symbol);
  }

  @Override
  protected void initialize_CDAttribute(FieldSymbolBuilder symbol, ASTCDAttribute ast) {
    super.initialize_CDAttribute(symbol, ast);

    symbolTableHelper.getModifierHandler().handle(ast.getModifier(), symbol);

    // don't store the initial value in the ST
  }
}
