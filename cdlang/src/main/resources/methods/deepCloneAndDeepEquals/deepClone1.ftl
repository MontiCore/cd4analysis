<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("originalClazzType")}
${originalClazzType.printType()} result = new ${originalClazzType.printType()}();
return this.deepClone(result, map);
