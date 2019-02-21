package matrices;

import org.apache.commons.math3.linear.DefaultRealMatrixPreservingVisitor;
import org.apache.commons.math3.linear.RealMatrixPreservingVisitor;
import org.apache.commons.math3.linear.SparseRealMatrix;

public class MatrixUtil {

	public static void printSparseMatrix(SparseRealMatrix m)
	{
		class MyFunction extends DefaultRealMatrixPreservingVisitor 
		{	
			@Override
			public void visit(int row, int col, double val) 
			{
				if(val != 0) System.out.println("("+row +"," +col +") = " + val);
			}			
		}
		
		RealMatrixPreservingVisitor function = new MyFunction();
		
		m.walkInOptimizedOrder(function);
	}
}
