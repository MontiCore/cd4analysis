package de.monticore.cd2alloy.generator;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd2alloy.cocos.CD2AlloyCoCos;
import de.monticore.cd4analysis._cocos.CD4AnalysisCoCoChecker;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cd4code.trafo.CD4CodeDirectCompositionTrafo;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OpenWorldGenerator extends CD2AlloyGenerator {
  public static String createGenericPart() {
    return "// ***** Generic Part ***** " + System.lineSeparator() + " " + System.lineSeparator()

        // Comment for abstract Signatures
        + "// The abstract signatures FName, Obj, Val, and EnumVal. " + System.lineSeparator()

        // Abstract Signature for Objects
        + "abstract sig Obj { get: FName -> {Obj + Val + EnumVal}, type: set Type } "
        + System.lineSeparator()
        // Abstract Signature for Names
        + "abstract sig FName {} " + System.lineSeparator()
        // Abstract Signature for Values
        + "abstract sig Val {} " + System.lineSeparator()
        // Abstract Signature for EnumValues
        + "abstract sig EnumVal {} " + System.lineSeparator() + System.lineSeparator()

        // Comment for Parametrized predicates
        + "// Predicates used to specify cardinality constraints for navigable association"
        + System.lineSeparator() + "// ends and for association ends of undirected associations."
        + System.lineSeparator() + System.lineSeparator()
        + "pred ObjAttrib[objs: set Obj, fName: one FName," + System.lineSeparator()
        + " fType: set {Obj + Val + EnumVal}] {" + System.lineSeparator()
        + " objs.get[fName] in fType" + System.lineSeparator() + " all o: objs| one o.get[fName] }"
        + System.lineSeparator() + System.lineSeparator()
        + "pred ObjFNames[objs: set Obj, fNames:set FName]{" + System.lineSeparator()
        + " no objs.get[FName - fNames] }" + System.lineSeparator() + System.lineSeparator()
        + "pred BidiAssoc[left: set Obj, lFName:one FName," + System.lineSeparator()
        + " right: set Obj, rFName:one FName] {" + System.lineSeparator()
        + " all l: left | all r: l.get[lFName] | l in r.get[rFName]" + System.lineSeparator()
        + " all r: right | all l: r.get[rFName] | r in l.get[lFName] }" + System.lineSeparator()
        + System.lineSeparator() + "pred Composition[compos: Obj->Obj, right: set Obj] {"
        + System.lineSeparator() + " all r: right | lone compos.r }" + System.lineSeparator() + " "
        + System.lineSeparator() + "fun rel[wholes: set Obj, fn: FName] : Obj->Obj {"
        + System.lineSeparator() + " {o1:Obj,o2:Obj|o1->fn->o2 in wholes <: get} } "
        + System.lineSeparator() + System.lineSeparator()
        + "// Predicates used to specify cardinality constraints for navigable association"
        + System.lineSeparator() + "// ends and for association ends of undirected associations. "
        + System.lineSeparator()
        + "pred ObjUAttrib[objs: set Obj, fName:one FName, fType:set Obj, up: Int] {"
        + System.lineSeparator() + " objs.get[fName] in fType" + System.lineSeparator()
        + " all o: objs| (#o.get[fName] =< up) } " + System.lineSeparator() + System.lineSeparator()
        + "pred ObjLAttrib[objs: set Obj, fName: one FName, fType: set Obj, low: Int] {"
        + System.lineSeparator() + " objs.get[fName] in fType" + System.lineSeparator()
        + " all o: objs | (#o.get[fName] >= low) }" + System.lineSeparator()
        + System.lineSeparator() + "pred ObjLUAttrib[objs:set Obj, fName:one FName, fType:set Obj,"
        + System.lineSeparator() + " low: Int, up: Int] {" + System.lineSeparator()
        + " ObjLAttrib[objs, fName, fType, low]" + System.lineSeparator()
        + " ObjUAttrib[objs, fName, fType, up] }" + System.lineSeparator() + System.lineSeparator()
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
        + System.lineSeparator() + "}" + System.lineSeparator() + System.lineSeparator()
        + "abstract sig Type { super: set Type, inst : set Obj}" + System.lineSeparator()
        + "abstract sig Enum {values: set EnumVal}" + System.lineSeparator()
        + System.lineSeparator() + "pred ObjTypes[obj: set Obj, types: set Type]{"
        + System.lineSeparator() + " all o:obj| o.type.super = types}" + System.lineSeparator()

        + System.lineSeparator() + "fact InstancesOfTypes {" + System.lineSeparator()
        + " all t: Type | t.inst = {o:Obj | t in o.type.super}}" + System.lineSeparator()
        + System.lineSeparator() + "fact NoCyclicalInheritance {" + System.lineSeparator()
        + " all t1: Type | all t2: Type | {t2 in t1.super} && {t1 in t2.super} => {t1 = t2}}"
        + System.lineSeparator() + System.lineSeparator() + "fact ReflexiveTransitiveInheritance {"
        + System.lineSeparator() + " all t1: Type | t1 in t1.super" + System.lineSeparator()
        + " all t1: Type | all t2: Type | {t2 in t1.super} => {t2.super in t1.super}}"
        + System.lineSeparator() + System.lineSeparator() + "fact GetConsistency {"
        + System.lineSeparator()
        + " all src: Obj | all q : FName | src.get[q] in EnumVal => {some e:Enum | ObjAttrib[src"
        + ".type.inst,q,e.values]}" + System.lineSeparator()
        + " all src: Obj | all q : FName | src.get[q] in Val => {some v:Val | ObjAttrib[src.type"
        + ".inst,q,v]}" + System.lineSeparator()
        + " all src: Obj | all q : FName | src.get[q] in Obj  => {some target : Type | all o : "
        + "src.type.inst | o.get[q] in target.inst}" + System.lineSeparator() + "}"
        + System.lineSeparator() + System.lineSeparator();
  }

  public static String generate(Set<ASTCDCompilationUnit> asts) {
    // Only generate a module for non-empty asts
    if (asts.isEmpty()) {
      return "";
    }

    for (ASTCDCompilationUnit ast : asts) {
      // build symbol table
      CD4CodeMill.globalScope().clear();
      BuiltInTypes.addBuiltInTypes(CD4CodeMill.globalScope());
      new CD4CodeDirectCompositionTrafo().transform(ast);
      CD2AlloyCoCos cd2aCoCos = new CD2AlloyCoCos();
      CD4AnalysisCoCoChecker cocos = cd2aCoCos.getCheckerForAllCoCos();
      CD4CodeMill.scopesGenitorDelegator().createFromAST(ast);
      CD4CodeSymbolTableCompleter c = new CD4CodeSymbolTableCompleter(
          ast.getMCImportStatementList(), MCBasicTypesMill.mCQualifiedNameBuilder().build());
      ast.accept(c.getTraverser());
      cocos.checkAll(ast);
    }

    renameASTs(asts);

    // Derive the name of the module
    String moduleName = generateModuleName(asts);

    // Initialize output variable
    StringBuilder module = new StringBuilder(
        "module " + moduleName + System.lineSeparator() + " " + System.lineSeparator());

    // Generate the Generic Part
    module.append(createGenericPart());

    // Signatures common to all CDs
    module.append(CD2AlloyGenerator.executeRuleU1(asts)).append(System.lineSeparator());
    module.append(CD2AlloyGenerator.executeRuleU2(asts)).append(System.lineSeparator());
    module.append(CD2AlloyGenerator.executeRuleU3(asts)).append(System.lineSeparator());
    module.append(executeRuleU4(asts)).append(System.lineSeparator());

    // Singleton Class Signatures for multi-instance semantics
    module.append(CD2AlloyGenerator.executeRuleU5(asts, true)).append(System.lineSeparator());

    // Functions specific to CD
    for (ASTCDCompilationUnit cd : asts) {
      module.append(executeRuleF1(cd)).append(System.lineSeparator());
      module.append(CD2AlloyGenerator.executeRuleF2(cd)).append(System.lineSeparator());
      module.append(CD2AlloyGenerator.executeRuleF3(cd)).append(System.lineSeparator());
      module.append(CD2AlloyGenerator.executeRuleF4(cd)).append(System.lineSeparator());
    }

    // semantics predicate for each CD
    for (ASTCDCompilationUnit cd : asts) {
      module.append(createPredicates(cd, true));
    }

    return module.toString();
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
      enumNameUnion.add(CD2AlloyQNameHelper.processQName(astcdEnum.getSymbol().getFullName()));
    }
    Set<String> enumTypeNameUnion = new HashSet<>();
    for (ASTCDEnum e : enumUnion) {
      List<ASTCDEnumConstant> v = e.getCDEnumConstantList();
      for (ASTCDEnumConstant astcdEnumConstant : v) {
        enumTypeNameUnion.add(CD2AlloyQNameHelper.processQName(e.getSymbol().getFullName()) + "_"
            + astcdEnumConstant.getName());
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
      commonSigs.append("one sig ")
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

  public static String createPredicates(ASTCDCompilationUnit cd, boolean newSemantics) {
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

    if (newSemantics) {
      predicate.append(executeRuleP0(cd)).append(System.lineSeparator());
    }
    else {
      predicate.append(("ObjTypes[Obj,(Type_Dummy)]"))
          .append(System.lineSeparator())
          .append(System.lineSeparator());
    }

    predicate.append("// Classes and attributes in ")
        .append(cdDefinition.getName())
        .append(System.lineSeparator());

    // P1: Declaration of Attributes
    predicate.append(executeRuleP1(cd)).append(System.lineSeparator());

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
   * additional rule for new semantics
   */
  public static String executeRuleP0(ASTCDCompilationUnit cd) {
    StringBuilder classFunctions = new StringBuilder();

    // The set of all classes in the class diagram
    Set<ASTCDClass> classes = new HashSet<>(cd.getCDDefinition().getCDClassesList());

    classFunctions.append("// P0: New rule for multi-instance semantics. ")
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
      classFunctions.append("all c: ")
          .append(CD2AlloyQNameHelper.processQName(astcdClass.getSymbol().getFullName()))
          .append(" | c.type=Type_")
          .append(CD2AlloyQNameHelper.processQName(astcdClass.getSymbol().getFullName()))
          .append(System.lineSeparator());

      // All subclasses connected with a '+'
      for (ASTCDType superType : superList) {
        classFunctions.append("Type_")
            .append(CD2AlloyQNameHelper.processQName(superType.getSymbol().getFullName()))
            .append(" in Type_")
            .append(CD2AlloyQNameHelper.processQName(astcdClass.getSymbol().getFullName()))
            .append(".super")
            .append(System.lineSeparator());
      }
    }

    return classFunctions.toString();

  }

  /**
   * Executes the F1 rule, which generates functions returning all atoms of all subclasses of all
   * classes in class diagram cd.
   */
  public static String executeRuleF1(ASTCDCompilationUnit cd) {
    StringBuilder classFunctions = new StringBuilder();

    // The set of all classes in the class diagram
    Set<ASTCDClass> classes = new HashSet<>(cd.getCDDefinition().getCDClassesList());

    classFunctions.append("// F1: Function returning all atoms of all subclasses of the class. ")
        .append(System.lineSeparator());
    for (ASTCDClass astcdClass : classes) {

      // Output F1
      // Functions + Names
      classFunctions.append("fun ")
          .append(CD2AlloyQNameHelper.processQName(astcdClass.getSymbol().getFullName()))
          .append("SubsCD")
          .append(cd.getCDDefinition().getName())
          .append(": set Obj { ")
          .append("Type_")
          .append(CD2AlloyQNameHelper.processQName(astcdClass.getSymbol().getFullName()))
          .append(".inst").append("}").append(System.lineSeparator());
    }

    return classFunctions.toString();
  }

}
