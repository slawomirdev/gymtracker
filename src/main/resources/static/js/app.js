(function () {
    const key = "gt-scroll:" + window.location.pathname;

    const stored = sessionStorage.getItem(key);
    if (stored) {
        const pos = parseInt(stored, 10);
        if (!Number.isNaN(pos)) {
            window.scrollTo(0, pos);
        }
        sessionStorage.removeItem(key);
    }

    const save = () => {
        sessionStorage.setItem(key, String(window.scrollY));
    };

    document.querySelectorAll("form").forEach((form) => {
        form.addEventListener("submit", save);
    });
    window.addEventListener("beforeunload", save);
})();
