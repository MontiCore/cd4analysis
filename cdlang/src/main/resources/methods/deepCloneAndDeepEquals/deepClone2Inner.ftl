<#-- (c) https://github.com/MontiCore/monticore -->
<#-- inner method for deepClone -->
<#-- this method is used to clone different attributes of the pojo class -->
<#-- its primary purpose is to enable recursive which are need when resolving Lists and Sets -->
${tc.signature("mCType", "PojoClazzesAsStringList","thisObjectName", "resultObjectName")}
<#assign CD4AnalysisTypeDispatcher = glex.getGlobalVar("cd4AnalysisTypeDispatcher")>
<#-- create the result object at the very start and fill thisObject and the resultObjects in  the map -->
<#assign newResultObjectName = "newResult" + mCType.hashCode()?replace(".","")?replace(",","")>
<#-- Array type -->
<#if (CD4AnalysisTypeDispatcher.isMCArrayTypesASTMCArrayType(mCType))>
if(${thisObjectName} == null) {
  ${resultObjectName} = null;
} else {
  if(map.get(${thisObjectName}) == null) {
  <#assign arrayType = mCType.getMCType()>
  <#assign arrayTypeName = arrayType.printType()>
  <#assign depth = mCType.getDimensions()>
  <#assign resultBracketsWithSize = "">
  <#assign resultBracketsInitialize = "">
  <#assign thisObjectArrayBracketsWith0index = "">
  <#assign resultObjectCurrentBrackets = "">
  <#list 0..depth-1 as i>
    int arrayDim${i} = ${thisObjectName + thisObjectArrayBracketsWith0index}.length;
    <#assign thisObjectArrayBracketsWith0index = thisObjectArrayBracketsWith0index + "[0]">
    <#assign resultBracketsWithSize = resultBracketsWithSize + "[arrayDim" + i + "]">
    <#assign resultBracketsInitialize = resultBracketsInitialize + "[]">
    <#assign resultObjectCurrentBrackets = resultObjectCurrentBrackets + "[i${i}]">
  </#list>
  ${arrayTypeName}${resultBracketsInitialize} ${newResultObjectName} = new ${arrayTypeName}${resultBracketsWithSize};
  <#list 0..depth-1 as i>
    for(int i${i} = 0; i${i} < arrayDim${i}; i${i}++) {
  </#list>
  <#assign innerTypeResultName = "innerType" + mCType.hashCode()?replace(".","")?replace(",","")>
  ${arrayTypeName} ${innerTypeResultName};
  ${includeArgs("methods.deepCloneAndDeepEquals.deepClone2Inner", arrayType, PojoClazzesAsStringList, thisObjectName + resultObjectCurrentBrackets, innerTypeResultName)}
  ${newResultObjectName + resultObjectCurrentBrackets} = ${innerTypeResultName};
  <#list 0..depth-1 as i>
    }
    <#assign mapAddArrayBrackets = "">
    <#list 0..(depth-(i+1)) as j>
       <#if j == 0>
         <#assign mapAddArrayBrackets = mapAddArrayBrackets >
       <#else>
         <#assign mapAddArrayBrackets = mapAddArrayBrackets + "[i" + (j-1) + "]">
       </#if>
    </#list>
    map.put(${thisObjectName} ${mapAddArrayBrackets}, new Object[] {${newResultObjectName + mapAddArrayBrackets}, true});
  </#list>
  }else{
      ${resultObjectName} = (${mCType.printType()}) map.get(${thisObjectName})[0];
  }
}
<#-- Set types -->
<#elseif (CD4AnalysisTypeDispatcher.isMCCollectionTypesASTMCSetType(mCType))>
if(${thisObjectName} == null) {
  ${resultObjectName} = null;
} else {
  <#assign innerType = (mCType.getMCTypeArgument().getMCTypeOpt().get())>
  <#assign iteratorName = "iterator"+mCType.hashCode()?replace(".","")?replace(",","")>
  if(map.get(${thisObjectName}) == null) {
    ${resultObjectName} = new HashSet<>();
    map.put(${thisObjectName}, new Object[] {${resultObjectName}, true});
    java.util.Iterator<${innerType.printType()}> ${iteratorName} = ${thisObjectName}.iterator();
    while(${iteratorName}.hasNext()) {
      <#assign newInnerType = "newInnerType" + mCType.hashCode()?replace(".","")?replace(",","")>
      ${innerType.printType()} ${newResultObjectName};
      ${innerType.printType()} ${newInnerType} = ${iteratorName}.next();
      ${includeArgs("methods.deepCloneAndDeepEquals.deepClone2Inner", innerType, PojoClazzesAsStringList, newInnerType, newResultObjectName)}
      ${resultObjectName}.add(${newResultObjectName});
    }
  }else{
    ${resultObjectName} = (${mCType.printType()}) map.get(${thisObjectName})[0];
  }
}
<#-- List types -->
<#elseif (CD4AnalysisTypeDispatcher.isMCCollectionTypesASTMCListType(mCType))>
if(${thisObjectName} == null) {
  ${resultObjectName} = null;
} else {
  <#assign innerType = (mCType.getMCTypeArgument().getMCTypeOpt().get())>
  <#assign iteratorName = "iterator"+mCType.hashCode()?replace(".","")?replace(",","")>
  if(map.get(${thisObjectName}) == null) {
    ${resultObjectName} = new ArrayList<>();
    map.put(${thisObjectName}, new Object[] {${resultObjectName}, true});
    java.util.Iterator<${innerType.printType()}> ${iteratorName} = ${thisObjectName}.iterator();
    while(${iteratorName}.hasNext()) {
      <#assign newInnerType = "newInnerType" + mCType.hashCode()?replace(".","")?replace(",","")>
      ${innerType.printType()} ${newResultObjectName};
      ${innerType.printType()} ${newInnerType} = ${iteratorName}.next();
      ${includeArgs("methods.deepCloneAndDeepEquals.deepClone2Inner", innerType, PojoClazzesAsStringList, newInnerType, newResultObjectName)}
      ${resultObjectName}.add(${newResultObjectName});
    }
  }else{
    ${resultObjectName} = (${mCType.printType()}) map.get(${thisObjectName})[0];
  }
}
<#-- Map types -->
<#elseif (CD4AnalysisTypeDispatcher.isMCCollectionTypesASTMCMapType(mCType))>
if(${thisObjectName} == null) {
  ${resultObjectName} = null;
} else {
  <#assign keyType = mCType.getKey().getMCTypeOpt().get()>
  <#assign valueType = mCType.getValue().getMCTypeOpt().get()>
  <#assign iteratorName = "iterator"+mCType.hashCode()?replace(".","")?replace(",","")>
  if(map.get(${thisObjectName}) == null) {
    ${resultObjectName} = new HashMap<>();
    map.put(${thisObjectName}, new Object[] {${resultObjectName}, true});
    java.util.Iterator<${keyType.printType()}> ${iteratorName} = ${thisObjectName}.keySet().iterator();
    while(${iteratorName}.hasNext()) {
      <#assign thisKeyName = "thisKey" + mCType.hashCode()?replace(".","")?replace(",","")>
      <#assign thisValueName = "thisValue" + mCType.hashCode()?replace(".","")?replace(",","")>
      <#assign clonedKeyName = "clonedKey" + mCType.hashCode()?replace(".","")?replace(",","")>
      <#assign clonedValueName = "clonedValue" + mCType.hashCode()?replace(".","")?replace(",","")>
      ${keyType.printType()} ${thisKeyName} = ${iteratorName}.next();
      ${valueType.printType()} ${thisValueName} = ${thisObjectName}.get(${thisKeyName});
      ${keyType.printType()} ${clonedKeyName};
      ${valueType.printType()} ${clonedValueName};
      ${includeArgs("methods.deepCloneAndDeepEquals.deepClone2Inner", keyType, PojoClazzesAsStringList, thisKeyName, clonedKeyName)}
      ${includeArgs("methods.deepCloneAndDeepEquals.deepClone2Inner", valueType, PojoClazzesAsStringList, thisValueName, clonedValueName)}
      ${resultObjectName}.put(${clonedKeyName},${clonedValueName});
    }
  }else{
    ${resultObjectName} = (${mCType.printType()}) map.get(${thisObjectName})[0];
  }
}
<#-- Optional types -->
<#elseif (CD4AnalysisTypeDispatcher.isMCCollectionTypesASTMCOptionalType(mCType))>
if(${thisObjectName} == null) {
  ${resultObjectName} = null;
} else {
  ${resultObjectName} = Optional.empty();
  if(map.get(${thisObjectName}) == null) {
    if(${thisObjectName}.isPresent()) {
      <#assign innerType = (mCType.getMCTypeArgument().getMCTypeOpt().get())>
      <#assign newInnerType = "newInnerType" + innerType.hashCode()?replace(".","")?replace(",","")>
      ${innerType.printType()} ${newInnerType} = ${thisObjectName}.get();
      ${innerType.printType()} ${newResultObjectName};
      if(map.get(${newInnerType}) == null) {
        map.put(${thisObjectName}, new Object[] {${resultObjectName}, true});
        ${includeArgs("methods.deepCloneAndDeepEquals.deepClone2Inner", innerType, PojoClazzesAsStringList, newInnerType, newResultObjectName)}
        <#-- this is needed because the optional.empty() reference is changed when filling the optional with a value->>
        <#-- Because we can not have circular references in Optionals it is ok in this case to add the optional to the list after it has been resolved -->
        ${resultObjectName} = Optional.of(${newResultObjectName});
        map.put(${thisObjectName}, new Object[] {${resultObjectName}, true});
      }else{
        ${newResultObjectName} = (${innerType.printType()}) map.get(${newInnerType})[0];
        ${resultObjectName} = Optional.of(${newResultObjectName});
        map.put(${thisObjectName}, new Object[] {${resultObjectName}, true});
      }
    } else {
      ${resultObjectName} = Optional.empty();
      map.put(${thisObjectName}, new Object[] {${resultObjectName}, true});
    }
  }else{
    ${resultObjectName} = (${mCType.printType()}) map.get(${thisObjectName})[0];
  }
}
<#-- Primitive types -->
<#-- Primitive types can not be null -->
<#elseif (CD4AnalysisTypeDispatcher.isMCBasicTypesASTMCPrimitiveType(mCType))>
${resultObjectName} = ${thisObjectName};
<#-- Pojo class types -->
<#else>
<#-- only when the type is present in the class diagram the getDefiningSymbol is present -->
  <#if mCType.getDefiningSymbol().isPresent()>
  <#assign resolvedClassName = mCType.getDefiningSymbol().get().getFullName()>
  <#else>
     <#assign resolvedClassName = mCType.getMCQualifiedName().getQName()>
  </#if>
  <#if (PojoClazzesAsStringList?seq_contains(resolvedClassName))>
if(${thisObjectName} == null) {
  ${resultObjectName} = null;
} else {
  if(map.get(${thisObjectName}) == null) {
    ${resultObjectName} = ${thisObjectName}.deepClone(map);
  }else{
    ${resultObjectName} = (${mCType.printType()}) map.get(${thisObjectName})[0];
  }
}
<#-- All other types -->
  <#else>
if(${thisObjectName} == null) {
  ${resultObjectName} = null;
} else {
<#if mCType?has_content && mCType.printType()?has_content && (mCType.printType() == "java.lang.String" || mCType.printType() == "String")>
if(${thisObjectName} == null) {
  ${resultObjectName} = null;
} else {
  if(map.get(${thisObjectName}) == null) {
    ${resultObjectName} = new String(${thisObjectName});
    map.put(${thisObjectName}, new Object[] {${resultObjectName}, true});
   }else{
    ${resultObjectName} = (${mCType.printType()}) map.get(${thisObjectName})[0];
   }
}
<#else>
<#-- we cannot do this correctly if we land here the user has to implement the deepClone method via the TOP-Mechanism -->
<#-- adding to the map would not contribute, as we will copy the object anyway and multiple references will still be multiple references afterwards -->
  ${resultObjectName} = ${thisObjectName};
</#if>
}
  </#if>
</#if>
