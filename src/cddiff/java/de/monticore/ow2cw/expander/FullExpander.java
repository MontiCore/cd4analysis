package de.monticore.ow2cw.expander;

import de.monticore.cd2alloy.generator.CD2AlloyQNameHelper;
import de.monticore.cd4analysis.CD4AnalysisMill;
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
    for (ASTCDType astcdType : typeList) {
      Optional<CDTypeSymbol> opt = scope.resolveCDTypeDown(astcdType.getSymbol().getFullName());
      if (!opt.isPresent()) {
        addClone(astcdType);
      }
      else {
        addMissingAttributes(opt.get().getAstNode(), astcdType.getCDAttributeList());
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

  public void addMissingAssociations(Collection<ASTCDAssociation> originals,
      boolean withCardinalities) {
    for (ASTCDAssociation srcAssoc : originals) {
      boolean found = getCD().getCDDefinition()
          .getCDAssociationsList()
          .stream()
          .anyMatch(targetAssoc -> CDAssociationHelper.sameAssociation(targetAssoc, srcAssoc));
      if (!found) {
        ASTCDAssociation newAssoc = srcAssoc.deepClone();
        if (!withCardinalities) {
          newAssoc.getRight().setCDCardinalityAbsent();
          newAssoc.getLeft().setCDCardinalityAbsent();
        }
        //todo: check if class/interface has stereotype ""
        getCD().getCDDefinition().getCDElementList().add(newAssoc);
      }
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
        if (CDAssociationHelper.strictMatch(target, src)) {
          matchDir(src, target);
          break;
        }
        if (CDAssociationHelper.strictReverseMatch(target, src)) {
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
        if (CDAssociationHelper.strictMatch(target, src)) {
          mismatchDir(src, target);
          break;
        }
        if (CDAssociationHelper.strictReverseMatch(target, src)) {
          mismatchDirInReverse(src, target);
          break;
        }
      }
    }
  }

  public Set<ASTCDAssociation> addDummyAssociations(Collection<ASTCDAssociation> originals,
      String dummyClassName) {

    Set<ASTCDAssociation> dummies = new HashSet<>();
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
            dummyClassName).ifPresent(dummies::add);

      }
      else {

        if (original.getLeft().isPresentCDRole()) {
          roleName = original.getLeft().getCDRole().getName();
        }
        else {
          roleName = CD2AlloyQNameHelper.processQName2RoleName(
              original.getLeftQualifiedName().getQName());
        }

        buildDummyAssociation(original.getRightQualifiedName().getQName(), roleName,
            dummyClassName).ifPresent(dummies::add);
      }
    }
    return dummies;
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

  public void addClass2Package(ASTCDClass astcdClass, String packageName) {
    expander.addClass2Package(astcdClass, packageName);
  }

  public void addClone(ASTCDType cdType) {
    expander.addClone(cdType);
  }

  public void addDummyClass(ASTCDClass srcClass) {
    expander.addDummyClass(srcClass);
  }

  public void addDummyClass(String dummyName) {
    expander.addDummyClass(dummyName);
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
