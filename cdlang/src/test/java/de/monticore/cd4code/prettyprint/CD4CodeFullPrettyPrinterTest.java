/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4code.prettyprint;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code.CD4CodeTestBasis;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import java.io.IOException;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class CD4CodeFullPrettyPrinterTest extends CD4CodeTestBasis {

  @Test
  public void completeModel() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit =
        p.parse(getFilePath("cd4code/parser/MyLife2.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);
    final ASTCDCompilationUnit node = astcdCompilationUnit.get();

    String output = CD4CodeMill.prettyPrint(node, true);

    final Optional<ASTCDCompilationUnit> astcdCompilationUnitReParsed = p.parse_String(output);
    checkNullAndPresence(p, astcdCompilationUnitReParsed);

    final ASTCDCompilationUnit nodeReparsed = astcdCompilationUnitReParsed.get();
    node.deepEquals(nodeReparsed);
  }

  @Test
  public void testParsePrettyPrinted() throws IOException {
    testParsePrettyPrinted("cd4code/parser/Auction.cd");
    testParsePrettyPrinted("cd4code/parser/Complete.cd");
    testParsePrettyPrinted("cd4code/parser/MinimalPackages.cd");
    testParsePrettyPrinted("cd4code/parser/MinimalST.cd");
    testParsePrettyPrinted("cd4code/parser/MyLife2.cd");
    testParsePrettyPrinted("cd4code/parser/Packages.cd");
    testParsePrettyPrinted("cd4code/parser/MinimalPackages.cd");
    testParsePrettyPrinted("cd4code/parser/UseJavaTypes.cd");

    testParsePrettyPrinted("cd4codebasis/parser/Simple.cd");
  }

  protected void testParsePrettyPrinted(String filePath) throws IOException {
    // Test if the pretty-printed output of a model can be parsed again and if that newly parsed
    // model is deep-equaling the original one
    ASTCDCompilationUnit ast = parse(filePath);
    String pretty = CD4CodeMill.prettyPrint(ast, true);

    Optional<ASTCDCompilationUnit> prettyASTOpt = p.parse_String(pretty);
    checkNullAndPresence(p, prettyASTOpt);

    if (!ast.deepEquals(prettyASTOpt.get())) {
      Assert.assertEquals(
          "Did not deep-equal: " + filePath,
          pretty,
          CD4CodeMill.prettyPrint(prettyASTOpt.get(), true));
      Assert.fail("Did not deep-equal: " + filePath);
    }
  }
}
