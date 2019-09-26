/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cd.cocos.ebnf;

import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbol;
import de.monticore.cd.cd4analysis._cocos.CD4AnalysisASTCDClassCoCo;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbolReference;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

/**
 * Checks that classes do only extend other classes.
 *
 * @author Robert Heim
 */
public class ClassExtendsOnlyClasses implements CD4AnalysisASTCDClassCoCo {
  
  @Override
  public void check(ASTCDClass clazz) {
    CDTypeSymbol symbol = (CDTypeSymbol) clazz.getSymbol();
    Optional<CDTypeSymbolReference> optSuperType = symbol.getSuperClass();
    if (optSuperType.isPresent()) {
      CDTypeSymbol superType = optSuperType.get();
      if (!superType.isClass()) {
        Log.error(String.format(
            "0xC4A08 Class %s cannot extend %s %s. A class may only extend classes.",
            clazz.getName(),
            superType.isInterface()
                ? "interface"
                : "enum", superType.getName()),
            clazz.get_SourcePositionStart());
      }
    }
  }
}
