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

/**
 * Enum class for method parameter or return types
 * @author Kalyan Mulampaka
 *
 */
public enum ParameterType
{
    OBJECT ("Object", "Object"),
    STRING ("String", "String"),
    BOOLEAN ("Boolean", "boolean"),
    DATE ("Date", "Date"),
    DATETIME ("Date", "Date"),
    TIMESTAMP ("Timestamp", "Timestamp"),
    LONG ("Long", "long"),
    INTEGER ("Integer", "int"),
    FLOAT ("Float", "float"),
    DOUBLE ("Double", "double"),
    BIGDECIMAL ("BigDecimal", "BigDecimal"),
    CHAR ("Character", "char"),
    LIST ("List", "List")
    ;

    private String name;
    private String primitiveName;

    private ParameterType (String name, String primitiveName)
    {
        this.name = name;
        this.primitiveName = primitiveName;

    }
    public String getName ()
    {
        return name;
    }
    public void setName (String name)
    {
        this.name = name;
    }

    public String getPrimitiveName ()
    {
        return this.primitiveName;
    }

    public void setPrimitiveName (String primitiveName)
    {
        this.primitiveName = primitiveName;
    }


}
