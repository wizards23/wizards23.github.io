package com.example.app.entity;

/**
 * webService参数对象
 * 泛型类
 *
 */
public class WebParameter<T> {
	
	private String name;   //参数名称
	private T value;  //参数值
	
	public WebParameter(){}
	
	public WebParameter(String name,T value){
		this.name=name;
		this.value=value;
	}
	
	public String getName() {
		return name;
	}
	public T getValue() {
		return value;
	}
	
}

