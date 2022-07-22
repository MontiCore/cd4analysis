package de.monticore.ow2cw;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd.facade.CDExtendUsageFacade;
import de.monticore.cd.facade.CDInterfaceUsageFacade;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cd4code._symboltable.ICD4CodeGlobalScope;
import de.monticore.cd4code.trafo.CD4CodeDirectCompositionTrafo;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.ow2cw.expander.BasicExpander;
import de.monticore.ow2cw.expander.FullExpander;
import de.monticore.ow2cw.expander.VariableExpander;

import java.util.*;
import java.util.stream.Collectors;

public class ReductionTrafo {

  public static final String COMMON_INTERFACE = "Object";

  /**
   * transform 2 CDs for Open-to-Closed World Reduction of CDDiff; completeSymbolTable() cannot be
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
    handleAssocDirections(first, second);

    /*
    transform first
    */

    // built symbol table
    CD4CodeMill.scopesGenitorDelegator().createFromAST(first);

    FullExpander expander1 = new FullExpander(new VariableExpander(first));
    FullExpander expander2 = new FullExpander(new VariableExpander(second));

    List<ASTCDType> typeList = new ArrayList<>();
    typeList.addAll(second.getCDDefinition().getCDClassesList());
    typeList.addAll(second.getCDDefinition().getCDInterfacesList());

    /*
    Add classes and interfaces exclusive to second as classes without attributes, extends and
    implements and built new associations for classes that are only <<complete>> in second
     */
    Set<ASTCDAssociation> newAssocs = new HashSet<>(
        expander1.getDummies4Diff(typeList, COMMON_INTERFACE));

    //Add missing Enums and new EnumConstant if Enum is <<complete>> in second.
    expander1.addNewEnumConstants(second.getCDDefinition().getCDEnumsList());

    //create common interface for all classes in first
    CD4CodeMill.scopesGenitorDelegator().createFromAST(first);
    createCommonInterface(first, COMMON_INTERFACE);

    // add subclass to each interface and abstract class
    addSubClasses4Diff(first);

    // add new associations for classes that are only <<complete>> in second
    expander1.addAssociationsWithoutConflicts(newAssocs);

    // add a unidirectional super-association in first for each association in second
    Set<ASTCDAssociation> superSet = expander1.buildSuperAssociations(
        second.getCDDefinition().getCDAssociationsList(), COMMON_INTERFACE);
    expander1.addAssociationsWithoutConflicts(superSet);

    /*
    transform second
     */

    //re-build symbol tables
    CD4CodeMill.scopesGenitorDelegator().createFromAST(first);
    CD4CodeMill.scopesGenitorDelegator().createFromAST(second);

    //create common interface for all classes in second
    createCommonInterface(second, COMMON_INTERFACE);

    // add classes, interfaces and attributes exclusive to first
    expander2.addMissingTypesAndAttributes(first.getCDDefinition().getCDClassesList());
    expander2.addMissingTypesAndAttributes(first.getCDDefinition().getCDInterfacesList());

    // add enums and enum constants exclusive to first
    expander2.addMissingEnumsAndConstants(first.getCDDefinition().getCDEnumsList());

    // add inheritance relation to first, unless it causes cyclical inheritance
    copyInheritance(first, second);

    // add a unidirectional super-association in second for each association in first
    /*
    superSet = expander2.buildSuperAssociations(first.getCDDefinition().getCDAssociationsList(),
        commonInterface);
    expander1.addAssociationsWithoutConflicts(superSet);
    */

    // add all non-conflicting associations from first to second
    Set<ASTCDAssociation> noConflictSet = new HashSet<>(
        first.getCDDefinition().getCDAssociationsList());
    noConflictSet.removeAll(CDAssociationHelper.collectConflictingAssociations(first, second));

    expander2.addAssociationClones(noConflictSet);

  }

  public static void addDummyClass4Associations(ASTCDCompilationUnit first, String dummyName) {
    CD4CodeMill.scopesGenitorDelegator().createFromAST(first);
    FullExpander expander = new FullExpander(new VariableExpander(first));
    expander.addDummyClass(dummyName);
  }

  public static void addSubClasses4Diff(ASTCDCompilationUnit first) {
    CD4CodeMill.scopesGenitorDelegator().createFromAST(first);
    FullExpander expander = new FullExpander(new VariableExpander(first));

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
  public static void handleAssocDirections(ASTCDCompilationUnit first,
      ASTCDCompilationUnit second) {

    CD4CodeMill.scopesGenitorDelegator().createFromAST(first);
    CD4CodeMill.scopesGenitorDelegator().createFromAST(second);

    FullExpander expander1 = new FullExpander(new VariableExpander(first));
    FullExpander expander2 = new FullExpander(new VariableExpander(second));

    expander1.updateDir4Diff(second.getCDDefinition().getCDAssociationsList());
    expander2.updateDir2Match(first.getCDDefinition().getCDAssociationsList());
    expander1.updateUnspecifiedDir2Default();
    expander2.updateUnspecifiedDir2Default();
  }

  public void createCommonInterface(ASTCDCompilationUnit cd, String commonInterface) {

    ICD4CodeArtifactScope scope = CD4CodeMill.scopesGenitorDelegator().createFromAST(cd);
    FullExpander expander = new FullExpander(new BasicExpander(cd));
    if (expander.addDummyInterface(commonInterface).isPresent()) {

      for (ASTCDClass current : cd.getCDDefinition().getCDClassesList()) {
        scope = CD4CodeMill.scopesGenitorDelegator().createFromAST(cd);
        if (CDInheritanceHelper.getAllSuper(current, scope).size() == 1 && (!current.getName()
            .equals(commonInterface))) {
          Set<String> implementsSet = CDInheritanceHelper.getDirectInterfaces(current, scope)
              .stream()
              .map(i -> i.getSymbol().getFullName())
              .collect(Collectors.toSet());
          implementsSet.add(commonInterface);
          current.setCDInterfaceUsage(CDInterfaceUsageFacade.getInstance()
              .createCDInterfaceUsage(implementsSet.toArray(new String[0])));
        }
      }

      for (ASTCDInterface current : cd.getCDDefinition().getCDInterfacesList()) {
        scope = CD4CodeMill.scopesGenitorDelegator().createFromAST(cd);
        if (CDInheritanceHelper.getAllSuper(current, scope).size() == 1 && (!current.getName()
            .equals(commonInterface))) {
          Set<String> extendsSet = CDInheritanceHelper.getDirectInterfaces(current, scope)
              .stream()
              .map(i -> i.getSymbol().getFullName())
              .collect(Collectors.toSet());
          extendsSet.add(commonInterface);
          current.setCDExtendUsage(CDExtendUsageFacade.getInstance()
              .createCDExtendUsage(extendsSet.toArray(new String[0])));
        }
      }

    }

    CD4CodeMill.scopesGenitorDelegator().createFromAST(cd);

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
    for (ASTCDInterface current : interfaces) {
      inheritanceGraph.get(current)
          .removeAll(inheritanceGraph.get(current)
              .stream()
              .filter(superType -> !(interfaces.contains(superType)))
              .collect(Collectors.toSet()));
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
    FullExpander expander = new FullExpander(new VariableExpander(targetCD));

    for (ASTCDInterface current : interfaces) {
      Set<String> extendsSet = new HashSet<>();
      for (ASTCDType superType : inheritanceGraph.get(current)) {
        if (interfaces.contains(superType)) {
          extendsSet.add(superType.getSymbol().getFullName());
        }
      }
      expander.updateExtends(current, extendsSet);
    }
    for (ASTCDClass current : classes) {
      Set<String> extendsSet = new HashSet<>();
      Set<String> implementsSet = new HashSet<>();
      for (ASTCDType superType : inheritanceGraph.get(current)) {
        if (classes.contains(superType)) {
          extendsSet.add(superType.getSymbol().getFullName());
        }
        else if (interfaces.contains(superType)) {
          implementsSet.add(superType.getSymbol().getFullName());
        }
      }
      expander.updateExtends(current, extendsSet);
      expander.updateImplements(current, implementsSet);
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
