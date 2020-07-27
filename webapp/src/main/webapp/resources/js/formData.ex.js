(function () {
    const FormDataItem = (function () {
        return function (name, value, blobName) {
            this.name = name;
            this.value = value;
            this.blobName = blobName;
        };
    })();

    FormData.prototype._values = FormData.prototype.values;
    delete FormData.prototype.values;

    const _append = FormData.prototype.append;
    FormData.prototype.append = function (name, value, blobName) {
        if (!this.values) {
            this.values = [];
        }
        this.values.push(new FormDataItem(name, value, blobName));
        const argumentsArray = [];
        for (let i = 0; i < arguments.length; i++)
            argumentsArray.push(arguments[i]);
        _append.apply(this, argumentsArray);
    };

    FormData.prototype.stringify = function () {
        return $.param(this.values ? this.values : []);
    };
})();
