/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package cd4analysis.symboltable;

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

  // Collection types
  public final static String Collection = "java.util.Collection";

  public final static String List = "java.util.List";

  public final static String Map = "java.util.Map";

  public static Collection<String> wrapperTypes = Arrays.asList(Integer,
      Double, Short, Byte, Float, Long, Character, Boolean);

  public static Collection<String> primitiveTypes = Arrays.asList(IntPimitive,
      DoublePimitive, ShortPimitive, BytePimitive, Float, LongPimitive,
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
            .contains("java.lang." + typeName));
  }

  /**
   * Checks is the given type a primitive type
   * 
   * @return
   */
  public static boolean isPrimitiveType(String typeName) {
    return primitiveTypes.contains(typeName);
  }

  public static boolean isString(CDTypeSymbol typeSymbol) {
    
    System.err.println("TEST for isString: #######################################################");
    System.err.println(typeSymbol.getFullName());
    boolean returnValue = String.equals(typeSymbol.getFullName())|| (typeSymbol.getFullName().lastIndexOf('.') == -1 && String.equals("java.lang." + typeSymbol.getFullName()));
    System.err.println("RETURN: "+returnValue);
    return returnValue;
  }

  public static boolean isCollectionType(CDTypeSymbol typeSymbol) {
    return collectionTypes.contains(typeSymbol.getFullName());
  }

}
