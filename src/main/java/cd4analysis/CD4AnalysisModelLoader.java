/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package cd4analysis;

import de.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.modelloader.ModelingLanguageModelLoader;
import de.se_rwth.commons.Names;
import mc.helper.NameHelper;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;

public class CD4AnalysisModelLoader extends ModelingLanguageModelLoader<ASTCDCompilationUnit> {

  public CD4AnalysisModelLoader(CD4AnalysisLanguage language) {
    super(language);
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
}
