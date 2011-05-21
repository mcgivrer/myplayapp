/**
 * Project: myplayapp
 * Objet: Liste de jeu d'un utliisateur.'
 */
package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.data.validation.Required;
import play.db.jpa.Model;

/**
 * Liste de jeux pour un utilisateur connecté, proposant un titre (<code>title</code>),
 * une <code>description</code>, l'utilisateur propriétaire (<code>user</code>) ainsi 
 * que la liste des jeux de l'utilsateur (<code>games</code>).
 * A noter que les éléments de la liste ne sont pas des entités <code>Game</code> mais
 * des <code>GameListItem</code>, plus complet et orienté utilisateur.
 * @author frederic
 * @see app.models.UserGame
 * @see app.models.Game
 * @see app.models.User
 */
@Entity
public class GameList extends Model {

	/**
	 * nom de la liste de jeux
	 */
	@Required
	public String title;

	/**
	 * description pour cette liste de jeux
	 */
	@Required
	@Lob
	public String description;

	/**
	 * Utilisateur propriétaire de la liste
	 */
	@Required
	@ManyToOne
	public User user;

	/**
	 * Liste des jeux (GameListItem) éléments.
	 */
	@OneToMany(mappedBy="gameList", cascade=CascadeType.ALL)
	public List<GameListItem> games;
	
	/**
	 * Constructeur par défaut d'une GameList.
	 * @param title
	 * @param description
	 * @param user
	 * @param games
	 */
	public GameList(String title, String description, User user, List<GameListItem> games) {
		this.title = title;
		this.description = description;
		this.user = user;
		this.games = games;
	}

	/**
 	 * Affichage du titre de la liste.
	 */
	public String toString(){
		return this.title + " - "+games.size()+" game(s)";
	}
}
