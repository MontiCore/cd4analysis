/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.cd4analysis.cocos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import mc.helper.IndentPrinter;
import cd4analysis.cocos.CD4ACoCoHelper;
import cd4analysis.prettyprint.CDConcretePrettyPrinter;
import de.cd4analysis._ast.ASTCDAssociation;
import de.cd4analysis._ast.ASTCDQualifier;
import de.cd4analysis._ast.CD4AnalysisNodeFactory;
import de.monticore.types._ast.ASTQualifiedName;
import de.monticore.types._ast.TypesNodeFactory;
import de.se_rwth.commons.StringTransformations;

/**
 * Tool to generate test cases for associations. Output of
 * {@link #printAssociations(List)} is a list of associations that should be
 * copied into a CD model and output of
 * {@link #printTestCases(List, ErrorMessagePrinter)} is a list of expected-errors
 * that should be copied to a junit-test case.<br>
 * <br>
 * E.g.:<br>
 * 
 * <pre>
 * {@code
 * 
 * assoc0 E -> A ;
 * 
 * assoc1 E -> B ;
 * 
 * assoc2 E -> E ;
 * 
 * 
 * Collection<String> expectedErrors = Arrays.asList(
 *   CoCoHelper.buildErrorMsg(errorCode, "Association assoc0 (E -> A) is invalid, because an association's source may not be an Enumeration."),
 *   CoCoHelper.buildErrorMsg(errorCode, "Association assoc1 (E -> B) is invalid, because an association's source may not be an Enumeration."),
 *   CoCoHelper.buildErrorMsg(errorCode, "Association assoc2 (E -> E) is invalid, because an association's source may not be an Enumeration."),
 *   // ...
 * )}
 * </pre>
 * 
 * {@link #allDirections(ASTCDAssociation)} should be applied first, because
 * other methods may rely on a set direction. The other functions can be applied
 * to generate specific test cases. <br>
 * <br>
 * In the output the types A, B, E and I are expected to exist in the model and
 * have the following semantics A = class, B = class, E = enum, I = interface
 * 
 * @author Robert Heim
 */
public class AssocTestGenerator {
  
  public static void main(String[] args) {
    ASTCDAssociation assoc = CD4AnalysisNodeFactory.createASTCDAssociation();
    
    List<ASTCDAssociation> allPossibilities = allDirections(assoc)
        .stream()
        .flatMap(a -> allTypeCombinations(a, false).stream())
        // .flatMap(a -> allTypedQualifierPositions(a, true).stream())
        .collect(Collectors.toList());
    
    printAssociations(allPossibilities);
    printTestCases(allPossibilities, new ErrorMessagePrinter() {
      
      @Override
      public String print(ASTCDAssociation assoc) {
        String msg = "Association %s is invalid, because an association's source may not be an Enumeration.";
        return String.format(msg, CD4ACoCoHelper.printAssociation(assoc));
      }
    });
  }
  
  public static void printAssociations(List<ASTCDAssociation> allPossibilities) {
    IndentPrinter printer = new IndentPrinter();
    CDConcretePrettyPrinter p = new CDConcretePrettyPrinter();
    int c = 0;
    for (ASTCDAssociation a : allPossibilities) {
      a.setName("assoc" + c);
      p.prettyPrint(a, printer);
      c++;
    }
    System.out.println(printer.getContent());
  }
  
  private static interface ErrorMessagePrinter
  {
    public String print(ASTCDAssociation assoc);
  }
  
  public static void printTestCases(List<ASTCDAssociation> allPossibilities,
      ErrorMessagePrinter errorMsgPrinter) {
    int c = 0;
    System.out.println("Collection<String> expectedErrors = Arrays.asList(");
    
    for (ASTCDAssociation a : allPossibilities) {
      a.setName("assoc" + c);
      System.out.println("  CoCoHelper.buildErrorMsg(errorCode, \""
          + errorMsgPrinter.print(a)
          + "\"),");
      
      c++;
    }
    System.out.println(")");
  }
  
  /**
   * TODO: Write me!
   * 
   * @param assoc
   * @param valid if true enums will not be a source, if false only enum will be
   * sources
   * @return
   */
  private static List<ASTCDAssociation> allTypeCombinations(ASTCDAssociation assoc, boolean valid) {
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
   * Generates possible type-qualifiers combinations for the given association.
   * 
   * @param assoc
   * @param valid if true only valid positions are generated (e.g. A <- [T] B,
   * but not A [T] <- B or A [T] <- [T] B, false only invalid ones are generated
   * @return
   */
  private static List<ASTCDAssociation> allTypedQualifierPositions(ASTCDAssociation assoc,
      boolean valid) {
    List<ASTCDAssociation> re = new ArrayList<>();
    
    ASTCDQualifier ql = CD4AnalysisNodeFactory.createASTCDQualifier();
    ql.setName("LeftTypedQualifier");
    ASTCDQualifier qr = CD4AnalysisNodeFactory.createASTCDQualifier();
    qr.setName("RightTypedQualifier");
    
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
   * Capitalizes all role names to make them invalid.
   * 
   * @param assocs
   * @return
   */
  private List<ASTCDAssociation> capitalizeAllRoles(List<ASTCDAssociation> assocs) {
    List<ASTCDAssociation> re = new ArrayList<>();
    for (ASTCDAssociation a : assocs) {
      a = a.deepClone();
      if (a.getLeftRole().isPresent()) {
        a.setLeftRole(StringTransformations.capitalize(a.getLeftRole().get()));
      }
      if (a.getRightRole().isPresent()) {
        a.setRightRole(StringTransformations.capitalize(a.getRightRole().get()));
      }
      re.add(a);
    }
    return re;
  }
  
  /**
   * TODO: Write me!
   * 
   * @param assoc
   * @param valid if true only valid positions are used , e.g. A -> (roleRight)
   * B, if false only invalid ones are used, e.g. A (roleLeft) -> B.
   * @return
   */
  private static List<ASTCDAssociation> allRolePositions(ASTCDAssociation assoc,
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
  
  private static List<ASTCDAssociation> allDirections(ASTCDAssociation assoc) {
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
