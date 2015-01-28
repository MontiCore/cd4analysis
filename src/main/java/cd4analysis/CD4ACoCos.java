/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package cd4analysis;

import cd4analysis.cocos.ebnf.UniqueAttributeInClassCoco;
import cd4analysis.cocos.mcg.AssociationModifierCoCo;
import de.cd4analysis._cocos.CD4AnalysisCoCoChecker;

/**
 * Set of CoCos for CD4A.
 *
 * @author Robert Heim
 */
public class CD4ACoCos {
  
  public CD4AnalysisCoCoChecker getCheckerForAllCoCos() {
    CD4AnalysisCoCoChecker checker = new CD4AnalysisCoCoChecker();
    
    checker.addCoCo(new UniqueAttributeInClassCoco());
    checker.addCoCo(new AssociationModifierCoCo());
    // TODO RE<-RH checker.addCoCo(coco);
    return checker;
  }
}
