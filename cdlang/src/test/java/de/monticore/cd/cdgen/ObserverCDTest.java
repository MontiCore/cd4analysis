package de.monticore.cd.cdgen;

import de.monticore.cd.codegen.CDGenService;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4analysis.trafo.CD4AnalysisAfterParseTrafo;
import de.monticore.cd4analysis.trafo.CDAssociationCreateFieldsFromAllRoles;
import de.monticore.cd4analysis.trafo.CDAssociationCreateFieldsFromNavigableRoles;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDInterfaceUsage;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis.trafo.CDBasisDefaultPackageTrafo;
import de.monticore.cdgen.CDGenSetup;
import de.monticore.cdgen.decorators.BuilderDecorator;
import de.monticore.cdgen.decorators.ObserverDecorator;
import de.monticore.cdgen.decorators.SetterDecorator;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mccollectiontypes.types3.MCCollectionSymTypeRelations;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import java.io.IOException;
import java.util.Optional;
import static org.junit.Assert.assertTrue;

public class ObserverCDTest {

  static ASTCDClass pojoClass;
  static ASTCDCompilationUnit pojoCompilationUnit;

  //TODO: path
  String pathToObserverPatternInterfaces = "test";

  @BeforeClass
  public static void init() throws IOException {
    LogStub.initPlusLog();
    CD4CodeMill.reset();
    CD4CodeMill.init();
    CD4C.reset();
    CD4C.init(new GeneratorSetup());
    BasicSymbolsMill.initializePrimitives();
    MCCollectionSymTypeRelations.init();
    LogStub.initPlusLog();

    CDGenSetup setup = new CDGenSetup();

    // the execution of the BuilderDecorator depends on the SetterDecorator executed previously
    setup.withDecorator(new SetterDecorator());
    setup.configApplyMatchName(SetterDecorator.class, ("setter"));
    setup.configIgnoreMatchName(SetterDecorator.class, ("noSetter"));

    setup.withDecorator(new BuilderDecorator());
    setup.configApplyMatchName(BuilderDecorator.class, "builder");
    setup.configIgnoreMatchName(BuilderDecorator.class, "noBuilder");

    setup.withDecorator(new ObserverDecorator());
    setup.configApplyMatchName(ObserverDecorator.class, "observer");
    setup.configIgnoreMatchName(ObserverDecorator.class, "noObserver");

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
  }

  private static Optional<ASTCDCompilationUnit> parseStringToCompilationUnit() throws IOException {
    return CD4CodeMill.parser().parse_String("classdiagram MyCD {\n" +
      " <<setter,observer>> public class OtherC { \n" +
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

  @Test
  public void testImport() {
    //TODO cannot check if the import is present with the object as CD4C adds it with a template
  }

  @Test
  public void testAddObservableInterface() {
    Assert.assertTrue(pojoClass.isPresentCDInterfaceUsage());
    ASTCDInterfaceUsage interfaceUsage = pojoClass.getCDInterfaceUsage();
    Assert.assertNotNull(interfaceUsage);
    assertTrue(interfaceUsage.getInterfaceList().stream().anyMatch( i-> i instanceof ASTMCQualifiedType && ((ASTMCQualifiedType) i).getMCQualifiedName().getQName().equals(pathToObserverPatternInterfaces + ".Observable")));
  }
}
