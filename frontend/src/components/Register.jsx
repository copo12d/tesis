export function Register() {
  return (
    <div>
      <h2>Registro</h2>
      <form>
        <input type="text" placeholder="Usuario" required />
        <input type="password" placeholder="Contraseña" required />
        <button type="submit">Registrarse</button>
      </form>
    </div>
  );
}