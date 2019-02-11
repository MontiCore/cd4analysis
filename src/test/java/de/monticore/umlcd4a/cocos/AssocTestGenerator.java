/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAssociation;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCardinality;
import de.monticore.umlcd4a.cd4analysis._ast.ASTModifier;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDStereoValue;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDStereotype;
import de.monticore.umlcd4a.cd4analysis._ast.CD4AnalysisMill;
import de.monticore.umlcd4a.cd4analysis._ast.CD4AnalysisNodeFactory;
import de.monticore.umlcd4a.cocos.AssocTestGeneratorTool.ErrorMessagePrinter;

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
 *        return "  CoCoFinding.error(errorCode, \""
 *            + String.format(msg, CD4ACoCoHelper.printAssociation(assoc)) + "\"),";
 *      }
 *    });
 * 
 * output:
 *   CoCoFinding.error(errorCode, "Association assoc0 (E -> A) is invalid, because an association's source may not be an Enumeration."),
 *   CoCoFinding.error(errorCode, "Association assoc1 (E -> B) is invalid, because an association's source may not be an Enumeration."),
 *   CoCoFinding.error(errorCode, "Association assoc2 (E -> E) is invalid, because an association's source may not be an Enumeration."),
 *   // ...
 * </pre>
 * 
 * @author Robert Heim
 */
@Deprecated //new class at cd4analysis/src/main/java/de/monticore/cd
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
    
    System.out.println("Collection<CoCoFinding> expectedErrors = Arrays.asList(");
    
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
    
    System.out.println("Collection<CoCoFinding> expectedErrors = Arrays.asList(");
    
    AssocTestGeneratorTool.printTestCases(allPossibilities, new ErrorMessagePrinter() {
      @Override
      public String print(ASTCDAssociation assoc) {
        String msg = "Role %s of association %s must start in lower-case.";
        String invalidRoleName = null;
        if (assoc.isPresentLeftRole()) {
          invalidRoleName = assoc.getLeftRole();
        }
        else {
          if (assoc.isPresentRightRole()) {
            invalidRoleName = assoc.getRightRole();
          }
        }
        if (null == invalidRoleName) {
          throw new RuntimeException("At least one of the roles must be set.");
        }
        return "  CoCoFinding.error(errorCode, \""
            + String.format(msg, invalidRoleName, CD4ACoCoHelper.printAssociation(assoc)) + "\"),";
      }
    });
    System.out.println(");");
  }
  
  public static void main(String[] args) {
    generateInvalidCompositeCardinalities();
  }
  
  /**
   * 0xCD4AC0024
   */
  public static void generateInvalidOrderedAssocs() {
    ASTCDAssociation assoc = CD4AnalysisNodeFactory.createASTCDAssociation();
    
    // dirty, but we don't have time: the predicate will set the
    // ordered-stereotype as well
    // this predicate ensures a correct position of the <<ordered>> stereotype
    // and an invalid cardinality ( [1] or [0..1] ).
    Predicate<ASTCDAssociation> isInvalidOrderedAssoc = new Predicate<ASTCDAssociation>() {
      @Override
      public boolean test(ASTCDAssociation assoc) {
        
        ASTCardinality cardinality = null;
        ASTModifier modifier = null;
        
        List<ASTCDStereoValue> stereoOrdered = new ArrayList<>();
        stereoOrdered.add(CD4AnalysisMill.cDStereoValueBuilder().setName("ordered").build());
        ASTCDStereotype stereoType = CD4AnalysisNodeFactory.createASTCDStereotype(stereoOrdered);
        
        boolean rightSideInvalid = false;
        if (assoc.isPresentRightCardinality()) {
          cardinality = assoc.getRightCardinality();
          if (cardinality.isOne() || cardinality.isOptional()) {
            if (!assoc.isPresentRightModifier()) {
              modifier = CD4AnalysisNodeFactory.createASTModifier();
              assoc.setRightModifier(modifier);
            }
            modifier = assoc.getRightModifier();
            modifier.setStereotype(stereoType);
            rightSideInvalid = true;
          }
        }
        boolean leftSideInvalid = false;
        if (assoc.isPresentLeftCardinality()) {
          cardinality = assoc.getLeftCardinality();
          if (cardinality.isOne() || cardinality.isOptional()) {
            if (!assoc.isPresentLeftModifier()) {
              modifier = CD4AnalysisNodeFactory.createASTModifier();
              assoc.setLeftModifier(modifier);
            }
            modifier = assoc.getLeftModifier();
            modifier.setStereotype(stereoType);
            leftSideInvalid = true;
          }
        }
        
        // A -> B
        if (assoc.isLeftToRight()) {
          return rightSideInvalid;
        }
        // A <- B
        else if (assoc.isRightToLeft()) {
          return leftSideInvalid;
        }
        // A <-> B | A -- B
        else if (assoc.isBidirectional() || assoc.isUnspecified()) {
          return (leftSideInvalid || rightSideInvalid);
        }
        return false;
      }
    };
    
    List<ASTCDAssociation> allPossibilities = AssocTestGeneratorTool.allDirections(assoc)
        .stream()
        .flatMap(a -> AssocTestGeneratorTool.allTypeCombinations(a, true).stream())
        .flatMap(a -> AssocTestGeneratorTool.allCardinalityCombinations(a).stream())
        .filter(isInvalidOrderedAssoc)
        .collect(Collectors.toList());
    String modelContents = AssocTestGeneratorTool.printAssociations(allPossibilities);
    System.out.println(modelContents);
    
    System.out.println("Collection<CoCoFinding> expectedErrors = Arrays.asList(");
    
    ErrorMessagePrinter errorMessagePrinter = new ErrorMessagePrinter() {
      @Override
      public String print(ASTCDAssociation assoc) {
        String msg = "Association %s is invalid, because ordered associations are forbidden for a cardinality lower or equal to 1.";
        return "  CoCoFinding.error(errorCode, \""
            + String.format(msg, CD4ACoCoHelper.printAssociation(assoc)) + "\"),";
      }
    };
    AssocTestGeneratorTool.printTestCases(allPossibilities, errorMessagePrinter);
    
    System.out.println(");");
    
  }
  
  /**
   * 0xCD4AC0021
   */
  public static void generateEnumAsSource() {
    ASTCDAssociation assoc = CD4AnalysisNodeFactory.createASTCDAssociation();
    List<ASTCDAssociation> allPossibilities = AssocTestGeneratorTool.allDirections(assoc)
        .stream()
        .flatMap(a -> AssocTestGeneratorTool.allTypeCombinations(a, false).stream())
        .collect(Collectors.toList());
    String modelContents = AssocTestGeneratorTool.printAssociations(allPossibilities);
    System.out.println(modelContents);
    
    System.out.println("Collection<CoCoFinding> expectedErrors = Arrays.asList(");
    
    ErrorMessagePrinter errorMessagePrinter = new ErrorMessagePrinter() {
      @Override
      public String print(ASTCDAssociation assoc) {
        String msg = "Association %s is invalid, because an association's source may not be an Enumeration.";
        return "  CoCoFinding.error(errorCode, \""
            + String.format(msg, CD4ACoCoHelper.printAssociation(assoc)) + "\"),";
      }
    };
    AssocTestGeneratorTool.printTestCases(allPossibilities, errorMessagePrinter);
    
    System.out.println(");");
  }
  
  public static void generateInvalidCompositeCardinalities() {
    ErrorMessagePrinter errorMessagePrinter = new ErrorMessagePrinter() {
      @Override
      public String print(ASTCDAssociation assoc) {
        String msg = "The composition %s has an invalid cardinality %s larger than one.";
        ASTCardinality cardinality = null;
        
        if (assoc.isRightToLeft()) {
          if (assoc.isPresentRightCardinality()) {
            cardinality = assoc.getRightCardinality();
          }
        }
        else {
          // all other directions are interpreted as: left side is the
          // composite, right side are the elements.
          if (assoc.isPresentLeftCardinality()) {
            cardinality = assoc.getLeftCardinality();
          }
        }
        
        String invalidCardinalityStr = null;
        if (cardinality != null) {
          invalidCardinalityStr = CD4ACoCoHelper.printCardinality(cardinality);
        }
        if (null != invalidCardinalityStr) {
          return "  CoCoFinding.error(errorCode, \""
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
          if (assoc.isPresentRightCardinality()) {
            cardinality = assoc.getRightCardinality();
          }
          // we don't allow navigation direction <- for compositions so we mark
          // them as not beeing invalid here as we don't want them in the test
          // for the cardinality checks
          return false;
        }
        else {
          // all other directions are interpreted as: left side is the
          // composite, right side are the elements.
          if (assoc.isPresentLeftCardinality()) {
            cardinality = assoc.getLeftCardinality();
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
    
    System.out.println("Collection<CoCoFinding> expectedErrors = Arrays.asList(");
    AssocTestGeneratorTool.printTestCases(allPossibilities, errorMessagePrinter);
    
    System.out.println(");");
    
  }
}
