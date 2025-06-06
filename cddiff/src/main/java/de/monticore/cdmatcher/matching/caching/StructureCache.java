package de.monticore.cdmatcher.matching.caching;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class StructureCache {

  private static final Map<ASTCDAssociation, CachedAssoc> assocCache = new HashMap<>();
  private static final Map<ASTCDType, CachedType> typeCache = new HashMap<>();

  public static boolean addAssociation(ASTCDAssociation assoc, ASTCDType leftType, ASTCDType rightType) {
    if (assocCache.containsKey(assoc)) {
      return false;
    }
    assocCache.put(assoc, new CachedAssoc(assoc, leftType, rightType));
    return true;
  }

  public static Optional<ASTCDType> getLeftType(ASTCDAssociation assoc) {
    if (assocCache.containsKey(assoc)) {
      ASTCDType leftType = assocCache.get(assoc).getLeftType();
      if(leftType == null) {
        return Optional.empty();
      }
      return Optional.of(leftType);
    }
    return Optional.empty();
  }

  public static Optional<ASTCDType> getRightType(ASTCDAssociation assoc) {
    if (assocCache.containsKey(assoc)) {
      ASTCDType rightType = assocCache.get(assoc).getRightType();
      if(rightType == null) {
        return Optional.empty();
      }
      return Optional.of(rightType);
    }
    return Optional.empty();
  }

  public static boolean addType(ASTCDType type) {
    if (typeCache.containsKey(type)) {
      return false;
    }
    typeCache.put(type, new CachedType(type));
    return true;
  }

  public static boolean addAllAssociations(ASTCDType type, Set<ASTCDAssociation> associations) {
    CachedType cachedType = typeCache.get(type);
    if(cachedType == null) {
      return false;
    }
    for (ASTCDAssociation assoc : associations) {
      cachedType.addAssociation(assoc);
    }
    return true;
  }

  public static Set<ASTCDAssociation> getAssociations(ASTCDType type) {
    CachedType cachedType = typeCache.get(type);
    if (cachedType != null) {
      return cachedType.getAssociations();
    }
    return Set.of();
  }

  public static boolean addAllDirectAssociations(ASTCDType type, Set<ASTCDAssociation> associations) {
    CachedType cachedType = typeCache.get(type);
    if (cachedType == null) {
      return false;
    }
    for (ASTCDAssociation assoc : associations) {
      cachedType.addDirectAssociation(assoc);
    }
    return true;
  }

  public static Set<ASTCDAssociation> getDirectAssociations(ASTCDType type) {
    CachedType cachedType = typeCache.get(type);
    if (cachedType != null) {
      return cachedType.getDirectAssociations();
    }
    return Set.of();
  }

  public static boolean addAllAttributes(ASTCDType type, Set<ASTCDAttribute> attribute) {
    CachedType cachedType = typeCache.get(type);
    if (cachedType == null) {
      return false;
    }
    for (ASTCDAttribute attr : attribute) {
      cachedType.addAttribute(attr);
    }
    return true;
  }

  public static Set<ASTCDAttribute> getAttributes(ASTCDType type) {
    CachedType cachedType = typeCache.get(type);
    if (cachedType != null) {
      return cachedType.getAttributes();
    }
    return Set.of();
  }

  public static boolean addAllDirectAttributes(ASTCDType type, Set<ASTCDAttribute> attributes) {
    CachedType cachedType = typeCache.get(type);
    if (cachedType == null) {
      return false;
    }
    for (ASTCDAttribute attr : attributes) {
      cachedType.addDirectAttribute(attr);
    }
    return true;
  }

  public static Set<ASTCDAttribute> getDirectAttributes(ASTCDType type) {
    CachedType cachedType = typeCache.get(type);
    if (cachedType != null) {
      return cachedType.getDirectAttributes();
    }
    return Set.of();
  }

  public static boolean addAllSuperTypes(ASTCDType type, Set<ASTCDType> superTypes) {
    CachedType cachedType = typeCache.get(type);
    if (cachedType == null) {
      return false;
    }
    for (ASTCDType superType : superTypes) {
      cachedType.addSuperType(superType);
    }
    return true;
  }

  public static Set<ASTCDType> getSuperTypes(ASTCDType type) {
    CachedType cachedType = typeCache.get(type);
    if (cachedType != null) {
      return cachedType.getSuperTypes();
    }
    return Set.of();
  }

  public static boolean addAllDirectSuperTypes(ASTCDType type, Set<ASTCDType> superTypes) {
    CachedType cachedType = typeCache.get(type);
    if (cachedType == null) {
      return false;
    }
    for (ASTCDType superType : superTypes) {
      cachedType.addDirectSuperType(superType);
    }
    return true;
  }

  public static Set<ASTCDType> getDirectSuperTypes(ASTCDType type) {
    CachedType cachedType = typeCache.get(type);
    if (cachedType != null) {
      return cachedType.getDirectSuperTypes();
    }
    return Set.of();
  }

  public static boolean addAllDirectSubTypes(ASTCDType type, Set<ASTCDType> subTypes) {
    CachedType cachedType = typeCache.get(type);
    if (cachedType == null) {
      return false;
    }
    for (ASTCDType subType : subTypes) {
      cachedType.addDirectSubType(subType);
    }
    return true;
  }

  public static Set<ASTCDType> getDirectSubTypes(ASTCDType type) {
    CachedType cachedType = typeCache.get(type);
    if (cachedType != null) {
      return cachedType.getDirectSubTypes();
    }
    return Set.of();
  }

  public static void clear() {
    assocCache.clear();
    typeCache.clear();
  }
}
