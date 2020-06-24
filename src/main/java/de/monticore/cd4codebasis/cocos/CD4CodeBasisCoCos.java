/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4codebasis.cocos;

import de.monticore.cd4codebasis._cocos.CD4CodeBasisCoCoChecker;

public class CD4CodeBasisCoCos {
  public CD4CodeBasisCoCoChecker getCheckerForAllCoCos() {
    CD4CodeBasisCoCoChecker checker = new CD4CodeBasisCoCoChecker();
    addCheckerForAllCoCos(checker);
    return checker;
  }

  protected void addCheckerForAllCoCos(CD4CodeBasisCoCoChecker checker) {
    checker.addChecker(getCheckerForEbnfCoCos());
    checker.addChecker(getCheckerForMcgCoCos());
    checker.addChecker(getCheckerForMcg2EbnfCoCos());
  }

  public CD4CodeBasisCoCoChecker getCheckerForEbnfCoCos() {
    CD4CodeBasisCoCoChecker checker = new CD4CodeBasisCoCoChecker();
    addEbnfCoCos(checker);
    return checker;
  }

  public CD4CodeBasisCoCoChecker getCheckerForMcgCoCos() {
    CD4CodeBasisCoCoChecker checker = new CD4CodeBasisCoCoChecker();
    addMcgCoCos(checker);
    return checker;
  }

  public CD4CodeBasisCoCoChecker getCheckerForMcg2EbnfCoCos() {
    CD4CodeBasisCoCoChecker checker = new CD4CodeBasisCoCoChecker();
    addMcg2EbnfCoCos(checker);
    return checker;
  }

  protected void addEbnfCoCos(CD4CodeBasisCoCoChecker checker) {
  }

  protected void addMcgCoCos(CD4CodeBasisCoCoChecker checker) {

  }

  protected void addMcg2EbnfCoCos(CD4CodeBasisCoCoChecker checker) {

  }
}
