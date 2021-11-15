/*
 * (c) https://github.com/MontiCore/monticore
 */
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
          Files.readLines(new File("target/generated/auctionTP/de/monticore/cd4code/parser/Auction.java"),
                      Charset.defaultCharset())
                .contains("// empty body provided by the templatePath arg")
         );
  }

}
