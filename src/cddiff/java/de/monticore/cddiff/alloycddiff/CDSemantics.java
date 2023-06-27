/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.alloycddiff;

/**
 * We distinguish between open-world and closed-world semantics, as well as object structures with
 * simple objects and object structures with super-type-aware (STA) objects. Super-type-aware
 * objects list all types they instantiate via the `instanceof`-stereotype.
 */
public enum CDSemantics {
  SIMPLE_CLOSED_WORLD,
  STA_CLOSED_WORLD,

  SIMPLE_OPEN_WORLD,
  STA_OPEN_WORLD
}
