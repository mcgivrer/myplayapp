import models.Game;

import org.junit.Test;

import play.test.UnitTest;

/**
 * Classe de test de la persistence des entités de l'application.
 */

/**
 * @author frederic
 * 
 */
public class EntitiesTest extends UnitTest {

	@Test
	public void testGame() {
		Game game = new Game("MyGame", "X360", "My Description game", true,
				"Test of the game", "My Studio", "My Editor", 2010, 8,
				"public/images/x360/my_game/cover/mygame-cover.jpg");
		game.save();

		Long id = game.getId();
		assertNotNull(id);
	}

}
