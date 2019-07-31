package utils;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;

import lombok.Data;

/**
 * 
 * @author craig
 *
 */
@Data
public class BertInputFeatures {

	private int [] inputIds;
	private int [] inputMask;
	private int [] segmentIds;
	
	public BertInputFeatures(int maxSequenceLength, List<String> inputTokenList, SequencialMap<String> vocabMap) {
		
		this.inputIds = new int[maxSequenceLength];
		this.inputMask = new int[maxSequenceLength];
		this.segmentIds = new int[maxSequenceLength];
		
		int inputLen = inputTokenList.size();
		if (inputLen > Constant.maxSequenceLength - 2) {
			inputTokenList = inputTokenList.subList(inputLen - (Constant.maxSequenceLength - 2), inputLen);
//			inputTokenList = inputTokenList.subList(0, Constant.maxSequenceLength - 2);  //TODO 不同的截取方式，可对比尝试看不同效果
		}
		
		List<String> alteredList = Lists.newArrayList("[CLS]");
		alteredList.addAll(inputTokenList);
		alteredList.add("[SEP]");
		for (int i = 0; i < alteredList.size(); i++) {
			inputIds [i] = vocabMap.getIndex(alteredList.get(i));
			if(inputIds [i] == -1) {
				inputIds [i] = vocabMap.getIndex("[UNK]");
			}
			inputMask[i] = 1;
			segmentIds[i] = 0;
		}
		
	}
	
	
	@Override
	public String toString(){
		List<String> to = Lists.newArrayList();
		to.add(Arrays.toString(inputIds));
		to.add(Arrays.toString(inputMask));
		to.add(Arrays.toString(segmentIds));
		return to.toString();
	}
	
	
}
