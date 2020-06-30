/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4codebasis.cocos;

import de.monticore.cd.cocos.CoCoParent;
import de.monticore.cd4codebasis._cocos.CD4CodeBasisCoCoChecker;

public class CD4CodeBasisCoCos extends CoCoParent<CD4CodeBasisCoCoChecker> {
  @Override
  public CD4CodeBasisCoCoChecker createNewChecker() {
    return new CD4CodeBasisCoCoChecker();
  }

  @Override
  protected void addCheckerForAllCoCos(CD4CodeBasisCoCoChecker checker) {
    checker.addChecker(getCheckerForEbnfCoCos(checker));
    checker.addChecker(getCheckerForMcgCoCos(checker));
    checker.addChecker(getCheckerForMcg2EbnfCoCos(checker));
  }
}
