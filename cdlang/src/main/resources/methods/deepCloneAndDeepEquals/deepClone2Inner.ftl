<#-- (c) https://github.com/MontiCore/monticore -->
<#-- inner method for deepClone -->
<#-- this method is used to clone different attributes of the pojo class -->
<#-- its primary purpose is to enable recursive which are need when resolving Lists and Sets -->
${tc.signature("mCType", "PojoClazzesAsStringList","thisObjectName", "resultName")}
<#assign CD4AnalysisTypeDispatcher = glex.getGlobalVar("cd4AnalysisTypeDispatcher")>
<#-- create the result object at the very start and fill thisObject and the resultObjects in  the map -->
<#assign newResultName = "newResult" + mCType.hashCode()?replace(".","")?replace(",","")>
<#-- Set types -->
<#if (CD4AnalysisTypeDispatcher.isMCCollectionTypesASTMCSetType(mCType))>
if(${thisObjectName} == null) {
  ${resultName} = null;
} else {
  <#assign innerType = (mCType.getMCTypeArgument().getMCTypeOpt().get())>
  <#assign iteratorName = "iterator"+mCType.hashCode()?replace(".","")?replace(",","")>
  if(map.get(${thisObjectName}) == null) {
    ${resultName} = new HashSet<>();
    map.put(${thisObjectName}, new Object[] {${resultName}, false});
    java.util.Iterator<${innerType.printType()}> ${iteratorName} = ${thisObjectName}.iterator();
    while(${iteratorName}.hasNext()) {
      <#assign newInnerType = "newInnerType" + mCType.hashCode()?replace(".","")?replace(",","")>
      ${innerType.printType()} ${newResultName};
      ${innerType.printType()} ${newInnerType} = ${iteratorName}.next();
      ${includeArgs("methods.deepCloneAndDeepEquals.deepClone2Inner", innerType, PojoClazzesAsStringList, newInnerType, newResultName)}
      ${resultName}.add(${newResultName});
    }
  }else{
    ${resultName} = (${mCType.printType()}) map.get(${thisObjectName})[0];
  }
  map.get(${thisObjectName})[1] = true;
}
<#-- List types -->
<#elseif (CD4AnalysisTypeDispatcher.isMCCollectionTypesASTMCListType(mCType))>
if(${thisObjectName} == null) {
  ${resultName} = null;
} else {
  <#assign innerType = (mCType.getMCTypeArgument().getMCTypeOpt().get())>
  <#assign iteratorName = "iterator"+mCType.hashCode()?replace(".","")?replace(",","")>
  if(map.get(${thisObjectName}) == null) {
    ${resultName} = new ArrayList<>();
    map.put(${thisObjectName}, new Object[] {${resultName}, false});
    java.util.Iterator<${innerType.printType()}> ${iteratorName} = ${thisObjectName}.iterator();
    while(${iteratorName}.hasNext()) {
      <#assign newInnerType = "newInnerType" + mCType.hashCode()?replace(".","")?replace(",","")>
      ${innerType.printType()} ${newResultName};
      ${innerType.printType()} ${newInnerType} = ${iteratorName}.next();
      ${includeArgs("methods.deepCloneAndDeepEquals.deepClone2Inner", innerType, PojoClazzesAsStringList, newInnerType, newResultName)}
      ${resultName}.add(${newResultName});
    }
  }else{
    ${resultName} = (${mCType.printType()}) map.get(${thisObjectName})[0];
  }
  map.get(${thisObjectName})[1] = true;
}
<#-- Optional types -->
<#elseif (CD4AnalysisTypeDispatcher.isMCCollectionTypesASTMCOptionalType(mCType))>
if(${thisObjectName} == null) {
  ${resultName} = null;
} else {
  if(${thisObjectName}.isPresent()) {
    <#assign innerType = (mCType.getMCTypeArgument().getMCTypeOpt().get())>
    <#assign newInnerType = "newInnerType" + mCType.hashCode()?replace(".","")?replace(",","")>
    <#assign optionalResultName = "optionalResult" + mCType.hashCode()?replace(".","")?replace(",","")>
    ${innerType.printType()} ${newInnerType} = ${thisObjectName}.get();
    ${mCType.printType()} ${optionalResultName} = Optional.empty();
    ${innerType.printType()} ${newResultName};
    if(map.get(${thisObjectName}) == null) {
      map.put(${thisObjectName}, new Object[] {${optionalResultName}, false});
      ${includeArgs("methods.deepCloneAndDeepEquals.deepClone2Inner", innerType, PojoClazzesAsStringList, newInnerType, newResultName)}
    }else{
      ${newResultName} = (${innerType.printType()}) map.get(${thisObjectName})[0];
    }
    map.get(${thisObjectName})[1] = true;
    ${resultName} = Optional.of(${newInnerType});
  } else {
    ${resultName} = Optional.empty();
  }
}
<#-- primitive types -->
<#-- can not be null -->
<#elseif (CD4AnalysisTypeDispatcher.isMCBasicTypesASTMCPrimitiveType(mCType))>
${resultName} = ${thisObjectName};
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
  ${resultName} = null;
} else {
  ${mCType.printType()} ${newResultName} = new ${mCType.printType()}Builder().unsafeBuild();
  if(map.get(${thisObjectName}) == null) {
    map.put(${thisObjectName}, new Object[] {${thisObjectName}, false});
    ${resultName} = ${thisObjectName}.deepClone(${newResultName}, map);
  }else{
    ${resultName} = (${mCType.printType()}) map.get(${thisObjectName})[0];
  }
  map.get(${thisObjectName})[1] = true;
}
<#-- all other types -->
  <#else>
if(${thisObjectName} == null) {
  ${resultName} = null;
} else {
<#-- we cannot do this correctly if we land here the user has to implement the deepClone method via the TOP-Mechanism -->
<#-- adding to the map would not contribute, as we will copy the object anyway and multiple references will still be multiple references afterwards -->
  ${resultName} = ${thisObjectName};
}
  </#if>
</#if>
