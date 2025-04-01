/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.util;

import de.monticore.cdmerge.BaseTest;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** TODO: Write me! */
public class JPrimitiveTypeTest extends BaseTest {

  @Test
  public void testGetType() {
    assertSame(JPrimitiveType.BYTE, JPrimitiveType.getType("byte"));
    assertSame(JPrimitiveType.SHORT, JPrimitiveType.getType("short"));
    assertSame(JPrimitiveType.INT, JPrimitiveType.getType("int"));
    assertSame(JPrimitiveType.LONG, JPrimitiveType.getType("long"));
    assertSame(JPrimitiveType.FLOAT, JPrimitiveType.getType("float"));
    assertSame(JPrimitiveType.DOUBLE, JPrimitiveType.getType("double"));
    assertSame(JPrimitiveType.BOOLEAN, JPrimitiveType.getType("boolean"));
    assertSame(JPrimitiveType.CHAR, JPrimitiveType.getType("char"));
    assertSame(JPrimitiveType.STRING, JPrimitiveType.getType("string"));

    try {
      JPrimitiveType.getType("Date");
      fail("NoSuchElementException expected!");
    } catch (NoSuchElementException e) {
      assertEquals("JPrimitiveType does not contain a type with name Date", e.getMessage());
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
    assertSame(JPrimitiveType.BOOLEAN, type.get());

    // One boolean
    type = JPrimitiveType.getCommonSuperType(JPrimitiveType.BOOLEAN, JPrimitiveType.CHAR);
    assertFalse(type.isPresent());

    // Chars and Strings
    type = JPrimitiveType.getCommonSuperType(JPrimitiveType.STRING, JPrimitiveType.CHAR);
    assertTrue(type.isPresent());
    assertSame(JPrimitiveType.STRING, type.get());

    // Float and double
    type = JPrimitiveType.getCommonSuperType(JPrimitiveType.FLOAT, JPrimitiveType.DOUBLE);
    assertTrue(type.isPresent());
    assertSame(JPrimitiveType.DOUBLE, type.get());

    // String and double
    type = JPrimitiveType.getCommonSuperType(JPrimitiveType.STRING, JPrimitiveType.DOUBLE);
    assertTrue(type.isPresent());
    assertSame(JPrimitiveType.STRING, type.get());
  }
}
