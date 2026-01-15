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

(function () {
    const modal = document.getElementById("bulkExerciseModal");
    const list = document.getElementById("bulkExerciseList");
    const template = document.getElementById("bulkExerciseRowTemplate");
    const addButton = document.getElementById("bulkExerciseAddRow");
    const resetButton = document.getElementById("bulkExerciseReset");
    const emptyAlert = document.getElementById("bulkExerciseEmpty");
    const form = document.getElementById("bulkExerciseForm");
    if (!modal || !list || !template || !addButton || !form) {
        return;
    }

    const cloneRow = () => {
        const node = template.content.firstElementChild.cloneNode(true);
        const hidden = node.querySelector(".bulk-active-hidden");
        if (hidden) {
            hidden.value = "true";
        }
        return node;
    };

    const reindexRows = () => {
        const rows = Array.from(list.querySelectorAll(".bulk-row"));
        rows.forEach((row, index) => {
            const indexLabel = row.querySelector(".bulk-row-index");
            if (indexLabel) {
                indexLabel.textContent = String(index + 1);
            }
            row.querySelectorAll("[data-name-template]").forEach((input) => {
                const pattern = input.getAttribute("data-name-template");
                if (!pattern) {
                    return;
                }
                input.name = pattern.replace("__index__", index);
            });
        });
        list.querySelectorAll(".bulk-remove-row").forEach((btn) => {
            btn.disabled = rows.length === 1;
        });
    };

    const addRow = () => {
        list.appendChild(cloneRow());
        reindexRows();
        emptyAlert?.classList.add("d-none");
    };

    const resetForm = () => {
        list.innerHTML = "";
        addRow();
        emptyAlert?.classList.add("d-none");
    };

    addButton.addEventListener("click", addRow);
    resetButton?.addEventListener("click", resetForm);

    list.addEventListener("click", (event) => {
        if (!event.target.classList.contains("bulk-remove-row")) {
            return;
        }
        const row = event.target.closest(".bulk-row");
        if (row && list.children.length > 1) {
            row.remove();
            reindexRows();
        }
    });

    list.addEventListener("change", (event) => {
        if (!event.target.classList.contains("bulk-active")) {
            return;
        }
        const row = event.target.closest(".bulk-row");
        const hidden = row ? row.querySelector(".bulk-active-hidden") : null;
        if (hidden) {
            hidden.value = event.target.checked ? "true" : "false";
        }
    });

    list.addEventListener("input", (event) => {
        if (event.target.classList.contains("bulk-name")) {
            emptyAlert?.classList.add("d-none");
        }
    });

    form.addEventListener("submit", (event) => {
        Array.from(list.querySelectorAll(".bulk-row")).forEach((row) => {
            const nameInput = row.querySelector(".bulk-name");
            if (nameInput && nameInput.value.trim() === "") {
                row.remove();
            }
        });
        if (!list.children.length) {
            event.preventDefault();
            emptyAlert?.classList.remove("d-none");
            resetForm();
            return;
        }
        reindexRows();
        Array.from(list.querySelectorAll(".bulk-row")).forEach((row, index) => {
            const checkbox = row.querySelector(".bulk-active");
            const hidden = row.querySelector(".bulk-active-hidden");
            if (hidden) {
                hidden.name = `exercises[${index}].active`;
                hidden.value = checkbox && checkbox.checked ? "true" : "false";
            }
        });
    });

    modal.addEventListener("shown.bs.modal", resetForm);

    if (!list.children.length) {
        addRow();
    }
})();
