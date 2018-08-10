package net.dertod2.UltimateZones.Utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TwoMap<K1, K2, V> {
	private Map<K1, K2> keyMap = new HashMap<K1, K2>();
	private Map<K2, V> valueMap = new HashMap<K2, V>();
	
	public V getByKey1(K1 k1) {
		K2 k2 = this.keyMap.get(k1);
		return k2 != null ? this.valueMap.get(k2) : null;
	}
	
	public V getByKey2(K2 k2) {
		return this.valueMap.get(k2);
	}
	
	public Collection<V> values() {
		return this.valueMap.values();
	}
	
	public Set<K1> getKeySet1() {
		return this.keyMap.keySet();
	}
	
	public Set<K2> getKeySet2() {
		return this.valueMap.keySet();
	}
	
	public V put(K1 k1, K2 k2, V v) {
		this.keyMap.put(k1, k2);
		return this.valueMap.put(k2, v);
	}
	
	public boolean containsKey1(K1 k1) {
		return this.keyMap.containsKey(k1);
	}
	
	public boolean containsKey2(K2 k2) {
		return this.valueMap.containsKey(k2);
	}
	
	public boolean containsValue(V v) {
		return this.valueMap.containsValue(v);
	}
	
	public V removeByKey1(K1 k1) {
		K2 k2 = this.keyMap.remove(k1);
		
		return k2 != null ? this.valueMap.remove(k2) : null;
	}
	
	public V removeByKey2(K2 k2) {
		for (K1 k1 : this.getKeySet1()) {
			if (this.keyMap.get(k1).equals(k2)) {
				this.keyMap.remove(k1);
				break;
			}
		}
		
		return k2 != null ? this.valueMap.remove(k2) : null;
	}
	
	public K2 getKeyByKey1(K1 k1) {
		return this.keyMap.get(k1);
	}
	
	public K1 getKeyByKey2(K2 k2) {
		for (K1 k1 : this.getKeySet1()) {
			if (this.keyMap.get(k1).equals(k2)) {
				return k1;
			}
		}
		
		return null;
	}
	
	public int size() {
		return this.valueMap.size();
	}
	
	public boolean isEmpty() {
		return this.valueMap.size() != 0;
	}

	public void clear() {
		this.keyMap.clear();
		this.valueMap.clear();
	}
}