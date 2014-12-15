/*
 * Copyright (c) 2014 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package cd4analysis.cocos;

import java.util.HashMap;

import de.cd4analysis._ast.ASTCDAttribute;
import de.cd4analysis._ast.ASTCDClass;
import de.monticore.cocos.AbstractContextCondition;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class UniqueAttributeInClassCoco extends AbstractContextCondition implements ClassCoCo {
  private static String NAME = CoCoChecker.class.getName();
  
  private String errorCode = "0x???";
  
  private String errorMsgFormat = "Attribute %s is already defined in class %s.";
  
  @Override
  public String getName() {
    return NAME;
  }
  
  /**
   * @see cd4analysis.cocos.ClassCoCo#check(de.cd4analysis._ast.ASTCDClass)
   */
  @Override
  public CoCoResult check(ASTCDClass node) {
    CoCoResult result = new CoCoResult();
    
    HashMap<String, ASTCDAttribute> duplicates = new HashMap<>();
    
    for (ASTCDAttribute field : node.getCDAttributes()) {
      node.getCDAttributes().stream()
          .filter(f -> (f != field) && f.getName().equals(field.getName()))
          .forEach(f2 -> duplicates.put(f2.getName(), f2));
    }
    
    if (!duplicates.isEmpty()) {
      for (ASTCDAttribute duplicate : duplicates.values()) {
        CoCoError e = new CoCoError(
            errorCode,
            String.format(errorMsgFormat, duplicate.getName(), node.getName()),
            "src position");
        result.addError(e);
      }
    }
    return result;
  }
  
  /**
   * @see cd4analysis.cocos.ClassCoCo#getErrorCode()
   */
  @Override
  public String getErrorCode() {
    return errorCode;
  }
  
}
