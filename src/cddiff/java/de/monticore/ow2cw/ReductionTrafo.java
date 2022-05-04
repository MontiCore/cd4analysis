package de.monticore.ow2cw;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cd4code._symboltable.ICD4CodeGlobalScope;
import de.monticore.cd4code.trafo.CD4CodeDirectCompositionTrafo;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Stream;

public class ReductionTrafo {

  protected MCBasicTypesFullPrettyPrinter pp = new MCBasicTypesFullPrettyPrinter(
      new IndentPrinter());

  /**
   * transform 2 CDs for Open-to-Closed World Reduction of CDDiff
   * completeSymbolTable() cannot be used, because CDs likely define the same symbols
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
        modStation1.addNewSubClass(astcdClass.getName()+"4Diff",astcdClass);
      }
    }
    for (ASTCDInterface astcdInterface : first.getCDDefinition().getCDInterfacesList()) {
      modStation1.addNewSubClass(astcdInterface.getName()+"4Diff",astcdInterface);
    }

    // add classes exclusive to second as classes without attributes, extends and implements
    for (ASTCDClass astcdClass : second.getCDDefinition().getCDClassesList()) {
      Optional<CDTypeSymbol> opt = scope1.resolveCDTypeDown(astcdClass.getSymbol().getFullName());
      if (!opt.isPresent()) {

        // construct empty clone

        ASTModifier newModifier =
            CD4CodeMill.modifierBuilder().build();

        ASTCDClass newClass = CD4CodeMill.cDClassBuilder()
            .setName(astcdClass.getName())
            .setCDExtendUsageAbsent()
            .setCDInterfaceUsageAbsent()
            .setModifier(newModifier)
            .build();
        modStation1.addClass2Package(newClass, modStation1.determinePackageName(astcdClass));
      }
    }
    CD4CodeMill.scopesGenitorDelegator().createFromAST(first);

    //add associations exclusive to second, but without cardinalities
    // todo: visitor?
    for (ASTCDAssociation assoc2 : second.getCDDefinition().getCDAssociationsList()) {
      boolean found =
          first.getCDDefinition().getCDAssociationsList()
              .stream()
              .anyMatch(assoc1 -> sameAssociation(assoc1,assoc2));
      if (!found) {
        ASTCDAssociation newAssoc = assoc2.deepClone();
        newAssoc.getRight().setCDCardinalityAbsent();
        newAssoc.getLeft().setCDCardinalityAbsent();
        //todo: check if class/interface has stereotype ""
        first.getCDDefinition().getCDElementList().add(newAssoc);
      }
    }
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
    completeInheritanceInSecond(first,second);

    // add associations exclusive to first
    for (ASTCDAssociation assoc1 : first.getCDDefinition().getCDAssociationsList()) {
      boolean found = false;
      for (ASTCDAssociation assoc2 : second.getCDDefinition().getCDAssociationsList()) {
        if (sameAssociation(assoc1, assoc2)) {
          // specify undirected associations
          //todo: normalize direction
          copyDirection(assoc1, assoc2);
          found = true;
          break;
        }
      }
      if (!found) {
        second.getCDDefinition().getCDElementList().add(assoc1.deepClone());
      }
    }
  }

  /**
   * check if assoc1 and assoc2 are the same association
   * i.e. references AND role names match
   */
  protected boolean sameAssociation(ASTCDAssociation assoc1, ASTCDAssociation assoc2) {
    return strictMatch(assoc1,assoc2) || reverseMatch(assoc1,assoc2);
  }

  /**
   * Copy direction from assoc1 to assoc2 if assoc2 has underspecified direction
   */
  protected void copyDirection(ASTCDAssociation assoc1, ASTCDAssociation assoc2) {
    if (strictMatch(assoc1,assoc2)){
      if (!(assoc2.getCDAssocDir().isDefinitiveNavigableLeft() || assoc2.getCDAssocDir()
          .isDefinitiveNavigableRight())) {
        assoc2.setCDAssocDir(assoc1.getCDAssocDir().deepClone());
      }
      return;
    }
    if (reverseMatch(assoc1, assoc2)){
      if (!(assoc2.getCDAssocDir().isDefinitiveNavigableLeft() || assoc2.getCDAssocDir()
          .isDefinitiveNavigableRight())) {
        if (assoc1.getCDAssocDir().isBidirectional()) {
          assoc2.setCDAssocDir(CD4AnalysisMill.cDBiDirBuilder().build());
          return;
        }
        if (assoc1.getCDAssocDir().isDefinitiveNavigableRight()) {
          assoc2.setCDAssocDir(CD4AnalysisMill.cDLeftToRightDirBuilder().build());
          return;
        }
        if (assoc1.getCDAssocDir().isDefinitiveNavigableLeft()) {
          assoc2.setCDAssocDir(CD4AnalysisMill.cDBiDirBuilder().build());
        }
      }
    }
  }

  private boolean strictMatch(ASTCDAssociation assoc1, ASTCDAssociation assoc2) {
    // check left reference
    if (!assoc1.getLeftQualifiedName()
        .getQName()
        .equals(assoc2.getLeftQualifiedName().getQName())) {
      return false;
    }

    // check right reference
    if (!assoc1.getRightQualifiedName()
        .getQName()
        .equals(assoc2.getRightQualifiedName().getQName())) {
      return false;
    }

    String roleName1;
    String roleName2;

    // check left role names
    if (assoc1.getLeft().isPresentCDRole()) {
      roleName1 = assoc1.getLeft().getCDRole().getName();
    }
    else {
      roleName1 = assoc1.getLeftQualifiedName().getQName();
    }

    if (assoc2.getLeft().isPresentCDRole()) {
      roleName2 = assoc2.getLeft().getCDRole().getName();
    }
    else {
      roleName2 = assoc2.getLeftQualifiedName().getQName();
    }

    if (!roleName1.equals(roleName2)) {
      return false;
    }

    // check right role names
    if (assoc1.getRight().isPresentCDRole()) {
      roleName1 = assoc1.getRight().getCDRole().getName();
    }
    else {
      roleName1 = assoc1.getRightQualifiedName().getQName();
    }

    if (assoc2.getRight().isPresentCDRole()) {
      roleName2 = assoc2.getRight().getCDRole().getName();
    }
    else {
      roleName2 = assoc2.getRightQualifiedName().getQName();
    }

    return roleName1.equals(roleName2);
  }

  private boolean reverseMatch(ASTCDAssociation assoc1, ASTCDAssociation assoc2) {
    // check left reference
    if (!assoc1.getLeftQualifiedName()
        .getQName()
        .equals(assoc2.getRightQualifiedName().getQName())) {
      return false;
    }

    // check right reference
    if (!assoc1.getRightQualifiedName()
        .getQName()
        .equals(assoc2.getLeftQualifiedName().getQName())) {
      return false;
    }

    String roleName1;
    String roleName2;

    // check left role names
    if (assoc1.getLeft().isPresentCDRole()) {
      roleName1 = assoc1.getLeft().getCDRole().getName();
    }
    else {
      roleName1 = assoc1.getLeftQualifiedName().getQName();
    }

    if (assoc2.getRight().isPresentCDRole()) {
      roleName2 = assoc2.getRight().getCDRole().getName();
    }
    else {
      roleName2 = assoc2.getRightQualifiedName().getQName();
    }

    if (!roleName1.equals(roleName2)) {
      return false;
    }

    // check right role names
    if (assoc1.getRight().isPresentCDRole()) {
      roleName1 = assoc1.getRight().getCDRole().getName();
    }
    else {
      roleName1 = assoc1.getRightQualifiedName().getQName();
    }

    if (assoc2.getLeft().isPresentCDRole()) {
      roleName2 = assoc2.getLeft().getCDRole().getName();
    }
    else {
      roleName2 = assoc2.getLeftQualifiedName().getQName();
    }

    return roleName1.equals(roleName2);
  }

  /**
   * add each inheritance-relations exclusive to first to second
   * unless it causes cyclical inheritance
   */
  protected void completeInheritanceInSecond(ASTCDCompilationUnit first, ASTCDCompilationUnit second) {

    ICD4CodeArtifactScope scope2;

    // for each class in first, find the corresponding class in second and add all
    // legal extends/implements relations
    for (ASTCDClass srcClass : first.getCDDefinition().getCDClassesList()) {

      // re-build symbol table
      scope2 = CD4CodeMill.scopesGenitorDelegator().createFromAST(second);

      ASTCDClass targetClass = null;

      // I don't use resolve to avoid reflection
      for (ASTCDClass someClass : second.getCDDefinition().getCDClassesList()){
        if (srcClass.getSymbol().getFullName().equals(someClass.getSymbol().getFullName())){
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
          if (isNewSuper(superType, targetClass, scope2) && inducesNoInheritanceCycle(superType,
              targetClass, scope2)) {
            extendsList.add(superType);
          }
          targetClass.setCDExtendUsage(
                CD4CodeMill.cDExtendUsageBuilder().addAllSuperclass(extendsList).build());

        }
        List<ASTMCObjectType> interfaceList = new ArrayList<>(targetClass.getInterfaceList());
        for (ASTMCObjectType superType : srcClass.getInterfaceList()) {
          if (isNewSuper(superType, targetClass, scope2) && inducesNoInheritanceCycle(superType,
              targetClass, scope2)) {
            interfaceList.add(superType);
          }
          targetClass.setCDInterfaceUsage(
                CD4CodeMill.cDInterfaceUsageBuilder().addAllInterface(interfaceList).build());
        }
      }
    }
    for (ASTCDInterface srcInterface : first.getCDDefinition().getCDInterfacesList()) {
      scope2 = CD4CodeMill.scopesGenitorDelegator().createFromAST(second);

      ASTCDInterface targetInterface = null;

      for (ASTCDInterface someInterface : second.getCDDefinition().getCDInterfacesList()){
        if (srcInterface.getSymbol().getFullName().equals(someInterface.getSymbol().getFullName())){
          targetInterface = someInterface;
        }
      }

      if (targetInterface == null) {
        Log.error(
            String.format("0xCDD09: Could not find interface %s",
                srcInterface.getSymbol().getFullName()));
      }
      else {
        List<ASTMCObjectType> extendsList = new ArrayList<>(targetInterface.getInterfaceList());
        for (ASTMCObjectType superType : srcInterface.getInterfaceList()) {
          if (isNewSuper(superType, targetInterface,scope2) && inducesNoInheritanceCycle(superType,
              targetInterface,scope2)) {
            extendsList.add(superType);
          }
          targetInterface.setCDExtendUsage(
                CD4CodeMill.cDExtendUsageBuilder().addAllSuperclass(extendsList).build());
        }
      }
    }
    CD4CodeMill.scopesGenitorDelegator().createFromAST(second);
    removeRedundantAttributes(second);
  }

  /**
   * check if newSuper is not already a superclass/interface of targetNode
   */
  protected boolean isNewSuper(ASTMCObjectType newSuper, ASTCDType targetNode,
      ICD4CodeArtifactScope artifactScope) {
    for (ASTCDType oldSuper : getAllSuper(targetNode, artifactScope)) {
      if (oldSuper.getSymbol().getFullName().contains(newSuper.printType(pp))) {
        return false;
      }
    }
    return true;
  }

  /**
   * check if newSuper does not cause cyclical inheritance
   */
  protected boolean inducesNoInheritanceCycle(ASTMCObjectType newSuper, ASTCDType targetNode,
      ICD4CodeArtifactScope artifactScope) {

    for (ASTCDType superSuper : getAllSuper(resolveClosestType(targetNode, newSuper.printType(pp),
        artifactScope), artifactScope)) {
      if (superSuper.getSymbol().getFullName().equals(targetNode.getSymbol().getFullName())) {
        return false;
      }
    }
    return true;
  }

  /**
   * remove redundant attributes
   */
  protected void removeRedundantAttributes(ASTCDCompilationUnit ast) {
    ICD4CodeArtifactScope artifactScope = CD4CodeMill.scopesGenitorDelegator().createFromAST(ast);
    for (ASTCDClass astcdClass : ast.getCDDefinition().getCDClassesList()) {
      for (ASTCDAttribute attribute : astcdClass.getCDAttributeList()) {
        if (findInSuper(attribute, astcdClass, artifactScope)) {
          astcdClass.removeCDMember(attribute);
        }
      }
    }
    for (ASTCDInterface astcdInterface : ast.getCDDefinition().getCDInterfacesList()) {
      for (ASTCDAttribute attribute : astcdInterface.getCDAttributeList()) {
        if (findInSuper(attribute, astcdInterface, artifactScope)) {
          astcdInterface.removeCDMember(attribute);
        }
      }
    }
    CD4CodeMill.scopesGenitorDelegator().createFromAST(ast);
  }

  /**
   * check if attribute is in superclass/interface
   */
  protected boolean findInSuper(ASTCDAttribute attribute1, ASTCDType cdType,
      ICD4CodeArtifactScope artifactScope) {
    for (ASTCDType supertype : getAllSuper(cdType, artifactScope)) {
      if (supertype != cdType) {
        for (ASTCDAttribute attribute2 : supertype.getCDAttributeList()) {
          if (attribute1.getName().equals(attribute2.getName())) {
            return true;
          }
        }
      }
    }
    return false;
  }

  /**
   * return all superclasses and interfaces of cdType
   */
  protected List<ASTCDType> getAllSuper(ASTCDType cdType, ICD4CodeArtifactScope artifactScope) {
    List<ASTCDType> superList = new ArrayList<>(getDirectSuperClasses(cdType,artifactScope));
    superList.addAll(getDirectInterfaces(cdType,artifactScope));

    List<ASTCDType> nextSuperSuperList = new ArrayList<>();
    for (ASTCDType nextSuper : superList) {
      nextSuperSuperList.addAll(getAllSuper(nextSuper, artifactScope));
    }
    superList.addAll(nextSuperSuperList);
    superList.add(cdType);
    return superList;
  }

  /**
   * return all superclasses from SuperClassList
   * since I cannot use getSymbol().getSuperClassesOnly()
   */
  protected List<ASTCDType> getDirectSuperClasses(ASTCDType cdType, ICD4CodeArtifactScope artifactScope) {
    List<ASTCDType> extendsList = new ArrayList<>();
    for (ASTMCObjectType superType : cdType.getSuperclassList()) {
      extendsList.add(resolveClosestType(cdType,superType.printType(pp),artifactScope));
    }
    return extendsList;
  }

  /**
   * return all interfaces from InterfaceList
   * since I cannot use getSymbol().getInterfaceList()
   */
  protected List<ASTCDType> getDirectInterfaces(ASTCDType cdType, ICD4CodeArtifactScope artifactScope) {
    List<ASTCDType> interfaceList = new ArrayList<>();
    for (ASTMCObjectType superType : cdType.getInterfaceList()) {
      interfaceList.add(resolveClosestType(cdType,superType.printType(pp),artifactScope));
    }
    return interfaceList;
  }

  /**
   * helper-method to resolve extended/implemented class/interface
   */
  protected ASTCDType resolveClosestType(ASTCDType srcNode,
      String targetName, ICD4CodeArtifactScope artifactScope){

    List<CDTypeSymbol> symbolList =
        artifactScope.resolveCDTypeDownMany(targetName);

    if (symbolList.isEmpty()){
      Log.error(String.format("0xCDD15: Could not resolve %s", targetName));
    }

    CDTypeSymbol current = symbolList.get(0);
    int currentMatch = getPositionWhereTextDiffer(current.getFullName(),srcNode.getSymbol().getFullName());
    int nextMatch;

    for (CDTypeSymbol symbol : symbolList){
      nextMatch = getPositionWhereTextDiffer(symbol.getFullName(),
          srcNode.getSymbol().getFullName());
      if (currentMatch < nextMatch){
        current = symbol;
      }

    }

    return current.getAstNode();

  }

  /**
   * could not find an existing method like that
   */
  private int getPositionWhereTextDiffer(String a, String b) {
    int position = 0;
    while ( b.length() > position &&
        a.length() > position &&
        a.charAt(position) == b.charAt(position)) {
      position++;
    }
    return position;
  }


  protected <T,E> Stream<T> retainAll(Collection<T> input, Collection<E> otherSet,
      BiFunction<T,E,Boolean> pred){
    return input.stream()
        .filter(z->
            otherSet.stream().anyMatch(o->pred.apply(z,o))
        );
  }
}
