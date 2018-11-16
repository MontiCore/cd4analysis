package de.monticore.umlcd4a.cocos.ebnf;

import de.monticore.umlcd4a.CD4ACoCos;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisCoCoChecker;
import de.monticore.umlcd4a.cocos.AbstractCoCoTest;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
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
        Log.enableFailQuick(false);
    }

    @Before
    public void setUp() {
        Log.getFindings().clear();
    }

    @Test
    public void testRoleNameUniqueReadOnly() {
        testModelNoErrors("src/test/resources/de/monticore/umlcd4a/cocos/TypeAssoc.cd");
    }

    @Override
    protected CD4AnalysisCoCoChecker getChecker() {
        return new CD4ACoCos().getCheckerForEbnfCoCos()
                .addCoCo(new AssociationNameUnique())
                .addCoCo(new AssociationNoConflictWithCardinalities())
                .addCoCo(new AssociationNoConflictWithDerivedCardinalities());
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
}
