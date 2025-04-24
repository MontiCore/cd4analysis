<#-- (c) https://github.com/MontiCore/monticore -->
<#-- inner method for deepEquals -->
<#-- this method is used to compare the types of the current object with the types of the given object -->
<#-- its primary purpose is to enable recursive which are need when resolving Lists and Sets -->
${tc.signature("originalClazzType","mCType","typeName", "PojoClazzesAsStringList","firstObjectName", "secondObjectName","resultBooleanName")}
<#assign CD4AnalysisTypeDispatcher = glex.getGlobalVar("cd4AnalysisTypeDispatcher")>
<#-- Set types -->
<#if (CD4AnalysisTypeDispatcher.isMCCollectionTypesASTMCSetType(mCType))>
<#assign innerType = (mCType.getMCTypeArgument())>
if(${firstObjectName}.size() != ${secondObjectName}.size()){
  ${resultBooleanName} = false;
} else {
  <#assign firstIteratorName = "it1" + mCType.hashCode()?replace(".","")>
  <#assign secondIteratorName = "it2" + mCType.hashCode()?replace(".","")>
  <#assign it1NextName = "it1Next" + mCType.hashCode()?replace(".","")>
  <#assign it2NextName = "it2Next" + mCType.hashCode()?replace(".","")>
  <#assign matchFoundName = "matchFound" + mCType.hashCode()?replace(".","")>
  java.util.Iterator<${innerType.printType()}> firstIteratorName = ${firstObjectName}.iterator();
  while(firstIteratorName.hasNext()){
    ${innerType.printType()} ${it1NextName} = firstIteratorName.next();
    boolean ${matchFoundName} = false;
    java.util.Iterator<${innerType.printType()}> ${secondIteratorName} = ${secondObjectName}.iterator();
    while(${secondIteratorName}.hasNext()){
      ${innerType.printType()} ${it2NextName} = ${secondIteratorName}.next();
      ${includeArgs("methods.deepCloneAndDeepEquals.deepEquals3Inner", originalClazzType, innerType,innerType.printType() PojoClazzesAsStringList, it1NextName, it2NextName, matchFoundName)};
      if(${matchFoundName}){
        break;
      }
    }
    if(!${matchFoundName}){
      ${resultBooleanName} = false;
    }
  }
}
<#-- List types -->
<#elseif (CD4AnalysisTypeDispatcher.isMCCollectionTypesASTMCListType(mCType))>
if(${firstObjectName}.size() != ${secondObjectName}.size()){
  ${resultBooleanName} = false;
} else {
if(forceSameOrder){
  <#assign firstIteratorName = "it1" + mCType.hashCode()?replace(".","")>
  <#assign secondIteratorName = "it2" + mCType.hashCode()?replace(".","")>
  <#assign it1NextName = "it1Next" + mCType.hashCode()?replace(".","")>
  <#assign it2NextName = "it2Next" + mCType.hashCode()?replace(".","")>
  <#assign isEqual = "isEqual" + mCType.hashCode()?replace(".","")>
  while(firstIteratorName.hasNext()){
    java.util.Iterator<${innerType.printType()}> firstIteratorName = ${firstObjectName}.iterator();
    java.util.Iterator<${innerType.printType()}> ${secondIteratorName} = ${secondObjectName}.iterator();
    ${innerType.printType()} ${it1NextName} = firstIteratorName.next();
    ${innerType.printType()} ${it2NextName} = ${secondIteratorName}.next();
    $boolean ${isEqual} = true;
    ${includeArgs("methods.deepCloneAndDeepEquals.deepEquals3Inner", originalClazzType, innerType,innerType.printType() PojoClazzesAsStringList, it1NextName, it2NextName, isEqual)};
    if(!${isEqual}){
      return false;
    }
  }
} else {
  <#assign firstIteratorName = "it1" + mCType.hashCode()?replace(".","")>
  <#assign secondIteratorName = "it2" + mCType.hashCode()?replace(".","")>
  <#assign it1NextName = "it1Next" + mCType.hashCode()?replace(".","")>
  <#assign it2NextName = "it2Next" + mCType.hashCode()?replace(".","")>
  <#assign matchFoundName = "matchFound" + mCType.hashCode()?replace(".","")>
  java.util.Iterator<${innerType.printType()}> firstIteratorName = ${firstObjectName}.iterator();
  while(firstIteratorName.hasNext()){
    ${innerType.printType()} ${it1NextName} = firstIteratorName.next();
    boolean ${matchFoundName} = false;
    java.util.Iterator<${innerType.printType()}> ${secondIteratorName} = ${secondObjectName}.iterator();
    while(${secondIteratorName}.hasNext()){
      ${innerType.printType()} ${it2NextName} = ${secondIteratorName}.next();
      ${includeArgs("methods.deepCloneAndDeepEquals.deepEquals3Inner", originalClazzType, innerType,innerType.printType() PojoClazzesAsStringList, it1NextName, it2NextName, matchFoundName)};
      if(${matchFoundName}){
        break;
      }
    }
    if(!${matchFoundName}){
      ${resultBooleanName} = false;
    }
  }
  }
}
<#-- optional types -->
<#elseif (CD4AnalysisTypeDispatcher.isMCCollectionTypesASTMCOptionalType(mCType))>
if(${firstObjectName}.isPresent() != ${secondObjectName}.isPresent() ||
  (${firstObjectName}.isPresent() && !${firstObjectName}.get().deepEquals(${secondObjectName}.get(), forceSameOrder, visitedObjects))){
  ${resultBooleanName} = false;
}
<#-- primitive types -->
<#elseif mCType??>
<#if (CD4AnalysisTypeDispatcher.isMCBasicTypesASTMCPrimitiveType(mCType))>
${resultBooleanName} = ${firstObjectName} == ${secondObjectName};
<#-- pojo class types -->
<#elseif (PojoClazzesAsStringList?seq_contains(typeName))>
${resultBooleanName} = ${firstObjectName}.deepEquals(${secondObjectName}, forceSameOrder, visitedObjects);
<#-- all other types -->
<#else>
  ${resultBooleanName} = ${secondObjectName}.equals(${firstObjectName});
</#if>
</#if>

