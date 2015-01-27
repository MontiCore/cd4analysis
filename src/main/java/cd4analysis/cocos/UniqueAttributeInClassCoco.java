/*
 * Copyright (c) 2014 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package cd4analysis.cocos;

import java.util.HashMap;

import de.cd4analysis._ast.ASTCDAttribute;
import de.cd4analysis._ast.ASTCDClass;
import de.cd4analysis._cocos.CD4AnalysisASTCDClassCoCo;
import de.monticore.cocos.CoCoHelper;
import de.se_rwth.commons.logging.Log;

/**
 * Example CoCo
 *
 * @author Robert Heim
 */
public class UniqueAttributeInClassCoco implements CD4AnalysisASTCDClassCoCo {
  
  public static final String ERROR_CODE = "0x???";
  
  public static final String ERROR_MSG_FORMAT = "Attribute %s is already defined in class %s.";
  
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
