package de.monticore.umlcd4a.symboltable;

import de.cd4analysis._ast.ASTCDAssociation;
import de.cd4analysis._ast.ASTCDAttribute;
import de.cd4analysis._ast.ASTCDClass;
import de.cd4analysis._ast.ASTCDMethod;
import de.monticore.symboltable.ArtifactScope;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.MutableScope;
import de.monticore.umlcd4a.symboltable.references.CDTypeSymbolReference;
import org.junit.Test;

import static de.monticore.symboltable.modifiers.BasicAccessModifier.PRIVATE;
import static de.monticore.symboltable.modifiers.BasicAccessModifier.PROTECTED;
import static de.monticore.symboltable.modifiers.BasicAccessModifier.PUBLIC;
import static org.junit.Assert.*;

public class CD4AnalysisSymbolTableCreatorTest {

  @Test
  public void testSymbolTableCreation() {
    final GlobalScope globalScope = CD4AGlobalScopeTestFactory.create();


    final CDTypeSymbol personType = globalScope.<CDTypeSymbol> resolve(
        "de.monticore.umlcd4a.symboltable.CD1.Person", CDTypeSymbol.KIND).orNull();
    assertNotNull(personType);

    // Scope Hierarchy: GlobalScope -> ArtifactScope -> ClassDiagramScope ->* ...
    assertEquals(1, globalScope.getSubScopes().size());
    final ArtifactScope artifactScope = (ArtifactScope) globalScope.getSubScopes().get(0);



    assertEquals(1, artifactScope.getSubScopes().size());
    // Continue with the class diagram scope.Else, if globalScope or artifact scope was used, all
    // symbols had to be resolved by their qualified name.
    final MutableScope cdScope = artifactScope.getSubScopes().get(0);

    // TODO PN find better solution
    // Quickfix for using default types: add built-in types
    cdScope.define(new CDTypeSymbol("int"));
    cdScope.define(new CDTypeSymbol("boolean"));
    cdScope.define(new CDTypeSymbol("String"));

    assertNotNull(personType.getSpannedScope());
    assertSame(personType, personType.getSpannedScope().getSpanningSymbol().get());
    assertEquals("Person", personType.getName());
    assertEquals("de.monticore.umlcd4a.symboltable.CD1.Person", personType.getFullName());
    assertEquals("de.monticore.umlcd4a.symboltable", personType.getPackageName());
    assertTrue(personType.isPublic());
    // AST
    assertTrue(personType.getAstNode().isPresent());
    assertTrue(personType.getAstNode().get() instanceof ASTCDClass);
    // Associations
    assertEquals(1, personType.getAssociations().size());
    // Fields
    assertEquals(3, personType.getFields().size());
    final CDFieldSymbol nameField = personType.getField("name").get();
    assertEquals("name", nameField.getName());
    assertTrue(nameField.isPublic());
    assertEquals("de.monticore.umlcd4a.symboltable.CD1.Person.name", nameField.getFullName());
    assertEquals("de.monticore.umlcd4a.symboltable", nameField.getPackageName());
    assertEquals("String", nameField.getType().getName());
    // AST
    assertTrue(nameField.getAstNode().isPresent());
    assertTrue(nameField.getAstNode().get() instanceof ASTCDAttribute);
    assertEquals("secondName", personType.getField("secondName").get().getName());
    assertTrue(personType.getField("secondName").get().isPrivate());
    assertEquals("age", personType.getField("age").get().getName());
    assertTrue(personType.getField("age").get().isProtected());
    // Field Stereotypes
    assertEquals(1, nameField.getStereotypes().size());
    assertEquals("SF", nameField.getStereotype("SF").get().getName());
    assertEquals("SF", nameField.getStereotype("SF").get().getValue());
    // Stereotypes
    assertEquals(2, personType.getStereotypes().size());
    assertEquals("S1", personType.getStereotype("S1").get().getName());
    assertEquals("S2", personType.getStereotype("S2").get().getName());
    // TODO PN name and value are not distinguished. Is this ok?
    assertEquals("S1", personType.getStereotype("S1").get().getValue());
    assertEquals("S2", personType.getStereotype("S2").get().getValue());
    // Methods
    assertEquals(2, personType.getMethods().size());
    final CDMethodSymbol setNameMethod = personType.getMethod("setName").orNull();
    assertNotNull(setNameMethod);
    assertEquals("setName", setNameMethod.getName());
    assertSame(personType, setNameMethod.getDefiningType());
    assertTrue(setNameMethod.isPublic());
    assertFalse(setNameMethod.isConstructor());
    assertFalse(setNameMethod.isFinal());
    assertFalse(setNameMethod.isAbstract());
    assertFalse(setNameMethod.isEllipsisParameterMethod());
    assertEquals("String", setNameMethod.getReturnType().getName());
    // Parameters
    assertEquals(2, setNameMethod.getParameters().size());
    assertEquals("name", setNameMethod.getParameters().get(0).getName());
    assertTrue(setNameMethod.getParameters().get(0).isParameter());
    final CDFieldSymbol prefixParameter = setNameMethod.getParameters().get(1);
    assertEquals("prefix", prefixParameter.getName());
    assertTrue(prefixParameter.isParameter());
    assertEquals("String", prefixParameter.getType().getName());
    // AST
    assertTrue(setNameMethod.getAstNode().isPresent());
    assertTrue(setNameMethod.getAstNode().get() instanceof ASTCDMethod);

    assertTrue(personType.getMethod("getAge").isPresent());
    assertTrue(personType.getMethod("getAge").get().isPrivate());
    assertEquals(0, personType.getMethod("getAge").get().getParameters().size());


    final CDTypeSymbol profType = cdScope.<CDTypeSymbol>resolve("Prof", CDTypeSymbol.KIND)
        .orNull();
    assertNotNull(profType);
    assertEquals("de.monticore.umlcd4a.symboltable.CD1.Prof", profType.getFullName());
    assertTrue(profType.isPrivate());
    assertEquals(1, profType.getFields().size());
    assertEquals("uni", profType.getField("uni").get().getName());
    assertTrue(profType.getField("uni").get().isDerived());
    // Super class
    assertTrue(profType.getSuperClass().isPresent());
    assertEquals(personType.getName(), profType.getSuperClass().get().getName());
    // The referenced symbol is the SAME as the one in the symbol table.
    assertSame(personType, ((CDTypeSymbolReference) profType.getSuperClass().get())
        .getReferencedSymbol());
    // Interfaces
    assertEquals(2, profType.getInterfaces().size());
    assertEquals("de.monticore.umlcd4a.symboltable.CD1.Printable", profType.getInterfaces().get(0).getFullName());
    assertEquals("de.monticore.umlcd4a.symboltable.CD1.Callable", profType.getInterfaces().get(1).getFullName());
    assertEquals(3, profType.getSuperTypes().size());
    // Associations
    assertEquals(1, profType.getAssociations().size());

    final CDTypeSymbol printableType = cdScope.<CDTypeSymbol>resolve("Printable", CDTypeSymbol.KIND)
        .orNull();
    assertNotNull(printableType);
    assertEquals("Printable", printableType.getName());
    assertEquals("de.monticore.umlcd4a.symboltable.CD1.Printable", printableType.getFullName());
    assertEquals("de.monticore.umlcd4a.symboltable", printableType.getPackageName());
    assertTrue(printableType.isInterface());
    assertTrue(printableType.isProtected());
    // Methods
    final CDMethodSymbol printMethod = printableType.getMethod("print").orNull();
    assertNotNull(printMethod);
    assertEquals("print", printMethod.getName());
    assertTrue(printMethod.isProtected());
    assertFalse(printMethod.isConstructor());
    assertFalse(printMethod.isFinal());
    assertTrue(printMethod.isAbstract());
    assertTrue(printMethod.isEllipsisParameterMethod());
    assertEquals(1, printMethod.getParameters().size());
    assertEquals("s", printMethod.getParameters().get(0).getName());
    // Associations
    assertEquals(0, printableType.getAssociations().size());


    final CDTypeSymbol callableType = cdScope.<CDTypeSymbol>resolve("Callable", CDTypeSymbol.KIND)
        .orNull();
    assertNotNull(callableType);
    assertEquals("de.monticore.umlcd4a.symboltable.CD1.Callable", callableType.getFullName());
    assertTrue(callableType.isInterface());
    assertTrue(callableType.isPublic());
    assertEquals(1, callableType.getInterfaces().size());
    assertEquals("de.monticore.umlcd4a.symboltable.CD1.Printable", callableType.getInterfaces().get(0).getFullName());

    final CDTypeSymbol enumType = cdScope.<CDTypeSymbol>resolve("E", CDTypeSymbol.KIND).orNull();
    assertNotNull(enumType);
    assertEquals("de.monticore.umlcd4a.symboltable.CD1.E", enumType.getFullName());
    assertTrue(enumType.isEnum());
    assertTrue(enumType.isPublic());
    // Enum Constants
    assertEquals(2, enumType.getEnumConstants().size());
    assertEquals("A", enumType.getEnumConstants().get(0).getName());
    assertEquals(enumType.getName(), enumType.getEnumConstants().get(0).getType().getName());
    assertEquals("B", enumType.getEnumConstants().get(1).getName());
    assertEquals(enumType.getName(), enumType.getEnumConstants().get(1).getType().getName());
    assertEquals(enumType.getEnumConstants(), enumType.getFields());
    // Interfaces
    assertEquals(1, enumType.getInterfaces().size());
    assertEquals("de.monticore.umlcd4a.symboltable.CD1.Printable", enumType.getInterfaces().get(0).getFullName());

    // Bidirectional association A <-> B is splitted into two associations A -> B and A <- B.
    // A -> B
    final CDAssociationSymbol memberAssocLeft2Right = cdScope.<CDAssociationSymbol>resolve("prof",
        CDAssociationSymbol.KIND).orNull();
    assertNotNull(memberAssocLeft2Right);
    assertEquals("prof", memberAssocLeft2Right.getName());
    assertEquals("member", memberAssocLeft2Right.getAssocName());
    assertTrue(memberAssocLeft2Right.isBidirectional());
    assertEquals(personType.getName(), memberAssocLeft2Right.getSourceType().getName());
    assertEquals(profType.getName(), memberAssocLeft2Right.getTargetType().getName());
    assertEquals(0, memberAssocLeft2Right.getSourceCardinality().getMin());
    assertEquals(Cardinality.STAR, memberAssocLeft2Right.getSourceCardinality().getMax());
    assertTrue(memberAssocLeft2Right.getSourceCardinality().isMultiple());
    assertEquals(1, memberAssocLeft2Right.getTargetCardinality().getMin());
    assertEquals(1, memberAssocLeft2Right.getTargetCardinality().getMax());
    assertFalse(memberAssocLeft2Right.getTargetCardinality().isMultiple());
    // AST
    assertTrue(memberAssocLeft2Right.getAstNode().isPresent());
    assertTrue(memberAssocLeft2Right.getAstNode().get() instanceof ASTCDAssociation);
    // A <- B
    final CDAssociationSymbol memberAssocRight2Left = cdScope.<CDAssociationSymbol>resolve("person",
        CDAssociationSymbol.KIND).orNull();
    assertNotNull(memberAssocRight2Left);
    assertEquals("person", memberAssocRight2Left.getName());
    assertEquals("member", memberAssocRight2Left.getAssocName());
    assertTrue(memberAssocRight2Left.isBidirectional());
    assertEquals(profType.getName(), memberAssocRight2Left.getSourceType().getName());
    assertEquals(personType.getName(), memberAssocRight2Left.getTargetType().getName());
    assertEquals(1, memberAssocRight2Left.getSourceCardinality().getMin());
    assertEquals(1, memberAssocRight2Left.getSourceCardinality().getMax());
    assertFalse(memberAssocRight2Left.getSourceCardinality().isMultiple());
    assertEquals(0, memberAssocRight2Left.getTargetCardinality().getMin());
    assertEquals(Cardinality.STAR, memberAssocRight2Left.getTargetCardinality().getMax());
    assertTrue(memberAssocRight2Left.getTargetCardinality().isMultiple());
    // AST
    assertTrue(memberAssocRight2Left.getAstNode().isPresent());
    assertTrue(memberAssocRight2Left.getAstNode().get() instanceof ASTCDAssociation);
    // Stereotype
    assertEquals(1, memberAssocRight2Left.getStereotypes().size());
    assertEquals("SA", memberAssocRight2Left.getStereotype("SA").get().getValue());
    assertEquals("SA", memberAssocRight2Left.getStereotype("SA").get().getName());

    // A -> B
    final CDAssociationSymbol ecAssocLeft2Right = cdScope.<CDAssociationSymbol>resolve("callable",
        CDAssociationSymbol.KIND).orNull();
    assertNotNull(ecAssocLeft2Right);
    assertEquals("callable", ecAssocLeft2Right.getName());
    assertEquals("ec", ecAssocLeft2Right.getAssocName());
    assertTrue(ecAssocLeft2Right.isBidirectional());
    assertEquals("de.monticore.umlcd4a.symboltable.CD1.E", ecAssocLeft2Right.getSourceType().getFullName());
    assertEquals("de.monticore.umlcd4a.symboltable.CD1.Callable", ecAssocLeft2Right.getTargetType().getFullName());
    assertEquals(1, ecAssocLeft2Right.getSourceCardinality().getMin());
    assertEquals(Cardinality.STAR, ecAssocLeft2Right.getSourceCardinality().getMax());
    assertTrue(ecAssocLeft2Right.getSourceCardinality().isMultiple());
    assertEquals(0, ecAssocLeft2Right.getTargetCardinality().getMin());
    assertEquals(1, ecAssocLeft2Right.getTargetCardinality().getMax());
    assertFalse(ecAssocLeft2Right.getTargetCardinality().isMultiple());
    // A <- B
    final CDAssociationSymbol ecAssocRight2Left = cdScope.<CDAssociationSymbol>resolve("e",
        CDAssociationSymbol.KIND).orNull();
    assertNotNull(ecAssocRight2Left);
    assertEquals("e", ecAssocRight2Left.getName());
    assertEquals("ec", ecAssocRight2Left.getAssocName());
    assertTrue(ecAssocRight2Left.isBidirectional());
    assertEquals("de.monticore.umlcd4a.symboltable.CD1.Callable", ecAssocRight2Left.getSourceType().getFullName());
    assertEquals("de.monticore.umlcd4a.symboltable.CD1.E", ecAssocRight2Left.getTargetType().getFullName());
    assertEquals(0, ecAssocRight2Left.getSourceCardinality().getMin());
    assertEquals(1, ecAssocRight2Left.getSourceCardinality().getMax());
    assertFalse(ecAssocRight2Left.getSourceCardinality().isMultiple());
    assertEquals(1, ecAssocRight2Left.getTargetCardinality().getMin());
    assertEquals(Cardinality.STAR, ecAssocRight2Left.getTargetCardinality().getMax());
    assertTrue(ecAssocRight2Left.getTargetCardinality().isMultiple());


    // Modifier Test //

    // Class is public
    assertTrue(cdScope.resolve("Person", CDTypeSymbol.KIND, PUBLIC).isPresent());
    assertTrue(cdScope.resolve("Person", CDTypeSymbol.KIND, PROTECTED)
        .isPresent());
    assertTrue(cdScope.resolve("Person", CDTypeSymbol.KIND, PRIVATE).isPresent());

    // Prof is private
    assertFalse(cdScope.resolve("Prof", CDTypeSymbol.KIND, PUBLIC).isPresent());
    assertFalse(cdScope.resolve("Prof", CDTypeSymbol.KIND, PROTECTED).isPresent());
    assertTrue(cdScope.resolve("Prof", CDTypeSymbol.KIND, PRIVATE).isPresent());

    // Printable is protected
    assertFalse(cdScope.resolve("Printable", CDTypeSymbol.KIND, PUBLIC)
        .isPresent());
    assertTrue(cdScope.resolve("Printable", CDTypeSymbol.KIND, PROTECTED).isPresent());
    assertTrue(cdScope.resolve("Printable", CDTypeSymbol.KIND, PRIVATE).isPresent());


    // Resolve fields from super class //
    // public fields can be resolved
    assertFalse(profType.getField("name").isPresent());
    assertTrue(profType.getSpannedScope().resolve("name", CDFieldSymbol.KIND).isPresent());

    // protected fields can be resolved
    assertTrue(profType.getSpannedScope().resolve("age", CDFieldSymbol.KIND).isPresent());
    assertFalse(profType.getField("age").isPresent());

    // private fields CANNOT be resolved...
    assertFalse(profType.getSpannedScope().resolve("secondName", CDFieldSymbol.KIND).isPresent());
    // ... even if resolving with the private access modifier.
    assertFalse(profType.getSpannedScope().resolve("secondName", CDFieldSymbol.KIND, PRIVATE).isPresent());
    assertFalse(profType.getField("secondName").isPresent());

    // Resolve methods from super types //
    // public methods can be resolved
    assertTrue(profType.getSpannedScope().resolve("setName", CDMethodSymbol.KIND).isPresent());
    assertSame(setNameMethod, profType.getSpannedScope().resolve("setName", CDMethodSymbol.KIND).get());
    assertFalse(profType.getMethod("setName").isPresent());

    // protected methods can be resolved
    assertTrue(profType.getSpannedScope().resolve("print", CDMethodSymbol.KIND).isPresent());
    assertSame(printMethod, profType.getSpannedScope().resolve("print", CDMethodSymbol.KIND).get());
    assertFalse(profType.getMethod("print").isPresent());

    // private methods CANNOT be resolved...
    assertFalse(profType.getSpannedScope().resolve("getAge", CDMethodSymbol.KIND).isPresent());
    // ... even if resolving with the private access modifier.
    assertFalse(profType.getSpannedScope().resolve("getAge", CDMethodSymbol.KIND, PRIVATE).isPresent());
    assertFalse(profType.getMethod("getAge").isPresent());
  }

}