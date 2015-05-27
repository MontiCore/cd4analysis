/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;
import de.monticore.modelloader.ModelingLanguageModelLoader;
import de.monticore.symboltable.ArtifactScope;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolverConfiguration;
import de.monticore.symboltable.Scope;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.symboltable.CD4AnalysisSymbolTableCreator;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

public class CD4AnalysisModelLoader extends ModelingLanguageModelLoader<ASTCDCompilationUnit> {

  public CD4AnalysisModelLoader(CD4AnalysisLanguage language) {
    super(language);
  }

  @Override
  protected void createSymbolTableFromAST(final ASTCDCompilationUnit ast, final String modelName,
      final MutableScope enclosingScope, final ResolverConfiguration resolverConfiguration) {
    final CD4AnalysisSymbolTableCreator symbolTableCreator = getModelingLanguage().getSymbolTableCreator
        (resolverConfiguration, enclosingScope).orElse(null);

    if (symbolTableCreator != null) {
      Log.debug("Start creation of symbol table for model \"" + modelName + "\".", CD4AnalysisModelLoader.class
          .getSimpleName());
      final Scope scope = symbolTableCreator.createFromAST(ast);

      if (!(scope instanceof ArtifactScope)) {
        Log.warn("Top scope of model " + modelName + " is expected to be a compilation scope, but"
            + " is scope \"" + scope.getName() + "\"");
      }

      Log.debug("Created symbol table for model \"" + modelName + "\".", CD4AnalysisModelLoader.class
          .getSimpleName());
    }
    else {
      Log.warn("No symbol created, because '" + getModelingLanguage().getName()
          + "' does not define a symbol table creator.");
    }
  }

  @Override
  public CD4AnalysisLanguage getModelingLanguage() {
    return (CD4AnalysisLanguage) super.getModelingLanguage();
  }
}
