/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.cd2alloy.generator;

import de.monticore.cd4analysis._parser.CD4AnalysisParser;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._ast.ASTCDCardinality;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Entry point for the CD2Alloy generator.
 */
public class CD2AlloyGenerator {
  private final static String LOGGER_NAME = CD2AlloyGenerator.class.getName();

  private final static MCBasicTypesFullPrettyPrinter pp = new MCBasicTypesFullPrettyPrinter(new IndentPrinter());

  /**
   * Generates the generic part of an alloy module
   *
   * @return String containing all predicates needed to model CDs in Alloy
   */
  private static String createGenericPart() {

    // Additional facts to restrict the solution space to usable solutions
//     genericPart.append("// Restrict names to names that are used for Objects" + System.lineSeparator() +
//         "fact OnlyUsedNames{" + System.lineSeparator() +
//         "  all  fName:FName | some v:Val  | some o:Obj | v in o.get[fName] " + System.lineSeparator() +
//         "}" + System.lineSeparator() + System.lineSeparator());
//     
//     genericPart.append("// Restrict values to values actually used in get relation  " + System.lineSeparator() +
//         "fact OnlyUsedValues{" + System.lineSeparator() +
//         "  all  v:Val | some fName:FName | some o:Obj | v in o.get[fName] " + System.lineSeparator() +
//         "}" + System.lineSeparator() + System.lineSeparator());
//    
//     genericPart.append("// Restrict enum values to enum values actually used in get relation  " + System.lineSeparator() +
//         "fact OnlyUsedEnumValues{" + System.lineSeparator() +
//         "  all  v:EnumVal | some fName:FName | some o:Obj | v in o.get[fName] " + System.lineSeparator() +
//         "}" + System.lineSeparator() + System.lineSeparator());


    return "// ***** Generic Part ***** " + System.lineSeparator() + " " + System.lineSeparator()

        // Comment for abstract Signatures
        + "// The abstract signatures FName, Obj, Val, and EnumVal. " + System.lineSeparator()

        // Abstract Signature for Objects
        + "abstract sig Obj { get: FName -> {Obj + Val + EnumVal} } " + System.lineSeparator()
        // Abstract Signature for Names
        + "abstract sig FName {} " + System.lineSeparator()
        // Abstract Signature for Values
        + "abstract sig Val {} " + System.lineSeparator()
        // Abstract Signature for EnumValues
        + "abstract sig EnumVal {} " + System.lineSeparator() + " " + System.lineSeparator()

        // Comment for Parametrized predicates
        + "// Predicates used to specify cardinality constraints for navigable association"
        + System.lineSeparator()
        + "// ends and for association ends of undirected associations."
        + System.lineSeparator() + "pred ObjAttrib[objs: set Obj, fName: one FName,"
        + System.lineSeparator() + " fType: set {Obj + Val + EnumVal}] {"
        + System.lineSeparator() + " objs.get[fName] in fType" + System.lineSeparator()
        + " all o: objs| one o.get[fName] }" + System.lineSeparator() + System.lineSeparator()
        + "pred ObjFNames[objs: set Obj, fNames:set FName]{" + System.lineSeparator()
        + " no objs.get[FName - fNames] }" + System.lineSeparator() + System.lineSeparator()
        + "pred BidiAssoc[left: set Obj, lFName:one FName," + System.lineSeparator()
        + " right: set Obj, rFName:one FName] {" + System.lineSeparator()
        + " all l: left | all r: l.get[lFName] | l in r.get[rFName]" + System.lineSeparator()
        + " all r: right | all l: r.get[rFName] | r in l.get[lFName] }" + System.lineSeparator()
        + System.lineSeparator() + "pred Composition[compos: Obj->Obj, right: set Obj] {"
        + System.lineSeparator() + " all r: right | lone compos.r }" + System.lineSeparator()
        + " " + System.lineSeparator() + "fun rel[wholes: set Obj, fn: FName] : Obj->Obj {"
        + System.lineSeparator() + " {o1:Obj,o2:Obj|o1->fn->o2 in wholes <: get} } "
        + System.lineSeparator() + System.lineSeparator()
        + "// Predicates used to specify cardinality constraints for navigable association"
        + System.lineSeparator()
        + "// ends and for association ends of undirected associations. "
        + System.lineSeparator()
        + "pred ObjUAttrib[objs: set Obj, fName:one FName, fType:set Obj, up: Int] {"
        + System.lineSeparator() + " objs.get[fName] in fType" + System.lineSeparator()
        + " all o: objs| (#o.get[fName] =< up) } " + System.lineSeparator()
        + System.lineSeparator()
        + "pred ObjLAttrib[objs: set Obj, fName: one FName, fType: set Obj, low: Int] {"
        + System.lineSeparator() + " objs.get[fName] in fType" + System.lineSeparator()
        + " all o: objs | (#o.get[fName] >= low) }" + System.lineSeparator()
        + System.lineSeparator()
        + "pred ObjLUAttrib[objs:set Obj, fName:one FName, fType:set Obj,"
        + System.lineSeparator() + " low: Int, up: Int] {" + System.lineSeparator()
        + " ObjLAttrib[objs, fName, fType, low]" + System.lineSeparator()
        + " ObjUAttrib[objs, fName, fType, up] }" + System.lineSeparator()
        + System.lineSeparator()
        + "// Parametrized predicates used to specify cardinality constraints for non-"
        + System.lineSeparator() + "// navigable association ends. " + System.lineSeparator()
        + "pred ObjL[objs: set Obj, fName:one FName, fType: set Obj, low: Int] {"
        + System.lineSeparator() + " all r: objs | # { l: fType | r in l.get[fName]} >= low } "
        + System.lineSeparator() + System.lineSeparator()
        + "pred ObjU[objs: set Obj, fName:one FName, fType: set Obj, up: Int] {"
        + System.lineSeparator() + " all r: objs | # { l: fType | r in l.get[fName]} =< up } "
        + System.lineSeparator() + System.lineSeparator()
        + "pred ObjLU[objs: set Obj, fName:one FName, fType: set Obj," + System.lineSeparator()
        + " low: Int, up: Int] {" + System.lineSeparator() + " ObjL[objs, fName, fType, low]"
        + System.lineSeparator() + " ObjU[objs, fName, fType, up] }" + System.lineSeparator()
        + System.lineSeparator() + ""

        // Additional Fact from in TechRep Example to exclude illegal
        + "fact NonEmptyInstancesOnly {" + System.lineSeparator() + " some Obj"
        + System.lineSeparator() + "}" + System.lineSeparator() + System.lineSeparator();
  }

  /**
   * This helper functions processes parts of a name such that they can be used
   * in Alloy
   *
   * @return The processed name
   */
  private static String partHandler(List<String> parts) {
    StringBuilder completeName = new StringBuilder();

    // Combine all parts using "_" as separator instead of "."
    for (String part : parts) {
      completeName.append(part).append("_");
    }
    // Remove last "_"
    completeName = new StringBuilder(completeName.substring(0, completeName.length() - 1));

    // Process to lowercase only
    completeName = new StringBuilder(completeName.toString().toLowerCase());

    return completeName.toString();
  }

  public static String executeRuleU1(Set<ASTCDCompilationUnit> asts) {
    StringBuilder commonSigs = new StringBuilder();

    // Union of all classes
    Set<ASTCDClass> classUnion = new HashSet<>();
    for (ASTCDCompilationUnit astcdCompilationUnit : asts) {
      Set<ASTCDClass> classSet = new HashSet<>(
              astcdCompilationUnit.getCDDefinition().getCDClassesList());
      classUnion.addAll(classSet);
    }

    // Union of all class Names
    Set<String> classNameUnion = new HashSet<>();
    for (ASTCDClass astcdClass : classUnion) {
      classNameUnion.add(astcdClass.getName());
    }

    // Output generation
    commonSigs.append("// U1: Common classes ").append(System.lineSeparator());
    for (String className : classNameUnion) {
      commonSigs.append("sig ");
      commonSigs.append(className);
      commonSigs.append(" extends Obj {}").append(System.lineSeparator());
    }

    return commonSigs.toString();
  }

  public static String executeRuleU2(Set<ASTCDCompilationUnit> asts) {
    StringBuilder commonSigs = new StringBuilder();

    // Union of all classes
    Set<ASTCDClass> classUnion = new HashSet<>();
    for (ASTCDCompilationUnit astcdCompilationUnit : asts) {
      Set<ASTCDClass> classSet = new HashSet<>(
              astcdCompilationUnit.getCDDefinition().getCDClassesList());
      classUnion.addAll(classSet);
    }

    // Union of all attributes in all classes
    Set<ASTCDAttribute> attributeUnion = new HashSet<>();
    for (ASTCDClass astcdClass : classUnion) {
      Set<ASTCDAttribute> attributes = new HashSet<>(astcdClass.getCDAttributeList());
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
      Set<ASTCDAssociation> associationSet = new HashSet<>(
              astcdCompilationUnit.getCDDefinition().getCDAssociationsList());
      associationUnion.addAll(associationSet);
    }

    // Union of all Role Names
    Set<String> roleNameUnion = new HashSet<>();
    for (ASTCDAssociation association : associationUnion) {
      if (association.getLeft().isPresentCDRole()) {
        roleNameUnion.add(association.getLeft().getCDRole().getName());
      } else {
        // Preprocess parts of reference names and add them as role name
        String name = partHandler(association.getLeftReferenceName());
        roleNameUnion.add(name);
      }
      if (association.getRight().isPresentCDRole()) {
        roleNameUnion.add(association.getRight().getCDRole().getName());
      } else {
        // Preprocess parts of reference names and add them as role name
        String name = partHandler(association.getRightReferenceName());
        roleNameUnion.add(name);
      }
    }

    // Union of all attribute and role names
    Set<String> nameUnion = new HashSet<>();
    nameUnion.addAll(attributeNameUnion);
    nameUnion.addAll(roleNameUnion);

    // Generate output of U2 rule
    commonSigs.append("// U2: Common names ").append(System.lineSeparator());
    for (String n : nameUnion) {
      commonSigs.append("one sig ")
          .append(n)
          .append(" extends FName {}")
          .append(System.lineSeparator());
    }

    return commonSigs.toString();
  }

  public static String executeRuleU3(Set<ASTCDCompilationUnit> asts) {
    StringBuilder commonSigs = new StringBuilder();

    // Union of all classes
    Set<ASTCDClass> classUnion = new HashSet<>();
    for (ASTCDCompilationUnit astcdCompilationUnit : asts) {
      Set<ASTCDClass> classSet = new HashSet<>(
              astcdCompilationUnit.getCDDefinition().getCDClassesList());
      classUnion.addAll(classSet);
    }

    // Union of all class Names
    Set<String> classNameUnion = new HashSet<>();
    for (ASTCDClass astcdClass : classUnion) {
      classNameUnion.add(astcdClass.getName());
    }

    // Union of all attributes in all classes
    Set<ASTCDAttribute> attributeUnion = new HashSet<>();
    for (ASTCDClass astcdClass : classUnion) {
      Set<ASTCDAttribute> attributes = new HashSet<>(astcdClass.getCDAttributeList());
      attributeUnion.addAll(attributes);
    }

    // Union of all Attribute Names
    Set<String> attributeNameUnion = new HashSet<>();
    for (ASTCDAttribute astcdAttribute : attributeUnion) {
      attributeNameUnion.add(astcdAttribute.getName());
    }

    // Union of all Interfaces
    Set<ASTCDInterface> interfaceUnion = new HashSet<>();
    for (ASTCDCompilationUnit astcdCompilationUnit : asts) {
      Set<ASTCDInterface> interfaceSet = new HashSet<>(
              astcdCompilationUnit.getCDDefinition().getCDInterfacesList());
      interfaceUnion.addAll(interfaceSet);
    }
    // Union of all interface Names
    Set<String> interfaceNameUnion = new HashSet<>();
    for (ASTCDInterface astcdInterface : interfaceUnion) {
      interfaceNameUnion.add(astcdInterface.getName());
    }
    // Union of all Enums
    Set<ASTCDEnum> enumUnion = new HashSet<>();
    for (ASTCDCompilationUnit astcdCompilationUnit : asts) {
      Set<ASTCDEnum> enumSet = new HashSet<>(
              astcdCompilationUnit.getCDDefinition().getCDEnumsList());
      enumUnion.addAll(enumSet);
    }
    // Union of all Enum Names
    Set<String> enumNameUnion = new HashSet<>();
    for (ASTCDEnum astcdEnum : enumUnion) {
      enumNameUnion.add(astcdEnum.getName());
    }
    Set<String> enumTypeNameUnion = new HashSet<>();
    for (ASTCDEnum e : enumUnion) {
      List<ASTCDEnumConstant> v = e.getCDEnumConstantList();
      for (ASTCDEnumConstant astcdEnumConstant : v) {
        enumTypeNameUnion.add(e.getName() + "_" + astcdEnumConstant.getName());
      }
    }

    // Union of all Class or Interface Type Names
    Set<String> ciTypes = new HashSet<>();
    ciTypes.addAll(classNameUnion);
    ciTypes.addAll(interfaceNameUnion);

    // Union of all primitive or unknown types
    Set<String> puTypes = new HashSet<>();
    for (ASTCDAttribute astcdAttribute : attributeUnion) {
      // TODO: Im Tech.-Report sind enums drin, im Beispiel nicht, klären!
      String typeName = astcdAttribute.getMCType().printType(pp);
      if (!ciTypes.contains(typeName) && !enumNameUnion.contains(typeName)) {
        puTypes.add(typeName);
      }
    }

    // Generate rule output
    commonSigs.append("// U3: Concrete primitive or unknown types ").append(System.lineSeparator());
    for (String type : puTypes) {
      commonSigs.append("one sig type_")
          .append(type)
          .append(" extends Val {}")
          .append(System.lineSeparator());
    }

    return commonSigs.toString();
  }

  public static String executeRuleU4(Set<ASTCDCompilationUnit> asts) {
    StringBuilder commonSigs = new StringBuilder();

    // Union of all Enums
    Set<ASTCDEnum> enumUnion = new HashSet<>();
    for (ASTCDCompilationUnit astcdCompilationUnit : asts) {
      Set<ASTCDEnum> enumSet = new HashSet<>(
              astcdCompilationUnit.getCDDefinition().getCDEnumsList());
      enumUnion.addAll(enumSet);
    }
    // Union of all Enum Names
    Set<String> enumNameUnion = new HashSet<>();
    for (ASTCDEnum astcdEnum : enumUnion) {
      enumNameUnion.add(astcdEnum.getName());
    }
    Set<String> enumTypeNameUnion = new HashSet<>();
    for (ASTCDEnum e : enumUnion) {
      List<ASTCDEnumConstant> v = e.getCDEnumConstantList();
      for (ASTCDEnumConstant astcdEnumConstant : v) {
        enumTypeNameUnion.add(e.getName() + "_" + astcdEnumConstant.getName());
      }
    }

    // Generate rule output
    commonSigs.append("// U4: Concrete enum values ").append(System.lineSeparator());
    for (String enumTypeName : enumTypeNameUnion) {
      commonSigs.append("one sig enum_");
      commonSigs.append(enumTypeName);
      commonSigs.append(" extends EnumVal {}").append(System.lineSeparator());
    }

    return commonSigs.toString();
  }

  /**
   * A helper function to generate all signatures common to all CDs
   */
  private static String createCommonSignatures(Set<ASTCDCompilationUnit> asts) {

    return "// ***** Structures common to both CDs ***** " + System.lineSeparator()
        + System.lineSeparator()

        // ***** Collect all elements by computing their union *****

        // ***** Translation Rules *****
        // U1: Common Classes are Objects
        // TODO: interfaces werden im Report nicht behandelt bzw. explizit
        // ignoriert, tauchen im generierten
        // Code aber auf
        + executeRuleU1(asts) + "" + System.lineSeparator()

        // U2: Common names
        + executeRuleU2(asts) + "" + System.lineSeparator()

        // U3: Concrete primitive or unknown types
        + executeRuleU3(asts) + "" + System.lineSeparator()

        // U4: Concrete enum values
        + executeRuleU4(asts) + "" + System.lineSeparator();
  }

  /**
   * Executes the F1 rule, which generates functions returning all atoms of all
   * subclasses of all classes in class diagram cd.
   *
   */
  public static String executeRuleF1(ASTCDCompilationUnit cd) {
    StringBuilder classFunctions = new StringBuilder();

    // The set of all classes in the class diagram
    Set<ASTCDClass> classes = new HashSet<>(cd.getCDDefinition().getCDClassesList());

    classFunctions.append("// F1: Function returning all atoms of all subclasses of the class. ")
        .append(System.lineSeparator());
    for (ASTCDClass astcdClass : classes) {
      Set<ASTCDClass> subs = new HashSet<>();

      // Computation of Superclasses
      for (ASTCDClass sub : classes) {
        Set<ASTCDClass> superclasses = superClasses(sub, classes);

        if (superclasses.contains(astcdClass)) {
          subs.add(sub);
        }
      }

      // Output F1
      // Functions + Names
      classFunctions.append("fun ")
          .append(astcdClass.getName())
          .append("SubsCD")
          .append(cd.getCDDefinition().getName())
          .append(": set Obj { ");

      // All subclasses connected with a '+'
      for (ASTCDClass sub : subs) {
        classFunctions.append(sub.getName());
        classFunctions.append(" + ");
      }
      // Remove last '+'
      classFunctions.delete(classFunctions.length() - 2, classFunctions.length());
      classFunctions.append("}").append(System.lineSeparator());
    }

    return classFunctions.toString();
  }

  /**
   * A helper function to compute the transitive hull of all Superclasses of a
   * class astcdClass in classes.
   *
   * @return All superclasses of a Class
   */
  private static Set<ASTCDClass> superClasses(ASTCDClass astcdClass, Set<ASTCDClass> classes) {
    // Initialize variables
    Set<ASTCDClass> superclasses = new HashSet<>();
    LinkedList<ASTCDClass> toProcess = new LinkedList<>();
    toProcess.add(astcdClass);
    superclasses.add(astcdClass);

    // Add all superclasses of the superclasses
    while (!toProcess.isEmpty()) {
      ASTCDClass currentClass = toProcess.pop();
      superclasses.add(currentClass);

      for (ASTMCObjectType o : currentClass.getSuperclassList()) {
        // Add all superclasses to the processing list
        for (ASTCDClass astClass : classes) {
          if (o.printType(pp).equals(astClass.getName())) {
            toProcess.add(astClass);
          }
        }
      }
    }

    return superclasses;
  }

  /**
   * Executes the F2 rule, which generates functions returning all atoms of all
   * classes implementing an interface for all interfaces of the class diagram
   * cd.
   *
   */
  public static String executeRuleF2(ASTCDCompilationUnit cd) {
    StringBuilder classFunctions = new StringBuilder();

    // The set of all classes in the class diagram
    Set<ASTCDClass> classes = new HashSet<>(cd.getCDDefinition().getCDClassesList());

    classFunctions.append(
            "// F2: Functions returning all instances of classes implementing the interface. ")
        .append(System.lineSeparator());
    Set<ASTCDInterface> interfaces = new HashSet<>(
            cd.getCDDefinition().getCDInterfacesList());
    for (ASTCDInterface astcdInterface : interfaces) {
      classFunctions.append("fun ")
          .append(astcdInterface.getName())
          .append("SubsCD")
          .append(cd.getCDDefinition().getName())
          .append(": set Obj { ");

      // Compute the set of all classes implementing the interface.
      Set<ASTCDClass> impls = new HashSet<>();
      for (ASTCDClass astcdClass : classes) {
        Set<ASTCDClass> superclasses = superClasses(astcdClass, classes);

        for (ASTCDClass superClass : superclasses) {
          Set<ASTCDInterface> implInterfaces = interfaces(superClass, interfaces);

          if (implInterfaces.contains(astcdInterface)) {
            impls.add(astcdClass);
          }
        }
      }
      // Output
      if (impls.size() > 0) {
        for (ASTCDClass impl : impls) {
          classFunctions.append(impl.getName());
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
   * A helper function to compute the transitive hull of all interfaces
   * implemented by a class superClass in environment classes.
   *
   */
  private static Set<ASTCDInterface> interfaces(ASTCDClass superClass,
                                                Set<ASTCDInterface> allowedInterfaces) {
    // Initialize variables
    Set<ASTCDInterface> interfaces = new HashSet<>();
    LinkedList<ASTCDInterface> toProcess = new LinkedList<>();

    // Add all interfaces of the superclass to the processing List
    for (ASTMCObjectType astcdInterface : superClass.getInterfaceList()) {
      for (ASTCDInterface allowedInterface : allowedInterfaces) {
        if (astcdInterface.printType(pp).equals(allowedInterface.getName())) {
          toProcess.add(allowedInterface);
          break;
        }
      }
    }

    // Add all interfaces implemented by superclass or its superclasses and
    // implemented interfaces
    while (!toProcess.isEmpty()) {
      // Pop element from processing list and add it to the result
      ASTCDInterface currentInterface = toProcess.pop();
      interfaces.add(currentInterface);

      // Add all interfaces implemented by the current interface to the
      // processing list
      for (ASTMCObjectType refType : currentInterface.getInterfaceList()) {
        for (ASTCDInterface astcdInterface : allowedInterfaces) {
          if (refType.printType(pp).equals(astcdInterface.getName())) {
            toProcess.add(astcdInterface);
            break;
          }
        }
      }
    }

    return interfaces;
  }

  /**
   * Executes the F3 rule, which creates a function for each enumeration type in
   * the CD cd that returns the enumeration’s possible values.
   *
   * @return String for Alloy Module
   */
  public static String executeRuleF3(ASTCDCompilationUnit cd) {
    StringBuilder classFunctions = new StringBuilder();

    // The Definition of the class diagram
    ASTCDDefinition cdDefinition = cd.getCDDefinition();

    // The set of all enums in the class diagram
    Set<ASTCDEnum> enums = new HashSet<>(cdDefinition.getCDEnumsList());

    // Comment for F3 rule
    classFunctions.append(
            "// F3: Functions returning all possible enum values for all enums in the CD. ")
        .append(System.lineSeparator());
    for (ASTCDEnum e : enums) {
      classFunctions.append("fun ")
          .append(e.getName())
          .append("EnumCD")
          .append(cdDefinition.getName())
          .append(": set EnumVal { ");
      List<ASTCDEnumConstant> enumVals = e.getCDEnumConstantList();
      for (ASTCDEnumConstant enumVal : enumVals) {
        classFunctions.append("enum_").append(e.getName()).append("_").append(enumVal.getName());
        classFunctions.append(" + ");
      }
      // Remove last '+'
      classFunctions.delete(classFunctions.length() - 2, classFunctions.length());
      classFunctions.append("}").append(System.lineSeparator());
    }

    return classFunctions.toString();
  }

  /**
   * Executes the rule F4, which creates a function for every part of a
   * composite whole-part-relation that returns all linked whole/part instance
   * pairs in the Class Diagram cd.
   */
  public static String executeRuleF4(ASTCDCompilationUnit cd) {
    StringBuilder classFunctions = new StringBuilder();

    // The CD Definition
    ASTCDDefinition cdDefinition = cd.getCDDefinition();

    // All associations
    List<ASTCDAssociation> associations = cdDefinition.getCDAssociationsList();

    // The Parts Set
    Set<String> parts = new HashSet<>();
    for (ASTCDAssociation a : associations) {
      if (a.getCDAssocType().isComposition()) {
        parts.addAll(a.getRightReferenceName());
      }
    }

    classFunctions.append(
            "// F4: Creates a function for every part of a composite whole-part-relation")
        .append(System.lineSeparator())
        .append("// that returns all linked whole/part instance pairs.")
        .append(System.lineSeparator());
    for (String part : parts) {
      // Compute Comps
      Set<ASTCDAssociation> comps = new HashSet<>();
      for (ASTCDAssociation comp : associations) {
        if (comp.getCDAssocType().isComposition()) {
          for (String compRightReferenceName : comp.getRightReferenceName()) {
            if (compRightReferenceName.equals(part)) {
              comps.add(comp);
            }
          }
        }
      }

      // Generate Alloy function
      classFunctions.append("fun ")
          .append(part)
          .append("CompFieldsCD")
          .append(cdDefinition.getName())
          .append(": Obj->Obj {")
          .append(System.lineSeparator());
      for (ASTCDAssociation c : comps) {
        // TODO: Das ist im tech Report unterspezifiziert, gilt das immer?
        if (c.getRight().isPresentCDRole() && (c.getLeftReferenceName().size() == 1)) {
          classFunctions.append("  rel[")
              .append(c.getLeftReferenceName().get(0))
              .append("SubsCD")
              .append(cdDefinition.getName());
          classFunctions.append(",").append(c.getRight().getCDRole().getName()).append("] + ");
        } else {
          Log.error(c + ": has no roleName or more than one LeftReferenceName.");
        }
      }
      // Remove last '+'
      classFunctions.delete(classFunctions.length() - 2, classFunctions.length());

      classFunctions
          .append(System.lineSeparator())
          .append("}")
          .append(System.lineSeparator());
    }
    return classFunctions.toString();
  }

  /**
   * Generates the Functions for Subclassing, Interfaces, Compositions, and
   * Enums
   */
  private static String createFunctions(ASTCDCompilationUnit cd) {

    // Comment for each alloy module

    return "// ***** Functions specific to CD " + cd.getCDDefinition().getName() + " ***** "
        + System.lineSeparator() + System.lineSeparator()

        // F1: Functions returning all atoms of all subclasses of the class.
        + executeRuleF1(cd) + System.lineSeparator()

        // F2: Functions returning all instances of classes implementing the
        // interface.
        + executeRuleF2(cd) + System.lineSeparator()

        // F3: Functions returning all possible enum values for all enums in the CD.
        + executeRuleF3(cd) + System.lineSeparator()

        // F4: Creates a function for every part of a composite whole-part-relation
        // that returns all linked whole/part instance pairs.
        + executeRuleF4(cd) + System.lineSeparator();
  }

  /**
   * Translation rule to translate type names from a CD to corresponding Alloy
   * functions or signatures.
   */
  private static String executeRuleH1(String type, ASTCDCompilationUnit cd) {
    StringBuilder result = new StringBuilder();

    // CD Definition
    ASTCDDefinition cdDefinition = cd.getCDDefinition();

    // Is type an enum?
    for (ASTCDEnum e : cdDefinition.getCDEnumsList()) {
      if (type.equals(e.getName())) {
        result.append(type).append("EnumCD").append(cdDefinition.getName());
        return result.toString();
      }
    }

    // Is type a class?
    for (ASTCDClass c : cdDefinition.getCDClassesList()) {
      if (type.equals(c.getName())) {
        result.append(type).append("SubsCD").append(cdDefinition.getName());
        return result.toString();
      }
    }

    // Is type an interface?
    for (ASTCDInterface i : cdDefinition.getCDInterfacesList()) {
      if (type.equals(i.getName())) {
        result.append(type).append("SubsCD").append(cdDefinition.getName());
        return result.toString();
      }
    }

    // Type is none of the above
    result.append("type_").append(type);
    return result.toString();
  }

  /**
   * Rule P1 uses predicate ObjAttrib to declare the attributes of every class
   * in the class diagram cd.
   */
  public static String executeRuleP1(ASTCDCompilationUnit cd) {
    StringBuilder predicate = new StringBuilder();

    // Definition of the cd
    ASTCDDefinition cdDefinition = cd.getCDDefinition();

    // All classes of the cd
    Set<ASTCDClass> cdClasses = new HashSet<>(cdDefinition.getCDClassesList());

    // Comment
    predicate.append("// P1: Attribute declaration").append(System.lineSeparator());
    for (ASTCDClass astcdClass : cdClasses) {
      // Compute the attribute union of all superclasses
      Set<ASTCDAttribute> attributeUnion = new HashSet<>();
      for (ASTCDClass attributeClass : superClasses(astcdClass, cdClasses)) {
        attributeUnion.addAll(attributeClass.getCDAttributeList());
      }

      // Generate Alloy predicate
      for (ASTCDAttribute astcdAttribute : attributeUnion) {
        predicate.append("ObjAttrib[").append(astcdClass.getName()).append(", ");
        predicate.append(astcdAttribute.getName()).append(", ");
        predicate.append(executeRuleH1(astcdAttribute.getMCType().printType(pp), cd))
            .append("]")
            .append(System.lineSeparator());
      }

    }
    return predicate.toString();
  }

  /**
   * Application of Rule P2, which restricts the tuples of the get relation to
   * the attributes of the class and to the role names of its partners in
   * associations.
   */
  public static String executeRuleP2(ASTCDCompilationUnit cd) {
    StringBuilder predicate = new StringBuilder();

    // Definition of the cd
    ASTCDDefinition cdDefinition = cd.getCDDefinition();

    // All classes of the cd
    Set<ASTCDClass> cdClasses = new HashSet<>(cdDefinition.getCDClassesList());

    // All interfaces of the cd
    Set<ASTCDInterface> cdInterfaces = new HashSet<>(cdDefinition.getCDInterfacesList());

    // Comment
    predicate.append(
            "// P2: Restricts the tuples of the get relation to the attributes of the class ")
        .append(System.lineSeparator())
        .append("// and to the role names of its partners in associations. ")
        .append(System.lineSeparator());

    for (ASTCDClass astcdClass : cdClasses) {
      // All names of superclasses and their interfaces
      Set<String> superNames = new HashSet<>();
      for (ASTCDClass superclass : superClasses(astcdClass, cdClasses)) {
        // Add all names of super classes
        superNames.add(superclass.getName());

        // Add all names of implemented interfaces by this class
        for (ASTCDInterface i : interfaces(superclass, cdInterfaces)) {
          superNames.add(i.getName());
        }
      }

      // Computation of fields
      Set<String> fields = new HashSet<>();

      // All attributes of the superclasses
      for (ASTCDClass superclass : superClasses(astcdClass, cdClasses)) {
        for (ASTCDAttribute a : superclass.getCDAttributeList()) {
          fields.add(a.getName());
        }
      }


      // All left roles
      for (ASTCDAssociation a : cdDefinition.getCDAssociationsList()) {
        // Skip associations with wrong direction
        if (a.getCDAssocDir().isDefinitiveNavigableRight() && !a.getCDAssocDir().isBidirectional()) {
          continue;
        }
        for (String lrName : a.getRightReferenceName()) {
          if (a.getLeft().isPresentCDRole() && superNames.contains(lrName)) {
            fields.add(a.getLeft().getCDRole().getName());
          } else {
            // TODO: Nur ein test
            if (superNames.contains(lrName)) {
              // Preprocess parts of reference names and add them as role name
              String name = partHandler(a.getLeftReferenceName());
              fields.add(name);
            }
          }
        }
      }

      // All right roles
      for (ASTCDAssociation a : cdDefinition.getCDAssociationsList()) {
        // Skip associations with wrong direction
        if (a.getCDAssocDir().isDefinitiveNavigableLeft() && !a.getCDAssocDir().isBidirectional()) {
          continue;
        }
        for (String rrName : a.getLeftReferenceName()) {
          if (a.getRight().isPresentCDRole() && superNames.contains(rrName)) {
            fields.add(a.getRight().getCDRole().getName());
          } else {
            // TODO: Nur ein test
            if (superNames.contains(rrName)) {
              // Preprocess parts of reference names and add them as role name
              String name = partHandler(a.getRightReferenceName());
              fields.add(name);
            }
          }
        }
      }

      // Output
      predicate.append("ObjFNames[").append(astcdClass.getName()).append(", ");
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
   * Application of Rule P3, which ensures that signatures representing abstract
   * classes have no atoms and that signatures representing singleton classes
   * contain exactly one atom.
   */
  public static String executeRuleP3(ASTCDCompilationUnit cd) {
    StringBuilder predicate = new StringBuilder();

    // CD Definition
    ASTCDDefinition cdDefinition = cd.getCDDefinition();

    // Comment
    predicate.append("// P3: Atoms for Singleton classes and abstarct classses ")
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
      if (astcdClass.getModifier().toString().equals(" singleton ")) {
        singletonClasses.add(astcdClass);
      }
    }
    // Alloy predicate for abstract classes
    for (ASTCDClass astcdClass : abstractClasses) {
      predicate.append("no ").append(astcdClass.getName()).append(System.lineSeparator());
    }

    // Alloy predicates for singleton classes
    for (ASTCDClass astcdClass : singletonClasses) {
      predicate.append("one ").append(astcdClass.getName()).append(System.lineSeparator());
    }

    return predicate.toString();
  }

  /**
   * Application of Rule P4, which restricts all objects in object models of the
   * CD to be instances of the classes of the CD.
   */
  public static String executeRuleP4(ASTCDCompilationUnit cd) {
    StringBuilder predicate = new StringBuilder();

    // CD Definition
    ASTCDDefinition cdDefinition = cd.getCDDefinition();

    // All classes of the
    List<ASTCDClass> cdClasses = cdDefinition.getCDClassesList();

    // Comment
    predicate.append("// P4: Restrict Objects to instances of classes in the CD. ")
        .append(System.lineSeparator());

    predicate.append("Obj = (");
    if (cdClasses.size() > 0) {
      for (ASTCDClass astcdClass : cdClasses) {
        predicate.append(astcdClass.getName());
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

  /**
   * Rule A1 constraints the sets of links of bidirectional associations.
   */
  public static String executeRuleA1(ASTCDCompilationUnit cd) {
    StringBuilder predicate = new StringBuilder();

    // CD Definition
    ASTCDDefinition cdDefinition = cd.getCDDefinition();

    // Associations in cd
    List<ASTCDAssociation> cdAssociations = cdDefinition.getCDAssociationsList();

    // Comment
    predicate.append("// A1: Constraints the sets of links of bidirectional associations.")
        .append(System.lineSeparator());
    for (ASTCDAssociation a : cdAssociations) {
      if (a.getCDAssocDir().isBidirectional() || (!a.getCDAssocDir()
              .isDefinitiveNavigableLeft() && !a.getCDAssocDir()
              .isDefinitiveNavigableRight())) {
        List<String> leftReferenceNames = a.getLeftReferenceName();
        List<String> rightReferenceNames = a.getRightReferenceName();

        // Generation
        predicate.append("BidiAssoc[");
        for (String lRName : leftReferenceNames) {
          predicate.append(executeRuleH1(lRName, cd));
          predicate.append(",");
        }
        if (a.getRight().isPresentCDRole()) {
          predicate.append(a.getRight().getCDRole().getName());
        } else {
          // Preprocess parts of reference names and add them as role name
          String name = partHandler(a.getRightReferenceName());
          predicate.append(name);
        }
        predicate.append(",");
        for (String rRName : rightReferenceNames) {
          predicate.append(executeRuleH1(rRName, cd));
          predicate.append(",");
        }
        if (a.getLeft().isPresentCDRole()) {
          predicate.append(a.getLeft().getCDRole().getName());
        } else {
          // Preprocess parts of reference names and add them as role name
          String name = partHandler(a.getLeftReferenceName());
          predicate.append(name);
        }
        predicate.append("]").append(System.lineSeparator());
      }
    }

    return predicate.toString();
  }

  /**
   * Executes Rule A2, which ensures that parts of compositions have at most one
   * whole.
   */
  public static String executeRuleA2(ASTCDCompilationUnit cd) {
    StringBuilder predicate = new StringBuilder();

    // CD Definition
    ASTCDDefinition cdDefinition = cd.getCDDefinition();

    // Comment
    predicate.append("// A2: ensures that parts of compositions have at most one whole.")
        .append(System.lineSeparator());

    for (ASTCDAssociation a : cdDefinition.getCDAssociationsList()) {
      for (String part : a.getRightReferenceName()) {
        if (a.getCDAssocType().isComposition()) {
          // Generation
          predicate.append("Composition[");
          predicate.append(part);
          predicate.append("CompFieldsCD");
          predicate.append(cdDefinition.getName());
          predicate.append(",");
          predicate.append(part);
          predicate.append("SubsCD");
          predicate.append(cdDefinition.getName());
          predicate.append("]").append(System.lineSeparator());
        }
      }
    }
    return predicate.toString();
  }

  /**
   * Applies rule A3, which ensures the cardinality constraints stated on the
   * left sides of bidirectional associations, undirected association, and
   * associations that are navigable from right to left are respected.
   */
  public static String executeRuleA3(ASTCDCompilationUnit cd) {
    StringBuilder predicate = new StringBuilder();

    // CD Definition
    ASTCDDefinition cdDefinition = cd.getCDDefinition();

    // Comment
    predicate.append(
            "// A3: ensures the cardinality constraints stated on the left sides of bidirec-")
        .append(System.lineSeparator())
        .append(
            "// tional associations, undirected association, and associations that are navigable "
                + "from right")
        .append(System.lineSeparator())
        .append("// to left are respected.")
        .append(System.lineSeparator());

    for (ASTCDAssociation asc : cdDefinition.getCDAssociationsList()) {
      if ((asc.getCDAssocDir().isBidirectional() || asc.getCDAssocDir()
              .isDefinitiveNavigableLeft() || (!asc.getCDAssocDir()
              .isDefinitiveNavigableLeft() && !asc
              .getCDAssocDir()
              .isDefinitiveNavigableRight()))
              && asc.getLeft().isPresentCDCardinality()) {
        predicate.append(appendRemainingRuleA(3, cd, asc));
      }
    }

    return predicate.toString();
  }

  /**
   * Executes Rule 4, which ensures the cardinality constraints stated on the
   * right sides of associations, which are navigable from right to left, are
   * respected.
   */
  public static String executeRuleA4(ASTCDCompilationUnit cd) {
    StringBuilder predicate = new StringBuilder();

    // CD Definition
    ASTCDDefinition cdDefinition = cd.getCDDefinition();

    // Comment
    predicate.append("// A4: ensures the cardinality constraints stated on the right sides of")
        .append(System.lineSeparator())
        .append("// associations, which are navigable from right to left, are respected.")
        .append(System.lineSeparator());

    // Computation
    for (ASTCDAssociation asc : cdDefinition.getCDAssociationsList()) {
      if ((asc.getCDAssocDir().isDefinitiveNavigableLeft() && !asc.getCDAssocDir().isBidirectional()) && asc.getRight().isPresentCDCardinality()) {
        predicate.append(appendRemainingRuleA(4, cd, asc));
      }
    }
    return predicate.toString();
  }

  /**
   * Executes Rule 5, which ensures the cardinality constraints stated on the
   * right sides of bidirectional associations, undirected association, and
   * associations that are navigable from right to left are respected.
   */
  public static String executeRuleA5(ASTCDCompilationUnit cd) {
    StringBuilder predicate = new StringBuilder();

    // CD Definition
    ASTCDDefinition cdDefinition = cd.getCDDefinition();

    // Comment
    predicate.append("// A5: ensures the cardinality constraints stated on the right sides of")
        .append(System.lineSeparator())
        .append(
            "// bidirectional associations, undirected association, and associations that are "
                + "navigable from")
        .append(System.lineSeparator())
        .append("// right to left are respected.")
        .append(System.lineSeparator());

    // Computation
    for (ASTCDAssociation asc : cdDefinition.getCDAssociationsList()) {
      if ((asc.getCDAssocDir().isBidirectional() || asc.getCDAssocDir()
              .isDefinitiveNavigableRight() || (!asc.getCDAssocDir()
              .isDefinitiveNavigableLeft() && !asc
              .getCDAssocDir()
              .isDefinitiveNavigableRight()))
              && asc.getRight().isPresentCDCardinality()) {
        predicate.append(appendRemainingRuleA(5, cd, asc));
      }
    }
    return predicate.toString();
  }

  /**
   * Executes Rule 5, which ensures the cardinality constraints stated on the
   * left sides of associations that are navigable from left to right are
   * respected.
   */
  public static String executeRuleA6(ASTCDCompilationUnit cd) {
    StringBuilder predicate = new StringBuilder();

    // CD Definition
    ASTCDDefinition cdDefinition = cd.getCDDefinition();

    // Comment
    predicate.append(
            "// A6: ensures the cardinality constraints stated on the left sides of associ-")
        .append(System.lineSeparator())
        .append("// ations that are navigable from left to right are respected.")
        .append(System.lineSeparator());

    // Computation
    for (ASTCDAssociation asc : cdDefinition.getCDAssociationsList()) {
      if ((asc.getCDAssocDir().isDefinitiveNavigableRight() && !asc.getCDAssocDir().isBidirectional()) && asc.getLeft().isPresentCDCardinality()) {
        predicate.append(appendRemainingRuleA(6, cd, asc));
      }
    }
    return predicate.toString();
  }


  /**
   * private helper method that completes predicate for rules A4 - A6
   */
  private static StringBuilder appendRemainingRuleA(int ruleID, ASTCDCompilationUnit cd,
      ASTCDAssociation association){

    StringBuilder predicate = new StringBuilder();

    String infix="";
    ASTCDCardinality cardinality;
    List<String> firstReferenceName;
    List<String> secondReferenceName;
    boolean rolePresent;
    String roleName;

    switch (ruleID){
      case 3 : {
        infix="Attrib";
        // Get left cardinality
        cardinality = association.getLeft().getCDCardinality();
        firstReferenceName = association.getRightReferenceName();
        secondReferenceName = association.getLeftReferenceName();
        rolePresent = association.getLeft().isPresentCDRole();
        if (rolePresent) {
          roleName = association.getLeft().getCDRole().getName();
        } else {
          roleName = partHandler(secondReferenceName);
        }
        break;
      }
      case 4 : {
        // Get right cardinality
        cardinality = association.getRight().getCDCardinality();
        firstReferenceName = association.getLeftReferenceName();
        secondReferenceName = association.getRightReferenceName();
        rolePresent = association.getLeft().isPresentCDRole();
        if (rolePresent) {
          roleName = association.getLeft().getCDRole().getName();
        } else {
          roleName = partHandler(firstReferenceName);
        }
        break;
      }
      case 5 : {
        infix="Attrib";
        // Get right cardinality
        cardinality = association.getRight().getCDCardinality();
        firstReferenceName = association.getLeftReferenceName();
        secondReferenceName = association.getRightReferenceName();
        rolePresent = association.getRight().isPresentCDRole();
        if (rolePresent) {
          roleName = association.getRight().getCDRole().getName();
        } else {
          roleName = partHandler(secondReferenceName);
        }
        break;
      }
      case 6 : {
        // Get left cardinality
        cardinality = association.getLeft().getCDCardinality();
        firstReferenceName = association.getRightReferenceName();
        secondReferenceName = association.getLeftReferenceName();
        rolePresent = association.getRight().isPresentCDRole();
        if (rolePresent) {
          roleName = association.getRight().getCDRole().getName();
        } else {
          roleName = partHandler(firstReferenceName);
        }
        break;
      }
      default: {
        Log.error("incorrect ruleID, method is designed for A4-A6 only");
        return predicate;
      }
    }

    String lowerCardinality;
    String upperCardinality = "";

    if (cardinality.isOne() || cardinality.isOpt()) {
      predicate.append("ObjLU").append(infix).append("[");

      // Get lower Cardinality
      if (cardinality.isOne()) {
        lowerCardinality = "1";
      } else {
        lowerCardinality = "0";
      }
      // Set upperCardinality
      upperCardinality = "1";
    } else {
      predicate.append("ObjL").append(infix).append("[");

      // Set lower cardinality
      if (cardinality.isAtLeastOne()) {
        lowerCardinality = "1";
      } else {
        lowerCardinality = "0";
      }
    }

    for (String part : firstReferenceName) {
      predicate.append(executeRuleH1(part, cd));
      predicate.append(", ");
    }

    predicate.append(roleName);
    predicate.append(", ");


    for (String part : secondReferenceName) {
      predicate.append(executeRuleH1(part, cd));
      predicate.append(", ");
    }

    // Write cardinalities
    predicate.append(lowerCardinality);
    if (!upperCardinality.equals("")) {
      predicate.append(", ");
      predicate.append(upperCardinality);
    }
    predicate.append("]").append(System.lineSeparator());
    return predicate;
  }

  /**
   * Creates all predicates necessary for the description of the semantics
   */
  public static String createPredicates(ASTCDCompilationUnit cd) {
    StringBuilder predicate = new StringBuilder();

    // The definition of the CD
    ASTCDDefinition cdDefinition = cd.getCDDefinition();

    // Comment
    predicate.append("// Semantics predicate ")
        .append(cdDefinition.getName())
        .append(System.lineSeparator());

    // Begin predicate
    predicate.append("pred ")
        .append(cdDefinition.getName())
        .append(" {")
        .append(System.lineSeparator())
        .append(System.lineSeparator());

    predicate.append("// Classes and attributes in ")
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
    predicate.append("// Associations in ")
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
   * @param asts            the set of asts to generate the alloy module for.
   */
  public static String generate(Set<ASTCDCompilationUnit> asts) {
    // Only generate a module for non-empty asts
    if (asts.isEmpty()) {
      return "";
    }

    // TODO: Could be externalised in a preprocessing function
    // Check if two CDs have the same name and rename them, if this is the case
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
          ((ASTCDCompilationUnit) astsArray[j]).getCDDefinition().setName(currentName);

          // Reset j to repeat test
          j = i + 1;

          changed = true;
        }
      }
    }

    // adapt changes in asts if changes appeared
    if (changed) {
      asts = new HashSet<>();
      for (Object o : astsArray) {
        ASTCDCompilationUnit ast = (ASTCDCompilationUnit) o;
        asts.add(ast);
      }
    }

    // Derive the name of the module
    String moduleName = generateModuleName(asts);

    // Initialize output variable
    StringBuilder module = new StringBuilder(
        "module " + moduleName + System.lineSeparator() + " " + System.lineSeparator());

    // Generate the Generic Part
    module.append(createGenericPart());

    // Signatures common to all CDs
    module.append(createCommonSignatures(asts));

    // Functions specific to CD
    for (ASTCDCompilationUnit cd : asts) {
      module.append(createFunctions(cd));
    }

    // semantics predicate for each CD
    for (ASTCDCompilationUnit cd : asts) {
      module.append(createPredicates(cd));
    }

    return module.toString();
  }

  /**
   * Writes a string containing an alloy module into a file
   *
   * @param module          the string the Alloy module should contain
   * @param outputDirectory the directory to generate the Alloy Module in.
   */
  public static void saveModule(String module, String moduleName, File outputDirectory) {
    // Set Output Path
    String outputPath = outputDirectory.toString() + "/" + moduleName.toLowerCase() + "/";
    Path outputFile = Paths.get(outputPath, moduleName + ".als");

    // Write results into a file
    try {
      FileUtils.writeStringToFile(outputFile.toFile(), module, Charset.defaultCharset());
    } catch (IOException e) {
      e.printStackTrace();

      Log.error("Could not create Alloy Module file.");
    }

    Log.trace(LOGGER_NAME, "Generated Alloy Module " + moduleName
            + " in folder" + outputDirectory.getAbsolutePath());
  }

  /**
   * Writes a string containing an alloy module into a file and return path
   *
   * @param module          the string the Alloy module should contain
   * @param outputDirectory the directory to generate the Alloy Module in.
   * @return Path to the file
   */
  public static Path saveModulePath(String module, String moduleName, File outputDirectory) {
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
   * @param asts            the set of asts to generate the alloy module for.
   * @param outputDirectory the directory to generate the Alloy Module in.
   */
  public static void generateModuleToFile(Set<ASTCDCompilationUnit> asts, File outputDirectory) {
    // Generate the name of the module
    String moduleName = generateModuleName(asts);

    // Generate Standard module
    String module = generate(asts);

    System.out.println(module);

    // Save the module in the output Directory
    saveModule(module, moduleName, outputDirectory);
  }

  /**
   * Generates the Alloy Module in outputDirectory using the ast.
   *
   * @param asts the set of asts to generate the alloy module for.
   */
  public static String generateModule(Set<ASTCDCompilationUnit> asts) {

    return generate(asts);
  }

  /**
   * Generates the Alloy Module in outputDirectory using the ast.
   * TODO: Better documentation
   * @param filePaths ?
   */
  public static String generateModuleFromFiles(Set<String> filePaths) {
    Set<ASTCDCompilationUnit> cds = new HashSet<>();

    for (String modelFile : filePaths) {
      Path model = Paths.get(modelFile);
      CD4AnalysisParser parser = new CD4AnalysisParser();
      Optional<ASTCDCompilationUnit> optCD;
      try {
        optCD = parser.parse(model.toString());
        // assertFalse(parser.hasErrors());
        assert(optCD.isPresent());
        cds.add(optCD.get());
      } catch (Exception e) {
        e.printStackTrace();
        Log.error("There was an exception when parsing the model " + modelFile + ": "
                + e.getMessage());
      }
    }

    return generate(cds);
  }

  public static String generateModuleName(Set<ASTCDCompilationUnit> asts) {
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