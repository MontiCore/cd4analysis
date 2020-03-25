/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cd.cocos.ebnf;

import de.monticore.cd.cd4analysis._ast.ASTCDType;
import de.monticore.cd.cd4analysis._ast.ASTCDDefinition;
import de.monticore.cd.cd4analysis._cocos.CD4AnalysisASTCDDefinitionCoCo;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Checks that type names start upper-case.
 *
 */
public class TypeNameUpperCase implements CD4AnalysisASTCDDefinitionCoCo {
  
  @Override
  public void check(ASTCDDefinition cdDefinition) {
    Collection<ASTCDType> types = new ArrayList<>();
    types.addAll(cdDefinition.getCDClassList());
    check(types, "class");
    types.clear();
    types.addAll(cdDefinition.getCDInterfaceList());
    check(types, "interface");
    types.clear();
    types.addAll(cdDefinition.getCDEnumList());
    check(types, "enum");
  }
  
  /**
   * Does the actual check.
   * 
   * @param types
   * @param kind kind of the types (class, interface, or enum)
   */
  private void check(Collection<ASTCDType> types, String kind) {
    for (ASTCDType cdType : types) {
      if (!Character.isUpperCase(cdType.getName().charAt(0))) {
        Log.error(String.format("0xC4A05 The first character of the %s %s must be upper-case.",
            kind, cdType.getName()),
            cdType.get_SourcePositionStart());
      }
    }
  }
}
