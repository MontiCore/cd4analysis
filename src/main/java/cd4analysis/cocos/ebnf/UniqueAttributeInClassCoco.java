/*
 * Copyright (c) 2014 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package cd4analysis.cocos.ebnf;

import java.util.HashMap;

import de.cd4analysis._ast.ASTCDAttribute;
import de.cd4analysis._ast.ASTCDClass;
import de.cd4analysis._cocos.CD4AnalysisASTCDClassCoCo;
import de.monticore.cocos.CoCoHelper;
import de.se_rwth.commons.logging.Log;

/**
 * Ensures that an attribute name does not occur twice in a class.
 *
 * @author Robert Heim
 */
public class UniqueAttributeInClassCoco implements CD4AnalysisASTCDClassCoCo {
  
  // TODO error code
  public static final String ERROR_CODE = "0xCD4AC0015";
  
  public static final String ERROR_MSG_FORMAT = "Attribute %s is defined multiple times in class %s.";
  
  @Override
  public void check(ASTCDClass node) {
    HashMap<String, ASTCDAttribute> duplicates = new HashMap<>();
    
    for (ASTCDAttribute field : node.getCDAttributes()) {
      node.getCDAttributes().stream()
          .filter(f -> (f != field) && f.getName().equals(field.getName()))
          .forEach(f2 -> duplicates.put(f2.getName(), f2));
    }
    
    if (!duplicates.isEmpty()) {
      for (ASTCDAttribute duplicate : duplicates.values()) {
        Log.error(CoCoHelper.buildErrorMsg(
            ERROR_CODE,
            String.format(ERROR_MSG_FORMAT, duplicate.getName(), node.getName()),
            node.get_SourcePositionStart()));
      }
    }
  }
  
}
