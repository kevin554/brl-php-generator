package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class TestGenerator {

    public static final String DB_NAME = "pruebaphp";

    public static void main(String[] args) {
        final ArrayList<Table> tables = new ArrayList<>();
        tables.add(new Table("personas", "id", "nombres", "apellidos"));

        /* packages */
        createDirectories("dao/", "dao/bll/", "dao/dal", "dao/dto");

        /* "dto classes" */
        for (Table table : tables) {
            try (BufferedWriter bw = createFile("dao/dto/" + table.getName() + ".php")) {
                String s = "<?php\n"
                        + "\n"
                        + "class " + table.getName() + " {\n"
                        + "\n";

                /* attrs */
                ArrayList<String> columns = table.getColumns();
                for (int i = 0; i < columns.size(); i++) {
                    s += "    public $" + columns.get(i) + ";\n";
                }
                
                s += "\n";

                /* getters */
                for (int i = 0; i < columns.size(); i++) {
                    s = s.concat("    function get" + firstUppercased(columns.get(i)) + "() {\n");
                    s = s.concat("        return $this->" + columns.get(i) + ";\n");
                    s = s.concat("    }\n"
                            + "\n");
                }

                /* setters */
                for (int i = 0; i < columns.size(); i++) {
                    s = s.concat("    function set" + firstUppercased(columns.get(i)) + "($" + columns.get(i) + ") {\n");
                    s = s.concat("        $this->" + columns.get(i) + " = $" + columns.get(i) + ";\n");
                    s = s.concat("    }\n"
                            + "\n");
                }

                s += "}\n";

                bw.write(s);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }

        /* MySQL connection */
        try (BufferedWriter bw = createFile("dao/dal/Connection.php")) {
            String clase = "<?php\n"
                    + "\n"
                    + "class Connection {\n"
                    + "\n"
                    + "    private $server = \"localhost\";\n"
                    + "    private $usr = \"root\";\n"
                    + "    private $pass = \"\";\n"
                    + "    private $db = \"" + DB_NAME + "\";\n"
                    + "    private $connection;\n"
                    + "\n"
                    + "    function getConnection() {\n"
                    + "        if ($this->connection == null) {\n"
                    + "             $this->connection = new PDO(\"mysql:host=$this->server;dbname=$this->db;charset=utf8\", $this->usr, $this->pass);\n"
                    + "             $this->connection->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);\n"
                    + "             $this->connection->setAttribute(PDO::ATTR_EMULATE_PREPARES, false);\n"
                    + "        }\n"
                    + "\n"
                    + "        return $this->connection;\n"
                    + "    }\n"
                    + "\n"
                    + "    function query($csql) {\n"
                    + "        return $this->getConnection()->query($csql);\n"
                    + "    }\n"
                    + "\n"
                    + "    function queryWithParams($csql, $paramArray) {\n"
                    + "        $q = $this->getConnection()->prepare($csql);\n"
                    + "        $q->execute($paramArray);\n"
                    + "\n"
                    + "        return $q;\n"
                    + "    }\n"
                    + "\n"
                    + "    function getLastInsertedId() {\n"
                    + "        return $this->getConnection()->lastInsertId();\n"
                    + "    }\n"
                    + "\n"
                    + "}\n";

            bw.write(clase);
        } catch (Exception ex) {
            System.out.println(ex);
        }

        /* bll classes */
        for (Table table : tables) {
            try (BufferedWriter bw = createFile("dao/bll/" + table.getName()+ "BLL.php")) {
                String s = "<?php\n"
                        + "\n"
                        + "class " + table.getName() + "BLL {\n"
                        + "\n"
                        + "    private $tableName = \"" + table.getName() + "\";\n"
                        + "\n"
                        + "    public function insert(" + table.concatenatedColumns("$") + ") {\n"
                        + "        $objConexion = new Connection();\n"
                        + "        $objConexion->queryWithParams(\"insert into $this->tableName (" + table.concatenatedColumns() + ") values (" + table.concatenatedColumns(":") + ")\", array(";
                
                for (String columna : table.getColumns()) {
                    s += "\":" + columna + "\" => $" + columna + ",";
                }
                s = s.substring(0, s.length() - 1);
                s += "));\n"
                        + "    }\n"
                        + "\n";
                
                s += "    public function update(" + table.concatenatedColumns("$") + ") {\n"
                        + "        $objConexion = new Connection();\n"
                        + "        $objConexion->queryWithParams(\"update $this->tableName set ";
                
                for (String columna : table.getColumns()) {
                    s += columna + " = :" + columna + ", ";
                }
                s = s.substring(0, s.length()-2);
                s += " where id = :id\", array(";
                
                for (String columna : table.getColumns()) {
                    s += "\":" + columna + "\" => $" + columna + ",";
                }
                s = s.substring(0, s.length()-1);
                s += "));\n"
                        + "    }\n"
                        + "\n";
                
                s += "    public function delete($id) {\n"
                        + "        $objConexion = new Connection();\n"
                        + "        $objConexion->queryWithParams(\"delete from $this->tableName where id = :id\", array(\":id\" => $id));\n"
                        + "    }\n"
                        + "\n";
                
                s += "    public function select($id) {\n"
                        + "        $objConexion = new Connection();\n"
                        + "        $res = $objConexion->queryWithParams(\"select " + table.concatenatedColumns() + " from $this->tableName where id = :id\", array(\":id\" => $id));\n"
                        + "\n"
                        + "        if ($res->rowCount() == 0) {\n"
                        + "            return null;\n"
                        + "        }\n"
                        + "\n"
                        + "        $row = $res->fetch(PDO::FETCH_ASSOC);\n"
                        + "        $obj = $this->rowToDto($row);\n"
                        + "\n"
                        + "        return $obj;\n"
                        + "    }\n"
                        + "\n";
                
                s += "    public function selectAll() {\n"
                        + "        $objConexion = new Connection();\n"
                        + "        $res = $objConexion->query(\"select " + table.getColumns() + " from $this->tableName\");\n"
                        + "        $personList = array();\n"
                        + "\n"
                        + "        while ($row = $res->fetch(PDO::FETCH_ASSOC)) {\n"
                        + "            $obj = $this->rowToDto($row);\n"
                        + "            $personList[] = $obj;\n"
                        + "        }\n"
                        + "\n"
                        + "        return $personList;\n"
                        + "    }\n"
                        + "\n";
                        
                 s += "    public function rowToDto($row) {\n"
                         + "        $obj = new Persona();\n"
                         + "        $obj->setId($row[\"id\"]);\n"
                         + "        $obj->setNombres($row[\"nombres\"]);\n"
                         + "        $obj->setApellidos($row[\"apellidos\"]);\n"
                         + "        $obj->setEdad($row[\"edad\"]);\n"
                         + "        $obj->setSexo($row[\"sexo\"]);\n"
                         + "        $obj->setFechaNacimiento($row[\"fecha_nacimiento\"]);\n"
                         + "\n"
                         + "        return $obj;\n"
                         + "    }\n"
                         + "\n"
                         + "}\n";
                 
                bw.write(s);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    private static void createDirectories(String... ubicaciones) {
        for (String ubicacion : ubicaciones) {
            File directorio = new File("src/" + ubicacion);
            directorio.mkdirs();
        }
    }

    private static BufferedWriter createFile(String ubicacion) throws IOException {
        return new BufferedWriter(new FileWriter("src/" + ubicacion));
    }
    
    private static String firstUppercased(String word) {
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }

}