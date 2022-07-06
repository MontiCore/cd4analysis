package de.monticore.ow2cw.expander;

import de.monticore.cd2alloy.generator.CD2AlloyQNameHelper;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.ow2cw.CDAssociationHelper;

import java.util.*;

public class FullExpander implements CDExpander {
  final protected CDExpander expander;

  /**
   * @param expander checking if adding/etc is allowed
   */
  public FullExpander(CDExpander expander) {
    this.expander = expander;
  }

  public <T extends ASTCDType> void addMissingTypesAndAttributes(Collection<T> typeList) {
    ICD4CodeArtifactScope scope = CD4CodeMill.scopesGenitorDelegator().createFromAST(getCD());
    for (ASTCDType type : typeList) {
      Optional<CDTypeSymbol> opt = scope.resolveCDTypeDown(type.getSymbol().getFullName());
      if (!opt.isPresent()) {
        if (type instanceof ASTCDInterface){
          addDummyInterface((ASTCDInterface) type).ifPresent(newInterface -> addMissingAttributes(newInterface,
              type.getCDAttributeList()));
        } else {
          addDummyClass(type).ifPresent(newClass -> addMissingAttributes(newClass,
              type.getCDAttributeList()));
        }
      }
      else {
        addMissingAttributes(opt.get().getAstNode(), type.getCDAttributeList());
      }
    }
  }

  public void addMissingEnumsAndConstants(Collection<ASTCDEnum> enumList) {
    ICD4CodeArtifactScope scope = CD4CodeMill.scopesGenitorDelegator().createFromAST(getCD());
    // add enums and enum constants exclusive to first
    for (ASTCDEnum astcdEnum : enumList) {
      Optional<CDTypeSymbol> opt = scope.resolveCDTypeDown(astcdEnum.getSymbol().getFullName());
      if (!opt.isPresent()) {
        addClone(astcdEnum);
      }
      else {
        for (ASTCDEnumConstant constant : astcdEnum.getCDEnumConstantList()) {
          boolean found = opt.get()
              .getFieldList()
              .stream()
              .anyMatch(field -> field.getName().equals(constant.getName()));
          if (!found) {
            // I wanted to avoid reflection, but I think this is just reflection with extra steps...
            for (ASTCDEnum someEnum : getCD().getCDDefinition().getCDEnumsList()) {
              if (astcdEnum.getSymbol().getFullName().equals(someEnum.getSymbol().getFullName())) {
                someEnum.addCDEnumConstant(constant.deepClone());
              }
            }
          }
        }
      }
    }
  }

  public void addMissingAttributes(ASTCDType cdType, Collection<ASTCDAttribute> missingAttributes) {
    /*if (check stereotype if allowed to add attribute to class ){
      // add Attribute
    }
     */
    for (ASTCDAttribute attribute1 : missingAttributes) {
      boolean found = cdType.getCDAttributeList()
          .stream()
          .anyMatch(attribute2 -> attribute1.getName().equals(attribute2.getName()));
      if (!found) {
        ASTCDAttribute newAttribute = attribute1.deepClone();
        cdType.addCDMember(newAttribute);
      }
    }
  }

  public void addAssociationClones(Collection<ASTCDAssociation> originals) {
    for (ASTCDAssociation srcAssoc : originals) {
        ASTCDAssociation newAssoc = srcAssoc.deepClone();
        newAssoc.getRight().setCDCardinalityAbsent();
        newAssoc.getLeft().setCDCardinalityAbsent();
        addAssociation(newAssoc);
    }
  }

  /**
   * update directions of underspecified associations to match those in targets Open-World allows
   * specification: unspecified -> uni-directional -> bi-directional Closed-World only allows:
   * unspecified -> uni-directional / bi-directional
   */
  public void updateDir2Match(Collection<ASTCDAssociation> targets) {
    for (ASTCDAssociation src : getCD().getCDDefinition().getCDAssociationsList()) {
      for (ASTCDAssociation target : targets) {
        if (CDAssociationHelper.sameAssociation(target, src)) {
          matchDir(src, target);
          break;
        }
        if (CDAssociationHelper.sameAssociationInReverse(target, src)) {
          matchDirInReverse(src, target);
          break;
        }
      }
    }
  }

  /**
   * update directions of underspecified associations to differ to those in targets Open-World
   * allows specification: unspecified -> uni-directional -> bi-directional
   */
  public void updateDir4Diff(Collection<ASTCDAssociation> targets) {

    for (ASTCDAssociation src : getCD().getCDDefinition().getCDAssociationsList()) {
      for (ASTCDAssociation target : targets) {
        if (CDAssociationHelper.sameAssociation(target, src)) {
          mismatchDir(src, target);
          break;
        }
        if (CDAssociationHelper.sameAssociationInReverse(target, src)) {
          mismatchDirInReverse(src, target);
          break;
        }
      }
    }
  }

  public Set<ASTCDAssociation> buildSuperAssociations(Collection<ASTCDAssociation> originals,
      String dummyClassName) {

    Set<ASTCDAssociation> superSet = new HashSet<>();
    String roleName;

    for (ASTCDAssociation original : originals) {

      if (original.getCDAssocDir().isDefinitiveNavigableRight()) {

        if (original.getRight().isPresentCDRole()) {
          roleName = original.getRight().getCDRole().getName();
        }
        else {
          roleName = CD2AlloyQNameHelper.processQName2RoleName(
              original.getRightQualifiedName().getQName());
        }

        buildDummyAssociation(original.getLeftQualifiedName().getQName(), roleName,
            dummyClassName).ifPresent(superSet::add);

      }

      if (original.getCDAssocDir().isDefinitiveNavigableLeft()) {

        if (original.getLeft().isPresentCDRole()) {
          roleName = original.getLeft().getCDRole().getName();
        }
        else {
          roleName = CD2AlloyQNameHelper.processQName2RoleName(
              original.getLeftQualifiedName().getQName());
        }

        buildDummyAssociation(original.getRightQualifiedName().getQName(), roleName,
            dummyClassName).ifPresent(superSet::add);
      }
    }
    return superSet;
  }

  public void addAssociationsWithoutConflicts(Collection<ASTCDAssociation> dummySet) {
    ICD4CodeArtifactScope scope = CD4CodeMill.scopesGenitorDelegator().createFromAST(getCD());
    for (ASTCDAssociation dummy : dummySet){
      if (getCD().getCDDefinition().getCDAssociationsList().stream().noneMatch(assoc -> CDAssociationHelper.inConflict(dummy, assoc, scope))){
        addAssociation(dummy);
      }
    }
  }


  /*
  delegated methods
   */

  public ASTCDCompilationUnit getCD() {
    return expander.getCD();
  }

  /**
   * add newClass as subclass to superclass
   */
  public void addNewSubClass(String name, ASTCDClass superclass) {
    expander.addNewSubClass(name, superclass);
  }

  /**
   * add newClass as sub-class to astcdInterface
   */
  public void addNewSubClass(String name, ASTCDInterface astcdInterface) {
    expander.addNewSubClass(name, astcdInterface);
  }

  public void addType2Package(ASTCDType astcdType, String packageName) {
    expander.addType2Package(astcdType, packageName);
  }

  public Optional<ASTCDType> addClone(ASTCDType cdType) {
    return expander.addClone(cdType);
  }

  public Optional<ASTCDClass> addDummyClass(ASTCDType srcType) {
    return expander.addDummyClass(srcType);
  }

  public Optional<ASTCDInterface> addDummyInterface(ASTCDInterface srcInterface) {
    return expander.addDummyInterface(srcInterface);
  }

  public Optional<ASTCDClass> addDummyClass(String dummyName) {
    return expander.addDummyClass(dummyName);
  }

  public Optional<ASTCDInterface> addDummyInterface(String dummyName) {
    return expander.addDummyInterface(dummyName);
  }

  public void addAssociation(ASTCDAssociation assoc){
    expander.addAssociation(assoc);
  }


  public void mismatchDir(ASTCDAssociation src, ASTCDAssociation target) {
    expander.mismatchDir(src,target);
  }

  public void mismatchDirInReverse(ASTCDAssociation src, ASTCDAssociation target) {
    expander.mismatchDirInReverse(src,target);
  }

  public void matchDir(ASTCDAssociation src, ASTCDAssociation target) {
    expander.matchDir(src,target);
  }

  public void matchDirInReverse(ASTCDAssociation src, ASTCDAssociation target) {
    expander.matchDirInReverse(src,target);
  }

  public Optional<ASTCDAssociation> buildDummyAssociation(String left, String roleName,
      String right) {
    return expander.buildDummyAssociation(left, roleName, right);
  }

  public void updateUnspecifiedDir2Default() {
    expander.updateUnspecifiedDir2Default();
  }

}
