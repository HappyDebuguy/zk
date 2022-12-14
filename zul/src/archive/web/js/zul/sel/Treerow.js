/* Treerow.js

	Purpose:

	Description:

	History:
		Wed Jun 10 15:32:43     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A treerow.
 * <p>Default {@link #getZclass}: z-treerow
 */
zul.sel.Treerow = zk.$extends(zul.Widget, {
	/** Returns the {@link Tree} instance containing this element.
	 * @return Tree
	 */
	getTree: function () {
		return this.parent ? this.parent.getTree() : null;
	},
	/** Returns the level this cell is. The root is level 0.
	 * @return int
	 */
	getLevel: function () {
		return this.parent ? this.parent.getLevel() : 0;
	},
	/** Returns the {@link Treechildren} associated with this
	 * {@link Treerow}.
	 * @return Treechildren
	 */
	getLinkedTreechildren: function () {
		return this.parent ? this.parent.treechildren : null;
	},
	domClass_: function (no) {
		var scls = this.$supers('domClass_', arguments),
			p = this.parent;
		if (p && (!no || !no.zclass)) {
			if (p.isDisabled())
				scls += (scls ? ' ' : '') + this.$s('disabled');
			if (p.isSelected())
				scls += (scls ? ' ' : '') + this.$s('selected');
		}
		return scls;
	},
	domTooltiptext_: function () {
		return this._tooltiptext || this.parent._tooltiptext || this.parent.parent._tooltiptext;
	},
	//@Override
	domStyle_: function (no) {
		// patch the case that treerow is hidden by treeitem visibility
		return ((this.parent && !this.parent._isRealVisible() && this.isVisible()) ?
				'display:none;' : '') + this.$supers('domStyle_', arguments);
	},
	//@Override
	removeChild: function (child) {
		for (var w = child.firstChild; w;) {
			var n = w.nextSibling; //remember, since remove will null the link
			child.removeChild(w); //deep first
			w = n;
		}
		this.$supers('removeChild', arguments);
	},
	//@Override
	doClick_: function (evt) {
		var ti = this.parent,
			tg = evt.domTarget;
		if (tg == this.$n('open') || tg == this.$n('icon')) {
			ti.setOpen(!ti._open);
			evt.stop();
			this.getTree().focus();
		} else if (!ti.isDisabled())
			this.$supers('doClick_', arguments);
	},
	//@Override
	scrollIntoView: function () {
		var bar = this.getTree()._scrollbar;
		if (bar) {
			bar.syncSize();
			bar.scrollToElement(this.$n());
		} else {
			this.$supers('scrollIntoView', arguments);
		}
	},
	deferRedrawHTML_: function (out) {
		out.push('<tr', this.domAttrs_({domClass: 1}), ' class="z-renderdefer"></tr>');
	},
	bind_: function () {
		this.$supers(zul.sel.Treerow, 'bind_', arguments);
		zWatch.listen({onResponse: this});
	},
	unbind_: function () {
		zWatch.unlisten({onResponse: this});
		this.$supers(zul.sel.Treerow, 'unbind_', arguments);
	},
	onChildAdded_: function (child) {
		this.$supers('onChildAdded_', arguments);
		// ZK-5107
		this._shallCheckClearCache = true;
	},
	onChildRemoved_: function (child) {
		this.$supers('onChildRemoved_', arguments);
		// ZK-5107
		this._shallCheckClearCache = true;
	},
	onResponse: function () {
		if (this._shallCheckClearCache) {
			this._shallCheckClearCache = false;
			let p = this.getTree();
			if (p && p.isCheckmark()) {
				this.clearCache();
			}
		}
	}
});