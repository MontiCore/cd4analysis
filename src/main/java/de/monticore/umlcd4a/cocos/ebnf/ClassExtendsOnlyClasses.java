package de.monticore.umlcd4a.cocos.ebnf;

import java.util.Optional;

import de.monticore.cocos.CoCoLog;
import de.monticore.umlcd4a._ast.ASTCDClass;
import de.monticore.umlcd4a._cocos.CD4AnalysisASTCDClassCoCo;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;

/**
 * Checks that classes do only extend other classes.
 * 
 * @author Robert Heim
 */
public class ClassExtendsOnlyClasses implements CD4AnalysisASTCDClassCoCo {
  
  public static final String ERROR_CODE = "0xC4A08";
  
  public static final String ERROR_MSG_FORMAT = "Class %s cannot extend %s %s. A class may only extend classes.";
  
  @Override
  public void check(ASTCDClass iface) {
    CDTypeSymbol symbol = (CDTypeSymbol) iface.getSymbol().get();
    Optional<CDTypeSymbol> optSuperType = symbol.getSuperClass();
    if (optSuperType.isPresent()) {
      CDTypeSymbol superType = optSuperType.get();
      if (!superType.isClass()) {
        CoCoLog.error(ERROR_CODE,
            String.format(ERROR_MSG_FORMAT, iface.getName(),
                superType.isInterface()
                    ? "interface"
                    : "enum", superType.getName()),
            iface.get_SourcePositionStart());
      }
    }
  }
}
