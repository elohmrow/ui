/**
 * This file Copyright (c) 2019 Magnolia International
 * Ltd.  (http://www.magnolia-cms.com). All rights reserved.
 *
 *
 * This file is dual-licensed under both the Magnolia
 * Network Agreement and the GNU General Public License.
 * You may elect to use one or the other of these licenses.
 *
 * This file is distributed in the hope that it will be
 * useful, but AS-IS and WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, or NONINFRINGEMENT.
 * Redistribution, except as permitted by whichever of the GPL
 * or MNA you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU General
 * Public License, Version 3, as published by the Free Software
 * Foundation.  You should have received a copy of the GNU
 * General Public License, Version 3 along with this program;
 * if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * 2. For the Magnolia Network Agreement (MNA), this file
 * and the accompanying materials are made available under the
 * terms of the MNA which accompanies this distribution, and
 * is available at http://www.magnolia-cms.com/mna.html
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.ui.field;

import info.magnolia.cms.util.QueryUtil;
import info.magnolia.jcr.iterator.FilteringPropertyIterator;
import info.magnolia.jcr.predicate.JCRMgnlPropertyHidingPredicate;
import info.magnolia.objectfactory.ComponentProvider;
import info.magnolia.ui.api.context.UiContext;
import info.magnolia.ui.api.i18n.I18NAuthoringSupport;
import info.magnolia.ui.form.field.definition.SelectFieldOptionDefinition;
import info.magnolia.ui.form.field.factory.TwinColSelectFieldFactory;
import info.magnolia.ui.form.field.transformer.Transformer;
import info.magnolia.ui.vaadin.integration.jcr.JcrNodeAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.v7.data.Item;
import com.vaadin.v7.ui.AbstractSelect;
import com.vaadin.v7.ui.TwinColSelect;

/**
 * GUI builder for the Tag Management field.
 */
public class TagManagementFieldFactory extends TwinColSelectFieldFactory<TagManagementFieldDefinition> {
    /**
     * Internal bean to represent basic tag data.
     */
    private static class Tag {
        public String name;
        public String uuid;

        public Tag(String name, String uuid) {
            this.name = name;
            this.uuid = uuid;
        }
    }

    private static final Logger log = LoggerFactory.getLogger(TagManagementFieldFactory.class);
    private ComponentProvider componentProvider;

    @Inject
    public TagManagementFieldFactory(TagManagementFieldDefinition definition, Item relatedFieldItem, UiContext uiContext, I18NAuthoringSupport i18nAuthoringSupport, ComponentProvider componentProvider) {
        super(definition, relatedFieldItem, uiContext, i18nAuthoringSupport, componentProvider);

        definition.setOptions(getOptions());
        this.componentProvider = componentProvider;
    }

    /**
     * @deprecated since 5.4.7 - use {@link #TagManagementFieldFactory(TagManagementFieldDefinition, Item, UiContext, I18NAuthoringSupport, ComponentProvider)} instead.
     */
    @Deprecated
    public TagManagementFieldFactory(TagManagementFieldDefinition definition, Item relatedFieldItem, ComponentProvider componentProvider) {
        this(definition, relatedFieldItem, null, componentProvider.getComponent(I18NAuthoringSupport.class), componentProvider);
    }

    @Override
    protected AbstractSelect createFieldComponent() {
        super.createFieldComponent();
        select.setMultiSelect(true);
        select.setNullSelectionAllowed(true);
        return select;
    }

    @Override
    protected AbstractSelect createSelectionField() {
        return new TwinColSelect();
    }

    /**
     * Returns the available tags with those already assigned marked selected, according to the current node.
     */
    @Override
    public List<SelectFieldOptionDefinition> getOptions() {
        List<SelectFieldOptionDefinition> options = new ArrayList<SelectFieldOptionDefinition>();
        List<Tag> allTags = getAllTags(); // name,uuid
        Set<String> assignedTags = getAssignedTags();
        for (Tag tag : allTags) {
            SelectFieldOptionDefinition option = new SelectFieldOptionDefinition();
            option.setValue(tag.uuid);
            option.setLabel(tag.name);
            if (assignedTags.contains(tag.uuid)) {
                option.setSelected(true);
            }
            options.add(option);
        }
        return options;
    }

    private List<Tag> getAllTags() {
        List<Tag> tags = new ArrayList<Tag>();
        try {
            NodeIterator ni = QueryUtil.search("tags", "SELECT * FROM [mgnl:tag] ORDER BY name()");
            while (ni.hasNext()) {
                Node n = ni.nextNode();
                String name = n.getName();
                String uuid = n.getIdentifier();
                tags.add(new Tag(name, uuid));
            }
        } catch (RepositoryException e) {
            log.error("Cannot read tags from the [tags] workspace.", e);
        }
        return tags;
    }

    private Set<String> getAssignedTags() {
        Set<String> tags = new HashSet<String>();
        try {
            Node mainNode = ((JcrNodeAdapter) item).getJcrItem();
            if (mainNode.hasNode("tags")) {
                Node tagsNode = mainNode.getNode("tags");
                if (tagsNode == null) {
                    // shouldn't happen, just in case
                    return tags;
                }
                for (PropertyIterator iter = new FilteringPropertyIterator(tagsNode.getProperties(), new JCRMgnlPropertyHidingPredicate()); iter.hasNext(); ) {
                    Property p = iter.nextProperty();
                    tags.add(p.getString());
                }
            }
        } catch (RepositoryException re) {
            log.error("Cannot read assigned tags.", re);
        }
        return tags;
    }

    /**
     * Create a new Instance of {@link Transformer}.
     */
    @Override
    protected Transformer<?> initializeTransformer(Class<? extends Transformer<?>> transformerClass) {
        return this.componentProvider.newInstance(transformerClass, item, definition, HashSet.class, getAssignedTags(), "tags");
    }
}