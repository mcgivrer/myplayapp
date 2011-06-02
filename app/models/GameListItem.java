/**
 * Project: myplayapp
 * Objet: Jeu pour un utilisateur.
 */
package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import javax.persistence.CascadeType;

import play.data.validation.Required;
import play.db.jpa.Model;

/**
 * Item d'une liste (<code>GameList</code>) pour l'utilisateur (<code>user</code>).
 * L'item UserGame apporte en plus des informations du jeu (<code>Game</code>), 
 * une rate attribuée par l'utilisateur détenteur de ce jeu ainsi qu'un commentaire de
 * ce même utilisateur.
 * @see app.models.User
 * @see app.models.GameList
 * @author frederic
 */
@Entity
public class GameListItem extends Model {
	/**
	 * Liste d'appartenance de cet item
	 */
	@Required
	@ManyToOne
	public GameList gameList;
    /**
     * Utilisateur détenteur de ce jeux
     */
	@Required
	@ManyToOne
	public User user;

    /**
     * Jeu possédé par l'utilisateur.
     */
	@Required
	@ManyToOne(cascade=CascadeType.REFRESH)
	public Game game;
	
	/**
	 * rate attribuée par l'utilisateur
	 */
	@Required
	public Integer rate;
	
	/**
	 * Commentaire de l'utilisateur (user) user sur ce jeu (game).
	 */
	@OneToMany
	public List<Comment> comments;

	/**
	 * Constructeur par défaut pour l'item de liste.
	 * @param user
	 * @param game
	 * @param rate
	 * @param comment
	 */
	public GameListItem(User user, Game game, Integer rate, List<Comment> comments) {
		this.user = user;
		this.game = game;
		this.rate = rate;
		this.comments = comments;
	}

	/**
 	 * Affichage du titre de la liste.
	 */
	public String toString(){
		return this.user.username + " / " + this.game.title;
	}

}
