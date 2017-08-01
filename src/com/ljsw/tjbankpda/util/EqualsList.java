package com.ljsw.tjbankpda.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
/**
 * 判断2个集合相等的方法
 * @author Administrator
 *
 */
public class EqualsList {
	public static EqualsList elist;
	public EqualsList() {
		if(elist ==null)
		elist = new EqualsList();
	}
	
	public static <T> boolean equals(Collection<T> a, Collection<T> b) {
		if (a == null) {
			return false;
		}
		if (b == null) {
			return false;
		}
		if (a.isEmpty() && b.isEmpty()) {
			return true;
		}
		if (a.size() != b.size()) {
			return false;
		}
		List<T> alist = new ArrayList<T>(a);
		List<T> blist = new ArrayList<T>(b);
		Collections.sort(alist, new Comparator<T>() {
			public int compare(T o1, T o2) {
				return o1.hashCode() - o2.hashCode();
			}

		});

		Collections.sort(blist, new Comparator<T>() {
			public int compare(T o1, T o2) {
				return o1.hashCode() - o2.hashCode();
			}

		});

		return alist.equals(blist);
	}
}
