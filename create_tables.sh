export ORACLE_HOME=/u01/app/oracle/product/11.2.0/xe; 
export ORACLE_SID=XE; 
$ORACLE_HOME/bin/sqlplus -S travis/travis <<SQL
whenever sqlerror exit 2;
CREATE SCHEMA AUTHORIZATION travis; 
ALTER SESSION SET CURRENT_SCHEMA = travis;


create table sucursal (
  id_sucursal   varchar2(6)      primary key,
  direccion     varchar2(20) not null,
  ciudad        varchar2(20) not null,
  codigo_postal char(5)
);

create table empleado (
  id_empleado        varchar2(6)      primary key,
  nombre_empleado    varchar2(30) not null,
  apellidos_empleado varchar2(60) not null,
  trabajo            varchar2(30) check (trabajo in ('director','comercial')),
  sexo               char(1)      check (sexo in ('h','m')),
  fecha_nacimiento   date,
  salario            number       not null,
  sucursal           varchar2(6)      constraint fk_sucursal references sucursal( id_sucursal)
);
	
create table propietario (
  id_propietario        varchar2(6)      primary key,
  nombre_propietario    varchar2(30) not null,
  apellidos_propietario varchar2(60) not null,
  direccion             varchar2(60) not null,
  telefono              char(9)      not null
);
	
create table finca (
  id_finca      varchar2(10) primary key,
  direccion     varchar2(30) not null,
  ciudad        varchar2(30) not null,
  codigo_postal char(5),
  tipo          varchar2(15) check (tipo in ('piso', 'unifamiliar', 'finca rural')) ,
  habitaciones  integer,
  banios         integer,
  calefaccion   varchar2(40),
  ascensor      char(2)      check (ascensor in ('Y', 'N')),
  alquiler      number(6,2)       not null,
  propietario   varchar2(6)      not null references propietario(id_propietario)
);
	
create table cliente (
  id_cliente        varchar2(6)      primary key,
  nombre_cliente    varchar2(30) not null,
  apellidos_cliente varchar2(60) not null,
  telefono          char(9)      not null,
  preferencia       varchar2(11) check (preferencia in ('piso', 'unifamiliar', 'finca rural')),
  presupuesto       number       not null
);
	
create table visita (
  id_cliente   varchar2(6)      references cliente(id_cliente) not null,
  id_finca     varchar2(10) references finca(id_finca) not null,
  id_empleado  varchar2(6)      references empleado(id_empleado) not null,
  fecha_visita date         not null,
  comentarios varchar2(250)
);

alter table Visita ADD constraint pk_visitas primary key(id_cliente,id_empleado,id_finca, fecha_visita);
	
create table captacion (
  id_finca        varchar2(10) references finca(id_finca),
  id_empleado     varchar2(6)      references empleado(id_empleado),
  fecha_captacion date         not null
);
alter table captacion ADD constraint pk_captaciones primary key(id_finca,id_empleado,fecha_captacion);

CREATE OR REPLACE FUNCTION numeroFincasEnCiudad
(ciudad in  VARCHAR2) RETURN NUMBER
is
  n NUMBER(7);
  numero NUMBER(7);
  ciudad2  varchar2(50) := ciudad;
  cursor c1 is
    select count(*) from finca where ciudad=ciudad2;
BEGIN
   open c1;
   fetch c1 into n;
  return n;
END;
/


SQL
