/**
 * Sample implementation for ImageImpl
 * extracted from : http://groups.google.com/group/play-framework/browse_thread/thread/fb81d19afdfd22e7
 * @author Michael Boyd
 */
package models.shared;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import models.Game;

/**
 * @author frederic
 *
 */
@Entity(name = "shops_game_image") 
public class SampleImage extends ImageImpl 
{ 
    @ManyToOne 
    private Game game; 
    private int displayOrder; 
    /** 
     * @see ImageImpl.VariantImpl 
     */ 
    public enum Variant implements ImageImpl.VariantImpl 
    { 
        SQUARE_70(70, 70), 
        SQUARE_140(140, 140), 
        SQUARE_200(200, 200), 
        LANDSCAPE_400(400, -1), 
        FULL(-1, -1); 
        private final int maxWidth; 
        private final int maxHeight; 
        Variant(final int maxWidth, final int maxHeight) 
        { 
            this.maxWidth = maxWidth; 
            this.maxHeight = maxHeight; 
        } 
        public int getMaxWidth() 
        { 
            return maxWidth; 
        } 
        public int getMaxHeight() 
        { 
            return maxHeight; 
        } 
    } 
    @Override 
    protected String getAwsFolderName() 
    { 
        return "game-image"; 
    } 
    @Override 
    protected VariantImpl[] getVariants() 
    { 
        return Variant.values(); 
    } 
    @Override 
    protected VariantImpl getVariantFull() 
    { 
        return Variant.FULL; 
    } 
} 