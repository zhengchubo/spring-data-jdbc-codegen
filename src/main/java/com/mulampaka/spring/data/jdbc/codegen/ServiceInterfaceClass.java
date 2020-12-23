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

public class ServiceInterfaceClass extends BaseClass
{

    final static Logger logger = LoggerFactory.getLogger (ServiceInterfaceClass.class);
    public static String CLASS_SUFFIX = "Service";

    public ServiceInterfaceClass()
    {
        this.classSuffix = CLASS_SUFFIX;
        this.addImports ();
    }

    @Override
    protected void addImports ()
    {
        this.imports.add("java.util.List");
    }

    protected void printClassAnnotations ()
    {
    }

    @Override
    protected void printClassDefn ()
    {
        sourceBuf.append ("public interface " + WordUtils.capitalize (CodeGenUtil.normalize (name)) + this.classSuffix);
    }

    @Override
    protected void preprocess ()
    {
    }

    protected void printMethods (String tabString)
    {
        this.sourceBuf.append(tabString + this.name + " getItemByParam(" + this.name + " param);\n\n");
        this.sourceBuf.append(tabString + "List<" + this.name + "> getListByParam(" + this.name + " param);\n\n");
        this.sourceBuf.append(tabString + "Long getCountByParam(" + this.name + " param);\n\n");
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
        this.printClassDefn ();
        this.printClassExtends ();
        super.printClassImplements ();

        super.printOpenBrace (0, 2, tabString);
        this.printMethods(tabString);

        super.printUserSourceCode (tabString);

        super.printCloseBrace (0, 2, tabString);
    }



}
