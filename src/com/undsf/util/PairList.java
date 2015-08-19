package com.undsf.util;

import java.util.ArrayList;
import java.util.List;
import java.util.RandomAccess;

/**
 * Created by Arathi on 2015/4/25.
 */
public class PairList<K,V> extends ArrayList<Pair<K,V>>
        implements List<Pair<K,V>>,RandomAccess, Cloneable, java.io.Serializable {

    public boolean add(K key, V value){
        Pair<K,V> pair = new Pair<K,V>(key, value);
        return add(pair);
    }

}
