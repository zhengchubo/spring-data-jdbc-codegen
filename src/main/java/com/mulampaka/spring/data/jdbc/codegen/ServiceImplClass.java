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

import com.mulampaka.spring.data.jdbc.codegen.util.CodeGenUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcOperations;

public class ServiceImplClass extends BaseClass
{

    final static Logger logger = LoggerFactory.getLogger (ServiceImplClass.class);
    private static String CLASS_SUFFIX = "ServiceImpl";

    private String repoClassName;

    public ServiceImplClass()
    {
        this.classSuffix = CLASS_SUFFIX;
        this.addImports ();
    }

    @Override
    protected void addImports ()
    {
        this.imports.add("org.springframework.stereotype.Service");
        this.imports.add("javax.annotation.Resource");
        this.imports.add("java.util.ArrayList;");
        this.imports.add("java.util.Objects");
        this.imports.add("java.util.List");
    }

    @Override
    protected void printClassComments ()
    {
        sourceBuf.append ("\n/**\n");
        sourceBuf.append (" *\n");
        sourceBuf.append (" * @author Anonymous\n");
        sourceBuf.append (" */\n");
    }

    protected void printClassAnnotations ()
    {
        sourceBuf.append ("@Service(\"" + this.name.substring(0, 1).toLowerCase() + this.name.substring(1) + "Service\")\n");
    }

    @Override
    protected void printClassDefn ()
    {
        sourceBuf.append ("public class " + WordUtils.capitalize (CodeGenUtil.normalize (name)) + this.classSuffix);
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
    protected void preprocess ()
    {
    }

    protected void printFields (String tabString)
    {
        String repoName = repoClassName.substring(0, 1).toLowerCase() + repoClassName.substring(1);
        this.sourceBuf.append(tabString + "@Resource(name = \"" + repoName + "\")\n" + tabString + repoClassName + " " + repoName + ";\n\n");
    }

    protected void printMethods (String tabString)
    {
        String repoName = repoClassName.substring(0, 1).toLowerCase() + repoClassName.substring(1);
        this.sourceBuf.append(tabString + "@Override\n" + tabString + "public " + this.name + " getItemByParam(" + this.name + " param) {\n\n");
        this.sourceBuf.append(tabString + tabString + "if (Objects.isNull(param)) {\n");
        this.sourceBuf.append(tabString + tabString + tabString + "return null;\n");
        this.sourceBuf.append(tabString + tabString + "}\n");
        this.sourceBuf.append(tabString + tabString + "return " + repoName + ".getItemByParam(param);\n");
        this.sourceBuf.append(tabString + "}\n\n");

        this.sourceBuf.append(tabString + "@Override\n" + tabString + "public List<" + this.name + "> getListByParam(" + this.name + " param) {\n\n");
        this.sourceBuf.append(tabString + tabString + "if (Objects.isNull(param)) {\n");
        this.sourceBuf.append(tabString + tabString + tabString + "return new ArrayList<>();\n");
        this.sourceBuf.append(tabString + tabString + "}\n");
        this.sourceBuf.append(tabString + tabString + "return " + repoName + ".getListByParam(param);\n");
        this.sourceBuf.append(tabString + "}\n\n");

        this.sourceBuf.append(tabString + "@Override\n" + tabString + "public Long getCountByParam(" + this.name + " param) {\n\n");
        this.sourceBuf.append(tabString + tabString + "if (Objects.isNull(param)) {\n");
        this.sourceBuf.append(tabString + tabString + tabString + "return 0L;\n");
        this.sourceBuf.append(tabString + tabString + "}\n");
        this.sourceBuf.append(tabString + tabString + "return " + repoName + ".getCountByParam(param);\n");
        this.sourceBuf.append(tabString + "}\n\n");

    }

    @Override
    public void generateSource(String tabString)
    {
        this.name = WordUtils.capitalize (CodeGenUtil.normalize (this.name));
        this.repoClassName = this.name + RepositoryClass.CLASS_SUFFIX;

        this.preprocess ();

        super.printPackage ();
        super.printImports ();
        this.printClassComments ();

        this.printClassAnnotations ();
        this.printClassDefn ();
        this.printClassExtends ();
        super.printClassImplements ();

        super.printOpenBrace (0, 2, tabString);
        super.printLogger (tabString);
        this.printFields(tabString);
        this.printMethods(tabString);

        super.printCloseBrace (0, 2, tabString);
    }



}
