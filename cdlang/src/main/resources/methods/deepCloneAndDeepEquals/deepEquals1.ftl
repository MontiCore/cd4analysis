<#-- (c) https://github.com/MontiCore/monticore -->
<#-- 1 stands for 1 argument which is the object to compare with -->
<#-- this method just calls the deepEquals method with the second argument being set to true -->
<#-- therefore enforcing the right order of elements in set and lists -->
return deepEquals(o, true);
