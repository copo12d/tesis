import { useIsMobile } from "../features/mobile/hooks/useIsMobile";
import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import toast from "react-hot-toast";

export default function MobileRoute({ children }) {
  const isMobile = useIsMobile();
  const navigate = useNavigate();

  useEffect(() => {
    if (!isMobile) {
      toast.error("Esta función solo está disponible desde un teléfono móvil.");
      navigate("/login", { replace: true });
    }
  }, [isMobile, navigate]);

  if (!isMobile) {
    // No renderiza nada mientras redirige
    return null;
  }

  return children;
}