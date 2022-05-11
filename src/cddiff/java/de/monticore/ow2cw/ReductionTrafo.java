package de.monticore.ow2cw;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cd4code._symboltable.ICD4CodeGlobalScope;
import de.monticore.cd4code.trafo.CD4CodeDirectCompositionTrafo;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;

import java.util.Optional;

public class ReductionTrafo {

  protected MCBasicTypesFullPrettyPrinter pp = new MCBasicTypesFullPrettyPrinter(
      new IndentPrinter());

  /**
   * transform 2 CDs for Open-to-Closed World Reduction of CDDiff completeSymbolTable() cannot be
   * used, because CDs likely define the same symbols
   * todo: check if elements have stereotype ""
   */
  public void transform(ASTCDCompilationUnit first, ASTCDCompilationUnit second) {

    ICD4CodeGlobalScope gscope = CD4CodeMill.globalScope();
    gscope.clear();
    BuiltInTypes.addBuiltInTypes(gscope);

    new CD4CodeDirectCompositionTrafo().transform(first);
    new CD4CodeDirectCompositionTrafo().transform(second);

    transformFirst(first, second);
    transformSecond(first, second);
  }

  /**
   * transform the first CD
   */
  protected void transformFirst(ASTCDCompilationUnit first, ASTCDCompilationUnit second) {

    // construct symbol tables
    ICD4CodeArtifactScope scope1 = CD4CodeMill.scopesGenitorDelegator().createFromAST(first);
    CD4CodeMill.scopesGenitorDelegator().createFromAST(second);

    CDModStation modStation1 = new CDModStation(first);

    // add subclass to each interface and abstract class
    for (ASTCDClass astcdClass : first.getCDDefinition().getCDClassesList()) {
      if (astcdClass.getModifier().isAbstract()) {
        modStation1.addNewSubClass(astcdClass.getName() + "4Diff", astcdClass);
      }
    }
    for (ASTCDInterface astcdInterface : first.getCDDefinition().getCDInterfacesList()) {
      modStation1.addNewSubClass(astcdInterface.getName() + "4Diff", astcdInterface);
    }

    // add classes exclusive to second as classes without attributes, extends and implements
    for (ASTCDClass astcdClass : second.getCDDefinition().getCDClassesList()) {
      Optional<CDTypeSymbol> opt = scope1.resolveCDTypeDown(astcdClass.getSymbol().getFullName());
      if (!opt.isPresent()) {
        modStation1.addDummyClass(astcdClass);
      }
    }
    CD4CodeMill.scopesGenitorDelegator().createFromAST(first);

    //add associations exclusive to second, but without cardinalities
    // todo: visitor?
    modStation1.addMissingAssociations(second.getCDDefinition().getCDAssociationsList(), false);
  }

  /**
   * transform the second CD
   */
  protected void transformSecond(ASTCDCompilationUnit first, ASTCDCompilationUnit second) {

    //re-build symbol tables
    CD4CodeMill.scopesGenitorDelegator().createFromAST(first);
    CD4CodeMill.scopesGenitorDelegator().createFromAST(second);

    CDModStation modStation2 = new CDModStation(second);

    // add classes, interfaces and attributes exclusive to first
    modStation2.addMissingTypesAndAttributes(first.getCDDefinition().getCDClassesList());
    modStation2.addMissingTypesAndAttributes(first.getCDDefinition().getCDInterfacesList());

    // add enums and enum constants exclusive to first
    modStation2.addMissingEnumsAndConstants(first.getCDDefinition().getCDEnumsList());

    // add inheritance relation to first, unless it causes cyclical inheritance
    CDInheritanceHelper.copyInheritance(first, second);

    // add associations exclusive to first
    modStation2.addMissingAssociations(first.getCDDefinition().getCDAssociationsList(), true);
    modStation2.updateDir2Match(first.getCDDefinition().getCDAssociationsList());
  }

}
