/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.cdgen;

import de.monticore.cd.codegen.DecoratorConfig;
import de.monticore.cd.codegen.decorators.*;
import de.monticore.cd.codegen.decorators.matcher.MatchResult;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.runtime.junit.MCAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

class ObserverDecoratorTest extends AbstractDecoratorTest {
  
  /**
   * Test the {@link ObserverDecorator} by applying it to a CD. The
   * cdlang/src/cdGenIntTest/java/observer/ObserverDecoratorResultTest then tests the generated
   * result
   */
  @Test
  void testObserver() throws Exception {
    var opt = CD4CodeMill.parser().parse_String("""
         classdiagram TestObserver {
         <<setter,observable>> public class OtherC {
           public int myInt;
           public boolean myBool;
           -> (manyB) B [*] public;
           -> (optB) B [0..1] public;
           -> (oneB) B [1] public;
           public int ov;
         }
        <<setter>>public class B {}
        
          // Test Setter&Observer interaction
          <<observable,setter>>   class CA {}
          <<observable,setter>>   class CB {}
          <<observable,setter>>   class CC {}
          <<observable,setter>>   class CD {}
          association CA <-> CB;
          association CA <-> CC [*];
          association CA <-> CD [0..1];
        
          <<observable,setter>>   class DA {}
          <<setter>>              class DB {}
          <<setter>>              class DC {}
          <<setter>>              class DD {}
          association DA <-> DB;
          association DA <-> DC [*];
          association DA <-> DD [0..1];
        
          <<setter>>              class EA {}
          <<observable,setter>>   class EB {}
          <<observable,setter>>   class EC {}
          <<observable,setter>>   class ED {}
          association EA <-> EB;
          association EA <-> EC [*];
          association EA <-> ED [0..1];
        
        }""");
    
    // The classes CA, CB, CC, and CD test observers with bidirectional assocs
    // The classes DA, ..., DD test observers with bidirectional assocs (if only the DA class is observable)
    // The classes EA, ..., ED test observers with bidirectional assocs (if only the B...D classes are observable)
    
    Assertions.assertTrue(opt.isPresent());
    
    super.doTest(opt.get());
    
    MCAssertions.assertNoFindings();
  }
  
  @Test
  public void testTemplateExistence() {
    //test existence of the templates
    List<Path> templatePaths = new ArrayList<>();
    templatePaths.add(Paths.get("src/main/resources/methods/observer/addObserver.ftl"));
    templatePaths.add(Paths.get("src/main/resources/methods/observer/removeObserver.ftl"));
    templatePaths.add(Paths.get("src/main/resources/methods/observer/notifyObserver.ftl"));
    templatePaths.add(Paths.get(
        "src/main/resources/methods/observer/notifyObserverAttributeSpecific.ftl"));
    for (Path temPath : templatePaths) {
      Assertions.assertTrue(Files.exists(temPath));
    }
  }
  
  @Override
  public void initializeDecConf(GlobalExtensionManagement glex, DecoratorConfig config,
      GeneratorSetup setup) {
    config.withCopyCreator().defaultApply();
    config.withDecorator(new GetterDecorator());
    config.configApplyMatchName(GetterDecorator.class, "getter");
    config.configIgnoreMatchName(GetterDecorator.class, "noGetter");
    config.withDecorator(new SetterDecorator());
    config.configApplyMatchName(SetterDecorator.class, "setter");
    config.configIgnoreMatchName(SetterDecorator.class, "noSetter");
    config.withDecorator(new NavigableSetterDecorator());
    config.configApplyMatchName(NavigableSetterDecorator.class, "setter");
    config.configIgnoreMatchName(NavigableSetterDecorator.class, "noSetter");
    config.withDecorator(new ObserverDecorator());
    config.configApplyMatchName(ObserverDecorator.class, "observable");
    config.configIgnoreMatchName(ObserverDecorator.class, "notObservable");
    config.withDecorator(new CardinalityDefaultDecorator());
    config.configDefault(CardinalityDefaultDecorator.class, MatchResult.APPLY);
  }
  
}
