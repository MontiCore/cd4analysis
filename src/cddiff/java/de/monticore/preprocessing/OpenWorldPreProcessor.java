package de.monticore.preprocessing;

import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import jline.internal.Log;
import org.apache.commons.lang3.StringUtils;

public class OpenWorldPreProcessor {
  public void completeCDs(ASTCDCompilationUnit ast1, ASTCDCompilationUnit ast2){

    CD4CodeFullPrettyPrinter pprinter = new CD4CodeFullPrettyPrinter();
    ast1.accept(pprinter.getTraverser());

    String cd1= pprinter.getPrinter().getContent();

    while (!cd1.endsWith("}")) {
      cd1 = StringUtils.chop(cd1);
    }
    cd1 = StringUtils.chop(cd1);

    Log.info(cd1);

    pprinter = new CD4CodeFullPrettyPrinter();
    ast2.accept(pprinter.getTraverser());

    String cd2= pprinter.getPrinter().getContent();

    while (!cd2.endsWith("}")) {
      cd2 = StringUtils.chop(cd2);
    }
    cd2 = StringUtils.chop(cd2);

    Log.info(cd2);

    //todo: pre-processing

  }
}
