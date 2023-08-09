/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4analysis._parser;

import com.google.common.io.Files;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

public class CD4AnalysisParser extends CD4AnalysisParserTOP {
  boolean _checkFileAndPackageName = true;

  public CD4AnalysisParser() {}

  public CD4AnalysisParser(boolean checkFileAndPackageName) {
    this._checkFileAndPackageName = checkFileAndPackageName;
  }

  public static void checkFileAndPackageName(String fileName, ASTCDCompilationUnit ast) {
    String pathName = Paths.get(fileName).toString();

    @SuppressWarnings("UnstableApiUsage")
    String simpleFileName = Files.getNameWithoutExtension(pathName);
    String modelName = ast.getCDDefinition().getName();
    String packageName = Names.getPackageFromPath(Names.getPathFromFilename(pathName));
    String packageDeclaration;
    if (ast.isPresentMCPackageDeclaration()) {
      packageDeclaration = ast.getMCPackageDeclaration().getMCQualifiedName().getQName();
    } else {
      packageDeclaration = "";
    }

    if (!modelName.equals(simpleFileName)) {
      Log.error(
          String.format(
              "0xCD100: The name of the diagram %s"
                  + " is not identical to the name of the file %s"
                  + " (without its file extension).",
              modelName, fileName),
          ast.getCDDefinition().get_SourcePositionStart());
    }
    if (!packageName.endsWith(packageDeclaration)) {
      Log.error(
          String.format(
              "0xCD101: The package declaration %s"
                  + " of the diagram (%s) must not differ from the"
                  + " package of the diagram file.",
              packageDeclaration, fileName),
          ast.isPresentMCPackageDeclaration()
              ? ast.getMCPackageDeclaration().get_SourcePositionStart()
              : ast.get_SourcePositionStart());
    }
  }

  @Override
  public Optional<ASTCDCompilationUnit> parse(String fileName) throws IOException {
    final Optional<ASTCDCompilationUnit> parse = super.parse(fileName);
    if (_checkFileAndPackageName) {
      parse.ifPresent(p -> checkFileAndPackageName(fileName, p));
    }
    return parse;
  }
}
