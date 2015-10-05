package de.monticore.umlcd4a.symboltable;

import static de.monticore.symboltable.modifiers.BasicAccessModifier.PRIVATE;
import static de.monticore.symboltable.modifiers.BasicAccessModifier.PROTECTED;
import static de.monticore.symboltable.modifiers.BasicAccessModifier.PUBLIC;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.symboltable.ArtifactScope;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.types.references.ActualTypeArgument;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAssociation;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDEnum;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDInterface;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.monticore.umlcd4a.symboltable.references.CDTypeSymbolReference;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.Slf4jLog;

public class CD4AnalysisSymbolTableCreatorTest {
  
  @BeforeClass
  public static void setup() {
    Slf4jLog.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testSymbolTableCreation() {
    final GlobalScope globalScope = CD4AGlobalScopeTestFactory.create();
    
    final CDSymbol cdSymbol = globalScope.<CDSymbol> resolve(
        "de.monticore.umlcd4a.symboltable.CD1", CDSymbol.KIND).orElse(null);
    assertNotNull(cdSymbol);

    // Scope Hierarchy: GlobalScope -> ArtifactScope -> ClassDiagramScope ->* ...
    assertEquals(1, globalScope.getSubScopes().size());
    final ArtifactScope artifactScope = (ArtifactScope) globalScope.getSubScopes().get(0);
    assertSame(artifactScope, cdSymbol.getEnclosingScope());

    assertEquals(1, artifactScope.getSubScopes().size());

    // TODO PN find better solution
    // Quickfix for using default types: add built-in types
    globalScope.add(new CDTypeSymbol("int"));
    globalScope.add(new CDTypeSymbol("boolean"));
    globalScope.add(new CDTypeSymbol("String"));
    final CDTypeSymbol builtInList = new CDTypeSymbol("List");
    builtInList.setPackageName("java.util");
    globalScope.add(builtInList);


    final CDTypeSymbol creatureType = cdSymbol.getType("Creature").orElse(null);

    assertNotNull(creatureType.getSpannedScope());
    assertSame(creatureType, creatureType.getSpannedScope().getSpanningSymbol().get());
    assertEquals("Creature", creatureType.getName());
    assertTrue(creatureType.isAbstract());
    assertEquals("de.monticore.umlcd4a.symboltable.CD1.Creature", creatureType.getFullName());
    assertEquals("de.monticore.umlcd4a.symboltable", creatureType.getPackageName());
    assertTrue(creatureType.isPublic());
    // AST
    assertTrue(creatureType.getAstNode().isPresent());
    assertTrue(creatureType.getAstNode().get() instanceof ASTCDClass);
    assertSame(creatureType, creatureType.getAstNode().get().getSymbol().get());
    assertSame(creatureType.getEnclosingScope(), creatureType.getAstNode().get().getEnclosingScope().get());
    // Fields
    assertEquals(1, creatureType.getFields().size());
    final CDFieldSymbol extinctField = creatureType.getField("extinct").get();
    assertEquals("extinct", extinctField.getName());


    final CDTypeSymbol personType = globalScope.<CDTypeSymbol> resolve(
        "de.monticore.umlcd4a.symboltable.CD1.Person", CDTypeSymbol.KIND).orElse(null);
    assertNotNull(personType);
    assertNotNull(personType.getSpannedScope());
    assertSame(personType, personType.getSpannedScope().getSpanningSymbol().get());
    assertEquals("Person", personType.getName());
    assertEquals("de.monticore.umlcd4a.symboltable.CD1.Person", personType.getFullName());
    assertEquals("de.monticore.umlcd4a.symboltable", personType.getPackageName());
    assertTrue(personType.isPublic());
    // AST
    assertTrue(personType.getAstNode().isPresent());
    assertTrue(personType.getAstNode().get() instanceof ASTCDClass);
    assertSame(personType, personType.getAstNode().get().getSymbol().get());
    assertSame(personType.getEnclosingScope(), personType.getAstNode().get().getEnclosingScope().get());
    // Associations
    assertEquals(1, personType.getAssociations().size());
    // Fields
    assertEquals(7, personType.getFields().size());
    final CDFieldSymbol nameField = personType.getField("name").get();
    assertEquals("name", nameField.getName());
    assertTrue(nameField.isPublic());
    assertEquals("de.monticore.umlcd4a.symboltable.CD1.Person.name", nameField.getFullName());
    assertEquals("de.monticore.umlcd4a.symboltable", nameField.getPackageName());
    assertEquals("String", nameField.getType().getName());
    // AST
    assertTrue(nameField.getAstNode().isPresent());
    assertTrue(nameField.getAstNode().get() instanceof ASTCDAttribute);
    assertSame(nameField, nameField.getAstNode().get().getSymbol().get());
    assertSame(nameField.getEnclosingScope(), nameField.getAstNode().get().getEnclosingScope().get());
    final CDFieldSymbol secondNameField = personType.getField("secondName").get();
    assertEquals("secondName", secondNameField.getName());
    assertTrue(secondNameField.isPrivate());
    final CDFieldSymbol ageField = personType.getField("age").get();
    assertEquals("age", ageField.getName());
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
    final CDMethodSymbol setNameMethod = personType.getMethod("setName").orElse(null);
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
    assertSame(setNameMethod, setNameMethod.getAstNode().get().getSymbol().get());
    assertSame(setNameMethod.getEnclosingScope(), setNameMethod.getAstNode().get().getEnclosingScope().get());

    assertTrue(personType.getMethod("getAge").isPresent());
    assertTrue(personType.getMethod("getAge").get().isPrivate());
    assertEquals(0, personType.getMethod("getAge").get().getParameters().size());


    final CDTypeSymbol profType = cdSymbol.getType("Prof").orElse(null);
    assertNotNull(profType);
    assertEquals("de.monticore.umlcd4a.symboltable.CD1.Prof", profType.getFullName());
    assertTrue(profType.isPrivate());
    assertEquals(2, profType.getFields().size());
    assertEquals("uni", profType.getField("uni").get().getName());
    assertTrue(profType.getField("uni").get().isDerived());
    final CDFieldSymbol profFieldPP = profType.getField("pp").orElse(null);
    assertNotNull(profFieldPP);
    final CDTypeSymbolReference personList = (CDTypeSymbolReference) profFieldPP.getType();
    assertEquals("List", personList.getName());
    assertEquals("List<Person>", personList.getStringRepresentation());
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

    final CDTypeSymbol printableType = cdSymbol.getType("Printable").orElse(null);
    assertNotNull(printableType);
    assertEquals("Printable", printableType.getName());
    assertEquals("de.monticore.umlcd4a.symboltable.CD1.Printable", printableType.getFullName());
    assertEquals("de.monticore.umlcd4a.symboltable", printableType.getPackageName());
    assertTrue(printableType.isInterface());
    assertTrue(printableType.isProtected());
    // Methods
    final CDMethodSymbol printMethod = printableType.getMethod("print").orElse(null);
    assertNotNull(printMethod);
    assertEquals("print", printMethod.getName());
    assertTrue(printMethod.isProtected());
    assertFalse(printMethod.isConstructor());
    assertFalse(printMethod.isFinal());
    assertTrue(printMethod.isAbstract());
    assertTrue(printMethod.isEllipsisParameterMethod());
    assertEquals(1, printMethod.getParameters().size());
    assertEquals("s", printMethod.getParameters().get(0).getName());
    // AST
    assertTrue(printableType.getAstNode().isPresent());
    assertTrue(printableType.getAstNode().get() instanceof ASTCDInterface);
    assertSame(printableType, printableType.getAstNode().get().getSymbol().get());
    assertSame(printableType.getEnclosingScope(), printableType.getAstNode().get().getEnclosingScope().get());
    // Associations
    assertEquals(0, printableType.getAssociations().size());


    final CDTypeSymbol callableType = cdSymbol.getType("Callable").orElse(null);
    assertNotNull(callableType);
    assertEquals("de.monticore.umlcd4a.symboltable.CD1.Callable", callableType.getFullName());
    assertTrue(callableType.isInterface());
    assertTrue(callableType.isPublic());
    assertEquals(1, callableType.getInterfaces().size());
    assertEquals("de.monticore.umlcd4a.symboltable.CD1.Printable", callableType.getInterfaces().get(0).getFullName());

    final CDTypeSymbol enumType = cdSymbol.getType("E").orElse(null);
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
    // AST
    assertTrue(enumType.getAstNode().isPresent());
    assertTrue(enumType.getAstNode().get() instanceof ASTCDEnum);
    assertSame(enumType, enumType.getAstNode().get().getSymbol().get());
    assertSame(enumType.getEnclosingScope(), enumType.getAstNode().get().getEnclosingScope().get());

    final Scope cdScope = cdSymbol.getSpannedScope();
    // Bidirectional association A <-> B is splitted into two associations A -> B and A <- B.
    // A -> B
    final CDAssociationSymbol memberAssocLeft2Right = (CDAssociationSymbol)
        cdScope.resolve(new CDAssociationNameAndTargetNamePredicate("member", "Prof")).orElse(null);
    assertNotNull(memberAssocLeft2Right);
    assertEquals("member", memberAssocLeft2Right.getName());
    assertEquals("member", memberAssocLeft2Right.getAssocName().orElse(""));
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
    final CDAssociationSymbol memberAssocRight2Left = (CDAssociationSymbol) cdScope.resolve(new CDAssociationNameAndTargetNamePredicate("member", "Person")).orElse(null);
    assertNotNull(memberAssocRight2Left);
    assertEquals("member", memberAssocRight2Left.getName());
    assertEquals("member", memberAssocRight2Left.getAssocName().orElse(""));
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
    final CDAssociationSymbol ecAssocLeft2Right = (CDAssociationSymbol) cdScope.resolve(new CDAssociationNameAndTargetNamePredicate("ec", "Callable")).orElse(null);
    assertNotNull(ecAssocLeft2Right);
    assertEquals("ec", ecAssocLeft2Right.getName());
    assertEquals("ec", ecAssocLeft2Right.getAssocName().orElse(""));
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
    final CDAssociationSymbol ecAssocRight2Left = (CDAssociationSymbol) cdScope.resolve(new CDAssociationNameAndTargetNamePredicate("ec", "E")).orElse(null);
    assertNotNull(ecAssocRight2Left);
    assertEquals("ec", ecAssocRight2Left.getName());
    assertEquals("ec", ecAssocRight2Left.getAssocName().orElse(""));
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


    // resolve method by signature
    assertTrue(personType.getSpannedScope().resolve(
        new CDMethodSignaturePredicate("setName", "String", "String")).isPresent());
    assertSame(setNameMethod, personType.getSpannedScope().resolve(
        new CDMethodSignaturePredicate("setName", "String", "String")).get());

    assertFalse(personType.getSpannedScope().resolve(
        new CDMethodSignaturePredicate("setName", "String")).isPresent());

    assertFalse(personType.getSpannedScope().resolve(
        new CDMethodSignaturePredicate("setName", "String", "int")).isPresent());

    assertFalse(personType.getSpannedScope().resolve(
        new CDMethodSignaturePredicate("setName", "String", "String", "String")).isPresent());

    assertFalse(personType.getSpannedScope().resolve(
        new CDMethodSignaturePredicate("getAge", "String", "String")).isPresent());



    // getAllVisibleFieldsOfSuperTypes()
    final Collection<CDFieldSymbol> superFieldsOfProf = profType.getAllVisibleFieldsOfSuperTypes();

    assertEquals(3, superFieldsOfProf.size());
    // fields of direct super class //
    assertTrue(superFieldsOfProf.contains(nameField));
    assertTrue(superFieldsOfProf.contains(ageField));
    // secondName is private
    assertFalse(superFieldsOfProf.contains(secondNameField));
    // fields of super super class //
    assertTrue(superFieldsOfProf.contains(extinctField));

    // hasSuperType()
    assertTrue(profType.hasSuperType("Prof"));
    assertTrue(profType.hasSuperType("Person"));
    assertTrue(profType.hasSuperType("Creature"));

    // hasSuperTypeByFullName()
    assertTrue(profType.hasSuperTypeByFullName("de.monticore.umlcd4a.symboltable.CD1.Prof"));
    assertTrue(profType.hasSuperTypeByFullName("de.monticore.umlcd4a.symboltable.CD1.Person"));
    assertTrue(profType.hasSuperTypeByFullName("de.monticore.umlcd4a.symboltable.CD1.Creature"));
  }
  
  @Test
  public void testTypeSymbolReferencesForGenerics() {
    final GlobalScope globalScope = CD4AGlobalScopeTestFactory.create();

    final CDSymbol cdSymbol = globalScope.<CDSymbol> resolve(
        "de.monticore.umlcd4a.symboltable.Generics", CDSymbol.KIND).orElse(null);
    assertNotNull(cdSymbol);
    
    final CDTypeSymbol clazz = globalScope.<CDTypeSymbol> resolve(
        "de.monticore.umlcd4a.symboltable.Generics.A", CDTypeSymbol.KIND).orElse(null);
    assertNotNull(clazz);
    assertEquals("de.monticore.umlcd4a.symboltable.Generics.A", clazz.getFullName());

    //Type arguments
    CDFieldSymbol attribute = clazz.getField("g1").orElse(null);
    assertNotNull(attribute);
    CDTypeSymbolReference attributeType = (CDTypeSymbolReference) attribute.getType();
    assertEquals("List", attributeType.getName());
    assertEquals("List<String>", attributeType.getStringRepresentation());
    assertEquals(1, attributeType.getActualTypeArguments().size());
    ActualTypeArgument typeArgument = attributeType.getActualTypeArguments().get(0);
    assertEquals("String", typeArgument.getType().getName());
    assertFalse(typeArgument.isLowerBound());
    assertFalse(typeArgument.isUpperBound());
    
    attribute = clazz.getField("g2").orElse(null);
    assertNotNull(attribute);
    attributeType = (CDTypeSymbolReference) attribute.getType();
    assertEquals("List", attributeType.getName());
    assertEquals("List<B>", attributeType.getStringRepresentation());
    assertEquals(1, attributeType.getActualTypeArguments().size());
    typeArgument = attributeType.getActualTypeArguments().get(0);
    assertEquals("B", typeArgument.getType().getName());
    assertFalse(typeArgument.isLowerBound());
    assertFalse(typeArgument.isUpperBound());
    
    attribute = clazz.getField("g3").orElse(null);
    assertNotNull(attribute);
    attributeType = (CDTypeSymbolReference) attribute.getType();
    assertEquals("List", attributeType.getName());
    assertEquals("List<C>", attributeType.getStringRepresentation());
    assertEquals(1, attributeType.getActualTypeArguments().size());
    typeArgument = attributeType.getActualTypeArguments().get(0);
    assertEquals("C", typeArgument.getType().getName());
    assertFalse(typeArgument.isLowerBound());
    assertFalse(typeArgument.isUpperBound());
    
    attribute = clazz.getField("g4").orElse(null);
    assertNotNull(attribute);
    attributeType = (CDTypeSymbolReference) attribute.getType();
    assertEquals("List", attributeType.getName());
    assertEquals("List<?>", attributeType.getStringRepresentation());
    assertEquals(1, attributeType.getActualTypeArguments().size());
    typeArgument = attributeType.getActualTypeArguments().get(0);
    assertEquals("?", typeArgument.getType().getName());
    assertFalse(typeArgument.isLowerBound());
    assertFalse(typeArgument.isUpperBound());
    
    attribute = clazz.getField("g5").orElse(null);
    assertNotNull(attribute);
    attributeType = (CDTypeSymbolReference) attribute.getType();
    assertEquals("List", attributeType.getName());
    assertEquals("List<? extends B>", attributeType.getStringRepresentation());
    assertEquals(1, attributeType.getActualTypeArguments().size());
    typeArgument = attributeType.getActualTypeArguments().get(0);
    assertEquals("B", typeArgument.getType().getName());
    assertFalse(typeArgument.isLowerBound());
    assertTrue(typeArgument.isUpperBound());
    
    attribute = clazz.getField("g6").orElse(null);
    assertNotNull(attribute);
    attributeType = (CDTypeSymbolReference) attribute.getType();
    assertEquals("List", attributeType.getName());
    assertEquals("List<? super B>", attributeType.getStringRepresentation());
    assertEquals(1, attributeType.getActualTypeArguments().size());
    typeArgument = attributeType.getActualTypeArguments().get(0);
    assertEquals("B", typeArgument.getType().getName());
    assertTrue(typeArgument.isLowerBound());
    assertFalse(typeArgument.isUpperBound());
    
    attribute = clazz.getField("g7").orElse(null);
    assertNotNull(attribute);
    attributeType = (CDTypeSymbolReference) attribute.getType();
    assertEquals("List", attributeType.getName());
    assertEquals("List<? extends C>", attributeType.getStringRepresentation());
    assertEquals(1, attributeType.getActualTypeArguments().size());
    typeArgument = attributeType.getActualTypeArguments().get(0);
    assertEquals("C", typeArgument.getType().getName());
    assertFalse(typeArgument.isLowerBound());
    assertTrue(typeArgument.isUpperBound());
    
    attribute = clazz.getField("g8").orElse(null);
    assertNotNull(attribute);
    attributeType = (CDTypeSymbolReference) attribute.getType();
    assertEquals("List", attributeType.getName());
    assertEquals("List<? super C>", attributeType.getStringRepresentation());
    assertEquals(1, attributeType.getActualTypeArguments().size());
    typeArgument = attributeType.getActualTypeArguments().get(0);
    assertEquals("C", typeArgument.getType().getName());
    assertTrue(typeArgument.isLowerBound());
    assertFalse(typeArgument.isUpperBound());
    
    attribute = clazz.getField("g9").orElse(null);
    assertNotNull(attribute);
    attributeType = (CDTypeSymbolReference) attribute.getType();
    assertEquals("List", attributeType.getName());
    assertEquals("List<List<String>>", attributeType.getStringRepresentation());
    assertEquals(1, attributeType.getActualTypeArguments().size());
    typeArgument = attributeType.getActualTypeArguments().get(0);
    CDTypeSymbolReference innerTypeArgument = (CDTypeSymbolReference) typeArgument.getType();
    assertEquals("List", innerTypeArgument.getName());
    assertEquals("List<String>", innerTypeArgument.getStringRepresentation());
    assertEquals(1, innerTypeArgument.getActualTypeArguments().size());
    assertEquals("String", innerTypeArgument.getActualTypeArguments().get(0).getType().getName());
    
    attribute = clazz.getField("g10").orElse(null);
    assertNotNull(attribute);
    attributeType = (CDTypeSymbolReference) attribute.getType();
    assertEquals("List", attributeType.getName());
    assertEquals("List<List<B>>", attributeType.getStringRepresentation());
    assertEquals(1, attributeType.getActualTypeArguments().size());
    typeArgument = attributeType.getActualTypeArguments().get(0);
    innerTypeArgument = (CDTypeSymbolReference) typeArgument.getType();
    assertEquals("List", innerTypeArgument.getName());
    assertEquals("List<B>", innerTypeArgument.getStringRepresentation());
    assertEquals(1, innerTypeArgument.getActualTypeArguments().size());
    assertEquals("B", innerTypeArgument.getActualTypeArguments().get(0).getType().getName());
    
    attribute = clazz.getField("g11").orElse(null);
    assertNotNull(attribute);
    attributeType = (CDTypeSymbolReference) attribute.getType();
    assertEquals("List", attributeType.getName());
    assertEquals("List<List<C>>", attributeType.getStringRepresentation());
    assertEquals(1, attributeType.getActualTypeArguments().size());
    typeArgument = attributeType.getActualTypeArguments().get(0);
    innerTypeArgument = (CDTypeSymbolReference) typeArgument.getType();
    assertEquals("List", innerTypeArgument.getName());
    assertEquals("List<C>", innerTypeArgument.getStringRepresentation());
    assertEquals(1, innerTypeArgument.getActualTypeArguments().size());
    assertEquals("C", innerTypeArgument.getActualTypeArguments().get(0).getType().getName());
    
    attribute = clazz.getField("g12").orElse(null);
    assertNotNull(attribute);
    attributeType = (CDTypeSymbolReference) attribute.getType();
    assertEquals("List", attributeType.getName());
    assertEquals("List<List<?>>", attributeType.getStringRepresentation());
    assertEquals(1, attributeType.getActualTypeArguments().size());
    typeArgument = attributeType.getActualTypeArguments().get(0);
    innerTypeArgument = (CDTypeSymbolReference) typeArgument.getType();
    assertEquals("List", innerTypeArgument.getName());
    assertEquals("List<?>", innerTypeArgument.getStringRepresentation());
    assertEquals(1, innerTypeArgument.getActualTypeArguments().size());
    assertEquals("?", innerTypeArgument.getActualTypeArguments().get(0).getType().getName());

  }

}
