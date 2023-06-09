package com.concordia.soen.flow.metrics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.cassandra.cli.CliParser.newColumnFamily_return;

import com.concordia.soen.antipattern.AntiPatternModel;
import com.concordia.soen.antipattern.CatchGenericDetector;
import com.concordia.soen.antipattern.CatchandDoNothingDetector;
import com.concordia.soen.antipattern.CatchandReturnnullDetector;
import com.concordia.soen.antipattern.DestructiveWrappingDetector;
import com.concordia.soen.antipattern.DummyHandlerDetector;
import com.concordia.soen.antipattern.IgnoringInterruptedExceptionDetector;
import com.concordia.soen.antipattern.LogandReturnNullDetector;
import com.concordia.soen.antipattern.LogandThrowDetector;
import com.concordia.soen.antipattern.NestedTryDetector;
import com.concordia.soen.antipattern.ThrowWithinFinallyDetector;
import com.concordia.soen.antipattern.ThrowsGenericDetector;
import com.concordia.soen.antipattern.ThrowsKitchenSinkDetector;

public class FlowMetricsApp {
	public static void main(String[] args) {
		
		List<CatchFlowMetricsModel> catchFlowMetricsList = new ArrayList<CatchFlowMetricsModel>();
		List<TryFlowMetricsModel> tryFlowMetricsList = new ArrayList<TryFlowMetricsModel>();
		List<AntiPatternModel> antiPatternList = new ArrayList<AntiPatternModel>();
		
		String path = "/Users/asiftanim/Downloads/hadoop-release-2.6.0";
//		String path = "/Users/phong/Downloads/hibernate-orm-5.0.0.Final";
		String projectName = "hadoop-2.6";
		
		for(int i=0; i<2; i++) {
			if(i == 1) {
				path = "/Users/asiftanim/Downloads/hibernate-orm-5.0.0.Final";
//				path = "/Users/phong/Downloads/hadoop-release-2.6.0";
				projectName = "hibernate-5.0";
			}
			
			System.out.println("Application working with: " + projectName);
			
			List<String> filePathList = FileUtil.getFilePath(path);
			
			for(String file : filePathList) {
				
				//Generate Catch Based metrics
				new CatchQuantity(file);
				new CatchSizeLOC(file);
				new CatchSizeSLOC(file);
				new FlowHandlingAction(file);
				
				CatchFlowMetricsModel catchModel = new CatchFlowMetricsModel(
						file, 
						projectName, 
						CatchQuantity.count, CatchSizeLOC.count, 
						CatchSizeSLOC.count, 
						FlowHandlingAction.caughtExceptions, 
						FlowHandlingAction.totalPossibleExceptions, 
						FlowHandlingAction.percentageOfCaughtExceptions);
				
				catchFlowMetricsList.add(catchModel);
				
				//Generate Try Based metrics
				new InvokedMethods(file);
				new TryQuantity(file);
				new TrySizeLOC(file);
				new TrySizeSLOC(file);
				
				TryFlowMetricsModel tryModel = new TryFlowMetricsModel(file,
						projectName,
						InvokedMethods.count, 
						TryQuantity.count, 
						TrySizeLOC.count, 
						TrySizeSLOC.count);
				
				tryFlowMetricsList.add(tryModel);
				
				new DestructiveWrappingDetector(file);
				new NestedTryDetector(file);
				new ThrowsKitchenSinkDetector(file);
				new ThrowWithinFinallyDetector(file);
				new CatchandDoNothingDetector(file);
				new CatchandReturnnullDetector(file);
				new DummyHandlerDetector(file);
				new IgnoringInterruptedExceptionDetector(file);
				new CatchGenericDetector(file);
				new ThrowsGenericDetector(file);
				new LogandReturnNullDetector(file);
				new LogandThrowDetector(file);
				
				AntiPatternModel antiModel = new AntiPatternModel(
						file, 
						projectName, 
						DestructiveWrappingDetector.count, 
						NestedTryDetector.count, 
						ThrowsKitchenSinkDetector.count, 
						ThrowWithinFinallyDetector.count,
						CatchandDoNothingDetector.count,
						CatchandReturnnullDetector.count,
						DummyHandlerDetector.count,
						IgnoringInterruptedExceptionDetector.count,
						CatchGenericDetector.count,
						ThrowsGenericDetector.count,
						LogandReturnNullDetector.count,
						LogandThrowDetector.count);
				
				antiPatternList.add(antiModel);
				
			}
		}
		
		String catchColumn = "FilePath,Project,CatchQuantity,CatchSizeLOC,CatchSizeSLOC,CaughtExceptions,TotalPossibleExceptions,PercentageOfCaughtExceptions";
		CatchFlowMetricsModel.GenerateCSVFromList(catchFlowMetricsList, "Catch_Based.csv", catchColumn);
		System.out.println("CSV Generated for Catch Based");
		
		String tryColumn = "FilePath,Project,InvokedMethods,TryQuantity,TrySizeLOC,TrySizeSLOC";
		TryFlowMetricsModel.GenerateCSVFromList(tryFlowMetricsList, "Try_Based.csv", tryColumn);
		System.out.println("CSV Generated for Try Based");
		

		String antiPatternColumn = "FilePath,Project,DestructiveWrapping,NestedTry,ThorwsKitchenSink,ThrowWithinFinally,"
				+ "CatchandDoNothing,CatchandReturnnull,DummyHandler,IgnoringInterruptedException,CatchGeneric,ThrowsGeneric,LogandReturnNull"
				+ ",LogandThrowDetector";
		AntiPatternModel.GenerateCSVFromList(antiPatternList, "Throws_Anti-Pattern_Based.csv", antiPatternColumn);
		System.out.println("CSV Generated for Anti Pattern Based");
	}
}
