package net.tentrup.einsatzserver.util;

import org.junit.Assert;
import org.junit.Test;

public class TemplateTest {

	@Test
	public void testApply() {
		Template t1 = new Template("Hast Du Lust, am {$Datum} Dienst bei {$Bezeichnung} in {$Ort} zu machen?");
		String text1 = t1.apply("Beschreibung", "Einsatzort", "04.03.2014");
		Assert.assertEquals("Hast Du Lust, am 04.03.2014 Dienst bei Beschreibung in Einsatzort zu machen?", text1);
		Template t2 = new Template("Hast Du Lust, am {$} Dienst bei {$Bezeichnung} in {$Ort} zu machen?");
		String text2 = t2.apply("Beschreibung", "Einsatzort", "04.03.2014");
		Assert.assertEquals("Hast Du Lust, am {$} Dienst bei Beschreibung in Einsatzort zu machen?", text2);
	}
}
