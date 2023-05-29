package de.monticore.cd2smt.cd2smtGenerator;

import de.monticore.cd2smt.cd2smtGenerator.assocStrategies.AssociationStrategy;
import de.monticore.cd2smt.cd2smtGenerator.classStrategies.ClassStrategy;
import de.monticore.cd2smt.cd2smtGenerator.inhrStrategies.InheritanceData;
import de.monticore.cd2smt.cd2smtGenerator.inhrStrategies.InheritanceStrategy;
import de.se_rwth.commons.logging.Log;

public class CD2SMTMill {
  private static ClassStrategy.Strategy cs;
  private static InheritanceData.Strategy is;
  private static AssociationStrategy.Strategy as;

  public static void init(
      ClassStrategy.Strategy cs, InheritanceData.Strategy is, AssociationStrategy.Strategy as) {
    CD2SMTMill.cs = cs;
    CD2SMTMill.is = is;
    CD2SMTMill.as = as;
  }

  public static void initDefault() {
    cs = ClassStrategy.Strategy.DS;
    is = InheritanceStrategy.Strategy.ME;
    as = AssociationStrategy.Strategy.DEFAULT;
  }

  public static CD2SMTGenerator cd2SMTGenerator() {
    if (cs == null || is == null || as == null) {
      Log.error("ERROR: CD2SMTMill was not initialized with Strategies");
    }
    return new CD2SMTGenerator(cs, is, as);
  }
}
