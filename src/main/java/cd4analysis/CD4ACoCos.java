/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package cd4analysis;

import cd4analysis.cocos.ebnf.RoleNamesLowerCaseCoco;
import cd4analysis.cocos.ebnf.UniqueAttributeInClassCoco;
import cd4analysis.cocos.mcg.AssociationModifierCoCo;
import cd4analysis.cocos.mcg2ebnf.ClassModifierOnlyAbstractCoCo;
import cd4analysis.cocos.mcg2ebnf.ClassNoConstructorsCoCo;
import cd4analysis.cocos.mcg2ebnf.ClassNoMethodsCoCo;
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
    checker.addCoCo(new RoleNamesLowerCaseCoco());
    // TODO RE<-RH checker.addCoCo(coco);
    
    addMcg2EbnfCoCos(checker);
    
    return checker;
  }
  
  private void addMcg2EbnfCoCos(CD4AnalysisCoCoChecker checker) {
    checker.addCoCo(new ClassModifierOnlyAbstractCoCo());
    checker.addCoCo(new ClassNoConstructorsCoCo());
    checker.addCoCo(new ClassNoMethodsCoCo());
  }
}
