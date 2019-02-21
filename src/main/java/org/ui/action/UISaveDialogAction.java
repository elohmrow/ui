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
package org.ui.action;

import info.magnolia.ui.api.action.ActionExecutionException;
import info.magnolia.ui.dialog.action.SaveDialogAction;
import info.magnolia.ui.dialog.action.SaveDialogActionDefinition;
import info.magnolia.ui.form.EditorCallback;
import info.magnolia.ui.form.EditorValidator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.v7.data.Item;

/**
 * org.ui.action.
 */
public class UISaveDialogAction<T extends SaveDialogActionDefinition> extends SaveDialogAction {
    private static final Logger log = LoggerFactory.getLogger(UISaveDialogAction.class);
    protected final Item item;
    protected final EditorCallback callback;
    protected final EditorValidator validator;

    public UISaveDialogAction(T definition, Item item, EditorValidator validator, EditorCallback callback) {
        super(definition, item, validator, callback);
        this.item = item;
        this.callback = callback;
        this.validator = validator;
    }

    @Override
    public void execute() throws ActionExecutionException {
        log.warn("\n==============\nEmail Sent!\n==============\n");
        super.execute();
    }
}
