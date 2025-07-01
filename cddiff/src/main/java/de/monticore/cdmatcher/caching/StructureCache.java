/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmatcher.caching;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import org.antlr.v4.runtime.misc.MultiMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class StructureCache {

  private final Map<ASTCDAssociation, CachedAssoc> assocCache = new HashMap<>();
  private final Map<ASTCDType, CachedType> typeCache = new HashMap<>();

  public boolean addAssociation(ASTCDAssociation assoc, ASTCDType leftType, ASTCDType rightType) {
    if (assocCache.containsKey(assoc)) {
      return false;
    }
    assocCache.put(assoc, new CachedAssoc(assoc, leftType, rightType));
    return true;
  }

  public Optional<ASTCDType> getLeftType(ASTCDAssociation assoc) {
    if (assocCache.containsKey(assoc)) {
      ASTCDType leftType = assocCache.get(assoc).getLeftType();
      if (leftType == null) {
        return Optional.empty();
      }
      return Optional.of(leftType);
    }
    return Optional.empty();
  }

  public Optional<ASTCDType> getRightType(ASTCDAssociation assoc) {
    if (assocCache.containsKey(assoc)) {
      ASTCDType rightType = assocCache.get(assoc).getRightType();
      if (rightType == null) {
        return Optional.empty();
      }
      return Optional.of(rightType);
    }
    return Optional.empty();
  }

  public boolean addType(ASTCDType type) {
    if (typeCache.containsKey(type)) {
      return false;
    }
    typeCache.put(type, new CachedType(type));
    return true;
  }

  public boolean addAllAssociations(ASTCDType type, Set<ASTCDAssociation> associations) {
    CachedType cachedType = typeCache.get(type);
    if (cachedType == null) {
      return false;
    }
    for (ASTCDAssociation assoc : associations) {
      cachedType.addAssociation(assoc);
    }
    return true;
  }

  public Set<ASTCDAssociation> getAssociations(ASTCDType type) {
    CachedType cachedType = typeCache.get(type);
    if (cachedType != null) {
      return cachedType.getAssociations();
    }
    return Set.of();
  }

  public boolean addAllDirectAssociations(ASTCDType type, Set<ASTCDAssociation> associations) {
    CachedType cachedType = typeCache.get(type);
    if (cachedType == null) {
      return false;
    }
    for (ASTCDAssociation assoc : associations) {
      cachedType.addDirectAssociation(assoc);
    }
    return true;
  }

  public Set<ASTCDAssociation> getDirectAssociations(ASTCDType type) {
    CachedType cachedType = typeCache.get(type);
    if (cachedType != null) {
      return cachedType.getDirectAssociations();
    }
    return Set.of();
  }

  public boolean addAllAttributes(ASTCDType type, Set<ASTCDAttribute> attribute) {
    CachedType cachedType = typeCache.get(type);
    if (cachedType == null) {
      return false;
    }
    for (ASTCDAttribute attr : attribute) {
      cachedType.addAttribute(attr);
    }
    return true;
  }

  public Set<ASTCDAttribute> getAttributes(ASTCDType type) {
    CachedType cachedType = typeCache.get(type);
    if (cachedType != null) {
      return cachedType.getAttributes();
    }
    return Set.of();
  }

  public boolean addAllDirectAttributes(ASTCDType type, Set<ASTCDAttribute> attributes) {
    CachedType cachedType = typeCache.get(type);
    if (cachedType == null) {
      return false;
    }
    for (ASTCDAttribute attr : attributes) {
      cachedType.addDirectAttribute(attr);
    }
    return true;
  }

  public Set<ASTCDAttribute> getDirectAttributes(ASTCDType type) {
    CachedType cachedType = typeCache.get(type);
    if (cachedType != null) {
      return cachedType.getDirectAttributes();
    }
    return Set.of();
  }

  public boolean addAllSuperTypes(ASTCDType type, Set<ASTCDType> superTypes) {
    CachedType cachedType = typeCache.get(type);
    if (cachedType == null) {
      return false;
    }
    for (ASTCDType superType : superTypes) {
      cachedType.addSuperType(superType);
    }
    return true;
  }

  public Set<ASTCDType> getSuperTypes(ASTCDType type) {
    CachedType cachedType = typeCache.get(type);
    if (cachedType != null) {
      return cachedType.getSuperTypes();
    }
    return Set.of();
  }

  public boolean addAllDirectSuperTypes(ASTCDType type, Set<ASTCDType> superTypes) {
    CachedType cachedType = typeCache.get(type);
    if (cachedType == null) {
      return false;
    }
    for (ASTCDType superType : superTypes) {
      cachedType.addDirectSuperType(superType);
    }
    return true;
  }

  public Set<ASTCDType> getDirectSuperTypes(ASTCDType type) {
    CachedType cachedType = typeCache.get(type);
    if (cachedType != null) {
      return cachedType.getDirectSuperTypes();
    }
    return Set.of();
  }

  public boolean addAllDirectSubTypes(ASTCDType type, Set<ASTCDType> subTypes) {
    CachedType cachedType = typeCache.get(type);
    if (cachedType == null) {
      return false;
    }
    for (ASTCDType subType : subTypes) {
      cachedType.addDirectSubType(subType);
    }
    return true;
  }

  public Set<ASTCDType> getDirectSubTypes(ASTCDType type) {
    CachedType cachedType = typeCache.get(type);
    if (cachedType != null) {
      return cachedType.getDirectSubTypes();
    }
    return Set.of();
  }

  public MultiMap<ASTCDType, ASTCDType> getSuperTypeMap() {
    MultiMap<ASTCDType, ASTCDType> superTypeMap = new MultiMap<>();
    for (CachedType cachedType : typeCache.values()) {
      superTypeMap.put(cachedType.getCachedType(), new ArrayList<>(cachedType.getSuperTypes()));
    }
    return superTypeMap;
  }

}
