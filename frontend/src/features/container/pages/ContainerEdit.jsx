import { useEffect, useMemo, useState } from "react";
import { Box, Spinner, Text } from "@chakra-ui/react";
import { useNavigate, useParams } from "react-router-dom";
import { ContainerForm } from "../components/ContainerForm";
import { ContainerAPI } from "../api/container.api";
import { Marker, Popup } from "react-leaflet";

export function ContainerEdit() {
  const { id: idParam } = useParams();
  const id = Number(idParam);
  const navigate = useNavigate();

  const [loading, setLoading] = useState(true);
  const [updating, setUpdating] = useState(false);
  const [container, setContainer] = useState(null);
  const [error, setError] = useState("");
  // NUEVO: coords controladas por el padre
  const [coords, setCoords] = useState(null);

  useEffect(() => {
    if (!Number.isFinite(id)) {
      setError("ID inválido.");
      setLoading(false);
      return;
    }
    (async () => {
      try {
        setLoading(true);
        const res = await ContainerAPI.getById(id);
        const data = res?.data?.data ?? res?.data ?? null;
        setContainer(data);

        const lat = Number(data?.latitude);
        const lng = Number(data?.longitude);
        if (Number.isFinite(lat) && Number.isFinite(lng)) {
          setCoords([lat, lng]);
        }
      } catch {
        setError("No se pudo cargar el contenedor.");
      } finally {
        setLoading(false);
      }
    })();
  }, [id]);

  // Handlers del marker (definidos en el padre)
  const markerHandlers = useMemo(
    () => ({
      dragend(e) {
        const { lat, lng } = e.target.getLatLng();
        setCoords([lat, lng]);
      },
    }),
    []
  );

  // Texto del popup
  const popup = (
    <Popup>
      Ubicación del contenedor
      <br />
      {coords ? (
        <>
          Lat: {coords[0].toFixed(6)} <br />
          Lng: {coords[1].toFixed(6)}
        </>
      ) : null}
    </Popup>
  );

  return (
    <Box h="100vh" overflowY="auto" bg="gray.50" px={4} py={14}>
      {loading ? (
        <Box display="flex" justifyContent="center" py={10}>
          <Spinner />
        </Box>
      ) : (
        <>
          {error && (
            <Text color="red.500" textAlign="center" mb={4}>
              {error}
            </Text>
          )}

          <ContainerForm
            initialValues={container}
            loading={updating}
            submitText="Actualizar contenedor"
            title="Editar contenedor"
            // Inyectamos un Marker controlado por el padre (draggable y con handlers)
            mapMarker={
              coords ? (
                <Marker
                  position={coords}
                  draggable
                  eventHandlers={markerHandlers}
                >
                  {popup}
                </Marker>
              ) : null
            }
            onSubmit={async (values) => {
              try {
                setUpdating(true);
                // Asegura enviar las coords actuales controladas por el padre
                const payload = {
                  ...values,
                  ...(Array.isArray(coords)
                    ? { latitude: coords[0], longitude: coords[1] }
                    : {}),
                };
                const ok = await ContainerAPI.update(id, payload);
                if (ok !== false) navigate("/container/list");
              } finally {
                setUpdating(false);
              }
            }}
          />
        </>
      )}
    </Box>
  );
}