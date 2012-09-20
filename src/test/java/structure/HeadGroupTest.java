/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package structure;

import java.io.IOException;
import java.util.List;
import junit.framework.TestCase;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import util.GenericAtomDetector;

/**
 *
 * @author pmoreno
 */
public class HeadGroupTest extends TestCase {
    
    public HeadGroupTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of values method, of class HeadGroup.
     *
    public void testValues() {
        System.out.println("values");
        HeadGroup[] expResult = null;
        HeadGroup[] result = HeadGroup.values();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of valueOf method, of class HeadGroup.
     *
    public void testValueOf() {
        System.out.println("valueOf");
        String name = "";
        HeadGroup expResult = null;
        HeadGroup result = HeadGroup.valueOf(name);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getHeadMolecule method, of class HeadGroup, for every HeadGroup.
     */
    public void testGetHeadMolecule() throws Exception {
        System.out.println("getHeadMolecule");
        IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
        for (HeadGroup headGroup : HeadGroup.values()) {
            System.out.println("Reading mol for : "+headGroup.name());
            IAtomContainer result = headGroup.getHeadMolecule(builder);
            assertNotNull(result);
        }
    }

    /**
     * Test of getNumOfSlots method, of class HeadGroup.
     */
    public void testGetNumOfSlots() {
        System.out.println("getNumOfSlots");
        for (HeadGroup headGroup : HeadGroup.values()) {
            assertNotNull(headGroup.getNumOfSlots());
        }
    }
    
    public void testMG1NumberOfSlots() throws CDKException, IOException {
        System.out.println("MG1 number of slots test");
        
        HeadGroup mg1 = HeadGroup.MG1;
        IAtomContainer molMG1 = mg1.getHeadMolecule(SilentChemObjectBuilder.getInstance());
        assertNotNull(molMG1);
        GenericAtomDetector detector = new GenericAtomDetector();
        List<IPseudoAtom> generics = detector.detectGenericAtoms(molMG1);
        assertNotNull(generics);
        assertEquals(1, generics.size());
    }
}
