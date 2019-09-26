/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.cd4analysis._parser;

import com.google.common.io.Files;
import de.monticore.cd.cd4analysis._parser.CD4AnalysisParserTOP;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

public class CD4AnalysisParser extends CD4AnalysisParserTOP {
  
  /**
   * Besides parsing, this also checks that the filename equals the model name and
   * the package declaration equals the suffix of the package name of the model.
   * 
   * @see de.monticore.cd.cd4analysis._parser.CDCompilationUnitMCParser#parse(String)
   */
  @Override
  public Optional<de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit> parseCDCompilationUnit(String filename)
      throws IOException {
    Optional<de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit> ast = super
        .parseCDCompilationUnit(filename);
    if (ast.isPresent()) {
      // Use pathName instead of filename (because of correct separators)
      String pathName = Paths.get(filename).toString();
      String simpleFileName = Files.getNameWithoutExtension(pathName);
      String modelName = ast.get().getCDDefinition().getName();
      String packageName = Names.getPackageFromPath(Names.getPathFromFilename(pathName));
      String packageDeclaration = Names.getQualifiedName(ast.get().getPackageList());
      if (!modelName.equals(simpleFileName)) {
        Log.error("0xC4A02 The name of the diagram " + modelName
            + " is not identical to the name of the file " + filename
            + " (without its fileextension).");
      }
      if(!packageName.endsWith(packageDeclaration)){
        Log.error("0xC4A03 The package declaration " + packageDeclaration + " of the diagram must not differ from the "
                + "package of the diagram file.");
      }

    }
    return ast;
  }

}
