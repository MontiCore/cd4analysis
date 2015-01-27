/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package cd4analysis;

import de.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.modelloader.SpecificModelLoader;

public class CD4AnalysisModelLoader extends SpecificModelLoader<ASTCDCompilationUnit> {

  public CD4AnalysisModelLoader(CD4AnalysisLanguage language) {
    super(language);
  }
}
