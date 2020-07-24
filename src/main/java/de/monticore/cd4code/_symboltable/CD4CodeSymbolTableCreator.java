/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4code._symboltable;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.se_rwth.commons.Names;

import java.util.Collections;
import java.util.Deque;

public class CD4CodeSymbolTableCreator extends CD4CodeSymbolTableCreatorTOP {
  public CD4CodeSymbolTableCreator(ICD4CodeScope enclosingScope) {
    super(enclosingScope);
    setRealThis(this);
  }

  public CD4CodeSymbolTableCreator(Deque<? extends ICD4CodeScope> scopeStack) {
    super(scopeStack);
    setRealThis(this);
  }

  @Override
  public CD4CodeArtifactScope createFromAST(ASTCDCompilationUnit rootNode) {
    CD4CodeArtifactScope artifactScope = CD4CodeMill
        .cD4CodeArtifactScopeBuilder()
        .setPackageName(
            Names.getQualifiedName(rootNode.isPresentCDPackageStatement() ? rootNode.getCDPackageStatement().getPackageList() : Collections.emptyList()))
        .build();
    putOnStack(artifactScope);
    rootNode.accept(getRealThis());
    return artifactScope;
  }
}
