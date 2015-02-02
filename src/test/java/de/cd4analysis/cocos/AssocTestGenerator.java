/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.cd4analysis.cocos;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import cd4analysis.cocos.CD4ACoCoHelper;
import de.cd4analysis._ast.ASTCDAssociation;
import de.cd4analysis._ast.ASTCardinality;
import de.cd4analysis._ast.CD4AnalysisNodeFactory;
import de.cd4analysis.cocos.AssocTestGeneratorTool.ErrorMessagePrinter;

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
 *         //.flatMap(a -> allTypedQualifierPositions(a, validModel).stream())
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
public class AssocTestGenerator {
  
  /**
   * 0xCD4A0019, 0xCD4A0020
   * 
   * @param valid whether the associations should be valid or not.
   * @param leftQualifier
   * @param rightQualifier
   */
  public static void generateQualifiedAssocTests(boolean valid, String leftQualifier,
      String rightQualifier, ErrorMessagePrinter errorMsgPrinter) {
    ASTCDAssociation assoc = CD4AnalysisNodeFactory.createASTCDAssociation();
    List<ASTCDAssociation> allPossibilities = AssocTestGeneratorTool
        .allDirections(assoc)
        .stream()
        .flatMap(a -> AssocTestGeneratorTool.allTypeCombinations(a, true).stream())
        .flatMap(
            a -> AssocTestGeneratorTool.allQualifierPositions(a, valid, leftQualifier,
                rightQualifier).stream())
        .collect(Collectors.toList());
    
    String modelContents = AssocTestGeneratorTool.printAssociations(allPossibilities);
    System.out.println(modelContents);
    
    System.out.println("Collection<String> expectedErrors = Arrays.asList(");
    
    AssocTestGeneratorTool.printTestCases(allPossibilities, errorMsgPrinter);
    
    System.out.println(");");
  }
  
  /**
   * 0xCD4AC0017
   */
  public static void generateInvalidRoleNamesTests() {
    ASTCDAssociation assoc = CD4AnalysisNodeFactory.createASTCDAssociation();
    List<ASTCDAssociation> allPossibilities = AssocTestGeneratorTool
        .allDirections(assoc)
        .stream()
        .flatMap(a -> AssocTestGeneratorTool.allTypeCombinations(a, true).stream())
        .flatMap(a -> AssocTestGeneratorTool.allRolePositions(a, true).stream())
        .map(a -> AssocTestGeneratorTool.capitalizeRoles(a))
        .collect(Collectors.toList());
    
    String modelContents = AssocTestGeneratorTool.printAssociations(allPossibilities);
    System.out.println(modelContents);
    
    System.out.println("Collection<String> expectedErrors = Arrays.asList(");
    
    AssocTestGeneratorTool.printTestCases(allPossibilities, new ErrorMessagePrinter() {
      @Override
      public String print(ASTCDAssociation assoc) {
        String msg = "Role %s of association %s must start in lower-case.";
        String invalidRoleName = null;
        if (assoc.getLeftRole().isPresent()) {
          invalidRoleName = assoc.getLeftRole().get();
        }
        else {
          if (assoc.getRightRole().isPresent()) {
            invalidRoleName = assoc.getRightRole().get();
          }
        }
        if (null == invalidRoleName) {
          throw new RuntimeException("At least one of the roles must be set.");
        }
        return "  CoCoHelper.buildErrorMsg(errorCode, \""
            + String.format(msg, invalidRoleName, CD4ACoCoHelper.printAssociation(assoc)) + "\"),";
      }
    });
    System.out.println(");");
  }
  
  public static void main(String[] args) {
    
    
  }
  
  public static void generateInvalidCompositeCardinalities() {
    ErrorMessagePrinter errorMessagePrinter = new ErrorMessagePrinter() {
      @Override
      public String print(ASTCDAssociation assoc) {
        String msg = "The composite of composition %s has an invalid cardinality %s larger than one.";
        ASTCardinality cardinality = null;
        
        if (assoc.isRightToLeft()) {
          if (assoc.getRightCardinality().isPresent()) {
            cardinality = assoc.getRightCardinality().get();
          }
        }
        else {
          // all other directions are interpreted as: left side is the
          // composite, right side are the elements.
          if (assoc.getLeftCardinality().isPresent()) {
            cardinality = assoc.getLeftCardinality().get();
          }
        }
        
        String invalidCardinalityStr = null;
        if (cardinality != null) {
          if (cardinality.isMany()) {
            invalidCardinalityStr = "[*]";
          }
          else if (cardinality.isOneToMany()) {
            invalidCardinalityStr = "[1..*]";
          }
        }
        if (null != invalidCardinalityStr) {
          return "  CoCoHelper.buildErrorMsg(errorCode, \""
              + String.format(msg, CD4ACoCoHelper.printAssociation(assoc), invalidCardinalityStr)
              + "\"),";
          
        }
        // all other cases are valid:
        // if no cardinality was set on the composite's side it is interpreted
        // as 1. Correct cardinalities on the composite's side are [1] and
        // [0..1].
        return "";
      }
    };
    Predicate<ASTCDAssociation> isInvalidCompositeCardinality = new Predicate<ASTCDAssociation>() {
      @Override
      public boolean test(ASTCDAssociation assoc) {
        ASTCardinality cardinality = null;
        
        if (assoc.isRightToLeft()) {
          if (assoc.getRightCardinality().isPresent()) {
            cardinality = assoc.getRightCardinality().get();
          }
        }
        else {
          // all other directions are interpreted as: left side is the
          // composite, right side are the elements.
          if (assoc.getLeftCardinality().isPresent()) {
            cardinality = assoc.getLeftCardinality().get();
          }
        }
        if (cardinality != null) {
          if (cardinality.isMany()) {
            return true;
          }
          else if (cardinality.isOneToMany()) {
            return true;
          }
        }
        // other cases are valid
        return false;
      }
    };
    ASTCDAssociation assoc = CD4AnalysisNodeFactory.createASTCDAssociation();
    assoc.setComposition(true);
    List<ASTCDAssociation> allPossibilities = AssocTestGeneratorTool.allDirections(assoc)
        .stream()
        .flatMap(a -> AssocTestGeneratorTool.allTypeCombinations(a, true).stream())
        .flatMap(a -> AssocTestGeneratorTool.allCardinalityCombinations(a).stream())
        .filter(isInvalidCompositeCardinality)
        .collect(Collectors.toList());
    String modelContents = AssocTestGeneratorTool.printAssociations(allPossibilities);
    System.out.println(modelContents);
    
    System.out.println("Collection<String> expectedErrors = Arrays.asList(");
    AssocTestGeneratorTool.printTestCases(allPossibilities, errorMessagePrinter);
    
    System.out.println(");");
    
  }
}
