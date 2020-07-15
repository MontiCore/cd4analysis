/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd.plantuml;

import de.monticore.cd4analysis._symboltable.CD4AnalysisGlobalScope;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._parser.CD4CodeParser;
import de.monticore.cd4code._symboltable.CD4CodeGlobalScope;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCreatorDelegator;
import de.monticore.cd4code.prettyprint.CD4CodePlantUMLPrettyPrinter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.io.paths.ModelPath;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import net.sourceforge.plantuml.code.Transcoder;
import net.sourceforge.plantuml.code.TranscoderUtil;
import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

public class PlantUMLUtil {
  /**
   * this needs internet - it connects to the plantuml-server to render the image and downloads it
   */
  public static void printCD2PlantUMLServer(String pathCD, String outputPathSVG, PlantUMLConfig plantUMLConfig)
      throws IOException {

    final String cdString = new String(Files.readAllBytes(Paths.get(pathCD)));

    final String plantUMLString = printCD2PlantUML(cdString, plantUMLConfig);
    Transcoder t = TranscoderUtil.getDefaultTranscoder();
    String url = "http://www.plantuml.com/plantuml/svg/" + t.encode(plantUMLString);
    System.out.println(url);
    BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
    String svg = IOUtils.toString(in, "UTF-8");
    try (PrintWriter out = new PrintWriter(outputPathSVG)) {
      out.println(svg);
    }
  }

  /**
   * this needs GraphViz/JDOT installed on your PC
   */
  public static void printCD2PlantUMLLocally(String pathCD, String outputPathSVG, PlantUMLConfig plantUMLConfig)
      throws IOException {

    final String cdString = new String(Files.readAllBytes(Paths.get(pathCD)));

    final String plantUMLString = printCD2PlantUML(cdString, plantUMLConfig);
    final SourceStringReader reader = new SourceStringReader(plantUMLString);
    final ByteArrayOutputStream os = new ByteArrayOutputStream();
    // Write the first image to "os"
    reader.generateImage(os, new FileFormatOption(FileFormat.SVG));
    os.close();

    // The XML is stored into svg
    final String svg = new String(os.toByteArray(), StandardCharsets.UTF_8);
    try (PrintWriter out = new PrintWriter(outputPathSVG)) {
      out.println(svg);
    }
  }

  public static void printCD2PlantUMLModelFileLocally(String pathCD, String outputPath, PlantUMLConfig plantUMLConfig)
      throws IOException {

    final String cdString = new String(Files.readAllBytes(Paths.get(pathCD)));

    final String plantUMLString = printCD2PlantUML(cdString, plantUMLConfig);

    try (PrintWriter out = new PrintWriter(outputPath)) {
      out.println(plantUMLString);
    }
  }

  protected static String printCD2PlantUML(String cdString, PlantUMLConfig config) {
    final PlantUMLPrettyPrintUtil plantUMLPrettyPrintUtil = new PlantUMLPrettyPrintUtil(new IndentPrinter(), config);
    CD4CodePlantUMLPrettyPrinter cdVisitor = new CD4CodePlantUMLPrettyPrinter(plantUMLPrettyPrintUtil);
    CD4CodeParser parser = new CD4CodeParser();
    String plantUMLString = "@startuml\n@enduml";

    try {
      Optional<ASTCDCompilationUnit> astCD = parser.parse_String(cdString);
      if (astCD.isPresent()) {
        final CD4CodeGlobalScope globalScope = CD4CodeMill
            .cD4CodeGlobalScopeBuilder()
            .setModelPath(new ModelPath(Paths.get("")))
            .setModelFileExtension(CD4AnalysisGlobalScope.EXTENSION)
            .addBuiltInTypes()
            .build();
        final CD4CodeSymbolTableCreatorDelegator symbolTableCreator = CD4CodeMill
            .cD4CodeSymbolTableCreatorDelegatorBuilder()
            .setGlobalScope(globalScope)
            .build();
        symbolTableCreator.createFromAST(astCD.get());

        cdVisitor.prettyprint(astCD.get());
        plantUMLString = plantUMLPrettyPrintUtil.getPrinter().getContent();
      }
    }
    catch (IOException e) {
      Log.error("Cannot display CD since it contains errors!");
    }

    return plantUMLString;
  }
}
