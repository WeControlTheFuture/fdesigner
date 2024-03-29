/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.fdesigner.felix;

import java.util.ArrayList;
import java.util.List;

import org.fdesigner.framework.framework.Version;
import org.fdesigner.framework.framework.namespace.BundleNamespace;
import org.fdesigner.framework.framework.namespace.IdentityNamespace;
import org.fdesigner.framework.framework.namespace.PackageNamespace;
import org.fdesigner.framework.resource.Capability;
import org.fdesigner.framework.resource.Namespace;
import org.fdesigner.framework.resource.Requirement;
import org.fdesigner.framework.resource.Resource;

public class Util
{
    public static String getSymbolicName(Resource resource)
    {
        List<Capability> caps = resource.getCapabilities(null);
        for (Capability cap : caps)
        {
            if (cap.getNamespace().equals(IdentityNamespace.IDENTITY_NAMESPACE))
            {
                return cap.getAttributes().get(IdentityNamespace.IDENTITY_NAMESPACE).toString();
            }
        }
        return null;
    }

    public static Version getVersion(Resource resource)
    {
        List<Capability> caps = resource.getCapabilities(null);
        for (Capability cap : caps)
        {
            if (cap.getNamespace().equals(IdentityNamespace.IDENTITY_NAMESPACE))
            {
                return (Version)
                    cap.getAttributes().get(IdentityNamespace.CAPABILITY_VERSION_ATTRIBUTE);
            }
        }
        return null;
    }

    public static boolean isFragment(Resource resource)
    {
        List<Capability> caps = resource.getCapabilities(null);
        for (Capability cap : caps)
        {
            if (cap.getNamespace().equals(IdentityNamespace.IDENTITY_NAMESPACE))
            {
                String type = (String)
                    cap.getAttributes().get(IdentityNamespace.CAPABILITY_TYPE_ATTRIBUTE);
                return (type != null) && type.equals(IdentityNamespace.TYPE_FRAGMENT);
            }
        }
        return false;
    }

    public static boolean isOptional(Requirement req)
    {
        String resolution = req.getDirectives().get(Namespace.REQUIREMENT_RESOLUTION_DIRECTIVE);
        return Namespace.RESOLUTION_OPTIONAL.equalsIgnoreCase(resolution);
    }

    public static boolean isMultiple(Requirement req)
    {
    	return Namespace.CARDINALITY_MULTIPLE.equals(req.getDirectives()
            .get(Namespace.REQUIREMENT_CARDINALITY_DIRECTIVE)) && !isDynamic(req);
    }

    public static boolean isDynamic(Requirement req)
    {
    	return PackageNamespace.RESOLUTION_DYNAMIC.equals(req.getDirectives()
            .get(Namespace.REQUIREMENT_RESOLUTION_DIRECTIVE));
    }

    public static boolean isReexport(Requirement req)
    {
        return BundleNamespace.VISIBILITY_REEXPORT.equals(req.getDirectives()
            .get(BundleNamespace.REQUIREMENT_VISIBILITY_DIRECTIVE));
    }

    public static List<Requirement> getDynamicRequirements(List<Requirement> reqs)
    {
        List<Requirement> result = new ArrayList<Requirement>();
        if (reqs != null)
        {
            for (Requirement req : reqs)
            {
                String resolution = req.getDirectives()
                    .get(PackageNamespace.REQUIREMENT_RESOLUTION_DIRECTIVE);
                if ((resolution != null)
                    && resolution.equals(PackageNamespace.RESOLUTION_DYNAMIC))
                {
                    result.add(req);
                }
            }
        }
        return result;
    }
}