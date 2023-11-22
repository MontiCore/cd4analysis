/* (c) https://github.com/MontiCore/monticore */
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

  public Checker addCheckerForEbnfCoCos() {
    return addCheckerForEbnfCoCos(createNewChecker());
  }

  public Checker addCheckerForEbnfCoCos(Checker checker) {
    addEbnfCoCos(checker);
    return checker;
  }

  public Checker addCheckerForMcgCoCos() {
    return addCheckerForMcgCoCos(createNewChecker());
  }

  public Checker addCheckerForMcgCoCos(Checker checker) {
    addMcgCoCos(checker);
    return checker;
  }

  public Checker addCheckerForMcg2EbnfCoCos() {
    return addCheckerForMcg2EbnfCoCos(createNewChecker());
  }

  public Checker addCheckerForMcg2EbnfCoCos(Checker checker) {
    addMcg2EbnfCoCos(checker);
    return checker;
  }

  protected abstract void addCheckerForAllCoCos(Checker checker);

  protected void addEbnfCoCos(Checker checker) {}

  protected void addMcgCoCos(Checker checker) {}

  protected void addMcg2EbnfCoCos(Checker checker) {}
}
