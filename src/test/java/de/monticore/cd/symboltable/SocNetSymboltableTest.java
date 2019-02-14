/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.cd.symboltable;

import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.modifiers.BasicAccessModifier;
import de.monticore.cd.symboltable.CDAssociationNameAndTargetNamePredicate;
import de.monticore.cd.symboltable.CDAssociationSymbol;
import de.monticore.cd.symboltable.CDFieldSymbol;
import de.monticore.cd.symboltable.CDSymbol;
import de.monticore.cd.symboltable.CDTypeSymbol;
import de.monticore.cd.symboltable.Cardinality;
import de.monticore.cd.symboltable.references.CDTypeSymbolReference;
import org.junit.Test;

import static org.junit.Assert.*;

public class SocNetSymboltableTest {

  final static String CD_FQN = "de.monticore.umlcd4a.symboltable.SocNet";
  final static String PACKAGE = CD_FQN + ".";

  private CDSymbol cdSymbol;
  private Scope cdScope;

  @Test
  public void testSocNet() {
    final GlobalScope globalScope = CD4AGlobalScopeTestFactory.create();

    // Besides resolving the Profile symbol the symbol table is initialized
    cdSymbol = globalScope.<CDSymbol>resolve(CD_FQN, CDSymbol.KIND).orElse(null);
    assertNotNull(cdSymbol);

    cdScope = cdSymbol.getSpannedScope();

    assertEquals(31, cdScope.getSymbolsSize());

    final CDTypeSymbol profile = testProfileClass();

    testPersonClass(profile);
    testGroupClass(profile);

    testMemberAssociation();
    testOrginaizersAssociation();

    testRelationshipClass();
    testInvitedAssociation();

    testRelationTypeEnum();
    testRelationTypeAssociation();

    testPostInterface();

    testReceivedAssociation();
    testInstantMessageClass();
    testPhotoMessageClass();

    testPhotoAssociation();
    testPhotoClass();
    testTagClass();

  }

  private CDTypeSymbol testProfileClass() {
    CDTypeSymbol profile = cdSymbol.getType("Profile").orElse(null);
    assertNotNull(profile);
    assertEquals(PACKAGE + "Profile", profile.getFullName());
    assertTrue(profile.isAbstract());
    assertFalse(profile.isInterface());
    assertFalse(profile.getSuperClass().isPresent());
    assertEquals(3, profile.getFields().size());

    CDFieldSymbol profileNameField = profile.getField("profileName").get();
    assertEquals("profileName", profileNameField.getName());
    assertFalse(profileNameField.isDerived());

    CDFieldSymbol numOfPostsField = profile.getField("numOfPosts").get();
    assertEquals("numOfPosts", numOfPostsField.getName());
    assertTrue(numOfPostsField.isDerived());

    CDFieldSymbol friendsField = profile.getField("friends").get();
    assertEquals("friends", friendsField.getName());
    assertTrue(friendsField.isDerived());
    return profile;
  }

  private void testPersonClass(CDTypeSymbol profile) {
    CDTypeSymbol person = cdSymbol.getType("Person").orElse(null);
    assertNotNull(person);
    assertEquals(PACKAGE + "Person", person.getFullName());
    assertTrue(person.getSuperClass().isPresent());
    assertSame(profile, ((CDTypeSymbolReference) (person.getSuperClass().get()))
        .getReferencedSymbol());
    assertEquals(profile.getName(), person.getSuperClass().get().getName());
    assertEquals(7, person.getFields().size());
    assertTrue(person.getField("lastVisit").isPresent());
    assertTrue(person.getField("firstName").isPresent());
    assertTrue(person.getField("secondName").isPresent());
    assertTrue(person.getField("dateOfBirth").isPresent());
    assertTrue(person.getField("zip").isPresent());
    assertTrue(person.getField("city").isPresent());
    assertTrue(person.getField("country").isPresent());

    assertEquals(3, person.getAssociations().size());
  }

  private void testGroupClass(CDTypeSymbol profile) {
    CDTypeSymbol group = cdSymbol.getType("Group").orElse(null);
    assertNotNull(group);
    assertEquals(PACKAGE + "Group", group.getFullName());
    assertTrue(group.getSuperClass().isPresent());
    assertSame(profile, ((CDTypeSymbolReference) (group.getSuperClass().get()))
        .getReferencedSymbol());
    assertEquals(profile.getName(), group.getSuperClass().get().getName());
    assertEquals(4, group.getFields().size());
    assertTrue(group.getField("isOpen").isPresent());
    assertTrue(group.getField("created").isPresent());
    assertTrue(group.getField("purpose").isPresent());
    assertTrue(group.getField("members").isPresent());
    assertTrue(group.getField("members").get().isDerived());
  }

  private void testMemberAssociation() {
    // Person -> Group
    CDAssociationSymbol groupAssoc = (CDAssociationSymbol) cdScope.resolve("member", CDAssociationSymbol.KIND, BasicAccessModifier.ALL_INCLUSION,
        new CDAssociationNameAndTargetNamePredicate("member", "Group")).orElse(null);
    assertNotNull(groupAssoc);
    assertEquals("member", groupAssoc.getName());
    assertEquals("member", groupAssoc.getAssocName().orElse(""));
    assertTrue(groupAssoc.isBidirectional());
    assertEquals(PACKAGE + "Person", groupAssoc.getSourceType().getFullName());
    assertEquals(PACKAGE + "Group", groupAssoc.getTargetType().getFullName());
    assertEquals(Cardinality.STAR, groupAssoc.getSourceCardinality().getMax());
    assertTrue(groupAssoc.getSourceCardinality().isMultiple());
    assertEquals(Cardinality.STAR, groupAssoc.getTargetCardinality().getMax());
    assertTrue(groupAssoc.getTargetCardinality().isMultiple());

    // Person <- Group
    CDAssociationSymbol personAssoc = (CDAssociationSymbol) cdScope.resolve("member", CDAssociationSymbol.KIND, BasicAccessModifier.ALL_INCLUSION,
        new CDAssociationNameAndTargetNamePredicate("member", "Person")).orElse(null);
    assertNotNull(personAssoc);
    assertEquals("member", personAssoc.getName());
    assertEquals("member", personAssoc.getAssocName().orElse(""));
    assertTrue(personAssoc.isBidirectional());
    assertEquals(PACKAGE + "Group", personAssoc.getSourceType().getFullName());
    assertEquals(PACKAGE + "Person", personAssoc.getTargetType().getFullName());
    assertEquals(Cardinality.STAR, personAssoc.getSourceCardinality().getMax());
    assertTrue(personAssoc.getSourceCardinality().isMultiple());
    assertEquals(Cardinality.STAR, personAssoc.getTargetCardinality().getMax());
    assertTrue(personAssoc.getTargetCardinality().isMultiple());
  }

  private void testOrginaizersAssociation() {
    // ->
    CDTypeSymbol person = cdSymbol.getType("Person").orElse(null);
    assertNotNull(person);
    CDAssociationSymbol organizedAssoc = person.getAssociations().stream().filter(a -> a.getDerivedName().equals("organized")).findAny().orElse(null);
    assertNotNull(organizedAssoc);
    assertFalse(organizedAssoc.getAssocName().isPresent());
    assertEquals("", organizedAssoc.getName());
    assertEquals("organized", organizedAssoc.getDerivedName());
    assertEquals("organized", organizedAssoc.getTargetRole().orElse(""));
    assertFalse(organizedAssoc.getAssocName().isPresent());
    assertTrue(organizedAssoc.isBidirectional());
    assertEquals(PACKAGE + "Person", organizedAssoc.getSourceType().getFullName());
    assertEquals(PACKAGE + "Group", organizedAssoc.getTargetType().getFullName());
    assertEquals(1, organizedAssoc.getSourceCardinality().getMax());
    assertFalse(organizedAssoc.getSourceCardinality().isMultiple());
    assertEquals(Cardinality.STAR, organizedAssoc.getTargetCardinality().getMax());
    assertTrue(organizedAssoc.getTargetCardinality().isMultiple());

    // <-
    CDTypeSymbol group = cdSymbol.getType("Group").orElse(null);
    assertNotNull(group);
    CDAssociationSymbol organizerAssoc = group.getAssociations().stream().filter(a -> a.getDerivedName().equals("organizer")).findAny().orElse(null);
    assertNotNull(organizerAssoc);
    assertEquals("", organizerAssoc.getName());
    assertFalse(organizerAssoc.getAssocName().isPresent());
    assertEquals("organizer", organizerAssoc.getDerivedName());
    assertEquals("organizer", organizerAssoc.getTargetRole().orElse(""));
    assertFalse(organizerAssoc.getAssocName().isPresent());
    assertTrue(organizerAssoc.isBidirectional());
    assertEquals(PACKAGE + "Group", organizerAssoc.getSourceType().getFullName());
    assertEquals(PACKAGE + "Person", organizerAssoc.getTargetType().getFullName());
    assertEquals(Cardinality.STAR, organizerAssoc.getSourceCardinality().getMax());
    assertTrue(organizerAssoc.getSourceCardinality().isMultiple());
    assertEquals(1, organizerAssoc.getTargetCardinality().getMax());
    assertFalse(organizerAssoc.getTargetCardinality().isMultiple());
  }

  private void testRelationshipClass() {
    CDTypeSymbol relationship = cdSymbol.getType("Relationship").orElse(null);
    assertNotNull(relationship);
    assertEquals(PACKAGE + "Relationship", relationship.getFullName());
    assertFalse(relationship.getSuperClass().isPresent());
    assertEquals(3, relationship.getFields().size());
    assertTrue(relationship.getField("isPending").isPresent());
    assertTrue(relationship.getField("requested").isPresent());
    assertTrue(relationship.getField("accepted").isPresent());
  }

  private void testInvitedAssociation() {
    // ->
    // TODO PN ambiguous exception is thrown, because two associations target Profile, etc. -> Profile
//    CDAssociationSymbol organizedAssoc = cdScope.<CDAssociationSymbol>resolve("profile",
//        CDAssociationSymbol.KIND).orElse(null);
//    assertNotNull(organizedAssoc);
    // TODO PN continue

    // <-
  }

  private void testRelationTypeEnum() {
    CDTypeSymbol relationship = cdSymbol.getType("RelationType").orElse(null);
    assertNotNull(relationship);
    assertEquals(PACKAGE + "RelationType", relationship.getFullName());
    assertFalse(relationship.getSuperClass().isPresent());
    assertTrue(relationship.isEnum());
    assertEquals(5, relationship.getFields().size());
    assertEquals(5, relationship.getEnumConstants().size());
    assertTrue(relationship.getField("FRIEND").isPresent());
    assertTrue(relationship.getField("FRIEND").get().isFinal());
    assertTrue(relationship.getField("FRIEND").get().isStatic());

    assertTrue(relationship.getField("FAMILY").isPresent());
    assertTrue(relationship.getField("FOLLOWER").isPresent());
    assertTrue(relationship.getField("COLLEAGUE").isPresent());
    assertTrue(relationship.getField("OTHER").isPresent());
  }

  private void testRelationTypeAssociation() {
    // ->
    CDTypeSymbol relationship = cdSymbol.getType("Relationship").orElse(null);
    assertNotNull(relationship);
    CDAssociationSymbol assoc = relationship.getAssociations().stream().filter(a -> a.getDerivedName().equals("relationType")).findAny().orElse(null);
    assertNotNull(assoc);
    assertEquals("", assoc.getName());
    assertEquals("relationType", assoc.getDerivedName());
    assertFalse(assoc.getSourceRole().isPresent());
    assertFalse(assoc.getAssocName().isPresent());
    assertFalse(assoc.isBidirectional());
    assertEquals(PACKAGE + "Relationship", assoc.getSourceType().getFullName());
    assertEquals(PACKAGE + "RelationType", assoc.getTargetType().getFullName());
    assertEquals(1, assoc.getSourceCardinality().getMax());
    assertFalse(assoc.getSourceCardinality().isMultiple());
    assertEquals(1, assoc.getTargetCardinality().getMax());
    assertFalse(assoc.getTargetCardinality().isMultiple());

    // <- does not exist, because association is uni-directional
  }

  private void testPostInterface() {
    CDTypeSymbol post = cdSymbol.getType("Post").orElse(null);
    assertNotNull(post);
    assertEquals(PACKAGE + "Post", post.getFullName());
    assertTrue(post.isAbstract());
    assertTrue(post.isInterface());
  }

  private void testReceivedAssociation() {
    // ->
    // TODO PN ambiguous exception is thrown, because two associations target Profile, etc. -> Profile
//    CDAssociationSymbol postAssoc = cdScope.<CDAssociationSymbol>resolve("post",
//        CDAssociationSymbol.KIND).orElse(null);
//    assertNotNull(postAssoc);
    // TODO PN continue

    // <-
//    CDAssociationSymbol profileAssoc = cdScope.<CDAssociationSymbol>resolve("profile",
//        CDAssociationSymbol.KIND).orElse(null);
//    assertNotNull(profileAssoc);
//    assertEquals("organizer", profileAssoc.getName());
    // TODO PN continue
  }

  private void testInstantMessageClass() {
    CDTypeSymbol symbol = cdSymbol.getType("InstantMessage").orElse(null);
    assertNotNull(symbol);
    assertEquals(PACKAGE + "InstantMessage", symbol.getFullName());
    assertFalse(symbol.getSuperClass().isPresent());
    assertEquals(1, symbol.getInterfaces().size());
    assertEquals(PACKAGE + "Post", symbol.getInterfaces().get(0).getFullName());
    assertEquals(2, symbol.getFields().size());
    assertTrue(symbol.getField("timestamp").isPresent());
    assertTrue(symbol.getField("content").isPresent());
  }

  private void testPhotoMessageClass() {
    CDTypeSymbol symbol = cdSymbol.getType("PhotoMessage").orElse(null);
    assertNotNull(symbol);
    assertEquals(PACKAGE + "PhotoMessage", symbol.getFullName());
    assertTrue(symbol.getSuperClass().isPresent());
    assertEquals(PACKAGE + "InstantMessage", symbol.getSuperClass().get().getFullName());
    assertEquals(0, symbol.getFields().size());
  }

  private void testPhotoAssociation() {
    // ->
    CDTypeSymbol photo = cdSymbol.getType("Photo").orElse(null);
    assertNotNull(photo);
    CDAssociationSymbol photoMessageAssoc = photo.getAssociations().stream().filter(a -> a.getDerivedName().equals("photoMessage")).findAny().orElse(null);
    assertNotNull(photoMessageAssoc);
    assertEquals("", photoMessageAssoc.getName());
    assertEquals("photoMessage", photoMessageAssoc.getDerivedName());
    assertFalse(photoMessageAssoc.getTargetRole().isPresent());
    assertFalse(photoMessageAssoc.getAssocName().isPresent());
    assertTrue(photoMessageAssoc.isBidirectional());
    assertEquals(PACKAGE + "Photo", photoMessageAssoc.getSourceType().getFullName());
    assertEquals(PACKAGE + "PhotoMessage", photoMessageAssoc.getTargetType().getFullName());
    assertEquals(Cardinality.STAR, photoMessageAssoc.getSourceCardinality().getMax());
    assertTrue(photoMessageAssoc.getSourceCardinality().isMultiple());
    assertEquals(1, photoMessageAssoc.getTargetCardinality().getMax());
    assertFalse(photoMessageAssoc.getTargetCardinality().isMultiple());

    // <-
    CDTypeSymbol photoMsg = cdSymbol.getType("PhotoMessage").orElse(null);
    assertNotNull(photoMsg);
    CDAssociationSymbol photoAssoc = photoMsg.getAssociations().stream().filter(a -> a.getDerivedName().equals("picture")).findAny().orElse(null);
    assertNotNull(photoAssoc);
    assertEquals("", photoAssoc.getName());
    assertEquals("picture", photoAssoc.getDerivedName());
    assertEquals("picture", photoAssoc.getTargetRole().orElse(""));
    assertFalse(photoAssoc.getAssocName().isPresent());
    assertTrue(photoAssoc.isBidirectional());
    assertEquals(PACKAGE + "PhotoMessage", photoAssoc.getSourceType().getFullName());
    assertEquals(PACKAGE + "Photo", photoAssoc.getTargetType().getFullName());
    assertEquals(1, photoAssoc.getSourceCardinality().getMax());
    assertFalse(photoAssoc.getSourceCardinality().isMultiple());
    assertEquals(Cardinality.STAR, photoAssoc.getTargetCardinality().getMax());
    assertTrue(photoAssoc.getTargetCardinality().isMultiple());
  }

  private void testPhotoClass() {
    CDTypeSymbol photo = cdSymbol.getType("Photo").orElse(null);
    assertNotNull(photo);
    assertEquals(PACKAGE + "Photo", photo.getFullName());
    assertFalse(photo.getSuperClass().isPresent());
    assertEquals(2, photo.getFields().size());
    assertTrue(photo.getField("height").isPresent());
    assertTrue(photo.getField("width").isPresent());
  }

  private void testTagClass() {
    CDTypeSymbol photo = cdSymbol.getType("Tag").orElse(null);
    assertNotNull(photo);
    assertEquals(PACKAGE + "Tag", photo.getFullName());
    assertFalse(photo.getSuperClass().isPresent());
    assertEquals(1, photo.getFields().size());
    assertTrue(photo.getField("confirmed").isPresent());
  }

  // TODO PN test last to associations as soon as ambiguous problem with associations is solved.

}
