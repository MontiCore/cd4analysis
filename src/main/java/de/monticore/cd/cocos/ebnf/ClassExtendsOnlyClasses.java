/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cd.cocos.ebnf;

import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._cocos.CD4AnalysisASTCDClassCoCo;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbol;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that classes do only extend other classes.
 *
 * @author Robert Heim
 */
public class ClassExtendsOnlyClasses implements CD4AnalysisASTCDClassCoCo {

  @Override
  public void check(ASTCDClass clazz) {
    CDTypeSymbol symbol = (CDTypeSymbol) clazz.getSymbol();
    if (symbol.isPresentSuperClass()) {
      CDTypeSymbol superType = symbol.getSuperClass().getLoadedSymbol();
      if (!superType.isIsClass()) {
        Log.error(String.format(
            "0xC4A08 Class %s cannot extend %s %s. A class may only extend classes.",
            clazz.getName(),
            superType.isIsInterface()
                ? "interface"
                : "enum", superType.getName()),
            clazz.get_SourcePositionStart());
      }
    }
  }
}
