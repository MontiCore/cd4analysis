/*
 * Copyright (c) 2014 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package cd4analysis.cocos;

import java.util.Collection;
import java.util.HashSet;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class CoCoResult {
  
  private Collection<CoCoError> errors = new HashSet<CoCoError>();
  
  public Collection<CoCoError> getErrors() {
    return errors;
  }
  
  public void addError(CoCoError e) {
    errors.add(e);
  }
  
  public boolean isSucceeded() {
    return errors.size() == 0;
  }
}
