(function ($) {
    $.blockUIFixed = function () {
        $.blockUI({
            css: {
                'border-color': 'transparent',
                'background-color': 'transparent',
                top: '50%',
                left: '50%',
                width: '',
                transform: 'translate(-50%,-50%)'
            },
            message: function () {
                if (loadingImageSource)
                    return $('<img/>', {src: loadingImageSource});

                return $('<div/>', {text: 'Communicating with the server..'});
            }
        });
    };

    $.onlyNumber = function (dom) {
        const value = '' + $(dom).val();
        let onlyNumber = '';
        for (let i = 0; i < value.length; i++) {
            const char = value.charAt(i);
            if (parseInt(char) >= 0 && parseInt(char) <= 9)
                onlyNumber += '' + char;
        }
        return onlyNumber;
    };

    $.addQueryString = function (url, data) {
        function concatDelimiter(url) {
            return url + (url.indexOf('?') >= 0 ? '&' : '?');
        }

        function addQueryString(url, key, value) {
            if (data === null || data === undefined)
                return url;

            if (value instanceof Array) {
                value.map(function (e) {
                    url = concatDelimiter(url) + encodeURIComponent(key) + '=' + encodeURIComponent(e);
                });
            } else {
                url = concatDelimiter(url) + encodeURIComponent(key) + '=' + encodeURIComponent(value);
            }

            return url;
        }

        if (!data || typeof data !== "object")
            return concatDelimiter(url) + encodeURIComponent((data === null || data === undefined) ? '' : data);

        for (let key in data) {
            if (data.hasOwnProperty(key)) {
                url = addQueryString(url, key, data[key]);
            }
        }

        return url;
    };

    $.fn.formDataObject = function () {
        const data = {};
        $(this).find('[name]').each(function () {
            const $this = $(this);
            const inputName = $this.attr('name');

            if (inputName.length > 0) {
                if ($this.attr('type') && $this.attr('type').toLowerCase() === "checkbox") {
                    if (!$this.is(':checked'))
                        return;

                    if (!data[inputName] || !(data[inputName] instanceof Array))
                        data[inputName] = [];

                    data[inputName].push($this.val());
                } else if ($this.attr('type') && $this.attr('type').toLowerCase() === "radio") {
                    if ($this.is(':checked')) {
                        data[inputName] = $this.val();
                    }
                } else {
                    data[inputName] = $this.val();
                }
            }
        });

        return data;
    };

    $.fn.asLeanModal = function () {
        return this.each(function () {
            const $this = $(this);
            $this.leanModal($this.data('options') ? $this.data('options').toObject() : {});
        });
    };

    $.fn.asInputFloat = function () {
        return this.each(function () {
            if (!$(this).is('input') && !$(this).is('textarea'))
                return;
            $(this).bind('focusout', (function () {
                const value = '' + $(this).val();
                let result = '';
                let firstComma = false;
                for (let i = 0; i < value.length; i++) {
                    const char = value.charAt(i);
                    if (parseInt(char) >= 0 && parseInt(char) <= 9)
                        result += '' + char;
                    if (char === '.' && !firstComma) {
                        firstComma = true;
                        result += '' + char;
                    }
                }

                if (result.substr(result.length - 1, 1) === '.')
                    result = result.substr(0, result.length - 1);

                return $(this).val(result);
            }));
        });
    };

    $.fn.asInputNumerical = function () {
        return this.each(function () {
            if (!$(this).is('input') && !$(this).is('textarea'))
                return;
            $(this).bind('keyup', (function () {
                return $(this).val($.onlyNumber(this));
            }));
        });
    };

    /**
     * ??-???-????
     * ??-????-????
     * ???-????-????
     * */
    $.fn.asInputPhone = function () {
        return this.each(function () {
            if (!$(this).is('input') && !$(this).is('textarea'))
                return;
            $(this).bind('keyup', (function () {
                const numbers = $.onlyNumber(this);

                let result = '';
                for (let i = 0; i < numbers.length && i < 11; i++) {
                    if ((numbers.length <= 9 && (i === 2 || i === 5))
                        || (numbers.length === 10 && (i === 2 || i === 6))
                        || (numbers.length > 10 && (i === 3 || i === 7)))
                        result += '-';

                    result += numbers.charAt(i);
                }

                return $(this).val(result);
            }));
        });
    };

    $.fn.asInputRestrictLength = function () {
        return this.each(function () {
            const $this = $(this);

            if (!$this.is('input') && !$this.is('textarea'))
                return;

            const options = $this.data('length').toObject();
            const maxLength = parseInt(options.length);

            let container = $this;
            const greatContainerCount = parseInt(options.container);
            if (!isNaN(greatContainerCount) && isFinite(greatContainerCount)) {
                for (let i = 0; i < greatContainerCount; i++)
                    container = container.parent();
            } else if (options.container != null && options.container.length > 0) {
                container = $this.closest(options.container);
            } else {
                container = $('body');
            }

            const target = container.find(options.target);
            if (!isNaN(maxLength) && isFinite(maxLength) && maxLength > 0) {
                $this.bind('keyup keydown keypress', checkLength);
                checkLength();
            }

            function checkLength() {
                const length = $this.val().length;
                if (length > maxLength)
                    $this.val($this.val().substring(0, maxLength));

                target.text(length > maxLength ? maxLength : length);
            }
        });
    };

    $.ajaxLoad = function (url, target, method, data, formData) {
        $.blockUIFixed();

        return $.ajax({
            url: $.addQueryString($.addQueryString(url, $.extend({ajaxLoaderReturnTo: location.href}, method === 'get' ? data : {})), {____t: new Date().getTime()}),
            cache: false,
            data: method === 'get' ? null : formData,
            type: method,
            processData: false,
            contentType: false,
            dataType: 'html'
        }).done(function (html) {
            target.empty();

            const mixedNodes = $.parseHTML(html, null, true);
            for (let i = 0; i < mixedNodes.length; i++) {
                const node = $(mixedNodes[i]);
                if (node.is('script')) {
                    const id = node.attr('id');
                    if (id) $(document.getElementById(id)).remove();
                    $('body').append(node);
                } else {
                    node.appendTo(target)
                        .bindHelpers();
                }
            }
        }).fail(function (e) {
            if (e.status === 401) alert("접근 권한이 없습니다. 로그인 정보를 확인하세요.");
            else alert("An error occurred. Please try again later");
        }).always(function () {
            $.unblockUI();
        });
    };

    $.ajaxLoadForm = function (_this, isForm) {
        const ajaxLoaderData = $.extend({target: _this}, $(_this).data());
        const target = $(ajaxLoaderData.target);
        const url = isForm ? _this.action ? _this.action : $(_this).data('action') : _this.href ? _this.href : $(_this).data('href');
        const method = isForm ? _this.method ? _this.method : $(_this).data('method') : 'get';

        const data = {};
        const formData = new FormData();

        $($(_this).find('[name]').filter('select, textarea, input').serializeArray()).each(function () {
            data[this.name] = this.value;
            formData.append(this.name, this.value);
        });

        $(_this).find('input[type="file"]').each(function () {
            const input = this;
            $.each(input.files, function (file) {
                formData.append(input.name, file, file.name);
            });
        });

        return $.ajaxLoad(url, target, method, data, formData);
    };

    $.fn.asAjaxLoader = function () {
        return this.each(function () {
            const _this = this;
            const isForm = $(this).is('form') || $(this).hasClass('-as-form');

            $(this).on(isForm ? 'submit' : 'click', function (event) {
                if (event.currentTarget !== _this) return;
                event.preventDefault();

                $.ajaxLoadForm(_this, isForm);
            });
        });
    };

    $.fn.asDatepicker = function () {
        return this.each(function () {
            const $this = $(this);
            const format = $this.data('format');
            try {
                $this.datepicker("destroy");
            } catch (ignored) {
            }
            $this.removeAttr('id');
            $this.removeClass('hasDatepicker');
            const options = $this.data('options') ? $this.data('options').toObject() : {};
            $this.datepicker($.extend(options, {
                dateFormat: format != null && format.length > 0 ? format : options.dateFormat != null ? options.dateFormat : 'yy-mm-dd'
            }));
        });
    };

    $.fn.asLeadSelector = function () {
        if ($(this).is('input[type=checkbox]'))
            return this.change(function () {
                if ($(this).is(":checked"))
                    $($(this).data('target')).prop("checked", true);
                else
                    $($(this).data('target')).prop("checked", false);

                const post = $(this).data('post');
                if (post != null) {
                    const args = $(this).data('args');
                    eval(post)(this, args ? args.toObject() : null);
                }
            });
        else
            return this.click(function () {
                const status = $(this).data('status');
                if (status === 'on') {
                    $($(this).data('target')).prop("checked", false);
                    $(this).data('status', 'off');
                } else {
                    $($(this).data('target')).prop("checked", true);
                    $(this).data('status', 'on');
                }

                const post = $(this).data('post');
                if (post != null) {
                    const args = $(this).data('args');
                    eval(post)(this, args ? args.toObject() : null);
                }
            });
    };

    $.fn.animateCount = function () {
        return this.each(function () {
            $(this).prop('Count', 0).animate({
                Counter: $(this).text()
            }, {
                duration: 3000,
                easing: 'swing',
                step: function (now) {
                    $(this).text(Math.ceil(now));
                }
            })
        });
    };

    $.fn.asJsonData = function () {
        const deferred = jQuery.Deferred();
        const data = {};
        const files = {};

        const emptynull = $(this).attr('data-emptynull') === "true";

        function set(name, multiple, value) {
            const members = name.split('.');

            for (let target = data, i = 0; i < members.length; i++) {
                const last = i === members.length - 1;
                const member = members[i];
                const checkMap = /([0-9a-zA-Z_]+)\[([^\]]+)]/.exec(member);
                if (checkMap) {
                    const key = checkMap[1];
                    const attr = checkMap[2];
                    if (target[key] === undefined) target[key] = {};
                    target = target[key];
                    if (last) {
                        if (multiple) if (!target[attr]) target[attr] = [];
                        if (multiple) target[attr].push(value);
                        else target[attr] = value;
                    } else {
                        if (target[attr] === undefined) target[attr] = {};
                        target = target[attr];
                    }
                } else {
                    if (last) {
                        if (multiple) if (!target[member]) target[member] = [];
                        if (multiple) target[member].push(value);
                        else target[member] = value;
                    } else {
                        if (target[member] === undefined) target[member] = {};
                        target = target[member];
                    }
                }
            }
        }

        function convertFileToData() {
            for (let name in files) {
                if (files.hasOwnProperty(name)) {
                    let file = files[name];
                    if (file instanceof Array) {
                        if (!file.length) {
                            delete files[name];
                            continue;
                        }
                        file = file.splice(0, 1)[0];
                    } else {
                        delete files[name];
                    }

                    readFile(file).done(function (result) {
                        set(name, files[name] instanceof Array, {
                            data: result.data.split(',')[1],
                            fileName: extractFilename(result.fileName)
                        });
                        convertFileToData();
                    }).fail(function (error) {
                        deferred.reject('[' + name + '] ' + error);
                    });
                }
                return;
            }
            deferred.resolve(data);
        }

        $(this).find('[name]').each(function () {
            const $this = $(this);
            const multiple = $this.is('[multiple]') || $this.is('[data-multiple]');
            const name = $(this).attr('name');
            const value = $this.val();

            switch (($this.attr('type') || 'text').toLowerCase()) {
                case 'file': {
                    if (!this.files) break;
                    if (multiple) {
                        if (!files[name]) files[name] = [];
                        $.each(this.files, function (i, file) {
                            files[name].push(file);
                        });
                    } else if (this.files && this.files[0]) {
                        files[name] = this.files[0];
                    }
                    break;
                }
                case 'radio':
                case 'checkbox': {
                    if ($this.is(':checked'))
                        set(name, multiple, value);
                    break;
                }
                default: {
                    if (!emptynull || value)
                        set(name, multiple, value);
                }
            }
        });

        convertFileToData();
        return deferred;
    };

    $.fn.asJsonSubmit = function () {
        return this.submit(function (event) {
            event.preventDefault();
            return submitJsonData(this);
        });
    };

})(jQuery);