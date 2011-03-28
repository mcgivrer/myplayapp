package shared;


import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

import play.Play;
import play.db.Model.BinaryField;
import play.exceptions.UnexpectedException;
import play.libs.Codec;
import play.libs.IO;

/**
 * Picture management for Play! Models attributes.
 * 
 * @author frederic
 * 
 */
public class Picture<T> implements BinaryField,UserType, IPicture{
	/**
	 * Unic Id
	 */
	private String UUID;
	/**
	 * Type of data
	 */
	private String type;
	/**
	 * File attachement
	 */
	private Map<String, File> files;

	/**
	 * Default constructor.
	 */
	public Picture() {
	}

    public String[] setThumbnailsSize(){
    	return new String[]{"micro-60x80","mini-90x120","medium-120x190","paysage-320x80"};
    }
    public String setAttachmentPath(){
    	return "data";
    }
	
	/**
	 * Parameterized Constructor.
	 * 
	 * @param UUID
	 * @param type
	 */
	private Picture(String UUID, String type) {
		this.UUID = UUID;
		this.type = type;
	}

	/**
	 * @see play.db.Model.BinaryField.get()
	 */
	public InputStream get() {
		return get("");
	}

	/**
	 * Specific implementation for Thumbnailing picture.
	 * 
	 * @param size
	 * @return
	 */
	public InputStream get(String size) {
		if (exists(size)) {
			try {
				return new FileInputStream(getFile(size));
			} catch (Exception e) {
				throw new UnexpectedException(e);
			}
		}
		return null;
	}

	/**
	 * Set InputStream to the file system.
	 */
	public void set(InputStream is, String type) {
		this.UUID = Codec.UUID();
		this.type = type;
		IO.write(is, getFile(""));
		try {
			generateThumbnails();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * generate Thumbnails on size defined.
	 * 
	 * @throws Exception
	 */
	public void generateThumbnails() throws Exception {
		String size;
		ThumbnailGenerator thb = new ThumbnailGenerator();
		// Generate Thumbnails
		for (int i = 0; i < setThumbnailsSize().length; i++) {
			size = setThumbnailsSize()[i];
			String sizeName = size.split("-")[0];
			thb.create(getFile(sizeName), getFile(""), size);
		}
	}

	public long length() {
		return getFile("").length();
	}

	public String type() {
		return type;
	}

	public boolean exists() {
		return UUID != null && getFile("").exists();
	}

	/**
	 * Specific implementation to manage Thumbnail size.
	 * 
	 * @param size
	 * @return
	 */
	public boolean exists(String size) {
		return UUID != null && getFile(size).exists();
	}

	/**
	 * Return file for size qualified attachment.
	 * 
	 * @return
	 */
	public File getFile(String size) {
		if (files == null) {
			files = new HashMap<String, File>();
		}
		if (files.get(size) == null) {
			files.put(size, new File(getStore(this.setAttachmentPath()
					+ File.separator), UUID
					+ (size != null && size != "" ? "." + size : "") + ".jpg"));
		}
		return files.get(size);
	}

	public int[] sqlTypes() {
		return new int[] { Types.VARCHAR };
	}

	public Class returnedClass() {
		
		try {
			return Class.forName(this.getClass().getName());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public boolean equals(Object o, Object o1) throws HibernateException {
		return o == null ? false : o.equals(o1);
	}

	public int hashCode(Object o) throws HibernateException {
		return o.hashCode();
	}

	// TODO: After we switch to Hibernate 3.6, Hibernate.STRING must be changed
	// to
	// Hibernate.StringType.INSTANCE (how stupid is that to deprecate stuff
	// before offering
	// an alternative?
	@SuppressWarnings("deprecation")
	public Object nullSafeGet(ResultSet rs, String[] names, Object o)
			throws HibernateException, SQLException {
		String val = (String) Hibernate.STRING.nullSafeGet(rs, names[0]);
		if (val == null || val.length() == 0 || !val.contains("|")) {
			return new Picture<T>();
		}
		return new Picture<T>(val.split("[|]")[0], val.split("[|]")[1]);
	}

	public void nullSafeSet(PreparedStatement ps, Object o, int i)
			throws HibernateException, SQLException {
		if (o != null) {
			ps.setString(i, ((Picture<T>) o).UUID + "|" + ((Picture<T>) o).type);
		} else {
			ps.setNull(i, Types.VARCHAR);
		}
	}

	public Object deepCopy(Object o) throws HibernateException {
		if (o == null) {
			return null;
		}
		return new Picture<T>(this.UUID, this.type);
	}

	public boolean isMutable() {
		return true;
	}

	public Serializable disassemble(Object o) throws HibernateException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Object assemble(Serializable srlzbl, Object o)
			throws HibernateException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Object replace(Object o, Object o1, Object o2)
			throws HibernateException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	//

	public static String getUUID(String dbValue) {
		return dbValue.split("[|]")[0];
	}

	public static File getStore(String path) {
		String name = Play.configuration.getProperty("attachments.path",
				"attachments");
		File store = null;
		if (new File(name).isAbsolute()) {
			store = new File(name + File.separator + path);
		} else {
			store = Play.getFile(name + File.separator + path);
		}
		if (!store.exists()) {
			store.mkdirs();
		}
		return store;
	}

}
