/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.symboltable;

import java.util.Arrays;
import java.util.Collection;

/**
 * TODO: Change me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 */
public class CDTypes {
  
  // Wrapper types
  public final static String Integer = "java.lang.Integer";
  
  public final static String Double = "java.lang.Double";
  
  public final static String Short = "java.lang.Short";
  
  public final static String Byte = "java.lang.Byte";
  
  public final static String Float = "java.lang.Float";
  
  public final static String Long = "java.lang.Long";
  
  public final static String Character = "java.lang.Character";
  
  public final static String Boolean = "java.lang.Boolean";
  
  // Primitive types
  public final static String IntPimitive = "int";
  
  public final static String DoublePimitive = "double";
  
  public final static String ShortPimitive = "short";
  
  public final static String BytePimitive = "byte";
  
  public final static String FloatPimitive = "float";
  
  public final static String LongPimitive = "long";
  
  public final static String CharPimitive = "char";
  
  public final static String BooleanPimitive = "boolean";
  
  // Well known types
  public final static String String = "java.lang.String";
  
  public final static String JAVA_LANG_PACKAGE = "java.lang.";
  
  // Collection types
  public final static String Collection = "java.util.Collection";
  
  public final static String List = "java.util.List";
  
  public final static String Map = "java.util.Map";
  
  public static Collection<String> wrapperTypes = Arrays.asList(Integer,
      Double, Short, Byte, Float, Long, Character, Boolean);
  
  public static Collection<String> primitiveTypes = Arrays.asList(IntPimitive,
      DoublePimitive, ShortPimitive, BytePimitive, FloatPimitive, LongPimitive,
      CharPimitive, BooleanPimitive);
  
  public static Collection<String> dataTypes = Arrays.asList(String);
  
  public static Collection<String> collectionTypes = Arrays.asList(Collection,
      List, Map);
  
  /**
   * Checks is the given type an implicit type
   * 
   * @return
   */
  public static boolean isWrapperType(String typeName) {
    return wrapperTypes.contains(typeName)
        || (typeName.lastIndexOf('.') == -1 && wrapperTypes
            .contains(JAVA_LANG_PACKAGE + typeName));
  }
  
  /**
   * Checks is the given type a primitive type
   * 
   * @return
   */
  public static boolean isPrimitiveType(String typeName) {
    return primitiveTypes.contains(typeName);
  }
  
  /**
   * Converts the given primitive type to the wrapper type
   * 
   * @param primitiveType
   * @return wrapper type if the given type is a primitive type else the given
   * type
   */
  public static String primitiveToWrapper(String primitiveType) {
    String wrapperName = primitiveType;
    switch (primitiveType) {
      case IntPimitive:
        wrapperName = Integer.replaceFirst(JAVA_LANG_PACKAGE, "");
      case DoublePimitive:
        wrapperName = Double.replaceFirst(JAVA_LANG_PACKAGE, "");
      case ShortPimitive:
        wrapperName = Short.replaceFirst(JAVA_LANG_PACKAGE, "");
      case BytePimitive:
        wrapperName = Byte.replaceFirst(JAVA_LANG_PACKAGE, "");
      case FloatPimitive:
        wrapperName = Float.replaceFirst(JAVA_LANG_PACKAGE, "");
      case LongPimitive:
        wrapperName = Long.replaceFirst(JAVA_LANG_PACKAGE, "");
      case CharPimitive:
        wrapperName = Character.replaceFirst(JAVA_LANG_PACKAGE, "");
      case BooleanPimitive:
        wrapperName = Boolean.replaceFirst(JAVA_LANG_PACKAGE, "");
      default:
        break;
    }
    return wrapperName;
  }
  
  /**
   * Derives the Wrapper to a given primitive type. If the
   * parameter isn't a primitive type, the result is the given type
   * 
   * @param primitiveType
   * @return wrapper type if the given type is a primitive type else the given
   * type
   */
  public static String primitiveToQualifiedWrapper(String primitiveType) {
    return primitiveToQualifiedWrapperOrDefault(primitiveType, primitiveType);
  }
  
  /**
   * Derives the Wrapper to a given primitive type. If the
   * parameter isn't a primitive type, the result is the given default type
   * 
   * @param primitiveType - the given type
   * @param defaultType - default type
   * @return the wrapped version
   */
  public static String primitiveToQualifiedWrapperOrDefault(String primitiveType, String defaultType) {
    switch (primitiveType) {
      case IntPimitive:
        return Integer;
      case DoublePimitive:
        return Double;
      case ShortPimitive:
        return Short;
      case BytePimitive:
        return Byte;
      case FloatPimitive:
        return Float;
      case LongPimitive:
        return Long;
      case CharPimitive:
        return Character;
      case BooleanPimitive:
        return Boolean;
      default:
        return defaultType;
    }
  }
  
  /**
   * Converts the given wrapper type to the primitive type
   * 
   * @param wrapperType
   * @return primitive type if the given type is a wrapper type else the given
   * type
   */
  public static String wrapperToPrimitive(String wrapperType) {
    if (wrapperType.indexOf('.') == -1) {
      wrapperType = JAVA_LANG_PACKAGE + wrapperType;
    }
    switch (wrapperType) {
      case Integer:
        return IntPimitive;
      case Double:
        return DoublePimitive;
      case Short:
        return ShortPimitive;
      case Byte:
        return BytePimitive;
      case Float:
        return FloatPimitive;
      case Long:
        return LongPimitive;
      case Character:
        return CharPimitive;
      case Boolean:
        return BooleanPimitive;
      default:
        return wrapperType;
    }
  }
  
  public static boolean isString(CDTypeSymbol typeSymbol) {
    boolean returnValue = String.equals(typeSymbol.getFullName())
        || (typeSymbol.getFullName().lastIndexOf('.') == -1 && String.equals("java.lang."
            + typeSymbol.getFullName()));
    return returnValue;
  }
  
  public static boolean isCollectionType(CDTypeSymbol typeSymbol) {
    return collectionTypes.contains(typeSymbol.getFullName());
  }
  
  public static boolean isInteger(String typeName) {
    return IntPimitive.equals(typeName)
        || (typeName.lastIndexOf('.') == -1 && Integer.equals(JAVA_LANG_PACKAGE + typeName));
  }
  
  public static boolean isDouble(String typeName) {
    return DoublePimitive.equals(typeName)
        || (typeName.lastIndexOf('.') == -1 && Double.equals(JAVA_LANG_PACKAGE + typeName));
  }
  
  public static boolean isShort(String typeName) {
    return ShortPimitive.equals(typeName)
        || (typeName.lastIndexOf('.') == -1 && Short.equals(JAVA_LANG_PACKAGE + typeName));
  }
  
  public static boolean isByte(String typeName) {
    return BytePimitive.equals(typeName)
        || (typeName.lastIndexOf('.') == -1 && Byte.equals(JAVA_LANG_PACKAGE + typeName));
  }
  
  public static boolean isFloat(String typeName) {
    return FloatPimitive.equals(typeName)
        || (typeName.lastIndexOf('.') == -1 && Float.equals(JAVA_LANG_PACKAGE + typeName));
  }
  
  public static boolean isLong(String typeName) {
    return LongPimitive.equals(typeName)
        || (typeName.lastIndexOf('.') == -1 && Long.equals(JAVA_LANG_PACKAGE + typeName));
  }
  
  public static boolean isCharacter(String typeName) {
    return CharPimitive.equals(typeName)
        || (typeName.lastIndexOf('.') == -1 && Character.equals(JAVA_LANG_PACKAGE + typeName));
  }
  
  public static boolean isBoolean(String typeName) {
    return BooleanPimitive.equals(typeName)
        || (typeName.lastIndexOf('.') == -1 && Boolean.equals(JAVA_LANG_PACKAGE + typeName));
  }
  
}
