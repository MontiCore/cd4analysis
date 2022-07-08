package de.monticore.syntax2semdiff.cd2sg;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import de.monticore.alloycddiff.CDSemantics;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.ow2cw.CDInheritanceHelper;
import de.monticore.syntax2semdiff.cd2sg.metamodel.SupportAssociationPack;
import de.monticore.syntax2semdiff.cd2sg.metamodel.SupportClass;
import de.monticore.syntax2semdiff.cd2sg.metamodel.SupportAssociation;
import de.monticore.syntax2semdiff.cd2sg.metamodel.SupportGroup;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static de.monticore.syntax2semdiff.cd2sg.SupportHelper.*;

public class CD2SGGenerator {
  protected Map<String, SupportClass> supportClassGroup = new HashMap<>();
  protected Map<String, SupportAssociation> supportAssociationGroup = new HashMap<>();
  protected MutableGraph<String> inheritanceGraph = GraphBuilder.directed().build();
  protected Map<String, Set<String>> enumClassMap = new HashMap<>();

  /**
   * generating SupportGroup
   */
  public SupportGroup generateSupportGroup(ASTCDCompilationUnit cd, CDSemantics type) {
    SupportGroup supportGroup = new SupportGroup();
    ICD4CodeArtifactScope scope = CD4CodeMill.scopesGenitorDelegator().createFromAST(cd);

    createSupportClassForSimpleClassAndAbstractClass(cd, scope);
    createSupportClassForInterface(cd, scope);
    createSupportClassForEnum(cd, scope);
    createSupportAssociation(cd);
    solveInheritance();
    solveOverlap();

    supportGroup.setModel(cd);
    supportGroup.setType(type);
    supportGroup.setSupportClassGroup(supportClassGroup);
    supportGroup.setSupportAssociationGroup(supportAssociationGroup);
    supportGroup.setInheritanceGraph(inheritanceGraph);
    supportGroup.setRefSetAssociationList(createSupportRefSetAssociation(supportAssociationGroup, inheritanceGraph));
    return supportGroup;
  }

  /********************************************************************
   *********************    Start for Class    ************************
   *******************************************************************/

  /**
   * create SupportClass object for class and abstract class in AST
   */
  public void createSupportClassForSimpleClassAndAbstractClass(ASTCDCompilationUnit cd, ICD4CodeArtifactScope scope) {
    List<ASTCDClass> astcdClassList = cd.getCDDefinition().getCDClassesList();
    List<ASTCDEnum> astcdEnumList = cd.getCDDefinition().getCDEnumsList();

    for (ASTCDType astcdType : astcdClassList) {
      SupportClass supportClass = createSupportClassHelper(astcdType, scope, astcdEnumList);
      supportClassGroup.put(supportClass.getName(), supportClass);
    }
  }

  /**
   * create SupportClass object for interface in AST
   */
  public void createSupportClassForInterface(ASTCDCompilationUnit cd, ICD4CodeArtifactScope scope) {
    List<ASTCDInterface> astcdInterfaceList = cd.getCDDefinition().getCDInterfacesList();
    List<ASTCDEnum> astcdEnumList = cd.getCDDefinition().getCDEnumsList();

    for (ASTCDType astcdType : astcdInterfaceList) {
      SupportClass supportClass = createSupportClassHelper(astcdType, scope, astcdEnumList);
      supportClassGroup.put(supportClass.getName(), supportClass);
    }
  }

  /**
   * create SupportClass object for enum in AST
   */
  public void createSupportClassForEnum(ASTCDCompilationUnit cd, ICD4CodeArtifactScope scope) {
    List<ASTCDEnum> astcdEnumList = cd.getCDDefinition().getCDEnumsList();

    for (ASTCDType astcdType : astcdEnumList) {
      SupportClass supportClass = createSupportClassHelper(astcdType, scope, astcdEnumList);
      supportClassGroup.put(supportClass.getName(), supportClass);
    }
  }


  /**
   * all creating SupportClass functions are based on this helper
   */
  public SupportClass createSupportClassHelper(ASTCDType astcdType, ICD4CodeArtifactScope scope, List<ASTCDEnum> astcdEnumList) {
    SupportClass supportClass = new SupportClass(astcdType);

    if (!astcdType.getClass().equals(ASTCDEnum.class)) {

      // create InheritanceGraph
      Set<ASTCDType> directSuperSet = CDInheritanceHelper.getDirectSuperClasses(astcdType, scope);
      directSuperSet.addAll(CDInheritanceHelper.getDirectInterfaces(astcdType, scope));
      createInheritanceGraph(astcdType, directSuperSet);

      // add attributes
      for (ASTCDAttribute astcdAttribute : astcdType.getCDAttributeList()) {
        if (astcdEnumList.stream().anyMatch(s -> s.getName().equals(astcdAttribute.printType()))) {
          supportClass.addAttribute(astcdAttribute);
          creatEnumClassMapHelper("SupportEnum_" + astcdAttribute.printType(), supportClass.getName());
        } else {
          supportClass.addAttribute(astcdAttribute);
        }
      }
    } else {
      // add supportLink4EnumClass
      supportClass.setSupportLink4EnumClass(enumClassMap.get(supportClass.getName()));
    }

    return supportClass;
  }

  /**
   * After all SupportClass created, putting the temporary enumClassMap into SupportClassGroup.
   */
  public void creatEnumClassMapHelper(String enumClass, String baseClass) {
    Set<String> set = enumClassMap.getOrDefault(enumClass, new HashSet<>());
    set.add(baseClass);
    enumClassMap.put(enumClass, set);
  }

  /**
   * create inheritance graph
   * childClass -> parentClass
   */
  public void createInheritanceGraph(ASTCDType child, Collection<ASTCDType> directSuperList) {
    String childClass =
      getSupportClassKindStrHelper(distinguishASTCDTypeHelper(child)) + "_" + child.getSymbol().getFullName();
    inheritanceGraph.addNode(childClass);
    directSuperList.forEach(parent -> {
      String parentClass =
        getSupportClassKindStrHelper(distinguishASTCDTypeHelper(parent)) + "_" + parent.getSymbol().getFullName();
      inheritanceGraph.putEdge(childClass, parentClass);
    });
  }

  /********************************************************************
   ********************* Start for Association ************************
   *******************************************************************/

  /**
   * create SupportAssociation object for association in AST
   */
  public void createSupportAssociation(ASTCDCompilationUnit cd) {
    List<ASTCDAssociation> astcdAssociationList = cd.getCDDefinition().getCDAssociationsList();
    for (ASTCDAssociation astcdAssociation : astcdAssociationList) {
      createSupportAssociationHelper(astcdAssociation, false);
    }
  }

  /**
   * the creating SupportAssociation functions are based on this helper
   */
  public void createSupportAssociationHelper(ASTCDAssociation astcdAssociation, Boolean isInherited) {
    // add role name if the original ASTCDAssociation has no role name for one side or both side
    astcdAssociation = generateASTCDAssociationRoleName(astcdAssociation);
    SupportAssociation currentAssoc = new SupportAssociation(astcdAssociation, isInherited);
    currentAssoc.setSupportLeftClass(
      findSupportClass4OriginalClassName(supportClassGroup, currentAssoc.getLeftOriginalClassName()));
    currentAssoc.setSupportRightClass(
      findSupportClass4OriginalClassName(supportClassGroup, currentAssoc.getRightOriginalClassName()));
    supportAssociationGroup.put(currentAssoc.getName(), currentAssoc);
  }

  /********************************************************************
   ******************** Solution for Inheritance **********************
   *******************************************************************/

  /**
   * solve the inheritance problem:
   *  1. add inherited attributes into corresponding SupportClass
   *  2. generate inherited associations and put them into SupportAssociationGroup
   */
  private void solveInheritance() {
    List<List<String>> waitList = new ArrayList<>();
    SupportHelper supportHelper = new SupportHelper();
    getAllBottomSupportClassNode(inheritanceGraph).forEach(supportClassName ->
      waitList.addAll(
        supportHelper.getAllInheritancePath4SupportClass(supportClassGroup.get(supportClassName), inheritanceGraph)));
    waitList.forEach(path -> {
      if (path.size() > 1) {

        for (int i = 0; i < path.size() - 1; i++) {
          SupportClass parent = supportClassGroup.get(path.get(i));
          SupportClass child = supportClassGroup.get(path.get(i + 1));

          // for attributes
          parent.getEditedElement().getCDAttributeList().forEach(e -> {
            String type = "SupportEnum_" + e.printType();
            // update enumClassMap
            if (enumClassMap.containsKey(type)) {
              Set<String> set = enumClassMap.get(type);
              set.add(child.getName());
              enumClassMap.put(type, set);
            }
            // add inherited attribute into child supportClass
            child.addAttribute(e);

          });

          // update all SupportEnum
          updateSupportEnum();

          // for association
          String parentOriginalName = parent.getOriginalClassName();
          String childOriginalName = child.getOriginalClassName();
          Map<String, SupportAssociation> associationMap =
            fuzzySearchSupportAssociationByClassName(supportAssociationGroup, parentOriginalName);

          associationMap.forEach((oldName, oldSupportAssociation) -> {

            String prefix = oldName.split("_")[0];
            String leftClass = oldSupportAssociation.getSupportLeftClass().getOriginalClassName();
            String leftRoleName = oldSupportAssociation.getSupportLeftClassRoleName();
            String direction = formatDirection(oldSupportAssociation.getSupportDirection());
            String rightRoleName = oldSupportAssociation.getSupportRightClassRoleName();
            String rightClass = oldSupportAssociation.getSupportRightClass().getOriginalClassName();
            leftClass = leftClass.equals(parentOriginalName) ? childOriginalName : leftClass;
            rightClass = rightClass.equals(parentOriginalName) ? childOriginalName : rightClass;
            String newName =
              prefix + "_" + leftClass + "_" + leftRoleName + "_" + direction + "_" + rightRoleName + "_" + rightClass;
            if (!supportAssociationGroup.containsKey(newName)) {
              ASTCDAssociation oldASTAssoc = oldSupportAssociation.getOriginalElement();
              ASTCDAssociation newASTAssoc = oldASTAssoc.deepClone();
              SupportClass leftSupportClass = oldSupportAssociation.getSupportLeftClass();
              SupportClass rightSupportClass = oldSupportAssociation.getSupportRightClass();
              if (oldSupportAssociation.getSupportLeftClass().getOriginalClassName().contains(parentOriginalName)) {
                newASTAssoc = editASTCDAssociationLeftSideBySupportClass(newASTAssoc, child);
                leftSupportClass = child;
              }
              if (oldSupportAssociation.getSupportRightClass().getOriginalClassName().contains(parentOriginalName)) {
                newASTAssoc = editASTCDAssociationRightSideBySupportClass(newASTAssoc, child);
                rightSupportClass = child;
              }
              SupportAssociation newSupportAssociation = new SupportAssociation(newASTAssoc, true);
              newSupportAssociation.setSupportLeftClass(leftSupportClass);
              newSupportAssociation.setSupportRightClass(rightSupportClass);
              supportAssociationGroup.put(newSupportAssociation.getName(), newSupportAssociation);
            }
          });
        }
      }
    });
  }

  /**
   * After solving inheritance problem update the Enum SupportClass in SupportClassGroup
   */
  private void updateSupportEnum() {
    enumClassMap.forEach((k, v) -> {
      SupportClass supportEnum = supportClassGroup.get(k);
      supportEnum.setSupportLink4EnumClass(v);
    });
  }

  /********************************************************************
   ********************   Solution for Overlap   **********************
   *******************************************************************/

  /**
   * solve the duplicate association with overlap part
   */
  private void solveOverlap() {

    Map<String, SupportAssociation> clonedSupportAssociationGroup = supportAssociationGroup.entrySet()
      .stream()
      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    clonedSupportAssociationGroup.forEach((currentAssocName, currentAssoc) -> {
      if (currentAssoc.getSupportKind() == SupportGroup.SupportAssociationKind.SUPPORT_ASC) {
        List<SupportAssociationPack> matchedAssocList =
          fuzzySearchSupportAssociationBySupportAssociationWithoutDirection(supportAssociationGroup, currentAssoc);
        if (matchedAssocList.size() > 0) {
          supportAssociationGroup.remove(currentAssoc.getName());
          AtomicReference<SupportAssociation> newSupportAssoc = new AtomicReference<>(currentAssoc);

          matchedAssocList.forEach(e -> {
            SupportAssociation existAssoc = e.getSupportAssociation();
            boolean isReverse = e.isReverse();

            if (!isReverse) {
              String directionResult = supportAssociationDirectionHelper(
                existAssoc.getSupportDirection(), newSupportAssoc.get().getSupportDirection());
              SupportGroup.SupportAssociationCardinality leftCardinalityResult = supportAssociationCardinalityHelper(
                existAssoc.getSupportLeftClassCardinality(), newSupportAssoc.get().getSupportLeftClassCardinality());
              SupportGroup.SupportAssociationCardinality rightCardinalityResult = supportAssociationCardinalityHelper(
                existAssoc.getSupportRightClassCardinality(), newSupportAssoc.get().getSupportRightClassCardinality());
              switch (directionResult) {
                case "current":
                  supportAssociationGroup.remove(existAssoc.getName());
                  newSupportAssoc.get().setSupportLeftClassCardinality(leftCardinalityResult);
                  newSupportAssoc.get().setSupportRightClassCardinality(rightCardinalityResult);
                  newSupportAssoc.get().setSupportKind(SupportGroup.SupportAssociationKind.SUPPORT_ASC);
                  break;
                case "exist":
                  supportAssociationGroup.remove(existAssoc.getName());
                  existAssoc.setSupportLeftClassCardinality(leftCardinalityResult);
                  existAssoc.setSupportRightClassCardinality(rightCardinalityResult);
                  existAssoc.setSupportKind(SupportGroup.SupportAssociationKind.SUPPORT_ASC);
                  newSupportAssoc.set(existAssoc);
                  break;
                default:
                  break;
              }
            } else {
              String directionResult4Current = supportAssociationDirectionHelper(
                reverseDirection(existAssoc.getSupportDirection()), newSupportAssoc.get().getSupportDirection());
              SupportGroup.SupportAssociationCardinality leftCardinalityResult4Current = supportAssociationCardinalityHelper(
                existAssoc.getSupportRightClassCardinality(), newSupportAssoc.get().getSupportLeftClassCardinality());
              SupportGroup.SupportAssociationCardinality rightCardinalityResult4Current = supportAssociationCardinalityHelper(
                existAssoc.getSupportLeftClassCardinality(), newSupportAssoc.get().getSupportRightClassCardinality());
              switch (directionResult4Current) {
                case "current":
                  supportAssociationGroup.remove(existAssoc.getName());
                  newSupportAssoc.get().setSupportLeftClassCardinality(leftCardinalityResult4Current);
                  newSupportAssoc.get().setSupportRightClassCardinality(rightCardinalityResult4Current);
                  newSupportAssoc.get().setSupportKind(SupportGroup.SupportAssociationKind.SUPPORT_ASC);
                  break;
                case "exist":
                  supportAssociationGroup.remove(existAssoc.getName());
                  existAssoc.setSupportLeftClassCardinality(rightCardinalityResult4Current);
                  existAssoc.setSupportRightClassCardinality(leftCardinalityResult4Current);
                  existAssoc.setSupportKind(SupportGroup.SupportAssociationKind.SUPPORT_ASC);
                  SupportAssociation reversedAssoc = reverseSupportAssociation(existAssoc, existAssoc.getEditedElement().getCDAssocDir());
                  newSupportAssoc.set(reversedAssoc);
                  break;
                default:
                  break;
              }
            }
          });
          supportAssociationGroup.put(newSupportAssoc.get().getName(), newSupportAssoc.get());
        }
      }

    });
  }

}
