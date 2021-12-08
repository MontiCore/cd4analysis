/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4code;

import com.google.common.io.Files;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;


public class CD4CodeCLIGeneratorTest extends CD4CodeTestBasis {

  @Test
  public void testLanguageTeaser() throws RecognitionException {
    String[] input = {
      "-i", "src/test/resources/de/monticore/cd4code/generator/Auction.cd",
      "-o", "target/generated/auction"
    };
    CD4CodeCLI.main(input);
  }

  @Test
  public void testLanguageTeaserTemplatePath() throws RecognitionException, IOException {
    String[] input = {
      "-i", "src/test/resources/de/monticore/cd4code/generator/Auction.cd",
      "-fp", "src/test/resources/templatePath",
      "-o", "target/generated/auctionTP"
    };
    CD4CodeCLI.main(input);

    Assert.assertTrue("Did not find via templatepath provided template content ",
                      Files.readLines(new File("target/generated/auctionTP/auction/Auction.java"),
                                      Charset.defaultCharset())
                        .contains("// empty body provided by the templatePath arg")
                     );
  }

  @Test
  public void testLanguageTeaserConfigTemplate() throws RecognitionException {
    String[] input = {
      "-i", "src/test/resources/de/monticore/cd4code/generator/Auction.cd",
      "-ct", "cd2java.CD2Java",
      "-o", "target/generated/auctionCT"
    };
    CD4CodeCLI.main(input);
  }


  @Test
  public void testLanguageTeaserTemplatePathAndDifferentConfigTemplate() throws RecognitionException, IOException {
    String[] input = {
      "-i", "src/test/resources/de/monticore/cd4code/generator/Auction.cd",
      "-fp", "src/test/resources/templatePath", "src/test/resources/templatePath2",
      "-ct", "cd2java.CD2JavaTestCT",
      "-o", "target/generated/auctionTPDCT"
    };
    CD4CodeCLI.main(input);

    // Test if the config template was loaded from the additional template path
    Assert.assertTrue("Did not find via templatepath provided template content ",
                      Files.readLines(new File("target/generated/auctionTPDCT/auction/Auction.java"),
                                      Charset.defaultCharset())
                        .contains("// empty body provided and configured by the config template")
                     );
  }

  @Test
  public void testLanguageTeaserTemplatePathAndConfigTemplate() throws RecognitionException, IOException {
    String[] input = {
      "-i", "src/test/resources/de/monticore/cd4code/generator/Auction.cd",
      "-fp", "src/test/resources/templatePath", "src/test/resources/templatePath2",
      "-ct", "cd2java.CD2Java",
      "-o", "target/generated/auctionTPCT"
    };
    CD4CodeCLI.main(input);

    // Test if the config template was loaded from the additional template path
    Assert.assertTrue("Did not find via templatepath provided template content ",
                      Files.readLines(new File("target/generated/auctionTPCT/auction/Auction.java"),
                                      Charset.defaultCharset())
                        .contains("// empty body provided and configured by the config template")
                     );
  }

  @Test
  public void testLanguageTeaserTemplatePathAndConfigTemplateWithSeparateOptions()
    throws RecognitionException, IOException {
    String[] input = {
      "-i", "src/test/resources/de/monticore/cd4code/generator/Auction.cd",
      "-fp", "src/test/resources/templatePath",
      "-fp", "src/test/resources/templatePath2",
      "-ct", "cd2java.CD2Java",
      "-o", "target/generated/auctionTPCTSep"
    };
    CD4CodeCLI.main(input);

    // Test if the config template was loaded from the additional template path
    Assert.assertTrue("Did not find via templatepath provided template content ",
                      Files.readLines(new File("target/generated/auctionTPCTSep/auction/Auction.java"),
                                      Charset.defaultCharset())
                        .contains("// empty body provided and configured by the config template")
                     );
  }

}
