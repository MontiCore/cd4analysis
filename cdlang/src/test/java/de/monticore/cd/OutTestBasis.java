/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.After;
import org.junit.BeforeClass;

/** a base class for tests to use stdout and stderr */
public class OutTestBasis extends TestBasis {
  protected static final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  protected static final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

  @BeforeClass
  public static void beforeClass() {
    System.setOut(new PrintStream(outContent));
    System.setErr(new PrintStream(errContent));
  }

  @After
  public void reset() {
    outContent.reset();
    errContent.reset();
  }

  protected String getOut() {
    return outContent.toString();
  }

  protected String getErr() {
    return errContent.toString();
  }

  protected String getAndResetOut() {
    final String out = getOut();
    outContent.reset();
    return out;
  }

  protected String getAndResetErr() {
    final String out = getErr();
    errContent.reset();
    return out;
  }
}
