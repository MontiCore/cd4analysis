/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/ 
 */
package de.monticore.umlcd4a.cd4analysis._parser;

import java.io.IOException;
import java.util.Optional;

import com.google.common.io.Files;

import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

public class CD4AnalysisParser extends CD4AnalysisParserTOP {
  
  /**
   * Besides parsing, this also checks that the filename equals the model name and
   * the package declaration equals the suffix of the package name of the model.
   * 
   * @see de.monticore.umlcd4a.cd4analysis._parser.CDCompilationUnitMCParser#parse(java.lang.String)
   */
  @Override
  public Optional<de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit> parseCDCompilationUnit(String filename)
      throws IOException {
    Optional<de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit> ast = super
        .parseCDCompilationUnit(filename);
    if (ast.isPresent()) {
      String simpleFileName = Files.getNameWithoutExtension(filename);
      String modelName = ast.get().getCDDefinition().getName();
      String packageName = Names.getPackageFromPath(Names.getPathFromFilename(filename));
      String packageDeclaration = Names.getQualifiedName(ast.get().getPackage());
      if (!modelName.equals(simpleFileName)) {
        Log.error("0xC4A02 The name of the diagram " + modelName
            + " is not identical to the name of the file " + filename
            + " (without its fileextension).");
      }
      if(!packageName.endsWith(packageDeclaration)){
        Log.error("0xC4A03 The package declaration " + packageDeclaration + " of the grammar must not differ from the "
                + "package of the grammar file.");
      }

    }
    return ast;
  }

}
