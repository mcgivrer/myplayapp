
import models.User;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.test.Fixtures;
 
/**
 * Classe de démarrage de l'application. 
 * 
 * Lors du premier démarrage la méthode
 * <code>doJob</code> vérifie que aucun utilisateur n'est déjà créé. Si c'est le
 * cas, celle-ci charge laconfiguration par défaut contenu dans le fichier Yaml
 * <code>cont/initial-data.yml</code>.
 * 
 * @author Frédéric Delorme<frederic.delorme@gmail.com>
 *
 */
@OnApplicationStart
public class Bootstrap extends Job { 
	/* (non-Javadoc)
	 * @see play.jobs.Job#doJob()
	 */
	public void doJob() {
		// Check if the database is empty
		if(User.count() == 0) {
			Fixtures.deleteDatabase();
			Fixtures.loadModels("initial-data.yml");
		}
	}
 
}
