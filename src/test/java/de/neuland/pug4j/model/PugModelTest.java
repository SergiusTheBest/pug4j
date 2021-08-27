package de.neuland.pug4j.model;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class PugModelTest {

	private PugModel model;

	@Before
	public void setup() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("hello", "world");
		map.put("foo", "bar");
		model = new PugModel(map);
	}

	@Test
	public void scope() throws Exception {
		assertEquals("world", model.get("hello"));
		model.pushScope();
		model.putLocal("hello", "new world");
		assertEquals("new world", model.get("hello"));
		model.popScope();
		assertEquals("world", model.get("hello"));
	}
	
	@Test
	public void defaults() throws Exception {
		Map<String, Object> defaults = new HashMap<String, Object>();
		defaults.put("hello", "world");

		model = new PugModel(defaults);
		model.put("new", true);
		
		assertFalse(defaults.containsKey("new"));
		assertTrue(model.containsKey("new"));
		assertEquals(model.get("hello"), "world");
	}
}
