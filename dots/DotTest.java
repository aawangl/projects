import static org.junit.Assert.*;

import org.junit.Test;


public class DotTest {

	@Test
	public void testConstructor() {
		Dot d = new Dot();
		Dot q = new Dot();
		Dot w = new Dot();
		Dot e = new Dot();
		Dot a = new Dot(1);
		Dot b = new Dot(2);
		
		assertEquals(a.getColor(), 1);
		assertEquals(b.getColor(), 2);
		assertTrue(d.getColor() >= 1 && d.getColor() <= 5);
		assertTrue(q.getColor() >= 1 && q.getColor() <= 5);
		assertTrue(w.getColor() >= 1 && w.getColor() <= 5);
		assertTrue(e.getColor() >= 1 && e.getColor() <= 5);
	}
	
	@Test
	public void testIsSameColor() {
		Dot d = new Dot(2);
		Dot a = new Dot(2);
		Dot b = new Dot(3);
		
		assertTrue(d.isSameColor(a));
		assertFalse(d.isSameColor(b));
	}

}
