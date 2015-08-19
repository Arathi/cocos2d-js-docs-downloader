package com.undsf.util;

/**
 * Created by Arathi on 2015/4/25.
 * 类似C++标准库中的pair<K,V>
 */
public class Pair<K,V> {
    public K first;
    public V second;

    public Pair(K key, V value){
        this.first = key;
        this.second = value;
    }

    public K getKey(){
        return first;
    }

    public V getValue(){
        return second;
    }

    public K getFirst(){
        return first;
    }

    public V getSecond(){
        return second;
    }
}
