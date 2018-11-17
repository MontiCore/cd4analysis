package de.monticore.umlcd4a.prettyprint;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import net.sourceforge.plantuml.code.Transcoder;
import net.sourceforge.plantuml.code.TranscoderUtil;
import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;

@Ignore("This test is only for documentation how to create the graphical representation of your class diagram")
public class PrintCD2SVGTest {

    // Use this test to generate your layout
    @Test
    public void testImageServer() throws IOException {
        PrintCD2PlantUML.printCD2PlantUMLServer("src/test/resources/de/monticore/umlcd4a/cocos/TypeAssoc.cd",
                "src/test/resources/de/monticore/umlcd4a/cocos/TypeAssoc.svg", true, true, true, true,
                true, false, 80, 50);
    }

    // Use this test to generate your layout --> to see impact of other layouting options
    @Test
    public void testImageServer2() throws IOException {
        PrintCD2PlantUML.printCD2PlantUMLServer("src/test/resources/de/monticore/umlcd4a/cocos/TypeAssoc.cd",
                "src/test/resources/de/monticore/umlcd4a/cocos/TypeAssoc.svg", true, true, true, false,
                false, true, 100, 100);
    }

    // copied from http://plantuml.com/api
    @Test
    public void testPlantUMLWithJDOT() throws IOException {
        String source = "@startuml\n";
        source += "Bob -> Alice : hello\n";
        source += "@enduml\n";

        SourceStringReader reader = new SourceStringReader(source);
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        // Write the first image to "os"
        String desc = reader.generateImage(os, new FileFormatOption(FileFormat.SVG));
        os.close();

        // The XML is stored into svg
        final String svg = new String(os.toByteArray(), Charset.forName("UTF-8"));
        System.out.println(svg);
    }

    @Test
    public void testPlantUMLServer() throws IOException {
        Transcoder t = TranscoderUtil.getDefaultTranscoder();
        String s = "Alice->Bob: hello1\nAlice->Bob: hello2\n";
        String url = "http://www.plantuml.com/plantuml/svg/" + t.encode(s);
        System.err.println(url);
        BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
        System.out.println();
        System.out.println(IOUtils.toString(in, "UTF-8"));
    }

    @Test
    public void testImageLocally() throws IOException {
        PrintCD2PlantUML.printCD2PlantUMLLocally("src/test/resources/de/monticore/umlcd4a/cocos/TypeAssoc.cd",
                "src/test/resources/de/monticore/umlcd4a/cocos/TypeAssoc.svg", true, true, true, true);
    }

}
