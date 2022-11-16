/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.util;

import static org.junit.Assert.*;

import de.monticore.cdmerge.BaseTest;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.Test;

/** TODO: Write me! */
public class JPrimitiveTypeTest extends BaseTest {

  @Test
  public void testGetType() {
    assertTrue(JPrimitiveType.getType("byte") == JPrimitiveType.BYTE);
    assertTrue(JPrimitiveType.getType("short") == JPrimitiveType.SHORT);
    assertTrue(JPrimitiveType.getType("int") == JPrimitiveType.INT);
    assertTrue(JPrimitiveType.getType("long") == JPrimitiveType.LONG);
    assertTrue(JPrimitiveType.getType("float") == JPrimitiveType.FLOAT);
    assertTrue(JPrimitiveType.getType("double") == JPrimitiveType.DOUBLE);
    assertTrue(JPrimitiveType.getType("boolean") == JPrimitiveType.BOOLEAN);
    assertTrue(JPrimitiveType.getType("char") == JPrimitiveType.CHAR);
    assertTrue(JPrimitiveType.getType("string") == JPrimitiveType.STRING);

    try {
      JPrimitiveType.getType("Date");
      fail("NoSuchElementException expected!");
    } catch (NoSuchElementException e) {
      assertTrue(e.getMessage().equals("JPrimitiveType does not contain a type with name Date"));
    }
  }

  @Test
  public void testIsPrimitive() {
    assertTrue(JPrimitiveType.isPrimitiveType("byte"));
    assertTrue(JPrimitiveType.isPrimitiveType("short"));
    assertTrue(JPrimitiveType.isPrimitiveType("int"));
    assertTrue(JPrimitiveType.isPrimitiveType("long"));
    assertTrue(JPrimitiveType.isPrimitiveType("float"));
    assertTrue(JPrimitiveType.isPrimitiveType("double"));
    assertTrue(JPrimitiveType.isPrimitiveType("boolean"));
    assertTrue(JPrimitiveType.isPrimitiveType("char"));
    assertTrue(JPrimitiveType.isPrimitiveType("string"));
    assertFalse(JPrimitiveType.isPrimitiveType("Date"));
    assertFalse(JPrimitiveType.isPrimitiveType("AE"));
  }

  @Test
  public void testGetCommonSuperType() {
    // Two booleans
    Optional<JPrimitiveType> type =
        JPrimitiveType.getCommonSuperType(JPrimitiveType.BOOLEAN, JPrimitiveType.BOOLEAN);
    assertTrue(type.isPresent());
    assertTrue(type.get() == JPrimitiveType.BOOLEAN);

    // One boolean
    type = JPrimitiveType.getCommonSuperType(JPrimitiveType.BOOLEAN, JPrimitiveType.CHAR);
    assertFalse(type.isPresent());

    // Chars and Strings
    type = JPrimitiveType.getCommonSuperType(JPrimitiveType.STRING, JPrimitiveType.CHAR);
    assertTrue(type.isPresent());
    assertTrue(type.get() == JPrimitiveType.STRING);

    // Float and double
    type = JPrimitiveType.getCommonSuperType(JPrimitiveType.FLOAT, JPrimitiveType.DOUBLE);
    assertTrue(type.isPresent());
    assertTrue(type.get() == JPrimitiveType.DOUBLE);

    // String and double
    type = JPrimitiveType.getCommonSuperType(JPrimitiveType.STRING, JPrimitiveType.DOUBLE);
    assertTrue(type.isPresent());
    assertTrue(type.get() == JPrimitiveType.STRING);
  }
}
