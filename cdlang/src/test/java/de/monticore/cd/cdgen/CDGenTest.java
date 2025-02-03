/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.cdgen;

import de.monticore.cd.codegen.CDGenerator;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd4analysis.trafo.CD4AnalysisAfterParseTrafo;
import de.monticore.cd4analysis.trafo.CDAssociationCreateFieldsFromAllRoles;
import de.monticore.cd4analysis.trafo.CDAssociationCreateFieldsFromNavigableRoles;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cdbasis.trafo.CDBasisDefaultPackageTrafo;
import de.monticore.cdgen.CDGenSetup;
import de.monticore.cdgen.decorators.*;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;

public class CDGenTest {

  @Test
  public void doTest() throws Exception {
    LogStub.initPlusLog();
    CDGenSetup setup = new CDGenSetup();

    String[] options = new String[]{
      "MyCD.CliC:noGetter",
      "MyCD.CliC.f:getter",
      "MyCD.CliC:attributesFromRoles=all",
    };

    setup.withDecorator(new GetterDecorator());
    setup.configApplyMatchName(GetterDecorator.class, "getter");
    setup.configIgnoreMatchName(GetterDecorator.class, "noGetter");

    setup.withDecorator(new SetterDecorator());
    setup.configApplyMatchName(SetterDecorator.class, ("setter"));
    setup.configIgnoreMatchName(SetterDecorator.class, ("noSetter"));

    setup.withDecorator(new NavigableSetterDecorator());
    setup.configApplyMatchName(NavigableSetterDecorator.class, "setter");
    setup.configIgnoreMatchName(NavigableSetterDecorator.class, "noSetter");


    setup.withDecorator(new BuilderDecorator());
    setup.configApplyMatchName(BuilderDecorator.class, "builder");
    setup.configIgnoreMatchName(BuilderDecorator.class,"noBuilder");

    setup.withDecorator(new ObserverDecorator());
    setup.configApplyMatchName(ObserverDecorator.class,"observable");
    setup.configIgnoreMatchName(ObserverDecorator.class, "notObservable");

    setup.withCLIConfig(Arrays.asList(options));


    CD4CodeMill.reset();
    CD4CodeMill.init();
    var opt = CD4CodeMill.parser().parse_String("classdiagram MyCD {\n" +
      " <<getter>> public class  MyC { \n" +
      " boolean myBool;" +
      " public int myInt;" +
      " <<noGetter>> public int pubX;" +
      " }" +
      " public class CliC { \n" +
      "   int f;\n" +
      "   int e;\n" +
      " }\n"+
      "<<setter,getter,builder,observable>> public class OtherC { \n" +
      " public int myInt;\n" +
//      " -> (manyB) B [*];\n" +
//      " -> (optB) B [0..1] ;\n" +
      " -> (oneB) B [1]; \n" +
      " }\n" +
      "<<setter>>public class B { " +
      "}\n " +
      "association OtherC (binavC) <-> (binavB) B;" + // TODO: same without setter
      "}");

    // After parse Trafos
    var afterParseTrafo = new CD4AnalysisAfterParseTrafo();
    afterParseTrafo.transform(opt.get());

    BasicSymbolsMill.initializePrimitives();

    // Create ST
    CD4CodeMill.scopesGenitorDelegator().createFromAST(opt.get());

    // Complete ST
    opt.get().accept(new CD4CodeSymbolTableCompleter(opt.get()).getTraverser());

    // Transform with ST
    CDAssociationCreateFieldsFromAllRoles roleTrafo = new CDAssociationCreateFieldsFromNavigableRoles();
    final CD4CodeTraverser traverser = CD4CodeMill.inheritanceTraverser();
    traverser.add4CDAssociation(roleTrafo);
    traverser.setCDAssociationHandler(roleTrafo);
    roleTrafo.transform(opt.get());

    // Prepare
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    generatorSetup.setOutputDirectory(new File("target/outtest"));

    generatorSetup.getOutputDirectory().mkdirs();

    CDGenerator generator = new CDGenerator(generatorSetup);


    var decorated = setup.decorate(opt.get(), roleTrafo.getFieldToRoles(), Optional.of(glex));

    System.err.println(CD4CodeMill.prettyPrint(decorated, true));

    // Post-Decorate
    CD4CodeTraverser t = CD4CodeMill.inheritanceTraverser();
    t.add4CDBasis(new CDBasisDefaultPackageTrafo());
    decorated.accept(t);

    generator.generate(decorated);
    System.err.println(generatorSetup.getOutputDirectory().getAbsolutePath());
  }
}
