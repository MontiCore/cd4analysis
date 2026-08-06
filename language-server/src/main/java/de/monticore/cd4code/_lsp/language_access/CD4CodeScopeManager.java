/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4code._lsp.language_access;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cd4code.trafo.CD4CodeDirectCompositionTrafo;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.io.paths.MCPath;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.se_rwth.commons.logging.Log;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class CD4CodeScopeManager extends CD4CodeScopeManagerTOP {
  
  @Override
  public void initGlobalScope(MCPath modelPath) {
    super.initGlobalScope(modelPath);
    syncAccessGlobalScope(gs -> {
      BuiltInTypes.addBuiltInTypes(gs);
      BasicSymbolsMill.initializeString();
    });
  }
  
  @Override
  public CD4CodeArtifactScopeWithFindings createArtifactScope(ASTCDCompilationUnit ast,
      ICD4CodeArtifactScope old) {
    // Apply transformations required after parsing
    new CD4CodeDirectCompositionTrafo().transform(ast);
    
    CD4CodeArtifactScopeWithFindings res = super.createArtifactScope(ast, old);
    ast.accept(new CD4CodeSymbolTableCompleter(ast).getTraverser());
    res.findings.addAll(Log.getFindings());
    
    return res;
  }
  
  @Override
  public Map<ASTCDCompilationUnit, CD4CodeArtifactScopeWithFindings> createAllArtifactScopes(
      Collection<ASTCDCompilationUnit> astNodes) {
    final Map<ASTCDCompilationUnit, CD4CodeArtifactScopeWithFindings> res = new LinkedHashMap<>();
    syncAccessGlobalScope(gs -> {
      clearGlobalScope();
      if (supportsIterativeScopeAppending()) {
        for (ASTCDCompilationUnit node : astNodes) {
          Log.getFindings().clear();
          // Apply transformations
          new CD4CodeDirectCompositionTrafo().transform(node);
          // Use super.createArtifactScope, so that the completer is not used
          res.put(node, super.createArtifactScope(node, null));
        }
        
        // Phase 2: complete symbol table when base structure is finished
        for (ASTCDCompilationUnit ast : astNodes) {
          Log.getFindings().clear();
          ast.accept(new CD4CodeSymbolTableCompleter(ast).getTraverser());
          res.get(ast).findings.addAll(Log.getFindings());
        }
      }
    });
    return res;
  }
  
}
