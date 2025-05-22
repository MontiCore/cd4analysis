package de.monticore.cdmatcher;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdassociation._symboltable.CDRoleSymbol;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDMember;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cddiff.CDDiffUtil;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class CDTypeSimilarity implements CDSimilarity<ASTCDType>{
  public Double computeWeight(ASTCDType srcElem, ASTCDType tgtElem) {

    Set<ASTCDMember> srcMembers = CDDiffUtil.getAllSuperTypes(srcElem).stream().flatMap(st -> st.getCDMemberList().stream()).collect(Collectors.toSet());
    Set<ASTCDMember> tgtMembers = CDDiffUtil.getAllSuperTypes(tgtElem).stream().flatMap(st -> st.getCDMemberList().stream()).collect(Collectors.toSet());

    Set<ASTCDMember> tgtDeletedMembers = new LinkedHashSet<>(tgtMembers);
    Set<ASTCDMember> similarMembers = new LinkedHashSet<>();

    for (ASTCDMember x : srcMembers) {
      for (ASTCDMember y : tgtMembers) {
        if (x instanceof ASTCDAttribute && y instanceof ASTCDAttribute) {
          if (((ASTCDAttribute) x).getName().equals(((ASTCDAttribute) y).getName())) {
            tgtDeletedMembers.remove(y);
            similarMembers.add(x);
          }
        } else if (x instanceof ASTCDMethod && y instanceof ASTCDMethod) {
          if (((ASTCDMethod) x).getName().equals(((ASTCDMethod) y).getName())) {
            tgtDeletedMembers.remove(y);
            similarMembers.add(x);
          }
        }
      }
    }

    Set<ASTCDMember> allMembers = new LinkedHashSet<>(srcMembers);
    allMembers.addAll(tgtDeletedMembers);

    Set<CDRoleSymbol> srcRoles = CDDiffUtil.getAllSuperTypes(srcElem).stream().flatMap(st -> st.getSymbol().getCDRoleList().stream()).collect(Collectors.toSet());
    Set<CDRoleSymbol> tgtRoles = CDDiffUtil.getAllSuperTypes(tgtElem).stream().flatMap(st -> st.getSymbol().getCDRoleList().stream()).collect(Collectors.toSet());

    Set<CDRoleSymbol> tgtDeletedRoles = new LinkedHashSet<>(tgtRoles);
    Set<CDRoleSymbol> similarRoles = new LinkedHashSet<>();

    for (CDRoleSymbol x : srcRoles) {
      for (CDRoleSymbol y : tgtRoles) {
        if (x.getName().equals(y.getName())) {
          tgtDeletedRoles.remove(y);
          similarRoles.add(x);
        }
      }
    }

    Set<CDRoleSymbol> allRoles = new LinkedHashSet<>(tgtRoles);
    allRoles.addAll(tgtDeletedRoles);

    double similarity = similarMembers.size() + similarRoles.size();
    double unionSize = allMembers.size() + allRoles.size();

    if (unionSize < 1) {
      unionSize = 1;
    }

    if (srcElem.getName().equals(tgtElem.getName())){
      similarity+=2;
    }

    if (srcElem.getSymbol().getInternalQualifiedName().equals(tgtElem.getSymbol().getInternalQualifiedName())){
      similarity++;
    }

    return similarity / unionSize;
  }
}
