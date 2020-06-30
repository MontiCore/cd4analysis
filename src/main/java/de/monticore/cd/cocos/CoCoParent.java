/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd.cocos;

public abstract class CoCoParent<Checker> {
  public abstract Checker createNewChecker();

  public Checker getCheckerForAllCoCos() {
    return getCheckerForAllCoCos(createNewChecker());
  }

  public Checker getCheckerForAllCoCos(Checker checker) {
    addCheckerForAllCoCos(checker);
    return checker;
  }

  public Checker getCheckerForEbnfCoCos() {
    return getCheckerForEbnfCoCos(createNewChecker());
  }

  public Checker getCheckerForEbnfCoCos(Checker checker) {
    addEbnfCoCos(checker);
    return checker;
  }

  public Checker getCheckerForMcgCoCos() {
    return getCheckerForMcgCoCos(createNewChecker());
  }

  public Checker getCheckerForMcgCoCos(Checker checker) {
    addMcgCoCos(checker);
    return checker;
  }

  public Checker getCheckerForMcg2EbnfCoCos() {
    return getCheckerForMcg2EbnfCoCos(createNewChecker());
  }

  public Checker getCheckerForMcg2EbnfCoCos(Checker checker) {
    addMcg2EbnfCoCos(checker);
    return checker;
  }

  protected abstract void addCheckerForAllCoCos(Checker checker);

  protected void addEbnfCoCos(Checker checker) {
  }

  protected void addMcgCoCos(Checker checker) {
  }

  protected void addMcg2EbnfCoCos(Checker checker) {
  }
}
