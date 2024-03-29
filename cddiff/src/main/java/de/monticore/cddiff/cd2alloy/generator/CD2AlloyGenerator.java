/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.cd2alloy.generator;

import de.monticore.cd4analysis._parser.CD4AnalysisParser;
import de.monticore.cd4code.trafo.CD4CodeDirectCompositionTrafo;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.*;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.se_rwth.commons.logging.Log;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import org.apache.commons.io.FileUtils;

/** Entry point for the CD2Alloy generator. */
public class CD2AlloyGenerator {
  private final String LOGGER_NAME = CD2AlloyGenerator.class.getName();

  public static CD2AlloyGenerator getInstance() {
    return new CD2AlloyGenerator();
  }

  protected CD2AlloyGenerator() {}

  /**
   * Generates the generic part of an alloy module
   *
   * @return String containing all predicates needed to model CDs in Alloy
   */
  protected String createGenericPart() {

    // Additional facts to restrict the solution space to usable solutions
    //     genericPart.append("// Restrict names to names that are used for Objects" + System
    //     .lineSeparator() +
    //         "fact OnlyUsedNames{" + System.lineSeparator() +
    //         "  all  fName:FName | some v:Val  | some o:Obj | v in o.get[fName] " + System
    //         .lineSeparator() +
    //         "}" + System.lineSeparator() + System.lineSeparator());
    //
    //     genericPart.append("// Restrict values to values actually used in get relation  " +
    //     System.lineSeparator() +
    //         "fact OnlyUsedValues{" + System.lineSeparator() +
    //         "  all  v:Val | some fName:FName | some o:Obj | v in o.get[fName] " + System
    //         .lineSeparator() +
    //         "}" + System.lineSeparator() + System.lineSeparator());
    //
    //     genericPart.append("// Restrict enum values to enum values actually used in get
    //     relation  " + System.lineSeparator() +
    //         "fact OnlyUsedEnumValues{" + System.lineSeparator() +
    //         "  all  v:EnumVal | some fName:FName | some o:Obj | v in o.get[fName] " + System
    //         .lineSeparator() +
    //         "}" + System.lineSeparator() + System.lineSeparator());

    return "// ***** Generic Part ***** "
        + System.lineSeparator()
        + " "
        + System.lineSeparator()

        // Comment for abstract Signatures
        + "// The abstract signatures FName, Obj, Val, and EnumVal. "
        + System.lineSeparator()

        // Abstract Signature for Objects
        + "abstract sig Obj { get: FName -> {Obj + Val + EnumVal}, type: Type } "
        + System.lineSeparator()
        // Abstract Signature for Names
        + "abstract sig FName {} "
        + System.lineSeparator()
        // Abstract Signature for Values
        + "abstract sig Val {} "
        + System.lineSeparator()
        // Abstract Signature for EnumValues
        + "abstract sig EnumVal {} "
        + System.lineSeparator()
        + "abstract sig Type { super: set Type, inst : set Obj}"
        + System.lineSeparator()
        + " "
        + System.lineSeparator()

        // Comment for Parametrized predicates
        + "pred ObjTypes[obj: set Obj, types: set Type]{"
        + System.lineSeparator()
        + " all o:obj| o.type.super = types}"
        + System.lineSeparator()
        + System.lineSeparator()
        + "// Predicates used to specify cardinality constraints for navigable association"
        + System.lineSeparator()
        + "// ends and for association ends of undirected associations."
        + System.lineSeparator()
        + "pred ObjAttrib[objs: set Obj, fName: one FName,"
        + System.lineSeparator()
        + " fType: set {Obj + Val + EnumVal}] {"
        + System.lineSeparator()
        + " objs.get[fName] in fType"
        + System.lineSeparator()
        + " all o: objs| one o.get[fName] }"
        + System.lineSeparator()
        + System.lineSeparator()
        + "pred ObjFNames[objs: set Obj, fNames:set FName]{"
        + System.lineSeparator()
        + " no objs.get[FName - fNames] }"
        + System.lineSeparator()
        + System.lineSeparator()
        + "pred BidiAssoc[left: set Obj, lFName:one FName,"
        + System.lineSeparator()
        + " right: set Obj, rFName:one FName] {"
        + System.lineSeparator()
        + " all l: left | all r: l.get[lFName] | l in r.get[rFName]"
        + System.lineSeparator()
        + " all r: right | all l: r.get[rFName] | r in l.get[lFName] }"
        + System.lineSeparator()
        + System.lineSeparator()
        + "pred Composition[compos: Obj->Obj, right: set Obj] {"
        + System.lineSeparator()
        + " all r: right | lone compos.r }"
        + System.lineSeparator()
        + " "
        + System.lineSeparator()
        + "fun rel[wholes: set Obj, fn: FName] : Obj->Obj {"
        + System.lineSeparator()
        + " {o1:Obj,o2:Obj|o1->fn->o2 in wholes <: get} } "
        + System.lineSeparator()
        + System.lineSeparator()
        + "// Predicates used to specify cardinality constraints for navigable association"
        + System.lineSeparator()
        + "// ends and for association ends of undirected associations. "
        + System.lineSeparator()
        + "pred ObjUAttrib[objs: set Obj, fName:one FName, fType:set Obj, up: Int] {"
        + System.lineSeparator()
        + " objs.get[fName] in fType"
        + System.lineSeparator()
        + " all o: objs| (#o.get[fName] =< up) } "
        + System.lineSeparator()
        + System.lineSeparator()
        + "pred ObjLAttrib[objs: set Obj, fName: one FName, fType: set Obj, low: Int] {"
        + System.lineSeparator()
        + " objs.get[fName] in fType"
        + System.lineSeparator()
        + " all o: objs | (#o.get[fName] >= low) }"
        + System.lineSeparator()
        + System.lineSeparator()
        + "pred ObjLUAttrib[objs:set Obj, fName:one FName, fType:set Obj,"
        + System.lineSeparator()
        + " low: Int, up: Int] {"
        + System.lineSeparator()
        + " ObjLAttrib[objs, fName, fType, low]"
        + System.lineSeparator()
        + " ObjUAttrib[objs, fName, fType, up] }"
        + System.lineSeparator()
        + System.lineSeparator()
        + "// Parametrized predicates used to specify cardinality constraints for non-"
        + System.lineSeparator()
        + "// navigable association ends. "
        + System.lineSeparator()
        + "pred ObjL[objs: set Obj, fName:one FName, fType: set Obj, low: Int] {"
        + System.lineSeparator()
        + " all r: objs | # { l: fType | r in l.get[fName]} >= low } "
        + System.lineSeparator()
        + System.lineSeparator()
        + "pred ObjU[objs: set Obj, fName:one FName, fType: set Obj, up: Int] {"
        + System.lineSeparator()
        + " all r: objs | # { l: fType | r in l.get[fName]} =< up } "
        + System.lineSeparator()
        + System.lineSeparator()
        + "pred ObjLU[objs: set Obj, fName:one FName, fType: set Obj,"
        + System.lineSeparator()
        + " low: Int, up: Int] {"
        + System.lineSeparator()
        + " ObjL[objs, fName, fType, low]"
        + System.lineSeparator()
        + " ObjU[objs, fName, fType, up] }"
        + System.lineSeparator()
        + System.lineSeparator()
        + "fact InstancesOfTypes {"
        + System.lineSeparator()
        + " all t: Type | t.inst = {o:Obj | t in o.type.super}}"
        + System.lineSeparator()
        + System.lineSeparator()
        + ""

        // Additional Fact from in TechRep Example to exclude illegal
        + "fact NonEmptyInstancesOnly {"
        + System.lineSeparator()
        + " some Obj"
        + System.lineSeparator()
        + "}"
        + System.lineSeparator()
        + System.lineSeparator();
  }

  public String executeRuleU1(Set<ASTCDCompilationUnit> asts) {
    StringBuilder commonSigs = new StringBuilder();

    // Union of all classes
    Set<ASTCDType> typeUnion = new HashSet<>();
    for (ASTCDCompilationUnit astcdCompilationUnit : asts) {
      typeUnion.addAll(new HashSet<>(astcdCompilationUnit.getCDDefinition().getCDClassesList()));
      typeUnion.addAll(new HashSet<>(astcdCompilationUnit.getCDDefinition().getCDInterfacesList()));
    }

    // Union of all type names
    Set<String> typeNameUnion = new HashSet<>();
    for (ASTCDType astcdType : typeUnion) {
      typeNameUnion.add(CDDiffUtil.escape2Alloy(astcdType.getSymbol().getInternalQualifiedName()));
    }

    // Output generation
    commonSigs.append("// U1: Common types ").append(System.lineSeparator());
    if (typeNameUnion.isEmpty()) {
      commonSigs.append("fact {no Obj}").append(System.lineSeparator());
    }
    for (String typeName : typeNameUnion) {
      commonSigs.append("sig ");
      commonSigs.append(typeName);
      commonSigs.append(" extends Obj {}").append(System.lineSeparator());
    }

    return commonSigs.toString();
  }

  public String executeRuleU2(Set<ASTCDCompilationUnit> asts) {
    StringBuilder commonSigs = new StringBuilder();

    // Union of all classes
    Set<ASTCDClass> classUnion = new HashSet<>();
    for (ASTCDCompilationUnit astcdCompilationUnit : asts) {
      Set<ASTCDClass> classSet =
          new HashSet<>(astcdCompilationUnit.getCDDefinition().getCDClassesList());
      classUnion.addAll(classSet);
    }

    // Union of all interfaces
    Set<ASTCDInterface> interfaceUnion = new HashSet<>();
    for (ASTCDCompilationUnit astcdCompilationUnit : asts) {
      Set<ASTCDInterface> interfaceSet =
          new HashSet<>(astcdCompilationUnit.getCDDefinition().getCDInterfacesList());
      interfaceUnion.addAll(interfaceSet);
    }

    // Union of all attributes in all classes
    Set<ASTCDAttribute> attributeUnion = new HashSet<>();
    for (ASTCDClass astcdClass : classUnion) {
      Set<ASTCDAttribute> attributes = new HashSet<>(astcdClass.getCDAttributeList());
      attributeUnion.addAll(attributes);
    }

    // Union of all attributes in all interfaces
    for (ASTCDInterface astcdInterface : interfaceUnion) {
      Set<ASTCDAttribute> attributes = new HashSet<>(astcdInterface.getCDAttributeList());
      attributeUnion.addAll(attributes);
    }

    // Union of all Attribute Names
    Set<String> attributeNameUnion = new HashSet<>();
    for (ASTCDAttribute astcdAttribute : attributeUnion) {
      attributeNameUnion.add(astcdAttribute.getName());
    }

    // Union of all Associations
    Set<ASTCDAssociation> associationUnion = new HashSet<>();
    for (ASTCDCompilationUnit astcdCompilationUnit : asts) {
      Set<ASTCDAssociation> associationSet =
          new HashSet<>(astcdCompilationUnit.getCDDefinition().getCDAssociationsList());
      associationUnion.addAll(associationSet);
    }

    // Union of all Role Names
    Set<String> roleNameUnion = new HashSet<>();
    for (ASTCDAssociation association : associationUnion) {

      String leftRole = CDDiffUtil.inferRole(association.getLeft());
      String rightRole = CDDiffUtil.inferRole(association.getRight());

      if (association.getCDAssocDir().isDefinitiveNavigableLeft()) {
        roleNameUnion.add(leftRole);
      }

      if (association.getCDAssocDir().isDefinitiveNavigableRight()) {
        roleNameUnion.add(rightRole);
      }

      if (!(association.getCDAssocDir().isDefinitiveNavigableLeft()
          || association.getCDAssocDir().isDefinitiveNavigableRight())) {
        roleNameUnion.add(leftRole);
        roleNameUnion.add(rightRole);
      }
    }

    // Union of all attribute and role names
    Set<String> nameUnion = new HashSet<>();
    nameUnion.addAll(attributeNameUnion);
    nameUnion.addAll(roleNameUnion);

    // Generate output of U2 rule
    commonSigs.append("// U2: Common names ").append(System.lineSeparator());
    if (nameUnion.isEmpty()) {
      commonSigs.append("fact {no FName}").append(System.lineSeparator());
    }
    for (String n : nameUnion) {
      commonSigs
          .append("one sig ")
          .append(n)
          .append(" extends FName {}")
          .append(System.lineSeparator());
    }

    return commonSigs.toString();
  }

  public String executeRuleU3(Set<ASTCDCompilationUnit> asts) {
    StringBuilder commonSigs = new StringBuilder();

    // Union of all classes
    Set<ASTCDClass> classUnion = new HashSet<>();
    for (ASTCDCompilationUnit astcdCompilationUnit : asts) {
      Set<ASTCDClass> classSet =
          new HashSet<>(astcdCompilationUnit.getCDDefinition().getCDClassesList());
      classUnion.addAll(classSet);
    }

    // Union of all class Names
    Set<String> classNameUnion = new HashSet<>();
    for (ASTCDClass astcdClass : classUnion) {
      classNameUnion.add(
          CDDiffUtil.escape2Alloy(astcdClass.getSymbol().getInternalQualifiedName()));
    }

    // Union of all attributes in all classes
    Set<ASTCDAttribute> attributeUnion = new HashSet<>();
    for (ASTCDClass astcdClass : classUnion) {
      Set<ASTCDAttribute> attributes = new HashSet<>(astcdClass.getCDAttributeList());
      attributeUnion.addAll(attributes);
    }

    // Union of all Interfaces
    Set<ASTCDInterface> interfaceUnion = new HashSet<>();
    for (ASTCDCompilationUnit astcdCompilationUnit : asts) {
      Set<ASTCDInterface> interfaceSet =
          new HashSet<>(astcdCompilationUnit.getCDDefinition().getCDInterfacesList());
      interfaceUnion.addAll(interfaceSet);
    }
    // Union of all interface Names
    Set<String> interfaceNameUnion = new HashSet<>();
    for (ASTCDInterface astcdInterface : interfaceUnion) {
      interfaceNameUnion.add(
          CDDiffUtil.escape2Alloy(astcdInterface.getSymbol().getInternalQualifiedName()));
    }
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
      enumNameUnion.add(CDDiffUtil.escape2Alloy(astcdEnum.getSymbol().getInternalQualifiedName()));
    }

    // Union of all Class or Interface Type Names
    Set<String> ciTypes = new HashSet<>();
    ciTypes.addAll(classNameUnion);
    ciTypes.addAll(interfaceNameUnion);

    // Union of all primitive or unknown types
    Set<String> puTypes = new HashSet<>();
    for (ASTCDAttribute astcdAttribute : attributeUnion) {
      // TODO: Im Tech.-Report sind enums drin, im Beispiel nicht, klären!
      String typeName = CDDiffUtil.escape2Alloy(astcdAttribute.getMCType().printType());
      if (!ciTypes.contains(typeName) && !enumNameUnion.contains(typeName)) {
        puTypes.add(typeName);
      }
    }

    // Generate rule output
    commonSigs.append("// U3: Concrete primitive or unknown types ").append(System.lineSeparator());
    if (puTypes.isEmpty()) {
      commonSigs.append("fact {no Val}").append(System.lineSeparator());
    }
    for (String type : puTypes) {
      commonSigs
          .append("one sig type_")
          .append(type)
          .append(" extends Val {}")
          .append(System.lineSeparator());
    }

    return commonSigs.toString();
  }

  public String executeRuleU4(Set<ASTCDCompilationUnit> asts) {
    StringBuilder commonSigs = new StringBuilder();

    // Union of all Enums
    Set<ASTCDEnum> enumUnion = new HashSet<>();
    for (ASTCDCompilationUnit astcdCompilationUnit : asts) {
      Set<ASTCDEnum> enumSet =
          new HashSet<>(astcdCompilationUnit.getCDDefinition().getCDEnumsList());
      enumUnion.addAll(enumSet);
    }

    Set<String> enumTypeNameUnion = new HashSet<>();
    for (ASTCDEnum e : enumUnion) {
      List<ASTCDEnumConstant> v = e.getCDEnumConstantList();
      for (ASTCDEnumConstant astcdEnumConstant : v) {
        enumTypeNameUnion.add(
            CDDiffUtil.escape2Alloy(
                e.getSymbol().getInternalQualifiedName() + "." + astcdEnumConstant.getName()));
      }
    }

    // Generate rule output
    commonSigs.append("// U4: Concrete enum values ").append(System.lineSeparator());
    if (enumTypeNameUnion.isEmpty()) {
      commonSigs.append("fact {no EnumVal}").append(System.lineSeparator());
    }
    for (String enumTypeName : enumTypeNameUnion) {
      commonSigs.append("one sig enum_");
      commonSigs.append(enumTypeName);
      commonSigs.append(" extends EnumVal {}").append(System.lineSeparator());
    }

    return commonSigs.toString();
  }

  public String executeRuleU5(Set<ASTCDCompilationUnit> asts, boolean newSemantics) {

    if (!newSemantics) {
      return ("one sig Type_Dummy4SimpleSemantics extends Type {}") + System.lineSeparator();
    }

    StringBuilder commonSigs = new StringBuilder();

    // Union of all classes and interfaces
    Set<ASTCDType> typeUnion = new HashSet<>();
    for (ASTCDCompilationUnit astcdCompilationUnit : asts) {
      typeUnion.addAll(new HashSet<>(astcdCompilationUnit.getCDDefinition().getCDClassesList()));
      typeUnion.addAll(new HashSet<>(astcdCompilationUnit.getCDDefinition().getCDInterfacesList()));
    }

    // Union of all class Names
    Set<String> classNameUnion = new HashSet<>();
    for (ASTCDType astcdType : typeUnion) {
      classNameUnion.add(CDDiffUtil.escape2Alloy(astcdType.getSymbol().getInternalQualifiedName()));
    }

    // Output generation
    commonSigs.append("// U5: Common types ").append(System.lineSeparator());
    for (String className : classNameUnion) {
      commonSigs.append("lone sig Type_");
      commonSigs.append(className);
      commonSigs.append(" extends Type {}").append(System.lineSeparator());
      commonSigs.append("fact{some Type_");
      commonSigs.append(className);
      commonSigs.append(" => some Type_");
      commonSigs.append(className);
      commonSigs.append(".inst}").append(System.lineSeparator());
    }

    // Union of all type names
    Set<String> typeNameUnion = new HashSet<>();
    for (ASTCDType astcdType : typeUnion) {
      typeNameUnion.add(astcdType.getSymbol().getInternalQualifiedName());
    }
    commonSigs.append("fact{");

    for (String typeName : typeNameUnion) {
      commonSigs
          .append("all c: ")
          .append(CDDiffUtil.escape2Alloy(typeName))
          .append(" | c.type=Type_")
          .append(CDDiffUtil.escape2Alloy(typeName))
          .append(System.lineSeparator())
          .append("Type_")
          .append(CDDiffUtil.escape2Alloy(typeName))
          .append(" in Type_")
          .append(CDDiffUtil.escape2Alloy(typeName))
          .append(".super")
          .append(System.lineSeparator());
    }

    commonSigs.append("}");

    return commonSigs.toString();
  }

  /** A helper function to generate all signatures common to all CDs */
  private String createCommonSignatures(Set<ASTCDCompilationUnit> asts) {

    return "// ***** Structures common to both CDs ***** "
        + System.lineSeparator()
        + System.lineSeparator()

        // ***** Collect all elements by computing their union *****

        // ***** Translation Rules *****
        // U1: Common Classes are Objects
        // TODO: interfaces werden im Report nicht behandelt bzw. explizit
        // ignoriert, tauchen im generierten
        // Code aber auf
        + executeRuleU1(asts)
        + ""
        + System.lineSeparator()

        // U2: Common names
        + executeRuleU2(asts)
        + ""
        + System.lineSeparator()

        // U3: Concrete primitive or unknown types
        + executeRuleU3(asts)
        + ""
        + System.lineSeparator()

        // U4: Concrete enum values
        + executeRuleU4(asts)
        + ""
        + System.lineSeparator();
  }

  /**
   * Executes the F1 rule, which generates functions returning all atoms of all subclasses of all
   * classes in class diagram cd.
   */
  public String executeRuleF1(ASTCDCompilationUnit cd) {
    StringBuilder classFunctions = new StringBuilder();

    // The set of all classes in the class diagram
    Set<ASTCDClass> classes = new HashSet<>(cd.getCDDefinition().getCDClassesList());

    classFunctions
        .append("// F1: Function returning all atoms of all subclasses of the class. ")
        .append(System.lineSeparator());
    for (ASTCDClass astcdClass : classes) {
      Set<ASTCDClass> subs = new HashSet<>();

      // Computation of Superclasses
      for (ASTCDClass sub : classes) {
        Set<ASTCDClass> superclasses = CDDiffUtil.getAllSuperclasses(sub, classes);

        if (superclasses.contains(astcdClass)) {
          subs.add(sub);
        }
      }

      // Output F1
      // Functions + Names
      classFunctions
          .append("fun ")
          .append(CDDiffUtil.escape2Alloy(astcdClass.getSymbol().getInternalQualifiedName()))
          .append("SubsCD")
          .append(cd.getCDDefinition().getName())
          .append(": set Obj { ");

      // All subclasses connected with a '+'
      for (ASTCDClass sub : subs) {
        classFunctions.append(CDDiffUtil.escape2Alloy(sub.getSymbol().getInternalQualifiedName()));
        classFunctions.append(" + ");
      }
      // Remove last '+'
      classFunctions.delete(classFunctions.length() - 2, classFunctions.length());
      classFunctions.append("}").append(System.lineSeparator());
    }

    return classFunctions.toString();
  }

  /**
   * Executes the F2 rule, which generates functions returning all atoms of all interfaces of all
   * classes implementing the interfaces in class diagram cd.
   */
  public String executeRuleF2(ASTCDCompilationUnit cd) {
    StringBuilder classFunctions = new StringBuilder();

    // The set of all classes in the class diagram
    Set<ASTCDClass> classes = new HashSet<>(cd.getCDDefinition().getCDClassesList());

    classFunctions
        .append("// F2: Functions returning all instances of classes implementing the interface. ")
        .append(System.lineSeparator());
    Set<ASTCDInterface> interfaces = new HashSet<>(cd.getCDDefinition().getCDInterfacesList());
    for (ASTCDInterface astcdInterface : interfaces) {
      classFunctions
          .append("fun ")
          .append(CDDiffUtil.escape2Alloy(astcdInterface.getSymbol().getInternalQualifiedName()))
          .append("SubsCD")
          .append(cd.getCDDefinition().getName())
          .append(": set Obj { ");

      // Compute the set of all classes implementing the interface.
      Set<ASTCDClass> impls = new HashSet<>();
      for (ASTCDClass astcdClass : classes) {
        Set<ASTCDClass> superclasses = CDDiffUtil.getAllSuperclasses(astcdClass, classes);

        for (ASTCDClass superClass : superclasses) {
          Set<ASTCDInterface> implInterfaces = CDDiffUtil.getAllInterfaces(superClass, interfaces);

          if (implInterfaces.contains(astcdInterface)) {
            impls.add(astcdClass);
          }
        }
      }
      // Output
      if (impls.size() > 0) {
        for (ASTCDClass impl : impls) {
          classFunctions.append(
              CDDiffUtil.escape2Alloy(impl.getSymbol().getInternalQualifiedName()));
          classFunctions.append(" + ");
        }
        // Remove last '+'
        classFunctions.delete(classFunctions.length() - 2, classFunctions.length());
      } else {
        classFunctions.append("none");
      }
      classFunctions.append("}").append(System.lineSeparator());
    }

    return classFunctions.toString();
  }

  /**
   * Executes the F3 rule, which creates a function for each enumeration type in the CD cd that
   * returns the enumeration’s possible values.
   *
   * @return String for Alloy Module
   */
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
          .append(CDDiffUtil.escape2Alloy(e.getSymbol().getInternalQualifiedName()))
          .append("EnumCD")
          .append(cdDefinition.getName())
          .append(": set EnumVal { ");
      List<ASTCDEnumConstant> enumVals = e.getCDEnumConstantList();

      if (enumVals.isEmpty()) {
        classFunctions.append("none }").append(System.lineSeparator());
      } else {

        for (ASTCDEnumConstant enumVal : enumVals) {
          classFunctions
              .append("enum_")
              .append(
                  CDDiffUtil.escape2Alloy(
                      e.getSymbol().getInternalQualifiedName() + "." + enumVal.getName()));
          classFunctions.append(" + ");
        }
        // Remove last '+'
        classFunctions.delete(classFunctions.length() - 2, classFunctions.length());
        classFunctions.append("}").append(System.lineSeparator());
      }
    }

    return classFunctions.toString();
  }

  /**
   * TODO: Muss später vielleicht nochmal überarbeitet werden. Executes the rule F4, which creates a
   * function for every part of a composite whole-part-relation that returns all linked whole/part
   * instance pairs in the Class Diagram cd.
   */
  public String executeRuleF4(ASTCDCompilationUnit cd) {
    StringBuilder classFunctions = new StringBuilder();

    // The CD Definition
    ASTCDDefinition cdDefinition = cd.getCDDefinition();

    // All associations
    List<ASTCDAssociation> associations = cdDefinition.getCDAssociationsList();

    // The Parts Set
    Set<String> parts = new HashSet<>();
    for (ASTCDAssociation a : associations) {
      if (a.getCDAssocType().isComposition()) {
        parts.add(a.getRightQualifiedName().getQName());
      }
    }

    classFunctions
        .append("// F4: Creates a function for every part of a composite whole-part-relation")
        .append(System.lineSeparator())
        .append("// that returns all linked whole/part instance pairs.")
        .append(System.lineSeparator());
    for (String part : parts) {
      // Compute Comps
      Set<ASTCDAssociation> comps = new HashSet<>();
      for (ASTCDAssociation comp : associations) {
        if (comp.getCDAssocType().isComposition()) {
          if (comp.getRightQualifiedName().getQName().equals(part)) {
            comps.add(comp);
          }
        }
      }

      // Generate Alloy function
      classFunctions
          .append("fun ")
          .append(CDDiffUtil.escape2Alloy(part))
          .append("CompFieldsCD")
          .append(cdDefinition.getName())
          .append(": Obj->Obj {")
          .append(System.lineSeparator());
      for (ASTCDAssociation c : comps) {
        classFunctions
            .append("  rel[")
            .append(CDDiffUtil.escape2Alloy(c.getLeftQualifiedName().getQName()))
            .append("SubsCD")
            .append(cdDefinition.getName());
        classFunctions.append(",").append(CDDiffUtil.inferRole(c.getRight())).append("] + ");
      }
      // Remove last '+'
      classFunctions.delete(classFunctions.length() - 2, classFunctions.length());

      classFunctions.append(System.lineSeparator()).append("}").append(System.lineSeparator());
    }
    return classFunctions.toString();
  }

  /** Generates the Functions for Subclassing, Interfaces, Compositions, and Enums */
  private String createFunctions(ASTCDCompilationUnit cd) {

    // Comment for each alloy module

    return "// ***** Functions specific to CD "
        + cd.getCDDefinition().getName()
        + " ***** "
        + System.lineSeparator()
        + System.lineSeparator()

        // F1: Functions returning all atoms of all subclasses of the class.
        + executeRuleF1(cd)
        + System.lineSeparator()

        // F2: Functions returning all instances of classes implementing the
        // interface.
        + executeRuleF2(cd)
        + System.lineSeparator()

        // F3: Functions returning all possible enum values for all enums in the CD.
        + executeRuleF3(cd)
        + System.lineSeparator()

        // F4: Creates a function for every part of a composite whole-part-relation
        // that returns all linked whole/part instance pairs.
        + executeRuleF4(cd)
        + System.lineSeparator();
  }

  /**
   * Translation rule to translate type names from a CD to corresponding Alloy functions or
   * signatures.
   */
  protected String executeRuleH1(String type, ASTCDCompilationUnit cd) {
    StringBuilder result = new StringBuilder();

    // CD Definition
    ASTCDDefinition cdDefinition = cd.getCDDefinition();

    // Is type an enum?
    for (ASTCDEnum e : cdDefinition.getCDEnumsList()) {
      if (type.equals(CDDiffUtil.escape2Alloy(e.getSymbol().getInternalQualifiedName()))) {
        result.append(type).append("EnumCD").append(cdDefinition.getName());
        return result.toString();
      }
    }

    // Is type a class?
    for (ASTCDClass c : cdDefinition.getCDClassesList()) {
      if (type.equals(CDDiffUtil.escape2Alloy(c.getSymbol().getInternalQualifiedName()))) {
        result.append(type).append("SubsCD").append(cdDefinition.getName());
        return result.toString();
      }
    }

    // Is type an interface?
    for (ASTCDInterface i : cdDefinition.getCDInterfacesList()) {
      if (type.equals(CDDiffUtil.escape2Alloy(i.getSymbol().getInternalQualifiedName()))) {
        result.append(type).append("SubsCD").append(cdDefinition.getName());
        return result.toString();
      }
    }

    // Type is none of the above
    result.append("type_").append(type);
    return result.toString();
  }

  /** additional rule for new semantics */
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
      Set<ASTCDType> superList = new HashSet<>(CDDiffUtil.getAllSuperclasses(astcdClass, classes));
      for (ASTCDClass superclass : CDDiffUtil.getAllSuperclasses(astcdClass, classes)) {
        superList.addAll(CDDiffUtil.getAllInterfaces(superclass, allInterfaces));
      }

      // Output P0
      // Functions + Names
      classFunctions
          .append("ObjTypes[")
          .append(CDDiffUtil.escape2Alloy(astcdClass.getSymbol().getInternalQualifiedName()))
          .append(",(");

      // All subclasses connected with a '+'
      for (ASTCDType superType : superList) {
        classFunctions
            .append("Type_")
            .append(CDDiffUtil.escape2Alloy(superType.getSymbol().getInternalQualifiedName()))
            .append(" + ");
      }
      // Remove last '+'
      classFunctions.delete(classFunctions.length() - 3, classFunctions.length());
      classFunctions.append(")]").append(System.lineSeparator());

      String name = CDDiffUtil.escape2Alloy(astcdClass.getSymbol().getInternalQualifiedName());
      classFunctions
          .append("{ some ")
          .append(name)
          .append(" => some Type_")
          .append(name)
          .append("}")
          .append(System.lineSeparator());
      for (ASTCDType superType : superList) {
        String superName =
            CDDiffUtil.escape2Alloy(superType.getSymbol().getInternalQualifiedName());
        classFunctions
            .append("{ some ")
            .append(name)
            .append(" => some Type_")
            .append(superName)
            .append("}")
            .append(System.lineSeparator());
      }
      classFunctions.append(System.lineSeparator());
    }

    return classFunctions.toString();
  }

  /**
   * Rule P1 uses predicate ObjAttrib to declare the attributes of every class in the class diagram
   * cd.
   */
  public String executeRuleP1(ASTCDCompilationUnit cd) {
    StringBuilder predicate = new StringBuilder();

    // Definition of the cd
    ASTCDDefinition cdDefinition = cd.getCDDefinition();

    // All classes of the cd
    Set<ASTCDClass> cdClasses = new HashSet<>(cdDefinition.getCDClassesList());
    Set<ASTCDInterface> cdInterfaces = new HashSet<>(cdDefinition.getCDInterfacesList());

    // Comment
    predicate.append("// P1: Attribute declaration").append(System.lineSeparator());
    for (ASTCDClass astcdClass : cdClasses) {
      // Compute the attribute union of all superclasses
      Set<ASTCDAttribute> attributeUnion = new HashSet<>();
      for (ASTCDClass attributeClass : CDDiffUtil.getAllSuperclasses(astcdClass, cdClasses)) {
        attributeUnion.addAll(attributeClass.getCDAttributeList());
      }
      for (ASTCDInterface attributeInterface :
          CDDiffUtil.getAllInterfaces(astcdClass, cdInterfaces)) {
        attributeUnion.addAll(attributeInterface.getCDAttributeList());
      }

      // Generate Alloy predicate
      for (ASTCDAttribute astcdAttribute : attributeUnion) {
        predicate
            .append("ObjAttrib[")
            .append(CDDiffUtil.escape2Alloy(astcdClass.getSymbol().getInternalQualifiedName()))
            .append(", ");
        predicate.append(astcdAttribute.getName()).append(", ");
        predicate
            .append(
                executeRuleH1(CDDiffUtil.escape2Alloy(astcdAttribute.getMCType().printType()), cd))
            .append("]")
            .append(System.lineSeparator());
      }
    }
    return predicate.toString();
  }

  /**
   * Application of Rule P2, which restricts the tuples of the get relation to the attributes of the
   * class and to the role names of its partners in associations.
   */
  public String executeRuleP2(ASTCDCompilationUnit cd) {
    StringBuilder predicate = new StringBuilder();

    // Definition of the cd
    ASTCDDefinition cdDefinition = cd.getCDDefinition();

    // All classes of the cd
    Set<ASTCDClass> cdClasses = new HashSet<>(cdDefinition.getCDClassesList());

    // All interfaces of the cd
    Set<ASTCDInterface> cdInterfaces = new HashSet<>(cdDefinition.getCDInterfacesList());

    // Comment
    predicate
        .append("// P2: Restricts the tuples of the get relation to the attributes of the class ")
        .append(System.lineSeparator())
        .append("// and to the role names of its partners in associations. ")
        .append(System.lineSeparator());

    for (ASTCDClass astcdClass : cdClasses) {
      // All names of superclasses and their interfaces
      Set<String> superNames = new HashSet<>();
      for (ASTCDClass superclass : CDDiffUtil.getAllSuperclasses(astcdClass, cdClasses)) {
        // Add all names of super classes
        superNames.add(CDDiffUtil.escape2Alloy(superclass.getSymbol().getInternalQualifiedName()));

        // Add all names of implemented interfaces by this class
        for (ASTCDInterface i : CDDiffUtil.getAllInterfaces(superclass, cdInterfaces)) {
          superNames.add(CDDiffUtil.escape2Alloy(i.getSymbol().getInternalQualifiedName()));
        }
      }

      // Computation of fields
      Set<String> fields = new HashSet<>();

      // All attributes of the superclasses
      for (ASTCDClass superclass : CDDiffUtil.getAllSuperclasses(astcdClass, cdClasses)) {
        for (ASTCDAttribute a : superclass.getCDAttributeList()) {
          fields.add(a.getName());
        }
      }

      // All attributes of the superclasses
      for (ASTCDInterface astcdInterface : CDDiffUtil.getAllInterfaces(astcdClass, cdInterfaces)) {
        for (ASTCDAttribute a : astcdInterface.getCDAttributeList()) {
          fields.add(a.getName());
        }
      }

      // All left roles
      for (ASTCDAssociation a : cdDefinition.getCDAssociationsList()) {
        // Skip associations with wrong direction
        if (a.getCDAssocDir().isDefinitiveNavigableRight()
            && !a.getCDAssocDir().isBidirectional()) {
          continue;
        }
        String lrName = CDDiffUtil.escape2Alloy(a.getRightQualifiedName().getQName());
        if (superNames.contains(lrName)) {
          fields.add(CDDiffUtil.inferRole(a.getLeft()));
        }
      }

      // All right roles
      for (ASTCDAssociation a : cdDefinition.getCDAssociationsList()) {
        // Skip associations with wrong direction
        if (a.getCDAssocDir().isDefinitiveNavigableLeft() && !a.getCDAssocDir().isBidirectional()) {
          continue;
        }
        String rrName = CDDiffUtil.escape2Alloy(a.getLeftQualifiedName().getQName());
        if (superNames.contains(rrName)) {
          fields.add(CDDiffUtil.inferRole(a.getRight()));
        }
      }

      // Output
      predicate
          .append("ObjFNames[")
          .append(CDDiffUtil.escape2Alloy(astcdClass.getSymbol().getInternalQualifiedName()))
          .append(", ");
      if (fields.size() > 0) {
        for (String field : fields) {
          predicate.append(field);
          predicate.append(" + ");
        }
        // Remove last '+'
        predicate.delete(predicate.length() - 2, predicate.length());

        // TODO: Nur im Beispiel und ich habe keine Ahnung, warum!
        predicate.append(" + none");
      } else {
        predicate.append("none ");
      }
      predicate.append("]").append(System.lineSeparator());
    }

    return predicate.toString();
  }

  /**
   * Application of Rule P3, which ensures that signatures representing abstract classes have no
   * atoms and that signatures representing singleton classes contain exactly one atom.
   */
  public String executeRuleP3(ASTCDCompilationUnit cd) {
    StringBuilder predicate = new StringBuilder();

    // CD Definition
    ASTCDDefinition cdDefinition = cd.getCDDefinition();

    // Comment
    predicate
        .append("// P3: Atoms for interfaces, singleton, and abstract classes")
        .append(System.lineSeparator());

    // Abstract classes should not have objects
    // The set of abstract classes:
    Set<ASTCDClass> abstractClasses = new HashSet<>();
    // The set of singleton classes
    Set<ASTCDClass> singletonClasses = new HashSet<>();
    for (ASTCDClass astcdClass : cdDefinition.getCDClassesList()) {
      if (astcdClass.getModifier().isAbstract()) {
        abstractClasses.add(astcdClass);
      }
      // TODO: Überprüfen und testen -> Gibt es nicht, was also tun?
      if (astcdClass.getModifier().isPresentStereotype()
          && astcdClass.getModifier().getStereotype().contains("singleton")) {
        singletonClasses.add(astcdClass);
      }
    }
    // Alloy predicate for abstract classes
    for (ASTCDClass astcdClass : abstractClasses) {
      predicate
          .append("no ")
          .append(CDDiffUtil.escape2Alloy(astcdClass.getSymbol().getInternalQualifiedName()))
          .append(System.lineSeparator());
    }

    // Alloy predicate for interfaces
    for (ASTCDInterface astcdInterface : cd.getCDDefinition().getCDInterfacesList()) {
      predicate
          .append("no ")
          .append(CDDiffUtil.escape2Alloy(astcdInterface.getSymbol().getInternalQualifiedName()))
          .append(System.lineSeparator());
    }

    // Alloy predicates for singleton classes
    // todo: needs to be fixed
    for (ASTCDClass astcdClass : singletonClasses) {
      predicate
          .append("one ")
          .append(CDDiffUtil.escape2Alloy(astcdClass.getSymbol().getInternalQualifiedName()))
          .append("SubsCD")
          .append(cdDefinition.getName())
          .append(System.lineSeparator());
    }

    return predicate.toString();
  }

  /**
   * Application of Rule P4, which restricts all objects in object models of the CD to be instances
   * of the classes of the CD.
   */
  public String executeRuleP4(ASTCDCompilationUnit cd) {
    StringBuilder predicate = new StringBuilder();

    // CD Definition
    ASTCDDefinition cdDefinition = cd.getCDDefinition();

    // All classes of the
    List<ASTCDClass> cdClasses = cdDefinition.getCDClassesList();

    // Comment
    predicate
        .append("// P4: Restrict Objects to instances of classes in the CD. ")
        .append(System.lineSeparator());

    predicate.append("Obj = (");
    if (cdClasses.size() > 0) {
      for (ASTCDClass astcdClass : cdClasses) {
        predicate.append(
            CDDiffUtil.escape2Alloy(astcdClass.getSymbol().getInternalQualifiedName()));
        predicate.append(" + ");
      }
      // Remove last '+'
      predicate.delete(predicate.length() - 2, predicate.length());
    } else {
      predicate.append("none").append(System.lineSeparator());
    }
    predicate.append(")").append(System.lineSeparator());

    return predicate.toString();
  }

  /** Rule A1 constraints the sets of links of bidirectional associations. */
  public String executeRuleA1(ASTCDCompilationUnit cd) {
    StringBuilder predicate = new StringBuilder();

    // CD Definition
    ASTCDDefinition cdDefinition = cd.getCDDefinition();

    // Associations in cd
    List<ASTCDAssociation> cdAssociations = cdDefinition.getCDAssociationsList();

    // Comment
    predicate
        .append("// A1: Constraints the sets of links of bidirectional associations.")
        .append(System.lineSeparator());
    for (ASTCDAssociation a : cdAssociations) {
      if (a.getCDAssocDir().isBidirectional()
          || (!a.getCDAssocDir().isDefinitiveNavigableLeft()
              && !a.getCDAssocDir().isDefinitiveNavigableRight())) {
        List<String> rightReferenceNames = a.getRightReferenceName();

        // Generation
        predicate.append("BidiAssoc[");

        predicate.append(
            executeRuleH1(CDDiffUtil.escape2Alloy(a.getLeftQualifiedName().getQName()), cd));
        predicate.append(",");

        predicate.append(CDDiffUtil.escape2Alloy(CDDiffUtil.inferRole(a.getRight())));
        predicate.append(",");

        predicate.append(
            executeRuleH1(CDDiffUtil.escape2Alloy(a.getRightQualifiedName().getQName()), cd));
        predicate.append(",");

        predicate.append(CDDiffUtil.escape2Alloy(CDDiffUtil.inferRole(a.getLeft())));
        predicate.append("]").append(System.lineSeparator());
      }
    }

    return predicate.toString();
  }

  /** Executes Rule A2, which ensures that parts of compositions have at most one whole. */
  public String executeRuleA2(ASTCDCompilationUnit cd) {
    StringBuilder predicate = new StringBuilder();

    // CD Definition
    ASTCDDefinition cdDefinition = cd.getCDDefinition();

    // Comment
    predicate
        .append("// A2: ensures that parts of compositions have at most one whole.")
        .append(System.lineSeparator());

    for (ASTCDAssociation a : cdDefinition.getCDAssociationsList()) {
      for (String part : a.getRightReferenceName()) {
        if (a.getCDAssocType().isComposition()) {
          // Generation
          predicate.append("Composition[");
          predicate.append(CDDiffUtil.escape2Alloy(a.getRightQualifiedName().getQName()));
          predicate.append("CompFieldsCD");
          predicate.append(cdDefinition.getName());
          predicate.append(",");
          predicate.append(CDDiffUtil.escape2Alloy(a.getRightQualifiedName().getQName()));
          predicate.append("SubsCD");
          predicate.append(cdDefinition.getName());
          predicate.append("]").append(System.lineSeparator());
        }
      }
    }
    return predicate.toString();
  }

  /**
   * Applies rule A3, which ensures the cardinality constraints stated on the left sides of
   * bidirectional associations, undirected association, and associations that are navigable from
   * right to left are respected.
   */
  public String executeRuleA3(ASTCDCompilationUnit cd) {
    StringBuilder predicate = new StringBuilder();

    // CD Definition
    ASTCDDefinition cdDefinition = cd.getCDDefinition();

    // Comment
    predicate
        .append("// A3: ensures the cardinality constraints stated on the left sides of bidirec-")
        .append(System.lineSeparator())
        .append(
            "// tional associations, undirected association, and associations that are navigable "
                + "from right")
        .append(System.lineSeparator())
        .append("// to left are respected.")
        .append(System.lineSeparator());

    for (ASTCDAssociation asc : cdDefinition.getCDAssociationsList()) {
      if ((asc.getCDAssocDir().isBidirectional()
          || asc.getCDAssocDir().isDefinitiveNavigableLeft()
          || (!asc.getCDAssocDir().isDefinitiveNavigableLeft()
              && !asc.getCDAssocDir().isDefinitiveNavigableRight()))) {
        predicate.append(appendRemainingRuleA(3, cd, asc));
      }
    }

    return predicate.toString();
  }

  /**
   * Executes Rule 4, which ensures the cardinality constraints stated on the right sides of
   * associations, which are navigable from right to left, are respected.
   */
  public String executeRuleA4(ASTCDCompilationUnit cd) {
    StringBuilder predicate = new StringBuilder();

    // CD Definition
    ASTCDDefinition cdDefinition = cd.getCDDefinition();

    // Comment
    predicate
        .append("// A4: ensures the cardinality constraints stated on the right sides of")
        .append(System.lineSeparator())
        .append("// associations, which are navigable from right to left, are respected.")
        .append(System.lineSeparator());

    // Computation
    for (ASTCDAssociation asc : cdDefinition.getCDAssociationsList()) {
      if ((asc.getCDAssocDir().isDefinitiveNavigableLeft()
          && !asc.getCDAssocDir().isBidirectional())) {
        predicate.append(appendRemainingRuleA(4, cd, asc));
      }
    }
    return predicate.toString();
  }

  /**
   * Executes Rule 5, which ensures the cardinality constraints stated on the right sides of
   * bidirectional associations, undirected association, and associations that are navigable from
   * right to left are respected.
   */
  public String executeRuleA5(ASTCDCompilationUnit cd) {
    StringBuilder predicate = new StringBuilder();

    // CD Definition
    ASTCDDefinition cdDefinition = cd.getCDDefinition();

    // Comment
    predicate
        .append("// A5: ensures the cardinality constraints stated on the right sides of")
        .append(System.lineSeparator())
        .append(
            "// bidirectional associations, undirected association, and associations that are "
                + "navigable from")
        .append(System.lineSeparator())
        .append("// right to left are respected.")
        .append(System.lineSeparator());

    // Computation
    for (ASTCDAssociation asc : cdDefinition.getCDAssociationsList()) {
      if ((asc.getCDAssocDir().isBidirectional()
          || asc.getCDAssocDir().isDefinitiveNavigableRight()
          || (!asc.getCDAssocDir().isDefinitiveNavigableLeft()
              && !asc.getCDAssocDir().isDefinitiveNavigableRight()))) {
        predicate.append(appendRemainingRuleA(5, cd, asc));
      }
    }
    return predicate.toString();
  }

  /**
   * Executes Rule 6, which ensures the cardinality constraints stated on the left sides of
   * associations that are navigable from left to right are respected.
   */
  public String executeRuleA6(ASTCDCompilationUnit cd) {
    StringBuilder predicate = new StringBuilder();

    // CD Definition
    ASTCDDefinition cdDefinition = cd.getCDDefinition();

    // Comment
    predicate
        .append("// A6: ensures the cardinality constraints stated on the left sides of associ-")
        .append(System.lineSeparator())
        .append("// ations that are navigable from left to right are respected.")
        .append(System.lineSeparator());

    // Computation
    for (ASTCDAssociation asc : cdDefinition.getCDAssociationsList()) {
      if ((asc.getCDAssocDir().isDefinitiveNavigableRight()
          && !asc.getCDAssocDir().isBidirectional())) {
        predicate.append(appendRemainingRuleA(6, cd, asc));
      }
    }
    return predicate.toString();
  }

  /** private helper method that completes predicate for rules A4 - A6 */
  private StringBuilder appendRemainingRuleA(
      int ruleID, ASTCDCompilationUnit cd, ASTCDAssociation association) {

    StringBuilder predicate = new StringBuilder();

    String infix = "";
    String firstReferenceName;
    String secondReferenceName;
    String roleName;

    String lowerCardinality;
    String upperCardinality;

    int leftLowerBound = 0;
    int leftUpperBound = -1;
    int rightLowerBound = 0;
    int rightUpperBound = -1;

    if (association.getLeft().isPresentCDCardinality()) {
      leftLowerBound = association.getLeft().getCDCardinality().getLowerBound();
      if (!association.getLeft().getCDCardinality().toCardinality().isNoUpperLimit()) {
        leftUpperBound = association.getLeft().getCDCardinality().getUpperBound();
      }
    }

    if (association.getRight().isPresentCDCardinality()) {
      rightLowerBound = association.getRight().getCDCardinality().getLowerBound();
      if (!association.getRight().getCDCardinality().toCardinality().isNoUpperLimit()) {
        rightUpperBound = association.getRight().getCDCardinality().getUpperBound();
      }
    }

    switch (ruleID) {
      case 3:
        {
          infix = "Attrib";

          lowerCardinality = "" + leftLowerBound;

          if (leftUpperBound < 0) {
            upperCardinality = "";
          } else {
            upperCardinality = "" + leftUpperBound;
          }

          firstReferenceName = association.getRightQualifiedName().getQName();
          secondReferenceName = association.getLeftQualifiedName().getQName();
          roleName = CDDiffUtil.inferRole(association.getLeft());
          break;
        }
      case 4:
        {
          lowerCardinality = "" + rightLowerBound;

          if (rightUpperBound < 0) {
            upperCardinality = "";
          } else {
            upperCardinality = "" + rightUpperBound;
          }

          firstReferenceName = association.getLeftQualifiedName().getQName();
          secondReferenceName = association.getRightQualifiedName().getQName();
          roleName = CDDiffUtil.inferRole(association.getLeft());
          break;
        }
      case 5:
        {
          infix = "Attrib";

          lowerCardinality = "" + rightLowerBound;

          if (rightUpperBound < 0) {
            upperCardinality = "";
          } else {
            upperCardinality = "" + rightUpperBound;
          }

          firstReferenceName = association.getLeftQualifiedName().getQName();
          secondReferenceName = association.getRightQualifiedName().getQName();
          roleName = CDDiffUtil.inferRole(association.getRight());
          break;
        }
      case 6:
        {
          lowerCardinality = "" + leftLowerBound;

          if (leftUpperBound < 0) {
            upperCardinality = "";
          } else {
            upperCardinality = "" + leftUpperBound;
          }

          firstReferenceName = association.getRightQualifiedName().getQName();
          secondReferenceName = association.getLeftQualifiedName().getQName();
          roleName = CDDiffUtil.inferRole(association.getRight());
          break;
        }
      default:
        {
          Log.error("0xCDD05: incorrect ruleID, method is designed for A4-A6 only");
          return predicate;
        }
    }

    if (upperCardinality.equals("")) {
      predicate.append("ObjL").append(infix).append("[");
    } else {
      predicate.append("ObjLU").append(infix).append("[");
    }

    predicate.append(executeRuleH1(CDDiffUtil.escape2Alloy(firstReferenceName), cd));
    predicate.append(", ");

    predicate.append(CDDiffUtil.escape2Alloy(roleName));
    predicate.append(", ");

    predicate.append(executeRuleH1(CDDiffUtil.escape2Alloy(secondReferenceName), cd));
    predicate.append(", ");

    // Write cardinalities
    predicate.append(lowerCardinality);
    if (!upperCardinality.equals("")) {
      predicate.append(", ");
      predicate.append(upperCardinality);
    }
    predicate.append("]").append(System.lineSeparator());
    return predicate;
  }

  /** Creates all predicates necessary for the description of the semantics */
  public String createPredicates(ASTCDCompilationUnit cd, boolean newSemantics) {
    StringBuilder predicate = new StringBuilder();

    // The definition of the CD
    ASTCDDefinition cdDefinition = cd.getCDDefinition();

    // Comment
    predicate
        .append("// Semantics predicate ")
        .append(cdDefinition.getName())
        .append(System.lineSeparator());

    // Begin predicate
    predicate
        .append("pred ")
        .append(cdDefinition.getName())
        .append(" {")
        .append(System.lineSeparator())
        .append(System.lineSeparator());

    if (newSemantics) {
      predicate.append(executeRuleP0(cd)).append(System.lineSeparator());
    } else {
      predicate
          .append(("ObjTypes[Obj,(Type_Dummy4SimpleSemantics)]"))
          .append(System.lineSeparator())
          .append(System.lineSeparator());
    }

    predicate
        .append("// Classes and attributes in ")
        .append(cdDefinition.getName())
        .append(System.lineSeparator());

    // P1: Declaration of Attributes
    predicate.append(executeRuleP1(cd)).append(System.lineSeparator());

    // P2: Add all attributes
    predicate.append(executeRuleP2(cd)).append(System.lineSeparator());

    // P3: Abstract classes have no objects singletons exactly one
    predicate.append(executeRuleP3(cd)).append(System.lineSeparator());

    // P4: Objects must be Objects of the class diagram
    predicate.append(executeRuleP4(cd)).append(System.lineSeparator());

    // Handle Associations
    // Comment
    predicate
        .append("// Associations in ")
        .append(cdDefinition.getName())
        .append(System.lineSeparator());

    // A1: Constraints the sets of links of bidirectional associations.
    predicate.append(executeRuleA1(cd)).append(System.lineSeparator());

    // A2: Ensures parts of compositions have at most one whole.
    predicate.append(executeRuleA2(cd)).append(System.lineSeparator());

    // A3: ensures the cardinality constraints stated on the left sides of
    // bidirectional associations, undirected association, and associations that
    // are navigable from right to left are respected.
    predicate.append(executeRuleA3(cd)).append(System.lineSeparator());

    // A4: ensures the cardinality constraints stated on the right sides of
    // associations, which are navigable from right to left, are respected.
    predicate.append(executeRuleA4(cd)).append(System.lineSeparator());

    // A5: ensures the cardinality constraints stated on the right sides of
    // bidirectional associations, undirected association, and associations that
    // are navigable from
    // right to left are respected.
    predicate.append(executeRuleA5(cd)).append(System.lineSeparator());

    // A6: ensures the cardinality constraints stated on the left sides of
    // associations that are navigable from left to right are respected.
    predicate.append(executeRuleA6(cd));

    predicate.append(System.lineSeparator()).append("}");

    return predicate.toString();
  }

  /**
   * Generates the Alloy Module in outputDirectory using the ast.
   *
   * @param asts the set of asts to generate the alloy module for.
   */
  public String generate(Set<ASTCDCompilationUnit> asts, boolean newSemantics) {
    // Only generate a module for non-empty asts
    if (asts.isEmpty()) {
      return "";
    }

    for (ASTCDCompilationUnit ast : asts) {
      // build symbol table
      // CD4CodeMill.globalScope().clear();
      new CD4CodeDirectCompositionTrafo().transform(ast);
      CDDiffUtil.refreshSymbolTable(ast);
    }

    // Check if two CDs have the same name and rename them, if this is the case
    renameASTs(asts);

    // Derive the name of the module
    String moduleName = generateModuleName(asts);

    // Initialize output variable
    StringBuilder module =
        new StringBuilder(
            "module " + moduleName + System.lineSeparator() + " " + System.lineSeparator());

    // Generate the Generic Part
    module.append(createGenericPart());

    // Signatures common to all CDs
    module.append(createCommonSignatures(asts));

    // Singleton Class Signatures for multi-instance semantics
    module.append(executeRuleU5(asts, newSemantics)).append(System.lineSeparator());

    // Functions specific to CD
    for (ASTCDCompilationUnit cd : asts) {
      module.append(createFunctions(cd));
    }

    // semantics predicate for each CD
    for (ASTCDCompilationUnit cd : asts) {
      module.append(createPredicates(cd, newSemantics));
    }

    return module.toString();
  }

  public void renameASTs(Collection<ASTCDCompilationUnit> asts) {
    int versNr = 0;
    boolean changed = false;
    Object[] astsArray = asts.toArray();
    for (int i = 0; i < astsArray.length; i++) {
      String currentName = ((ASTCDCompilationUnit) astsArray[i]).getCDDefinition().getName();
      for (int j = i + 1; j < astsArray.length; j++) {
        String nextName = ((ASTCDCompilationUnit) astsArray[j]).getCDDefinition().getName();

        // Check if a different module has the same name
        if (currentName.equals(nextName)) {
          // Rename modules and repeat test
          currentName = currentName + "_v" + versNr;
          ((ASTCDCompilationUnit) astsArray[i]).getCDDefinition().setName(currentName);
          versNr++;
          nextName = nextName + "_v" + versNr;
          ((ASTCDCompilationUnit) astsArray[j]).getCDDefinition().setName(nextName);

          // Reset j to repeat test
          j = i + 1;

          changed = true;
        }
      }
    }
    if (changed) {
      asts = new HashSet<>();
      for (Object o : astsArray) {
        ASTCDCompilationUnit ast = (ASTCDCompilationUnit) o;
        asts.add(ast);
      }
    }
  }

  /**
   * Writes a string containing an alloy module into a file
   *
   * @param module the string the Alloy module should contain
   * @param outputDirectory the directory to generate the Alloy Module in.
   */
  public void saveModule(String module, String moduleName, File outputDirectory) {
    // Set Output Path
    String outputPath = outputDirectory.toString() + "/" + moduleName.toLowerCase() + "/";
    Path outputFile = Paths.get(outputPath, moduleName + ".als");

    // Write results into a file
    try {
      FileUtils.writeStringToFile(outputFile.toFile(), module, Charset.defaultCharset());
    } catch (IOException e) {
      e.printStackTrace();

      Log.error("0xCDD06: Could not create Alloy Module file.");
    }

    Log.trace(
        LOGGER_NAME,
        "Generated Alloy Module " + moduleName + " in folder" + outputDirectory.getAbsolutePath());
  }

  /**
   * Writes a string containing an alloy module into a file and return path
   *
   * @param module the string the Alloy module should contain
   * @param outputDirectory the directory to generate the Alloy Module in.
   * @return Path to the file
   */
  public Path saveModulePath(String module, String moduleName, File outputDirectory) {
    // Set Output Path
    String outputPath = outputDirectory.toString() + "/" + moduleName.toLowerCase() + "/";
    Path outputFile = Paths.get(outputPath, moduleName + ".als");

    // Write results into a file
    saveModule(module, moduleName, outputDirectory);

    return outputFile;
  }

  /**
   * Generates the Alloy Module in outputDirectory using the ast.
   *
   * @param asts the set of asts to generate the alloy module for.
   * @param outputDirectory the directory to generate the Alloy Module in.
   * @param newSemantics specify whether to use simple or multi-instance semantics
   */
  public void generateModuleToFile(
      Set<ASTCDCompilationUnit> asts, File outputDirectory, boolean newSemantics) {
    // Generate the name of the module
    String moduleName = generateModuleName(asts);

    // Generate Standard module
    String module = generate(asts, newSemantics);

    // Save the module in the output Directory
    saveModule(module, moduleName, outputDirectory);
  }

  public void generateModuleToFile(Set<ASTCDCompilationUnit> asts, File outputDirectory) {
    generateModuleToFile(asts, outputDirectory, false);
  }

  /**
   * Generates the Alloy Module in outputDirectory using the ast.
   *
   * @param asts the set of asts to generate the alloy module for.
   */
  public String generateModule(Set<ASTCDCompilationUnit> asts, boolean newSemantics) {

    return generate(asts, newSemantics);
  }

  /**
   * Generates the Alloy Module in outputDirectory using the ast. TODO: Better documentation
   *
   * @param filePaths ?
   */
  public String generateModuleFromFiles(Set<String> filePaths) {
    Set<ASTCDCompilationUnit> cds = new HashSet<>();

    for (String modelFile : filePaths) {
      Path model = Paths.get(modelFile);
      CD4AnalysisParser parser = new CD4AnalysisParser();
      Optional<ASTCDCompilationUnit> optCD;
      try {
        optCD = parser.parse(model.toString());
        // assertFalse(parser.hasErrors());
        assert (optCD.isPresent());
        cds.add(optCD.get());
      } catch (Exception e) {
        e.printStackTrace();
        Log.error(
            "There was an exception when parsing the model " + modelFile + ": " + e.getMessage());
      }
    }

    return generate(cds, false);
  }

  public String generateModuleName(Set<ASTCDCompilationUnit> asts) {
    // Derive the name of the module
    // Is done here so that we can add different checks before the generation in
    // the future
    StringBuilder moduleName = new StringBuilder();
    for (ASTCDCompilationUnit astcdCompilationUnit : asts) {
      moduleName.append(astcdCompilationUnit.getCDDefinition().getName());
      moduleName.append("_");
    }
    moduleName.append("module");

    return moduleName.toString();
  }
}
