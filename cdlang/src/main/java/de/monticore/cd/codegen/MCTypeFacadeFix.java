/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen;

import com.google.common.collect.Lists;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.monticore.types.mccollectiontypes._ast.ASTMCListType;
import de.monticore.types.mccollectiontypes._ast.ASTMCSetType;
import de.monticore.types.mcfullgenerictypes.MCFullGenericTypesMill;

// TODO: Move this fix upstream if MCFullGenericTypes (?) is initialized 
public class MCTypeFacadeFix {
  
  public static ASTMCListType createListTypeOf(final ASTMCType type) {
    // Fix for IElem<IStateElem>
    var b = MCFullGenericTypesMill.mCListTypeBuilder();
    b.setMCTypeArgument(CD4CodeMill.mCCustomTypeArgumentBuilder().setMCType(type.deepClone())
        .build());
    return b.build();
  }
  
  public static ASTMCSetType createSetTypeOf(final ASTMCType type) {
    // Fix for IElem<IStateElem>
    var b = MCFullGenericTypesMill.mCSetTypeBuilder();
    b.setMCTypeArgument(CD4CodeMill.mCCustomTypeArgumentBuilder().setMCType(type.deepClone())
        .build());
    return b.build();
  }
  
  public ASTMCGenericType createCollectionTypeOf(final ASTMCType type) {
    var b = MCFullGenericTypesMill.mCBasicGenericTypeBuilder();
    b.setNamesList(Lists.newArrayList("Collection"));
    b.addMCTypeArgument(CD4CodeMill.mCCustomTypeArgumentBuilder().setMCType(type.deepClone())
        .build());
    return b.build();
  }
  
}
