/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a;

import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisCoCoChecker;
import de.monticore.umlcd4a.cocos.ebnf.*;
import de.monticore.umlcd4a.cocos.mcg.*;
import de.monticore.umlcd4a.cocos.mcg2ebnf.*;

/**
 * Set of CoCos for CD4A.
 *
 * @author Robert Heim
 */
public class CD4ACoCos {

  public CD4AnalysisCoCoChecker getCheckerForAllCoCos() {
    CD4AnalysisCoCoChecker checker = new CD4AnalysisCoCoChecker();

    addEbnfCoCos(checker);
    addMcgCoCos(checker);
    addMcg2EbnfCoCos(checker);

    return checker;
  }
  
  public CD4AnalysisCoCoChecker getCheckerForCode() {
    CD4AnalysisCoCoChecker checker = new CD4AnalysisCoCoChecker();

    addEbnfCoCos(checker);
    addMcgCoCos(checker);

    return checker;
  }

  public CD4AnalysisCoCoChecker getCheckerForMcgCoCos() {
    CD4AnalysisCoCoChecker checker = new CD4AnalysisCoCoChecker();
    addMcgCoCos(checker);
    return checker;
  }

  public CD4AnalysisCoCoChecker getCheckerForEbnfCoCos() {
    CD4AnalysisCoCoChecker checker = new CD4AnalysisCoCoChecker();
    addEbnfCoCos(checker);
    return checker;
  }

  public CD4AnalysisCoCoChecker getCheckerForMcg2EbnfCoCos() {
    CD4AnalysisCoCoChecker checker = new CD4AnalysisCoCoChecker();
    addMcg2EbnfCoCos(checker);
    return checker;
  }

  private void addEbnfCoCos(CD4AnalysisCoCoChecker checker) {
    checker.addCoCo(new DiagramNameUpperCase());
    checker.addCoCo(new UniqueTypeNames());
    checker.addCoCo(new TypeNameUpperCase());
    checker.addCoCo(new EnumConstantsUnique());
    checker.addCoCo(new AttributeUniqueInClassCoco());
    checker.addCoCo(new AssociationSourceTypeNotExternal());
    checker.addCoCo(new ExtendsNotCyclic());
    checker.addCoCo(new InterfaceExtendsOnlyInterfaces());
    checker.addCoCo(new ClassExtendsOnlyClasses());
    checker.addCoCo(new ClassImplementOnlyInterfaces());
    checker.addCoCo(new EnumImplementOnlyInterfaces());
    checker.addCoCo(new GenericsNotNested());
    checker.addCoCo(new GenericTypeHasParameters());
    checker.addCoCo(new GenericParameterCountMatch());
    checker.addCoCo(new AttributeTypeCompatible());
    checker.addCoCo(new AttributeNameLowerCase());
    checker.addCoCo(new AttributeOverriddenTypeMatch());
    checker.addCoCo(new AttributeTypeExists());
    checker.addCoCo(new CompositionCardinalityValid());
    checker.addCoCo(new AssociationQualifierTypeExists());
    checker.addCoCo(new AssociationQualifierAttributeExistsInTarget());
    checker.addCoCo(new AssociationQualifierOnCorrectSide());
    checker.addCoCo(new AssociationSourceNotEnum());
    checker.addCoCo(new AssociationOrderedCardinalityGreaterOne());
    checker.addCoCo(new AssociationNameLowerCase());
    checker.addCoCo(new AssociationRoleNameLowerCase());
    // This CoCo is temporary disabled as the association name does not need to be unique within a model.
    // Instead, it must be unique within a specific class hierarchy.
    // checker.addCoCo(new AssociationNameUnique());
    checker.addCoCo(new AssociationNameNoConflictWithAttribute());
    checker.addCoCo(new AssociationRoleNameNoConflictWithAttribute());
    checker.addCoCo(new AssociationRoleNameNoConflictWithOtherRoleNames());
    checker.addCoCo(new AssociationSrcAndTargetTypeExistChecker());
    checker.addCoCo(new TypeNoInitializationOfDerivedAttribute());
  }

  private void addMcgCoCos(CD4AnalysisCoCoChecker checker) {
    checker.addCoCo(new AssociationModifierCoCo());
    checker.addCoCo(new InterfaceAttributesStaticCoCo());
    checker.addCoCo(new InterfaceInvalidModifiersCoCo());
    checker.addCoCo(new AttributeNotAbstractCoCo());
    checker.addCoCo(new ModifierNotMultipleVisibilitiesCoCo());
    checker.addCoCo(new ClassInvalidModifiersCoCo());
    checker.addCoCo(new EnumInvalidModifiersCoCo());
  }

  private void addMcg2EbnfCoCos(CD4AnalysisCoCoChecker checker) {
    checker.addCoCo(new ClassModifierOnlyAbstractCoCo());
    checker.addCoCo(new ClassNoConstructorsCoCo());
    checker.addCoCo(new ClassNoMethodsCoCo());
    checker.addCoCo(new AttributeModifierOnlyDerivedCoCo());
    checker.addCoCo(new InterfaceNoModifierCoCo());
    checker.addCoCo(new InterfaceNoAttributesCoCo());
    checker.addCoCo(new InterfaceNoMethodsCoCo());
    checker.addCoCo(new EnumNoModifierCoCo());
    checker.addCoCo(new EnumNoConstructorsCoCo());
    checker.addCoCo(new EnumNoAttributesCoCo());
    checker.addCoCo(new EnumNoMethodsCoCo());
    checker.addCoCo(new AssociationNoStereotypesCoCo());
    checker.addCoCo(new AssociationEndModifierRestrictionCoCo());
    checker.addCoCo(new StereoValueNoValueCoCo());
  }
}
