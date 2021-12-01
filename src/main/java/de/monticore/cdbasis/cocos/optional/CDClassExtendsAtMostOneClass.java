/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdbasis.cocos.optional;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._cocos.CDBasisASTCDClassCoCo;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Checks that classes do only extend other classes.
 */
public class CDClassExtendsAtMostOneClass implements CDBasisASTCDClassCoCo {
  @Override
  public void check(ASTCDClass clazz) {
    final List<SymTypeExpression> superclassList = clazz.getSymbol().getSuperClassesOnly();
    if (superclassList.size() > 1) {
      Log.error(String.format(
          "0xCDC2F: Class %s cannot extend multiple classes, but extends (%s). A class may only extend one class.",
          clazz.getName(),
          superclassList.stream()
              .map(s -> s.getTypeInfo().getName())
              .collect(Collectors.joining(", "))),
          clazz.get_SourcePositionStart());
    }
  }
}
