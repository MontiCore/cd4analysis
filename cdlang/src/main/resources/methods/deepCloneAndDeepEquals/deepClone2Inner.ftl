<#-- (c) https://github.com/MontiCore/monticore -->
<#-- inner method for deepClone -->
<#-- this method is used to clone different attributes of the pojo class -->
<#-- its primary purpose is to enable recursive which are need when resolving Lists and Sets -->
${tc.signature("mCType", "PojoClazzesAsStringList","thisObjectName", "resultObjectName")}
<#assign CD4AnalysisTypeDispatcher = glex.getGlobalVar("cd4AnalysisTypeDispatcher")>
<#-- create the result object at the very start and fill thisObject and the resultObjects in  the map -->
<#assign newResultObjectName = "newResult" + mCType.hashCode()?replace(".","")?replace(",","")>
<#-- array type -->
<#if (CD4AnalysisTypeDispatcher.isMCArrayTypesASTMCArrayType(mCType))>
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
<#-- primitive types -->
<#-- can not be null -->
<#elseif (CD4AnalysisTypeDispatcher.isMCBasicTypesASTMCPrimitiveType(mCType))>
${resultObjectName} = ${thisObjectName};
<#-- pojo class types -->
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
<#-- all other types -->
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
