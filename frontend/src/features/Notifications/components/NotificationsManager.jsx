import { useNotifications } from "../hooks/useNotifications";
import { Link } from "react-router-dom";
import toast from "react-hot-toast";
import { FiX,FiInfo } from "react-icons/fi"; // <- NUEVO

export function NotificationsManager({
  openPath = "/container/list?status=FULL",
  intervalMs = 900_000, // prod: 900_000
}) {
  useNotifications({
    autoFetch: true,
    intervalMs,
    notifyOnEveryPollWhenHasItems: true,
    onStatusChange: ({ hasItems, count, message }) => {
      if (hasItems) {
        toast(
          (t) => (
            <div>
              <div style={{ display: "flex", gap: 8 }}>
                <div style={{ flex: 1 }}>
                  Hay {count} contenedor(es) lleno(s).{" "}
                  <Link
                    to={openPath}
                    style={{ color: "#0ea5e9", textDecoration: "underline" }}
                  > <br/>
                    Ver lista
                  </Link>
                </div>

                <button
                  type="button"
                  onClick={() => toast.dismiss(t.id)}
                  title="Cerrar"
                  aria-label="Cerrar notificación"
                  style={{
                    background: "transparent",
                    border: "none",
                    color: "#334155",
                    width: 28,
                    height: 28,
                    borderRadius: "50%",
                    cursor: "pointer",
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "center",
                  }}
                >
                  <FiX size={18} />
                </button>
              </div>
            </div>
          ),
          { duration: 4000, icon: <FiInfo />,}
        );
      } else {
        const text = message || "No hay contenedores llenos";
        toast(
          (t) => (
            <div style={{ display: "flex", gap: 8, alignItems: "center" }}>
              <div style={{ flex: 1 }}>{text}</div>
              <button
                type="button"
                onClick={() => toast.dismiss(t.id)}
                title="Cerrar"
                aria-label="Cerrar notificación"
                style={{
                  background: "transparent",
                  border: "none",
                  color: "#334155",
                  width: 28,
                  height: 28,
                  borderRadius: "50%",
                  cursor: "pointer",
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "center",
                }}
              >
                <FiX size={18} />
              </button>
            </div>
          ),
          { duration: 4000, icon: <FiInfo /> }
        );
      }
    },
  });

  return null;
}