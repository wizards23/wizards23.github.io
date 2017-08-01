package com.ljsw.tjbankpda.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Table {

	private HashMap<String, Column> columns = new HashMap<String, Column>();

	public Column get(String name) {

		return columns.get(name);
	}

	public void add(Column column) {
		columns.put(column.getName(), column);
	}

	public static Table[] doParse(String msg) {
		// 切割表
		String[] tables = msg.split("\r\n");
		Table[] tabs = new Table[tables.length];
		for (int t = 0; t < tables.length; t++) {
			// 切割列
			String[] objs = tables[t].split("\\|");

			Table table = new Table();
			for (int i = 0; i < objs.length; i++) {
				String obj = objs[i];
				Column column = new Column();

				// 切割出列名与值组
				String[] nameAndValues = obj.split(":");
				column.setName(nameAndValues[0]);

				if (nameAndValues.length > 1) {
					// 切割值组
					String[] values = nameAndValues[1].split(",");
					List<String> listValue = new LinkedList<String>();
					for (int j = 0; j < values.length; j++) {
						listValue.add(values[j]);
					}
					column.setValues(listValue);
				}
				table.add(column);
			}
			tabs[t] = table;
		}
		return tabs;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (Column bag : columns.values()) {
			buf.append(bag.getName() + ":");
			int size = bag.values.size();
			List<String> values = bag.values;
			for (int i = 0; i < size; i++) {
				if (i < size - 1)
					buf.append(values.get(i) + ",");
				else
					buf.append(values.get(i));
			}
			buf.append("|");
		}
		return buf.toString();
	}

	public static void main(String[] args) {
		String str = "TCKTNAME:百元面额,二十元面额,十元面额,伍十元面额|NUMS:919400,134940,37320,110000|FLAG:0,0,0,0|\n"
				+ "DTLNAME:存折,银行卡,支票|DTLNUMS:1620,6260,1560|\n"
				+ "NUMID:|CVOUN:30|";
		Table[] tables = doParse(str);
		for (int i = 0; i < tables.length; i++)
			System.out.println(tables[i].toString());
	}

}
