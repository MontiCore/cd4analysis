/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.plantuml;

import de.monticore.cd4code._parser.CD4CodeParser;
import de.monticore.cd4code.prettyprint.CD4CodePlantUMLFullPrettyPrinter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

public class PlantUMLUtil {
  public static final String PLANTUML_EMPTY = "@startuml\n@enduml";

  /**
   * this needs GraphViz/JDOT installed on your PC
   */
  public static Path writeCdToPlantUmlSvg(
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType") Optional<ASTCDCompilationUnit> astCD,
    Path outputPathSVG,
    PlantUMLConfig plantUMLConfig)
    throws IOException {

    final String plantUMLString = toPlantUmlModelString(astCD, plantUMLConfig);
    final SourceStringReader reader = new SourceStringReader(plantUMLString);
    final ByteArrayOutputStream os = new ByteArrayOutputStream();
    // Write the first image to "os"
    reader.outputImage(os, new FileFormatOption(FileFormat.SVG));
    os.close();

    // The XML is stored into svg
    final String svg = os.toString(StandardCharsets.UTF_8);
    try (PrintWriter out = new PrintWriter(outputPathSVG.toString())) {
      out.println(svg);
    }

    return outputPathSVG;
  }


  /**
   * this needs GraphViz/JDOT installed on your PC
   */
  public static void writeCdToPlantUmlSvg(
    String pathCD, Path outputPathSVG, PlantUMLConfig plantUMLConfig) throws IOException {

    final String cdString = new String(Files.readAllBytes(Paths.get(pathCD)));

    final String plantUMLString = toPlantUmlModelString(cdString, plantUMLConfig);
    final SourceStringReader reader = new SourceStringReader(plantUMLString);
    final ByteArrayOutputStream os = new ByteArrayOutputStream();
    // Write the first image to "os"
    reader.outputImage(os, new FileFormatOption(FileFormat.SVG));
    os.close();

    // The XML is stored into svg
    final String svg = os.toString(StandardCharsets.UTF_8);
    try (PrintWriter out = new PrintWriter(outputPathSVG.toString())) {
      out.println(svg);
    }
  }

  public static Path writeCdToPlantUmlModelFile(
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType") Optional<ASTCDCompilationUnit> astCD,
    Path outputPath,
    PlantUMLConfig plantUMLConfig)
    throws IOException {
    final String plantUMLString = toPlantUmlModelString(astCD, plantUMLConfig);

    try (PrintWriter out = new PrintWriter(outputPath.toString())) {
      out.println(plantUMLString);
    }

    return outputPath;
  }

  public static void writeCdToPlantUmlModelFile(
    String pathCD, Path outputPath, PlantUMLConfig plantUMLConfig) throws IOException {
    final String cdString = new String(Files.readAllBytes(Paths.get(pathCD)));

    final String plantUMLString = toPlantUmlModelString(cdString, plantUMLConfig);

    try (PrintWriter out = new PrintWriter(outputPath.toString())) {
      out.println(plantUMLString);
    }
  }

  protected static String toPlantUmlModelString(
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType") Optional<ASTCDCompilationUnit> astCD,
    PlantUMLConfig config) {
    final PlantUMLPrettyPrintUtil plantUMLPrettyPrintUtil = new PlantUMLPrettyPrintUtil(new IndentPrinter(), config);
    CD4CodePlantUMLFullPrettyPrinter prettyPrinter = new CD4CodePlantUMLFullPrettyPrinter(plantUMLPrettyPrintUtil);
    if (astCD.isPresent()) {
      prettyPrinter.prettyprint(astCD.get());
      return prettyPrinter.getPrinter().getContent();
    }

    return PLANTUML_EMPTY;
  }

  protected static String toPlantUmlModelString(String cdString, PlantUMLConfig config) {
    CD4CodeParser parser = new CD4CodeParser();

    try {
      Optional<ASTCDCompilationUnit> astCD = parser.parse_String(cdString);
      return toPlantUmlModelString(astCD, config);
    } catch (IOException e) {
      Log.error("Cannot display CD since it contains errors!");
    }

    return PLANTUML_EMPTY;
  }

  /////////////////////////////////////////////////////////////////////////////
  // Depricated below
  /////////////////////////////////////////////////////////////////////////////

  /**
   * this needs GraphViz/JDOT installed on your PC
   */
  @Deprecated(forRemoval = true)
  public static String printCD2PlantUMLLocally(
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType") Optional<ASTCDCompilationUnit> astCD,
    String outputPathSVG,
    PlantUMLConfig plantUMLConfig)
    throws IOException {
    return writeCdToPlantUmlSvg(astCD, Path.of(outputPathSVG), plantUMLConfig).toString();
  }

  /**
   * this needs GraphViz/JDOT installed on your PC
   */
  @Deprecated(forRemoval = true)
  public static void printCD2PlantUMLLocally(
    String pathCD, String outputPathSVG, PlantUMLConfig plantUMLConfig) throws IOException {
    writeCdToPlantUmlSvg(pathCD, Path.of(outputPathSVG), plantUMLConfig);
  }

  @Deprecated(forRemoval = true)
  public static String printCD2PlantUMLModelFileLocally(
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType") Optional<ASTCDCompilationUnit> astCD,
    String outputPath,
    PlantUMLConfig plantUMLConfig)
    throws IOException {
    return writeCdToPlantUmlModelFile(astCD, Path.of(outputPath), plantUMLConfig).toString();
  }

  @Deprecated(forRemoval = true)
  public static void printCD2PlantUMLModelFileLocally(
    String pathCD, String outputPath, PlantUMLConfig plantUMLConfig) throws IOException {
    writeCdToPlantUmlModelFile(pathCD, Path.of(outputPath), plantUMLConfig);
  }

  @Deprecated(forRemoval = true)
  protected static String printCD2PlantUML(
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType") Optional<ASTCDCompilationUnit> astCD,
    PlantUMLConfig config) {
    return toPlantUmlModelString(astCD, config);
  }

  @Deprecated(forRemoval = true)
  protected static String printCD2PlantUML(String cdString, PlantUMLConfig config) {
    return toPlantUmlModelString(cdString, config);
  }
}
