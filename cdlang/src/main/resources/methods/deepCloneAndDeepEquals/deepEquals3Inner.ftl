<#-- (c) https://github.com/MontiCore/monticore -->
<#-- inner method for deepEquals -->
<#-- this method is used to compare the types of the current object with the types of the given object -->
<#-- its primary purpose is to enable recursive which are need when resolving Lists and Sets -->
${tc.signature("mCType", "PojoClazzesAsStringList","firstObjectName", "secondObjectName","resultBooleanName")}
<#assign CD4AnalysisTypeDispatcher = glex.getGlobalVar("cd4AnalysisTypeDispatcher")>
<#-- Define a macro to repeat a string n times -->
<#-- Array types -->
<#if (CD4AnalysisTypeDispatcher.isMCArrayTypesASTMCArrayType(mCType))>
<#assign arrayType = mCType.getMCType()>
<#assign arrayTypeName = arrayType.printType()>
<#assign depth = mCType.getDimensions()>
if(${firstObjectName} == null && ${secondObjectName} == null){
  ${resultBooleanName} = true;
}else{
  <#assign thisObjectArrayBracketsWith0index = "">
  <#list 0..depth-1 as i>
    int firstArrayDim${i} = ${firstObjectName + thisObjectArrayBracketsWith0index}.length;
    <#assign thisObjectArrayBracketsWith0index = thisObjectArrayBracketsWith0index + "[0]">
  </#list>
  <#list 0..depth-1 as i>
    <#assign mapAddArrayBrackets = "">
    <#list 0..i as j>
       <#if j == 0>
         <#assign mapAddArrayBrackets = mapAddArrayBrackets >
       <#else>
         <#assign mapAddArrayBrackets = mapAddArrayBrackets + "[i${j-1}]" >
       </#if>
    </#list>
    if(${firstObjectName}${mapAddArrayBrackets}.length != ${secondObjectName}${mapAddArrayBrackets}.length){
      ${resultBooleanName} = false;
    }else{
      for(int i${i} = 0; i${i} < firstArrayDim${i}; i${i}++) {
  </#list>
  <#assign isEqual = "isEqual">
  boolean isEqual = true;
  if(forceSameOrder){
    ${includeArgs("methods.deepCloneAndDeepEquals.deepEquals3Inner", arrayType, PojoClazzesAsStringList, firstObjectName + thisObjectArrayBracketsWith0index, secondObjectName + thisObjectArrayBracketsWith0index, isEqual)};
  }else{
    ${arrayType.printType()} ${firstObjectName}${mapAddArrayBrackets} = ${mCType.printType()};

  }
  if(!isEqual){
    ${resultBooleanName} = false;
  }
  <#list 0..depth-1 as i>
   }
  }
  </#list>


}
<#-- Set types -->
<#elseif (CD4AnalysisTypeDispatcher.isMCCollectionTypesASTMCSetType(mCType))>
<#assign innerType = (mCType.getMCTypeArgument().getMCTypeOpt().get())>
if(${firstObjectName} == null && ${secondObjectName} == null){
  ${resultBooleanName} = true;
}else{
  if((${firstObjectName} == null || ${secondObjectName} == null)||(${firstObjectName}.size() != ${secondObjectName}.size())){
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
        ${includeArgs("methods.deepCloneAndDeepEquals.deepEquals3Inner", innerType, PojoClazzesAsStringList, it1NextName, it2NextName, matchFoundName)};
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
<#-- List types -->
<#elseif (CD4AnalysisTypeDispatcher.isMCCollectionTypesASTMCListType(mCType))>
<#assign innerType = (mCType.getMCTypeArgument().getMCTypeOpt().get())>
if(${firstObjectName} == null && ${secondObjectName} == null){
  ${resultBooleanName} = true;
}else{
  if((${firstObjectName} == null || ${secondObjectName} == null)||(${firstObjectName}.size() != ${secondObjectName}.size())){
    ${resultBooleanName} = false;
  }
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
      ${includeArgs("methods.deepCloneAndDeepEquals.deepEquals3Inner", innerType,PojoClazzesAsStringList, it1NextName, it2NextName, isEqual)};
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
        ${includeArgs("methods.deepCloneAndDeepEquals.deepEquals3Inner", innerType, PojoClazzesAsStringList, it1NextName, it2NextName, matchFoundName)};
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
<#-- Map types -->
<#elseif (CD4AnalysisTypeDispatcher.isMCCollectionTypesASTMCMapType(mCType))>
if(${firstObjectName} == null && ${secondObjectName} == null){
  ${resultBooleanName} = true;
}else{
  if((${firstObjectName} == null || ${secondObjectName} == null)||(${firstObjectName}.size() != ${secondObjectName}.size())){
    ${resultBooleanName} = false;
  }else{
    <#assign keyType = mCType.getKey().getMCTypeOpt().get()>
    <#assign valueType = mCType.getValue().getMCTypeOpt().get()>
    <#assign firstKeySetName = "firstKeySet" + mCType.hashCode()?replace(".","")?replace(",","")>
    <#assign secondKeySetName = "secondKeySet" + mCType.hashCode()?replace(".","")?replace(",","")>
    <#assign firstKeySetIteratorName = "firstKeySetIterator" + mCType.hashCode()?replace(".","")?replace(",","")>
    Set<${keyType.printType()}> ${firstKeySetName} = ${firstObjectName}.keySet();
    Set<${keyType.printType()}> ${secondKeySetName} = ${secondObjectName}.keySet();
    Iterator<${keyType.printType()}> ${firstKeySetIteratorName} = ${firstKeySetName}.iterator();
    while(${firstKeySetIteratorName}.hasNext()){
      <#assign firstKeyObjectName = "firstKeyObjectName" + mCType.hashCode()?replace(".","")?replace(",","")>
      <#assign firstValueObjectName = "firstValueObjectName" + mCType.hashCode()?replace(".","")?replace(",","")>
      ${keyType.printType()} ${firstKeyObjectName} = ${firstKeySetIteratorName}.next();
      ${valueType.printType()} ${firstValueObjectName} = ${firstObjectName}.get(${firstKeyObjectName});
      <#assign secondKeySetIteratorName = "secondKeySetIteratorName" + mCType.hashCode()?replace(".","")?replace(",","")>
      <#assign secondKeySetName = "secondKeySet" + mCType.hashCode()?replace(".","")?replace(",","")>
      Iterator<${keyType.printType()}> ${secondKeySetIteratorName} = ${secondKeySetName}.iterator();
      <#assign matchFoundName = "matchFound" + mCType.hashCode()?replace(".","")?replace(",","")>
      boolean ${matchFoundName} = false;
      while(${secondKeySetIteratorName}.hasNext()){
        <#assign secondKeyObjectName = "secondKeyObject" + mCType.hashCode()?replace(".","")?replace(",","")>
        <#assign secondValueObjectName = "secondValueObjectName" + mCType.hashCode()?replace(".","")?replace(",","")>
        ${keyType.printType()} ${secondKeyObjectName} = ${secondKeySetIteratorName}.next();
        ${valueType.printType()} ${secondValueObjectName} = ${secondObjectName}.get(${secondKeyObjectName});
        <#assign keyIsEqual = "keyIsEqual" + mCType.hashCode()?replace(".","")?replace(",","")>
        <#assign valueIsEqual = "valueIsEqual" + mCType.hashCode()?replace(".","")?replace(",","")>
        boolean ${keyIsEqual} = true;
        boolean ${valueIsEqual} = true;
        ${includeArgs("methods.deepCloneAndDeepEquals.deepEquals3Inner", keyType, PojoClazzesAsStringList, firstKeyObjectName, secondKeyObjectName, keyIsEqual)};
        ${includeArgs("methods.deepCloneAndDeepEquals.deepEquals3Inner", valueType, PojoClazzesAsStringList, firstValueObjectName, secondValueObjectName, valueIsEqual)};
        if(${keyIsEqual} && ${valueIsEqual}){
          ${matchFoundName} = true;
          break;
        }
      }
      if(!${matchFoundName}){
        return false;
      }
    }
  }
}
<#-- Optional types -->
<#elseif (CD4AnalysisTypeDispatcher.isMCCollectionTypesASTMCOptionalType(mCType))>
<#assign innerType = (mCType.getMCTypeArgument().getMCTypeOpt().get())>
<#-- if the first object is not present and the second object is present, return false -->
if(${firstObjectName} == null && ${secondObjectName} == null){
  ${resultBooleanName} = true;
} else if(${firstObjectName} == null || ${secondObjectName} == null){
  ${resultBooleanName} = false;
}else{
  if(${firstObjectName}.isPresent() && ${secondObjectName}.isEmpty() ||
    ${firstObjectName}.isEmpty() && ${secondObjectName}.isPresent()){
    ${resultBooleanName} = false;
  } else if(${firstObjectName}.isPresent() && ${secondObjectName}.isPresent()){
    ${includeArgs("methods.deepCloneAndDeepEquals.deepEquals3Inner", innerType, PojoClazzesAsStringList, firstObjectName + ".get()", secondObjectName + ".get()", resultBooleanName)};
  }
}
<#-- Primitive types -->
<#-- Primitive types can not be null -->
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
if(${firstObjectName} == null && ${secondObjectName} == null){
  ${resultBooleanName} = true;
}else if(${firstObjectName} == null || ${secondObjectName} == null){
  ${resultBooleanName} = false;
}else{
  ${resultBooleanName} = ${firstObjectName}.deepEquals(${secondObjectName}, forceSameOrder, visitedObjects);
}
<#-- all other types -->
  <#else>
  if(${firstObjectName} == null && ${secondObjectName} == null){
    ${resultBooleanName} = true;
  } else if(${firstObjectName} == null || ${secondObjectName} == null){
    ${resultBooleanName} = false;
  } else {
  ${resultBooleanName} = ${secondObjectName}.equals(${firstObjectName});
  }
  </#if>
</#if>

