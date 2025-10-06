import { useEffect, useState } from "react";

/**
 * Hook para detectar si el usuario está en un dispositivo móvil
 * usando user agent o ancho de pantalla.
 * @param {number} breakpoint - Ancho máximo en px para considerar "móvil" (por defecto 768)
 * @returns {boolean} true si es móvil, false si no
 */
export function useIsMobile(breakpoint = 768) {
  const [isMobile, setIsMobile] = useState(window.innerWidth <= breakpoint);
  useEffect(() => {
    const handleResize = () => setIsMobile(window.innerWidth <= breakpoint);
    window.addEventListener("resize", handleResize);
    return () => window.removeEventListener("resize", handleResize);
  }, [breakpoint]);
  return isMobile;
}