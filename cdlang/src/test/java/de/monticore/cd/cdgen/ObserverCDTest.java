package de.monticore.cd.cdgen;

import de.monticore.cd.codegen.CDGenService;
import de.monticore.cd.codegen.CDGenerator;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd.codegen.DecoratorConfig;
import de.monticore.cd.codegen.decorators.*;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4analysis.trafo.CD4AnalysisAfterParseTrafo;
import de.monticore.cd4analysis.trafo.CDAssociationCreateFieldsFromAllRoles;
import de.monticore.cd4analysis.trafo.CDAssociationCreateFieldsFromNavigableRoles;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDInterfaceUsage;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis.trafo.CDBasisDefaultPackageTrafo;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mccollectiontypes.types3.MCCollectionSymTypeRelations;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import static org.junit.Assert.assertTrue;

public class ObserverCDTest {

  private static String pojoClassContent;
  static ASTCDClass pojoClass;
  static ASTCDCompilationUnit pojoCompilationUnit;

  //TODO add the interfaces Observer and Observable to the CD runtime
  //TODO: replace path with the correct path to the CD runtime
  String pathToObserverPatternInterfaces = "test";
  
  public static void init() throws IOException {
    LogStub.initPlusLog();
    CD4CodeMill.reset();
    CD4CodeMill.init();
    CD4C.reset();
    CD4C.init(new GeneratorSetup());
    BasicSymbolsMill.initializePrimitives();
    MCCollectionSymTypeRelations.init();
    LogStub.initPlusLog();

    generateClass();
  }

  @Test
  public void testImport() throws IOException {
    init();
    Assert.assertTrue(pojoClassContent.contains("import test.Observable;"));
    Assert.assertTrue(pojoClassContent.contains("import test.Observer;"));
  }

  @Test
  public void testAddObservableInterface() throws IOException {
    init();
    Assert.assertTrue(pojoClass.isPresentCDInterfaceUsage());
    ASTCDInterfaceUsage interfaceUsage = pojoClass.getCDInterfaceUsage();
    Assert.assertNotNull(interfaceUsage);
    assertTrue(interfaceUsage.getInterfaceList().stream().anyMatch( i-> i instanceof ASTMCQualifiedType && ((ASTMCQualifiedType) i).getMCQualifiedName().getQName().equals(pathToObserverPatternInterfaces + ".Observable")));
  }

  @Test
  public void testObservableInterfaceMethodSignatures() throws IOException {
    init();
    Assert.assertTrue(pojoClass.getCDMethodList().stream().anyMatch(m -> m.getName().equals("addObserver")));
    Assert.assertTrue(pojoClass.getCDMethodList().stream().anyMatch(m -> m.getName().equals("removeObserver")));
    Assert.assertTrue(pojoClass.getCDMethodList().stream().anyMatch(m -> m.getName().equals("notifyObservers")));
    Assert.assertTrue(pojoClass.getCDMethodList().stream().anyMatch(m -> m.getName().equals("getUpdatedDate")));

    ASTCDMethod addObserver = pojoClass.getCDMethodList().stream().filter(m -> m.getName().equals("addObserver")).findFirst().get();
    ASTCDMethod removeObserver = pojoClass.getCDMethodList().stream().filter(m -> m.getName().equals("removeObserver")).findFirst().get();
    ASTCDMethod notifyObservers = pojoClass.getCDMethodList().stream().filter(m -> m.getName().equals("notifyObservers")).findFirst().get();
    ASTCDMethod getUpdatedData = pojoClass.getCDMethodList().stream().filter(m -> m.getName().equals("getUpdatedDate")).findFirst().get();

    // addObserver is public and has one parameter of type Observer
    Assert.assertTrue(addObserver.getModifier().isPublic());
    Assert.assertEquals(1, addObserver.getCDParameterList().size());

    // removeObserver is public and has one parameter of type Observer#
    Assert.assertTrue(removeObserver.getModifier().isPublic());
    Assert.assertEquals(1, removeObserver.getCDParameterList().size());

    // notifyObservers is public and has no parameters
    Assert.assertTrue(notifyObservers.getModifier().isPublic());
    Assert.assertTrue(notifyObservers.getCDParameterList().isEmpty());

    // getUpdatedData is public and returns an Object
    Assert.assertTrue(getUpdatedData.getModifier().isPublic());
    Assert.assertTrue(getUpdatedData.getCDParameterList().isEmpty());
  }

  private static Optional<ASTCDCompilationUnit> parseStringToCompilationUnit() throws IOException {
    return CD4CodeMill.parser().parse_String("classdiagram MyCD {\n" +
      " <<setter,observable>> public class OtherC { \n" +
      " public int myInt;\n" +
      " public boolean myBool;\n" +
      " -> (manyB) B [*];\n" +
      " -> (optB) B [0..1] ;\n" +
      " -> (oneB) B [1]; \n" +
      " }\n" +
      "<<setter>>public class B { " +
      "}\n " +
      "}");
  }

  public static void generateClass() throws IOException {
    DecoratorConfig setup = new DecoratorConfig();

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
    setup.configIgnoreMatchName(BuilderDecorator.class, "noBuilder");

    setup.withDecorator(new ObserverDecorator());
    setup.configApplyMatchName(ObserverDecorator.class, "observable");
    setup.configIgnoreMatchName(ObserverDecorator.class, "notObservable");

    Optional<ASTCDCompilationUnit> opt = parseStringToCompilationUnit();

    // After parse Trafos
    CD4AnalysisAfterParseTrafo afterParseTrafo = new CD4AnalysisAfterParseTrafo();
    afterParseTrafo.transform(opt.get());

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
    glex.setGlobalValue("mcTypeFacade", MCTypeFacade.getInstance());
    glex.setGlobalValue("mcCollectionSymTypeRelations", new MCCollectionSymTypeRelations());
    glex.setGlobalValue("cdGenService", new CDGenService());

    ASTCDCompilationUnit decorated = setup.decorate(opt.get(), roleTrafo.getFieldToRoles(), Optional.of(glex));

    // Post-Decorate
    CD4CodeTraverser t = CD4CodeMill.inheritanceTraverser();
    t.add4CDBasis(new CDBasisDefaultPackageTrafo());
    decorated.accept(t);

    pojoCompilationUnit = decorated;
    ASTCDPackage cdPackage = decorated.getCDDefinition().getCDPackagesList().get(0);
    pojoClass = (ASTCDClass) cdPackage.getCDElement(0);

    // only used when analyzing the body's of methods/constructors
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    generatorSetup.setOutputDirectory(new File("target/outtest"));
    generatorSetup.getOutputDirectory().mkdirs();
    CDGenerator generator = new CDGenerator(generatorSetup);
    generator.generate(decorated);
    try {
      // Define the path to the file
      Path filePath = Paths.get("target/outtest/MyCD/OtherC.java");

      // Read all lines from the file
      List<String> lines = Files.readAllLines(filePath);

      // Convert List<String> to a single String
      StringBuilder stringBuilder = new StringBuilder();
      for (String line : lines) {
        stringBuilder.append(line).append("\n");
      }
      pojoClassContent = stringBuilder.toString();

    } catch (IOException e) {
      System.err.println("Error reading file: " + e.getMessage());
    }
  }
}
