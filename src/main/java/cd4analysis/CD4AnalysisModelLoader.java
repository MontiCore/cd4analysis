/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package cd4analysis;

import cd4analysis.symboltable.CD4AnalysisSymbolTableCreator;
import de.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.modelloader.ModelingLanguageModelLoader;
import de.monticore.symboltable.ArtifactScope;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolverConfiguration;
import de.monticore.symboltable.Scope;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import mc.helper.NameHelper;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;

public class CD4AnalysisModelLoader extends ModelingLanguageModelLoader<ASTCDCompilationUnit> {

  public CD4AnalysisModelLoader(CD4AnalysisLanguage language) {
    super(language);
  }

  @Override
  protected void createSymbolTableFromAST(final ASTCDCompilationUnit ast, final String modelName,
      final MutableScope enclosingScope, final ResolverConfiguration resolverConfiguration) {
    final CD4AnalysisSymbolTableCreator symbolTableCreator = getModelingLanguage().getSymbolTableCreator
        (resolverConfiguration, enclosingScope).orNull();

    if (symbolTableCreator != null) {
      Log.info("Start creation of symbol table for model \"" + modelName + "\".", CD4AnalysisModelLoader.class
          .getSimpleName());
      final Scope scope = symbolTableCreator.createFromAST(ast);

      if (!(scope instanceof ArtifactScope)) {
        Log.warn("Top scope of model " + modelName + " is expected to be a compilation scope, but"
            + " is scope \"" + scope.getName() + "\"");
      }

      Log.info("Created symbol table for model \"" + modelName + "\".", CD4AnalysisModelLoader.class
          .getSimpleName());
    }
    else {
      Log.warn("No symbol created, because '" + getModelingLanguage().getName()
          + "' does not define a symbol table creator.");
    }
  }

  @Override
  protected String calculateModelName(String name) {
    checkArgument(!isNullOrEmpty(name));

    // a.b.CD.MyClass => a.b.CD is model name
    if (NameHelper.isQualifiedName(name)) {
      return Names.getQualifier(name);
    }

    return name;
  }

  @Override
  public CD4AnalysisLanguage getModelingLanguage() {
    return (CD4AnalysisLanguage) super.getModelingLanguage();
  }
}