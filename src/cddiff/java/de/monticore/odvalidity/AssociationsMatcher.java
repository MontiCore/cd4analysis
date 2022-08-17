package de.monticore.odvalidity;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdassociation._ast.*;
import de.monticore.cdassociation.prettyprint.CDAssociationFullPrettyPrinter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.alloycddiff.CDSemantics;
import de.monticore.odbasis._ast.ASTODName;
import de.monticore.odbasis._ast.ASTODNamedObject;
import de.monticore.odbasis._ast.ASTODObject;
import de.monticore.odbasis._ast.ASTObjectDiagram;
import de.monticore.odlink._ast.ASTODLink;
import de.monticore.odlink._ast.ASTODLinkLeftSide;
import de.monticore.odlink._ast.ASTODLinkRightSide;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.se_rwth.commons.logging.Log;

import java.util.*;
import java.util.stream.Collectors;


public class AssociationsMatcher {

  MCBasicTypesFullPrettyPrinter pp;

  private final static String INSTANCE_OF_STEREOTYPE = "instanceof";

  private boolean openWorldDiffFound;

  private Map<Integer, List<ASTODName>> leftMap;

  private Map<Integer, List<ASTODName>> rightMap;

  private ASTCDCompilationUnit cd;

  public AssociationsMatcher(MCBasicTypesFullPrettyPrinter prettyPrinter) {
    this.pp = prettyPrinter;
    leftMap = new HashMap<>();
    rightMap = new HashMap<>();
  }

  //TODO: consider qualifiers, compare super/sub types when making diff
  public boolean checkAssociations(ASTObjectDiagram od, ASTCDCompilationUnit cd, CDSemantics semantic) {

    leftMap = new HashMap<>();
    rightMap = new HashMap<>();
    this.cd = cd;

    List<ASTODLink> transformedLinks =
        new LinkTrafo().transformLinksToLTR(ODHelper.getAllLinks(od));
    ASTCDAssociation[] associations = cd.getCDDefinition().getCDAssociationsList().toArray(new ASTCDAssociation[0]);

    //Iterate over all links and try to match them against the given list of associations
    for (ASTODLink link : transformedLinks) {
      boolean associationFound = false;
      //reset open world match found marker
      openWorldDiffFound = false;
      int i = 0;
      while (i < associations.length && !associationFound) {

        //Associations are not required to have role names, but their type should be used as
        // default if none is present
        ASTCDAssociation a = associations[i];
        //set the association right role name to the class name if none present
        if (!a.getRight().isPresentCDRole()) {
          a.setRight((ASTCDAssocRightSide) setAssociationTypeAsRoleName(a, false));
        }
        //set the association left role name if none present
        if (!a.getLeft().isPresentCDRole()) {
          a.setLeft((ASTCDAssocLeftSide) setAssociationTypeAsRoleName(a, true));
        }
        associationFound = matchLinkAgainstAssociation(link, a, i, od, semantic);
        i += 1;

      }
      //If this link could not be matched, the result depends on the chosen semantics
      if (!associationFound) {
        //open world semantics allows for links without match
        if (Semantic.isOpenWorld(semantic)) {
          //but not if they are specified by an association
          if (openWorldDiffFound) {
            Log.warn("Link found with diff.");
            return false;
          }
          //open world without any match means no diff
        } else {
          Log.warn(
            "No association found matching link with objects: " + link.getLeftReferenceNames());
        return false;
      }}
    }
    //iterate over all objects to check required associations
    for(ASTODObject object: ODHelper.getAllObjects(od)){
      //we can only check this for named objects since links need names for reference
      if(object instanceof ASTODNamedObject){
        ASTODNamedObject namedObj = (ASTODNamedObject) object;
        for(int i = 0; i < associations.length; i++){
          if (!checkRequiredAssociationsPresent(namedObj, cd, semantic, associations)){
            return false;
          }
        }
      }
    }

    return true;
  }

  /**
   * Orchestrates the single checks by association direction. Returns true if all checks find a
   * match. Returns false otherwise.
   * @param associationIndex the index of the association in the Array of association maps. This
   *                         is used to identify the matched association and links when counting
   *                         cardinalities.
   * @param semantic Open or closed world semantics
   */
  private boolean matchLinkAgainstAssociation(ASTODLink link, ASTCDAssociation association,
      int associationIndex, ASTObjectDiagram od, CDSemantics semantic) {

    List<ASTODObject> objects = ODHelper.getAllObjects(od);

    //check object and associated class types as well as role names (condition for open world
    // matching)
    if (checkAssociationByDirection(link, association, associationIndex, objects, semantic)) {
      //check types
      if (association.getCDAssocType().isAssociation() && link.isComposition()) {
        openWorldDiffFound = true;
        Log.warn("Link representing an association was a composition in the OD.");
        return false;
      }
      if (association.getCDAssocType().isComposition() && !link.isComposition()) {
        openWorldDiffFound = true;
        Log.warn("Link representing an association was a composition in the OD.");
        return false;
      }
      return true;
    }
    else {
      return false;
    }
  }

  /**
   * Checks links with associations by considering association direction and role names for matching
   * and then check for types.
   *
   * @param link        a link with left to right being its assumed direction
   * @param association an association with any direction
   * @param objects     a list of all objects in the od
   * @return true if the link matches the association, false otherwise
   */
  private boolean checkAssociationByDirection(ASTODLink link, ASTCDAssociation association,
      int associationIndex, List<ASTODObject> objects, CDSemantics semantic) {

    //Check direction of association (assume links are always left to right)
    boolean lTR =
        association.getCDAssocDir().isDefinitiveNavigableRight() && !association.getCDAssocDir()
            .isDefinitiveNavigableLeft();
    boolean rTL =
        association.getCDAssocDir().isDefinitiveNavigableLeft() && !association.getCDAssocDir()
            .isDefinitiveNavigableRight();

    boolean result = false;

    if (lTR) {
      result = checkRoleNamesAndExecuteDiff(true, link, association, associationIndex, objects, semantic);
    }
    else if (rTL) {
      result = checkRoleNamesAndExecuteDiff(false, link, association, associationIndex, objects, semantic);
    }
    else {

      //links all face left to right
      //check which side matches depending on association direction
      if (link.getODLinkRightSide().isPresentRole()) {
        String targetRoleName = link.getODLinkRightSide().getRole();

        if (association.getRight().getCDRole().getName().equals(targetRoleName)) {
          //association goes left to right
          result = checkRoleNamesAndExecuteDiff(true, link, association, associationIndex,
              objects,semantic);
        }
        else if (association.getLeft().isPresentCDRole() && association.getLeft()
            .getCDRole()
            .getName()
            .equals(targetRoleName)) {
          // and association goes right to left
          result = checkRoleNamesAndExecuteDiff(false, link, association, associationIndex,
              objects, semantic);
        }
        if (!result) {
          Log.warn("No association side matched the target role name: " + targetRoleName);
        }
      }
      else {
        Log.error("Err: Link did not have a target role name. Left: " + link.getLeftReferenceNames()
            + " Right: " + link.getRightReferenceNames());
      }
    }
    return result;
  }

  private boolean checkRoleNamesAndExecuteDiff(boolean isLeftToRight, ASTODLink link,
      ASTCDAssociation association, int associationIndex, List<ASTODObject> objects,
      CDSemantics semantic) {

    ASTODLinkRightSide linkRight = link.getODLinkRightSide();
    ASTODLinkLeftSide linkLeft = link.getODLinkLeftSide();
    ASTCDAssocRightSide associationRight = association.getRight();
    ASTCDAssocLeftSide associationLeft = association.getLeft();

    if (isLeftToRight) {
      if (linkRight.isPresentRole()) {

        //set association name in case none is present
        if (!associationRight.isPresentCDRole()) {
          associationRight = (ASTCDAssocRightSide) setAssociationTypeAsRoleName(association, false);
          association.setRight(associationRight);
        }
        if (associationRight.getCDRole().getName().equals(linkRight.getRole())) {
          if (!diffObjectsTypeBySideOfAssociation(link, objects, false, false, association,
              semantic)) {
            return false;
          }
        }
        else {
          return false;
        }
        //the origin role name, if not present doesn't matter, but a warning should be logged
        if (!associationLeft.isPresentCDRole() || !linkLeft.isPresentRole()) {
          Log.warn("Compared link or association had no role name in origin or names didn't match. "
              + "link: " + link.getLeftReferenceNames() + "Association: "
              + associationRight.getCDRole().getName());
        }
        else if (!associationLeft.getCDRole().getName().equals(linkLeft.getRole())) {
          //if role names are present in the origin, they need to match
          return false;
        }
        if (!diffObjectsTypeBySideOfAssociation(link, objects, true, true, association, semantic)) {
          return false;
        }
        //Add links to keep track of which association is represented
        this.rightMap.merge(associationIndex, linkRight.getReferenceNamesList(), (old, n) -> {
          old.addAll(n);
          return old;
        });
        this.leftMap.merge(associationIndex, linkLeft.getReferenceNamesList(), (o, n) -> {
          o.addAll(n);
          return o;
        });

      }
      else {
        Log.error("Err: Link did not have a target role name. Left: " + link.getLeftReferenceNames()
            + " Right: " + link.getRightReferenceNames());
        return false;
      }
    }
    //if not left to right, the opposite sides of link and association have to be checked
    else {
        //associations are allowed to not have roles specified, but then should have their type
        // in lower cases set as role
        if (!associationLeft.isPresentCDRole()) {
          associationLeft = (ASTCDAssocLeftSide) setAssociationTypeAsRoleName(association, true);
          association.setLeft(associationLeft);
        }
        //check diff, iff role names in navigation direction match
        if (associationLeft.getCDRole().getName().equals(linkRight.getRole())) {
          //open/closed world diff??
          if (!diffObjectsTypeBySideOfAssociation(link, objects, false, true, association, semantic)) {
            return false;
          }
        }
        else {
          return false;
        }
        if (associationRight.isPresentCDRole() && linkLeft.isPresentRole()) {
          if (!associationRight.getCDRole().getName().equals(linkLeft.getRole())) {
            Log.warn("Compared link or association had no role name in origin. link: "
                + link.getRightReferenceNames() + "Association: " + associationRight.getCDRole()
                .getName());
          }
        }
        if (!diffObjectsTypeBySideOfAssociation(link, objects, true, false, association, semantic)) {
          return false;
        }
        //only add referenced objects to maps, if they are required by cardinality
        if(isRequiredDueToCardinality(association)){
        this.rightMap.merge(associationIndex,
            linkLeft.getReferenceNamesList(),
            (old, n) -> {
          old.addAll(n);
          return old;
        });
        this.leftMap.merge(associationIndex, linkRight.getReferenceNamesList(), (old, n) -> {
          old.addAll(n);
          return old;
        });
        }
      }

    //check single link cardinality
    if(checkCardinality(link, association)) {
      return true;
    } else {
      openWorldDiffFound = true;
      return false;
    }
  }

  /**
   *  Compares the objects referenced in one side of a link with an association side definition by
   *  comparing the types with the allowed classes for the association.
   * @param link A link referencing objects by name
   * @param objects A list of all objects to find the type of the referenced object names
   * @param leftLinkSide true if the links left side should be compared
   * @param leftAssociationSide true if the left association side should be compared
   * @param association An association defining possible links.
   * @return true iff the referenced objects are of types allowed by the association.
   */
  private boolean diffObjectsTypeBySideOfAssociation(ASTODLink link, List<ASTODObject> objects,
      boolean leftLinkSide, boolean leftAssociationSide, ASTCDAssociation association,
      CDSemantics semantic) {

    //check types
    List<String> objectNames;
    if (leftLinkSide) {
      objectNames = link.getLeftReferenceNames();
    }
    else {
      objectNames = link.getRightReferenceNames();
    }

    for (String s : objectNames) {
      //get astNode by object name
      Optional<ASTODObject> obj = objects.stream().filter(o -> o.getName().equals(s)).findFirst();
      if (obj.isPresent()) {
        //match types
        String associationType = getAssociationTypeBySide(association, leftAssociationSide);

        if (!isOAndCTypeMatch(cd, (ASTODNamedObject) obj.get(), associationType, semantic)) {
          return false;
        }
      }
      else {
        Log.warn("No Object with name " + s + " was found in object list.");
        return false;
      }
    }
    return true;
  }

  /**
   * Method to check if any given object has a missing association that is required by the cd if
   * the object exists.
   */
  private boolean checkRequiredAssociationsPresent(ASTODNamedObject object,
      ASTCDCompilationUnit cd, CDSemantics semantic, ASTCDAssociation[] associationArray){
    //retrieve all associations that have to exist iff an object of its type is instantiated
    List<ASTCDAssociation> associations =
        cd.getCDDefinition().getCDAssociationsList().stream().filter(
                this::isRequiredDueToCardinality)
            //then filter for associations that match the objects type on either side
            .filter(req -> isOAndCTypeMatch(cd, object, getAssociationTypeBySide(req, true),
                semantic)|| isOAndCTypeMatch(cd, object, getAssociationTypeBySide(req, false), semantic))
            .collect(Collectors.toList());

    boolean result = false;

    //iterate over the required associations for the object
    for(ASTCDAssociation assoc : associations){
      for(int i = 0; i < associationArray.length; i++) {
        //find the indices of the required associations and check if they are referenced
        if(assoc.equals(associationArray[i])){
          result = (isObjectReferencedForAssociationindex(object, i, leftMap) ||
              isObjectReferencedForAssociationindex(object, i, rightMap));
        }
      }
      //if there is no reference for the required association, false is returned
      if(!result){
        Log.warn("The object " + object.getName() + " was missing a required association:" + new CDAssociationFullPrettyPrinter(new IndentPrinter()).prettyprint(assoc));
        return false;
      }
    }
    //a reference to the object was found for every required association
    return true;

  }

  private boolean isObjectReferencedForAssociationindex(ASTODNamedObject object,
      int index, Map<Integer, List<ASTODName>> map){
    if(map.containsKey(index)){
      return map.get(index).stream().anyMatch(n -> n.getName().equals(object.getName()));
    } else {
      return false;
    }
  }

  /**
   * Matches the given object with the class name given. Returns true iff the objects type is
   * the given class or a subclass of it. For open world semantics the object has to have the
   * <<instanceof>> stereotype set for any additional super types. Otherwise the type match
   * might fail for added types.
   * @param cd cd compilation unit to check in
   * @param object the object which should be matched
   * @param cdType the class name that should be matched
   * @param semantic the used semantic to determine diffs strategy
   */
  private boolean isOAndCTypeMatch(ASTCDCompilationUnit cd, ASTODNamedObject object,
      String cdType, CDSemantics semantic) {

    Log.debug("Do type check for association with type " + cdType + " and object " + object.getName(), "AssociationMatcherLog");
    String odType = pp.prettyprint(object.getMCObjectType());

    CD4CodeMill.reset();
    CD4CodeMill.globalScope().clear();
    CD4CodeMill.init();
    CD4CodeMill.globalScope().init();

    ICD4CodeArtifactScope scope = CD4CodeMill.scopesGenitorDelegator().createFromAST(cd);
    Set<String> objectSuperTypes = MultiInstanceMatcher.getSuperSet(odType, scope);

    //if present, make use of the instanceOf stereotype
    if(Semantic.isOpenWorld(semantic)){
      ASTModifier modifier = object.getModifier();
      if(modifier.isPresentStereotype()){
        if(modifier.getStereotype().contains(INSTANCE_OF_STEREOTYPE)){
          String instanceStereotype = modifier.getStereotype().getValue(INSTANCE_OF_STEREOTYPE);
          List<String> instances =
              Arrays.stream(instanceStereotype.split(",")).collect(Collectors.toList());
          instances.addAll(objectSuperTypes);
          //in the open world case, with existing "instanceOf" stereotype, we know that the
          // objects type has to inherit from the given class type. Otherwise we can only tell
          // for types present in closed world scenario
          if(instances.stream().anyMatch(sup -> sup.equals(cdType))){
            return true;
          }else {
            openWorldDiffFound = true;
            return false;
          }
        }
      }
    }

    //Without the stereotype the types have to be checked the usual way and open world diffs
    // can't be determined here
    if(objectSuperTypes.stream().anyMatch(sup -> sup.equals(cdType))){
      return true;
    } else {
      Log.warn("Object of type: " + odType + " did not match class type: " + cdType + " or any"
          + " Subtypes of it.");
      return false;
    }
  }

  private String getAssociationTypeBySide(ASTCDAssociation association, boolean left) {
    if (left) {
      return pp.prettyprint(association.getLeft().getMCQualifiedType());
    }
    else {
      return pp.prettyprint((association.getRight().getMCQualifiedType()));
    }
  }


  private boolean checkCardinality(ASTODLink link, ASTCDAssociation assoc) {

    List<String> left = link.getLeftReferenceNames();
    List<String> right = link.getRightReferenceNames();

    boolean leftOK = true;
    boolean rightOK = true;

    boolean lTR = assoc.getCDAssocDir().isDefinitiveNavigableRight() && !assoc.getCDAssocDir()
        .isDefinitiveNavigableLeft();
    boolean rTL = assoc.getCDAssocDir().isDefinitiveNavigableLeft() && !assoc.getCDAssocDir()
        .isDefinitiveNavigableRight();

    //Those lists are the combined references of all links matching the same association
    long leftNo = left.stream().distinct().count();
    long rightNo = right.stream().distinct().count();

    if (assoc.getLeft().isPresentCDCardinality()) {
      ASTCDCardinality leftCard = assoc.getLeft().getCDCardinality();
      if (lTR) {
        leftOK = compareNumberWithCardinality(leftCard, leftNo);
      }
      else if (rTL) {
        leftOK = compareNumberWithCardinality(leftCard, rightNo);
      } else {
        //undirected or bidirectional so check for role names, both roles have to be present
        if(assoc.getLeft().isPresentCDRole() && assoc.getRight().isPresentCDRole()) {
          //the link represents the right to left side of the bidirectional association
          if(assoc.getLeft().getCDRole().getName().equals(link.getODLinkRightSide().getRole())){
            leftOK = compareNumberWithCardinality(leftCard, rightNo);
          }
        } else {
          Log.error("Bidirectional Associations have to have roles in both directions.");
        }
      }
    }

    if (assoc.getRight().isPresentCDCardinality()) {
      ASTCDCardinality rightCard = assoc.getRight().getCDCardinality();
      if (lTR) {
        rightOK = compareNumberWithCardinality(rightCard, rightNo);
      }
      else if (rTL) {
        rightOK = compareNumberWithCardinality(rightCard, leftNo);
      } else {
        //undirected or bidirectional so check for role names, both roles have to be present
        if(assoc.getLeft().isPresentCDRole() && assoc.getRight().isPresentCDRole()) {
          //the link represents the left to right side of the bidirectional association
          if(assoc.getRight().getCDRole().getName().equals(link.getODLinkRightSide().getRole())){
            rightOK = compareNumberWithCardinality(rightCard, rightNo);
          }
        } else {
          Log.error("Bidirectional Associations have to have roles in both directions.");
        }
      }
    }

    return leftOK && rightOK;
  }

  /**
   * Sets the associations' role to the type of the side. The affected association side is
   * determined by the isLeftSide flag.
   * @param association an association that should have its role name changed
   * @param isLeftSide pass in true, if the left association role is supposed to be changed.
   * @return TODO
   */
  private ASTCDAssocSide setAssociationTypeAsRoleName(ASTCDAssociation association,
      boolean isLeftSide) {
    if (isLeftSide) {
      ASTCDAssocLeftSide left = association.getLeft();
      left.setCDRole(
          new ASTCDRoleBuilder().setName(getAssociationTypeBySide(association, true).toLowerCase())
              .build());
      return left;
    }
    else {
      ASTCDAssocRightSide right = association.getRight();
      right.setCDRole(
          new ASTCDRoleBuilder().setName(getAssociationTypeBySide(association, false).toLowerCase())
              .build());
      return right;
    }
  }

  private boolean compareNumberWithCardinality(ASTCDCardinality card, long elements) {

    //is *
    if (card.isMult()) {
      return true;
    }
    else
      //is +
      if (card.isAtLeastOne()) {
        if (elements < 1) {
          Log.warn("Link violates cardinality \"at least one [+]\" constraint.");
          return false;
        }
        else {
          return true;
        }
      }
      else
        //is bound
        if (card.isOne()) {
          if (elements != 1) {
            Log.warn("Link violates cardinality [1] constraint.");
            return false;
          }
          else {
            return true;
          }
        }
        else {
          if (elements > card.getUpperBound() && elements < card.getLowerBound()) {
            Log.warn("Link violates cardinality boundary constraint.");
            return false;
          }
          else {
            return true;
          }
        }
  }

  /**
   * Checks if an association is required due to its cardinalities. Eg. A[+] -> B[+] would have
   * to be represented by a link, since both sides have to reference at least one object.
   * @return true if the association is required.
   */
  private boolean isRequiredDueToCardinality(ASTCDAssociation assoc){
    //only if both sides have at least one cardinality, they are required
    return hasCardinalityAtLeastGreaterZero(assoc.getRight()) && hasCardinalityAtLeastGreaterZero(assoc.getLeft());
  }
  /**
   * Helper method to check if there has to be at least one object linked to fit this association.
   */
  private boolean hasCardinalityAtLeastGreaterZero(ASTCDAssocSide side) {
    if(side.isPresentCDCardinality()){
      ASTCDCardinality card = side.getCDCardinality();
      return card.isAtLeastOne() || card.isOne();
    } else {
      return false;
    }
  }

}