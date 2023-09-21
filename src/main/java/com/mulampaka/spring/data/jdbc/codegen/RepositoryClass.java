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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mulampaka.spring.data.jdbc.codegen.util.CodeGenUtil;

public class RepositoryClass extends BaseClass
{

    final static Logger logger = LoggerFactory.getLogger (RepositoryClass.class);
    public static String CLASS_SUFFIX = "Repository";

    private static String TBL_DESC_CLASS = "com.nurkiewicz.jdbcrepository.TableDescription";

    public RepositoryClass ()
    {
        this.classSuffix = CLASS_SUFFIX;
        super.setExtendsClassName ("com.nurkiewicz.jdbcrepository.JdbcRepository");
        this.addImports ();
    }

    @Override
    protected void addImports ()
    {
        this.imports.add("org.springframework.beans.factory.annotation.Autowired");
        this.imports.add("org.springframework.dao.EmptyResultDataAccessException");
        this.imports.add("org.springframework.jdbc.core.JdbcOperations");
        this.imports.add("org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource");
        this.imports.add("org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate");
        this.imports.add("org.springframework.jdbc.core.namedparam.SqlParameterSource");
        this.imports.add("org.springframework.stereotype.Repository");
        this.imports.add("java.util.List");
        this.imports.add("java.util.Objects");
        this.imports.add("javax.annotation.Resource");
    }

    protected void printClassAnnotations ()
    {
        sourceBuf.append ("@Repository\n");
    }

    @Override
    protected void printClassExtends ()
    {
        super.printClassExtends ();

        if (StringUtils.isNotBlank (extendsClassName))
        {
            sourceBuf.append ("<");
            sourceBuf.append (this.name + ", ");
            if (this.pkeys.size () == 0)
            {
                sourceBuf.append ("String");
            }
            else if (pkeys.size () == 1)
            {
                sourceBuf.append (this.pkeys.values ().iterator ().next ().getName ());
            }
            else
            {
                sourceBuf.append ("Object[]");
            }
            sourceBuf.append (">");

        }
    }

    @Override
    protected void printCtor (String tabString)
    {
        if (this.pkeys.size () == 0)
        {
            // add ctor
            sourceBuf.append (tabString + "public " + this.name + this.classSuffix + "()\n");
            super.printOpenBrace (1, 1, tabString);
            sourceBuf.append (tabString + tabString + "super (" + this.name + DBClass.DB_CLASSSUFFIX + ".ROW_MAPPER, " + this.name + DBClass.DB_CLASSSUFFIX + ".ROW_UNMAPPER, "
                    + this.name + DBClass.DB_CLASSSUFFIX + ".getTableName (),");
            sourceBuf.append (this.name + DBClass.DB_CLASSSUFFIX + ".COLUMNS.ID.getColumnName()");
            sourceBuf.append (");\n");
            super.printCloseBrace (1, 2, tabString);

            // add postcreate
            sourceBuf.append (tabString + "@Override\n");
            sourceBuf.append (tabString + "protected " + this.name + " postCreate(" + this.name + " entity, Number generatedId)\n");
            super.printOpenBrace (1, 1, tabString);
            sourceBuf.append (tabString + tabString + "entity.setId(generatedId.intValue());\n");
            sourceBuf.append (tabString + tabString + "entity.setPersisted(true);\n");
            sourceBuf.append (tabString + tabString + "return entity;\n");
            super.printCloseBrace (1, 2, tabString);
        }
        else if (this.pkeys.size () == 1)
        {
            // add ctor
            sourceBuf.append (tabString + "public " + this.name + this.classSuffix + "()\n");
            super.printOpenBrace (1, 1, tabString);
            sourceBuf.append (tabString + tabString + "super (" + this.name + DBClass.DB_CLASSSUFFIX + ".ROW_MAPPER, " + this.name + DBClass.DB_CLASSSUFFIX + ".ROW_UNMAPPER, "
                    + this.name + DBClass.DB_CLASSSUFFIX + ".getTableName ());\n");
            super.printCloseBrace (1, 2, tabString);

            // add ctor2
            sourceBuf.append (tabString + "public " + this.name + this.classSuffix + "(");

            sourceBuf.append ("RowMapper<" + this.name + "> rowMapper, RowUnmapper<" + this.name + "> rowUnmapper, String idColumn)\n");
            super.printOpenBrace (1, 1, tabString);

            sourceBuf.append (tabString + tabString + "super (" + this.name + DBClass.DB_CLASSSUFFIX + ".ROW_MAPPER, " + this.name + DBClass.DB_CLASSSUFFIX + ".ROW_UNMAPPER, " + this.name + DBClass.DB_CLASSSUFFIX + ".getTableName (), idColumn);\n");
            super.printCloseBrace (1, 2, tabString);

            // add postcreate
            sourceBuf.append (tabString + "@Override\n");
            sourceBuf.append (tabString + "protected " + this.name + " postCreate(" + this.name + " entity, Number generatedId)\n");
            super.printOpenBrace (1, 1, tabString);
            String key = this.pkeys.keySet ().iterator ().next ();
            ParameterType keyType = this.pkeys.values ().iterator ().next ();
            String keyTypeName = "";
            switch (keyType)
            {
            case INTEGER:
                keyTypeName = "int";
                break;
            case LONG:
                keyTypeName = "long";
                break;
            case DOUBLE:
                keyTypeName = "double";
                break;
            default:
                keyTypeName = "int";
            }
            if ("id".equalsIgnoreCase (key))
            {
                sourceBuf.append (tabString + tabString + "entity.setId(generatedId." + keyTypeName + "Value());\n");
            }
            sourceBuf.append (tabString + tabString + "entity.setPersisted(true);\n");
            sourceBuf.append (tabString + tabString + "return entity;\n");
            super.printCloseBrace (1, 2, tabString);
        }
        else
        {
            // add ctor
            sourceBuf.append (tabString + "public " + this.name + this.classSuffix + "()\n");
            super.printOpenBrace (1, 1, tabString);
            sourceBuf.append (tabString + tabString + "super (" + this.name + DBClass.DB_CLASSSUFFIX + ".ROW_MAPPER, " + this.name + DBClass.DB_CLASSSUFFIX + ".ROW_UNMAPPER, "
                    + "new TableDescription(" + this.name + DBClass.DB_CLASSSUFFIX + ".getTableName (), null,");
            int i = this.pkeys.size ();
            for (String key : this.pkeys.keySet ())
            {
                sourceBuf.append (this.name + DBClass.DB_CLASSSUFFIX + ".COLUMNS." + key.toUpperCase () + ".getColumnName()");
                --i;
                if (i > 0) {
                    sourceBuf.append (",");
                }
            }
            sourceBuf.append ("));\n");
            super.printCloseBrace (1, 2, tabString);

            // add postcreate
            sourceBuf.append (tabString + "@Override\n");
            sourceBuf.append (tabString + "protected " + this.name + " postCreate(" + this.name + " entity, Number generatedId)\n");
            super.printOpenBrace (1, 1, tabString);
            sourceBuf.append (tabString + tabString + "entity.setPersisted(true);\n");
            sourceBuf.append (tabString + tabString + "return entity;\n");
            super.printCloseBrace (1, 2, tabString);

        }
        sourceBuf.append ("\n");
    }


    protected void printFKeyMethods (String tabString)
    {
        logger.debug ("Generating foreign pkeys methods: # of FKeys:{} ", this.fkeys.size ());
        // add methods from foreign pkeys
        if (!this.fkeys.isEmpty ())
        {
            for (String fkColName : this.fkeys.keySet ())
            {
                ForeignKey fkey = this.fkeys.get (fkColName);
                String refObj = WordUtils.capitalize (CodeGenUtil.normalize (fkey.getFkTableName ()));
                String methodClassName = CodeGenUtil.pluralizeName (refObj, this.getDontPluralizeWords ());
                sourceBuf.append (tabString + "public List<" + refObj + "> get" + methodClassName + "By" + WordUtils.capitalize (CodeGenUtil.normalize (fkColName)) + " (Long " + CodeGenUtil.normalize (fkColName) + ")\n");
                this.printOpenBrace (1, 1, tabString);
                sourceBuf.append (tabString + tabString + "String sql = \"select * from \" + " + refObj + DBClass.DB_CLASSSUFFIX + ".getTableName() + " + "\" where \" + " + refObj + DBClass.DB_CLASSSUFFIX + ".COLUMNS." + fkColName.toUpperCase () + ".getColumnName() + \" = ? \";\n");
                sourceBuf.append (tabString + tabString + "return this.jdbcOperations.query (sql, new Object[] { " + CodeGenUtil.normalize (fkColName) + " }, " + refObj + DBClass.DB_CLASSSUFFIX + ".ROW_MAPPER);\n");
                this.printCloseBrace (1, 2, tabString);
            }
        }
        sourceBuf.append ("\n");
    }

    @Override
    protected void preprocess ()
    {
        if (this.pkeys.size () != 1)
        {
            if (!this.imports.contains (TBL_DESC_CLASS)) {
                this.imports.add (TBL_DESC_CLASS);
            }
        }
        else
        {
            if (!this.imports.contains ("org.springframework.jdbc.core.RowMapper")) {
                this.imports.add ("org.springframework.jdbc.core.RowMapper");
            }
            if (!this.imports.contains ("com.nurkiewicz.jdbcrepository.RowUnmapper")) {
                this.imports.add ("com.nurkiewicz.jdbcrepository.RowUnmapper");
            }
        }
    }

    protected void printFields (String tabString)
    {
        this.sourceBuf.append(tabString + "@Autowired\n" + tabString + "private JdbcOperations jdbcOperations;\n\n");
        this.sourceBuf.append(tabString + "@Resource\n" + tabString + "private NamedParameterJdbcTemplate namedTemplate;\n\n");
    }

    protected void printMethods (String tabString)
    {
        this.sourceBuf.append(tabString + "protected JdbcOperations getJdbcOperations()\n");
        this.sourceBuf.append(tabString + "{\n");
        this.sourceBuf.append(tabString + tabString + "return this.jdbcOperations;\n");
        this.sourceBuf.append(tabString + "}\n\n");

        this.sourceBuf.append(tabString + "public " + this.name + " getItemByParam(" + this.name + " param) {\n\n");
        this.sourceBuf.append(tabString + tabString + "String sql = \" SELECT * FROM \" + " + this.name + DBClass.DB_CLASSSUFFIX + ".getTableName() + \" WHERE 1 = 1 \";\n");
        this.sourceBuf.append(tabString + tabString + "SqlParameterSource params = new BeanPropertySqlParameterSource(param);\n");
        this.sourceBuf.append(tabString + tabString + "sql += getEntityParamSql(param) + \" LIMIT 1 \";\n");
        this.sourceBuf.append(tabString + tabString + "try {\n");
        this.sourceBuf.append(tabString + tabString + tabString + "return namedTemplate.queryForObject(sql, params, " + this.name + DBClass.DB_CLASSSUFFIX + ".ROW_MAPPER);\n");
        this.sourceBuf.append(tabString + tabString + "} catch (EmptyResultDataAccessException e) {\n");
        this.sourceBuf.append(tabString + tabString + tabString + "return null;\n");
        this.sourceBuf.append(tabString + tabString + "}\n");
        this.sourceBuf.append(tabString + "}\n\n");

        this.sourceBuf.append(tabString + "public List<" + this.name + "> getListByParam(" + this.name + " param) {\n\n");
        this.sourceBuf.append(tabString + tabString + "String sql = \" SELECT * FROM \" + " + this.name + DBClass.DB_CLASSSUFFIX + ".getTableName() + \" WHERE 1 = 1 \";\n");
        this.sourceBuf.append(tabString + tabString + "SqlParameterSource params = new BeanPropertySqlParameterSource(param);\n");
        this.sourceBuf.append(tabString + tabString + "sql += getEntityParamSql(param);\n");
        this.sourceBuf.append(tabString + tabString + "return namedTemplate.query(sql, params, " + this.name + DBClass.DB_CLASSSUFFIX + ".ROW_MAPPER);\n");
        this.sourceBuf.append(tabString + "}\n\n");

        this.sourceBuf.append(tabString + "public Long getCountByParam(" + this.name + " param) {\n\n");
        this.sourceBuf.append(tabString + tabString + "String sql = \" SELECT COUNT(*) FROM \" + " + this.name + DBClass.DB_CLASSSUFFIX + ".getTableName() + \" WHERE 1 = 1 \";\n");
        this.sourceBuf.append(tabString + tabString + "SqlParameterSource params = new BeanPropertySqlParameterSource(param);\n");
        this.sourceBuf.append(tabString + tabString + "sql += getEntityParamSql(param);\n");
        this.sourceBuf.append(tabString + tabString + "return namedTemplate.queryForObject(sql, params, Long.class);\n");
        this.sourceBuf.append(tabString + "}\n\n");

        this.sourceBuf.append(tabString + "private String getEntityParamSql(" + this.name + " param) {\n");
        this.sourceBuf.append(tabString + tabString + "StringBuffer sb = new StringBuffer();\n");
        this.sourceBuf.append(tabString + tabString + "if (Objects.nonNull(param.getId())) {\n");
        this.sourceBuf.append(tabString + tabString + tabString + "sb.append(\" AND \").append(" + this.name + DBClass.DB_CLASSSUFFIX + ".COLUMNS.ID.getColumnName()).append(\" = :id \");\n");
        this.sourceBuf.append(tabString + tabString + "}\n");
        this.sourceBuf.append(tabString + tabString + "return sb.toString();\n");
        this.sourceBuf.append(tabString + "}\n\n");
    }

    @Override
    public void generateSource(String tabString)
    {
        this.preprocess ();

        this.name = WordUtils.capitalize (CodeGenUtil.normalize (this.name));

        super.printPackage ();
        super.printImports ();
        super.printClassComments ();

        this.printClassAnnotations ();
        super.printClassDefn ();
        this.printClassExtends ();
        super.printClassImplements ();

        super.printOpenBrace (0, 2, tabString);
        super.printLogger (tabString);
        this.printFields(tabString);
        this.printCtor (tabString);
        this.printMethods(tabString);

        this.printFKeyMethods (tabString);

        super.printUserSourceCode (tabString);

        super.printCloseBrace (0, 2, tabString);
    }



}
