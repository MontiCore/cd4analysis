/*
 * Copyright (c) 2014 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package cd4analysis.symboltable;

import com.google.common.base.Optional;
import de.monticore.symboltable.BaseScope;
import de.monticore.symboltable.ScopeManipulationApi;


public class CDTypeScope extends BaseScope {

  public CDTypeScope(Optional<ScopeManipulationApi> enclosingScope) {
    super(enclosingScope, true);
  }

  public CDTypeScope() {
    this(Optional.absent());
  }
}
