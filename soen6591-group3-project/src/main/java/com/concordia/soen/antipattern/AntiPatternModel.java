package com.concordia.soen.antipattern;

import java.io.FileWriter;
import java.util.List;

import com.concordia.soen.flow.metrics.CatchFlowMetricsModel;

public class AntiPatternModel {

	private String FilePath;
	private String Project;
	private int DestructiveWrapping;
	private int NestedTry;
	private int ThorwsKitchenSink;
	private int ThrowWithinFinally;
	private int CatchandDoNothing;
	private int CatchandReturnnull;
	private int DummyHandler;
	private int CatchGeneric;
	
	public AntiPatternModel(String filePath, String project, int destructiveWrapping, int nestedTry,
			int thorwsKitchenSink, int throwWithinFinally, int catchandDoNothing, int catchandReturnnull, int dummyHandler,
			int catchGeneric) {


		super();
		FilePath = filePath;
		Project = project;
		DestructiveWrapping = destructiveWrapping;
		NestedTry = nestedTry;
		ThorwsKitchenSink = thorwsKitchenSink;
		ThrowWithinFinally = throwWithinFinally;
		CatchandDoNothing= catchandDoNothing;
		CatchandReturnnull =catchandReturnnull;
		DummyHandler = dummyHandler;
		CatchGeneric=catchGeneric;
	}

	public int getCatchGeneric() {
		return CatchGeneric;
	}

	public void setCatchGeneric(int catchGeneric) {
		CatchGeneric = catchGeneric;
	}

	public String getFilePath() {
		return FilePath;
	}

	public void setFilePath(String filePath) {
		FilePath = filePath;
	}

	public String getProject() {
		return Project;
	}

	public void setProject(String project) {
		Project = project;
	}

	public int getCatchandReturnnull() {
		return CatchandReturnnull;
	}

	public void setCatchandReturnnull(int catchandReturnnull) {
		CatchandReturnnull = catchandReturnnull;
	}

	public int getDestructiveWrapping() {
		return DestructiveWrapping;
	}

	public void setDestructiveWrapping(int destructiveWrapping) {
		DestructiveWrapping = destructiveWrapping;
	}

	public int getNestedTry() {
		return NestedTry;
	}

	public void setNestedTry(int nestedTry) {
		NestedTry = nestedTry;
	}

	public int getThorwsKitchenSink() {
		return ThorwsKitchenSink;
	}

	public void setThorwsKitchenSink(int thorwsKitchenSink) {
		ThorwsKitchenSink = thorwsKitchenSink;
	}

	public int getThrowWithinFinally() {
		return ThrowWithinFinally;
	}

	public void setThrowWithinFinally(int throwWithinFinally) {
		ThrowWithinFinally = throwWithinFinally;
	}

	public int getCatchandDoNothing() {
		return CatchandDoNothing;
	}

	public void setCatchandDoNothing(int catchandDoNothing) {
		CatchandDoNothing = catchandDoNothing;
	}
	
	public int getDummyHandler() {
		return DummyHandler;
	}

	public void setDummyHandler(int dummyHandler) {
		DummyHandler = dummyHandler;
	}
	
	public String returnCSV() {
		String CSV = FilePath + "," + Project + "," + DestructiveWrapping + "," + NestedTry + "," + ThorwsKitchenSink + "," 
	+ ThrowWithinFinally + "," + CatchandDoNothing + "," + CatchandReturnnull+ "," + DummyHandler + "," + CatchGeneric;
		return CSV;
	}
	
	public static void GenerateCSVFromList(List<AntiPatternModel> catchFlowMetricsList, String csvName, String column) {
		try {
			FileWriter writer = new FileWriter(csvName);
			writer.write(column + "\n");
			
			for(AntiPatternModel model : catchFlowMetricsList) {
				writer.write(model.returnCSV() + "\n");
			}
			writer.close();
		}catch(Exception e) {
			System.out.println(e);
		}
	}
	
}
