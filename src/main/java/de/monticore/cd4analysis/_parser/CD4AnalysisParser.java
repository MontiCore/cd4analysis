/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4analysis._parser;

import com.google.common.io.Files;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Paths;
import java.util.Optional;

public class CD4AnalysisParser extends CD4AnalysisParserTOP {
  CD4AnalysisAfterParseDelegatorVisitor afterParseTrafo = new CD4AnalysisAfterParseDelegatorVisitor();

  @Override
  public Optional<ASTCDCompilationUnit> parse(String fileName)
      throws IOException {
    final Optional<ASTCDCompilationUnit> parse = super.parse(fileName);
    parse.ifPresent(p -> p.accept(afterParseTrafo));
    parse.ifPresent(p -> checkFileAndPackageName(fileName, p));
    return parse;
  }

  public static void checkFileAndPackageName(String fileName, ASTCDCompilationUnit ast) {
    String pathName = Paths.get(fileName).toString();
    String simpleFileName = Files.getNameWithoutExtension(pathName);
    String modelName = ast.getCDDefinition().getName();
    String packageName = Names.getPackageFromPath(Names.getPathFromFilename(pathName));
    String packageDeclaration;
    if (ast.isPresentCDPackageStatement()) {
      packageDeclaration = Names.getQualifiedName(ast.getCDPackageStatement().getPackageList());
    }
    else {
      packageDeclaration = "";
    }

    if (!modelName.equals(simpleFileName)) {
      Log.error("0xCD100 The name of the diagram " + modelName
          + " is not identical to the name of the file " + fileName
          + " (without its fileextension).");
    }
    if (!packageName.endsWith(packageDeclaration)) {
      Log.error("0xCD100 The package declaration " + packageDeclaration
          + " of the diagram (" + fileName + ") must not differ from the "
          + "package of the diagram file.");
    }
  }

  @Override
  public Optional<ASTCDCompilationUnit> parse(Reader reader)
      throws IOException {
    final Optional<ASTCDCompilationUnit> parse = super.parse(reader);
    parse.ifPresent(p -> p.accept(afterParseTrafo));
    return parse;
  }

  @Override
  public Optional<ASTCDCompilationUnit> parse_String(String str)
      throws IOException {
    final Optional<ASTCDCompilationUnit> parse = super.parse_String(str);
    parse.ifPresent(p -> p.accept(afterParseTrafo));
    return parse;
  }
}
