/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd.plantuml;

import de.monticore.cd4code._parser.CD4CodeParser;
import de.monticore.cd4code.prettyprint.CD4CodePlantUMLPrettyPrinter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
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
    IndentPrinter printer = new IndentPrinter();
    CD4CodePlantUMLPrettyPrinter cdVisitor = new CD4CodePlantUMLPrettyPrinter(printer, config);
    CD4CodeParser parser = new CD4CodeParser();
    String plantUMLString = "@startuml\n@enduml";

    try {
      Optional<ASTCDCompilationUnit> astCD = parser.parse_String(cdString);
      if (astCD.isPresent()) {
        cdVisitor.prettyprint(astCD.get());
        plantUMLString = printer.getContent();
      }
    }
    catch (IOException e) {
      Log.error("Cannot display CD since it contains errors!");
    }

    return plantUMLString;
  }
}
