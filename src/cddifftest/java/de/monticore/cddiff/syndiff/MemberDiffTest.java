package de.monticore.cddiff.syndiff;

import de.monticore.ast.ASTNode;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffTestBasis;
import de.monticore.cddiff.syndiff.semdiff.CDMemberDiff;
import java.io.IOException;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;

public class MemberDiffTest extends CDDiffTestBasis {

  /*--------------------------------------------------------------------*/
  // Syntax Diff Tests

  public static final String dir =
      "src/cddifftest/resources/de/monticore/cddiff/syndiff/MemberDiff/";
  protected ASTCDCompilationUnit tgt;
  protected ASTCDCompilationUnit src;

  @Test
  public void testMember1() {
    parseModels("Source1.cd", "Target1.cd");

    ASTCDClass cNew = CDTestHelper.getClass("A", src.getCDDefinition());
    ASTCDClass cOld = CDTestHelper.getClass("A", tgt.getCDDefinition());

    ASTNode attributeNew = CDTestHelper.getAttribute(cNew, "a");
    ASTNode attributeOld = CDTestHelper.getAttribute(cOld, "a");

    CDMemberDiff attrDiff = new CDMemberDiff(attributeNew, attributeOld);
    System.out.println(attrDiff.printSrcMember());
    System.out.println(attrDiff.printTgtMember());
    System.out.println(attrDiff.getBaseDiff());
  }

  public void parseModels(String concrete, String ref) {
    try {
      Optional<ASTCDCompilationUnit> src =
          CD4CodeMill.parser().parseCDCompilationUnit(dir + concrete);
      Optional<ASTCDCompilationUnit> tgt = CD4CodeMill.parser().parseCDCompilationUnit(dir + ref);
      if (src.isPresent() && tgt.isPresent()) {
        CD4CodeMill.scopesGenitorDelegator().createFromAST(src.get());
        CD4CodeMill.scopesGenitorDelegator().createFromAST(tgt.get());
        src.get().accept(new CD4CodeSymbolTableCompleter(src.get()).getTraverser());
        tgt.get().accept(new CD4CodeSymbolTableCompleter(tgt.get()).getTraverser());
        this.tgt = tgt.get();
        this.src = src.get();
      } else {
        Assert.fail("Could not parse CDs.");
      }

    } catch (IOException e) {
      Assert.fail(e.getMessage());
    }
  }
}
