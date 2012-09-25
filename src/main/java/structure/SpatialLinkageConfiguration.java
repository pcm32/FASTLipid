/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package structure;

/**
 * This enumeration specifies how the chain should be bounded to the head,
 * either through an Acyl -(C1=O)-O-Head or Alkyl -(C2=O)-O-Head. The close or distant
 * attribute defines whether it is the closest position to the head (secondary carbon)
 * or the distant position to the head (primary carbon).
 *
 * This all supposes a glycerol between the head and the fatty acids.
 *
 * @author pmoreno
 */
public enum SpatialLinkageConfiguration {
    AcylDistant,AcylClose,AlkylDistant,AlkylClose;
}
