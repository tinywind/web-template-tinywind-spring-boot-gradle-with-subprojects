const {test} = require('node:test');
const assert = require('node:assert/strict');
const {execFileSync} = require('node:child_process');
const {existsSync, readFileSync} = require('node:fs');
const {JSDOM} = require('jsdom');

function packagedAsset(jar, resource) {
    const war = '.tmp/build/webapp/libs/webapp-0.1-SNAPSHOT.war';
    assert.ok(existsSync(war), 'Run ./gradlew test :webapp:bootWar before npm test.');
    return execFileSync('java', ['-Xmx64m', '-cp', '.tmp/build/webapp/classes/java/test',
        'org.tinywind.server.compatibility.WebJarAssetReader', war, jar, resource], {encoding: 'utf8'});
}

function page(html) {
    const dom = new JSDOM('<!doctype html><body>' + html + '</body>', {runScripts: 'outside-only'});
    dom.window.eval(packagedAsset('jquery-3.7.1.jar', 'META-INF/resources/webjars/jquery/3.7.1/jquery.min.js'));
    const $ = dom.window.jQuery;
    dom.window.contextPath = '';
    return {dom, $};
}

test('packaged D3 renders the actual line, bar, stacked bar and pie helpers', async () => {
    const {dom} = page('<div id="line" style="width:640px;height:360px"></div>'
        + '<div id="bar" style="width:640px;height:360px"></div>'
        + '<div id="stacked" style="width:640px;height:360px"></div>'
        + '<svg id="pie" style="width:200px;height:200px"></svg>');
    try {
        dom.window.eval(packagedAsset('d3js-6.7.0.jar', 'META-INF/resources/webjars/d3js/6.7.0/d3.min.js'));
        dom.window.eval(readFileSync('webapp/src/main/webapp/resources/js/depend.func.js', 'utf8'));
        assert.equal(dom.window.d3.version, '6.7.0');
        const data = [{label: 'First', value: 4, extra: 2}, {label: 'Second', value: 6, extra: 3}];
        for (const [id, fn] of [['line', 'drawLineChart'], ['bar', 'drawBarChart'], ['stacked', 'drawStackedBarChart']]) {
            dom.window[fn](dom.window.document.getElementById(id), data, 'label', ['value', 'extra']);
        }
        dom.window.drawPieChart('#pie', 0.4);
        await new Promise(resolve => setTimeout(resolve, 1400));
        assert.equal(dom.window.document.querySelectorAll('#line path.d3-line').length, 2);
        assert.equal(dom.window.document.querySelectorAll('#line circle').length, 4);
        for (const id of ['bar', 'stacked']) {
            const bars = [...dom.window.document.querySelectorAll('#' + id + ' rect')];
            assert.equal(bars.length, 4);
            assert.ok(bars.every(bar => Number(bar.getAttribute('height')) > 0));
        }
        const piePaths = [...dom.window.document.querySelectorAll('#pie path')];
        assert.equal(piePaths.length, 2);
        assert.ok(piePaths.every(path => path.getAttribute('d').startsWith('M')));
    } finally { dom.window.close(); }
});

test('packaged BlockUI preserves the actual loading helper and unblock behavior', () => {
    const {dom, $} = page('');
    try {
        dom.window.eval(packagedAsset('jquery-blockui-2.70-1.jar',
            'META-INF/resources/webjars/jquery-blockui/2.70/jquery.blockUI.js'));
        dom.window.loadingImageSource = '/resources/images/loading.svg';
        dom.window.eval(readFileSync('webapp/src/main/webapp/resources/js/jquery.ex.js', 'utf8'));
        $.blockUIFixed();
        assert.equal(dom.window.document.querySelectorAll('.blockUI.blockOverlay').length, 1);
        assert.equal(dom.window.document.querySelector('.blockUI.blockMsg img').getAttribute('src'),
            '/resources/images/loading.svg');
        $.unblockUI({fadeOut: 0});
        assert.equal(dom.window.document.querySelectorAll('.blockUI').length, 0);
    } finally { dom.window.close(); }
});
