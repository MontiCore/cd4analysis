package de.monticore.preprocessing;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cd4code._symboltable.ICD4CodeGlobalScope;
import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cd4code.trafo.CD4CodeAfterParseTrafo;
import de.monticore.cd4code.trafo.CD4CodeDirectCompositionTrafo;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.lang3.StringUtils;

public class OpenWorldPreProcessor {

  /**
   * Pre-process 2 CDs for OW-CDDiff
   * todo: pre-processing
   */
  public String completeCDs(ASTCDCompilationUnit ast1, ASTCDCompilationUnit ast2){

    CD4CodeFullPrettyPrinter pprinter = new CD4CodeFullPrettyPrinter();
    ast1.accept(pprinter.getTraverser());

    String cd1= pprinter.getPrinter().getContent();

    while (!cd1.endsWith("}")) {
      cd1 = StringUtils.chop(cd1);
    }
    cd1 = StringUtils.chop(cd1);

    Log.info(cd1, this.getClass().getName());

    pprinter = new CD4CodeFullPrettyPrinter();
    ast2.accept(pprinter.getTraverser());

    String cd2= pprinter.getPrinter().getContent();

    while (!cd2.endsWith("}")) {
      cd2 = StringUtils.chop(cd2);
    }
    cd2 = StringUtils.chop(cd2);

    Log.info(cd2, this.getClass().getName());

    //test resolve via symbol table
    new CD4CodeAfterParseTrafo().transform(ast1);
    new CD4CodeDirectCompositionTrafo().transform(ast1);

    new CD4CodeAfterParseTrafo().transform(ast2);
    new CD4CodeDirectCompositionTrafo().transform(ast2);

    ICD4CodeGlobalScope gscope = CD4CodeMill.globalScope();
    BuiltInTypes.addBuiltInTypes(gscope);

    //new CD4CodeAfterParseTrafo().transform(ast2);

    ICD4CodeArtifactScope scope1 = CD4CodeMill.scopesGenitorDelegator().createFromAST(ast1);
    ICD4CodeArtifactScope scope2 = CD4CodeMill.scopesGenitorDelegator().createFromAST(ast2);

    //CD4CodeSymbolTableCompleter tableCompleter1 = new CD4CodeSymbolTableCompleter(ast1);
    //CD4CodeSymbolTableCompleter tableCompleter2 = new CD4CodeSymbolTableCompleter(ast2);

    //ast1.accept(tableCompleter1.getTraverser());
    //ast2.accept(tableCompleter2.getTraverser());

    return scope1.resolveCDTypeDown("Task").get().getFullName();

  }
}
