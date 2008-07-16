/*
  Copyright 2006-2007 The University Of Chicago
  Copyright 2006-2007 University Corporation for Advanced Internet Development, Inc.
  Copyright 2006-2007 EDUCAUSE
 
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.apache.org/licenses/LICENSE-2.0
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package edu.internet2.middleware.ldappc.ldap;

/**
 * This defines constants for the EduPermission object class.
 */
public class EduPermission
{
    /**
     * Name of the eduPermission object class
     */
    public static final String OBJECT_CLASS = "eduPermission";

    /**
     * Class to hold attribute constants
     */
    public class Attribute
    {
        /**
         * eduPermissionSubsystemId attribute
         */
        public static final String EDU_PERMISSION_SUBSYSTEM_ID = "eduPermissionSubsystemId";

        /**
         * eduPermissionFunctionId attribute
         */
        public static final String EDU_PERMISSION_FUNCTION_ID = "eduPermissionFunctionId";

        /**
         * eduPermissionId attribute
         */
        public static final String EDU_PERMISSION_ID = "eduPermissionId";

        /**
         * eduPermissionLimitId attribute
         */
        public static final String EDU_PERMISSION_LIMIT_ID = "eduPermissionLimitId";

        /**
         * eduPermissionScopeId attribute
         */
        public static final String EDU_PERMISSION_SCOPE_ID = "eduPermissionScopeId";

        /**
         * eduPermissionScopeName attribute
         */
        public static final String EDU_PERMISSION_SCOPE_NAME = "eduPermissionScopeName";

        /**
         * eduPermissionLimit attribute
         */
        public static final String EDU_PERMISSION_LIMIT = "eduPermissionLimit";
    }
}