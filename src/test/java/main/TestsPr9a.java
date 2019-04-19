package main;

import static org.junit.Assert.assertEquals;

import org.dbunit.dataset.IDataSet;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import main.java.model.ExcepcionDeAplicacion;
import sol.GestorBD;
import util.TestsUtil;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestsPr9a extends TestsUtil {

	@BeforeClass
	public static void creacionGestorBD() {
		gbd = new GestorBD();
		url = GestorBD.getPropiedad("url");
		user = GestorBD.getPropiedad("user");
		password = GestorBD.getPropiedad("password");
		schema = GestorBD.getPropiedad("schema");
	}

	// Antes de ejecutar cada test, eliminamos el estado previo de la BD, eliminando
	// los registros insertados en el test previo y cargando los datos requeridos
	// para dicho test.
	@Before
	public void importDataSet() throws Exception {
		IDataSet dataSet = readDataSet();
		cleanlyInsertDataset(dataSet);
	}

	@Test
	public void testGetFincasPorCiudad() throws ExcepcionDeAplicacion {
		try {
			// Obtenemos el numero de fincas en Zaragoza
			int numFincas= gbd.fincasPorCiudad("Zaragoza");
			// Comprobamos que coincide con el numero esperado
			assertEquals("Falla al comprobar el numero de fincas", 6, numFincas);
		} catch (ExcepcionDeAplicacion e) {
			e.printStackTrace();
		}
	}

	

}
