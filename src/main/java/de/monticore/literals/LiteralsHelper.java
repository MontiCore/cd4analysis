package de.monticore.literals;

import java.io.CharConversionException;

/**
 * This class provides methods for converting literals. The LiteralsHelper is a singleton.
 * 
 * @author Martin Schindler
 */
public class LiteralsHelper {
  
  private static LiteralsHelper instance;
  
  /**
   * We have a singleton.
   */
  private LiteralsHelper() {
  }
  
  /**
   * Returns the singleton instance.
   * 
   * @return The instance.
   */
  public static LiteralsHelper getInstance() {
    if (instance == null) {
      instance = new LiteralsHelper();
    }
    return instance;
  }
  
  /**
   * Decodes a char literal into a char
   * 
   * @param s char literal as string including "'"
   * @return decoded char
   * @throws CharConversionException
   */
  public char decodeChar(String s) throws CharConversionException {
    if (s.length() == 1) { // single char
      return s.charAt(0);
    }
    else if (s.length() == 2 && s.charAt(0) == '\\') { // escape sequence
      switch (s.charAt(1)) {
        case 'b':
          return '\b';
        case 't':
          return '\t';
        case 'n':
          return '\n';
        case 'f':
          return '\f';
        case 'r':
          return '\r';
        case '"':
          return '\"';
        case '\'':
          return '\'';
        case '\\':
          return '\\';
        default:
          break;
      }
    }
    else if (s.charAt(0) == '\\' && s.charAt(1) == 'u') { // unicode
      return (char) Integer.parseInt(s.substring(2), 16);
    }
    throw new CharConversionException("Unable to convert String " + s + " to char.");
  }
  
  /**
   * Decodes a string literal into a string
   * 
   * @param s string literal excluding '"'
   * @return decoded string
   * @throws Exception
   */
  public String decodeString(String s) throws Exception {
    String ret = "";
    String in = s;
    
    try {
      while (in.length() != 0) {
        if (in.charAt(0) == '\\') {
          if (in.charAt(1) == 'u') { // unicode
            ret += decodeChar(in.substring(0, 6));
            in = in.substring(6);
          }
          else { // escape sequence
            ret += decodeChar(in.substring(0, 2));
            in = in.substring(2);
          }
        }
        else { // single char
          ret += in.charAt(0);
          in = in.substring(1);
        }
      }
    }
    catch (StringIndexOutOfBoundsException e) {
      throw new Exception("Invalid String " + s);
    }
    
    return ret.intern();
  }
  
  /**
   * Decodes an int literal into an int
   * 
   * @param s int literal as string including '"'
   * @return decoded int
   * @throws NumberFormatException
   */
  public int decodeInt(String s) throws NumberFormatException {
    int radix = 10;
    if (s.startsWith("0") && s.length() > 1) {
      if (s.startsWith("0x") || s.startsWith("0X")) {
        radix = 16;
        s = s.substring(2);
      }
      else {
        radix = 8;
      }
    }
    return Integer.parseInt(s, radix);
  }
  
  /**
   * Decodes a long literal into a long
   * 
   * @param s long literal as string including '"'
   * @return decoded long
   * @throws NumberFormatException
   */
  public long decodeLong(String s) throws NumberFormatException {
    int radix = 10;
    if (s.startsWith("0") && s.length() > 2) {
      if (s.startsWith("0x") || s.startsWith("0X")) {
        radix = 16;
        s = s.substring(2);
      }
      else {
        radix = 8;
      }
    }
    s = s.substring(0, s.length() - 1);
    return Long.parseLong(s, radix);
  }
  
  /**
   * Decodes a float literal into a float
   * 
   * @param s float literal as string including '"'
   * @return decoded float
   * @throws NumberFormatException
   */
  public float decodeFloat(String s) throws NumberFormatException {
    // workaround as parseFloat() does not parse 0xp1F correctly
    if (s.toLowerCase().startsWith("0xp")) {
      s = "0x0p" + s.substring(3); // 0xp1F == 0x0p1F == 0.0
    }
    return Float.parseFloat(s);
  }
  
  /**
   * Decodes a double literal into a double
   * 
   * @param s double literal as string including '"'
   * @return decoded double
   * @throws NumberFormatException
   */
  public double decodeDouble(String s) throws NumberFormatException {
    // workaround as parseDouble() does not parse 0xp1 correctly
    if (s.toLowerCase().startsWith("0xp")) {
      s = "0x0p" + s.substring(3); // 0xp1 == 0x0p1 == 0.0
    }
    return Double.parseDouble(s);
  }
  
}
