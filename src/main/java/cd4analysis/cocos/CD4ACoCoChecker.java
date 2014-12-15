/*
 * Copyright (c) 2014 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package cd4analysis.cocos;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class CD4ACoCoChecker extends CoCoChecker {
  public CD4ACoCoChecker() {
    addClassCoCo(new UniqueAttributeInClassCoco());
  }
}
