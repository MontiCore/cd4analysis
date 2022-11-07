/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.strategies;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.cdmerge.BaseTest;
import de.monticore.cdmerge.config.CDMergeConfig;
import de.monticore.cdmerge.config.MergeParameter;
import de.monticore.cdmerge.exceptions.MergingException;
import de.monticore.cdmerge.merging.mergeresult.MergeBlackBoard;
import de.monticore.cdmerge.merging.strategies.DefaultAtributeMerger;
import de.monticore.cdmerge.merging.strategies.DefaultTypeMergeStrategy;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.monticore.umlmodifier._ast.ASTModifierBuilder;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class EnumConstantsMergeTest extends BaseTest {

  private final DefaultTypeMergeStrategy TESTANT;

  private final ASTModifier DefaultModifier = new ASTModifierBuilder().build();

  /**
   * Constructor for de.monticore.umlcd4a.mergetool.merging.strategies.DefaultTypeMergeStrategyTest
   */
  public EnumConstantsMergeTest() {
    CDMergeConfig.Builder b = new CDMergeConfig.Builder(true);
    b.withParam(MergeParameter.NO_INPUT_MODELS)
        .withParam(MergeParameter.SAVE_RESULT_TO_FILE, MergeParameter.OFF)
        .withParam(MergeParameter.CHECK_ONLY)
        .withParam(MergeParameter.LOG_DEBUG, MergeParameter.ON);
    ;

    MergeBlackBoard blackBoard = new MergeBlackBoard(b.build());
    this.TESTANT = new DefaultTypeMergeStrategy(blackBoard, new DefaultAtributeMerger(blackBoard));
  }

  @Test
  public void testMergeEnumsNonStrictOrder() {
    List<ASTCDEnumConstant> ec1 = new ArrayList<>();
    ec1.add(CD4CodeMill.cDEnumConstantBuilder().setName("A").build());
    ec1.add(CD4CodeMill.cDEnumConstantBuilder().setName("B").build());
    ec1.add(CD4CodeMill.cDEnumConstantBuilder().setName("C").build());
    ec1.add(CD4CodeMill.cDEnumConstantBuilder().setName("D").build());
    ec1.add(CD4CodeMill.cDEnumConstantBuilder().setName("L").build());
    ec1.add(CD4CodeMill.cDEnumConstantBuilder().setName("E").build());
    final ASTCDEnum enum1 = CD4CodeMill.cDEnumBuilder()
        .setName("E")
        .setModifier(DefaultModifier)
        .setCDEnumConstantsList(ec1)
        .build();

    List<ASTCDEnumConstant> ec2 = new ArrayList<>();
    ec2.add(CD4CodeMill.cDEnumConstantBuilder().setName("F").build());
    ec2.add(CD4CodeMill.cDEnumConstantBuilder().setName("A").build());
    ec2.add(CD4CodeMill.cDEnumConstantBuilder().setName("G").build());
    ec2.add(CD4CodeMill.cDEnumConstantBuilder().setName("B").build());
    ec2.add(CD4CodeMill.cDEnumConstantBuilder().setName("R").build());
    ec2.add(CD4CodeMill.cDEnumConstantBuilder().setName("E").build());
    ec2.add(CD4CodeMill.cDEnumConstantBuilder().setName("T").build());
    final ASTCDEnum enum2 = CD4CodeMill.cDEnumBuilder()
        .setName("E")
        .setModifier(DefaultModifier)
        .setCDEnumConstantsList(ec2)
        .build();

    final ASTCDEnum mergedEnum = CD4CodeMill.cDEnumBuilder()
        .setName("E")
        .setModifier(DefaultModifier)
        .build();

    final List<ASTCDEnumConstant> EXPECTED = new ArrayList<>();
    EXPECTED.add(CD4CodeMill.cDEnumConstantBuilder().setName("F").build());
    EXPECTED.add(CD4CodeMill.cDEnumConstantBuilder().setName("A").build());
    EXPECTED.add(CD4CodeMill.cDEnumConstantBuilder().setName("G").build());
    EXPECTED.add(CD4CodeMill.cDEnumConstantBuilder().setName("B").build());
    EXPECTED.add(CD4CodeMill.cDEnumConstantBuilder().setName("C").build());
    EXPECTED.add(CD4CodeMill.cDEnumConstantBuilder().setName("D").build());
    EXPECTED.add(CD4CodeMill.cDEnumConstantBuilder().setName("L").build());
    EXPECTED.add(CD4CodeMill.cDEnumConstantBuilder().setName("R").build());
    EXPECTED.add(CD4CodeMill.cDEnumConstantBuilder().setName("E").build());
    EXPECTED.add(CD4CodeMill.cDEnumConstantBuilder().setName("T").build());
    try {
      assertFalse(TESTANT.mergeEnumConstants(enum1, enum2, mergedEnum));
    }
    catch (MergingException unexpected) {
      fail(unexpected.getMessage());
    }
    checkOrder(EXPECTED, mergedEnum.getCDEnumConstantList());
  }

  @Test
  public void testMergeEnumsNonStrictOrderStartingSame() {
    List<ASTCDEnumConstant> ec1 = new ArrayList<>();
    ec1.add(CD4CodeMill.cDEnumConstantBuilder().setName("A").build());
    ec1.add(CD4CodeMill.cDEnumConstantBuilder().setName("B").build());
    ec1.add(CD4CodeMill.cDEnumConstantBuilder().setName("E").build());
    final ASTCDEnum enum1 = CD4CodeMill.cDEnumBuilder()
        .setName("E")
        .setModifier(DefaultModifier)
        .setCDEnumConstantsList(ec1)
        .build();

    List<ASTCDEnumConstant> ec2 = new ArrayList<>();
    ec2.add(CD4CodeMill.cDEnumConstantBuilder().setName("A").build());
    ec2.add(CD4CodeMill.cDEnumConstantBuilder().setName("G").build());
    ec2.add(CD4CodeMill.cDEnumConstantBuilder().setName("B").build());
    ec2.add(CD4CodeMill.cDEnumConstantBuilder().setName("T").build());
    final ASTCDEnum enum2 = CD4CodeMill.cDEnumBuilder()
        .setName("E")
        .setModifier(DefaultModifier)
        .setCDEnumConstantsList(ec2)
        .build();

    final ASTCDEnum mergedEnum = CD4CodeMill.cDEnumBuilder()
        .setName("E")
        .setModifier(DefaultModifier)
        .build();

    final List<ASTCDEnumConstant> EXPECTED = new ArrayList<>();
    EXPECTED.add(CD4CodeMill.cDEnumConstantBuilder().setName("A").build());
    EXPECTED.add(CD4CodeMill.cDEnumConstantBuilder().setName("G").build());
    EXPECTED.add(CD4CodeMill.cDEnumConstantBuilder().setName("B").build());
    EXPECTED.add(CD4CodeMill.cDEnumConstantBuilder().setName("E").build());
    EXPECTED.add(CD4CodeMill.cDEnumConstantBuilder().setName("T").build());
    try {
      assertFalse(TESTANT.mergeEnumConstants(enum1, enum2, mergedEnum));
    }
    catch (MergingException unexpected) {
      fail(unexpected.getMessage());
    }
    checkOrder(EXPECTED, mergedEnum.getCDEnumConstantList());
  }

  @Test
  public void testMergeEnumsNonStrictOrderEndingSame() {

    List<ASTCDEnumConstant> ec1 = new ArrayList<>();
    ec1.add(CD4CodeMill.cDEnumConstantBuilder().setName("H").build());
    ec1.add(CD4CodeMill.cDEnumConstantBuilder().setName("A").build());
    ec1.add(CD4CodeMill.cDEnumConstantBuilder().setName("B").build());
    ec1.add(CD4CodeMill.cDEnumConstantBuilder().setName("C").build());
    ec1.add(CD4CodeMill.cDEnumConstantBuilder().setName("D").build());
    ec1.add(CD4CodeMill.cDEnumConstantBuilder().setName("L").build());
    ec1.add(CD4CodeMill.cDEnumConstantBuilder().setName("E").build());
    final ASTCDEnum enum1 = CD4CodeMill.cDEnumBuilder()
        .setName("E")
        .setModifier(DefaultModifier)
        .setCDEnumConstantsList(ec1)
        .build();

    List<ASTCDEnumConstant> ec2 = new ArrayList<>();
    ec2.add(CD4CodeMill.cDEnumConstantBuilder().setName("F").build());
    ec2.add(CD4CodeMill.cDEnumConstantBuilder().setName("A").build());
    ec2.add(CD4CodeMill.cDEnumConstantBuilder().setName("G").build());
    ec2.add(CD4CodeMill.cDEnumConstantBuilder().setName("B").build());
    ec2.add(CD4CodeMill.cDEnumConstantBuilder().setName("R").build());
    ec2.add(CD4CodeMill.cDEnumConstantBuilder().setName("E").build());
    final ASTCDEnum enum2 = CD4CodeMill.cDEnumBuilder()
        .setName("E")
        .setModifier(DefaultModifier)
        .setCDEnumConstantsList(ec2)
        .build();

    final ASTCDEnum mergedEnum = CD4CodeMill.cDEnumBuilder()
        .setName("E")
        .setModifier(DefaultModifier)
        .build();

    final List<ASTCDEnumConstant> EXPECTED = new ArrayList<>();
    EXPECTED.add(CD4CodeMill.cDEnumConstantBuilder().setName("H").build());
    EXPECTED.add(CD4CodeMill.cDEnumConstantBuilder().setName("F").build());
    EXPECTED.add(CD4CodeMill.cDEnumConstantBuilder().setName("A").build());
    EXPECTED.add(CD4CodeMill.cDEnumConstantBuilder().setName("G").build());
    EXPECTED.add(CD4CodeMill.cDEnumConstantBuilder().setName("B").build());
    EXPECTED.add(CD4CodeMill.cDEnumConstantBuilder().setName("C").build());
    EXPECTED.add(CD4CodeMill.cDEnumConstantBuilder().setName("D").build());
    EXPECTED.add(CD4CodeMill.cDEnumConstantBuilder().setName("L").build());
    EXPECTED.add(CD4CodeMill.cDEnumConstantBuilder().setName("R").build());
    EXPECTED.add(CD4CodeMill.cDEnumConstantBuilder().setName("E").build());

    try {
      assertFalse(TESTANT.mergeEnumConstants(enum1, enum2, mergedEnum));
    }
    catch (MergingException unexpected) {
      fail(unexpected.getMessage());
    }
    checkOrder(EXPECTED, mergedEnum.getCDEnumConstantList());

  }

  @Test
  public void testMergeEnumsNonStrictOrder2() {

    List<ASTCDEnumConstant> ec1 = new ArrayList<>();
    ec1.add(CD4CodeMill.cDEnumConstantBuilder().setName("A").build());
    ec1.add(CD4CodeMill.cDEnumConstantBuilder().setName("B").build());
    ec1.add(CD4CodeMill.cDEnumConstantBuilder().setName("C").build());
    ec1.add(CD4CodeMill.cDEnumConstantBuilder().setName("D").build());

    final ASTCDEnum enum1 = CD4CodeMill.cDEnumBuilder()
        .setName("E")
        .setModifier(DefaultModifier)
        .setCDEnumConstantsList(ec1)
        .build();

    List<ASTCDEnumConstant> ec2 = new ArrayList<>();
    ec2.add(CD4CodeMill.cDEnumConstantBuilder().setName("A").build());
    ec2.add(CD4CodeMill.cDEnumConstantBuilder().setName("G").build());
    ec2.add(CD4CodeMill.cDEnumConstantBuilder().setName("H").build());

    final ASTCDEnum enum2 = CD4CodeMill.cDEnumBuilder()
        .setName("E")
        .setModifier(DefaultModifier)
        .setCDEnumConstantsList(ec2)
        .build();

    final ASTCDEnum mergedEnum = CD4CodeMill.cDEnumBuilder()
        .setName("E")
        .setModifier(DefaultModifier)
        .build();

    final List<ASTCDEnumConstant> EXPECTED = new ArrayList<>();
    EXPECTED.add(CD4CodeMill.cDEnumConstantBuilder().setName("A").build());
    EXPECTED.add(CD4CodeMill.cDEnumConstantBuilder().setName("B").build());
    EXPECTED.add(CD4CodeMill.cDEnumConstantBuilder().setName("C").build());
    EXPECTED.add(CD4CodeMill.cDEnumConstantBuilder().setName("D").build());
    EXPECTED.add(CD4CodeMill.cDEnumConstantBuilder().setName("G").build());
    EXPECTED.add(CD4CodeMill.cDEnumConstantBuilder().setName("H").build());

    try {
      assertFalse(TESTANT.mergeEnumConstants(enum1, enum2, mergedEnum));
    }
    catch (MergingException unexpected) {
      fail(unexpected.getMessage());
    }
    checkOrder(EXPECTED, mergedEnum.getCDEnumConstantList());

  }

  @Test
  public void testMergeEnumsSubset1() {
    List<ASTCDEnumConstant> ec1 = new ArrayList<>();
    ec1.add(CD4CodeMill.cDEnumConstantBuilder().setName("A").build());
    ec1.add(CD4CodeMill.cDEnumConstantBuilder().setName("C").build());
    ec1.add(CD4CodeMill.cDEnumConstantBuilder().setName("E").build());
    final ASTCDEnum enum1 = CD4CodeMill.cDEnumBuilder()
        .setName("E")
        .setModifier(DefaultModifier)
        .setCDEnumConstantsList(ec1)
        .build();

    List<ASTCDEnumConstant> ec2 = new ArrayList<>();
    ec2.add(CD4CodeMill.cDEnumConstantBuilder().setName("A").build());
    ec2.add(CD4CodeMill.cDEnumConstantBuilder().setName("B").build());
    ec2.add(CD4CodeMill.cDEnumConstantBuilder().setName("X").build());
    ec2.add(CD4CodeMill.cDEnumConstantBuilder().setName("C").build());
    ec2.add(CD4CodeMill.cDEnumConstantBuilder().setName("D").build());
    ec2.add(CD4CodeMill.cDEnumConstantBuilder().setName("Y").build());
    ec2.add(CD4CodeMill.cDEnumConstantBuilder().setName("E").build());
    final ASTCDEnum enum2 = CD4CodeMill.cDEnumBuilder()
        .setName("E")
        .setModifier(DefaultModifier)
        .setCDEnumConstantsList(ec2)
        .build();

    final ASTCDEnum mergedEnum = CD4CodeMill.cDEnumBuilder()
        .setModifier(DefaultModifier)
        .setName("E")
        .build();

    final List<ASTCDEnumConstant> EXPECTED = new ArrayList<>();
    EXPECTED.add(CD4CodeMill.cDEnumConstantBuilder().setName("A").build());
    EXPECTED.add(CD4CodeMill.cDEnumConstantBuilder().setName("B").build());
    EXPECTED.add(CD4CodeMill.cDEnumConstantBuilder().setName("X").build());
    EXPECTED.add(CD4CodeMill.cDEnumConstantBuilder().setName("C").build());
    EXPECTED.add(CD4CodeMill.cDEnumConstantBuilder().setName("D").build());
    EXPECTED.add(CD4CodeMill.cDEnumConstantBuilder().setName("Y").build());
    EXPECTED.add(CD4CodeMill.cDEnumConstantBuilder().setName("E").build());

    try {
      assertTrue(TESTANT.mergeEnumConstants(enum1, enum2, mergedEnum));
    }
    catch (MergingException unexpected) {
      fail(unexpected.getMessage());
    }
    checkOrder(EXPECTED, mergedEnum.getCDEnumConstantList());

  }

  @Test
  public void testMergeEnumsSubset2() {
    List<ASTCDEnumConstant> ec1 = new ArrayList<>();
    ec1.add(CD4CodeMill.cDEnumConstantBuilder().setName("A").build());
    ec1.add(CD4CodeMill.cDEnumConstantBuilder().setName("B").build());
    ec1.add(CD4CodeMill.cDEnumConstantBuilder().setName("X").build());
    ec1.add(CD4CodeMill.cDEnumConstantBuilder().setName("C").build());
    ec1.add(CD4CodeMill.cDEnumConstantBuilder().setName("D").build());
    ec1.add(CD4CodeMill.cDEnumConstantBuilder().setName("Y").build());
    ec1.add(CD4CodeMill.cDEnumConstantBuilder().setName("E").build());
    final ASTCDEnum enum1 = CD4CodeMill.cDEnumBuilder()
        .setName("E")
        .setModifier(DefaultModifier)
        .setCDEnumConstantsList(ec1)
        .build();

    List<ASTCDEnumConstant> ec2 = new ArrayList<>();
    ec2.add(CD4CodeMill.cDEnumConstantBuilder().setName("A").build());
    ec2.add(CD4CodeMill.cDEnumConstantBuilder().setName("C").build());
    ec2.add(CD4CodeMill.cDEnumConstantBuilder().setName("E").build());
    final ASTCDEnum enum2 = CD4CodeMill.cDEnumBuilder()
        .setName("E")
        .setModifier(DefaultModifier)
        .setCDEnumConstantsList(ec2)
        .build();

    final ASTCDEnum mergedEnum = CD4CodeMill.cDEnumBuilder()
        .setModifier(DefaultModifier)
        .setName("E")
        .build();

    final List<ASTCDEnumConstant> EXPECTED = new ArrayList<>();
    EXPECTED.add(CD4CodeMill.cDEnumConstantBuilder().setName("A").build());
    EXPECTED.add(CD4CodeMill.cDEnumConstantBuilder().setName("B").build());
    EXPECTED.add(CD4CodeMill.cDEnumConstantBuilder().setName("X").build());
    EXPECTED.add(CD4CodeMill.cDEnumConstantBuilder().setName("C").build());
    EXPECTED.add(CD4CodeMill.cDEnumConstantBuilder().setName("D").build());
    EXPECTED.add(CD4CodeMill.cDEnumConstantBuilder().setName("Y").build());
    EXPECTED.add(CD4CodeMill.cDEnumConstantBuilder().setName("E").build());
    try {
      assertTrue(TESTANT.mergeEnumConstants(enum1, enum2, mergedEnum));
    }
    catch (MergingException unexpected) {
      fail(unexpected.getMessage());
    }
    checkOrder(EXPECTED, mergedEnum.getCDEnumConstantList());

  }

  @Test
  public void testMergeEnumsEmptyEnum2() {
    List<ASTCDEnumConstant> ec1 = new ArrayList<>();
    ec1.add(CD4CodeMill.cDEnumConstantBuilder().setName("A").build());
    ec1.add(CD4CodeMill.cDEnumConstantBuilder().setName("C").build());
    ec1.add(CD4CodeMill.cDEnumConstantBuilder().setName("E").build());
    final ASTCDEnum enum1 = CD4CodeMill.cDEnumBuilder()
        .setName("E")
        .setModifier(DefaultModifier)
        .setCDEnumConstantsList(ec1)
        .build();

    List<ASTCDEnumConstant> ec2 = new ArrayList<>();
    final ASTCDEnum enum2 = CD4CodeMill.cDEnumBuilder()
        .setName("E")
        .setModifier(DefaultModifier)
        .setCDEnumConstantsList(ec2)
        .build();

    final ASTCDEnum mergedEnum = CD4CodeMill.cDEnumBuilder()
        .setName("E")
        .setModifier(DefaultModifier)
        .build();

    final List<ASTCDEnumConstant> EXPECTED = new ArrayList<>();
    EXPECTED.add(CD4CodeMill.cDEnumConstantBuilder().setName("A").build());
    EXPECTED.add(CD4CodeMill.cDEnumConstantBuilder().setName("C").build());
    EXPECTED.add(CD4CodeMill.cDEnumConstantBuilder().setName("E").build());
    try {
      assertTrue(TESTANT.mergeEnumConstants(enum1, enum2, mergedEnum));
    }
    catch (MergingException unexpected) {
      fail(unexpected.getMessage());
    }

    checkOrder(EXPECTED, mergedEnum.getCDEnumConstantList());

  }

  @Test
  public void testMergeEnumsBothEmpty() {
    List<ASTCDEnumConstant> ec1 = new ArrayList<>();

    final ASTCDEnum enum1 = CD4CodeMill.cDEnumBuilder()
        .setName("E")
        .setModifier(DefaultModifier)
        .setCDEnumConstantsList(ec1)
        .build();

    List<ASTCDEnumConstant> ec2 = new ArrayList<>();
    final ASTCDEnum enum2 = CD4CodeMill.cDEnumBuilder()
        .setName("E")
        .setModifier(DefaultModifier)
        .setCDEnumConstantsList(ec2)
        .build();

    final ASTCDEnum mergedEnum = CD4CodeMill.cDEnumBuilder()
        .setName("E")
        .setModifier(DefaultModifier)
        .build();

    final List<ASTCDEnumConstant> EXPECTED = new ArrayList<>();

    try {
      assertTrue(TESTANT.mergeEnumConstants(enum1, enum2, mergedEnum));
    }
    catch (MergingException unexpected) {
      fail(unexpected.getMessage());
    }

    checkOrder(EXPECTED, mergedEnum.getCDEnumConstantList());

  }

  @Test
  public void testMergeEnumsEmptyEnum1() {
    List<ASTCDEnumConstant> ec1 = new ArrayList<>();
    final ASTCDEnum enum1 = CD4CodeMill.cDEnumBuilder()
        .setName("E")
        .setModifier(DefaultModifier)
        .setCDEnumConstantsList(ec1)
        .build();

    List<ASTCDEnumConstant> ec2 = new ArrayList<>();
    ec2.add(CD4CodeMill.cDEnumConstantBuilder().setName("A").build());
    ec2.add(CD4CodeMill.cDEnumConstantBuilder().setName("C").build());
    ec2.add(CD4CodeMill.cDEnumConstantBuilder().setName("E").build());
    final ASTCDEnum enum2 = CD4CodeMill.cDEnumBuilder()
        .setName("E")
        .setModifier(DefaultModifier)
        .setCDEnumConstantsList(ec2)
        .build();

    final ASTCDEnum mergedEnum = CD4CodeMill.cDEnumBuilder()
        .setName("E")
        .setModifier(DefaultModifier)
        .build();

    final List<ASTCDEnumConstant> EXPECTED = new ArrayList<>();
    EXPECTED.add(CD4CodeMill.cDEnumConstantBuilder().setName("A").build());
    EXPECTED.add(CD4CodeMill.cDEnumConstantBuilder().setName("C").build());
    EXPECTED.add(CD4CodeMill.cDEnumConstantBuilder().setName("E").build());
    try {
      assertTrue(TESTANT.mergeEnumConstants(enum1, enum2, mergedEnum));
    }
    catch (MergingException unexpected) {
      fail(unexpected.getMessage());
    }
    checkOrder(EXPECTED, mergedEnum.getCDEnumConstantList());

  }

  @Test
  public void testMergeEnumsDisjoint() {
    List<ASTCDEnumConstant> ec1 = new ArrayList<>();
    ec1.add(CD4CodeMill.cDEnumConstantBuilder().setName("A").build());
    ec1.add(CD4CodeMill.cDEnumConstantBuilder().setName("C").build());
    ec1.add(CD4CodeMill.cDEnumConstantBuilder().setName("D").build());
    final ASTCDEnum enum1 = CD4CodeMill.cDEnumBuilder()
        .setName("E")
        .setModifier(DefaultModifier)
        .setCDEnumConstantsList(ec1)
        .build();

    List<ASTCDEnumConstant> ec2 = new ArrayList<>();
    ec2.add(CD4CodeMill.cDEnumConstantBuilder().setName("E").build());
    ec2.add(CD4CodeMill.cDEnumConstantBuilder().setName("F").build());
    ec2.add(CD4CodeMill.cDEnumConstantBuilder().setName("G").build());
    final ASTCDEnum enum2 = CD4CodeMill.cDEnumBuilder()
        .setName("E")
        .setModifier(DefaultModifier)
        .setCDEnumConstantsList(ec2)
        .build();

    final ASTCDEnum mergedEnum = CD4CodeMill.cDEnumBuilder()
        .setName("E")
        .setModifier(DefaultModifier)
        .build();

    final List<ASTCDEnumConstant> EXPECTED = new ArrayList<>();
    EXPECTED.add(CD4CodeMill.cDEnumConstantBuilder().setName("A").build());
    EXPECTED.add(CD4CodeMill.cDEnumConstantBuilder().setName("C").build());
    EXPECTED.add(CD4CodeMill.cDEnumConstantBuilder().setName("D").build());
    EXPECTED.add(CD4CodeMill.cDEnumConstantBuilder().setName("E").build());
    EXPECTED.add(CD4CodeMill.cDEnumConstantBuilder().setName("F").build());
    EXPECTED.add(CD4CodeMill.cDEnumConstantBuilder().setName("G").build());
    try {
      assertFalse(TESTANT.mergeEnumConstants(enum1, enum2, mergedEnum));
    }
    catch (MergingException unexpected) {
      fail(unexpected.getMessage());
    }

    checkOrder(EXPECTED, mergedEnum.getCDEnumConstantList());

  }

  @Test
  public void testMergeEnumsSingleMatch() {
    List<ASTCDEnumConstant> ec1 = new ArrayList<>();
    ec1.add(CD4CodeMill.cDEnumConstantBuilder().setName("A").build());
    final ASTCDEnum enum1 = CD4CodeMill.cDEnumBuilder()
        .setName("E")
        .setModifier(DefaultModifier)
        .setCDEnumConstantsList(ec1)
        .build();

    List<ASTCDEnumConstant> ec2 = new ArrayList<>();
    ec2.add(CD4CodeMill.cDEnumConstantBuilder().setName("A").build());
    final ASTCDEnum enum2 = CD4CodeMill.cDEnumBuilder()
        .setName("E")
        .setModifier(DefaultModifier)
        .setCDEnumConstantsList(ec2)
        .build();

    final ASTCDEnum mergedEnum = CD4CodeMill.cDEnumBuilder()
        .setName("E")
        .setModifier(DefaultModifier)
        .build();

    final List<ASTCDEnumConstant> EXPECTED = new ArrayList<>();
    EXPECTED.add(CD4CodeMill.cDEnumConstantBuilder().setName("A").build());
    try {
      assertTrue(TESTANT.mergeEnumConstants(enum1, enum2, mergedEnum));
    }
    catch (MergingException unexpected) {
      fail(unexpected.getMessage());
    }
    checkOrder(EXPECTED, mergedEnum.getCDEnumConstantList());

  }

  @Test
  public void testMergeEnumsIdempotent() {
    List<ASTCDEnumConstant> ec1 = new ArrayList<>();
    ec1.add(CD4CodeMill.cDEnumConstantBuilder().setName("A").build());
    ec1.add(CD4CodeMill.cDEnumConstantBuilder().setName("C").build());
    ec1.add(CD4CodeMill.cDEnumConstantBuilder().setName("E").build());
    final ASTCDEnum enum1 = CD4CodeMill.cDEnumBuilder()
        .setName("E")
        .setModifier(DefaultModifier)
        .setCDEnumConstantsList(ec1)
        .build();

    List<ASTCDEnumConstant> ec2 = new ArrayList<>();
    ec2.add(CD4CodeMill.cDEnumConstantBuilder().setName("A").build());
    ec2.add(CD4CodeMill.cDEnumConstantBuilder().setName("C").build());
    ec2.add(CD4CodeMill.cDEnumConstantBuilder().setName("E").build());
    final ASTCDEnum enum2 = CD4CodeMill.cDEnumBuilder()
        .setName("E")
        .setModifier(DefaultModifier)
        .setCDEnumConstantsList(ec2)
        .build();

    final ASTCDEnum mergedEnum = CD4CodeMill.cDEnumBuilder()
        .setName("E")
        .setModifier(DefaultModifier)
        .build();

    final List<ASTCDEnumConstant> EXPECTED = new ArrayList<>();
    EXPECTED.add(CD4CodeMill.cDEnumConstantBuilder().setName("A").build());
    EXPECTED.add(CD4CodeMill.cDEnumConstantBuilder().setName("C").build());
    EXPECTED.add(CD4CodeMill.cDEnumConstantBuilder().setName("E").build());
    try {
      assertTrue(TESTANT.mergeEnumConstants(enum1, enum2, mergedEnum));
    }
    catch (MergingException unexpected) {
      fail(unexpected.getMessage());
    }
    checkOrder(EXPECTED, mergedEnum.getCDEnumConstantList());

  }

  @Test
  public void testMergeEnumsConflictOrder() {
    List<ASTCDEnumConstant> ec1 = new ArrayList<>();
    ec1.add(CD4CodeMill.cDEnumConstantBuilder().setName("A").build());
    ec1.add(CD4CodeMill.cDEnumConstantBuilder().setName("B").build());

    final ASTCDEnum enum1 = CD4CodeMill.cDEnumBuilder()
        .setName("E")
        .setModifier(DefaultModifier)
        .setCDEnumConstantsList(ec1)
        .build();

    List<ASTCDEnumConstant> ec2 = new ArrayList<>();
    ec2.add(CD4CodeMill.cDEnumConstantBuilder().setName("B").build());
    ec2.add(CD4CodeMill.cDEnumConstantBuilder().setName("A").build());
    final ASTCDEnum enum2 = CD4CodeMill.cDEnumBuilder()
        .setName("E")
        .setModifier(DefaultModifier)
        .setCDEnumConstantsList(ec2)
        .build();

    final ASTCDEnum mergedEnum = CD4CodeMill.cDEnumBuilder()
        .setName("E")
        .setModifier(DefaultModifier)
        .build();

    try {
      TESTANT.mergeEnumConstants(enum1, enum2, mergedEnum);
      fail("Conflicting order or Enum Constants should cause exception");
    }
    catch (MergingException expected) {
      // EXPECTED
    }

  }

  @Test
  public void testMergeEnumsConflictOrder2() {
    List<ASTCDEnumConstant> ec1 = new ArrayList<>();
    ec1.add(CD4CodeMill.cDEnumConstantBuilder().setName("A").build());
    ec1.add(CD4CodeMill.cDEnumConstantBuilder().setName("B").build());
    ec1.add(CD4CodeMill.cDEnumConstantBuilder().setName("C").build());

    final ASTCDEnum enum1 = CD4CodeMill.cDEnumBuilder()
        .setName("E")
        .setModifier(DefaultModifier)
        .setCDEnumConstantsList(ec1)
        .build();

    List<ASTCDEnumConstant> ec2 = new ArrayList<>();
    ec2.add(CD4CodeMill.cDEnumConstantBuilder().setName("B").build());
    ec2.add(CD4CodeMill.cDEnumConstantBuilder().setName("A").build());
    final ASTCDEnum enum2 = CD4CodeMill.cDEnumBuilder()
        .setName("E")
        .setModifier(DefaultModifier)
        .setCDEnumConstantsList(ec2)
        .build();

    final ASTCDEnum mergedEnum = CD4CodeMill.cDEnumBuilder()
        .setName("E")
        .setModifier(DefaultModifier)
        .build();
    try {
      TESTANT.mergeEnumConstants(enum1, enum2, mergedEnum);
      fail("Conflicting order or Enum Constants should cause exception");
    }
    catch (MergingException expected) {
      // EXPECTED
    }

  }

  @Test
  public void testMergeEnumsConflictOrder3() {
    List<ASTCDEnumConstant> ec1 = new ArrayList<>();
    ec1.add(CD4CodeMill.cDEnumConstantBuilder().setName("A").build());
    ec1.add(CD4CodeMill.cDEnumConstantBuilder().setName("F").build());
    ec1.add(CD4CodeMill.cDEnumConstantBuilder().setName("B").build());
    ec1.add(CD4CodeMill.cDEnumConstantBuilder().setName("C").build());

    final ASTCDEnum enum1 = CD4CodeMill.cDEnumBuilder()
        .setName("E")
        .setModifier(DefaultModifier)
        .setCDEnumConstantsList(ec1)
        .build();

    List<ASTCDEnumConstant> ec2 = new ArrayList<>();
    ec2.add(CD4CodeMill.cDEnumConstantBuilder().setName("B").build());
    ec2.add(CD4CodeMill.cDEnumConstantBuilder().setName("C").build());
    ec2.add(CD4CodeMill.cDEnumConstantBuilder().setName("A").build());
    final ASTCDEnum enum2 = CD4CodeMill.cDEnumBuilder()
        .setName("E")
        .setModifier(DefaultModifier)
        .setCDEnumConstantsList(ec2)
        .build();

    final ASTCDEnum mergedEnum = CD4CodeMill.cDEnumBuilder()
        .setName("E")
        .setModifier(DefaultModifier)
        .build();
    try {
      TESTANT.mergeEnumConstants(enum1, enum2, mergedEnum);
      fail("Conflicting order or Enum Constants should cause exception");
    }
    catch (MergingException expected) {
      // EXPECTED
    }

  }

  @Test
  public void testMergeEnumsSingleNoMatch() {
    List<ASTCDEnumConstant> ec1 = new ArrayList<>();
    ec1.add(CD4CodeMill.cDEnumConstantBuilder().setName("A").build());
    final ASTCDEnum enum1 = CD4CodeMill.cDEnumBuilder()
        .setName("E")
        .setModifier(DefaultModifier)
        .setCDEnumConstantsList(ec1)
        .build();

    List<ASTCDEnumConstant> ec2 = new ArrayList<>();
    ec2.add(CD4CodeMill.cDEnumConstantBuilder().setName("B").build());
    final ASTCDEnum enum2 = CD4CodeMill.cDEnumBuilder()
        .setName("E")
        .setModifier(DefaultModifier)
        .setCDEnumConstantsList(ec2)
        .build();

    final ASTCDEnum mergedEnum = CD4CodeMill.cDEnumBuilder()
        .setName("E")
        .setModifier(DefaultModifier)
        .build();

    final List<ASTCDEnumConstant> EXPECTED = new ArrayList<>();
    EXPECTED.add(CD4CodeMill.cDEnumConstantBuilder().setName("A").build());
    EXPECTED.add(CD4CodeMill.cDEnumConstantBuilder().setName("B").build());
    try {
      assertFalse(TESTANT.mergeEnumConstants(enum1, enum2, mergedEnum));
    }
    catch (MergingException unexpected) {
      fail(unexpected.getMessage());
    }
    checkOrder(EXPECTED, mergedEnum.getCDEnumConstantList());

  }

  // @Test
  public void testMergeEnumsConflictParameter() {

  }

  private void checkOrder(List<ASTCDEnumConstant> expected, List<ASTCDEnumConstant> testresult) {
    assertTrue(expected.size() == testresult.size());
    for (int i = 0; i < testresult.size(); i++) {
      assertTrue(expected.get(i).getName().equalsIgnoreCase(testresult.get(i).getName()));
    }
  }

}
