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
    Set<B> manyBTest = Set.of(new B(), new B());
    Set<B> manyBEmptySet = new HashSet<>();
    B optBTest = new B();
    B oneBTest = new B();

    TestBuilderWithoutSetter objWithoutPojoSetters = new TestBuilderWithoutSetterBuilder()
      .setManyB(manyBTest)
      .setOneB(oneBTest)
      .setOptB(optBTest)
      .setMyBool(true)
      .setMyInt(1)
      .build();

    Assertions.assertSame(objWithoutPojoSetters.getManyB(), manyBTest);
    Assertions.assertSame(objWithoutPojoSetters.getOneB(), oneBTest);
    Assertions.assertSame(objWithoutPojoSetters.getOptB(), optBTest);
    Assertions.assertTrue(objWithoutPojoSetters.isMyBool());
    Assertions.assertEquals(1, objWithoutPojoSetters.getMyInt());


    TestBuilderWithSetter objWithPojoSetters = new TestBuilderWithSetterBuilder()
      .setManyB(manyBEmptySet)
      .setOneB(oneBTest)
      .setOptB(null)
      .setMyBool(false)
      .setMyInt(1)
      .build();

    Assertions.assertSame(objWithPojoSetters.getManyB(),manyBEmptySet);
    Assertions.assertSame(objWithPojoSetters.getOneB(),oneBTest);
    // does not work as it will throw an IllegalStateException and Log.error
    // Assertions.assertNull(objWithPojoSetters.getOptB());
    Assertions.assertEquals(1, objWithPojoSetters.getMyInt());
    Assertions.assertFalse(objWithPojoSetters.isMyBool());

    //should work fine
    objWithPojoSetters =  new TestBuilderWithSetterBuilder()
      .setManyB(manyBEmptySet)
      .setOneB(oneBTest)
      .setOptB(null)
      .setMyBool(false)
      .setMyInt(1)
      .unsafeBuild();

    objWithPojoSetters= new TestBuilderWithSetterBuilder()
      .setManyB(manyBEmptySet)
      .setOneB(oneBTest)
      .setMyBool(false)
      .setMyInt(1)
      .build();

    TestBuilderWithSetter objPojoWithSetter = new TestBuilderWithSetterBuilder()
      // setManyB(manyBTest)
      // setOptB(optBTest)
      .setOneB(oneBTest)
       //.setMyBool(false)
      // .setMyInt(1)
      .build();

    Assertions.assertTrue(objPojoWithSetter.getManyB().isEmpty());
    Assertions.assertSame(objPojoWithSetter.getOneB(), oneBTest);
    // does not work as it will throw an IllegalStateException and Log.error because of the getOptB implementation
    // Assertions.assertNull(objWithPojoSetters.getOptB());
    Assertions.assertFalse(objPojoWithSetter.isMyBool()); // default value
    Assertions.assertEquals(0, objPojoWithSetter.getMyInt()); // default value


    //we need to disable the fail quick mode, otherwise the test will be skipped
    // Afterward we will test for error messages
    Log.enableFailQuick(false);
    Log.clearFindings();

    Assertions.assertThrows(IllegalStateException.class, () -> new TestBuilderWithSetterBuilder()
      .setManyB(manyBEmptySet)
      //setOptB(optBTest)
      //setOneB is not set
      .setMyBool(true)
      .setMyInt(1)
      .build());
    Assertions.assertEquals(1, Log.getFindings().size());
    Assertions.assertEquals("0x16725x33453",Log.getFindings().get(0).getMsg());
    Log.clearFindings();

    Assertions.assertThrows(IllegalStateException.class, () -> new TestBuilderWithoutSetterBuilder()
      .setManyB(manyBEmptySet)
      .setOptB(optBTest)
      //setOneB is not set
      .setMyBool(true)
      .setMyInt(1)
      .build());
    Assertions.assertEquals(1, Log.getFindings().size());
    Assertions.assertEquals("0x16725x33448",Log.getFindings().get(0).getMsg());
    Log.clearFindings();

    //no error since ManyB is Null and the created Object just has ManyB = null
    // manyB=null
    // v.setOptB(null);
    // v.setOneB(null);
    new TestBuilderWithSetterBuilder()
      .setMyBool(true)
      .setMyInt(1)
      .unsafeBuild();
    //unsafeBuild should not log errors
    Assertions.assertEquals(0,Log.getFindings().size());

   new TestBuilderWithoutSetterBuilder()
      .setMyBool(true)
      .setMyInt(1)
      .unsafeBuild();
    //unsafeBuild should not log errors
    Assertions.assertEquals(0,Log.getFindings().size());

    //constructor methods
    Constructor<TestBuilderWithSetterBuilder> constructorWithSetter = TestBuilderWithSetterBuilder.class.getDeclaredConstructor();
    BigInteger constructorModifier = BigInteger.valueOf(constructorWithSetter.getModifiers());
    Assertions.assertEquals(Modifier.PUBLIC, constructorModifier.intValue());

    Constructor<TestBuilderWithoutSetterBuilder> constructorWithoutSetter = TestBuilderWithoutSetterBuilder.class.getDeclaredConstructor();
    BigInteger constructorWithoutSetterModifier = BigInteger.valueOf(constructorWithoutSetter.getModifiers());
    Assertions.assertEquals(Modifier.PUBLIC, constructorWithoutSetterModifier.intValue());

    //build methods
    Method buildWithSetter = TestBuilderWithSetterBuilderTOP.class.getDeclaredMethod("build");
    BigInteger modifier = BigInteger.valueOf(buildWithSetter.getModifiers());
    Assertions.assertEquals(Modifier.PUBLIC, modifier.intValue());

    Method buildWithoutSetter = TestBuilderWithoutSetterBuilder.class.getDeclaredMethod("build");
    BigInteger modifierWithoutSetter = BigInteger.valueOf(buildWithoutSetter.getModifiers());
    Assertions.assertEquals(Modifier.PUBLIC, modifierWithoutSetter.intValue());

    //unsafeBuild methods
    Method unsafeBuildWithSetter = TestBuilderWithSetterBuilderTOP.class.getDeclaredMethod("unsafeBuild");
    BigInteger unsafeModifier = BigInteger.valueOf(unsafeBuildWithSetter.getModifiers());
    Assertions.assertEquals(Modifier.PUBLIC, unsafeModifier.intValue());

    Method unsafeBuildWithoutSetter = TestBuilderWithoutSetterBuilder.class.getDeclaredMethod("unsafeBuild");
    BigInteger unsafeModifierWithoutSetter = BigInteger.valueOf(unsafeBuildWithoutSetter.getModifiers());
    Assertions.assertEquals(Modifier.PUBLIC, unsafeModifierWithoutSetter.intValue());

    //isValid methods
    Method isValidWithSetter = TestBuilderWithSetterBuilderTOP.class.getDeclaredMethod("isValid");
    BigInteger isValidModifier = BigInteger.valueOf(isValidWithSetter.getModifiers());
    Assertions.assertEquals(Modifier.PRIVATE, isValidModifier.intValue());

    Method isValidWithoutSetter = TestBuilderWithoutSetterBuilder.class.getDeclaredMethod("isValid");
    BigInteger isValidModifierWithoutSetter = BigInteger.valueOf(isValidWithoutSetter.getModifiers());
    Assertions.assertEquals(Modifier.PRIVATE, isValidModifierWithoutSetter.intValue());

    //set methods
    Method setManyBWithSetter = TestBuilderWithSetterBuilderTOP.class.getDeclaredMethod("setManyB", Set.class);
    Assertions.assertEquals(Modifier.PUBLIC, setManyBWithSetter.getModifiers());

    Method setManyBWithoutSetter = TestBuilderWithoutSetterBuilder.class.getDeclaredMethod("setManyB", Set.class);
    Assertions.assertEquals(Modifier.PUBLIC, setManyBWithoutSetter.getModifiers());

    Method setOptBWithSetter = TestBuilderWithSetterBuilderTOP.class.getDeclaredMethod("setOptB", B.class);
    Assertions.assertEquals(Modifier.PUBLIC, setOptBWithSetter.getModifiers());

    Method setOptBWithoutSetter = TestBuilderWithoutSetterBuilder.class.getDeclaredMethod("setOptB", B.class);
    Assertions.assertEquals(Modifier.PUBLIC, setOptBWithoutSetter.getModifiers());

    Method setOneBWithSetter = TestBuilderWithSetterBuilderTOP.class.getDeclaredMethod("setOneB", B.class);
    Assertions.assertEquals(Modifier.PUBLIC, setOneBWithSetter.getModifiers());

    Method setOneBWithoutSetter = TestBuilderWithoutSetterBuilder.class.getDeclaredMethod("setOneB", B.class);
    Assertions.assertEquals(Modifier.PUBLIC, setOneBWithoutSetter.getModifiers());

    Method setMyIntWithSetter = TestBuilderWithSetterBuilderTOP.class.getDeclaredMethod("setMyInt", int.class);
    Assertions.assertEquals(Modifier.PUBLIC, setMyIntWithSetter.getModifiers());

    Method setMyIntWithoutSetter = TestBuilderWithoutSetterBuilder.class.getDeclaredMethod("setMyInt", int.class);
    Assertions.assertEquals(Modifier.PUBLIC, setMyIntWithoutSetter.getModifiers());

    Method setMyBoolWithSetter = TestBuilderWithSetterBuilderTOP.class.getDeclaredMethod("setMyBool", boolean.class);
    Assertions.assertEquals(Modifier.PUBLIC, setMyBoolWithSetter.getModifiers());

    Method setMyBoolWithoutSetter = TestBuilderWithoutSetterBuilder.class.getDeclaredMethod("setMyBool", boolean.class);
    Assertions.assertEquals(Modifier.PUBLIC, setMyBoolWithoutSetter.getModifiers());

    //setAbsent methods
    Method setManyBAbsentWithSetter = TestBuilderWithSetterBuilderTOP.class.getDeclaredMethod("setManyBAbsent");
    Assertions.assertEquals(Modifier.PUBLIC, setManyBAbsentWithSetter.getModifiers());

    Method setManyBAbsentWithoutSetter = TestBuilderWithoutSetterBuilder.class.getDeclaredMethod("setManyBAbsent");
    Assertions.assertEquals(Modifier.PUBLIC, setManyBAbsentWithoutSetter.getModifiers());

    Method setOptBAbsentWithSetter = TestBuilderWithSetterBuilderTOP.class.getDeclaredMethod("setOptBAbsent");
    Assertions.assertEquals(Modifier.PUBLIC, setOptBAbsentWithSetter.getModifiers());

    Method setOptBAbsentWithoutSetter = TestBuilderWithoutSetterBuilder.class.getDeclaredMethod("setOptBAbsent");
    Assertions.assertEquals(Modifier.PUBLIC, setOptBAbsentWithoutSetter.getModifiers());

    //setAbsent should not exist for cardinality of 1
    Assertions.assertThrows(NoSuchMethodException.class, () -> TestBuilderWithSetterBuilderTOP.class.getDeclaredMethod("setOneBAbsent"));
    Assertions.assertThrows(NoSuchMethodException.class, () -> TestBuilderWithoutSetterBuilder.class.getDeclaredMethod("setOneBAbsent"));
    Assertions.assertThrows(NoSuchMethodException.class, () -> TestBuilderWithSetterBuilderTOP.class.getDeclaredMethod("setMyIntAbsent"));
    Assertions.assertThrows(NoSuchMethodException.class, () -> TestBuilderWithoutSetterBuilder.class.getDeclaredMethod("setMyIntAbsent"));
    Assertions.assertThrows(NoSuchMethodException.class, () -> TestBuilderWithSetterBuilderTOP.class.getDeclaredMethod("setMyBoolAbsent"));
    Assertions.assertThrows(NoSuchMethodException.class, () -> TestBuilderWithoutSetterBuilder.class.getDeclaredMethod("setMyBoolAbsent"));
  }
}
