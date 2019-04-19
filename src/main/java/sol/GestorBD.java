package main.java.sol;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Properties;

import main.java.debug.Debug;
import main.java.model.Cliente;
import main.java.model.Empleado;
import main.java.model.ExcepcionDeAplicacion;
import main.java.model.Finca;
import main.java.model.Propietario;
import main.java.model.Sucursal;
import main.java.pers.Persistencia;

public class GestorBD implements Persistencia {
//  // Registro del driver apropiado para la BD a utilizar
//  static {
//    try {
//      Class.forName("oracle.jdbc.driver.OracleDriver");
//    } catch (ClassNotFoundException e) {
//      System.out.println("No puedo cargar el driver JDBC de la BD");
//    }
//  }
	private static final String URL = getPropiedad("url");;
	private static final String USR = getPropiedad("user");
	private static final String PWD = getPropiedad("password");


  public static String getPropiedad(String clave){
		 String valor=null;
	    try {
	      Properties props = new Properties();
	      props.load(new FileInputStream("src/conexion.properties"));
	   
	       valor= props.getProperty(clave);
	      
	    } catch (IOException ex) {
	      ex.printStackTrace();
	    }
	    return valor;
	    }

  public Finca getFinca(String id) throws ExcepcionDeAplicacion {
    return new Finca("a134", "Jorge Vigón, 3", "Logroño", "26009", "piso", 3, 3, "si", true,
                     new Propietario("p325", "Angel", "Abad", "Gran Via, 3", "941232323"), 45.6);
  }

  public Empleado getEmpleado(String id) throws ExcepcionDeAplicacion {
    return new Empleado("q12e", "Pepe", "Perez", "director", 'V', 1897.88,
                        java.util.Calendar.getInstance(),
                        new Sucursal("e34", "Gran Via, 8", "Logroño", "26005"));
  }

  public List<Finca> buscaFincas(double precioMin, double precioMax) throws ExcepcionDeAplicacion {
    List<Finca> l = new java.util.ArrayList<Finca>();
    l.add(new Finca("a134", "Jorge Vigón, 3", "Logroño", "26009", "piso", 3, 3, "si", true,
                    new Propietario("p325", "Angel", "Abad", "Gran Via, 3", "941232323"), 45.6));
    return l;
  }

  public int incrementarSueldo(float porcentaje) throws ExcepcionDeAplicacion {
    return 0;
  }

  public void eliminarCliente(String id) throws ExcepcionDeAplicacion {
  }
/*
  public int aniadirClientes(List clientes) throws model.ExcepcionDeAplicacion {
    return 0;
  }*/

  public int eliminarEmpleados(List<String> empleados) throws ExcepcionDeAplicacion {
    return 0;
  }

  public void aniadirEmpleado(Empleado empleado) throws ExcepcionDeAplicacion {
  }

  /**
   * Devuelve el número de fincas que hay en una ciudad.
   * @param ciudad String con el nombre de la ciudad
   * @return int numero de fincas en la ciudad
   */
  public int fincasPorCiudad(String ciudad) throws ExcepcionDeAplicacion {
    Connection con = null;
    try {
      // Establecimiento de la conexion
      con = DriverManager.getConnection(URL,USR,PWD);
      Debug.prec(con);

      CallableStatement call = con.prepareCall("{?=call numFincasEnCiudad(?)}");
      call.registerOutParameter(1, Types.INTEGER);
      call.setString(2, ciudad);
      call.execute();
      int n = call.getInt(1);
      call.close();

      return n;
    } catch (SQLException e) {
      e.printStackTrace();
      throw new ExcepcionDeAplicacion(e);
    } finally {
      try {
        if (con != null) {
          con.close();
        }
      } catch (SQLException ex) {
        ex.printStackTrace();
        throw new ExcepcionDeAplicacion(ex);
      }
    }

  }

  /**
   * Devuelve un objeto de tipo cliente cuyo telefono coincidad con el suministrado
   * como parámetro.
   * @param tfno String tfno del cliente a buscar
   * @return Cliente el cliente ue tenga el tfno o null si no hay ninguno con
   *                 ese tfno
   */
  public Cliente clienteDeTfno(String tfno) throws ExcepcionDeAplicacion {
//    CREATE OR REPLACE PROCEDURE clienteTfno(tfno VARCHAR2, id OUT VARCHAR2, nombre OUT VARCHAR2,
//                                        apellidos OUT VARCHAR2, preferencia OUT VARCHAR2,
//                                        presupuesto OUT NUMBER)
    Cliente c = null;
    Connection con = null;
    try {
      // Establecimiento de la conexion
      con = DriverManager.getConnection("jdbc:oracle:thin:@fran:1521:fgi", "prbd", "prbdprbd");
      Debug.prec(con);
      CallableStatement call = con.prepareCall("{call clienteTfno(?,?,?,?,?,?)}");
      call.registerOutParameter(2, Types.VARCHAR);
      call.registerOutParameter(3, Types.VARCHAR);
      call.registerOutParameter(4, Types.VARCHAR);
      call.registerOutParameter(5, Types.VARCHAR);
      call.registerOutParameter(6, Types.DOUBLE);
      call.setString(1, tfno);
      call.execute();
      String id = call.getString(2);
      if (id != null) {
        c = new Cliente(id, call.getString(3), call.getString(4), tfno, call.getString(5),
                        call.getDouble(6));
      }
      call.close();
    } catch (SQLException e) {
      e.printStackTrace();
      throw new ExcepcionDeAplicacion(e);
    } finally {
      try {
        if (con != null) {
          con.close();
        }
      } catch (SQLException ex) {
        ex.printStackTrace();
        throw new ExcepcionDeAplicacion(ex);
      }
    }
    return c;
  }

  public void incrementarAlquiler(float porcentaje) throws ExcepcionDeAplicacion {
    Connection con = null;
    try {
      // Establecimiento de la conexion
      con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:pbd", "prbd", "prbdprbd");
      Debug.prec(con);
      con.setAutoCommit(false);
      CallableStatement call = con.prepareCall("{call subirAlquiler(?)}");
      call.setFloat(1, porcentaje);
      call.execute();
      con.commit();
      call.close();
    } catch (SQLException e) {
      try {
        if (con != null) {
          con.rollback();
        }
      } catch (SQLException ex) {
        ex.printStackTrace();
        throw new ExcepcionDeAplicacion(ex);
      }
      e.printStackTrace();
      throw new ExcepcionDeAplicacion(e);
    } finally {
      try {
        if (con != null) {
          con.close();
        }
      } catch (SQLException ex) {
        ex.printStackTrace();
        throw new ExcepcionDeAplicacion(ex);
      }
    }
  }

// Estos son metodos para probar la devolución de ARRAYs. Pero el de Strings no funciona
// Devuelve una cosas raras.

//  public List fincasEnPresupuesto(String idCliente) throws model.ExcepcionDeAplicacion {
//    Connection con = null;
//    try {
//      // Establecimiento de la conexion
//      con = DriverManager.getConnection("jdbc:oracle:thin:@fran:1521:fgi","prbd","prbdprbd");
//      Debug.prec(con);
//
//      CallableStatement call = con.prepareCall("{call fincasAlquilables(?,?)}");
//      call.registerOutParameter(2, Types.ARRAY, "STRING_ARRAY");
//  //    OracleCallableStatement call = (OracleCallableStatement)con.prepareCall("{call fincasAlquilables(?,?)}");
//  //    call.registerOutParameter(2, OracleTypes.ARRAY, "STRING_ARRAY");
//      call.setString(1, idCliente);
//      call.execute();
//      java.sql.Array a = call.getArray(2);
//
//  //    ARRAY a = call.getARRAY(2);
//  //    System.out.println(a.getSQLTypeName());
//
//      java.sql.ResultSet rs = a.getResultSet();
//      while (rs.next()) {
//        System.out.println(rs.getInt(1));
//        System.out.println(rs.getString(2));
//      }
//  //      String[] ids = (String[]) a.getArray();
//  //      for (int i = 0; i < ids.length; i++) {
//  //        System.out.println(ids[i]);
//  //      }
//      call.close();
//    } catch (SQLException e) {
//      e.printStackTrace();
//      throw new ExcepcionDeAplicacion(e);
//    } finally {
//      try {
//        if (con != null) con.close();
//      } catch (SQLException ex) {
//        ex.printStackTrace();
//        throw new ExcepcionDeAplicacion(ex);
//      }
//    }
//
//    return null;
//  }

//  public List alquileres(String idCliente) throws model.ExcepcionDeAplicacion {
//    Connection con = null;
//    try {
//      // Establecimiento de la conexion
//      con = DriverManager.getConnection("jdbc:oracle:thin:@fran:1521:fgi","prbd","prbdprbd");
//      Debug.prec(con);
//
//      ArrayDescriptor descriptor = ArrayDescriptor.createDescriptor("NUMBER_ARRAY", con);
//
//      CallableStatement call = con.prepareCall("{call alquileres(?,?)}");
//      call.registerOutParameter(2, Types.ARRAY, "NUMBER_ARRAY");
//  //    OracleCallableStatement call = (OracleCallableStatement)con.prepareCall("{call alquileres(?,?)}");
//  //    call.registerOutParameter(2, OracleTypes.ARRAY, "NUMBER_ARRAY");
//      call.setString(1, idCliente);
//      call.execute();
//      java.sql.Array a = call.getArray(2);
//  //    ARRAY a = call.getARRAY(2);
//  //    System.out.println(a.getSQLTypeName());
//
//      java.sql.ResultSet rs = a.getResultSet();
//      while (rs.next()) {
//        System.out.print(rs.getInt(1)+" ");
//        System.out.println(rs.getString(2));
//      }
//  //      String[] ids = (String[]) a.getArray();
//  //      for (int i = 0; i < ids.length; i++) {
//  //        System.out.println(ids[i]);
//  //      }
//      call.close();
//    } catch (SQLException e) {
//      e.printStackTrace();
//      throw new ExcepcionDeAplicacion(e);
//    } finally {
//      try {
//        if (con != null) con.close();
//      } catch (SQLException ex) {
//        ex.printStackTrace();
//        throw new ExcepcionDeAplicacion(ex);
//      }
//    }
//
//    return null;
//  }


  public static void main(String[] args) {
    try {
//      bd.GestorBD.eliminarCliente("JCRC01");
//      bd.GestorBD.eliminarCliente("MPAF01");
//      bd.GestorBD.aniadirEmpleado(new Empleado("AAba", "Alberto",
//                                               "Abad Antiguo", "Comercial", 'h', 1500,
//                                               new java.util.GregorianCalendar(1975, 7, 24),
//                                               new Sucursal("BrZa01", "Bretón 4", "Zaragoza",
//                                                            "50009")));
//      bd.GestorBD.aniadirEmpleado(new Empleado("Salv01", "Sonia",
//                                               "Alvarez Garcia", "Director",
//                                               'm', 2300,
//                                               new java.util.GregorianCalendar(1968, 3, 1),
//                                               new Sucursal("JoLo01", "Jorge Vigón 68",
      //                                                            "Logroño", "26004")));

      System.out.println(new GestorBD().fincasPorCiudad("Zaragoza"));
//      System.out.println(bd.GestorBD.fincasPorCiudad("Logroño"));
//      bd.GestorBD.incrementarAlquiler(10);
//      Cliente c = bd.GestorBD.clienteDeTfno("976123456");
//      System.out.println(c);
//      c = bd.GestorBD.clienteDeTfno("976123411");
//      //      System.out.println(c);

    } catch (ExcepcionDeAplicacion ex) {
      ex.printStackTrace();
    }

  }

}
