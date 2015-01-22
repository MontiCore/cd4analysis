/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package cd4analysis.symboltable;

import cd4analysis.symboltable.references.CDTypeSymbolReference;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Symbol;
import org.junit.Test;

import static org.junit.Assert.*;

public class SocNetSymboltableTest {

  final static String PACKAGE = "cd4analysis.symboltable.SocNet.";
  private Scope topScope;

  @Test
  public void testSocNet() {
    GlobalScope globalScope = CD4AGlobalScopeTestFactory.create();

    // Besides resolving the Profile symbol the symbol table is initialized
    globalScope.resolve(PACKAGE + "Profile", CDTypeSymbol.KIND);

    // Continue with compilationScope. Else, if globalScope was used, all symbols had to be
    // resolved by their qualified name.
    topScope = globalScope.getSubScopes().get(0);

    assertEquals(31, topScope.getSymbols().size());
    assertEquals(31, topScope.resolveLocally(Symbol.KIND).size());

    // TODO PN test types of all fields

    CDTypeSymbol profile = testProfileClass();

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
    CDTypeSymbol profile = topScope.<CDTypeSymbol>resolve("Profile", CDTypeSymbol.KIND).orNull();
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
    CDTypeSymbol person = topScope.<CDTypeSymbol>resolve("Person", CDTypeSymbol.KIND).orNull();
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
  }

  private void testGroupClass(CDTypeSymbol profile) {
    CDTypeSymbol group = topScope.<CDTypeSymbol>resolve("Group", CDTypeSymbol.KIND).orNull();
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
    CDAssociationSymbol groupAssoc = topScope.<CDAssociationSymbol>resolve("group",
        CDAssociationSymbol.KIND).orNull();
    assertNotNull(groupAssoc);
    assertEquals("group", groupAssoc.getName());
    assertEquals("member", groupAssoc.getAssocName());
    assertTrue(groupAssoc.isBidirectional());
    assertEquals(PACKAGE + "Person", groupAssoc.getSourceType().getFullName());
    assertEquals(PACKAGE + "Group", groupAssoc.getTargetType().getFullName());
    assertEquals(Cardinality.STAR, groupAssoc.getSourceCardinality().getMax());
    assertTrue(groupAssoc.getSourceCardinality().isMultiple());
    assertEquals(Cardinality.STAR, groupAssoc.getTargetCardinality().getMax());
    assertTrue(groupAssoc.getTargetCardinality().isMultiple());

    // Person <- Group
    CDAssociationSymbol personAssoc = topScope.<CDAssociationSymbol>resolve("person",
        CDAssociationSymbol.KIND).orNull();
    assertNotNull(personAssoc);
    assertEquals("person", personAssoc.getName());
    assertEquals("member", personAssoc.getAssocName());
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
    CDAssociationSymbol organizedAssoc = topScope.<CDAssociationSymbol>resolve("organized",
        CDAssociationSymbol.KIND).orNull();
    assertNotNull(organizedAssoc);
    assertEquals("organized", organizedAssoc.getName());
    assertEquals("organized", organizedAssoc.getRole());
    assertTrue(organizedAssoc.getAssocName().isEmpty());
    assertTrue(organizedAssoc.isBidirectional());
    assertEquals(PACKAGE + "Person", organizedAssoc.getSourceType().getFullName());
    assertEquals(PACKAGE + "Group", organizedAssoc.getTargetType().getFullName());
    assertEquals(1, organizedAssoc.getSourceCardinality().getMax());
    assertFalse(organizedAssoc.getSourceCardinality().isMultiple());
    assertEquals(Cardinality.STAR, organizedAssoc.getTargetCardinality().getMax());
    assertTrue(organizedAssoc.getTargetCardinality().isMultiple());

    // <-
    CDAssociationSymbol organizerAssoc = topScope.<CDAssociationSymbol>resolve("organizer",
        CDAssociationSymbol.KIND).orNull();
    assertNotNull(organizerAssoc);
    assertEquals("organizer", organizerAssoc.getName());
    assertEquals("organizer", organizerAssoc.getRole());
    assertTrue(organizerAssoc.getAssocName().isEmpty());
    assertTrue(organizerAssoc.isBidirectional());
    assertEquals(PACKAGE + "Group", organizerAssoc.getSourceType().getFullName());
    assertEquals(PACKAGE + "Person", organizerAssoc.getTargetType().getFullName());
    assertEquals(Cardinality.STAR, organizerAssoc.getSourceCardinality().getMax());
    assertTrue(organizerAssoc.getSourceCardinality().isMultiple());
    assertEquals(1, organizerAssoc.getTargetCardinality().getMax());
    assertFalse(organizerAssoc.getTargetCardinality().isMultiple());
  }

  private void testRelationshipClass() {
    CDTypeSymbol relationship = topScope.<CDTypeSymbol>resolve("Relationship", CDTypeSymbol.KIND)
        .orNull();
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
//    CDAssociationSymbol organizedAssoc = topScope.<CDAssociationSymbol>resolve("profile",
//        CDAssociationSymbol.KIND).orNull();
//    assertNotNull(organizedAssoc);
    // TODO PN continue

    // <-
  }

  private void testRelationTypeEnum() {
    CDTypeSymbol relationship = topScope.<CDTypeSymbol>resolve("RelationType", CDTypeSymbol.KIND)
        .orNull();
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
    CDAssociationSymbol assoc = topScope.<CDAssociationSymbol>resolve("relationType",
        CDAssociationSymbol.KIND).orNull();
    assertNotNull(assoc);
    assertEquals("relationType", assoc.getName());
    assertTrue(assoc.getRole().isEmpty());
    assertTrue(assoc.getAssocName().isEmpty());
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
    CDTypeSymbol post = topScope.<CDTypeSymbol>resolve("Post", CDTypeSymbol.KIND).orNull();
    assertNotNull(post);
    assertEquals(PACKAGE + "Post", post.getFullName());
    assertTrue(post.isAbstract());
    assertTrue(post.isInterface());
  }

  private void testReceivedAssociation() {
    // ->
    // TODO PN ambiguous exception is thrown, because two associations target Profile, etc. -> Profile
//    CDAssociationSymbol postAssoc = topScope.<CDAssociationSymbol>resolve("post",
//        CDAssociationSymbol.KIND).orNull();
//    assertNotNull(postAssoc);
    // TODO PN continue

    // <-
//    CDAssociationSymbol profileAssoc = topScope.<CDAssociationSymbol>resolve("profile",
//        CDAssociationSymbol.KIND).orNull();
//    assertNotNull(profileAssoc);
//    assertEquals("organizer", profileAssoc.getName());
    // TODO PN continue
  }

  private void testInstantMessageClass() {
    CDTypeSymbol symbol = topScope.<CDTypeSymbol>resolve("InstantMessage", CDTypeSymbol.KIND).orNull();
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
    CDTypeSymbol symbol = topScope.<CDTypeSymbol>resolve("PhotoMessage", CDTypeSymbol.KIND).orNull();
    assertNotNull(symbol);
    assertEquals(PACKAGE + "PhotoMessage", symbol.getFullName());
    assertTrue(symbol.getSuperClass().isPresent());
    assertEquals(PACKAGE + "InstantMessage", symbol.getSuperClass().get().getFullName());
    assertEquals(0, symbol.getFields().size());
  }

  private void testPhotoAssociation() {
    // ->
    CDAssociationSymbol photoMessageAssoc = topScope.<CDAssociationSymbol>resolve("photoMessage",
        CDAssociationSymbol.KIND).orNull();
    assertNotNull(photoMessageAssoc);
    assertTrue(photoMessageAssoc.getRole().isEmpty());
    assertEquals("photoMessage", photoMessageAssoc.getName());
    assertTrue(photoMessageAssoc.getAssocName().isEmpty());
    assertTrue(photoMessageAssoc.isBidirectional());
    assertEquals(PACKAGE + "Photo", photoMessageAssoc.getSourceType().getFullName());
    assertEquals(PACKAGE + "PhotoMessage", photoMessageAssoc.getTargetType().getFullName());
    assertEquals(Cardinality.STAR, photoMessageAssoc.getSourceCardinality().getMax());
    assertTrue(photoMessageAssoc.getSourceCardinality().isMultiple());
    assertEquals(1, photoMessageAssoc.getTargetCardinality().getMax());
    assertFalse(photoMessageAssoc.getTargetCardinality().isMultiple());

    // <-
    CDAssociationSymbol photoAssoc = topScope.<CDAssociationSymbol>resolve("picture",
        CDAssociationSymbol.KIND).orNull();
    assertNotNull(photoAssoc);
    assertEquals("picture", photoAssoc.getName());
    assertEquals("picture", photoAssoc.getRole());
    assertTrue(photoAssoc.getAssocName().isEmpty());
    assertTrue(photoAssoc.isBidirectional());
    assertEquals(PACKAGE + "PhotoMessage", photoAssoc.getSourceType().getFullName());
    assertEquals(PACKAGE + "Photo", photoAssoc.getTargetType().getFullName());
    assertEquals(1, photoAssoc.getSourceCardinality().getMax());
    assertFalse(photoAssoc.getSourceCardinality().isMultiple());
    assertEquals(Cardinality.STAR, photoAssoc.getTargetCardinality().getMax());
    assertTrue(photoAssoc.getTargetCardinality().isMultiple());
  }

  private void testPhotoClass() {
    CDTypeSymbol photo = topScope.<CDTypeSymbol>resolve("Photo", CDTypeSymbol.KIND).orNull();
    assertNotNull(photo);
    assertEquals(PACKAGE + "Photo", photo.getFullName());
    assertFalse(photo.getSuperClass().isPresent());
    assertEquals(2, photo.getFields().size());
    assertTrue(photo.getField("height").isPresent());
    assertTrue(photo.getField("width").isPresent());
  }

  private void testTagClass() {
    CDTypeSymbol photo = topScope.<CDTypeSymbol>resolve("Tag", CDTypeSymbol.KIND).orNull();
    assertNotNull(photo);
    assertEquals(PACKAGE + "Tag", photo.getFullName());
    assertFalse(photo.getSuperClass().isPresent());
    assertEquals(1, photo.getFields().size());
    assertTrue(photo.getField("confirmed").isPresent());
  }

  // TODO PN test last to associations as soon as ambiguous problem with associations is solved.

}
