(function () {

    const STORAGE_KEY = "botengine-theme";

    function obtenerTemaGuardado() {

        return localStorage.getItem(
            STORAGE_KEY
        ) || "dark";
    }

    function aplicarTema(tema) {

        const valor =
            tema === "light"
                ? "light"
                : "dark";

        document.documentElement.setAttribute(
            "data-theme",
            valor
        );
    }

    function marcarSeleccion(tema) {

        document
            .querySelectorAll("[data-theme-choice]")
            .forEach(function (boton) {

                boton.classList.toggle(
                    "selected",
                    boton.dataset.themeChoice === tema
                );

            });
    }


    // Aplicacion inicial
    aplicarTema(
        obtenerTemaGuardado()
    );


    document.addEventListener(
        "DOMContentLoaded",
        function () {

            let temaGuardado =
                obtenerTemaGuardado();

            let temaPendiente =
                temaGuardado;

            aplicarTema(
                temaGuardado
            );

            marcarSeleccion(
                temaGuardado
            );


            document
                .querySelectorAll("[data-theme-choice]")
                .forEach(function (boton) {

                    boton.addEventListener(
                        "click",
                        function () {

                            temaPendiente =
                                boton.dataset.themeChoice;

                            // Vista previa
                            aplicarTema(
                                temaPendiente
                            );

                            marcarSeleccion(
                                temaPendiente
                            );

                            const mensaje =
                                document.getElementById(
                                    "themeStatus"
                                );

                            if (mensaje) {

                                mensaje.textContent =
                                    "Cambio pendiente. Presione Guardar apariencia.";

                                mensaje.className =
                                    "theme-status visible";
                            }
                        }
                    );
                });


            const guardar =
                document.getElementById(
                    "saveThemeButton"
                );

            if (guardar) {

                guardar.addEventListener(
                    "click",
                    function () {

                        localStorage.setItem(
                            STORAGE_KEY,
                            temaPendiente
                        );

                        temaGuardado =
                            temaPendiente;

                        aplicarTema(
                            temaGuardado
                        );

                        marcarSeleccion(
                            temaGuardado
                        );

                        const mensaje =
                            document.getElementById(
                                "themeStatus"
                            );

                        if (mensaje) {

                            mensaje.textContent =
                                "Apariencia guardada correctamente.";

                            mensaje.className =
                                "theme-status visible saved";
                        }
                    }
                );
            }
        }
    );


    // Si Chrome restaura una pagina desde cache,
    // vuelve a leer el tema almacenado.
    window.addEventListener(
        "pageshow",
        function () {

            const tema =
                obtenerTemaGuardado();

            aplicarTema(
                tema
            );

            marcarSeleccion(
                tema
            );
        }
    );


    // Sincroniza el tema entre pestañas abiertas.
    window.addEventListener(
        "storage",
        function (event) {

            if (event.key === STORAGE_KEY) {

                const tema =
                    obtenerTemaGuardado();

                aplicarTema(
                    tema
                );

                marcarSeleccion(
                    tema
                );
            }
        }
    );

})();