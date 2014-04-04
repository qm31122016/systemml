/**
 * IBM Confidential
 * OCO Source Materials
 * (C) Copyright IBM Corp. 2010, 2014
 * The source code for this program is not published or otherwise divested of its trade secrets, irrespective of what has been deposited with the U.S. Copyright Office.
 */

package com.ibm.bi.dml.test.integration.functions.parfor;

import java.util.HashMap;

import junit.framework.Assert;

import org.junit.Test;

import com.ibm.bi.dml.api.DMLScript.RUNTIME_PLATFORM;
import com.ibm.bi.dml.hops.OptimizerUtils;
import com.ibm.bi.dml.lops.LopProperties.ExecType;
import com.ibm.bi.dml.runtime.controlprogram.parfor.stat.InfrastructureAnalyzer;
import com.ibm.bi.dml.runtime.matrix.io.MatrixValue.CellIndex;
import com.ibm.bi.dml.test.integration.AutomatedTestBase;
import com.ibm.bi.dml.test.integration.TestConfiguration;
import com.ibm.bi.dml.test.utils.TestUtils;
import com.ibm.bi.dml.utils.Statistics;

public class ParForRepeatedOptimizationTest extends AutomatedTestBase 
{
	@SuppressWarnings("unused")
	private static final String _COPYRIGHT = "Licensed Materials - Property of IBM\n(C) Copyright IBM Corp. 2010, 2014\n" +
                                             "US Government Users Restricted Rights - Use, duplication  disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";
	
	private final static String TEST_NAME = "parfor_repeatedopt";
	private final static String TEST_DIR = "functions/parfor/";
	private final static double eps = 1e-8;
	
	private final static int rows = 1000000; 
	private final static int cols = 10;  
	private final static double sparsity = 0.7;
	
	@Override
	public void setUp() 
	{
		addTestConfiguration( TEST_NAME, new TestConfiguration(TEST_DIR, TEST_NAME, new String[]{"R"}) ); 
	}

	//TODO combination with update
	
	@Test
	public void testParForRepeatedOptNoReuseNoUpdateCP() 
	{
		int numExpectedMRJobs = 1+3; //reblock, 3*partition
		runParForRepeatedOptTest( false, false, ExecType.CP, numExpectedMRJobs );
	}
	
	@Test
	public void testParForRepeatedOptNoReuseUpdateCP() 
	{
		int numExpectedMRJobs = 1+3+3; //reblock, 3*partition, 3*GMR
		runParForRepeatedOptTest( false, true, ExecType.CP, numExpectedMRJobs );
	}
	
	/*
	@Test
	public void testParForRepeatedOptReuseNoUpdateCP() 
	{
		int numExpectedMRJobs = 1+1; //reblock, partition
		runParForRepeatedOptTest( true, false, ExecType.CP, numExpectedMRJobs );
	}
	
	@Test
	public void testParForRepeatedOptReuseUpdateCP() 
	{
		int numExpectedMRJobs = 1+3+3; //reblock, 3*partition, 3*GMR
		runParForRepeatedOptTest( true, true, ExecType.CP, numExpectedMRJobs );
	}
	*/

	
	
	/**
	 * 
	 * @param outer execution mode of outer parfor loop
	 * @param inner execution mode of inner parfor loop
	 * @param instType execution mode of instructions
	 */
	private void runParForRepeatedOptTest( boolean reusePartitionedData, boolean update, ExecType et, int numExpectedMR )
	{
		RUNTIME_PLATFORM platformOld = rtplatform;
		double memfactorOld = OptimizerUtils.MEM_UTIL_FACTOR;
		
		TestConfiguration config = getTestConfiguration(TEST_NAME);
		config.addVariable("rows", rows);
		config.addVariable("cols", cols);
		
		try
		{
			rtplatform = (et==ExecType.MR) ? RUNTIME_PLATFORM.HADOOP : RUNTIME_PLATFORM.HYBRID;
			OptimizerUtils.MEM_UTIL_FACTOR = computeMemoryUtilFactor( 70 ); //force partitioning
		
			String HOME = SCRIPT_DIR + TEST_DIR;
			fullDMLScriptName = HOME + TEST_NAME + ".dml";
			programArgs = new String[]{"-stats","-args", HOME + INPUT_DIR + "V" , 
					                        Integer.toString(rows),
					                        Integer.toString(cols),
					                        HOME + OUTPUT_DIR + "R",
					                        Integer.toString(update?1:0)};
			fullRScriptName = HOME + TEST_NAME + ".R";
			rCmd = "Rscript" + " " + fullRScriptName + " " + 
			       HOME + INPUT_DIR + " " + HOME + EXPECTED_DIR + " " + Integer.toString(update?1:0);
			
			loadTestConfiguration(config);
	
	        double[][] V = getRandomMatrix(rows, cols, 0, 1, sparsity, 7);
			writeInputMatrix("V", V, true);
	
			runTest(true, false, null, -1);
			runRScript(true);
			
			Assert.assertEquals("Unexpected number of executed MR jobs.", numExpectedMR, Statistics.getNoOfExecutedMRJobs()); 
			
			//compare matrices
			HashMap<CellIndex, Double> dmlfile = readDMLMatrixFromHDFS("R");
			HashMap<CellIndex, Double> rfile  = readRMatrixFromFS("R");
			TestUtils.compareMatrices(dmlfile, rfile, eps, "DML", "R");	
			
			
			//TODO check compiled and execute jobs
		}
		finally
		{
			rtplatform = platformOld;
			OptimizerUtils.MEM_UTIL_FACTOR = memfactorOld;
		}
	}
	
	/**
	 * 
	 * @param mb
	 * @return
	 */
	private double computeMemoryUtilFactor( int mb )
	{
		double factor = Math.min(1, ((double)1024*1024*mb)/InfrastructureAnalyzer.getLocalMaxMemory());
		//System.out.println("Setting max mem util factor: "+factor);
		return factor;
	}
}