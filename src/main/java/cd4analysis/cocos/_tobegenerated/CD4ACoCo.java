/*
 * Copyright (c) 2014 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package cd4analysis.cocos._tobegenerated;

import de.cd4analysis._tool.CD4AnalysisBaseInterface;
import de.monticore.cocos.ContextConditionResult;

/**
 * TODO: This interface should be generated as well as all the concrete typed
 * interfaces for each AST-Type
 *
 * @author Robert Heim
 */
public interface CD4ACoCo<T extends CD4AnalysisBaseInterface> {
  public ContextConditionResult check(T node);
}
