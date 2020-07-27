(function ($) {
    "use strict";

    $(window).on('load', function () {
        $('body').bindHelpers();
    });

    $(document).on("click", 'a[href="#"]', function (e) {
        e.preventDefault();
    });
})(jQuery);