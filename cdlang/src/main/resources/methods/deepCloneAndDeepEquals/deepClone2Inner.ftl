<#-- (c) https://github.com/MontiCore/monticore -->
<#-- inner method for deepClone -->
<#-- this method is used to clone different attributes of the pojo class -->
<#-- its primary purpose is to enable recursive which are need when resolving Lists and Sets -->
${tc.signature("mCType", "PojoClazzesAsStringList","thisObjectName", "resultName")}
<#assign CD4AnalysisTypeDispatcher = glex.getGlobalVar("cd4AnalysisTypeDispatcher")>
<#-- Set types -->
<#if (CD4AnalysisTypeDispatcher.isMCCollectionTypesASTMCSetType(mCType))>
if(${thisObjectName} == null) {
  ${resultName} = null;
} else {
<#assign innerType = (mCType.getMCTypeArgument().getMCTypeOpt().get())>
<#assign iteratorName = "iterator"+mCType.hashCode()?replace(".","")?replace(",","")>
java.util.Iterator<${innerType.printType()}> ${iteratorName} = ${thisObjectName}.iterator();
while(${iteratorName}.hasNext()) {
  <#assign newInnerType = "newInnerType" + mCType.hashCode()?replace(".","")?replace(",","")>
  ${innerType.printType()} ${newInnerType} = ${iteratorName}.next();
    ${includeArgs("methods.deepCloneAndDeepEquals.deepClone2Inner", innerType, PojoClazzesAsStringList, newInnerType, resultName)}
}

}
<#-- List types -->
<#elseif (CD4AnalysisTypeDispatcher.isMCCollectionTypesASTMCListType(mCType))>
if(${thisObjectName} == null) {
  ${resultName} = null;
} else {
<#assign innerType = (mCType.getMCTypeArgument().getMCTypeOpt().get())>
<#assign iteratorName = "iterator"+mCType.hashCode()?replace(".","")?replace(",","")>
java.util.Iterator<${innerType.printType()}> ${iteratorName} = ${thisObjectName}.iterator();
while(${iteratorName}.hasNext()) {
  <#assign newInnerType = "newInnerType" + mCType.hashCode()?replace(".","")?replace(",","")>
  ${innerType.printType()} ${newInnerType} = ${iteratorName}.next();
    ${includeArgs("methods.deepCloneAndDeepEquals.deepClone2Inner", innerType, PojoClazzesAsStringList, newInnerType, resultName)}
}

} optional types -->
<#elseif (CD4AnalysisTypeDispatcher.isMCCollectionTypesASTMCOptionalType(mCType))>
if(${thisObjectName} == null) {
  ${resultName} = null;
} else {
  if(${thisObjectName}.isPresent()) {
    <#assign innerType = (mCType.getMCTypeArgument().getMCTypeOpt().get())>
    <#assign newInnerType = "newInnerType" + mCType.hashCode()?replace(".","")?replace(",","")>
    ${innerType.printType()} ${newInnerType} = ${thisObjectName}.get();
    ${includeArgs("methods.deepCloneAndDeepEquals.deepClone2Inner", innerType, PojoClazzesAsStringList, newInnerType, resultName)}
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
  ${resultName} = ${thisObjectName}.deepClone(result, map);

}
<#-- all other types -->
  <#else>
if(${thisObjectName} == null) {
  ${resultName} = null;
} else {

}
  </#if>
</#if>
