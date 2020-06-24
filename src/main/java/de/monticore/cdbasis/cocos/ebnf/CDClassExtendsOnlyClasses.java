/*
 * (c) https://github.com/MontiCore/monticore
 */

/*
 * (c) https://github.com/MontiCore/monticore
 */

/*
 * (c) https://github.com/MontiCore/monticore
 */

/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cdbasis.cocos.ebnf;

import de.monticore.cd.prettyprint.CDTypeKindPrinter;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._cocos.CDBasisASTCDClassCoCo;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.typesymbols._symboltable.OOTypeSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.List;

/**
 * Checks that classes do only extend other classes.
 */
public class CDClassExtendsOnlyClasses implements CDBasisASTCDClassCoCo {

  @Override
  public void check(ASTCDClass clazz) {
    OOTypeSymbol symbol = clazz.getSymbol();

    if (!clazz.isPresentCDExtendUsage()) {
      return;
    }
    final List<ASTMCObjectType> superclassList = clazz.getCDExtendUsage().getSuperclassList();
    symbol.getSuperTypeList().stream().filter(s -> !s.getTypeInfo().isIsClass()).forEach(e ->
        Log.error(String.format(
            "0xCDC08: Class %s cannot extend %s %s. A class may only extend classes.",
            clazz.getName(),
            new CDTypeKindPrinter().print((ASTCDType) e.getTypeInfo().getAstNode())),
            clazz.get_SourcePositionStart())
    );
  }
}
