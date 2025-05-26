<#-- (c) https://github.com/MontiCore/monticore -->
<#-- 3 stands for 3 argument which is the object to compare with -->
<#-- the first argument is the object to compare with -->
<#-- the second argument is a boolean which will decide if the right order of elements in set and lists is enforced -->
<#-- the third argument is a map which will be used to store the already visited objects as the language can have circular structure -->
<#-- to remember the visited objects we therefore need to save them -->
${tc.signature("originalClazzType", "attributeList", "PojoClazzesAsStringList")}
<#-- as we want terminate on cyclic relations we need to add the object before we compare it sto our visited objects -->
<#-- we will later delete it after comparing, so that if a object exists multiple times in a non cyclic way, it is checked anyways-->
if(!(o instanceof ${originalClazzType.printType()})){
  return false;
}
${originalClazzType.printType()} castO = (${originalClazzType.printType()}) o;
if(visitedObjects.get(this) != null){
  if(visitedObjects.get(this).contains(castO)){
    return true;
  }
  visitedObjects.get(this).add(castO);
}else{
  visitedObjects.put(this,new HashSet(Collections.singletonList(castO)));
}
<#if attributeList??>
<#list attributeList as attr>
<#-- we need to declare a boolean result, as in recursive list checks we cannot return false when we check while having the flag forceSameOrder set to false -->
<#assign resultBooleanName = "result" + attr.getName()?cap_first + attr.getMCType().printType()?cap_first?replace(".","")?replace("<","")?replace(">","")?replace("[","")?replace("]","")?replace(",","")>
boolean ${resultBooleanName} = true;
<#assign firstObjectName = "this." + attr.getName()>
<#assign secondObjectName = "castO." + attr.getName()>
  <#-- we call the deepEquals3Inner template here which can be called repulsively when the type is a List or a Set -->
  ${includeArgs("methods.deepCloneAndDeepEquals.deepEquals3Inner", attr.getMCType(), PojoClazzesAsStringList, firstObjectName, secondObjectName, resultBooleanName)}
visitedObjects.get(this).remove(castO);
if(! ${resultBooleanName}){
  return false;
}
</#list>
</#if>
return true;
