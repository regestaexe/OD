/*

This file is part of Ext JS 4

Copyright (c) 2011 Sencha Inc

Contact:  http://www.sencha.com/contact

GNU General Public License Usage
This file may be used under the terms of the GNU General Public License version 3.0 as published by the Free Software Foundation and appearing in the file LICENSE included in the packaging of this file.  Please review the following information to ensure the GNU General Public License version 3.0 requirements will be met: http://www.gnu.org/copyleft/gpl.html.

If you are unsure which license is appropriate for your use, please contact the sales department at http://www.sencha.com/contact.

*/
/**
 * @class FeedViewer.FeedPanel
 * @extends Ext.panel.Panel
 *
 * Shows a list of available feeds. Also has the ability to add/remove and load feeds.
 *
 * @constructor
 * Create a new Feed Panel
 * @param {Object} config The config object
 */

Ext.define('message.MessageTypesPanel', {
    extend: 'Ext.panel.Panel',
    animCollapse: true,
    layout: 'fit',
    title: 'Categorie',

    initComponent: function(){
        Ext.apply(this, {
            items: this.createView()
        });
        this.addEvents(
            'feedremove',
            'feedselect'
        );
        this.callParent(arguments);
    },
    createView: function(){
        this.view = Ext.create('widget.dataview', {
            store: Ext.create('Ext.data.Store', {
                model: 'MessageType',
                data: this.feeds
            }),
            selModel: {
                mode: 'SINGLE',
                listeners: {
                    scope: this,
                    selectionchange: this.onSelectionChange
                }
            },
            listeners: {
                scope: this,
                contextmenu: this.onContextMenu,
                viewready: this.onViewReady
            },
            trackOver: true,
            cls: 'feed-list',
            itemSelector: '.feed-list-item',
            overItemCls: 'feed-list-item-hover',
            tpl: '<tpl for="."><div class="feed-list-item">{title}</div></tpl>'
        });
        return this.view;
    },

    onViewReady: function(){
        this.view.getSelectionModel().select(this.view.store.first());
    },
    onSelectionChange: function(){
        var selected = this.getSelectedItem();
       // this.toolbar.getComponent('remove').setDisabled(!selected);
        this.loadFeed(selected);
    },
    onLoadClick: function(){
        this.loadFeed(this.menu.activeFeed);
    },
    loadFeed: function(rec){
    	alert('click');
        if (rec) {
            this.fireEvent('feedselect', this, rec.get('title'), rec.get('url'));
        }
    },
    getSelectedItem: function(){
        return this.view.getSelectionModel().getSelection()[0] || false;
    },
    onContextMenu: function(view, index, el, event){
        var menu = this.menu;

        event.stopEvent();
        menu.activeFeed = view.store.getAt(index);
        menu.showAt(event.getXY());
    }
});

