/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.symboltable;

@Deprecated //new class at cd4analysis/src/main/java/de/monticore/cd
public class Stereotype {

  private final String name;
  private final String value;

  public Stereotype(String name, String value) {
    this.name = name;
    this.value = value;
  }

  public String getName() {
    return name;
  }

  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    if (value==null) {
      return name;
    } else {
      return name + " \"" + value + "\"";
    }
  }

  public boolean compare(String name, String value) {
    if (this.getName().equals(name)) {
      if ((this.getValue() != null && this.getValue().equals(value)) || (( this.getValue()==null) && (value==null))) {
        return true;
      }
    }
    return false;
  }

}

