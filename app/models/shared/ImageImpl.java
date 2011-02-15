/**
 * Sample implementation for ImageImpl
 * extracted from : http://groups.google.com/group/play-framework/browse_thread/thread/fb81d19afdfd22e7
 * 
 * @author Michael Boyd
 * 
 * "I have an abstract class, ImageImpl, that all images extend. Is this 
 * appropriate to add the framework? Here is what I do (sorry, this part is 
 * long ...)" 
 */
package models.shared; 

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.persistence.MappedSuperclass;
import javax.persistence.PostRemove;
import javax.persistence.PrePersist;
import javax.persistence.Transient;

import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.Model;
/** 
 * A type of image, with its own variations of size. 
 */ 
@MappedSuperclass 
abstract public class ImageImpl extends Model 
{ 
    /** 
     * Name of the file uploaded. This is never used, but kept "just incase". 
     */ 
    @Required 
    protected String originalFilename; 
    /** 
     * Width in pixels. 
     */ 
    @Required 
    protected int originalWidth; 
    /** 
     * Height in pixels. 
     */ 
    @Required 
    protected int originalHeight; 
    /** 
     * The file's extension (no leading dot). Must be either "jpg" or "png". 
     */ 
    @Required 
    @MaxSize(3) 
    @MinSize(3) 
    protected String extension; 
    /** 
     * When the image was uploaded. 
     */ 
    protected Date createdAt; 
    @Transient 
    private final static File DIR_ROOT = play.Play.getFile("public/uploads"); 
    @Transient 
    private final static String AWS_BUCKET_NAME = "lights51"; 
    
    abstract protected String getAwsFolderName(); 
    
    abstract protected VariantImpl[] getVariants(); 
    
    abstract protected VariantImpl getVariantFull(); 
    /** 
     * The variants of size this image type can have. 
     */ 
    public interface VariantImpl 
    { 
        /** 
         * Get the variant's maximum width dimension. If it has no maximum width, -1 is returned. 
         * @return 
         */ 
        public int getMaxWidth(); 
        /** 
         * Get the variant's maximum height dimension. If it has no maximum height, -1 is returned. 
         * @return 
         */ 
        public int getMaxHeight(); 
        /** 
         * Used in file names. (This method doesn't need implemented - it's part of the language). 
         * @return 
         */ 
        public String name(); 
    } 
    /** 
     * Get the filename (including any directory common to local and S3) for any variant. 
     * 
     * @param variant 
     * @return 
     */ 
    protected String getVariantFilename(VariantImpl variant) 
    { 
        return getAwsFolderName() + "/" 
            + ((int) Math.floor(id / 1000.0f)) + "/" 
            + id + "_" + variant.name() + "." + extension; 
    } 
    /** 
     * @param variant 
     * @return Full path to image on the file system. 
     */ 
    public File getLocalPath(VariantImpl variant) 
    { 
        return new File(DIR_ROOT.getPath() + "/" + getVariantFilename(variant)); 
    } 
    /** 
     * Get the URL for file on S3 (path only, no host - use <code>getPublicUrl()</code> for that). 
     * 
     * @param variant 
     * @return 
     */ 
    public String getS3Path(VariantImpl variant) 
    { 
        return getVariantFilename(variant); 
    } 
    /** 
     * Get the URL for file on Amazon S3. 
     * 
     * @param variant 
     * @return Full URL. 
     */ 
    public String getPublicUrl(VariantImpl variant) 
    { 
        return "//staticpublic.lights51.net" 
        	+ play.Play.configuration.getProperty("application.domainExt") 
            + "/uploads/" + getVariantFilename(variant); 
        // @todo 
        // return "http://" + AWS_BUCKET_NAME + ".s3.amazonaws.com/" + getVariantFilename(variant); 
    } 
    
    /**
     * Set Url to copy to
     * @param url
     */
    public void setUrlToCopyFrom(String url) 
    { 
        if (extension == null) 
        { 
            extension = url.substring(url.lastIndexOf('.') + 1); 
            assert extension.length() == 3; 
        } 
        if (id == null) 
        { 
            this.save(); 
            assert id != null; 
        } 
        try 
        { 
            File full = getLocalPath(getVariantFull()); 
            URL urlO = new URL(url); 
            //lights51.utilities.FileUtils.copy(urlO, full, false); 
            if (originalFilename == null) 
            { 
                originalFilename = urlO.getFile(); 
            } 
            BufferedImage img = ImageIO.read(full); 
            originalWidth = img.getWidth(); 
            originalHeight = img.getHeight(); 
            img = null; 
            urlO = null; 
            full = null; 
        } 
        catch (IOException ex) 
        { 
            ex.printStackTrace(); 
            //new Log(ex).save(); 
            this.delete(); 
            return; 
        } 
        for (VariantImpl v : getVariants()) 
        { 
            if (v != getVariantFull()) 
            { 
                generateVariant(v); 
            } 
        } 
    } 
    /** 
     * Delete <code>variant</code> from the image bucket on S3, and from the local file system. 
     * 
     * @param variant 
     */ 
    protected void deleteVariant(VariantImpl variant) 
    { 
        getLocalPath(variant).delete(); 
        // @todo 
        // S3.deleteObject(AWS_BUCKET_NAME, getS3Path(variant)); 
    } 
    @PrePersist 
    private void prePersist() 
    { 
        if (createdAt == null) 
        { 
            createdAt = new Date(); 
        } 
    } 
    @PostRemove 
    private void postDelete() 
    { 
        for (VariantImpl v : getVariants()) 
        { 
            deleteVariant(v); 
        } 
    } 
    public String getOriginalFilename() 
    { 
        return originalFilename; 
    } 
    public int getOriginalWidth() 
    { 
        return originalWidth; 
    } 
    public int getOriginalHeight() 
    { 
        return originalHeight; 
    } 
    public String getExtension() 
    { 
        return extension; 
    } 
    public Date getCreatedAt() 
    { 
        return createdAt; 
    } 
    /** 
     * Generates the specified variant of this image and uploads it to Amazon S3 storage. 
     * @param v Should not be the FULL variant. 
     */ 
    public void generateVariant(VariantImpl v) 
    { 
        int variantWidth = v.getMaxWidth(); 
        int variantHeight = v.getMaxHeight(); 
        File full = getLocalPath(getVariantFull()); 
        File target = getLocalPath(v); 
        if (target.exists() && play.Play.mode == play.Play.Mode.DEV) 
        { 
            return; 
        } 
        // Square images are cropped, so that the dimensions aren't scewed. 
        if (variantWidth == variantHeight && variantWidth > 0) 
        { 
            int x1, y1, x2, y2; 
            if (originalWidth > originalHeight) 
            { 
                y1 = 0; 
                y2 = originalHeight; 
                int removeFromEachXSide = (originalWidth - originalHeight) / 2; 
                x1 = removeFromEachXSide; 
                x2 = originalWidth - removeFromEachXSide; 
            } 
            else 
            { 
                x1 = 0; 
                x2 = originalWidth; 
                int removeFromEachYSide = (originalHeight - originalWidth) / 2;
                y1 = removeFromEachYSide; 
                y2 = originalHeight - removeFromEachYSide; 
            } 
            play.libs.Images.crop(full, target, x1, y1, x2, y2); 
            play.libs.Images.resize(target, target, variantWidth, variantHeight); 
        } 
        else if ((variantWidth > 0) && (originalWidth >= variantWidth)) 
        { 
            int newHeight = (int) (originalHeight * ((double) variantWidth /(double) originalWidth)); 
            play.libs.Images.resize(full, target, variantWidth, newHeight); 
        } 
        else if ((variantHeight > 0) && (originalHeight >= variantHeight)) 
        { 
            int newWidth = (int) (originalWidth * ((double) variantHeight / (double) originalHeight)); 
            play.libs.Images.resize(full, target, originalWidth, newWidth); 
        } 
        // Resize to make the image bigger. 
        else if ((variantWidth > 0) && (originalWidth < variantWidth)) 
        { 
            play.libs.Images.resize(full, target, variantWidth, -1); 
        } 
        // Resize to make the image bigger. 
        else if ((variantHeight > 0) && (originalHeight < variantHeight)) 
        { 
            play.libs.Images.resize(full, target, -1, variantHeight); 
        } 
        else 
        { 
            throw new RuntimeException("Unexpected resize: " 
                + originalWidth + ", " + originalHeight + ", " + variantWidth + ", " + variantHeight); 
        } 
        // @todo 
        // return S3.putObjectFile($getLocalPath(variant), AWS_BUCKET_NAME,getS3Path(variant), S3.ACL_PUBLIC_READ); 
    } 
}