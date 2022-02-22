package de.monticore.ow2cw;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd._visitor.CDElementVisitor;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cd4code._symboltable.ICD4CodeGlobalScope;
import de.monticore.cd4code.trafo.CD4CodeAfterParseTrafo;
import de.monticore.cd4code.trafo.CD4CodeDirectCompositionTrafo;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDElement;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.tr.cdbasistr._ast.ASTCDElement_List;

public class ReductionTrafo {

  private ASTCDCompilationUnit first;
  private ASTCDCompilationUnit second;
  private ICD4CodeArtifactScope scope1;
  private ICD4CodeArtifactScope scope2;

  /**
   * Pre-process 2 CDs for OW-CDDiff
   */
  public void transform(ASTCDCompilationUnit ast1, ASTCDCompilationUnit ast2){

    first = ast1;
    second = ast2;

    new CD4CodeAfterParseTrafo().transform(first);
    new CD4CodeDirectCompositionTrafo().transform(first);

    new CD4CodeAfterParseTrafo().transform(second);
    new CD4CodeDirectCompositionTrafo().transform(second);

    ICD4CodeGlobalScope gscope = CD4CodeMill.globalScope();
    BuiltInTypes.addBuiltInTypes(gscope);

    scope1 = CD4CodeMill.scopesGenitorDelegator().createFromAST(first);
    scope2 = CD4CodeMill.scopesGenitorDelegator().createFromAST(second);

    //todo: actual transformation
    completeSecond();

  }

  private void completeSecond(){

    //todo: add missing attributes, enum-values, associations and inheritance-relations

    // add missing classes
    for(ASTCDClass astcdClass : first.getCDDefinition().getCDClassesList()){
      if(!scope2.resolveCDTypeDown(astcdClass.getSymbol().getFullName()).isPresent()){
        ASTCDClass newClass = astcdClass.deepClone();
        second.getCDDefinition().addCDElement(newClass);
      }
    }

    // add missing classes
    for(ASTCDInterface astcdInterface : first.getCDDefinition().getCDInterfacesList()){
      if(!scope2.resolveCDTypeDown(astcdInterface.getSymbol().getFullName()).isPresent()){
        ASTCDInterface newInterface = astcdInterface.deepClone();
        second.getCDDefinition().addCDElement(newInterface);
      }
    }

    // add missing enums
    for(ASTCDEnum astcdEnum : first.getCDDefinition().getCDEnumsList()){
      if(!scope2.resolveCDTypeDown(astcdEnum.getSymbol().getFullName()).isPresent()){
        ASTCDEnum newEnum = astcdEnum.deepClone();
        second.getCDDefinition().addCDElement(newEnum);
      }
    }

  }

}
