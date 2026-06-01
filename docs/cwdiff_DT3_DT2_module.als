module DigitalTwin3_DigitalTwin2_module
 
// ***** Generic Part ***** 
 
// The abstract signatures FName, Obj, Val, and EnumVal. 
abstract sig Obj { get: FName -> {Obj + Val + EnumVal}, type: Type } 
abstract sig FName {} 
abstract sig Val {} 
abstract sig EnumVal {} 
abstract sig Type { super: set Type, inst : set Obj}
 
pred ObjTypes[obj: set Obj, types: set Type]{
 all o:obj| o.type.super = types}

// Predicates used to specify cardinality constraints for navigable association
// ends and for association ends of undirected associations.
pred ObjAttrib[objs: set Obj, fName: one FName,
 fType: set {Obj + Val + EnumVal}] {
 objs.get[fName] in fType
 all o: objs| one o.get[fName] }

pred ObjFNames[objs: set Obj, fNames:set FName]{
 no objs.get[FName - fNames] }

pred BidiAssoc[left: set Obj, lFName:one FName,
 right: set Obj, rFName:one FName] {
 all l: left | all r: l.get[lFName] | l in r.get[rFName]
 all r: right | all l: r.get[rFName] | r in l.get[lFName] }

pred Composition[compos: Obj->Obj, right: set Obj] {
 all r: right | lone compos.r }
 
fun rel[wholes: set Obj, fn: FName] : Obj->Obj {
 {o1:Obj,o2:Obj|o1->fn->o2 in wholes <: get} } 

// Predicates used to specify cardinality constraints for navigable association
// ends and for association ends of undirected associations. 
pred ObjUAttrib[objs: set Obj, fName:one FName, fType:set Obj, up: Int] {
 objs.get[fName] in fType
 all o: objs| (#o.get[fName] =< up) } 

pred ObjLAttrib[objs: set Obj, fName: one FName, fType: set Obj, low: Int] {
 objs.get[fName] in fType
 all o: objs | (#o.get[fName] >= low) }

pred ObjLUAttrib[objs:set Obj, fName:one FName, fType:set Obj,
 low: Int, up: Int] {
 ObjLAttrib[objs, fName, fType, low]
 ObjUAttrib[objs, fName, fType, up] }

// Parametrized predicates used to specify cardinality constraints for non-
// navigable association ends. 
pred ObjL[objs: set Obj, fName:one FName, fType: set Obj, low: Int] {
 all r: objs | # { l: fType | r in l.get[fName]} >= low } 

pred ObjU[objs: set Obj, fName:one FName, fType: set Obj, up: Int] {
 all r: objs | # { l: fType | r in l.get[fName]} =< up } 

pred ObjLU[objs: set Obj, fName:one FName, fType: set Obj,
 low: Int, up: Int] {
 ObjL[objs, fName, fType, low]
 ObjU[objs, fName, fType, up] }

fact InstancesOfTypes {
 all t: Type | t.inst = {o:Obj | t in o.type.super}}

fact NonEmptyInstancesOnly {
 some Obj
}

// ***** Structures common to both CDs ***** 

// U1: Common types 
sig ProcessModel extends Obj {}
sig DigitalShadow extends Obj {}
sig DigitalTwin extends Obj {}
sig DataTrace extends Obj {}
sig DataModel extends Obj {}
sig Model extends Obj {}
sig System extends Obj {}
sig Machine extends Obj {}

// U2: Common names 
one sig models extends FName {}
one sig original extends FName {}
one sig of extends FName {}
one sig traces extends FName {}
one sig shadows extends FName {}

// U3: Concrete primitive or unknown types 
fact {no Val}

// U4: Concrete enum values 
fact {no EnumVal}

// U5: Common types 
lone sig Type_ProcessModel extends Type {}
fact{some Type_ProcessModel => some Type_ProcessModel.inst}
lone sig Type_DigitalShadow extends Type {}
fact{some Type_DigitalShadow => some Type_DigitalShadow.inst}
lone sig Type_DigitalTwin extends Type {}
fact{some Type_DigitalTwin => some Type_DigitalTwin.inst}
lone sig Type_DataTrace extends Type {}
fact{some Type_DataTrace => some Type_DataTrace.inst}
lone sig Type_DataModel extends Type {}
fact{some Type_DataModel => some Type_DataModel.inst}
lone sig Type_Model extends Type {}
fact{some Type_Model => some Type_Model.inst}
lone sig Type_System extends Type {}
fact{some Type_System => some Type_System.inst}
lone sig Type_Machine extends Type {}
fact{some Type_Machine => some Type_Machine.inst}
fact{all c: ProcessModel | c.type=Type_ProcessModel
Type_ProcessModel in Type_ProcessModel.super
all c: DigitalShadow | c.type=Type_DigitalShadow
Type_DigitalShadow in Type_DigitalShadow.super
all c: DigitalTwin | c.type=Type_DigitalTwin
Type_DigitalTwin in Type_DigitalTwin.super
all c: DataTrace | c.type=Type_DataTrace
Type_DataTrace in Type_DataTrace.super
all c: DataModel | c.type=Type_DataModel
Type_DataModel in Type_DataModel.super
all c: Model | c.type=Type_Model
Type_Model in Type_Model.super
all c: System | c.type=Type_System
Type_System in Type_System.super
all c: Machine | c.type=Type_Machine
Type_Machine in Type_Machine.super
}
// ***** Functions specific to CD DigitalTwin3 ***** 

// F1: Function returning all atoms of all subclasses of the class. 
fun DigitalTwinSubsCDDigitalTwin3: set Obj { DigitalTwin }
fun DataModelSubsCDDigitalTwin3: set Obj { DataModel }
fun SystemSubsCDDigitalTwin3: set Obj { System + Machine }
fun ModelSubsCDDigitalTwin3: set Obj { DataModel + Model + ProcessModel }
fun DataTraceSubsCDDigitalTwin3: set Obj { DataTrace }
fun ProcessModelSubsCDDigitalTwin3: set Obj { ProcessModel }
fun DigitalShadowSubsCDDigitalTwin3: set Obj { DigitalShadow }
fun MachineSubsCDDigitalTwin3: set Obj { Machine }

// F2: Functions returning all instances of classes implementing the interface. 

// F3: Functions returning all possible enum values for all enums in the CD. 

// F4: Creates a function for every part of a composite whole-part-relation
// that returns all linked whole/part instance pairs.
fun DigitalShadowCompFieldsCDDigitalTwin3: Obj->Obj {
  rel[DigitalTwinSubsCDDigitalTwin3,shadows] 
}
fun DataTraceCompFieldsCDDigitalTwin3: Obj->Obj {
  rel[DigitalShadowSubsCDDigitalTwin3,traces] 
}
fun ModelCompFieldsCDDigitalTwin3: Obj->Obj {
  rel[DigitalTwinSubsCDDigitalTwin3,models] 
}

// ***** Functions specific to CD DigitalTwin2 ***** 

// F1: Function returning all atoms of all subclasses of the class. 
fun DigitalTwinSubsCDDigitalTwin2: set Obj { DigitalTwin }
fun MachineSubsCDDigitalTwin2: set Obj { Machine }
fun ModelSubsCDDigitalTwin2: set Obj { Model + DataModel + ProcessModel }
fun DigitalShadowSubsCDDigitalTwin2: set Obj { DigitalShadow }
fun DataTraceSubsCDDigitalTwin2: set Obj { DataTrace }
fun DataModelSubsCDDigitalTwin2: set Obj { DataModel }
fun ProcessModelSubsCDDigitalTwin2: set Obj { ProcessModel }

// F2: Functions returning all instances of classes implementing the interface. 

// F3: Functions returning all possible enum values for all enums in the CD. 

// F4: Creates a function for every part of a composite whole-part-relation
// that returns all linked whole/part instance pairs.
fun DigitalShadowCompFieldsCDDigitalTwin2: Obj->Obj {
  rel[DigitalTwinSubsCDDigitalTwin2,shadows] 
}
fun DataTraceCompFieldsCDDigitalTwin2: Obj->Obj {
  rel[DigitalShadowSubsCDDigitalTwin2,traces] 
}
fun ModelCompFieldsCDDigitalTwin2: Obj->Obj {
  rel[DigitalTwinSubsCDDigitalTwin2,models] 
}

// Semantics predicate DigitalTwin3
pred DigitalTwin3 {

// P0: New rule for multi-instance semantics. 
ObjTypes[DigitalTwin,(Type_DigitalTwin)]
{ some DigitalTwin => some Type_DigitalTwin}
{ some DigitalTwin => some Type_DigitalTwin}

ObjTypes[DataModel,(Type_DataModel + Type_Model)]
{ some DataModel => some Type_DataModel}
{ some DataModel => some Type_DataModel}
{ some DataModel => some Type_Model}

ObjTypes[System,(Type_System)]
{ some System => some Type_System}
{ some System => some Type_System}

ObjTypes[Model,(Type_Model)]
{ some Model => some Type_Model}
{ some Model => some Type_Model}

ObjTypes[DataTrace,(Type_DataTrace)]
{ some DataTrace => some Type_DataTrace}
{ some DataTrace => some Type_DataTrace}

ObjTypes[ProcessModel,(Type_Model + Type_ProcessModel)]
{ some ProcessModel => some Type_ProcessModel}
{ some ProcessModel => some Type_Model}
{ some ProcessModel => some Type_ProcessModel}

ObjTypes[DigitalShadow,(Type_DigitalShadow)]
{ some DigitalShadow => some Type_DigitalShadow}
{ some DigitalShadow => some Type_DigitalShadow}

ObjTypes[Machine,(Type_System + Type_Machine)]
{ some Machine => some Type_Machine}
{ some Machine => some Type_System}
{ some Machine => some Type_Machine}


// Classes and attributes in DigitalTwin3
// P1: Attribute declaration

// P2: Restricts the tuples of the get relation to the attributes of the class 
// and to the role names of its partners in associations. 
ObjFNames[DigitalTwin, models + original + shadows  + none]
ObjFNames[DataModel, none ]
ObjFNames[System, traces  + none]
ObjFNames[Model, none ]
ObjFNames[DataTrace, of  + none]
ObjFNames[ProcessModel, none ]
ObjFNames[DigitalShadow, traces  + none]
ObjFNames[Machine, traces  + none]

// P3: Atoms for interfaces, singleton, and abstract classes
no System
no Model

// P4: Restrict Objects to instances of classes in the CD. 
Obj = (System + Machine + DigitalTwin + Model + DataModel + ProcessModel + DigitalShadow + DataTrace )

// Associations in DigitalTwin3
// A1: Constraints the sets of links of bidirectional associations.
BidiAssoc[DataTraceSubsCDDigitalTwin3,of,SystemSubsCDDigitalTwin3,traces]

// A2: ensures that parts of compositions have at most one whole.
Composition[ModelCompFieldsCDDigitalTwin3,ModelSubsCDDigitalTwin3]
Composition[DigitalShadowCompFieldsCDDigitalTwin3,DigitalShadowSubsCDDigitalTwin3]
Composition[DataTraceCompFieldsCDDigitalTwin3,DataTraceSubsCDDigitalTwin3]

// A3: ensures the cardinality constraints stated on the left sides of bidirec-
// tional associations, undirected association, and associations that are navigable from right
// to left are respected.
ObjLAttrib[SystemSubsCDDigitalTwin3, traces, DataTraceSubsCDDigitalTwin3, 0]

// A4: ensures the cardinality constraints stated on the right sides of
// associations, which are navigable from right to left, are respected.

// A5: ensures the cardinality constraints stated on the right sides of
// bidirectional associations, undirected association, and associations that are navigable from
// right to left are respected.
ObjLUAttrib[DigitalTwinSubsCDDigitalTwin3, original, SystemSubsCDDigitalTwin3, 1, 1]
ObjLUAttrib[DataTraceSubsCDDigitalTwin3, of, SystemSubsCDDigitalTwin3, 1, 1]
ObjLAttrib[DigitalTwinSubsCDDigitalTwin3, models, ModelSubsCDDigitalTwin3, 1]
ObjLAttrib[DigitalTwinSubsCDDigitalTwin3, shadows, DigitalShadowSubsCDDigitalTwin3, 1]
ObjLAttrib[DigitalShadowSubsCDDigitalTwin3, traces, DataTraceSubsCDDigitalTwin3, 1]

// A6: ensures the cardinality constraints stated on the left sides of associ-
// ations that are navigable from left to right are respected.
ObjL[SystemSubsCDDigitalTwin3, original, DigitalTwinSubsCDDigitalTwin3, 0]
ObjLU[ModelSubsCDDigitalTwin3, models, DigitalTwinSubsCDDigitalTwin3, 1, 1]
ObjLU[DigitalShadowSubsCDDigitalTwin3, shadows, DigitalTwinSubsCDDigitalTwin3, 1, 1]
ObjLU[DataTraceSubsCDDigitalTwin3, traces, DigitalShadowSubsCDDigitalTwin3, 1, 1]

}// Semantics predicate DigitalTwin2
pred DigitalTwin2 {

// P0: New rule for multi-instance semantics. 
ObjTypes[DigitalTwin,(Type_DigitalTwin)]
{ some DigitalTwin => some Type_DigitalTwin}
{ some DigitalTwin => some Type_DigitalTwin}

ObjTypes[Machine,(Type_Machine)]
{ some Machine => some Type_Machine}
{ some Machine => some Type_Machine}

ObjTypes[Model,(Type_Model)]
{ some Model => some Type_Model}
{ some Model => some Type_Model}

ObjTypes[DigitalShadow,(Type_DigitalShadow)]
{ some DigitalShadow => some Type_DigitalShadow}
{ some DigitalShadow => some Type_DigitalShadow}

ObjTypes[DataTrace,(Type_DataTrace)]
{ some DataTrace => some Type_DataTrace}
{ some DataTrace => some Type_DataTrace}

ObjTypes[DataModel,(Type_Model + Type_DataModel)]
{ some DataModel => some Type_DataModel}
{ some DataModel => some Type_Model}
{ some DataModel => some Type_DataModel}

ObjTypes[ProcessModel,(Type_Model + Type_ProcessModel)]
{ some ProcessModel => some Type_ProcessModel}
{ some ProcessModel => some Type_Model}
{ some ProcessModel => some Type_ProcessModel}


// Classes and attributes in DigitalTwin2
// P1: Attribute declaration

// P2: Restricts the tuples of the get relation to the attributes of the class 
// and to the role names of its partners in associations. 
ObjFNames[DigitalTwin, models + original + shadows  + none]
ObjFNames[Machine, traces  + none]
ObjFNames[Model, none ]
ObjFNames[DigitalShadow, traces  + none]
ObjFNames[DataTrace, of  + none]
ObjFNames[DataModel, none ]
ObjFNames[ProcessModel, none ]

// P3: Atoms for interfaces, singleton, and abstract classes
no Model

// P4: Restrict Objects to instances of classes in the CD. 
Obj = (Machine + DigitalTwin + Model + DataModel + ProcessModel + DigitalShadow + DataTrace )

// Associations in DigitalTwin2
// A1: Constraints the sets of links of bidirectional associations.
BidiAssoc[DataTraceSubsCDDigitalTwin2,of,MachineSubsCDDigitalTwin2,traces]

// A2: ensures that parts of compositions have at most one whole.
Composition[ModelCompFieldsCDDigitalTwin2,ModelSubsCDDigitalTwin2]
Composition[DigitalShadowCompFieldsCDDigitalTwin2,DigitalShadowSubsCDDigitalTwin2]
Composition[DataTraceCompFieldsCDDigitalTwin2,DataTraceSubsCDDigitalTwin2]

// A3: ensures the cardinality constraints stated on the left sides of bidirec-
// tional associations, undirected association, and associations that are navigable from right
// to left are respected.
ObjLAttrib[MachineSubsCDDigitalTwin2, traces, DataTraceSubsCDDigitalTwin2, 0]

// A4: ensures the cardinality constraints stated on the right sides of
// associations, which are navigable from right to left, are respected.

// A5: ensures the cardinality constraints stated on the right sides of
// bidirectional associations, undirected association, and associations that are navigable from
// right to left are respected.
ObjLUAttrib[DigitalTwinSubsCDDigitalTwin2, original, MachineSubsCDDigitalTwin2, 1, 1]
ObjLUAttrib[DataTraceSubsCDDigitalTwin2, of, MachineSubsCDDigitalTwin2, 1, 1]
ObjLAttrib[DigitalTwinSubsCDDigitalTwin2, models, ModelSubsCDDigitalTwin2, 1]
ObjLAttrib[DigitalTwinSubsCDDigitalTwin2, shadows, DigitalShadowSubsCDDigitalTwin2, 1]
ObjLAttrib[DigitalShadowSubsCDDigitalTwin2, traces, DataTraceSubsCDDigitalTwin2, 1]

// A6: ensures the cardinality constraints stated on the left sides of associ-
// ations that are navigable from left to right are respected.
ObjL[MachineSubsCDDigitalTwin2, original, DigitalTwinSubsCDDigitalTwin2, 0]
ObjLU[ModelSubsCDDigitalTwin2, models, DigitalTwinSubsCDDigitalTwin2, 1, 1]
ObjLU[DigitalShadowSubsCDDigitalTwin2, shadows, DigitalTwinSubsCDDigitalTwin2, 1, 1]
ObjLU[DataTraceSubsCDDigitalTwin2, traces, DigitalShadowSubsCDDigitalTwin2, 1, 1]

}

pred diff {
DigitalTwin3 and not DigitalTwin2
}

run diff for 7
