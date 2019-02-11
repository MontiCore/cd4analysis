package de.monticore.cd.cocos.ebnf;

import de.monticore.cd.cocos.AbstractCoCoTest;
import de.monticore.umlcd4a.CD4ACoCos;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisCoCoChecker;
import de.monticore.umlcd4a.cocos.ebnf.AssociationNameUnique;
import de.monticore.umlcd4a.cocos.ebnf.AssociationNoConflictWithCardinalities;
import de.monticore.umlcd4a.cocos.ebnf.AssociationNoConflictWithDerivedCardinalities;
import de.monticore.umlcd4a.cocos.ebnf.AssociationRoleNameNoConflictWithOtherRoleNamesSpecMode;
import de.monticore.umlcd4a.cocos.others.AssociationNavigationHasCardinality;
import de.monticore.umlcd4a.cocos.others.ClassOnlyValidStereotype;
import de.monticore.umlcd4a.cocos.others.InterfaceOnlyValidStereotype;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;

public class Assoc2Test extends AbstractCoCoTest {
    private static String MODEL_PATH_INVALID = "src/test/resources/de/monticore/umlcd4a/cocos/ebnf/invalid/";

    private static String MODEL_PATH_VALID = "src/test/resources/de/monticore/umlcd4a/cocos/ebnf/valid/";

    @BeforeClass
    public static void init() {
        LogStub.init();
        Log.enableFailQuick(false);
    }

    @Before
    public void setUp() {
        Log.getFindings().clear();
    }

    @Test
    public void testEmbeddedMontiArc() {
        testModelNoErrors("src/test/resources/de/monticore/umlcd4a/cocos/EmbeddedMontiArc.cd");
    }

    @Override
    protected CD4AnalysisCoCoChecker getChecker() {
        return new CD4ACoCos().getCheckerForEbnfCoCos()
            .addCoCo(new AssociationNameUnique())
            .addCoCo(new AssociationNoConflictWithCardinalities())
            .addCoCo(new AssociationNoConflictWithDerivedCardinalities())
            .addCoCo(new AssociationNavigationHasCardinality())
            .addCoCo(new AssociationRoleNameNoConflictWithOtherRoleNamesSpecMode())
            .addCoCo(new ClassOnlyValidStereotype())
            .addCoCo(new InterfaceOnlyValidStereotype());
    }

    @Test
    public void testC4A32() {
        String modelName = "C4A32.cd";
        String errorCode = "0xC4A32";

        testModelNoErrors(MODEL_PATH_VALID + modelName);

        Collection<Finding> expectedErrors = Arrays.asList(
                Finding.error(errorCode + " The target cardinality (0 .. 1) of the inherited read-only association `CDAssociationSymbol foo/: A1() -> (foo)B1` is not a superset of the target cardinality (1 ..*) of the association `CDAssociationSymbol foo/: A2() -> (foo)B2`")
        );
        testModelForErrors(MODEL_PATH_INVALID + modelName,expectedErrors);

        Log.getFindings().clear();

        expectedErrors = Arrays.asList(
                Finding.error(errorCode + " The target cardinality (0 .. 1) of the inherited read-only association `CDAssociationSymbol foo/: A1() -> (foo)B1` is not a superset of the target cardinality (1 ..*) of the association `CDAssociationSymbol foo/: A3() -> (foo)B3`")
                );
        testModelForErrors(MODEL_PATH_INVALID + "C4A32_2.cd", expectedErrors);
    }

    @Test
    public void testC4A33() {
        String modelName = "C4A33.cd";
        String errorCode = "0xC4A33";

        testModelNoErrors(MODEL_PATH_VALID + modelName);

        Collection<Finding> expectedErrors = Arrays.asList(
                Finding.error(errorCode + " Association `association A2 -> (foo) B1  [1]  ;` has same target role name and source type extends source type of association `association A1 -> (foo) B1  [1]  ;`. So the \"inherited\" association `association A2 -> (foo) B1  [1]  ;` should be a derived association.")
        );
        testModelForErrors(MODEL_PATH_INVALID + modelName,expectedErrors);
    }

    @Test
    public void testC4A37() {
        String modelName = "C4A37.cd";
        String errorCode = "0xC4A37";

        // positive test is the same positive test as in Assoc2Test#testC4A33()

        Collection<Finding> expectedErrors = Arrays.asList(
                Finding.error(errorCode + " The target cardinality (0 .. 1) of the derived (inherited) association `association /A2 -> (foo) B1  [0..1]  ;` does not math the target cardinality (1 .. 1) of the association `association A1 -> (foo) B1  [1]  ;`")
        );
        testModelForErrors(MODEL_PATH_INVALID + modelName, expectedErrors);
    }

    @Test
    public void testC4A38() {
        String modelName = "C4A38.cd";
        String errorCode = "0xC4A38";

        testModelNoErrors("src/test/resources/de/monticore/umlcd4a/cocos/others/valid/" + modelName);

        Collection<Finding> expectedErrors = Arrays.asList(
                Finding.error(errorCode + " Association `association C <- A ;` has left navigation arrow (<-), but no left cardinality."),
                Finding.error(errorCode + " Association `association A -> D ;` has right navigation arrow (->), but no right cardinality."),
                Finding.error(errorCode + " Association `association B <-> D ;` has left navigation arrow (<-), but no left cardinality."),
                Finding.error(errorCode + " Association `association B <-> D ;` has right navigation arrow (->), but no right cardinality.")
        );

        testModelForErrors("src/test/resources/de/monticore/umlcd4a/cocos/others/invalid/" + modelName, expectedErrors);
    }

    /**
     * The corresponding CoCo is temporary disabled as the association name does not need to be unique within a model.
     * Instead, it must be unique within a specific class hierarchy.
     */
    @Test
    public void testC4A26() {
        String modelName = "C4A26.cd";
        String errorCode = "0xC4A26";

        testModelNoErrors(MODEL_PATH_VALID + modelName);
        testModelNoErrors(MODEL_PATH_VALID + "C4A26_2.cd"); // border-case

        Collection<Finding> expectedErrors = Arrays.asList(
                Finding.error(errorCode + " Association namespace clash `B::foo1` of associations `association foo1 B -> C  [1]  ;` and `association foo1 B -> A  [1]  ;`."),
                Finding.error(errorCode + " Association namespace clash `B::foo1` of associations `association foo1 B -> A  [1]  ;` and `association foo1 B -> C  [1]  ;`.")
        );
        testModelForErrors(MODEL_PATH_INVALID + "C4A26_2.cd", expectedErrors);

        Log.getFindings().clear();

        expectedErrors = Arrays.asList(
                Finding.error(errorCode + " Association namespace clash `B::foo` of associations `association foo A -> B  [1]  ;` and `association foo C -> B  [1]  ;`."),
                Finding.error(errorCode + " Association namespace clash `B::foo` of associations `association foo C -> B  [1]  ;` and `association foo A -> B  [1]  ;`.")
        );
        testModelForErrors(MODEL_PATH_INVALID + "C4A26_3.cd", expectedErrors);
    }

    @Test
    public void testC4A39() {
        String modelName = "C4A39.cd";
        String errorCode = "0xC4A39";

        testModelNoErrors(MODEL_PATH_VALID + modelName);

        Collection<Finding> expectedErrors = Arrays.asList(
                Finding.error(errorCode + " Role namespace clash `B1::foo` of associations `association A1 (foo) -> (bar) B1  [1]  ;` and `association A2 (foo) -> (bar2) B1  [1]  ;`."),
                Finding.error(errorCode + " Role namespace clash `B1::foo` of associations `association A2 (foo) -> (bar2) B1  [1]  ;` and `association A1 (foo) -> (bar) B1  [1]  ;`.")
        );
        testModelForErrors(MODEL_PATH_INVALID + modelName, expectedErrors);
    }
}
