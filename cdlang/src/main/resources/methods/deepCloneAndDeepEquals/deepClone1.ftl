<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("originalClazzType")}
${originalClazzType.printType()} result = new ${originalClazzType.printType()}();
map.put(this, new Object[] {result, false});
return this.deepClone(result, map);
