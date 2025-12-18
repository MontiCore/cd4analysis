/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.ow2cw;

import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.ICDBasisScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symboltable.resolving.ResolvedSeveralEntriesForSymbolException;
import de.monticore.types.mcarraytypes._ast.ASTMCArrayType;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.monticore.types.mccollectiontypes._ast.ASTMCListType;
import de.monticore.types.mccollectiontypes._ast.ASTMCOptionalType;
import de.monticore.types.mccollectiontypes._ast.ASTMCSetType;
import de.se_rwth.commons.logging.Log;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class CDAttributeHelper {

  protected static List<Class<?>> nestedTypes = List.of(
      ASTMCListType.class,
      ASTMCSetType.class,
      ASTMCOptionalType.class,
      ASTMCArrayType.class
  );

  public static boolean isPrimitiveType(ASTMCType type) {
    return type instanceof ASTMCPrimitiveType;
  }

  public static ASTMCPrimitiveType getPrimitiveType(ASTMCType type) {
    if(!isPrimitiveType(type)) {
      return null;
    }
    return (ASTMCPrimitiveType) type;
  }

  public static boolean isNestedType(ASTMCType type) {
    return nestedTypes.stream().anyMatch(nestedType -> nestedType.isInstance(type));
  }

  public static boolean isQualifiedType(ASTMCType type) {
    return type instanceof ASTMCQualifiedType;
  }

  public static boolean isGenericType(ASTMCType type) {
    return type instanceof ASTMCGenericType;
  }

  public static boolean isArrayType(ASTMCType type) {
    return type instanceof ASTMCArrayType;
  }


  public static ASTMCQualifiedType getQualifiedType(ASTMCType type) {
    if(!isQualifiedType(type)) {
      return null;
    }
    return (ASTMCQualifiedType) type;
  }

  public static ASTCDType resolveInnermostClass(ASTMCType type, ICD4CodeArtifactScope scope) {
    if (isGenericType(type)) {
      ASTMCType innerType = resolveInnermostClass(type);
      if (innerType != null) {
        return resolveClass(innerType, scope);
      }
      return null;
    } else if (isArrayType(type)) {
      return resolveClass(((ASTMCArrayType) type).getMCType(), scope);
    }
    return resolveClass(type, scope);
  }

  public static ASTCDType resolveInnermostClass(ASTCDAttribute attribute) {
    ASTMCType type = attribute.getMCType();
    if (isGenericType(type)) {
      ASTMCType innerType = resolveInnermostClass(type);
      if (innerType != null) {
        return resolveClass(innerType, getCD4CodeArtifactScope(attribute.getEnclosingScope()));
      }
      return null;
    } else if (isArrayType(type)) {
      return resolveClass(((ASTMCArrayType) type).getMCType(), getCD4CodeArtifactScope(attribute.getEnclosingScope()));
    }
    return resolveClass(type, getCD4CodeArtifactScope(attribute.getEnclosingScope()));
  }

  public static ASTMCType resolveInnermostClass(ASTMCType type) {
    if(isArrayType(type)) {
      return resolveInnermostClass(((ASTMCArrayType) type).getMCType());
    }
    if(isGenericType(type)) {
      if(((ASTMCGenericType) type).getMCTypeArgumentList().size() != 1) {
        return null;
      }
      ASTMCType innerType = ((ASTMCGenericType) type).getMCTypeArgument(0).getMCTypeOpt().orElse(null);
      if (innerType instanceof ASTMCGenericType) {
        return resolveInnermostClass(innerType);
      }
      return innerType;
    }
    return type;
  }

  /**
   * Resolves the class for a qualified type, returns null if the type is not qualified or cannot be resolved
   * If the type is nested, use {@link #resolveInnermostClass(ASTMCType, ICD4CodeArtifactScope)}.
   * @param type the type to resolve
   * @param scope the scope to resolve the type in
   * @return the resolved class or null
   */
  public static ASTCDType resolveClass(ASTMCType type, ICD4CodeArtifactScope scope) {
    if(!isQualifiedType(type)) {
      return null;
    }
    ASTMCQualifiedType qualifiedType = (ASTMCQualifiedType) type;
    Optional<TypeSymbol> typeSymbol = Optional.empty();
    try {
      typeSymbol = scope.resolveType(qualifiedType.getMCQualifiedName().getBaseName());
    } catch (ResolvedSeveralEntriesForSymbolException e) {
      // resolve by full name
      typeSymbol = scope.resolveType(qualifiedType.getMCQualifiedName().getQName());
    }
    if (typeSymbol.isPresent() && typeSymbol.get().isPresentAstNode() && typeSymbol.get()
        .getAstNode() instanceof ASTCDType) {
      return (ASTCDType) typeSymbol.get().getAstNode();
    }
    return null;
  }

  public static Set<ASTCDAttribute> getAttributes(ASTCDType cdType) {
    return new HashSet<>(cdType.getCDAttributeList());
  }

  public static boolean hasSameNestings(ASTCDAttribute attr1, ASTCDAttribute attr2) {
    return hasSameNestings(attr1.getMCType(), attr2.getMCType());
  }

  private static boolean hasSameNestings(ASTMCType type1, ASTMCType type2) {
    if( type1 == null || type2 == null || (isNestedType(type1) != isNestedType(type2))) {
      return false;
    }
    if(!isNestedType(type1)) {
      return true;
    }
    if(isArrayType(type1) != isArrayType(type2)) {
      return false;
    }
    if(isArrayType(type1)) {
      ASTMCArrayType arrayType1 = (ASTMCArrayType) type1;
      ASTMCArrayType arrayType2 = (ASTMCArrayType) type2;
      return hasSameNestings(arrayType1.getMCType(), arrayType2.getMCType());
    }
    ASTMCGenericType genericType1 = (ASTMCGenericType) type1;
    ASTMCGenericType genericType2 = (ASTMCGenericType) type2;
    if(genericType1.getMCTypeArgumentList().isEmpty() || genericType2.getMCTypeArgumentList().isEmpty() ||
      genericType1.getMCTypeArgumentList().size() != genericType2.getMCTypeArgumentList().size()) {
      return false;
    }

    if(genericType1.getName(0).equals(genericType2.getName(0))) {
      if(genericType1.getMCTypeArgument(0).getMCTypeOpt().isPresent() &&
         genericType2.getMCTypeArgument(0).getMCTypeOpt().isPresent()) {
        return hasSameNestings(genericType1.getMCTypeArgument(0).getMCTypeOpt().get(),
          genericType2.getMCTypeArgument(0).getMCTypeOpt().get());
      }
    }
    return false;
  }

  public static ICD4CodeArtifactScope getCD4CodeArtifactScope(ICDBasisScope scope) {
    if (scope instanceof ICD4CodeArtifactScope) {
      return (ICD4CodeArtifactScope) scope;
    }
    else if (scope == null) {
      Log.error("0xCDD20: ACDType was not contained in a CD4CodeArtifactScope.");
      return null;

    }
    else {
      return getCD4CodeArtifactScope(scope.getEnclosingScope());
    }
  }
}
