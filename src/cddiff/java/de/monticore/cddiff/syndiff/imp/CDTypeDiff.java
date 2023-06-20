package de.monticore.cddiff.syndiff.imp;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cddiff.syndiff.DiffTypes;
import de.monticore.cddiff.syndiff.ICDTypeDiff;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import edu.mit.csail.sdg.alloy4.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.monticore.cddiff.*;
import de.monticore.cddiff.syndiff.imp.CDSyntaxDiff;

import static de.monticore.cddiff.ow2cw.CDInheritanceHelper.*;

public class CDTypeDiff implements ICDTypeDiff {
  private final ASTCDType elem1;
  private final ASTCDType elem2;
  private List<CDMemberDiff> changedMembers;
  private List<ASTCDAttribute> addedAttributes;
  private List<ASTCDAttribute> deletedAttributes;
  private List<ASTCDEnumConstant> addedConstants;
  private List<ASTCDEnumConstant> deletedConstants;
  private List<Pair<ASTCDAttribute, ASTCDAttribute>> matchedAttributes;
  private List<Pair<ASTCDEnumConstant, ASTCDEnumConstant>> matchedConstants;
  private List<DiffTypes> baseDiffs;

  protected CDTypeDiff(ASTCDType elem1, ASTCDType elem2) {
    this.elem1 = elem1;
    this.elem2 = elem2;
  }

  @Override
  public ASTCDType getElem1() {
    return elem1;
  }

  @Override
  public ASTCDType getElem2() {
    return elem2;
  }

  @Override
  public List<CDMemberDiff> getChangedMembers() {
    return changedMembers;
  }

  @Override
  public void setChangedMembers(List<CDMemberDiff> changedMembers) {
    this.changedMembers = changedMembers;
  }

  @Override
  public List<ASTCDAttribute> getAddedAttributes() {
    return addedAttributes;
  }

  @Override
  public void setAddedAttributes(List<ASTCDAttribute> addedAttributes) {
    this.addedAttributes = addedAttributes;
  }

  @Override
  public List<ASTCDAttribute> getDeletedAttribute() {
    return deletedAttributes;
  }

  @Override
  public void setDeletedAttribute(List<ASTCDAttribute> deletedAttribute) {
    this.deletedAttributes = deletedAttribute;
  }

  @Override
  public List<ASTCDEnumConstant> getAddedConstants() {
    return addedConstants;
  }

  @Override
  public void setAddedConstants(List<ASTCDEnumConstant> addedConstants) {
    this.addedConstants = addedConstants;
  }

  @Override
  public List<ASTCDEnumConstant> getDeletedConstants() {
    return deletedConstants;
  }

  @Override
  public List<Pair<ASTCDAttribute, ASTCDAttribute>> getMatchedAttributes() {
    return null;
  }

  @Override
  public List<Pair<ASTCDEnumConstant, ASTCDEnumConstant>> getMatchedConstants() {
    return null;
  }

  @Override
  public void changedAttribute() {}

  @Override
  public List<ASTCDClass> getClassesForEnum() {
    return null;
  }

  @Override
  public void setDeletedConstants(List<ASTCDEnumConstant> deletedConstants) {
    this.deletedConstants = deletedConstants;
  }

  @Override
  public List<DiffTypes> getBaseDiffs() {
    return baseDiffs;
  }

  @Override
  public void setBaseDiffs(List<DiffTypes> baseDiffs) {
    this.baseDiffs = baseDiffs;
  }

  @Override
  public String sterDiff() {
    return "Modifier changed from " + getElem2().getModifier() + " to " + getElem1().getModifier();
  }

  public String attDiff() {
    StringBuilder stringBuilder = new StringBuilder();
    for (CDMemberDiff member : getChangedMembers()) {
      if (member.getElem1() instanceof ASTCDAttribute) {
        ASTCDAttribute attribute1 = (ASTCDAttribute) member.getElem1();
        ASTCDAttribute attribute2 = (ASTCDAttribute) member.getElem2();
        stringBuilder.append(
            "Attribute type changed from "
                + attribute2.getMCType().printType()
                + " to "
                + attribute1.getMCType().printType());
      }
    }
    return null;
  }

  /**
   * Check if an attribute is really deleted.
   * @param attribute from list deletedAttributes
   * @return false if not found in inheritance hierarchy
   */
  public boolean isDeleted(ASTCDAttribute attribute, ICD4CodeArtifactScope artifactScope){
    return isAttributInSuper(attribute, getElem1(), artifactScope);
  }

  /**
   * Check if an attribute is really added.
   * @param attribute from addedList
   * @param compilationUnit for diagram (trg)
   * @return false if not found in all subclasses
   */
  public boolean isAdded(ASTCDAttribute attribute, ASTCDCompilationUnit compilationUnit){
    List<ASTCDClass> classList = getSpannedInheritance((ASTCDClass) getElem2(), compilationUnit);
    boolean conditionSatisfied = false; // Track if the condition is satisfied
    for (ASTCDClass astcdClass : classList) {
      if (!astcdClass.getCDAttributeList().contains(attribute)) {
        Set<ASTCDType> astcdClassList = getAllSuper(astcdClass, CD4CodeMill.scopesGenitorDelegator().createFromAST(compilationUnit));
        astcdClassList.remove(getElem2());
        for (ASTCDType type : astcdClassList) {
          if (type instanceof ASTCDClass && type.getCDAttributeList().contains(attribute)) {
            conditionSatisfied = true; // Set the flag to true if the condition holds
            break;
          }
        }
      }
      if (conditionSatisfied) {
        break; // Break out of the first loop if the condition is satisfied
      }
    }
    return conditionSatisfied;
  }

  public List<ASTCDClass> getSpannedInheritance(ASTCDClass astcdClass, ASTCDCompilationUnit compilationUnit){
    List<ASTCDClass> subclasses = new ArrayList<>();
    for (ASTCDClass childClass : compilationUnit.getCDDefinition().getCDClassesList()) {
      if ((getAllSuper(childClass, CD4CodeMill.scopesGenitorDelegator().createFromAST(compilationUnit))).contains(astcdClass)) {
        subclasses.add(childClass);
      }
    }
    return subclasses;
  }

  public List<Pair<ASTCDClass, ASTCDEnumConstant>> newConstants(){
    List<Pair<ASTCDClass, ASTCDEnumConstant>> pairList = new ArrayList<>();
    if (!getAddedConstants().isEmpty()){
      for (ASTCDEnumConstant constant : getAddedConstants()){
        pairList.add(new Pair<>((ASTCDClass) getElem1(), constant));
      }
    }
    return pairList;
  }

  public void findMemberDiff(CDMemberDiff memberDiff, ASTCDCompilationUnit compilationUnit){
    if (!getElem1().getModifier().isAbstract()) {
      for (DiffTypes type : memberDiff.getBaseDiff()) {
        switch (type) {
          case CHANGED_ATTRIBUTE: //add to Diff List new Pair(getElem1(), memberDiff.getElem1()
          case CHANGED_VISIBILITY: //give as output to user - no semDiff
            //other cases
        }
      }
    }
    else { //class is abstract and can't be instantiated - get a subclass
      for (ASTCDClass astcdClass : compilationUnit.getCDDefinition().getCDClassesList()){
        if (getDirectSuperClasses(astcdClass, CD4CodeMill.scopesGenitorDelegator().createFromAST(compilationUnit)).contains(getElem1())){// can be made to contains ONLY - extends as in java or C++?
          for (DiffTypes type : memberDiff.getBaseDiff()) {
            switch (type) {
              case CHANGED_ATTRIBUTE: //add to Diff List new Pair(astcdClass, memberDiff.getElem1())
              case CHANGED_VISIBILITY: //give as output to user - no semDiff
                //other cases?
            }
          }
        }
      }
    }
  }
}
