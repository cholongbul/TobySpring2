package springbook.user.serviceAbstract1.dao;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

//sum과 multyple에서 겹치는 부분 분리해내기
public class Calculator {

	public <T> T lineReadTemplate(String filepath, LineCallback<T> callback, T initVal) throws IOException {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(filepath));
			T res = initVal;
			String line = null;
			while ((line = br.readLine()) != null) {// 파일의 각 라인을 루
				res = callback.doSomethingWithLine(line, res);
			}
			return res;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			throw e;
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					System.out.println(e.getMessage());
				}
			}
		}
	}



	public Integer calcSum(String filepath) throws IOException {// 템플릿/콜백 적용
		LineCallback<Integer> sumCallback = new LineCallback<Integer>() {


			@Override
			public Integer doSomethingWithLine(String line,
					Integer value) {
				return value + Integer.valueOf(line);

			}

		};
		return lineReadTemplate(filepath, sumCallback, 0);

	}

	public Integer calcMultiply(String filepath) throws IOException {
		LineCallback<Integer> multiCallback = new LineCallback<Integer>() {

			@Override
			public Integer doSomethingWithLine(String line, Integer value) {
				return value * Integer.valueOf(line);
			}
		};
		return lineReadTemplate(filepath, multiCallback, 1);
	}
	
	public String concatenate(String filepath) throws IOException{
		LineCallback<String> concatenateCallback =
				new LineCallback<String>() {
					
					@Override
					public String doSomethingWithLine(String line, String value) {
						return value + line;
					}};
					return lineReadTemplate(filepath, concatenateCallback, "");
	}
}
