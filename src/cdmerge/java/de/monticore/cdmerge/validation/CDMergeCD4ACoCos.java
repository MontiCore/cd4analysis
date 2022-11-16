/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.validation;

import de.monticore.cd4code._cocos.CD4CodeCoCoChecker;
import de.monticore.cd4code.typescalculator.FullDeriveFromCD4Code;
import de.monticore.cdassociation.cocos.ebnf.*;
import de.monticore.cdbasis.cocos.ebnf.*;
import de.monticore.cdbasis.cocos.mcg.ModifierNotMultipleVisibilitiesCoCo;
import de.monticore.cdbasis.cocos.mcg2ebnf.CDPackageNotContainingCDPackage;
import de.monticore.cdinterfaceandenum.cocos.ebnf.*;

/** This CoCos Set is adapt from the CD4ACoCo Set as several CoCos are disabled */
@Deprecated
public class CDMergeCD4ACoCos {

  public CD4CodeCoCoChecker getCheckerForMergedCDs() {
    CD4CodeCoCoChecker checker = new CD4CodeCoCoChecker();
    addCoCos(checker);
    return checker;
  }

  private void addCoCos(CD4CodeCoCoChecker checker) {

    // CD Basis - EBNF
    checker.addCoCo(new CDAttributeInitialTypeCompatible(new FullDeriveFromCD4Code()));
    checker.addCoCo(new CDAttributeNameLowerCaseIfNotStatic());
    checker.addCoCo(new CDAttributeOverridden());
    // FIXME  disabled for now
    // checker.addCoCo(new CDAttributeTypeExists());
    // checker.addCoCo(new CDAttributeUniqueInClassCoco());
    checker.addCoCo(new CDClassExtendsNotCyclic());
    checker.addCoCo(new CDClassExtendsOnlyClasses());
    checker.addCoCo(new CDClassImplementsNotCyclic());
    checker.addCoCo(new CDClassImplementsOnlyInterfaces());
    checker.addCoCo(new CDClassNameUpperCase());
    checker.addCoCo(new CDDefinitionNameUpperCase());
    checker.addCoCo(new CDDefinitionUniqueCDTypeNames());
    checker.addCoCo(new CDPackageNameUnique());
    checker.addCoCo(new CDPackageUniqueCDTypeNames());
    checker.addCoCo(new CDTypeNoInitializationOfDerivedAttribute());
    checker.addCoCo(new RoleAndFieldNamesUnique());

    // CD Basis - MCG
    checker.addCoCo(new ModifierNotMultipleVisibilitiesCoCo());

    // CD Basis - MCG2EBNF
    checker.addCoCo(new CDPackageNotContainingCDPackage());

    // CD Assocication - EBNF
    // checker.addCoCo(new CDAssociationByAttributeFieldExist());
    checker.addCoCo(new CDAssociationHasSymbol());
    checker.addCoCo(new CDAssociationNameLowerCase());
    checker.addCoCo(new CDAssociationNameUnique());
    checker.addCoCo(new CDAssociationOrderedCardinalityGreaterOne());
    checker.addCoCo(new CDAssociationRoleNameLowerCase());
    // FIXME  disabled for now
    // checker.addCoCo(new CDAssociationRoleNameNoConflictWithLocalAttribute());
    // FIXME  disabled for now
    // checker.addCoCo(new CDAssociationSourceNotEnum());

    // CD InterfaceAndEnum - EBNF
    checker.addCoCo(new CDEnumConstantUnique());
    checker.addCoCo(new CDEnumImplementsNotCyclic());
    checker.addCoCo(new CDEnumImplementsOnlyInterfaces());
    checker.addCoCo(new CDInterfaceExtendsNotCyclic());
    checker.addCoCo(new CDInterfaceExtendsOnlyInterfaces());

    // CD InterfaceAndEnum - MCG
    checker.addCoCo(new CDEnumConstantUnique());
    checker.addCoCo(new CDEnumImplementsNotCyclic());
    checker.addCoCo(new CDEnumImplementsOnlyInterfaces());
    checker.addCoCo(new CDInterfaceExtendsNotCyclic());
    checker.addCoCo(new CDInterfaceExtendsOnlyInterfaces());

    // TODO Cocos do not exist anymore, possibly obsolete
    // checker.addCoCo(new CDAssociationRoleNameNoConflictWithOtherRoleName());
    // checker.addCoCo(new CDAssociactionCompositionCardinalityValid());
    // checker.addCoCo(new CDAttributeOverriddenTypeMatch());

  }
}
