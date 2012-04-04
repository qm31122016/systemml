package dml.packagesupport;

import java.io.DataOutputStream;

import org.netlib.util.intW;
import org.nimble.hadoop.HDFSFileManager;
import dml.packagesupport.FIO;
import dml.packagesupport.Matrix;
import dml.packagesupport.Matrix.ValueType;
import dml.packagesupport.PackageFunction;
import dml.packagesupport.PackageRuntimeException;
/**
 * Wrapper class for eigen value computation
 * @author aghoting
 *
 */
public class EigenWrapper extends PackageFunction {

 
	private static final long serialVersionUID = 87925877955636432L;
	
	String e_values_file = "PackageSupport/EigenValuesFile";
	String e_vectors_file = "PackageSupport/EigenVectorsFile";
	
	//eigen values matrix
	Matrix return_e_values;
	//eigen vectors matrix
	Matrix return_e_vectors; 

	@Override
	public int getNumFunctionOutputs() {
 
		return 2;
		
	}

	@Override
	public FIO getFunctionOutput(int pos) {
		
		if(pos == 0)
			return return_e_values;
		
		if(pos == 1)
			return return_e_vectors;
		
		throw new PackageRuntimeException("Invalid function output being requested");
	}

	@Override
	public void execute() { 

		Matrix input_m = (Matrix) this.getFunctionInput(0);
		double [][] arr = input_m.getMatrixAsDoubleArray();
		
		 
		intW numValues = new intW(0); 
		intW info = new intW(0);
		double []e_values = new double[arr.length];
		double [][]e_vectors = new double[arr.length][arr.length];
		int[] i_suppz = new int[2* arr.length];
		double []work = new double[26*arr.length];
		int []iwork = new int[10*arr.length];
		
		try
		{
			//compute eigen values and vectors
			org.netlib.lapack.DSYEVR.DSYEVR("V", "A", "U", arr.length, arr, -1, -1, -1, -1, 0.0, numValues, e_values, e_vectors, i_suppz, work, 26*arr.length, iwork, 10*arr.length, info);
	 		

			//write out eigen values and eigen vectors
			DataOutputStream ostream = HDFSFileManager.getOutputStreamStatic(e_values_file, true);
			
			for(int i=0; i < e_values.length; i++)
			{
				 
				ostream.writeBytes((i+1) + " " + (i+1) + " " + e_values[i] + "\n");	
			}
		
			ostream.close();
			
			ostream = HDFSFileManager.getOutputStreamStatic(e_vectors_file, true);
			for(int i=0; i < e_vectors.length; i++)
			{
				for(int j=0; j < e_vectors[i].length; j++)
				{
					 
					ostream.writeBytes((i+1) + " " + (j+1) + " " + e_vectors[i][j] + "\n");	
				}
			}
		
			ostream.close();
			
			return_e_values = new Matrix(e_values_file, e_values.length, e_values.length, ValueType.Double);
			return_e_vectors = new Matrix(e_vectors_file, e_vectors.length, e_vectors.length, ValueType.Double);
		}
		catch(Exception e)
		{
			throw new PackageRuntimeException("Error performing eigen");
		}
	}

}
