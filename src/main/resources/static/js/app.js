document.addEventListener("DOMContentLoaded", function () {
    document.documentElement.dataset.appReady = "true";

    const timers = new WeakMap();
    const lastParams = new WeakMap();
    const topbar = document.querySelector(".topbar");

    function asElement(target) {
        return target && target.nodeType === 1 ? target : null;
    }

    function targetFor(element) {
        const selector = element.getAttribute("hx-target");
        return selector ? document.querySelector(selector) : null;
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

    function toastStack() {
        let stack = document.querySelector(".toast-stack");
        if (!stack) {
            stack = document.createElement("div");
            stack.className = "toast-stack";
            document.body.appendChild(stack);
        }
        return stack;
    }

    function showToast(message) {
        if (!message) return;
        const toast = document.createElement("div");
        toast.className = "toast";
        toast.textContent = message;
        toastStack().appendChild(toast);
        window.setTimeout(function () {
            toast.style.opacity = "0";
            toast.style.transform = "translateY(8px)";
            window.setTimeout(function () {
                toast.remove();
            }, 220);
        }, 3200);
    }

    function setLoading(form, loading) {
        form.classList.toggle("is-loading", loading);
        const shell = form.closest(".filter-shell");
        if (shell) shell.classList.toggle("is-loading", loading);
    }

    function setSubmitState(form, submitting) {
        form.dataset.submitting = submitting ? "true" : "false";
        form.querySelectorAll('button[type="submit"]').forEach(function (button) {
            button.disabled = submitting;
            if (submitting) {
                button.dataset.originalText = button.textContent;
            } else if (button.dataset.originalText) {
                button.textContent = button.dataset.originalText;
            }
        });
    }

    function swap(target, html, swapMode) {
        if (!target) return;
        const targetId = target.id;
        if ((swapMode || "").includes("outerHTML")) {
            target.outerHTML = html;
        } else {
            target.innerHTML = html;
        }
        initDynamic(document);
        const swapped = targetId ? document.getElementById(targetId) : null;
        if (swapped && swapped.querySelector(".success-card")) {
            swapped.scrollIntoView({ behavior: "smooth", block: "start" });
        }
    }

    function refreshForm(form) {
        const url = form.getAttribute("hx-get");
        const target = targetFor(form);
        if (!url || !target) return;
        const params = formParams(form);
        const query = params.toString();
        lastParams.set(form, query);
        setLoading(form, true);
        fetch(url + (query ? "?" + query : ""), { headers: { "HX-Request": "true" } })
            .then(function (response) { return response.text(); })
            .then(function (html) { swap(target, html, form.getAttribute("hx-swap")); })
            .catch(function () { showToast("خطا در به روزرسانی نتایج"); })
            .finally(function () { setLoading(form, false); });
    }

    function updateSlotCards(root) {
        root.querySelectorAll(".slot-card").forEach(function (card) {
            const input = card.querySelector('input[type="radio"]');
            card.classList.toggle("selected", Boolean(input && input.checked));
        });
    }

    function updateStarPickers(root) {
        root.querySelectorAll(".star-picker label").forEach(function (label) {
            const input = label.querySelector('input[type="radio"]');
            label.classList.toggle("selected", Boolean(input && input.checked));
        });
    }

    function animateCounts(root) {
        if (window.matchMedia("(prefers-reduced-motion: reduce)").matches) return;
        root.querySelectorAll("[data-count]").forEach(function (el) {
            if (el.dataset.countAnimated === "true") return;
            const end = Number.parseInt(el.dataset.count, 10);
            if (!Number.isFinite(end)) return;
            el.dataset.countAnimated = "true";
            const startTime = performance.now();
            const duration = 850;
            function frame(now) {
                const progress = Math.min((now - startTime) / duration, 1);
                el.textContent = Math.round(end * progress).toString();
                if (progress < 1) requestAnimationFrame(frame);
            }
            requestAnimationFrame(frame);
        });
    }

    function initDynamic(root) {
        updateSlotCards(root);
        updateStarPickers(root);
        animateCounts(root);
        root.querySelectorAll("[data-toast]").forEach(function (element) {
            if (element.dataset.toastShown === "true") return;
            element.dataset.toastShown = "true";
            showToast(element.dataset.toastMessage || element.textContent.trim());
        });
    }

    function updateTopbar() {
        if (topbar) topbar.classList.toggle("is-scrolled", window.scrollY > 12);
    }

    function initMobileBookingCta() {
        const cta = document.querySelector("[data-mobile-booking-cta]");
        const bookingPanel = document.getElementById("booking-panel");
        if (!cta || !bookingPanel || !("IntersectionObserver" in window)) return;
        const observer = new IntersectionObserver(function (entries) {
            const visible = entries.some(function (entry) { return entry.isIntersecting; });
            cta.classList.toggle("is-visible", !visible && window.innerWidth <= 760);
        }, { threshold: 0.08 });
        observer.observe(bookingPanel);
        window.addEventListener("resize", function () {
            const rect = bookingPanel.getBoundingClientRect();
            const visible = rect.top < window.innerHeight && rect.bottom > 0;
            cta.classList.toggle("is-visible", window.innerWidth <= 760 && !visible);
        });
    }

    document.addEventListener("click", function (event) {
        const element = asElement(event.target);
        if (!element) return;

        const toggle = element.closest("[data-nav-toggle]");
        if (toggle) {
            const topbar = toggle.closest(".topbar");
            const open = !topbar.classList.contains("nav-open");
            topbar.classList.toggle("nav-open", open);
            toggle.setAttribute("aria-expanded", String(open));
            return;
        }

        const navLink = element.closest(".nav-links a");
        if (navLink) {
            const currentTopbar = navLink.closest(".topbar");
            const currentToggle = currentTopbar ? currentTopbar.querySelector("[data-nav-toggle]") : null;
            if (currentTopbar) currentTopbar.classList.remove("nav-open");
            if (currentToggle) currentToggle.setAttribute("aria-expanded", "false");
        }

        const slotInput = element.closest(".slot-card input");
        if (slotInput) updateSlotCards(document);

        const starInput = element.closest(".star-picker input");
        if (starInput) updateStarPickers(document);

        const anchor = element.closest('a[href^="#"]');
        if (anchor && anchor.hash.length > 1) {
            const target = document.querySelector(anchor.hash);
            if (target) {
                event.preventDefault();
                target.scrollIntoView({ behavior: "smooth", block: "start" });
            }
        }
    });

    document.addEventListener("change", function (event) {
        const element = asElement(event.target);
        if (!element) return;
        if (element.closest(".slot-card")) updateSlotCards(document);
        if (element.closest(".star-picker")) updateStarPickers(document);
    });

    document.addEventListener("input", function (event) {
        const element = asElement(event.target);
        const form = element ? element.closest("form[hx-get]") : null;
        if (!form) return;
        event.stopImmediatePropagation();
        clearTimeout(timers.get(form));
        timers.set(form, setTimeout(function () {
            refreshForm(form);
        }, 350));
    }, true);

    document.addEventListener("change", function (event) {
        const element = asElement(event.target);
        const form = element ? element.closest("form[hx-get]") : null;
        if (form) event.stopImmediatePropagation();
        if (form) refreshForm(form);
    }, true);

    document.addEventListener("submit", function (event) {
        const element = asElement(event.target);
        const form = element ? element.closest("form[hx-post]") : null;
        if (!form) return;
        event.preventDefault();
        event.stopImmediatePropagation();
        if (form.dataset.submitting === "true") return;
        if (!form.checkValidity()) {
            form.reportValidity();
            return;
        }
        const target = targetFor(form);
        setSubmitState(form, true);
        setLoading(form, true);
        fetch(form.getAttribute("hx-post"), {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
                "HX-Request": "true"
            },
            body: formParams(form).toString()
        })
            .then(function (response) { return response.text(); })
            .then(function (html) { swap(target, html, form.getAttribute("hx-swap")); })
            .catch(function () { showToast("ثبت درخواست انجام نشد"); })
            .finally(function () {
                setSubmitState(form, false);
                setLoading(form, false);
            });
    }, true);

    document.body.addEventListener("htmx:beforeRequest", function (event) {
        const form = event.target && event.target.closest ? event.target.closest("form") : null;
        if (form) setLoading(form, true);
    });

    document.body.addEventListener("htmx:afterRequest", function (event) {
        const form = event.target && event.target.closest ? event.target.closest("form") : null;
        if (form) setLoading(form, false);
        initDynamic(document);
    });

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

    window.addEventListener("scroll", updateTopbar, { passive: true });
    updateTopbar();
    initDynamic(document);
    initMobileBookingCta();
});
