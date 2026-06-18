document.addEventListener("DOMContentLoaded", function () {
    document.documentElement.dataset.appReady = "true";

    const timers = new WeakMap();
    const lastParams = new WeakMap();

    function targetFor(element) {
        const selector = element.getAttribute("hx-target");
        return selector ? document.querySelector(selector) : null;
    }

    function swap(target, html, swapMode) {
        if (!target) return;
        if ((swapMode || "").includes("outerHTML")) {
            target.outerHTML = html;
        } else {
            target.innerHTML = html;
        }
    }

    function formParams(form) {
        const params = new URLSearchParams();
        form.querySelectorAll("input, select, textarea").forEach(function (field) {
            if (!field.name || field.disabled) return;
            if ((field.type === "checkbox" || field.type === "radio") && !field.checked) return;
            if (field.tagName === "SELECT" && field.multiple) {
                Array.from(field.selectedOptions).forEach(function (option) {
                    params.append(field.name, option.value);
                });
                return;
            }
            params.append(field.name, field.value);
        });
        return params;
    }

    function refreshForm(form) {
        const url = form.getAttribute("hx-get");
        const target = targetFor(form);
        if (!url || !target) return;
        const params = formParams(form);
        lastParams.set(form, params.toString());
        fetch(url + "?" + params.toString(), { headers: { "HX-Request": "true" } })
            .then((response) => response.text())
            .then((html) => swap(target, html, form.getAttribute("hx-swap")));
    }

    document.addEventListener("input", function (event) {
        const form = event.target.closest("form[hx-get]");
        if (!form) return;
        event.stopImmediatePropagation();
        clearTimeout(timers.get(form));
        timers.set(form, setTimeout(function () {
            refreshForm(form);
        }, 350));
    }, true);

    document.addEventListener("change", function (event) {
        const form = event.target.closest("form[hx-get]");
        if (form) event.stopImmediatePropagation();
        if (form) refreshForm(form);
    }, true);

    document.addEventListener("submit", function (event) {
        const form = event.target.closest("form[hx-post]");
        if (!form) return;
        event.preventDefault();
        event.stopImmediatePropagation();
        const target = targetFor(form);
        fetch(form.getAttribute("hx-post"), {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
                "HX-Request": "true"
            },
            body: formParams(form).toString()
        })
            .then((response) => response.text())
            .then((html) => swap(target, html, form.getAttribute("hx-swap")));
    }, true);

    setInterval(function () {
        document.querySelectorAll("form[hx-get]").forEach(function (form) {
            const current = formParams(form).toString();
            if (!lastParams.has(form)) {
                lastParams.set(form, current);
                return;
            }
            if (lastParams.get(form) !== current) {
                refreshForm(form);
            }
        });
    }, 600);
});
