package de.monticore.ow2cw;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cd4code._symboltable.ICD4CodeGlobalScope;
import de.monticore.cd4code.trafo.CD4CodeDirectCompositionTrafo;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;

import java.util.Optional;

public class ReductionTrafo {

  private ASTCDCompilationUnit first;
  private ASTCDCompilationUnit second;
  private ICD4CodeArtifactScope scope1;
  private ICD4CodeArtifactScope scope2;
  private final static MCBasicTypesFullPrettyPrinter pp = new MCBasicTypesFullPrettyPrinter(new IndentPrinter());

  /**
   * Pre-process 2 CDs for OW-CDDiff
   */
  public void transform(ASTCDCompilationUnit ast1, ASTCDCompilationUnit ast2){

    ICD4CodeGlobalScope gscope = CD4CodeMill.globalScope();
    gscope.clear();
    BuiltInTypes.addBuiltInTypes(gscope);

    first = ast1;
    second = ast2;

    new CD4CodeDirectCompositionTrafo().transform(first);
    new CD4CodeDirectCompositionTrafo().transform(second);


    scope1 = CD4CodeMill.scopesGenitorDelegator().createFromAST(first);
    scope2 = CD4CodeMill.scopesGenitorDelegator().createFromAST(second);

    //todo: actual transformation
    completeSecond();

  }

  private void completeSecond(){

    //todo: add missing attributes, enum-values, associations and inheritance-relations
    for(ASTCDPackage astcdPackage : first.getCDDefinition().getCDPackagesList()){
      if(!scope2.resolveCDTypeDown(astcdPackage.getSymbol().getFullName()).isPresent()){
        ASTCDPackage newPackage = astcdPackage.deepClone();
        second.getCDDefinition().addCDElement(newPackage);
      }
    }

    // add missing classes and attributes
    for(ASTCDClass astcdClass : first.getCDDefinition().getCDClassesList()){
      Optional<CDTypeSymbol> opt = scope2.resolveCDTypeDown(astcdClass.getSymbol().getFullName());
      if(!opt.isPresent()){
        ASTCDClass newClass = astcdClass.deepClone();
        second.getCDDefinition().addCDElement(newClass);
      } else{
        for(ASTCDAttribute attribute1 : astcdClass.getCDAttributeList()){
          boolean found = false;
          for (ASTCDAttribute attribute2 : opt.get().getAstNode().getCDAttributeList()){
            if (attribute1.getName().equals(attribute2.getName())){
              found = true;
              break;
            }
          }
          if (!found){
            ASTCDAttribute newAttribute = attribute1.deepClone();
            opt.get().getAstNode().addCDMember(newAttribute);
          }
        }
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
      Optional<CDTypeSymbol> opt = scope2.resolveCDTypeDown(astcdEnum.getSymbol().getFullName());
      if(!opt.isPresent()){
        ASTCDEnum newEnum = astcdEnum.deepClone();
        second.getCDDefinition().addCDElement(newEnum);
      } else {
        for (ASTCDEnumConstant constant : astcdEnum.getCDEnumConstantList()){
          boolean found = false;
          for (FieldSymbol field : opt.get().getFieldList()){
            if (field.getName().equals(constant.getName())){
              found = true;
              break;
            }
          }
          if (!found && (opt.get().getAstNode() instanceof ASTCDEnum)){
            ASTCDEnumConstant newConstant = constant.deepClone();
            ((ASTCDEnum) opt.get().getAstNode()).addCDEnumConstant(newConstant);
          }
        }
      }
    }

    // remove redundant attributes
    for(ASTCDClass astcdClass : second.getCDDefinition().getCDClassesList()){
      for (ASTCDAttribute attribute : astcdClass.getCDAttributeList()) {
        if (findInSuperClass(attribute, astcdClass)) {
          astcdClass.removeCDMember(attribute);
        }
      }
    }

  }

  private boolean findInSuperClass(ASTCDAttribute attribute1, ASTCDClass astcdClass){
    for (ASTMCObjectType type : astcdClass.getSuperclassList()){
      Optional<CDTypeSymbol> opt =
          astcdClass.getEnclosingScope().resolveCDTypeDown(type.printType(pp));
      if(!opt.isPresent()){
        opt = scope2.resolveCDTypeDown(type.printType(pp));
      }
      if(opt.isPresent()){
        for(ASTCDAttribute attribute2 : opt.get().getAstNode().getCDAttributeList()){
          if (attribute1.getName().equals(attribute2.getName())){
            return true;
          }
        }
      }
    }
    return false;
  }

}
