/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.cdgen;

import de.monticore.cd.codegen.CDGenService;
import de.monticore.cd.codegen.CDGenerator;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd.codegen.DecoratorConfig;
import de.monticore.cd4analysis._util.CD4AnalysisTypeDispatcher;
import de.monticore.cd4analysis.trafo.CD4AnalysisAfterParseTrafo;
import de.monticore.cd4analysis.trafo.CDAssociationCreateFieldsFromAllRoles;
import de.monticore.cd4analysis.trafo.CDAssociationCreateFieldsFromNavigableRoles;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis.trafo.CDBasisDefaultPackageTrafo;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mccollectiontypes.types3.MCCollectionSymTypeRelations;
import de.se_rwth.commons.logging.LogStub;
import java.io.File;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;

public class AbstractCDGenTest {

  protected DecoratorConfig setup;
  protected File outputDir;

  @BeforeEach
  public void init() {
    LogStub.initPlusLog();
    CD4CodeMill.reset();
    CD4CodeMill.init();
    this.setup = new DecoratorConfig();
    this.outputDir = new File("target/cdGenOutTest/" + getClass().getSimpleName());
  }

  public void doTest(ASTCDCompilationUnit cd) {
    // After parse Trafos
    var afterParseTrafo = new CD4AnalysisAfterParseTrafo();
    afterParseTrafo.transform(cd);

    BasicSymbolsMill.initializePrimitives();
    MCCollectionSymTypeRelations.init();

    // Create ST
    CD4CodeMill.scopesGenitorDelegator().createFromAST(cd);

    // Complete ST
    cd.accept(new CD4CodeSymbolTableCompleter(cd).getTraverser());

    // Transform with ST
    CDAssociationCreateFieldsFromAllRoles roleTrafo =
        new CDAssociationCreateFieldsFromNavigableRoles();
    final CD4CodeTraverser traverser = CD4CodeMill.inheritanceTraverser();
    traverser.add4CDAssociation(roleTrafo);
    traverser.setCDAssociationHandler(roleTrafo);
    roleTrafo.transform(cd);

    // Prepare
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());
    glex.setGlobalValue("mcTypeFacade", MCTypeFacade.getInstance());
    glex.setGlobalValue("cdGenService", new CDGenService());
    glex.setGlobalValue("cd4AnalysisTypeDispatcher", new CD4AnalysisTypeDispatcher());
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    generatorSetup.setOutputDirectory(this.outputDir);

    generatorSetup.getOutputDirectory().mkdirs();

    CDGenerator generator = new CDGenerator(generatorSetup);

    var decoratedOpt = setup.decorate(cd, roleTrafo.getFieldToRoles(), Optional.of(glex));

    System.err.println(CD4CodeMill.prettyPrint(decoratedOpt.get(), true));

    // Post-Decorate
    CD4CodeTraverser t = CD4CodeMill.inheritanceTraverser();
    t.add4CDBasis(new CDBasisDefaultPackageTrafo());
    decoratedOpt.get().accept(t);

    generator.generate(decoratedOpt.get());
    System.out.println(
        "Wrote CDGenTest results to " + generatorSetup.getOutputDirectory().getAbsolutePath());
  }

  public void makeMethodsInInterfacesAbstract(ASTCDCompilationUnit ast) {
    for (ASTCDInterface cdInterface : ast.getCDDefinition().getCDInterfacesList()) {
      for (ASTCDMethod method : cdInterface.getCDMethodList()) {
        method.getModifier().setAbstract(true);
      }
    }
  }
}
