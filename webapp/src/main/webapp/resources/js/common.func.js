const restUtils = (function ($) {
    function call(url, data, type, urlParam, noneBlockUi) {
        if (!noneBlockUi) $.blockUIFixed();

        return $.ajax({
            type: type,
            url: $.addQueryString(urlParam ? $.addQueryString(url, data) : url, {____t: new Date().getTime()}),
            data: urlParam ? null : typeof data === "object" ? JSON.stringify(data) : data,
            contentType: "application/json; charset=utf-8",
            dataType: "json"
        }).always(function () {
            if (!noneBlockUi) $.unblockUI();
        });
    }

    return {
        get: function (url, data, noneBlockUi) {
            return call(url, data, 'GET', true, noneBlockUi);
        },
        post: function (url, data, noneBlockUi) {
            return call(url, data, 'POST', false, noneBlockUi);
        },
        put: function (url, data, noneBlockUi) {
            return call(url, data, 'PUT', false, noneBlockUi);
        },
        patch: function (url, data, noneBlockUi) {
            return call(url, data, 'PATCH', false, noneBlockUi);
        },
        delete: function (url, data, noneBlockUi) {
            return call(url, data, 'DELETE', true, noneBlockUi);
        }
    }
})(jQuery);

function submitJsonData(form) {
    const _this = form;
    const $form = $(form);

    const deferred = $.Deferred();

    $form.asJsonData().done(function (data) {
        const before = $form.data('before');
        if (before) {
            let bReturn = window[before].apply(this, [data]);
            if (typeof bReturn !== 'undefined' && !!!bReturn) return false;
        }

        restUtils[($form.data('method') || _this.method || 'get').toLowerCase()].apply(_this, [_this.action, data, $form.data('noneBlockUi')]).done(function (data) {
            if (window.activeLogging) console.log(data);
            submitDone(_this, data, $form.data('done'));

            deferred.resolve(data, $form);
        }).fail(function (e) {
            try {
                const data = JSON.parse(e.responseText);
                if (window.activeLogging) console.log(data);
                submitDone(_this, data);
            } catch (exception) {
                if (e.status === 404)
                    return alert("Failed to request processing. Please retry it.");
                alert("error[" + e.status + "]: " + e.statusText);
            }

            deferred.reject(e);
        });
    }).fail(function (error) {
        alert(error);
    });

    return deferred.promise();
}

function readFile(file) {
    const deferred = jQuery.Deferred();
    if (!file) {
        deferred.reject("'file' is invalid.");
        return deferred;
    }

    const reader = new FileReader();
    reader.addEventListener("load", function () {
        deferred.resolve({
            data: reader.result,
            fileName: file.name
        });
    }, false);
    reader.addEventListener("error", function (error) {
        deferred.reject(error);
    }, false);
    reader.readAsDataURL(file);

    return deferred;
}

function extractFilename(path) {
    const lastSlash = path.lastIndexOf('/');
    const lastReverseSlash = path.lastIndexOf('\\');
    return path.substr(Math.max(lastSlash, lastReverseSlash) + 1);
}

// 출처: http://stackoverflow.com/questions/2901102/how-to-print-number-with-commas-as-thousands-separators-in-javascript
function numberWithCommas(n, floatCount) {
    const parts = n.toString().split(".");

    const float = parts[1]
        ? floatCount != null && floatCount >= 0 ? parts[1].toString().substring(0, floatCount) : parts[1]
        : null;

    return parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",") + (float ? "." + float : "");
}

function nullSpace(o, postfix) {
    return o != null ? o + (postfix != null ? postfix : '') : ' ';
}

function removeDuplicatedElement(names) {
    const uniqueNames = [];
    $.each(names, function (i, el) {
        if ($.inArray(el, uniqueNames) === -1) uniqueNames.push(el);
    });
    return uniqueNames;
}

/**
 * 숫자 포맷팅. 실수 부분을 컷팅하기 위한 용도
 *
 * @param number 입력숫자
 * @param count 보여질 숫자 갯수
 * @param wholeInteger count를 초과했을 때, 나머지 정수를 보여줄지 여부. false이면 'count' 숫자 외엔 0으로 채움
 */
function numberOnlyCount(number, count, wholeInteger) {
    const parts = number.toString().split(".");

    const integerCount = parts[0].length >= count ? count : parts[0].length;
    const integer = wholeInteger || parts[0].length <= count
        ? parts[0]
        : parseInt(parts[0]) - (parseInt(parts[0]) % Math.pow(10, parts[0].length - count));

    const float = !parts[1] || integerCount >= count || count - integerCount <= 0
        ? ''
        : '.' + pad(Math.floor(parseFloat('0.' + parts[1]) * Math.pow(10, count - integerCount)), count - integerCount);

    return integer.toString() + float.toString();
}

function numberOnlyFloatCount(number, count, wholeInteger) {
    const parts = number.toString().split(".");
    return parts[0] + '.' + pad(Math.floor(parseFloat('0.' + (parts[1] || 0)) * Math.pow(10, count)), count);
}

function pad(num, size) {
    let s = num + "";
    while (s.length < size) s = "0" + s;
    return s;
}

//src: http://stackoverflow.com/questions/11381673/detecting-a-mobile-browser
const userAgent = (function () {
    const agent = navigator.userAgent || navigator.vendor || window.opera;

    function condition1() {
        return /(android|bb\d+|meego).+mobile|avantgo|bada\/|blackberry|blazer|compal|elaine|fennec|hiptop|iemobile|ip(hone|od)|iris|kindle|lge |maemo|midp|mmp|mobile.+firefox|netfront|opera m(ob|in)i|palm( os)?|phone|p(ixi|re)\/|plucker|pocket|psp|series([46])0|symbian|treo|up\.(browser|link)|vodafone|wap|windows ce|xda|xiino/i.test(agent);
    }

    function condition2() {
        return /1207|6310|6590|3gso|4thp|50[1-6]i|770s|802s|a wa|abac|ac(er|oo|s-)|ai(ko|rn)|al(av|ca|co)|amoi|an(ex|ny|yw)|aptu|ar(ch|go)|as(te|us)|attw|au(di|-m|r |s )|avan|be(ck|ll|nq)|bi(lb|rd)|bl(ac|az)|br([ev])w|bumb|bw-([nu])|c55\/|capi|ccwa|cdm-|cell|chtm|cldc|cmd-|co(mp|nd)|craw|da(it|ll|ng)|dbte|dc-s|devi|dica|dmob|do([cp])o|ds(12|-d)|el(49|ai)|em(l2|ul)|er(ic|k0)|esl8|ez([4-7]0|os|wa|ze)|fetc|fly([-_])|g1 u|g560|gene|gf-5|g-mo|go(\.w|od)|gr(ad|un)|haie|hcit|hd-([mpt])|hei-|hi(pt|ta)|hp( i|ip)|hs-c|ht(c([- _agpst])|tp)|hu(aw|tc)|i-(20|go|ma)|i230|iac([ \-\/])|ibro|idea|ig01|ikom|im1k|inno|ipaq|iris|ja([tv])a|jbro|jemu|jigs|kddi|keji|kgt([ \/])|klon|kpt |kwc-|kyo([ck])|le(no|xi)|lg( g|\/([klu])|50|54|-[a-w])|libw|lynx|m1-w|m3ga|m50\/|ma(te|ui|xo)|mc(01|21|ca)|m-cr|me(rc|ri)|mi(o8|oa|ts)|mmef|mo(01|02|bi|de|do|t([- ov])|zz)|mt(50|p1|v )|mwbp|mywa|n10[0-2]|n20[2-3]|n30([02])|n50([025])|n7(0([01])|10)|ne(([cm])-|on|tf|wf|wg|wt)|nok([6i])|nzph|o2im|op(ti|wv)|oran|owg1|p800|pan([adt])|pdxg|pg(13|-([1-8]|c))|phil|pire|pl(ay|uc)|pn-2|po(ck|rt|se)|prox|psio|pt-g|qa-a|qc(07|12|21|32|60|-[2-7]|i-)|qtek|r380|r600|raks|rim9|ro(ve|zo)|s55\/|sa(ge|ma|mm|ms|ny|va)|sc(01|h-|oo|p-)|sdk\/|se(c([-01])|47|mc|nd|ri)|sgh-|shar|sie([-m])|sk-0|sl(45|id)|sm(al|ar|b3|it|t5)|so(ft|ny)|sp(01|h-|v-|v )|sy(01|mb)|t2(18|50)|t6(00|10|18)|ta(gt|lk)|tcl-|tdg-|tel([im])|tim-|t-mo|to(pl|sh)|ts(70|m-|m3|m5)|tx-9|up(\.b|g1|si)|utst|v400|v750|veri|vi(rg|te)|vk(40|5[0-3]|-v)|vm40|voda|vulc|vx(52|53|60|61|70|80|81|83|85|98)|w3c([- ])|webc|whit|wi(g |nc|nw)|wmlb|wonu|x700|yas-|your|zeto|zte-/i.test(agent.substr(0, 4));
    }

    function condition3() {
        return /android|ipad|playbook|silk/i.test(agent);
    }

    return {
        isMobile: function () {
            return condition1() || condition2();
        },
        isMobileOrTable: function () {
            return this.isMobile() || condition3()
        },
        getInternetExplorerVersion: function () {
            let rv = -1;
            if (navigator.appName === 'Microsoft Internet Explorer') {
                const ua = navigator.userAgent;
                const re = new RegExp("MSIE ([0-9]+[\.0-9]*)");
                if (re.exec(ua) != null)
                    rv = parseFloat(RegExp.$1);
            } else if (navigator.appName === 'Netscape') {
                const ua = navigator.userAgent;
                const re = new RegExp("Trident/.*rv:([0-9]+[\.0-9]*)");
                if (re.exec(ua) != null)
                    rv = parseFloat(RegExp.$1);
            }
            return rv;
        }
    };
})();

function guid() {
    function s4() {
        return Math.floor((1 + Math.random()) * 0x10000)
            .toString(16)
            .substring(1);
    }

    return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
        s4() + '-' + s4() + s4() + s4();
}

/**
 * onkeydown="return stopEnterPropagation(event);"
 */
function stopEnterPropagation(event) {
    if (event.keyCode !== 13) return true;
    event.stopPropagation();
    return false;
}

function viewImage(imageUrl) {
    window.open('', '').document.write('<html lang="ko"><body onclick="self.close();"><img title="클릭하시면 창이 닫힙니다." src="' + imageUrl + '"</body></html>');
}

function reload() {
    location.reload();
}

function refreshPageWithoutParameters() {
    location.href = location.pathname;
}

function keys(o) {
    const result = [];

    for (let key in o)
        if (o.hasOwnProperty(key))
            result.push(key);

    return result;
}

function values(o) {
    const result = [];

    for (let key in o)
        if (o.hasOwnProperty(key))
            result.push(o[key]);

    return result;
}

function printErrorLog() {
    console.error(arguments);
}

function htmlQuote(text) {
    return text.replace(/"/gi, "&quot;")
        .replace(/'/gi, "&#39;")
        .replace(/</gi, "&lt;");
}