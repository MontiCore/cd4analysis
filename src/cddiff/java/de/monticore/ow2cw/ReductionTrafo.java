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
import net.sourceforge.plantuml.Log;

import java.util.ArrayList;
import java.util.List;
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
        addMissingAttribute(opt.get(), astcdClass.getCDAttributeList());
      }
    }

    // missing interfaces and attributes
    for(ASTCDInterface astcdInterface : first.getCDDefinition().getCDInterfacesList()){
      Optional<CDTypeSymbol> opt = scope2.resolveCDTypeDown(astcdInterface.getSymbol().getFullName());
      if(!opt.isPresent()){
        ASTCDInterface newInterface = astcdInterface.deepClone();
        second.getCDDefinition().addCDElement(newInterface);
      } else{
        addMissingAttribute(opt.get(), astcdInterface.getCDAttributeList());
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
    completeInheritance();
    removeRedundancies();
  }

  /**
   * add all inheritance-relations exclusive to first to second
   * todo: unless we get cyclical inheritance
   */
  private void completeInheritance(){
    for (ASTCDClass astcdClass : first.getCDDefinition().getCDClassesList()){
      scope2 = CD4CodeMill.scopesGenitorDelegator().createFromAST(second);
      Optional<CDTypeSymbol> opt = scope2.resolveCDTypeDown(astcdClass.getSymbol().getFullName());
      if(!opt.isPresent()){
        Log.error(String.format("0xCDD08: Could not find %s",
            astcdClass.getSymbol().getFullName()));
      } else{
        ASTCDType targetNode = opt.get().getAstNode();
        List<ASTMCObjectType> extendsList = new ArrayList<>(targetNode.getSuperclassList());
        for (ASTMCObjectType superType : astcdClass.getSuperclassList()){
          if(isNewSuper(superType,targetNode)) {
            extendsList.add(superType);
          }
          if(targetNode instanceof ASTCDClass){
            ((ASTCDClass) targetNode).setCDExtendUsage(CD4CodeMill.cDExtendUsageBuilder()
                .addAllSuperclass(extendsList).build());
          }
        }
        List<ASTMCObjectType> interfaceList = new ArrayList<>(targetNode.getInterfaceList());
        for (ASTMCObjectType superType : astcdClass.getInterfaceList()){
          if(isNewSuper(superType,targetNode)) {
            interfaceList.add(superType);
          }
          if(targetNode instanceof ASTCDClass){
            ((ASTCDClass) targetNode).setCDInterfaceUsage(CD4CodeMill.cDInterfaceUsageBuilder()
                .addAllInterface(interfaceList).build());
          }
        }
      }
    }
    for(ASTCDInterface astcdInterface : first.getCDDefinition().getCDInterfacesList()){
      scope2 = CD4CodeMill.scopesGenitorDelegator().createFromAST(second);
      Optional<CDTypeSymbol> opt = scope2.resolveCDTypeDown(astcdInterface.getSymbol().getFullName());
      if(!opt.isPresent()){
        Log.error(String.format("0xCDD08: Could not find %s",
            astcdInterface.getSymbol().getFullName()));
      } else{
        ASTCDType targetNode = opt.get().getAstNode();
        List<ASTMCObjectType> extendsList = new ArrayList<>(targetNode.getInterfaceList());
        for (ASTMCObjectType superType : astcdInterface.getInterfaceList()){
          if(isNewSuper(superType,targetNode)) {
            extendsList.add(superType);
          }
          if(targetNode instanceof ASTCDInterface){
            ((ASTCDInterface) targetNode).setCDExtendUsage(CD4CodeMill.cDExtendUsageBuilder()
                .addAllSuperclass(extendsList).build());
          }
        }
      }
    }
  }

  private boolean isNewSuper(ASTMCObjectType newSuper, ASTCDType targetNode) {
    for(ASTCDType oldSuper : getAllSuper(targetNode)){
      if(oldSuper.getSymbol().getFullName().contains(newSuper.printType(pp))){
        return false;
      }
    }
    return true;
  }

  // remove redundant attributes
  private void removeRedundancies(){
    scope2 = CD4CodeMill.scopesGenitorDelegator().createFromAST(second);
    for(ASTCDClass astcdClass : second.getCDDefinition().getCDClassesList()){
      for (ASTCDAttribute attribute : astcdClass.getCDAttributeList()) {
        if (findInSuper(attribute, astcdClass)) {
          astcdClass.removeCDMember(attribute);
        }
      }
    }
    for(ASTCDInterface astcdInterface : second.getCDDefinition().getCDInterfacesList()){
      for (ASTCDAttribute attribute : astcdInterface.getCDAttributeList()) {
        if (findInSuper(attribute, astcdInterface)) {
          astcdInterface.removeCDMember(attribute);
        }
      }
    }
    scope2 = CD4CodeMill.scopesGenitorDelegator().createFromAST(second);
  }

  private void addMissingAttribute(CDTypeSymbol typeSymbol, List<ASTCDAttribute> cdAttributeList) {
    for(ASTCDAttribute attribute1 : cdAttributeList){
      boolean found = false;
      for (ASTCDAttribute attribute2 : typeSymbol.getAstNode().getCDAttributeList()){
        if (attribute1.getName().equals(attribute2.getName())){
          found = true;
          break;
        }
      }
      if (!found){
        ASTCDAttribute newAttribute = attribute1.deepClone();
        typeSymbol.getAstNode().addCDMember(newAttribute);
      }
    }
  }

  private boolean findInSuper(ASTCDAttribute attribute1, ASTCDType cdType){
    for(ASTCDType supertype : getAllSuper(cdType)){
      if(supertype != cdType){
        for(ASTCDAttribute attribute2 : supertype.getCDAttributeList()){
          if (attribute1.getName().equals(attribute2.getName())){
            return true;
          }
        }
      }
    }
    return false;
  }

  private List<ASTCDType> getAllSuper(ASTCDType cdType){
    List<ASTCDType> superList = new ArrayList<>(getDirectSuperClasses(cdType));
    superList.addAll(getDirectInterfaces(cdType));

    List<ASTCDType> nextSuperSuperList = new ArrayList<>();
    for(ASTCDType nextSuper : superList){
      nextSuperSuperList.addAll(getAllSuper(nextSuper));
    }
    superList.addAll(nextSuperSuperList);
    superList.add(cdType);
    return superList;
  }

  private List<ASTCDType> getDirectSuperClasses(ASTCDType cdType){
    List<ASTCDType> extendsList = new ArrayList<>();
    for (ASTMCObjectType superType : cdType.getSuperclassList()){
      Optional<CDTypeSymbol> opt =
          cdType.getEnclosingScope().resolveCDTypeDown(superType.printType(pp));
      if(!opt.isPresent()){
        opt = scope2.resolveCDTypeDown(superType.printType(pp));
      }
      opt.ifPresent(cdTypeSymbol -> extendsList.add(cdTypeSymbol.getAstNode()));
    }
    return extendsList;
  }

  private List<ASTCDType> getDirectInterfaces(ASTCDType cdType){
    List<ASTCDType> interfaceList = new ArrayList<>();
    for (ASTMCObjectType superType : cdType.getInterfaceList()){
      Optional<CDTypeSymbol> opt =
          cdType.getEnclosingScope().resolveCDTypeDown(superType.printType(pp));
      if(!opt.isPresent()){
        opt = scope2.resolveCDTypeDown(superType.printType(pp));
      }
      opt.ifPresent(cdTypeSymbol -> interfaceList.add(cdTypeSymbol.getAstNode()));
    }
    return interfaceList;
  }

}
