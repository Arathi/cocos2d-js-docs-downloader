package com.undsf.util;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 扩展随机数类
 * @author Arathi
 *
 */
public class Random extends java.util.Random {
	
	/**
	 * 获取介于min和max之间的随机整数
	 * @param min
	 * @param max
	 * @return
	 */
	public int nextInt(int min, int max) {
		if (min==max || min==max-1) return min;
		if (min>max){
			int t = min;
			min = max;
			max = t;
		}
		int delta = max - min;
		int randomInt = min + nextInt(delta);
		return randomInt;
	}
	
	/**
	 * 获取介于min和max之间的随机浮点数
	 * @param min
	 * @param max
	 * @return
	 */
	public double nextDouble(double min, double max) {
		if (min==max) return min;
		if (min>max){
			double t = min;
			min = max;
			max = t;
		}
		double delta = max - min;
		double randomDouble = min + delta*nextDouble();
		return randomDouble;
	}

	/**
	 * 获取Set中的任意元素
	 */
	public <E> E nextElement(Set<E> set){
		if (set.isEmpty()) return null;
		int randomIndex = nextInt(set.size());
		int index = 0;
		for (E e : set){
			if (index++ == randomIndex){
				return e;
			}
		}
		return null;
	}

	/**
	 * 获取Map中的任意Entry
	 */
	public <K,V> Map.Entry<K,V> nextElement(Map<K,V> map){
		if ( map.isEmpty() ) return null;
		int randomIndex = nextInt(map.size());
		int index = 0;
		for (Map.Entry<K,V> e : map.entrySet()){
			if (index++ == randomIndex){
				return e;
			}
		}
		return null;
	}

	/**
	 * 获取List中的任意元素
	 */
	public <E> E nextElement(List<E> list){
		if (list.isEmpty()) return null;
		int index = nextInt(list.size());
		return list.get(index);
	}

}
