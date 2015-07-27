/**
 * IBM Confidential
 * OCO Source Materials
 * (C) Copyright IBM Corp. 2010, 2015
 * The source code for this program is not published or otherwise divested of its trade secrets, irrespective of what has been deposited with the U.S. Copyright Office.
 */

package com.ibm.bi.dml.test.integration.functions.binary.matrix;

import java.util.HashMap;

import org.junit.Test;

import com.ibm.bi.dml.api.DMLScript;
import com.ibm.bi.dml.api.DMLScript.RUNTIME_PLATFORM;
import com.ibm.bi.dml.lops.LopProperties.ExecType;
import com.ibm.bi.dml.runtime.matrix.data.MatrixValue.CellIndex;
import com.ibm.bi.dml.test.integration.AutomatedTestBase;
import com.ibm.bi.dml.test.integration.TestConfiguration;
import com.ibm.bi.dml.test.utils.TestUtils;

/**
 * 
 */
public class QuantileTest extends AutomatedTestBase 
{
	@SuppressWarnings("unused")
	private static final String _COPYRIGHT = "Licensed Materials - Property of IBM\n(C) Copyright IBM Corp. 2010, 2015\n" +
                                             "US Government Users Restricted Rights - Use, duplication  disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";
	
	private final static String TEST_NAME1 = "Quantile";
	private final static String TEST_NAME2 = "Median";
	private final static String TEST_NAME3 = "IQM";
	
	private final static String TEST_DIR = "functions/binary/matrix/";
	private final static double eps = 1e-10;
	
	private final static int rows = 1973;
	private final static int maxVal = 7; 
	private final static double sparsity1 = 0.9;
	private final static double sparsity2 = 0.3;
	
	@Override
	public void setUp() 
	{
		TestUtils.clearAssertionInformation();
		addTestConfiguration(TEST_NAME1, new TestConfiguration(TEST_DIR, TEST_NAME1, new String[] { "R" })   ); 
		addTestConfiguration(TEST_NAME2, new TestConfiguration(TEST_DIR, TEST_NAME2, new String[] { "R" })   ); 
		addTestConfiguration(TEST_NAME3, new TestConfiguration(TEST_DIR, TEST_NAME3, new String[] { "R" })   ); 		
	}
	
	@Test
	public void testQuantile1DenseCP() 
	{
		runQuantileTest(TEST_NAME1, 0.25, false, ExecType.CP);
	}
	
	@Test
	public void testQuantile2DenseCP() 
	{
		runQuantileTest(TEST_NAME1, 0.50, false, ExecType.CP);
	}
	
	@Test
	public void testQuantile3DenseCP() 
	{
		runQuantileTest(TEST_NAME1, 0.75, false, ExecType.CP);
	}
	
	@Test
	public void testQuantile1SparseCP() 
	{
		runQuantileTest(TEST_NAME1, 0.25, true, ExecType.CP);
	}
	
	@Test
	public void testQuantile2SparseCP() 
	{
		runQuantileTest(TEST_NAME1, 0.50, true, ExecType.CP);
	}
	
	@Test
	public void testQuantile3SparseCP() 
	{
		runQuantileTest(TEST_NAME1, 0.75, true, ExecType.CP);
	}
	
	@Test
	public void testQuantile1DenseMR() 
	{
		runQuantileTest(TEST_NAME1, 0.25, false, ExecType.MR);
	}
	
	@Test
	public void testQuantile2DenseMR() 
	{
		runQuantileTest(TEST_NAME1, 0.50, false, ExecType.MR);
	}
	
	@Test
	public void testQuantile3DenseMR() 
	{
		runQuantileTest(TEST_NAME1, 0.75, false, ExecType.MR);
	}
	
	@Test
	public void testQuantile1SparseMR() 
	{
		runQuantileTest(TEST_NAME1, 0.25, true, ExecType.MR);
	}
	
	@Test
	public void testQuantile2SparseMR() 
	{
		runQuantileTest(TEST_NAME1, 0.50, true, ExecType.MR);
	}
	
	@Test
	public void testQuantile3SparseMR() 
	{
		runQuantileTest(TEST_NAME1, 0.75, true, ExecType.MR);
	}

	@Test
	public void testQuantile1DenseSP() 
	{
		runQuantileTest(TEST_NAME1, 0.25, false, ExecType.SPARK);
	}
	
	@Test
	public void testQuantile2DenseSP() 
	{
		runQuantileTest(TEST_NAME1, 0.50, false, ExecType.SPARK);
	}
	
	@Test
	public void testQuantile3DenseSP() 
	{
		runQuantileTest(TEST_NAME1, 0.75, false, ExecType.SPARK);
	}
	
	@Test
	public void testQuantile1SparseSP() 
	{
		runQuantileTest(TEST_NAME1, 0.25, true, ExecType.SPARK);
	}
	
	@Test
	public void testQuantile2SparseSP() 
	{
		runQuantileTest(TEST_NAME1, 0.50, true, ExecType.SPARK);
	}
	
	@Test
	public void testQuantile3SparseSP() 
	{
		runQuantileTest(TEST_NAME1, 0.75, true, ExecType.SPARK);
	}

	@Test
	public void testMedianDenseCP() 
	{
		runQuantileTest(TEST_NAME2, -1, false, ExecType.CP);
	}
	
	@Test
	public void testMedianSparseCP() 
	{
		runQuantileTest(TEST_NAME2, -1, true, ExecType.CP);
	}
	
	@Test
	public void testMedianDenseMR() 
	{
		runQuantileTest(TEST_NAME2, -1, false, ExecType.MR);
	}
	
	@Test
	public void testMedianSparseMR() 
	{
		runQuantileTest(TEST_NAME2, -1, true, ExecType.MR);
	}
	
	@Test
	public void testMedianDenseSP() 
	{
		runQuantileTest(TEST_NAME2, -1, false, ExecType.SPARK);
	}

	@Test
	public void testMedianSparseSP() 
	{
		runQuantileTest(TEST_NAME2, -1, true, ExecType.SPARK);
	}

	@Test
	public void testIQMDenseCP() 
	{
		runQuantileTest(TEST_NAME3, -1, false, ExecType.CP);
	}
	
	@Test
	public void testIQMSparseCP() 
	{
		runQuantileTest(TEST_NAME3, -1, true, ExecType.CP);
	}
	
	@Test
	public void testIQMDenseMR() 
	{
		runQuantileTest(TEST_NAME3, -1, false, ExecType.MR);
	}
	
	@Test
	public void testIQMSparseMR() 
	{
		runQuantileTest(TEST_NAME3, -1, true, ExecType.MR);
	}
	
	@Test
	public void testIQMDenseSP() 
	{
		runQuantileTest(TEST_NAME3, -1, false, ExecType.SPARK);
	}

	@Test
	public void testIQMSparseSP() 
	{
		runQuantileTest(TEST_NAME3, -1, true, ExecType.SPARK);
	}
	
	/**
	 * 
	 * @param sparseM1
	 * @param sparseM2
	 * @param instType
	 */
	private void runQuantileTest( String TEST_NAME, double p, boolean sparse, ExecType et)
	{
		//rtplatform for MR
		RUNTIME_PLATFORM platformOld = rtplatform;
		switch( et ){
			case MR: rtplatform = RUNTIME_PLATFORM.HADOOP; break;
			case SPARK: rtplatform = RUNTIME_PLATFORM.SPARK; break;
			default: rtplatform = RUNTIME_PLATFORM.HYBRID; break;
		}
	
		boolean sparkConfigOld = DMLScript.USE_LOCAL_SPARK_CONFIG;
		if( rtplatform == RUNTIME_PLATFORM.SPARK )
			DMLScript.USE_LOCAL_SPARK_CONFIG = true;
		
		try
		{
			TestConfiguration config = getTestConfiguration(TEST_NAME);
			
			String HOME = SCRIPT_DIR + TEST_DIR;
			fullDMLScriptName = HOME + TEST_NAME + ".dml";
			programArgs = new String[]{"-args", HOME + INPUT_DIR + "A",
					                        Double.toString(p),
					                        HOME + OUTPUT_DIR + "R"};
			fullRScriptName = HOME + TEST_NAME + ".R";
			rCmd = "Rscript" + " " + fullRScriptName + " " + 
			       HOME + INPUT_DIR + " " + p + " "+ HOME + EXPECTED_DIR;
			
			loadTestConfiguration(config);
	
			//generate actual dataset (always dense because values <=0 invalid)
			double sparsitya = sparse ? sparsity2 : sparsity1;
			double[][] A = getRandomMatrix(rows, 1, 1, maxVal, sparsitya, 1236); 
			writeInputMatrixWithMTD("A", A, true);
			
			runTest(true, false, null, -1); 
			runRScript(true); 
			
			//compare matrices 
			HashMap<CellIndex, Double> dmlfile = readDMLMatrixFromHDFS("R");
			HashMap<CellIndex, Double> rfile  = readRMatrixFromFS("R");
			TestUtils.compareMatrices(dmlfile, rfile, eps, "Stat-DML", "Stat-R");
		}
		finally
		{
			rtplatform = platformOld;
			DMLScript.USE_LOCAL_SPARK_CONFIG = sparkConfigOld;
		}
	}

}