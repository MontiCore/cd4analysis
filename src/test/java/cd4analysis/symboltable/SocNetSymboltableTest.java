/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package cd4analysis.symboltable;

import cd4analysis.CD4AnalysisLanguage;
import cd4analysis.symboltable.references.CDTypeSymbolReference;
import de.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.symboltable.CompilationUnitScope;
import de.monticore.symboltable.ResolverConfiguration;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.SymbolKind;
import de.monticore.symboltable.resolving.DefaultResolver;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class SocNetSymboltableTest {

  final static String PACKAGE = "cd4analysis.symboltable.SocNet.";
  private Scope topScope;

  @Test
  public void testSocNet() {
    ResolverConfiguration resolverConfiguration = new ResolverConfiguration();
    resolverConfiguration.addTopScopeResolvers(DefaultResolver.newResolver(CDTypeSymbol.class,
        CDTypeSymbol.KIND));
    resolverConfiguration.addTopScopeResolvers(DefaultResolver.newResolver(CDFieldSymbol.class,
        CDFieldSymbol.KIND));
    resolverConfiguration.addTopScopeResolvers(DefaultResolver.newResolver(CDAssociationSymbol.class,
        CDAssociationSymbol.KIND));

    CD4AnalysisLanguage cdLanguage = new CD4AnalysisLanguage();

    ASTCDCompilationUnit compilationUnit;
    try {
      compilationUnit = cdLanguage.getParser().parse
          ("src/test/resources/cd4analysis/symboltable/SocNet.cd").get();
      assertNotNull(compilationUnit);
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }


    topScope = cdLanguage.getSymbolTableCreator(resolverConfiguration, null).get()
        .createFromAST(compilationUnit);

    assertTrue(topScope instanceof CompilationUnitScope);

    assertEquals(31, topScope.getSymbols().size());
    assertEquals(31, topScope.resolveLocally(SymbolKind.INSTANCE).size());

    // TODO PN test types of all fields
    CDTypeSymbol profile = testProfileType();

    testPersonType(profile);
    testGroupType(profile);

    testMemberAssociation();

  }



  private CDTypeSymbol testProfileType() {
    CDTypeSymbol profile = topScope.<CDTypeSymbol>resolve("Profile", CDTypeSymbol.KIND).orNull();
    assertNotNull(profile);
    assertEquals(PACKAGE + "Profile", profile.getName());
    assertTrue(profile.isAbstract());
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

  private void testPersonType(CDTypeSymbol profile) {
    CDTypeSymbol person = topScope.<CDTypeSymbol>resolve("Person", CDTypeSymbol.KIND).orNull();
    assertNotNull(person);
    assertEquals(PACKAGE + "Person", person.getName());
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

  private void testGroupType(CDTypeSymbol profile) {
    CDTypeSymbol group = topScope.<CDTypeSymbol>resolve("Group", CDTypeSymbol.KIND).orNull();
    assertNotNull(group);
    assertEquals(PACKAGE + "Group", group.getName());
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
    assertTrue(groupAssoc.isBidirectional());
    assertEquals(PACKAGE + "Person", groupAssoc.getSourceType().getName());
    assertEquals(PACKAGE + "Group", groupAssoc.getTargetType().getName());
    assertEquals(Cardinality.STAR, groupAssoc.getSourceCardinality().getMax());
    assertTrue(groupAssoc.getSourceCardinality().isMultiple());
    assertEquals(Cardinality.STAR, groupAssoc.getTargetCardinality().getMax());
    assertTrue(groupAssoc.getTargetCardinality().isMultiple());

    // Person <- Group
    CDAssociationSymbol personAssoc = topScope.<CDAssociationSymbol>resolve("person",
        CDAssociationSymbol.KIND).orNull();
    assertNotNull(personAssoc);
    assertEquals("person", personAssoc.getName());
    assertTrue(personAssoc.isBidirectional());
    assertEquals(PACKAGE + "Group", personAssoc.getSourceType().getName());
    assertEquals(PACKAGE + "Person", personAssoc.getTargetType().getName());
    assertEquals(Cardinality.STAR, personAssoc.getSourceCardinality().getMax());
    assertTrue(personAssoc.getSourceCardinality().isMultiple());
    assertEquals(Cardinality.STAR, personAssoc.getTargetCardinality().getMax());
    assertTrue(personAssoc.getTargetCardinality().isMultiple());
  }

  private void testOrginaizersAssociation() {
    CDAssociationSymbol associationSymbol = topScope.<CDAssociationSymbol>resolve("member",
        CDAssociationSymbol.KIND).orNull();
    assertNotNull(associationSymbol);
    assertEquals("member", associationSymbol.getName());
    assertTrue(associationSymbol.isBidirectional());
    assertEquals(PACKAGE + "Person", associationSymbol.getSourceType().getName());
    assertEquals(PACKAGE + "Group", associationSymbol.getTargetType().getName());
    assertEquals(Cardinality.STAR, associationSymbol.getSourceCardinality().getMax());
    assertTrue(associationSymbol.getSourceCardinality().isMultiple());
    assertEquals(Cardinality.STAR, associationSymbol.getTargetCardinality().getMax());
    assertTrue(associationSymbol.getTargetCardinality().isMultiple());
  }
}
