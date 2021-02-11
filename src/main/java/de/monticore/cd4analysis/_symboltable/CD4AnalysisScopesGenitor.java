/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4analysis._symboltable;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.symboltable.ImportStatement;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.stream.Collectors;

public class CD4AnalysisScopesGenitor
    extends CD4AnalysisScopesGenitorTOP {
  
  public  CD4AnalysisScopesGenitor()  {
    
  }


  public CD4AnalysisScopesGenitor(ICD4AnalysisScope enclosingScope) {
    super(enclosingScope);
  }

  public CD4AnalysisScopesGenitor(Deque<? extends ICD4AnalysisScope> scopeStack) {
    super(scopeStack);
  }

  @Override
  public ICD4AnalysisArtifactScope createFromAST(ASTCDCompilationUnit rootNode) {
    ICD4AnalysisArtifactScope artifactScope = CD4AnalysisMill
        .artifactScope();
    artifactScope.setPackageName(rootNode.isPresentMCPackageDeclaration() ? rootNode.getMCPackageDeclaration().getMCQualifiedName().getQName() : "");
    artifactScope.setImportsList(rootNode.getMCImportStatementList().stream().map(i -> new ImportStatement(i.getQName(), i.isStar())).collect(Collectors.toList()));
    artifactScope.setName(rootNode.getCDDefinition().getName());

    putOnStack(artifactScope);
    rootNode.accept(getTraverser());
    return artifactScope;
  }
}
