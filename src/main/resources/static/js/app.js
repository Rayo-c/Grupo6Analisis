// Por ahora este formulario NO valida contra el backend.
// Cuando creemos el endpoint (ej. POST /api/auth/login), aquí se hará
// un fetch() real hacia esa ruta.
//
// Nota de seguridad para más adelante: la validación real de usuario/contraseña
// SIEMPRE debe pasar por el backend. El navegador nunca debe decidir si un
// login es válido, porque cualquiera puede leer y modificar el JavaScript
// del lado del cliente con las herramientas de desarrollador.

document.getElementById('loginForm').addEventListener('submit', function (e) {
  e.preventDefault();

  const usuario = document.getElementById('usuario').value;
  const mensaje = document.getElementById('mensaje');

  mensaje.textContent = `Formulario recibido para "${usuario}" (todavía sin conexión al backend)`;
});
