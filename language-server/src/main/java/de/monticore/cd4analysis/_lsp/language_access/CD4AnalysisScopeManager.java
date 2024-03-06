package de.monticore.cd4analysis._lsp.language_access;

import de.monticore.cd4analysis._symboltable.CD4AnalysisSymbolTableCompleter;
import de.monticore.cd4analysis._symboltable.ICD4AnalysisArtifactScope;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.io.paths.MCPath;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.se_rwth.commons.logging.Log;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CD4AnalysisScopeManager extends CD4AnalysisScopeManagerTOP {

  @Override
  public void initGlobalScope(MCPath modelPath) {
    BasicSymbolsMill.init();
    super.initGlobalScope(modelPath);
    syncAccessGlobalScope(gs -> {
        BasicSymbolsMill.initializePrimitives();
        BasicSymbolsMill.initializeString();
    });
  }

  @Override
  public CD4AnalysisArtifactScopeWithFindings createArtifactScope(
      ASTCDCompilationUnit ast, ICD4AnalysisArtifactScope old) {
    CD4AnalysisArtifactScopeWithFindings res = super.createArtifactScope(ast, old);
    ast.accept(new CD4AnalysisSymbolTableCompleter(ast).getTraverser());
    res.findings.addAll(Log.getFindings());

    return res;
  }

  @Override
  public Map<ASTCDCompilationUnit, CD4AnalysisArtifactScopeWithFindings> createAllArtifactScopes(
      Collection<ASTCDCompilationUnit> astNodes) {
    final Map<ASTCDCompilationUnit, CD4AnalysisArtifactScopeWithFindings> res = new HashMap<>();
    syncAccessGlobalScope(
        gs -> {
          clearGlobalScope();
          if (supportsIterativeScopeAppending()) {
            for (ASTCDCompilationUnit node : astNodes) {
              Log.getFindings().clear();
              // Use super.createArtifactScope, so that the completer is not used
              res.put(node, super.createArtifactScope(node, null));
            }

            // Phase 2: complete symbol table when base structure is finished
            for (ASTCDCompilationUnit ast : astNodes) {
              Log.getFindings().clear();
              ast.accept(new CD4AnalysisSymbolTableCompleter(ast).getTraverser());
              res.get(ast).findings.addAll(Log.getFindings());
            }
          }
        });
    return res;
  }
}
