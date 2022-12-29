/* (c) https://github.com/MontiCore/monticore */
package de.monticore.odvalidity;

import de.monticore.cddiff.alloycddiff.CDSemantics;

public class Semantic {

  public static boolean isOpenWorld(CDSemantics semantic) {
    return semantic == CDSemantics.SIMPLE_OPEN_WORLD
        || semantic == CDSemantics.MULTI_INSTANCE_OPEN_WORLD;
  }

  public static boolean isClosedWorld(CDSemantics semantic) {
    return semantic == CDSemantics.SIMPLE_CLOSED_WORLD
        || semantic == CDSemantics.MULTI_INSTANCE_CLOSED_WORLD;
  }

  public static boolean isMultiInstance(CDSemantics semantic) {
    return semantic == CDSemantics.MULTI_INSTANCE_OPEN_WORLD
        || semantic == CDSemantics.MULTI_INSTANCE_CLOSED_WORLD;
  }
}
