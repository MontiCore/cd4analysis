/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdbasis.cocos.ebnf;

import de.monticore.cd.cocos.CoCoHelper;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._cocos.CDBasisASTCDClassCoCo;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.se_rwth.commons.logging.Log;

/**
 * Ensures that an attribute name does not occur twice in a class.
 */
public class CDAttributeUniqueInClass implements CDBasisASTCDClassCoCo {
	@Override
	public void check(ASTCDClass node) {
		CoCoHelper.findDuplicatesBy(node.getSymbol().getFieldList(), VariableSymbol::getName)
				.forEach(e -> Log.error(String.format("0xCDC06: Attribute %s is defined multiple times in class %s.",
						e.getName(), node.getName()), node.get_SourcePositionStart()));
	}

}
