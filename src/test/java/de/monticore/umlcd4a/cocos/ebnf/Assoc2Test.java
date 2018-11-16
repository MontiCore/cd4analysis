package de.monticore.umlcd4a.cocos.ebnf;

import de.monticore.umlcd4a.CD4ACoCos;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisCoCoChecker;
import de.monticore.umlcd4a.cocos.AbstractCoCoTest;
import org.junit.Test;

public class Assoc2Test extends AbstractCoCoTest {

    @Test
    public void testRoleNameUniqueReadOnly() {
        testModelNoErrors("src/test/resources/de/monticore/umlcd4a/cocos/TypeAssoc.cd");
    }

    @Override
    protected CD4AnalysisCoCoChecker getChecker() {
        return new CD4ACoCos().getCheckerForEbnfCoCos().addCoCo(new AssociationNameUnique());
    }
}
