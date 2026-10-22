/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen;

import de.monticore.cd.cdgen.AbstractDecoratorTest;
import de.monticore.cd.codegen.decorators.BuilderDecorator;
import de.monticore.cd.codegen.decorators.CardinalityDefaultDecorator;
import de.monticore.cd.codegen.decorators.GetterDecorator;
import de.monticore.cd.codegen.decorators.SetterDecorator;
import de.monticore.cd.codegen.decorators.matcher.MatchResult;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BuilderDecoratorTest extends AbstractDecoratorTest {

  @Test
  public void decoratorTest() throws IOException {

    final Optional<ASTCDCompilationUnit> optAST = CD4CodeMill.parser().parse_String("""
        classdiagram TestBuilder {
          <<setter,getter,builder>>
          public class TestBuilderWithSetter {
            public int myInt;
            public boolean myBool;
            public String myString;
          }
          <<builder>>
          public abstract class TestAbstractBuilder {
            public int myAbstractInt;
            public boolean myAbstractBool;
          }
        }""");

    assertTrue(optAST.isPresent());
    final ASTCDCompilationUnit ast = optAST.get();

    super.doTest(ast);

    assertTrue(Log.getFindings().isEmpty());
  }

  @Override
  public void initializeDecConf(GlobalExtensionManagement glex, DecoratorConfig config,
      GeneratorSetup setup) {

    config.withCopyCreator().defaultApply();
    // getter
    config.withDecorator(new GetterDecorator());
    config.configApplyMatchName(GetterDecorator.class, "getter");
    config.configIgnoreMatchName(GetterDecorator.class, "noGetter");

    // setter
    config.withDecorator(new SetterDecorator());
    config.configApplyMatchName(SetterDecorator.class, "setter");
    config.configIgnoreMatchName(SetterDecorator.class, "noSetter");

    // builder
    config.withDecorator(new BuilderDecorator());
    config.configApplyMatchName(BuilderDecorator.class, "builder");
    config.configIgnoreMatchName(BuilderDecorator.class, "noBuilder");

    config.withDecorator(new CardinalityDefaultDecorator());
    config.configDefault(CardinalityDefaultDecorator.class, MatchResult.APPLY);
  }

}
