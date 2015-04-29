/*
 * Copyright (c) 2014 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos.ebnf;

import java.util.HashMap;

import de.monticore.cocos.CoCoLog;
import de.monticore.umlcd4a._ast.ASTCDAttribute;
import de.monticore.umlcd4a._ast.ASTCDClass;
import de.monticore.umlcd4a._cocos.CD4AnalysisASTCDClassCoCo;

/**
 * Ensures that an attribute name does not occur twice in a class.
 *
 * @author Robert Heim
 */
public class AttributeUniqueInClassCoco implements CD4AnalysisASTCDClassCoCo {
  
  public static final String ERROR_CODE = "0xC4A15";
  
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
        CoCoLog.error(
            ERROR_CODE,
            String.format(ERROR_MSG_FORMAT, duplicate.getName(), node.getName()),
            node.get_SourcePositionStart());
      }
    }
  }
  
}
