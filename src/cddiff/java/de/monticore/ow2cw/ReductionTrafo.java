package de.monticore.ow2cw;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cd4code._symboltable.ICD4CodeGlobalScope;
import de.monticore.cd4code.trafo.CD4CodeDirectCompositionTrafo;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class ReductionTrafo {

  /**
   * transform 2 CDs for Open-to-Closed World Reduction of CDDiff completeSymbolTable() cannot be
   * used, because CDs likely define the same symbols
   * todo: check if elements have stereotype ""
   */
  public void transform(ASTCDCompilationUnit first, ASTCDCompilationUnit second) {

    /*
    transform first
     */

    ICD4CodeGlobalScope gscope = CD4CodeMill.globalScope();
    gscope.clear();
    BuiltInTypes.addBuiltInTypes(gscope);

    new CD4CodeDirectCompositionTrafo().transform(first);
    new CD4CodeDirectCompositionTrafo().transform(second);

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

    //add unbound associations exclusive to second if they override associations in first
    Collection<ASTCDAssociation> overrides = CDAssociationHelper.collectOverridingAssociations(
        second, first);

    modStation1.addMissingAssociations(overrides, false);

    //add dummy associations where possible
    List<ASTCDAssociation> isolated = new ArrayList<>(
        second.getCDDefinition().getCDAssociationsList());
    isolated.removeAll(overrides);
    Collection<ASTCDAssociation> dummyCol = modStation1.addDummyAssociations(isolated);

    /*
    transform second
     */

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
    copyInheritance(first, second);

    // add associations exclusive to first, except for dummy association
    List<ASTCDAssociation> noDummyList = first.getCDDefinition().getCDAssociationsList();
    noDummyList.removeAll(dummyCol);

    modStation2.addMissingAssociations(noDummyList, true);
    modStation2.updateDir2Match(noDummyList);

  }

  /**
   * add each inheritance-relations exclusive to srcCD to targetCD unless it causes cyclical
   * inheritance
   */
  public void copyInheritance(ASTCDCompilationUnit srcCD, ASTCDCompilationUnit targetCD) {

    ICD4CodeArtifactScope scope2;

    // for each class in first, find the corresponding class in second and add all
    // legal extends/implements relations
    for (ASTCDClass srcClass : srcCD.getCDDefinition().getCDClassesList()) {

      // re-build symbol table
      scope2 = CD4CodeMill.scopesGenitorDelegator().createFromAST(targetCD);

      ASTCDClass targetClass = null;

      // I don't use resolve to avoid reflection
      for (ASTCDClass someClass : targetCD.getCDDefinition().getCDClassesList()) {
        if (srcClass.getSymbol().getFullName().equals(someClass.getSymbol().getFullName())) {
          targetClass = someClass;
        }
      }

      if (targetClass == null) {
        Log.error(
            String.format("0xCDD08: Could not find class %s", srcClass.getSymbol().getFullName()));
      }
      else {
        List<ASTMCObjectType> extendsList = new ArrayList<>(targetClass.getSuperclassList());
        for (ASTMCObjectType superType : srcClass.getSuperclassList()) {
          if (CDInheritanceHelper.isNewSuper(superType, targetClass, scope2)
              && CDInheritanceHelper.inducesNoInheritanceCycle(superType, targetClass, scope2)) {
            extendsList.add(superType);
          }
          targetClass.setCDExtendUsage(
              CD4CodeMill.cDExtendUsageBuilder().addAllSuperclass(extendsList).build());

        }
        List<ASTMCObjectType> interfaceList = new ArrayList<>(targetClass.getInterfaceList());
        for (ASTMCObjectType superType : srcClass.getInterfaceList()) {
          if (CDInheritanceHelper.isNewSuper(superType, targetClass, scope2)
              && CDInheritanceHelper.inducesNoInheritanceCycle(superType, targetClass, scope2)) {
            interfaceList.add(superType);
          }
          targetClass.setCDInterfaceUsage(
              CD4CodeMill.cDInterfaceUsageBuilder().addAllInterface(interfaceList).build());
        }
      }
    }
    for (ASTCDInterface srcInterface : srcCD.getCDDefinition().getCDInterfacesList()) {
      scope2 = CD4CodeMill.scopesGenitorDelegator().createFromAST(targetCD);

      ASTCDInterface targetInterface = null;

      for (ASTCDInterface someInterface : targetCD.getCDDefinition().getCDInterfacesList()) {
        if (srcInterface.getSymbol()
            .getFullName()
            .equals(someInterface.getSymbol().getFullName())) {
          targetInterface = someInterface;
        }
      }

      if (targetInterface == null) {
        Log.error(String.format("0xCDD09: Could not find interface %s",
            srcInterface.getSymbol().getFullName()));
      }
      else {
        List<ASTMCObjectType> extendsList = new ArrayList<>(targetInterface.getInterfaceList());
        for (ASTMCObjectType superType : srcInterface.getInterfaceList()) {
          if (CDInheritanceHelper.isNewSuper(superType, targetInterface, scope2)
              && CDInheritanceHelper.inducesNoInheritanceCycle(superType, targetInterface,
              scope2)) {
            extendsList.add(superType);
          }
          targetInterface.setCDExtendUsage(
              CD4CodeMill.cDExtendUsageBuilder().addAllSuperclass(extendsList).build());
        }
      }
    }
    CD4CodeMill.scopesGenitorDelegator().createFromAST(targetCD);
    removeRedundantAttributes(targetCD);
  }

  /**
   * remove redundant attributes
   */
  public void removeRedundantAttributes(ASTCDCompilationUnit ast) {
    ICD4CodeArtifactScope artifactScope = CD4CodeMill.scopesGenitorDelegator().createFromAST(ast);
    for (ASTCDClass astcdClass : ast.getCDDefinition().getCDClassesList()) {
      for (ASTCDAttribute attribute : astcdClass.getCDAttributeList()) {
        if (CDInheritanceHelper.findInSuper(attribute, astcdClass, artifactScope)) {
          astcdClass.removeCDMember(attribute);
        }
      }
    }
    for (ASTCDInterface astcdInterface : ast.getCDDefinition().getCDInterfacesList()) {
      for (ASTCDAttribute attribute : astcdInterface.getCDAttributeList()) {
        if (CDInheritanceHelper.findInSuper(attribute, astcdInterface, artifactScope)) {
          astcdInterface.removeCDMember(attribute);
        }
      }
    }
    CD4CodeMill.scopesGenitorDelegator().createFromAST(ast);
  }

}
