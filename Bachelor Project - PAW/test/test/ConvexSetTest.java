package test;

import java.io.IOException;
import java.util.Random;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Assert;

import allocation.algorithms.PopularConvexSet;
import preference.PreferenceRelationCreator;
import preference.scenario.Scenario;
import preference.scenario.ScenarioIO;
import preference.scenario.ScenarioIOException;

public class ConvexSetTest {
	private static final int CASE_COUNT = 100;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void testGetAllAllocations() {
		Random r = new Random();
		for(int i=0;i<CASE_COUNT;i++) {
			int size = 3 + r.nextInt(4);
			Scenario s = PreferenceRelationCreator.createScenarioImpartialCulture(size, size, true);
			
			PopularConvexSet conv = new PopularConvexSet();
			if(conv.getAllAllocations(s).isEmpty()) {
				System.out.println("ERROR!");
				System.out.println("Run: "+i);
				System.out.println("Size: "+size);
				System.out.println(s);
				
				Assert.assertTrue("Could not generate profile", false);
				break;
			}
		}
		
		
		int[] size = {1,2,3,3};
		for(int i=1;i<=4;i++) {
			try {
				Scenario sc = ScenarioIO.loadScenario("/Users/Dominik/Documents/Studium/Semester 6/Bachelor Arbeit/Test Cases/case "+i+".txt");
				PopularConvexSet conv = new PopularConvexSet();
				Assert.assertEquals("Wrong result for case "+i, size[i-1], conv.getAllAllocations(sc).size());
			} catch (IOException | ScenarioIOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Assert.assertTrue("File Error",false);
			}
			
			
		}
		
		
		
	}

}
