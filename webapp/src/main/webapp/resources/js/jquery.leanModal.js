// leanModal v1.1 by Ray Stone - http://finelysliced.com.au
// Dual licensed under the MIT and GPL

// Modify by tinywind

(function ($) {
    window.leanModal = {
        _stackModal: [],
        _stackZIndex: [],
        _zIndexBackground: 910000,
        _zIndexBase: 920000,
        _zIndexInterval: 10,
        getModalZIndex: function (modal) {
            for (let i = 0; i < this._stackModal.length; i++)
                if (this._stackModal[i] === modal) {
                    console.error("already contains modal");
                    return null;
                }
            this._stackModal.push(modal);

            if (this._stackZIndex.length === 0) {
                const zIndex = this._zIndexBase;
                this._stackZIndex.push(zIndex);
                return zIndex;
            } else {
                zIndex = this._stackZIndex[this._stackZIndex.length - 1] + this._zIndexInterval;
                this._stackZIndex.push(zIndex);
                return zIndex;
            }
        },
        checkHideLeanOverlayContainer: function (modal) {
            for (let i = 0; i < this._stackModal.length; i++)
                if (this._stackModal[i] === modal) {
                    this._stackModal.splice(i, 1);
                    this._stackZIndex.splice(i, 1);
                }

            return this._stackModal.length === 0;
        }
    };

    $.fn.extend({
        leanModal: function (options) {
            if (options === 'destroy') {
                return this.each(function () {
                    const objects = this['lean-modal-objects'];
                    if (objects != null && objects instanceof Object) {
                        if (objects.modal != null && objects.modal.length > 0)
                            window.leanModal.checkHideLeanOverlayContainer(objects.modal[0]);

                        for (let key in objects)
                            if (objects.hasOwnProperty(key))
                                $(objects[key]).remove();
                    }
                });
            }

            const defaults = {closeButton: null};
            options = $.extend(defaults, options);
            let overlayContainer = $('#lean-overlay-container');
            if (overlayContainer.length === 0)
                overlayContainer = $('<div/>', {
                    id: 'lean-overlay-container'
                }).css({
                    position: 'fixed',
                    top: 0,
                    bottom: 0,
                    left: 0,
                    right: 0,
                    display: 'none',
                    'z-index': window.leanModal._zIndexBackground + ""
                }).appendTo($('body'));

            return this.each(function () {
                const overlay = $('<div/>', {
                    'class': 'lean-overlay'
                }).css({
                    display: "none",
                    opacity: 0.5,
                    position: "absolute",
                    top: 0,
                    bottom: 0,
                    left: 0,
                    right: 0,
                    'background-color': 'black'
                });
                overlayContainer.append(overlay);

                function showModal(modal, overlay) {
                    overlayContainer.show();
                    const zIndex = window.leanModal.getModalZIndex(modal[0]);
                    const top = (window.innerHeight - modal.outerHeight()) / 2;
                    modal.css({
                        'z-index': zIndex,
                        top: top < 0 ? "0px" : top + "px",
                        'max-height': (window.innerHeight - (top < 0 ? 0 : top)) + "px",
                        'margin-left': -(modal.outerWidth() / 2) + "px"
                    }).show()
                        .focus();
                    overlay.css({'z-index': zIndex - 1}).show();

                    $('body').addClass('no-scroll');
                }

                function closeModal(modal, overlay) {
                    modal.hide();
                    overlay.hide();

                    window.leanModal.checkHideLeanOverlayContainer(modal[0]);

                    const leanOverlayList = overlayContainer.find('.lean-overlay');
                    const numHiddenLeanOverlay = leanOverlayList.not(':hidden').length;
                    if (numHiddenLeanOverlay <= 0) {
                        overlayContainer.hide();
                        $('body').removeClass('no-scroll');
                    }
                }

                const href = $(this).attr("href");
                const modal = $(href != null ? href : $(this).attr('data-href'));
                modal.detach().appendTo(overlayContainer);

                if (modal.css('position') === 'fixed') {
                    modal.css({
                        display: "none",
                        'overflow-y': 'auto'
                    });
                } else {
                    modal.css({
                        display: "none",
                        'overflow-y': 'auto',
                        position: "absolute",
                        // bottom: 0,
                        left: 50 + "%",
                        'margin-left': -(modal.outerWidth() / 2) + "px"
                    });
                }

                $(this).click(function () {
                    showModal(modal, overlay);
                });

                $(options.closeButton).click(function () {
                    closeModal(modal, overlay);
                });

                this['lean-modal-objects'] = {modal: modal, overlay: overlay}
            });
        }
    })
})(jQuery);
