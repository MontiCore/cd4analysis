<#-- (c) https://github.com/MontiCore/monticore -->
<#-- inner method for deepClone -->
<#-- this method is used to clone different attributes of the pojo class -->
<#-- its primary purpose is to enable recursive which are need when resolving Lists and Sets -->
${tc.signature("originalClazzType","mCType", "PojoClazzesAsStringList","thisObjectName", "resultName")}
<#assign CD4AnalysisTypeDispatcher = glex.getGlobalVar("cd4AnalysisTypeDispatcher")>
<#-- Set types -->
<#if (CD4AnalysisTypeDispatcher.isMCCollectionTypesASTMCSetType(mCType))>
if(${thisObjectName} == null) {
  ${resultName} = null;
} else {

}
<#-- List types -->
<#elseif (CD4AnalysisTypeDispatcher.isMCCollectionTypesASTMCListType(mCType))>
if(${thisObjectName} == null) {
  ${resultName} = null;
} else {

}
<#-- optional types -->
<#elseif (CD4AnalysisTypeDispatcher.isMCCollectionTypesASTMCOptionalType(mCType))>
if(${thisObjectName} == null) {
  ${resultName} = null;
} else {

}

<#-- primitive types -->
<#-- can not be null -->
<#elseif (CD4AnalysisTypeDispatcher.isMCBasicTypesASTMCPrimitiveType(mCType))>

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

}
<#-- all other types -->
  <#else>
if(${thisObjectName} == null) {
  ${resultName} = null;
} else {

}
  </#if>
</#if>
