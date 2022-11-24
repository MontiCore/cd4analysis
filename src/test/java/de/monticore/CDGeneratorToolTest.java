/* (c) https://github.com/MontiCore/monticore */
package de.monticore;

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CDGeneratorToolTest {

  @Test
  public void testGeneratorTool() {
    CDGeneratorTool.main(new String[] {"-i", "src/test/resources/de/monticore/Example.cd", "-c"});
    assertTrue(true);
  }


}
