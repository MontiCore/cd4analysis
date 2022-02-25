/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdassociation.cocos.ebnf;

import de.monticore.cdassociation._symboltable.CDRoleSymbol;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._cocos.CDBasisASTCDClassCoCo;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.logging.Log;

import java.util.*;

/**
 * Checks that Role and Field names are unique within classes.
 * Fields and roles defined in superclasses are also incorporated.
 */
public class RoleAndFieldNamesUnique implements CDBasisASTCDClassCoCo {

  private static final String FIELD_ROLE_DEFINED_MULTIPLE_TIMES = "Name of the field or role '%s' is not unique for the class '%s'. " + "The role and field names of each class must be unique for the class.";

  /**
   * @param node class to check.
   */
  @Override
  public void check(ASTCDClass node) {
    if (!node.isPresentSymbol()) {
      return;
    }

    Set<String> fieldAndRoleNames = new HashSet<>();
    Deque<OOTypeSymbol> toProcess = new ArrayDeque<>();
    toProcess.add(node.getSymbol());
    Set<OOTypeSymbol> processed = new HashSet<>();

    while (!toProcess.isEmpty()) {
      OOTypeSymbol curSymbol = toProcess.pop();
      processed.add(curSymbol);

      for (FieldSymbol field : curSymbol.getFieldList()) {
        if (fieldAndRoleNames.contains(field.getName())) {
          Log.error(String.format("0xC4A28" + FIELD_ROLE_DEFINED_MULTIPLE_TIMES, field.getName(), node.getName()));
        }
        else {
          fieldAndRoleNames.add(field.getName());
        }
      }

      if (curSymbol instanceof CDTypeSymbol) {
        for (CDRoleSymbol role : ((CDTypeSymbol) curSymbol).getCDRoleList()) {
          if (fieldAndRoleNames.contains(role.getName())) {
            Log.error(String.format("0xC4A29" + FIELD_ROLE_DEFINED_MULTIPLE_TIMES, role.getName(), node.getName()));
          }
          else {
            fieldAndRoleNames.add(role.getName());
          }
        }
      }

      List<SymTypeExpression> superClassExpressions = curSymbol.getSuperClassesOnly();
      for (SymTypeExpression superClassExpression : superClassExpressions) {
        TypeSymbol superClassSymbol = superClassExpression.getTypeInfo();
        if (superClassSymbol instanceof OOTypeSymbol) {
          if (!processed.contains(superClassSymbol)) {
            toProcess.push((OOTypeSymbol) superClassSymbol);
          }
        }
      }
    }
  }

}
