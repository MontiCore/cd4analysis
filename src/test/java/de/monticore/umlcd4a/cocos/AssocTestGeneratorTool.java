/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import mc.helper.IndentPrinter;
import de.cd4analysis._ast.ASTCDAssociation;
import de.cd4analysis._ast.ASTCDClass;
import de.cd4analysis._ast.ASTCDEnum;
import de.cd4analysis._ast.ASTCDInterface;
import de.cd4analysis._ast.ASTCDQualifier;
import de.cd4analysis._ast.ASTCardinality;
import de.cd4analysis._ast.ASTModifier;
import de.cd4analysis._ast.CD4AnalysisNodeFactory;
import de.monticore.types._ast.ASTQualifiedName;
import de.monticore.types._ast.TypesNodeFactory;
import de.monticore.umlcd4a.prettyprint.CDConcretePrettyPrinter;
import de.se_rwth.commons.StringTransformations;

/**
 * Tool to generate test cases for associations. Output of
 * {@link #printAssociations(List)} is a list of associations that should be
 * copied into a CD model and output of
 * {@link #printTestCases(List, ErrorMessagePrinter)} is a list of
 * expected-errors that should be copied to a junit-test case.<br>
 * <br>
 * {@link #allDirections(ASTCDAssociation)} should be applied first, because
 * other methods may rely on a set direction. If you do not call {
 * {@link #allTypeCombinations(ASTCDAssociation, boolean)} you should set the
 * left and right reference type of the association before generating the
 * directions.The other functions can be applied to generate specific test
 * cases.<br>
 * Example:<br>
 * 
 * <pre>
 * {@code
 * boolean validModel = false;
 * ASTCDAssociation assoc = CD4AnalysisNodeFactory.createASTCDAssociation();
 * List<ASTCDAssociation> allPossibilities = allDirections(assoc)
 *     .stream()
 *       .flatMap(a -> allTypeCombinations(a, validModel).stream())
 *         //.flatMap(a -> allModifierCombinations(a, validModel).stream())
 *         //.flatMap(a -> allRolePositions(a, validModel).stream())
 *         //.flatMap(a -> allCardinalityCombinations(a).stream())
 *         //.flatMap(a -> allQualifierPositions(a, validModel).stream())
 *     .collect(Collectors.toList());
 *     
 * printAssociations(allPossibilities);
 * output:
 *   assoc0 E -> A ;
 * 
 *   assoc1 E -> B ;
 * 
 *   assoc2 E -> E ;
 * 
 * printTestCases(allPossibilities, new ErrorMessagePrinter() {
 *      {@literal @}Override
 *      public String print(ASTCDAssociation assoc) {
 *        String msg = "Association %s is invalid, because an association's source may not be an Enumeration.";
 *        return "  CoCoHelper.buildErrorMsg(errorCode, \""
 *            + String.format(msg, CD4ACoCoHelper.printAssociation(assoc)) + "\"),";
 *      }
 *    });
 * 
 * output:
 *   CoCoHelper.buildErrorMsg(errorCode, "Association assoc0 (E -> A) is invalid, because an association's source may not be an Enumeration."),
 *   CoCoHelper.buildErrorMsg(errorCode, "Association assoc1 (E -> B) is invalid, because an association's source may not be an Enumeration."),
 *   CoCoHelper.buildErrorMsg(errorCode, "Association assoc2 (E -> E) is invalid, because an association's source may not be an Enumeration."),
 *   // ...
 * </pre>
 * 
 * @author Robert Heim
 */
public class AssocTestGeneratorTool {
  
  public static String printAssociations(List<ASTCDAssociation> allPossibilities) {
    IndentPrinter printer = new IndentPrinter();
    CDConcretePrettyPrinter p = new CDConcretePrettyPrinter();
    int c = 0;
    
    printer.indent();
    
    ASTCDClass classA = CD4AnalysisNodeFactory.createASTCDClass();
    classA.setName("A");
    p.prettyPrint(classA, printer);
    
    ASTCDClass classB = CD4AnalysisNodeFactory.createASTCDClass();
    classB.setName("B");
    p.prettyPrint(classB, printer);
    
    ASTCDEnum e = CD4AnalysisNodeFactory.createASTCDEnum();
    e.setName("E");
    p.prettyPrint(e, printer);
    
    ASTCDInterface i = CD4AnalysisNodeFactory.createASTCDInterface();
    i.setName("I");
    p.prettyPrint(i, printer);
    
    for (ASTCDAssociation a : allPossibilities) {
      if (a.isComposition()) {
        a.setName("comp" + c);
      }
      else {
        a.setAssociation(true);
        a.setName("assoc" + c);
      }
      
      p.prettyPrint(a, printer);
      c++;
    }
    
    return printer.getContent();
  }
  
  public static interface ErrorMessagePrinter
  {
    public String print(ASTCDAssociation assoc);
  }
  
  public static void printTestCases(List<ASTCDAssociation> allPossibilities,
      ErrorMessagePrinter errorMsgPrinter) {
    int c = 0;
    for (ASTCDAssociation a : allPossibilities) {
      if (a.isComposition()) {
        a.setName("comp" + c);
      }
      else {
        a.setAssociation(true);
        a.setName("assoc" + c);
      }
      System.out.println(errorMsgPrinter.print(a));
      c++;
    }
  }
  
  /**
   * includes "no/package visibility"
   */
  private static String[] visibilities = { "private", "protected", "", "public" };
  
  private static String[] modifieres = { "abstract", "final", "static", "derived" };
  
  /**
   * TODO: Write me!
   * 
   * @param assoc
   * @param valid if true only valid modifier combinations are generated, only
   * invalid combinations otherwise.
   * @return
   */
  public static List<ASTCDAssociation> allModifierCombinations(ASTCDAssociation assoc,
      boolean valid) {
    List<ASTCDAssociation> re = new ArrayList<>();
    Set<Set<String>> modifierSet = valid
        ? getValidModifierCombinations()
        : getInvalidModifierCombinations();
    
    for (Set<String> modifiers : modifierSet) {
      // only left side
      ASTCDAssociation clone = assoc.deepClone();
      setModifiers(clone, modifiers, "left");
      re.add(clone);
      // only rightside
      clone = assoc.deepClone();
      setModifiers(clone, modifiers, "right");
      re.add(clone);
      for (Set<String> modifiers2 : modifierSet) {
        // both sides
        clone = assoc.deepClone();
        setModifiers(clone, modifiers, "left");
        setModifiers(clone, modifiers2, "right");
        re.add(clone);
      }
    }
    
    return re;
  }
  
  private static Set<Set<String>> getValidModifierCombinations() {
    Set<Set<String>> result = new HashSet<>();
    
    for (String v : visibilities) {
      // only visibility, note that this includes "no visibility".
      Set<String> currentCombination = new HashSet<String>();
      currentCombination.add(v);
      result.add(currentCombination);
      addModifierCombinationRecursive(result, currentCombination, 0);
    }
    
    // remove invalid ones
    Iterator<Set<String>> it = result.iterator();
    while (it.hasNext()) {
      Set<String> comb = it.next();
      for (Set<String> invalidCombination : getInvalidModifierCombinations()) {
        if (comb.containsAll(invalidCombination)) {
          it.remove();
          break;
        }
      }
    }
    
    return result;
  }
  
  private static void addModifierCombinationRecursive(Set<Set<String>> result,
      Set<String> currentParentCombination, int depth) {
    // depth indicates the count of modifiers
    if (depth < modifieres.length) {
      depth++;
      for (String m : modifieres) {
        Set<String> newCombination = new HashSet<>(currentParentCombination);
        newCombination.add(m);
        result.add(newCombination);
        addModifierCombinationRecursive(result, newCombination, depth);
      }
    }
  }
  
  private static Set<Set<String>> getInvalidModifierCombinations() {
    
    String[][] invalidModifierCombinationsArr = {
        { "abstract", "private" },
        { "abstract", "final" },
        { "abstract", "static" },
        { "derived", "final" },
        { "derived", "private" }
    };
    List<String[]> invalidModifierCombinations = new ArrayList<>(
        Arrays.asList(invalidModifierCombinationsArr));
    // more invalid: two different visibilities
    for (String v1 : visibilities) {
      for (String v2 : visibilities) {
        if (!v1.equals(v2)) {
          String[] invalid = { v1, v2 };
          invalidModifierCombinations.add(invalid);
        }
      }
    }
    
    Set<Set<String>> re = new HashSet<>();
    for (String[] comb : invalidModifierCombinations) {
      re.add(new HashSet<String>(Arrays.asList(comb)));
    }
    return re;
  }
  
  /**
   * TODO: Write me!
   * 
   * @param modifierObj
   * @param modifiers a subset of { "abstract", "final", "static", "derived",
   * "private", "protected", "", "public"}
   */
  private static void setModifiers(ASTCDAssociation assoc, Set<String> modifiers, String side) {
    ASTModifier modifierObj = CD4AnalysisNodeFactory.createASTModifier();
    for (String modifier : modifiers) {
      switch (modifier) {
        case "abstract":
          modifierObj.setAbstract(true);
          break;
        case "final":
          modifierObj.setFinal(true);
          break;
        case "static":
          modifierObj.setStatic(true);
          break;
        case "derived":
          modifierObj.setDerived(true);
          break;
        case "private":
          modifierObj.setPrivate(true);
          break;
        case "protected":
          modifierObj.setProtected(true);
          break;
        case "public":
          modifierObj.setPublic(true);
          break;
      }
    }
    switch (side) {
      case "left":
        assoc.setLeftModifier(modifierObj);
        break;
      case "right":
        assoc.setRightModifier(modifierObj);
        break;
    }
  }
  
  /**
   * TODO: Write me!
   * 
   * @param assoc
   * @return
   */
  public static List<ASTCDAssociation> allCardinalityCombinations(ASTCDAssociation assoc) {
    List<ASTCDAssociation> re = new ArrayList<>();
    String[] cardinalities = { "0..1", "1", "1..*", "*" };
    
    for (String c1 : cardinalities) {
      // only left side
      ASTCDAssociation clone = assoc.deepClone();
      setCardinality(clone, c1, "left");
      re.add(clone);
      // only rightside
      clone = assoc.deepClone();
      setCardinality(clone, c1, "right");
      re.add(clone);
      for (String c2 : cardinalities) {
        // both sides
        clone = assoc.deepClone();
        setCardinality(clone, c1, "left");
        setCardinality(clone, c2, "right");
        re.add(clone);
      }
    }
    return re;
  }
  
  /**
   * TODO: Write me!
   * 
   * @param assoc
   * @param cardinality one of { "0..1", "1", "0..1", "*" }
   * @param side one of {"left", "right"}
   */
  private static void setCardinality(ASTCDAssociation assoc, String cardinality, String side) {
    ASTCardinality c = CD4AnalysisNodeFactory.createASTCardinality();
    // since java 7 we can switch strings ;)
    switch (cardinality) {
      case "0..1":
        c.setOptional(true);
        break;
      case "1":
        c.setOne(true);
        break;
      case "1..*":
        c.setOneToMany(true);
        break;
      case "*":
        c.setMany(true);
        break;
    }
    switch (side) {
      case "left":
        assoc.setLeftCardinality(c);
        break;
      case "right":
        assoc.setRightCardinality(c);
        break;
    }
  }
  
  /**
   * TODO: Write me!
   * 
   * @param assoc
   * @param valid if true enums will not be a source, if false only enum will be
   * sources
   * @return
   */
  public static List<ASTCDAssociation> allTypeCombinations(ASTCDAssociation assoc, boolean valid) {
    List<ASTCDAssociation> re = new ArrayList<>();
    // A = class, B = class, E = enum, I = interface
    String[] types = { "A", "B", "E", "I" };
    for (String tLeft : types) {
      for (String tRight : types) {
        boolean invalidCase = false;
        
        if (tLeft.equals("E")) {
          if (assoc.isLeftToRight() || assoc.isSimple() || assoc.isBidirectional()) {
            invalidCase = true;
          }
        }
        
        if (tRight.equals("E")) {
          if (assoc.isRightToLeft() || assoc.isSimple() || assoc.isBidirectional()) {
            
            invalidCase = true;
          }
        }
        
        if ((valid && !invalidCase) || (!valid && invalidCase)) {
          ASTCDAssociation clone = assoc.deepClone();
          ASTQualifiedName leftReferenceName = TypesNodeFactory.createASTQualifiedName(Arrays
              .asList(tLeft));
          ASTQualifiedName rightReferenceName = TypesNodeFactory.createASTQualifiedName(Arrays
              .asList(tRight));
          clone.setLeftReferenceName(leftReferenceName);
          clone.setRightReferenceName(rightReferenceName);
          re.add(clone);
        }
      }
    }
    return re;
  }
  
  /**
   * Generates possible qualifiers combinations for the given association.
   * 
   * @param assoc
   * @param valid if true only valid positions are generated (e.g. A <- [T] B,
   * but not A [T] <- B or A [T] <- [T] B, false only invalid ones are generated
   * @return
   */
  public static List<ASTCDAssociation> allQualifierPositions(ASTCDAssociation assoc,
      boolean valid, String leftQualifier, String rightQualifier) {
    List<ASTCDAssociation> re = new ArrayList<>();
    
    ASTCDQualifier ql = CD4AnalysisNodeFactory.createASTCDQualifier();
    ql.setName(leftQualifier);
    ASTCDQualifier qr = CD4AnalysisNodeFactory.createASTCDQualifier();
    qr.setName(rightQualifier);
    
    if (assoc.isBidirectional() || assoc.isSimple()) {
      if (valid) {
        ASTCDAssociation clone = assoc.deepClone();
        clone.setLeftQualifier(ql.deepClone());
        clone.setRightQualifier(qr.deepClone());
        re.add(clone);
        
        clone = assoc.deepClone();
        clone.setLeftQualifier(ql.deepClone());
        re.add(clone);
        
        clone = assoc.deepClone();
        clone.setRightQualifier(qr.deepClone());
        re.add(clone);
      }
      // no invalids
    }
    if (assoc.isRightToLeft()) {
      if (valid) {
        ASTCDAssociation clone = assoc.deepClone();
        clone.setRightQualifier(qr.deepClone());
        re.add(clone);
      }
      else {
        ASTCDAssociation clone = assoc.deepClone();
        clone.setLeftQualifier(ql.deepClone());
        re.add(clone);
        
        clone = assoc.deepClone();
        clone.setLeftQualifier(ql.deepClone());
        clone.setRightQualifier(qr.deepClone());
        re.add(clone);
      }
    }
    
    if (assoc.isLeftToRight()) {
      if (valid) {
        ASTCDAssociation clone = assoc.deepClone();
        clone.setLeftQualifier(ql.deepClone());
        re.add(clone);
      }
      else {
        ASTCDAssociation clone = assoc.deepClone();
        clone.setRightQualifier(qr.deepClone());
        re.add(clone);
        
        clone = assoc.deepClone();
        clone.setLeftQualifier(ql.deepClone());
        clone.setRightQualifier(qr.deepClone());
        re.add(clone);
      }
    }
    return re;
  }
  
  /**
   * clones the assoc and capitalizes all role names to make them invalid.
   * 
   * @param assoc the assoc
   * @return the new assoc with uppercase role names
   */
  public static ASTCDAssociation capitalizeRoles(ASTCDAssociation a) {
    a = a.deepClone();
    if (a.getLeftRole().isPresent()) {
      a.setLeftRole(StringTransformations.capitalize(a.getLeftRole().get()));
    }
    if (a.getRightRole().isPresent()) {
      a.setRightRole(StringTransformations.capitalize(a.getRightRole().get()));
    }
    return a;
  }
  
  /**
   * TODO: Write me!
   * 
   * @param assoc
   * @param valid if true only valid positions are used , e.g. A -> (roleRight)
   * B, if false only invalid ones are used, e.g. A (roleLeft) -> B.
   * @return
   */
  public static List<ASTCDAssociation> allRolePositions(ASTCDAssociation assoc,
      boolean valid) {
    List<ASTCDAssociation> re = new ArrayList<>();
    
    String leftRole = "leftRole";
    String rightRole = "rightRole";
    
    if (assoc.isBidirectional() || assoc.isSimple()) {
      if (valid) {
        ASTCDAssociation clone = assoc.deepClone();
        clone.setLeftRole(leftRole);
        clone.setRightRole(rightRole);
        re.add(clone);
        
        clone = assoc.deepClone();
        clone.setLeftRole(leftRole);
        re.add(clone);
        
        clone = assoc.deepClone();
        clone.setRightRole(rightRole);
        re.add(clone);
      }
      // no invalids
    }
    if (assoc.isRightToLeft()) {
      if (valid) {
        ASTCDAssociation clone = assoc.deepClone();
        clone.setLeftRole(leftRole);
        re.add(clone);
      }
      else {
        ASTCDAssociation clone = assoc.deepClone();
        clone.setRightRole(rightRole);
        re.add(clone);
        
        clone = assoc.deepClone();
        clone.setLeftRole(leftRole);
        clone.setRightRole(rightRole);
        re.add(clone);
      }
    }
    
    if (assoc.isLeftToRight()) {
      if (valid) {
        ASTCDAssociation clone = assoc.deepClone();
        clone.setRightRole(rightRole);
        re.add(clone);
      }
      else {
        ASTCDAssociation clone = assoc.deepClone();
        clone.setLeftRole(leftRole);
        re.add(clone);
        
        clone = assoc.deepClone();
        clone.setLeftRole(leftRole);
        clone.setRightRole(rightRole);
        re.add(clone);
      }
    }
    return re;
  }
  
  public static List<ASTCDAssociation> allDirections(ASTCDAssociation assoc) {
    List<ASTCDAssociation> re = new ArrayList<>();
    
    // ->
    ASTCDAssociation clone = assoc.deepClone();
    clone.setLeftToRight(true);
    re.add(clone);
    
    // <-
    clone = assoc.deepClone();
    clone.setRightToLeft(true);
    re.add(clone);
    
    // <->
    clone = assoc.deepClone();
    clone.setBidirectional(true);
    re.add(clone);
    
    // --
    clone = assoc.deepClone();
    clone.setSimple(true);
    re.add(clone);
    return re;
  }

}