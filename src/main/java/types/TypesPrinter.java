package types;

/**
 * @deprecated STATE DEL PN will be implemented with new symbol table concept
 */
import de.monticore.types._ast.ASTArrayType;
import de.monticore.types._ast.ASTPrimitiveType;
import de.monticore.types._ast.ASTReferenceType;
import de.monticore.types._ast.ASTReferenceTypeList;
import de.monticore.types._ast.ASTReturnType;
import de.monticore.types._ast.ASTSimpleReferenceType;
import de.monticore.types._ast.ASTType;
import de.monticore.types._ast.ASTTypeArgument;
import de.monticore.types._ast.ASTTypeArgumentList;
import de.monticore.types._ast.ASTTypeArguments;
import de.monticore.types._ast.ASTTypeParameters;
import de.monticore.types._ast.ASTTypeVariableDeclaration;
import de.monticore.types._ast.ASTTypeVariableDeclarationList;
import de.monticore.types._ast.ASTVoidType;
import de.monticore.types._ast.ASTWildcardType;

// TODO GV: used in dexMC4 - remove this class after the first bootstrapping of MC4
/**
 * This class provides methods for printing types as Strings. The TypesPrinter
 * is a singleton.
 * 
 * @author Martin Schindler
 */
@Deprecated
public class TypesPrinter {
  
  private static TypesPrinter instance;
  
  /**
   * We have a singleton.
   */
  private TypesPrinter() {
  }
  
  /**
   * Returns the singleton instance.
   * 
   * @return The instance.
   */
  private static TypesPrinter getInstance() {
    if (instance == null) {
      instance = new TypesPrinter();
    }
    return instance;
  }
  
  /******************************************************************
   * INTERFACES
   ******************************************************************/
  
  /**
   * Converts an ASTType to a String
   * 
   * @param type ASTType to be converted
   * @return String representation of "type"
   */
  public static String printType(ASTType type) {
    return getInstance().doPrintType(type);
  }
  
  protected String doPrintType(ASTType type) {
    if (type instanceof ASTArrayType) {
      return doPrintArrayType((ASTArrayType) type);
    }
    if (type instanceof ASTPrimitiveType) {
      return doPrintPrimitiveType((ASTPrimitiveType) type);
    }
    if (type instanceof ASTReferenceType) {
      return doPrintReferenceType((ASTReferenceType) type);
    }
    return "";
  }
  
  /**
   * Converts an ASTReferenceType to a String
   * 
   * @param type ASTReferenceType to be converted
   * @return String representation of "type"
   */
  public static String printReferenceType(ASTReferenceType type) {
    return getInstance().doPrintReferenceType(type);
  }
  
  protected String doPrintReferenceType(ASTReferenceType type) {
    if (type instanceof ASTSimpleReferenceType) {
      return doPrintSimpleReferenceType((ASTSimpleReferenceType) type);
    }
    // TODO MB, GV
    /*
    if (type instanceof ASTQualifiedType) {
      return doPrintQualifiedType((ASTQualifiedType) type);
    }
    */
    return "";
  }
  
  /**
   * Converts an ASTReturnType to a String
   * 
   * @param type ASTReturnType to be converted
   * @return String representation of "type"
   */
  public static String printReturnType(ASTReturnType type) {
    return getInstance().doPrintReturnType(type);
  }
  
  protected String doPrintReturnType(ASTReturnType type) {
    if (type instanceof ASTType) {
      return doPrintType((ASTType) type);
    }
    if (type instanceof ASTVoidType) {
      return doPrintVoidType((ASTVoidType) type);
    }
    return "";
  }
  
  /**
   * Converts an ASTTypeArgument to a String
   * 
   * @param type ASTTypeArgument to be converted
   * @return String representation of "type"
   */
  public static String printTypeArgument(ASTTypeArgument type) {
    return getInstance().doPrintTypeArgument(type);
  }
  
  protected String doPrintTypeArgument(ASTTypeArgument type) {
    if (type instanceof ASTWildcardType) {
      return doPrintWildcardType((ASTWildcardType) type);
    }
    if (type instanceof ASTType) {
      return doPrintType((ASTType) type);
    }
    return "";
  }
  
  /******************************************************************
   * Rules
   ******************************************************************/
  
  /**
   * Converts ASTTypeParameters to a String
   * 
   * @param params ASTTypeParameters to be converted
   * @return String representation of "params"
   */
  public static String printTypeParameters(ASTTypeParameters params) {
    return getInstance().doPrintTypeParameters(params);
  }
  
  protected String doPrintTypeParameters(ASTTypeParameters params) {
    if (params != null && params.getTypeVariableDeclarations() != null && params.getTypeVariableDeclarations().size() != 0) {
      return "<" + doPrintTypeVariableDeclarationList(params.getTypeVariableDeclarations()) + ">";
    }
    return "";
  }
  
  /**
   * Converts an ASTTypeVariableDeclarationList to a String
   * 
   * @param decl ASTTypeVariableDeclarationList to be converted
   * @return String representation of "decl"
   */
  public static String printTypeVariableDeclarationList(ASTTypeVariableDeclarationList decl) {
    return getInstance().doPrintTypeVariableDeclarationList(decl);
  }
  
  protected String doPrintTypeVariableDeclarationList(ASTTypeVariableDeclarationList decl) {
    String ret = "";
    if (decl != null) {
      String sep = "";
      for (ASTTypeVariableDeclaration d : decl) {
        ret += sep + doPrintTypeVariableDeclaration(d);
        sep = ", ";
      }
    }
    return ret;
  }
  
  /**
   * Converts an ASTTypeVariableDeclaration to a String
   * 
   * @param decl ASTTypeVariableDeclaration to be converted
   * @return String representation of "decl"
   */
  public static String printTypeVariableDeclaration(ASTTypeVariableDeclaration decl) {
    return getInstance().doPrintTypeVariableDeclaration(decl);
  }
  
  protected String doPrintTypeVariableDeclaration(ASTTypeVariableDeclaration decl) {
    String ret = "";
    if (decl != null) {
      ret = decl.getName();
      if (decl.getUpperBounds() != null && !decl.getUpperBounds().isEmpty()) {
        String sep = " extends ";
        for (ASTType type : decl.getUpperBounds()) {
          ret += sep + doPrintType(type);
          sep = " & ";
        }
      }
    }
    return ret;
  }
  
  /**
   * Converts an ASTVoidType to a String
   * 
   * @param type ASTVoidType to be converted
   * @return String representation of "type"
   */
  public static String printVoidType(ASTVoidType type) {
    return getInstance().doPrintVoidType(type);
  }
  
  protected String doPrintVoidType(ASTVoidType type) {
    if (type != null) {
      return "void";
    }
    return "";
  }
  
  /**
   * Converts an ASTPrimitiveType to a String
   * 
   * @param type ASTPrimitiveType to be converted
   * @return String representation of "type"
   */
  public static String printPrimitiveType(ASTPrimitiveType type) {
    return getInstance().doPrintPrimitiveType(type);
  }
  
  protected String doPrintPrimitiveType(ASTPrimitiveType type) {
    if (type != null) {
      return type.toString();
    }
    return "";
  }
  
  /**
   * Converts an ASTArrayType to a String
   * 
   * @param type ASTArrayType to be converted
   * @return String representation of "type"
   */
  public static String printArrayType(ASTArrayType type) {
    return getInstance().doPrintArrayType(type);
  }
  
//TODO GV, MB
  protected String doPrintArrayType(ASTArrayType type) {
//    if (type != null) {
//      String dimension = "";
//      for (int i = 0; i < type.getDimensions(); i++) {
//        dimension += "[]";
//      }
//      return doPrintType(type.getComponentType()) + dimension;
//    }
    return "";
  }
  
  /**
   * Converts an ASTReferenceTypeList to a String
   * 
   * @param type ASTReferenceTypeList to be converted
   * @return String representation of "type"
   */
  public static String printReferenceTypeList(ASTReferenceTypeList type) {
    return getInstance().doPrintReferenceTypeList(type);
  }
  
  protected String doPrintReferenceTypeList(ASTReferenceTypeList type) {
    String ret = "";
    if (type != null) {
      String sep = "";
      for (ASTReferenceType refType : type) {
        ret += sep + doPrintReferenceType(refType);
        sep = ", ";
      }
    }
    return ret;
  }
  
  /**
   * Converts an ASTSimpleReferenceType to a String
   * 
   * @param type ASTSimpleReferenceType to be converted
   * @return String representation of "type"
   */
  public static String printSimpleReferenceType(ASTSimpleReferenceType type) {
    return getInstance().doPrintSimpleReferenceType(type);
  }
  
  protected String doPrintSimpleReferenceType(ASTSimpleReferenceType type) {
    // TODO GV, MB
//    if (type != null) {
//      return NameHelper.dotSeparatedStringFromList(type.getName()) + doPrintTypeArguments(type.getTypeArguments());
//    }
    return "";
  }
  
  /**
   * Converts an ASTQualifiedType to a String
   * 
   * @param type ASTQualifiedType to be converted
   * @return String representation of "type"
   */
  // TODO MB, GV
  /*
  public static String printQualifiedType(ASTQualifiedType type) {
    return getInstance().doPrintQualifiedType(type);
  }
  
  protected String doPrintQualifiedType(ASTQualifiedType type) {
    String ret = "";
    if (type != null) {
      if (type.getQualification() != null) {
        ret += doPrintType(type.getQualification()) + ".";
      }
      ret += type.getName() + doPrintTypeArguments(type.getTypeArguments());
    }
    return ret;
  }
  */
  
  /**
   * Converts ASTTypeArguments to a String
   * 
   * @param args ASTTypeArguments to be converted
   * @return String representation of "args"
   */
  public static String printTypeArguments(ASTTypeArguments args) {
    return getInstance().doPrintTypeArguments(args);
  }
  
  protected String doPrintTypeArguments(ASTTypeArguments args) {
    if (args != null && args.getTypeArguments() != null && args.getTypeArguments().size() != 0) {
      return "<" + doPrintTypeArgumentList(args.getTypeArguments()) + ">";
    }
    return "";
  }
  
  /**
   * Converts an ASTTypeArgumentList to a String
   * 
   * @param argList ASTTypeArgumentList to be converted
   * @return String representation of "argList"
   */
  public static String printTypeArgumentList(ASTTypeArgumentList argList) {
    return getInstance().doPrintTypeArgumentList(argList);
  }
  
  protected String doPrintTypeArgumentList(ASTTypeArgumentList argList) {
    String ret = "";
    if (argList != null) {
      String sep = "";
      for (ASTTypeArgument arg : argList) {
        ret += sep + doPrintTypeArgument(arg);
        sep = ", ";
      }
    }
    return ret;
  }
  
  /**
   * Converts an ASTWildcardType to a String
   * 
   * @param type ASTWildcardType to be converted
   * @return String representation of "type"
   */
  public static String printWildcardType(ASTWildcardType type) {
    return getInstance().doPrintWildcardType(type);
  }
  
  // TODO GV, MB
  protected String doPrintWildcardType(ASTWildcardType type) {
    String ret = "";
//    if (type != null) {
//      ret = "?";
//      if (type.getUpperBound() != null) {
//        ret += " extends " + doPrintType(type.getUpperBound());
//      }
//      else if (type.getLowerBound() != null) {
//        ret += " super " + doPrintType(type.getLowerBound());
//      }
//    }
    return ret;
  }
  
}
