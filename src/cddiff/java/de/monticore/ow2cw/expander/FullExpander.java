package de.monticore.ow2cw.expander;

import de.monticore.cd.facade.MCQualifiedNameFacade;
import de.monticore.cd2alloy.generator.CD2AlloyQNameHelper;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis._auxiliary.MCBasicTypesMillForCD4Analysis;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdassociation.CDAssociationMill;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.ow2cw.CDAssociationHelper;

import java.util.*;

public class FullExpander implements CDExpander{
  final protected CDExpander expander;

  /**
   * @param expander checking if adding/etc is allowed
   */
  public FullExpander(CDExpander expander) {
    this.expander = expander;
  }

  public ASTCDCompilationUnit getCD() {
    return expander.getCD();
  }

  /**
   * add newClass as subclass to superclass
   */
  public void addNewSubClass(String name, ASTCDClass superclass) {
    expander.addNewSubClass(name,superclass);
  }

  /**
   * add newClass as sub-class to astcdInterface
   */
  public void addNewSubClass(String name, ASTCDInterface astcdInterface) {
    expander.addNewSubClass(name,astcdInterface);
  }

  /**
   * Default Package is troublesome!
   * todo: fix problem with nested packages
   */
  public void addClass2Package(ASTCDClass astcdClass, String packageName) {
    expander.addClass2Package(astcdClass,packageName);
  }

  /**
   * Default Package is troublesome!
   * todo: fix problem with nested packages
   */
  public void addClone(ASTCDType cdType) {
    expander.addClone(cdType);
  }

  public void addDummyClass(ASTCDClass srcClass) {
    expander.addDummyClass(srcClass);
  }

  public void addDummyClass(String dummyName) {
    expander.addDummyClass(dummyName);
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

  public void addMissingAssociations(Collection<ASTCDAssociation> assocs,
      boolean withCardinalities) {
    for (ASTCDAssociation srcAssoc : assocs) {
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
   * update directions of underspecified associations to match those in assocs
   * Open-World allows specification: unspecified -> uni-directional -> bi-directional
   * Closed-World only allows: unspecified -> uni-directional / bi-directional
   */
  public void updateDir2Match(Collection<ASTCDAssociation> assocs, boolean isOpenWorld) {
    for (ASTCDAssociation src : assocs) {
      for (ASTCDAssociation target : getCD().getCDDefinition().getCDAssociationsList()) {
        if (CDAssociationHelper.strictMatch(src, target)) {
          if (isOpenWorld && (!target.getCDAssocDir().isBidirectional()) && src.getCDAssocDir()
              .isBidirectional()) {
            target.setCDAssocDir(CD4AnalysisMill.cDBiDirBuilder().build());
            break;
          }
          if (!(target.getCDAssocDir().isDefinitiveNavigableLeft() || target.getCDAssocDir()
              .isDefinitiveNavigableRight())) {
            target.setCDAssocDir(src.getCDAssocDir().deepClone());
          }
          break;
        }
        if (CDAssociationHelper.strictReverseMatch(src, target)) {
          if (isOpenWorld && (!target.getCDAssocDir().isBidirectional()) && src.getCDAssocDir()
              .isBidirectional()) {
            target.setCDAssocDir(CD4AnalysisMill.cDBiDirBuilder().build());
            break;
          }
          if (!(target.getCDAssocDir().isDefinitiveNavigableLeft() || target.getCDAssocDir()
              .isDefinitiveNavigableRight())) {
            if (src.getCDAssocDir().isDefinitiveNavigableRight()) {
              target.setCDAssocDir(CD4AnalysisMill.cDLeftToRightDirBuilder().build());
            }
            else {
              if (src.getCDAssocDir().isDefinitiveNavigableLeft()) {
                target.setCDAssocDir(CD4AnalysisMill.cDRightToLeftDirBuilder().build());
              }
            }
          }
          break;
        }
      }
    }
  }

  /**
   * update directions of underspecified associations to differ to those in assocs
   * Open-World allows specification: unspecified -> uni-directional -> bi-directional
   */
  public void updateDir4Diff(Collection<ASTCDAssociation> assocs) {
    for (ASTCDAssociation src : assocs) {
      for (ASTCDAssociation target : getCD().getCDDefinition().getCDAssociationsList()) {
        if (CDAssociationHelper.strictMatch(src, target)) {
          if (!(target.getCDAssocDir().isDefinitiveNavigableLeft() || target.getCDAssocDir()
              .isDefinitiveNavigableRight())) {
            if (src.getCDAssocDir().isDefinitiveNavigableRight()) {
              target.setCDAssocDir(CD4AnalysisMill.cDRightToLeftDirBuilder().build());
            }
            else if (src.getCDAssocDir().isDefinitiveNavigableLeft()) {
              target.setCDAssocDir(CD4AnalysisMill.cDLeftToRightDirBuilder().build());
            }
          }
          break;
        }
        if (CDAssociationHelper.strictReverseMatch(src, target)) {
          if (!(target.getCDAssocDir().isDefinitiveNavigableLeft() || target.getCDAssocDir()
              .isDefinitiveNavigableRight())) {
            if (src.getCDAssocDir().isDefinitiveNavigableRight()) {
              target.setCDAssocDir(CD4AnalysisMill.cDLeftToRightDirBuilder().build());
            }
            else if (src.getCDAssocDir().isDefinitiveNavigableLeft()) {
              target.setCDAssocDir(CD4AnalysisMill.cDRightToLeftDirBuilder().build());
            }
          }
          break;
        }
      }
    }
  }

  public Set<ASTCDAssociation> addDummyAssociations(Collection<ASTCDAssociation> isolated,
      String dummyClassName) {

    Set<ASTCDAssociation> dummies = new HashSet<>();
    String roleName;

    for (ASTCDAssociation src : isolated) {

      if (src.getCDAssocDir().isDefinitiveNavigableRight()) {

        if (src.getRight().isPresentCDRole()) {
          roleName = src.getRight().getCDRole().getName();
        }
        else {
          roleName = CD2AlloyQNameHelper.processQName2RoleName(
              src.getRightQualifiedName().getQName());
        }

        dummies.add(
            buildDummyAssociation(src.getLeftQualifiedName().getQName(), roleName, dummyClassName));

      }
      else {

        if (src.getLeft().isPresentCDRole()) {
          roleName = src.getLeft().getCDRole().getName();
        }
        else {
          roleName = CD2AlloyQNameHelper.processQName2RoleName(
              src.getLeftQualifiedName().getQName());
        }

        dummies.add(buildDummyAssociation(src.getRightQualifiedName().getQName(), roleName,
            dummyClassName));
      }
    }
    return dummies;
  }

  public ASTCDAssociation buildDummyAssociation(String left, String roleName, String right) {

    ASTCDAssociation dummy = CDAssociationMill.cDAssociationBuilder()
        .setModifier(CD4CodeMill.modifierBuilder().build())
        .setCDAssocType(CDAssociationMill.cDAssocTypeAssocBuilder().build())
        .setLeft(CDAssociationMill.cDAssocLeftSideBuilder()
            .setCDCardinalityAbsent()
            .setCDRoleAbsent()
            .setCDOrderedAbsent()
            .setCDQualifierAbsent()
            .setModifier(CD4CodeMill.modifierBuilder().build())
            .setMCQualifiedType(MCBasicTypesMillForCD4Analysis.mCQualifiedTypeBuilder()
                .setMCQualifiedName(MCQualifiedNameFacade.createQualifiedName(left))
                .build())
            .build())
        .setCDAssocDir(CD4AnalysisMill.cDLeftToRightDirBuilder().build())
        .setRight(CDAssociationMill.cDAssocRightSideBuilder()
            .setCDCardinalityAbsent()
            .setCDRole(CDAssociationMill.cDRoleBuilder().setName(roleName).build())
            .setCDOrderedAbsent()
            .setCDQualifierAbsent()
            .setModifier(CD4CodeMill.modifierBuilder().build())
            .setMCQualifiedType(MCBasicTypesMillForCD4Analysis.mCQualifiedTypeBuilder()
                .setMCQualifiedName(MCQualifiedNameFacade.createQualifiedName(right))
                .build())
            .build())
        .build();
    getCD().getCDDefinition().getCDElementList().add(dummy);
    return dummy;
  }

  public void updateUnspecifiedDir2Default() {
    for (ASTCDAssociation assoc : getCD().getCDDefinition().getCDAssociationsList()) {
      if (!(assoc.getCDAssocDir().isDefinitiveNavigableRight() || assoc.getCDAssocDir()
          .isDefinitiveNavigableLeft())) {
        assoc.setCDAssocDir(CD4AnalysisMill.cDBiDirBuilder().build());
      }
    }
  }

}
