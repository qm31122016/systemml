/**
 * (C) Copyright IBM Corp. 2010, 2015
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package com.ibm.bi.dml.api.jmlc;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import com.ibm.bi.dml.runtime.controlprogram.ForProgramBlock;
import com.ibm.bi.dml.runtime.controlprogram.FunctionProgramBlock;
import com.ibm.bi.dml.runtime.controlprogram.IfProgramBlock;
import com.ibm.bi.dml.runtime.controlprogram.Program;
import com.ibm.bi.dml.runtime.controlprogram.ProgramBlock;
import com.ibm.bi.dml.runtime.controlprogram.WhileProgramBlock;
import com.ibm.bi.dml.runtime.instructions.Instruction;
import com.ibm.bi.dml.runtime.instructions.cp.VariableCPInstruction;

public class JMLCUtils 
{
	
	
	/**
	 * Removes rmvar instructions that would remove any of the given outputs.
	 * This is important for keeping registered outputs after the program terminates.
	 * 
	 * @param prog
	 */
	public static void cleanupRuntimeProgram( Program prog, String[] outputs)
	{
		Map<String, FunctionProgramBlock> funcMap = prog.getFunctionProgramBlocks();
		if( funcMap != null && !funcMap.isEmpty() )
		{
			for( Entry<String, FunctionProgramBlock> e : funcMap.entrySet() )
			{
				FunctionProgramBlock fpb = e.getValue();
				for( ProgramBlock pb : fpb.getChildBlocks() )
					rCleanupRuntimeProgram(pb, outputs);
			}
		}
		
		for( ProgramBlock pb : prog.getProgramBlocks() )
			rCleanupRuntimeProgram(pb, outputs);
	}
	
	/**
	 * 
	 * @param pb
	 * @param outputs
	 */
	private static void rCleanupRuntimeProgram( ProgramBlock pb, String[] outputs )
	{
		if( pb instanceof WhileProgramBlock )
		{
			WhileProgramBlock wpb = (WhileProgramBlock)pb;
			for( ProgramBlock pbc : wpb.getChildBlocks() )
				rCleanupRuntimeProgram(pbc,outputs);
		}
		else if( pb instanceof IfProgramBlock )
		{
			IfProgramBlock ipb = (IfProgramBlock)pb;
			for( ProgramBlock pbc : ipb.getChildBlocksIfBody() )
				rCleanupRuntimeProgram(pbc,outputs);
			for( ProgramBlock pbc : ipb.getChildBlocksElseBody() )
				rCleanupRuntimeProgram(pbc,outputs);
		}
		else if( pb instanceof ForProgramBlock )
		{
			ForProgramBlock fpb = (ForProgramBlock)pb;
			for( ProgramBlock pbc : fpb.getChildBlocks() )
				rCleanupRuntimeProgram(pbc,outputs);
		}
		else
		{
			ArrayList<Instruction> tmp = pb.getInstructions();
			for( int i=0; i<tmp.size(); i++ )
			{
				Instruction linst = tmp.get(i);
				if( linst instanceof VariableCPInstruction && ((VariableCPInstruction)linst).isRemoveVariable() )
				{
					VariableCPInstruction varinst = (VariableCPInstruction) linst;
					for( String var : outputs )
						if( varinst.isRemoveVariable(var) )
						{
							tmp.remove(i);
							i--;
							break;
						}
				}
			}
		}
	}
}
