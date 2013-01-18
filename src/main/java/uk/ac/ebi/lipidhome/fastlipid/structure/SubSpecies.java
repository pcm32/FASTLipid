/**
 * SubSpecies.java
 *
 * 2012.08.20
 *
 * This file is part of the CheMet library
 * 
 * The CheMet library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * CheMet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with CheMet.  If not, see <http://www.gnu.org/licenses/>.
 */

package uk.ac.ebi.lipidhome.fastlipid.structure;


import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * @name    SubSpecies
 * @date    2012.08.20
 * @version $Rev$ : Last Changed $Date$
 * @author  pmoreno
 * @author  $Author$ (this version)
 * @brief   ...class description...
 *
 */
public class SubSpecies {

    private static final Logger LOGGER = Logger.getLogger( SubSpecies.class );

    private List<FattyAcidSpecies> fattyAcids;
    private List<SingleLinkConfiguration> linkages;
    private String name;
    private HeadGroup hg;
    //private Integer isomerCount;

    public SubSpecies(ChemInfoContainer cont) {
        this.hg = cont.getHg();
        this.linkages = new ArrayList<SingleLinkConfiguration>(cont.getLinkers());
        this.fattyAcids = new ArrayList<FattyAcidSpecies>();
        cont.getChainsInfo();
        for (ChainInfoContainer chainInfoContainer : cont.getChains()) {
            this.fattyAcids.add(new FattyAcidSpecies(chainInfoContainer.getNumCarbons(), chainInfoContainer.getDoubleBondPositions().size()));
        }
        
    }
    
    public void addFattyAcid(FattyAcidSpecies fattyAcid, SingleLinkConfiguration linkage) {
        this.fattyAcids.add(fattyAcid);
        this.linkages.add(linkage);
    }
    
    /**
     * @return the fattyAcids
     */
    public List<FattyAcidSpecies> getFattyAcids() {
        return fattyAcids;
    }

    /**
     * @return the linkages
     */
    public List<SingleLinkConfiguration> getLinkages() {
        return linkages;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    public HeadGroup getHeadGroup() {
        return hg;
    }

    @Override
    public String toString() {
        return "H:"+hg.name()+" L:"+linkages.toString()+" FAs:"+fattyAcids.toString();
    }
    
}
