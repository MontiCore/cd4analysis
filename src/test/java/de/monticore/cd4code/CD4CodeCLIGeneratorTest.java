/*
 * (c) https://github.com/MontiCore/monticore
 */
package de.monticore.cd4code;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;


public class CD4CodeCLIGeneratorTest extends CD4CodeTestBasis {

  @Test
  public void testLanguageTeaser() throws RecognitionException {
    String[] input = {
      "-i", "src/test/resources/de/monticore/cd4code/generator/Auction.cd",
      "-o", "target/generated/auction"
    };
    CD4CodeCLI.main(input);
  }

}
