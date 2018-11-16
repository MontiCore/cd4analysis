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
                .addCoCo(new AssociationNoConflictWithCardinalities());
    }

    @Test
    public void testAssocNameUniqueReadOnly() {
        String modelName = "C4A32.cd";
        String errorCode = "0xC4A32";

        testModelNoErrors(MODEL_PATH_VALID + modelName);

        Collection<Finding> expectedErrors = Arrays.asList(
                Finding.error(errorCode + " The target cardinality (0 .. 1) of the inherited read-only association `CDAssociationSymbol foo/: A1() -> (foo)B1` is not a superset of the target cardinality (1 ..*) of the association `CDAssociationSymbol foo/: A2() -> (foo)B2`")
        );
        testModelForErrors(MODEL_PATH_INVALID + modelName,expectedErrors);
    }
}
