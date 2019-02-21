package matrices;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DefaultRealMatrixChangingVisitor;
import org.apache.commons.math3.linear.DefaultRealMatrixPreservingVisitor;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.OpenMapRealMatrix;
import org.apache.commons.math3.linear.RealMatrixChangingVisitor;
import org.apache.commons.math3.linear.RealMatrixPreservingVisitor;
import org.apache.commons.math3.linear.RealVector;

/**
 * @author C. Savkli, Dec 26, 2017
 * @version 1.0
 */

public class Example8_SparseMatrixLibrary
{
	public Example8_SparseMatrixLibrary()
	{	
		this.constructAndPrintSparseMatrix();
//		this.testBasicMatrixAlgebra();
//		this.testAggregateRowsAndColumns();
//		this.testOperateOnElementsUsingVisitorInterface();		
//		this.testAggregateElementsUsingVisitorInterface();
//		this.testEigenDecomposition();
	}
	
	private void constructAndPrintSparseMatrix()
	{
		System.out.println("\n### Construct and print sparse matrix###\n");
		OpenMapRealMatrix a = new OpenMapRealMatrix(2,2);
        a.setEntry(0, 0, 1);
        a.setEntry(0, 1, 0);
        a.setEntry(1, 0, -3);
        a.setEntry(1, 1, 1);
        MatrixUtil.printSparseMatrix(a);
    }
	
	private void testBasicMatrixAlgebra()
	{
		System.out.println("\n### Test basic matrix algebra ###\n");
		OpenMapRealMatrix a = new OpenMapRealMatrix(2,2);
        a.setEntry(0, 0, 1);
        a.setEntry(0, 1, 2);
        a.setEntry(1, 0, 3);
        a.setEntry(1, 1, 1);
                
        OpenMapRealMatrix b = new OpenMapRealMatrix(2,2);
        b.setEntry(0, 0, 2);
        b.setEntry(1, 0, 1);
        b.setEntry(1, 0, 0);
        b.setEntry(1, 1, -1);
        
        System.out.println("A: "+ a);
        
        System.out.println("B: " + b);
                
        OpenMapRealMatrix mult = a.multiply(b);

        System.out.println("A*B: " + mult.toString());
        
        OpenMapRealMatrix scaled = (OpenMapRealMatrix) a.scalarMultiply(2);
        
        System.out.println("2A: " + scaled);
        
        OpenMapRealMatrix incremented = (OpenMapRealMatrix) a.scalarAdd(3);
        
        System.out.println("A elements incremented by 3: " + incremented);
	}
			
	public void testAggregateRowsAndColumns()
	{
		System.out.println("\n### Test row and column sum ###\n");
		OpenMapRealMatrix a2 = new OpenMapRealMatrix(2,2);
		a2.setEntry(0, 0, 1);
		a2.setEntry(0, 1, 2);
		a2.setEntry(1, 1, 7);
		a2.setEntry(1, 0, 3);
		System.out.println(a2+"\n");
		
		System.out.println("Row and column sums using multiplication:");
		
		double ONE = 1.0;
		RealVector ones = new ArrayRealVector(2,ONE);
		
		RealVector rowSum = a2.operate(ones);
		RealVector columnSum = a2.preMultiply(ones);
		
		System.out.println("Row sum = " + rowSum);
		System.out.println("Column sum = " + columnSum);
	}
	
	private void testOperateOnElementsUsingVisitorInterface()
	{
		System.out.println("\n### Test in place element operation ###\n");
		OpenMapRealMatrix a2 = new OpenMapRealMatrix(2,2);
		a2.setEntry(0, 0, 1);
		a2.setEntry(0, 1, 2);
		a2.setEntry(1, 1, 7);
		a2.setEntry(1, 0, 3);
		System.out.println("A: "+a2+"\n");
        
		class MyFunction extends DefaultRealMatrixChangingVisitor 
		{
			@Override
			public double visit(int row, int col, double val) {return val*val;}
		}
		
		RealMatrixChangingVisitor function = new MyFunction();
		
		a2.walkInOptimizedOrder(function);
		
        System.out.println("a_(ij)^2: "+a2);
	}
	
	private void testAggregateElementsUsingVisitorInterface()
	{
		System.out.println("\n### Test aggregation ###\n");
		OpenMapRealMatrix a2 = new OpenMapRealMatrix(2,2);
		a2.setEntry(0, 0, 1);
		a2.setEntry(0, 1, 2);
		a2.setEntry(1, 1, 7);
		a2.setEntry(1, 0, 3);
		System.out.println(a2+"\n");
        
		class MyFunction extends DefaultRealMatrixPreservingVisitor 
		{
			double sum = 0.0;
			
			@Override
			public void visit(int row, int col, double val) 
			{
				sum = sum + val*val;
			}
			
			public double end() { return sum;}
		}
		
		RealMatrixPreservingVisitor function = new MyFunction();
		
		double sumSquared = a2.walkInOptimizedOrder(function);
		
        System.out.println("Sum squared elements : "+sumSquared);
	}
		
	private void testEigenDecomposition()
	{
		System.out.println("\n### Test Eigenvalue Decomposition ###\n");
		OpenMapRealMatrix a2 = new OpenMapRealMatrix(2,2);
        a2.setEntry(0, 0, 0);
        a2.setEntry(0, 1, 1);
        a2.setEntry(1, 0, 1);
        a2.setEntry(1, 1, 0);
        
        System.out.println(a2+"\n");
        
        EigenDecomposition eigenDecomposition = new EigenDecomposition(a2);
        double[] eigenvalues = eigenDecomposition.getRealEigenvalues();

		for(int j=0; j<2; j++)
		{
			RealVector eigenvector = eigenDecomposition.getEigenvector(j);
			System.out.println("eigenvalue : "+eigenvalues[j]);
			System.out.println("eigenvector : " + eigenvector+"\n");
		}
	}

	public static void main(String[] args)
	{
        new Example8_SparseMatrixLibrary();
	}
}
