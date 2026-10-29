const {test} = require('node:test');
const assert = require('node:assert/strict');
const {readFileSync} = require('node:fs');
const {JSDOM} = require('jsdom');
const jquery = require('jquery');

function page(script) {
    const dom = new JSDOM('<!doctype html><body></body>', {runScripts: 'outside-only'});
    const $ = jquery(dom.window);
    dom.window.jQuery = $;
    dom.window.eval(readFileSync('webapp/src/main/webapp/resources/js/' + script, 'utf8'));
    return {dom, $};
}

for (const field of ['__proto__.polluted', 'constructor.prototype.polluted', 'user[__proto__].polluted', 'user[constructor].prototype.polluted', 'user.!__proto__[label].value']) {
    test('form serialization blocks ' + field, async () => {
        const {dom, $} = page('jquery.ex.js');
        try {
            const form = $('<form>').append($('<input>', {name: field, value: 'injected'}));
            assert.equal(form.asJsonData().state(), 'rejected');
            assert.equal(dom.window.Object.prototype.polluted, undefined);
        } finally {
            delete dom.window.Object.prototype.polluted;
            dom.window.close();
        }
    });
}

test('form serialization preserves nested maps and repeated fields', async () => {
    const {dom, $} = page('jquery.ex.js');
    try {
        const form = $('<form>').append(
            $('<input>', {name: 'user.name', value: 'Alice'}),
            $('<input>', {name: 'labels[en]', value: 'Name'}),
            $('<input>', {name: 'tags', value: 'a', 'data-multiple': 'true'}),
            $('<input>', {name: 'tags', value: 'b', 'data-multiple': 'true'})
        );
        assert.equal(JSON.stringify(await Promise.resolve(form.asJsonData())),
            JSON.stringify({user: {name: 'Alice'}, labels: {en: 'Name'}, tags: ['a', 'b']}));
    } finally { dom.window.close(); }
});

for (const path of ['user.toString.label', 'user[toString].label', 'user.name', 'user[name].label']) {
    test('form serialization skips scalar traversal in ' + path, async () => {
        const {dom, $} = page('jquery.ex.js');
        const builtins = [dom.window.Object.prototype, dom.window.String.prototype,
            dom.window.String.prototype.toString, dom.window.Array.prototype];
        const properties = builtins.map(value => Object.getOwnPropertyNames(value));
        try {
            const form = $('<form>').append(
                $('<input>', {name: 'user', value: 'Alice'}),
                $('<input>', {name: path, value: 'label'}),
                $('<input>', {name: 'title', value: 'Report'})
            );
            const result = await Promise.resolve(form.asJsonData());
            assert.equal(JSON.stringify(result), JSON.stringify({user: 'Alice', title: 'Report'}));
            for (let i = 0; i < builtins.length; i++) {
                assert.deepEqual(Object.getOwnPropertyNames(builtins[i]), properties[i]);
            }
        } finally { dom.window.close(); }
    });
}

test('form serialization creates own arrays for repeated inherited property names', async () => {
    const {dom, $} = page('jquery.ex.js');
    try {
        const form = $('<form>').append(
            $('<input>', {name: 'toString', value: 'a', 'data-multiple': 'true'}),
            $('<input>', {name: 'toString', value: 'b', 'data-multiple': 'true'}),
            $('<input>', {name: 'labels[toString]', value: 'c', 'data-multiple': 'true'}),
            $('<input>', {name: 'labels[toString]', value: 'd', 'data-multiple': 'true'})
        );
        assert.equal(JSON.stringify(await Promise.resolve(form.asJsonData())),
            JSON.stringify({toString: ['a', 'b'], labels: {toString: ['c', 'd']}}));
    } finally { dom.window.close(); }
});

test('form serialization skips repeated fields that conflict with a scalar', async () => {
    const {dom, $} = page('jquery.ex.js');
    try {
        const form = $('<form>').append(
            $('<input>', {name: 'tags', value: 'first'}),
            $('<input>', {name: 'tags', value: 'second', 'data-multiple': 'true'})
        );
        assert.equal(JSON.stringify(await Promise.resolve(form.asJsonData())), JSON.stringify({tags: 'first'}));
    } finally { dom.window.close(); }
});

test('modal target does not parse an HTML attribute as markup', () => {
    const {dom, $} = page('jquery.leanModal.js');
    try {
        const trigger = $('<a>').attr('href', '<img id="injected" src=x onerror="alert(1)">').appendTo('body');
        assert.doesNotThrow(() => trigger.leanModal());
        assert.equal(dom.window.document.querySelector('#injected'), null);
    } finally { dom.window.close(); }
});

test('modal retains fragment target open and destroy behavior', () => {
    const {dom, $} = page('jquery.leanModal.js');
    try {
        $('<div>', {id: 'modal'}).appendTo('body');
        const trigger = $('<a>', {href: '#modal'}).appendTo('body');
        trigger.leanModal().trigger('click');
        assert.equal(dom.window.leanModal._stackModal.length, 1);
        trigger.leanModal('destroy');
        assert.equal(dom.window.leanModal._stackModal.length, 0);
    } finally { dom.window.close(); }
});
