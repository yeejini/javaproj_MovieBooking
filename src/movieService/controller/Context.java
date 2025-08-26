package movieService.controller;

import java.util.HashMap;
import java.util.Map;

public class Context<T> {

	private Map<String, T> data = new HashMap<>();

	public Map<String, T> getData() {
		return data;
	}

	public void setData(Map<String, T> data) {
		this.data = data;
	}

}
