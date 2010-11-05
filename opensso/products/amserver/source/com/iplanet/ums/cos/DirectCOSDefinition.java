/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2005 Sun Microsystems Inc. All Rights Reserved
 *
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at
 * https://opensso.dev.java.net/public/CDDLv1.0.html or
 * opensso/legal/CDDLv1.0.txt
 * See the License for the specific language governing
 * permission and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at opensso/legal/CDDLv1.0.txt.
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * $Id: DirectCOSDefinition.java,v 1.3 2008/06/25 05:41:47 qcheng Exp $
 *
 */

package com.iplanet.ums.cos;

import java.util.ArrayList;
import java.util.Collection;

import com.iplanet.services.ldap.Attr;
import com.iplanet.services.ldap.AttrSet;
import com.iplanet.services.ldap.ModSet;
import com.iplanet.services.util.I18n;
import com.iplanet.ums.CreationTemplate;
import com.iplanet.ums.Guid;
import com.iplanet.ums.IUMSConstants;
import com.iplanet.ums.PersistentObject;
import com.iplanet.ums.SearchResults;
import com.iplanet.ums.TemplateManager;
import com.iplanet.ums.UMSException;

/**
 * This class represents a Direct (or Classic) COS definition.
 * @supported.api
 */
public class DirectCOSDefinition extends PersistentObject implements
        ICOSDefinition {

    /**
     * NoArg Constructor
     */
    public DirectCOSDefinition() {
    }

    /**
     * Constructor with attribute set argument. The attribute set needs to
     * contain all the required attributes for this definition: name,
     * cosspecifier, cosattribute (with qualifier).
     * 
     * @param attrSet
     *            the attribute set
     * 
     * @throws UMSException
     *             The exception thrown from the DirectCOSDefinition constructor
     *             accepting a creation template and attribute set.
     * @see com.iplanet.ums.cos.DirectCOSDefinition#DirectCOSDefinition
     *      (CreationTemplate, AttrSet)
     * @supported.api
     */
    public DirectCOSDefinition(AttrSet attrSet) throws UMSException {
        this(TemplateManager.getTemplateManager().getCreationTemplate(_class,
                null), attrSet);
    }

    /**
     * Constructor with creation template and attribute set arguments.
     * 
     * @param template
     *            the Creation template.
     * @param attrSet
     *            the attribute set
     * 
     * @throws UMSException
     *             The exception thrown from the parent class constructor.
     * @see com.iplanet.ums.PersistentObject#PersistentObject (CreationTemplate,
     *      AttrSet)
     * @supported.api
     */
    public DirectCOSDefinition(CreationTemplate template, AttrSet attrSet)
            throws UMSException {
        super(template, attrSet);
    }

    /**
     * Sets the name of this COS.
     * 
     * @param name
     *            the name of this COS.
     * @supported.api
     */
    public void setName(String name) {
        setAttribute(new Attr(ICOSDefinition.DEFAULT_NAMING_ATTR, name));
    }

    /**
     * Returns the name of this COS.
     * 
     * @return the name of this COS
     * @supported.api
     */
    public String getName() {
        String attributeValue = null;
        Attr attribute = getAttribute(getNamingAttribute());
        if (attribute != null) {
            attributeValue = attribute.getValue();
        }
        return attributeValue;
    }

    /**
     * Adds the COS attribute to the definition. The COS attribute is the name
     * of the attribute for which you want to generate a value.
     * 
     * @param attrName
     *            The name of the attribute (for example, mailQuota)
     * @param qualifier
     *            An integer representing the following values: "default" - The
     *            server only returns a generated value if there is no
     *            corresponding attribute value stored with the entry.
     *            "override" - This value will always be generated by the server
     *            (it will override entry values). "operational" - the attribute
     *            will only be returned if it is requested in the search.
     *            "operational" can be combined with "default" or "override".
     *            These values are represented as integers in the ICOSDefinition
     *            interface.
     * 
     * @throws UMSException
     *             The exception thrown from the data layer.
     * @supported.api
     */
    public void addCOSAttribute(String attrName, int qualifier)
            throws UMSException {
        StringBuffer attrStr = new StringBuffer();
        if (qualifier < ICOSDefinition.minQualifier
                || qualifier > ICOSDefinition.maxQualifier) {
            String msg = i18n.getString(IUMSConstants.BAD_COS_ATTR_QUALIFIER);
            throw new UMSException(msg);
        }

        attrStr.append(attrName);
        attrStr.append(" ");
        attrStr.append(ICOSDefinition.qualifiers[qualifier]);
        modify(ICOSDefinition.COSATTRIBUTE, attrStr.toString(), ModSet.ADD);
    }

    /**
     * Removes the COS attribute from the definition.
     * 
     * @param attrName
     *            The name of the attribute to be removed.
     * @supported.api
     */
    public void removeCOSAttribute(String attrName) {
        modify(new Attr(ICOSDefinition.COSATTRIBUTE, attrName), ModSet.DELETE);
    }

    /**
     * Retrieves the COS attributes for this definition.
     * 
     * @return String[] A string array of COS attributes (for example,
     *         mailquota).
     * @supported.api
     */
    public String[] getCOSAttributes() {
        Attr attr = getAttribute(ICOSDefinition.COSATTRIBUTE);
        return attr.getStringValues();
    }

    /**
     * Sets the COS specifier. The COS specifier is the attribute value used in
     * conjunction with the template entry's DN, to identify the template entry.
     * 
     * @param cosSpecifier
     *            The COS specifier.
     * @supported.api
     */
    public void setCOSSpecifier(String cosSpecifier) {
        setAttribute(new Attr(COSSPECIFIER, cosSpecifier));
    }

    /**
     * Returns the COS specifier.
     * 
     * @return the COS specifier
     * 
     * @see DirectCOSDefinition#setCOSSpecifier(String cosSpecifier)
     * @supported.api
     */
    public String getCOSSpecifier() {
        String attributeValue = null;
        Attr attribute = getAttribute(COSSPECIFIER);
        if (attribute != null) {
            attributeValue = attribute.getValue();
        }
        return attributeValue;
    }

    /**
     * Adds a COS Template to this COS definition. This COS definition must be
     * persistent before adding the template.
     * 
     * @param cosTemplate
     *            The COS Template to be added.
     * 
     * @throws UMSException
     *             The exception thrown from the data layer.
     * @supported.api
     */
    public void addCOSTemplate(COSTemplate cosTemplate) throws UMSException {
        if (getGuid() == null) {
            String msg = i18n
                    .getString(IUMSConstants.DEFINITION_NOT_PERSISTENT);
            throw new UMSException(msg);
        }

        if (getAttribute(ICOSDefinition.COSTEMPLATEDN) == null) {
            this.modify(new Attr(ICOSDefinition.COSTEMPLATEDN, getGuid()
                    .getDn()), ModSet.ADD);
            this.save();
        }
        this.addChild(cosTemplate);
    }

    /**
     * Removes a COS Template from this COS definition.
     * 
     * @param name
     *            The name of the template to be removed.
     * 
     * @throws UMSException
     *             The exception thrown from the data layer.
     * @supported.api
     */
    public void removeCOSTemplate(String name) throws UMSException {
        Guid tGuid = new Guid(COSTemplate.DEFAULT_NAMING_ATTR + "=" + name
                + "," + this.getGuid());
        this.removeChild(tGuid);
    }

    /**
     * Removes all COS Templates from this COS definition.
     * 
     * @throws UMSException
     *             The exception thrown from the data layer.
     * @supported.api
     */
    public void removeCOSTemplates() throws UMSException {
        ArrayList aList = (ArrayList) getCOSTemplates();
        for (int i = 0; i < aList.size(); i++) {
            COSTemplate cosTemplate = (COSTemplate) aList.get(i);
            cosTemplate.remove();
        }
    }

    /**
     * Returns a template from this definition given the name of the template.
     * 
     * @param name
     *            The name of the template to be returned.
     * 
     * @return The COS template.
     * 
     * @throws COSNotFoundException
     *             The exception thrown if the COS template is not found.
     * @throws UMSException
     *             The exception thrown from the data layer.
     * @supported.api
     */
    public COSTemplate getCOSTemplate(String name) throws COSNotFoundException,
            UMSException {
        COSTemplate cosTemplate = null;
        String[] resultAttributes = { "*" };
        SearchResults sr = this.search("(" + COSTemplate.DEFAULT_NAMING_ATTR
                + "=" + name + ")", resultAttributes, null);
        while (sr.hasMoreElements()) {
            cosTemplate = (COSTemplate) sr.next();
            sr.abandon();
        }
        if (cosTemplate == null) {
            String msg = i18n.getString(IUMSConstants.COS_TEMPLATE_NOT_FOUND);
            throw new COSNotFoundException(msg);
        }
        return cosTemplate;
    }

    /**
     * Returns all templates for this definition.
     * 
     * @return a collection of COS templates
     * 
     * @throws UMSException
     *             The exception thrown from the data layer.
     * @supported.api
     */
    public Collection getCOSTemplates() throws UMSException {
        COSTemplate cosTemplate = null;
        Collection cosTemplates = new ArrayList();
        String[] resultAttributes = { "*" };
        SearchResults sr = this.search("(objectclass=costemplate)",
                resultAttributes, null);
        while (sr.hasMoreElements()) {
            cosTemplate = (COSTemplate) sr.next();
            cosTemplates.add(cosTemplate);
        }
        return cosTemplates;
    }

    private static final Class _class =
        com.iplanet.ums.cos.DirectCOSDefinition.class;

    private static I18n i18n = I18n.getInstance(IUMSConstants.UMS_PKG);
}
