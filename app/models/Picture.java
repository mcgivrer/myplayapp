/**
 * 
 */
package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;

/**
 * @author frederic
 *
 */
@Entity
public class Picture extends Model {
	
	@MaxSize(value=255)
	public String title;
	
	@Required
	public String image;
	
	@Required
	public String type;
	
	@Required
	@ManyToOne
	public Game game;

	/**
	 * @param title
	 * @param image
	 * @param type
	 * @param game
	 */
	public Picture(String title, String image, String type, Game game) {
		this.title = title;
		this.image = image;
		this.type = type;
		this.game = game;
	}
}
