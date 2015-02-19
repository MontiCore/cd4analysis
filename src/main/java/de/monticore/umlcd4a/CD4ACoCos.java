/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a;

import de.cd4analysis._cocos.CD4AnalysisCoCoChecker;
import de.monticore.umlcd4a.cocos.ebnf.RoleNamesLowerCaseCoco;
import de.monticore.umlcd4a.cocos.ebnf.UniqueAttributeInClassCoco;
import de.monticore.umlcd4a.cocos.mcg.AssociationModifierCoCo;
import de.monticore.umlcd4a.cocos.mcg.AttributeNotAbstractCoCo;
import de.monticore.umlcd4a.cocos.mcg.ClassInvalidModifiersCoCo;
import de.monticore.umlcd4a.cocos.mcg.EnumInvalidModifiersCoCo;
import de.monticore.umlcd4a.cocos.mcg.InterfaceAttributesStaticCoCo;
import de.monticore.umlcd4a.cocos.mcg.InterfaceInvalidModifiersCoCo;
import de.monticore.umlcd4a.cocos.mcg.ModifierNotMultipleVisibilitiesCoCo;
import de.monticore.umlcd4a.cocos.mcg2ebnf.AssociationEndModifierRestrictionCoCo;
import de.monticore.umlcd4a.cocos.mcg2ebnf.AssociationNoStereotypesCoCo;
import de.monticore.umlcd4a.cocos.mcg2ebnf.AttributeModifierOnlyDerivedCoCo;
import de.monticore.umlcd4a.cocos.mcg2ebnf.ClassModifierOnlyAbstractCoCo;
import de.monticore.umlcd4a.cocos.mcg2ebnf.ClassNoConstructorsCoCo;
import de.monticore.umlcd4a.cocos.mcg2ebnf.ClassNoMethodsCoCo;
import de.monticore.umlcd4a.cocos.mcg2ebnf.EnumNoConstructorsCoCo;
import de.monticore.umlcd4a.cocos.mcg2ebnf.EnumNoMethodsCoCo;
import de.monticore.umlcd4a.cocos.mcg2ebnf.EnumNoModifierCoCo;
import de.monticore.umlcd4a.cocos.mcg2ebnf.InterfaceNoAttributesCoCo;
import de.monticore.umlcd4a.cocos.mcg2ebnf.InterfaceNoMethodsCoCo;
import de.monticore.umlcd4a.cocos.mcg2ebnf.InterfaceNoModifierCoCo;

/**
 * Set of CoCos for CD4A.
 *
 * @author Robert Heim
 */
public class CD4ACoCos {
  
  public static CD4AnalysisCoCoChecker getCheckerForAllCoCos() {
    CD4AnalysisCoCoChecker checker = new CD4AnalysisCoCoChecker();
    
    addEbnfCoCos(checker);
    addMcgCoCos(checker);
    addMcg2EbnfCoCos(checker);
    
    return checker;
  }
  
  public static CD4AnalysisCoCoChecker getCheckerForMcgCoCos() {
    CD4AnalysisCoCoChecker checker = new CD4AnalysisCoCoChecker();
    addMcgCoCos(checker);
    return checker;
  }
  
  public static CD4AnalysisCoCoChecker getCheckerForEbnfCoCos() {
    CD4AnalysisCoCoChecker checker = new CD4AnalysisCoCoChecker();
    addEbnfCoCos(checker);
    return checker;
  }
  
  public static CD4AnalysisCoCoChecker getCheckerForMcg2EbnfCoCos() {
    CD4AnalysisCoCoChecker checker = new CD4AnalysisCoCoChecker();
    addMcg2EbnfCoCos(checker);
    return checker;
  }
  
  private static void addEbnfCoCos(CD4AnalysisCoCoChecker checker) {
    checker.addCoCo(new UniqueAttributeInClassCoco());
    checker.addCoCo(new RoleNamesLowerCaseCoco());
    // TODO RE<-RH checker.addCoCo(coco);
  }
  
  private static void addMcgCoCos(CD4AnalysisCoCoChecker checker) {
    checker.addCoCo(new AssociationModifierCoCo());
    checker.addCoCo(new InterfaceAttributesStaticCoCo());
    checker.addCoCo(new InterfaceInvalidModifiersCoCo());
    checker.addCoCo(new AttributeNotAbstractCoCo());
    checker.addCoCo(new ModifierNotMultipleVisibilitiesCoCo());
    checker.addCoCo(new ClassInvalidModifiersCoCo());
    checker.addCoCo(new EnumInvalidModifiersCoCo());
  }
  
  private static void addMcg2EbnfCoCos(CD4AnalysisCoCoChecker checker) {
    checker.addCoCo(new ClassModifierOnlyAbstractCoCo());
    checker.addCoCo(new ClassNoConstructorsCoCo());
    checker.addCoCo(new ClassNoMethodsCoCo());
    checker.addCoCo(new AttributeModifierOnlyDerivedCoCo());
    checker.addCoCo(new InterfaceNoModifierCoCo());
    checker.addCoCo(new InterfaceNoAttributesCoCo());
    checker.addCoCo(new InterfaceNoMethodsCoCo());
    checker.addCoCo(new EnumNoModifierCoCo());
    checker.addCoCo(new EnumNoConstructorsCoCo());
    checker.addCoCo(new EnumNoMethodsCoCo());
    checker.addCoCo(new AssociationNoStereotypesCoCo());
    checker.addCoCo(new AssociationEndModifierRestrictionCoCo());
  }
}
