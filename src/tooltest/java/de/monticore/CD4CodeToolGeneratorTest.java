/* (c) https://github.com/MontiCore/monticore */
package de.monticore;

import com.google.common.io.Files;
import de.monticore.cd4code.CD4CodeTestBasis;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Assert;
import org.junit.Test;

public class CD4CodeToolGeneratorTest extends CD4CodeTestBasis {

  @Test
  public void testLanguageTeaser() throws RecognitionException {
    String[] input = {
      "-i",
      "src/test/resources/de/monticore/cd4code/generator/Auction.cd",
      "--gen",
      "-o",
      "target/generated/auction"
    };
    CD4CodeTool.main(input);
  }

  @Test
  public void testLanguageTeaserTemplatePath() throws RecognitionException, IOException {
    String[] input = {
      "-i",
      "src/test/resources/de/monticore/cd4code/generator/Auction.cd",
      "-fp",
      "src/test/resources/templatePath",
      "--gen",
      "-o",
      "target/generated/auctionTP"
    };
    de.monticore.CD4CodeTool.main(input);

    Assert.assertTrue(
        "Did not find via templatepath provided template content ",
        Files.readLines(
                new File(
                    "target/generated/auctionTP/de/monticore/cd4code/parser/auction/auction/Auction.java"),
                Charset.defaultCharset())
            .contains("// empty body provided by the templatePath arg"));
  }

  @Test
  public void testLanguageTeaserConfigTemplate() throws RecognitionException {
    String[] input = {
      "-i",
      "src/test/resources/de/monticore/cd4code/generator/Auction.cd",
      "-ct",
      "cd2java.CD2Java",
      "--gen",
      "-o",
      "target/generated/auctionCT"
    };
    de.monticore.CD4CodeTool.main(input);
  }

  @Test
  public void testLanguageTeaserTopMechanism() throws RecognitionException {
    String[] input = {
      "-i",
      "src/test/resources/de/monticore/cd4code/generator/Auction.cd",
      "-ct",
      "cd2java.CD2Java",
      "--gen",
      "-hwc",
      "src/tooltest/resources",
      "-o",
      "target/generated/auctionTOP"
    };
    de.monticore.CD4CodeTool.main(input);
  }

  @Test
  public void testLanguageTeaserTemplatePathAndDifferentConfigTemplate()
      throws RecognitionException, IOException {
    String[] input = {
      "-i",
      "src/test/resources/de/monticore/cd4code/generator/Auction.cd",
      "-fp",
      "src/test/resources/templatePath",
      "src/test/resources/templatePath2",
      "-ct",
      "cd2java.CD2JavaTestCT",
      "--gen",
      "-o",
      "target/generated/auctionTPDCT"
    };
    CD4CodeTool.main(input);

    // Test if the config template was loaded from the additional template path
    Assert.assertTrue(
        "Did not find via templatepath provided template content ",
        Files.readLines(
                new File(
                    "target/generated/auctionTPDCT/de/monticore/cd4code/parser/auction/auction/Auction.java"),
                Charset.defaultCharset())
            .contains("// empty body provided and configured by the config template"));
  }

  @Test
  public void testLanguageTeaserTemplatePathAndConfigTemplate()
      throws RecognitionException, IOException {
    String[] input = {
      "-i",
      "src/test/resources/de/monticore/cd4code/generator/Auction.cd",
      "-fp",
      "src/test/resources/templatePath",
      "src/test/resources/templatePath2",
      "-ct",
      "cd2java.CD2Java",
      "--gen",
      "-o",
      "target/generated/auctionTPCT"
    };
    CD4CodeTool.main(input);

    // Test if the config template was loaded from the additional template path
    Assert.assertTrue(
        "Did not find via templatepath provided template content ",
        Files.readLines(
                new File(
                    "target/generated/auctionTPCT/de/monticore/cd4code/parser/auction/auction/Auction.java"),
                Charset.defaultCharset())
            .contains("// empty body provided and configured by the config template"));
  }

  @Test
  public void testLanguageTeaserTemplatePathAndConfigTemplateWithSeparateOptions()
      throws RecognitionException, IOException {
    String[] input = {
      "-i",
      "src/test/resources/de/monticore/cd4code/generator/Auction.cd",
      "-fp",
      "src/test/resources/templatePath",
      "-fp",
      "src/test/resources/templatePath2",
      "-ct",
      "cd2java.CD2Java",
      "--gen",
      "-o",
      "target/generated/auctionTPCTSep"
    };
    CD4CodeTool.main(input);

    // Test if the config template was loaded from the additional template path
    Assert.assertTrue(
        "Did not find via templatepath provided template content ",
        Files.readLines(
                new File(
                    "target/generated/auctionTPCTSep/de/monticore/cd4code/parser/auction/auction/Auction.java"),
                Charset.defaultCharset())
            .contains("// empty body provided and configured by the config template"));
  }
}
