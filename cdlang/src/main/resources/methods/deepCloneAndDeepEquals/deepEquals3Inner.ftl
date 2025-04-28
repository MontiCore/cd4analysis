<#-- (c) https://github.com/MontiCore/monticore -->
<#-- inner method for deepEquals -->
<#-- this method is used to compare the types of the current object with the types of the given object -->
<#-- its primary purpose is to enable recursive which are need when resolving Lists and Sets -->
${tc.signature("originalClazzType","mCType", "PojoClazzesAsStringList","firstObjectName", "secondObjectName","resultBooleanName")}
<#assign CD4AnalysisTypeDispatcher = glex.getGlobalVar("cd4AnalysisTypeDispatcher")>
<#-- Set types -->
<#if (CD4AnalysisTypeDispatcher.isMCCollectionTypesASTMCSetType(mCType))>
<#assign innerType = (mCType.getMCTypeArgument().getMCTypeOpt().get())>
if(${firstObjectName}.size() != ${secondObjectName}.size()){
  ${resultBooleanName} = false;
} else {
  <#assign firstIteratorName = "it1" + mCType.hashCode()?replace(".","")?replace(",","")>
  <#assign secondIteratorName = "it2" + mCType.hashCode()?replace(".","")?replace(",","")>
  <#assign it1NextName = "it1Next" + mCType.hashCode()?replace(".","")?replace(",","")>
  <#assign it2NextName = "it2Next" + mCType.hashCode()?replace(".","")?replace(",","")>
  <#assign matchFoundName = "matchFound" + mCType.hashCode()?replace(".","")?replace(",","")>
  java.util.Iterator<${innerType.printType()}> ${firstIteratorName} = ${firstObjectName}.iterator();
  while(${firstIteratorName}.hasNext()){
    ${innerType.printType()} ${it1NextName} = ${firstIteratorName}.next();
    boolean ${matchFoundName} = true;
    java.util.Iterator<${innerType.printType()}> ${secondIteratorName} = ${secondObjectName}.iterator();
    while(${secondIteratorName}.hasNext()){
      ${matchFoundName} = true;
      ${innerType.printType()} ${it2NextName} = ${secondIteratorName}.next();
      ${includeArgs("methods.deepCloneAndDeepEquals.deepEquals3Inner", originalClazzType, innerType, PojoClazzesAsStringList, it1NextName, it2NextName, matchFoundName)};
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
<#assign innerType = (mCType.getMCTypeArgument().getMCTypeOpt().get())>
if(${firstObjectName}.size() != ${secondObjectName}.size()){
  ${resultBooleanName} = false;
} else {
if(forceSameOrder){
  <#assign firstIteratorName = "it1" + mCType.hashCode()?replace(".","")?replace(",","")>
  <#assign secondIteratorName = "it2" + mCType.hashCode()?replace(".","")?replace(",","")>
  <#assign it1NextName = "it1Next" + mCType.hashCode()?replace(".","")?replace(",","")>
  <#assign it2NextName = "it2Next" + mCType.hashCode()?replace(".","")?replace(",","")>
  <#assign isEqual = "isEqual" + mCType.hashCode()?replace(".","")?replace(",","")>
  java.util.Iterator<${innerType.printType()}> ${firstIteratorName} = ${firstObjectName}.iterator();
  java.util.Iterator<${innerType.printType()}> ${secondIteratorName} = ${secondObjectName}.iterator();
  while(${firstIteratorName}.hasNext()){
    ${innerType.printType()} ${it1NextName} = ${firstIteratorName}.next();
    ${innerType.printType()} ${it2NextName} = ${secondIteratorName}.next();
    boolean ${isEqual} = true;
    ${includeArgs("methods.deepCloneAndDeepEquals.deepEquals3Inner", originalClazzType, innerType,PojoClazzesAsStringList, it1NextName, it2NextName, isEqual)};
    if(!${isEqual}){
      return false;
    }
  }
} else {
  <#assign firstIteratorName = "it1" + mCType.hashCode()?replace(".","")?replace(",","")>
  <#assign secondIteratorName = "it2" + mCType.hashCode()?replace(".","")?replace(",","")>
  <#assign it1NextName = "it1Next" + mCType.hashCode()?replace(".","")?replace(",","")>
  <#assign it2NextName = "it2Next" + mCType.hashCode()?replace(".","")?replace(",","")>
  <#assign matchFoundName = "matchFound" + mCType.hashCode()?replace(".","")?replace(",","")>
  java.util.Iterator<${innerType.printType()}> ${firstIteratorName} = ${firstObjectName}.iterator();
  while(${firstIteratorName}.hasNext()){
    ${innerType.printType()} ${it1NextName} = ${firstIteratorName}.next();
    boolean ${matchFoundName} = true;
    java.util.Iterator<${innerType.printType()}> ${secondIteratorName} = ${secondObjectName}.iterator();
    while(${secondIteratorName}.hasNext()){
      ${matchFoundName} = true;
      ${innerType.printType()} ${it2NextName} = ${secondIteratorName}.next();
      ${includeArgs("methods.deepCloneAndDeepEquals.deepEquals3Inner", originalClazzType, innerType, PojoClazzesAsStringList, it1NextName, it2NextName, matchFoundName)};
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
<#assign innerType = (mCType.getMCTypeArgument().getMCTypeOpt().get())>
<#-- if the first object is not present and the second object is present, return false -->
if(${firstObjectName}.isPresent() && ${secondObjectName}.isEmpty() ||
  ${firstObjectName}.isEmpty() && ${secondObjectName}.isPresent()){
  ${resultBooleanName} = false;
} else if(${firstObjectName}.isPresent() && ${secondObjectName}.isPresent()){
  ${includeArgs("methods.deepCloneAndDeepEquals.deepEquals3Inner", originalClazzType, innerType, PojoClazzesAsStringList, firstObjectName + ".get()", secondObjectName + ".get()", resultBooleanName)};
}
<#-- primitive types -->
<#elseif (CD4AnalysisTypeDispatcher.isMCBasicTypesASTMCPrimitiveType(mCType))>
${resultBooleanName} = ${firstObjectName} == ${secondObjectName};
<#-- pojo class types -->
<#else>
<#-- only when the type is present in the class diagram the getDefiningSymbol is present -->
  <#if mCType.getDefiningSymbol().isPresent()>
  <#assign resolvedClassName = mCType.getDefiningSymbol().get().getFullName()>
  <#else>
     <#assign resolvedClassName = mCType.getMCQualifiedName().getQName()>
  </#if>
   <#if (PojoClazzesAsStringList?seq_contains(resolvedClassName))>
  ${resultBooleanName} = ${firstObjectName}.deepEquals(${secondObjectName}, forceSameOrder, visitedObjects);
<#-- all other types -->
  <#else>
  ${resultBooleanName} = ${secondObjectName}.equals(${firstObjectName});
  </#if>
</#if>

