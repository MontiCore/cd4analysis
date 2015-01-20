/*
 * Copyright (c) 2014 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package cd4analysis.cocos;

import java.util.HashMap;

import mc.ast.ASTNode;
import cd4analysis.cocos._tobegenerated.CD4AClassCoCo;
import de.cd4analysis._ast.ASTCDAttribute;
import de.cd4analysis._ast.ASTCDClass;
import de.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cocos.AbstractContextCondition;
import de.monticore.cocos.CoCoError;
import de.monticore.cocos.ContextConditionResult;
import de.monticore.utils.ASTNodes;

/**
 * Example CoCo
 *
 * @author Robert Heim
 */
public class UniqueAttributeInClassCoco extends AbstractContextCondition implements CD4AClassCoCo {
  private static String NAME = UniqueAttributeInClassCoco.class.getName();
  
  public static final String ERROR_CODE = "0x???";
  
  public static final String ERROR_MSG_FORMAT = "Attribute %s is already defined in class %s.";
  
  @Override
  public ContextConditionResult check(ASTCDClass node) {
    HashMap<String, ASTCDAttribute> duplicates = new HashMap<>();
    
    for (ASTCDAttribute field : node.getCDAttributes()) {
      node.getCDAttributes().stream()
          .filter(f -> (f != field) && f.getName().equals(field.getName()))
          .forEach(f2 -> duplicates.put(f2.getName(), f2));
    }
    ContextConditionResult result = ContextConditionResult.empty();
    if (!duplicates.isEmpty()) {
      for (ASTCDAttribute duplicate : duplicates.values()) {
        CoCoError e = new CoCoError(
            ERROR_CODE,
            String.format(ERROR_MSG_FORMAT, duplicate.getName(), node.getName()),
            node.get_SourcePositionStart());
        result.addError(e);
      }
    }
    
    return result;
  }
  
  /**
   * @see de.monticore.cocos.ContextCondition#getName()
   */
  @Override
  public String getName() {
    return NAME;
  }
  
}
