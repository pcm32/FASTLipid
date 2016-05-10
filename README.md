# FASTLipid
FastLipid - The underlying theoretical lipid generator powering LipidHome

## Execution

The tool can be used both as a Java executable (in command line mode), as nodes in the Knime workflow environment, or as library to be embedded in other Java projects.

### Command line mode

The binary distribution includes two folders, target and bin, coming from the Maven distribution. In the folder bin, a shell script can be used to execute the MassSearch execution plan:

```shell
sh runMassQuery.sh
Option "-m" is required
java uk.ac.ebi.lipidhome.fastlipid.exec.MassSearch [options...] arguments...
 -h VAL : List desired head groups to use, separated by comma. Default uses all
          of them.
 -l VAL : List desired linkers to use, separated by comma. Default uses all of
          them.
 -m N   : Mass to search.
 -o VAL : Output files prefixes
 -r VAL : Result type: (S) Species, (SP) sub-species, or (I) Isomers, sep by a
          comma. Default uses all.
 -t N   : PPM tolerance.
```

Where an example execution is:

```
sh runMassQuery.sh -m 411.2022037 -o /tmp/test -r SP,S,I -t 100 -h PE,PC,PI
```

In this case, three files would be generated in the /tmp/ dir with the prefix test, with the results. If -h is ommitted, all known head groups (DG(3), Glycerol(3), PA(2), PC(2), PE(2), PG(2), PI(2), PS(2), DG12(2), DG13(2), LPA1(1), LPC1(1), LPE1(1), LPG1(1), LPI1(1), LPS1(1), MG1(1), LPA2(1), LPC2(1), LPE2(1), LPG2(1), LPI2(1), LPS2(1), MG2(1)) are used. Linkers (-l) can be either Acyl or Alkyl currently (again, not specifying -l uses all of them).

Species stands for HeadGroup + Linkers + Total Carbons in FAs + Total double bonds in FAs combinations.
SubSpecies stands for HeadGroup + Linkers + (Carbons in FA1, Carbons in FA2,...., Carbons in FAN) + (Double bonds in FA1, Double bonds in FA2,...., Double bonds in FAN).
Isomers stands for actually defined molecules, where the positions of the double bonds are defined on each fatty acid chain (output in this case is an SDF file, though molecules have no spatial coordinates nor stereochemistry)   

For more details on the nomenclature, please see the [LipidHome paper](http://www.ncbi.nlm.nih.gov/pubmed/23667450).
