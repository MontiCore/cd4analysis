package de.monticore.cddiff.syndiff.imp;

import static de.monticore.cddiff.syndiff.imp.Syn2SemDiffHelper.getSpannedInheritance;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cd4code.trafo.CD4CodeDirectCompositionTrafo;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._ast.ASTCDAssociationNode;
import de.monticore.cdassociation._ast.ASTCDRole;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cddiff.syndiff.datastructures.AssocStruct;
import de.monticore.cddiff.syndiff.interfaces.ICDSyntaxDiff;
import de.monticore.cddiff.syndiff.datastructures.*;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.matcher.MatchingStrategy;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;
import edu.mit.csail.sdg.alloy4.Pair;

import java.util.*;

import static de.monticore.cddiff.ow2cw.CDAssociationHelper.sameAssociation;
import static de.monticore.cddiff.ow2cw.CDAssociationHelper.sameAssociationInReverse;
import static de.monticore.cddiff.ow2cw.CDInheritanceHelper.getAllSuper;
import static de.monticore.cddiff.syndiff.imp.Syn2SemDiffHelper.*;

public class CDSyntaxDiff extends CDDiffHelper implements ICDSyntaxDiff {
  private ASTCDCompilationUnit srcCD;
  private ASTCDCompilationUnit tgtCD;
  private List<CDTypeDiff> changedClasses;
  private List<CDAssocDiff> changedAssocs;
  private List<ASTCDClass> addedClasses;
  private List<ASTCDClass> deletedClasses;
  private List<ASTCDEnum> addedEnums;
  private List<ASTCDEnum> deletedEnums;
  private List<ASTCDAssociation> addedAssocs;
  private List<ASTCDAssociation> deletedAssocs;
  private List<Pair<ASTCDClass, List<ASTCDClass>>> addedInheritance;
  private List<Pair<ASTCDClass, List<ASTCDClass>>> deletedInheritance;
  private List<Pair<ASTCDClass, ASTCDClass>> matchedClasses;
  private List<Pair<ASTCDEnum, ASTCDEnum>> matchedEnums;
  private List<Pair<ASTCDInterface, ASTCDInterface>> matchedInterfaces;
  private List<Pair<ASTCDAssociation, ASTCDAssociation>> matchedAssocs;
  //protected MatchingStrategy<ASTCDType> typeMatcher;
  //protected MatchingStrategy<ASTCDAssociation> assocMatcher;
  //Print
  protected StringBuilder outPutAll;
  protected StringBuilder cd1Colored;
  protected StringBuilder cd2Colored;
  protected StringBuilder outPutAllNC;
  //CD4CodeFullPrettyPrinter pp = new CD4CodeFullPrettyPrinter(new IndentPrinter());
  //Print end

  public Syn2SemDiffHelper getHelper() {
    return helper;
  }

  public Syn2SemDiffHelper helper = Syn2SemDiffHelper.getInstance();

  public CDSyntaxDiff(ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD,
                      MatchingStrategy<ASTCDType> typeMatcher,
                      MatchingStrategy<ASTCDAssociation> assocMatcher) {
    this.srcCD = srcCD;
    this.tgtCD = tgtCD;
    helper.setSrcCD(srcCD);
    helper.setTgtCD(tgtCD);
    this.matchedClasses = new ArrayList<>();
    this.matchedEnums = new ArrayList<>();
    this.matchedInterfaces = new ArrayList<>();
    this.matchedAssocs = new ArrayList<>();
    this.changedClasses = new ArrayList<>();
    this.changedAssocs = new ArrayList<>();
    this.addedClasses = new ArrayList<>();
    this.deletedClasses = new ArrayList<>();
    this.addedEnums = new ArrayList<>();
    this.deletedEnums = new ArrayList<>();
    this.deletedAssocs = new ArrayList<>();
    this.addedAssocs = new ArrayList<>();

    CD4CodeFullPrettyPrinter pp = new CD4CodeFullPrettyPrinter(new IndentPrinter());
    // Trafo to make in-class declarations of compositions appear in the association list
    new CD4CodeDirectCompositionTrafo().transform(srcCD);
    new CD4CodeDirectCompositionTrafo().transform(tgtCD);

    // Create scopes for both class diagrams
    ICD4CodeArtifactScope scopeSrcCD = CD4CodeMill.scopesGenitorDelegator().createFromAST(srcCD);
    ICD4CodeArtifactScope scopeTgtCD = CD4CodeMill.scopesGenitorDelegator().createFromAST(tgtCD);

    addAllChangedClasses(srcCD, tgtCD);
    addAllChangedAssocs();
    addAllAddedClasses(srcCD, tgtCD);
    addAllDeletedClasses(srcCD, tgtCD);
    addAllAddedEnums(srcCD, tgtCD);
    addAllDeletedEnums(srcCD, tgtCD);
    addAllAddedAssocs(srcCD, tgtCD);
    addAllDeletedAssocs(srcCD, tgtCD);
    addAllMatchedClasses(srcCD, tgtCD, typeMatcher);
    addAllMatchedInterfaces(srcCD, tgtCD, typeMatcher);
    addAllMatchedAssocs(srcCD, tgtCD, assocMatcher);

    StringBuilder initial = new StringBuilder();
    StringBuilder classPrints = new StringBuilder();
    StringBuilder classPrintsNC = new StringBuilder();
    StringBuilder interfacePrints = new StringBuilder();
    StringBuilder interfacePrintsNC = new StringBuilder();
    StringBuilder enumPrints = new StringBuilder();
    StringBuilder enumPrintsNC = new StringBuilder();
    StringBuilder assoPrints = new StringBuilder();
    StringBuilder assoPrintsNC = new StringBuilder();

    List<org.apache.commons.lang3.tuple.Pair<Integer, String>> onlyCD1Sort = new ArrayList<>();
    List<org.apache.commons.lang3.tuple.Pair<Integer, String>> onlyCD2Sort = new ArrayList<>();

    initial
      .append(System.lineSeparator())
      .append("In the following the syntax diff between ")
      .append(tgtCD.getCDDefinition().getName())
      .append(" and ")
      .append(srcCD.getCDDefinition().getName())
      .append(" is created")
      .append(System.lineSeparator())
      .append(System.lineSeparator());

    for (Pair<ASTCDClass,ASTCDClass> x : matchedClasses) {
      CDTypeDiff t = new CDTypeDiff(x.a, x.b, scopeSrcCD, scopeTgtCD);
      onlyCD1Sort.add(org.apache.commons.lang3.tuple.Pair.of(t.getTgtElem().get_SourcePositionStart().getLine(), t.printSrcCD()));
      onlyCD2Sort.add(org.apache.commons.lang3.tuple.Pair.of(t.getSrcElem().get_SourcePositionStart().getLine(), t.printTgtCD()));
    }

    for (Pair<ASTCDInterface,ASTCDInterface> x : matchedInterfaces) {
      CDTypeDiff t = new CDTypeDiff(x.a, x.b, scopeSrcCD, scopeTgtCD);
      onlyCD1Sort.add(org.apache.commons.lang3.tuple.Pair.of(t.getTgtElem().get_SourcePositionStart().getLine(), t.printSrcCD()));
      onlyCD2Sort.add(org.apache.commons.lang3.tuple.Pair.of(t.getSrcElem().get_SourcePositionStart().getLine(), t.printTgtCD()));
    }

    for (Pair<ASTCDEnum,ASTCDEnum> x : matchedEnums) {
      CDTypeDiff t = new CDTypeDiff(x.a, x.b, scopeSrcCD, scopeTgtCD);
      onlyCD1Sort.add(org.apache.commons.lang3.tuple.Pair.of(t.getTgtElem().get_SourcePositionStart().getLine(), t.printSrcCD()));
      onlyCD2Sort.add(org.apache.commons.lang3.tuple.Pair.of(t.getSrcElem().get_SourcePositionStart().getLine(), t.printTgtCD()));
    }

    for (Pair<ASTCDAssociation,ASTCDAssociation> x : matchedAssocs) {
      CDAssocDiff a = new CDAssocDiff(x.a, x.b);
      onlyCD1Sort.add(org.apache.commons.lang3.tuple.Pair.of(a.getTgtElem().get_SourcePositionStart().getLine(), a.printTgtAssoc()));
      onlyCD2Sort.add(org.apache.commons.lang3.tuple.Pair.of(a.getTgtElem().get_SourcePositionStart().getLine(), a.printSrcAssoc()));
    }


    if (!deletedClasses.isEmpty()) {
      classPrints
        .append("Line Deleted Classes from CD1 (")
        .append(tgtCD.getCDDefinition().getName())
        .append(") :")
        .append(System.lineSeparator());
      classPrintsNC
        .append("Line Deleted Classes from CD1 (")
        .append(tgtCD.getCDDefinition().getName())
        .append(") :")
        .append(System.lineSeparator());

      for (ASTCDClass x : deletedClasses) {
        String tmp = COLOR_DELETE + pp.prettyprint(x) + RESET;
        classPrints
          .append("CD1: ")
          .append(x.get_SourcePositionStart().getLine())
          .append("   ")
          .append(tmp);
        classPrintsNC
          .append("CD1: ")
          .append(x.get_SourcePositionStart().getLine())
          .append("   ")
          .append(pp.prettyprint(x));
        onlyCD1Sort.add(org.apache.commons.lang3.tuple.Pair.of(x.get_SourcePositionStart().getLine(), tmp));
      }
    }

    if (!addedClasses.isEmpty()) {
      classPrints
        .append("Line Added Classes to CD2 (")
        .append(srcCD.getCDDefinition().getName())
        .append(") :")
        .append(System.lineSeparator());
      classPrintsNC
        .append("Line Added Classes to CD2 (")
        .append(srcCD.getCDDefinition().getName())
        .append(") :")
        .append(System.lineSeparator());

      for (ASTCDClass x : addedClasses) {
        String tmp = COLOR_ADD + pp.prettyprint(x) + RESET;
        classPrints
          .append("CD2: ")
          .append(x.get_SourcePositionStart().getLine())
          .append("   ")
          .append(tmp);
        classPrintsNC
          .append("CD2: ")
          .append(x.get_SourcePositionStart().getLine())
          .append("   ")
          .append(pp.prettyprint(x));
        onlyCD2Sort.add(org.apache.commons.lang3.tuple.Pair.of(x.get_SourcePositionStart().getLine(), tmp));
      }
    }

    if (!deletedEnums.isEmpty()) {
      enumPrints
        .append("Line Deleted Enums from CD1 (")
        .append(tgtCD.getCDDefinition().getName())
        .append(") :")
        .append(System.lineSeparator());
      enumPrintsNC
        .append("Line Deleted Enums from CD1 (")
        .append(tgtCD.getCDDefinition().getName())
        .append(") :")
        .append(System.lineSeparator());

      for (ASTCDEnum x : deletedEnums) {
        String tmp = COLOR_DELETE + pp.prettyprint(x) + RESET;
        enumPrints
          .append("CD1: ")
          .append(x.get_SourcePositionStart().getLine())
          .append("   ")
          .append(tmp);
        enumPrintsNC
          .append("CD1: ")
          .append(x.get_SourcePositionStart().getLine())
          .append("   ")
          .append(pp.prettyprint(x));
        onlyCD1Sort.add(org.apache.commons.lang3.tuple.Pair.of(x.get_SourcePositionStart().getLine(), tmp));
      }
    }

    if (!addedEnums.isEmpty()) {
      enumPrints
        .append("Line Added Enums to CD2 (")
        .append(srcCD.getCDDefinition().getName())
        .append(") :")
        .append(System.lineSeparator());
      enumPrintsNC
        .append("Line Added Enums to CD2 (")
        .append(srcCD.getCDDefinition().getName())
        .append(") :")
        .append(System.lineSeparator());

      for (ASTCDEnum x : addedEnums) {
        String tmp = COLOR_ADD + pp.prettyprint(x) + RESET;
        enumPrints
          .append("CD2: ")
          .append(x.get_SourcePositionStart().getLine())
          .append("   ")
          .append(tmp);
        enumPrintsNC
          .append("CD2: ")
          .append(x.get_SourcePositionStart().getLine())
          .append("   ")
          .append(pp.prettyprint(x));
        onlyCD2Sort.add(org.apache.commons.lang3.tuple.Pair.of(x.get_SourcePositionStart().getLine(), tmp));
      }
    }

    if (!deletedAssocs.isEmpty()) {
      assoPrints
        .append("Line Deleted Associations from CD1 (")
        .append(tgtCD.getCDDefinition().getName())
        .append(") :")
        .append(System.lineSeparator());
      assoPrintsNC
        .append("Line Deleted Associations from CD1 (")
        .append(tgtCD.getCDDefinition().getName())
        .append(") :")
        .append(System.lineSeparator());

      for (ASTCDAssociationNode x : deletedAssocs) {
        String tmp = COLOR_DELETE + pp.prettyprint(x) + RESET;
        assoPrints
          .append("CD1: ")
          .append(x.get_SourcePositionStart().getLine())
          .append("   ")
          .append(tmp);
        assoPrintsNC
          .append("CD1: ")
          .append(x.get_SourcePositionStart().getLine())
          .append("   ")
          .append(pp.prettyprint(x));
        onlyCD1Sort.add(org.apache.commons.lang3.tuple.Pair.of(x.get_SourcePositionStart().getLine(), tmp));
      }
    }

    if (!addedAssocs.isEmpty()) {
      assoPrints
        .append("Line Added Associations to CD2 (")
        .append(srcCD.getCDDefinition().getName())
        .append(") :")
        .append(System.lineSeparator());
      assoPrintsNC
        .append("Line Added Associations to CD2 (")
        .append(srcCD.getCDDefinition().getName())
        .append(") :")
        .append(System.lineSeparator());

      for (ASTCDAssociationNode x : addedAssocs) {
        String tmp = COLOR_ADD + pp.prettyprint(x) + RESET;
        assoPrints
          .append("CD2: ")
          .append(x.get_SourcePositionStart().getLine())
          .append("   ")
          .append(tmp);
        assoPrintsNC
          .append("CD2: ")
          .append(x.get_SourcePositionStart().getLine())
          .append("   ")
          .append(pp.prettyprint(x));
        onlyCD2Sort.add(org.apache.commons.lang3.tuple.Pair.of(x.get_SourcePositionStart().getLine(), tmp));
      }
    }

    StringBuilder outPutAll = new StringBuilder();
    outPutAll.append(initial);

    StringBuilder outPutAllNC = new StringBuilder();
    outPutAllNC.append(initial);

    onlyCD1Sort.sort(Comparator.comparing(p -> +p.getLeft()));
    StringBuilder outPutCD1 = new StringBuilder();
    outPutCD1.append("classdiagram ").append(tgtCD.getCDDefinition().getName()).append(" {");
    for (org.apache.commons.lang3.tuple.Pair<Integer, String> x : onlyCD1Sort) {
      outPutCD1.append(System.lineSeparator()).append(x.getValue());
    }
    outPutCD1.append(System.lineSeparator()).append("}");
    this.cd1Colored = outPutCD1;

    onlyCD2Sort.sort(Comparator.comparing(p -> +p.getLeft()));
    StringBuilder outPutCD2 = new StringBuilder();
    outPutCD2.append("classdiagram ").append(srcCD.getCDDefinition().getName()).append(" {");
    for (org.apache.commons.lang3.tuple.Pair<Integer, String> x : onlyCD2Sort) {
      outPutCD2.append(System.lineSeparator()).append(x.getValue());
    }
    outPutCD2.append(System.lineSeparator()).append("}");
    this.cd2Colored = outPutCD2;

    outPutAll.append(classPrints);
    outPutAll.append(interfacePrints);
    outPutAll.append(assoPrints);
    outPutAll.append(enumPrints);
    this.outPutAll = outPutAll;

    outPutAllNC.append(classPrintsNC);
    outPutAllNC.append(interfacePrintsNC);
    outPutAllNC.append(assoPrintsNC);
    outPutAllNC.append(enumPrintsNC);
    this.outPutAllNC = outPutAllNC;
  }

  @Override
  public ASTCDCompilationUnit getSrcCD() {
    return srcCD;
  }

  @Override
  public void setSrcCD(ASTCDCompilationUnit srcCD) {
    this.srcCD = srcCD;
  }

  @Override
  public ASTCDCompilationUnit getTgtCD() {
    return tgtCD;
  }

  @Override
  public void setTgtCD(ASTCDCompilationUnit tgtCD) {
    this.tgtCD = tgtCD;
  }

  @Override
  public List<CDTypeDiff> getChangedClasses() {
    return changedClasses;
  }

  @Override
  public void setChangedClasses(List<CDTypeDiff> changedCLasses) {
    this.changedClasses = changedCLasses;
  }

  @Override
  public List<CDTypeDiff> getChangedTypes() {
    return null;
  }

  @Override
  public List<CDAssocDiff> getChangedAssocs() {
    return changedAssocs;
  }

  @Override
  public void setChangedAssocs(List<CDAssocDiff> changedAssocs) {
    this.changedAssocs = changedAssocs;
  }

  @Override
  public List<ASTCDClass> getAddedClasses() {
    return addedClasses;
  }

  @Override
  public void setAddedClasses(List<ASTCDClass> addedClasses) {
    this.addedClasses = addedClasses;
  }

  @Override
  public List<ASTCDClass> getDeletedClasses() {
    return deletedClasses;
  }

  @Override
  public void setDeletedClasses(List<ASTCDClass> deletedClasses) {
    this.deletedClasses = deletedClasses;
  }

  @Override
  public List<ASTCDEnum> getAddedEnums() {
    return addedEnums;
  }

  @Override
  public void setAddedEnums(List<ASTCDEnum> addedEnums) {
    this.addedEnums = addedEnums;
  }

  @Override
  public List<ASTCDEnum> getDeletedEnums() {
    return deletedEnums;
  }

  @Override
  public void setDeletedEnums(List<ASTCDEnum> deletedEnums) {
    this.deletedEnums = deletedEnums;
  }

  @Override
  public List<ASTCDAssociation> getAddedAssocs() {
    return addedAssocs;
  }

  @Override
  public void setAddedAssocs(List<ASTCDAssociation> addedAssocs) {
    this.addedAssocs = addedAssocs;
  }

  @Override
  public List<ASTCDAssociation> getDeletedAssocs() {
    return deletedAssocs;
  }

  @Override
  public void setDeletedAssocs(List<ASTCDAssociation> deletedAssocs) {
    this.deletedAssocs = deletedAssocs;
  }

  public List<Pair<ASTCDClass, List<ASTCDClass>>> getAddedInheritance() {
    return addedInheritance;
  }
  public void setAddedInheritance(List<Pair<ASTCDClass, List<ASTCDClass>>> addedInheritance) {
    this.addedInheritance = addedInheritance;
  }
  public List<Pair<ASTCDClass, List<ASTCDClass>>> getDeletedInheritance() {
    return deletedInheritance;
  }
  public void setDeletedInheritance(List<Pair<ASTCDClass, List<ASTCDClass>>> deletedInheritance) {
    this.deletedInheritance = deletedInheritance;
  }

  @Override
  public List<Pair<ASTCDClass, ASTCDClass>> getMatchedClasses() {
    return matchedClasses;
  }

  @Override
  public List<Pair<ASTCDEnum, ASTCDEnum>> getMatchedEnums() {
    return matchedEnums;
  }

  @Override
  public List<Pair<ASTCDInterface, ASTCDInterface>> getMatchedInterfaces() {
    return matchedInterfaces;
  }

  @Override
  public void setMatchedClasses(List<Pair<ASTCDClass, ASTCDClass>> matchedClasses) {
    this.matchedClasses = matchedClasses;
  }

  @Override
  public List<Pair<ASTCDAssociation, ASTCDAssociation>> getMatchedAssocs() {
    return matchedAssocs;
  }

  @Override
  public void setMatchedAssocs(List<Pair<ASTCDAssociation, ASTCDAssociation>> matchedAssocs) {
    this.matchedAssocs = matchedAssocs;
  }

  @Override
  public void setMatchedEnums(List<Pair<ASTCDEnum, ASTCDEnum>> matchedEnums) {
    this.matchedEnums = matchedEnums;
  }

  @Override
  public void setMatchedInterfaces(List<Pair<ASTCDInterface, ASTCDInterface>> matchedInterfaces) {
    this.matchedInterfaces = matchedInterfaces;
  }

  /**
   * Checks if each of the added classes refactors the old structure. The class must be abstract,
   * its subclasses in the old CD need to have all of its attributes, and it can't have new ones.
   */
  public boolean isSupClass(ASTCDClass astcdClass){
    if (astcdClass.getModifier().isAbstract()){
      List<ASTCDClass> classesToCheck = getSpannedInheritance(helper.getSrcCD(), astcdClass);
      List<ASTCDAttribute> attributes = astcdClass.getCDAttributeList();
      for (ASTCDClass classToCheck : classesToCheck){
        for (ASTCDAttribute attribute : attributes){
          if (Syn2SemDiffHelper.isAttContainedInClass(attribute, classToCheck)){

          } else {
            Set<ASTCDClass> classes = CDDiffUtil.getAllSuperclasses(classToCheck, helper.getSrcCD().getCDDefinition().getCDClassesList());
            classes.remove(astcdClass);
            boolean isContained = false;
            for (ASTCDClass superOfSub : classes){
              if (Syn2SemDiffHelper.isAttContainedInClass(attribute, superOfSub)){
                isContained = true;
                break;
              }
            }
            if (!isContained){
              return false;
            }
          }
        }
      }
    } else {
      return false;
    }
    return true;
  }

  private ASTCDClass findMatchedClass(ASTCDClass astcdClass){
    ASTCDClass matchedClass = null;
    for (Pair<ASTCDClass, ASTCDClass> pair : getMatchedClasses()){
      if(pair.a.equals(astcdClass)){
        matchedClass = pair.b;
      }
    }
    return matchedClass;
  }

  public Set<Pair<ASTCDClass, Set<ASTCDClass>>> deletedClasses(){
    Set<Pair<ASTCDClass, Set<ASTCDClass>>> diff = new HashSet<>();
    for (Pair<ASTCDClass, List<ASTCDClass>> struc : deletedInheritance){
      List<ASTCDClass> superClasses = struc.b;
      Set<ASTCDClass> currentDiff = new HashSet<>();
      for (ASTCDClass superClass : superClasses){
        if (isClassDeleted(struc.a, struc.a)){
          currentDiff.add(superClass);
        }
      }
      if (!currentDiff.isEmpty()){
        diff.add(new Pair<>(struc.a, currentDiff));
      }
    }
    return diff;
  }

  public boolean isClassDeleted(ASTCDClass astcdClass, ASTCDClass subClass){
    //check if a deleted class brings a semantic difference
    //check if all subclasses have the attributes from this class from tgt tgtCD
    //check if there were outgoing(or bidirectional) associations that weren't zero assocs and check if all subclasses have them
    Pair<ASTCDClass, List<ASTCDAttribute>> allAtts = getHelper().getAllAttr( astcdClass);
    if (subClass != null) {
      for (ASTCDAttribute attribute : allAtts.b) {
        boolean conditionSatisfied = false; // Track if the condition is satisfied
        if (!helper.getNotInstanClassesSrc().contains(subClass)
          && !Syn2SemDiffHelper.isAttContainedInClass(attribute, subClass)) {
          Set<ASTCDType> astcdClassList = getAllSuper(subClass, (ICD4CodeArtifactScope) srcCD.getEnclosingScope());
          for (ASTCDType type : astcdClassList) {
            if (type instanceof ASTCDClass
              && !helper.getNotInstanClassesSrc().contains((ASTCDClass) type)) {
              if (Syn2SemDiffHelper.isAttContainedInClass(attribute, (ASTCDClass) type)) {
                conditionSatisfied = true; // Set the flag to true if the condition holds
                break;
              }
            }
          }
        } else {
          conditionSatisfied = true;
        }
        if (!conditionSatisfied) {//found a subclass that doesn't have this attribute
          return true;// Break out of the first loop if the condition is satisfied
        }
      }
    }
    boolean isContained = false;
    for (AssocStruct assocStruct : getHelper().getTrgMap().get(astcdClass)){
      if (areZeroAssocs(assocStruct, assocStruct)) {
        for (AssocStruct baseAssoc : getHelper().getSrcMap().get(subClass)) {
          if (Syn2SemDiffHelper.sameAssociationTypeWithClasses(baseAssoc.getAssociation(), assocStruct.getAssociation())
            || Syn2SemDiffHelper.sameAssociationTypeInReverseWithClasses(baseAssoc.getAssociation(), assocStruct.getAssociation())) {
            isContained = true;
          }
        }
        if (!isContained) {
          return true;
        } else {
          isContained = false;
        }
      }
      List<ASTCDClass> subClasses = getSpannedInheritance(srcCD, subClass);
      for (ASTCDClass sub : subClasses) {
        if (findMatchedClass(sub) != null) {
          for (AssocStruct assocStruct1 : getHelper().getSrcMap().get(sub)) {
            if (Syn2SemDiffHelper.sameAssociationTypeWithClasses(assocStruct1.getAssociation(), assocStruct.getAssociation())
              || Syn2SemDiffHelper.sameAssociationTypeInReverseWithClasses(assocStruct1.getAssociation(), assocStruct.getAssociation())) {
              isContained = true;
            }
          }
          if (!isContained) {
            return true;
          } else {
            isContained = false;
          }
        }
      }
    }
    //check if there were outgoing(or bidirectional) associations that weren't zero assocs and check if all subclasses have them - done (only outgoing are saved to the values of a key)
    return false;
  }

  public Set<Pair<ASTCDClass, Set<ASTCDClass>>> addedInheritance(){
    Set<Pair<ASTCDClass, Set<ASTCDClass>>> diff = new HashSet<>();
    for (Pair<ASTCDClass, List<ASTCDClass>> struc : addedInheritance){
      List<ASTCDClass> subclasses = struc.b;
      Set<ASTCDClass> currentDiff = new HashSet<>();
      for (ASTCDClass subClass : subclasses){
        if (isInheritanceAdded(struc.a, subClass)){
          currentDiff.add(subClass);
        }
      }
      if (!currentDiff.isEmpty()){
        diff.add(new Pair<>(struc.a, currentDiff));
      }
    }
    return diff;
  }

  public Set<InheritanceDiff> mergeInheritanceDiffs(){
    Set<Pair<ASTCDClass, Set<ASTCDClass>>> added = addedInheritance();
    Set<Pair<ASTCDClass, Set<ASTCDClass>>> deleted = deletedClasses();
    Set<InheritanceDiff> set = new HashSet<>();
    for (Pair<ASTCDClass, Set<ASTCDClass>> pair : added){
      InheritanceDiff diff = new InheritanceDiff(new Pair<>(pair.a, findMatchedClass(pair.a)));
      diff.setNewDirectSuper(new ArrayList<>(pair.b));
      set.add(diff);
    }
    for (Pair<ASTCDClass, Set<ASTCDClass>> pair : deleted){
      boolean holds = true;
      for (InheritanceDiff diff : set){
        if (pair.a.equals(diff.getAstcdClasses())){
          diff.setOldDirectSuper(new ArrayList<>(pair.b));
          holds = false;
          break;
        }
      }
      if (!holds){
        InheritanceDiff diff = new InheritanceDiff(new Pair<>(pair.a, findMatchedClass(pair.a)));
        diff.setOldDirectSuper(new ArrayList<>(pair.b));
        set.add(diff);
      }
    }
    return set;
  }

  public boolean isInheritanceAdded(ASTCDClass astcdClass, ASTCDClass subClass) {
    //reversed case
    //check if new attributes existed in the given subclass - use function from CDTypeDiff
    //check if the associations also existed(are subtypes of the associations) in the tgtMap - same subfunction from isClassDeleted
    Pair<ASTCDClass, List<ASTCDAttribute>> allAtts = getHelper().getAllAttr(astcdClass);
    if (subClass != null) {
      for (ASTCDAttribute attribute : allAtts.b) {
        boolean conditionSatisfied = false; // Track if the condition is satisfied
        if (!helper.getNotInstanClassesTgt().contains(astcdClass)
          && !Syn2SemDiffHelper.isAttContainedInClass(attribute, astcdClass)) {
          Set<ASTCDType> astcdClassList = getAllSuper(astcdClass, (ICD4CodeArtifactScope) tgtCD.getEnclosingScope());
          for (ASTCDType type : astcdClassList) {
            if (type instanceof ASTCDClass
              && helper.getNotInstanClassesSrc().contains((ASTCDClass) type)
              && Syn2SemDiffHelper.isAttContainedInClass(attribute, (ASTCDClass) type)) {
              conditionSatisfied = true; // Set the flag to true if the condition holds
              break;
            }
          }
        } else {
          conditionSatisfied = true;
        }
        if (!conditionSatisfied) {//found a subclass that doesn't have this attribute
          return true;// Break out of the first loop if the condition is satisfied
        } else {
          conditionSatisfied = false;
        }
      }
    }
    //check if there were outgoing(or bidirectional) associations that weren't zero assocs and check if all subclasses have them - only outgoing are saved in the map
    boolean isContained = false;
    for (AssocStruct assocStruct : getHelper().getSrcMap().get(astcdClass)) {
      if (areZeroAssocs(assocStruct, assocStruct)) {
        for (AssocStruct baseAssoc : getHelper().getTrgMap().get(subClass)) {
          if (Syn2SemDiffHelper.sameAssociationTypeWithClasses(baseAssoc.getAssociation(), assocStruct.getAssociation())
            || Syn2SemDiffHelper.sameAssociationTypeInReverseWithClasses(baseAssoc.getAssociation(), assocStruct.getAssociation())) {
            isContained = true;
          }
        }
        if (!isContained) {
          return true;
        } else {
          isContained = false;
        }
        ASTCDClass matchedClass = findMatchedClass(subClass);
        List<ASTCDClass> subClasses = getSpannedInheritance(tgtCD, matchedClass);
        for (ASTCDClass sub : subClasses) {
          if (findMatchedClass(sub) != null) {
            for (AssocStruct assocStruct1 : getHelper().getTrgMap().get(sub)) {
              if (Syn2SemDiffHelper.sameAssociationTypeWithClasses(assocStruct1.getAssociation(), assocStruct.getAssociation())
                || Syn2SemDiffHelper.sameAssociationTypeInReverseWithClasses(assocStruct1.getAssociation(), assocStruct.getAssociation())) {
                isContained = true;
              }
            }
            if (!isContained) {
              return true;
            } else {
              isContained = false;
            }
          }
        }
      }
    }
    return false;
  }

  private ASTCDClass findMatchedSrc(ASTCDClass astcdClass){
    for (Pair<ASTCDClass, ASTCDClass> pair : matchedClasses){
      if (pair.b.equals(astcdClass)){
        return pair.a;
      }
    }
    return null;
  }

  public ASTCDClass isAssocDeleted(ASTCDAssociation association, ASTCDClass astcdClass) {
    AssocStruct assocStruct = getAssocStrucForClass(astcdClass, association);
    assert assocStruct != null;
    if (assocStruct.getDirection().equals(AssocDirection.BiDirectional)) {
      if (!(assocStruct.getAssociation().getLeft().getCDCardinality().isMult()
        || assocStruct.getAssociation().getLeft().getCDCardinality().isOpt())
        && !(assocStruct.getAssociation().getRight().getCDCardinality().isMult()
        || assocStruct.getAssociation().getRight().getCDCardinality().isOpt())) {
        if (!astcdClass.getModifier().isAbstract() && !isContainedInSuper(association, astcdClass)) {
          return astcdClass;
        } else {
          return allSubclassesHaveIt(association, astcdClass);
        }
      }
    } else if (assocStruct.getSide().equals(ClassSide.Left)) {
      if (!(assocStruct.getAssociation().getLeft().getCDCardinality().isMult()
        || assocStruct.getAssociation().getLeft().getCDCardinality().isOpt())) {
        if (!astcdClass.getModifier().isAbstract() && !isContainedInSuper(association, astcdClass)) {
          return astcdClass;
        } else {
          return allSubclassesHaveIt(association, astcdClass);
        }
      }
    } else {
      if (!(assocStruct.getAssociation().getRight().getCDCardinality().isMult()
        || assocStruct.getAssociation().getRight().getCDCardinality().isOpt())) {
        if (!astcdClass.getModifier().isAbstract() && !isContainedInSuper(association, astcdClass)) {
          return astcdClass;
        } else {
          return allSubclassesHaveIt(association, astcdClass);
        }
      }
    }
    return null;
  }

  //add those to deleted/added inheritance - done
  private boolean isContainedInSuper(ASTCDAssociation association, ASTCDClass astcdClass){
    for (AssocStruct assocStruct : helper.getSrcMap().get(astcdClass)){
      if (Syn2SemDiffHelper.sameAssociationTypeWithClasses(assocStruct.getAssociation(), association)
        || Syn2SemDiffHelper.sameAssociationTypeInReverseWithClasses(assocStruct.getAssociation(), association)){
        return true;
      }
    }
    return false;
  }

  private ASTCDClass allSubclassesHaveIt(ASTCDAssociation association, ASTCDClass astcdClass){
    for (ASTCDClass subClass : getSpannedInheritance(srcCD, astcdClass)){
      boolean isContained = false;
      for (AssocStruct assocStruct : helper.getSrcMap().get(subClass)){
        if (Syn2SemDiffHelper.sameAssociationTypeWithClasses(assocStruct.getAssociation(), association)
          || Syn2SemDiffHelper.sameAssociationTypeInReverseWithClasses(assocStruct.getAssociation(), association)){
          isContained = true;
          break;
        }
      }
      if (!isContained){
        return subClass;
      }
    }
    return null;
  }

  //look again at added/deleted assocs for missing cases - done
  //add check if we can derive a diff - the class isn't abstract(or not part of notInstantiatableSrc) or this is is true for some subclass
  //List isn't needed - just one class
  //for that check if we have an association from the source target with the same association type and the class isn't abstract
  //or it has a subclass that isn't abstract
  public Pair<ASTCDAssociation, List<ASTCDClass>> deletedAssoc(ASTCDAssociation astcdAssociation){
    List<ASTCDClass> classes = new ArrayList<>();
    if (astcdAssociation.getLeft().isPresentCDCardinality()){
      if (!(astcdAssociation.getLeft().getCDCardinality().isOpt()
        || astcdAssociation.getLeft().getCDCardinality().isMult())){
        ASTCDClass astcdClass = Syn2SemDiffHelper.getConnectedClasses(astcdAssociation, tgtCD).a;
        ASTCDClass matched = isSecondElementInPair(astcdClass);
        if (matched != null){
          classes.add(matched);
        }
      }
    }
    if (astcdAssociation.getRight().isPresentCDCardinality()){
      if (!(astcdAssociation.getRight().getCDCardinality().isMult()
        || astcdAssociation.getRight().getCDCardinality().isOpt())){
        ASTCDClass astcdClass = Syn2SemDiffHelper.getConnectedClasses(astcdAssociation, tgtCD).b;
        ASTCDClass matched = isSecondElementInPair(astcdClass);
        if (matched != null){
          classes.add(matched);
        }
      }
    }
    if (!classes.isEmpty()){
      return new Pair<>(astcdAssociation, classes);
    }
    return null;
  }
//  public Pair<ASTCDAssociation, ASTCDClass> isAssocDeleted(AssocStruct assocStruct){
//    if (!assocStruct.getDirection().equals(AssocDirection.BiDirectional)){
//      ASTCDClass astcdClass;
//      if (assocStruct.getDirection().equals(AssocDirection.LeftToRight)){
//        astcdClass = getConnectedClasses(assocStruct.getAssociation(), srcCD).b;
//      }
//      else {
//        astcdClass = getConnectedClasses(assocStruct.getAssociation(), srcCD).a;
//      }
//      if (astcdClass.getModifier().isAbstract()){
//        ASTCDClass subClass = notAbstractSub(astcdClass);
//        if (getAssocStrucForClass(subClass, assocStruct.getAssociation()) == null){
//          //found diff
//        }
//      }
//      else {
//        if (getAssocStrucForClass(astcdClass, assocStruct.getAssociation()) == null){
//          //found diff
//        }
//      }
//    }
//    else {
//      //assocstruc is bidirectional
//      //I don't think that we need this
//      //With sameAssociation type we still check if both are bidirectional
//      //
//    }
//    return null;
//  }

  /**
   * Find a subclass in srcCD that can be instantiated
   * @param astcdClass root class
   * @return instantiatable subclass
   */
  private ASTCDClass notAbstractSub(ASTCDClass astcdClass){
    List<ASTCDClass> subclasses = getSpannedInheritance(srcCD, astcdClass);
    for (ASTCDClass sub : subclasses){
      ASTCDClass matchedClass = findMatchedClass(astcdClass);
      if (!sub.getModifier().isAbstract()
        && !helper.getNotInstanClassesSrc().contains(sub)){
        return sub;
      }
    }
    return null;
  }

  /**
   * Get the an AssocStruc that has the same type
   * @param astcdClass class to search in
   * @param association association to match with
   * @return matched association, if found
   */
  private AssocStruct getAssocStrucForClass(ASTCDClass astcdClass, ASTCDAssociation association){
    for (AssocStruct assocStruct : helper.getSrcMap().get(astcdClass)){
      if (sameAssociationType(assocStruct.getAssociation(), association)
        || sameAssociationTypeInReverse(assocStruct.getAssociation(), association)){
        return assocStruct;
      }
    }
    return null;
  }

  public ASTCDClass isSecondElementInPair(ASTCDClass astClass) {
    for (Pair<ASTCDClass, ASTCDClass> pair : matchedClasses) {
      if (pair.b.equals(astClass)) {
        return pair.a;
      }
    }
    return null;
  }

  public boolean getSTADiff(ASTCDClass astcdClass){
    ASTCDClass oldClass = findMatchedClass(astcdClass);
    List<ASTCDClass> oldCLasses = getSuperClasses(tgtCD, oldClass);
    List<ASTCDClass> newClasses = getSuperClasses(srcCD, astcdClass);
    boolean different = false;
    for (ASTCDClass class1 : oldCLasses){
      for (ASTCDClass class2 : newClasses){
        if (class1.getSymbol().getInternalQualifiedName().equals(class2.getSymbol().getInternalQualifiedName())){
          different = true;
          break;
        }
      }
      if (different){
        break;
      }
    }
    for (ASTCDClass class1 : newClasses){
      for (ASTCDClass class2 : oldCLasses){
        if (class1.getSymbol().getInternalQualifiedName().equals(class2.getSymbol().getInternalQualifiedName())){
          different = true;
          break;
        }
      }
    }
    return different;
  }


  /**
   *
   * Check if a deleted @param astcdAssociation was needed in cd2, but not in cd1.
   * @return true if we have a case where we can instantiate a class without instantiating another.
   */
  //false idea
  //if old was bidirectional, check if both sides allowed 0. If this is the case - we have no semDiff
  //if this isn't the case, we have a semDiff for the side(s) that doesn't allow 0
  @Override
  public boolean isNotNeededAssoc(ASTCDAssociation astcdAssociation){
    if (astcdAssociation.getCDAssocDir().isBidirectional()){
      Pair<ASTCDClass, ASTCDClass> pair = Syn2SemDiffHelper.getConnectedClasses(astcdAssociation, getSrcCD());
      //List<ASTCDClass> superClassesLeft = Syn2SemDiffHelper.getSuperClasses(this, pair.a);
      List<ASTCDClass> superClassesRight = Syn2SemDiffHelper.getSuperClasses(getTgtCD(), pair.b);
      for (AssocStruct association : helper.getSrcMap().get(pair.a)){
        if (association.getDirection() == AssocDirection.BiDirectional
          && association.getSide() == ClassSide.Left
          && Syn2SemDiffHelper.sameAssociationType(astcdAssociation, association.getAssociation())
          && superClassesRight.contains(Syn2SemDiffHelper.getConnectedClasses(association.getAssociation(), getSrcCD()).b)){
          return false;
        } else if (association.getDirection() == AssocDirection.BiDirectional
          && association.getSide() == ClassSide.Right
          && Syn2SemDiffHelper.sameAssociationType(astcdAssociation, association.getAssociation())
          && superClassesRight.contains(Syn2SemDiffHelper.getConnectedClasses(association.getAssociation(), getSrcCD()).a)){
          return false;
        }
      }
    } else if (astcdAssociation.getCDAssocDir().isDefinitiveNavigableRight()){
      //leftSide
      Pair<ASTCDClass, ASTCDClass> pair = Syn2SemDiffHelper.getConnectedClasses(astcdAssociation, getSrcCD());
      List<ASTCDClass> superClassesRight = Syn2SemDiffHelper.getSuperClasses(getTgtCD(), pair.b);
      for (AssocStruct association : helper.getSrcMap().get(pair.a)){
        if (association.getSide() == ClassSide.Left
          && association.getAssociation().getCDAssocDir().isDefinitiveNavigableRight()
          && Syn2SemDiffHelper.sameAssociationType(astcdAssociation, association.getAssociation())
          && superClassesRight.contains(Syn2SemDiffHelper.getConnectedClasses(astcdAssociation, getSrcCD()).b)){
          return true;
        }
        if (association.getSide() == ClassSide.Right //reversed assoc
          && association.getAssociation().getCDAssocDir().isDefinitiveNavigableLeft()
          && Syn2SemDiffHelper.sameAssociationType(astcdAssociation, association.getAssociation())
          && superClassesRight.contains(Syn2SemDiffHelper.getConnectedClasses(astcdAssociation, getSrcCD()).a)){
          return true;
        }
      }
    } else if (astcdAssociation.getCDAssocDir().isDefinitiveNavigableLeft()) {
      //rightSide
      Pair<ASTCDClass, ASTCDClass> pair = Syn2SemDiffHelper.getConnectedClasses(astcdAssociation, getSrcCD());
      List<ASTCDClass> superClassesLeft = Syn2SemDiffHelper.getSuperClasses(getTgtCD(), pair.a);
      for (AssocStruct association : helper.getSrcMap().get(pair.b)){
        if (association.getDirection() == AssocDirection.LeftToRight
          && association.getSide() == ClassSide.Left
          && Syn2SemDiffHelper.sameAssociationType(astcdAssociation, association.getAssociation())
          && superClassesLeft.contains(Syn2SemDiffHelper.getConnectedClasses(astcdAssociation, getSrcCD()).b)){
          return true;
        }
        if (association.getDirection() == AssocDirection.RightToLeft
          && association.getSide() == ClassSide.Right
          && Syn2SemDiffHelper.sameAssociationType(association.getAssociation(), astcdAssociation)
          && superClassesLeft.contains(Syn2SemDiffHelper.getConnectedClasses(astcdAssociation, getSrcCD()).a)){
          return true;
        }
      }
    }
    return false;
  }

  /**
   *
   * Check if an added association brings a semantic difference.
   *
   * @return true if a class can now have a new relation to another.
   */
  @Override
  public boolean isAddedAssoc(ASTCDAssociation astcdAssociation) {
    //List<ASTCDAssociation> list = typeMatcher.getMatchedElements(astcdAssociation);
    //this must replace first if()
    //so just check if the list isn't empty?
    if (astcdAssociation.getCDAssocDir().isBidirectional()){
      Pair<ASTCDClass, ASTCDClass> pair = Syn2SemDiffHelper.getConnectedClasses(astcdAssociation, getSrcCD());
      ASTCDClass matchedRight = findMatchedClass(pair.b);
      ASTCDClass matchedLeft = findMatchedClass(pair.a);
      if (matchedRight != null && matchedLeft != null){
        List<ASTCDClass> superClasses = Syn2SemDiffHelper.getSuperClasses(getSrcCD(), matchedLeft);
        for (AssocStruct assocStruct : helper.getTrgMap().get(matchedRight)){
          if (assocStruct.getSide().equals(ClassSide.Left)
            && assocStruct.getAssociation().getCDAssocDir().isBidirectional()
            && Syn2SemDiffHelper.sameAssociationType(astcdAssociation, assocStruct.getAssociation())
            && superClasses.contains(Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), getTgtCD()).b)){
            return false;
          }
          if (assocStruct.getSide().equals(ClassSide.Right)
            && assocStruct.getAssociation().getCDAssocDir().isBidirectional()
            && Syn2SemDiffHelper.sameAssociationType(astcdAssociation, assocStruct.getAssociation())
            && superClasses.contains(Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), getTgtCD()).a)){
            return false;
          }
        }
      }
    }

    if (astcdAssociation.getCDAssocDir().isDefinitiveNavigableLeft()){
      Pair<ASTCDClass, ASTCDClass> pair = Syn2SemDiffHelper.getConnectedClasses(astcdAssociation, getSrcCD());
      ASTCDClass matchedRight = findMatchedClass(pair.b);
      ASTCDClass matchedLeft = findMatchedClass(pair.a);
      if (matchedRight != null && matchedLeft != null){
        List<ASTCDClass> superClasses = Syn2SemDiffHelper.getSuperClasses(getSrcCD(), matchedLeft);
        for (AssocStruct assocStruct : helper.getTrgMap().get(matchedRight)){
          if (assocStruct.getSide().equals(ClassSide.Left)
            && assocStruct.getAssociation().getCDAssocDir().isBidirectional()
            && Syn2SemDiffHelper.sameAssociationType(astcdAssociation, assocStruct.getAssociation())
            && superClasses.contains(Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), getTgtCD()).b)){
            return false;
          }
        }
      }
    }

    if (astcdAssociation.getCDAssocDir().isDefinitiveNavigableLeft()){
      Pair<ASTCDClass, ASTCDClass> pair = Syn2SemDiffHelper.getConnectedClasses(astcdAssociation, getSrcCD());
      ASTCDClass matchedRight = findMatchedClass(pair.b);
      ASTCDClass matchedLeft = findMatchedClass(pair.a);
      if (matchedRight != null && matchedLeft != null){
        List<ASTCDClass> superClasses = Syn2SemDiffHelper.getSuperClasses(getSrcCD(), matchedLeft);
        for (AssocStruct assocStruct : helper.getTrgMap().get(matchedLeft)){
          if (assocStruct.getAssociation().getCDAssocDir().isDefinitiveNavigableLeft()
            && Syn2SemDiffHelper.sameAssociationType(astcdAssociation, assocStruct.getAssociation())
            && superClasses.contains(Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), getTgtCD()).a)){
            return false;
          }
        }
      }
    }

    return true;
  }

  public boolean isAssocAdded(ASTCDAssociation association){
    Pair<ASTCDClass, ASTCDClass> pair = Syn2SemDiffHelper.getConnectedClasses(association, getSrcCD());
    ASTCDClass matchedRight = findMatchedClass(pair.b);
    ASTCDClass matchedLeft = findMatchedClass(pair.a);
    if (matchedLeft != null && matchedRight != null) {
      AssocStruct matchedAssocStruc = getAssocStrucForClass(matchedLeft, association);
      assert matchedAssocStruc != null;
      if (association.getCDAssocDir().isDefinitiveNavigableLeft()) {
        for (AssocStruct assocStruct : helper.getTrgMap().get(matchedRight)) {
          if (Syn2SemDiffHelper.sameAssociationType(assocStruct.getAssociation(), association)
            && (helper.inInheritanceRelation(matchedAssocStruc, assocStruct) || sameTgt(matchedAssocStruc, assocStruct))) {
            return false;
          }
          else if (allSubclassesHaveIt(association, matchedRight) != null){
            return false;
          }
        }
      }
      if (association.getCDAssocDir().isDefinitiveNavigableRight()) {
        for (AssocStruct assocStruct : helper.getTrgMap().get(matchedLeft)) {
          if ((Syn2SemDiffHelper.sameAssociationType(assocStruct.getAssociation(), association)
            && (helper.inInheritanceRelation(matchedAssocStruc, assocStruct) || sameTgt(matchedAssocStruc, assocStruct)))) {
            return false;
          }
          else if (allSubclassesHaveIt(association, matchedLeft) != null){
            return false;
          }
        }
      }
    }
    return true;
  }

  /**
   * Get the differences in a matched pair as a String
   * @param diff object of type CDAssocDiff or CDTypeDIff
   * @return differences as a String
   */
  @Override
  public String findDiff(Object diff) {
    if (diff instanceof CDTypeDiff) {
      CDTypeDiff obj = (CDTypeDiff) diff;
      StringBuilder stringBuilder = new StringBuilder();
      for (DiffTypes type : obj.getBaseDiff()) {
        switch (type) {
          case STEREOTYPE_DIFFERENCE:
            stringBuilder.append(obj.sterDiff());
          case CHANGED_ATTRIBUTE:
            stringBuilder.append(obj.attDiff());
        }
      }
      return stringBuilder.toString();
    } else {
      CDAssocDiff obj = (CDAssocDiff) diff;
      StringBuilder difference = new StringBuilder();
      for (DiffTypes type : obj.getBaseDiff()) {
        switch (type) {
          case CHANGED_ASSOCIATION_ROLE:
            difference.append(obj.roleDiff());
          case CHANGED_ASSOCIATION_DIRECTION:
            difference.append(obj.dirDiff());
            //ADD RIGHT MULTIPLICITY - Done
          case CHANGED_ASSOCIATION_LEFT_MULTIPLICITY :
          case CHANGED_ASSOCIATION_RIGHT_MULTIPLICITY:
            difference.append(obj.cardDiff());
        }
      }
      return difference.toString();
    }
  }

  //TODO: add more test
  /**
   * Find all overlapping and all duplicated associations.
   * When comparing associations, we distinguish two cases:
   * 1) association and superAssociation
   * 2) two associations with the same source
   * For the first case we do the following:
   * If the two associations are in conflict(they have the same role name in target direction) and
   * the target classes are in an inheritance relation(B extends C or C extends B), the subAssociation needs to be merged with the superAssociation.
   * If the associations are in a conflict, but aren't in an inheritance relation, then the subAssociation can't exist(A.r would lead to classes with different types).
   * For the last, we also consider the cardinalities of the associations. If they are additionally at least 1, then the subclass(and its subclasses) can't exist
   * (A.r always has to lead to different classes, which is not allowed).
   * The second case is handled the same way. We distinguish the cases, because in the first one additional delete operation for the used datastructure must be executed.
   * The implementation can be changed o work without the cases.
   */
  @Override
  public void findOverlappingAssocs(){
    Set<ASTCDClass> srcToDelete = new HashSet<>();
    Set<Pair<ASTCDClass, ASTCDRole>> srcAssocsToDelete = new HashSet<>();
    Set<Pair<AssocStruct, AssocStruct>> srcAssocsToMerge = new HashSet<>();
    Set<DeleteStruc> srcAssocsToMergeWithDelete = new HashSet<>();
    Set<ASTCDClass> tgtToDelete = new HashSet<>();
    Set<Pair<AssocStruct, AssocStruct>> tgtAssocsToMerge = new HashSet<>();
    Set<Pair<ASTCDClass, ASTCDRole>> tgtAssocsToDelete = new HashSet<>();
    Set<DeleteStruc> tgtAssocsToMergeWithDelete = new HashSet<>();
    for (ASTCDClass astcdClass : helper.getSrcMap().keySet()) {
      for (AssocStruct association : helper.getSrcMap().get(astcdClass)) {
        if (!association.isSuperAssoc()) {
          for (AssocStruct superAssoc : helper.getSrcMap().get(astcdClass)) {
            if (superAssoc.isSuperAssoc() && !association.equals(superAssoc)) {
              if (isInConflict(association, superAssoc) && helper.inInheritanceRelation(association, superAssoc)) {
                if (!sameRoleNames(association, superAssoc)) {
                  Log.error("Bad overlapping found");
                }
                //same target role names and target classes are in inheritance relation
                //associations need to be merged
                srcAssocsToMergeWithDelete.add(new DeleteStruc(association, superAssoc, astcdClass));
              } else if (isInConflict(association, superAssoc) && !helper.inInheritanceRelation(association, superAssoc)) {
                //two associations with same target role names, but target classes are not in inheritance relation
                //if trg cardinality on one of them is 0..1 or 0..* then such association can't exist
                //if trg cardinality on one of them is 1 or 1..* then such association can't exist and also no objects of this type can exist
                if (areZeroAssocs(association, superAssoc)) {
                  //such association can't exist
                  //delete
                  srcAssocsToDelete.add(new Pair<>(astcdClass, getConflict(association, superAssoc)));
                  //Do I need to give some output about the class
                } else {
                  //such class can't exist
                  //delete
                  helper.updateSrc(astcdClass);
                  srcToDelete.add(astcdClass);
                }
              }
            } else if (!association.equals(superAssoc)) {
              //comparison between direct associations
              if (isInConflict(association, superAssoc) && helper.inInheritanceRelation(association, superAssoc)) {
                srcAssocsToMerge.add(new Pair<>(association, superAssoc));
              } else if (isInConflict(association, superAssoc) && !helper.inInheritanceRelation(association, superAssoc)) {
                if (areZeroAssocs(association, superAssoc)) {
                  srcAssocsToDelete.add(new Pair<>(astcdClass, getConflict(association, superAssoc)));
                } else {
                  helper.updateSrc(astcdClass);
                  srcToDelete.add(astcdClass);
                }
              }
              else if (sameAssociation(association.getAssociation(), superAssoc.getAssociation())
                || sameAssociationInReverse(association.getAssociation(), superAssoc.getAssociation())){
                srcAssocsToMergeWithDelete.add(new DeleteStruc(association, superAssoc, astcdClass));
              }
            }
          }
        }
      }
    }

    for (ASTCDClass astcdClass : helper.getTrgMap().keySet()) {
      for (AssocStruct association : helper.getSrcMap().get(astcdClass)) {
        if (!association.isSuperAssoc()) {
          for (AssocStruct superAssoc : helper.getSrcMap().get(astcdClass)) {
            if (superAssoc.isSuperAssoc() && !association.equals(superAssoc)) {
              if (isInConflict(association, superAssoc) && helper.inInheritanceRelation(association, superAssoc)) {
                if (!sameRoleNames(association, superAssoc)) {
                  Log.error("Bad overlapping found");
                }
                //same target role names and target classes are in inheritance relation
                //associations need to be merged
                tgtAssocsToMergeWithDelete.add(new DeleteStruc(association, superAssoc, astcdClass));
              } else if (isInConflict(association, superAssoc) && !helper.inInheritanceRelation(association, superAssoc)) {
                //two associations with same target role names, but target classes are not in inheritance relation
                //if trg cardinality on one of them is 0..1 or 0..* then such association can't exist
                //if trg cardinality on one of them is 1 or 1..* then such association can't exist and also no objects of this type can exist
                if (areZeroAssocs(association, superAssoc)) {
                  //such association can't exist
                  //delete
                  tgtAssocsToDelete.add(new Pair<>(astcdClass, getConflict(association, superAssoc)));
                  //Do I need to give some output about the class
                } else {
                  //such class can't exist
                  //delete
                  helper.updateTgt(astcdClass);
                  tgtToDelete.add(astcdClass);
                }
              }
            } else if (!association.equals(superAssoc)) {
              //comparison between direct associations
              if (isInConflict(association, superAssoc) && helper.inInheritanceRelation(association, superAssoc)) {
                tgtAssocsToMerge.add(new Pair<>(association, superAssoc));
              } else if (isInConflict(association, superAssoc) && !helper.inInheritanceRelation(association, superAssoc)) {
                if (areZeroAssocs(association, superAssoc)) {
                  tgtAssocsToDelete.add(new Pair<>(astcdClass, getConflict(association, superAssoc)));
                } else {
                  helper.updateTgt(astcdClass);
                  tgtToDelete.add(astcdClass);
                }
              }
              else if (sameAssociation(association.getAssociation(), superAssoc.getAssociation())
                || sameAssociationInReverse(association.getAssociation(), superAssoc.getAssociation())){
                tgtAssocsToMergeWithDelete.add(new DeleteStruc(association, superAssoc, astcdClass));
              }
            }
          }
        }
      }
    }
    for (ASTCDClass astcdClass : srcToDelete) {
      helper.getSrcMap().removeAll(astcdClass);
      for (ASTCDClass subClass : getSpannedInheritance(srcCD, astcdClass)) {
        helper.getSrcMap().removeAll(subClass);
      }
    }
    for (Pair<ASTCDClass, ASTCDRole> pair : srcAssocsToDelete) {
      deleteAssocsFromSrc(pair.a, pair.b);
    }
    for (DeleteStruc pair : srcAssocsToMergeWithDelete) {
      if (!helper.getNotInstanClassesSrc().contains(pair.getAstcdClass())) {
        setBiDirRoleName(pair.getAssociation(), pair.getSuperAssoc());
        mergeAssocs(pair.getAssociation(), pair.getSuperAssoc());
      }
    }
    for (DeleteStruc pair : srcAssocsToMergeWithDelete) {
      helper.getSrcMap().remove(pair.getAstcdClass(), pair.getSuperAssoc());
    }
    for (Pair<AssocStruct, AssocStruct> pair : srcAssocsToMerge) {
      setBiDirRoleName(pair.a, pair.b);
      mergeAssocs(pair.a, pair.b);
    }
    for (ASTCDClass astcdClass : tgtToDelete) {
      helper.getTrgMap().removeAll(astcdClass);
      for (ASTCDClass subClass : getSpannedInheritance(tgtCD, astcdClass)) {
        helper.getTrgMap().removeAll(subClass);
      }
    }
    for (Pair<ASTCDClass, ASTCDRole> pair : tgtAssocsToDelete) {
      deleteAssocsFromTgt(pair.a, pair.b);
    }
    for (DeleteStruc pair : tgtAssocsToMergeWithDelete) {
      if (!helper.getNotInstanClassesTgt().contains(pair.getAstcdClass())) {
        setBiDirRoleName(pair.getAssociation(), pair.getSuperAssoc());
        mergeAssocs(pair.getAssociation(), pair.getSuperAssoc());
      }
    }
    for (DeleteStruc pair : tgtAssocsToMergeWithDelete) {
      helper.getTrgMap().remove(pair.getAstcdClass(), pair.getSuperAssoc());
    }
    for (Pair<AssocStruct, AssocStruct> pair : tgtAssocsToMerge) {
      setBiDirRoleName(pair.a, pair.b);
      mergeAssocs(pair.a, pair.b);
    }
  }

  /**
   * Delete associations from srcMap with a specific role name
   * @param astcdClass source class
   * @param role role name
   */
  public void deleteAssocsFromSrc(ASTCDClass astcdClass, ASTCDRole role){
    Iterator<AssocStruct> iterator = helper.getSrcMap().get(astcdClass).iterator();
   while (iterator.hasNext()){
     AssocStruct assocStruct = iterator.next();
      if (assocStruct.getSide().equals(ClassSide.Left)
        && assocStruct.getAssociation().getRight().getCDRole().getName().equals(role.getName())){
        iterator.remove();
      }
      if (assocStruct.getSide().equals(ClassSide.Right)
        && assocStruct.getAssociation().getLeft().getCDRole().getName().equals(role.getName())){
        iterator.remove();
      }
    }
  }

  /**
   * Delete associations from trgMap with a specific role name
   * @param astcdClass source class
   * @param role role name
   */
  public void deleteAssocsFromTgt(ASTCDClass astcdClass, ASTCDRole role){
    Iterator<AssocStruct> iterator = helper.getTrgMap().get(astcdClass).iterator();
    while (iterator.hasNext()){
      AssocStruct assocStruct = iterator.next();
      if (assocStruct.getSide().equals(ClassSide.Left)
        && assocStruct.getAssociation().getRight().getCDRole().getName().equals(role.getName())){
        iterator.remove();
      }
      if (assocStruct.getSide().equals(ClassSide.Right)
        && assocStruct.getAssociation().getLeft().getCDRole().getName().equals(role.getName())){
        iterator.remove();
      }
    }
  }

  public Pair<ASTCDClass, Set<ASTCDAttribute>> newAttributes(InheritanceDiff inheritanceDiff) {
    Set<ASTCDClass> classes = new HashSet<>();
    for (ASTCDClass astcdClass : inheritanceDiff.getNewDirectSuper()){
      boolean isContained = false;
      for (ASTCDClass astcdClass1 : inheritanceDiff.getOldDirectSuper()){
        if (astcdClass.getName().equals(astcdClass1.getName())) {
          isContained = true;
          break;
        }
      }
      if (!isContained){
        classes.add(astcdClass);
      }
    }
    Set<ASTCDAttribute> attributes = new HashSet<>();
    for (ASTCDClass astcdClass : classes) {
      for (ASTCDAttribute attribute : getHelper().getAllAttr(astcdClass).b) {
        boolean isContained = false;
        for (ASTCDAttribute attribute1 : getHelper().getAllAttr(inheritanceDiff.getAstcdClasses().b).b) {
          if (attribute.getName().equals(attribute1.getName())
            && attribute.printType().equals(attribute1.printType())) {
            isContained = true;
            break;
          }
        }
        if (!isContained) {
          attributes.add(attribute);
        }
      }
    }
    return new Pair<>(inheritanceDiff.getAstcdClasses().a, attributes);
  }

  public Pair<ASTCDClass, Set<ASTCDAttribute>> deletedAttributes(InheritanceDiff inheritanceDiff){
    Set<ASTCDClass> classes = new HashSet<>();
    for (ASTCDClass astcdClass : inheritanceDiff.getOldDirectSuper()){
      boolean isContained = false;
      for (ASTCDClass astcdClass1 : inheritanceDiff.getNewDirectSuper()){
        if (astcdClass.getName().equals(astcdClass1.getName())) {
          isContained = true;
          break;
        }
      }
      if (!isContained){
        classes.add(astcdClass);
      }
    }
    Set<ASTCDAttribute> attributes = new HashSet<>();
    for (ASTCDClass astcdClass : classes) {
      for (ASTCDAttribute attribute : getHelper().getAllAttr(astcdClass).b) {
        boolean isContained = false;
        for (ASTCDAttribute attribute1 : getHelper().getAllAttr(inheritanceDiff.getAstcdClasses().a).b) {
          if (attribute.getName().equals(attribute1.getName())
            && attribute.printType().equals(attribute1.printType())) {
            isContained = true;
          }
        }
        if (!isContained) {
          attributes.add(attribute);
        }
      }
    }
    return new Pair<>(inheritanceDiff.getAstcdClasses().a, attributes);
  }

  public Pair<ASTCDClass, Set<ASTCDAssociation>> newAssocs(InheritanceDiff inheritanceDiff){
    Set<ASTCDAssociation> associations = new HashSet<>();
    for (ASTCDClass astcdClass : inheritanceDiff.getNewDirectSuper()){
      for (AssocStruct assocStruct : helper.getSrcMap().get(astcdClass)){
        boolean isContained = false;
        for (AssocStruct assocStruct1 : helper.getTrgMap().get(inheritanceDiff.getAstcdClasses().a)){
          Pair<ASTCDClass, ASTCDClass> pair = Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), srcCD);
          Pair<ASTCDClass, ASTCDClass> pair1 = Syn2SemDiffHelper.getConnectedClasses(assocStruct1.getAssociation(), tgtCD);
          if (sameTgt(assocStruct, assocStruct1)
            && (sameAssociation(assocStruct.getAssociation(), assocStruct1.getAssociation())
            || sameAssociation(assocStruct.getAssociation(), assocStruct1.getAssociation()))){
            isContained = true;
          }
        }
        if (!isContained){
          associations.add(assocStruct.getAssociation());
        }
      }
    }
    return new Pair<>(inheritanceDiff.getAstcdClasses().a, associations);
  }

  //composition - association.getCDType.isComposition()

  public boolean sameTgt(AssocStruct assocStruct, AssocStruct assocStruct2){
    if (assocStruct.getSide().equals(ClassSide.Left)){
      if (assocStruct2.getSide().equals(ClassSide.Left)){
        return Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), srcCD).b.getName()
          .equals(Syn2SemDiffHelper.getConnectedClasses(assocStruct2.getAssociation(), tgtCD).b.getName());
      } else {
        return Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), srcCD).b.getName()
          .equals(Syn2SemDiffHelper.getConnectedClasses(assocStruct2.getAssociation(), tgtCD).a.getName());
      }
    }
    else {
      if (assocStruct2.getSide().equals(ClassSide.Left)){
        return Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), srcCD).a.getName()
          .equals(Syn2SemDiffHelper.getConnectedClasses(assocStruct2.getAssociation(), tgtCD).b.getName());
      } else {
        return Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), srcCD).a.getName()
          .equals(Syn2SemDiffHelper.getConnectedClasses(assocStruct2.getAssociation(), tgtCD).a.getName());
      }
    }
  }
  public List<ASTCDAssociation> addedAssocList(){
    List<ASTCDAssociation> associationList = new ArrayList<>();
    for (ASTCDAssociation association : addedAssocs){
      Pair<ASTCDClass, ASTCDClass> assocClasses = Syn2SemDiffHelper.getConnectedClasses(association, srcCD);
      if ((helper.findMatchingAssocStructSrc(association, assocClasses.a) != null
        || helper.findMatchingAssocStructSrc(association, assocClasses.b) != null)
        && isAddedAssoc(association)){
        associationList.add(association);
      }
    }
    return associationList;
  }

  public List<Pair<ASTCDAssociation, ASTCDClass>> deletedAssocList() {
    List<Pair<ASTCDAssociation, ASTCDClass>> list = new ArrayList<>();
    for (ASTCDAssociation association : deletedAssocs) {
      Pair<ASTCDClass, ASTCDClass> pair = Syn2SemDiffHelper.getConnectedClasses(association, srcCD);
      if (association.getCDAssocDir().isBidirectional()){
        ASTCDClass astcdClass = isAssocDeleted(association, pair.a) ;
        ASTCDClass astcdClass1 = isAssocDeleted(association, pair.b);
        if (astcdClass != null ){
          list.add(new Pair<>(association, pair.a));
        }
        else if (astcdClass1 != null){
          list.add(new Pair<>(association, pair.b));
        }
      }
      else if (association.getCDAssocDir().isDefinitiveNavigableLeft()) {
        ASTCDClass astcdClass = isAssocDeleted(association, pair.b);
        if (astcdClass != null){
          list.add(new Pair<>(association, pair.b));
        }
      }
      else {
        ASTCDClass astcdClass = isAssocDeleted(association, pair.a);
        if (astcdClass != null){
          list.add(new Pair<>(association, pair.a));
        }
      }
    }
    return list;
  }

  public List<ASTCDClass> addedClassList(){
    List<ASTCDClass> classList = new ArrayList<>();
    for (ASTCDClass astcdClass : addedClasses){
      if (!helper.getNotInstanClassesSrc().contains(astcdClass)
        && isSupClass(astcdClass)){
        classList.add(astcdClass);
      }
    }
    return classList;
  }

  public List<Pair<ASTCDClass, List<ASTCDAttribute>>> changedAttributeList() {
    List<Pair<ASTCDClass, List<ASTCDAttribute>>> list = new ArrayList<>();
    for (CDTypeDiff typeDiff : changedClasses) {
      if (typeDiff.getSrcElem() instanceof ASTCDClass
        && !helper.getNotInstanClassesSrc().contains((ASTCDClass) typeDiff.getSrcElem())
        && typeDiff.getBaseDiff().contains(DiffTypes.CHANGED_ATTRIBUTE)) {
        list.add(typeDiff.deletedAttributes( ));
      }
    }
    return list;
  }

  public boolean srcAndTgtExist(CDAssocDiff assocDiff){
    Pair<ASTCDClass, ASTCDClass> pair = Syn2SemDiffHelper.getConnectedClasses(assocDiff.getSrcElem(), srcCD);
    if (assocDiff.findMatchingAssocStructSrc(assocDiff.getSrcElem(), pair.a) != null
      || assocDiff.findMatchingAssocStructSrc(assocDiff.getSrcElem(), pair.b) != null){
      Pair<ASTCDClass, ASTCDClass> pair2 = Syn2SemDiffHelper.getConnectedClasses(assocDiff.getTgtElem(), tgtCD);
      if ((assocDiff.findMatchingAssocStructTgt(assocDiff.getTgtElem(), pair2.a) != null
        || assocDiff.findMatchingAssocStructTgt(assocDiff.getTgtElem(), pair2.b) != null)){
        return true;
      }
    }
    return false;
  }

  public List<TypeDiffStruc> changedTypes(){
    List<TypeDiffStruc> list = new ArrayList<>();
    for (CDTypeDiff typeDiff : changedClasses){
      TypeDiffStruc diff = new TypeDiffStruc();
      diff.setBaseDiff(typeDiff.getBaseDiff());
      if (typeDiff.getSrcElem() instanceof ASTCDEnum){
        diff.setAddedConstants(typeDiff.newConstants());
      } else if (!helper.getNotInstanClassesSrc().contains((ASTCDClass) typeDiff.getSrcElem())) {
        if (typeDiff.getSrcElem().getModifier().isAbstract()) {
          ASTCDClass instanSubClass = helper.minDiffWitness((ASTCDClass) typeDiff.getSrcElem());
          if (instanSubClass != null) {
            if (typeDiff.getBaseDiff().contains(DiffTypes.CHANGED_ATTRIBUTE)) {
              diff.setMemberDiff(typeDiff.changedAttribute());
              List<Pair<ASTCDAttribute, ASTCDAttribute>> pairs = new ArrayList<>();
              for (ASTCDAttribute attribute : typeDiff.changedAttribute().b) {
                pairs.add(new Pair<>(attribute, typeDiff.getOldAttribute(attribute)));
              }
              diff.setMatchedAttributes(pairs);
            }
            if (typeDiff.getBaseDiff().contains(DiffTypes.ADDED_ATTRIBUTE)) {
              diff.setAddedAttributes(typeDiff.addedAttributes());
            }
            if (typeDiff.getBaseDiff().contains(DiffTypes.REMOVED_ATTRIBUTE)) {
              diff.setDeletedAttributes(typeDiff.deletedAttributes());
            }
            if (typeDiff.getBaseDiff().contains(DiffTypes.STEREOTYPE_DIFFERENCE)) {
              diff.setChangedStereotype(typeDiff.isClassNeeded());
            }
          }
        }
      }
    }
    return list;
  }

  public List<AssocDiffStruc> changedAssoc() {
    List<AssocDiffStruc> list = new ArrayList<>();
    for (CDAssocDiff assocDiff : changedAssocs) {
      Pair<ASTCDClass, ASTCDClass> pairDef = Syn2SemDiffHelper.getConnectedClasses(assocDiff.getSrcElem(), srcCD);
      Pair<ASTCDClass, ASTCDClass> pair = null;
      if (pairDef.a.getModifier().isAbstract() || pairDef.b.getModifier().isAbstract()) {
        pair = getClassesForAssoc(pairDef);
      } else {
        pair = pairDef;
      }
      if (!helper.getNotInstanClassesSrc().contains(pair.a) && !helper.getNotInstanClassesSrc().contains(pair.b)) {
        AssocDiffStruc diff = new AssocDiffStruc();
        if (assocDiff.getBaseDiff().contains(DiffTypes.CHANGED_ASSOCIATION_ROLE)) {
          if (srcAndTgtExist(assocDiff)) {
            diff.setChangedRoleNames(assocDiff.getRoleDiff().b);
          }
        }
        if (assocDiff.getBaseDiff().contains(DiffTypes.CHANGED_ASSOCIATION_DIRECTION)) {
          if (srcAndTgtExist(assocDiff)
            && assocDiff.isDirectionChanged()) {
            diff.setChangedDir(true);
          }
        }

        if (assocDiff.getBaseDiff().contains(DiffTypes.CHANGED_TARGET)) {
          if (srcAndTgtExist(assocDiff)) {
            diff.setChangedTgt(assocDiff.getChangedTgtClass().b);
          }
        }
        // NOT LEFT MULTIPLICITY, DO ALSO FOR RIGHT MULTIPLICITY
        //Done
        if (assocDiff.getBaseDiff().contains(DiffTypes.CHANGED_ASSOCIATION_LEFT_MULTIPLICITY)) {
          if (srcAndTgtExist(assocDiff)) {
            diff.setChangedCard(assocDiff.getCardDiff().b);
          }
        }
        if (assocDiff.getBaseDiff().contains(DiffTypes.CHANGED_ASSOCIATION_RIGHT_MULTIPLICITY)) {
          if (srcAndTgtExist(assocDiff)) {
            diff.setChangedCard(assocDiff.getCardDiff().b);
          }
        }

      }
    }
    return list;
  }

  private Pair<ASTCDClass, ASTCDClass> getClassesForAssoc(Pair<ASTCDClass, ASTCDClass> pair){
    ASTCDClass left = null;
    ASTCDClass right = null;
    if (pair.a.getModifier().isAbstract()){
      left = helper.minDiffWitness(pair.a);
    }
    if (pair.b.getModifier().isAbstract()){
      right = helper.minDiffWitness(pair.b);
    }
    if (left != null && right != null){
      return new Pair<>(left, right);
    }
    return null;
  }

  public List<ASTCDClass> srcExistsTgtNot(){
    List<ASTCDClass> list = new ArrayList<>();
    for (ASTCDClass astcdClass : helper.getNotInstanClassesTgt()){
      if (!helper.getNotInstanClassesSrc().contains(astcdClass)){
        list.add(astcdClass);
      }
    }
    return list;
  }

  //function that checks for each ASTCDCLass if the superclasses in the new and in the old diagram are the same
  public List<ASTCDClass> getSTADiff(){
    List<ASTCDClass> list = new ArrayList<>();
    for (ASTCDClass astcdClass : helper.getSrcCD().getCDDefinition().getCDClassesList()){
      ASTCDClass matchedClass = findMatchedClass(astcdClass);
      if (matchedClass != null){
        List<ASTCDClass> superClasses = getSuperClasses(helper.getSrcCD(), astcdClass);
        List<ASTCDClass> matchedSuperClasses = getSuperClasses(helper.getTgtCD(), matchedClass);
        Set<ASTCDClass> superClassesSet = new HashSet<>(superClasses);
        Set<ASTCDClass> matchedSuperClassesSet = new HashSet<>(matchedSuperClasses);
        if (!superClassesSet.equals(matchedSuperClassesSet)){
          list.add(astcdClass);
        }
      }
    }
    return list;
  }

  //TODO: CDSyntax2SemDiff4ASTODHelper

  /*--------------------------------------------------------------------*/

  // FERTIG
  public void addAllChangedClasses(ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD) {
    ICD4CodeArtifactScope scopeSrcCD = (ICD4CodeArtifactScope) srcCD.getEnclosingScope();
    ICD4CodeArtifactScope scopeTgtCD = (ICD4CodeArtifactScope) tgtCD.getEnclosingScope();

    for(Pair<ASTCDClass, ASTCDClass> pair : matchedClasses){
      CDTypeDiff typeDiff = new CDTypeDiff(pair.a, pair.b, scopeSrcCD, scopeTgtCD);
      if(!typeDiff.getBaseDiff().isEmpty()){
        changedClasses.add(typeDiff);
      }
    }
  }

  // FERTIG
  public void addAllChangedAssocs() {
    for(Pair<ASTCDAssociation, ASTCDAssociation> pair : matchedAssocs){
      CDAssocDiff assocDiff = new CDAssocDiff(pair.a, pair.b);
      if(!assocDiff.getBaseDiff().isEmpty()){
        changedAssocs.add(assocDiff);
      }
    }
  }

  //FERTIG
  public void addAllAddedClasses(ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD) {
    for (ASTCDClass srcClass : srcCD.getCDDefinition().getCDClassesList()) {
      boolean notFound = true;
      for (ASTCDClass tgtClass : tgtCD.getCDDefinition().getCDClassesList()) {
        if (srcClass.getName().equals(tgtClass.getName())) {
          notFound = false;
          break;
        }
      }
      if (notFound) {
        addedClasses.add(srcClass);
      }
    }
  }

  //FERTIG
  public void addAllDeletedClasses(ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD) {
    for (ASTCDClass tgtClass : tgtCD.getCDDefinition().getCDClassesList()) {
      boolean notFound = true;
      for (ASTCDClass srcClass : srcCD.getCDDefinition().getCDClassesList()) {
        if (srcClass.getName().equals(tgtClass.getName())) {
          notFound = false;
          break;
        }
      }
      if (notFound) {
        deletedClasses.add(tgtClass);
      }
    }
  }

  //FERTIG
  public void addAllAddedEnums(ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD) {
    for (ASTCDEnum srcEnum : srcCD.getCDDefinition().getCDEnumsList()) {
      boolean notFound = true;
      for (ASTCDEnum tgtEnum : tgtCD.getCDDefinition().getCDEnumsList()) {
        if (srcEnum.getName().equals(tgtEnum.getName())) {
          notFound = false;
          break;
        }
      }
      if (notFound) {
        addedEnums.add(srcEnum);
      }
    }
  }

  //FERTIG
  public void addAllDeletedEnums(ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD) {
    for (ASTCDEnum tgtEnum : tgtCD.getCDDefinition().getCDEnumsList()) {
      boolean notFound = true;
      for (ASTCDEnum srcEnum : srcCD.getCDDefinition().getCDEnumsList()) {
        if (srcEnum.getName().equals(tgtEnum.getName())) {
          notFound = false;
          break;
        }
      }
      if (notFound) {
        deletedEnums.add(tgtEnum);
      }
    }
  }

  //FERTIG
  public void addAllAddedAssocs(ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD) {
    for (ASTCDAssociation srcAssoc : srcCD.getCDDefinition().getCDAssociationsList()) {
      boolean notFound = true;
      for (ASTCDAssociation tgtAssoc : tgtCD.getCDDefinition().getCDAssociationsList()) {
        if (srcAssoc.getName().equals(tgtAssoc.getName())) {
          notFound = false;
          break;
        }
      }
      if (notFound) {
        addedAssocs.add(srcAssoc);
      }
    }
  }

  //FERTIG
  public void addAllDeletedAssocs(ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD) {
    for (ASTCDAssociation tgtAssoc : tgtCD.getCDDefinition().getCDAssociationsList()) {
      boolean notFound = true;
      for (ASTCDAssociation srcAssoc : srcCD.getCDDefinition().getCDAssociationsList()) {
        if (srcAssoc.getName().equals(tgtAssoc.getName())) {
          notFound = false;
          break;
        }
      }
      if (notFound) {
        deletedAssocs.add(tgtAssoc);
      }
    }
  }

  //FERTIG
  public void addAllMatchedClasses(ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD,
                                   MatchingStrategy<ASTCDType> typeMatcher) {
    List<ASTCDClass> tgtClasses = tgtCD.getCDDefinition().getCDClassesList();
    for (ASTCDClass srcClass : srcCD.getCDDefinition().getCDClassesList()) {
      for (ASTCDClass tgtClass : tgtClasses) {
        if (typeMatcher.isMatched(srcClass, tgtClass)) {
          matchedClasses.add(new Pair(srcClass, tgtClass));
          tgtClasses.remove(tgtClass);
          break;
        }
      }
    }
  }

  //FERTIG
  public void addAllMatchedInterfaces(ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD,
                                      MatchingStrategy<ASTCDType> typeMatcher) {
    for (ASTCDInterface srcInterface : srcCD.getCDDefinition().getCDInterfacesList()) {
      for (ASTCDInterface tgtInterface : tgtCD.getCDDefinition().getCDInterfacesList()) {
        if (typeMatcher.isMatched(srcInterface, tgtInterface)) {
          matchedInterfaces.add(new Pair(srcInterface, tgtInterface));
          break;
        }
      }
    }
  }

  //FERTIG
  public void addAllMatchedAssocs(ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD,
                                  MatchingStrategy<ASTCDAssociation> assocMatcher) {
    for (ASTCDAssociation srcAssoc : srcCD.getCDDefinition().getCDAssociationsList()) {
      for (ASTCDAssociation tgtAssoc : tgtCD.getCDDefinition().getCDAssociationsList()) {
        if (assocMatcher.isMatched(srcAssoc, tgtAssoc)) {
          matchedAssocs.add(new Pair(srcAssoc, tgtAssoc));
          break;
        }
      }
    }
  }

  //FERTIG
  public void addAllMatchedEnums(ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD) {
    for (ASTCDEnum srcEnum : srcCD.getCDDefinition().getCDEnumsList()) {
      for (ASTCDEnum tgtEnum : tgtCD.getCDDefinition().getCDEnumsList()) {
        if (srcEnum.getName().equals(tgtEnum.getName())) {
          matchedEnums.add(new Pair(srcEnum, tgtEnum));
        }
      }
    }
  }

  private StringBuilder createMatchString(
    CDTypeDiff x, boolean coloured) {
    StringBuilder tmp = new StringBuilder();
    // Header for first element
    tmp.append("CD1 (")
      .append(tgtCD.getCDDefinition().getName())
      .append(") Line: ")
      .append(x.getTgtElem().get_SourcePositionStart().getLine())
      .append("-")
      .append(x.getTgtElem().get_SourcePositionEnd().getLine())
      .append(System.lineSeparator());

    // Add either coloured or plain
    if (coloured) {
      tmp.append(x.printSrcCD());
    }
    // Header for second element
    tmp.append(System.lineSeparator())
      .append("CD2 (")
      .append(srcCD.getCDDefinition().getName())
      .append(") Line: ")
      .append(x.getSrcElem().get_SourcePositionStart().getLine())
      .append("-")
      .append(x.getSrcElem().get_SourcePositionEnd().getLine())
      .append(System.lineSeparator());

    // Add either coloured or plain
    if (coloured) {
      tmp.append(x.printTgtCD());
    }

    tmp.append(System.lineSeparator())
      .append(x.getDiffType())
      .append(System.lineSeparator())
      .append(System.lineSeparator());
    return tmp;
  }

  private StringBuilder createMatchString(CDAssocDiff x, boolean coloured) {
    StringBuilder tmp = new StringBuilder();
    // Header
    tmp.append("CD1 (")
      .append(tgtCD.getCDDefinition().getName())
      .append(") and CD2 (")
      .append(srcCD.getCDDefinition().getName())
      .append(")")
      .append(System.lineSeparator())
      .append("CD1: ")
      .append(x.getTgtElem().get_SourcePositionStart().getLine())
      .append("  ");

    // Add either coloured or plain
    if (coloured) {
      tmp.append(x.printTgtAssoc());
    }
    // Header for second element
    tmp.append(System.lineSeparator())
      .append("CD2: ")
      .append(x.getSrcElem().get_SourcePositionStart().getLine())
      .append("  ");

    // Add either coloured or plain
    if (coloured) {
      tmp.append(x.printSrcAssoc());
    }

    tmp.append(System.lineSeparator())
      .append(x.getDiffType())
      .append(System.lineSeparator())
      .append(System.lineSeparator());
    return tmp;
  }
  //public void print() {System.out.println(outPutAll);}

  //public void printCD1() {System.out.println(cd1Colored);}

  //public void printCD2() {System.out.println(cd2Colored);}

  public String print() {
    return outPutAll.toString();
  }

  public String printTgtCD() {
    return cd1Colored.toString();
  }

  public String printSrcCD () {
    return cd2Colored.toString();
  }

}
