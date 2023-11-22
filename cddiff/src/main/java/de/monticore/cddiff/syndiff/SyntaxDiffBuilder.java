package de.monticore.cddiff.syndiff;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;

public class SyntaxDiffBuilder extends DiffPrinter {
  ASTCDCompilationUnit srcCD;
  ASTCDCompilationUnit tgtCD;

  /**
   * Constructs a SyntaxDiffBuilder instance for comparing two CDs.
   *
   * @param srcCD The source CD.
   * @param tgtCD The target CD.
   */
  public SyntaxDiffBuilder(ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD) {
    this.srcCD = srcCD;
    this.tgtCD = tgtCD;
    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(srcCD, tgtCD);
    setStrings(syntaxDiff);
  }

  // --print src
  public String printSrcCD() {
    return outputSrc.toString();
  }
  // --print tgt
  public String printTgtCD() {
    return outputTgt.toString();
  }
  // --print added
  public String printOnlyAdded() {
    return outputAdded.toString();
  }
  // --print deleted
  public String printOnlyDeleted() {
    return outputDeleted.toString();
  }
  // --print changed
  public String printOnlyChanged() {
    return outputChanged.toString();
  }
  // --print diff
  public String printDiff() {
    return outputDiff.toString();
  }
}
