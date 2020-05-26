/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.cocos.ebnf;

import de.monticore.cd.cd4analysis._ast.ASTCDField;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._cocos.CD4AnalysisASTCDClassCoCo;
import de.se_rwth.commons.logging.Log;

import java.util.HashMap;

/**
 * Ensures that an attribute name does not occur twice in a class.
 *
 */
public class AttributeUniqueInClassCoco implements CD4AnalysisASTCDClassCoCo {
  
  @Override
  public void check(ASTCDClass node) {
    HashMap<String, ASTCDField> duplicates = new HashMap<>();
    
    for (ASTCDField field : node.getCDAttributeList()) {
      node.getCDAttributeList().stream()
          .filter(f -> (f != field) && f.getName().equals(field.getName()))
          .forEach(f2 -> duplicates.put(f2.getName(), f2));
    }
    
    if (!duplicates.isEmpty()) {
      for (ASTCDField duplicate : duplicates.values()) {
        Log.error(
            String.format("0xC4A15 Attribute %s is defined multiple times in class %s.",
                duplicate.getName(), node.getName()),
            node.get_SourcePositionStart());
      }
    }
  }
  
}
