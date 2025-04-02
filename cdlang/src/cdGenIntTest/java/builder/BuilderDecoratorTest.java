/* (c) https://github.com/MontiCore/monticore */
package builder;

import TestBuilder.*;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

/**
 * Test the result of the Builder Decorator. When we arrive in this test, the output compiles
 * correctly
 */
public class BuilderDecoratorTest {

  @Test
  public void test() throws Exception {
    //
    Set<B> set = Set.of(new B(), new B());
    Set<B> emptySet = new HashSet<>();

    TestBuilderWithoutSetter objWithoutPojoSetters = new TestBuilderWithoutSetterBuilder()
      .setManyB(set)
      .setMyBool(true)
      .setOneB(new B())
      .setMyInt(1)
      .setOptB(new B())
      .build();

    Assertions.assertEquals(1, objWithoutPojoSetters.getMyInt());
    Assertions.assertTrue(objWithoutPojoSetters.isMyBool());
    Assertions.assertFalse(objWithoutPojoSetters.isEmptyManyB());
    Assertions.assertTrue(objWithoutPojoSetters.containsAllManyB(set));
    Assertions.assertEquals(2, objWithoutPojoSetters.toArrayManyB().length);

    TestBuilderWithSetter objWithPojoSetters = new TestBuilderWithSetterBuilder()
      .setManyB(set)
      .setMyBool(true)
      .setOneB(new B())
      .setMyInt(1)
      .setOptB(new B())
      .build();

    Assertions.assertEquals(1, objWithPojoSetters.getMyInt());
    Assertions.assertTrue(objWithPojoSetters.isMyBool());
    Assertions.assertFalse(objWithPojoSetters.isEmptyManyB());
    Assertions.assertTrue(objWithPojoSetters.containsAllManyB(set));
    Assertions.assertEquals(2, objWithPojoSetters.toArrayManyB().length);

    //TODO gradle skips here because of Log.error I DO NOT CALL Log.enableFailQuick(true) after
    var failQuickEnabled = Log.isFailQuickEnabled();
    de.se_rwth.commons.logging.Log.enableFailQuick(false);

    Assertions.assertThrows(IllegalStateException.class, () -> new TestBuilderWithSetterBuilder()
      .setManyB(emptySet)
      .setMyBool(true)
      .setMyInt(1)
      .build());
    Assertions.assertThrows(IllegalStateException.class, () -> new TestBuilderWithoutSetterBuilder()
      .setManyB(emptySet)
      .setMyBool(true)
      .setMyInt(1)
      .build());
    Assertions.assertThrows(NullPointerException.class, () -> new TestBuilderWithSetterBuilder()
      .setMyBool(true)
      .setMyInt(1)
      .unsafeBuild());
    Assertions.assertThrows(NullPointerException.class, () -> new TestBuilderWithoutSetterBuilder()
      .setMyBool(true)
      .setMyInt(1)
      .unsafeBuild());

    //constructor methods
    Constructor<TestBuilderWithSetterBuilder> constructorWithSetter = TestBuilderWithSetterBuilder.class.getDeclaredConstructor();
    BigInteger constructorModifier = BigInteger.valueOf(constructorWithSetter.getModifiers());
    Assertions.assertEquals(Modifier.PUBLIC, constructorModifier.intValue());
    Assertions.assertEquals(0, constructorWithSetter.getParameterTypes().length);

    Constructor<TestBuilderWithoutSetterBuilder> constructorWithoutSetter = TestBuilderWithoutSetterBuilder.class.getDeclaredConstructor();
    BigInteger constructorWithoutSetterModifier = BigInteger.valueOf(constructorWithoutSetter.getModifiers());
    Assertions.assertEquals(Modifier.PUBLIC, constructorWithoutSetterModifier.intValue());
    Assertions.assertEquals(0, constructorWithoutSetter.getParameterTypes().length);

    //build methods
    Method buildWithSetter = TestBuilderWithSetterBuilderTOP.class.getDeclaredMethod("build");
    BigInteger modifier = BigInteger.valueOf(buildWithSetter.getModifiers());
    Assertions.assertEquals(Modifier.PUBLIC, modifier.intValue());
    Class<?>[] buildWithSetterParameterTypes = buildWithSetter.getParameterTypes();
    Assertions.assertEquals(0, buildWithSetterParameterTypes.length);

    Method buildWithoutSetter = TestBuilderWithoutSetterBuilder.class.getDeclaredMethod("build");
    BigInteger modifierWithoutSetter = BigInteger.valueOf(buildWithoutSetter.getModifiers());
    Assertions.assertEquals(Modifier.PUBLIC, modifierWithoutSetter.intValue());
    Class<?>[] buildWithoutSetterParameterTypes = buildWithoutSetter.getParameterTypes();
    Assertions.assertEquals(0, buildWithoutSetterParameterTypes.length);

    //unsafeBuild methods
    Method unsafeBuildWithSetter = TestBuilderWithSetterBuilderTOP.class.getDeclaredMethod("unsafeBuild");
    BigInteger unsafeModifier = BigInteger.valueOf(unsafeBuildWithSetter.getModifiers());
    Assertions.assertEquals(Modifier.PUBLIC, unsafeModifier.intValue());
    Class<?>[] unsafeBuildWithSetterParameterTypes = unsafeBuildWithSetter.getParameterTypes();
    Assertions.assertEquals(0, unsafeBuildWithSetterParameterTypes.length);

    Method unsafeBuildWithoutSetter = TestBuilderWithoutSetterBuilder.class.getDeclaredMethod("unsafeBuild");
    BigInteger unsafeModifierWithoutSetter = BigInteger.valueOf(unsafeBuildWithoutSetter.getModifiers());
    Assertions.assertEquals(Modifier.PUBLIC, unsafeModifierWithoutSetter.intValue());
    Class<?>[] unsafeBuildWithoutSetterParameterTypes = unsafeBuildWithoutSetter.getParameterTypes();
    Assertions.assertEquals(0, unsafeBuildWithoutSetterParameterTypes.length);

    //isValid methods
    Method isValidWithSetter = TestBuilderWithSetterBuilderTOP.class.getDeclaredMethod("isValid");
    BigInteger isValidModifier = BigInteger.valueOf(isValidWithSetter.getModifiers());
    Assertions.assertEquals(Modifier.PRIVATE, isValidModifier.intValue());
    Class<?>[] isValidWithSetterParameterTypes = isValidWithSetter.getParameterTypes();
    Assertions.assertEquals(0, isValidWithSetterParameterTypes.length);

    Method isValidWithoutSetter = TestBuilderWithoutSetterBuilder.class.getDeclaredMethod("isValid");
    BigInteger isValidModifierWithoutSetter = BigInteger.valueOf(isValidWithoutSetter.getModifiers());
    Assertions.assertEquals(Modifier.PRIVATE, isValidModifierWithoutSetter.intValue());
    Class<?>[] isValidWithoutSetterParameterTypes = isValidWithoutSetter.getParameterTypes();
    Assertions.assertEquals(0, isValidWithoutSetterParameterTypes.length);

    //set methods
    Method setManyBWithSetter = TestBuilderWithSetterBuilderTOP.class.getDeclaredMethod("setManyB", Set.class);
    Assertions.assertEquals(Modifier.PUBLIC, setManyBWithSetter.getModifiers());
    Class<?>[] setManyBWithSetterParameterTypes = setManyBWithSetter.getParameterTypes();
    Assertions.assertEquals(1, setManyBWithSetterParameterTypes.length);
    Assertions.assertEquals(Set.class, setManyBWithSetterParameterTypes[0]);

    Method setManyBWithoutSetter = TestBuilderWithoutSetterBuilder.class.getDeclaredMethod("setManyB", Set.class);
    Assertions.assertEquals(Modifier.PUBLIC, setManyBWithoutSetter.getModifiers());
    Class<?>[] setManyBWithoutSetterParameterTypes = setManyBWithoutSetter.getParameterTypes();
    Assertions.assertEquals(1, setManyBWithoutSetterParameterTypes.length);
    Assertions.assertEquals(Set.class, setManyBWithoutSetterParameterTypes[0]);

    Method setOptBWithSetter = TestBuilderWithSetterBuilderTOP.class.getDeclaredMethod("setOptB", B.class);
    Assertions.assertEquals(Modifier.PUBLIC, setOptBWithSetter.getModifiers());
    Class<?>[] setOptBWithSetterParameterTypes = setOptBWithSetter.getParameterTypes();
    Assertions.assertEquals(1, setOptBWithSetterParameterTypes.length);
    Assertions.assertEquals(B.class, setOptBWithSetterParameterTypes[0]);

    Method setOptBWithoutSetter = TestBuilderWithoutSetterBuilder.class.getDeclaredMethod("setOptB", B.class);
    Assertions.assertEquals(Modifier.PUBLIC, setOptBWithoutSetter.getModifiers());
    Class<?>[] setOptBWithoutSetterParameterTypes = setOptBWithoutSetter.getParameterTypes();
    Assertions.assertEquals(1, setOptBWithoutSetterParameterTypes.length);
    Assertions.assertEquals(B.class, setOptBWithoutSetterParameterTypes[0]);

    Method setOneBWithSetter = TestBuilderWithSetterBuilderTOP.class.getDeclaredMethod("setOneB", B.class);
    Assertions.assertEquals(Modifier.PUBLIC, setOneBWithSetter.getModifiers());
    Class<?>[] setOneBWithSetterParameterTypes = setOneBWithSetter.getParameterTypes();
    Assertions.assertEquals(1, setOneBWithSetterParameterTypes.length);
    Assertions.assertEquals(B.class, setOneBWithSetterParameterTypes[0]);

    Method setOneBWithoutSetter = TestBuilderWithoutSetterBuilder.class.getDeclaredMethod("setOneB", B.class);
    Assertions.assertEquals(Modifier.PUBLIC, setOneBWithoutSetter.getModifiers());
    Class<?>[] setOneBWithoutSetterParameterTypes = setOneBWithoutSetter.getParameterTypes();
    Assertions.assertEquals(1, setOneBWithoutSetterParameterTypes.length);
    Assertions.assertEquals(B.class, setOneBWithoutSetterParameterTypes[0]);

    Method setMyIntWithSetter = TestBuilderWithSetterBuilderTOP.class.getDeclaredMethod("setMyInt", int.class);
    Assertions.assertEquals(Modifier.PUBLIC, setMyIntWithSetter.getModifiers());
    Class<?>[] setMyIntWithSetterParameterTypes = setMyIntWithSetter.getParameterTypes();
    Assertions.assertEquals(1, setMyIntWithSetterParameterTypes.length);
    Assertions.assertEquals(int.class, setMyIntWithSetterParameterTypes[0]);

    Method setMyIntWithoutSetter = TestBuilderWithoutSetterBuilder.class.getDeclaredMethod("setMyInt", int.class);
    Assertions.assertEquals(Modifier.PUBLIC, setMyIntWithoutSetter.getModifiers());
    Class<?>[] setMyIntWithoutSetterParameterTypes = setMyIntWithoutSetter.getParameterTypes();
    Assertions.assertEquals(1, setMyIntWithoutSetterParameterTypes.length);
    Assertions.assertEquals(int.class, setMyIntWithoutSetterParameterTypes[0]);

    Method setMyBoolWithSetter = TestBuilderWithSetterBuilderTOP.class.getDeclaredMethod("setMyBool", boolean.class);
    Assertions.assertEquals(Modifier.PUBLIC, setMyBoolWithSetter.getModifiers());
    Class<?>[] parameterTypesWithSetter = setMyBoolWithSetter.getParameterTypes();
    Assertions.assertEquals(1, parameterTypesWithSetter.length);
    Assertions.assertEquals(boolean.class, parameterTypesWithSetter[0]);


    Method setMyBoolWithoutSetter = TestBuilderWithoutSetterBuilder.class.getDeclaredMethod("setMyBool", boolean.class);
    Assertions.assertEquals(Modifier.PUBLIC, setMyBoolWithoutSetter.getModifiers());
    Class<?>[] parameterTypes = setMyBoolWithoutSetter.getParameterTypes();
    Assertions.assertEquals(1, parameterTypes.length);
    Assertions.assertEquals(boolean.class, parameterTypes[0]);

    //setAbsent methods
    Method setManyBAbsentWithSetter = TestBuilderWithSetterBuilderTOP.class.getDeclaredMethod("setManyBAbsent");
    Assertions.assertEquals(Modifier.PUBLIC, setManyBAbsentWithSetter.getModifiers());
    Class<?>[] setManyBAbsentWithSetterParameterTypes = setManyBAbsentWithSetter.getParameterTypes();
    Assertions.assertEquals(0, setManyBAbsentWithSetterParameterTypes.length);

    Method setManyBAbsentWithoutSetter = TestBuilderWithoutSetterBuilder.class.getDeclaredMethod("setManyBAbsent");
    Assertions.assertEquals(Modifier.PUBLIC, setManyBAbsentWithoutSetter.getModifiers());
    Class<?>[] setManyBAbsentWithoutSetterParameterTypes = setManyBAbsentWithoutSetter.getParameterTypes();
    Assertions.assertEquals(0, setManyBAbsentWithoutSetterParameterTypes.length);

    Method setOptBAbsentWithSetter = TestBuilderWithSetterBuilderTOP.class.getDeclaredMethod("setOptBAbsent");
    Assertions.assertEquals(Modifier.PUBLIC, setOptBAbsentWithSetter.getModifiers());
    Class<?>[] setOptBAbsentWithSetterParameterTypes = setOptBAbsentWithSetter.getParameterTypes();
    Assertions.assertEquals(0, setOptBAbsentWithSetterParameterTypes.length);

    Method setOptBAbsentWithoutSetter = TestBuilderWithoutSetterBuilder.class.getDeclaredMethod("setOptBAbsent");
    Assertions.assertEquals(Modifier.PUBLIC, setOptBAbsentWithoutSetter.getModifiers());
    Class<?>[] setOptBAbsentWithoutSetterParameterTypes = setOptBAbsentWithoutSetter.getParameterTypes();
    Assertions.assertEquals(0, setOptBAbsentWithoutSetterParameterTypes.length);
    //setAbsent should not exist for cardinality of 1
    Assertions.assertThrows(NoSuchMethodException.class, () -> TestBuilderWithSetterBuilderTOP.class.getDeclaredMethod("setOneBAbsent"));
    Assertions.assertThrows(NoSuchMethodException.class, () -> TestBuilderWithoutSetterBuilder.class.getDeclaredMethod("setOneBAbsent"));
    Assertions.assertThrows(NoSuchMethodException.class, () -> TestBuilderWithSetterBuilderTOP.class.getDeclaredMethod("setMyIntAbsent"));
    Assertions.assertThrows(NoSuchMethodException.class, () -> TestBuilderWithoutSetterBuilder.class.getDeclaredMethod("setMyIntAbsent"));
    Assertions.assertThrows(NoSuchMethodException.class, () -> TestBuilderWithSetterBuilderTOP.class.getDeclaredMethod("setMyBoolAbsent"));
    Assertions.assertThrows(NoSuchMethodException.class, () -> TestBuilderWithoutSetterBuilder.class.getDeclaredMethod("setMyBoolAbsent"));
  }
}
