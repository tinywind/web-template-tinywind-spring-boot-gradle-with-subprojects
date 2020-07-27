(function ($) {
    function findAndMe(selector, context) {
        return context.find(selector).add(context.filter(selector));
    }

    $.fn.bindHelpers = function () {
        findAndMe('.-ajax-loader', this).asAjaxLoader();
        findAndMe('.-json-submit', this).asJsonSubmit();
        findAndMe('.-lead-selector', this).asLeadSelector();
        findAndMe('.-lean-modal', this).asLeanModal();
        findAndMe('.-input-float', this).asInputFloat();
        findAndMe('.-input-numerical', this).asInputNumerical();
        findAndMe('.-input-phone', this).asInputPhone();
        findAndMe('.-input-restrict-length', this).asInputRestrictLength();
        findAndMe('.-count', this).animateCount();
        findAndMe('input[type=hidden][name^=_][value]', this).remove();
        findAndMe('.modal-close', this).each(function () {
            $(this).click(function () {
                $(this).closest('.modal').modalHide();
            });
        });
        findAndMe('.selectable-only tr', this).not('.ui').click(function () {
            if (!$(this).attr('data-id')) return;

            $(this).siblings().removeClass('active');

            if ($(this).hasClass('active')) {
                $(this).removeClass('active');
                $('.-control-entity[data-entity="' + $(this).closest('table').attr('data-entity') + '"]').hide();
            } else {
                $(this).addClass('active');
                $('.-control-entity[data-entity="' + $(this).closest('table').attr('data-entity') + '"]').show();
            }
        });
        findAndMe('.selectable tr', this).not('.ui').click(function () {
            if (!$(this).attr('data-id')) return;

            const table = $(this).closest('table');
            const entityName = table.attr('data-entity');
            $(this).toggleClass('active');
            const activeTrList = table.find('tr.active');
            if (activeTrList.length > 0) {
                $('.-control-entity[data-entity="' + entityName + '"]').show();
            } else {
                $('.-control-entity[data-entity="' + entityName + '"]').hide();
            }
        });

        /* jquery-ui.datepicker 호출. id 재생성되므로, 기존 id 속성을 삭제시킨다. */
        findAndMe('.-datepicker', this).asDatepicker();

        return this;
    };
})($ || jQuery);