/**
 * 
 * Copyright 2013
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @author Kalyan Mulampaka
 */
package com.mulampaka.spring.data.jdbc.codegen;

import java.util.List;
import org.apache.commons.lang3.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mulampaka.spring.data.jdbc.codegen.util.CodeGenUtil;

/**
 * Class to represent the db metadata, row mappers and unmappers
 * 
 * @author Kalyan Mulampaka
 * 
 */
public class DBClass extends BaseClass
{

    final static Logger logger = LoggerFactory.getLogger (DBClass.class);
    public static String DB_CLASSSUFFIX = "DB";



    public DBClass ()
    {
        this.addImports ();
        this.classSuffix = DB_CLASSSUFFIX;
    }

    @Override
    protected void addImports ()
    {
        this.imports.add ("java.sql.SQLException");
        this.imports.add ("org.springframework.jdbc.core.RowMapper");
        this.imports.add ("java.sql.ResultSet");
        this.imports.add ("java.util.LinkedHashMap");
        this.imports.add ("java.util.Map");
        this.imports.add ("com.nurkiewicz.jdbcrepository.RowUnmapper");
    }

    protected void printDBTableInfo(String tabString)
    {
        // add the table name
        sourceBuf.append (tabString + "private static String TABLE_NAME = \"" + this.name + "\";\n\n");

        // add the table name
        sourceBuf.append (tabString + "private static String TABLE_ALIAS = \"" + CodeGenUtil.createTableAlias (this.name.toLowerCase ()) + "\";\n\n");

        sourceBuf.append (tabString + "public static String getTableName()\n" + tabString + "{\n" + tabString + tabString + "return TABLE_NAME;\n" + tabString + "}\n\n");

        sourceBuf.append (tabString + "public static String getTableAlias()\n" + tabString + "{\n" + tabString + tabString + "return TABLE_NAME + \" as \" + TABLE_ALIAS;\n" + tabString + "}\n\n");
        
        sourceBuf.append (tabString + "public static String getAlias()\n" + tabString + "{\n" + tabString + tabString + "return TABLE_ALIAS;\n" + tabString + "}\n\n");
    }

    protected void printSelectAllColumns (String tabString)
    {
        sourceBuf.append (tabString + "public static String selectAllColumns(boolean ... useAlias)\n" + tabString + "{\n" + tabString + tabString + "return (useAlias[0] ? TABLE_ALIAS : TABLE_NAME) + \".*\";\n" + tabString + "}\n\n");
    }

    protected void printRowMapper (String tabString)
    {
        String name = WordUtils.capitalize (CodeGenUtil.normalize (this.name));
        // create mapper
        sourceBuf.append (tabString + "public static final RowMapper<" + name + "> ROW_MAPPER = new " + name + "RowMapper ();\n");

        sourceBuf.append (tabString + "public static final class  " + name + "RowMapper implements RowMapper<" + name + ">\n");
        this.printOpenBrace (1, 1, tabString);
        sourceBuf.append (tabString + tabString + "@Override\n");
        sourceBuf.append (tabString + tabString + "public " + name + " mapRow(ResultSet rs, int rowNum) throws SQLException \n");
        this.printOpenBrace (2, 1, tabString);
        sourceBuf.append (tabString + tabString + tabString + name + " obj = new " + name + "();\n");
        for (Field field : this.fields)
        {
            if (field.isPersistable ())
            {
                String typeName = field.getType ().getName ();
                if (field.getType () == ParameterType.INTEGER)
                {
                    typeName = "Int";
                }
                else if (field.getType () == ParameterType.DATE)
                {
                    typeName = "Timestamp";
                }
                sourceBuf.append (tabString + tabString + tabString + "obj.set" + WordUtils.capitalize (CodeGenUtil.normalize (field.getName ())) + "(rs.get" + typeName + "(COLUMNS." + field.getName ().toUpperCase () + ".getColumnName()));\n");
                if (field.getType() == ParameterType.LONG || field.getType() == ParameterType.INTEGER) {
                    sourceBuf.append (tabString + tabString + tabString + "if (rs.wasNull()) {\n");
                    sourceBuf.append (tabString + tabString + tabString + tabString + "obj.set").append(WordUtils.capitalize (CodeGenUtil.normalize (field.getName ())) + "(null);\n");
                    sourceBuf.append (tabString + tabString + tabString + "}\n");
                }
            }
        }

        if (this.pkeys.size () > 1)
        {
            sourceBuf.append (tabString + tabString + tabString + "obj.setPersisted(true);\n");
        }
        sourceBuf.append (tabString + tabString + tabString + "return obj;\n");
        this.printCloseBrace (2, 1, tabString);// end of method
        this.printCloseBrace (1, 2, tabString); // end of inner mapper class
    }

    protected void printRowUnMapper(String tabString)
    {
        String name = WordUtils.capitalize (CodeGenUtil.normalize (this.name));
        // create unmapper
        sourceBuf.append (tabString + "public static final RowUnmapper<" + name + "> ROW_UNMAPPER = new " + name + "RowUnmapper ();\n");
        sourceBuf.append (tabString + "public static final class " + name + "RowUnmapper implements RowUnmapper<" + name + ">\n");
        this.printOpenBrace (1, 1, tabString);
        String objName = name.toLowerCase ();
        sourceBuf.append (tabString + tabString + "@Override\n");
        sourceBuf.append (tabString + tabString + "public Map<String, Object> mapColumns(" + name + " " + objName + ")\n");
        this.printOpenBrace (2, 1, tabString);
        sourceBuf.append (tabString + tabString + tabString + "Map<String, Object> mapping = new LinkedHashMap<String, Object>();\n");
        for (Field field : this.fields)
        {
            if (field.isPersistable ())
            {
                if (field.getType () == ParameterType.DATE)
                {
                    sourceBuf.append (tabString + tabString + tabString + "if (" + objName + ".get" + WordUtils.capitalize (CodeGenUtil.normalize (field.getName ())) + "() != null)\n");
                    this.printOpenBrace (3, 1, tabString);
                    sourceBuf.append (tabString + tabString + tabString + tabString + "mapping.put(COLUMNS." + field.getName ().toUpperCase () + ".getColumnName(), new Timestamp (" + objName + ".get" + WordUtils.capitalize (CodeGenUtil.normalize (field.getName ())) + "().getTime()));\n");
                    this.printCloseBrace (3, 1, tabString);
                }
                else
                {
                    sourceBuf.append (tabString + tabString + tabString + "mapping.put(COLUMNS." + field.getName ().toUpperCase () + ".getColumnName(), " + objName + ".get" + WordUtils.capitalize (CodeGenUtil.normalize (field.getName ())) + "());\n");
                }
            }
        }
        sourceBuf.append (tabString + tabString + tabString + "return mapping;\n");
        this.printCloseBrace (2, 1, tabString);
        this.printCloseBrace (1, 2, tabString);// end of inner unmapper class
    }

    protected void printAliasRowMapper (String tabString)
    {
        String name = WordUtils.capitalize (CodeGenUtil.normalize (this.name));
        // create alias mapper
        sourceBuf.append (tabString + "public static final RowMapper<" + name + "> ALIAS_ROW_MAPPER = new " + name + "AliasRowMapper ();\n");

        sourceBuf.append (tabString + "public static final class  " + name + "AliasRowMapper implements RowMapper<" + name + ">\n");
        this.printOpenBrace (1, 1, tabString);
        List<Relation> relations = this.relations.get (this.name);

        if (relations != null && !relations.isEmpty ())
        {
            boolean loadAllRelations = false;

            for (Relation relation : relations)
            {
                switch (relation.getType ())
                {
                case ONE_TO_ONE:
                    loadAllRelations = true;
                    String child = CodeGenUtil.normalize (relation.getChild ());
                    sourceBuf.append (tabString + tabString + "private boolean load" + WordUtils.capitalize (child) + " = false;\n");
                    sourceBuf.append (tabString + tabString + "public void setLoad" + WordUtils.capitalize (child) + " (boolean load" + WordUtils.capitalize (child) + ")\n");
                    this.printOpenBrace (2, 1, tabString);
                    sourceBuf.append (tabString + tabString + tabString + "this.load" + WordUtils.capitalize (child) + " = load" + WordUtils.capitalize (child) + ";\n");
                    this.printCloseBrace (2, 2, tabString);
                    break;
                case ONE_TO_MANY:
                case UNKNOWN:
                    break;
                }
            }
            if (loadAllRelations)
            {
                sourceBuf.append (tabString + tabString + "private boolean loadAllRelations = false;\n");
                sourceBuf.append (tabString + tabString + "public void setLoadAllRelations (boolean loadAllRelations)\n");
                this.printOpenBrace (2, 1, tabString);
                sourceBuf.append (tabString + tabString + tabString + "this.loadAllRelations = loadAllRelations;\n");
                this.printCloseBrace (2, 2, tabString);
            }
        }

        if (!this.fkeys.isEmpty ())
        {
            sourceBuf.append (tabString + tabString + "private boolean loadAllFKeys = false;\n");
            sourceBuf.append (tabString + tabString + "public void setLoadAllFKeys (boolean loadAllFKeys)\n");
            this.printOpenBrace (2, 1, tabString);
            sourceBuf.append (tabString + tabString + tabString + "this.loadAllFKeys = loadAllFKeys;\n");
            this.printCloseBrace (2, 2, tabString);

            for (String fkColName : this.fkeys.keySet ())
            {
                ForeignKey fkey = this.fkeys.get (fkColName);
                String refObj = WordUtils.capitalize (CodeGenUtil.normalize (fkey.getFieldName ()));
                sourceBuf.append (tabString + tabString + "private boolean load" + refObj + " = false;\n");
                sourceBuf.append (tabString + tabString + "public void setLoad" + refObj + " (boolean load" + refObj + ")\n");
                this.printOpenBrace (2, 1, tabString);
                sourceBuf.append (tabString + tabString + tabString + "this.load" + refObj + " = load" + refObj + ";\n");
                this.printCloseBrace (2, 2, tabString);
            }
        }

        sourceBuf.append (tabString + tabString + "@Override\n");
        sourceBuf.append (tabString + tabString + "public " + name + " mapRow(ResultSet rs, int rowNum) throws SQLException \n");
        this.printOpenBrace (2, 1, tabString);
        sourceBuf.append (tabString + tabString + tabString + name + " obj = new " + name + "();\n");
        for (Field field : this.fields)
        {
            if (field.isPersistable ())
            {
                String typeName = field.getType ().getName ();
                if (field.getType () == ParameterType.INTEGER)
                {
                    typeName = "Int";
                }
                else if (field.getType () == ParameterType.DATE)
                {
                    typeName = "Timestamp";
                }
                sourceBuf.append (tabString + tabString + tabString + "obj.set" + WordUtils.capitalize (CodeGenUtil.normalize (field.getName ())) + "(rs.get" + typeName + "(COLUMNS." + field.getName ().toUpperCase () + ".getColumnAliasName()));\n");
                if (field.getType() == ParameterType.LONG || field.getType() == ParameterType.INTEGER) {
                    sourceBuf.append (tabString + tabString + tabString + "if (rs.wasNull()) {\n");
                    sourceBuf.append (tabString + tabString + tabString + tabString + "obj.set").append(WordUtils.capitalize (CodeGenUtil.normalize (field.getName ())) + "(null);\n");
                    sourceBuf.append (tabString + tabString + tabString + "}\n");
                }
            }
        }
        if (this.pkeys.size () > 1)
        {
            sourceBuf.append (tabString + tabString + tabString + "obj.setPersisted(true);\n");
        }
        if (!this.fkeys.isEmpty ())
        {
            for (String fkColName : this.fkeys.keySet ())
            {
                ForeignKey fkey = this.fkeys.get (fkColName);
                String refObj = WordUtils.capitalize (CodeGenUtil.normalize (fkey.getFieldName ()));
                String refClass = WordUtils.capitalize (CodeGenUtil.normalize (fkey.getRefTableName ()));
                sourceBuf.append (tabString + tabString + tabString + "if (this.loadAllFKeys || this.load" + refObj + ")\n");
                sourceBuf.append (tabString + tabString + tabString + tabString + "obj.set" + refObj + "(" + refClass + DBClass.DB_CLASSSUFFIX + ".ALIAS_ROW_MAPPER.mapRow(rs, rowNum)" + ");\n");
            }
        }
        this.printRelations (tabString);
        sourceBuf.append (tabString + tabString + tabString + "return obj;\n");
        this.printCloseBrace (2, 1, tabString); // end of method
        this.printCloseBrace (1, 2, tabString); // end of inner alias mapper class
    }

    protected void printRelations (String tabString)
    {
        List<Relation> relations = this.relations.get (this.name);
        if (relations != null && !relations.isEmpty ())
        {
            for (Relation relation : relations)
            {
                switch (relation.getType ())
                {
                case ONE_TO_ONE:
                    String child = CodeGenUtil.normalize (relation.getChild ());
                    sourceBuf.append (tabString + tabString + tabString + "if (this.loadAllRelations || this.load" + WordUtils.capitalize (child) + ")\n");
                    sourceBuf.append (tabString + tabString + tabString + tabString + "obj.set" + WordUtils.capitalize (child) + "(" + WordUtils.capitalize (child) + DBClass.DB_CLASSSUFFIX + ".ALIAS_ROW_MAPPER.mapRow(rs, rowNum)" + ");\n");
                    break;
                case ONE_TO_MANY:
                case UNKNOWN:
                    break;
                }
            }
        }
    }

    protected void printAllAliasesMethod (String tabString)
    {
        // create all aliases
        sourceBuf.append (tabString + "public static StringBuffer getAllColumnAliases ()\n");
        this.printOpenBrace (1, 1, tabString);
        sourceBuf.append (tabString + tabString + "StringBuffer strBuf = new StringBuffer ();\n");
        sourceBuf.append (tabString + tabString + "int i = COLUMNS.values ().length;\n");
        sourceBuf.append (tabString + tabString + "for (COLUMNS c : COLUMNS.values ())\n");
        this.printOpenBrace (2, 1, tabString);
        sourceBuf.append (tabString + tabString + tabString + "strBuf.append (c.getColumnAliasAsName ());\n");
        sourceBuf.append (tabString + tabString + tabString + "if (--i > 0)\n");
        this.printOpenBrace (3, 1, tabString);
        sourceBuf.append (tabString + tabString + tabString + tabString + "strBuf.append (\", \");\n");
        this.printCloseBrace (3, 1, tabString);
        this.printCloseBrace (2, 1, tabString);
        sourceBuf.append (tabString + tabString + "return strBuf;\n");
        this.printCloseBrace (1, 2, tabString);
    }


    private void printColumnsEnum (String tabString)
    {
        sourceBuf.append (tabString + "public enum COLUMNS\n");
        this.printOpenBrace (1, 1, tabString);

        for (Field field : this.fields)
        {
            if (field.isPersistable ())
            {
                sourceBuf.append (tabString + tabString + field.getName ().toUpperCase () + "(\"" + field.getName () + "\"),\n");
            }
        }
        sourceBuf.append (tabString + tabString + ";\n");
        sourceBuf.append ("\n");
        sourceBuf.append (tabString + tabString + "private String columnName;\n\n");
        // create the constructor
        sourceBuf.append (tabString + tabString + "private COLUMNS (String columnName)\n");
        this.printOpenBrace (2, 1, tabString);
        sourceBuf.append (tabString + tabString + tabString + "this.columnName = columnName;\n");
        this.printCloseBrace (2, 2, tabString);
        //create setters/getters
        sourceBuf.append (tabString + tabString + "public void setColumnName (String columnName)\n");
        this.printOpenBrace (2, 1, tabString);
        sourceBuf.append (tabString + tabString + tabString + "this.columnName = columnName;\n");
        this.printCloseBrace (2, 2, tabString);

        sourceBuf.append (tabString + tabString + "public String getColumnName ()\n");
        this.printOpenBrace (2, 1, tabString);
        sourceBuf.append (tabString + tabString + tabString + "return this.columnName;\n");
        this.printCloseBrace (2, 2, tabString);

        sourceBuf.append (tabString + tabString + "public String getColumnAlias ()\n");
        this.printOpenBrace (2, 1, tabString);
        sourceBuf.append (tabString + tabString + tabString + "return TABLE_ALIAS + \".\" + this.columnName;\n");
        this.printCloseBrace (2, 2, tabString);

        sourceBuf.append (tabString + tabString + "public String getColumnAliasAsName ()\n");
        this.printOpenBrace (2, 1, tabString);
        sourceBuf.append (tabString + tabString + tabString + "return TABLE_ALIAS  + \".\" + this.columnName + \" as \" + TABLE_ALIAS + \"_\" + this.columnName;\n");
        this.printCloseBrace (2, 2, tabString);

        sourceBuf.append (tabString + tabString + "public String getColumnAliasName ()\n");
        this.printOpenBrace (2, 1, tabString);
        sourceBuf.append (tabString + tabString + tabString + "return TABLE_ALIAS + \"_\" + this.columnName;\n");
        this.printCloseBrace (2, 2, tabString);

        this.printCloseBrace (1, 2, tabString);
    }

    @Override
    protected void preprocess ()
    {

    }

    @Override
    public void generateSource(String tabString)
    {
        // generate the default stuff from the super class
        super.printPackage ();

        super.printImports ();

        super.printClassComments ();

        super.printClassDefn ();

        super.printClassImplements ();

        this.printOpenBrace (0, 2, tabString);

        this.printDBTableInfo (tabString);

        this.printSelectAllColumns (tabString);

        this.printColumnsEnum (tabString);

        this.printCtor (tabString);

        this.printRowMapper (tabString);

        this.printRowUnMapper (tabString);

        this.printAliasRowMapper (tabString);

        this.printAllAliasesMethod (tabString);

        super.printUserSourceCode (tabString);

        this.printCloseBrace (0, 0, tabString); // end of class
        //logger.debug ("Printing Class file content:\n" + sourceBuf.toString ());
    }

}
