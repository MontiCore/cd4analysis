/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4code._symboltable;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.symboltable.ImportStatement;
import de.se_rwth.commons.Names;

import java.util.Collections;
import java.util.Deque;
import java.util.stream.Collectors;

public class CD4CodeSymbolTableCreator extends CD4CodeSymbolTableCreatorTOP {
  public CD4CodeSymbolTableCreator() {
    super();
    setRealThis(this);
  }

  public CD4CodeSymbolTableCreator(ICD4CodeScope enclosingScope) {
    super(enclosingScope);
    setRealThis(this);
  }

  public CD4CodeSymbolTableCreator(Deque<? extends ICD4CodeScope> scopeStack) {
    super(scopeStack);
    setRealThis(this);
  }

  @Override
  public ICD4CodeArtifactScope createFromAST(ASTCDCompilationUnit rootNode) {
    ICD4CodeArtifactScope artifactScope = CD4CodeMill
        .cD4CodeArtifactScopeBuilder()
        .setPackageName(
            Names.getQualifiedName(rootNode.isPresentCDPackageStatement() ? rootNode.getCDPackageStatement().getPackageList() : Collections.emptyList()))
        .setImportsList(rootNode.getMCImportStatementList().stream().map(i -> new ImportStatement(i.getQName(), i.isStar())).collect(Collectors.toList()))
        .build();
    putOnStack(artifactScope);
    rootNode.accept(getRealThis());
    return artifactScope;
  }
}
