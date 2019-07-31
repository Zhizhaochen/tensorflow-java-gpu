package utils;

import java.nio.IntBuffer;

import org.tensorflow.Tensor;

/**
 * 
 * @author craig
 *
 */
public class Constant {

	public static final int maxSequenceLength = 100;
	public static final Tensor<?> tensorLabelIds = Tensor.create(new long[] { 1 }, IntBuffer.wrap(new int[] { 0 }));
	public static final String labelStr = "'6685607','6685928','1121094','6699899','1113700','1120366','6685892','6682063','5864638','1114393','6685857','6685859','24177676','6685456','6685897','1137272','6685854','6682623','6698431','13439666','13422307','6696333','10641161','6688197','13050499','6698712','6704363','13422305','13422304','6685881','6685884','6703358','5864688','6685601','6685447','1120931','6698387','6672551','5611828','20430148','6685829','6704317','6685593','5864652','6685518','20329551','6685939','5611799','20304124','20302145','1125807','1120318','6685940','6685863','1121921'";
	
}
