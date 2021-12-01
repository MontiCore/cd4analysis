/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.plantuml;

import de.monticore.cd4code._parser.CD4CodeParser;
import de.monticore.cd4code.prettyprint.CD4CodePlantUMLFullPrettyPrinter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

public class PlantUMLUtil {
  public static final String PLANTUML_EMPTY = "@startuml\n@enduml";

  /*
  /**
   * this needs internet - it connects to the plantuml-server to render the image and downloads it
   */
  /*
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
  }*/

  /**
   * this needs GraphViz/JDOT installed on your PC
   */
  public static String printCD2PlantUMLLocally(@SuppressWarnings("OptionalUsedAsFieldOrParameterType") Optional<ASTCDCompilationUnit> astCD, String outputPathSVG, PlantUMLConfig plantUMLConfig)
      throws IOException {

    final String plantUMLString = printCD2PlantUML(astCD, plantUMLConfig);
    final SourceStringReader reader = new SourceStringReader(plantUMLString);
    final ByteArrayOutputStream os = new ByteArrayOutputStream();
    // Write the first image to "os"
    reader.outputImage(os, new FileFormatOption(FileFormat.SVG));
    os.close();

    // The XML is stored into svg
    final String svg = new String(os.toByteArray(), StandardCharsets.UTF_8);
    try (PrintWriter out = new PrintWriter(outputPathSVG)) {
      out.println(svg);
    }

    return outputPathSVG;
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
    reader.outputImage(os, new FileFormatOption(FileFormat.SVG));
    os.close();

    // The XML is stored into svg
    final String svg = new String(os.toByteArray(), StandardCharsets.UTF_8);
    try (PrintWriter out = new PrintWriter(outputPathSVG)) {
      out.println(svg);
    }
  }

  public static String printCD2PlantUMLModelFileLocally(@SuppressWarnings("OptionalUsedAsFieldOrParameterType") Optional<ASTCDCompilationUnit> astCD, String outputPath, PlantUMLConfig plantUMLConfig)
      throws IOException {
    final String plantUMLString = printCD2PlantUML(astCD, plantUMLConfig);

    try (PrintWriter out = new PrintWriter(outputPath)) {
      out.println(plantUMLString);
    }

    return outputPath;
  }

  public static void printCD2PlantUMLModelFileLocally(String pathCD, String outputPath, PlantUMLConfig plantUMLConfig)
      throws IOException {
    final String cdString = new String(Files.readAllBytes(Paths.get(pathCD)));

    final String plantUMLString = printCD2PlantUML(cdString, plantUMLConfig);

    try (PrintWriter out = new PrintWriter(outputPath)) {
      out.println(plantUMLString);
    }
  }

  protected static String printCD2PlantUML(@SuppressWarnings("OptionalUsedAsFieldOrParameterType") Optional<ASTCDCompilationUnit> astCD, PlantUMLConfig config) {
    final PlantUMLPrettyPrintUtil plantUMLPrettyPrintUtil = new PlantUMLPrettyPrintUtil(new IndentPrinter(), config);
    CD4CodePlantUMLFullPrettyPrinter cdVisitor = new CD4CodePlantUMLFullPrettyPrinter(plantUMLPrettyPrintUtil);
    if (astCD.isPresent()) {
      plantUMLPrettyPrintUtil.getPrinter().print(cdVisitor.prettyprint(astCD.get()));
      return plantUMLPrettyPrintUtil.getPrinter().getContent();
    }

    return PLANTUML_EMPTY;
  }

  protected static String printCD2PlantUML(String cdString, PlantUMLConfig
      config) {
    CD4CodeParser parser = new CD4CodeParser();

    try {
      Optional<ASTCDCompilationUnit> astCD = parser.parse_String(cdString);
      return printCD2PlantUML(astCD, config);
    }
    catch (IOException e) {
      Log.error("Cannot display CD since it contains errors!");
    }

    return PLANTUML_EMPTY;
  }
}
