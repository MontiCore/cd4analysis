/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a;

import java.util.Arrays;
import java.util.List;

/**
 * Defines which types are built in in CD4A.<br/>
 * <br/>
 * TODO we should support the built in types as real types/symbols.
 * 
 * @author Robert Heim
 */
public class BuiltInTypes {
  
  private static final List<String> builtInTypes = Arrays.asList(new String[] {
      "String",
      "char", "Character",
      "int", "Integer",
      "double", "Double",
      "float", "Float",
      "long", "Long",
      "boolean", "Boolean",
      "Date", "List", "Optional", "Set",
      "Map"
  });
  
  public static boolean isBuiltInType(String typeName) {
    return builtInTypes.contains(typeName);
  }
}
