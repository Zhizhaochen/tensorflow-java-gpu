package core;

import java.io.IOException;
import java.net.URL;
import java.nio.IntBuffer;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.Session;
import org.tensorflow.Tensor;
import org.tensorflow.framework.ConfigProto;
import org.tensorflow.framework.GPUOptions;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import jodd.util.StringUtil;
import utils.BertInputFeatures;
import utils.Constant;
import utils.SequencialMap;

/**
 * 
 * @author craig
 *
 */
public class Predictor {
	
	private SequencialMap<String> vocabMap;
	private SequencialMap<String> labelMap;
	private SavedModelBundle bundle;
	private Session sess;
	
	/*--------------------------------------------------------------------*/
	public Predictor(String modelDir, String dicFile) {
		ConfigProto configProto = ConfigProto.newBuilder()
                .setAllowSoftPlacement(true) // allow less GPUs than configured
                .setGpuOptions(GPUOptions.newBuilder().setAllowGrowth(true).setPerProcessGpuMemoryFraction(0.01).build())
                .build();
		
		bundle = SavedModelBundle.loader(modelDir).withTags("serve").withConfigProto(configProto.toByteArray()).load();
//		bundle = SavedModelBundle.load(modelDir, "serve");
		sess = bundle.session();
		init(dicFile);
	}
	
	/**
	 * 
	 */
	private void init(String dicFile) {
		try {
			List<String> vocabList = IOUtils.readLines(new URL(dicFile).openStream(), Charsets.UTF_8);
			if(!vocabList.isEmpty()) {
				vocabMap = new SequencialMap<>(vocabList.subList(1, vocabList.size()) , 1);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		List<String> kidList = Lists.newArrayList(Constant.labelStr.split(",")).stream().map(kid -> StringUtil.remove(kid, '\'')).collect(Collectors.toList());
		labelMap = new SequencialMap<>(kidList, 0);
	}

	/**
	 * 
	 * @param inputFeatures
	 * @return
	 */
	public float[] run(BertInputFeatures inputFeatures) {
       
		Tensor<?> tensorInputIds = Tensor.create(
            new long[] {1, Constant.maxSequenceLength}, 
            IntBuffer.wrap(inputFeatures.getInputIds()) 
        );
		
		Tensor<?> tensorInputMask = Tensor.create(
	        new long[] {1, Constant.maxSequenceLength}, 
	        IntBuffer.wrap(inputFeatures.getInputMask()) 
	    );
		
		Tensor<?> tensorSegmentIds = Tensor.create(
	       new long[] {1, Constant.maxSequenceLength}, 
	       IntBuffer.wrap(inputFeatures.getSegmentIds()) 
	    );
		
		long start = System.currentTimeMillis();
        List<Tensor<?>> result = sess.runner()
                .feed("input_ids_1:0", tensorInputIds)
                .feed("input_mask_1:0", tensorInputMask)
                .feed("label_ids_1:0", Constant.tensorLabelIds)
                .feed("segment_ids_1:0", tensorSegmentIds)
                .fetch("loss/Softmax:0")
                .run();
        System.out.println("session runner: " + (System.currentTimeMillis() - start));
       
        float[][] scores = new float[1][labelMap.size()];
        result.get(0).copyTo(scores);
        result.get(0).close();
        tensorInputIds.close();
        tensorInputMask.close();
        tensorSegmentIds.close();
        return scores[0];
	}
	
	/**
	 * 
	 * @param input
	 * @return
	 */
	public Map<String, Float> pred(String input) {
		
		List<String> splitToList = Splitter.on(" ").omitEmptyStrings().trimResults().splitToList(input);
		Map<String, Float> resultMap = Maps.newHashMap();
		BertInputFeatures inputFeatures = new BertInputFeatures(Constant.maxSequenceLength, splitToList, vocabMap);
		float [] softMax = run(inputFeatures);
		for (int i = 0; i < softMax.length; i++) {
			resultMap.put(labelMap.getFromIndex(i), softMax[i]);
		}
		return resultMap;
	}
	
	/*--------------------------------------------------------------------*/
	public static void main(String[] args) {
		
		String modelDir = "/Users/craig/stream_0726/saved_model/1564484189";
		String dicFile = "http://50826.oss-cn-hangzhou-zmf.aliyuncs.com/bert_stream_20190730/bert_stream_vocab_variance.txt";      
		Predictor predictor = new Predictor(modelDir, dicFile);
		
		String in = "orderdetail refund tmall dispute wirelessapplyirelessapply refund tmall dispute wirelessdetail taobao alicare index taobao alicare index refund tmall dispute wirelessapplyrouter refund tmall dispute wirelessapply refund tmall dispute wirelessapplyrouter orderdetail uland taobao coupon edetail uland taobao coupon edetail uland taobao coupon edetail uland taobao coupon edetail uland taobao coupon edetail";  
		
		for (int i = 0; i < 10000; i++) {
			Map<String, Float> pred = predictor.pred(in);
			System.out.println(pred);
		}
		
	}
	
}



