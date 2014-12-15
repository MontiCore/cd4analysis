/*
 * Copyright (c) 2014 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package cd4analysis.cocos;

import de.cd4analysis._ast.ASTCDClass;
import de.monticore.cocos.ContextCondition;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public interface ClassCoCo extends ContextCondition {
  public CoCoResult check(ASTCDClass node);
  
  public String getErrorCode();
  
}
