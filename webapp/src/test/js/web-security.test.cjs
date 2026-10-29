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

for (const field of ['__proto__.polluted', 'constructor.prototype.polluted', 'user[__proto__].polluted', 'user[constructor].prototype.polluted']) {
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
