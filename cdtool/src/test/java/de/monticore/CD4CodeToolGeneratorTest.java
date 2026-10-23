/* (c) https://github.com/MontiCore/monticore */
package de.monticore;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.common.io.Files;
import de.monticore.cd4code.CD4CodeTestBasis;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.jupiter.api.Test;

public class CD4CodeToolGeneratorTest extends CD4CodeTestBasis {
  
  @Test
  public void testLanguageTeaser() throws RecognitionException {
    String[] input = { "-i", "src/test/resources/de/monticore/cd4code/generator/Auction.cd",
        "--gen", "-o", "target/generated/auction" };
    new CD4CodeTool().run(input);
  }
  
  @Test
  public void testLanguageTeaserTemplatePath() throws RecognitionException, IOException {
    String[] input = { "-i", "src/test/resources/de/monticore/cd4code/generator/Auction.cd", "-fp",
        "src/test/resources/templatePath", "--gen", "-o", "target/generated/auctionTP" };
    new de.monticore.CD4CodeTool().run(input);
    
    assertTrue(Files.readLines(new File("target/generated/auctionTP/Auction/auction/Auction.java"),
        Charset.defaultCharset()).contains("// empty body provided by the templatePath arg"),
        "Did not find via templatepath provided template content ");
  }
  
  @Test
  public void testLanguageTeaserConfigTemplate() throws RecognitionException {
    String[] input = { "-i", "src/test/resources/de/monticore/cd4code/generator/Auction.cd", "-ct",
        "cd2java.CD2Java", "--gen", "-o", "target/generated/auctionCT" };
    new de.monticore.CD4CodeTool().run(input);
  }
  
  @Test
  public void testLanguageTeaserTopMechanism() throws RecognitionException {
    String[] input = { "-i", "src/test/resources/de/monticore/cd4code/generator/Auction.cd", "-ct",
        "cd2java.CD2Java", "--gen", "-hwc", "src/test/resources", "-o",
        "target/generated/auctionTOP" };
    new de.monticore.CD4CodeTool().run(input);
  }
  
  @Test
  public void testLanguageTeaserTemplatePathAndDifferentConfigTemplate()
      throws RecognitionException, IOException {
    String[] input = { "-i", "src/test/resources/de/monticore/cd4code/generator/Auction.cd", "-fp",
        "src/test/resources/templatePath", "src/test/resources/templatePath2", "-ct",
        "cd2java.CD2JavaTestCT", "--gen", "-o", "target/generated/auctionTPDCT" };
    new CD4CodeTool().run(input);
    
    // Test if the config template was loaded from the additional template path
    assertTrue(Files.readLines(new File(
        "target/generated/auctionTPDCT/Auction/auction/Auction.java"), Charset.defaultCharset())
        .contains("// empty body provided and configured by the config template"),
        "Did not find via templatepath provided template content ");
  }
  
  @Test
  public void testLanguageTeaserTemplatePathAndConfigTemplate() throws RecognitionException,
      IOException {
    String[] input = { "-i", "src/test/resources/de/monticore/cd4code/generator/Auction.cd", "-fp",
        "src/test/resources/templatePath", "src/test/resources/templatePath2", "-ct",
        "cd2java.CD2Java", "--gen", "-o", "target/generated/auctionTPCT" };
    new CD4CodeTool().run(input);
    
    // Test if the config template was loaded from the additional template path
    assertTrue(Files.readLines(new File(
        "target/generated/auctionTPCT/Auction/auction/Auction.java"), Charset.defaultCharset())
        .contains("// empty body provided and configured by the config template"),
        "Did not find via templatepath provided template content ");
  }
  
  @Test
  public void testLanguageTeaserTemplatePathAndConfigTemplateWithSeparateOptions()
      throws RecognitionException, IOException {
    String[] input = { "-i", "src/test/resources/de/monticore/cd4code/generator/Auction.cd", "-fp",
        "src/test/resources/templatePath", "-fp", "src/test/resources/templatePath2", "-ct",
        "cd2java.CD2Java", "--gen", "-o", "target/generated/auctionTPCTSep" };
    new CD4CodeTool().run(input);
    
    // Test if the config template was loaded from the additional template path
    assertTrue(Files.readLines(new File(
        "target/generated/auctionTPCTSep/Auction/auction/Auction.java"), Charset.defaultCharset())
        .contains("// empty body provided and configured by the config template"),
        "Did not find via templatepath provided template content ");
  }
  
}
