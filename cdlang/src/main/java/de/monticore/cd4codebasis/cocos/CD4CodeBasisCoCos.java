/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4codebasis.cocos;

import de.monticore.cd.cocos.CoCoParent;
import de.monticore.cd4codebasis._cocos.CD4CodeBasisCoCoChecker;
import de.monticore.cd4codebasis.cocos.ebnf.CD4CodeEnumConstantParameterMatchConstructorArguments;
import de.monticore.cd4codebasis.cocos.ebnf.CDMethodSignatureParameterNamesUnique;
import de.monticore.types.check.AbstractDerive;

public class CD4CodeBasisCoCos extends CoCoParent<CD4CodeBasisCoCoChecker> {

  protected final AbstractDerive calculator;

  public CD4CodeBasisCoCos(AbstractDerive calculator) {
    this.calculator = calculator;
  }

  @Override
  public CD4CodeBasisCoCoChecker createNewChecker() {
    return new CD4CodeBasisCoCoChecker();
  }

  @Override
  protected void addCheckerForAllCoCos(CD4CodeBasisCoCoChecker checker) {
    addCheckerForEbnfCoCos(checker);
    addCheckerForMcgCoCos(checker);
    addCheckerForMcg2EbnfCoCos(checker);
  }

  @Override
  protected void addEbnfCoCos(CD4CodeBasisCoCoChecker checker) {
    super.addEbnfCoCos(checker);

    // CDMethodSignature
    checker.addCoCo(new CDMethodSignatureParameterNamesUnique());

    // CD4CodeEnumConstant
    checker.addCoCo(new CD4CodeEnumConstantParameterMatchConstructorArguments(calculator));
  }
}
