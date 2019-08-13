package de.monticore.cd.prettyprint;

import de.monticore.cd.cd4analysis._parser.CD4AnalysisParser;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
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
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

public class PrintCD2PlantUML {

    /** this needs internet - it connects to the plantuml-server to render the image and downloads it */
    public static void printCD2PlantUMLServer(String pathCD, String outputPathSVG, Boolean showAtt, Boolean showAssoc,
                                              Boolean showRoles, Boolean showCard) throws IOException {
        printCD2PlantUMLServer(pathCD, outputPathSVG, showAtt, showAssoc, showRoles, showCard, false, false,-1, -1);
    }

    /** this needs internet - it connects to the plantuml-server to render the image and downloads it */
    public static void printCD2PlantUMLServer(String pathCD, String outputPathSVG, Boolean showAtt, Boolean showAssoc,
                                               Boolean showRoles, Boolean showCard, boolean ortho, boolean shortenWords, int nodesep, int ranksep) throws IOException {

        final String cdString = new String(Files.readAllBytes(Paths.get(pathCD)));
        final String plantUMLString = printCD2PlantUML(cdString, showAtt, showAssoc, showRoles, showCard, ortho, shortenWords, nodesep, ranksep);
        Transcoder t = TranscoderUtil.getDefaultTranscoder();
        String url = "http://www.plantuml.com/plantuml/svg/" + t.encode(plantUMLString);
        System.out.println(url);
        BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
        String svg = IOUtils.toString(in, "UTF-8");
        try (PrintWriter out = new PrintWriter(outputPathSVG)) {
            out.println(svg);
        }
    }

    /** this needs GraphViz/JDOT installed on your PC */
    public static void printCD2PlantUMLLocally(String pathCD, String outputPathSVG, Boolean showAtt, Boolean showAssoc,
                                              Boolean showRoles, Boolean showCard) throws IOException {
        printCD2PlantUMLLocally(pathCD, outputPathSVG, showAtt, showAssoc, showRoles, showCard, false, false, -1, -1);
    }

    /** this needs GraphViz/JDOT installed on your PC */
    public static void printCD2PlantUMLLocally(String pathCD, String outputPathSVG, Boolean showAtt, Boolean showAssoc,
                                             Boolean showRoles, Boolean showCard, boolean ortho, boolean shortenWords, int nodesep, int ranksep) throws IOException {

        final String cdString = new String(Files.readAllBytes(Paths.get(pathCD)));
        final String plantUMLString = printCD2PlantUML(cdString, showAtt, showAssoc, showRoles, showCard, ortho, shortenWords, nodesep, ranksep);
        final SourceStringReader reader = new SourceStringReader(plantUMLString);
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        // Write the first image to "os"
        reader.generateImage(os, new FileFormatOption(FileFormat.SVG));
        os.close();

        // The XML is stored into svg
        final String svg = new String(os.toByteArray(), Charset.forName("UTF-8"));
        try (PrintWriter out = new PrintWriter(outputPathSVG)) {
            out.println(svg);
        }
    }


    protected static String printCD2PlantUML(String cdString, Boolean showAtt, Boolean showAssoc,
                                             Boolean showRoles, Boolean showCard, boolean ortho, boolean shortenWords, int nodesep, int ranksep) {
        IndentPrinter printer = new IndentPrinter();
        CD4A2PlantUMLVisitor cdVisitor = new CD4A2PlantUMLVisitor(printer, showAtt, showAssoc, showRoles, showCard, ortho, shortenWords, nodesep, ranksep);
        CD4AnalysisParser parser = new CD4AnalysisParser();
        String plantUMLString = "@startuml\n@enduml";

        try {
            Optional<ASTCDCompilationUnit> astCD = parser.parse_String(cdString);
            if (astCD.isPresent()) {
                cdVisitor.print2PlantUML(astCD.get());
                plantUMLString = printer.getContent();
            }
        } catch (IOException e) {
            Log.error("Cannot display CD since it contains errors!");
        }

        return plantUMLString;
    }

}
