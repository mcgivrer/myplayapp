/**
 * 
 */
package shared;

import models.User;

/**
 * Avatar class modelisation
 * @author frederic
 * 
 */
public class UserPicture extends Picture<User> implements IPicture {

	/*
	 * (non-Javadoc)
	 * 
	 * @see shared.IPicture#setAttachmentPath()
	 */
	@Override
	public String[] setThumbnailsSize() {
		return new String[] { "micro-60x80", "mini-90x120", "medium-120x190",
				"paysage-320x80" };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see shared.IPicture#setAttachmentPath()
	 */
	@Override
	public String setAttachmentPath() {
		return "users";
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see shared.IPicture#returnedClass()
	 */
	@Override
	public Class returnedClass() {
		return UserPicture.class;
	}

	
}
