/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cd.cocos.ebnf;

import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._cocos.CD4AnalysisASTCDClassCoCo;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbol;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbolLoader;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

/**
 * Checks that classes that extended an external class are abstract in case that
 * the external class does not provide an empty constructor.
 * 
 * @author Robert Heim
 */
public class ClassExtendExternalType implements CD4AnalysisASTCDClassCoCo {
  
  @Override
  public void check(ASTCDClass clazz) {
    CDTypeSymbol symbol = (CDTypeSymbol) clazz.getSymbol();
    Optional<CDTypeSymbolLoader> optSuperType = symbol.getSuperClassOpt();
    if (optSuperType.isPresent()) {
      CDTypeSymbol superType = optSuperType.get().getLoadedSymbol();
      if (isExternal(superType)) {
        boolean hasEmptyConstructor = superType.getMethods()
            .stream()
            .filter(c -> (c.isIsConstructor()) && c.getParameters().isEmpty())
            .count() > 0;
        if (!hasEmptyConstructor) {
          Log.error(
              String
                  .format(
                      "0xC4A36 Class %s extends the external class %s, which does not provide an empty constructor and thus %s must be abstract.",
                      clazz.getName(),
                      superType.getName(),
                      clazz.getName()),
              clazz.get_SourcePositionStart());
        }
      }
    }
  }
  
  private boolean isExternal(CDTypeSymbol s) {
    // TODO PN <- RH how to calculate this? s. #1566
    throw new RuntimeException("not implemented");
  }
}
