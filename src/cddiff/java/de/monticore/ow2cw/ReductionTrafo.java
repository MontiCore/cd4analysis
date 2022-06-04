package de.monticore.ow2cw;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd.facade.CDExtendUsageFacade;
import de.monticore.cd.facade.CDInterfaceUsageFacade;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cd4code._symboltable.ICD4CodeGlobalScope;
import de.monticore.cd4code.trafo.CD4CodeDirectCompositionTrafo;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.ow2cw.expander.BasicExpander;
import de.monticore.ow2cw.expander.FullExpander;
import de.monticore.ow2cw.expander.OpenWorldExpander;

import java.util.*;
import java.util.stream.Collectors;

public class ReductionTrafo {

  /**
   * transform 2 CDs for Open-to-Closed World Reduction of CDDiff completeSymbolTable() cannot be
   * used, because CDs likely define the same symbols
   */
  public void transform(ASTCDCompilationUnit first, ASTCDCompilationUnit second) {

    // set-up

    ICD4CodeGlobalScope gscope = CD4CodeMill.globalScope();
    gscope.clear();
    BuiltInTypes.addBuiltInTypes(gscope);

    new CD4CodeDirectCompositionTrafo().transform(first);
    new CD4CodeDirectCompositionTrafo().transform(second);

    //handle association directions
    handleAssocDirections(first, second, true);

    // construct symbol tables
    FullExpander expander1 = new FullExpander(new OpenWorldExpander(first));
    FullExpander expander2 = new FullExpander(new OpenWorldExpander(second));

        /*
    transform first
     */

    // add subclass to each interface and abstract class
    addSubClasses4Diff(first);

    String dummyClassName = "Dummy4Diff";
    addDummyClass4Associations(first,dummyClassName);

    // get artifact-scope
    ICD4CodeArtifactScope scope1 = CD4CodeMill.scopesGenitorDelegator().createFromAST(first);

    // add classes and interfacesexclusive to second as classes without attributes, extends and
    // implements
    for (ASTCDClass astcdClass : second.getCDDefinition().getCDClassesList()) {
      Optional<CDTypeSymbol> opt = scope1.resolveCDTypeDown(astcdClass.getSymbol().getFullName());
      if (!opt.isPresent()) {
        expander1.addDummyClass(astcdClass);
      }
    }
    for (ASTCDInterface astcdInterface : second.getCDDefinition().getCDInterfacesList()) {
      Optional<CDTypeSymbol> opt = scope1.resolveCDTypeDown(astcdInterface.getSymbol().getFullName());
      if (!opt.isPresent()) {
        expander1.addDummyClass(astcdInterface);
      }
    }
    CD4CodeMill.scopesGenitorDelegator().createFromAST(first);

    //collect all super-associations exclusive to second
    Set<ASTCDAssociation> superAssociations = CDAssociationHelper.collectSuperAssociations(
        second, first);

    //collect all conflicting associations in second
    Set<ASTCDAssociation> conflicts = CDAssociationHelper.collectConflictingAssociations(
        second, first);

    //add dummy associations where possible
    Set<ASTCDAssociation> isolated = new HashSet<>(
        second.getCDDefinition().getCDAssociationsList());
    isolated.removeAll(superAssociations);
    isolated.removeAll(conflicts);
    Set<ASTCDAssociation> dummySet = expander1.addDummyAssociations(isolated, dummyClassName);

    /*
    add all non-conflicting super-associations to first without cardinality constraints
    */
    superAssociations.removeAll(conflicts);
    expander1.addMissingAssociations(superAssociations, false);

    /*
    transform second
     */

    //re-build symbol tables
    CD4CodeMill.scopesGenitorDelegator().createFromAST(first);
    CD4CodeMill.scopesGenitorDelegator().createFromAST(second);

    // add classes, interfaces and attributes exclusive to first
    expander2.addMissingTypesAndAttributes(first.getCDDefinition().getCDClassesList());
    expander2.addMissingTypesAndAttributes(first.getCDDefinition().getCDInterfacesList());

    // add enums and enum constants exclusive to first
    expander2.addMissingEnumsAndConstants(first.getCDDefinition().getCDEnumsList());

    // add inheritance relation to first, unless it causes cyclical inheritance
    copyInheritance(first, second);

    /*
    add associations exclusive to first, except for dummy association and other conflicting
    associations
     */
    Set<ASTCDAssociation> noDummySet =
        new HashSet<>(first.getCDDefinition().getCDAssociationsList());
    noDummySet.removeAll(dummySet);
    noDummySet.removeAll(CDAssociationHelper.collectConflictingAssociations(first, second));

    expander2.addMissingAssociations(noDummySet, true);

  }

  public static void addDummyClass4Associations(ASTCDCompilationUnit first, String dummyName) {
    CD4CodeMill.scopesGenitorDelegator().createFromAST(first);
    FullExpander expander = new FullExpander(new OpenWorldExpander(first));
    expander.addDummyClass(dummyName);
  }

  public static void addSubClasses4Diff(ASTCDCompilationUnit first) {
    CD4CodeMill.scopesGenitorDelegator().createFromAST(first);
    FullExpander expander = new FullExpander(new OpenWorldExpander(first));

    for (ASTCDClass astcdClass : first.getCDDefinition().getCDClassesList()) {
      if (astcdClass.getModifier().isAbstract()) {
        expander.addNewSubClass(astcdClass.getName() + "Sub4Diff", astcdClass);
      }
    }
    for (ASTCDInterface astcdInterface : first.getCDDefinition().getCDInterfacesList()) {
      expander.addNewSubClass(astcdInterface.getName() + "Sub4Diff", astcdInterface);
    }
  }

  /**
   * handles unspecified AssocDir for close-world and open-world diff; open-world also allows to
   * transform uni-directional AssocDir into bi-directional AssocDir
   */
  public static void handleAssocDirections(ASTCDCompilationUnit first, ASTCDCompilationUnit second,
      boolean isOpenWorld) {

    CD4CodeMill.scopesGenitorDelegator().createFromAST(first);
    CD4CodeMill.scopesGenitorDelegator().createFromAST(second);

    FullExpander expander1;
    FullExpander expander2;

    if (isOpenWorld){
      expander1 = new FullExpander(new OpenWorldExpander(first));
      expander2 = new FullExpander(new OpenWorldExpander(second));
    } else {
      expander1 = new FullExpander(new BasicExpander(first));
      expander2 = new FullExpander(new BasicExpander(second));
    }

    expander1.updateDir4Diff(second.getCDDefinition().getCDAssociationsList());
    expander2.updateDir2Match(first.getCDDefinition().getCDAssociationsList());
    expander1.updateUnspecifiedDir2Default();
    expander2.updateUnspecifiedDir2Default();
  }

  /**
   * add each inheritance-relations exclusive to srcCD to targetCD unless it causes cyclical
   * inheritance
   */
  public void copyInheritance(ASTCDCompilationUnit srcCD, ASTCDCompilationUnit targetCD) {

    ICD4CodeArtifactScope srcScope = CD4CodeMill.scopesGenitorDelegator().createFromAST(srcCD);
    ICD4CodeArtifactScope targetScope = CD4CodeMill.scopesGenitorDelegator()
        .createFromAST(targetCD);

    // Create a map that maps each type to all its supertypes according to both CDs
    Map<ASTCDType, Set<ASTCDType>> inheritanceGraph = new HashMap<>();

    List<ASTCDClass> classes = targetCD.getCDDefinition().getCDClassesList();
    List<ASTCDInterface> interfaces = targetCD.getCDDefinition().getCDInterfacesList();

    Set<ASTCDType> typeSet = new HashSet<>();
    typeSet.addAll(classes);
    typeSet.addAll(interfaces);

    for (ASTCDType type : typeSet) {
      inheritanceGraph.put(type, new HashSet<>(CDInheritanceHelper.getAllSuper(type, targetScope)));
      Optional<CDTypeSymbol> optType = srcScope.resolveCDTypeDown(type.getSymbol().getFullName());
      if (optType.isPresent()) {
        for (ASTCDType superType : CDInheritanceHelper.getAllSuper(optType.get().getAstNode(),
            srcScope)) {
          targetScope.resolveCDTypeDown(superType.getSymbol().getFullName())
              .ifPresent(cdTypeSymbol -> inheritanceGraph.get(type).add(cdTypeSymbol.getAstNode()));
        }
      }
      inheritanceGraph.get(type).remove(type);
    }

    // make sure interfaces do not extend classes
    for (ASTCDType type : typeSet) {
      if (interfaces.contains(type)) {
        inheritanceGraph.get(type)
            .removeAll(inheritanceGraph.get(type)
                .stream()
                .filter(superType -> !(interfaces.contains(superType)))
                .collect(Collectors.toSet()));
      }
      /*
      else if (type.getModifier().isAbstract()) {
        inheritanceGraph.get(type)
            .removeAll(inheritanceGraph.get(type)
                .stream()
                .filter(superType -> !(interfaces.contains(superType) || superType.getModifier()
                    .isAbstract()))
                .collect(Collectors.toSet()));
      }
      */
    }

    // remove cyclical inheritance
    for (ASTCDType type : typeSet) {
      inheritanceGraph.get(type)
          .removeIf(superType -> inheritanceGraph.get(superType).contains(type)
              && !CDInheritanceHelper.getAllSuper(type, targetScope).contains(superType));
    }

    //remove redundant inheritance
    for (ASTCDType type : typeSet) {
      Set<ASTCDType> superSet = new HashSet<>(inheritanceGraph.get(type));
      for (ASTCDType superType : inheritanceGraph.get(type)) {
        superSet.removeAll(inheritanceGraph.get(superType));
      }
      inheritanceGraph.put(type, superSet);
    }

    //update targetAST (distinguish between extends vs implements)
    for (ASTCDType type : typeSet) {
      if (interfaces.contains(type)) {
        ASTCDInterface current = interfaces.get(interfaces.indexOf(type));
        Set<String> extendsSet = new HashSet<>();
        for (ASTCDType superType : inheritanceGraph.get(type)) {
          if (interfaces.contains(superType)) {
            extendsSet.add(superType.getSymbol().getFullName());
          }
        }
        if (extendsSet.isEmpty()) {
          current.setCDExtendUsageAbsent();
        }
        else {
          current.setCDExtendUsage(CDExtendUsageFacade.getInstance()
              .createCDExtendUsage(extendsSet.toArray(new String[0])));
        }
      }
      else {
        ASTCDClass current = classes.get(classes.indexOf(type));
        Set<String> extendsSet = new HashSet<>();
        Set<String> implementsSet = new HashSet<>();
        for (ASTCDType superType : inheritanceGraph.get(type)) {
          if (classes.contains(superType)) {
            extendsSet.add(superType.getSymbol().getFullName());
          }
          else if (interfaces.contains(superType)) {
            implementsSet.add(superType.getSymbol().getFullName());
          }
        }
        if (extendsSet.isEmpty()) {
          current.setCDExtendUsageAbsent();
        }
        else {
          current.setCDExtendUsage(CDExtendUsageFacade.getInstance()
              .createCDExtendUsage(extendsSet.toArray(new String[0])));
        }
        if (implementsSet.isEmpty()) {
          current.setCDInterfaceUsageAbsent();
        }
        else {
          current.setCDInterfaceUsage(CDInterfaceUsageFacade.getInstance()
              .createCDInterfaceUsage(implementsSet.toArray(new String[0])));
        }
      }
    }
    CD4CodeMill.scopesGenitorDelegator().createFromAST(targetCD);
    removeRedundantAttributes(targetCD);

  }

  /**
   * removes redundant occurrences of attributes in subclasses that are already inherited from a
   * superclass
   */
  public void removeRedundantAttributes(ASTCDCompilationUnit ast) {
    ICD4CodeArtifactScope artifactScope = CD4CodeMill.scopesGenitorDelegator().createFromAST(ast);
    for (ASTCDClass astcdClass : ast.getCDDefinition().getCDClassesList()) {
      for (ASTCDAttribute attribute : astcdClass.getCDAttributeList()) {
        if (CDInheritanceHelper.isAttributInSuper(attribute, astcdClass, artifactScope)) {
          astcdClass.removeCDMember(attribute);
        }
      }
    }
    for (ASTCDInterface astcdInterface : ast.getCDDefinition().getCDInterfacesList()) {
      for (ASTCDAttribute attribute : astcdInterface.getCDAttributeList()) {
        if (CDInheritanceHelper.isAttributInSuper(attribute, astcdInterface, artifactScope)) {
          astcdInterface.removeCDMember(attribute);
        }
      }
    }
    CD4CodeMill.scopesGenitorDelegator().createFromAST(ast);
  }

}
