package org.interview.meta;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 键值对
 * @author shersfy
 * @date 2018-02-05
 *
 * @copyright Copyright shersfy 2018 All Rights Reserved.
 * @param <K>
 * @param <V>
 */
public class MapData<K, V> extends Data {

	private Map<K, V> hashMap = new HashMap<K, V>();

	public V put(K key, V value) {
		return hashMap.put(key, value);
	}

	public void putAll(Map<? extends K, ? extends V> m) {
		hashMap.putAll(m);
	}

	public V get(K key) {
		return hashMap.get(key);
	}
	
	public Set<K> keySet() {
		return hashMap.keySet();
	}
	
}
