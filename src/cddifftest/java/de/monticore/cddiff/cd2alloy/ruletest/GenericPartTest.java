/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.cd2alloy.ruletest;

import de.monticore.cd2alloy.generator.CD2AlloyGenerator;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.AbstractTest;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertTrue;

/**
 * Tests the P1 rule using the examples from the technical report
 *
 */
public class GenericPartTest extends AbstractTest {

  ASTCDCompilationUnit mvAst = parseModel("src/cddifftest/resources/de/monticore/cddiff/VehicleManagement/cd1.cd");

  ASTCDCompilationUnit m1Ast = parseModel("src/cddifftest/resources/de/monticore/cddiff/Manager/Employees1.cd");

  ASTCDCompilationUnit m2Ast = parseModel("src/cddifftest/resources/de/monticore/cddiff/Manager/Employees2.cd");

  String genericPart = " // ***** Generic Part ***** " + System.lineSeparator() +
      " " + System.lineSeparator() +
      "// The abstract signatures FName, Obj, Val, and EnumVal. " + System.lineSeparator() +
      "abstract sig Obj { get: FName -> {Obj + Val + EnumVal} } " + System.lineSeparator() +
      "abstract sig FName {} " + System.lineSeparator() +
      "abstract sig Val {} " + System.lineSeparator() +
      "abstract sig EnumVal {} " + System.lineSeparator() +
      " " + System.lineSeparator() +
      "// Predicates used to specify cardinality constraints for navigable association" + System.lineSeparator() +
      "// ends and for association ends of undirected associations." + System.lineSeparator() +
      "pred ObjAttrib[objs: set Obj, fName: one FName," + System.lineSeparator() +
      " fType: set {Obj + Val + EnumVal}] {" + System.lineSeparator() +
      " objs.get[fName] in fType" + System.lineSeparator() +
      " all o: objs| one o.get[fName] }" + System.lineSeparator() +
      "" + System.lineSeparator() +
      "pred ObjFNames[objs: set Obj, fNames:set FName]{" + System.lineSeparator() +
      " no objs.get[FName - fNames] }" + System.lineSeparator() +
      "" + System.lineSeparator() +
      "pred BidiAssoc[left: set Obj, lFName:one FName," + System.lineSeparator() +
      " right: set Obj, rFName:one FName] {" + System.lineSeparator() +
      " all l: left | all r: l.get[lFName] | l in r.get[rFName]" + System.lineSeparator() +
      " all r: right | all l: r.get[rFName] | r in l.get[lFName] }" + System.lineSeparator() +
      "" + System.lineSeparator() +
      "pred Composition[compos: Obj->Obj, right: set Obj] {" + System.lineSeparator() +
      " all r: right | lone compos.r }" + System.lineSeparator() +
      " " + System.lineSeparator() +
      "fun rel[wholes: set Obj, fn: FName] : Obj->Obj {" + System.lineSeparator() +
      " {o1:Obj,o2:Obj|o1->fn->o2 in wholes <: get} } " + System.lineSeparator() +
      "" + System.lineSeparator() +
      "// Predicates used to specify cardinality constraints for navigable association" + System.lineSeparator() +
      "// ends and for association ends of undirected associations. " + System.lineSeparator() +
      "pred ObjUAttrib[objs: set Obj, fName:one FName, fType:set Obj, up: Int] {" + System.lineSeparator() +
      " objs.get[fName] in fType" + System.lineSeparator() +
      " all o: objs| (#o.get[fName] =< up) } " + System.lineSeparator() +
      "" + System.lineSeparator() +
      "pred ObjLAttrib[objs: set Obj, fName: one FName, fType: set Obj, low: Int] {" + System.lineSeparator() +
      " objs.get[fName] in fType" + System.lineSeparator() +
      " all o: objs | (#o.get[fName] >= low) }" + System.lineSeparator() +
      "" + System.lineSeparator() +
      "pred ObjLUAttrib[objs:set Obj, fName:one FName, fType:set Obj," + System.lineSeparator() +
      " low: Int, up: Int] {" + System.lineSeparator() +
      " ObjLAttrib[objs, fName, fType, low]" + System.lineSeparator() +
      " ObjUAttrib[objs, fName, fType, up] }" + System.lineSeparator() +
      "" + System.lineSeparator() +
      "// Parametrized predicates used to specify cardinality constraints for non-" + System.lineSeparator() +
      "// navigable association ends. " + System.lineSeparator() +
      "pred ObjL[objs: set Obj, fName:one FName, fType: set Obj, low: Int] {" + System.lineSeparator() +
      " all r: objs | # { l: fType | r in l.get[fName]} >= low } " + System.lineSeparator() +
      "" + System.lineSeparator() +
      "pred ObjU[objs: set Obj, fName:one FName, fType: set Obj, up: Int] {" + System.lineSeparator() +
      " all r: objs | # { l: fType | r in l.get[fName]} =< up } " + System.lineSeparator() +
      "" + System.lineSeparator() +
      "pred ObjLU[objs: set Obj, fName:one FName, fType: set Obj," + System.lineSeparator() +
      " low: Int, up: Int] {" + System.lineSeparator() +
      " ObjL[objs, fName, fType, low]" + System.lineSeparator() +
      " ObjU[objs, fName, fType, up] }" + System.lineSeparator() +
      "" + System.lineSeparator() +
      "fact NonEmptyInstancesOnly {" + System.lineSeparator() +
      " some Obj" + System.lineSeparator() +
      "}";

  private void checkGenericPart(String result, String expectedResult) {
    // Remove module name
    result = result.replaceAll("module .*[_module]", "");

    // Remove comments
    result = result.replaceAll("//.*" + System.lineSeparator(), "");
    expectedResult = expectedResult.replaceAll("//.*" + System.lineSeparator(), "");

    // Remove all line breaks
    result = result.replaceAll("\\R", "");
    expectedResult = expectedResult.replaceAll("\\R", "");

    // All Parsed alloy modules must have the generic part as prefix
    assertTrue(result.startsWith(expectedResult));
  }

  @Test
  public void testGenericPart_MV() {
    // Create singleton set for CD
    Set<ASTCDCompilationUnit> asts = new HashSet<>();
    asts.add(mvAst);

    // Generate Module
    String module = CD2AlloyGenerator.generateModule(asts);

    // Check if generic part was correctly created
    checkGenericPart(module, genericPart);
  }

  @Test
  public void testGenericPart_cd2v1() {
    // Create singleton set for CD
    Set<ASTCDCompilationUnit> asts = new HashSet<>();
    asts.add(m1Ast);

    // Generate Module
    String module = CD2AlloyGenerator.generateModule(asts);

    // Check if generic part was correctly created
    checkGenericPart(module, genericPart);

  }

  @Test
  public void testGenericPart_cd2v2() {
    // Create singleton set for CD
    Set<ASTCDCompilationUnit> asts = new HashSet<>();
    asts.add(m2Ast);

    // Generate Module
    String module = CD2AlloyGenerator.generateModule(asts);

    // Check if generic part was correctly created
    checkGenericPart(module, genericPart);
  }
}
