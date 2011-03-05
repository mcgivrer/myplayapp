package ext;

import java.io.StringWriter;

import jj.play.org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import jj.play.org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder;
import jj.play.org.eclipse.mylyn.wikitext.textile.core.TextileLanguage;
import play.templates.JavaExtensions;

public class TextileExtension extends JavaExtensions {

	public static String textile(String text){
		StringWriter writer = new StringWriter();
		HtmlDocumentBuilder builder = new HtmlDocumentBuilder(writer);
		// Empêche la génération des balises html et body.
		builder.setEmitAsDocument(false);

		// Création du parser Textile
		MarkupParser parser = new MarkupParser(new TextileLanguage());
		parser.setBuilder(builder);
		parser.parse(text);

		return writer.toString();

	}
}