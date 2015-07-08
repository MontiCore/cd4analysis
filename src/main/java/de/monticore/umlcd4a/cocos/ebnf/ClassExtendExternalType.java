package de.monticore.umlcd4a.cocos.ebnf;

import java.util.Optional;

import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisASTCDClassCoCo;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that classes that extended an external class are abstract in case that
 * the external class does not provide an empty constructor.
 * 
 * @author Robert Heim
 */
public class ClassExtendExternalType implements CD4AnalysisASTCDClassCoCo {
  
  @Override
  public void check(ASTCDClass clazz) {
    CDTypeSymbol symbol = (CDTypeSymbol) clazz.getSymbol().get();
    Optional<CDTypeSymbol> optSuperType = symbol.getSuperClass();
    if (optSuperType.isPresent()) {
      CDTypeSymbol superType = optSuperType.get();
      if (isExternal(superType)) {
        boolean hasEmptyConstructor = superType.getConstructors()
            .stream()
            .filter(c -> c.getParameters().isEmpty())
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
