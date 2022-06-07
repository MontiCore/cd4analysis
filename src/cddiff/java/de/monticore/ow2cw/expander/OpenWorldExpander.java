package de.monticore.ow2cw.expander;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;

public class OpenWorldExpander extends BasicExpander{

  public OpenWorldExpander(ASTCDCompilationUnit cd) {
    super(cd);
  }

  @Override
  public void matchDir(ASTCDAssociation src, ASTCDAssociation target) {
    if ((!src.getCDAssocDir().isBidirectional()) && target.getCDAssocDir()
        .isBidirectional()) {
      src.setCDAssocDir(CD4AnalysisMill.cDBiDirBuilder().build());
    } else {
      super.matchDir(src, target);
    }
  }

  @Override
  public void matchDirInReverse(ASTCDAssociation src, ASTCDAssociation target) {
    if ((!src.getCDAssocDir().isBidirectional()) && target.getCDAssocDir().isBidirectional()) {
      src.setCDAssocDir(CD4AnalysisMill.cDBiDirBuilder().build());
    }
    else {
      super.matchDirInReverse(src, target);
    }
  }

}
