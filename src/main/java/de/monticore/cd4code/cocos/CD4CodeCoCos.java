/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4code.cocos;

import de.monticore.cd.cocos.CoCoParent;
import de.monticore.cd4code._cocos.CD4CodeCoCoChecker;
import de.monticore.cdbasis.cocos.ebnf.CDAttributeOverridden;

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
    return checker;
  }
}
