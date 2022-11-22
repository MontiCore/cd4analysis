package de.monticore.cddiff.cd2alloy.generator;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OpenWorldGenerator extends CD2AlloyGenerator {

  public static CD2AlloyGenerator getInstance() {
    return new OpenWorldGenerator();
  }

  protected OpenWorldGenerator() {}

  @Override
  public String createGenericPart() {
    return super.createGenericPart()
        + System.lineSeparator()

        // additional signature
        + "abstract sig Enum {values: set EnumVal}"
        + System.lineSeparator()
        + System.lineSeparator()

        // additional facts
        + "fact NonEmptyInstancesOnly {"
        + System.lineSeparator()
        + " some Obj"
        + System.lineSeparator()
        + "}"
        + System.lineSeparator()
        + System.lineSeparator()
        + "fact InstancesOfTypes {"
        + System.lineSeparator()
        + " all t: Type | t.inst = {o:Obj | t in o.type.super}}"
        + System.lineSeparator()
        + System.lineSeparator()
        + "fact NoCyclicalInheritance {"
        + System.lineSeparator()
        + " all t1: Type | all t2: Type | {t2 in t1.super} && {t1 in t2.super} => {t1 = t2}}"
        + System.lineSeparator()
        + System.lineSeparator()
        + "fact ReflexiveTransitiveInheritance {"
        + System.lineSeparator()
        + " all t1: Type | t1 in t1.super"
        + System.lineSeparator()
        + " all t1: Type | all t2: Type | {t2 in t1.super} => {t2.super in t1.super}}"
        + System.lineSeparator()
        + System.lineSeparator()
        + "fact GetConsistency {"
        + System.lineSeparator()
        + " all src: Obj | all q : FName | src.get[q] in EnumVal => {some e:Enum | ObjAttrib[src"
        + ".type.inst,q,e.values]}"
        + System.lineSeparator()
        + " all src: Obj | all q : FName | src.get[q] in Val => {some v:Val | ObjAttrib[src.type"
        + ".inst,q,v]}"
        + System.lineSeparator()
        + " all src: Obj | all q : FName | src.get[q] in Obj  => {some target : Type | all o : "
        + "src.type.inst | o.get[q] in target.inst}"
        + System.lineSeparator()
        + "}"
        + System.lineSeparator()
        + System.lineSeparator();
  }

  @Override
  public String executeRuleU4(Set<ASTCDCompilationUnit> asts) {
    StringBuilder commonSigs = new StringBuilder();

    // Union of all Enums
    Set<ASTCDEnum> enumUnion = new HashSet<>();
    for (ASTCDCompilationUnit astcdCompilationUnit : asts) {
      Set<ASTCDEnum> enumSet =
          new HashSet<>(astcdCompilationUnit.getCDDefinition().getCDEnumsList());
      enumUnion.addAll(enumSet);
    }
    // Union of all Enum Names
    Set<String> enumNameUnion = new HashSet<>();
    for (ASTCDEnum astcdEnum : enumUnion) {
      enumNameUnion.add(CDDiffUtil.escape2Alloy(astcdEnum.getSymbol().getFullName()));
    }
    Set<String> enumTypeNameUnion = new HashSet<>();
    for (ASTCDEnum e : enumUnion) {
      List<ASTCDEnumConstant> v = e.getCDEnumConstantList();
      for (ASTCDEnumConstant astcdEnumConstant : v) {
        enumTypeNameUnion.add(
            CDDiffUtil.escape2Alloy(e.getSymbol().getFullName() + "." + astcdEnumConstant.getName()));
      }
    }

    // Generate rule output
    commonSigs.append("// U4: Concrete enum values ").append(System.lineSeparator());
    for (String enumTypeName : enumTypeNameUnion) {
      commonSigs.append("one sig enum_");
      commonSigs.append(enumTypeName);
      commonSigs.append(" extends EnumVal {}").append(System.lineSeparator());
    }

    commonSigs.append(System.lineSeparator());

    for (String enumName : enumNameUnion) {
      commonSigs
          .append("one sig ")
          .append(enumName)
          .append(" extends Enum {}")
          .append(System.lineSeparator())
          .append("fact{")
          .append(enumName)
          .append(".")
          .append("values = (");
      for (String enumTypName : enumTypeNameUnion) {
        if (enumTypName.contains(enumName)) {
          commonSigs.append(" enum_").append(enumTypName).append(" +");
        }
      }
      commonSigs.delete(commonSigs.length() - 1, commonSigs.length());
      commonSigs.append(")}").append(System.lineSeparator());
    }

    return commonSigs.toString();
  }

  /** additional rule for new semantics */
  @Override
  public String executeRuleP0(ASTCDCompilationUnit cd) {
    StringBuilder classFunctions = new StringBuilder();

    // The set of all classes in the class diagram
    Set<ASTCDClass> classes = new HashSet<>(cd.getCDDefinition().getCDClassesList());

    classFunctions
        .append("// P0: New rule for multi-instance semantics. ")
        .append(System.lineSeparator());
    for (ASTCDClass astcdClass : classes) {

      // Computation of Superclasses
      Set<ASTCDInterface> allInterfaces = new HashSet<>(cd.getCDDefinition().getCDInterfacesList());
      Set<ASTCDType> superList = new HashSet<>(superClasses(astcdClass, classes));
      for (ASTCDClass superclass : superClasses(astcdClass, classes)) {
        superList.addAll(interfaces(superclass, allInterfaces));
      }

      // Output P0
      // Functions + Names
      classFunctions
          .append("all c: ")
          .append(CDDiffUtil.escape2Alloy(astcdClass.getSymbol().getFullName()))
          .append(" | c.type=Type_")
          .append(CDDiffUtil.escape2Alloy(astcdClass.getSymbol().getFullName()))
          .append(System.lineSeparator());

      // All subclasses connected with a '+'
      for (ASTCDType superType : superList) {
        classFunctions
            .append("Type_")
            .append(CDDiffUtil.escape2Alloy(superType.getSymbol().getFullName()))
            .append(" in Type_")
            .append(CDDiffUtil.escape2Alloy(astcdClass.getSymbol().getFullName()))
            .append(".super")
            .append(System.lineSeparator());
      }
    }

    Set<ASTCDInterface> interfaces = new HashSet<>(cd.getCDDefinition().getCDInterfacesList());
    for (ASTCDInterface astcdInterface : interfaces) {

      // Computation of Superclasses
      Set<ASTCDInterface> allInterfaces = new HashSet<>(cd.getCDDefinition().getCDInterfacesList());
      Set<ASTCDType> superList = new HashSet<>(interfaces(astcdInterface, allInterfaces));

      // Output P0
      // Functions + Names
      classFunctions
          .append("all i: ")
          .append(CDDiffUtil.escape2Alloy(astcdInterface.getSymbol().getFullName()))
          .append(" | i.type=Type_")
          .append(CDDiffUtil.escape2Alloy(astcdInterface.getSymbol().getFullName()))
          .append(System.lineSeparator());

      // All subclasses connected with a '+'
      for (ASTCDType superType : superList) {
        classFunctions
            .append("Type_")
            .append(CDDiffUtil.escape2Alloy(superType.getSymbol().getFullName()))
            .append(" in Type_")
            .append(CDDiffUtil.escape2Alloy(astcdInterface.getSymbol().getFullName()))
            .append(".super")
            .append(System.lineSeparator());
      }
    }

    return classFunctions.toString();
  }

  @Override
  public String executeRuleP2(ASTCDCompilationUnit cd) {
    return "";
  }

  /**
   * Executes the F1 rule, which generates functions returning all atoms of all subclasses of all
   * classes in class diagram cd.
   */
  @Override
  public String executeRuleF1(ASTCDCompilationUnit cd) {
    StringBuilder classFunctions = new StringBuilder();

    // The set of all classes in the class diagram
    Set<ASTCDClass> classes = new HashSet<>(cd.getCDDefinition().getCDClassesList());

    classFunctions
        .append("// F1: Function returning all atoms of all subclasses of the class. ")
        .append(System.lineSeparator());
    for (ASTCDClass astcdClass : classes) {

      // Output F1
      // Functions + Names
      classFunctions
          .append("fun ")
          .append(CDDiffUtil.escape2Alloy(astcdClass.getSymbol().getFullName()))
          .append("SubsCD")
          .append(cd.getCDDefinition().getName())
          .append(": set Obj { ")
          .append("Type_")
          .append(CDDiffUtil.escape2Alloy(astcdClass.getSymbol().getFullName()))
          .append(".inst")
          .append("}")
          .append(System.lineSeparator());
    }

    return classFunctions.toString();
  }

  /**
   * Executes the F2 rule, which generates functions returning all atoms of all interfaces of all
   * classes implementing the interfaces in class diagram cd.
   */
  @Override
  public String executeRuleF2(ASTCDCompilationUnit cd) {
    StringBuilder interfaceFunctions = new StringBuilder();

    // The set of all classes in the class diagram
    Set<ASTCDInterface> interfaces = new HashSet<>(cd.getCDDefinition().getCDInterfacesList());

    interfaceFunctions
        .append(
            "// F2: Function returning all atoms of all classes implementing "
                + "the interface"
                + ". ")
        .append(System.lineSeparator());
    for (ASTCDInterface astcdInterface : interfaces) {

      // Output F1
      // Functions + Names
      interfaceFunctions
          .append("fun ")
          .append(CDDiffUtil.escape2Alloy(astcdInterface.getSymbol().getFullName()))
          .append("SubsCD")
          .append(cd.getCDDefinition().getName())
          .append(": set Obj { ")
          .append("Type_")
          .append(CDDiffUtil.escape2Alloy(astcdInterface.getSymbol().getFullName()))
          .append(".inst")
          .append("}")
          .append(System.lineSeparator());
    }

    return interfaceFunctions.toString();
  }

  /**
   * Executes the F3 rule, which creates a function for each enumeration type in the CD cd that
   * returns the enumeration’s possible values.
   *
   * @return String for Alloy Module
   */
  @Override
  public String executeRuleF3(ASTCDCompilationUnit cd) {
    StringBuilder classFunctions = new StringBuilder();

    // The Definition of the class diagram
    ASTCDDefinition cdDefinition = cd.getCDDefinition();

    // The set of all enums in the class diagram
    Set<ASTCDEnum> enums = new HashSet<>(cdDefinition.getCDEnumsList());

    // Comment for F3 rule
    classFunctions
        .append("// F3: Functions returning all possible enum values for all enums in the CD. ")
        .append(System.lineSeparator());
    for (ASTCDEnum e : enums) {
      classFunctions
          .append("fun ")
          .append(CDDiffUtil.escape2Alloy(e.getSymbol().getFullName()))
          .append("EnumCD")
          .append(cdDefinition.getName())
          .append(": set EnumVal { ")
          .append(CDDiffUtil.escape2Alloy(e.getSymbol().getFullName()))
          .append(".values }")
          .append(System.lineSeparator());
    }

    return classFunctions.toString();
  }

  @Override
  public String executeRuleP4(ASTCDCompilationUnit cd) {
    return "";
  }
}
