/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.ow2cw.expander;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cddiff.ow2cw.CDAssociationHelper;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import java.util.*;

public class FullExpander implements CDExpander {
  protected final CDExpander expander;

  /** @param expander checking if adding/etc is allowed */
  public FullExpander(CDExpander expander) {
    this.expander = expander;
  }

  public void updateImportStatements(Collection<ASTMCImportStatement> imports) {
    imports.forEach(this::addImportStatement);
  }

  public <T extends ASTCDType> void addMissingTypesAndAttributes(Collection<T> typeList) {
    ICD4CodeArtifactScope scope = (ICD4CodeArtifactScope) getCD().getEnclosingScope();
    for (ASTCDType type : typeList) {
      Optional<CDTypeSymbol> opt =
          scope.resolveCDTypeDown(type.getSymbol().getInternalQualifiedName());
      if (opt.isEmpty()) {
        if (type instanceof ASTCDInterface) {
          addDummyInterface((ASTCDInterface) type)
              .ifPresent(
                  newInterface -> addMissingAttributes(newInterface, type.getCDAttributeList()));
        } else {
          addDummyClass(type)
              .ifPresent(newClass -> addMissingAttributes(newClass, type.getCDAttributeList()));
        }
      } else {
        addMissingAttributes(opt.get().getAstNode(), type.getCDAttributeList());
      }
    }
  }

  public void addMissingEnumsAndConstants(Collection<ASTCDEnum> enumCol) {
    ICD4CodeArtifactScope scope = (ICD4CodeArtifactScope) getCD().getEnclosingScope();
    // add enums and enum constants exclusive to first
    for (ASTCDEnum astcdEnum : enumCol) {
      Optional<CDTypeSymbol> opt =
          scope.resolveCDTypeDown(astcdEnum.getSymbol().getInternalQualifiedName());
      if (opt.isEmpty()) {
        addClone(astcdEnum);
      } else {
        for (ASTCDEnumConstant constant : astcdEnum.getCDEnumConstantList()) {
          boolean found =
              opt.get().getFieldList().stream()
                  .anyMatch(field -> field.getName().equals(constant.getName()));
          if (!found) {
            // I wanted to avoid reflection, but I think this is just reflection with extra steps...
            for (ASTCDEnum someEnum : getCD().getCDDefinition().getCDEnumsList()) {
              if (astcdEnum
                  .getSymbol()
                  .getInternalQualifiedName()
                  .equals(someEnum.getSymbol().getInternalQualifiedName())) {
                addEnumConstant(someEnum, constant.deepClone());
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
      boolean found =
          cdType.getCDAttributeList().stream()
              .anyMatch(attribute2 -> attribute1.getName().equals(attribute2.getName()));
      if (!found) {
        ASTCDAttribute newAttribute = attribute1.deepClone();
        addAttribute(cdType, newAttribute);
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

  public Set<ASTCDAssociation> buildSuperAssociations(
      Collection<ASTCDAssociation> originals, String dummyClassName) {

    Set<ASTCDAssociation> superSet = new HashSet<>();
    String roleName;

    for (ASTCDAssociation original : originals) {

      if (original.getCDAssocDir().isDefinitiveNavigableRight()) {
        buildDummyAssociation(
                original.getLeftQualifiedName().getQName(),
                CDDiffUtil.inferRole(original.getRight()),
                dummyClassName)
            .ifPresent(superSet::add);
      }

      if (original.getCDAssocDir().isDefinitiveNavigableLeft()) {
        buildDummyAssociation(
                original.getRightQualifiedName().getQName(),
                CDDiffUtil.inferRole(original.getLeft()),
                dummyClassName)
            .ifPresent(superSet::add);
      }
    }
    return superSet;
  }

  public void addAssociationsWithoutConflicts(Collection<ASTCDAssociation> dummySet) {
    ICD4CodeArtifactScope scope = (ICD4CodeArtifactScope) getCD().getEnclosingScope();
    for (ASTCDAssociation dummy : dummySet) {
      if (getCD().getCDDefinition().getCDAssociationsList().stream()
          .noneMatch(assoc -> CDAssociationHelper.inConflict(dummy, assoc, scope))) {
        addAssociation(dummy);
      }
    }
  }

  public Set<ASTCDAssociation> getDummies4Diff(
      Collection<ASTCDType> typeCol, String assocTargetName) {
    ICD4CodeArtifactScope scope = (ICD4CodeArtifactScope) getCD().getEnclosingScope();
    Set<ASTCDAssociation> newAssocs = new HashSet<>();
    for (ASTCDType srcType : typeCol) {
      Optional<CDTypeSymbol> opt =
          scope.resolveCDTypeDown(srcType.getSymbol().getInternalQualifiedName());
      if (opt.isEmpty()) {
        addDummyClass(srcType);
      }
      if (srcType.getModifier().isPresentStereotype()
          && srcType.getModifier().getStereotype().contains(VariableExpander.VAR_TAG)) {
        buildDummyAssociation(
                srcType.getSymbol().getInternalQualifiedName(),
                "myNew" + assocTargetName,
                assocTargetName)
            .ifPresent(newAssocs::add);
      }
    }
    return newAssocs;
  }

  public void addNewEnumConstants(Collection<ASTCDEnum> enumCol) {
    for (ASTCDEnum srcEnum : enumCol) {
      if (srcEnum.getModifier().isPresentStereotype()
          && srcEnum.getModifier().getStereotype().contains(VariableExpander.VAR_TAG)) {
        for (ASTCDEnum targetEnum : getCD().getCDDefinition().getCDEnumsList()) {
          if (srcEnum
              .getSymbol()
              .getInternalQualifiedName()
              .equals(targetEnum.getSymbol().getInternalQualifiedName())) {
            addEnumConstant(
                targetEnum,
                CD4CodeMill.cD4CodeEnumConstantBuilder()
                    .setArgumentsAbsent()
                    .setName("myNew" + targetEnum.getName() + "Const")
                    .build());
            break;
          }
        }
      }
    }
  }

  /*
  delegated methods
   */

  public ASTCDCompilationUnit getCD() {
    return expander.getCD();
  }

  public void addImportStatement(ASTMCImportStatement imp) {
    expander.addImportStatement(imp);
  }

  /** add newClass as subclass to superclass */
  public void addNewSubClass(String name, ASTCDClass superclass) {
    expander.addNewSubClass(name, superclass);
  }

  /** add newClass as sub-class to astcdInterface */
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

  public void addAssociation(ASTCDAssociation assoc) {
    expander.addAssociation(assoc);
  }

  public void addAttribute(ASTCDType type, ASTCDAttribute attribute) {
    expander.addAttribute(type, attribute);
  }

  public void addEnumConstant(ASTCDEnum targetEnum, ASTCDEnumConstant constant) {
    expander.addEnumConstant(targetEnum, constant);
  }

  public void updateExtends(ASTCDClass targetClass, Set<String> extendsSet) {
    expander.updateExtends(targetClass, extendsSet);
  }

  public void updateImplements(ASTCDClass targetClass, Set<String> implementsSet) {
    expander.updateImplements(targetClass, implementsSet);
  }

  public void updateExtends(ASTCDInterface targetInterface, Set<String> extendsSet) {
    expander.updateExtends(targetInterface, extendsSet);
  }

  public void mismatchDir(ASTCDAssociation src, ASTCDAssociation target) {
    expander.mismatchDir(src, target);
  }

  public void mismatchDirInReverse(ASTCDAssociation src, ASTCDAssociation target) {
    expander.mismatchDirInReverse(src, target);
  }

  public void matchDir(ASTCDAssociation src, ASTCDAssociation target) {
    expander.matchDir(src, target);
  }

  public void matchDirInReverse(ASTCDAssociation src, ASTCDAssociation target) {
    expander.matchDirInReverse(src, target);
  }

  public Optional<ASTCDAssociation> buildDummyAssociation(
      String left, String roleName, String right) {
    return expander.buildDummyAssociation(left, roleName, right);
  }

  public void updateUnspecifiedDir2Default() {
    expander.updateUnspecifiedDir2Default();
  }
}
