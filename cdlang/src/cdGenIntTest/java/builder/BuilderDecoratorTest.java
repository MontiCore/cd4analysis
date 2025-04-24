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
import java.util.Set;

/**
 * Test the result of the Builder Decorator. When we arrive in this test, the output compiles
 * correctly
 */
public class BuilderDecoratorTest {

  @Test
  public void test() throws Exception {
    checkClassAndMethodExistence();
    Set<B> manyBTest = Set.of(new B(), new B());
    B optBTest = new B();
    B oneBTest = new B();

    /**
     * Test the generated build method with all setters
     */
    //we need to disable the fail quick mode, otherwise the test will be skipped
    // Afterward we will test for error messages
    Log.enableFailQuick(false);
    Log.clearFindings();

    /**
     * build
     */
    //build with all parameters set
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
      .setManyB(manyBTest)
      .setOneB(oneBTest)
      .setOptB(optBTest)
      .setMyBool(true)
      .setMyInt(1)
      .build();
    Assertions.assertSame(objWithPojoSetters.getManyB(), manyBTest);
    Assertions.assertSame(objWithPojoSetters.getOneB(), oneBTest);
    Assertions.assertSame(objWithPojoSetters.getOptB(), optBTest);
    Assertions.assertTrue(objWithPojoSetters.isMyBool());
    Assertions.assertEquals(1, objWithPojoSetters.getMyInt());

    //build with ManyB set to null -> an empty list should be created
    TestBuilderWithSetter objWithPojoSettersManyBNull = new TestBuilderWithSetterBuilder()
      .setManyB(null)
      .setOneB(oneBTest)
      .setOptB(optBTest)
      .setMyBool(false)
      .setMyInt(1)
      .build();
    Assertions.assertEquals(0, objWithPojoSettersManyBNull.getManyB().size());
    Assertions.assertSame(objWithPojoSettersManyBNull.getOneB(), oneBTest);
    Assertions.assertSame(objWithPojoSettersManyBNull.getOptB(), optBTest);
    Assertions.assertFalse(objWithPojoSettersManyBNull.isMyBool());
    Assertions.assertEquals(1, objWithPojoSettersManyBNull.getMyInt());

    TestBuilderWithoutSetter objWithoutPojoSettersManyBNull = new TestBuilderWithoutSetterBuilder()
      .setManyB(null)
      .setOneB(oneBTest)
      .setOptB(optBTest)
      .setMyBool(false)
      .setMyInt(1)
      .build();
    Assertions.assertEquals(0, objWithoutPojoSettersManyBNull.getManyB().size());
    Assertions.assertSame(objWithoutPojoSettersManyBNull.getOneB(), oneBTest);
    Assertions.assertSame(objWithoutPojoSettersManyBNull.getOptB(), optBTest);
    Assertions.assertFalse(objWithoutPojoSettersManyBNull.isMyBool());
    Assertions.assertEquals(1, objWithoutPojoSettersManyBNull.getMyInt());

    //build with Opt set to null -> an error will occur
    TestBuilderWithSetter objWithPojoSettersOptNull = new TestBuilderWithSetterBuilder()
      .setManyB(manyBTest)
      .setOneB(oneBTest)
      .setOptB(null)
      .setMyBool(false)
      .setMyInt(1)
      .build();
    Log.clearFindings();
    Assertions.assertSame(objWithPojoSettersOptNull.getManyB(), manyBTest);
    Assertions.assertSame(objWithPojoSettersOptNull.getOneB(), oneBTest);
    Assertions.assertThrows(IllegalStateException.class, objWithPojoSettersOptNull::getOptB);
    Assertions.assertEquals(1, Log.getFindings().size());
    Assertions.assertEquals("0xA7003x23650 get for OptB can't return a value. Attribute is empty.", Log.getFindings().get(0).getMsg());
    Log.clearFindings();
    Assertions.assertEquals(1, objWithPojoSettersOptNull.getMyInt());
    Assertions.assertFalse(objWithPojoSettersOptNull.isMyBool());

    TestBuilderWithoutSetter objWithoutPojoSettersOptNull = new TestBuilderWithoutSetterBuilder()
      .setManyB(manyBTest)
      .setOneB(oneBTest)
      .setOptB(null)
      .setMyBool(false)
      .setMyInt(1)
      .build();
    Log.clearFindings();
    Assertions.assertSame(objWithoutPojoSettersOptNull.getManyB(), manyBTest);
    Assertions.assertSame(objWithoutPojoSettersOptNull.getOneB(), oneBTest);
    Assertions.assertThrows(IllegalStateException.class, objWithoutPojoSettersOptNull::getOptB);
    Assertions.assertEquals(1, Log.getFindings().size());
    Assertions.assertEquals("0xA7003x23651 get for OptB can't return a value. Attribute is empty.", Log.getFindings().get(0).getMsg());
    Log.clearFindings();
    Assertions.assertEquals(1, objWithoutPojoSettersOptNull.getMyInt());
    Assertions.assertFalse(objWithoutPojoSettersOptNull.isMyBool());

    //build with OneB set to null -> the build should not work
    Assertions.assertThrows(IllegalStateException.class, () -> new TestBuilderWithSetterBuilder()
      .setManyB(manyBTest)
      .setOneB(null)
      .setOptB(optBTest)
      .setMyBool(false)
      .setMyInt(1)
      .build());
    Assertions.assertSame(1, Log.getFindings().size());
    Assertions.assertEquals("0x16725x33453", Log.getFindings().get(0).getMsg());
    Log.clearFindings();

    Assertions.assertThrows(IllegalStateException.class, () -> new TestBuilderWithoutSetterBuilder()
      .setManyB(manyBTest)
      .setOneB(null)
      .setOptB(optBTest)
      .setMyBool(false)
      .setMyInt(1)
      .build());
    Assertions.assertSame(1, Log.getFindings().size());
    Assertions.assertEquals("0x16725x33448", Log.getFindings().get(0).getMsg());
    Log.clearFindings();

    //build with manyB not set -> an empty list should be created
    TestBuilderWithSetter objWithPojoSettersManyBNotSet = new TestBuilderWithSetterBuilder()
      //.setManyB(manyBTest)
      .setOneB(oneBTest)
      .setOptB(optBTest)
      .setMyBool(false)
      .setMyInt(1)
      .build();
    Assertions.assertEquals(0, objWithPojoSettersManyBNotSet.getManyB().size());

    TestBuilderWithoutSetter objWithoutPojoSettersManyBNotSet = new TestBuilderWithoutSetterBuilder()
      //.setManyB(manyBTest)
      .setOneB(oneBTest)
      .setOptB(optBTest)
      .setMyBool(false)
      .setMyInt(1)
      .build();
    Assertions.assertEquals(0, objWithoutPojoSettersManyBNotSet.getManyB().size());

    //build with oneB not set -> an error will occur
    Assertions.assertThrows(IllegalStateException.class, () -> new TestBuilderWithSetterBuilder()
      .setManyB(manyBTest)
      //.setOneB(oneBTest)
      .setOptB(optBTest)
      .setMyBool(false)
      .setMyInt(1)
      .build());
    Assertions.assertSame(1, Log.getFindings().size());
    Assertions.assertEquals("0x16725x33453", Log.getFindings().get(0).getMsg());
    Log.clearFindings();

    Assertions.assertThrows(IllegalStateException.class, () -> new TestBuilderWithoutSetterBuilder()
      .setManyB(manyBTest)
      //.setOneB(oneBTest)
      .setOptB(optBTest)
      .setMyBool(false)
      .setMyInt(1)
      .build());
    Assertions.assertSame(1, Log.getFindings().size());
    Assertions.assertEquals("0x16725x33448", Log.getFindings().get(0).getMsg());
    Log.clearFindings();

    //build with optB not set
    TestBuilderWithSetter objWithPojoSettersOptBNotSet = new TestBuilderWithSetterBuilder()
      .setManyB(manyBTest)
      .setOneB(oneBTest)
      //.setOptB(optBTest)
      .setMyBool(false)
      .setMyInt(1)
      .build();
    Assertions.assertSame(objWithPojoSettersOptBNotSet.getManyB(), manyBTest);
    Assertions.assertSame(objWithPojoSettersOptBNotSet.getOneB(), oneBTest);
    Assertions.assertThrows(IllegalStateException.class, objWithPojoSettersOptBNotSet::getOptB);
    Assertions.assertEquals(1, Log.getFindings().size());
    Assertions.assertEquals("0xA7003x23650 get for OptB can't return a value. Attribute is empty.", Log.getFindings().get(0).getMsg());
    Log.clearFindings();
    Assertions.assertFalse(objWithPojoSettersOptBNotSet.isMyBool());
    Assertions.assertEquals(1, objWithPojoSettersOptBNotSet.getMyInt());

    TestBuilderWithoutSetter objWithoutPojoSettersOptBNotSet = new TestBuilderWithoutSetterBuilder()
      .setManyB(manyBTest)
      .setOneB(oneBTest)
      //.setOptB(optBTest)
      .setMyBool(false)
      .setMyInt(1)
      .build();
    Assertions.assertSame(objWithoutPojoSettersOptBNotSet.getManyB(), manyBTest);
    Assertions.assertSame(objWithoutPojoSettersOptBNotSet.getOneB(), oneBTest);
    Assertions.assertThrows(IllegalStateException.class, objWithoutPojoSettersOptBNotSet::getOptB);
    Assertions.assertEquals(1, Log.getFindings().size());
    Assertions.assertEquals("0xA7003x23651 get for OptB can't return a value. Attribute is empty.", Log.getFindings().get(0).getMsg());
    Log.clearFindings();
    Assertions.assertFalse(objWithoutPojoSettersOptBNotSet.isMyBool());
    Assertions.assertEquals(1, objWithoutPojoSettersOptBNotSet.getMyInt());

    /**
     * unsafeBuild
     */
    //unsafeBuild with all parameters set
    TestBuilderWithoutSetter unsafeBuildObjWithoutPojoSetters = new TestBuilderWithoutSetterBuilder()
      .setManyB(manyBTest)
      .setOneB(oneBTest)
      .setOptB(optBTest)
      .setMyBool(true)
      .setMyInt(1)
      .unsafeBuild();
    Assertions.assertSame(unsafeBuildObjWithoutPojoSetters.getManyB(), manyBTest);
    Assertions.assertSame(unsafeBuildObjWithoutPojoSetters.getOneB(), oneBTest);
    Assertions.assertSame(unsafeBuildObjWithoutPojoSetters.getOptB(), optBTest);
    Assertions.assertTrue(unsafeBuildObjWithoutPojoSetters.isMyBool());
    Assertions.assertEquals(1, unsafeBuildObjWithoutPojoSetters.getMyInt());

    TestBuilderWithSetter unsafeBuildObjWithPojoSetters = new TestBuilderWithSetterBuilder()
      .setManyB(manyBTest)
      .setOneB(oneBTest)
      .setOptB(optBTest)
      .setMyBool(true)
      .setMyInt(1)
      .unsafeBuild();
    Assertions.assertSame(unsafeBuildObjWithPojoSetters.getManyB(), manyBTest);
    Assertions.assertSame(unsafeBuildObjWithPojoSetters.getOneB(), oneBTest);
    Assertions.assertSame(unsafeBuildObjWithPojoSetters.getOptB(), optBTest);
    Assertions.assertTrue(unsafeBuildObjWithPojoSetters.isMyBool());
    Assertions.assertEquals(1, unsafeBuildObjWithPojoSetters.getMyInt());

    //unsafeBuild with ManyB set to null -> the list is set to null
    TestBuilderWithSetter unsafeBuildObjWithPojoSettersManyBNull = new TestBuilderWithSetterBuilder()
      .setManyB(null)
      .setOneB(oneBTest)
      .setOptB(optBTest)
      .setMyBool(false)
      .setMyInt(1)
      .unsafeBuild();
    Assertions.assertNull(unsafeBuildObjWithPojoSettersManyBNull.getManyB());
    Assertions.assertSame(unsafeBuildObjWithPojoSettersManyBNull.getOneB(), oneBTest);
    Assertions.assertSame(unsafeBuildObjWithPojoSettersManyBNull.getOptB(), optBTest);
    Assertions.assertFalse(unsafeBuildObjWithPojoSettersManyBNull.isMyBool());
    Assertions.assertEquals(1, unsafeBuildObjWithPojoSettersManyBNull.getMyInt());

    TestBuilderWithoutSetter unsafeBuildObjWithoutPojoSettersManyBNull = new TestBuilderWithoutSetterBuilder()
      .setManyB(null)
      .setOneB(oneBTest)
      .setOptB(optBTest)
      .setMyBool(false)
      .setMyInt(1)
      .unsafeBuild();
    Assertions.assertNull(unsafeBuildObjWithoutPojoSettersManyBNull.getManyB());
    Assertions.assertSame(unsafeBuildObjWithoutPojoSettersManyBNull.getOneB(), oneBTest);
    Assertions.assertSame(unsafeBuildObjWithoutPojoSettersManyBNull.getOptB(), optBTest);
    Assertions.assertFalse(unsafeBuildObjWithoutPojoSettersManyBNull.isMyBool());
    Assertions.assertEquals(1, unsafeBuildObjWithoutPojoSettersManyBNull.getMyInt());

    //unsafeBuild with Opt set to null -> an error will occur
    TestBuilderWithSetter unsafeBuildObjWithPojoSettersOptNull = new TestBuilderWithSetterBuilder()
      .setManyB(manyBTest)
      .setOneB(oneBTest)
      .setOptB(null)
      .setMyBool(false)
      .setMyInt(1)
      .unsafeBuild();
    Log.clearFindings();
    Assertions.assertSame(unsafeBuildObjWithPojoSettersOptNull.getManyB(), manyBTest);
    Assertions.assertSame(unsafeBuildObjWithPojoSettersOptNull.getOneB(), oneBTest);
    Assertions.assertThrows(IllegalStateException.class, unsafeBuildObjWithPojoSettersOptNull::getOptB);
    Assertions.assertEquals(1, Log.getFindings().size());
    Assertions.assertEquals("0xA7003x23650 get for OptB can't return a value. Attribute is empty.", Log.getFindings().get(0).getMsg());
    Log.clearFindings();
    Assertions.assertEquals(1, unsafeBuildObjWithPojoSettersOptNull.getMyInt());
    Assertions.assertFalse(unsafeBuildObjWithPojoSettersOptNull.isMyBool());

    TestBuilderWithoutSetter unsafeBuildObjWithoutPojoSettersOptNull = new TestBuilderWithoutSetterBuilder()
      .setManyB(manyBTest)
      .setOneB(oneBTest)
      .setOptB(null)
      .setMyBool(false)
      .setMyInt(1)
      .unsafeBuild();
    Log.clearFindings();
    Assertions.assertSame(unsafeBuildObjWithoutPojoSettersOptNull.getManyB(), manyBTest);
    Assertions.assertSame(unsafeBuildObjWithoutPojoSettersOptNull.getOneB(), oneBTest);
    Assertions.assertThrows(IllegalStateException.class, unsafeBuildObjWithoutPojoSettersOptNull::getOptB);
    Assertions.assertEquals(1, Log.getFindings().size());
    Assertions.assertEquals("0xA7003x23651 get for OptB can't return a value. Attribute is empty.", Log.getFindings().get(0).getMsg());
    Log.clearFindings();
    Assertions.assertEquals(1, unsafeBuildObjWithoutPojoSettersOptNull.getMyInt());
    Assertions.assertFalse(unsafeBuildObjWithoutPojoSettersOptNull.isMyBool());

    //unsafeBuild with OneB set to null -> set it to null
    TestBuilderWithSetter unsafeBuildObjWithPojoSetterOneBNull = new TestBuilderWithSetterBuilder()
      .setManyB(manyBTest)
      .setOneB(null)
      .setOptB(optBTest)
      .setMyBool(false)
      .setMyInt(1)
      .unsafeBuild();
    Assertions.assertNull(unsafeBuildObjWithPojoSetterOneBNull.getOneB());

    TestBuilderWithoutSetter unsafeBuildObjWithoutPojoSetterOneBNull =  new TestBuilderWithoutSetterBuilder()
      .setManyB(manyBTest)
      .setOneB(null)
      .setOptB(optBTest)
      .setMyBool(false)
      .setMyInt(1)
      .unsafeBuild();
    Assertions.assertNull(unsafeBuildObjWithoutPojoSetterOneBNull.getOneB());

    //unsafeBuild with manyB not set -> set it to null
    TestBuilderWithSetter unsafeBuildObjWithPojoSettersManyBNotSet = new TestBuilderWithSetterBuilder()
      //.setManyB(manyBTest)
      .setOneB(oneBTest)
      .setOptB(optBTest)
      .setMyBool(false)
      .setMyInt(1)
      .unsafeBuild();
    Assertions.assertNull(unsafeBuildObjWithPojoSettersManyBNotSet.getManyB());

    TestBuilderWithoutSetter unsafeBuildObjWithoutPojoSettersManyBNotSet = new TestBuilderWithoutSetterBuilder()
      //.setManyB(manyBTest)
      .setOneB(oneBTest)
      .setOptB(optBTest)
      .setMyBool(false)
      .setMyInt(1)
      .unsafeBuild();
    Assertions.assertNull(unsafeBuildObjWithoutPojoSettersManyBNotSet.getManyB());

    //unsafeBuild with oneB not set -> set it to null
    TestBuilderWithSetter unsafeBuildObjWithPojoSettersOneBNotSet = new TestBuilderWithSetterBuilder()
      .setManyB(manyBTest)
      //.setOneB(oneBTest)
      .setOptB(optBTest)
      .setMyBool(false)
      .setMyInt(1)
      .unsafeBuild();
    Assertions.assertNull(unsafeBuildObjWithPojoSettersOneBNotSet.getOneB());

    TestBuilderWithoutSetter  unsafeBuildObjWithoutPojoSettersOneBNotSet  = new TestBuilderWithoutSetterBuilder()
      .setManyB(manyBTest)
      //.setOneB(oneBTest)
      .setOptB(optBTest)
      .setMyBool(false)
      .setMyInt(1)
      .unsafeBuild();
    Assertions.assertNull(unsafeBuildObjWithoutPojoSettersOneBNotSet.getOneB());

    //unsafeBuild with optB not set
    TestBuilderWithSetter unsafeBuildObjWithPojoSettersOptBNotSet = new TestBuilderWithSetterBuilder()
      .setManyB(manyBTest)
      .setOneB(oneBTest)
      //.setOptB(optBTest)
      .setMyBool(false)
      .setMyInt(1)
      .unsafeBuild();
    Assertions.assertSame(unsafeBuildObjWithPojoSettersOptBNotSet.getManyB(), manyBTest);
    Assertions.assertSame(unsafeBuildObjWithPojoSettersOptBNotSet.getOneB(), oneBTest);
    Assertions.assertThrows(IllegalStateException.class, unsafeBuildObjWithPojoSettersOptBNotSet::getOptB);
    Assertions.assertEquals(1, Log.getFindings().size());
    Assertions.assertEquals("0xA7003x23650 get for OptB can't return a value. Attribute is empty.", Log.getFindings().get(0).getMsg());
    Log.clearFindings();
    Assertions.assertFalse(unsafeBuildObjWithPojoSettersOptBNotSet.isMyBool());
    Assertions.assertEquals(1, unsafeBuildObjWithPojoSettersOptBNotSet.getMyInt());

    TestBuilderWithoutSetter unsafeBuildObjWithoutPojoSettersOptBNotSet = new TestBuilderWithoutSetterBuilder()
      .setManyB(manyBTest)
      .setOneB(oneBTest)
      //.setOptB(optBTest)
      .setMyBool(false)
      .setMyInt(1)
      .unsafeBuild();
    Assertions.assertSame(unsafeBuildObjWithoutPojoSettersOptBNotSet.getManyB(), manyBTest);
    Assertions.assertSame(unsafeBuildObjWithoutPojoSettersOptBNotSet.getOneB(), oneBTest);
    Assertions.assertThrows(IllegalStateException.class, unsafeBuildObjWithoutPojoSettersOptBNotSet::getOptB);
    Assertions.assertEquals(1, Log.getFindings().size());
    Assertions.assertEquals("0xA7003x23651 get for OptB can't return a value. Attribute is empty.", Log.getFindings().get(0).getMsg());
    Log.clearFindings();
    Assertions.assertFalse(unsafeBuildObjWithoutPojoSettersOptBNotSet.isMyBool());
    Assertions.assertEquals(1, unsafeBuildObjWithoutPojoSettersOptBNotSet.getMyInt());
  }

  public void checkClassAndMethodExistence() throws Exception {
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
