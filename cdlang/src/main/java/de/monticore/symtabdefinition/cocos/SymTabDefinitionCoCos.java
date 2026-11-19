/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symtabdefinition.cocos;

import de.monticore.cd4code._cocos.CD4CodeCoCoChecker;
import de.monticore.cd4code.cocos.CD4CodeCoCos;
import de.monticore.cd4codebasis._cocos.CD4CodeBasisCoCoChecker;
import de.monticore.cd4codebasis.cocos.CD4CodeBasisCoCos;
import de.monticore.symtabdefinition._cocos.SymTabDefinitionCoCoChecker;

public class SymTabDefinitionCoCos {
  
  public SymTabDefinitionCoCoChecker getCheckerForAllCoCos() {
    SymTabDefinitionCoCoChecker checker = new SymTabDefinitionCoCoChecker();
    // add CD4C CoCos
    CD4CodeCoCoChecker cd4cEbnfChecker = new CD4CodeCoCoChecker();
    new CD4CodeCoCos().addCheckerForEbnfCoCos(cd4cEbnfChecker);
    checker.addChecker(cd4cEbnfChecker);
    // add CD4CodeBasis CoCos for the inherited sealed, non-sealed, and permits
    checker.addChecker(new CD4CodeBasisCoCos().addCheckerForSealedCoCos(
        new CD4CodeBasisCoCoChecker()));
    
    checker.addCoCo(new NoAssociationsSupported());
    checker.addCoCo(new NoInitialValueSupported());
    checker.addCoCo(new STDFunctionSignatureParameterNamesUnique());
    checker.addCoCo(new STDVariableUnique());
    
    return checker;
  }
  
}
