/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2alloy.cocos;

import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._cocos.CDBasisASTCDCompilationUnitCoCo;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Symbols that are operators in Alloy should not be contained in the cd
 *
 */
public class NotAlloyOperator implements CDBasisASTCDCompilationUnitCoCo {

  /**
   * @see CDBasisASTCDCompilationUnitCoCo#check(ASTCDCompilationUnit)
   */
  public void check(ASTCDCompilationUnit node) {
    // Check based on printed result

    // Get PrettyPrinter
    CD4CodeFullPrettyPrinter cdPrinter = new CD4CodeFullPrettyPrinter();
    // Output result
    String usedInput = cdPrinter.prettyprint(node);

    // Check if the used input contains illegal symbols, which are operators in
    // alloy
    List<String> illegalSymbols = new ArrayList<>();
    illegalSymbols.add("$");
    illegalSymbols.add("%");
    illegalSymbols.add("?");
    illegalSymbols.add("!");
    illegalSymbols.add("_");
    illegalSymbols.add("\"");
    illegalSymbols.add("'");


    for (String symbol : illegalSymbols) {
      if (usedInput.contains(symbol)) {
          // In current MontiCore this just works with warnings and
          // not with errors, because of a FIXME in the error case
          Log.warn(
              String.format("Symbol %s is not allowed, as it is already defined in alloy.",
                  symbol),
              node.get_SourcePositionStart());
      }
    }


  }

}
