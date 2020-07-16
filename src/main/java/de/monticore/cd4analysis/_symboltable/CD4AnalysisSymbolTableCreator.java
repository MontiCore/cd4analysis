/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4analysis._symboltable;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.se_rwth.commons.Names;

import java.util.Collections;
import java.util.Deque;

public class CD4AnalysisSymbolTableCreator
    extends CD4AnalysisSymbolTableCreatorTOP {

  public CD4AnalysisSymbolTableCreator(ICD4AnalysisScope enclosingScope) {
    super(enclosingScope);
    setRealThis(this);
  }

  public CD4AnalysisSymbolTableCreator(Deque<? extends ICD4AnalysisScope> scopeStack) {
    super(scopeStack);
    setRealThis(this);
  }

  @Override
  public CD4AnalysisArtifactScope createFromAST(ASTCDCompilationUnit rootNode) {
    CD4AnalysisArtifactScope artifactScope = CD4AnalysisMill
        .cD4AnalysisArtifactScopeBuilder()
        .setPackageName(Names.constructQualifiedName(rootNode.isPresentCDPackageStatement() ? rootNode.getCDPackageStatement().getPackageList() : Collections.emptyList()))
        .build();
    artifactScope.setName(rootNode.getCDDefinition().getName());

    putOnStack(artifactScope);
    rootNode.accept(getRealThis());
    return artifactScope;
  }
}
