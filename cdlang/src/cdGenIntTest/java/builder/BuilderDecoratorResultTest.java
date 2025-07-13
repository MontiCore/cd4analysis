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
 * Test the result of the Builder Decorator.
 */
public class BuilderDecoratorResultTest {
  
  @Test
  public void test() throws Exception {
    checkClassAndMethodExistence();
    
    //we need to disable the fail quick mode, otherwise the test will be skipped
    // Afterward we will test for error messages
    Log.enableFailQuick(false);
    
    testBuild();
    testUnsafeBuild();
    testConstructorModificationsAndCreations();
    Log.clearFindings();
  }
  
  @Test
  public void testBuild() {
    //we need to disable the fail quick mode, otherwise the test will be skipped
    // Afterward we will test for error messages
    Log.enableFailQuick(false);
    
    Set<B> manyBTest = Set.of(new B(), new B());
    B optBTest = new B();
    B oneBTest = new B();
    Level2class level2class = new Level2class();
    level2class.myInt = 1;
    TestEnum testEnum = TestEnum.ERROR;
    
    //build with all parameters set
    TestBuilderWithoutSetter objWithoutPojoSetters = new TestBuilderWithoutSetterBuilder().setManyB(
        manyBTest).setOneB(oneBTest).setOptB(optBTest).setMyBool(true).setMyInt(1).setMyLevel1(
            level2class).setMyTestEnum(testEnum).build();
    Assertions.assertSame(objWithoutPojoSetters.getManyB(), manyBTest);
    Assertions.assertSame(objWithoutPojoSetters.getOneB(), oneBTest);
    Assertions.assertSame(objWithoutPojoSetters.getOptB(), optBTest);
    Assertions.assertTrue(objWithoutPojoSetters.isMyBool());
    Assertions.assertSame(objWithoutPojoSetters.getMyLevel1(), level2class);
    Assertions.assertSame(objWithoutPojoSetters.getMyTestEnum(), testEnum);
    Assertions.assertEquals(1, objWithoutPojoSetters.getMyInt());
    
    TestBuilderWithSetter objWithPojoSetters = new TestBuilderWithSetterBuilder().setManyB(
        manyBTest).setOneB(oneBTest).setOptB(optBTest).setMyBool(true).setMyInt(1).setMyLevel1(
            level2class).setMyTestEnum(testEnum).build();
    Assertions.assertSame(objWithPojoSetters.getManyB(), manyBTest);
    Assertions.assertSame(objWithPojoSetters.getOneB(), oneBTest);
    Assertions.assertSame(objWithPojoSetters.getOptB(), optBTest);
    Assertions.assertTrue(objWithPojoSetters.isMyBool());
    Assertions.assertSame(objWithPojoSetters.getMyLevel1(), level2class);
    Assertions.assertSame(objWithPojoSetters.getMyTestEnum(), testEnum);
    Assertions.assertEquals(1, objWithPojoSetters.getMyInt());
    
    //build with ManyB set to null -> an empty list should be created
    TestBuilderWithSetter objWithPojoSettersManyBNull = new TestBuilderWithSetterBuilder().setManyB(
        null).setOneB(oneBTest).setOptB(optBTest).setMyBool(false).setMyInt(1).setMyLevel1(
            level2class).setMyTestEnum(testEnum).build();
    Assertions.assertEquals(0, objWithPojoSettersManyBNull.getManyB().size());
    Assertions.assertSame(objWithPojoSettersManyBNull.getOneB(), oneBTest);
    Assertions.assertSame(objWithPojoSettersManyBNull.getOptB(), optBTest);
    Assertions.assertFalse(objWithPojoSettersManyBNull.isMyBool());
    Assertions.assertEquals(1, objWithPojoSettersManyBNull.getMyInt());
    
    TestBuilderWithoutSetter objWithoutPojoSettersManyBNull = new TestBuilderWithoutSetterBuilder()
        .setManyB(null).setOneB(oneBTest).setOptB(optBTest).setMyBool(false).setMyInt(1)
        .setMyLevel1(level2class).setMyTestEnum(testEnum).build();
    Assertions.assertEquals(0, objWithoutPojoSettersManyBNull.getManyB().size());
    Assertions.assertSame(objWithoutPojoSettersManyBNull.getOneB(), oneBTest);
    Assertions.assertSame(objWithoutPojoSettersManyBNull.getOptB(), optBTest);
    Assertions.assertFalse(objWithoutPojoSettersManyBNull.isMyBool());
    Assertions.assertEquals(1, objWithoutPojoSettersManyBNull.getMyInt());
    
    //build with Opt set to null -> an error will occur
    TestBuilderWithSetter objWithPojoSettersOptNull = new TestBuilderWithSetterBuilder().setManyB(
        manyBTest).setOneB(oneBTest).setOptB(null).setMyBool(false).setMyInt(1).setMyLevel1(
            level2class).setMyTestEnum(testEnum).build();
    Log.clearFindings();
    Assertions.assertSame(objWithPojoSettersOptNull.getManyB(), manyBTest);
    Assertions.assertSame(objWithPojoSettersOptNull.getOneB(), oneBTest);
    Assertions.assertThrows(IllegalStateException.class, objWithPojoSettersOptNull::getOptB);
    Assertions.assertEquals(1, Log.getFindings().size());
    Assertions.assertTrue(Log.getFindings().get(0).getMsg().contains(
        "get for OptB can't return a value. Attribute is empty."));
    Log.clearFindings();
    Assertions.assertEquals(1, objWithPojoSettersOptNull.getMyInt());
    Assertions.assertFalse(objWithPojoSettersOptNull.isMyBool());
    
    TestBuilderWithoutSetter objWithoutPojoSettersOptNull = new TestBuilderWithoutSetterBuilder()
        .setManyB(manyBTest).setOneB(oneBTest).setOptB(null).setMyBool(false).setMyInt(1)
        .setMyLevel1(level2class).setMyTestEnum(testEnum).build();
    Log.clearFindings();
    Assertions.assertSame(objWithoutPojoSettersOptNull.getManyB(), manyBTest);
    Assertions.assertSame(objWithoutPojoSettersOptNull.getOneB(), oneBTest);
    Assertions.assertThrows(IllegalStateException.class, objWithoutPojoSettersOptNull::getOptB);
    Assertions.assertEquals(1, Log.getFindings().size());
    Assertions.assertTrue(Log.getFindings().get(0).getMsg().contains(
        "get for OptB can't return a value. Attribute is empty."));
    Log.clearFindings();
    Assertions.assertEquals(1, objWithoutPojoSettersOptNull.getMyInt());
    Assertions.assertFalse(objWithoutPojoSettersOptNull.isMyBool());
    
    //build with OneB set to null -> the build should not work
    Assertions.assertThrows(IllegalStateException.class, () -> new TestBuilderWithSetterBuilder()
        .setManyB(manyBTest).setOneB(null).setOptB(optBTest).setMyBool(false).setMyInt(1)
        .setMyLevel1(level2class).setMyTestEnum(testEnum).build());
    Assertions.assertSame(1, Log.getFindings().size());
    Assertions.assertTrue(Log.getFindings().get(0).getMsg().contains(
        "oneB of type TestBuilder.B must not be null"));
    Log.clearFindings();
    
    Assertions.assertThrows(IllegalStateException.class, () -> new TestBuilderWithoutSetterBuilder()
        .setManyB(manyBTest).setOneB(null).setOptB(optBTest).setMyBool(false).setMyInt(1)
        .setMyLevel1(level2class).setMyTestEnum(testEnum).build());
    Assertions.assertSame(1, Log.getFindings().size());
    Assertions.assertTrue(Log.getFindings().get(0).getMsg().contains(
        "oneB of type TestBuilder.B must not be null"));
    Log.clearFindings();
    
    //build with manyB not set -> an empty list should be created
    TestBuilderWithSetter objWithPojoSettersManyBNotSet = new TestBuilderWithSetterBuilder()
        //.setManyB(manyBTest)
        .setOneB(oneBTest).setOptB(optBTest).setMyBool(false).setMyInt(1).setMyLevel1(level2class)
        .setMyTestEnum(testEnum).build();
    Assertions.assertEquals(0, objWithPojoSettersManyBNotSet.getManyB().size());
    
    TestBuilderWithoutSetter objWithoutPojoSettersManyBNotSet =
        new TestBuilderWithoutSetterBuilder()
            //.setManyB(manyBTest)
            .setOneB(oneBTest).setOptB(optBTest).setMyBool(false).setMyInt(1).setMyLevel1(
                level2class).setMyTestEnum(testEnum).build();
    Assertions.assertEquals(0, objWithoutPojoSettersManyBNotSet.getManyB().size());
    
    //build with oneB not set -> an error will occur
    Assertions.assertThrows(IllegalStateException.class, () -> new TestBuilderWithSetterBuilder()
        .setManyB(manyBTest)
        //.setOneB(oneBTest)
        .setOptB(optBTest).setMyBool(false).setMyInt(1).setMyLevel1(level2class).setMyTestEnum(
            testEnum).build());
    Assertions.assertSame(1, Log.getFindings().size());
    Assertions.assertTrue(Log.getFindings().get(0).getMsg().contains(
        "oneB of type TestBuilder.B must not be null"));
    Log.clearFindings();
    
    Assertions.assertThrows(IllegalStateException.class, () -> new TestBuilderWithoutSetterBuilder()
        .setManyB(manyBTest)
        //.setOneB(oneBTest)
        .setOptB(optBTest).setMyBool(false).setMyInt(1).setMyLevel1(level2class).setMyTestEnum(
            testEnum).build());
    Assertions.assertSame(1, Log.getFindings().size());
    Assertions.assertTrue(Log.getFindings().get(0).getMsg().contains(
        "oneB of type TestBuilder.B must not be null"));
    Log.clearFindings();
    
    //build with optB not set
    TestBuilderWithSetter objWithPojoSettersOptBNotSet = new TestBuilderWithSetterBuilder()
        .setManyB(manyBTest).setOneB(oneBTest)
        //.setOptB(optBTest)
        .setMyBool(false).setMyInt(1).setMyLevel1(level2class).setMyTestEnum(testEnum).build();
    Assertions.assertSame(objWithPojoSettersOptBNotSet.getManyB(), manyBTest);
    Assertions.assertSame(objWithPojoSettersOptBNotSet.getOneB(), oneBTest);
    Assertions.assertThrows(IllegalStateException.class, objWithPojoSettersOptBNotSet::getOptB);
    Assertions.assertEquals(1, Log.getFindings().size());
    Assertions.assertTrue(Log.getFindings().get(0).getMsg().contains(
        "get for OptB can't return a value. Attribute is empty."));
    Log.clearFindings();
    Assertions.assertFalse(objWithPojoSettersOptBNotSet.isMyBool());
    Assertions.assertEquals(1, objWithPojoSettersOptBNotSet.getMyInt());
    
    TestBuilderWithoutSetter objWithoutPojoSettersOptBNotSet = new TestBuilderWithoutSetterBuilder()
        .setManyB(manyBTest).setOneB(oneBTest)
        //.setOptB(optBTest)
        .setMyBool(false).setMyInt(1).setMyLevel1(level2class).setMyTestEnum(testEnum).build();
    Assertions.assertSame(objWithoutPojoSettersOptBNotSet.getManyB(), manyBTest);
    Assertions.assertSame(objWithoutPojoSettersOptBNotSet.getOneB(), oneBTest);
    Assertions.assertThrows(IllegalStateException.class, objWithoutPojoSettersOptBNotSet::getOptB);
    Assertions.assertEquals(1, Log.getFindings().size());
    Assertions.assertTrue(Log.getFindings().get(0).getMsg().contains(
        "get for OptB can't return a value. Attribute is empty."));
    Log.clearFindings();
    Assertions.assertFalse(objWithoutPojoSettersOptBNotSet.isMyBool());
    Assertions.assertEquals(1, objWithoutPojoSettersOptBNotSet.getMyInt());
    
    //build with interface not set
    Assertions.assertThrows(IllegalStateException.class, () -> new TestBuilderWithSetterBuilder()
        .setManyB(manyBTest).setOneB(oneBTest).setOptB(optBTest).setMyBool(false).setMyInt(1)
        //.setMyLevel1(level2class)
        .setMyTestEnum(testEnum).build());
    Assertions.assertSame(1, Log.getFindings().size());
    Assertions.assertTrue(Log.getFindings().get(0).getMsg().contains(
        "myLevel1 of type Level1Interface must not be null"));
    Log.clearFindings();
    
    Assertions.assertThrows(IllegalStateException.class, () -> new TestBuilderWithoutSetterBuilder()
        .setManyB(manyBTest).setOneB(oneBTest).setOptB(optBTest).setMyBool(false).setMyInt(1)
        //.setMyLevel1(level2class)
        .setMyTestEnum(testEnum).build());
    Assertions.assertSame(1, Log.getFindings().size());
    Assertions.assertTrue(Log.getFindings().get(0).getMsg().contains(
        "myLevel1 of type Level1Interface must not be null"));
    Log.clearFindings();
    
    //build with enum not set
    Assertions.assertThrows(IllegalStateException.class, () -> new TestBuilderWithoutSetterBuilder()
        .setManyB(manyBTest).setOneB(oneBTest).setOptB(optBTest).setMyBool(false).setMyInt(1)
        .setMyLevel1(level2class)
        //.setMyTestEnum(testEnum)
        .build());
    Assertions.assertSame(1, Log.getFindings().size());
    Assertions.assertTrue(Log.getFindings().get(0).getMsg().contains(
        "myTestEnum of type TestEnum must not be null"));
    Log.clearFindings();
    
    Assertions.assertThrows(IllegalStateException.class, () -> new TestBuilderWithoutSetterBuilder()
        .setManyB(manyBTest).setOneB(oneBTest).setOptB(optBTest).setMyBool(false).setMyInt(1)
        .setMyLevel1(level2class)
        //.setMyTestEnum(testEnum)
        .build());
    Assertions.assertSame(1, Log.getFindings().size());
    Assertions.assertTrue(Log.getFindings().get(0).getMsg().contains(
        "myTestEnum of type TestEnum must not be null"));
    Log.clearFindings();
    
  }
  
  @Test
  public void testUnsafeBuild() {
    //we need to disable the fail quick mode, otherwise the test will be skipped
    // Afterward we will test for error messages
    Log.enableFailQuick(false);
    
    Set<B> manyBTest = Set.of(new B(), new B());
    B optBTest = new B();
    B oneBTest = new B();
    Level2class level2class = new Level2class();
    level2class.myInt = 1;
    TestEnum testEnum = TestEnum.ERROR;
    
    //unsafeBuild with all parameters set
    TestBuilderWithoutSetter unsafeBuildObjWithoutPojoSetters =
        new TestBuilderWithoutSetterBuilder().setManyB(manyBTest).setOneB(oneBTest).setOptB(
            optBTest).setMyBool(true).setMyInt(1).setMyLevel1(level2class).setMyTestEnum(testEnum)
            .unsafeBuild();
    Assertions.assertSame(unsafeBuildObjWithoutPojoSetters.getManyB(), manyBTest);
    Assertions.assertSame(unsafeBuildObjWithoutPojoSetters.getOneB(), oneBTest);
    Assertions.assertSame(unsafeBuildObjWithoutPojoSetters.getOptB(), optBTest);
    Assertions.assertTrue(unsafeBuildObjWithoutPojoSetters.isMyBool());
    Assertions.assertEquals(1, unsafeBuildObjWithoutPojoSetters.getMyInt());
    Assertions.assertSame(unsafeBuildObjWithoutPojoSetters.getMyLevel1(), level2class);
    Assertions.assertSame(unsafeBuildObjWithoutPojoSetters.getMyTestEnum(), testEnum);
    
    TestBuilderWithSetter unsafeBuildObjWithPojoSetters = new TestBuilderWithSetterBuilder()
        .setManyB(manyBTest).setOneB(oneBTest).setOptB(optBTest).setMyBool(true).setMyInt(1)
        .setMyLevel1(level2class).setMyTestEnum(testEnum).unsafeBuild();
    Assertions.assertSame(unsafeBuildObjWithPojoSetters.getManyB(), manyBTest);
    Assertions.assertSame(unsafeBuildObjWithPojoSetters.getOneB(), oneBTest);
    Assertions.assertSame(unsafeBuildObjWithPojoSetters.getOptB(), optBTest);
    Assertions.assertTrue(unsafeBuildObjWithPojoSetters.isMyBool());
    Assertions.assertEquals(1, unsafeBuildObjWithPojoSetters.getMyInt());
    Assertions.assertSame(unsafeBuildObjWithPojoSetters.getMyLevel1(), level2class);
    Assertions.assertSame(unsafeBuildObjWithPojoSetters.getMyTestEnum(), testEnum);
    
    //unsafeBuild with ManyB set to null -> the list is set to an empty set
    TestBuilderWithSetter unsafeBuildObjWithPojoSettersManyBNull =
        new TestBuilderWithSetterBuilder().setManyB(null).setOneB(oneBTest).setOptB(optBTest)
            .setMyBool(false).setMyInt(1).setMyLevel1(level2class).setMyTestEnum(testEnum)
            .unsafeBuild();
    Assertions.assertEquals(0, unsafeBuildObjWithPojoSettersManyBNull.getManyB().size());
    Assertions.assertSame(unsafeBuildObjWithPojoSettersManyBNull.getOneB(), oneBTest);
    Assertions.assertSame(unsafeBuildObjWithPojoSettersManyBNull.getOptB(), optBTest);
    Assertions.assertFalse(unsafeBuildObjWithPojoSettersManyBNull.isMyBool());
    Assertions.assertEquals(1, unsafeBuildObjWithPojoSettersManyBNull.getMyInt());
    Assertions.assertSame(unsafeBuildObjWithPojoSettersManyBNull.getMyLevel1(), level2class);
    Assertions.assertSame(unsafeBuildObjWithPojoSettersManyBNull.getMyTestEnum(), testEnum);
    
    TestBuilderWithoutSetter unsafeBuildObjWithoutPojoSettersManyBNull =
        new TestBuilderWithoutSetterBuilder().setManyB(null).setOneB(oneBTest).setOptB(optBTest)
            .setMyBool(false).setMyInt(1).setMyLevel1(level2class).setMyTestEnum(testEnum)
            .unsafeBuild();
    Assertions.assertEquals(0, unsafeBuildObjWithoutPojoSettersManyBNull.getManyB().size());
    Assertions.assertSame(unsafeBuildObjWithoutPojoSettersManyBNull.getOneB(), oneBTest);
    Assertions.assertSame(unsafeBuildObjWithoutPojoSettersManyBNull.getOptB(), optBTest);
    Assertions.assertFalse(unsafeBuildObjWithoutPojoSettersManyBNull.isMyBool());
    Assertions.assertEquals(1, unsafeBuildObjWithoutPojoSettersManyBNull.getMyInt());
    Assertions.assertSame(unsafeBuildObjWithoutPojoSettersManyBNull.getMyLevel1(), level2class);
    Assertions.assertSame(unsafeBuildObjWithoutPojoSettersManyBNull.getMyTestEnum(), testEnum);
    
    //unsafeBuild with Opt set to null -> an error will occur
    TestBuilderWithSetter unsafeBuildObjWithPojoSettersOptNull = new TestBuilderWithSetterBuilder()
        .setManyB(manyBTest).setOneB(oneBTest).setOptB(null).setMyBool(false).setMyInt(1)
        .setMyLevel1(level2class).setMyTestEnum(testEnum).unsafeBuild();
    Log.clearFindings();
    Assertions.assertSame(unsafeBuildObjWithPojoSettersOptNull.getManyB(), manyBTest);
    Assertions.assertSame(unsafeBuildObjWithPojoSettersOptNull.getOneB(), oneBTest);
    Assertions.assertThrows(IllegalStateException.class,
        unsafeBuildObjWithPojoSettersOptNull::getOptB);
    Assertions.assertEquals(1, Log.getFindings().size());
    Assertions.assertTrue(Log.getFindings().get(0).getMsg().contains(
        "get for OptB can't return a value. Attribute is empty."));
    Log.clearFindings();
    Assertions.assertEquals(1, unsafeBuildObjWithPojoSettersOptNull.getMyInt());
    Assertions.assertFalse(unsafeBuildObjWithPojoSettersOptNull.isMyBool());
    Assertions.assertSame(unsafeBuildObjWithPojoSettersOptNull.getMyLevel1(), level2class);
    Assertions.assertSame(unsafeBuildObjWithPojoSettersOptNull.getMyTestEnum(), testEnum);
    
    TestBuilderWithoutSetter unsafeBuildObjWithoutPojoSettersOptNull =
        new TestBuilderWithoutSetterBuilder().setManyB(manyBTest).setOneB(oneBTest).setOptB(null)
            .setMyBool(false).setMyInt(1).setMyLevel1(level2class).setMyTestEnum(testEnum)
            .unsafeBuild();
    Log.clearFindings();
    Assertions.assertSame(unsafeBuildObjWithoutPojoSettersOptNull.getManyB(), manyBTest);
    Assertions.assertSame(unsafeBuildObjWithoutPojoSettersOptNull.getOneB(), oneBTest);
    Assertions.assertThrows(IllegalStateException.class,
        unsafeBuildObjWithoutPojoSettersOptNull::getOptB);
    Assertions.assertEquals(1, Log.getFindings().size());
    Assertions.assertTrue(Log.getFindings().get(0).getMsg().contains(
        "get for OptB can't return a value. Attribute is empty."));
    Log.clearFindings();
    Assertions.assertEquals(1, unsafeBuildObjWithoutPojoSettersOptNull.getMyInt());
    Assertions.assertFalse(unsafeBuildObjWithoutPojoSettersOptNull.isMyBool());
    Assertions.assertSame(unsafeBuildObjWithoutPojoSettersOptNull.getMyLevel1(), level2class);
    Assertions.assertSame(unsafeBuildObjWithoutPojoSettersOptNull.getMyTestEnum(), testEnum);
    
    //unsafeBuild with OneB set to null -> set it to null
    TestBuilderWithSetter unsafeBuildObjWithPojoSetterOneBNull = new TestBuilderWithSetterBuilder()
        .setManyB(manyBTest).setOneB(null).setOptB(optBTest).setMyBool(false).setMyInt(1)
        .setMyLevel1(level2class).setMyTestEnum(testEnum).unsafeBuild();
    Assertions.assertNull(unsafeBuildObjWithPojoSetterOneBNull.getOneB());
    Assertions.assertSame(unsafeBuildObjWithPojoSetterOneBNull.getManyB(), manyBTest);
    Assertions.assertSame(unsafeBuildObjWithPojoSetterOneBNull.getOptB(), optBTest);
    Assertions.assertFalse(unsafeBuildObjWithPojoSetterOneBNull.isMyBool());
    Assertions.assertEquals(1, unsafeBuildObjWithPojoSetterOneBNull.getMyInt());
    Assertions.assertSame(unsafeBuildObjWithPojoSetterOneBNull.getMyLevel1(), level2class);
    Assertions.assertSame(unsafeBuildObjWithPojoSetterOneBNull.getMyTestEnum(), testEnum);
    
    TestBuilderWithoutSetter unsafeBuildObjWithoutPojoSetterOneBNull =
        new TestBuilderWithoutSetterBuilder().setManyB(manyBTest).setOneB(null).setOptB(optBTest)
            .setMyBool(false).setMyInt(1).setMyLevel1(level2class).setMyTestEnum(testEnum)
            .unsafeBuild();
    Assertions.assertNull(unsafeBuildObjWithoutPojoSetterOneBNull.getOneB());
    Assertions.assertSame(unsafeBuildObjWithoutPojoSetterOneBNull.getManyB(), manyBTest);
    Assertions.assertSame(unsafeBuildObjWithoutPojoSetterOneBNull.getOptB(), optBTest);
    Assertions.assertFalse(unsafeBuildObjWithoutPojoSetterOneBNull.isMyBool());
    Assertions.assertEquals(1, unsafeBuildObjWithoutPojoSetterOneBNull.getMyInt());
    Assertions.assertSame(unsafeBuildObjWithoutPojoSetterOneBNull.getMyLevel1(), level2class);
    Assertions.assertSame(unsafeBuildObjWithoutPojoSetterOneBNull.getMyTestEnum(), testEnum);
    
    //unsafeBuild with manyB not set -> set to an empty set
    TestBuilderWithSetter unsafeBuildObjWithPojoSettersManyBNotSet =
        new TestBuilderWithSetterBuilder()
            //.setManyB(manyBTest)
            .setOneB(oneBTest).setOptB(optBTest).setMyBool(false).setMyInt(1).setMyLevel1(
                level2class).setMyTestEnum(testEnum).unsafeBuild();
    Assertions.assertEquals(0, unsafeBuildObjWithPojoSettersManyBNotSet.getManyB().size());
    Assertions.assertSame(unsafeBuildObjWithPojoSettersManyBNotSet.getOneB(), oneBTest);
    Assertions.assertSame(unsafeBuildObjWithPojoSettersManyBNotSet.getOptB(), optBTest);
    Assertions.assertFalse(unsafeBuildObjWithPojoSettersManyBNotSet.isMyBool());
    Assertions.assertEquals(1, unsafeBuildObjWithPojoSettersManyBNotSet.getMyInt());
    Assertions.assertSame(unsafeBuildObjWithPojoSettersManyBNotSet.getMyLevel1(), level2class);
    Assertions.assertSame(unsafeBuildObjWithPojoSettersManyBNotSet.getMyTestEnum(), testEnum);
    
    TestBuilderWithoutSetter unsafeBuildObjWithoutPojoSettersManyBNotSet =
        new TestBuilderWithoutSetterBuilder()
            //.setManyB(manyBTest)
            .setOneB(oneBTest).setOptB(optBTest).setMyBool(false).setMyInt(1).setMyLevel1(
                level2class).setMyTestEnum(testEnum).unsafeBuild();
    Assertions.assertEquals(0, unsafeBuildObjWithoutPojoSettersManyBNotSet.getManyB().size());
    Assertions.assertSame(unsafeBuildObjWithoutPojoSettersManyBNotSet.getOneB(), oneBTest);
    Assertions.assertSame(unsafeBuildObjWithoutPojoSettersManyBNotSet.getOptB(), optBTest);
    Assertions.assertFalse(unsafeBuildObjWithoutPojoSettersManyBNotSet.isMyBool());
    Assertions.assertEquals(1, unsafeBuildObjWithoutPojoSettersManyBNotSet.getMyInt());
    Assertions.assertSame(unsafeBuildObjWithoutPojoSettersManyBNotSet.getMyLevel1(), level2class);
    Assertions.assertSame(unsafeBuildObjWithoutPojoSettersManyBNotSet.getMyTestEnum(), testEnum);
    
    //unsafeBuild with oneB not set -> set to an empty set
    TestBuilderWithSetter unsafeBuildObjWithPojoSettersOneBNotSet =
        new TestBuilderWithSetterBuilder().setManyB(manyBTest)
            //.setOneB(oneBTest)
            .setOptB(optBTest).setMyBool(false).setMyInt(1).setMyLevel1(level2class).setMyTestEnum(
                testEnum).unsafeBuild();
    Assertions.assertNull(unsafeBuildObjWithPojoSettersOneBNotSet.getOneB());
    Assertions.assertSame(unsafeBuildObjWithPojoSettersOneBNotSet.getManyB(), manyBTest);
    Assertions.assertSame(unsafeBuildObjWithPojoSettersOneBNotSet.getOptB(), optBTest);
    Assertions.assertFalse(unsafeBuildObjWithPojoSettersOneBNotSet.isMyBool());
    Assertions.assertEquals(1, unsafeBuildObjWithPojoSettersOneBNotSet.getMyInt());
    Assertions.assertSame(unsafeBuildObjWithPojoSettersOneBNotSet.getMyLevel1(), level2class);
    Assertions.assertSame(unsafeBuildObjWithPojoSettersOneBNotSet.getMyTestEnum(), testEnum);
    
    TestBuilderWithoutSetter unsafeBuildObjWithoutPojoSettersOneBNotSet =
        new TestBuilderWithoutSetterBuilder().setManyB(manyBTest)
            //.setOneB(oneBTest)
            .setOptB(optBTest).setMyBool(false).setMyInt(1).setMyLevel1(level2class).setMyTestEnum(
                testEnum).unsafeBuild();
    Assertions.assertNull(unsafeBuildObjWithoutPojoSettersOneBNotSet.getOneB());
    Assertions.assertSame(unsafeBuildObjWithoutPojoSettersOneBNotSet.getManyB(), manyBTest);
    Assertions.assertSame(unsafeBuildObjWithoutPojoSettersOneBNotSet.getOptB(), optBTest);
    Assertions.assertFalse(unsafeBuildObjWithoutPojoSettersOneBNotSet.isMyBool());
    Assertions.assertEquals(1, unsafeBuildObjWithoutPojoSettersOneBNotSet.getMyInt());
    Assertions.assertSame(unsafeBuildObjWithoutPojoSettersOneBNotSet.getMyLevel1(), level2class);
    Assertions.assertSame(unsafeBuildObjWithoutPojoSettersOneBNotSet.getMyTestEnum(), testEnum);
    
    //unsafeBuild with optB not set
    TestBuilderWithSetter unsafeBuildObjWithPojoSettersOptBNotSet =
        new TestBuilderWithSetterBuilder().setManyB(manyBTest).setOneB(oneBTest)
            //.setOptB(optBTest)
            .setMyBool(false).setMyInt(1).setMyLevel1(level2class).setMyTestEnum(testEnum)
            .unsafeBuild();
    Assertions.assertSame(unsafeBuildObjWithPojoSettersOptBNotSet.getManyB(), manyBTest);
    Assertions.assertSame(unsafeBuildObjWithPojoSettersOptBNotSet.getOneB(), oneBTest);
    Assertions.assertThrows(IllegalStateException.class,
        unsafeBuildObjWithPojoSettersOptBNotSet::getOptB);
    Assertions.assertEquals(1, Log.getFindings().size());
    Assertions.assertTrue(Log.getFindings().get(0).getMsg().contains(
        "get for OptB can't return a value. Attribute is empty."));
    Log.clearFindings();
    Assertions.assertFalse(unsafeBuildObjWithPojoSettersOptBNotSet.isMyBool());
    Assertions.assertEquals(1, unsafeBuildObjWithPojoSettersOptBNotSet.getMyInt());
    Assertions.assertSame(unsafeBuildObjWithPojoSettersOptBNotSet.getMyLevel1(), level2class);
    Assertions.assertSame(unsafeBuildObjWithPojoSettersOptBNotSet.getMyTestEnum(), testEnum);
    
    TestBuilderWithoutSetter unsafeBuildObjWithoutPojoSettersOptBNotSet =
        new TestBuilderWithoutSetterBuilder().setManyB(manyBTest).setOneB(oneBTest)
            //.setOptB(optBTest)
            .setMyBool(false).setMyInt(1).setMyLevel1(level2class).setMyTestEnum(testEnum)
            .unsafeBuild();
    Assertions.assertSame(unsafeBuildObjWithoutPojoSettersOptBNotSet.getManyB(), manyBTest);
    Assertions.assertSame(unsafeBuildObjWithoutPojoSettersOptBNotSet.getOneB(), oneBTest);
    Assertions.assertThrows(IllegalStateException.class,
        unsafeBuildObjWithoutPojoSettersOptBNotSet::getOptB);
    Assertions.assertEquals(1, Log.getFindings().size());
    Assertions.assertTrue(Log.getFindings().get(0).getMsg().contains(
        "get for OptB can't return a value. Attribute is empty."));
    Log.clearFindings();
    Assertions.assertFalse(unsafeBuildObjWithoutPojoSettersOptBNotSet.isMyBool());
    Assertions.assertEquals(1, unsafeBuildObjWithoutPojoSettersOptBNotSet.getMyInt());
    Assertions.assertSame(unsafeBuildObjWithoutPojoSettersOptBNotSet.getMyLevel1(), level2class);
    Assertions.assertSame(unsafeBuildObjWithoutPojoSettersOptBNotSet.getMyTestEnum(), testEnum);
    
    //unsafeBuild with interface not set
    TestBuilderWithSetter unsafeBuildObjWithPojoSetterMyLevel1NotSet =
        new TestBuilderWithSetterBuilder().setManyB(manyBTest).setOneB(oneBTest).setOptB(optBTest)
            .setMyBool(false).setMyInt(1)
            //.setMyLevel1(level2class)
            .setMyTestEnum(testEnum).unsafeBuild();
    Assertions.assertSame(unsafeBuildObjWithPojoSetterMyLevel1NotSet.getManyB(), manyBTest);
    Assertions.assertSame(unsafeBuildObjWithPojoSetterMyLevel1NotSet.getOneB(), oneBTest);
    Assertions.assertSame(unsafeBuildObjWithPojoSetterMyLevel1NotSet.getOneB(), oneBTest);
    Assertions.assertFalse(unsafeBuildObjWithPojoSetterMyLevel1NotSet.isMyBool());
    Assertions.assertEquals(1, unsafeBuildObjWithPojoSetterMyLevel1NotSet.getMyInt());
    Assertions.assertNull(unsafeBuildObjWithPojoSetterMyLevel1NotSet.getMyLevel1());
    Assertions.assertEquals(unsafeBuildObjWithPojoSetterMyLevel1NotSet.getMyTestEnum(), testEnum);
    
    TestBuilderWithoutSetter unsafeBuildObjWithoutPojoSetterMyLevel1NotSet =
        new TestBuilderWithoutSetterBuilder().setManyB(manyBTest).setOneB(oneBTest).setOptB(
            optBTest).setMyBool(false).setMyInt(1)
            //.setMyLevel1(level2class)
            .setMyTestEnum(testEnum).unsafeBuild();
    Assertions.assertSame(unsafeBuildObjWithoutPojoSetterMyLevel1NotSet.getManyB(), manyBTest);
    Assertions.assertSame(unsafeBuildObjWithoutPojoSetterMyLevel1NotSet.getOneB(), oneBTest);
    Assertions.assertSame(unsafeBuildObjWithoutPojoSetterMyLevel1NotSet.getOneB(), oneBTest);
    Assertions.assertFalse(unsafeBuildObjWithoutPojoSetterMyLevel1NotSet.isMyBool());
    Assertions.assertEquals(1, unsafeBuildObjWithoutPojoSetterMyLevel1NotSet.getMyInt());
    Assertions.assertNull(unsafeBuildObjWithoutPojoSetterMyLevel1NotSet.getMyLevel1());
    Assertions.assertEquals(unsafeBuildObjWithoutPojoSetterMyLevel1NotSet.getMyTestEnum(),
        testEnum);
    
    //unsafeBuild with enum not set
    TestBuilderWithSetter unsafeBuildObjWithPojoSettersMyTestEnumNotSet =
        new TestBuilderWithSetterBuilder().setManyB(manyBTest).setOneB(oneBTest).setOptB(optBTest)
            .setMyBool(false).setMyInt(1).setMyLevel1(level2class)
            //.setMyTestEnum(testEnum)
            .unsafeBuild();
    Assertions.assertSame(unsafeBuildObjWithPojoSettersMyTestEnumNotSet.getManyB(), manyBTest);
    Assertions.assertSame(unsafeBuildObjWithPojoSettersMyTestEnumNotSet.getOneB(), oneBTest);
    Assertions.assertSame(unsafeBuildObjWithPojoSettersMyTestEnumNotSet.getOptB(), optBTest);
    Assertions.assertFalse(unsafeBuildObjWithPojoSettersMyTestEnumNotSet.isMyBool());
    Assertions.assertEquals(1, unsafeBuildObjWithPojoSettersMyTestEnumNotSet.getMyInt());
    Assertions.assertSame(unsafeBuildObjWithPojoSettersMyTestEnumNotSet.getMyLevel1(), level2class);
    Assertions.assertNull(unsafeBuildObjWithPojoSettersMyTestEnumNotSet.getMyTestEnum());
    
    TestBuilderWithoutSetter unsafeBuildObjWithoutPojoSettersMyTestEnumNotSet =
        new TestBuilderWithoutSetterBuilder().setManyB(manyBTest).setOneB(oneBTest).setOptB(
            optBTest).setMyBool(false).setMyInt(1).setMyLevel1(level2class)
            //.setMyTestEnum(testEnum)
            .unsafeBuild();
    Assertions.assertSame(unsafeBuildObjWithoutPojoSettersMyTestEnumNotSet.getManyB(), manyBTest);
    Assertions.assertSame(unsafeBuildObjWithoutPojoSettersMyTestEnumNotSet.getOneB(), oneBTest);
    Assertions.assertSame(unsafeBuildObjWithoutPojoSettersMyTestEnumNotSet.getOptB(), optBTest);
    Assertions.assertFalse(unsafeBuildObjWithoutPojoSettersMyTestEnumNotSet.isMyBool());
    Assertions.assertEquals(1, unsafeBuildObjWithoutPojoSettersMyTestEnumNotSet.getMyInt());
    Assertions.assertSame(unsafeBuildObjWithoutPojoSettersMyTestEnumNotSet.getMyLevel1(),
        level2class);
    Assertions.assertNull(unsafeBuildObjWithoutPojoSettersMyTestEnumNotSet.getMyTestEnum());
    
    //unsafeBuild with no arguments
    TestBuilderWithoutSetter unsafeBuildEmpty = new TestBuilderWithoutSetterBuilder().unsafeBuild();
    Assertions.assertSame(0, unsafeBuildEmpty.getManyB().size());
    Assertions.assertSame(0, Log.getFindings().size());
  }
  
  @Test
  public void testConstructorModificationsAndCreations() {
    PrivateDefaultConstructor privateDefaultConstructor = new PrivateDefaultConstructorBuilder()
        .unsafeBuild();
    NoDefaultConstructor noDefaultConstructorBuilder = new NoDefaultConstructorBuilder()
        .unsafeBuild();
    
  }
  
  @Test
  public void checkClassAndMethodExistence() throws Exception {
    //constructor methods
    Constructor<TestBuilderWithSetterBuilder> constructorWithSetter =
        TestBuilderWithSetterBuilder.class.getDeclaredConstructor();
    BigInteger constructorModifier = BigInteger.valueOf(constructorWithSetter.getModifiers());
    Assertions.assertEquals(Modifier.PUBLIC, constructorModifier.intValue());
    
    Constructor<TestBuilderWithoutSetterBuilder> constructorWithoutSetter =
        TestBuilderWithoutSetterBuilder.class.getDeclaredConstructor();
    BigInteger constructorWithoutSetterModifier = BigInteger.valueOf(constructorWithoutSetter
        .getModifiers());
    Assertions.assertEquals(Modifier.PUBLIC, constructorWithoutSetterModifier.intValue());
    
    //build methods
    Method buildWithSetter = TestBuilderWithSetterBuilderTOP.class.getDeclaredMethod("build");
    BigInteger modifier = BigInteger.valueOf(buildWithSetter.getModifiers());
    Assertions.assertEquals(Modifier.PUBLIC, modifier.intValue());
    
    Method buildWithoutSetter = TestBuilderWithoutSetterBuilder.class.getDeclaredMethod("build");
    BigInteger modifierWithoutSetter = BigInteger.valueOf(buildWithoutSetter.getModifiers());
    Assertions.assertEquals(Modifier.PUBLIC, modifierWithoutSetter.intValue());
    
    //unsafeBuild methods
    Method unsafeBuildWithSetter = TestBuilderWithSetterBuilderTOP.class.getDeclaredMethod(
        "unsafeBuild");
    BigInteger unsafeModifier = BigInteger.valueOf(unsafeBuildWithSetter.getModifiers());
    Assertions.assertEquals(Modifier.PUBLIC, unsafeModifier.intValue());
    
    Method unsafeBuildWithoutSetter = TestBuilderWithoutSetterBuilder.class.getDeclaredMethod(
        "unsafeBuild");
    BigInteger unsafeModifierWithoutSetter = BigInteger.valueOf(unsafeBuildWithoutSetter
        .getModifiers());
    Assertions.assertEquals(Modifier.PUBLIC, unsafeModifierWithoutSetter.intValue());
    
    //isValid methods
    Method isValidWithSetter = TestBuilderWithSetterBuilderTOP.class.getDeclaredMethod("isValid");
    BigInteger isValidModifier = BigInteger.valueOf(isValidWithSetter.getModifiers());
    Assertions.assertEquals(Modifier.PRIVATE, isValidModifier.intValue());
    
    Method isValidWithoutSetter = TestBuilderWithoutSetterBuilder.class.getDeclaredMethod(
        "isValid");
    BigInteger isValidModifierWithoutSetter = BigInteger.valueOf(isValidWithoutSetter
        .getModifiers());
    Assertions.assertEquals(Modifier.PRIVATE, isValidModifierWithoutSetter.intValue());
    
    //set methods
    Method setManyBWithSetter = TestBuilderWithSetterBuilderTOP.class.getDeclaredMethod("setManyB",
        Set.class);
    Assertions.assertEquals(Modifier.PUBLIC, setManyBWithSetter.getModifiers());
    
    Method setManyBWithoutSetter = TestBuilderWithoutSetterBuilder.class.getDeclaredMethod(
        "setManyB", Set.class);
    Assertions.assertEquals(Modifier.PUBLIC, setManyBWithoutSetter.getModifiers());
    
    Method setOptBWithSetter = TestBuilderWithSetterBuilderTOP.class.getDeclaredMethod("setOptB",
        B.class);
    Assertions.assertEquals(Modifier.PUBLIC, setOptBWithSetter.getModifiers());
    
    Method setOptBWithoutSetter = TestBuilderWithoutSetterBuilder.class.getDeclaredMethod("setOptB",
        B.class);
    Assertions.assertEquals(Modifier.PUBLIC, setOptBWithoutSetter.getModifiers());
    
    Method setOneBWithSetter = TestBuilderWithSetterBuilderTOP.class.getDeclaredMethod("setOneB",
        B.class);
    Assertions.assertEquals(Modifier.PUBLIC, setOneBWithSetter.getModifiers());
    
    Method setOneBWithoutSetter = TestBuilderWithoutSetterBuilder.class.getDeclaredMethod("setOneB",
        B.class);
    Assertions.assertEquals(Modifier.PUBLIC, setOneBWithoutSetter.getModifiers());
    
    Method setMyIntWithSetter = TestBuilderWithSetterBuilderTOP.class.getDeclaredMethod("setMyInt",
        int.class);
    Assertions.assertEquals(Modifier.PUBLIC, setMyIntWithSetter.getModifiers());
    
    Method setMyIntWithoutSetter = TestBuilderWithoutSetterBuilder.class.getDeclaredMethod(
        "setMyInt", int.class);
    Assertions.assertEquals(Modifier.PUBLIC, setMyIntWithoutSetter.getModifiers());
    
    Method setMyBoolWithSetter = TestBuilderWithSetterBuilderTOP.class.getDeclaredMethod(
        "setMyBool", boolean.class);
    Assertions.assertEquals(Modifier.PUBLIC, setMyBoolWithSetter.getModifiers());
    
    Method setMyBoolWithoutSetter = TestBuilderWithoutSetterBuilder.class.getDeclaredMethod(
        "setMyBool", boolean.class);
    
    Method setMyLevel1WithSetter = TestBuilderWithSetterBuilderTOP.class.getDeclaredMethod(
        "setMyLevel1", Level1Interface.class);
    Assertions.assertEquals(Modifier.PUBLIC, setMyLevel1WithSetter.getModifiers());
    
    Method setMyLevel1WithoutSetter = TestBuilderWithoutSetterBuilder.class.getDeclaredMethod(
        "setMyLevel1", Level1Interface.class);
    Assertions.assertEquals(Modifier.PUBLIC, setMyLevel1WithoutSetter.getModifiers());
    
    Method setMyTestEnumWithSetter = TestBuilderWithSetterBuilderTOP.class.getDeclaredMethod(
        "setMyTestEnum", TestEnum.class);
    Assertions.assertEquals(Modifier.PUBLIC, setMyTestEnumWithSetter.getModifiers());
    
    Method setMyTestEnumWithoutSetter = TestBuilderWithoutSetterBuilder.class.getDeclaredMethod(
        "setMyTestEnum", TestEnum.class);
    Assertions.assertEquals(Modifier.PUBLIC, setMyTestEnumWithoutSetter.getModifiers());
    
    //setAbsent methods
    Method setManyBAbsentWithSetter = TestBuilderWithSetterBuilderTOP.class.getDeclaredMethod(
        "setManyBAbsent");
    Assertions.assertEquals(Modifier.PUBLIC, setManyBAbsentWithSetter.getModifiers());
    
    Method setManyBAbsentWithoutSetter = TestBuilderWithoutSetterBuilder.class.getDeclaredMethod(
        "setManyBAbsent");
    Assertions.assertEquals(Modifier.PUBLIC, setManyBAbsentWithoutSetter.getModifiers());
    
    Method setOptBAbsentWithSetter = TestBuilderWithSetterBuilderTOP.class.getDeclaredMethod(
        "setOptBAbsent");
    Assertions.assertEquals(Modifier.PUBLIC, setOptBAbsentWithSetter.getModifiers());
    
    Method setOptBAbsentWithoutSetter = TestBuilderWithoutSetterBuilder.class.getDeclaredMethod(
        "setOptBAbsent");
    Assertions.assertEquals(Modifier.PUBLIC, setOptBAbsentWithoutSetter.getModifiers());
    
    //setAbsent should not exist for cardinality of 1
    Assertions.assertThrows(NoSuchMethodException.class, () -> TestBuilderWithSetterBuilder.class
        .getDeclaredMethod("setOneBAbsent"));
    Assertions.assertThrows(NoSuchMethodException.class, () -> TestBuilderWithoutSetterBuilder.class
        .getDeclaredMethod("setOneBAbsent"));
    Assertions.assertThrows(NoSuchMethodException.class, () -> TestBuilderWithSetterBuilder.class
        .getDeclaredMethod("setMyIntAbsent"));
    Assertions.assertThrows(NoSuchMethodException.class, () -> TestBuilderWithoutSetterBuilder.class
        .getDeclaredMethod("setMyIntAbsent"));
    Assertions.assertThrows(NoSuchMethodException.class, () -> TestBuilderWithSetterBuilder.class
        .getDeclaredMethod("setMyBoolAbsent"));
    Assertions.assertThrows(NoSuchMethodException.class, () -> TestBuilderWithoutSetterBuilder.class
        .getDeclaredMethod("setMyBoolAbsent"));
    Assertions.assertThrows(NoSuchMethodException.class, () -> TestBuilderWithSetterBuilder.class
        .getDeclaredMethod("setMyLevel1Absent"));
    Assertions.assertThrows(NoSuchMethodException.class, () -> TestBuilderWithoutSetterBuilder.class
        .getDeclaredMethod("setMyLevel1Absent"));
    Assertions.assertThrows(NoSuchMethodException.class, () -> TestBuilderWithSetterBuilder.class
        .getDeclaredMethod("setMyTestEnumAbsent"));
    Assertions.assertThrows(NoSuchMethodException.class, () -> TestBuilderWithoutSetterBuilder.class
        .getDeclaredMethod("setMyTestEnumAbsent"));
  }
  
}
