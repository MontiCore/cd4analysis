package de.monticore.cd2alloy.generator;

public class OpenWorldGenerator extends CD2AlloyGenerator {
  public static String createGenericPart() {
    return "// ***** Generic Part ***** " + System.lineSeparator() + " " + System.lineSeparator()

        // Comment for abstract Signatures
        + "// The abstract signatures FName, Obj, Val, and EnumVal. " + System.lineSeparator()

        // Abstract Signature for Objects
        + "abstract sig Obj { get: FName -> {Obj + Val + EnumVal}, super: set Type } "
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
        + "src.type.inst | o.get[q] in target.inst}" + System.lineSeparator() + "}";
  }

}
