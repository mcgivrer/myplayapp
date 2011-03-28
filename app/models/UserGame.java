/**
 * Project: myplayapp
 * Objet: Jeu pour un utilisateur.
 */
package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;

/**
 * Item d'une liste (<code>GameList</code>) pour l'utilisateur (<code>user</code>).
 * L'item UserGame apporte en plus des informations du jeu (<code>Game</code>), 
 * une note attribuée par l'utilisateur détenteur de ce jeu ainsi qu'un commentaire de
 * ce même utilisateur.
 * @see app.models.User
 * @see app.models.GameList
 * @author frederic
 */
@Entity
public class UserGame extends Model {
    /**
     * Utilisateur détenteur de ce jeux
     */
	@Required
	public User user;

    /**
     * Jeu possédé par l'utilisateur.
     */
	@Required
	public Game game;
	
	/**
	 * Note attribuée par l'utilisateur
	 */
	@Required
	public Integer note;
	
	/**
	 * Commentaire de l'utilisateur (user) user sur ce jeu (game).
	 */
	@Required
	@Lob
	public String comment;

	/**
	 * Constructeur par défaut pour l'item de liste.
	 * @param user
	 * @param game
	 * @param note
	 * @param comment
	 */
	public UserGame(User user, Game game, Integer note, String comment) {
		this.user = user;
		this.game = game;
		this.note = note;
		this.comment = comment;
	}
}
