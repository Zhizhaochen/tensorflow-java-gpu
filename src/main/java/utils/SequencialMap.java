/**
 * 
 */
package utils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;

import com.google.common.collect.HashBiMap;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 
 * @author craig
 * @since 2018年6月8日 下午5:40:54
 */
@Getter
@NoArgsConstructor
public class SequencialMap<T> {
	
	private int startFrom = 0;
//	private Map<T, Integer> innerMap = Maps.newLinkedHashMap();
	private HashBiMap<T, Integer> innerMap = HashBiMap.create();
	
	/**
	 * 
	 * @param set
	 */
	public SequencialMap (Collection<T> set){
		for (T t : set) {
			put(t);
		}
	}
	
	/**
	 * 
	 * @param set
	 */
	public SequencialMap (Collection<T> set, final int startFrom){
		this.startFrom = startFrom;
		for (T t : set) {
			put(t);
		}
	}
	
	public void put(T t) {
		if(!innerMap.containsKey(t)) {
			innerMap.put(t, startFrom + innerMap.size());
		}
	}
	
	/**
	 * 位置
	 * @param t
	 * @return
	 */
	public Integer getIndex(T t) {
		Integer idx;
		return Objects.nonNull(idx = innerMap.get(t)) ? idx : -1;
	}
	
	/**
	 * 
	 * @param idx
	 * @return
	 */
	public T getFromIndex(int idx){
		return innerMap.inverse().getOrDefault(idx, null);
	}
	
	public Integer size() {
		return innerMap.size();
	}
	
	/**
	 * 
	 * @param outFilePath
	 */
	public void writeOut(String outFilePath) {
		try {
			FileUtils.writeLines(new File(outFilePath), this.getInnerMap().entrySet().stream()
					.map(ent -> ent.getKey() + "\t" + ent.getValue()).collect(Collectors.toList()));
		} catch (IOException e) {
			e.printStackTrace();
		};
	}

	/**
	 * 
	 * @param statusMapOrderWuliu
	 */
	public void putAll(Set<T> statusMapOrderWuliu) {
		for (T t : statusMapOrderWuliu) {
			put(t);
		}
	}
	
}




