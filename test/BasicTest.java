import org.junit.*;

import java.util.*;
import play.test.*;
import models.*;
import models.User.UserRole;

public class BasicTest extends UnitTest {

	public User user ;
	
	
	@Before
	public void setup(){
        Fixtures.loadYaml("initial-data.yml");
        //retrieve admin user
		user.findById(new Long(1));
	}
	
	@Test
	public void testCreateOrUpdateOrDeleteGame() {

		//Create
		Game game = new Game("MyGame", "X360", "My Description game", true,
				"Test of the game", "My Studio", "My Editor", new Date(), 8,
				"public/images/x360/my_game/cover/mygame-cover.jpg", 
				user);
		game.save();

		//Retrieve technical Id
		Long id = game.getId();
		assertNotNull("game is not created", id);

		// Update
		game = Game.findById(id);
		game.title="MyNewGame";
		game.save();

		//Retrieve and verify update
		Game game2 = Game.findById(id);
		assertSame("Game was not correctly updated", game2.title, game.title);

		game.delete();

		game = Game.findById(id);
        assertNull("Game was not deleted",game);
	}
	
}
