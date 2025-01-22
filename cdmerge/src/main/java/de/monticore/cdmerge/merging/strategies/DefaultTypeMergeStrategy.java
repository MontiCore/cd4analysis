/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.merging.strategies;

import de.monticore.ast.Comment;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cdmerge.config.PrecedenceConfig;
import de.monticore.cdmerge.exceptions.MergingException;
import de.monticore.cdmerge.log.ErrorLevel;
import de.monticore.cdmerge.log.MergePhase;
import de.monticore.cdmerge.matching.matchresult.ASTMatchGraph;
import de.monticore.cdmerge.merging.mergeresult.MergeBlackBoard;
import de.monticore.cdmerge.util.ASTCDHelper;
import de.monticore.cdmerge.util.CDMergeUtils;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.monticore.umlmodifier._ast.ASTModifierBuilder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Merges types locally, i.e. no consideration of super classes, implemented interfaces or the class
 * diagram in general
 */
public class DefaultTypeMergeStrategy extends MergerBase implements TypeMergeStrategy {

  private final AttributeMerger attrMerger;

  public DefaultTypeMergeStrategy(MergeBlackBoard board, AttributeMerger attrMerger) {
    super(board, MergePhase.TYPE_MERGING);
    this.attrMerger = attrMerger;
  }

  @Override
  public boolean canMergeHeterogeneousTypes() {
    return true;
  }

  @Override
  public ASTCDClass merge(
      ASTCDClass classFromCd1,
      ASTCDClass classFromCd2,
      ASTMatchGraph<ASTCDAttribute, ASTCDClass> matchResult) {

    log(
        ErrorLevel.FINE,
        "Merging "
            + getBlackBoard().getCurrentCD1Name()
            + "."
            + classFromCd1.getName()
            + " with "
            + getBlackBoard().getCurrentCD2Name()
            + "."
            + classFromCd2.getName());

    ASTModifier modifier =
        mergeModifier(classFromCd1.getModifier(), classFromCd2.getModifier())
            .orElseGet(() -> CD4CodeMill.modifierBuilder().build());
    // ================
    // === Modifier ===
    // ================

    // == ABSTRACT ==
    // Abstract if either of the source classes is abstract
    modifier.setAbstract(
        classFromCd1.getModifier().isAbstract() || classFromCd2.getModifier().isAbstract());

    // == FINAL ==
    if (classFromCd1.getModifier().isFinal() || classFromCd2.getModifier().isFinal()) {
      // One class is final - We won't merge this
      logError(
          "Class is declared final in "
              + (classFromCd1.getModifier().isFinal()
                  ? getBlackBoard().getCurrentInputCd1().getCDDefinition().getName()
                  : getBlackBoard().getCurrentInputCd2().getCDDefinition().getName())
              + ". Classes won't be merged if one class is declared final.",
          classFromCd1,
          classFromCd2);
    }
    modifier.setFinal(false);

    /*
     * TODO: Add modifier "local" for future versions. Local classes will *not* be
     * merged. Take the non-local class instead or none if both are declared local.
     */

    // PUBLIC, PRIVATE, PROTECTED, STATIC, DERIVED have no relevance for
    // classes -> ignore

    // We always assume that we merge classes with the same name
    ASTCDClass mergedClass =
        CD4CodeMill.cDClassBuilder().setName(classFromCd1.getName()).setModifier(modifier).build();

    // ==================
    // == Constructors ==
    // ==================
    // We only support pure data models...
    if (classFromCd1.getCDConstructorList().size() > 0) {
      logError("Constructors in classes are not supported.", classFromCd1);
    }
    if (classFromCd2.getCDConstructorList().size() > 0) {
      logError("Constructors in classes are not supported.", classFromCd2);
    }

    // ================
    // === Methods ====
    // ================
    // We only support pure data models...
    if (classFromCd1.getCDMethodList().size() > 0) {
      logError("Methods in classes are not supported.", classFromCd1);
    }
    if (classFromCd2.getCDMethodList().size() > 0) {
      logError("Methods in classes are not supported.", classFromCd2);
    }

    // ==============================
    // === Implemented Interfaces ===
    // ==============================
    ASTCDInterfaceUsage interfaces = CD4CodeMill.cDInterfaceUsageBuilder().build();
    interfaces.addAllInterface(
        mergeSuperInterfaces(classFromCd1.getInterfaceList(), classFromCd2.getInterfaceList()));
    if (interfaces.sizeInterface() > 0) {
      mergedClass.setCDInterfaceUsage(interfaces);
    } else {
      mergedClass.setCDInterfaceUsageAbsent();
    }

    // ===========================
    // == Extended Superclasses ==
    // ===========================
    String superClass1 = classFromCd1.printSuperclasses();
    String superClass2 = classFromCd2.printSuperclasses();
    if (!(superClass1.isEmpty() || superClass2.isEmpty())) {
      if (superClass1.equalsIgnoreCase(superClass2)) {
        mergedClass.setCDExtendUsage(classFromCd1.getCDExtendUsage());
      } else {
        // We have to find out if the different SuperClasses will be
        // Suptypes in the merged CD, then we
        // can pick the most specific of them
        Optional<ASTMCObjectType> superclass = determineSuperclass(classFromCd1, classFromCd2);
        if (superclass.isPresent()) {
          ASTCDExtendUsage extend = CD4CodeMill.cDExtendUsageBuilder().build();
          extend.addSuperclass(superclass.get());
          mergedClass.setCDExtendUsage(extend);
        } else {

          logError(
              "Merged classes have incompatible superclasses '"
                  + CDMergeUtils.getName(classFromCd2.getCDExtendUsage().getSuperclass(0))
                  + "' and '"
                  + CDMergeUtils.getName(classFromCd1.getCDExtendUsage().getSuperclass(0))
                  + "'. Merge would cause multi-inheritance, which is not supporterd",
              classFromCd1,
              classFromCd2);
        }
      }

    } else if (!superClass1.isEmpty()) {
      mergedClass.setCDExtendUsage(classFromCd1.getCDExtendUsage());
    } else if (!superClass2.isEmpty()) {
      mergedClass.setCDExtendUsage(classFromCd2.getCDExtendUsage());
    } else {
      mergedClass.setCDExtendUsageAbsent();
    }

    // Merge the Attributes
    attrMerger.mergeAttributes(classFromCd1, classFromCd2, matchResult, mergedClass);
    log(
        ErrorLevel.FINE,
        "Merged class " + CDMergeUtils.prettyPrintInline(mergedClass),
        classFromCd1,
        classFromCd2);
    return mergedClass;
  }

  private Optional<ASTMCObjectType> determineSuperclass(ASTCDClass class1, ASTCDClass class2) {
    ASTCDHelper cd1 = getBlackBoard().getASTCDHelperInputCD1();
    ASTCDHelper cd2 = getBlackBoard().getASTCDHelperInputCD2();

    String nameSuperClass;
    Optional<ASTCDClass> superclass;

    // Check if class1.superclass is a superclass of class2.superclass
    nameSuperClass = class1.printSuperclasses();
    if (!nameSuperClass.isEmpty()) {
      superclass = cd2.getClass(nameSuperClass);
      if (superclass.isPresent()
          && cd2.getLocalSuperClasses(class2.getName()).contains(superclass.get())) {
        // This class is a SuperClass of the class1.SuperClass, so
        // it's transitive and we can take it
        return Optional.of(class2.getSuperclassList().get(0));
      } else {
        // Check if we will merge heterogeneous types, then we can consider
        // superinterfaces, too
        if (getBlackBoard().getConfig().allowHeterogeneousMerge()) {
          Optional<ASTCDInterface> interfacePossibleSuperclass = cd2.getInterface(nameSuperClass);
          if (interfacePossibleSuperclass.isPresent()
              && cd2.getLocalImplementedInterfaces(class2.getName())
                  .contains(interfacePossibleSuperclass.get())) {
            // We are almost sure, but lets see if it is likely, that
            // these heterogeneous types will be merged
            // If the types will not be merged, then this be checked in
            // post-merged-validation
            if (class2.getName().equalsIgnoreCase(interfacePossibleSuperclass.get().getName())) {
              log(
                  ErrorLevel.INFO,
                  "Attention: Classes have different superclasses but it is assumed, that "
                      + "superclass '"
                      + nameSuperClass
                      + "' will be heterogeneously merged with interface '"
                      + interfacePossibleSuperclass.get().getName()
                      + "' and thus form a valid type hierarchy in the merged CD. Will be checked"
                      + " in Pos-Merge-Validation if activated.",
                  class1,
                  class2);
              return Optional.of(class2.getSuperclassList().get(0));
            }
          }
        }
      }
    }

    // Check if class2.superclass is a superclass of class1.superclass
    nameSuperClass = class2.printSuperclasses();
    if (!nameSuperClass.isEmpty()) {
      superclass = cd1.getClass(nameSuperClass);
      if (superclass.isPresent()
          && cd1.getLocalSuperClasses(class1.getName()).contains(superclass.get())) {
        // This class is a SuperClass of the class1.SuperClass, so
        // it's transitive and we can take it
        return Optional.of(class1.getSuperclassList().get(0));
      } else {
        // Check if we will merge heterogeneous types, then we can consider
        // superinterfaces, too
        if (getBlackBoard().getConfig().allowHeterogeneousMerge()) {
          Optional<ASTCDInterface> interfacePossibleSuperclass = cd1.getInterface(nameSuperClass);
          if (interfacePossibleSuperclass.isPresent()
              && cd1.getLocalImplementedInterfaces(class1.getName())
                  .contains(interfacePossibleSuperclass.get())) {
            // We are almost sure, but lets see if it is likely, that
            // these heterogeneous types will be merged
            // If the types will not be merged, then this be checked in
            // post-merged-validation
            if (class1.getName().equalsIgnoreCase(interfacePossibleSuperclass.get().getName())) {
              log(
                  ErrorLevel.INFO,
                  "Attention: Classes have different superclasses but it is assumed, that "
                      + "superclass '"
                      + nameSuperClass
                      + "' will be heterogeneously merged with interface '"
                      + interfacePossibleSuperclass.get().getName()
                      + "' and thus form a valid type hierarchy in the merged CD. Will be checked"
                      + " in Pos-Merge-Validation if activated.",
                  class1,
                  class2);
              return Optional.of(class1.getSuperclassList().get(0));
            }
          }
        }
      }
    }
    // No chance to find a valid super class...
    return Optional.empty();
  }

  @Override
  public ASTCDInterface merge(ASTCDInterface interface1, ASTCDInterface interface2) {

    ASTModifier modifier = CD4CodeMill.modifierBuilder().build();
    Optional<ASTModifier> mergedModifier =
        mergeModifier(interface1.getModifier(), interface2.getModifier());
    if (mergedModifier.isPresent()) {
      modifier = mergedModifier.get();
    }

    ASTCDInterface mergedInterface =
        CD4CodeMill.cDInterfaceBuilder()
            .setName(interface1.getName())
            .setModifier(modifier)
            .build();
    // FIXME Implement Attribute Merging
    if (interface1.getCDAttributeList().size() > 0) {
      logError("Attributes in interfaces are not supported.", interface1);
    }
    if (interface2.getCDAttributeList().size() > 0) {
      logError("Attributes in interfaces are not supported.", interface2);
    }
    if (interface1.getCDMethodList().size() > 0) {
      logError("Methods in interfaces are not supported.", interface1);
    }
    if (interface2.getCDMethodList().size() > 0) {
      logError("Methods in interfaces are not supported .", interface2);
    }
    /*
     * TODO: Add modifier "local" for future versions. Local interfaces will *not*
     * be merged. Take the non-local interface instead or none if both are declared
     * local.
     */

    // ==============================
    // === Implemented Interfaces ===
    // ==============================
    ASTCDExtendUsage extend = CD4CodeMill.cDExtendUsageBuilder().build();
    extend.addAllSuperclass(
        mergeSuperInterfaces(interface1.getInterfaceList(), interface2.getInterfaceList()));
    if (extend.getSuperclassList().size() > 0) {
      mergedInterface.setCDExtendUsage(extend);
    } else {
      mergedInterface.setCDExtendUsageAbsent();
    }

    log(
        ErrorLevel.FINE,
        "Merged interface " + CDMergeUtils.prettyPrintInline(mergedInterface),
        interface1,
        interface2);
    return mergedInterface;
  }

  @Override
  public ASTCDClass merge(ASTCDClass clazz, ASTCDInterface iface) {

    /*
     * TODO: Add modifier "local"
     */

    // The names should already match
    ASTCDClass mergedClass = clazz.deepClone();

    ASTModifier modifier = CD4CodeMill.modifierBuilder().build();
    Optional<ASTModifier> mergedModifier = mergeModifier(clazz.getModifier(), iface.getModifier());
    if (mergedModifier.isPresent()) {
      modifier = mergedModifier.get();
    }

    mergedClass.setModifier(modifier);

    ASTCDInterfaceUsage ifaces = CD4CodeMill.cDInterfaceUsageBuilder().build();
    ifaces.addAllInterface(
        mergeSuperInterfaces(clazz.getInterfaceList(), iface.getInterfaceList()));
    if (ifaces.getInterfaceList().size() > 0) {
      mergedClass.setCDInterfaceUsage(ifaces);
    } else {
      mergedClass.setCDExtendUsageAbsent();
    }

    log(ErrorLevel.FINE, "Merged class with interface " + mergedClass.getName(), clazz, iface);
    return mergedClass;
  }

  @Override
  public ASTCDClass merge(ASTCDClass clazz, ASTCDEnum en) {
    /*
     * TODO: Add modifier "local"
     */

    // The names should already match
    ASTCDClass mergedClass = clazz.deepClone();

    ASTModifier modifier = CD4CodeMill.modifierBuilder().build();
    Optional<ASTModifier> mergedModifier = mergeModifier(clazz.getModifier(), en.getModifier());
    if (mergedModifier.isPresent()) {
      modifier = mergedModifier.get();
    }

    mergedClass.setModifier(modifier);

    // Create a public final static String ... attribute for each constant;
    ASTCDAttributeBuilder attrBuilder = CD4CodeMill.cDAttributeBuilder();

    MCTypeFacade typeFacade = MCTypeFacade.getInstance();

    ASTModifierBuilder enumConstantModifier =
        CD4CodeMill.modifierBuilder().setPublic(true).setFinal(true).setStatic(true);
    for (ASTCDEnumConstant c : en.getCDEnumConstantList()) {
      mergedClass.addCDMember(
          attrBuilder
              .setName(c.getName())
              .setMCType(typeFacade.createStringType())
              .setModifier(enumConstantModifier.build())
              .build());
    }
    mergedClass.getInterfaceList().addAll(en.getInterfaceList());
    log(
        ErrorLevel.FINE,
        "Merged class with enum " + CDMergeUtils.prettyPrintInline(mergedClass),
        clazz,
        en);
    return mergedClass;
  }

  @Override
  public ASTCDEnum merge(ASTCDInterface inface, ASTCDEnum en) {

    /*
     * TODO: Add modifier "local"
     */

    // The names should already match
    ASTCDEnum mergedEnum = en.deepClone();
    ASTModifier modifier = CD4CodeMill.modifierBuilder().build();
    Optional<ASTModifier> mergedModifier = mergeModifier(inface.getModifier(), en.getModifier());
    if (mergedModifier.isPresent()) {
      modifier = mergedModifier.get();
    }

    mergedEnum.setModifier(modifier);
    if (inface.getInterfaceList().size() > 0) {
      if (mergedEnum.getInterfaceList().size() == 0) {
        mergedEnum.setCDInterfaceUsage(
            CD4CodeMill.cDInterfaceUsageBuilder()
                .setInterfaceList(inface.getInterfaceList())
                .build());
      } else {
        List<String> existingInterfaces =
            en.getInterfaceList().stream()
                .map(CDMergeUtils::getTypeName)
                .collect(Collectors.toList());
        for (ASTMCObjectType iface : inface.getInterfaceList()) {
          if (!existingInterfaces.contains(CDMergeUtils.getTypeName(iface))) {
            mergedEnum.getInterfaceList().add(iface);
          }
        }
      }
    }

    log(ErrorLevel.FINE, "Merged interface with enum " + inface.getName(), inface, en);
    return mergedEnum;
  }

  private void addPrecedenceConstants(ASTCDEnum astEnum, ASTCDDefinition cd, ASTCDEnum res) {
    PrecedenceConfig precedences = getConfig().getPrecedences();
    Optional<ASTCDEnumConstant> constant;
    List<String> additionalConstNames = precedences.getPrecedenceConstantsForEnum(astEnum, cd);
    for (String constName : additionalConstNames) {
      constant = CDMergeUtils.getConstFromEnum(constName, astEnum);
      if (constant.isPresent() && CDMergeUtils.getConstFromEnum(constName, res).isEmpty()) {
        res.getCDEnumConstantList().add(constant.get());
      }
    }
  }

  @Override
  public ASTCDEnum merge(ASTCDEnum enum1, ASTCDEnum enum2) {

    /*
     * TODO: Add modifier "local"
     */

    ASTModifier modifier = CD4CodeMill.modifierBuilder().build();
    Optional<ASTModifier> mergedModifier = mergeModifier(enum1.getModifier(), enum2.getModifier());
    if (mergedModifier.isPresent()) {
      modifier = mergedModifier.get();
    }
    ASTCDEnum mergedEnum =
        CD4CodeMill.cDEnumBuilder().setName(enum1.getName()).setModifier(modifier).build();

    PrecedenceConfig precedences = getConfig().getPrecedences();

    ASTCDDefinition leftCD = getBlackBoard().getCurrentInputCd1().getCDDefinition();
    ASTCDDefinition rightCD = getBlackBoard().getCurrentInputCd2().getCDDefinition();

    if (precedences.hasPrecedence(enum1, enum2, leftCD, rightCD)) {
      // No union: take only the constants of the left enum
      mergedEnum.setCDEnumConstantList(enum1.getCDEnumConstantList());
    } else if (precedences.hasPrecedence(enum2, enum1, rightCD, leftCD)) {
      // No union: take only the constants of the right enum
      mergedEnum.setCDEnumConstantList(enum2.getCDEnumConstantList());
    } else {
      // Default strategy: Union of constants
      try {
        if (!mergeEnumConstants(enum1, enum2, mergedEnum)) {
          String message =
              "Merging Constants of enum did not preserve strict but only partial order. It "
                  + "cannot be guaranteed that constants in the merged enum are in the desired "
                  + "order. Please check enum '"
                  + mergedEnum.getName()
                  + "' if the strict order matters.";
          logWarning(message, enum1, enum2);
          // Annotate the CD Node so we can see it in the pretty
          // printed result CD
          mergedEnum.add_PreComment(new Comment(message));
        }
      } catch (MergingException e) {
        logError(e);
      }
    }
    // Even if e.g. CD1.Enum overrides CD2.Enum, allow to add
    // CD2.Enum.Constant to the resulting class diagram
    addPrecedenceConstants(enum1, leftCD, mergedEnum);
    addPrecedenceConstants(enum2, rightCD, mergedEnum);
    log(
        ErrorLevel.FINE,
        "Merged enums " + CDMergeUtils.prettyPrintInline(mergedEnum),
        enum1,
        enum2);
    return mergedEnum;
  }

  /**
   * Merges all enum constants by preserving the partial order over all enum constants If there is
   * no unique order, than constants of enum1 are added first Example: Enum1: A B C D E F G Enum2: K
   * A G H D G L O Merged: K A B C G H D E F G L O
   *
   * @returns true if the order is still strict (i.e. no ambiguous partial order of elements) false
   *     if the order is only partial
   */
  // FIXME: Logging, Tagging and Error Mode should be configurable as parameter
  public boolean mergeEnumConstants(ASTCDEnum enum1, ASTCDEnum enum2, ASTCDEnum mergedEnum)
      throws MergingException {
    boolean ok = true;
    boolean orderStrictConsistent = true;
    int strictOrderCounter = 0;
    Iterator<ASTCDEnumConstant> constantIterator1 = enum1.getCDEnumConstantList().iterator();
    Iterator<ASTCDEnumConstant> constantIterator2 = enum2.getCDEnumConstantList().iterator();
    if (!constantIterator1.hasNext()) {
      mergedEnum.setCDEnumConstantList(enum2.getCDEnumConstantList());
      return true;
    }
    if (!constantIterator2.hasNext()) {
      mergedEnum.setCDEnumConstantList(enum1.getCDEnumConstantList());
      return true;
    }
    ASTCDEnumConstant constant1;
    ASTCDEnumConstant constant2 = constantIterator2.next();
    List<ASTCDEnumConstant> constants = new ArrayList<>();
    while (constantIterator1.hasNext()) {
      constant1 = constantIterator1.next();
      // Same constant: Add to Merged and move forward in both enums
      if (constant1.getName().equalsIgnoreCase(constant2.getName())) {
        if (constant1.deepEquals(constant2)) {
          strictOrderCounter = 0;
          constants.add(constant1);
          if (constantIterator2.hasNext()) {
            constant2 = constantIterator2.next();
            // if there are no more elements left in enum1, we add constant2
            if (!constantIterator1.hasNext()
                && !constant1.getName().equalsIgnoreCase(constant2.getName())) {
              constants.add(constant2);
            }
          } else {
            break;
          }
        } else {
          logError(
              "Constant '"
                  + enum1.getName()
                  + "."
                  + constant1.getName()
                  + " has different enum parameters and therefore cannot be merged",
              enum1,
              enum2);
          throw new MergingException(
              "Constant '"
                  + enum1.getName()
                  + "."
                  + constant1.getName()
                  + " has different enum parameters and therefore cannot be merged");
        }
      } else {
        // We take a look if we will encounter this enum constant later
        // and add all entries of enum2 before that
        Optional<ASTCDEnumConstant> matchedEnumIn2 =
            CDMergeUtils.getConstFromEnum(constant1.getName(), enum2);
        if (matchedEnumIn2.isPresent()) {
          if (enum2.getCDEnumConstantList().indexOf(constant2)
              < enum2.getCDEnumConstantList().indexOf(matchedEnumIn2.get())) {
            do {
              constants.add(constant2);
              constant2 = constantIterator2.next();
              if (strictOrderCounter > 0) {
                strictOrderCounter++;
              }

            } while (!constant2.getName().equals(matchedEnumIn2.get().getName()));
            if (strictOrderCounter > 1) {
              orderStrictConsistent = false;
            }
          } else {

            // We can't compare the order any longer, so we
            // quit
            throw new MergingException(
                "Constants have an inconsistent order in both enums - enum constants will not be "
                    + "merged!",
                PHASE,
                enum1,
                enum2);
          }
        }
        // We have added similar and all constants from enum2, add
        // constant from enum1 and continue
        // At this point we don't have a unique way to sort the element.
        // Though, the sorting is still consistent w.r.t. partial order
        // of all constants
        constants.add(constant1);

        if (constant1.getName().equalsIgnoreCase(constant2.getName())) {
          strictOrderCounter = 0;
          if (constantIterator2.hasNext()) {
            constant2 = constantIterator2.next();
          }
        } else {
          strictOrderCounter++;
        }
        // There might be a dangling constant from enum2 from line 268
        // above
        if (!constantIterator1.hasNext()
            && !constant1.getName().equalsIgnoreCase(constant2.getName())) {
          // We still have the iterator on an unhandled element, add
          // it and conclude this loop
          constants.add(constant2);
          strictOrderCounter++;
          if (strictOrderCounter > 1) {
            orderStrictConsistent = false;
          }
        }
      }
    }
    // Add remaining from enum1
    while (constantIterator1.hasNext()) {
      constants.add(constantIterator1.next());
    }
    // Add remaining from enum2
    while (constantIterator2.hasNext()) {
      constants.add(constantIterator2.next());
    }

    mergedEnum.setCDEnumConstantList(constants);
    return orderStrictConsistent;
  }

  protected List<ASTMCObjectType> mergeSuperInterfaces(
      List<ASTMCObjectType> interfaces1, List<ASTMCObjectType> interfaces2) {

    List<ASTMCObjectType> mergedInterfaces = new ArrayList<>(interfaces1);
    Set<String> names = new HashSet<>();
    mergedInterfaces.stream().map(CDMergeUtils::getName).forEach(names::add);
    // Add remaining from 2
    for (ASTMCObjectType iface : interfaces2) {
      if (!names.contains(CDMergeUtils.getName(iface))) {
        mergedInterfaces.add(iface);
        // Though should not happen to have
        // twice the same interface name
        names.add(CDMergeUtils.getName(iface));
      }
    }
    return mergedInterfaces;
  }
}
