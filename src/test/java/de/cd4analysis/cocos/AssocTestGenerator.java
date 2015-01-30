/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.cd4analysis.cocos;

import java.util.List;
import java.util.stream.Collectors;

import cd4analysis.cocos.CD4ACoCoHelper;
import de.cd4analysis._ast.ASTCDAssociation;
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
   * @param leftTypedQualifier
   * @param rightTypedQualifier
   */
  public static void generateTypedQualifiedAssocTests(String leftTypedQualifier,
      String rightTypedQualifier) {
    ASTCDAssociation assoc = CD4AnalysisNodeFactory.createASTCDAssociation();
    List<ASTCDAssociation> allPossibilities = AssocTestGeneratorTool
        .allDirections(assoc)
        .stream()
        .flatMap(a -> AssocTestGeneratorTool.allTypeCombinations(a, true).stream())
        .flatMap(
            a -> AssocTestGeneratorTool.allTypedQualifierPositions(a, true, leftTypedQualifier,
                rightTypedQualifier).stream())
        .collect(Collectors.toList());
    
    String modelContents = AssocTestGeneratorTool.printAssociations(allPossibilities);
    System.out.println(modelContents);
    
    System.out.println("Collection<String> expectedErrors = Arrays.asList(");
    
    AssocTestGeneratorTool.printTestCases(allPossibilities, new ErrorMessagePrinter() {
      @Override
      public String print(ASTCDAssociation assoc) {
        String msg = "The type %s of the typed qualified association %s could not be found. Only external datatypes and types defined within the classdiagram may be used.";
        String undefinedType = null;
        if (assoc.getLeftQualifier().isPresent()) {
          undefinedType = assoc.getLeftQualifier().get().getName();
        }
        else {
          if (assoc.getRightQualifier().isPresent()) {
            undefinedType = assoc.getRightQualifier().get().getName();
          }
        }
        if (null == undefinedType) {
          throw new RuntimeException("At least one of the qualifies must be set.");
        }
        return "  CoCoHelper.buildErrorMsg(errorCode, \""
            + String.format(msg, undefinedType, CD4ACoCoHelper.printAssociation(assoc)) + "\"),";
      }
    });
    System.out.println(");");
  }
  
  /**
   * 0xCD4AC0017
   * 
   * @param leftTypedQualifier
   * @param rightTypedQualifier
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
    // generateTypedQualifiedAssocTests("LeftTypedQualifier",
    // "RightTypedQualifier");
    generateInvalidRoleNamesTests();
  }
  
  public static void generateSth() {
    // ASTCDAssociation assoc = CD4AnalysisNodeFactory.createASTCDAssociation();
    // boolean validModel = false;
    // List<ASTCDAssociation> allPossibilities = allDirections(assoc)
    // .stream()
    // .flatMap(a -> allTypeCombinations(a, true).stream())
    // // .flatMap(a -> allModifierCombinations(a, validModel).stream())
    // // .flatMap(a -> allRolePositions(a, validModel).stream())
    // // .flatMap(a -> allCardinalityCombinations(a).stream())
    // .flatMap(a -> allTypedQualifierPositions(a, true).stream())
    // .collect(Collectors.toList());
    //
    // printAssociations(allPossibilities);
    // printTestCases(allPossibilities, new ErrorMessagePrinter() {
    // @Override
    // public String print(ASTCDAssociation assoc) {
    // String msg =
    // "Association %s is invalid, because an association's source may not be an Enumeration.";
    // return "  CoCoHelper.buildErrorMsg(errorCode, \""
    // + String.format(msg, CD4ACoCoHelper.printAssociation(assoc)) + "\"),";
    // }
    // });
  }
}
