import { useState } from "react";
import "../styles/admin-section.css";

export const AdminSection = () => {
  const [open, setOpen] = useState(false);

  return (
    <>
      <button
        className={`dropdown-btn ${open ? "active" : ""}`}
        onClick={() => setOpen(!open)}
      >
        Opciones Admin
        <i className="fa fa-caret-down"></i>
      </button>

      <div className={`admin-section ${open ? "show" : ""}`}>
        <a href="#">Usuarios</a>
        <a href="#">Roles</a>
        <a href="#">Logs</a>
      </div>
    </>
  );
};
