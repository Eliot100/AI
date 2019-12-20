import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class FactorTest {

	@Test
	void testJoiningFactors() {
		fail("Not yet implemented");
	}

	@Test
	void testEliminaton() {
		fail("Not yet implemented");
	}

	@Test
	void testMakeremovebycol()
	{
		
	}

	@Test
	void testMake_arr()
	{
		Factor f1 = new Factor();
		char[] known = {'a','b','c'};
		f1.known = known;
		String[] row1 = {"T","F","T"};
		String[] row2 = {"T","F","F"};
		String[] row3 = {"F","F","T"};
		String[][] matrix1 = {row1,row2,row3};
		f1.matrix = matrix1;
		int row = 0;
		Factor toeliminate = new Factor();
		char[] known2 = {'b'};
		toeliminate.known = known2;
		String[] arr = f1.make_arr(f1, row, toeliminate);
		String[] expected = {"T","T"};
		if(arr.length!=expected.length)
			fail("Not the same length");
		for (int i = 0; i < expected.length; i++)
		{
			assertEquals(expected[i], arr[i]);
		}
		
	}

	@Test
	void testGet_value_to_merge() 
	{
		String[] arr = {"T", "F"};
		int[] removebycol = {0,1};
		String[] row1 = {"T","F","T"};
		String[] row2 = {"T","F","F"};
		String[] row3 = {"F","F","T"};
		Factor f1 = new Factor();
		double[] unknown = {0.1,0.1,0.2};
		String[][] matrix1 = {row1,row2,row3};
		f1.matrix = matrix1;
		f1.unknown = unknown;
		double expected = f1.get_value_to_merge(arr, f1, removebycol);
		assertEquals(expected, 0.2);
		
	}

	@Test
	void testIs_row_included() 
	{
		String[] row = {"T", "F"};
		String[] arr = {"T","F","F"};
		int[] removebycol = {0,2};
		Factor f1 = new Factor();
		boolean expected = f1.is_row_included(arr, row, removebycol);
		assertEquals(expected, true);
	}
}
