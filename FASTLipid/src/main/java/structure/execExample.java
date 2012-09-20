/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package structure;

import structure.rule.BondDistance3nPlus2Rule;
import structure.rule.StarterDoubleBondRule;
import structure.rule.BondRule;
import structure.rule.NoDoubleBondsTogetherRule;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;

/**
 *
 * @author pmoreno
 */
public class execExample {

    public static void main(String[] args) {

        ChainFactory factory = ChainFactory.getInstance();
        factory.setChainIterator(34, 34, 2, 2);
        BondRule starter2 = new StarterDoubleBondRule(2);
        factory.addAlwaysRule(starter2);
        factory.addAlwaysRule(new NoDoubleBondsTogetherRule());
        factory.addAlwaysRule(new BondDistance3nPlus2Rule());
        long start = System.currentTimeMillis();
        IMolecule mol = factory.nextChain();
        //char[] mol = factory.nextCharChain();
        long generatedStructs=0;
        long current=start;
        while(mol!=null) {
            generatedStructs++;
            printChain(mol);
            if(generatedStructs % 100 == 0) {
                current = System.currentTimeMillis() - start;
                System.out.println("Time:"+current+"\tStructsGen:"+generatedStructs);
            }
            //mol = factory.nextCharChain();
            mol = factory.nextChain();
        }
        System.out.println("Generated structs:"+generatedStructs);
        current=System.currentTimeMillis() - start;
        System.out.println("Time:"+current);
    }

    private static void printChain(IMolecule mol) {
        String res = "C";
        for(IBond bond : mol.bonds()) {
            if(bond.getOrder().equals(IBond.Order.SINGLE))
                res+="-C";
            else
                res+="=C";
        }
        System.out.println(res);
    }

    private static void printChain(char[] mol) {
        String res = "C";
        for(char c : mol) {
            if(c == '1')
                res+="=C";
            else
                res+="-C";
        }
        System.out.println(res);
    }
}
