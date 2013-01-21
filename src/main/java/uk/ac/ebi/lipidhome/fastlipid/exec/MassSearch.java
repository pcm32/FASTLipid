/**
 * MassSearch.java
 *
 * 2012.10.12
 *
 * This file is part of the CheMet library
 *
 * The CheMet library is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * CheMet is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with CheMet. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.ebi.lipidhome.fastlipid.exec;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.SDFWriter;
import uk.ac.ebi.lipidhome.fastlipid.counter.BooleanRBCounterStartSeeder;
import uk.ac.ebi.lipidhome.fastlipid.generator.ChainFactoryGenerator;
import uk.ac.ebi.lipidhome.fastlipid.mass.PPMBasedMassRange;
import uk.ac.ebi.lipidhome.fastlipid.structure.*;
import uk.ac.ebi.lipidhome.fastlipid.structure.rule.BondDistance3nPlus2Rule;
import uk.ac.ebi.lipidhome.fastlipid.structure.rule.BondRule;
import uk.ac.ebi.lipidhome.fastlipid.structure.rule.NoDoubleBondsTogetherRule;
import uk.ac.ebi.lipidhome.fastlipid.structure.rule.StarterDoubleBondRule;

/**
 * @name MassSearch @date 2012.10.12
 *
 * @version $Rev$ : Last Changed $Date$
 * @author pmoreno
 * @author $Author$ (this version) 
 * @brief Execution for a mass search, to be exposed as command line.
 *
 */
public class MassSearch {

    private static final Logger LOGGER = Logger.getLogger(MassSearch.class);
    @Option(name = "-l", required = false, usage = "List desired linkers to use, separated by comma. "
    + "Default uses all of them.")
    private void setLinkers(String linkers) {
        linkersStr.addAll(Arrays.asList(linkers.split(",")));
    }
    private List<String> linkersStr = new ArrayList<String>();
    
    
    @Option(name = "-h", required = false, usage = "List desired head groups to use, separated by comma. "
    + "Defult uses all of them.")
    private void setHeadGroups(String heads) {
        headGrpsStr.addAll(Arrays.asList(heads.split(",")));
    }
    private List<String> headGrpsStr = new ArrayList<String>();
    
    
    @Option(name = "-m", required = true, usage = "Mass to search.")
    private Double mass;
    @Option(name = "-t", required = true, usage = "PPM tolerance.")
    private Float ppm;
    
    @Option(name = "-r", required = false, usage = "Result type: (S) Species, (SP) sub-species, or (I) Isomers, sep by a comma. Default uses all.")
    private void setResultType(String resType) {
        resultType.addAll(Arrays.asList(resType.split(",")));
    }
    private List<String> resultType = new ArrayList<String>();
    
    @Option(name = "-o", required = true, usage = "Output files prefixes")
    private String outputPrefix;
    
    public static void main(String[] args) throws CDKException, IOException {
        new MassSearch().doMain(args);
    }

    public void doMain(String[] args) throws CDKException, IOException {
        CmdLineParser parser = new CmdLineParser(this);

        try {
            // parse the arguments.
            parser.parseArgument(args);

        } catch (CmdLineException e) {
            // if there's a problem in the command line,
            // you'll get this exception. this will report
            // an error message.
            System.err.println(e.getMessage());
            System.err.println("java uk.ac.ebi.lipidhome.fastlipid.exec.MassSearch [options...] arguments...");
            // print the list of available options
            parser.printUsage(System.err);
            System.err.println();

            // print option sample. This is useful some time
            //System.err.println("  Example: java SampleMain"+parser.printExample(ALL));

            return;
        }
        BufferedWriter speciesWriter = null;
        BufferedWriter subSpeciesWriter = null;
        SDFWriter molWriter = null;
        /**
         * Set outputs
         */
        if(resultType.isEmpty()) {
            resultType.addAll(Arrays.asList("SP","S","I"));
        }
        if (resultType.contains("S")) {
            speciesWriter = new BufferedWriter(new FileWriter(outputPrefix + "_species.txt"));
        }
        if (resultType.contains("SP")) {
            subSpeciesWriter = new BufferedWriter(new FileWriter(outputPrefix + "_subspecies.txt"));
        }
        if (resultType.contains("I")) {
            molWriter = new SDFWriter(new FileWriter(outputPrefix + "_isomers.sdf"));
        }
        /**
         * Set possible headers
         */
        List<HeadGroup> allowedHeadGroups = new ArrayList<HeadGroup>();
        if (headGrpsStr == null || headGrpsStr.isEmpty()) {
            allowedHeadGroups.addAll(Arrays.asList(HeadGroup.values()));
        } else {
            for (String hgName : headGrpsStr) {
                allowedHeadGroups.add(HeadGroup.valueOf(hgName));
            }
        }
        /**
         * Set possible linkers
         */
        List<SingleLinkConfiguration> allowedLinkers = new ArrayList<SingleLinkConfiguration>();
        if (linkersStr == null || linkersStr.isEmpty()) {
            allowedLinkers.addAll(Arrays.asList(SingleLinkConfiguration.values()));
        } else {
            for (String linker : linkersStr) {
                allowedLinkers.add(SingleLinkConfiguration.valueOf(linker));
            }
        }
        /**
         * Set Rules
         */
        List<BondRule> rules = Arrays.asList(new BondDistance3nPlus2Rule(), new NoDoubleBondsTogetherRule(), new StarterDoubleBondRule(2));
        ChainFactoryGenerator cfGen = new ChainFactoryGenerator(rules, new BooleanRBCounterStartSeeder(rules), true);
        ChemInfoContainerGenerator cicgForMassRange = new ChemInfoContainerGenerator();
        cicgForMassRange.setGenerateMolFormula(true);
        cicgForMassRange.setUseCachedObjects(true);

        PPMBasedMassRange ppmRange = new PPMBasedMassRange(mass, ppm);

        MassRangeIsomersGetter instance = new MassRangeIsomersGetter(allowedHeadGroups, allowedLinkers, cfGen, ppmRange, Boolean.FALSE, cicgForMassRange);

        while (instance.hasNext()) {
            SpeciesInfoContainer sic = instance.next();
            if (resultType.contains("S")) {
                speciesWriter.write(sic.toString() + "\n");
            }
            if (resultType.contains("SP") || resultType.contains("I")) {
                GeneralIsomersGetterForCarbonsAndDoubleBonds gigfcadb = new GeneralIsomersGetterForCarbonsAndDoubleBonds(sic);
                //gigfcadb.configForSmilesOutput();
                ChainFactoryGenerator cfg = new ChainFactoryGenerator(rules, new BooleanRBCounterStartSeeder(rules), true);
                gigfcadb.setChainFactoryGenerator(cfg);

                gigfcadb.exec();

                Iterator<SubSpecies> subSpIt = gigfcadb.getSupSpeciesIterator();
                while (subSpIt.hasNext()) {
                    SubSpecies sp = subSpIt.next();
                    if (resultType.contains("SP")) {
                        subSpeciesWriter.write(sp.toString() + "\n");
                    }
                    if (resultType.contains("I")) {
                        GeneralIterativeLipidGetter gipg = new GeneralIterativeLipidGetter(sp, cfg);
                        ChemInfoContainerGenerator gen = new ChemInfoContainerGenerator();
                        gen.setGenerateChainInfoContainers(true);
                        gen.setGenerateCDKMol(true);
                        gipg.setChemInfoContainerGenerator(gen);
                        gipg.run();

                        int molCount=0;
                        while (true) {
                            ChemInfoContainer cic = gipg.nextChemInfoContainer();
                            if (cic == null) {
                                break;
                            }
                            molCount++;
                            try {
                            molWriter.write(cic.getCDKMolecule());
                            } catch(CDKException e) {
                                System.err.println("Could not write mol "+molCount);
                            }
                        }
                    }
                }
            }
        }
        
        if(speciesWriter!=null)
            speciesWriter.close();
        if(subSpeciesWriter!=null)
            subSpeciesWriter.close();
        if(molWriter!=null)
            molWriter.close();


    }
}
