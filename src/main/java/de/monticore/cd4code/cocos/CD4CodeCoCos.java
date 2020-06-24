/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4code.cocos;

import de.monticore.cd4code._cocos.CD4CodeCoCoChecker;

public class CD4CodeCoCos {
  public CD4CodeCoCoChecker getCheckerForAllCoCos() {
    CD4CodeCoCoChecker checker = new CD4CodeCoCoChecker();
    addCheckerForAllCoCos(checker);
    return checker;
  }

  public CD4CodeCoCoChecker getCheckerForEbnfCoCos() {
    CD4CodeCoCoChecker checker = new CD4CodeCoCoChecker();
    addEbnfCoCos(checker);
    return checker;
  }

  public CD4CodeCoCoChecker getCheckerForMcgCoCos() {
    CD4CodeCoCoChecker checker = new CD4CodeCoCoChecker();
    addMcgCoCos(checker);
    return checker;
  }

  public CD4CodeCoCoChecker getCheckerForMcg2EbnfCoCos() {
    CD4CodeCoCoChecker checker = new CD4CodeCoCoChecker();
    addMcg2EbnfCoCos(checker);
    return checker;
  }

  protected void addCheckerForAllCoCos(CD4CodeCoCoChecker checker) {
    checker.addChecker(getCheckerForEbnfCoCos());
    checker.addChecker(getCheckerForMcgCoCos());
    checker.addChecker(getCheckerForMcg2EbnfCoCos());
  }

  protected void addEbnfCoCos(CD4CodeCoCoChecker checker) {
  }

  protected void addMcgCoCos(CD4CodeCoCoChecker checker) {
  }

  protected void addMcg2EbnfCoCos(CD4CodeCoCoChecker checker) {
  }
}
