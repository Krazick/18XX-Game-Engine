package ge18xx.map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CubeTests {

	@BeforeEach
	void setUp () throws Exception {
	}

	@Test
	@DisplayName ("Map Cube Tests")
	void mapCubeTests () {
		Cube tCube1;
		Cube tCube2;
		Axial tAxial1;
		Axial tAxial2;
		
		tAxial1 = new Axial (7, 1);
		tAxial2 = new Axial (7, 2);
		tCube1 = new Cube (tAxial1);
		tCube2 = new Cube (tAxial2);
		System.out.println ("Axial/Cube 1: " + tAxial1.getCoordinates () + " | " + tCube1.getCoordinates ());
		System.out.println ("Axial/Cube 2: " + tAxial2.getCoordinates () + " | " + tCube2.getCoordinates ());
		System.out.println (tCube1.cubeDistance (tCube2));
	}

}
