/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4code.cocos;

import de.monticore.cd.cocos.CoCoParent;
import de.monticore.cd4code._cocos.CD4CodeCoCoChecker;
import de.monticore.cd4codebasis.cocos.ebnf.CDSealedOnlyOnClassesAndInterfaces;
import de.monticore.cdassociation._cocos.CDAssociationASTCDAssociationCoCo;
import de.monticore.cdbasis.cocos.ebnf.CDAttributeOverridden;
import de.monticore.types.mcbasictypes.cocos.QualifiedTypeHasNoTypeParameters;
import de.monticore.types.typeparameters.cocos.TypeParameterNoCyclicInheritance;
import de.monticore.types.typeparameters.cocos.TypeParametersHaveUniqueNames;

public class CD4CodeCoCos extends CoCoParent<CD4CodeCoCoChecker> {
  
  @Override
  public CD4CodeCoCoChecker createNewChecker() {
    return new CD4CodeCoCoChecker();
  }
  
  @Override
  protected void addCheckerForAllCoCos(CD4CodeCoCoChecker checker) {
    addCheckerForEbnfCoCos(checker);
    addCheckerForMcgCoCos(checker);
    addCheckerForMcg2EbnfCoCos(checker);
  }
  
  @Override
  public CD4CodeCoCoChecker addCheckerForEbnfCoCos(CD4CodeCoCoChecker checker) {
    super.addCheckerForEbnfCoCos(checker);
    
    // CDAttribute
    checker.addCoCo(new CDAttributeOverridden());
    // TypeParameters
    checker.addCoCo(new TypeParametersHaveUniqueNames());
    // TypeParameter
    checker.addCoCo(new TypeParameterNoCyclicInheritance());
    // MCBasicTypes
    checker.addCoCo(new QualifiedTypeHasNoTypeParameters());
    // CDAssociation: no sealed or non-sealed (once, visits associations and both ends)
    checker.addCoCo((CDAssociationASTCDAssociationCoCo) new CDSealedOnlyOnClassesAndInterfaces());
    return checker;
  }
  
}
