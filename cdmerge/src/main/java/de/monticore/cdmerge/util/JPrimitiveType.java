/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.util;

import de.monticore.types.mcbasictypes._ast.ASTMCType;
import java.util.NoSuchElementException;
import java.util.Optional;

/** Allows Conversion of PrimitiveTypes */
public enum JPrimitiveType {
  BYTE("byte"),
  SHORT("short"),
  INT("int"),
  LONG("long"),
  FLOAT("float"),
  DOUBLE("double"),
  BOOLEAN("boolean"),
  CHAR("char"),
  STRING("string");

  /** @return name */
  public String getName() {
    return this.name;
  }

  private String name;

  private JPrimitiveType(String name) {
    this.name = name;
  }

  public static boolean isPrimitiveType(String name) {
    for (JPrimitiveType type : values()) {
      if (type.name.equalsIgnoreCase(name)) {
        return true;
      }
    }
    return false;
  }

  public static JPrimitiveType getType(String name) {
    for (JPrimitiveType type : values()) {
      if (type.name.equalsIgnoreCase(name)) {
        return type;
      }
    }
    throw new NoSuchElementException("JPrimitiveType does not contain a type with name " + name);
  }

  /**
   * JAVA Widening Type Conversion byte to short, int, long, float, or double short to int, long,
   * float, or double char to int, long, float, or double int to long, float, or double long to
   * float or double float to double
   */
  public static Optional<JPrimitiveType> getCommonSuperType(
      JPrimitiveType type1, JPrimitiveType type2) {
    if (type1.equals(type2)) {
      return Optional.of(type1);
    }
    if (type1.equals(BOOLEAN) || type2.equals(BOOLEAN)) {
      // the other is not of type boolean and we don't convert booleans
      return Optional.empty();
    }
    if (((type1.equals(CHAR) || type2.equals(CHAR))
        && (type1.equals(STRING) || type2.equals(STRING)))) {
      return Optional.of(STRING);
    }
    // The rest are numbers
    if (type1.compareTo(type2) > 0) {
      return Optional.of(type1);
    } else {
      return Optional.of(type2);
    }
  }

  public static Optional<ASTMCType> getCommonSuperType(ASTMCType type1, ASTMCType type2) {
    if (isPrimitiveType(CDMergeUtils.getTypeName(type1))
        && isPrimitiveType(CDMergeUtils.getTypeName(type1))) {
      Optional<JPrimitiveType> commonSuperType =
          getCommonSuperType(
              getType(CDMergeUtils.getTypeName(type1)), getType(CDMergeUtils.getTypeName(type2)));
      if (commonSuperType.isPresent()) {
        if (commonSuperType.get().getName().equalsIgnoreCase(CDMergeUtils.getTypeName(type1))) {
          return Optional.of(type1);
        } else if (commonSuperType
            .get()
            .getName()
            .equalsIgnoreCase(CDMergeUtils.getTypeName(type2))) {
          return Optional.of(type2);
        }
      }
    }
    return Optional.empty();
  }
}
